/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {Injectable} from '@angular/core';
import {Deferred, IAuthToken, PromiseUtils, WindowUtils} from 'smarteditcommons';
import {
	CHILD_SMARTEDIT_SENDING_AUTH_ERROR,
	CHILD_SMARTEDIT_SENDING_AUTHTOKEN,
	SSO_DIALOG_MARKER,
	SSO_DIALOG_WINDOW,
	SSO_LOGOUT_ENTRY_POINT,
} from './ssoAuthenticationConstants';

import {SSO_AUTHENTICATION_ENTRY_POINT as SSO_AUTHENTICATION_ENTRY_POINT_DEFAULT} from 'smarteditcepcommons';

/*
 * Helper to initiate a SAML /SSO autentication sequence through a pop-up
 * (because the sequence involves auto-submiting html form at some point that causes a redirect and hence would
 * loose app context if not executed in a different window)
 * that ultimately loads the app again which in turn will detect its context and do the following:
 * - will not continue loading
 * - wil post the loginToken to the /authenticate end point to retrieve oAuth access
 * - will send back to parent (through postMessage) the retrieved oAuth access
 * - will close;
 */
@Injectable()
export class SSOAuthenticationHelper {

	private readonly logoutIframeId = 'logoutIframe';

	private deferred: Deferred<IAuthToken> | null = null;

	constructor(
		private windowUtils: WindowUtils,
		private promiseUtils: PromiseUtils,
		private SSO_AUTHENTICATION_ENTRY_POINT: string = SSO_AUTHENTICATION_ENTRY_POINT_DEFAULT
	) {
		this.listenForAuthTokenBeingSentBack();
	}

    /*
     * Initiates the SSO dialog through a pop-up
     */
	launchSSODialog(): Promise<IAuthToken> {
		this.deferred = this.promiseUtils.defer<IAuthToken>();
		const ssoAuthenticationEntryPoint =
			this.SSO_AUTHENTICATION_ENTRY_POINT + "?" + SSO_DIALOG_MARKER;
		this.window.open(
			ssoAuthenticationEntryPoint,
			SSO_DIALOG_WINDOW,
			'toolbar=no,scrollbars=no,resizable=no,top=200,left=200,width=1000,height=800'
		);

		return this.deferred.promise;
	}

    /*
     * case of:
     * - the App called from another app in an SSO context and that should therefore auto-authenticate with SSO
     * - last manual authentication was with SSO
     */
	isAutoSSOMain(): boolean {
		return (
			this.lastAuthenticatedWithSSO ||
			(this.window.name !== SSO_DIALOG_WINDOW &&
				new RegExp(`[?&]${SSO_DIALOG_MARKER}`).test(location.search))
		);
	}

	logout(): any {
		let logoutIframe = this.logoutIframe;
		if (!logoutIframe) {
			logoutIframe = this.document.createElement('iframe');
			logoutIframe.id = this.logoutIframeId;
			logoutIframe.style.display = 'none';
			this.document.body.appendChild(logoutIframe);
		}
		logoutIframe.src = SSO_LOGOUT_ENTRY_POINT;

		this.lastAuthenticatedWithSSO = false;
	}

	private listenForAuthTokenBeingSentBack(): void {
		this.window.addEventListener(
			'message',
			(event: MessageEvent) => {
				if (event.origin !== document.location.origin) {
					return;
				}

				this.logoutIframe && this.logoutIframe.remove();

				if (event.data.eventId === CHILD_SMARTEDIT_SENDING_AUTHTOKEN) {
					this.lastAuthenticatedWithSSO = true;
					this.deferred && this.deferred.resolve(event.data.authToken);
				} else if (event.data.eventId === CHILD_SMARTEDIT_SENDING_AUTH_ERROR) {
					this.deferred && this.deferred.reject(event.data.error);
				}
			},
			false
		);
	}

	private get window() {
		return this.windowUtils.getWindow();
	}

	// Using static variable (to be shared between multiple instances) will not work because this extension will be loaded more than once
	private get lastAuthenticatedWithSSO() {
		return (this.window as any).lastAuthenticatedWithSSO;
	}

	private set lastAuthenticatedWithSSO(value: boolean) {
		(this.window as any).lastAuthenticatedWithSSO = value;
	}

	private get document(): Document {
		return this.window.document;
	}

	private get logoutIframe(): HTMLIFrameElement | null {
		return this.document.querySelector<HTMLIFrameElement>(`iframe#${this.logoutIframeId}`);
	}
}

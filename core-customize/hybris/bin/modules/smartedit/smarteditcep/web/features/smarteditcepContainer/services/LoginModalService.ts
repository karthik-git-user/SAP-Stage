/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as lodash from 'lodash';
import {
	IAuthToken,
	ISessionService,
	IStorageService,
	LogService,
	SeInjectable,
	StringUtils,
	UrlUtils
} from 'smarteditcommons';

import {SSOAuthenticationHelper} from './authentication';

/* @internal */
interface ICredentialsMapRecord {
	client_id: string;
	client_secret?: string;
}

export interface LoginDialogForm {
	posted: boolean;
	errorMessage: string;
	failed: boolean;
	$valid: boolean;
}

export interface LoginData {
	initialized?: boolean;
	ssoEnabled?: boolean;
	authURI: string;
	clientCredentials: ICredentialsMapRecord;
}

export interface LoginModalFeedback {
	userHasChanged: boolean;
}

/* @internal */
interface IRequestPayload {
	username: string;
	password: string;
	grant_type: string;
}

export interface LoginModalService {
	initialized: boolean;
	authURI: string;
	auth: {
		username: string;
		password: string;
	};
	signinWithCredentials(loginDialogForm: LoginDialogForm): Promise<LoginModalFeedback>;
	signinWithSSO(loginDialogForm?: LoginDialogForm): Promise<LoginModalFeedback>;
}

/* @internal */
export function loginModalFactory(loginData: LoginData) {
	/* @ngInject */

	@SeInjectable()
	class LoginModalServiceImplem implements LoginModalService {
		public initialized: boolean;
		public authURIKey: string;
		public authURI: string;
		public auth: {
			username: string;
			password: string;
		};
		public ssoEnabled: boolean;

		constructor(
			private logService: LogService,
			private $http: angular.IHttpService,
			private sessionService: ISessionService,
			private storageService: IStorageService,
			private stringUtils: StringUtils,
			private urlUtils: UrlUtils,
			private modalManager: any,
			private DEFAULT_AUTHENTICATION_ENTRY_POINT: string,
			private sSOAuthenticationHelper: SSOAuthenticationHelper
		) {
			this.auth = {
				username: '',
				password: ''
			};

			this.authURI = loginData.authURI;

			/* @ngInject */
			modalManager.setShowHeaderDismiss(false);

			this.initialized = loginData.initialized || false;
			this.ssoEnabled = (loginData.ssoEnabled || false) && this.isMainEndPoint();

			storageService.removeAuthToken(this.authURI);
			this.authURIKey = btoa(this.authURI).replace(/=/g, '');

			if (this.sSOAuthenticationHelper.isAutoSSOMain()) {
				this.signinWithSSO();
			}
		}

		signinWithCredentials = (loginDialogForm: LoginDialogForm): Promise<LoginModalFeedback> => {
			loginDialogForm.posted = true;
			loginDialogForm.errorMessage = '';
			loginDialogForm.failed = false;

			if (!loginDialogForm.$valid) {
				loginDialogForm.errorMessage = 'smarteditcep.logindialogform.username.and.password.required';
				return Promise.reject<LoginModalFeedback>(null);
			}

			const payload = lodash.cloneDeep(loginData.clientCredentials || {}) as any as IRequestPayload;
			payload.username = this.auth.username;
			payload.password = this.auth.password;
			payload.grant_type = 'password';

			return this.sendCredentials(payload)
				.then(
					this.storeAccessToken,
					this.APIAuthenticationFailureReportFactory(loginDialogForm)
				)
				.then(this.acceptUser);
		}

		signinWithSSO = (loginDialogForm?: LoginDialogForm): Promise<LoginModalFeedback> => {
			if (loginDialogForm) {
				loginDialogForm.posted = true;
				loginDialogForm.failed = false;
			}
			return this.sSOAuthenticationHelper
				.launchSSODialog()
				.then(
					this.storeAccessToken,
					this.APIAuthenticationFailureReportFactory(loginDialogForm)
				)
				.then(this.acceptUser);
		}

		private APIAuthenticationFailureReportFactory = (
			loginDialogForm?: LoginDialogForm
		): any => {
			return (error: any) => {
				this.logService.debug(
					`API Authentication Failure: ${this.authURI} status: ${error.status}`
				);
				if (loginDialogForm) {
					loginDialogForm.errorMessage =
						(error.data && this.stringUtils.sanitize(error.data.error_description)) ||
						'smarteditcep.logindialogform.oauth.error.default';
					loginDialogForm.failed = true;
				}
				return Promise.reject(error);
			};
		}

		private storeAccessToken = (
			response: angular.IHttpResponse<IAuthToken> | IAuthToken
		): PromiseLike<boolean> => {
			const token: IAuthToken = ('data' in response) ? response.data : response;
			this.storageService.storeAuthToken(this.authURI, token);
			this.logService.debug(`API Authentication Success: ${this.authURI}`);
			return this.isMainEndPoint()
				? this.sessionService.hasUserChanged()
				: Promise.resolve(false);
		}

		private sendCredentials = (payload: IRequestPayload): Promise<angular.IHttpResponse<IAuthToken>> => {
			return Promise.resolve(this.$http({
				method: 'POST',
				url: this.authURI,
				headers: {
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				data: this.urlUtils.getQueryString(payload).replace('?', '')
			}));
		}

		private acceptUser = (hasUserChanged: boolean): Promise<LoginModalFeedback> => {
			this.modalManager.close({
				userHasChanged: hasUserChanged
			});
			if (this.isMainEndPoint()) {
				this.sessionService.setCurrentUsername();
			}
			return Promise.resolve({userHasChanged: hasUserChanged});
		}

		private isMainEndPoint() {
			return this.DEFAULT_AUTHENTICATION_ENTRY_POINT === this.authURI;
		}
	}
	return LoginModalServiceImplem;
}
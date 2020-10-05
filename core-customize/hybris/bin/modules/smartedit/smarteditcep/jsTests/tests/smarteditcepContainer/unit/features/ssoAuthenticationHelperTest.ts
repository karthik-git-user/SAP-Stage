/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {SSOAuthenticationHelper, SSOAuthenticationUtils} from 'smarteditcepcontainer/services/authentication';
import {promiseUtils, IAuthToken, WindowUtils} from 'smarteditcommons';
import {
	SSO_CLIENT_ID,
	SSO_LOGOUT_ENTRY_POINT,
	SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT,
} from 'smarteditcepcontainer/services/authentication/ssoAuthenticationConstants';

describe('ssoAuthenticationHelper', () => {
	let ssoAuthenticationHelper: SSOAuthenticationHelper;
	let ssoAuthenticationUtils: SSOAuthenticationUtils;
	let windowUtils: jasmine.SpyObj<WindowUtils>;
	let _window: jasmine.SpyObj<Window>;
	let opener: jasmine.SpyObj<Window>;
	let logoutIframe: jasmine.SpyObj<HTMLIFrameElement>;
	let _location: jasmine.SpyObj<Location>;

	const authToken: IAuthToken = {
		access_token: 'access_token',
		token_type: 'token_type'
	};

	const error = {
		some: 'error'
	};

	const errorResponse = {
		status: 404,
		error
	};

	beforeEach(() => {
		opener = jasmine.createSpyObj<Window>('mockWindow', ['postMessage']);
		_window = jasmine.createSpyObj<Window>('mockWindow', [
			'open',
			'opener',
			'close',
			'addEventListener',
			'fetch'
		]);
		(_window as any).opener = opener;

		logoutIframe = jasmine.createSpyObj<HTMLIFrameElement>('logoutIframe', ['remove']);

		const _document = jasmine.createSpyObj<Document>('mockDocument', [
			'location',
			'querySelector'
		]);

		_document.querySelector.and.callFake((selector: string) => {
			if (selector === 'iframe#logoutIframe') {
				return logoutIframe;
			}
			throw new Error(`unexpected selector '${selector}' passed to document.querySelector`);
		});

		_location = jasmine.createSpyObj<Location>('mockLocation', ['pathname', 'href']);
		(_window as any).document = _document;
		(_document as any).location = _location;
		(_location as any).origin = 'someorigin';

		windowUtils = jasmine.createSpyObj<WindowUtils>('windowUtils', ['getWindow']);
		windowUtils.getWindow.and.returnValue(_window);

		ssoAuthenticationUtils = new SSOAuthenticationUtils();

		ssoAuthenticationHelper = new SSOAuthenticationHelper(
			windowUtils,
			promiseUtils
		);
	});

	it('launchSSODialog will open a pop-up and listen for token being sent back', (done) => {
		expect(ssoAuthenticationHelper.isAutoSSOMain()).toBe(false);
		ssoAuthenticationHelper.launchSSODialog().then((token: IAuthToken) => {
			expect(_window.open).toHaveBeenCalledWith(
				'/samlsinglesignon/saml/smarteditcep?sso',
				'SSODIALOG_WINDOW',
				'toolbar=no,scrollbars=no,resizable=no,top=200,left=200,width=1000,height=800'
			);
			expect(token).toBe(authToken);

			expect(ssoAuthenticationHelper.isAutoSSOMain()).toBe(true);

			expect(logoutIframe.remove).toHaveBeenCalled();

			done();
		});

		const callback = _window.addEventListener.calls.argsFor(0)[1];

		callback({
			origin: document.location.origin,
			data: {
				eventId: 'ssoAuthenticate',
				authToken
			}
		});
	});

	it('completeDialog will open a pop-up and listen for error being sent back', (done) => {
		spyOn(ssoAuthenticationUtils, 'getWindow').and.returnValue(_window);

		const response = new Response(JSON.stringify(errorResponse), {status: 404});
		_window.fetch.and.callFake((url: string, config: any) => {
			if (
				url === SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT &&
				JSON.parse(config.body).client_id === SSO_CLIENT_ID
			) {
				return Promise.reject(response);
			}
			throw new Error(`unexpected url ${url} passed to window.fetch`);
		});

		ssoAuthenticationUtils.completeDialog().then(
			() => fail('should have rejected'),
			() => {
				expect(opener.postMessage).toHaveBeenCalledWith(
					{
						eventId: 'ssoAuthenticateError',
						error: {
							data: response,
							status: response.status
						}
					},
					'someorigin'
				);
				expect(_window.close).toHaveBeenCalled();

				done();
			}
		);
	});

	it('completeDialog will open a pop-up and listen for token being sent back', async () => {
		spyOn(ssoAuthenticationUtils, 'getWindow').and.returnValue(_window);

		_window.fetch.and.callFake((url: string, config: any) => {
			if (url === SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT && JSON.parse(config.body).client_id === SSO_CLIENT_ID) {
				return Promise.resolve(new Response(JSON.stringify(authToken)));
			}
			throw new Error(`unexpected url ${url} passed to window.fetch`);
		});

		await ssoAuthenticationUtils.completeDialog();

		expect(_window.fetch).toHaveBeenCalledWith(SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT, {
			method: 'POST',
			body: JSON.stringify({client_id: SSO_CLIENT_ID}),
			headers: {
				'Content-Type': 'application/json'
			}
		});

		expect(opener.postMessage).toHaveBeenCalledWith(
			{
				eventId: 'ssoAuthenticate',
				authToken
			},
			'someorigin'
		);
		expect(_window.close).toHaveBeenCalled();
	});

	it('logout will call sso logout in an iframe and reload the app in non SSO mode', () => {
		ssoAuthenticationHelper.logout();

		expect(logoutIframe.src).toBe(SSO_LOGOUT_ENTRY_POINT);
	});
});

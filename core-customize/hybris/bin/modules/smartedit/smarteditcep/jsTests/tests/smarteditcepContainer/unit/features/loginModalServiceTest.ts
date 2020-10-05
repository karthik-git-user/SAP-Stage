/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as lodash from 'lodash';
import {
	stringUtils,
	urlUtils,
	ISessionService,
	IStorageService,
	LogService
} from 'smarteditcommons';
import {loginModalFactory, LoginDialogForm, LoginModalService, SSOAuthenticationHelper} from 'smarteditcepcontainer/services';

describe('Login Modal Service', () => {
	const MESSAGE = "{{0[a='constructor'][a]('alert(\"XSS\")')()}}password";
	const SANITIZED_MESSAGE = "{{0[a='constructor'][a]\\('alert\\(\"XSS\"\\)'\\)\\(\\)}}password";

	const AUTH_URI_AND_CLIENT_CREDENTIALS_MOCK = {
		initialized: true,
		authURI: '/defaultAuthEntryPoint1',
		clientCredentials: {
			client_id: 'client_id_1'
		}
	};

	const DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK = '/defaultAuthEntryPoint1';

	const DUMMY_ERROR = {
		status: 401,
		data: {
			error_description: MESSAGE
		}
	};

	const VALID_LOGIN_FORM = {
		$valid: true
	} as LoginDialogForm;
	const INVALID_LOGIN_FORM = {
		$valid: false
	} as LoginDialogForm;

	let loginModalService: LoginModalService;
	let sessionServiceMock: jasmine.SpyObj<ISessionService>;
	let logService: jasmine.SpyObj<LogService>;
	let $http: jasmine.Spy;
	let $httpMock: jasmine.SpyObj<angular.IHttpService>;
	let storageService: jasmine.SpyObj<IStorageService>;
	let modalManager: jasmine.SpyObj<any>;
	let ssoAuthenticationHelper: jasmine.SpyObj<SSOAuthenticationHelper>;

	const oauthToken = {
		access_token: 'access-token1',
		token_type: 'bearer'
	};
	const expectedPayload = {
		client_id: 'client_id_1',
		username: 'someusername',
		password: 'somepassword',
		grant_type: 'password'
	};

	beforeEach(() => {
		ssoAuthenticationHelper = jasmine.createSpyObj<SSOAuthenticationHelper>(
			'ssoAuthenticationHelper',
			['isAutoSSOMain', 'launchSSODialog']
		);

		sessionServiceMock = jasmine.createSpyObj<ISessionService>('sessionService', [
			'hasUserChanged',
			'setCurrentUsername'
		]);
		sessionServiceMock.hasUserChanged.and.returnValue(false);

		logService = jasmine.createSpyObj<LogService>('logService', ['debug', 'error']);

		$http = jasmine.createSpy('$http');
		$httpMock = $http as any as jasmine.SpyObj<angular.IHttpService>;
		$http.and.callFake((payload: angular.IRequestConfig) => {
			if (payload.url !== DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK && payload.url !== '/authEntryPoint') {
				throw new Error(`unexpected http url ${payload.url}`);
			}
			if (payload.method !== 'POST') {
				throw new Error(`unexpected http method ${payload.method}`);
			}
			if (payload.headers['Content-Type'] !== 'application/x-www-form-urlencoded') {
				throw new Error(`unexpected http Content-Type ${payload.headers['Content-Type']}`);
			}
			if (lodash.isEqual(expectedPayload, urlUtils.parseQuery(payload.data))) {
				return Promise.resolve({data: oauthToken});
			} else {
				return Promise.reject(DUMMY_ERROR);
			}
		});

		storageService = jasmine.createSpyObj<IStorageService>('storageService', [
			'removeAuthToken',
			'storeAuthToken'
		]);
		modalManager = jasmine.createSpyObj('modalManager', ['close', 'setShowHeaderDismiss']);

		loginModalService = new (loginModalFactory(AUTH_URI_AND_CLIENT_CREDENTIALS_MOCK))(
			logService,
			$httpMock,
			sessionServiceMock,
			storageService,
			stringUtils,
			urlUtils,
			modalManager,
			DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
			ssoAuthenticationHelper
		);

		expect(modalManager.setShowHeaderDismiss).toHaveBeenCalledWith(false);
		expect(loginModalService.initialized).toBe(true);
	});

	describe('in non SSO', () => {
		it('when credentials are marked as wrong then returns a rejected promise', (done) => {
			loginModalService.signinWithCredentials(INVALID_LOGIN_FORM).then(
				() => {
					fail('should have rejected');
				},
				() => {
					expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
					expect(storageService.storeAuthToken).not.toHaveBeenCalled();
					expect(storageService.removeAuthToken).toHaveBeenCalled();
					done();
				}
			);
		});

		it('when backend rejects the authentication then returns a rejected promise with API failure error', (done) => {
			// when
			loginModalService.auth = {
				username: 'unexpected',
				password: 'unexpected'
			};

			loginModalService.signinWithCredentials(VALID_LOGIN_FORM).then(
				() => {
					fail('should have rejected');
				},
				() => {
					expect(storageService.removeAuthToken).toHaveBeenCalledWith(
						DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK
					);
					expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
					expect(storageService.storeAuthToken).not.toHaveBeenCalled();
					expect(VALID_LOGIN_FORM.errorMessage).toEqual(SANITIZED_MESSAGE);
					expect(modalManager.close).not.toHaveBeenCalledWith({
						userHasChanged: false
					});
					done();
				}
			);
		});

		it(`when backend accepts the authentication on default entry point 
            and user has not changed
            then auth token is stored
            and current user is updated`, async () => {
				sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(false));

				// when
				loginModalService.auth = {
					username: 'someusername',
					password: 'somepassword'
				};

				// then
				await loginModalService.signinWithCredentials(VALID_LOGIN_FORM);

				expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
				expect(storageService.storeAuthToken).toHaveBeenCalledWith(
					DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
					oauthToken
				);
				expect(storageService.removeAuthToken).toHaveBeenCalled();
				expect(modalManager.close).toHaveBeenCalledWith({
					userHasChanged: false
				});
			});

		it(`when backend accepts the authentication on default entry point 
            and user has changed
            then auth token is stored
            and current user is updated`, async () => {
				sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(true));

				// when
				loginModalService.auth = {
					username: 'someusername',
					password: 'somepassword'
				};

				// then
				await loginModalService.signinWithCredentials(VALID_LOGIN_FORM);

				expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
				expect(storageService.storeAuthToken).toHaveBeenCalledWith(
					DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
					oauthToken
				);
				expect(storageService.removeAuthToken).toHaveBeenCalled();
				expect(modalManager.close).toHaveBeenCalledWith({
					userHasChanged: true
				});
			});

		it(`when backend accepts the authentication on non default entry point 
            then auth token is stored
            and current user is not updated`, async () => {
				// given
				sessionServiceMock.hasUserChanged.and.throwError(
					'sessionServiceMock.hasUserChanged should not be invoked'
				);

				loginModalService.auth = {
					username: 'someusername',
					password: 'somepassword'
				};

				loginModalService.authURI = '/authEntryPoint';

				// when
				await loginModalService.signinWithCredentials(VALID_LOGIN_FORM);

				// then
				expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
				expect(storageService.storeAuthToken).toHaveBeenCalledWith(
					'/authEntryPoint',
					oauthToken
				);
				expect(storageService.removeAuthToken).toHaveBeenCalled();
				expect(modalManager.close).toHaveBeenCalledWith({
					userHasChanged: false
				});
			});
	});

	describe('in SSO', () => {
		it('when backend rejects the authentication then returns a rejected promise with API failure error', (done) => {
			// when
			ssoAuthenticationHelper.launchSSODialog.and.returnValue(Promise.reject(DUMMY_ERROR));

			loginModalService.signinWithSSO(VALID_LOGIN_FORM).then(
				() => {
					fail('should have rejected');
				},
				() => {
					expect(storageService.removeAuthToken).toHaveBeenCalledWith(
						DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK
					);
					expect(sessionServiceMock.setCurrentUsername).not.toHaveBeenCalled();
					expect(storageService.storeAuthToken).not.toHaveBeenCalled();
					expect(VALID_LOGIN_FORM.errorMessage).toEqual(SANITIZED_MESSAGE);
					expect(modalManager.close).not.toHaveBeenCalledWith({
						userHasChanged: false
					});
					done();
				}
			);
		});

		it(`when backend accepts the authentication on default entry point 
            and user has not changed
            then auth token is stored
            and current user is updated`, async () => {
				sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(false));

				// when
				ssoAuthenticationHelper.launchSSODialog.and.returnValue(Promise.resolve(oauthToken));

				// then
				await loginModalService.signinWithSSO(VALID_LOGIN_FORM);

				expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
				expect(storageService.storeAuthToken).toHaveBeenCalledWith(
					DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
					oauthToken
				);
				expect(storageService.removeAuthToken).toHaveBeenCalled();
				expect(modalManager.close).toHaveBeenCalledWith({
					userHasChanged: false
				});
			});

		it(`when backend accepts the authentication on default entry point 
            and user has changed
            then auth token is stored
            and current user is updated`, async () => {
				sessionServiceMock.hasUserChanged.and.returnValue(Promise.resolve(true));

				// when
				ssoAuthenticationHelper.launchSSODialog.and.returnValue(Promise.resolve(oauthToken));

				// then
				await loginModalService.signinWithSSO(VALID_LOGIN_FORM);

				expect(sessionServiceMock.setCurrentUsername).toHaveBeenCalled();
				expect(storageService.storeAuthToken).toHaveBeenCalledWith(
					DEFAULT_AUTHENTICATION_ENTRY_POINT_MOCK,
					oauthToken
				);
				expect(storageService.removeAuthToken).toHaveBeenCalled();
				expect(modalManager.close).toHaveBeenCalledWith({
					userHasChanged: true
				});
			});
	});
});
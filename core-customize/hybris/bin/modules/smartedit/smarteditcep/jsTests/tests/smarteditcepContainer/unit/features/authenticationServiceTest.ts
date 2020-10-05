/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {promiseHelper} from "testhelpers";
import {AuthenticationService, SSOAuthenticationHelper} from 'smarteditcepcontainer/index';
import {
	annotationService,
	stringUtils,
	CrossFrameEventService,
	GatewayProxied,
	ISharedDataService,
	IStorageService,
	LanguageService
} from 'smarteditcommons';

describe('outer AuthenticationService', () => {

	let authenticationService: AuthenticationService;
	let $q: jasmine.SpyObj<angular.IQService>;
	let $injectorMock: jasmine.SpyObj<angular.auto.IInjectorService>;
	let routeMock: jasmine.SpyObj<angular.route.IRouteService>;
	let languageServiceMock: jasmine.SpyObj<LanguageService>;
	let $locationMock: jasmine.SpyObj<angular.ILocationService>;
	let modalServiceMock: jasmine.SpyObj<any>;
	let sharedDataServiceMock: jasmine.SpyObj<ISharedDataService>;
	let storageServiceMock: jasmine.SpyObj<IStorageService>;
	let crossFrameEventServiceMock: jasmine.SpyObj<CrossFrameEventService>;
	let ssoAuthenticationHelper: jasmine.SpyObj<SSOAuthenticationHelper>;

	const DEFAULT_AUTHENTICATION_ENTRY_POINT = '/authorizationserver/oauth/token';
	const DEFAULT_AUTH_MAP = {
		api3: DEFAULT_AUTHENTICATION_ENTRY_POINT
	};
	const DEFAULT_CREDENTIALS_MAP = {
		[DEFAULT_AUTHENTICATION_ENTRY_POINT]: {
			client_id: 'smartedit'
		}
	};

	const EVENTS = {
		LOGOUT: 'some logout event'
	};

	const LANDING_PAGE_PATH = '/landingPage';

	beforeEach(() => {

		$q = promiseHelper.$q();
		$injectorMock = jasmine.createSpyObj<angular.auto.IInjectorService>('$injector', ['get']);
		routeMock = jasmine.createSpyObj<angular.route.IRouteService>('$route', ['reload']);
		languageServiceMock = jasmine.createSpyObj<LanguageService>('languageService', ['isInitialized']);
		$locationMock = jasmine.createSpyObj<angular.ILocationService>('$location', ['url']);
		modalServiceMock = jasmine.createSpyObj<any>('modalService', ['open']);
		sharedDataServiceMock = jasmine.createSpyObj('sharedDataService', ['get']);
		storageServiceMock = jasmine.createSpyObj<IStorageService>('storageService', ['isInitialized', 'getAuthToken', 'removeAllAuthTokens']);
		crossFrameEventServiceMock = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', ['publish']);
		ssoAuthenticationHelper = jasmine.createSpyObj<SSOAuthenticationHelper>(
			'ssoAuthenticationHelper',
			['isAutoSSOMain', 'logout']
		);
		const convertToArrayMock = (object: any) => {
			const configuration = [];
			for (const key in object) {
				if (key.indexOf('$') !== 0 && key.indexOf('toJSON') !== 0) {
					configuration.push({
						key,
						value: object[key]
					});
				}
			}
			return configuration;
		};
		const mergeMock = (additionalData: any, toMerge: any) => Object.assign(toMerge, additionalData);

		sharedDataServiceMock.get.and.callFake((key: string) => {
			const AUTHENTICATION_MAP = {
				api1: 'authEntryPoint1',
				api1more: 'authEntryPoint2',
				api2: 'authEntryPoint3'
			};

			const CREDENTIALS_MAP = {
				authEntryPoint1: {
					client_id: 'client_id_1',
					client_secret: 'client_secret_1'
				},
				authEntryPoint2: {
					client_id: 'client_id_2',
					client_secret: 'client_secret_2'
				}
			};

			const CONFIGURATION = {
				domain: 'thedomain'
			};

			switch (key) {
				case 'authenticationMap':
					return $q.when(AUTHENTICATION_MAP);
				case 'credentialsMap':
					return $q.when(CREDENTIALS_MAP);
				case 'configuration':
					return $q.when(CONFIGURATION);
				default:
					throw new Error('Unexpected value passed to sharedDataServiceMock.get()' + key);
			}
		});

		$injectorMock.get.and.callFake((key: string) => {
			switch (key) {
				case '$route':
					return routeMock;
				default:
					throw new Error('Unexpected value passed to $injectorMock.get() ' + key);
			}
		});

		crossFrameEventServiceMock.publish.and.returnValue($q.when());

		authenticationService = new AuthenticationService(
			$q,
			$injectorMock,
			$locationMock,
			languageServiceMock,
			modalServiceMock,
			LANDING_PAGE_PATH,
			DEFAULT_AUTHENTICATION_ENTRY_POINT,
			DEFAULT_AUTH_MAP,
			DEFAULT_CREDENTIALS_MAP,
			sharedDataServiceMock,
			storageServiceMock,
			crossFrameEventServiceMock,
			EVENTS,
			convertToArrayMock,
			mergeMock,
			stringUtils,
			ssoAuthenticationHelper
		);
	});

	it('checks GatewayProxied', () => {
		expect(annotationService.getClassAnnotation(AuthenticationService, GatewayProxied)).toEqual([
			'filterEntryPoints',
			'isAuthEntryPoint',
			'authenticate',
			'logout',
			'isReAuthInProgress',
			'setReAuthInProgress',
			'isAuthenticated']);
	});

	it('isReAuthInProgress reads status set by setReAuthInProgress', () => {
		expect(authenticationService.isReAuthInProgress("someURL")).toBeResolvedWithData(false);
		authenticationService.setReAuthInProgress("someURL");
		expect(authenticationService.isReAuthInProgress("someURL")).toBeResolvedWithData(true);
	});


	describe('#filterEntryPoints', () => {
		it('WHEN an entry point is filtered using filterEntryPoints AND the entry point matches one in the default auth map THEN the auth entry points returned will include the matched entry point', () => {
			// WHEN
			const promise = authenticationService.filterEntryPoints('api3');

			// THEN
			expect(promise).toBeResolvedWithData([DEFAULT_AUTHENTICATION_ENTRY_POINT]);
		});

		it('filterEntryPoints only keeps the values of authenticationMap the regex keys of which match the resource', () => {
			expect(authenticationService.filterEntryPoints("api1moreandmore"))
				.toBeResolvedWithData(['authEntryPoint1', 'authEntryPoint2']);

			expect(authenticationService.filterEntryPoints("api2/more"))
				.toBeResolvedWithData(['authEntryPoint3']);

			expect(authenticationService.filterEntryPoints("notfound"))
				.toBeResolvedWithData([]);
		});
	});

	describe('#isAuthEntryPoint', () => {
		it('isAuthEntryPoint returns true only if resource exactly matches at least one of the auth entry points or default auth entry point', () => {
			expect(authenticationService.isAuthEntryPoint("api1moreandmore")).toBeResolvedWithData(false);
			expect(authenticationService.isAuthEntryPoint("authEntryPoint1")).toBeResolvedWithData(true);
			expect(authenticationService.isAuthEntryPoint("authEntryPoint1more")).toBeResolvedWithData(false);
			expect(authenticationService.isAuthEntryPoint(DEFAULT_AUTHENTICATION_ENTRY_POINT)).toBeResolvedWithData(true);
		});
	});


	it('WHEN the entry point matches one in the default, default auth entry point is returned along with default client id', () => {
		// WHEN
		expect((authenticationService as any)._findLoginData('api3')).toBeResolvedWithData({
			authURI: DEFAULT_AUTHENTICATION_ENTRY_POINT,
			clientCredentials: {
				client_id: 'smartedit'
			}
		});

	});

	it('WHEN the entry point matches one in the auth map, corresponding entry point is returned along with relevant credentials', () => {
		// WHEN
		expect((authenticationService as any)._findLoginData('api1more')).toBeResolvedWithData({
			authURI: 'authEntryPoint1',
			clientCredentials: {
				client_id: 'client_id_1',
				client_secret: 'client_secret_1'
			}
		});
	});

	describe('#authenticate', () => {
		it('returns launch modalService and remove authInprogress flag', () => {

			modalServiceMock.open.and.returnValue($q.when("something"));
			languageServiceMock.isInitialized.and.returnValue($q.when());

			authenticationService.setReAuthInProgress("authEntryPoint1");
			authenticationService.authenticate("api1/more").catch(() => {
				fail("failed to resolve");
			});

			expect(modalServiceMock.open).toHaveBeenCalledWith({
				cssClasses: "se-login-modal",
				templateInline: jasmine.any(String),
				// templateUrl: 'ssoLoginDialog.html',
				controller: jasmine.any(Function)
			});
			expect(languageServiceMock.isInitialized).toHaveBeenCalled();
			expect(authenticationService.isReAuthInProgress("authEntryPoint1")).toBeResolvedWithData(false);
		});

		it('when user changed then reloads the page when user is changed', () => {
			modalServiceMock.open.and.returnValue($q.when({userHasChanged: true}));
			languageServiceMock.isInitialized.and.returnValue($q.when());
			authenticationService.authenticate("api1more").catch(() => {
				fail("failed to resolve");
			}).then(() => {
				expect(routeMock.reload).toHaveBeenCalled();
			});
		});

		it('when user not changed then reloads the page when user is changed', () => {
			modalServiceMock.open.and.returnValue($q.when({userHasChanged: false}));
			languageServiceMock.isInitialized.and.returnValue($q.when());
			authenticationService.authenticate("api1more").catch(() => {
				fail("failed to resolve");
			}).then(() => {
				expect(routeMock.reload).not.toHaveBeenCalled();
			});
		});
	});

	it('should return false when the access_token is not found in storage', () => {
		const ENTRY_POINTS = ['entryPoint1'];
		spyOn(authenticationService, 'filterEntryPoints').and.returnValue($q.when(ENTRY_POINTS));
		storageServiceMock.getAuthToken.and.returnValue($q.when(null));

		expect(authenticationService.isAuthenticated('url')).toBeResolvedWithData(false);

		expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
		expect(storageServiceMock.getAuthToken).toHaveBeenCalledWith('entryPoint1');
	});

	it('should return true when the access_token is found in the storage', () => {

		const ENTRY_POINTS = ['entryPoint1'];
		spyOn(authenticationService, 'filterEntryPoints').and.returnValue($q.when(ENTRY_POINTS));

		const AUTH_TOKEN = {
			access_token: 'access-token1',
			token_type: 'bearer'
		};
		storageServiceMock.getAuthToken.and.returnValue($q.when(AUTH_TOKEN));

		expect(authenticationService.isAuthenticated('url')).toBeResolvedWithData(true);

		expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
		expect(storageServiceMock.getAuthToken).toHaveBeenCalledWith('entryPoint1');
	});

	it('should return false when the entry point is not found in the authentication', () => {

		spyOn(authenticationService, 'filterEntryPoints').and.returnValue($q.when(null));
		storageServiceMock.getAuthToken.and.returnValue($q.when(null));

		expect(authenticationService.isAuthenticated('url')).toBeResolvedWithData(false);

		expect(authenticationService.filterEntryPoints).toHaveBeenCalledWith('url');
		expect(storageServiceMock.getAuthToken).toHaveBeenCalledWith(null);
	});

	describe('logout', () => {
		describe('in non SSO', () => {
			beforeEach(() => {
				ssoAuthenticationHelper.isAutoSSOMain.and.returnValue(false);
			});
			it('will reload current page if current page is landing page', () => {
				$locationMock.url.and.callFake((arg: any) => {
					if (!arg) {
						return LANDING_PAGE_PATH;
					}
					return null;
				});

				authenticationService.logout().then(() => {
					expect($locationMock.url).toHaveBeenCalledWith();
					expect(routeMock.reload).toHaveBeenCalled();
				});
			});

			it('will reload current page if current page is empty', () => {
				$locationMock.url.and.callFake((arg: any) => {
					if (!arg) {
						return '';
					}
					return null;
				});

				authenticationService.logout().then(() => {
					expect($locationMock.url).toHaveBeenCalledWith();
					expect(routeMock.reload).toHaveBeenCalled();
				});
			});

			it('will redirect to landing page if current page is not landing page', () => {
				$locationMock.url.and.callFake((arg: any) => {
					if (!arg) {
						return '/somepage';
					}
					return null;
				});

				authenticationService.logout().then(() => {
					expect($locationMock.url.calls.count()).toBe(2);
					expect($locationMock.url.calls.argsFor(0)).toEqual([]);
					expect($locationMock.url.calls.argsFor(1)).toEqual([LANDING_PAGE_PATH]);
				});
			});
		});

		describe('in SSO', () => {
			beforeEach(() => {
				ssoAuthenticationHelper.isAutoSSOMain.and.returnValue(true);
			});

			it('will reload without SSO', () => {
				authenticationService.logout().then(() => {
					expect(ssoAuthenticationHelper.logout).toHaveBeenCalled();
					expect(routeMock.reload).not.toHaveBeenCalled();
				});
			});
		});

		it('WHEN logout is called THEN a LOGOUT event is raised and auth tokens removed from storage', () => {
			authenticationService.logout().then(() => {
				expect(crossFrameEventServiceMock.publish).toHaveBeenCalledWith(EVENTS.LOGOUT);
				expect(storageServiceMock.removeAllAuthTokens).toHaveBeenCalled();
			});
		});
	});
}); 
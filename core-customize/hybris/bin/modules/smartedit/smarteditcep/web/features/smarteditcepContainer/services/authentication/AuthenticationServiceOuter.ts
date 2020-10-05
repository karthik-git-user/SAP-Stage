/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as angular from "angular";
import * as lodash from 'lodash';

import {
	CrossFrameEventService,
	GatewayProxied,
	IAuthenticationService,
	IAuthToken,
	ISharedDataService,
	IStorageService,
	LanguageService,
	SeInjectable,
	StringUtils,
	SETTINGS_URI
} from 'smarteditcommons';

import {loginModalFactory, LoginData, LoginModalFeedback} from '../LoginModalService';

import {SSOAuthenticationHelper} from './SSOAuthenticationHelper';

import "./loginDialog.scss";

/* @internal */
interface IAuthenticationMapEntry {
	key: string;
	value: string;
}

/* @internal */
interface IAuthMap {
	[entryPoint: string]: string;
}

/* @internal */
export interface ICredentialsMap {
	[entryPoint: string]: ICredentialsMapRecord;
}

/* @internal */
export interface ICredentialsMapRecord {
	client_id: string;
	client_secret?: string;
}

/* 1) ngResource and ngRoute pulled transitively
 * 2) translationServiceModule is needed since the templates/modal/loginDialog.html template uses translate filter
 * Not declaring it will make UNIT fail.
 * 3) because of translationServiceModule pulling $http, one cannot wire here $modal, restServiceFactory or profileService
 * otherwise one ends up with cyclic reference. On then needs resort to dynamic 'runtime' injection via $injector.get
 */
@GatewayProxied(
	'filterEntryPoints',
	'isAuthEntryPoint',
	'authenticate',
	'logout',
	'isReAuthInProgress',
	'setReAuthInProgress',
	'isAuthenticated'
)
@SeInjectable()
export class AuthenticationService extends IAuthenticationService {
	constructor(
		private $q: angular.IQService,
		private $injector: angular.auto.IInjectorService,
		private $location: angular.ILocationService,
		private languageService: LanguageService,
		private modalService: any,
		private LANDING_PAGE_PATH: string,
		private DEFAULT_AUTHENTICATION_ENTRY_POINT: string,
		private DEFAULT_AUTH_MAP: {},
		private DEFAULT_CREDENTIALS_MAP: ICredentialsMap,
		private sharedDataService: ISharedDataService,
		private storageService: IStorageService,
		private crossFrameEventService: CrossFrameEventService,
		private EVENTS: any,
		private convertToArray: (objectToConver: any) => {
			key: string;
			value: any;
		}[],
		private merge: <T, U>(source: T, target: U) => T & U,
		private stringUtils: StringUtils,
		private sSOAuthenticationHelper: SSOAuthenticationHelper
	) {
		super();
	}

	filterEntryPoints(resource: string): angular.IPromise<string[]> {
		return this.sharedDataService.get('authenticationMap').then((authenticationMap: IAuthMap) => {
			authenticationMap = this.merge(authenticationMap || {}, this.DEFAULT_AUTH_MAP);
			return this.convertToArray(authenticationMap)
				.filter((entry: IAuthenticationMapEntry) => new RegExp(entry.key, 'g').test(resource))
				.map((element: IAuthenticationMapEntry) => element.value);
		});
	}

	isAuthEntryPoint(resource: string): angular.IPromise<boolean> {
		return this.sharedDataService.get('authenticationMap').then((authenticationMap: IAuthMap) => {
			const authEntryPoints = this.convertToArray(authenticationMap)
				.map((element: IAuthenticationMapEntry) => element.value);
			return authEntryPoints.indexOf(resource) > -1 || resource === this.DEFAULT_AUTHENTICATION_ENTRY_POINT;
		});
	}

	authenticate(resource: string): angular.IPromise<void> {
		return this._findLoginData(resource).then((loginData: LoginData) => {
			return this._launchAuth(loginData).then((modalFeedback: LoginModalFeedback) => {
				this.$q.when(this.crossFrameEventService.publish(this.EVENTS.AUTHORIZATION_SUCCESS, {
					userHasChanged: modalFeedback.userHasChanged
				})).then(() => {
					if (modalFeedback.userHasChanged) {
						this.crossFrameEventService.publish(this.EVENTS.USER_HAS_CHANGED);
					}
					/**
					 * We only need to reload when the user has changed and all authentication forms were closed.
					 * There can be many authentication forms if some modules use different (from default one) end points.
					 */
					const reauthInProcess = lodash
						.values(this.reauthInProgress)
						.some((inProcess: boolean) => inProcess);

					if (modalFeedback.userHasChanged && !reauthInProcess) {
						(this.$injector.get('$route') as angular.route.IRouteService).reload();
					}
				});
				this.reauthInProgress[loginData.authURI] = false;
			});
		});
	}

	logout(): angular.IPromise<any[]> {
		// First, indicate the services that SmartEdit is logging out. This should give them the opportunity to clean up.
		// NOTE: This is not synchronous since some clean-up might be lengthy, and logging out should be fast.
		return this.$q.when(this.crossFrameEventService.publish(this.EVENTS.LOGOUT)).finally(() => {
			this.storageService.removeAllAuthTokens();
			const currentLocation = this.$location.url();

			if (this.sSOAuthenticationHelper.isAutoSSOMain()) {
				this.sSOAuthenticationHelper.logout();
			} else {
				if (this.stringUtils.isBlank(currentLocation) || currentLocation === this.LANDING_PAGE_PATH) {
					(this.$injector.get('$route') as angular.route.IRouteService).reload();
				} else {
					this.$location.url(this.LANDING_PAGE_PATH);
				}
			}
		});
	}

	isReAuthInProgress(entryPoint: string): angular.IPromise<boolean> {
		return this.$q.when(this.reauthInProgress[entryPoint] === true);
	}

	setReAuthInProgress(entryPoint: string): angular.IPromise<void> {
		this.reauthInProgress[entryPoint] = true;
		return this.$q.when();
	}

	isAuthenticated(url: string): angular.IPromise<boolean> {
		return this.filterEntryPoints(url).then((entryPoints: string[]) => {
			const authURI = entryPoints && entryPoints[0];
			return this.$q.when(this.storageService.getAuthToken(authURI)).then((authToken: IAuthToken) => {
				return !!authToken;
			});
		});
	}

	/*
	* will try determine first relevant authentication entry point from authenticationMap and retrieve potential client credentials to be added on top of user credentials
	*/
	private _findLoginData(resource: string): angular.IPromise<LoginData> {
		return this.filterEntryPoints(resource).then((entryPoints: string[]) => {
			return this.sharedDataService.get('credentialsMap').then((credentialsMap) => {
				credentialsMap = angular.extend(credentialsMap || {}, this.DEFAULT_CREDENTIALS_MAP);
				const authURI = entryPoints[0];
				return {
					authURI,
					clientCredentials: (credentialsMap as ICredentialsMap)[authURI]
				};
			});
		});
	}

	private _launchAuth(loginData: LoginData): angular.IPromise<any> {
		return this.languageService.isInitialized()
			.then(() => this.$q.all([
				this.$q.when(this.storageService.isInitialized()),
				this.$q.when(
					fetch(SETTINGS_URI)
						.then((res: any) => res.json())
						.then((response: any) => response['smartedit.sso.enabled'] === true || response['smartedit.sso.enabled'] === "true"))
			]))
			.then((result: [boolean, boolean]) =>
				this.modalService.open({
					cssClasses: 'se-login-modal',
					templateInline: require('./ssoLoginDialog.html'),
					controller: loginModalFactory({
						...loginData,
						initialized: result[0],
						ssoEnabled: result[1]
					})
				})
			);
	}
}

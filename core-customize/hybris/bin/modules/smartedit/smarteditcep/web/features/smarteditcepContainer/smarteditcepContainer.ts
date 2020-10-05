/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

import {SeModule} from 'smarteditcommons';
import {AuthenticationService, SSOAuthenticationHelper} from './services/authentication';
import {SmarteditcepCommonsModule} from 'smarteditcepcommons';

/**
 * @ngdoc overview
 * @name smarteditcepContainer
 * @description
 * SmarteditcepContainer Module
 */
@SeModule({
	imports: [
		SmarteditcepCommonsModule,
		'smarteditServicesModule'
	],
	providers: [
		SSOAuthenticationHelper,
		AuthenticationService
	]
})
export class SmarteditcepContainer {}


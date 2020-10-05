export const SSO_AUTHENTICATION_ENTRY_POINT = '/samlsinglesignon/saml/smarteditcep';

/**
 * @ngdoc overview
 * @name SmarteditcepCommonsModule
 * @description
 * SmarteditcepCommonsModule Module
 */

import {diNameUtils, SeModule} from 'smarteditcommons';
@SeModule({
	providers: [
		diNameUtils.makeValueProvider({SSO_AUTHENTICATION_ENTRY_POINT}),
	]
})
export class SmarteditcepCommonsModule {}
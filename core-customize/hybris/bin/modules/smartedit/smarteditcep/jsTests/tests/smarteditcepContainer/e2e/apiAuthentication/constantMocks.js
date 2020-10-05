/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

angular.module('e2eSSOMocks', ['smarteditcepCommonsModule'])
    .constant('SSO_AUTHENTICATION_ENTRY_POINT', '/jsTests/tests/smarteditcepContainer/e2e/apiAuthentication/ssoDialog.html');

try {
    angular.module('smarteditloader').requires.push('e2eSSOMocks');
} catch (e) {}
try {
    angular.module('smarteditcontainer').requires.push('e2eSSOMocks');
} catch (e) {}

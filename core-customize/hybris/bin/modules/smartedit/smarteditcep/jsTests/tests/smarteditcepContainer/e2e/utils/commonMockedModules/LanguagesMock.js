/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
angular.module('LanguageMockModule', ['ngMockE2E', 'resourceLocationsModule', 'functionsModule'])
    .run(function($httpBackend, resourceLocationToRegex, LANGUAGE_RESOURCE_URI, I18N_LANGUAGES_RESOURCE_URI) {
        $httpBackend.whenGET(resourceLocationToRegex(LANGUAGE_RESOURCE_URI)).respond(function() {
            return [200, {
                languages: [{
                    nativeName: 'English',
                    isocode: 'en',
                    name: 'English',
                    required: true
                }]
            }];
        });

        $httpBackend.whenGET(resourceLocationToRegex(I18N_LANGUAGES_RESOURCE_URI)).respond({
            languages: [{
                "isoCode": "en",
                "name": "English"
            }, {
                "isoCode": "fr",
                "name": "French"
            }]
        });
    });

try {
    angular.module('smarteditloader').requires.push('LanguageMockModule');
    angular.module('smarteditcontainer').requires.push('LanguageMockModule');
} catch (ex) {}

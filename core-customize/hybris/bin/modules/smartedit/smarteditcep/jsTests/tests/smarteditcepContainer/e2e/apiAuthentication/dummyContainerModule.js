/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
angular.module('dummyContainer', ['ngRoute', 'smarteditServicesModule'])
    .config(function($routeProvider) {
        $routeProvider.when('/customView', {
            templateUrl: 'web/customView.html'
        });
    })
    .run(
        ['$templateCache', function($templateCache) {
            $templateCache.put('web/customView.html',
                "<div class= \"customView\"> \n" +
                "customView" +
                "</div>" +
                "<iframe id='ySmartEditFrame' src=\"/jsTests/tests/smarteditcepContainer/e2e/apiAuthentication/customiframe.html\" style=\"width:100%;height:800px\"></iframe>"
            );
        }]
    );

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = (function() {

    var PageObject = {

        actions: {
            getAndWaitForWholeApp: function(url) {
                return browser.get(url).then(function() {
                    return browser.waitForWholeAppToBeReady();
                });
            },
            getAndWaitForLogin: function(url) {
                return browser.get(url).then(function() {
                    browser.clearLocalStorage();
                    browser.waitForAngularEnabled(false);
                    return this.waitForLoginModal();
                }.bind(this));
            },
            waitForLoginModal: function() {
                return browser.wait(protractor.ExpectedConditions.elementToBeClickable(element(by.css('input[id^="username_"]'))), 20000, "Timed out waiting for username input").then(function() {
                    return browser.waitForAngular();
                });
            },
            setWaitForPresence: function(implicitWait) {
                return browser.driver.manage().timeouts().implicitlyWait(implicitWait);
            },
            clearCookies: function() {
                return browser.driver.wait(function() {
                    return browser.driver.manage().deleteAllCookies().then(function() {
                        return true;
                    }, function(err) {
                        throw err;
                    });
                }, 5000, 'Timed out waiting for cookies to clear');
            }
        },

        assertions: {},

        constants: {},

        elements: {}

    };

    return PageObject;

})();

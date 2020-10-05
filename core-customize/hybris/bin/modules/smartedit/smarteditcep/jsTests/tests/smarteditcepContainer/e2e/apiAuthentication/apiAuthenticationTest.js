/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
var page = require('./../utils/components/Page.js');
var login = require('./../utils/components/Login.js');
var perspectives = require('./../utils/components/Perspectives.js');

function ssoWillFail(ssoWillFail) {
    browser.executeScript(
        'window.sessionStorage.setItem("sso.authenticate.failure", arguments[0])',
        ssoWillFail
    );
}

describe("Authentication", function() {
    describe('with credentials', function() {
        beforeEach(function() {
            ssoWillFail(false);
            page.actions.getAndWaitForLogin('jsTests/tests/smarteditcepContainer/e2e/apiAuthentication/apiAuthenticationTest.html');
        });
        afterEach(function() {
            browser.waitForAngularEnabled(true);
        });

        it("WHEN the user is not logged in THEN the user is presented with a login dialog", function() {
            expect(login.mainLoginUsernameInput().isPresent()).toBe(true);
            expect(login.mainLoginPasswordInput().isPresent()).toBe(true);
            expect(login.mainLoginSubmitButton().isPresent()).toBe(true);
        });

        it("WHEN the user submits an empty auth form THEN an error is displayed", function() {
            login.mainLoginSubmitButton().click();
            expect(login.authenticationError().getText()).toBe('Username and password required');
        });

        it("WHEN the user submits incorrect credentials THEN an error is displayed", function() {
            login.loginAsInvalidUser();
            expect(login.authenticationError().getText()).toBe('Invalid username or password');
        });

        describe('After Login', function() {
            beforeEach(function(done) {
                login.loginAsCmsManager().then(function() {
                    return perspectives.actions.selectPerspective(perspectives.constants.DEFAULT_PERSPECTIVES.ALL).then(function() {
                        done();
                    });
                });
            });

            afterEach(function() {
                login.logoutUser();
            });

            it("WHEN the user is not authenticated to fake1 or fake2 API THEN fake 1 nor fake 2 are visible", function() {
                browser.switchToIFrame();
                expect(browser.isAbsent(by.id('fake1'))).toBe(true);
                expect(browser.isAbsent(by.id('fake2'))).toBe(true);

                browser.switchToParent();
                login.loginToAuthForFake2();
                browser.switchToIFrame();
                expect(browser.isAbsent(by.id('fake1'))).toBe(true);
                expect(browser.isPresent(by.id('fake2'))).toBe(true);

                browser.switchToParent();
                login.loginToAuthForFake1();
                browser.switchToIFrame();
                expect(browser.isPresent(by.id('fake1'))).toBe(true);
                expect(browser.isPresent(by.id('fake2'))).toBe(true);
            });
        });
    });

    describe('Manual Login with SSO', function() {
        beforeEach(function(done) {
            ssoWillFail(false);
            page.actions.getAndWaitForLogin(
                'jsTests/tests/smarteditcepContainer/e2e/apiAuthentication/apiAuthenticationTest.html'
            );
            login.loginWithSSO().then(function() {
                browser.waitForWholeAppToBeReady();
                done();
            });
        });

        it('login is successful', function() {
            // verified by beforeEach that waits until no modal
            login.logoutUser();
        });

        it('after expiry, auto-reauth with SSO', function() {
            browser.clearLocalStorage();
            browser.waitUntilModalAppears();
            browser.waitForWholeAppToBeReady().then(function() {
                login.logoutUser();
            });
        });

        it("logout doesn't auto-reauth", function() {
            login.logoutUser();
            browser.waitUntilModalAppears();
            expect(login.mainLoginSubmitSSOButton().isPresent()).toBe(true);
        });
    });

    describe('Failure of Manual Login with SSO', function() {
        beforeEach(function(done) {
            page.actions.getAndWaitForLogin(
                'jsTests/tests/smarteditcepContainer/e2e/apiAuthentication/apiAuthenticationTest.html'
            );
            ssoWillFail(true);
            login.loginWithSSO().then(function() {
                done();
            });
        });

        it('SSO failure will show in the standard form', function() {
            expect(login.getAuthenticationErrorText()).toBe('SSO authentication issue');
        });

        afterEach(function() {
            ssoWillFail(false);
        });
    });

    describe('Auto Login with SSO (ex: from cloud portal)', function() {
        beforeEach(function(done) {
            ssoWillFail(false);
            page.actions.getAndWaitForWholeApp(
                'jsTests/tests/smarteditcepContainer/e2e/apiAuthentication/apiAuthenticationTest.html?sso'
            );
            browser.waitUntilNoModal();
            done();
        });

        it('login is successful', function() {
            // verified by beforeEach that waits until no modal
            login.logoutUser();
        });

        it('after expiry, auto-reauth with SSO', function() {
            browser.clearLocalStorage();
            browser.waitUntilModalAppears();
            browser.waitForWholeAppToBeReady().then(function() {
                login.logoutUser();
            });
        });

        it("logout doesn't auto-reauth", function() {
            login.logoutUser();
            browser.waitUntilModalAppears();
            expect(login.mainLoginSubmitSSOButton().isPresent()).toBe(true);
        });
    });
});

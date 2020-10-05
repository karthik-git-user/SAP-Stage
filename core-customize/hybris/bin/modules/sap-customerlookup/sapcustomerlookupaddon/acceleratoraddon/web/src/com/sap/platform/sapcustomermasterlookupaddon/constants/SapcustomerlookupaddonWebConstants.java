/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomermasterlookupaddon.constants;

/**
 * Global class for all Sapcustomerlookupaddon web constants. You can add global constants for your extension into this
 * class.
 */
public final class SapcustomerlookupaddonWebConstants // NOSONAR
{

    public static final String FORM_GLOBAL_ERROR = "form.global.error";

    public static final String CONSENT_FORM_GLOBAL_ERROR = "consent.form.global.error";

    public static final String PAGES_ACCOUNT_ACCOUNT_LOGIN_PAGE = "pages/account/accountLoginPage";
    
    public static final String COOKIE_DATA_DECODE_ERROR = "Cookie Data could not be decoded";

    public static final String COOKIE_DATA_MAPPING_ERROR = "Cookie Data could not be mapped into the Object";

    public static final String ANONYMOUS_COOKIE_CREATE_ERROR = "Error occurred while creating Anonymous cookie consents";

    private SapcustomerlookupaddonWebConstants() {
        //empty to avoid instantiating this constant class
    }

    // implement here constants used by this extension
}

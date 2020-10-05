/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.facade;

import de.hybris.platform.commercefacades.consent.ConsentFacade;

/**
 * Consent functionality for Customer master lookup
 */
public interface CustomerMasterConsentFacade extends ConsentFacade {
    /**
     * Give consent for registered user in storefront.
     *
     * @param emailId                -customer email ID
     * @param consentTemplateId      - Template ID
     * @param consentTemplateVersion - Template version
     */
    void giveConsentForEmailId(final String emailId, final String consentTemplateId, final Integer consentTemplateVersion);
}

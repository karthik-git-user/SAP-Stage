/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.facade.impl;

import com.sap.platform.sapcustomerlookupservice.facade.CustomerMasterConsentFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.consent.impl.DefaultConsentFacade;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default impl to give consent to registered user
 */
public class DefaultCustomerMasterConsentFacade extends DefaultConsentFacade implements CustomerMasterConsentFacade {

    @Override
    public void giveConsentForEmailId(final String emailId, final String consentTemplateId, final Integer consentTemplateVersion) {
        validateParameterNotNull(consentTemplateId, "Parameter consentTemplateId must not be null");
        validateParameterNotNull(consentTemplateVersion, "Parameter consentTemplateVersion must not be null");

        final CustomerModel customer = (CustomerModel) getUserService().getUserForUID(emailId);
        final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();

        final ConsentTemplateModel consentTemplate = getCommerceConsentService().getConsentTemplate(consentTemplateId,
                consentTemplateVersion, baseSite);
        getCommerceConsentService().giveConsent(customer, consentTemplate);
    }


}

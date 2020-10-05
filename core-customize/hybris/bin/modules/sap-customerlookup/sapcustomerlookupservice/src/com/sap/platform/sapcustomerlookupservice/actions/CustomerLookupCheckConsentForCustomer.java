/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.actions;

import com.sap.platform.sapcustomerlookupservice.constants.SapcustomerlookupserviceConstants;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

public class CustomerLookupCheckConsentForCustomer extends AbstractSimpleDecisionAction<StoreFrontCustomerProcessModel> {
    private CommerceConsentService commerceConsentService;

    public void setCommerceConsentService(CommerceConsentService commerceConsentService) {
        this.commerceConsentService = commerceConsentService;
    }

    @Override
    public Transition executeAction(final StoreFrontCustomerProcessModel businessProcessModel) {
        final CustomerModel customerModel = businessProcessModel.getCustomer();
        final ConsentTemplateModel consentTemplate = commerceConsentService.getConsentTemplate(SapcustomerlookupserviceConstants.CMS_LOOKUP_ENABLE_CONSENT_ID,
                1, businessProcessModel.getSite());

        boolean activeConsent = commerceConsentService.hasEffectivelyActiveConsent(customerModel, consentTemplate);

        if (activeConsent) {
            return Transition.OK;
        } else {
            return Transition.NOK;
        }


    }
}

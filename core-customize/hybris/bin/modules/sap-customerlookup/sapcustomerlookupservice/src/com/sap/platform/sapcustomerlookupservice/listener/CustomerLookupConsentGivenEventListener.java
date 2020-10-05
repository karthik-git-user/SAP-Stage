/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.listener;

import com.sap.platform.sapcustomerlookupservice.constants.SapcustomerlookupserviceConstants;
import com.sap.platform.sapcustomerlookupservice.events.CustomerLookupEvent;
import de.hybris.platform.commerceservices.event.*;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;


public class CustomerLookupConsentGivenEventListener extends AbstractEventListener<ConsentGivenEvent> {


    private BaseSiteService baseSiteService;
    private CommerceCommonI18NService commerceCommonI18NService;
    private BaseStoreService baseStoreService;
    private EventService eventService;


    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public void setCommerceCommonI18NService(CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }

    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    @Override
    protected void onEvent(final ConsentGivenEvent consentGivenEvent) {
        ConsentModel consentModel = consentGivenEvent.getConsent();
        if (consentModel.getConsentTemplate().getId().equals(SapcustomerlookupserviceConstants.CMS_LOOKUP_ENABLE_CONSENT_ID)) {
            raiseCustomerMasterLookupEvent(new CustomerLookupEvent(), consentModel.getCustomer());
        }


    }

    private void raiseCustomerMasterLookupEvent(final AbstractCommerceUserEvent customerLookupdEvent, final CustomerModel customerModel) {

        eventService.publishEvent(initializeEvent(customerLookupdEvent, customerModel));

    }


    protected AbstractCommerceUserEvent initializeEvent(final AbstractCommerceUserEvent event, final CustomerModel customerModel) {
        event.setBaseStore(baseStoreService.getCurrentBaseStore());
        event.setSite(baseSiteService.getCurrentBaseSite());
        event.setCustomer(customerModel);
        event.setLanguage(commerceCommonI18NService.getCurrentLanguage());
        event.setCurrency(commerceCommonI18NService.getCurrentCurrency());
        return event;
    }


}

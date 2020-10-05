/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.listener;

import com.sap.platform.sapcustomerlookupservice.token.DefaultSecureTokenServiceForCMS;
import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.event.RegisterEvent;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;


/**
 * Listener for customer registration events.
 */
public class CustomerDoubleOptInRegistrationEventListener extends AbstractAcceleratorSiteEventListener<RegisterEvent> {

    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private DefaultSecureTokenServiceForCMS secureTokenForCMS;

    public void setSecureTokenForCMS(DefaultSecureTokenServiceForCMS secureTokenForCMS) {
        this.secureTokenForCMS = secureTokenForCMS;
    }


    public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }


    /**
     * @param modelService the modelService to set
     */

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    protected void onSiteEvent(final RegisterEvent registerEvent) {

        final StoreFrontCustomerProcessModel storeFrontCustomerProcessModel = (StoreFrontCustomerProcessModel) businessProcessService
                .createProcess(
                        "customerDoubleOptInRegistrationEmail-Process-" + registerEvent.getCustomer().getUid() + "-" + System.currentTimeMillis(),
                        "customerDoubleOptInRegistrationEmail-Process");
        storeFrontCustomerProcessModel.setSite(registerEvent.getSite());
        storeFrontCustomerProcessModel.setCustomer(registerEvent.getCustomer());
        storeFrontCustomerProcessModel.setLanguage(registerEvent.getLanguage());
        storeFrontCustomerProcessModel.setCurrency(registerEvent.getCurrency());
        storeFrontCustomerProcessModel.setStore(registerEvent.getBaseStore());
        modelService.save(storeFrontCustomerProcessModel);

        if (registerEvent.getBaseStore().isCmsEmailVerificationEnabled()) {
            secureTokenForCMS.generateAndSaveSecureToken(registerEvent.getCustomer());
            registerEvent.getCustomer().setLoginDisabled(true);
        }
        modelService.save(registerEvent.getCustomer());
        businessProcessService.startProcess(storeFrontCustomerProcessModel);

    }

    @Override
    protected SiteChannel getSiteChannelForEvent(final RegisterEvent event) {
        final BaseSiteModel site = event.getSite();
        ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
        return site.getChannel();
    }

}

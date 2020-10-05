/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.listener;

import com.sap.platform.sapcustomerlookupservice.events.CustomerLookupEvent;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * This listener start the process to consume master look up service and replication
 */
public class CustomerLookupProcessEventListener extends AbstractEventListener<CustomerLookupEvent> {
    private ModelService modelService;
    private BusinessProcessService businessProcessService;


    public void setBusinessProcessService(BusinessProcessService businessProcessService) {
        this.businessProcessService = businessProcessService;
    }


    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    protected void onEvent(final CustomerLookupEvent customerLookupEvent) {
        if (customerLookupEvent.getBaseStore().isCmsLookupEnabled()) {
            final StoreFrontCustomerProcessModel storeFrontCustomerLookUpProcessModel = (StoreFrontCustomerProcessModel) businessProcessService
                    .createProcess(
                            "customerLookupService-process-" + customerLookupEvent.getCustomer().getUid() + "-" + System.currentTimeMillis(),
                            "customerLookupService-process");
            storeFrontCustomerLookUpProcessModel.setSite(customerLookupEvent.getSite());
            storeFrontCustomerLookUpProcessModel.setCustomer(customerLookupEvent.getCustomer());
            storeFrontCustomerLookUpProcessModel.setLanguage(customerLookupEvent.getLanguage());
            storeFrontCustomerLookUpProcessModel.setCurrency(customerLookupEvent.getCurrency());
            storeFrontCustomerLookUpProcessModel.setStore(customerLookupEvent.getBaseStore());
            modelService.save(storeFrontCustomerLookUpProcessModel);
            businessProcessService.startProcess(storeFrontCustomerLookUpProcessModel);
        }
    }


}

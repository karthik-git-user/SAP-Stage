/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.actions;

import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class CustomerMasterLookupCheckEmailVerificationForCustomer extends AbstractSimpleDecisionAction<StoreFrontCustomerProcessModel> {

    @Override
    public AbstractSimpleDecisionAction.Transition executeAction(final StoreFrontCustomerProcessModel businessProcessModel) {
        final CustomerModel customerModel = businessProcessModel.getCustomer();
        validateParameterNotNullStandardMessage("customerModel", customerModel);
        if (customerModel.getCmsEmailVerificationTimestamp() != null || !businessProcessModel.getStore().isCmsEmailVerificationEnabled()) {
            return Transition.OK;
        } else {
            return Transition.NOK;
        }


    }
}

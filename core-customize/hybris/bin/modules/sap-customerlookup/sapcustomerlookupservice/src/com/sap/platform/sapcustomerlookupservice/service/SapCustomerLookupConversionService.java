/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.service;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;

/**
 * To convert customer model to SAPCpiOutboundCustomerModel
 */
public interface SapCustomerLookupConversionService {

    /**
     * convertCustomerToSapCpiLookupCustomer
     * @param customerModel CustomerModel
     * @return SAPCpiOutboundCustomerModel
     */
    SAPCpiOutboundCustomerModel convertCustomerToSapCpiLookupCustomer(CustomerModel customerModel);
}

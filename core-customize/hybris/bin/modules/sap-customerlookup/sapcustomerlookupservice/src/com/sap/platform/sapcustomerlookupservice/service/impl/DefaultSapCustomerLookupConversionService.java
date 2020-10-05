/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.service.impl;

import com.sap.platform.sapcustomerlookupservice.service.SapCustomerLookupConversionService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;

import java.util.Set;

/**
 * This conversion is used to set customer lookup payload
 */
public class DefaultSapCustomerLookupConversionService implements SapCustomerLookupConversionService {


    private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCustomerLookupConversionService.class);

    private SAPGlobalConfigurationDAO globalConfigurationDAO;
    private ModelService modelService;
    private CustomerNameStrategy customerNameStrategy;

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
        this.customerNameStrategy = customerNameStrategy;
    }

    public void setGlobalConfigurationDAO(SAPGlobalConfigurationDAO globalConfigurationDAO) {
        this.globalConfigurationDAO = globalConfigurationDAO;
    }

    @Override
    public SAPCpiOutboundCustomerModel convertCustomerToSapCpiLookupCustomer(CustomerModel customerModel) {
        final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer =modelService.create(SAPCpiOutboundCustomerModel.class);
        final Set<SAPLogicalSystemModel> logicalSystems = globalConfigurationDAO.getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig();

        if (logicalSystems == null || logicalSystems.isEmpty()) {

            LOG.error("No Logical system is maintained in back-office");
            return null;

        }

        final SAPLogicalSystemModel logicalSystem = logicalSystems.stream().filter(ls -> ls.isDefaultLogicalSystem()).findFirst().orElse(null);

        if (logicalSystem == null) {
            LOG.error("No Default Logical system is maintained in back-office ");

            return null;
        }


        final SAPCpiOutboundConfigModel config = modelService.create(SAPCpiOutboundConfigModel.class);

        config.setReceiverName(logicalSystem.getSapLogicalSystemName());
        config.setReceiverPort(logicalSystem.getSapLogicalSystemName());
        config.setSenderName(logicalSystem.getSenderName());
        config.setSenderPort(logicalSystem.getSenderPort());
        config.setUrl(logicalSystem.getSapHTTPDestination().getTargetURL());
        config.setUsername(logicalSystem.getSapHTTPDestination().getUserid());

        sapCpiOutboundCustomer.setSapCpiConfig(config);

        // Customer
        final String[] names = customerNameStrategy.splitName(customerModel.getName());
        sapCpiOutboundCustomer.setCustomerId(customerModel.getCustomerID());
        sapCpiOutboundCustomer.setUid(customerModel.getUid());
        sapCpiOutboundCustomer.setFirstName(names[0]);
        sapCpiOutboundCustomer.setLastName(names[1]);

        return sapCpiOutboundCustomer;
    }
}

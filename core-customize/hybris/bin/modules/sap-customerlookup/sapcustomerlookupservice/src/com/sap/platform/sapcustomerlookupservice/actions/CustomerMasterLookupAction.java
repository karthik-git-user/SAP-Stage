/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.actions;


import com.sap.platform.sapcustomerlookupservice.service.SapCustomerLookupConversionService;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import org.apache.log4j.Logger;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;


/**
 * Responsible for executing action to fetch look up data and enriching the address model
 *
 */
public class CustomerMasterLookupAction extends AbstractSimpleDecisionAction<StoreFrontCustomerProcessModel> {

    private static final Logger LOG = Logger.getLogger(CustomerMasterLookupAction.class);
    public static final String SAP_CUSTOMER_LOOKUP_OUTBOUND = "OutboundB2CCustomerLookup";
    public static final String SAP_CUSTOMER_LOOKUP_MDM_DESTINATION = "SAPCustomerLookupMDMDestination";

    private OutboundServiceFacade outboundServiceFacade;

    private SapCustomerLookupConversionService sapCustomerLookupConversionService;

    public void setSapCustomerLookupConversionService(SapCustomerLookupConversionService sapCustomerLookupConversionService) {
        this.sapCustomerLookupConversionService = sapCustomerLookupConversionService;
    }

    public void setOutboundServiceFacade(OutboundServiceFacade outboundServiceFacade) {
        this.outboundServiceFacade = outboundServiceFacade;
    }



    @Override
    public Transition executeAction(final StoreFrontCustomerProcessModel businessProcessModel) {


        final CustomerModel customerModel = businessProcessModel.getCustomer();

        final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = sapCustomerLookupConversionService.convertCustomerToSapCpiLookupCustomer(customerModel);

        try {
            outboundServiceFacade.send(sapCpiOutboundCustomer, SAP_CUSTOMER_LOOKUP_OUTBOUND, SAP_CUSTOMER_LOOKUP_MDM_DESTINATION).subscribe( // onNext
                    responseEntityMap -> {
                        if (isSentSuccessfully(responseEntityMap)) {
                            LOG.info(String.format("The customer [%s] has been sent to the SAP MDM backend through SCPI! %n%s",
                                    customerModel.getCustomerID(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));


                        } else {
                            LOG.error(String.format("The customer [%s] has not been sent to the SAP MDM backend! %n%s",
                                    customerModel.getCustomerID(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE)));

                        }
                    }

                    // onError
                    , error -> {
                        throw new SystemException("Error while calling SCPI");

                    }


            );
        } catch (SystemException error) {
            LOG.error(String.format("The customer lookup query [%s] has not been sent to the SAP MDM backend through SCPI! %n%s",
                    customerModel.getCustomerID(), error.getMessage()), error);
            return Transition.NOK;
        } ;


        return Transition.OK;
    }


}




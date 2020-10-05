/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.facade.impl;

import com.sap.hybris.saprevenueclouddpaddon.facade.SapRevenueCloudDigitalPaymentFacade;
import com.sap.hybris.saprevenueclouddpaddon.service.SapRevenueCloudDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.facade.CisSapDigitalPaymentFacade;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Sap Revenue Cloud Digital Payment default implementation for {@link SapRevenueCloudDigitalPaymentFacade}
 */
public class DefaultSapRevenueCloudDigitalPaymentFacade implements SapRevenueCloudDigitalPaymentFacade {

    private SapRevenueCloudDigitalPaymentService sapRevenueCloudDigitalPaymentService;
    private SapDigitalPaymentService sapDigitalPaymentService;
    private CisSapDigitalPaymentFacade cisSapDigitalPaymentFacade;
    private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> ccPaymentInfoConverter;

    @Override
    public CCPaymentInfoData checkPaymentCardRegistration(String sessionId) {
        CreditCardPaymentInfoModel creditCardPaymentInfoModel = getSapRevenueCloudDigitalPaymentService().checkPaymentCardRegistration(sessionId);
        return creditCardPaymentInfoModel!=null ? getCcPaymentInfoConverter().convert(creditCardPaymentInfoModel) : null;
    }

    @Override
    public String getCardRegistrationUrl()
    {
        return getSapDigitalPaymentService().getCardRegistrationUrl();
    }


    @Override
    public String getSapDigitalPaymentRegisterCardSessionId() {
        return getCisSapDigitalPaymentFacade().getSapDigitalPaymentRegisterCardSession();
    }

    /**
     * @return the sapDigitalPaymentService
     */
    public SapDigitalPaymentService getSapDigitalPaymentService()
    {
        return sapDigitalPaymentService;
    }


    /**
     * @param sapDigitalPaymentService
     *           the sapDigitalPaymentService to set
     */
    public void setSapDigitalPaymentService(final SapDigitalPaymentService sapDigitalPaymentService)
    {
        this.sapDigitalPaymentService = sapDigitalPaymentService;
    }

    /**
     * @return cisSapDigitalPaymentFacade
     */
    public CisSapDigitalPaymentFacade getCisSapDigitalPaymentFacade() {
        return cisSapDigitalPaymentFacade;
    }

    /**
     * @param cisSapDigitalPaymentFacade {@link CisSapDigitalPaymentFacade}
     */
    public void setCisSapDigitalPaymentFacade(CisSapDigitalPaymentFacade cisSapDigitalPaymentFacade) {
        this.cisSapDigitalPaymentFacade = cisSapDigitalPaymentFacade;
    }

    /**
     * @return sapRevenueCloudDigitalPaymentService
     */
    public SapRevenueCloudDigitalPaymentService getSapRevenueCloudDigitalPaymentService() {
        return sapRevenueCloudDigitalPaymentService;
    }

    /**
     * @param sapRevenueCloudDigitalPaymentService revenue cloud digital payment service
     */
    public void setSapRevenueCloudDigitalPaymentService(SapRevenueCloudDigitalPaymentService sapRevenueCloudDigitalPaymentService) {
        this.sapRevenueCloudDigitalPaymentService = sapRevenueCloudDigitalPaymentService;
    }

    /**
     * @return ccPaymentInfoConverter
     */
    public Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCcPaymentInfoConverter() {
        return ccPaymentInfoConverter;
    }

    /**
     * @param ccPaymentInfoConverter credit card payment information converter
     */
    public void setCcPaymentInfoConverter(Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> ccPaymentInfoConverter) {
        this.ccPaymentInfoConverter = ccPaymentInfoConverter;
    }
}

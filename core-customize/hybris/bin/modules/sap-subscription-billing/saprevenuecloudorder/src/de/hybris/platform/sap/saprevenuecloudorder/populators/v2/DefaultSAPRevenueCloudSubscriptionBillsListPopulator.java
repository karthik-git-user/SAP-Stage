/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.v2;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.BillsList;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;

import java.math.BigDecimal;

import static de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants.SUBSCRIPTION_BILL_DATE_FORMAT;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Populate DTO {@link SubscriptionBillingData} with data from {@link BillsList}.
 *
 * @param <S> source class
 * @param <T> target class
 */

public class DefaultSAPRevenueCloudSubscriptionBillsListPopulator<S extends BillsList, T extends SubscriptionBillingData> implements Populator<S, T>
{
    private PriceDataFactory priceDataFactory;
    private CommonI18NService commonI18NService;


    @Override
    public void populate(S source, T target) throws ConversionException
    {
        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);

        final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getNetAmount().getAmount()),
                getCommonI18NService().getCurrentCurrency().getIsocode());
        target.setSubscriptionBillDate(SapRevenueCloudSubscriptionUtil.stringToDate(source.getBillingDate(), SUBSCRIPTION_BILL_DATE_FORMAT));
        target.setPrice(priceData);
        target.setBillingDate(source.getBillingDate());
        target.setBillingId(((Integer)source.getDocumentNumber()).toString());
        target.setItems(((Integer)source.getBillItems().size()).toString());
    }

    public CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }

    public PriceDataFactory getPriceDataFactory()
    {
        return priceDataFactory;
    }

    public void setPriceDataFactory(PriceDataFactory priceDataFactory)
    {
        this.priceDataFactory = priceDataFactory;
    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.bill.v2;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.MonetaryAmount;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.BillsList;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import static de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants.SUBSCRIPTION_BILL_DATE_FORMAT;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

/**
 * Populate DTO {@link SubscriptionBillingData} with data from {@link BillsList}.
 *
 * @param <S> source class
 * @param <T> target class
 */

public class DefaultBillSummaryPopulator<S extends Bill, T extends SubscriptionBillingData> implements Populator<S, T> {
    private PriceDataFactory priceDataFactory;
    private CommonI18NService commonI18NService;


    @Override
    public void populate(S source, T target) throws ConversionException {
        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);

        //Subscription Billing Date
        Date billingDate = source.getBillingDate();

        //Billing Id
        String billingId = Objects.toString(source.getDocumentNumber(), null);

        //Items
        String numOfItems = ((Integer) source.getBillItems().size()).toString();


        PriceData priceData = new PriceData();
        //Price Data
        //Billing type = Charge Total Amount is either Gross amount or Net Amount
        if (source.getBillingType().equals("CHARGE")) {
            MonetaryAmount totalAmount = source.getGrossAmount();
            if (totalAmount == null) {
                totalAmount = source.getNetAmount();
            }
            priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(totalAmount.getAmount()),
                    getCommonI18NService().getCurrentCurrency().getIsocode());
        }

        //Billing Type = Credit
        if (source.getBillingType().equals("CREDIT")) {
            MonetaryAmount totalAmount = source.getGrossCreditAmount();
            if (totalAmount == null) {
                totalAmount = source.getNetCreditAmount();
            }
            priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(totalAmount.getAmount()),
                    getCommonI18NService().getCurrentCurrency().getIsocode());
        }


        //Billing Date
        String strBillingDate = SapRevenueCloudSubscriptionUtil.dateToString(billingDate, SUBSCRIPTION_BILL_DATE_FORMAT);

        //Prepare Target
        target.setSubscriptionBillDate(billingDate);
        target.setBillingDate(strBillingDate);
        target.setBillingId(billingId);
        target.setPrice(priceData);
        target.setItems(numOfItems);
    }

    //<editor-fold desc="Setters and Getters">
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }
    //</editor-fold>
}

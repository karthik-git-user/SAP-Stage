/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.subscription;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.PaginationResult;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Charge;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.RatingPeriod;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Credit;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.service.BillService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageUnitData;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

/**
 * Populate DTO {@link SubscriptionData} with data from {@link Subscription}.
 *
 * @param <S> source class
 * @param <T> target class
 */
public class DefaultSubscriptionPopulator<S extends Subscription, T extends SubscriptionData>
        implements Populator<S, T> {

    private static final Logger LOG = Logger.getLogger(DefaultSubscriptionPopulator.class);

    private SapRevenueCloudProductService sapRevenueCloudProductService;
    private PriceDataFactory priceDataFactory;
    private CommonI18NService commonI18NService;
    private BillService billService;

    @Override
    public void populate(S source, T target) throws ConversionException {

		populateCurrentUsage(source.getSubscriptionId(), target);

    }

    private void populateCurrentUsage(String subscriptionId, SubscriptionData subscriptionData) {

        //Fetch Data
        String currentDate = LocalDate.now(ZoneOffset.UTC).toString();
        List<Bill> bills;
        try {
            PaginationResult<List<Bill>> billsPage = billService.getBillsPageBySubscriptionId(
                    subscriptionId,
                    currentDate,
                    null,
                    0,
                    Integer.MAX_VALUE,
                    null);
            bills = billsPage.getResult();

        } catch (SubscriptionServiceException e) {
            LOG.error("Cannot find current usage for Current Usage.",e);
            return;

        }

        //Populate Current Usage
        for (Bill bill : bills) {
            for (BillItem billItem : bill.getBillItems()) {
                if (subscriptionId.equals(billItem.getSubscription().getId())) {
                    List<UsageChargeData> chargeUsage = populateBillingCharges(billItem);
                    if(chargeUsage == null) {
                        List<UsageChargeData> creditUsage = populateBillingCredits(billItem);
                        subscriptionData.setCurrentUsages(creditUsage);
                    }else {
                        subscriptionData.setCurrentUsages(chargeUsage);
                    }

                }
            }
        }
    }

    private List<UsageChargeData> populateBillingCharges(BillItem billItem) {
        List<UsageChargeData> chargeEntries = new LinkedList<>();

        for (Charge charge : billItem.getCharges()) {

            RatingPeriod period = charge.getRatingPeriod();

            UsageChargeData usageChargeData = new UsageChargeData();
            usageChargeData.setFromDate(period.getStart());
            usageChargeData.setToDate(period.getEnd());

            SAPRatePlanElementModel planElementModel = sapRevenueCloudProductService.getRatePlanElementfromId(charge.getMetricId());
            if (null != planElementModel) {
                usageChargeData.setName(planElementModel.getName());
                if ("usage".equalsIgnoreCase(planElementModel.getType().getCode())) {

                    UsageUnitData unitData = new UsageUnitData();
                    String id = getUnitDataId(charge);
                    unitData.setId(id);

                    usageChargeData.setUsageUnit(unitData);
                    usageChargeData.setUsage(charge.getConsumedQuantity().getValue());
                }
            }


            if(charge.getGrossAmount() != null) {
                final PriceData grossAmount = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(charge.getGrossAmount().getAmount()),
                        commonI18NService.getCurrentCurrency().getIsocode());
                usageChargeData.setGrossAmount(grossAmount);
            }
            if(charge.getNetAmount() != null){
                final PriceData netAmount = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(charge.getNetAmount().getAmount()),
                        commonI18NService.getCurrentCurrency().getIsocode());
                usageChargeData.setNetAmount(netAmount);
            }

            chargeEntries.add(usageChargeData);
        }
        return chargeEntries;
    }

    private String getUnitDataId(Charge charge){
        UsageUnitModel unitModel = sapRevenueCloudProductService.getUsageUnitfromId(charge.getMetricId());

        if (unitModel != null) {
            if (charge.getConsumedQuantity().getValue() > 0) {
                return unitModel.getNamePlural();
            } else {
                return unitModel.getName();
            }
        }
        return null;
    }

    private List<UsageChargeData> populateBillingCredits(BillItem billItem) {
        List<UsageChargeData> creditEntries = new LinkedList<>();

        for (Credit credit : billItem.getCredits()) {

            RatingPeriod period = credit.getRatingPeriod();

            UsageChargeData usageCreditData = new UsageChargeData();
            usageCreditData.setFromDate(period.getStart());
            usageCreditData.setToDate(period.getEnd());

            SAPRatePlanElementModel planElementModel = sapRevenueCloudProductService.getRatePlanElementfromId(credit.getMetricId());
            if (null != planElementModel) {
                usageCreditData.setName(planElementModel.getName());
                if ("usage".equalsIgnoreCase(planElementModel.getType().getCode())) {

                    UsageUnitData unitData = new UsageUnitData();
                    String id = getUnitDataId(credit);
                    unitData.setId(id);

                    usageCreditData.setUsageUnit(unitData);
                    usageCreditData.setUsage(credit.getConsumedQuantity().getValue());
                }
            }


            if(credit.getGrossAmount() != null) {
                final PriceData grossAmount = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(credit.getGrossAmount().getAmount()),
                        commonI18NService.getCurrentCurrency().getIsocode());
                usageCreditData.setGrossAmount(grossAmount);
            }
            if(credit.getNetAmount() != null){
                final PriceData netAmount = priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(credit.getNetAmount().getAmount()),
                        commonI18NService.getCurrentCurrency().getIsocode());
                usageCreditData.setNetAmount(netAmount);
            }

            creditEntries.add(usageCreditData);
        }
        return creditEntries;
    }

    private String getUnitDataId(Credit credit){
        UsageUnitModel unitModel = sapRevenueCloudProductService.getUsageUnitfromId(credit.getMetricId());

        if (unitModel != null) {
            if (credit.getConsumedQuantity().getValue() > 0) {
                return unitModel.getNamePlural();
            } else {
                return unitModel.getName();
            }
        }
        return null;
    }
    //<editor-fold desc="Getters and Setters">

    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService) {
        this.sapRevenueCloudProductService = sapRevenueCloudProductService;
    }

    public void setBillService(BillService billService) {
        this.billService = billService;
    }

    //</editor-fold>
}

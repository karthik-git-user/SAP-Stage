/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.bill.v2;

import com.sap.hybris.saprevenuecloudproduct.model.SAPRatePlanElementModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Charge;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.MonetaryAmount;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.RatingPeriod;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.bill.v2.Credit;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageUnitData;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Populate DTO {@link SubscriptionBillingData} with data from {@link BillItem}.
 *
 * @param <S> source class
 * @param <T> target class
 */

public class DefaultBillItemPopulator<S extends BillItem, T extends SubscriptionBillingData> implements Populator<S, T> {

    private static final Logger LOG = Logger.getLogger(DefaultBillItemPopulator.class);

    private SapRevenueCloudProductService sapRevenueCloudProductService;
    private PriceDataFactory priceDataFactory;
    private CommonI18NService commonI18NService;
    private Populator<ProductModel, ProductData> productUrlPopulator;
    private CMSSiteService cmsSiteService;

    @Override
    public void populate(S source, T target) throws ConversionException {
        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);

        //Product Code
        String productCode = source.getProduct().getCode();

        //Subscription id
        String strDocumentNumber = String.valueOf(source.getSubscription().getDocumentNumber());

        //Price
        //In List page display only Total amount
        //Gross Amount
        MonetaryAmount totalAmount = source.getGrossAmount();
        if (totalAmount == null){ //If Gross amount is not available then Net Amount
            totalAmount = source.getNetAmount();
        }
        if(totalAmount == null){//If both amounts are not available then Gross Credit Amount

            totalAmount = source.getGrossCreditAmount();
        }
        if(totalAmount == null){
            totalAmount = source.getNetCreditAmount();
        }

        final PriceData price = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(totalAmount.getAmount()),
                getCommonI18NService().getCurrentCurrency().getIsocode());


        //List of Usage Charge
        List<UsageChargeData> usageCharges = getUsageChargeDataList(source);

        //List of Usage Credits
        List<UsageChargeData> usageCredits = getUsageCreditDataList(source);

        //Product Url
        String strProductUrl = StringUtils.EMPTY;
        final CatalogVersionModel currentCatalog = getCmsSiteService().getCurrentCatalogVersion();
        final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
                .getSubscriptionPricePlanForId(source.getRatePlan().getId(), currentCatalog);
        if ( !ObjectUtils.isEmpty(pricePlanModel) ) {
            final ProductModel productModel = pricePlanModel.getProduct();
            final ProductData productData = new ProductData();
            getProductUrlPopulator().populate(productModel, productData);
            strProductUrl = productData.getUrl();
        } else {
            LOG.error(String.format("Price Plan model is null for id: %s", source.getRatePlan().getId()));
        }

        //Prepare Target
        target.setProductCode(productCode);
        target.setSubscriptionId(strDocumentNumber); //On UI document number is coined as subscriptionId
        target.setPrice(price);
        target.setCharges(usageCharges);
        target.setCredits(usageCredits);
        target.setProductUrl(strProductUrl);
    }

    private List<UsageChargeData> getUsageChargeDataList(BillItem billItem) {
        List<UsageChargeData> chargeEntries = new LinkedList<>();
        for (Charge charge : billItem.getCharges()) {

            UsageChargeData usageChargeData = new UsageChargeData();
            populateUsageChargeData(charge, usageChargeData);

            chargeEntries.add(usageChargeData);
        }

        return chargeEntries;
    }

    private void populateUsageChargeData(Charge charge, UsageChargeData usageChargeData){

        //From Date
        RatingPeriod period = charge.getRatingPeriod();
        Date fromDate = period.getStart();

        //To Date
        Date toDate = period.getEnd();

        //Gross Amount
        if(charge.getGrossAmount() != null) {
            final PriceData grossAmount = getPriceDataFactory().create(PriceDataType.BUY,
                    BigDecimal.valueOf(charge.getGrossAmount().getAmount()),
                    getCommonI18NService().getCurrentCurrency().getIsocode());
            usageChargeData.setGrossAmount(grossAmount);
        }
        //Net Amount
        if(charge.getNetAmount() != null) {
            final PriceData netAmount = getPriceDataFactory().create(PriceDataType.BUY,
                    BigDecimal.valueOf(charge.getNetAmount().getAmount()),
                    getCommonI18NService().getCurrentCurrency().getIsocode());
            usageChargeData.setNetAmount(netAmount);
        }

        //Name, Usage, Usage Unit
        String strName = null;
        Float fltUsage = null;
        UsageUnitData usageUnit = null;
        SAPRatePlanElementModel planElementModel = getSapRevenueCloudProductService().getRatePlanElementfromId(charge.getMetricId());
        if ( !ObjectUtils.isEmpty(planElementModel) ) {
            strName = planElementModel.getName();
            if ("usage".equalsIgnoreCase(planElementModel.getType().getCode())) {
                fltUsage = charge.getConsumedQuantity().getValue();
                usageUnit = new UsageUnitData();
                populateUsageUnitData(charge, usageUnit);
            }
        }
        else{
            LOG.warn(String.format("SAPRatePlanElementModel is empty for Rate Plan Element Id: '%s'", charge.getMetricId()));
        }

        //Prepare Target
        usageChargeData.setFromDate(fromDate);
        usageChargeData.setToDate(toDate);
        usageChargeData.setName(strName);
        usageChargeData.setUsage(fltUsage);
        usageChargeData.setUsageUnit(usageUnit);

    }

    private void populateUsageUnitData(Charge charge, UsageUnitData usageUnit) {

        //Id
        UsageUnitModel unitModel = getSapRevenueCloudProductService().getUsageUnitfromId(charge.getMetricId());
        String strId;
        if ( !ObjectUtils.isEmpty(unitModel)  && charge.getConsumedQuantity().getValue() > 0) {
            strId = unitModel.getNamePlural();
        } else {
            strId = unitModel.getName();
        }

        //Prepare Target
        usageUnit.setId(strId);
    }

    // Credit Usage charge
    private List<UsageChargeData> getUsageCreditDataList(BillItem billItem) {
        List<UsageChargeData> creditEntries = new LinkedList<>();
        for (Credit credit : billItem.getCredits()) {

            UsageChargeData usageCreditData = new UsageChargeData();
            populateUsageCreditData(credit, usageCreditData);

            creditEntries.add(usageCreditData);
        }

        return creditEntries;
    }

    private void populateUsageCreditData(Credit credit, UsageChargeData usageCreditData){

        //From Date
        RatingPeriod period = credit.getRatingPeriod();
        Date fromDate = period.getStart();

        //To Date
        Date toDate = period.getEnd();

        //Gross Amount
        if(credit.getGrossAmount() != null) {
            final PriceData grossAmount = getPriceDataFactory().create(PriceDataType.BUY,
                    BigDecimal.valueOf(credit.getGrossAmount().getAmount()),
                    getCommonI18NService().getCurrentCurrency().getIsocode());
            usageCreditData.setGrossAmount(grossAmount);
        }
        //Net Amount
        if(credit.getNetAmount() != null) {
            final PriceData netAmount = getPriceDataFactory().create(PriceDataType.BUY,
                    BigDecimal.valueOf(credit.getNetAmount().getAmount()),
                    getCommonI18NService().getCurrentCurrency().getIsocode());
            usageCreditData.setNetAmount(netAmount);
        }

        //Name, Usage, Usage Unit
        String strName = null;
        Float fltUsage = null;
        UsageUnitData usageUnit = null;
        SAPRatePlanElementModel planElementModel = getSapRevenueCloudProductService().getRatePlanElementfromId(credit.getMetricId());
        if ( !ObjectUtils.isEmpty(planElementModel) ) {
            strName = planElementModel.getName();
            if ("usage".equalsIgnoreCase(planElementModel.getType().getCode())) {
                fltUsage = credit.getConsumedQuantity().getValue();
                usageUnit = new UsageUnitData();
                populateUsageCreditUnitData(credit, usageUnit);
            }
        }
        else{
            LOG.warn(String.format("SAPRatePlanElementModel is empty for Rate Plan Element Id: '%s'", credit.getMetricId()));
        }

        //Prepare Target
        usageCreditData.setFromDate(fromDate);
        usageCreditData.setToDate(toDate);
        usageCreditData.setName(strName);
        usageCreditData.setUsage(fltUsage);
        usageCreditData.setUsageUnit(usageUnit);

    }

    private void populateUsageCreditUnitData(Credit credit, UsageUnitData usageUnit) {

        //Id
        UsageUnitModel unitModel = getSapRevenueCloudProductService().getUsageUnitfromId(credit.getMetricId());
        String strId;
        if ( !ObjectUtils.isEmpty(unitModel)  && credit.getConsumedQuantity().getValue() > 0) {
            strId = unitModel.getNamePlural();
        } else {
            strId = unitModel.getName();
        }

        //Prepare Target
        usageUnit.setId(strId);
    }

    //<editor-fold desc="Getters and Setters" />
    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    private SapRevenueCloudProductService getSapRevenueCloudProductService() {
        return sapRevenueCloudProductService;
    }

    public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService) {
        this.sapRevenueCloudProductService = sapRevenueCloudProductService;
    }


    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    private Populator<ProductModel, ProductData> getProductUrlPopulator() {
        return productUrlPopulator;
    }

    public void setProductUrlPopulator(Populator<ProductModel, ProductData> productUrlPopulator) {
        this.productUrlPopulator = productUrlPopulator;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
    //</editor-fold>

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.v2;

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
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.Bill;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.Charge;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.RatingPeriod;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageUnitData;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageUnitModel;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants.SUBSCRIPTION_BILL_DATE_FORMAT;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Populate DTO {@link SubscriptionBillingData} with data from {@link Bill}.
 *
 * @param <S> source class
 * @param <T> target class
 */

public class DefaultSAPRevenueCloudSubscriptionBillDetailsPopulator<S extends Bill, T extends SubscriptionBillingData> implements Populator<S,T>
{

    private static final Logger LOG = Logger.getLogger(DefaultSAPRevenueCloudSubscriptionBillDetailsPopulator.class);

    private SapRevenueCloudProductService sapRevenueCloudProductService;
    private PriceDataFactory priceDataFactory;
    private CommonI18NService commonI18NService;
    private Populator<ProductModel, ProductData> productUrlPopulator;
    private CMSSiteService cmsSiteService;

    @Override
    public void populate(S source, T target) throws ConversionException
    {
        validateParameterNotNullStandardMessage("source", source);
        validateParameterNotNullStandardMessage("target", target);

        target.setProductCode(source.getProduct().getCode());
        target.setSubscriptionId(String.valueOf(source.getSubscription().getDocumentNumber()));
        final PriceData totalAmount = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(source.getNetAmount().getAmount()),
                getCommonI18NService().getCurrentCurrency().getIsocode());
        target.setPrice(totalAmount);
        target.setCharges(populateBillingCharges(source));
        if(null!=source.getCharges())
        {
            target.setCharges(populateBillingCharges(source));
        }
        final CatalogVersionModel currentCatalog = getCmsSiteService().getCurrentCatalogVersion();
        final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
                .getSubscriptionPricePlanForId(source.getRatePlan().getId(), currentCatalog);
        if(pricePlanModel!=null) {
            final ProductModel productModel = pricePlanModel.getProduct();
            final ProductData productData = new ProductData();
            getProductUrlPopulator().populate(productModel, productData);
            target.setProductUrl(productData.getUrl());
        }
        else{
            LOG.error(String.format("Price Plan model is null for id: %s", source.getRatePlan().getId()));
        }
    }

    protected List<UsageChargeData> populateBillingCharges(Bill bill)
    {
        List<UsageChargeData> chargeEntries = new ArrayList<>();

            for(Charge charge : bill.getCharges())
            {
                UsageChargeData usageChargeData = new UsageChargeData();
                RatingPeriod period = charge.getRatingPeriod();
                usageChargeData.setFromDate(SapRevenueCloudSubscriptionUtil.stringToDate(period.getStart(), SUBSCRIPTION_BILL_DATE_FORMAT));
                if(period.getEnd()!=null && !period.getEnd().isEmpty())
                {
                    usageChargeData.setToDate(SapRevenueCloudSubscriptionUtil.stringToDate(period.getEnd(), SUBSCRIPTION_BILL_DATE_FORMAT));
                }
                SAPRatePlanElementModel planElementModel = getSapRevenueCloudProductService().getRatePlanElementfromId(charge.getMetricId());
                if(null!=planElementModel)
                {
                    populateUsageChargeData(charge,usageChargeData,planElementModel);
                }

                final PriceData netAmount = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(charge.getNetAmount().getAmount()),
                        getCommonI18NService().getCurrentCurrency().getIsocode());
                usageChargeData.setNetAmount(netAmount);
                chargeEntries.add(usageChargeData);
            }

        return chargeEntries;
    }

    protected UsageChargeData populateUsageChargeData(Charge charge,UsageChargeData usageChargeData,SAPRatePlanElementModel planElementModel )

    {
        usageChargeData.setName(planElementModel.getName());
        if("usage".equalsIgnoreCase(planElementModel.getType().getCode()))
        {
            UsageUnitModel unitModel = getSapRevenueCloudProductService().getUsageUnitfromId(charge.getMetricId());
            UsageUnitData unitData = new UsageUnitData();
            if(unitModel!=null)
            {
                if(charge.getConsumedQuantity().getValue()>0)
                {
                    unitData.setId(unitModel.getNamePlural());
                }
                else
                {
                    unitData.setId(unitModel.getName());
                }
            }
            usageChargeData.setUsage((float) charge.getConsumedQuantity().getValue());
            usageChargeData.setUsageUnit(unitData);
        }

        return usageChargeData;
    }




    public PriceDataFactory getPriceDataFactory()
    {
        return priceDataFactory;
    }

    public void setPriceDataFactory(PriceDataFactory priceDataFactory)
    {
        this.priceDataFactory = priceDataFactory;
    }

    public SapRevenueCloudProductService getSapRevenueCloudProductService()
    {
        return sapRevenueCloudProductService;
    }

    public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService)
    {
        this.sapRevenueCloudProductService = sapRevenueCloudProductService;
    }


    public CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }


    public Populator<ProductModel, ProductData> getProductUrlPopulator() {
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



}

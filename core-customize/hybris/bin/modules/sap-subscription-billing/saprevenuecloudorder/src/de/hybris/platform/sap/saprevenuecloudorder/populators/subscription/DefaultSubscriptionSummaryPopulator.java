/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators.subscription;

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Item;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Payment;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.RenewalTerm;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Snapshot;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.subscription.v1.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.service.SubscriptionService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionfacades.data.PaymentData;
import de.hybris.platform.subscriptionfacades.data.RenewalTermData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;
import de.hybris.platform.subscriptionservices.enums.SubscriptionStatus;
import de.hybris.platform.subscriptionservices.exception.SubscriptionServiceException;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Populate DTO {@link SubscriptionData} with data from {@link Subscription}.
 *
 * @param <S> source class
 * @param <T> target class
 */
public class DefaultSubscriptionSummaryPopulator<S extends Subscription, T extends SubscriptionData>
        implements Populator<S, T> {

    private static final Logger LOG = Logger.getLogger(DefaultSubscriptionSummaryPopulator.class);

    private CMSSiteService cmsSiteService;
    private SapRevenueCloudProductService sapRevenueCloudProductService;
    private CommonI18NService commonI18NService;
    private SubscriptionService subscriptionService;
    private Populator<ProductModel, ProductData> productUrlPopulator;
    private Populator<ProductModel, ProductData> subscriptionProductPricePlanPopulator;
    private Map<String, SubscriptionStatus> subscriptionStatusMap;
    private Map<String, String> billingFrequencyMap;
    private Populator<SubscriptionPricePlanModel,ProductData> sbSubscriptionPricePopulator;

    @Override
    public void populate(S source, T target) throws ConversionException {

        //Renewal Term Data
        RenewalTerm renewalTerm = source.getRenewalTerm();
        RenewalTermData renewalTermData = null;
        if (renewalTerm != null) {
            renewalTermData = new RenewalTermData();
            populateRenewalTerm(renewalTerm, renewalTermData);
        }

        //Payment Data
        Payment payment = source.getPayment();
        PaymentData paymentData = null;
        if (payment != null) {
            paymentData = new PaymentData();
            populatePayment(payment, paymentData);
        }

        //Withdrawal Period End Date and Withdrawn At
        Date withdrawalPeriodEndDate = null;
        Date withdrawnAt = null;
        if (source.getCancellationPolicy() != null && source.getCancellationPolicy().getWithdrawalPeriodEndDate() != null) {
            withdrawalPeriodEndDate = source.getCancellationPolicy().getWithdrawalPeriodEndDate();
            withdrawnAt = source.getWithdrawnAt();
        }

        //Status
        String strStatus = source.getStatus();
        SubscriptionStatus status = getSubscriptionStatusMap().getOrDefault(strStatus, null);
        if (status == null) {
            LOG.error(String.format("Cannot Find mapping for status '%s'", strStatus));
        }

        //Valid Till Date
        String strValidTillDate = SapRevenueCloudSubscriptionUtil.dateToString(source.getValidUntil());

        //Prepare Target (except price plan) //This block handles partial data even if price plan doesn't exists
        target.setId(source.getSubscriptionId());
        target.setDocumentNumber(String.valueOf(source.getDocumentNumber()));
        target.setStartDate(source.getValidFrom());
        target.setEndDate(source.getValidUntil());
        target.setVersion(String.valueOf(source.getMetadata().getVersion()));
        target.setCustomerId(source.getCustomer().getId());
        target.setRenewalTerm(renewalTermData);
        target.setPayment(paymentData);
        target.setWithdrawalPeriodEndDate(withdrawalPeriodEndDate);
        target.setWithdrawnAt(withdrawnAt);
        target.setStatus(status);
        target.setValidTillDate(strValidTillDate);

        //Populate Product related details
        Snapshot snapshot = source.getSnapshots().get(source.getSnapshots().size() - 1); //As per Info Last snapshot in the list is the latest one
        Item item = snapshot.getItems().get(0);
        final String ratePlanId = item.getRatePlan().getId();

        //Find Product Url
        Date effectiveAt = item.getPricing().getPricingDate();
        if(effectiveAt == null){
            effectiveAt = snapshot.getEffectiveDate();
        }

        final List<CatalogVersionModel> catalogVersions = cmsSiteService.getAllCatalogs(cmsSiteService.getCurrentSite())
                .stream()
                .map(CatalogModel::getActiveCatalogVersion).collect(Collectors.toList());


        final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService().
                getSubscriptionPricesWithEffectiveDate(ratePlanId, catalogVersions,effectiveAt);

        if (pricePlanModel == null) {
            List<String> catalogsNames = catalogVersions.stream().map( catalogVersionModel -> {
                CatalogModel catalog = catalogVersionModel.getCatalog();
                return String.format("%s : %s", catalog.getId(), catalog.getVersion());
            }).collect(Collectors.toList());

            LOG.error(String.format("Cannot find Price plan model for ratePlan: '%s' and Catalogs: '%s' with effective date '%s' ", ratePlanId, catalogsNames, effectiveAt));
            return;
        }

        final ProductData productData = new ProductData();
        final ProductModel productModel = pricePlanModel.getProduct();
        getProductUrlPopulator().populate(productModel, productData);
        final String productUrl = productData.getUrl();

        //Find Product Name
        final String productName = pricePlanModel.getProduct().getName();

        //Billing Frequency
        BillingFrequencyModel billingFrequencyModel;
        String billingFrequency;
        try {
            billingFrequencyModel = getSubscriptionService().getBillingFrequency(productModel);
            billingFrequency = billingFrequencyModel.getNameInCart();
        } catch (SubscriptionServiceException ex) {
            LOG.error("Error occurred while fetching billing frequency: ", ex);
            throw new SystemException("Error occurred while fetching billing frequency: ", ex);
        }

        //Find Price Plan
       // populatePricePlan(productModel, productData);
        populateSbPricePlan(pricePlanModel,productData);
        SubscriptionPricePlanData pricePlanData = (SubscriptionPricePlanData) productData.getPrice();

        //Contract Frequency
        BillingPlanModel billingPlanModel = productModel.getSubscriptionTerm().getBillingPlan();
        String billingPlanModelId = billingPlanModel.getId();
        String contractFrequency = getBillingFrequencyMap().getOrDefault(billingPlanModelId, StringUtils.EMPTY);
        if (StringUtils.isEmpty(contractFrequency)) {
            LOG.warn(String.format("Unknown frequency code '%s'", billingPlanModel.getId()));
        }

        //Prepare Final Target
        target.setContractFrequency(contractFrequency);
        target.setProductCode(item.getProduct().getCode());
        target.setDescription(String.format("SAP Subscription For Product: %s", item.getProduct().getCode()));
        target.setRatePlanId(ratePlanId);
        target.setName(productName);
        target.setProductUrl(productUrl);
        target.setBillingFrequency(billingFrequency);
        target.setPricePlan(pricePlanData);

    }


    /**
     * Populates data from {@link SubscriptionPricePlanModel} to {@link ProductData}
     *
     * @param pricePlanModel {@link SubscriptionPricePlanModel}
     * @param productData  {@link ProductData} that needs to be populated
     */
    private void populateSbPricePlan(final SubscriptionPricePlanModel pricePlanModel, final ProductData productData ){
        if(pricePlanModel != null) {
            getSbSubscriptionPricePopulator().populate(pricePlanModel, productData);
        }
    }

    //Ends here
    /**
     * Populates data from {@link Payment} to {@link PaymentData}
     *
     * @param source {@link Payment}
     * @param target {@link PaymentData} that needs to be populated
     */
    private void populatePayment(final Payment source, final PaymentData target) {
        target.setPaymentCardToken(source.getPaymentCardToken());
        target.setPaymentMethod(source.getPaymentMethod());
    }

    /**
     * Populates data from {@link RenewalTerm} to {@link RenewalTermData}
     *
     * @param source {@link RenewalTerm}
     * @param target {@link RenewalTermData} that needs to be populated
     */
    private void populateRenewalTerm(final RenewalTerm source, final RenewalTermData target) {
        target.setEndDate(source.getEndDate());
        target.setPeriod(source.getPeriod());
    }


    /**
     * Populates data from {@link ProductModel} to {@link ProductData}
     *
     * @param productModel {@link ProductModel}
     * @param productData  {@link ProductData} that needs to be populated
     */
    private void populatePricePlan(final ProductModel productModel, final ProductData productData) {
        if (productModel != null) {
            getSubscriptionProductPricePlanPopulator().populate(productModel, productData);
        }
    }


    //<editor-fold desc="Getters and Setters">
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    private Populator<ProductModel, ProductData> getSubscriptionProductPricePlanPopulator() {
        return subscriptionProductPricePlanPopulator;
    }

    public void setSubscriptionProductPricePlanPopulator(
            Populator<ProductModel, ProductData> subscriptionProductPricePlanPopulator) {
        this.subscriptionProductPricePlanPopulator = subscriptionProductPricePlanPopulator;
    }

    private Populator<ProductModel, ProductData> getProductUrlPopulator() {
        return productUrlPopulator;
    }

    public void setProductUrlPopulator(Populator<ProductModel, ProductData> productUrlPopulator) {
        this.productUrlPopulator = productUrlPopulator;
    }

    private SapRevenueCloudProductService getSapRevenueCloudProductService() {
        return sapRevenueCloudProductService;
    }

    public void setSapRevenueCloudProductService(SapRevenueCloudProductService sapRevenueCloudProductService) {
        this.sapRevenueCloudProductService = sapRevenueCloudProductService;
    }

    private CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    private Map<String, SubscriptionStatus> getSubscriptionStatusMap() {
        return subscriptionStatusMap;
    }

    public void setSubscriptionStatusMap(Map<String, SubscriptionStatus> subscriptionStatusMap) {
        this.subscriptionStatusMap = subscriptionStatusMap;
    }

    private Map<String, String> getBillingFrequencyMap() {
        return billingFrequencyMap;
    }

    public void setBillingFrequencyMap(Map<String, String> billingFrequencyMap) {
        this.billingFrequencyMap = billingFrequencyMap;
    }

    private SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    public Populator<SubscriptionPricePlanModel, ProductData> getSbSubscriptionPricePopulator() {
        return sbSubscriptionPricePopulator;
    }

    public void setSbSubscriptionPricePopulator(Populator<SubscriptionPricePlanModel, ProductData> sbSubscriptionPricePopulator) {
        this.sbSubscriptionPricePopulator = sbSubscriptionPricePopulator;
    }
    //</editor-fold>
}

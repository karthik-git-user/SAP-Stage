/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service.impl;

import java.util.Date;
import java.util.List;

import com.hybris.charon.exp.BadRequestException;
import com.hybris.charon.exp.ClientException;
import com.hybris.charon.exp.ServerException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudBusinessException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudClientException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudServerException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.RevenueCloudUnknownException;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.*;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.v2.BillsList;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionConfigurationService;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.subscriptionservices.model.BillingFrequencyModel;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;

import org.apache.log4j.Logger;

import com.hybris.charon.RawResponse;

/**
 * Service to fetch Subscription Data from Revenue Cloud.
 * @deprecated instead use {@link DefaultBillService} and {@link DefaultSubscriptionService}
 */
@Deprecated(since = "1905.12", forRemoval = true)
public class DefaultSapRevenueCloudSubscriptionService implements SapRevenueCloudSubscriptionService
{
    private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudSubscriptionService.class);

    private SapRevenueCloudSubscriptionConfigurationService sapRevenueCloudSubscriptionConfigurationService;
    private UserService userService;

    @Override
    public List<Subscription> getSubscriptionsByClientId(final String clientId) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionsByClientId(clientId);
        }
        catch(
                BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (
                ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (
                ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public RawResponse<List<Subscription>> getSubscriptionsWithPagination(final String clientId,final int pageNumber,final int pageSize) {
        try{
            return getSapSubscriptionConfigurationService()
                    .getSapSubscriptionClient()
                    .getSubscriptionsWithPagination(clientId,pageNumber+1,pageSize, "documentNumber,desc");
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public Subscription getSubscriptionById(final String subscriptionId) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionById(subscriptionId);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch ( ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public void cancelSubscription(final String code, final CancelSubscription subscription)
    {
        try{
            getSapSubscriptionConfigurationService().getSapSubscriptionClient().cancelSubscription(code,subscription);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public void withdrawSubscription(String code, WithdrawSubscription subscription) {
        try{
            getSapSubscriptionConfigurationService().getSapSubscriptionClient().withdrawSubscription(code,subscription);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (
                ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    /**
     * @param code subscription id of subscription
     * @param subscription details of subscription
     */
    @Override
    @SuppressWarnings({"removal" })
    public void extendSubscription(final String code, final ExtendSubscription subscription) {
        try{
            getSapSubscriptionConfigurationService().getSapSubscriptionClient().extendSubscription(code, subscription);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public void changePaymentDetails(final String code, final ChangePaymentData changePaymentData)
    {
        if(changePaymentData.getPayment()==null || StringUtils.isBlank(changePaymentData.getPayment().getPaymentMethod())){
            throw new IllegalArgumentException("payment data cannot be null");
        }

        try{
            getSapSubscriptionConfigurationService().getSapSubscriptionClient().changePaymentDetails(code, changePaymentData);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public BillingFrequencyModel getBillingFrequency(final ProductModel productModel)
    {
        BillingFrequencyModel billingFrequency = null;
        final SubscriptionTermModel subscriptionTerm = productModel.getSubscriptionTerm();
        if (subscriptionTerm != null)
        {
            final BillingPlanModel billingPlan = subscriptionTerm.getBillingPlan();
            if (billingPlan != null && billingPlan.getBillingFrequency() != null)
            {
                billingFrequency = billingPlan.getBillingFrequency();
            }
        }
        return billingFrequency;

    }

    @Override
    public String computeCancellationDate(final String subscriptionsId, String reqCancellationDate) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient()
                    .getCancellationDate(subscriptionsId, reqCancellationDate)
                    .getEffectiveExpirationDate();
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    @SuppressWarnings({"removal" })
    public Subscription extendSubscription(String id, ExtendSubscription extendSubscription, boolean simulate) {
        try{
            return getSapSubscriptionConfigurationService()
                    .getSapSubscriptionClient()
                    .extendSubscription(id, extendSubscription, simulate);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public SubscriptionExtensionResponse extendSubscription(
            String subscriptionCode,
            SubscriptionExtensionForm extensionForm,
            boolean simulate) {

        //Prefer Unlimited over number of months
        if(extensionForm.getUnlimited()!=null && extensionForm.getUnlimited()){
            LOG.warn("extension form is marked for unlimited hence removing number of billing periods and extension date ");
            extensionForm.setNumberOfBillingPeriods(null);
            extensionForm.setExtensionDate(null);
        }

        //Default empty values
        if(extensionForm.getChangedAt() == null){
            extensionForm.setChangedAt(new Date());
        }

        if(extensionForm.getChangedBy()==null){
            String userMailId = getUserService().getCurrentUser().getUid();
            extensionForm.setChangedBy(userMailId);
        }

        //Process
        try{
            return getSapSubscriptionConfigurationService()
                    .getSapSubscriptionClient()
                    .extendSubscription(subscriptionCode, extensionForm, simulate);
        }
        catch(
                BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (
                ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (
                ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    @SuppressWarnings({"removal" })
    public List<Bills> getBillsBySubscriptionsId(final String customerId, final String fromDate, final String toDate) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionBills(customerId,
                    fromDate != null ? fromDate : "", toDate != null ? toDate : "");
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    @SuppressWarnings({"removal" })
    public List<Bills> getSubscriptionCurrentUsage(final String subscriptionId, final String currentDate) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient()
                    .getSubscriptionCurrentUsage(subscriptionId, currentDate);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    @SuppressWarnings({"removal" })
    public Bills getSubscriptionBillsById(final String billId) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionBillById(billId);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public RawResponse<List<BillsList>> getBillsBySubscriptionsId(String customerId, String fromDate, String toDate, int pageNumber, int pageSize, String sort){
        try{
            return getSapSubscriptionConfigurationService()
                    .getSapSubscriptionClient()
                    .getSubscriptionBills(customerId,fromDate != null ? fromDate : "", toDate != null ? toDate : "", pageNumber+1, pageSize);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public BillsList getSubscriptionBillsByBillId(final String billId) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient().getSubscriptionBillByBillId(billId);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    @Override
    public CancellationReversalResponse reverseCancellation(String code, CancellationReversal cancellationReversal) {
        try{
            return getSapSubscriptionConfigurationService().getSapSubscriptionClient().cancellationReversal(code, cancellationReversal);
        }
        catch(BadRequestException ex){
            LOG.error(ex);
            throw new RevenueCloudBusinessException();
        }
        catch (ModelNotFoundException | ClientException ex){
            LOG.error(ex);
            throw new RevenueCloudClientException();
        }
        catch (ServerException ex){
            LOG.error(ex);
            throw new RevenueCloudServerException();
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new RevenueCloudUnknownException();
        }
    }

    protected SapRevenueCloudSubscriptionConfigurationService getSapSubscriptionConfigurationService() {
        return sapRevenueCloudSubscriptionConfigurationService;
    }

    @Required
    public void setSapSubscriptionConfigurationService(final SapRevenueCloudSubscriptionConfigurationService sapRevenueCloudSubscriptionConfigurationService) {
        this.sapRevenueCloudSubscriptionConfigurationService = sapRevenueCloudSubscriptionConfigurationService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}

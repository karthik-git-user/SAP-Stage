/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.controllers.pages;

import com.sap.hybris.saprevenueclouddpaddon.controllers.SaprevenueclouddpaddonControllerConstants;
import com.sap.hybris.saprevenueclouddpaddon.facade.SapRevenueCloudDigitalPaymentFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentRegisterUrlException;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapRevenueCloudSubscriptionFacade;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.subscriptionfacades.data.PaymentData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/paymentcard")
public class SapRevenueCloudDigitalPaymentsController extends AbstractSearchPageController {

    private static final Logger LOG = Logger.getLogger(SapRevenueCloudDigitalPaymentsController.class);

    private static final String REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE= REDIRECT_PREFIX + "/my-account/subscription/";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    private static final String VERSION = "version";
    private static final String SUBSCRIPTION_QUERY_CODE = "s";
    private static final String VERSION_QUERY_CODE = "v";
    private static final String PAYMENT_DETAILS_ERROR = "checkout.multi.paymentMethod.addPaymentDetails.generalError";

    @Resource(name = "sessionService")
    private SessionService sessionService;

    @Resource(name = "sapRevenueCloudDigitalPaymentFacade")
    private SapRevenueCloudDigitalPaymentFacade sapRevenueCloudDigitalPaymentFacade;

    @Resource(name = "subscriptionFacade")
    private SapRevenueCloudSubscriptionFacade sapSubscriptionFacade;

    /*
     * Check if card is registered in digital payment if registered then it is saved in commerce
     */
    @RequireHardLogIn
    @RequestMapping(value="/poll", method = RequestMethod.GET)
    public String checkCardRegistration(@RequestParam(SUBSCRIPTION_QUERY_CODE) final String subscriptionCode,
                                        @RequestParam(VERSION_QUERY_CODE) final String version,
                                        final Model model)
    {
        String dpSessionId = sapRevenueCloudDigitalPaymentFacade.getSapDigitalPaymentRegisterCardSessionId();
        if(StringUtils.isEmpty(dpSessionId)){
            LOG.error("session Id of digital payment card cannot be null");
            return null;
        }

        CCPaymentInfoData ccPaymentInfoData = sapRevenueCloudDigitalPaymentFacade.checkPaymentCardRegistration(dpSessionId);
        try {
            sapSubscriptionFacade.populateCardTypeName(ccPaymentInfoData);
        }
        catch (SubscriptionFacadeException ex){
            LOG.error(ex);
            GlobalMessages.addErrorMessage(model, PAYMENT_DETAILS_ERROR);
        }

        model.addAttribute(SUBSCRIPTION_ID, subscriptionCode);
        model.addAttribute(VERSION, version);
        if(ccPaymentInfoData!=null) {
            List<CCPaymentInfoData> paymentInfos = new LinkedList<>();
            paymentInfos.add(ccPaymentInfoData);
            model.addAttribute("savedCards", paymentInfos);
            return SaprevenueclouddpaddonControllerConstants.Views.Fragments.DigitalPayments.CHANGE_PAYMENT_SAVED_CARDS_POPUP;
        }
        else{
            setSubscriptionCurrentPaymentInfo(subscriptionCode, model);
            return SaprevenueclouddpaddonControllerConstants.Views.Fragments.DigitalPayments.CHANGE_PAYMENT_DETAILS_POPUP;
        }
    }

    /*
     * Shows saved cards details popup for updating subscription payment details
     */
    @RequireHardLogIn
    @RequestMapping(value = "/savedCards",
            method = RequestMethod.GET)
    public String showSavedCards(@RequestParam(SUBSCRIPTION_QUERY_CODE) final String subscriptionCode,
                                 @RequestParam(VERSION_QUERY_CODE) final String version,
                                 final Model model,
                                 final RedirectAttributes redirectModel)
    {

        List<CCPaymentInfoData> paymentInfos = getUserFacade().getCCPaymentInfos(true);
        try {
            sapSubscriptionFacade.populateCardTypeName(paymentInfos);
        }
        catch (SubscriptionFacadeException ex){
            LOG.error(ex);
            GlobalMessages.addErrorMessage(model, PAYMENT_DETAILS_ERROR);
        }

        model.addAttribute(SUBSCRIPTION_ID, subscriptionCode);
        model.addAttribute(VERSION, version);
        model.addAttribute("savedCards", paymentInfos);
        return SaprevenueclouddpaddonControllerConstants.Views.Fragments.DigitalPayments.CHANGE_PAYMENT_SAVED_CARDS_POPUP;
    }

    /*
     * Add new payment card
     */
    @RequestMapping(value = "/registerNewCard", method = RequestMethod.GET)
    @RequireHardLogIn
    public String registerNewCard(@RequestHeader(value = "referer", required = false) final String referer,
                                  final Model model,
                                  final RedirectAttributes redirectModel)
    {
        String subscriptionCode;
        try
        {

            final String hopPostUrl = sapRevenueCloudDigitalPaymentFacade.getCardRegistrationUrl();

            //If the URL from the digital payment is not empty, redirect the user to the digital payment UI
            if (StringUtils.isNotEmpty(hopPostUrl))
            {
                return REDIRECT_PREFIX + hopPostUrl;
            }
        }
        catch (final SapDigitalPaymentRegisterUrlException ex)
        {
            logError(ex, "Digital payment registration URL request failed");
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    "text.account.subscription.changePaymentDetails.registerFailed");
        }
        catch (final RuntimeException ex)
        {
            //Generic exception handling block.
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
                    "text.account.subscription.changePaymentDetails.unknownError");
            logError(ex, "Digital payment registration URL request failed due to unknown error");
        }


        subscriptionCode = StringUtils.substringAfterLast(referer, "/");

        model.addAttribute("metaRobots", "noindex,nofollow");
        GlobalMessages.addErrorMessage(model, PAYMENT_DETAILS_ERROR);
        return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
    }

    /*
     * Subscription Change Payment Details Options Popup
     */
    @RequireHardLogIn
    @RequestMapping(value = "/changePaymentDetailsPopup", method = RequestMethod.GET)
    public String showChangePaymentDetailsPopup(@RequestParam(SUBSCRIPTION_QUERY_CODE) final String subscriptionCode,
                                                @RequestParam(VERSION_QUERY_CODE) final String version,
                                                final Model model)
    {

        setSubscriptionCurrentPaymentInfo(subscriptionCode, model);

        model.addAttribute(SUBSCRIPTION_ID, subscriptionCode);
        model.addAttribute(VERSION, version);


        return SaprevenueclouddpaddonControllerConstants.Views.Fragments.DigitalPayments.CHANGE_PAYMENT_DETAILS_POPUP;
    }

    /*
     * Sets current payment information of subscription in model
     */
    private void setSubscriptionCurrentPaymentInfo(String subscriptionCode, Model model){
        try {
            final SubscriptionData subscriptionDetails = sapSubscriptionFacade.getSubscription(subscriptionCode);
            if(subscriptionDetails == null){
                LOG.error("Subscription Details cannot be null");
                return;
            }
            PaymentData payment = subscriptionDetails.getPayment();

            CCPaymentInfoData paymentInfo = null;
            if (payment != null) {
                List<CCPaymentInfoData> savedCards = getUserFacade().getCCPaymentInfos(true);
                paymentInfo = searchPaymentCard(payment.getPaymentCardToken(), savedCards);
                model.addAttribute("paymentMethod", payment.getPaymentMethod());
            }

            model.addAttribute("currentPaymentInfo", paymentInfo);

        } catch (SubscriptionFacadeException e) {
            logError(e, e.getMessage());
            LOG.error(e);
        }
    }

    private static CCPaymentInfoData searchPaymentCard(String paymentCardToken, List<CCPaymentInfoData> savedCards){
        for (CCPaymentInfoData savedCard : savedCards) {
            if (StringUtils.equals(savedCard.getSubscriptionId(),paymentCardToken) ){
                return savedCard;
            }
        }
        return null;
    }


    /**
     * @param ex logs error
     */
    private static void logError(final Exception ex, final String errorMessage)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug(errorMessage + ex);
        }
        LOG.error(errorMessage);
    }


}

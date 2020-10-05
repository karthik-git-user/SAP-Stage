/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.controllers.pages;

import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.ChangePaymentDetailsForm;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionReverseCancellationForm;
import de.hybris.platform.saprevenuecloudorder.data.MetadataData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionData;
import de.hybris.platform.saprevenuecloudorder.data.SubscriptionExtensionFormData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapRevenueCloudSubscriptionFacade;
import de.hybris.platform.sap.sapsubscriptionaddon.controllers.SapsubscriptionaddonControllerConstants;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionBillForm;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionCancellationForm;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionExtensionForm;
import de.hybris.platform.sap.sapsubscriptionaddon.forms.SubscriptionWithdrawalForm;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;

/**
 * Controller for subscriptions page.
 */
@Controller
@RequestMapping("/my-account")
public class SapSubscriptionsPageController extends AbstractSearchPageController
{

	// Internal Redirects
	private static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";
	private static final String REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS = REDIRECT_PREFIX + "/my-account/subscriptions";
	private static final String REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE= REDIRECT_PREFIX + "/my-account/subscription/";

	// CMS Pages
	private static final String SUBSCRIPTIONS_CMS_PAGE = "subscriptions";
	private static final String SUBSCRIPTION_DETAILS_CMS_PAGE = "subscription";
	private static final String SUBSCRIPTION_BILL_CMS_PAGE = "subscriptionBills";
	private static final String SUBSCRIPTION_BILL_DETAILS_CMS_PAGE = "subscriptionBillDetails";

	private static final String SORT_DOC_NO_DESC = "documentNumber,desc";
    private static final String SORT_DOC_NO_ASC = "documentNumber,asc";
    private static final String SORT_BILL_DATE_DESC = "billingDate,desc";
    private static final String SORT_BILL_DATE_ASC = "billingDate,asc";
    private static final Integer DEFAULT_PAGE_SIZE = 10;

	/**
	 * We use this suffix pattern because of an issue with Spring 3.1 where a
	 * Uri value is incorrectly extracted if it contains on or more '.'
	 * characters. Please see https://jira.springsource.org/browse/SPR-6164 for
	 * a discussion on the issue and future resolution.
	 */
	private static final String SUBSCRIPTION_ID_PATH_VARIABLE_PATTERN = "{subscriptionCode:.*}";
	private static final Logger LOG = Logger.getLogger(SapSubscriptionsPageController.class);

	@Resource(name = "customerFacade")
	protected CustomerFacade customerFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "subscriptionFacade")
	private SapRevenueCloudSubscriptionFacade sapSubscriptionFacade;

	/*
	 * Display All Subscription
	 */
	@SuppressWarnings({"removal"})
	@RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
	@RequireHardLogIn
	public String subscriptions(@RequestParam(value = "page", defaultValue = "0") final int page,
                                @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                @RequestParam(value = "sort", required = false, defaultValue = SORT_DOC_NO_DESC) final String sortCode,
                                @Nonnull final Model model) throws CMSItemNotFoundException
	{
        SearchPageDataWrapper<SubscriptionData> searchPageData = null;
		try {
            SearchPageData<SubscriptionData> newSearchPageData = sapSubscriptionFacade.getSubscriptions(page, DEFAULT_PAGE_SIZE, sortCode);
            searchPageData = populateOldSearchPageData(newSearchPageData);
		    populateModel(model, searchPageData, showMode);
			searchPageData.getPagination().setSort(sortCode);

            List<SortData> sortDataList = populateSortDataList(sortCode, List.of(SORT_DOC_NO_DESC, SORT_DOC_NO_ASC));
            searchPageData.setSorts(sortDataList);
		}
		catch (final SubscriptionFacadeException e) {
			LOG.error("Error while retrieving subscriptions", e);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTIONS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTIONS_CMS_PAGE));
		model.addAttribute(SUBSCRIPTIONS_CMS_PAGE, searchPageData);
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
	}

	/*
	 * Get Subscription Details
	 */
	@RequestMapping(value = "/subscription/" + SUBSCRIPTION_ID_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	@RequireHardLogIn
	public String subscriptionDetail(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model)
			throws CMSItemNotFoundException
	{
		try
		{
			final SubscriptionData subscriptionDetails = sapSubscriptionFacade.getSubscription(subscriptionCode);
			model.addAttribute("subscriptionData", subscriptionDetails);
			final List<Breadcrumb> breadcrumbs = buildSubscriptionDetailBreadcrumb(subscriptionDetails);
			model.addAttribute("breadcrumbs", breadcrumbs);
			final List<ProductData> upsellingOptions = sapSubscriptionFacade
					.getUpsellingOptionsForSubscription(subscriptionDetails.getProductCode());
			model.addAttribute("upgradable", CollectionUtils.isNotEmpty(upsellingOptions));

		}
		catch (final SubscriptionFacadeException e)
		{
			LOG.warn("Attempted to load a subscription that does not exist or is not visible", e);
			return REDIRECT_MY_ACCOUNT;
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_DETAILS_CMS_PAGE));
		model.addAttribute("metaRobots", "no-index,no-follow");
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_DETAILS_CMS_PAGE));
		return getViewForPage(model);
	}

	/*
	 * Cancel Subscription
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/cancel", method = RequestMethod.POST)
	public String cancelSubscription(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model,
									 @ModelAttribute("subscriptionCancellationForm") final SubscriptionCancellationForm subscriptionCancellationForm,
									 final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			sapSubscriptionFacade.cancelSubscription(
					populateCancellationSubscriptionData(subscriptionCode, subscriptionCancellationForm));
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
					"text.account.subscription.cancel.success");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS;
		}
		catch (final Exception ex) // NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.account.subscription.cancel.error");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS;
		}
	}


	/*
	 * Withdraw Subscription
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/withdraw", method = RequestMethod.POST)
	public String withdrawSubscription(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model,
									   @ModelAttribute("version") final String version,@ModelAttribute("subscriptionWithdrawalForm") final SubscriptionWithdrawalForm subscriptionWithdrawalForm,
									   final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			sapSubscriptionFacade.withdrawSubscription(
					populateWithdrawalSubscriptionData(subscriptionCode, version));
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
					"text.account.subscription.withdraw.success");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS;
		}
		catch (final Exception ex) // NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.account.subscription.withdraw.error");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTIONS;
		}
	}

	/*
	 * extend subscription
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/extend", method = RequestMethod.POST)
	public String extendSubscription(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model,
									 @ModelAttribute("subscriptionExtensionForm") final SubscriptionExtensionForm subscriptionExtensionForm,
									 final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			boolean errors = validateExtensionForm(subscriptionExtensionForm,redirectModel);
			if(errors)
			{
				return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
			}

			sapSubscriptionFacade.extendSubscription(subscriptionCode, populateSubscriptionExtensionFormData(subscriptionExtensionForm) ,false);

			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER,
					"text.account.subscription.extendTerm.success");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		}
		catch (final Exception ex) // NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.account.subscription.extend.error");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		}
	}

	/*
	 * calculate effective cancellation date
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/caleffDate/{version:.*}", method = RequestMethod.GET)
	public String calculateCancellationDate(@PathVariable("subscriptionCode") final String subscriptionCode,@PathVariable("version") final String version,
											final Model model,final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			SubscriptionData subscriptionData = sapSubscriptionFacade.computeCancellationDate(subscriptionCode);
			model.addAttribute("subscriptionData", subscriptionData);
			model.addAttribute("version",version);
			model.addAttribute("subscriptionId", subscriptionCode);
			LOG.info("Effective End Date : " + subscriptionData.getEndDate());
			return SapsubscriptionaddonControllerConstants.Views.Fragments.Subscriptions.CancelSubscriptionPopup;

		}
		catch (final Exception ex) // NOSONAR
		{
			LOG.error("error occured while calculating cancellation date");
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"error occured while fetching effective end date");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		}
	}

	/*
	 *  withdrawal date
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/withdrawConfirmation/{version:.*}", method = RequestMethod.GET)
	public String calculateWithdrawalDate(@PathVariable("subscriptionCode") final String subscriptionCode,@PathVariable("version") final String version,
										  final Model model, @ModelAttribute("subscriptionWithdrawalForm") final SubscriptionWithdrawalForm subscriptionWithdrawalForm,
										  final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			model.addAttribute("version",version);
			model.addAttribute("subscriptionId", subscriptionCode);
			model.addAttribute("subscriptionWithdrawalForm", subscriptionWithdrawalForm);
			return SapsubscriptionaddonControllerConstants.Views.Fragments.Subscriptions.WithdrawSubscriptionPopup;

		}
		catch (final Exception ex) // NOSONAR
		{
			LOG.error("error occured while calculating Withdrawal date");
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"error occured while fetching effective withdrawal end date");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		}
	}

	/*
	 * calculate effective Extension date
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + "{subscriptionCode:.*}/calcExtnEffDate", method = RequestMethod.GET)
	public String calculateExtensionDate(@PathVariable("subscriptionCode") final String subscriptionCode, final Model model,
										 @ModelAttribute("subscriptionExtensionForm") final SubscriptionExtensionForm subscriptionExtensionForm,
										 final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{

		try
		{
			boolean errors = validateExtensionForm(subscriptionExtensionForm,redirectModel);
			if(errors)
			{
				return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
			}

			SubscriptionExtensionData extensionData = sapSubscriptionFacade.extendSubscription(subscriptionCode, populateSubscriptionExtensionFormData(subscriptionExtensionForm) ,true);
			model.addAttribute("extensionData", extensionData);
			model.addAttribute("subscriptionCode", subscriptionCode);
			model.addAttribute("subscriptionExtensionForm", subscriptionExtensionForm);
			LOG.info("Effective End Date : " + extensionData.getValidUntil());
			return SapsubscriptionaddonControllerConstants.Views.Fragments.Subscriptions.EXTEND_SUBSCRIPTION_POPUP;
		}
		catch (final Exception ex) // NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.account.subscription.extend.error");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
		}

	}


	/*
	 * fetch subscription bills
	 */
	@SuppressWarnings({"removal"})
    @RequireHardLogIn
    @RequestMapping(value = "/subscription/bills", method = RequestMethod.GET)
    public String getSubscriptionBillsPage( @RequestParam(value = "page", defaultValue = "0") final int page,
                                            @RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
                                            @RequestParam(value = "sort", defaultValue = SORT_BILL_DATE_DESC, required = false) final String sortCode,
                                            @ModelAttribute("subscriptionBillForm") final SubscriptionBillForm subscriptionBillForm,
                                            final RedirectAttributes redirectModel,
                                            final Model model
    ) throws CMSItemNotFoundException{

        SearchPageDataWrapper<SubscriptionBillingData> searchPageData = null;
		try {
            final SearchPageData<SubscriptionBillingData> newSearchPageData = sapSubscriptionFacade.getSubscriptionBillsHistory(
                    subscriptionBillForm.getFromDate(),
                    subscriptionBillForm.getToDate(),
                    page,
                    DEFAULT_PAGE_SIZE,
                    sortCode);
            searchPageData = populateOldSearchPageData(newSearchPageData);
            populateModel(model, searchPageData, showMode);
            searchPageData.getPagination().setSort(sortCode);

            List<SortData> sortDataList = populateSortDataList(sortCode, List.of(SORT_DOC_NO_DESC, SORT_DOC_NO_ASC, SORT_BILL_DATE_DESC, SORT_BILL_DATE_ASC));
            searchPageData.setSorts(sortDataList);
		}
		catch (final Exception ex) // NOSONAR
		{
			LOG.error("error occured while fetching subscription bills: ",ex);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
        model.addAttribute(SUBSCRIPTION_BILL_CMS_PAGE, searchPageData);
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions.bills"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
	}

	/*
	 * fetch subscription bill using billId
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/bills/"  + "{billId:.*}", method = RequestMethod.GET)
	public String getSubscriptionBillById( @PathVariable("billId") final String billId,final Model model,
										   @ModelAttribute("subscriptionBillForm") final SubscriptionBillForm subscriptionBillForm,
										   final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			List<SubscriptionBillingData> subscriptionBillList = sapSubscriptionFacade.getBillDetails(billId);
			final List<Breadcrumb> breadcrumbs = buildSubscriptionBillDetailBreadcrumb(billId);
			model.addAttribute("subscriptionBill", subscriptionBillList);
			model.addAttribute("breadcrumbs", breadcrumbs);
		}
		catch (final Exception ex) // NOSONAR
		{
			LOG.error("error occured while fetching subscription bills for BillId : "+ billId + ex);
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_DETAILS_CMS_PAGE));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);

	}

	/*
	 * fetch subscription bills
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/bills", method = RequestMethod.POST)
	public String getSubscriptionBills( final Model model,
										@ModelAttribute("subscriptionBillForm") final SubscriptionBillForm subscriptionBillForm,
										final RedirectAttributes redirectModel) throws CMSItemNotFoundException, SubscriptionFacadeException
	{
		try
		{
			Collection<SubscriptionBillingData> subscriptionBills = sapSubscriptionFacade.getSubscriptionBills(
					subscriptionBillForm.getFromDate(),subscriptionBillForm.getToDate());
			model.addAttribute(SUBSCRIPTION_BILL_CMS_PAGE, subscriptionBills);
		}
		catch (final Exception ex) // NOSONAR
		{
			LOG.error("error occured while fetching subscription bills " + ex);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(SUBSCRIPTION_BILL_CMS_PAGE));
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
	}

	/*
	 * Change Payment details of subscription
	 */
	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + SUBSCRIPTION_ID_PATH_VARIABLE_PATTERN + "/changePaymentDetails",  method = RequestMethod.POST)
	public String changePaymentDetails(@PathVariable("subscriptionCode") final String subscriptionCode,
									   @RequestParam(value = "invoice", required = false) final String strIsInvoicePayment,
									   final Model model,
									   @ModelAttribute("changePaymentDetailsForm") final ChangePaymentDetailsForm changePaymentDetailsForm,
									   final RedirectAttributes redirectModel)
	{
		boolean isInvoicePayment = false;
		if(!StringUtils.isEmpty(strIsInvoicePayment)){
			isInvoicePayment = Boolean.parseBoolean(strIsInvoicePayment);
		}

		String version = null;
		String paymentCardId = null;
		try {
			if (changePaymentDetailsForm != null) {
				version = changePaymentDetailsForm.getVersion();
				paymentCardId = changePaymentDetailsForm.getPaymentCardId();
			}

			validatePaymentDetails(version, paymentCardId, redirectModel, isInvoicePayment);

			if(isInvoicePayment) {
				sapSubscriptionFacade.changePaymentDetailsAsInvoice(populateChangePaymentDetailsData(subscriptionCode, version));
			}
			else {
				sapSubscriptionFacade.changePaymentDetailsAsCard(populateChangePaymentDetailsData(subscriptionCode, version), paymentCardId);
			}

			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"text.account.subscription.changePaymentDetails.success");
			return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;

		}
		catch (final Exception ex) // NOSONAR
		{
			logError(ex, "Failed: "+ex.getMessage());
		}


		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
				"text.account.subscription.changePaymentDetails.error");
		return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;

	}

	@RequireHardLogIn
	@RequestMapping(value = "/subscription/" + SUBSCRIPTION_ID_PATH_VARIABLE_PATTERN + "/reverseCancel", method = RequestMethod.POST)
	public String reverseCancellation(@PathVariable("subscriptionCode") final String subscriptionCode,
									  @ModelAttribute("reverseCancelForm") final SubscriptionReverseCancellationForm reversalForm,
									  final RedirectAttributes redirectModel) {

		try {
			sapSubscriptionFacade.reverseCancellation(
					populateReverseCancellationForm(subscriptionCode, reversalForm));

			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"text.account.subscription.cancel.reverse.success");
		} catch (final Exception ex) // NOSONAR
		{
			LOG.error("Error occurred during cancellation reversal: " + ex.getMessage());
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"text.account.subscription.cancel.reverse.error");
		}

		return REDIRECT_MY_ACCOUNT_SUBSCRIPTION_DETAIL_PAGE + subscriptionCode;
	}

	protected List<Breadcrumb> buildSubscriptionDetailBreadcrumb(final SubscriptionData subscriptionData)
	{
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions");
		breadcrumbs.get(breadcrumbs.size() - 1).setUrl("/my-account/subscriptions");
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.account.subscription.subscriptionBreadcrumb",
						new Object[] { subscriptionData.getDocumentNumber() }, "{0}",
						getI18nService().getCurrentLocale()),
				null));
		return breadcrumbs;
	}

	protected List<Breadcrumb> buildSubscriptionBillDetailBreadcrumb(final String billId)
	{
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.subscriptions.bills");
		breadcrumbs.get(breadcrumbs.size() - 1).setUrl("/my-account/subscription/bills");
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.account.subscription.subscriptionBillNumber",
						new Object[] { billId }, "{0}",
						getI18nService().getCurrentLocale()),
				null));
		return breadcrumbs;
	}

	protected SubscriptionData populateCancellationSubscriptionData(String code,
																	SubscriptionCancellationForm cancellationForm)
	{
		SubscriptionData subscriptionData = new SubscriptionData();
		subscriptionData.setVersion(cancellationForm.getVersion());
		subscriptionData.setId(code);
		subscriptionData.setValidTillDate(cancellationForm.getSubscriptionEndDate());
		subscriptionData.setEndDate(cancellationForm.getValidUntilDate());
		subscriptionData.setRatePlanId(cancellationForm.getRatePlanId());
		return subscriptionData;
	}

	protected SubscriptionData populateWithdrawalSubscriptionData(String code,
																  String version)
	{
		SubscriptionData subscriptionData = new SubscriptionData();
		subscriptionData.setVersion(version);
		subscriptionData.setId(code);
		return subscriptionData;
	}

	protected SubscriptionData populateExtendSubscriptionData(String code, SubscriptionExtensionForm extensionForm)
	{
		SubscriptionData subscriptionData = new SubscriptionData();
		subscriptionData.setVersion(extensionForm.getVersion());
		subscriptionData.setId(code);
		subscriptionData.setExtendedPeriod(extensionForm.getExtensionPeriod());
		subscriptionData.setValidTillDate(extensionForm.getValidTilldate());
		subscriptionData.setRatePlanId(extensionForm.getRatePlanId());
		subscriptionData.setUnlimited(extensionForm.isUnlimited());
		return subscriptionData;
	}

	protected SubscriptionExtensionFormData populateSubscriptionExtensionFormData(SubscriptionExtensionForm extensionForm)
	{
		MetadataData metadata = new MetadataData();
		metadata.setVersion(Integer.parseInt(extensionForm.getVersion()));

		SubscriptionExtensionFormData data = new SubscriptionExtensionFormData();
		if(extensionForm.getExtensionPeriod()!=null) {
			data.setNumberOfBillingPeriods(Integer.parseInt(extensionForm.getExtensionPeriod()));
		}
		data.setUnlimited(extensionForm.isUnlimited());
		data.setMetadata(metadata);
		return data;
	}

	protected SubscriptionData populateReverseCancellationForm(String code, SubscriptionReverseCancellationForm reversalForm){
		SubscriptionData subscriptionData = new SubscriptionData();
		subscriptionData.setVersion(reversalForm.getVersion());
		subscriptionData.setId(code);

		return subscriptionData;
	}

	protected boolean validateExtensionForm(final SubscriptionExtensionForm subscriptionExtensionForm,final RedirectAttributes redirectModel)
	{
		boolean errors = false;
		if(subscriptionExtensionForm.isUnlimited())
		{
			return false;
		}
		String extendedperiod = subscriptionExtensionForm.getExtensionPeriod();
		if(extendedperiod.isEmpty())
		{
			errors = true;
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,"subscription.extend.empty.period");
			return errors;
		}
		try
		{
			Integer.parseInt(extendedperiod);
		}
		catch (NumberFormatException ne)
		{
			errors = true;
			LOG.error("Entered value is not a valid number");
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,"enter a valid number");
			return errors;
		}
		return errors;
	}

	/**
	 * Validates payment details
	 */
	protected boolean validatePaymentDetails(String version, String paymentCardToken, RedirectAttributes redirectModel, boolean isInvoicePayment)
	{
		boolean success = false;

		if(StringUtils.isBlank(paymentCardToken) && !isInvoicePayment) {
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,"subscription.changePaymentDetails.empty.card");
			LOG.error("Payment Card token Cannot be empty");
		}
		else if(StringUtils.isBlank(version)) {
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,"subscription.changePaymentDetails.empty.version");
			LOG.error("Subscription version cannot be empty");
		}
		else{
			success = true;
		}

		return success;
	}

	protected SubscriptionData populateChangePaymentDetailsData(String code, String version)
	{
		SubscriptionData subscriptionData = new SubscriptionData();
		subscriptionData.setVersion(version);
		subscriptionData.setId(code);
		return subscriptionData;
	}

	@SuppressWarnings({"removal"})
    private <T> SearchPageDataWrapper<T> populateOldSearchPageData(SearchPageData<T> searchPageData){

        SearchPageDataWrapper<T> oldSearchPageData = new SearchPageDataWrapper<>();

        //Pagination Data
        PaginationData paginationData = searchPageData.getPagination();
        PaginationDataWrapper oldPaginationData = new PaginationDataWrapper();
        oldPaginationData.setCurrentPage(paginationData.getCurrentPage());
        oldPaginationData.setNumberOfPages(paginationData.getNumberOfPages());
        oldPaginationData.setPageSize(paginationData.getPageSize());
        oldPaginationData.setTotalNumberOfResults(paginationData.getTotalNumberOfResults());

        //SearchPageData
        oldSearchPageData.setPagination(oldPaginationData);
        oldSearchPageData.setResults(searchPageData.getResults());
        return oldSearchPageData;
    }

    @SuppressWarnings({"removal"})
    private List<SortData> populateSortDataList(String currentSort, List<String> sortCodeList){

        List<SortData> sortDataList = new LinkedList<>();
	    for(String sortCode: sortCodeList){
	        SortData sortData = new SortData();
	        sortData.setCode(sortCode);

	        boolean isSelected = StringUtils.equals(sortCode, currentSort);
            sortData.setSelected(isSelected);

            sortDataList.add(sortData);
        }

	    return sortDataList;
    }


    /**
	 * @param ex exception to log
	 */
	private static void logError(final Exception ex, final String errorMessage)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(errorMessage + ex);
		}
		LOG.error(errorMessage);
	}

    @SuppressWarnings({"removal"})
	private static class SearchPageDataWrapper<T> extends de.hybris.platform.commerceservices.search.pagedata.SearchPageData<T>{}

    @SuppressWarnings({"removal"})
    private static class PaginationDataWrapper extends de.hybris.platform.commerceservices.search.pagedata.PaginationData{}
}

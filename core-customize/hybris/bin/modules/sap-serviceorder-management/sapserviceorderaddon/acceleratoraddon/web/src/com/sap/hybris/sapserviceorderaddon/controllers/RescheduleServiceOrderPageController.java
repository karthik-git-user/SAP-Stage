/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderaddon.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.hybris.sapserviceorderaddon.forms.ServiceDetailsForm;
import com.sap.hybris.sapserviceorderaddon.forms.validators.ServiceDetailsFormValidator;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * Controller for cancel order pages
 */
@Controller
@RequestMapping(value = "/my-account/order")
public class RescheduleServiceOrderPageController extends AbstractSearchPageController
{
	private static final String TEXT_ACCOUNT_ORDER_ORDER_DETAILS_SERVICE_RESCHEDULE_POPUP_CONFIRMATION_ERROR = "text.account.order.orderDetails.service.reschedule.popup.confirmation.error";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final String REDIRECT_TO_ORDERS_HISTORY_PAGE = REDIRECT_PREFIX + "/my-account/orders";
	private static final String RESCHEDULE_SERVICE_ORDER_CMS_PAGE = "reschedule-service-order";
	
	private static final String SERVICE_DATE_FORMAT = "dd-MM-yyyyHH:mm";

	private static final String ZERO = "0";

	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "sapServiceOrderCheckoutFacade")
	private SapServiceOrderCheckoutFacade sapServiceOrderCheckoutFacade;
	
	@Resource(name = "sapServiceScheduleTimeList")
	private List<String> scheduleTimeList;
	
	@Resource(name = "serviceDetailsFormValidator")
	private ServiceDetailsFormValidator serviceDetailsFormValidator;
	
	
	@RequireHardLogIn
	@RequestMapping(value = "/{orderCode:.*}/rescheduleservice", method = RequestMethod.POST)
	public String rescheduleServiceOrder(@PathVariable("orderCode") final String orderCode,
			final Model model,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		// This will throw a runtime exception if the current customer or base store is not matching the passed.
		try
		{
			final OrderData orderData = getOrderData(orderCode);
			
			final Integer leadDays = sapServiceOrderCheckoutFacade.getLeadDaysForService();
			model.addAttribute("containsService", sapServiceOrderCheckoutFacade.containsServiceProductInCart());
			model.addAttribute("serviceScheduleTimeList", new ArrayList<String>(scheduleTimeList));
			final ServiceDetailsForm form = getServiceDetailsForm();
			model.addAttribute("rescheduleServiceForm", form);
			model.addAttribute("defaultServiceTime", form.getServiceTime());
			model.addAttribute("scheduleLeadDays", leadDays);
			model.addAttribute("orderCode", orderCode);
			model.addAttribute("orderData", orderData);
			
			setBreadcrumbs(model, orderCode, orderData);
			
		}
		catch (final Exception exception) //NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, TEXT_ACCOUNT_ORDER_ORDER_DETAILS_SERVICE_RESCHEDULE_POPUP_CONFIRMATION_ERROR);
			return REDIRECT_TO_ORDERS_HISTORY_PAGE;
		}
		
		storeCmsPageInModel(model, getContentPageForLabelOrId(RESCHEDULE_SERVICE_ORDER_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(RESCHEDULE_SERVICE_ORDER_CMS_PAGE));
		return getViewForPage(model); 
	}
	
	/*
 	 * Submit the confirmed cancel items to be cancelled
 	 */
	@RequireHardLogIn
	@RequestMapping(value = "/{orderCode:.*}/rescheduleservice/submit", method = RequestMethod.POST)
	public String postRescheduleServiceOrder(@ModelAttribute ServiceDetailsForm rescheduleServiceForm, final BindingResult bindingResult,
			final Model model, @PathVariable("orderCode") final String orderCode,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		// This will throw a runtime exception if the current customer or base store is not matching the passed.
		try
		{
			final OrderData orderData = getOrderData(orderCode);
			
			serviceDetailsFormValidator.validate(rescheduleServiceForm, bindingResult);
			if (bindingResult.hasErrors())
			{
				final Integer leadDays = sapServiceOrderCheckoutFacade.getLeadDaysForService();
				model.addAttribute("rescheduleServiceForm", rescheduleServiceForm);
				model.addAttribute("defaultServiceTime", rescheduleServiceForm.getServiceTime());
				model.addAttribute("containsService", sapServiceOrderCheckoutFacade.containsServiceProductInCart());
				model.addAttribute("serviceScheduleTimeList", new ArrayList<String>(scheduleTimeList));
				model.addAttribute("scheduleLeadDays", leadDays);
				model.addAttribute("orderCode", orderCode);
				model.addAttribute("orderData", orderData);
				
				GlobalMessages.addErrorMessage(model, "text.account.order.orderDetails.service.reschedule.confirmation.invalidform");
				setBreadcrumbs(model, orderCode, orderData);
				storeCmsPageInModel(model, getContentPageForLabelOrId(RESCHEDULE_SERVICE_ORDER_CMS_PAGE));
				model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
				setUpMetaDataForContentPage(model, getContentPageForLabelOrId(RESCHEDULE_SERVICE_ORDER_CMS_PAGE));
				return getViewForPage(model); 
			}
			else
			{
				final String scheduleDateTime = rescheduleServiceForm.getServiceDate().concat(rescheduleServiceForm.getServiceTime());
				final SimpleDateFormat dateFormat = new SimpleDateFormat(SERVICE_DATE_FORMAT);

				boolean result = sapServiceOrderCheckoutFacade.rescheduleServiceRequestDate(orderCode, dateFormat.parse(scheduleDateTime));
				if(result) {
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, getMessageSource()
							.getMessage("text.account.order.orderDetails.service.reschedule.popup.confirmation.success", null, getI18nService().getCurrentLocale()), null);
				} else { 
					GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, getMessageSource()
							.getMessage(TEXT_ACCOUNT_ORDER_ORDER_DETAILS_SERVICE_RESCHEDULE_POPUP_CONFIRMATION_ERROR, null, getI18nService().getCurrentLocale()), null);
				}
				return REDIRECT_TO_ORDERS_HISTORY_PAGE;
			}
			
		}
		catch (final Exception exception) //NOSONAR
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, TEXT_ACCOUNT_ORDER_ORDER_DETAILS_SERVICE_RESCHEDULE_POPUP_CONFIRMATION_ERROR);
			return REDIRECT_TO_ORDERS_HISTORY_PAGE;
		}
		
	}

	protected void setBreadcrumbs(final Model model, final String orderCode, final OrderData orderData) {
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb("/my-account/orders",
				getMessageSource().getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("/my-account/order/" + orderCode, getMessageSource()
				.getMessage("text.account.order.orderBreadcrumb", new Object[] { orderData.getCode() }, "Order {0}",
						getI18nService().getCurrentLocale()), null));
		breadcrumbs.add(new Breadcrumb("#",
				getMessageSource().getMessage("text.account.order.orderDetails.service.reschedule.button", null, getI18nService().getCurrentLocale()), null));
		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
	}

	protected OrderData getOrderData(final String orderCode) {
		final OrderData orderData = orderFacade.getOrderDetailsForCode(orderCode);
		List<OrderEntryData> orderEntriesData = orderData.getEntries().stream().filter(SapServiceOrderUtil::isServiceEntry).collect(Collectors.toList());
		orderData.setEntries(orderEntriesData);
		return orderData;
	}
	
	
	/**
	 * Populates service details and returns it
	 *
	 * @return serviceDetailsForm
	 */
	protected ServiceDetailsForm getServiceDetailsForm()
	{

		final Date serviceDate = sapServiceOrderCheckoutFacade.getServiceLeadDate();
		final ServiceDetailsForm form = new ServiceDetailsForm();
		populateServiceDetailsForm(form, serviceDate);
		return form;
	}
	
	/**
	 * Populate form with service date
	 *
	 * @param form
	 *           service details form
	 * @param serviceDate
	 *           service date
	 */
	protected void populateServiceDetailsForm(final ServiceDetailsForm form, final Date serviceDate)
	{
		final Calendar c = Calendar.getInstance();
		c.setTime(serviceDate);
		String date = Integer.toString(c.get(Calendar.DATE));
		String month = Integer.toString(c.get(Calendar.MONTH) + 1);
		String year = Integer.toString(c.get(Calendar.YEAR));
		String hours = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
		String minutes = Integer.toString(c.get(Calendar.MINUTE));
		if (date.length() == 1)
		{
			date = ZERO.concat(date);
		}

		if (month.length() == 1)
		{
			month = ZERO.concat(month);
		}

		if (hours.length() == 1)
		{
			hours = ZERO.concat(hours);
		}

		if (minutes.length() == 1)
		{
			minutes = ZERO.concat(minutes);
		}
		date = date.concat("-").concat(month).concat("-").concat(year);
		final String time = hours.concat(":").concat(minutes);

		form.setServiceDate(date);
		form.setServiceTime(time);
		
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderaddon.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.hybris.sapserviceorderaddon.forms.ServiceDetailsForm;
import com.sap.hybris.sapserviceorderaddon.forms.validators.ServiceDetailsFormValidator;


@Controller
@RequestMapping(value = "/checkout/multi/service-details")
public class SapServiceOrderCheckoutStepController extends AbstractCheckoutStepController
{

	private static final String SERVICE_DATE_FORMAT = "dd-MM-yyyyHH:mm";

	private static final String SERVICE_DETAILS = "service-details";

	private static final String ZERO = "0";

	private static final Logger LOG = Logger.getLogger(SapServiceOrderCheckoutStepController.class);

	@Resource(name = "sapServiceScheduleTimeList")
	private List<String> scheduleTimeList;

	@Resource(name = "sapServiceOrderCheckoutFacade")
	private SapServiceOrderCheckoutFacade sapServiceOrderCheckoutFacade;

	@Resource(name = "serviceDetailsFormValidator")
	private ServiceDetailsFormValidator serviceDetailsFormValidator;



	@Override
	@RequestMapping(method = RequestMethod.GET)
	@RequireHardLogIn
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{

		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		final Integer leadDays = sapServiceOrderCheckoutFacade.getLeadDaysForService();
		model.addAttribute("cartData", cartData);
		model.addAttribute("containsService", sapServiceOrderCheckoutFacade.containsServiceProductInCart());
		model.addAttribute("serviceScheduleTimeList", new ArrayList<String>(scheduleTimeList));
		final ServiceDetailsForm form = getServiceDetailsForm();
		model.addAttribute("serviceDetailsForm", form);
		model.addAttribute("defaultServiceTime", form.getServiceTime());
		model.addAttribute("scheduleLeadDays", leadDays);
		this.prepareDataForPage(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.serviceDetails.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return SapserviceorderaddonControllerConstants.SERVICE_DETAILS_PAGE;
	}

	/**
	 * Populates service details and returns it
	 *
	 * @return serviceDetailsForm
	 */
	protected ServiceDetailsForm getServiceDetailsForm()
	{

		final Date serviceDate = sapServiceOrderCheckoutFacade.getRequestedServiceDate() != null
				? sapServiceOrderCheckoutFacade.getRequestedServiceDate()
				: sapServiceOrderCheckoutFacade.getServiceLeadDate();
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
		final String year = Integer.toString(c.get(Calendar.YEAR));
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



	@RequestMapping(value = "/choose", method = RequestMethod.POST)
	@RequireHardLogIn
	public String choose(@ModelAttribute
	final ServiceDetailsForm serviceDetailsForm, final BindingResult bindingResult, final Model model)
			throws CMSItemNotFoundException, CommerceCartModificationException, ParseException
	{
		final Boolean containsServiceProduct = sapServiceOrderCheckoutFacade.containsServiceProductInCart();
		if (containsServiceProduct)
		{
			serviceDetailsFormValidator.validate(serviceDetailsForm, bindingResult);
			if (bindingResult.hasErrors())
			{
				final CartData cartData = getCheckoutFacade().getCheckoutCart();
				final Integer leadDays = sapServiceOrderCheckoutFacade.getLeadDaysForService();
				GlobalMessages.addErrorMessage(model, "checkout.error.servicedetails.formentry.invalid");
				model.addAttribute("cartData", cartData);
				model.addAttribute("serviceDetailsForm", serviceDetailsForm);
				model.addAttribute("defaultServiceTime", serviceDetailsForm.getServiceTime());
				model.addAttribute("containsService", sapServiceOrderCheckoutFacade.containsServiceProductInCart());
				model.addAttribute("serviceScheduleTimeList", new ArrayList<String>(scheduleTimeList));
				model.addAttribute("scheduleLeadDays", leadDays);
				prepareDataForPage(model);
				final ContentPageModel multiCheckoutSummaryPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
				storeCmsPageInModel(model, multiCheckoutSummaryPage);
				setUpMetaDataForContentPage(model, multiCheckoutSummaryPage);
				model.addAttribute(WebConstants.BREADCRUMBS_KEY,
						getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.serviceDetails.breadcrumb"));
				model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
				setCheckoutStepLinksForModel(model, getCheckoutStep());
				return SapserviceorderaddonControllerConstants.SERVICE_DETAILS_PAGE;
			}
			else
			{
				final String scheduleDateTime = serviceDetailsForm.getServiceDate().concat(serviceDetailsForm.getServiceTime());
				final SimpleDateFormat dateFormat = new SimpleDateFormat(SERVICE_DATE_FORMAT);
				LOG.info("service date and time - " + dateFormat.parse(scheduleDateTime));

				sapServiceOrderCheckoutFacade.updateCartWithServiceScheduleDate(dateFormat.parse(scheduleDateTime));
			}
		}

		return getCheckoutStep().nextStep();
	}

	@RequireHardLogIn
	@Override
	@RequestMapping(value = "/back", method = RequestMethod.GET)
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequireHardLogIn
	@Override
	@RequestMapping(value = "/next", method = RequestMethod.GET)
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(SERVICE_DETAILS);
	}
}

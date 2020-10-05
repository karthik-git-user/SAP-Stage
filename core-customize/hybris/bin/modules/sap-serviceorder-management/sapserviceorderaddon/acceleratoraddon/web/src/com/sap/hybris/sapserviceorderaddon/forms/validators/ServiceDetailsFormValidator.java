/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapserviceorderaddon.forms.validators;


import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sap.hybris.sapserviceorderaddon.forms.ServiceDetailsForm;


/**
 * Validator for {@link ServiceDetailsForm}.
 */
@Component("serviceDetailsFormValidator")
public class ServiceDetailsFormValidator implements Validator
{

	private static final String SERVICE_DATE_FIELD = "serviceDate";
	private static final String DATE_REGEX = "^([0-2][0-9]|(3)[0-1])(-)(((0)[0-9])|((1)[0-2]))(-)\\d{4}$";
	private static final String SERVICE_DATE_FORMAT = "dd-MM-yyyyHH:mm";

	@Resource(name = "sapServiceOrderCheckoutFacade")
	private SapServiceOrderCheckoutFacade sapServiceOrderCheckoutFacade;

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return ServiceDetailsForm.class.equals(clazz);
	}

	@Override
	public void validate(final Object form, final Errors errors)
	{
		if (form instanceof ServiceDetailsForm)
		{
			final ServiceDetailsForm serviceDetailsForm = (ServiceDetailsForm) form;

			if (serviceDetailsForm.getServiceDate() == null || serviceDetailsForm.getServiceDate().isEmpty())
			{
				errors.rejectValue(SERVICE_DATE_FIELD, "general.required");
				return;
			}

			if (serviceDetailsForm.getServiceDate() != null && !serviceDetailsForm.getServiceDate().isEmpty()
					&& !Pattern.matches(DATE_REGEX, serviceDetailsForm.getServiceDate()))
			{
				errors.rejectValue(SERVICE_DATE_FIELD, "checkout.multi.servicedateformat.error");
				return;
			}

			final Date leadDate = sapServiceOrderCheckoutFacade.getServiceLeadDate();

			final SimpleDateFormat dateFormat = new SimpleDateFormat(SERVICE_DATE_FORMAT);
			Date serviceDate;
			try
			{
				serviceDate = dateFormat.parse(serviceDetailsForm.getServiceDate().concat(serviceDetailsForm.getServiceTime()));
			}
			catch (final ParseException e)
			{
				errors.rejectValue(SERVICE_DATE_FIELD, "checkout.multi.servicedateformat.error");
				return;
			}

			if (serviceDate.before(leadDate))
			{
				errors.rejectValue(SERVICE_DATE_FIELD, "checkout.multi.servicedatevalid.error");
			}


		}


	}

}

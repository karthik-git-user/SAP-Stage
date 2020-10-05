/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.utils;

import de.hybris.platform.scimfacades.ScimUserEmail;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;


/**
 * Scim Utility class
 */
public class ScimUtils
{

	/**
	 * Constructor to suppress creation of objects of utility class
	 */
	private ScimUtils()
	{

	}

	/**
	 * Get first primary email from list of emails
	 *
	 * @return emailValue
	 */
	public static ScimUserEmail getPrimaryEmail(final List<ScimUserEmail> emails)
	{
		if (CollectionUtils.isNotEmpty(emails))
		{
			final List<ScimUserEmail> filteredEmails = emails.stream().filter(item -> BooleanUtils.isTrue(item.getPrimary()))
					.collect(Collectors.toList());

			return CollectionUtils.isNotEmpty(filteredEmails) ? filteredEmails.get(0) : null;
		}
		return null;
	}

}

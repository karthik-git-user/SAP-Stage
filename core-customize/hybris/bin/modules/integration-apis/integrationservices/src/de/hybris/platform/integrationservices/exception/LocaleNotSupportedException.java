/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.Locale;

/**
 * Indicates an attempt to use an invalid or unsupported ISO code with the localized data.
 */
public class LocaleNotSupportedException extends RuntimeException
{
	private final String language;

	/**
	 * Constructor to create LanguageTagNotSupportedException
	 *
	 * @param locale locale that does not correspond to an existing {@link LanguageModel}
	 */
	public LocaleNotSupportedException(final Locale locale)
	{
		super(String.format("The language provided [%s] is not available in the system.", locale));
		this.language = locale == null ? "" : locale.getLanguage();
	}

	public String getLanguage()
	{
		return language;
	}
}

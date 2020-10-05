/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.common.consent.impl;

import com.hybris.ymkt.common.consent.YmktConsentService;


/**
 * Empty implementation of {@link YmktConsentService} when the <code>sapymktconsent</code> extension is not used.<br>
 */
public class DefaultYmktConsentService implements YmktConsentService
{

	@Override
	public boolean getUserConsent(final String consentID)
	{
		return true;
	}

	@Override
	public boolean getUserConsent(final String customerId, final String consentID)
	{
		return true;
	}

}

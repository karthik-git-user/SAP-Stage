/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.consent.impl;

import java.util.Date;

import de.hybris.platform.commerceservices.consent.impl.DefaultCommerceConsentService;
import de.hybris.platform.commerceservices.event.ConsentGivenEvent;
import de.hybris.platform.commerceservices.event.ConsentWithdrawnEvent;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.consent.GigyaConsentService;

/**
 * Default implementation of GigyaConsentService
 */
public class DefaultGigyaConsentService extends DefaultCommerceConsentService implements GigyaConsentService {

	@Override
	public void giveConsent(final CustomerModel customer, final ConsentTemplateModel template,
			Date lastConsentModified) {

		final ConsentModel latestConsent = getActiveConsent(customer, template);

		if (latestConsent == null) {
			final ConsentModel consent = createConsentModel(customer, template);
			updateConsetGivenDateAndSave(lastConsentModified, consent);
		} else {
			// Update the existing consent entry itself and re-trigger ConsentGivenEvent if time stamp differs
			latestConsent.setConsentWithdrawnDate(null);
			updateConsetGivenDateAndSave(lastConsentModified, latestConsent);
		}
	}

	/**
	 * Method to update the date in consentGivenDate attribute of Consent model,
	 * save it and publish 'ConsentGivenEvent'.
	 */
	private void updateConsetGivenDateAndSave(Date lastConsentModified, ConsentModel latestConsent) {
		
		if (!(latestConsent.getConsentGivenDate() != null
				&& latestConsent.getConsentGivenDate().compareTo(lastConsentModified) == 0)) {
		
			latestConsent.setConsentGivenDate(lastConsentModified);
			getModelService().save(latestConsent);
			getEventService().publishEvent(initializeEvent(new ConsentGivenEvent(), latestConsent));
		}
	}

	@Override
	public void withdrawConsent(CustomerModel customer, ConsentTemplateModel template, Date lastConsentModified) {

		final ConsentModel consent = getActiveConsent(customer, template);

		if (consent != null && consent.getConsentWithdrawnDate() == null) {
			consent.setConsentWithdrawnDate(lastConsentModified);

			getModelService().save(consent);
			getEventService().publishEvent(initializeEvent(new ConsentWithdrawnEvent(), consent));
		}
	}

}

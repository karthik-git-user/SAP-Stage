/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.consent.impl;

import java.io.InvalidClassException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;

import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyafacades.consent.GigyaConsentFacade;
import de.hybris.platform.gigya.gigyaservices.consent.GigyaConsentService;
import de.hybris.platform.site.BaseSiteService;

/**
 * Default implementation of GigyaConsentFacade
 */
public class DefaultGigyaConsentFacade implements GigyaConsentFacade {

	private static final Logger LOG = Logger.getLogger(DefaultGigyaConsentFacade.class);

	private BaseSiteService baseSiteService;

	private GigyaConsentService gigyaConsentService;

	@Override
	public void synchronizeConsents(GSObject preferences, UserModel gigyaUser) {

		if (preferences != null && gigyaUser instanceof CustomerModel) {
			final CustomerModel customer = (CustomerModel) gigyaUser;
			final List<ConsentTemplateModel> templatesForCurrentSite = gigyaConsentService
					.getConsentTemplates(baseSiteService.getCurrentBaseSite());

			if (CollectionUtils.isNotEmpty(templatesForCurrentSite)) {
				templatesForCurrentSite.forEach(template -> {
					final String[] consentLevels = template.getId().split("\\.");
					final int version = template.getVersion();
					final GSObject preference = getPreferenceForTemplate(preferences, consentLevels, version);
					if (preference != null) {
						updateConsentFromGigya(customer, template, preference);
					}
				});

			}
		}
	}

	/**
	 * Method to update consent received from SAP CDC based on 'isConsentGranted'
	 * flag.
	 */
	private void updateConsentFromGigya(CustomerModel customer, ConsentTemplateModel template, GSObject preference) {

		final boolean isConsentGranted = Boolean.parseBoolean(getValueFromObject(preference, "isConsentGranted"));
		
		final String lastConsentModifiedAsCDCDateString = getValueFromObject(preference, "lastConsentModified");
		Date lastConsentModified = convertCDCDateStringToDate(lastConsentModifiedAsCDCDateString);
		lastConsentModified = lastConsentModified == null ? new Date() : lastConsentModified;

		if (isConsentGranted) {
			gigyaConsentService.giveConsent(customer, template, lastConsentModified);
		} else {
			gigyaConsentService.withdrawConsent(customer, template, lastConsentModified);
		}
	}

	/**
	 * Method to get value from the GSobject based on the key passed.
	 */
	private String getValueFromObject(GSObject preference, String key) {
		try {
			return preference.getString(key);
		} catch (GSKeyNotFoundException e) {
			LOG.error("Error while fetching information from CDC for key=" + key, e);
		}
		return null;
	}

	/**
	 * Method to get the valid preference object based on the consent level passed
	 * from various preferences returned from SAP CDC, null is returned if a valid
	 * preference object is not found.
	 */
	private GSObject getPreferenceForTemplate(GSObject preferences, String[] consentLevels, int version) {
		for (int i = 0; i < consentLevels.length; i++) {
			try {
				if (preferences.containsKey(consentLevels[i]) && i == consentLevels.length - 1) {
					GSObject consent = preferences.getObject(consentLevels[i]);
					if(consent.containsKey("docVersion")) {
						int docVersion = (int) consent.getDouble("docVersion");
						if(version == docVersion) {
							return preferences.getObject(consentLevels[i]);
						}
					}
					
				} else {
					preferences = preferences.getObject(consentLevels[i]);
				}
			} catch (GSKeyNotFoundException e) {
				LOG.error("Error while fetching preference " + e);
			} catch (InvalidClassException e) {
				LOG.error("Error while fetching preference ", e);
			}catch(NullPointerException e) {
				LOG.error("Error while fetching preference ", e);
			} 
		}
		return null;
	}

	/**
	 * Method to convert the UTC date returned from SAP CDC to date object, it
	 * returns null, if date string is not parsed successfully.
	 */
	private static Date convertCDCDateStringToDate(String date) {
		if (StringUtils.isNotEmpty(date)) {
			try {
				return Date.from(Instant.parse(date));
			} catch (Exception e) {
				LOG.error("Error parsing date received from CDC " + date, e);
			}
		}
		return null;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	public GigyaConsentService getGigyaConsentService() {
		return gigyaConsentService;
	}

	@Required
	public void setGigyaConsentService(GigyaConsentService gigyaConsentService) {
		this.gigyaConsentService = gigyaConsentService;
	}
}

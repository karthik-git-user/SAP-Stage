/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.consent;

import java.util.Date;

import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;

/**
 * Service to carry out consent related actions like give consent and withdraw
 * consent.
 */
public interface GigyaConsentService extends CommerceConsentService {

	/**
	 * Method to give consent, it either creates a new consent model it it doesn't
	 * exist. If it exists then update the existing model with last consent modified
	 * date
	 * 
	 * @param customer
	 * 			the customer who gave consent
	 * @param template
	 * 			the consent template
	 * @param lastConsentModified
	 * 			the last consent modified date
	 */
	void giveConsent(CustomerModel customer, ConsentTemplateModel template, Date lastConsentModified);

	/**
	 * Method to withdraw consent for the customer and consent template
	 * 
	 * @param customer
	 * 			the customer who withdrew the consent
	 * @param template
	 * 			the consent template
	 * @param lastConsentModified
	 * 			the last consent modified date
	 */
	void withdrawConsent(CustomerModel customer, ConsentTemplateModel template, Date lastConsentModified);

}

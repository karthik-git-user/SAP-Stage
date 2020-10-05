/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.consent;

import com.gigya.socialize.GSObject;

import de.hybris.platform.core.model.user.UserModel;

/**
 * Facade to carry out SAP CDC consent specific functionality
 */
public interface GigyaConsentFacade {

	/**
	 * Method to synchronize preferences received from SAP CDC to commerce only if
	 * consent templates are found
	 * 
	 * @param preferences
	 * 			the preferences of the user
	 * @param gigyaUser
	 * 			the customer
	 */
	void synchronizeConsents(GSObject preferences, UserModel gigyaUser);
}

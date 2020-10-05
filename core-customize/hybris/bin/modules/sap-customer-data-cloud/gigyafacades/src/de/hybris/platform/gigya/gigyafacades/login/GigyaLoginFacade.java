/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.login;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;

import com.gigya.socialize.GSKeyNotFoundException;


/**
 * Facade to carry out gigya login functionality
 */
public interface GigyaLoginFacade
{

	/**
	 * Method to process gigya login
	 *
	 * @param jsInfo
	 *           the js info object
	 * @param gigyaConfig
	 *           the gigya config model
	 * @return boolean, true if successfully processed otherwise false
	 */
	boolean processGigyaLogin(final GigyaJsOnLoginInfo jsInfo, GigyaConfigModel gigyaConfig);

	/**
	 * Get hybris uid for gigya user ID
	 *
	 * @param gigyaUid
	 *           the gigya uid
	 * @return String the uid of gigya user
	 */
	String getHybrisUidForGigyaUser(final String gigyaUid);

	/**
	 * Create new customer from gigya Uid
	 *
	 * @param gigyaConfig
	 *           the gigyaConfig model
	 * @param uid
	 *           the uid
	 * @throws DuplicateUidException
	 *            When duplicate uid found
	 * @return UserModel the user model created
	 */
	UserModel createNewCustomer(GigyaConfigModel gigyaConfig, String uid) throws DuplicateUidException;

	/**
	 * Processes gigya profile update
	 *
	 * @param jsInfo
	 *           the gigya js info object
	 * @param gigyaConfig
	 *           the gigya config model
	 * @return boolean, true if successfully processed
	 */
	boolean processGigyaProfileUpdate(final GigyaJsOnLoginInfo jsInfo, GigyaConfigModel gigyaConfig);

	/**
	 * Method to update user by scheduling update user task
	 *
	 * @param gigyaConfig
	 *           the gigyaConfig model
	 * @param user
	 *           the user model
	 * @throws GSKeyNotFoundException
	 *            exception when gs key not found
	 */
	void updateUser(GigyaConfigModel gigyaConfig, UserModel user) throws GSKeyNotFoundException;

}

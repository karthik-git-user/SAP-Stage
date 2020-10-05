/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.login.impl;

import de.hybris.platform.gigya.gigyaservices.data.GigyaAccount;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.login.impl.DefaultGigyaLoginService;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.socialize.GSResponse;


/**
 * B2B specific implementation of GigyaLoginService
 */
public class DefaultGigyaB2BLoginService extends DefaultGigyaLoginService
{

	@Override
	protected GigyaUserObject prepareAndGetUserObject(final ObjectMapper mapper, final GSResponse res) throws IOException
	{
		final GigyaAccount accObj = mapper.readValue(res.getData().toJsonString(), GigyaAccount.class);
		final GigyaUserObject usrObj = accObj.getProfile();
		usrObj.setLoginIDs(accObj.getLoginIDs());
		usrObj.setUID(accObj.getUID());
		usrObj.setGroups(accObj.getGroups());
		usrObj.setPreferences(accObj.getPreferences());
		usrObj.setDataCenter(accObj.getDataCenter());
		return usrObj;
	}
}

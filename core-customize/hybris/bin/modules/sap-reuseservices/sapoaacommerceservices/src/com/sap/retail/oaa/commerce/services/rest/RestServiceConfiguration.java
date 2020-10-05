/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.rest;

import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.retail.oaa.model.enums.Sapoaa_mode;


/**
 *
 */
public interface RestServiceConfiguration
{

	/**
	 * @param sapCarClient
	 */
	void setSapCarClient(String sapCarClient);

	/**
	 * @return sapCarClient
	 */
	String getSapCarClient();

	/**
	 * @param targetUrl
	 */
	void setTargetUrl(String targetUrl);

	/**
	 * @return targetUrl
	 */
	String getTargetUrl();

	/**
	 * @param password
	 */
	void setPassword(String password);

	/**
	 * @return password
	 */
	String getPassword();

	/**
	 * @param user
	 */
	void setUser(String user);

	/**
	 * @return user
	 */
	String getUser();

	/**
	 * @throws RestInitializationException
	 */
	void initializeConfiguration();

	/**
	 * @return oaaProfile
	 */
	String getOaaProfile();

	/**
	 * @param oaaProfile
	 */
	void setOaaProfile(String oaaProfile);

	/**
	 * @return connectionTimeout
	 */
	int getConnectTimeout();

	/**
	 * @return restTimeOut
	 */
	int getReadTimeout();

	/**
	 * @param itemCategory
	 *           for drop shipment
	 */
	void setItemCategory(String itemCategory);

	/**
	 * @return itemCategory for drop shipment
	 */
	String getItemCategory();

	/**
	 *
	 * @param salesChannel
	 */
	void setSalesChannel(String salesChannel);

	/**
	 *
	 * @return salesChannel
	 */
	String getSalesChannel();

	/**
	 *
	 * @param mode
	 */
	void setMode(Sapoaa_mode mode);

	/**
	 *
	 * @return mode
	 */
	Sapoaa_mode getMode();

	/**
	 *
	 * @param consumerId
	 */
	void setConsumerId(String consumerId);

	/**
	 *
	 * @return consumerId
	 */
	String getConsumerId();
}

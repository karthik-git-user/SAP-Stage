/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.impl;

import org.apache.commons.lang.StringUtils;

import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.retail.oaa.model.constants.SapoaamodelConstants;
import com.sap.retail.oaa.model.enums.Sapoaa_mode;

import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.util.Config;


/**
 * DefaultRestServiceConfiguration
 */
public class DefaultRestServiceConfiguration implements RestServiceConfiguration
{

	private String user;
	private String password;
	private String targetUrl;
	private String sapCarClient;
	private String oaaProfile;
	private String consumerId;
	private String salesChannel;
	private Sapoaa_mode mode;
	private String itemCategory;
	private int connectTimeout;
	private int readTimeout;

	private SAPGlobalConfigurationService sapGlobalConfigurationService;
	private SAPConfigurationService sapCoreConfigurationService;

	private static final String CONNECTION_TIMEOUT = "sapoaacommerceservices.rest.connectTimeout";
	private static final String READ_TIMEOUT = "sapoaacommerceservices.rest.readTimeout";

	protected void initializeGlobalConfiguration()
	{
		if (!sapGlobalConfigurationService.sapGlobalConfigurationExists())
		{
			throw new RestInitializationException("No global SAP configuration exists");
		}

		sapCarClient = (String) sapGlobalConfigurationService.getProperty(SapoaamodelConstants.SAPOAA_CARCLIENT);


		final ConfigurationPropertyAccess configurationPropertyAccess = sapGlobalConfigurationService.getAllPropertyAccesses()
				.get(SapoaamodelConstants.SAPOAA_CARHTTPDESTINATION);
		if (configurationPropertyAccess == null)
		{
			throw new RestInitializationException("No Http Destination maintained for REST Services");
		}

		user = (String) configurationPropertyAccess.getProperty(SAPHTTPDestinationModel.USERID);
		targetUrl = (String) configurationPropertyAccess.getProperty(SAPHTTPDestinationModel.TARGETURL);
		password = (String) configurationPropertyAccess.getProperty(SAPHTTPDestinationModel.PASSWORD);

	}

	@Override
	public void initializeConfiguration()
	{
		initializeGlobalConfiguration();
		initializeBaseStoreConfiguration();
		readProperties();
		validateConfiguration();
	}

	protected void readProperties()
	{
		connectTimeout = Integer.parseInt(Config.getParameter(CONNECTION_TIMEOUT));
		readTimeout = Integer.parseInt(Config.getParameter(READ_TIMEOUT));
	}

	protected void initializeBaseStoreConfiguration()
	{
		oaaProfile = (String) sapCoreConfigurationService.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_OAAPROFILE);
		salesChannel = (String) sapCoreConfigurationService.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_SALESCHANNEL);
		consumerId = (String) sapCoreConfigurationService.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_CONSUMERID);
		itemCategory = (String) sapCoreConfigurationService.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_VENDOR_ITEM_CATEGORY);
		mode = (Sapoaa_mode) sapCoreConfigurationService.getProperty(SapoaamodelConstants.PROPERTY_SAPOAA_MODE);
	}

	protected void validateConfiguration()
	{
		validateHttpDestination();
		validateMode();
	}

	protected void validateMode()
	{
		if (mode != null && mode.equals(Sapoaa_mode.SALESCHANNEL))
		{
			if (salesChannel == null || salesChannel.isEmpty())
			{
				throw new RestInitializationException("Sales Channel is not maintained");
			}
		}
		else
		{
			if (StringUtils.isEmpty(consumerId) || StringUtils.isEmpty(oaaProfile) )
			{
				throw new RestInitializationException("Oaa Consumer Id or Oaa Profile are not maintained");
			}
		}
	}

	protected void validateHttpDestination()
	{
		if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password) || StringUtils.isEmpty(targetUrl))
		{
			throw new RestInitializationException("Http Destination for REST Services is not maintained properly");
		}
	}

	@Override
	public String getUser()
	{
		return user;
	}

	@Override
	public void setUser(final String user)
	{
		this.user = user;
	}

	@Override
	public String getPassword()
	{
		return password;
	}

	@Override
	public void setPassword(final String password)
	{
		this.password = password;
	}

	@Override
	public String getTargetUrl()
	{
		return targetUrl;
	}

	@Override
	public void setTargetUrl(final String targetUrl)
	{
		this.targetUrl = targetUrl;
	}

	@Override
	public String getSapCarClient()
	{
		return sapCarClient;
	}

	@Override
	public String getOaaProfile()
	{
		return oaaProfile;
	}

	@Override
	public void setOaaProfile(final String oaaProfile)
	{
		this.oaaProfile = oaaProfile;
	}

	@Override
	public void setSapCarClient(final String sapCarClient)
	{
		this.sapCarClient = sapCarClient;
	}

	public void setSapGlobalConfigurationService(final SAPGlobalConfigurationService sapGlobalConfigurationService)
	{
		this.sapGlobalConfigurationService = sapGlobalConfigurationService;
	}

	public void setSapCoreConfigurationService(final SAPConfigurationService sapCoreConfigurationService)
	{
		this.sapCoreConfigurationService = sapCoreConfigurationService;
	}

	@Override
	public int getConnectTimeout()
	{
		return connectTimeout;
	}

	@Override
	public int getReadTimeout()
	{
		return readTimeout;
	}

	@Override
	public void setItemCategory(final String itemCategory)
	{
		this.itemCategory = itemCategory;

	}

	@Override
	public String getItemCategory()
	{
		return itemCategory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.rest.RestServiceConfiguration#setSalesChannel(java.lang.String)
	 */
	@Override
	public void setSalesChannel(final String salesChannel)
	{
		this.salesChannel = salesChannel;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.rest.RestServiceConfiguration#getSalesChannel()
	 */
	@Override
	public String getSalesChannel()
	{
		return this.salesChannel;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.rest.RestServiceConfiguration#setMode(com.sap.retail.oaa.model.enums.
	 * Sapoaa_switch)
	 */
	@Override
	public void setMode(final Sapoaa_mode mode)
	{
		this.mode = mode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.rest.RestServiceConfiguration#getMode()
	 */
	@Override
	public Sapoaa_mode getMode()
	{
		return mode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.rest.RestServiceConfiguration#setConsumerId(java.lang.String)
	 */
	@Override
	public void setConsumerId(final String consumerId)
	{
		this.consumerId = consumerId;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.rest.RestServiceConfiguration#getConsumerId()
	 */
	@Override
	public String getConsumerId()
	{
		return consumerId;
	}

}

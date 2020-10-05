/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.strategies.impl;

import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentConfigurationStrategy;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;



/**
 * Default implemenation statergy for getting the SAP Digital payment Configuration
 */
public class DefaultSapDigitalPaymentConfigurationStrategy implements SapDigitalPaymentConfigurationStrategy
{

	private GenericDao<SAPDigitalPaymentConfigurationModel> sapDigitalPaymentConfigurationDao;

	@Override
	public SAPDigitalPaymentConfigurationModel getSapDigitalPaymentConfiguration()
	{

		final List<SAPDigitalPaymentConfigurationModel> sapDpConfigList = getSapDigitalPaymentConfigurationDao().find();
		if (CollectionUtils.isNotEmpty(sapDpConfigList))
		{
			return sapDpConfigList.get(0);
		}
		return null;

	}

	/**
	 * @return the sapDigitalPaymentConfigurationDao
	 */
	public GenericDao<SAPDigitalPaymentConfigurationModel> getSapDigitalPaymentConfigurationDao()
	{
		return sapDigitalPaymentConfigurationDao;
	}

	/**
	 * @param sapDigitalPaymentConfigurationDao
	 *           the sapDigitalPaymentConfigurationDao to set
	 */
	public void setSapDigitalPaymentConfigurationDao(
			final GenericDao<SAPDigitalPaymentConfigurationModel> sapDigitalPaymentConfigurationDao)
	{
		this.sapDigitalPaymentConfigurationDao = sapDigitalPaymentConfigurationDao;
	}



}

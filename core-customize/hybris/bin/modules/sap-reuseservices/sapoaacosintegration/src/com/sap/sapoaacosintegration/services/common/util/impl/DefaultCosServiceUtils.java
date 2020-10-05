/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.common.util.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.GeoWebServiceWrapper;
import de.hybris.platform.storelocator.data.AddressData;

import java.security.SecureRandom;

import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;


/**
 * Default COS service utility implementation
 */
public class DefaultCosServiceUtils implements CosServiceUtils
{
	private static final String DIGITS = "0000";
	final SecureRandom rnd = new SecureRandom();

	private GeoWebServiceWrapper geoWebServiceWrapper;
	private FlexibleSearchService flexibleSearchService;


	/**
	 * generates a random item number in follwing pattern:
	 * ^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$
	 *
	 * @return {@link String}
	 */
	@Override
	public String generateItemNumber()
	{
		final StringBuilder itemNumberString = new StringBuilder(generateRandomString());
		itemNumberString.append(generateRandomString());
		itemNumberString.append("-");
		itemNumberString.append(generateRandomString());
		itemNumberString.append("-");
		itemNumberString.append(generateRandomString());
		itemNumberString.append("-");
		itemNumberString.append(generateRandomString());
		itemNumberString.append("-");
		itemNumberString.append(generateRandomString());
		itemNumberString.append(generateRandomString());
		itemNumberString.append(generateRandomString());
		return itemNumberString.toString();
	}

	/**
	 * Provides the GPS coordinates for the given {@link AddressData}
	 *
	 * @param addressData
	 * @return {@link GPS}
	 */
	@Override
	public GPS fetchDestinationCoordinates(final AddressData addressData)
	{
		return getGeoWebServiceWrapper().geocodeAddress(addressData);

	}

	protected String generateRandomString()
	{
		final String zeros = DIGITS;
		final String s = Integer.toString(rnd.nextInt(0X10000), 16);
		return zeros.substring(s.length()) + s;

	}

	/**
	 * @return the geoWebServiceWrapper
	 */
	public GeoWebServiceWrapper getGeoWebServiceWrapper()
	{
		return geoWebServiceWrapper;
	}

	/**
	 * @param {@link
	 * 			 String}
	 * @return {@link ConsumedDestinationModel}
	 */
	@Override
	public ConsumedDestinationModel getConsumedDestinationModelById(final String destinationId)
	{
		ConsumedDestinationModel destination = null;
		try
		{
			final ConsumedDestinationModel consumedDestinationModel = new ConsumedDestinationModel();
			consumedDestinationModel.setId(destinationId);
			destination = getFlexibleSearchService().getModelByExample(consumedDestinationModel);

			if (destination == null)
			{
				throw new ModelNotFoundException("Provided destination was not found.");
			}
		}
		catch (final RuntimeException e)
		{
			throw new ModelNotFoundException("Failed to find ConsumedDestination.", e);

		}
		return destination;
	}

	/**
	 * @param geoWebServiceWrapper
	 *           the geoWebServiceWrapper to set
	 */
	public void setGeoWebServiceWrapper(final GeoWebServiceWrapper geoWebServiceWrapper)
	{
		this.geoWebServiceWrapper = geoWebServiceWrapper;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}



}

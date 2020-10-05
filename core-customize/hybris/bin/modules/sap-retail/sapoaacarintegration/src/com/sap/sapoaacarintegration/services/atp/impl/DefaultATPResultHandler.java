/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.atp.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.atp.ATPResultHandler;
import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPBatchResponse;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResponse;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResultItem;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResultItems;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilitiesResponse;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;


/**
 * Default atp result handler
 */
public class DefaultATPResultHandler implements ATPResultHandler
{
	private static final Logger LOG = Logger.getLogger(DefaultATPResultHandler.class);
	private ServiceUtils serviceUtils;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.atp.ATPResultHandler#extractATPAvailabilitesFromATPResponse(com.sap.retail
	 * .oaa.commerce.services.atp.jaxb.pojos.in.ATPResponse2)
	 */
	@Override
	public List<ATPAvailability> extractATPAvailabilityFromATPResponse(final ATPResponse atp)
	{
		validateResponse(atp);
		return getAvailabilities(atp.getAtpAvailabilities());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.atp.ATPResultHandler#extractATPProductAvailabilityFromATPBatchResponse(com
	 * .sap.retail.oaa.commerce.services.atp.jaxb.pojos.in.ATPBatchResponse)
	 */
	@Override
	public List<ATPProductAvailability> extractATPProductAvailabilityFromATPBatchResponse(final ATPBatchResponse atpBatch)
	{
		validateResponse(atpBatch);
		return getProductAvailabilities(atpBatch.getAtpResultItems());
	}


	/**
	 * Get all availability entries from the response item.
	 *
	 * @param item
	 * @return list of all availabilities
	 */
	protected List<ATPAvailability> getAvailabilities(final AvailabilitiesResponse item)
	{
		final List<ATPAvailability> availabilities = new ArrayList<>();

		if (item.getAtpAvailabilities().isEmpty())
		{ // in case the service return no availability lines
			final ATPAvailability noStockAvailable = new ATPAvailability();
			noStockAvailable.setQuantity(Double.valueOf("0"));
			noStockAvailable.setAtpDate(new Date());
			availabilities.add(noStockAvailable);
		}
		else
		{
			for (final AvailabilityItemResponse availabilityItem : item.getAtpAvailabilities())
			{
				availabilities.add(getAvailability(availabilityItem));
			}
		}
		return availabilities;
	}

	/**
	 * Get single availability entry from the response item.
	 *
	 * @param availability
	 * @return availability
	 */
	protected ATPAvailability getAvailability(final AvailabilityItemResponse availability)
	{
		final ATPAvailability availabilityModel = new ATPAvailability();
		availabilityModel.setQuantity(Double.valueOf(availability.getQuantity()));
		availabilityModel.setAtpDate(serviceUtils.parseStringToDate(availability.getAtpDate()));
		return availabilityModel;
	}

	/**
	 * Get all product availability entries from the response item.
	 *
	 * @param atpResultItems
	 * @return list of all product availabilities
	 */
	protected List<ATPProductAvailability> getProductAvailabilities(final ATPResultItems atpResultItems)
	{
		final List<ATPProductAvailability> productAvailabilityList = new ArrayList<>();
		for (final ATPResultItem atpResultItem : atpResultItems.getAtpResultItemList())
		{
			final ATPProductAvailability productAvailability = getProductAvailability(atpResultItem);
			productAvailabilityList.add(productAvailability);
		}
		return productAvailabilityList;
	}

	/**
	 * Get single product availability entry from the response item.
	 *
	 * @param atpResultItem
	 * @return productAvailability
	 */
	protected ATPProductAvailability getProductAvailability(final ATPResultItem atpResultItem)
	{
		final ATPProductAvailability productAvailability = new ATPProductAvailability();
		productAvailability.setArticleId(serviceUtils.removeLeadingZeros(atpResultItem.getArticleId()));
		productAvailability.setSourceId(atpResultItem.getSourceId());
		productAvailability.setAvailabilityList(getAvailabilities(atpResultItem.getAtpAvailabilities()));
		return productAvailability;
	}

	/**
	 * Checks if the ATPResponse is valid.
	 *
	 * @param atp
	 * @throws ATPException
	 */
	protected void validateResponse(final ATPResponse atp)
	{
		if (!hasData(atp))
		{
			throw new ATPException("ATP rest service has not returned sufficent data");
		}

		if (serviceUtils.logMessageResponseAndCheckMessageType(LOG, atp.getMessages()))
		{
			throw new ATPException("ATP rest service has returned an error message");
		}
	}

	/**
	 * Checks if the ATPBatchResponse is valid.
	 *
	 * @param atpBatch
	 * @throws ATPException
	 */
	protected void validateResponse(final ATPBatchResponse atpBatch)
	{
		if (!hasData(atpBatch))
		{
			throw new ATPException("ATP batch rest service has not returned sufficent data");
		}

		if (serviceUtils.logMessageResponseAndCheckMessageType(LOG, atpBatch.getMessages()))
		{
			throw new ATPException("ATP rest service has returned an error message");
		}
	}

	/**
	 * Checks if the ATPBatchResponse has data to process
	 *
	 * @param atpBatch
	 * @return true if the response has data
	 */
	protected boolean hasData(final ATPBatchResponse atpBatch)
	{
		boolean isDataAvailable = true;
		
		if (atpBatch == null || atpBatch.getAtpResultItems() == null)
		{
			isDataAvailable = false;
		}

		return isDataAvailable;

	}

	/**
	 * Checks if the ATPResponse has data to process
	 *
	 * @param atp
	 * @return true if the response has data
	 */
	protected boolean hasData(final ATPResponse atp)
	{
		boolean isDataAvailable = true;
		if (atp == null || atp.getAtpAvailabilities() == null)
		{
			isDataAvailable = false;
		}
		return isDataAvailable;
	}

	/**
	 * @param serviceUtils
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}

	/**
	 * @return the serviceUtils
	 */
	protected ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}
}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.impl;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.AvailabilityResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.CartItemsResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultCartItemResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultsResponse;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.services.sourcing.CosSourcingResponseMapper;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponse;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponseItem;
import com.sap.sapoaacosintegration.services.sourcing.response.CosSourcingResponseItemScheduleLine;


/**
 * Default response mapper for COS response
 */
public class DefaultCosSourcingResponseMapper implements CosSourcingResponseMapper
{

	private String dateFormat = "yyyy-MM-dd";
	private ConfigurationService configurationService;

	/**
	 * maps the {@link CosSourcingResponse} to {@link SourcingResponse}
	 */
	@Override
	public SourcingResponse mapCosSourcingResponseToSourcingResponse(final CosSourcingResponse cosSourcingResponse)
	{
		final SourcingResponse sourcingResponse = new SourcingResponse();
		sourcingResponse.setReservationSuccessful(SapoaacosintegrationConstants.ABAP_TRUE);

		mapSourcingResultsResponse(sourcingResponse, cosSourcingResponse);
		return sourcingResponse;
	}

	protected void mapSourcingResultsResponse(final SourcingResponse sourcingResponse,
			final CosSourcingResponse cosSourcingResponse)
	{
		final SourcingResultsResponse sourcingResultsResponse = new SourcingResultsResponse();
		mapSourcingResultResponse(cosSourcingResponse, sourcingResultsResponse);
		sourcingResponse.setSourcingResults(sourcingResultsResponse);
	}

	protected void mapSourcingResultResponse(final CosSourcingResponse cosSourcingResponse,
			final SourcingResultsResponse sourcingResultsResponse)
	{
		final SourcingResultResponse resultResponse = new SourcingResultResponse();
		mapCartItemsResponse(cosSourcingResponse, sourcingResultsResponse, resultResponse);
	}


	protected void mapCartItemsResponse(final CosSourcingResponse cosSourcingResponse,
			final SourcingResultsResponse sourcingResultsResponse, final SourcingResultResponse resultResponse)
	{
		final CartItemsResponse cartItemsResponse = new CartItemsResponse();
		cartItemsResponse.setSourcingResultCartItems(mapSourcingResultCartItemResponses(cosSourcingResponse));
		resultResponse.setCartItems(cartItemsResponse);

		final List<SourcingResultResponse> sourcingResultResponses = new ArrayList<>();
		sourcingResultResponses.add(resultResponse);

		sourcingResultsResponse.setSourcingResult(sourcingResultResponses);
	}

	protected List<SourcingResultCartItemResponse> mapSourcingResultCartItemResponses(
			final CosSourcingResponse cosSourcingResponse)
	{
		final List<SourcingResultCartItemResponse> sourcingResultCartItemResponses = new ArrayList<>();

		for (final CosSourcingResponseItem cosSourcingResponseItem : cosSourcingResponse.getSourcings().get(0))
		{
			for (final CosSourcingResponseItemScheduleLine cosSourcingResponseItemScheduleLine : cosSourcingResponseItem
					.getResponseItemScheduleLine())
			{
				final SourcingResultCartItemResponse sourcingResultCartItems = new SourcingResultCartItemResponse();

				sourcingResultCartItems.setSource(cosSourcingResponseItemScheduleLine.getSource().getSourceId());
				//vendor case
				if (cosSourcingResponseItemScheduleLine.getSource().getSourceType()
						.equals(SapoaacosintegrationConstants.SOURCETYPE_THIRD_PARTY))
				{
					sourcingResultCartItems.setPurchSite(cosSourcingResponseItemScheduleLine.getSource().getSourceId());
				}
				sourcingResultCartItems.setExternalId(cosSourcingResponseItem.getItemId());
				mapAvailabilityResponse(cosSourcingResponseItemScheduleLine, sourcingResultCartItems);

				sourcingResultCartItemResponses.add(sourcingResultCartItems);

			}
		}
		return sourcingResultCartItemResponses;
	}

	protected void mapAvailabilityResponse(final CosSourcingResponseItemScheduleLine cosSourcingResponseItemScheduleLine,
			final SourcingResultCartItemResponse sourcingResultCartItems)
	{
		final AvailabilityResponse availabilityResponse = new AvailabilityResponse();

		final AvailabilityItemResponse availabilityItemResponse = new AvailabilityItemResponse();
		final Configuration config = getConfigurationService().getConfiguration();

		final SimpleDateFormat formatter = new SimpleDateFormat(
				config.getString(SapoaacosintegrationConstants.SOURCING_RESPONSE_DATEFORMAT_PROPERTY_KEY, getDateFormat()));
		availabilityItemResponse.setAtpDate(formatter.format(cosSourcingResponseItemScheduleLine.getAvailableFrom()));
		availabilityItemResponse.setQuantity(cosSourcingResponseItemScheduleLine.getQuantity());

		final List<AvailabilityItemResponse> availabilityItemResponses = new ArrayList<>();
		availabilityItemResponses.add(availabilityItemResponse);
		availabilityResponse.setAvailabilityItems(availabilityItemResponses);

		sourcingResultCartItems.setAvailability(availabilityResponse);

	}

	/**
	 * @param dateFormat
	 *           the dateFormat to set
	 */
	public void setDateFormat(final String dateFormat)
	{
		this.dateFormat = dateFormat;
	}


	/**
	 * @return the dateFormat
	 */
	public String getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}



}

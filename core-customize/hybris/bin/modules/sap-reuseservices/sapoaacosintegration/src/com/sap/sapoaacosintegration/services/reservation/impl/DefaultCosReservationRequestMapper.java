/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sap.retail.oaa.model.model.ScheduleLineModel;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.services.atp.request.CosSource;
import com.sap.sapoaacosintegration.services.reservation.CosReservationRequestMapper;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequest;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequestItem;
import com.sap.sapoaacosintegration.services.reservation.request.CosReservationRequestItemScheduleLine;



/**
 * Default Request Mapper for COS reservation Service.
 */
public class DefaultCosReservationRequestMapper implements CosReservationRequestMapper
{

	@Override
	public List<CosReservationRequestItem> mapOrderModelToReservationRequest(final AbstractOrderModel abstractOrderModel,
			final CosReservationRequest request, final String cartItemId)
	{
		final Map<String, CosReservationRequestItem> reservationRequestItems = new HashMap<>();

		for (final AbstractOrderEntryModel entry : abstractOrderModel.getEntries())
		{

			if (!cartItemId.equalsIgnoreCase(entry.getEntryNumber().toString()))
			{
				reservationRequestItems.put(entry.getProduct().getCode(), getReservationRequestItem(entry, reservationRequestItems));

			}
		}


		return new ArrayList<>(reservationRequestItems.values());
	}

	/**
	 * @param reservationRequestItems
	 *
	 */
	private CosReservationRequestItem getReservationRequestItem(final AbstractOrderEntryModel entry, final Map<String, CosReservationRequestItem> reservationRequestItems)
	{
		final String productId=entry.getProduct().getCode();
		CosReservationRequestItem item = reservationRequestItems.get(productId);

		if(item !=null) {

			for (final ScheduleLineModel scheduleLine : entry.getScheduleLines())
			{
				item.getScheduleLine().add(getScheduleLine(entry, scheduleLine));
			}
		}
		else
		{
			item = new CosReservationRequestItem();
			item.setProductId(entry.getProduct().getCode());
			final List<CosReservationRequestItemScheduleLine> cosScheduleLine = new ArrayList<>();
			for (final ScheduleLineModel scheduleLine : entry.getScheduleLines())
			{
				cosScheduleLine.add(getScheduleLine(entry, scheduleLine));
			}
			item.setScheduleLine(cosScheduleLine);

		}
		return item;

	}

	/**
	 *
	 */
	private CosReservationRequestItemScheduleLine getScheduleLine(final AbstractOrderEntryModel entry,
			final ScheduleLineModel scheduleLine)
	{
		final CosReservationRequestItemScheduleLine cosScheduleLine = new CosReservationRequestItemScheduleLine();

		cosScheduleLine.setUnit(StringUtils.left(entry.getUnit().getCode(), SapoaacosintegrationConstants.UNIT_MAX_LENGTH));
		cosScheduleLine.setQuantity(scheduleLine.getConfirmedQuantity().intValue());
		if (entry.getSapSource() != null)
		{
			final CosSource source = new CosSource();
			cosScheduleLine.setSource(checkAndSetSource(source, entry));
		}
		return cosScheduleLine;


	}

	/**
	 * @param source
	 *
	 */
	private CosSource checkAndSetSource(final CosSource source, final AbstractOrderEntryModel entry)
	{
			source.setSourceId(entry.getSapSource().getName());
			if(entry.getSapVendor() !=null)
			{
				source.setSourceType(SapoaacosintegrationConstants.SOURCETYPE_THIRD_PARTY);
			}
			else if(entry.getSapSource().getType().getCode().equals(SapoaacosintegrationConstants.SOURCETYPE_STORE))
			{
				source.setSourceType(SapoaacosintegrationConstants.SOURCETYPE_STORE);
			}
		else
		{
			source.setSourceType(SapoaacosintegrationConstants.SOURCETYPE_DC);
		}



		return source;
	}

}

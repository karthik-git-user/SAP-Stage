/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationRequest;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.CartRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.Values;
import com.sap.retail.oaa.model.enums.Sapoaa_mode;
import com.sap.sapoaacarintegration.services.sourcing.SourcingRequestMapper;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;


/**
 * Default implementation for mapping into sourcing requests
 */
public class DefaultSourcingRequestMapper extends SourcingRequestMapperBase implements SourcingRequestMapper
{
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.sourcing.SourcingRequestMapper#mapCartModelToSourcingRequest(de.hybris.
	 * platform .core.model.order.CartModel, boolean, boolean)
	 */
	@Override
	public SourcingAbapRequest mapCartModelToSourcingRequest(final CartModel cartModel, final boolean execAllStrategies,
			final boolean reserve, final RestServiceConfiguration restConfiguration)
	{
		return mapModelToSourcingRequest(cartModel, execAllStrategies, reserve,
				SapoaacommerceservicesConstants.RESERVATION_STATUS_CART, restConfiguration);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.sourcing.SourcingRequestMapper#mapOrderModelToSourcingRequest(de.hybris.
	 * platform .core.model.order.OrderModel, boolean, boolean)
	 */
	@Override
	public SourcingAbapRequest mapOrderModelToSourcingRequest(final OrderModel orderModel, final boolean execAllStrategies,
			final boolean reserve, final RestServiceConfiguration restConfiguration)
	{
		return mapModelToSourcingRequest(orderModel, execAllStrategies, reserve,
				SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER, restConfiguration);
	}

	protected SourcingAbapRequest mapModelToSourcingRequest(final AbstractOrderModel abstractOrderModel,
			final boolean execAllStrategies, final boolean reserve, final String reservationStatus,
			final RestServiceConfiguration restConfiguration)
	{
		final SourcingAbapRequest abap = new SourcingAbapRequest();
		final Values values = new Values();
		final SourcingRequest sourcing = new SourcingRequest();
		final CartRequest cart = new CartRequest();
		final ReservationRequest reservation = new ReservationRequest();

		this.setSourcingMode(sourcing, restConfiguration);

		// Create/update temporary reservation?
		if (reserve)
		{
			sourcing.setReserve(ABAP_TRUE);
			this.setReservationMode(reservation, restConfiguration);

			if (reservationStatus.equals(SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER))
			{
				reservation.setOrderId(abstractOrderModel.getCode());
			}
			reservation.setStatus(reservationStatus);
			sourcing.setReservation(reservation);
		}

		// Execute all sourcing strategies?
		if (execAllStrategies)
		{
			sourcing.setExecAllStrategies(ABAP_TRUE);
		}

		setCart(abstractOrderModel, cart, reservationStatus);
		sourcing.setCart(cart);
		values.setSourcing(sourcing);
		abap.setValues(values);

		return abap;
	}

	private void setSourcingMode(final SourcingRequest sourcing, final RestServiceConfiguration restConfiguration)
	{
		if (restConfiguration.getMode() != null && restConfiguration.getMode().equals(Sapoaa_mode.SALESCHANNEL))
		{
			sourcing.setSalesChannel(restConfiguration.getSalesChannel());
		}
		else
		{
			sourcing.setOaaProfileId(restConfiguration.getOaaProfile());
		}
	}

	private void setReservationMode(final ReservationRequest reservation, final RestServiceConfiguration restConfiguration)
	{
		if (restConfiguration.getMode() != null && restConfiguration.getMode().equals(Sapoaa_mode.SALESCHANNEL))
		{
			reservation.setSalesChannel(restConfiguration.getSalesChannel());
		}
		else
		{
			reservation.setConsumerId(restConfiguration.getConsumerId());
		}
	}

}

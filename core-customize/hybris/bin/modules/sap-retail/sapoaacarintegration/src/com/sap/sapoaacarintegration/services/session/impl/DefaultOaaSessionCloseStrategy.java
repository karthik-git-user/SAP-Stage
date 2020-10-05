/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.session.impl;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.web.DefaultSessionCloseStrategy;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.sap.sapoaacarintegration.services.reservation.strategy.ReservationStrategy;



/**
 * Default OAA Session Close Strategy. Deletes Reservation, when session is closed.
 */
public class DefaultOaaSessionCloseStrategy extends DefaultSessionCloseStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultOaaSessionCloseStrategy.class);

	private CartService cartService;
	private ReservationStrategy reservationStrategy;


	@Override
	public void closeJaloSession(final JaloSession session)
	{
		LOG.debug("Delete JALO Session");

		if(this.getCartService().hasSessionCart()) {
			this.getReservationStrategy().deleteReservation(this.getCartService().getSessionCart());
			LOG.info("Delete JALO Session : session cart exists : reservation deleted");
		}

		super.closeJaloSession(session);
	}

	@Override
	public void closeSessionInHttpSession(final HttpSession session)
	{
		LOG.debug("Delete HTTP Session");
		
		if(this.getCartService().hasSessionCart()) {
			this.getReservationStrategy().deleteReservation(this.getCartService().getSessionCart());
			LOG.info("Delete HTTP Session : session cart exists : reservation deleted");
		}
		
		super.closeSessionInHttpSession(session);
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the reservationStrategy
	 */
	public ReservationStrategy getReservationStrategy()
	{
		return reservationStrategy;
	}

	/**
	 * @param reservationStrategy
	 *           the reservationStrategy to set
	 */
	public void setReservationStrategy(final ReservationStrategy reservationStrategy)
	{
		this.reservationStrategy = reservationStrategy;
	}
}

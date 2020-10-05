/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request;

import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.ObjectUtils;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationRequest;


/**
 * Jaxb Pojo for XML creation
 */
public class SourcingRequest
{
	private String oaaProfileId;
	private String salesChannel;
	private String reserve = "";
	private ReservationRequest reservation;
	private CartRequest cart;
	private String execAllStrategies = "";
	private String exitAfterFirstSuccBo = "";

	//V1 Properties
	private String status;
	private String consumerId;



	public SourcingRequest()
	{
		super();
	}

	public SourcingRequest(final String oaaProfileId, final CartRequest cart)
	{
		super();
		this.oaaProfileId = oaaProfileId;
		this.cart = cart;
	}

	public SourcingRequest(final String oaaProfileId, final CartRequest cart, final String reserve,
			final ReservationRequest reservation)
	{
		this(oaaProfileId, cart);
		this.reserve = reserve;
		this.reservation = reservation;
	}

	@XmlElement(name = "OAA_PROFILE_ID")
	public String getOaaProfileId()
	{
		return oaaProfileId;
	}

	/**
	 * @param oaaProfileId
	 *           the oaaProfileId to set
	 */
	public void setOaaProfileId(final String oaaProfileId)
	{
		this.oaaProfileId = oaaProfileId;
	}

	@XmlElement(name = "CHANNEL_ID")
	public String getSalesChannel()
	{
		return salesChannel;
	}

	public void setSalesChannel(final String salesChannel)
	{
		this.salesChannel = salesChannel;
	}

	@XmlElement(name = "RESERVE")
	public String getReserve()
	{
		return reserve;
	}

	/**
	 * @param reserve
	 *           reserve to set
	 */
	public void setReserve(final String reserve)
	{
		this.reserve = reserve;
	}

	/**
	 * @return the reservation
	 */
	@XmlElement(name = "RESERVATION")
	public ReservationRequest getReservation()
	{
		return reservation;
	}

	/**
	 * @param reservation
	 *           the reservation to set
	 */
	public void setReservation(final ReservationRequest reservation)
	{
		this.reservation = reservation;
	}

	@XmlElement(name = "CART")
	public CartRequest getCart()
	{
		return cart;
	}

	/**
	 * @param cart
	 *           the cart to set
	 */
	public void setCart(final CartRequest cart)
	{
		this.cart = cart;
	}

	@XmlElement(name = "EXEC_ALL_STRATEGIES")
	public String getExecAllStrategies()
	{
		return execAllStrategies;
	}

	/**
	 * @param execAllStrategies
	 *           the execAllStrategies to set
	 */
	public void setExecAllStrategies(final String execAllStrategies)
	{
		this.execAllStrategies = execAllStrategies;
	}

	@Override
	public String toString()
	{
		return "Sourcing [oaaProfileId=" + oaaProfileId + ", salesChannel= " + salesChannel + ", execAllStrategies='"
				+ execAllStrategies + "', cart=" + ObjectUtils.toString(cart) + ", reserve=" + reserve + ", Reservation="
				+ ObjectUtils.toString(reservation) + "]";
	}

	/**
	 * @return the status
	 */
	@XmlElement(name = "RESERVATION_STATUS")
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status
	 *           the status to set
	 */
	public void setStatus(final String status)
	{
		this.status = status;
	}

	/**
	 * @return the consumerId
	 */
	@XmlElement(name = "CONSUMER_ID")
	public String getConsumerId()
	{
		return consumerId;
	}

	/**
	 * @param consumerId
	 *           the consumerId to set
	 */
	public void setConsumerId(final String consumerId)
	{
		this.consumerId = consumerId;
	}

	@XmlElement(name = "EXIT_AFTER_FIRST_SUCC_BO")
	public String getExitAfterFirstSuccBo()
	{
		return exitAfterFirstSuccBo;
	}

	/**
	 * @param exitAfterFirstSuccBo
	 *           the exitAfterFirstSuccBo to set
	 */
	public void setExitAfterFirstSuccBo(final String exitAfterFirstSuccBo)
	{
		this.exitAfterFirstSuccBo = exitAfterFirstSuccBo;
	}

}

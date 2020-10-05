/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request;

import javax.xml.bind.annotation.XmlElement;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.request.CartItems;


/**
 * Jaxb Pojo for XML creation
 */
public class CartRequest
{
	private String externalId;
	private String orderId;
	private String shippingMethod;
	private CartItems items;
	private DeliveryAddressRequest deliveryAddress;
	private Double totalPrice;
	private Double deliveryCost;
	private String currencyIsoCode;

	public CartRequest()
	{
		super();
	}

	public CartRequest(final String externalId, final String shippingMethod, final CartItems items,
			final DeliveryAddressRequest deliveryAddress)
	{
		super();
		this.externalId = externalId;
		this.shippingMethod = shippingMethod;
		this.items = items;
		this.deliveryAddress = deliveryAddress;
	}

	@XmlElement(name = "EXTERNAL_ID")
	public String getExternalId()
	{
		return externalId;
	}

	/**
	 * @param externalId
	 *           the externalId to set
	 */
	public void setExternalId(final String externalId)
	{
		this.externalId = externalId;
	}

	@XmlElement(name = "ORDER_ID")
	public String getOrderId()
	{
		return orderId;
	}

	@XmlElement(name = "SHIPPING_METHOD")
	public String getShippingMethod()
	{
		return shippingMethod;
	}

	/**
	 * @param shippingMethod
	 *           the shippingMethod to set
	 */
	public void setShippingMethod(final String shippingMethod)
	{
		this.shippingMethod = shippingMethod;
	}

	@XmlElement(name = "CART_ITEMS")
	public CartItems getItems()
	{
		return items;
	}

	/**
	 * @param items
	 *           the items to set
	 */
	public void setItems(final CartItems items)
	{
		this.items = items;
	}

	@XmlElement(name = "DELIVERY_ADDRESS")
	public DeliveryAddressRequest getDeliveryAddress()
	{
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress
	 *           the deliveryAddress to set
	 */
	public void setDeliveryAddress(final DeliveryAddressRequest deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
	}

	@Override
	public String toString()
	{
		String resultString = "Cart [externalId=" + externalId + ", orderId=  " + orderId + " , shippingMethod=" + shippingMethod
				+ ", items=" + items.toString();

		if (deliveryAddress != null)
		{
			resultString += ", deliveryAddress=" + deliveryAddress.toString();
		}
		resultString += "]";

		return resultString;
	}

	/**
	 * @param orderId
	 *           the orderId to set
	 */
	public void setOrderId(final String orderId)
	{
		this.orderId = orderId;

	}

	@XmlElement(name = "DOCUMENT_GROSS_VALUE")
	public Double getTotalPrice()
	{
		return totalPrice;
	}

	/**
	 * @param totalPrice
	 *           the totalPrice to set
	 */
	public void setTotalPrice(final Double totalPrice)
	{
		this.totalPrice = totalPrice;
	}

	@XmlElement(name = "DOCUMENT_SHIP_VALUE")
	public Double getDeliveryCost()
	{
		return deliveryCost;
	}

	/**
	 * @param deliveryCost
	 *           the deliveryCost to set
	 */
	public void setDeliveryCost(final Double deliveryCost)
	{
		this.deliveryCost = deliveryCost;
	}

	@XmlElement(name = "DOCUMENT_CURRENCY_ISO_CODE")
	public String getCurrencyIsoCode()
	{
		return currencyIsoCode;
	}

	/**
	 * @param currencyIsoCode
	 *           the currencyIsoCode to set
	 */
	public void setCurrencyIsoCode(final String currencyIsoCode)
	{
		this.currencyIsoCode = currencyIsoCode;
	}

}

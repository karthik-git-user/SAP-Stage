/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class SourcingResultResponse
{
	private String sourcingStrategyId;
	private String businessObjectiveId;
	private CartItemsResponse cartItems;
	private AdditionalInformations additionalInformations;

	@XmlElement(name = "CART_ITEMS")
	public CartItemsResponse getCartItems()
	{
		return cartItems;
	}

	/**
	 * @param cartItems
	 *           the cartItems to set
	 */
	public void setCartItems(final CartItemsResponse cartItems)
	{
		this.cartItems = cartItems;
	}

	@XmlElement(name = "SOURCING_STRATEGY_ID")
	public String getSourcingStrategyId()
	{
		return sourcingStrategyId;
	}

	/**
	 * @param sourcingStrategyId
	 *           the sourcingStrategyId to set
	 */
	public void setSourcingStrategyId(final String sourcingStrategyId)
	{
		this.sourcingStrategyId = sourcingStrategyId;
	}


	@XmlElement(name = "BUSINESS_OBJECTIVE_ID")
	public String getBusinessObjectiveId()
	{
		return businessObjectiveId;
	}

	/**
	 * @param businessObjectId
	 *           the businessObjectId to set
	 */
	public void setBusinessObjectiveId(final String businessObjectId)
	{
		this.businessObjectiveId = businessObjectId;
	}

	@XmlElement(name = "ADDITIONAL_INFORMATION")
	public AdditionalInformations getAdditionalInformations()
	{
		return additionalInformations;
	}

	/**
	 * @param additionalInformations
	 *           the additionalInformations to set
	 */
	public void setAdditionalInformations(final AdditionalInformations additionalInformations)
	{
		this.additionalInformations = additionalInformations;
	}
}

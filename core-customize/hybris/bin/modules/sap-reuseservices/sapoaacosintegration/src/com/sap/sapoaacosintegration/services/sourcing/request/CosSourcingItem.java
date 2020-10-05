/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


/**
 *
 */
public class CosSourcingItem
{
	private String itemId;
	private String productId;

	private double quantity;
	private String unit;
	private String context;

	/**
	 * @return the itemId
	 */
	@JsonGetter("itemId")
	public String getItemId()
	{
		return itemId;
	}

	/**
	 * @param itemId
	 *           the itemId to set
	 */
	@JsonSetter("itemId")
	public void setItemId(final String itemId)
	{
		this.itemId = itemId;
	}

	/**
	 * @return the productId
	 */
	@JsonGetter("productId")
	public String getProductId()
	{
		return productId;
	}

	/**
	 * @param productId
	 *           the productId to set
	 */
	@JsonSetter("productId")
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

	/**
	 * @return the quantity
	 */
	@JsonGetter("quantity")
	public double getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	@JsonSetter("quantity")
	public void setQuantity(final double quantity)
	{
		this.quantity = quantity;
	}

	/**
	 * @return the unit
	 */
	@JsonGetter("unit")
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	@JsonSetter("unit")
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the context
	 */
	@JsonGetter("context")
	public String getContext()
	{
		return context;
	}

	/**
	 * @param context
	 *           the context to set
	 */
	@JsonSetter("context")
	public void setContext(final String context)
	{
		this.context = context;
	}


}

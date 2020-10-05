/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.request;


import com.fasterxml.jackson.annotation.JsonGetter;
/**
 *
 */
public class Item
{
	private String productId;
	private String unit;
	private String itemId;
	private String context;
	private Double quantity;




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
	public void setProductId(final String productId)
	{
		this.productId = productId;
	}

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
	public void setItemId(final String itemId)
	{
		this.itemId = itemId;
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
	public void setContext(final String context)
	{
		this.context = context;
	}

	/**
	 * @return the quantity
	 */
	@JsonGetter("quantity")
	public Double getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final Double quantity)
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

	@JsonGetter("unit")
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}



}

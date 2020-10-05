/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class CosSourcingResponseItem
{

	private String productId;
	private String itemId;
	private String context;
	private List<CosSourcingResponseItemScheduleLine> scheduleLine;

	/**
	 * @return the articleId
	 */
	@JsonGetter("productId")
	public String getProductId()
	{
		return productId;
	}

	/**
	 * @param articleId
	 *           the articleId to set
	 */
	@JsonSetter("productId")
	public void setProductId(final String articleId)
	{
		this.productId = articleId;
	}

	/**
	 * @return the responseItemScheduleLine
	 */
	@JsonGetter("scheduleLine")
	public List<CosSourcingResponseItemScheduleLine> getResponseItemScheduleLine()
	{
		return scheduleLine;
	}

	/**
	 * @param responseItemScheduleLine
	 *           the responseItemScheduleLine to set
	 */
	@JsonSetter("scheduleLine")
	public void setResponseItemScheduleLine(final List<CosSourcingResponseItemScheduleLine> responseItemScheduleLine)
	{
		this.scheduleLine = responseItemScheduleLine;
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
	@JsonSetter("itemId")
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
	@JsonSetter("context")
	public void setContext(final String context)
	{
		this.context = context;
	}



}

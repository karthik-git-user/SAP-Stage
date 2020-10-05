/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.jaxb.pojos.request;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML creation
 */
public class CartItem
{

	private String externalId;
	private String articleId;
	private String quantity;
	private String unitIso;
	private String unit;
	private Double itemTotalPrice;
	private String currencyIsoCode;
	private String sourcePreselected;
	private String source;

	public CartItem()
	{
		super();
	}

	public CartItem(final String externalId, final String articleId, final String quantity, final String unitIso)
	{
		super();
		this.externalId = externalId;
		this.articleId = articleId;
		this.quantity = quantity;
		this.unitIso = unitIso;
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

	@XmlElement(name = "ARTICLE_ID")
	public String getArticleId()
	{
		return articleId;
	}

	/**
	 * @param articleId
	 *           the articleId to set
	 */
	public void setArticleId(final String articleId)
	{
		this.articleId = articleId;
	}

	@XmlElement(name = "QUANTITY")
	public String getQuantity()
	{
		return quantity;
	}

	/**
	 * @param quantity
	 *           the quantity to set
	 */
	public void setQuantity(final String quantity)
	{
		this.quantity = quantity;
	}

	@XmlElement(name = "UNIT_ISO")
	public String getUnitIso()
	{
		return unitIso;
	}

	/**
	 * @param unitIso
	 *           the unitIso to set
	 */
	public void setUnitIso(final String unitIso)
	{
		this.unitIso = unitIso;
	}

	@XmlElement(name = "UNIT")
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit
	 *           the unit to set
	 */
	public void setUnit(final String unit)
	{
		this.unit = unit;
	}

	@XmlElement(name = "ITEM_GROSS_VALUE")
	public Double getItemTotalPrice()
	{
		return itemTotalPrice;
	}

	/**
	 * @param itemTotalPrice
	 *           the itemTotalPrice to set
	 */
	public void setItemTotalPrice(final Double itemTotalPrice)
	{
		this.itemTotalPrice = itemTotalPrice;
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


	/**
	 * @return the sourcePreselected
	 */
	@XmlElement(name = "SOURCE_IS_PRESELECTED")
	public String getSourcePreselected()
	{
		return sourcePreselected;
	}

	/**
	 * @param sourcePreselected
	 *           the sourcePreselected to set
	 */
	public void setSourcePreselected(final String sourcePreselected)
	{
		this.sourcePreselected = sourcePreselected;
	}

	/**
	 * @return the source
	 */
	@XmlElement(name = "SOURCE")
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source
	 *           the source to set
	 */
	public void setSource(final String source)
	{
		this.source = source;
	}

	@Override
	public String toString()
	{
		return "CartItem [externalId=" + externalId + ", articleId=" + articleId + ", quantity=" + quantity + ", unitIso="
				+ unitIso + ", unit=" + unit + ", source=" + source + ", sourcePreselected=" + sourcePreselected + "]";
	}

}

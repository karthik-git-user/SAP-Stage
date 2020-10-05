/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.jaxb.pojos.request;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML creation
 */
public class CartItems
{

	private List<CartItem> items;

	public CartItems()
	{
		super();
		this.items = new ArrayList<>();
	}

	/**
	 * @param items
	 */
	public CartItems(final List<CartItem> items)
	{
		super();
		this.items = items;
	}

	@XmlElement(name = "ITEM")
	public List<CartItem> getItems()
	{
		return items;
	}

	/**
	 * @param items
	 *           the items to set
	 */
	public void setItems(final List<CartItem> items)
	{
		this.items = items;
	}

	/**
	 * @param item
	 *           add item to list
	 */
	public void addItem(final CartItem item)
	{
		if (this.items == null)
		{
			this.items = new ArrayList<>();
		}
		this.items.add(item);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("CartItems ");
		strBuilder.append("[");
		if (items != null && !items.isEmpty())
		{
			for (final CartItem item : items)
			{
				strBuilder.append(item.toString());
			}
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}

}

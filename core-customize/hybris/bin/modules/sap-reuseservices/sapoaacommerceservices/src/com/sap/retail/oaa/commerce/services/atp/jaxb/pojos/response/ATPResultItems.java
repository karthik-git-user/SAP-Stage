/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;


/**
 * Jaxb Pojo for XML reading
 */
public class ATPResultItems
{
	private List<ATPResultItem> atpResultItemList;

	public ATPResultItems()
	{
		atpResultItemList = new ArrayList<>();
	}

	@XmlElement(name = "ATP_RESULT_ITEM")
	public List<ATPResultItem> getAtpResultItemList()
	{
		return atpResultItemList;
	}

	/**
	 * @param atpResultItems
	 *           the atpResultItems to set
	 */
	public void setAtpResultItemList(final List<ATPResultItem> atpResultItems)
	{
		this.atpResultItemList = atpResultItems;
	}
}

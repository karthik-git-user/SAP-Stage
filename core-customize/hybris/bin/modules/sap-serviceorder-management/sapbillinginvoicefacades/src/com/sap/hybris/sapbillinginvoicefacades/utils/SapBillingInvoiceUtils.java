/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoicefacades.utils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;


/**
 *
 */
public class SapBillingInvoiceUtils
{

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	public SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}

	public String getSapOrderType(final SAPOrderModel sapOrder)
	{

		return sapOrder.getSapOrderType() != null ? sapOrder.getSapOrderType().toString() : null;


	}

	public Date s4DateStringToDate(final String dateString)
	{
		final Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(dateString);
		while (m.find())
		{
			if (m.group(1) != null)
			{
				return new Date(Long.parseLong(m.group(1)));
			}

		}
		return null;
	}

}

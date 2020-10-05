/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsfacades.facade.impl;


import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


import com.sap.hybris.sapentitlementsfacades.facade.SapEntitlementFacade;

import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;
import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementService;


/**
 * Facade implementation to manage Entitlements in SAP EMS.
 */
public class DefaultSapEntitlementFacade implements SapEntitlementFacade
{
	private SapEntitlementService sapEntitlementService;
	private Converter<Entitlement, EntitlementData> sapEntitlementConverter;
	private Converter<Entitlement, EntitlementData> sapEntitlementDetailsConverter;


	@Override
	public List<EntitlementData> getEntitlementsForCurrentCustomer(final int pageNumber, final int pageSize)
	{
		final Entitlements entitlements = getSapEntitlementService().getEntitlementsForCurrentCustomer(pageNumber, pageSize);
		if (entitlements == null || entitlements.getData() == null || entitlements.getData().getResponse() == null)
		{
			return Collections.emptyList();
		}
		final List<Entitlement> entitlementList = entitlements.getData().getResponse();
		final List<EntitlementData> entitlementDataList = new ArrayList<EntitlementData>();
		for (int i = entitlementList.size() - 1; i >= 0; i--)
		{
			final Entitlement entitlement = entitlementList.get(i);
			final EntitlementData entitlementData = getSapEntitlementConverter().convert(entitlement);
			entitlementDataList.add(entitlementData);
		}
		return entitlementDataList;
	}

	@Override
	public EntitlementData getEntitlementForNumber(final String entitlementNumber)
	{
		final Entitlements entitlements = getSapEntitlementService().getEntitlementForNumber(entitlementNumber);
		EntitlementData entitlementData = new EntitlementData();
		if (entitlements != null && entitlements.getData() != null && entitlements.getData().getResponse() != null)
		{
			final Entitlement entitlement = entitlements.getData().getResponse().get(0);
			entitlementData = getSapEntitlementDetailsConverter().convert(entitlement);
		}

		return entitlementData;
	}

	/**
	 * @return the sapEntitlementService
	 */
	public SapEntitlementService getSapEntitlementService()
	{
		return sapEntitlementService;
	}


	/**
	 * @param sapEntitlementService
	 *           the sapEntitlementService to set
	 */
	@Required
	public void setSapEntitlementService(final SapEntitlementService sapEntitlementService)
	{
		this.sapEntitlementService = sapEntitlementService;
	}

	/**
	 * @return the sapEntitlementConverter
	 */
	public Converter<Entitlement, EntitlementData> getSapEntitlementConverter()
	{
		return sapEntitlementConverter;
	}

	/**
	 * @param sapEntitlementConverter
	 *           the sapEntitlementConverter to set
	 */
	@Required
	public void setSapEntitlementConverter(final Converter<Entitlement, EntitlementData> sapEntitlementConverter)
	{
		this.sapEntitlementConverter = sapEntitlementConverter;
	}

	/**
	 * @return the sapEntitlementDetailsConverter
	 */
	public Converter<Entitlement, EntitlementData> getSapEntitlementDetailsConverter()
	{
		return sapEntitlementDetailsConverter;
	}

	/**
	 * @param sapEntitlementDetailsConverter
	 *           the sapEntitlementDetailsConverter to set
	 */
	@Required
	public void setSapEntitlementDetailsConverter(final Converter<Entitlement, EntitlementData> sapEntitlementDetailsConverter)
	{
		this.sapEntitlementDetailsConverter = sapEntitlementDetailsConverter;
	}

}

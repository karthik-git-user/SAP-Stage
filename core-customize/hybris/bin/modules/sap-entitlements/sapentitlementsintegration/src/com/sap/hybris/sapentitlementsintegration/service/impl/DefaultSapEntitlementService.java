/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.service.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapentitlementsintegration.exception.SapEntitlementException;
import com.sap.hybris.sapentitlementsintegration.pojo.EntitlementFilter;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.pojo.GetEntitlementRequest;
import com.sap.hybris.sapentitlementsintegration.pojo.Paging;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementOutboundService;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementService;


/**
 * Default implementation of SAP Entitlement Service
 */
public class DefaultSapEntitlementService implements SapEntitlementService
{
	private UserService userService;
	private SapEntitlementOutboundService sapEntitlementOutboundService;
	private static final String ENTITLEMENT_API_DESTINATION = "sapEntitlementsApi";

	@Override
	public Entitlements getEntitlementsForCurrentCustomer(final int pageNumber, final int pageSize)
	{
		final GetEntitlementRequest request = new GetEntitlementRequest();
		final List<EntitlementFilter> filters = new ArrayList<EntitlementFilter>();
		addCustomerFilter(filters);
		request.setFilters(filters);
		final Paging paging = new Paging();
		paging.setPageNum(pageNumber);
		paging.setPageSize(pageSize);
		request.setPaging(paging);
		return getSapEntitlementOutboundService().sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}

	@Override
	public Entitlements getEntitlementForNumber(final String entitlementNumber)
	{
		final GetEntitlementRequest request = new GetEntitlementRequest();
		final List<EntitlementFilter> filters = new ArrayList<EntitlementFilter>();
		addCustomerFilter(filters);
		addNumberFilter(filters, entitlementNumber);
		request.setFilters(filters);
		final Paging paging = new Paging();
		paging.setPageNum(1);
		paging.setPageSize(1);
		request.setPaging(paging);
		return getSapEntitlementOutboundService().sendRequest(request, Entitlements.class, ENTITLEMENT_API_DESTINATION);
	}

	protected void addCustomerFilter(final List<EntitlementFilter> filters)
	{
		final EntitlementFilter customerFilter = new EntitlementFilter();
		customerFilter.setAttribute("CustomerID");
		customerFilter.setOperation("eq");
		final List<String> values = new ArrayList<String>();
		values.add(getCustomerId());
		customerFilter.setValues(values);
		filters.add(customerFilter);
	}

	protected void addNumberFilter(final List<EntitlementFilter> filters, final String entitlementNumber)
	{
		final EntitlementFilter guidFilter = new EntitlementFilter();
		guidFilter.setAttribute("EntitlementNo");
		guidFilter.setOperation("eq");
		final List<String> values = new ArrayList<String>();
		values.add(entitlementNumber);
		guidFilter.setValues(values);
		filters.add(guidFilter);
	}

	protected String getCustomerId()
	{
		final UserModel user = getUserService().getCurrentUser();
		String customerId = "";
		if (user instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) user;
			customerId = customer.getCustomerID();
			return customerId;
		}
		else
		{
			throw new SapEntitlementException("User +" + user.getUid() + " is not an instance of CustomerModel");
		}
	}


	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}


	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the sapEntitlementOutboundService
	 */
	public SapEntitlementOutboundService getSapEntitlementOutboundService()
	{
		return sapEntitlementOutboundService;
	}

	/**
	 * @param sapEntitlementOutboundService
	 *           the sapEntitlementOutboundService to set
	 */
	@Required
	public void setSapEntitlementOutboundService(final SapEntitlementOutboundService sapEntitlementOutboundService)
	{
		this.sapEntitlementOutboundService = sapEntitlementOutboundService;
	}


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2b.outbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;


/***
 * @deprecated since 2005
 * Handles B2B Customer Models using Site Store and Event services
 */
@Deprecated (since = "2005", forRemoval = true)
public class SAPB2BModelService extends DefaultModelService
{
	private transient CommonI18NService commonI18NService;
	private transient BaseSiteService baseSiteService;
	private transient BaseStoreService baseStoreService;
	private transient EventService b2bEventService;

	@Override
	public void save(final Object customerModel)
	{
		super.save(customerModel);
		//b2bEventService.publishEvent(initializeEvent(new B2BRegistrationEvent(), (B2BCustomerModel) customerModel));
	}

	/**
	 * initializes an {@link AbstractCommerceUserEvent}
	 *
	 * @param event
	 * @param b2bCustomerModel
	 * @return the event
	 */
	protected AbstractCommerceUserEvent<BaseSiteModel> initializeEvent(final AbstractCommerceUserEvent<BaseSiteModel> event,
			final B2BCustomerModel b2bCustomerModel)
	{
		event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
		event.setSite(getBaseSiteService().getCurrentBaseSite());
		event.setCustomer(b2bCustomerModel);
		event.setLanguage(getCommonI18NService().getCurrentLanguage());
		event.setCurrency(getCommonI18NService().getCurrentCurrency());
		return event;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	public void setB2bEventService(final EventService b2bEventService)
	{
		this.b2bEventService = b2bEventService;
	}
}

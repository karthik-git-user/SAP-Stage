/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.inbound.events;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundOrderHelper;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;


public class SapOaaCpiOmsOrderConfirmationPersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapOaaCpiOmsOrderConfirmationPersistenceHook.class);
	private DataHubInboundOrderHelper sapDataHubInboundOrderHelper;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof SAPOrderModel)
		{
			LOG.info("The persistence hook sapCpiOmsOrderConfirmationPersistenceHook is called!");
			final SAPOrderModel sapOrderModel = (SAPOrderModel) item;
			getSapDataHubInboundOrderHelper().processOrderConfirmationFromHub(sapOrderModel.getCode());
			return Optional.empty();
		}

		return Optional.of(item);
	}

	protected DataHubInboundOrderHelper getSapDataHubInboundOrderHelper()
	{
		return sapDataHubInboundOrderHelper;
	}

	public void setSapDataHubInboundOrderHelper(DataHubInboundOrderHelper sapDataHubInboundOrderHelper)
	{
		this.sapDataHubInboundOrderHelper = sapDataHubInboundOrderHelper;
	}

}

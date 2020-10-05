/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundOrderHelper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmmOrderConfirmationPersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmmOrderConfirmationPersistenceHook.class);
	private DataHubInboundOrderHelper sapDataHubInboundOrderHelper;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof OrderModel)
		{
			LOG.info("The persistence hook sapCpiOmmOrderConfirmationPersistenceHook is called!");
			final OrderModel orderModel = (OrderModel) item;
			getSapDataHubInboundOrderHelper().processOrderConfirmationFromHub(orderModel.getCode());
			return Optional.empty();
		}
		return Optional.of(item);
	}

	public DataHubInboundOrderHelper getSapDataHubInboundOrderHelper()
	{
		return sapDataHubInboundOrderHelper;
	}

	@Required
	public void setSapDataHubInboundOrderHelper(DataHubInboundOrderHelper sapDataHubInboundOrderHelper)
	{
		this.sapDataHubInboundOrderHelper = sapDataHubInboundOrderHelper;
	}

}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchangeoms.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundOrderHelper;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class SapCpiOmsOrderConfirmationPersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsOrderConfirmationPersistenceHook.class);
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

	@Required
	public void setSapDataHubInboundOrderHelper(DataHubInboundOrderHelper sapDataHubInboundOrderHelper)
	{
		this.sapDataHubInboundOrderHelper = sapDataHubInboundOrderHelper;
	}

}

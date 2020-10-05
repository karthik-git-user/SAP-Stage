/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchangeoms.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl.SapOmsDataHubInboundStockLevelHelper;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SapCpiOmsStockLevelPersistenceHook extends SapOmsDataHubInboundStockLevelHelper implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsStockLevelPersistenceHook.class);

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof StockLevelModel)
		{
			LOG.info("The persistence hook sapCpiOmsStockLevelPersistenceHook is called!");
			final StockLevelModel stockLevelModel = (StockLevelModel) item;
			this.updateStockLevel(stockLevelModel, Long.valueOf(stockLevelModel.getAvailable()));
			return Optional.empty();
		}

		return Optional.of(item);
	}

	@Override
	protected boolean isInitialStockLevel(StockLevelModel stockLevelModel)
	{
		return stockLevelModel.getPk() == null;
	}

}

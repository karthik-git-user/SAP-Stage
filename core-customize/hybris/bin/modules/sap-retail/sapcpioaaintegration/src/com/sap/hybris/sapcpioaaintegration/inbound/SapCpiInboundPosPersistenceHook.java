/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaintegration.inbound;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SapCpiInboundPosPersistenceHook implements PrePersistHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiInboundPosPersistenceHook.class);
	private ModelService modelService;


	@Override
	public Optional<ItemModel> execute(final ItemModel itemData)
	{
		LOG.info("Entering SapCpiInboundPosPersistenceHook#execute");

		checkArgument(itemData != null, "Item Data cannot be null.");
		if (itemData instanceof PointOfServiceModel)
		{
			final PointOfServiceModel posModel = (PointOfServiceModel) itemData;
			final AddressModel address = posModel.getAddress();

			if (address != null && getModelService().isNew(address))
			{
				address.setOwner(posModel);
				posModel.setAddress(address);
				LOG.info("Exiting SapCpiInboundPosPersistenceHook#execute");
			}
			return Optional.of(posModel);
		}
		LOG.info("Exiting SapCpiInboundPosPersistenceHook#execute");
		return Optional.of(itemData);
	}



	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}

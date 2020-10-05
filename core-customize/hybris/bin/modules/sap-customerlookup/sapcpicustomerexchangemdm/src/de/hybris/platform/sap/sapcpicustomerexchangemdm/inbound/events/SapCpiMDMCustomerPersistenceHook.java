/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangemdm.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.sapcpicustomerexchangemdm.inbound.SapMDMCustomerImportService;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PersistenceHook To Update The Customer Replication Information in CustomerModel
 */
public class SapCpiMDMCustomerPersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiMDMCustomerPersistenceHook.class);
	private SapMDMCustomerImportService sapMDMCustomerImportService;

	/**
	 * @param item
	 * @return Optional<ItemModel>
	 */
	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof CustomerModel)
		{
			LOG.debug("The persistence hook sapCpiMDMCustomerPersistenceHook is called!");
			final CustomerModel mdmCustomerModel = (CustomerModel) item;
			sapMDMCustomerImportService.processConsumerReplicationNotificationFromMDM(mdmCustomerModel);
			return Optional.empty();
		}

		return Optional.of(item);
	}

	public void setSapMDMCustomerImportService(final SapMDMCustomerImportService sapMDMCustomerImportService)
	{
		this.sapMDMCustomerImportService = sapMDMCustomerImportService;
	}



}

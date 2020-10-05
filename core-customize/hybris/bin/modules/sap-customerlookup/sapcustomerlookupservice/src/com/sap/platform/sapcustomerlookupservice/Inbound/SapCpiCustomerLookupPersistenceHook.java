/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.Inbound;

import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.platform.sapcustomerlookupservice.constants.SapcustomerlookupserviceConstants;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

public class SapCpiCustomerLookupPersistenceHook implements PrePersistHook {

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCustomerLookupPersistenceHook.class);
	private ModelService modelService;
	private ConfigurationService configurationService;

	@Override
	public Optional<ItemModel> execute(ItemModel item) {
		if (item instanceof CustomerModel) {
			LOG.debug("The persistence hook sapCpiMDMCustomerPersistenceHook is called!");
			String defaultAddressUsage = configurationService.getConfiguration()
					.getString(SapcustomerlookupserviceConstants.ADDRESS_DEFAULT);
			final CustomerModel customerLookupModel = (CustomerModel) item;
			Collection<AddressModel> addresses = customerLookupModel.getAddresses();
			for (final AddressModel address : addresses) {
				String addressUsage = address.getSapAddressUsage();

				if (addressUsage != null && defaultAddressUsage.equals(addressUsage)) {
					customerLookupModel.setDefaultShipmentAddress(address);
					address.setSapAddressUsage("");
					break;
				}

			}
			modelService.saveAll(customerLookupModel);
			return Optional.empty();
		}
		return Optional.of(item);
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.enums.InterfaceVersion;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;



/**
 * {@link PPSConfigService} accessing the configuration via the {@link BaseStoreService}
 */
public class DefaultPPSConfigService implements PPSConfigService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPPSConfigService.class);
	private BaseStoreService baseStoreService;

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	@Override
	public boolean isPpsActive(final ProductModel prod)
	{
		final SAPConfigurationModel sapConfiguration = getSapConfig(prod);
		return isActiveInConfig(sapConfiguration);
	}

	@Override
	public boolean isPpsActive(final AbstractOrderModel order)
	{
		final SAPConfigurationModel sapConfiguration = getSapConfig(order);
		return isActiveInConfig(sapConfiguration);
	}

	private boolean isActiveInConfig(final SAPConfigurationModel sapConfiguration)
	{
		return sapConfiguration != null && Boolean.TRUE.equals(sapConfiguration.getSappps_active());
	}

	private SAPConfigurationModel getSapConfig(final AbstractOrderModel order)
	{
		if(null != order && order.getStore() != null) {
			return order.getStore().getSAPConfiguration();
		}else {
			return null;
		}
	}

	@Override
	public String getBusinessUnitId(final ProductModel prod)
	{
		if(getSapConfig(prod) != null) {
			return getSapConfig(prod).getSappps_businessUnitId();
		}else {
			return null;
		}
	}

	@Override
	public String getBusinessUnitId(final AbstractOrderModel order)
	{
		SAPConfigurationModel configurationModel = getSapConfig(order);
		if(null != configurationModel) {
			return configurationModel.getSappps_businessUnitId();
		}else {
			return null;
		}
	}


	@Override
	public InterfaceVersion getClientInterfaceVersion(final ProductModel prod)
	{
		return getSapConfig(prod).getSappps_interfaceVersion();
	}

	@Override
	public InterfaceVersion getClientInterfaceVersion(final AbstractOrderModel order)
	{
		SAPConfigurationModel configurationModel = getSapConfig(order);
		if(null != configurationModel) {
			return configurationModel.getSappps_interfaceVersion();
		}else {
			return null;
		}

	}


	@Override
	public SAPConfigurationModel getSapConfig(final ProductModel prod)
	{
		// Case #1: We browse a web shop
		BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		if (baseStore == null)
		{
			// Case #2: SOLR index / Backoffice / ... --> Just take 1st base store via catalog
			Collection<BaseStoreModel> baseStores = prod.getCatalogVersion().getCatalog().getBaseStores();
			if (baseStores == null || !baseStores.iterator().hasNext())
			{
				LOG.warn("Could not determine any base store for catalog {} which contains product {} with PK {}. PPS cannot be used", prod
						.getCatalogVersion().getCatalog().getId(), prod.getCode(), prod.getPk().getLong());
				return null;
			}
			else
			{
				baseStore = baseStores.iterator().next();
			}
		}
		return baseStore.getSAPConfiguration();
	}

	@Override
	public String getSourceSystemId(AbstractOrderModel order) {
		SAPConfigurationModel configurationModel = getSapConfig(order);
		if(null != configurationModel) {
			return configurationModel.getSappps_sourceSystemId();
		} else {
			return null;
		}
	}

	@Override
	public String getSourceSystemId(final ProductModel prod) {
		return getSapConfig(prod).getSappps_sourceSystemId();
	}

}

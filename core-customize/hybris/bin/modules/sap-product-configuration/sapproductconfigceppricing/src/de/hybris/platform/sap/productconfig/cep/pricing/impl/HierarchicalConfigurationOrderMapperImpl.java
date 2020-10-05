/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.cep.pricing.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cep.pricing.model.SAPCpiOutboundOrderS4hcConfigHeaderModel;
import de.hybris.platform.sap.productconfig.cep.pricing.model.SAPCpiOutboundOrderS4hcConfigValuationModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSCommerceExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;


public class HierarchicalConfigurationOrderMapperImpl implements SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>
{
	private ObjectMapper objectMapper = null;

	@Override
	public void map(final OrderModel source, final SAPCpiOutboundOrderModel target)
	{

		source.getEntries().stream().forEach(entry -> mapEntry(entry, target));
	}

	protected void mapEntry(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderModel target)
	{
		if (entry.getExternalConfiguration() != null)
		{
			final SAPCpiOutboundOrderItemModel targetEntry = findTargetEntry(entry, target);
			targetEntry.setS4hcConfigHeader(createConfiguration(entry.getExternalConfiguration(), targetEntry));
		}
	}

	protected SAPCpiOutboundOrderS4hcConfigHeaderModel createConfiguration(final String externalConfiguration,
			final SAPCpiOutboundOrderItemModel targetEntry)
	{
		final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader = new SAPCpiOutboundOrderS4hcConfigHeaderModel();
		configurationHeader.setSapCpiS4hcConfigValuations(new HashSet<SAPCpiOutboundOrderS4hcConfigValuationModel>());
		configurationHeader.setEntryNumber(targetEntry.getEntryNumber());
		configurationHeader.setOrderId(targetEntry.getOrderId());
		final CPSCommerceExternalConfiguration configuration = parseConfiguration(externalConfiguration);
		configuration.getExternalConfiguration().getRootItem().getCharacteristics()
				.forEach(cstic -> addCharacteristicValues(cstic, configurationHeader));

		return configurationHeader;
	}

	protected void addCharacteristicValues(final CPSExternalCharacteristic cstic,
			final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader)
	{
		final String csticName = cstic.getId();
		final List<CPSExternalValue> values = cstic.getValues();
		if (values != null)
		{
			values.forEach(value -> addCharacteristicValue(value, csticName, configurationHeader));
		}
	}

	protected void addCharacteristicValue(final CPSExternalValue value, final String csticName,
			final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader)
	{
		final SAPCpiOutboundOrderS4hcConfigValuationModel valuation = new SAPCpiOutboundOrderS4hcConfigValuationModel();
		valuation.setAuthor(value.getAuthor());
		valuation.setCharacteristic(csticName);
		valuation.setEntryNumber(configurationHeader.getEntryNumber());
		valuation.setOrderId(configurationHeader.getOrderId());
		valuation.setValue(value.getValue());
		configurationHeader.getSapCpiS4hcConfigValuations().add(valuation);
	}

	protected CPSCommerceExternalConfiguration parseConfiguration(final String externalConfiguration)
	{
		try
		{
			final CPSCommerceExternalConfiguration configuration = getObjectMapper().readValue(externalConfiguration,
					CPSCommerceExternalConfiguration.class);
			return configuration;
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Parsing external configuration failed: expected JSON of CPSExternalConfiguration", e);
		}
	}

	protected SAPCpiOutboundOrderItemModel findTargetEntry(final AbstractOrderEntryModel entry,
			final SAPCpiOutboundOrderModel target)
	{
		return target.getSapCpiOutboundOrderItems().stream()
				.filter(e -> e.getEntryNumber().equals(entry.getEntryNumber().toString())).findFirst().orElse(null);
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

}

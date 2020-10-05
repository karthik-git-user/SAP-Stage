/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.outbound.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import com.sap.hybris.sapcpioaaorderintegration.constants.SapCpiOaaOrderEntryCsvColumns;
import com.sap.hybris.sapcpioaaorderintegration.constants.SapcpioaaorderintegrationConstants;
import com.sap.retail.oaa.model.model.ScheduleLineModel;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.site.BaseSiteService;



/**
 * Adds ROCC OAA specific Fields to Order Entry for Order Replication
 */
public class SapCpiOaaOrderEntryContributor implements RawItemContributor<OrderModel>
{
	private static final Logger LOG = Logger.getLogger(SapCpiOaaOrderEntryContributor.class);
	private final Set<String> columns = new HashSet<>(Arrays.asList(OrderCsvColumns.ORDER_ID, OrderEntryCsvColumns.ENTRY_NUMBER,
			SapCpiOaaOrderEntryCsvColumns.SITE_ID, SapCpiOaaOrderEntryCsvColumns.CAC_SHIPPING_POINT,
			SapCpiOaaOrderEntryCsvColumns.VENDOR_ITEM_CATEGORY, SapCpiOaaOrderEntryCsvColumns.SCHEDULE_LINES));

	private SAPConfigurationService sapCoreConfigurationService;
	private BaseSiteService baseSiteService;
	private String datePattern;


	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * The date pattern to use when converting Date objects to Strings in UTC timezone This value is read from a property
	 * 'datahubadapter.datahuboutbound.date.pattern'
	 *
	 * @param datePattern
	 *           the date pattern
	 */
	public void setDatePattern(final String datePattern)
	{
		this.datePattern = datePattern;
	}

	/**
	 * @param sapCoreConfigurationService
	 *           the sapCoreConfigurationService to set
	 */
	public void setSapCoreConfigurationService(final SAPConfigurationService sapCoreConfigurationService)
	{
		this.sapCoreConfigurationService = sapCoreConfigurationService;
	}


	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		final List<Map<String, Object>> result = new ArrayList<>();

		for (final AbstractOrderEntryModel entry : entries)
		{
			//When no OAA specific fields are filled -> put no entry to result
			if (entry.getScheduleLines() == null && entry.getSapSource() == null && entry.getDeliveryPointOfService() == null)
			{
				continue;
			}

			final Map<String, Object> row = new HashMap<>();
			addOrderEntryKeyFields(order, entry, row);

			//Set ScheduleLines
			if (entry.getScheduleLines() != null)
			{
				addScheduleLines(entry, row);
			}

			//Set Source
			if (entry.getSapSource() != null)
			{
				row.put(SapCpiOaaOrderEntryCsvColumns.SITE_ID, entry.getSapSource().getName());
			}

			//Set Shipping Point for Click and Collect
			if (entry.getDeliveryPointOfService() != null)
			{
				row.put(SapCpiOaaOrderEntryCsvColumns.CAC_SHIPPING_POINT,
						entry.getDeliveryPointOfService().getSapoaa_cacShippingPoint());
			}

			//Set item category for vendor delivered items
			if (entry.getSapVendor() != null)
			{
				baseSiteService.setCurrentBaseSite(order.getSite(), false);

				row.put(SapCpiOaaOrderEntryCsvColumns.VENDOR_ITEM_CATEGORY,
						sapCoreConfigurationService
								.getProperty(SapcpioaaorderintegrationConstants.PROPERTY_SAPOAA_VENDOR_ITEM_CATEGORY));
			}

			addProductFields(order, entry, row);


			result.add(row);
		}
		return result;
	}

	/**
	 * @param order
	 * @param entry
	 * @param row
	 */
	public void addProductFields(final OrderModel order, final AbstractOrderEntryModel entry, final Map<String, Object> row)
	{

		row.put(OrderEntryCsvColumns.PRODUCT_CODE, entry.getProduct().getCode());
		final UnitModel unit = entry.getUnit();
		if (unit != null)
		{
			row.put(OrderEntryCsvColumns.ENTRY_UNIT_CODE, unit.getCode());
		}


		String language = order.getLanguage().getIsocode();
		String shortText = determineItemShortText(entry, language);

		if (shortText.isEmpty())
		{
			final List<LanguageModel> fallbackLanguages = order.getLanguage().getFallbackLanguages();
			if (!fallbackLanguages.isEmpty())
			{
				language = fallbackLanguages.get(0).getIsocode();
				shortText = determineItemShortText(entry, language);
			}
		}
		row.put(OrderEntryCsvColumns.PRODUCT_NAME, shortText);

	}

	public String determineItemShortText(final AbstractOrderEntryModel item, final String language)
	{
		final String shortText = item.getProduct().getName(new java.util.Locale(language));
		return shortText == null ? "" : shortText;
	}

	/**
	 * @param order
	 * @param entry
	 * @param row
	 */
	public void addOrderEntryKeyFields(final OrderModel order, final AbstractOrderEntryModel entry, final Map<String, Object> row)
	{
		row.put(OrderCsvColumns.ORDER_ID, order.getCode());
		row.put(OrderEntryCsvColumns.ENTRY_NUMBER, entry.getEntryNumber());

	}

	@Override
	public Set<String> getColumns()
	{
		return columns;
	}

	/**
	 * @param entry
	 * @param row
	 */
	public void addScheduleLines(final AbstractOrderEntryModel entry, final Map<String, Object> row)
	{
		LOG.info(entry.getScheduleLines().size() + " : Schedule Lines for entry: " + entry.getEntryNumber());
		StringBuilder scheduleLines = new StringBuilder();
		for (final ScheduleLineModel scheduleLine : entry.getScheduleLines())
		{
			if (!scheduleLines.toString().isEmpty())
			{
				scheduleLines = scheduleLines.append("/");
			}

			scheduleLines = scheduleLines.append(scheduleLine.getConfirmedQuantity().toString() + ";"
					+ DateFormatUtils.formatUTC(scheduleLine.getConfirmedDate(), datePattern));
		}
		row.put(SapCpiOaaOrderEntryCsvColumns.SCHEDULE_LINES, scheduleLines.toString());
	}

}

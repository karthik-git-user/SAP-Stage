/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangemdm.inbound.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchangemdm.inbound.SapMDMCustomerImportService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.sql.Timestamp;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapcustomerb2c.inbound.CustomerReplicationEvent;


/**
 * This Service Class Process the In Bound Customer
 */
public class DefaultSapMDMCustomerImportService implements SapMDMCustomerImportService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapMDMCustomerImportService.class);
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	private EventService eventService;

	/**
	 * Process the In Bound Customer Creation Notification From Back End
	 *
	 * @param sapCpiOutboundCustomerModel
	 *           CustomerModel
	 */
	@Override
	public void processConsumerReplicationNotificationFromMDM(final CustomerModel sapCpiOutboundCustomerModel)
	{
		// read customer
		final CustomerModel customer = readCustomer(sapCpiOutboundCustomerModel.getCustomerID());

		// update customer
		if (customer != null)
		{
			final String sapConsumerId = sapCpiOutboundCustomerModel.getSapConsumerID();
			final String customerId = sapCpiOutboundCustomerModel.getCustomerID();
			final boolean isReplicated = sapCpiOutboundCustomerModel.getSapIsReplicated();
			final String statusReplicated = "Status change to 'IsReplicated = true' at: "
					+ (new Timestamp(new Date().getTime())).toString();

			final boolean sapIsReplicated = customer.getSapIsReplicated();
			customer.setSapReplicationInfo(isReplicated ? statusReplicated : "");
			customer.setSapIsReplicated(sapIsReplicated ? sapIsReplicated : isReplicated);
			if (sapConsumerId != null && !sapConsumerId.isEmpty())
			{
				customer.setSapConsumerID(sapConsumerId);
			}
			modelService.save(customer);

			// raise event
			final CustomerReplicationEvent repEvent = createCustomerReplicationEvent(customerId);
			eventService.publishEvent(repEvent);
		}

	}

	/**
	 * Reads the customer via flexible search
	 *
	 * @param customerId
	 * @return CustomerModel
	 */

	protected CustomerModel readCustomer(final String customerId)
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {c:pk} FROM {Customer AS c} WHERE  {c.customerID} like ?customerId");
		flexibleSearchQuery.addQueryParameter("customerId", "%" + customerId + "%");
		CustomerModel customer = null;

		try
		{
			customer = this.flexibleSearchService.searchUnique(flexibleSearchQuery);
			if (customer == null)
			{
				final String msg = "Error while processing customer query. Called with not existing customer for customer ID: "
						+ customerId;
				LOG.info(msg);
			}
		}
		catch (final ModelNotFoundException e)
		{
			final String msg = "Query for customer ID: " + customerId + " not found";
			LOG.info(msg);
		}
		catch (final AmbiguousIdentifierException e)
		{
			final String msg = "Query for customer ID: " + customerId + " results in multiple entries";
			LOG.info(msg);
		}
		return customer;
	}


	/**
	 * Return new created CustomerReplicationEvent
	 *
	 * @param customerId
	 * @return CustomerReplicationEvent
	 *
	 */
	protected CustomerReplicationEvent createCustomerReplicationEvent(final String customerId)
	{
		return new CustomerReplicationEvent(customerId);
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}


	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}


}

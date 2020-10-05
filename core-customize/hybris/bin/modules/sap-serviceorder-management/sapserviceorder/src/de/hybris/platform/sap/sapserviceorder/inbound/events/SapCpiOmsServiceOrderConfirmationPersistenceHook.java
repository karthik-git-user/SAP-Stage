/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.inbound.events;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.odata2services.odata.persistence.exception.ItemNotFoundException;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessEvent.Builder;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapcpiorderexchangeoms.inbound.events.SapCpiOmsOrderConfirmationPersistenceHook;
import de.hybris.platform.sap.sapmodel.enums.ConsignmentEntryStatus;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/**
 * Pre-persistence hook to handle Service Order Notification
 */
public class SapCpiOmsServiceOrderConfirmationPersistenceHook extends SapCpiOmsOrderConfirmationPersistenceHook
{
	private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsServiceOrderConfirmationPersistenceHook.class);
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private EventService eventService;

	@Override
	public Optional<ItemModel> execute(final ItemModel item)
	{
		if (item instanceof SAPOrderModel)
		{
			final SAPOrderModel incomingSapOrder = (SAPOrderModel) item;
			
			LOG.info("Service order notification received. SAPOrder code: {}. Service Order ID: {}.", incomingSapOrder.getCode(), incomingSapOrder.getServiceOrderId());
			
			final SAPOrderModel sapOrder = readSapOrder(incomingSapOrder.getCode());

			if(shouldSAPOrderProcessed(incomingSapOrder, sapOrder)) {
				copyIncomingSAPOrderattributes(sapOrder, incomingSapOrder);
				handleConsignmentEvents(sapOrder);
			}
			
		}

		return Optional.empty();
	}
	
	protected boolean shouldSAPOrderProcessed(SAPOrderModel incomingSapOrder, SAPOrderModel existingSapOrder) {
		
		boolean result = false;
		if(incomingSapOrder.getSapOrderStatus().equals(SAPOrderStatus.CONFIRMED_FROM_ERP)) {
			//CONFIRMED FROM ERP SHOULD BE ONLY PROCESSED WHEN EXISTING STATUS IS SENT TO ERP OR SENT TO S4CM
			if(existingSapOrder.getSapOrderStatus().equals(SAPOrderStatus.SENT_TO_S4CM)||existingSapOrder.getSapOrderStatus().equals(SAPOrderStatus.SENT_TO_ERP)) {
				result = true;
			}
		}else if(!incomingSapOrder.getSapOrderStatus().equals(existingSapOrder.getSapOrderStatus())) {
			result = true;
		}
		
		return result;
	}

	protected void copyIncomingSAPOrderattributes(SAPOrderModel sapOrder, SAPOrderModel incomingSapOrder) {
		sapOrder.setServiceOrderId(incomingSapOrder.getServiceOrderId());
		
		if(incomingSapOrder.getSapOrderStatus() != null)
		{
			sapOrder.setSapOrderStatus(incomingSapOrder.getSapOrderStatus());
		}
		getModelService().save(sapOrder);
	}

	protected void handleConsignmentEvents(final SAPOrderModel sapOrder)
	{
		
		if(sapOrder.getSapOrderStatus().equals(SAPOrderStatus.SERVICE_ORDER_IS_REJECTED))
		{
			sapOrder.setSapOrderStatus(SAPOrderStatus.CONFIRMED_FROM_ERP);
			modelService.save(sapOrder);
			try {
				getSapDataHubInboundOrderHelper().cancelOrder(null, sapOrder.getCode());
			} catch (ImpExException e) {
				LOG.info("SAP order [{}] confirmation has not been processed!", sapOrder.getCode());
			}
		}
		
		else if (sapOrder.getSapOrderStatus().equals(SAPOrderStatus.SERVICE_ORDER_IS_COMPLETED))
		{
			sapOrder.getConsignments().stream().findFirst().ifPresent(consignment ->
			{
				triggerConsignmentProcessEvent(consignment, "_ConsignmentActionEvent", "confirmShipConsignment");
				consignment.getConsignmentEntries().forEach(entry -> entry.setStatus(ConsignmentEntryStatus.SERVICED));
				modelService.saveAll(consignment.getConsignmentEntries());
			});
			
			getEventService().publishEvent(new SetCompletionEvent("SetOrderCompletionStatus", sapOrder));
		}

		else if (sapOrder.getSapOrderStatus().equals(SAPOrderStatus.CONFIRMED_FROM_ERP))
		{
			sapOrder.getConsignments().stream().findFirst().ifPresent(consignment ->
			{
				triggerConsignmentProcessEvent(consignment, "_ConsignmentSubmissionConfirmationEvent", null);
				consignment.getConsignmentEntries().forEach(entry -> entry.setStatus(ConsignmentEntryStatus.READY));
				modelService.saveAll(consignment.getConsignmentEntries());
			});
		}
	}
	
	private void triggerConsignmentProcessEvent(ConsignmentModel consignment, String businessEventSuffix, String businessEventChoice)
	{
		
		String consignmentProcessCode = consignment.getConsignmentProcesses().iterator().next().getCode();
		BusinessProcessEvent event = buildConsignmentEvent(consignmentProcessCode, businessEventSuffix, businessEventChoice);
		getBusinessProcessService().triggerEvent(event);
			
	}

	private BusinessProcessEvent buildConsignmentEvent(String consignmentProcessCode, String businessEventSuffix,
			String businessEventChoice) {
		Builder eventBuilder = BusinessProcessEvent.builder(new StringBuilder(consignmentProcessCode).append(businessEventSuffix).toString());
		if(businessEventChoice != null)
		{
			eventBuilder = eventBuilder.withChoice(businessEventChoice);
		}
		return eventBuilder.build();
	}

	protected SAPOrderModel readSapOrder(final String sapOrderNumber)
	{
		SAPOrderModel sapOrder;
		
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {o:pk} FROM {SAPOrder AS o} WHERE  {o.code} = ?code");
		flexibleSearchQuery.addQueryParameter("code", sapOrderNumber);
		try
		{
			sapOrder = getFlexibleSearchService().searchUnique(flexibleSearchQuery);
		}
		catch(ModelNotFoundException e)
		{
			throw new ItemNotFoundException("SAPOrder", sapOrderNumber);
		}

		return sapOrder;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
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

	/**
	 * @return the businessProcessService
	 */
	public BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}


}

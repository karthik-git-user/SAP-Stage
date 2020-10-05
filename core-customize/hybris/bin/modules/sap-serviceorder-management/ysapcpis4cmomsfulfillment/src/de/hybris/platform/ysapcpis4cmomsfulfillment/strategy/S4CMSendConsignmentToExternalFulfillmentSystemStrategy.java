/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ysapcpis4cmomsfulfillment.strategy;

import static com.google.common.base.Preconditions.checkArgument;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundConversionService;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.warehousing.externalfulfillment.strategy.SendConsignmentToExternalFulfillmentSystemStrategy;
import de.hybris.platform.ysapcpis4cmomsfulfillment.constants.Ysapcpis4cmomsfulfillmentConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import rx.Single;
import rx.SingleSubscriber;


/**
 *
 */
public class S4CMSendConsignmentToExternalFulfillmentSystemStrategy implements SendConsignmentToExternalFulfillmentSystemStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(S4CMSendConsignmentToExternalFulfillmentSystemStrategy.class);

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;
	private OrderHistoryService orderHistoryService;
	private ModelService modelService;
	private SapCpiServiceOrderOutboundService sapCpiServiceOrderOutboundService;
	private SapCpiServiceOrderOutboundConversionService sapCpiServiceOrderOutboundConversionService;
	private OrderService orderService;
	private TimeService timeService;

	@Override
	public void sendConsignment(final ConsignmentModel consignment)
	{
		LOG.info("Sending S4CM Service Order to External System...");
		SAPPlantLogSysOrgModel sapPlantLogSysOrg;
		final OrderHistoryEntryModel orderHistoryEntry;

		Optional.ofNullable(consignment.getConsignmentEntries()).map(Collection::stream).orElseGet(Stream::empty)
				.forEach(consignmentEntry -> {
					consignmentEntry.setSapOrderEntryRowNumber(consignmentEntry.getOrderEntry().getEntryNumber() + 1);
					getModelService().save(consignmentEntry);
				});


		final OrderModel order = (OrderModel) consignment.getOrder();
		consignment.setShippingDate(order.getRequestedServiceStartDate());
		getModelService().save(consignment);

		if (getSapPlantLogSysOrgService() != null)
		{
			sapPlantLogSysOrg = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(order.getStore(),
					consignment.getWarehouse().getCode());
			orderHistoryEntry = initializeOrderHistory(order, sapPlantLogSysOrg.getLogSys().getSapLogicalSystemName());
			final SAPOrderModel sapOrder = initializeSapOrder(orderHistoryEntry, consignment);


			sendOrderToScpi(consignment, orderHistoryEntry).subscribe(

					new SingleSubscriber<ResponseEntity<Map>>()
					{
						@Override
						public void onSuccess(final ResponseEntity<Map> mapResponseEntity)
						{
							// Save order history entry
							orderHistoryEntry.setTimestamp(getTimeService().getCurrentTime());
							getOrderHistoryService().saveHistorySnapshot(orderHistoryEntry.getPreviousOrderVersion());
							getModelService().save(orderHistoryEntry);

							// Save SAPOrder
							sapOrder.setSapOrderStatus(SAPOrderStatus.SENT_TO_ERP);
							sapOrder.setOrder(order);
							getModelService().save(sapOrder);

							LOG.info(
									"Successfully replicated the SAP service order [{}] associated with SAP Commerce order [{}] to the SAP back end. {}",
									sapOrder.getCode(), order.getCode(), SapCpiOutboundService.getPropertyValue(mapResponseEntity,
											Ysapcpis4cmomsfulfillmentConstants.RESPONSE_MESSAGE));							
						}

						@Override
						public void onError(final Throwable error)
						{
							
							logServiceOrderPlacementErrorReason(error);
							LOG.error(
									"Could not replicate the SAP service order [{}] associated with SAP Commerce order [{}] to the SAP back end.",
									sapOrder.getCode(), order.getCode(), error);
						}
					});
		}

	}

	protected void logServiceOrderPlacementErrorReason(final Throwable error) {
		
		if(error instanceof HttpServerErrorException.InternalServerError)
		{
			LOG.error("An internal server error occurred while processing the service order through SCPI.");
		}
		else if(error instanceof HttpServerErrorException.ServiceUnavailable || error instanceof HttpServerErrorException.GatewayTimeout)
		{
			LOG.error("An error occurred while connecting to SCPI.");
		}
		else if(error instanceof HttpClientErrorException.Unauthorized)
		{
			LOG.error("An authorization error occurred while connecting to SCPI.");
		}
		else if(error instanceof HttpClientErrorException.NotFound)
		{
			LOG.error("An error occurred while locating the service order integration flow in SCPI.");
		}
	}

	/**
	 * Send the consignment to the external system after wrapping it within an order
	 *
	 * @param consignment
	 * @return
	 */
	protected Single<ResponseEntity<Map>> sendOrderToScpi(final ConsignmentModel consignment,
			final OrderHistoryEntryModel orderHistoryEntry)
	{

		// Read customizing data from the base store configuration
		final SAPPlantLogSysOrgModel sapPlantLogSysOrg = getSapPlantLogSysOrgService()
				.getSapPlantLogSysOrgForPlant(consignment.getOrder().getStore(), consignment.getWarehouse().getCode());

		// Clone hybris parent order
		final OrderModel clonedOrder = getOrderService().clone(null, null, consignment.getOrder(),
				orderHistoryEntry.getPreviousOrderVersion().getVersionID());

		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();

		// Copy order entries
		consignment.getConsignmentEntries().stream().forEach(entry -> orderEntries.add(entry.getOrderEntry()));

		// Set cloned order attributes
		Set<ConsignmentModel> consignmentsSet = new HashSet<>();
		consignmentsSet.add(consignment);
		
		clonedOrder.setConsignments(consignmentsSet);
		clonedOrder.setSapLogicalSystem(sapPlantLogSysOrg.getLogSys().getSapLogicalSystemName());
		clonedOrder.setSapSalesOrganization(sapPlantLogSysOrg.getSalesOrg());
		clonedOrder.setEntries(orderEntries);
		clonedOrder.setSapSystemType(sapPlantLogSysOrg.getLogSys().getSapSystemType());
		clonedOrder.setPaymentTransactions(consignment.getOrder().getPaymentTransactions());

		return getSapCpiServiceOrderOutboundService().sendServiceOrder(
				getSapCpiServiceOrderOutboundConversionService().convertServiceOrderToSapCpiOrder(clonedOrder))
				.toSingle();
	}

	/**
	 * Initialize an entry in the order history for the SAP order before sending it to the external system
	 *
	 * @param order
	 * @param logicalSystem
	 * @return
	 */
	protected OrderHistoryEntryModel initializeOrderHistory(final OrderModel order, final String logicalSystem)
	{

		final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);

		historyEntry.setOrder(order);
		historyEntry.setPreviousOrderVersion(snapshot);

		historyEntry.setDescription(String.format("SAP service document [%s] has been sent to the logical system [%s]!",
				snapshot.getVersionID(), logicalSystem));

		return historyEntry;

	}

	/**
	 * Initialize an SAP order from the parent Hybris order
	 *
	 * @param orderHistoryEntry
	 * @param consignment
	 * @return SAPOrderModel
	 */
	protected SAPOrderModel initializeSapOrder(final OrderHistoryEntryModel orderHistoryEntry, final ConsignmentModel consignment)
	{
		final SAPOrderModel sapOrder = getModelService().create(SAPOrderModel.class);
		sapOrder.setCode(orderHistoryEntry.getPreviousOrderVersion().getVersionID());
		Set<ConsignmentModel> consignmentsSet = new HashSet<>();
		consignmentsSet.add(consignment);
		
		sapOrder.setConsignments(consignmentsSet);
		sapOrder.setSapOrderType(SAPOrderType.SERVICE);
		
		return sapOrder;
	}

	public static boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		return Ysapcpis4cmomsfulfillmentConstants.SUCCESS
				.equalsIgnoreCase(getPropertyValue(responseEntityMap, Ysapcpis4cmomsfulfillmentConstants.RESPONSE_STATUS))
				&& responseEntityMap.getStatusCode().is2xxSuccessful();
	}

	public static String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{

		final Object next = responseEntityMap.getBody().keySet().iterator().next();
		checkArgument(next != null, String.format("SCPI response entity key set cannot be null for property [%s]!", property));

		final String responseKey = next.toString();
		checkArgument(responseKey != null && !responseKey.isEmpty(),
				String.format("SCPI response property can neither be null nor empty for property [%s]!", property));

		final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
		checkArgument(propertyValue != null, String.format("SCPI response property [%s] value cannot be null!", property));

		return propertyValue.toString();

	}

	/**
	 * @return the sapPlantLogSysOrgService
	 */
	public SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	/**
	 * @param sapPlantLogSysOrgService
	 *           the sapPlantLogSysOrgService to set
	 */
	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}

	/**
	 * @return the orderHistoryService
	 */
	public OrderHistoryService getOrderHistoryService()
	{
		return orderHistoryService;
	}

	/**
	 * @param orderHistoryService
	 *           the orderHistoryService to set
	 */
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
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
	 * @return the sapCpiServiceOrderOutboundService
	 */
	public SapCpiServiceOrderOutboundService getSapCpiServiceOrderOutboundService()
	{
		return sapCpiServiceOrderOutboundService;
	}

	/**
	 * @param sapCpiServiceOrderOutboundService
	 *           the sapCpiServiceOrderOutboundService to set
	 */
	public void setSapCpiServiceOrderOutboundService(final SapCpiServiceOrderOutboundService sapCpiServiceOrderOutboundService)
	{
		this.sapCpiServiceOrderOutboundService = sapCpiServiceOrderOutboundService;
	}

	public SapCpiServiceOrderOutboundConversionService getSapCpiServiceOrderOutboundConversionService() {
		return sapCpiServiceOrderOutboundConversionService;
	}

	public void setSapCpiServiceOrderOutboundConversionService(
			SapCpiServiceOrderOutboundConversionService sapCpiServiceOrderOutboundConversionService) {
		this.sapCpiServiceOrderOutboundConversionService = sapCpiServiceOrderOutboundConversionService;
	}

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * @param orderService
	 *           the orderService to set
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}

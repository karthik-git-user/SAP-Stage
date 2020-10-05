/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysapcpircomsfulfillment.strategy;


import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudOrderOutboundService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.warehousing.externalfulfillment.strategy.SendConsignmentToExternalFulfillmentSystemStrategy;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import rx.Single;
import rx.SingleSubscriber;


/**
 * Strategy class to send order to subscription billing system
 */
public class SapSendRCConsignmentToExternalFulfillmentSystemStrategy implements SendConsignmentToExternalFulfillmentSystemStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(SapSendRCConsignmentToExternalFulfillmentSystemStrategy.class);

	private SapRevenueCloudOrderOutboundService sapRevenueCloudOrderOutboundService;
	private ModelService modelService;
	private OrderService orderService;
	private TimeService timeService;
	private BusinessProcessService businessProcessService;
	private OrderHistoryService orderHistoryService;

	public static final String CONSIGNMENT_SUBMISSION_CONFIRMATION_EVENT = "_ConsignmentSubmissionConfirmationEvent";
	public static final String RESPONSE_MESSAGE = "responseMessage";

	@Override
	public void sendConsignment(final ConsignmentModel consignment)
	{

		consignment.getConsignmentEntries().forEach(consignmentEntry -> {
			consignmentEntry.setSapOrderEntryRowNumber(consignmentEntry.getOrderEntry().getEntryNumber() + 1);
			getModelService().save(consignmentEntry);
		});

		final OrderModel order = (OrderModel) consignment.getOrder();

		// Initialize order history entry
		final OrderHistoryEntryModel orderHistoryEntry = initializeOrderHistory(order);

		// Initialize SAPOrder
		final SAPOrderModel sapOrder = initializeSapOrder(orderHistoryEntry, consignment);

		final ConsignmentProcessModel processModel = consignment.getConsignmentProcesses().stream().findFirst().get();

		sendRCOrderToScpi(consignment, orderHistoryEntry).subscribe(

				new SingleSubscriber<ResponseEntity<Map>>()
				{
					@Override
					public void onSuccess(final ResponseEntity<Map> mapResponseEntity)
					{
						if (isSentSuccessfully(mapResponseEntity))
						{
							// Save order history final entry
							orderHistoryEntry.setTimestamp(getTimeService().getCurrentTime());
							getOrderHistoryService().saveHistorySnapshot(orderHistoryEntry.getPreviousOrderVersion());
							getModelService().save(orderHistoryEntry);

							// Save SAPOrder
							sapOrder.setSapOrderStatus(SAPOrderStatus.SENT_TO_REVENUE_CLOUD);
							sapOrder.setOrder(order);
							sapOrder.setSubscriptionOrder(true);
							sapOrder.setSapOrderType(SAPOrderType.SUBSCRIPTION);
							
							
							getModelService().save(sapOrder);

							order.setRevenueCloudOrderId(getPropertyValue(mapResponseEntity, "revenueCloudOrderId"));
							getModelService().save(order);

							LOG.info(
									"SAP Order [{}] that is related to Hybris order [{}] has been sent successfully to the SAP backend through SCPI! {}"
							,sapOrder.getCode(), order.getCode(), getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));

							final String event = new StringBuilder().append(processModel.getCode())
									.append(CONSIGNMENT_SUBMISSION_CONFIRMATION_EVENT).toString();

							LOG.info("Consignment confirmation event [{}] has been triggered!", event);
							getBusinessProcessService().triggerEvent(event);
						}
						else
						{
							LOG.error("SAP Order [{}] that is related to Hybris order [{}] has not been sent to the SAP backend!",
									sapOrder.getCode(), order.getCode());
						}
					}

					@Override
					public void onError(final Throwable error)
					{
						LOG.error(
								"SAP Order [{}] that is related to Hybris order [{}] has not been sent to the SAP backend through SCPI!",
								sapOrder.getCode(), order.getCode(), error);
					}
				});
	}

	protected Single<ResponseEntity<Map>> sendRCOrderToScpi(final ConsignmentModel consignment,
			final OrderHistoryEntryModel orderHistoryEntry)
	{
		// Clone Commerce parent order
		final OrderModel clonedOrder = getOrderService().clone(null, null, consignment.getOrder(),
				orderHistoryEntry.getPreviousOrderVersion().getVersionID());

		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();

		// Copy order entries
		consignment.getConsignmentEntries().stream().forEach(entry -> orderEntries.add(entry.getOrderEntry()));

		// Set cloned order attributes
		clonedOrder.setConsignments(convertConsignmentToSet(consignment));
		clonedOrder.setEntries(orderEntries);
		clonedOrder.setPaymentTransactions(consignment.getOrder().getPaymentTransactions());

		// Send cloned order to SCPI
		return getSapRevenueCloudOrderOutboundService().sendOrder(clonedOrder).toSingle();
	}

	private Set<ConsignmentModel> convertConsignmentToSet(ConsignmentModel consignment){
		if(consignment == null) {
			return Collections.emptySet();
		}

		Set<ConsignmentModel> consignments = new LinkedHashSet<>();
		consignments.add(consignment);
		return consignments;
	}

	/**
	 * Initialize an entry in the order history for the SAP order before sending it to the external system
	 *
	 * @param order Order
	 * @return historyEntry
	 */
	protected OrderHistoryEntryModel initializeOrderHistory(final OrderModel order)
	{

		final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
		final OrderHistoryEntryModel historyEntry = getModelService().create(OrderHistoryEntryModel.class);

		historyEntry.setOrder(order);
		historyEntry.setPreviousOrderVersion(snapshot);

		historyEntry
				.setDescription(String.format("SAP Subscription Order [%s] has been sent to the revenue cloud system !",
						snapshot.getVersionID()));

		return historyEntry;
	}

	/**
	 * Initialize an SAP order from the parent Hybris order
	 *
	 * @param orderHistoryEntry  Order History
	 * @param consignment Consignment
	 * @return SAPOrderModel
	 */
	protected SAPOrderModel initializeSapOrder(final OrderHistoryEntryModel orderHistoryEntry, final ConsignmentModel consignment)
	{
		final SAPOrderModel sapOrder = getModelService().create(SAPOrderModel.class);
		sapOrder.setCode(orderHistoryEntry.getPreviousOrderVersion().getVersionID());
		sapOrder.setConsignments(convertConsignmentToSet(consignment));
		return sapOrder;
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
	 * @return the sapRevenueCloudOrderOutboundService
	 */
	public SapRevenueCloudOrderOutboundService getSapRevenueCloudOrderOutboundService()
	{
		return sapRevenueCloudOrderOutboundService;
	}

	/**
	 * @param sapRevenueCloudOrderOutboundService
	 *           the sapRevenueCloudOrderOutboundService to set
	 */
	public void setSapRevenueCloudOrderOutboundService(
			final SapRevenueCloudOrderOutboundService sapRevenueCloudOrderOutboundService)
	{
		this.sapRevenueCloudOrderOutboundService = sapRevenueCloudOrderOutboundService;
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


}

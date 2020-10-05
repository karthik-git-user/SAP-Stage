/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.sap.sapcpiomsreturnsexchange.actions;




import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapcpiomsreturnsexchange.service.SapCpiOmsOutboundReturnService;
import de.hybris.platform.sap.sapcpiomsreturnsexchange.service.SapCpiOmsReturnsOutboundConversionService;
import de.hybris.platform.sap.sapcpireturnsexchange.constants.SapcpireturnsexchangeConstants;
import de.hybris.platform.sap.sapmodel.model.SAPReturnRequestsModel;
import de.hybris.platform.task.RetryLaterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sap.hybris.sapomsreturnprocess.actions.CancelReturnRequestToSAPAction;


/**
 *
 */
public class CancelReturnRequestToSAPSCPIAction extends CancelReturnRequestToSAPAction
{
	private static final Logger LOGGER = Logger.getLogger(CancelReturnRequestToSAPSCPIAction.class);
	private SapCpiOmsOutboundReturnService sapCpiOmsOutboundReturnService;
	private SapCpiOmsReturnsOutboundConversionService sapCpiOmsReturnsOutboundConversionService;


	@Override
	public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException
	{
		final ReturnRequestModel returnRequest = process.getReturnRequest();
		final OrderModel order = returnRequest.getOrder();

		final List<Transition> results = new ArrayList<>();
		final Set<SAPReturnRequestsModel> sapReturnRequests = returnRequest.getSapReturnRequests();

		for (final SAPReturnRequestsModel sapReturnRequest : sapReturnRequests)
		{
			sendReturnOrder(order, results, sapReturnRequest.getConsignmentsEntry(), returnRequest, sapReturnRequest);
		}

		if (!results.isEmpty() && results.stream().allMatch(result -> result.equals(Transition.OK)))
		{

			resetEndMessage(process);
			LOGGER.info("Cancel Return order sent successfully");
			return Transition.OK;

		}
		else
		{
			LOGGER.info("Cancel Return order not sent.");
			return Transition.NOK;
		}

	}

	/**
	 * This method send the cloned return request to SCPI.
	 *
	 * @param order
	 *           - original order
	 * @param results
	 *           - This is a list of Transition. It contains the status of each result.
	 * @param consignments
	 *           -set of consingmentEntry
	 * @param returnRequest
	 *           - Original return request model.
	 * @param sapReturnRequestOrders
	 *           It contains the list of SAPReturnRequest
	 */
	private void sendReturnOrder(final OrderModel order, final List<Transition> results,
			final Set<ConsignmentEntryModel> consignments, final ReturnRequestModel returnRequest,
			final SAPReturnRequestsModel sapReturnRequest)
	{

		final ReturnRequestModel clonedReturnModel = modelService.clone(returnRequest);
		updateLogicalSystem(order, consignments, clonedReturnModel);
		final List<ReturnEntryModel> returnOrderEntryList = new ArrayList<>();
		final String cloneReturnedRequestCode = sapReturnRequest.getCode();
		clonedReturnModel.setCode(cloneReturnedRequestCode);
		consignments.stream().forEach(consignmentEntry -> {
			final RefundEntryModel refundEntryModel = modelService.create(RefundEntryModel.class);
			refundEntryModel.setOrderEntry(consignmentEntry.getOrderEntry());
			refundEntryModel.setReturnRequest(clonedReturnModel);
			refundEntryModel.setExpectedQuantity(consignmentEntry.getReturnQuantity());
			refundEntryModel.setReason(findRefundReason(returnRequest, consignmentEntry.getOrderEntry()));
			returnOrderEntryList.add(refundEntryModel);

			for (final ReturnEntryModel returnEntry : returnRequest.getReturnEntries())
			{
				for (final ConsignmentEntryModel orderConsignmentEntry : returnEntry.getOrderEntry().getConsignmentEntries())
				{
					if (orderConsignmentEntry.getPk().equals(consignmentEntry.getPk()))
					{
						orderConsignmentEntry.setQuantityReturnedUptil(
								consignmentEntry.getQuantityReturnedUptil() - consignmentEntry.getReturnQuantity());
						modelService.save(orderConsignmentEntry);
					}
				}
			}
		});

		sapReturnRequest.setConsignmentsEntry(consignments);

		clonedReturnModel.setReturnEntries(returnOrderEntryList);

		LOGGER.info("Sending Cancel update for return order to SCPI");
		getSapCpiOmsOutboundReturnService().sendReturnOrder(getSapCpiOmsReturnsOutboundConversionService()
				.convertCancelReturnOrderToSapCpiOutboundReturnOrder(clonedReturnModel, consignments)).subscribe(

						// onNext
						responseEntityMap -> {

							Registry.activateMasterTenant();

							if (SapCpiOmsOutboundReturnService.isSentSuccessfully(responseEntityMap))
							{

								results.add(Transition.OK);
								LOGGER.info(String.format(
										"The OMS return order [%s] has been successfully sent to the SAP backend through SCPI! %n%s",
										returnRequest.getCode(), SapCpiOmsOutboundReturnService.getPropertyValue(responseEntityMap,
												SapcpireturnsexchangeConstants.RESPONSE_MESSAGE)));

							}
							else
							{
								results.add(Transition.NOK);
								LOGGER.error(String.format("The OMS return order [%s] has not been sent to the SAP backend! %n%s",
										returnRequest.getCode(), SapCpiOmsOutboundReturnService.getPropertyValue(responseEntityMap,
												SapcpireturnsexchangeConstants.RESPONSE_MESSAGE)));

							}

						}, error -> {

							Registry.activateMasterTenant();
							results.add(Transition.NOK);
							LOGGER.error(String.format("The OMS return order [%s] has not been sent to SCPI! %n%s",
									returnRequest.getCode(), error.getMessage()));

						}

		);
	}


	/**
	 * @return the sapCpiOmsOutboundReturnService
	 */
	public SapCpiOmsOutboundReturnService getSapCpiOmsOutboundReturnService()
	{
		return sapCpiOmsOutboundReturnService;
	}

	/**
	 * @param sapCpiOmsOutboundReturnService
	 *           the sapCpiOmsOutboundReturnService to set
	 */
	public void setSapCpiOmsOutboundReturnService(final SapCpiOmsOutboundReturnService sapCpiOmsOutboundReturnService)
	{
		this.sapCpiOmsOutboundReturnService = sapCpiOmsOutboundReturnService;
	}


	/**
	 * @return the sapCpiOmsReturnsOutboundConversionService
	 */
	public SapCpiOmsReturnsOutboundConversionService getSapCpiOmsReturnsOutboundConversionService()
	{
		return sapCpiOmsReturnsOutboundConversionService;
	}

	/**
	 * @param sapCpiOmsReturnsOutboundConversionService
	 *           the sapCpiOmsReturnsOutboundConversionService to set
	 */
	public void setSapCpiOmsReturnsOutboundConversionService(
			final SapCpiOmsReturnsOutboundConversionService sapCpiOmsReturnsOutboundConversionService)
	{
		this.sapCpiOmsReturnsOutboundConversionService = sapCpiOmsReturnsOutboundConversionService;
	}

}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiomsreturnsexchange.actions;



import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.processengine.BusinessProcessService;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapomsreturnprocess.actions.SendOmsReturnOrderToDataHubAction;
import com.sap.hybris.sapomsreturnprocess.enums.SAPReturnRequestOrderStatus;
import com.sap.hybris.sapomsreturnprocess.returns.keygenerator.KeyGeneratorLookup;
import com.sap.hybris.sapomsreturnprocess.returns.strategy.ReturnSourcingContext;


/**
 *
 */
public class SapOrderexchangeDefaultOmsSendReturnOrderToSCPIAction extends SendOmsReturnOrderToDataHubAction
{
	private static final Logger LOG = Logger.getLogger(SapOrderexchangeDefaultOmsSendReturnOrderToSCPIAction.class);


	private SapCpiOmsReturnsOutboundConversionService sapCpiOmsReturnsOutboundConversionService;

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

	private SapCpiOmsOutboundReturnService sapCpiOmsOutboundReturnService;

	private BusinessProcessService businessProcessService;

	private KeyGeneratorLookup keyGeneratorLookup;
	private ReturnSourcingContext returnSourcingContext;

	@Override
	@Required
	public void setReturnSourcingContext(final ReturnSourcingContext returnSourcingContext)
	{
		this.returnSourcingContext = returnSourcingContext;
	}


	@Override
	@Required
	public void setKeyGeneratorLookup(final KeyGeneratorLookup keyGeneratorLookup)
	{
		this.keyGeneratorLookup = keyGeneratorLookup;
	}




	@Override
	public Transition executeAction(final ReturnProcessModel process) throws RetryLaterException
	{
		final ReturnRequestModel returnRequest = process.getReturnRequest();
		final OrderModel order = returnRequest.getOrder();
		final List<SAPReturnRequestsModel> sapReturnRequestOrders = new ArrayList<>();

		final Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap = new HashMap<>();

		populateSAPReturnEntryConsignmentListMap(returnRequest, returnEntryConsignmentListMap);
		returnSourcingContext.splitConsignment(returnEntryConsignmentListMap);
		final List<Transition> results = new ArrayList<>();
		final Set<Set<ConsignmentEntryModel>> consignmentSet = groupSAPReturnOrderConsignments(order, returnEntryConsignmentListMap);

		consignmentSet.stream().forEach(consignmentEntry -> sendOMSReturnOrder(order, results, consignmentEntry, returnRequest,
				sapReturnRequestOrders, process));

		if (!results.isEmpty() && results.stream().allMatch(result -> result.equals(Transition.OK)))
		{

			resetEndMessage(process);
			saveSAPReturnRequest(sapReturnRequestOrders, returnRequest);
			return Transition.OK;

		}
		else
		{
			return Transition.NOK;
		}

	}

	/**
	 * This method group order entry as per the logical system and map it to the consignment set.
	 *
	 * @param order-
	 *           original order
	 * @param orderEntryConsignmentMap
	 *           - this map contains the mapping b/w each order entry and set of consignment entry model associated with it.
	 * @return set of consignmentEntry set.
	 */
	private Set<Set<ConsignmentEntryModel>> groupSAPReturnOrderConsignments(final OrderModel order,
			final Map<ReturnEntryModel, List<ConsignmentEntryModel>> orderEntryConsignmentMap)
	{

		final Set<ConsignmentEntryModel> entryModelSet = new HashSet<>();
		orderEntryConsignmentMap.values().stream().forEach(consignmentEntryModelList -> consignmentEntryModelList.stream()
				.forEach(consignmentEntryModel -> entryModelSet.add(consignmentEntryModel)));

		final Map<String, Map<String, Set<ConsignmentEntryModel>>> mapByLogSysSalesOrg = entryModelSet.stream()
				.collect(Collectors.groupingBy(consignmentEntry -> getSapLogSysName(order, consignmentEntry),
						Collectors.groupingBy(consignmentEntry -> getSapSalesOrgName(order, consignmentEntry), Collectors.toSet())));

		final Set<Set<ConsignmentEntryModel>> consignmentSets = new HashSet<Set<ConsignmentEntryModel>>();

		mapByLogSysSalesOrg.entrySet().stream().forEach(entryKey -> entryKey.getValue().entrySet().stream()
				.forEach(entryValue -> consignmentSets.add(entryValue.getValue())));

		/*
		 * Sample data
		 *
		 * [ [ConsignmentModel (8031240100875), ConsignmentModel (8437240100353)], [ConsignmentModel(8876240068231)],
		 *
		 * [ConsignmentModel (8980240100409), ConsignmentModel (8434240100619)], [ConsignmentModel(8261240068262)], ],
		 */
		return consignmentSets;
	}

	/**
	 * This method makes the map of order entry and list of consignment entry model.
	 *
	 * @param returnRequest
	 * @param returnEntryConsignmentListMap
	 */
	private void populateSAPReturnEntryConsignmentListMap(final ReturnRequestModel returnRequest,
			final Map<ReturnEntryModel, List<ConsignmentEntryModel>> returnEntryConsignmentListMap)
	{
		final List<ReturnEntryModel> returnEntryModels = returnRequest.getReturnEntries();
		for (final ReturnEntryModel returnEntryModel : returnEntryModels)
		{
			final AbstractOrderEntryModel entryModel = returnEntryModel.getOrderEntry();

			final Set<ConsignmentEntryModel> consignmentSet = entryModel.getConsignmentEntries();
			final List<ConsignmentEntryModel> consignmentList = new ArrayList<>(consignmentSet);
			Collections.sort(consignmentList,
					(consignment1, consignment2) -> consignment2.getShippedQuantity().compareTo(consignment1.getShippedQuantity()));
			returnEntryConsignmentListMap.put(returnEntryModel, consignmentList);

		}
	}

	/**
	 * This method saves the SAPReturnRequest in db.
	 *
	 * @param sapReturnRequestOrders
	 *           - List of SAPReturnRequest
	 * @param returnRequest
	 *           - original return request
	 */
	private void saveSAPReturnRequest(final List<SAPReturnRequestsModel> sapReturnRequestOrders,
			final ReturnRequestModel returnRequest)
	{
		for (final SAPReturnRequestsModel sapReturnRequestOrderModel : sapReturnRequestOrders)
		{
			sapReturnRequestOrderModel.setSapReturnRequestOrderStatus(SAPReturnRequestOrderStatus.SENT_TO_BACKEND);
			sapReturnRequestOrderModel.setReturnRequest(returnRequest);
			sapReturnRequestOrderModel.getConsignmentsEntry().forEach(consignmentEntry -> getModelService().save(consignmentEntry));
			getModelService().save(sapReturnRequestOrderModel);

		}

	}

	/**
	 * This method is used to create SAP Return request and this model in db for reference.
	 *
	 * @param sapReturnRequestOrders-
	 *           List of return request order
	 * @param consignments-
	 *           set of consignment
	 * @param cloneReturnedRequestCode-cloned
	 *           return request code value
	 */
	private void createSAPReturnRequestOrder(final List<SAPReturnRequestsModel> sapReturnRequestOrders,
			final Set<ConsignmentEntryModel> consignments, final String cloneReturnedRequestCode)
	{
		final SAPReturnRequestsModel sapReturnRequestOrder = modelService.create(SAPReturnRequestsModel.class);
		sapReturnRequestOrder.setCode(cloneReturnedRequestCode);
		final Optional<ConsignmentEntryModel> consignmententry = consignments.stream().findFirst();
		if (consignmententry.isPresent())
		{
			sapReturnRequestOrder.setReturnWarehouse(consignmententry.get().getConsignment().getWarehouse());
		}

		sapReturnRequestOrder.setConsignmentsEntry(consignments);

		sapReturnRequestOrders.add(sapReturnRequestOrder);

	}


	/**
	 * This method send the cloned return request to data hub.
	 *
	 * @param order
	 *           - original order
	 * @param results
	 *           - This is a list of SendToDataHubResult. It contains the status of each result.
	 * @param consignments-set
	 *           of consingmentEntry
	 * @param returnRequest-
	 *           Original return request model.
	 * @param sapReturnRequestOrders
	 *           It contains the list of SAPReturnRequest
	 */
	private void sendOMSReturnOrder(final OrderModel order, final List<Transition> results,
			final Set<ConsignmentEntryModel> consignments, final ReturnRequestModel returnRequest,
			final List<SAPReturnRequestsModel> sapReturnRequestOrders, final ReturnProcessModel process)
	{

		final ReturnRequestModel clonedReturnModel = modelService.clone(returnRequest);

		updateLogicalSystem(order, consignments, clonedReturnModel);
		final List<ReturnEntryModel> returnOrderEntryList = new ArrayList<>();
		final String cloneReturnedRequestCode = keyGeneratorLookup.lookupGenerator(clonedReturnModel.getSapLogicalSystem());
		//setting cloned return request code.
		clonedReturnModel.setCode(cloneReturnedRequestCode);
		createSAPReturnRequestOrder(sapReturnRequestOrders, consignments, cloneReturnedRequestCode);
		consignments.stream().forEach(consignmentEntry -> {
			final RefundEntryModel refundEntryModel = modelService.create(RefundEntryModel.class);
			refundEntryModel.setOrderEntry(consignmentEntry.getOrderEntry());
			refundEntryModel.setReturnRequest(clonedReturnModel);
			refundEntryModel.setExpectedQuantity(consignmentEntry.getReturnQuantity());
			refundEntryModel.setAmount(consignmentEntry.getAmount());
			refundEntryModel.setReason(findRefundReason(returnRequest, consignmentEntry.getOrderEntry()));
			returnOrderEntryList.add(refundEntryModel);
		});
		clonedReturnModel.setReturnEntries(returnOrderEntryList);

		getSapCpiOmsOutboundReturnService().sendReturnOrder(getSapCpiOmsReturnsOutboundConversionService()
				.convertReturnOrderToSapCpiOutboundReturnOrder(clonedReturnModel, consignments)).subscribe(

						// onNext
						responseEntityMap -> {

							Registry.activateMasterTenant();

							if (SapCpiOmsOutboundReturnService.isSentSuccessfully(responseEntityMap))
							{

								resetEndMessage(process);
								results.add(Transition.OK);
								LOG.info(String.format(
										"The OMS return order [%s] has been successfully sent to the SAP backend through SCPI! %n%s",
										returnRequest.getCode(), SapCpiOmsOutboundReturnService.getPropertyValue(responseEntityMap,
												SapcpireturnsexchangeConstants.RESPONSE_MESSAGE)));

							}
							else
							{
								results.add(Transition.NOK);
								LOG.error(String.format("The OMS return order [%s] has not been sent to the SAP backend! %n%s",
										returnRequest.getCode(), SapCpiOmsOutboundReturnService.getPropertyValue(responseEntityMap,
												SapcpireturnsexchangeConstants.RESPONSE_MESSAGE)));

							}

						}, error -> {

							Registry.activateMasterTenant();
							results.add(Transition.NOK);
							LOG.error(String.format("The OMS return order [%s] has not been sent to SCPI! %n%s", returnRequest.getCode(),
									error.getMessage()));

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

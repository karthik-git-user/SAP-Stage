/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiomsreturnsexchange.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiomsreturnsexchange.model.ExtendedSAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiomsreturnsexchange.service.SapCpiOmsReturnsOutboundConversionService;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderItemModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;
import de.hybris.platform.sap.sapcpireturnsexchange.service.impl.SapCpiOmmReturnsOutboundConversionService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;


/**
 *
 */
public class SapCpiOmsReturnsOutboundConversionServiceImpl extends SapCpiOmmReturnsOutboundConversionService
		implements SapCpiOmsReturnsOutboundConversionService
{


	private static final String RETURN_REQUEST_TYPE = "ReturnRequest";
	private RawItemContributor<ReturnRequestModel> returnOrderContributor;
	private RawItemContributor<ReturnRequestModel> cancelReturnOrderEntryContributor;



	public SAPCpiOutboundReturnOrderModel convertReturnOrderToSapCpiOutboundReturnOrder(final ReturnRequestModel returnRequest,
			final Set<ConsignmentEntryModel> consignments)
	{

		final SAPCpiOutboundReturnOrderModel sapCpiOutboundReturnOrder = new SAPCpiOutboundReturnOrderModel();

		returnOrderContributor.createRows(returnRequest).stream().findFirst().ifPresent(row -> {

			sapCpiOutboundReturnOrder.setSapCpiConfig(mapReturnOrderConfigInfo(returnRequest));
			sapCpiOutboundReturnOrder.setPreceedingDocumentId(getSAPPreceedingDocumentId(consignments));

			sapCpiOutboundReturnOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOutboundReturnOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
			sapCpiOutboundReturnOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
			sapCpiOutboundReturnOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
			sapCpiOutboundReturnOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));
			sapCpiOutboundReturnOrder.setRMA(mapAttribute(ReturnOrderEntryCsvColumns.RMA, row));

			sapCpiOutboundReturnOrder.setSapCpiOutboundPriceComponents(mapReturnOrderPrices(returnRequest));
			sapCpiOutboundReturnOrder.setSapCpiOutboundOrderItems(mapReturnOrderItems(returnRequest));
			sapCpiOutboundReturnOrder.setSapCpiOutboundPartnerRoles(mapReturnOrderPartners(returnRequest));

		});

		final SAPConfigurationModel storeConfigurationModel = getSAPConfigModel(returnRequest.getOrder());
		if (storeConfigurationModel != null)
		{
			sapCpiOutboundReturnOrder.setDivision(storeConfigurationModel.getSapcommon_division());
			sapCpiOutboundReturnOrder.setDistributionChannel(storeConfigurationModel.getSapcommon_distributionChannel());
			sapCpiOutboundReturnOrder.setSalesOrganization(storeConfigurationModel.getSapcommon_salesOrganization());
			sapCpiOutboundReturnOrder.setTransactionType(storeConfigurationModel.getReturnOrderProcesstype());
		}
		return sapCpiOutboundReturnOrder;
	}


	public SAPCpiOutboundReturnOrderModel convertCancelReturnOrderToSapCpiOutboundReturnOrder(
			final ReturnRequestModel returnRequest, final Set<ConsignmentEntryModel> consignments)
	{

		final SAPCpiOutboundReturnOrderModel sapCpiOutboundReturnOrder = new SAPCpiOutboundReturnOrderModel();

		returnOrderContributor.createRows(returnRequest).stream().findFirst().ifPresent(row -> {

			sapCpiOutboundReturnOrder.setSapCpiConfig(mapReturnOrderConfigInfo(returnRequest));
			sapCpiOutboundReturnOrder.setPreceedingDocumentId(getSAPPreceedingDocumentId(consignments));

			sapCpiOutboundReturnOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOutboundReturnOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
			sapCpiOutboundReturnOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
			sapCpiOutboundReturnOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
			sapCpiOutboundReturnOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));

			sapCpiOutboundReturnOrder.setSapCpiOutboundPriceComponents(mapReturnOrderPrices(returnRequest));
			sapCpiOutboundReturnOrder.setSapCpiOutboundOrderItems(mapCancelReturnOrderItems(returnRequest));
			sapCpiOutboundReturnOrder.setSapCpiOutboundPartnerRoles(mapReturnOrderPartners(returnRequest));
			sapCpiOutboundReturnOrder.setCancellationCode(returnRequest.getReasonCodeCancellation());

		});

		final SAPConfigurationModel storeConfigurationModel = getSAPConfigModel(returnRequest.getOrder());
		if (storeConfigurationModel != null)
		{
			sapCpiOutboundReturnOrder.setDivision(storeConfigurationModel.getSapcommon_division());
			sapCpiOutboundReturnOrder.setDistributionChannel(storeConfigurationModel.getSapcommon_distributionChannel());
			sapCpiOutboundReturnOrder.setSalesOrganization(storeConfigurationModel.getSapcommon_salesOrganization());
			sapCpiOutboundReturnOrder.setTransactionType(storeConfigurationModel.getReturnOrderProcesstype());
		}
		return sapCpiOutboundReturnOrder;
	}

	/**
	 *
	 */
	protected Set<SAPCpiOutboundOrderItemModel> mapCancelReturnOrderItems(final ReturnRequestModel returnRequest)
	{
		final List<SAPCpiOutboundOrderItemModel> sapCpiOrderItems = new ArrayList<>();

		cancelReturnOrderEntryContributor.createRows(returnRequest).forEach(row -> {

			final SAPCpiOutboundReturnOrderItemModel sapCpiOrderItem = new SAPCpiOutboundReturnOrderItemModel();
			final String returnReasonForEntry = mapAttribute(ReturnOrderEntryCsvColumns.RETURN_REASON_FOR_ENTRY, row);
			final String returnReason = !StringUtils.isBlank(returnReasonForEntry) ? returnReasonForEntry
					: mapAttribute(OrderEntryCsvColumns.REJECTION_REASON, row);
			sapCpiOrderItem.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
			sapCpiOrderItem.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));
			sapCpiOrderItem.setQuantity(mapAttribute(OrderEntryCsvColumns.QUANTITY, row));
			sapCpiOrderItem.setProductCode(mapAttribute(OrderEntryCsvColumns.PRODUCT_CODE, row));
			sapCpiOrderItem.setUnit(mapAttribute(OrderEntryCsvColumns.ENTRY_UNIT_CODE, row));
			sapCpiOrderItem.setWarehouse(mapAttribute(ReturnOrderEntryCsvColumns.WAREHOUSE, row));
			sapCpiOrderItem.setRejectionReason(returnReason);
			sapCpiOrderItem.setProductName(mapAttribute(OrderEntryCsvColumns.PRODUCT_NAME, row));
			sapCpiOrderItem.setCancellationCode(mapAttribute(ReturnOrderEntryCsvColumns.REASON_CODE_FOR_RETURN_CANCELLATION, row));
			sapCpiOrderItems.add(sapCpiOrderItem);

		});

		return new HashSet<>(sapCpiOrderItems);
	}


	/**
	 *
	 */
	private String getSAPPreceedingDocumentId(final Set<ConsignmentEntryModel> consignments)
	{

		final ConsignmentEntryModel consignment = consignments.stream().findFirst().orElse(null);
		if (consignment != null)
		{
			final OrderModel order = (OrderModel) (consignment.getConsignment().getOrder());
			final SAPOrderModel sapOrderModel = order.getSapOrders().stream()
					.filter(sapOrder -> verifyConsignmentWithSAPOrder(sapOrder.getConsignments(), consignment.getConsignment()))
					.findFirst().orElse(null);
			if (sapOrderModel != null)
			{
				return sapOrderModel.getCode();
			}
		}
		return null;
	}

	/**
	 *
	 */
	private boolean verifyConsignmentWithSAPOrder(final Set<ConsignmentModel> consignments, final ConsignmentModel consignment)
	{
		return consignments.stream().anyMatch(con -> con.equals(consignment));
	}

	private SAPConfigurationModel getSAPConfigModel(final OrderModel order)
	{
		if (order != null && order.getStore() != null)
		{
			return order.getStore().getSAPConfiguration();
		}
		return null;
	}

	@Override
	protected SAPCpiOutboundConfigModel mapReturnOrderConfigInfo(final ReturnRequestModel returnRequest)
	{
		final String returnOrderLogicalSystem = returnRequest.getSapLogicalSystem();

		Assert.hasText(returnOrderLogicalSystem, "The field [SapLogicalSystemName] cannot be empty");

		final SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();

		final Optional<SAPLogicalSystemModel> sapLogicalSystemOptional = getSapCoreSAPGlobalConfigurationDAO()
				.getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig().stream()
				.filter(logSys -> returnOrderLogicalSystem.equalsIgnoreCase(logSys.getSapLogicalSystemName())).findFirst();

		if (sapLogicalSystemOptional.isPresent())
		{

			final SAPLogicalSystemModel sapLogicalSystem = sapLogicalSystemOptional.get();
			final int NUMBER_2 = 2;
			final int NUMBER_1 = 1;
			final int NUMBER_0 = 0;
			final int NUMBER_3 = 3;

			sapCpiOutboundConfig.setSenderName(sapLogicalSystem.getSenderName());
			sapCpiOutboundConfig.setSenderPort(sapLogicalSystem.getSenderPort());

			sapCpiOutboundConfig.setReceiverName(sapLogicalSystem.getSapLogicalSystemName());
			sapCpiOutboundConfig.setReceiverPort(sapLogicalSystem.getSapLogicalSystemName());

			final Optional<ExtendedSAPHTTPDestinationModel> httpDestincation = resolveHTTPDestination(sapLogicalSystem);

			if (httpDestincation.isPresent())
			{
				final ExtendedSAPHTTPDestinationModel returnOrderHTTPDestincation = httpDestincation.get();

				final String targetUrl = returnOrderHTTPDestincation.getTargetURL();

				sapCpiOutboundConfig.setUrl(targetUrl);
				sapCpiOutboundConfig.setUsername(returnOrderHTTPDestincation.getUserid());
				final String[] sapClientSplit = targetUrl.split("sap-client=");

				if (sapClientSplit.length == NUMBER_2 && StringUtils.isNotBlank(sapClientSplit[NUMBER_1]))
				{
					sapCpiOutboundConfig.setClient(sapClientSplit[NUMBER_1].substring(NUMBER_0, NUMBER_3));
				}
				else
				{
					sapCpiOutboundConfig.setClient(StringUtils.EMPTY);
				}

			}

		}

		return sapCpiOutboundConfig;

	}

	/**
	 *
	 */
	private Optional<ExtendedSAPHTTPDestinationModel> resolveHTTPDestination(final SAPLogicalSystemModel sapLogicalSystem)
	{
		final SAPHTTPDestinationModel httpDestination = sapLogicalSystem.getSapHTTPDestination();
		if (httpDestination != null)
		{
			return httpDestination.getObjSpecificHTTPDestination().stream()
					.filter(logSys -> RETURN_REQUEST_TYPE.equals(logSys.getObjectType().getCode())).findFirst();

		}
		return Optional.empty();
	}

	@Override
	protected RawItemContributor<ReturnRequestModel> getReturnOrderContributor()
	{
		return returnOrderContributor;
	}

	@Override
	@Required
	public void setReturnOrderContributor(final RawItemContributor<ReturnRequestModel> returnOrderContributor)
	{
		this.returnOrderContributor = returnOrderContributor;
	}

	/**
	 * @return the cancelReturnOrderEntryContributor
	 */
	public RawItemContributor<ReturnRequestModel> getCancelReturnOrderEntryContributor()
	{
		return cancelReturnOrderEntryContributor;
	}


	/**
	 * @param cancelReturnOrderEntryContributor
	 *           the cancelReturnOrderEntryContributor to set
	 */
	@Required
	public void setCancelReturnOrderEntryContributor(
			final RawItemContributor<ReturnRequestModel> cancelReturnOrderEntryContributor)
	{
		this.cancelReturnOrderEntryContributor = cancelReturnOrderEntryContributor;
	}


}

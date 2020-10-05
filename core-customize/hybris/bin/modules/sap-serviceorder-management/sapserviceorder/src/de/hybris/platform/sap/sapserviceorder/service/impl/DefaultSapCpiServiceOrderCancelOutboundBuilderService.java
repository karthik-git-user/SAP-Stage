/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderDestinationService;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundBuilderService;
import de.hybris.platform.store.BaseStoreModel;

public class DefaultSapCpiServiceOrderCancelOutboundBuilderService implements SapCpiServiceOrderOutboundBuilderService<OrderCancelRecordEntryModel,List<SAPCpiOutboundServiceOrderModel>>{

	private static final String CREATION_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private SapCpiOrderDestinationService sapCpiOrderDestinationService;
	
	@Override
	public List<SAPCpiOutboundServiceOrderModel> build(OrderCancelRecordEntryModel source) {

		OrderModel order = source.getModificationRecord().getOrder();
		final SAPConfigurationModel sapConfig = order.getStore().getSAPConfiguration(); 
		String cancelCreationDate = new SimpleDateFormat(CREATION_DATE_FORMAT).format(new Date());
		
		List<SAPCpiOutboundServiceOrderModel> cancelServiceOrders = new ArrayList<>();
		order.getSapOrders().stream()
			.filter(sapOrder -> SAPOrderType.SERVICE.equals(sapOrder.getSapOrderType()))
			.forEach(serviceOrder -> {
				
				SAPCpiOutboundServiceOrderModel cancelServiceOrder = new SAPCpiOutboundServiceOrderModel();
				
				cancelServiceOrder.setCommerceOrderId(order.getCode());
				cancelServiceOrder.setOrderId(serviceOrder.getCode());
				cancelServiceOrder.setServiceOrderId(serviceOrder.getServiceOrderId());
				cancelServiceOrder.setTransactionType(sapConfig.getServiceOrderTransactionType());
				cancelServiceOrder.setCreationDate(cancelCreationDate);
				
				mapCpiConfig(serviceOrder, cancelServiceOrder);
				
				cancelServiceOrders.add(cancelServiceOrder);
				
			});
				
		return cancelServiceOrders;
	}

	protected void mapCpiConfig(SAPOrderModel serviceOrder, SAPCpiOutboundServiceOrderModel cancelServiceOrder) {
		ConsignmentModel serviceConsignment = serviceOrder.getConsignments().stream().findFirst().get();
		BaseStoreModel store = serviceConsignment.getOrder().getStore();
		String plantCode = serviceConsignment.getWarehouse().getCode();
		cancelServiceOrder.setSapCpiConfig(buildCpiConfig(store, plantCode));
	}
	
	protected SAPCpiOutboundConfigModel buildCpiConfig(BaseStoreModel store, String plantCode)
	{
		SAPCpiOutboundConfigModel cpiConfig = new SAPCpiOutboundConfigModel();
		SAPLogicalSystemModel sapLogicalSystem = getSapCpiOrderDestinationService().readSapLogicalSystem(store, plantCode);
		
		cpiConfig.setUrl(sapLogicalSystem.getSapHTTPDestination().getTargetURL());
		cpiConfig.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());
		
		return cpiConfig;
	}

	public SapCpiOrderDestinationService getSapCpiOrderDestinationService() {
		return sapCpiOrderDestinationService;
	}

	public void setSapCpiOrderDestinationService(SapCpiOrderDestinationService sapCpiOrderDestinationService) {
		this.sapCpiOrderDestinationService = sapCpiOrderDestinationService;
	}

}

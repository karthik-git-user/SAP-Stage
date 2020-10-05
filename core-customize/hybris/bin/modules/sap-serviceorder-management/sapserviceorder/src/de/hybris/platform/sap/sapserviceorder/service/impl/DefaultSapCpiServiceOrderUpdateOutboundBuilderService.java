/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderDestinationService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderItemModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundBuilderService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

public class DefaultSapCpiServiceOrderUpdateOutboundBuilderService
		implements SapCpiServiceOrderOutboundBuilderService<ConsignmentModel,SAPCpiOutboundServiceOrderModel> {
	
	private ModelService modelService;
	private SapCpiOrderDestinationService sapCpiOrderDestinationService;
	private static final String REQUEST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	
	@Override
	public SAPCpiOutboundServiceOrderModel build(ConsignmentModel consignment)
	{
		final SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel = getModelService().create(SAPCpiOutboundServiceOrderModel.class);
		
		mapHeader(consignment, sapCpiOutboundServiceOrderModel);
		mapServiceOrderItems(consignment, sapCpiOutboundServiceOrderModel);
		mapCpiConfig(consignment, sapCpiOutboundServiceOrderModel);
		
		return sapCpiOutboundServiceOrderModel;
	}

	protected void mapHeader(ConsignmentModel consignment,
			SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		
		sapCpiOutboundServiceOrderModel.setCommerceOrderId(consignment.getOrder().getCode());
		sapCpiOutboundServiceOrderModel.setOrderId(consignment.getSapOrder().getCode());
		sapCpiOutboundServiceOrderModel.setServiceOrderId(consignment.getSapOrder().getServiceOrderId());
		
		sapCpiOutboundServiceOrderModel.setSapOrderStatus(consignment.getSapOrder().getSapOrderStatus());
	}

	protected void mapServiceOrderItems(ConsignmentModel consignment,
			SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		
		sapCpiOutboundServiceOrderModel.setSapCpiOutboundServiceOrderItems(new HashSet<SAPCpiOutboundServiceOrderItemModel>());
		
		DateFormat dateFormat = new SimpleDateFormat(REQUEST_DATE_FORMAT);
		String requestedDate = dateFormat.format(consignment.getOrder().getRequestedServiceStartDate());
		
		consignment.getConsignmentEntries().forEach(entry -> {
			final SAPCpiOutboundServiceOrderItemModel outboundEntry = getModelService().create(SAPCpiOutboundServiceOrderItemModel.class);
			
			outboundEntry.setEntryNumber(String.valueOf(entry.getSapOrderEntryRowNumber()));
			outboundEntry.setRequestedServiceStartDateTime(requestedDate);
			
			sapCpiOutboundServiceOrderModel.getSapCpiOutboundServiceOrderItems().add(outboundEntry);
		});
	}

	protected void mapCpiConfig(ConsignmentModel consignment, SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel)
	{
		String plantCode = consignment.getWarehouse().getCode();
		BaseStoreModel store = consignment.getOrder().getStore();
		
		sapCpiOutboundServiceOrderModel.setSapCpiConfig(buildCpiConfig(store, plantCode));
	}
	
	protected SAPCpiOutboundConfigModel buildCpiConfig(BaseStoreModel store, String plantCode)
	{
		SAPCpiOutboundConfigModel cpiConfig = new SAPCpiOutboundConfigModel();
		SAPLogicalSystemModel sapLogicalSystem = getSapCpiOrderDestinationService().readSapLogicalSystem(store, plantCode);
		
		cpiConfig.setUrl(sapLogicalSystem.getSapHTTPDestination().getTargetURL());
		cpiConfig.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());
		
		return cpiConfig;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public SapCpiOrderDestinationService getSapCpiOrderDestinationService() {
		return sapCpiOrderDestinationService;
	}

	public void setSapCpiOrderDestinationService(SapCpiOrderDestinationService sapCpiOrderDestinationService) {
		this.sapCpiOrderDestinationService = sapCpiOrderDestinationService;
	}
}

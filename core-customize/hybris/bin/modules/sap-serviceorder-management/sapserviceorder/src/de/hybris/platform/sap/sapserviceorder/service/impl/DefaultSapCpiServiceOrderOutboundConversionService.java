/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderOutboundConversionService;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundConversionService;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Implementation for populating attributes corresponding to Service Order
 */
public class DefaultSapCpiServiceOrderOutboundConversionService extends SapCpiOmmOrderOutboundConversionService implements SapCpiServiceOrderOutboundConversionService
{
	private static final Logger LOG = Logger.getLogger(DefaultSapCpiServiceOrderOutboundConversionService.class);
	private List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundServiceOrderModel>> sapCpiServiceOrderMappers;
	private ModelService modelService;

	/**
	 * Method for converting orderModel to integration object
	 * @param orderModel commerce order object to be converted to integration object
	 * @return order integration object
	 */
	@Override
	public SAPCpiOutboundServiceOrderModel convertServiceOrderToSapCpiOrder(final OrderModel orderModel)
	{
		LOG.info("DefaultSapCpiServiceOrderOutboundConversionService is called");
		
		final SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel = modelService.create(SAPCpiOutboundServiceOrderModel.class);
		super.convertOrderToSapCpiOrder(orderModel, sapCpiOutboundServiceOrderModel);
		
	    getSapCpiServiceOrderMappers().forEach(mapper -> mapper.map(orderModel, sapCpiOutboundServiceOrderModel));


		return sapCpiOutboundServiceOrderModel;
	}

	
	public List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundServiceOrderModel>> getSapCpiServiceOrderMappers() {
		return sapCpiServiceOrderMappers;
	}

	public void setSapCpiServiceOrderMappers(
			List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundServiceOrderModel>> sapCpiServiceOrderMappers) {
		this.sapCpiServiceOrderMappers = sapCpiServiceOrderMappers;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing;

import com.sap.retail.oaa.commerce.services.rest.OaaRestService;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;

import de.hybris.platform.core.model.order.AbstractOrderModel;




/**
 * Service for Sourcing
 */
public interface SourcingService extends OaaRestService
{

	/**
	 * Call the REST Service in Customer Activity Repository (CAR) and persist the result (Schedule Lines) in the given
	 * cart.
	 *
	 * @param model
	 *           Hybris cart or order model
	 * @throws {@link SourcingException},{@link BackendDownException}
	 */
	void callRestServiceAndPersistResult(final AbstractOrderModel model);

	/**
	 * Call the REST service in Customer Activity Repository (CAR) and return the service call response without
	 * persisting in hybris to the method invoker.
	 *
	 * @param orderModel
	 *           Hybris order or cart
	 * @param execAllStrategies
	 *           Flag which indicates if a all sourcing strategies should be executed.
	 * @param reserve
	 *           Create or update a temporary reservation in case the sourcing was executed successfully
	 * @return {@link SourcingResponse} 
	 * 			 The response from the sourcing call
	 * @throws {@link SourcingException} ,{@link BackendDownException}
	 */
	SourcingResponse callRestService(final AbstractOrderModel orderModel, final boolean execAllStrategies, final boolean reserve);



}

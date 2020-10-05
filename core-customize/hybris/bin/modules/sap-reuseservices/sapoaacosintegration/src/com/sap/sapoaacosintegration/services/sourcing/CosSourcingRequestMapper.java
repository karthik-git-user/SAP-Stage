/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import com.sap.sapoaacosintegration.services.sourcing.request.CosSourcingRequest;


/**
 * Request mapper for COS sourcing request
 */
public interface CosSourcingRequestMapper
{


	/**
	 * @param orderModel
	 * @return {@link CosSourcingRequest}
	 */
	CosSourcingRequest prepareCosSourcingRequest(AbstractOrderModel orderModel);

}

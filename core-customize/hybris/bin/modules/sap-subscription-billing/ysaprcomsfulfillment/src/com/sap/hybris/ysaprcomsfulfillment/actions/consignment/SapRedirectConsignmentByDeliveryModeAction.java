/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysaprcomsfulfillment.actions.consignment;

import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.sap.saporderexchangeoms.model.SapConsignmentProcessModel;
import org.apache.log4j.Logger;


/**
 * Redirects to the proper wait node depending on whether a consignment is for ship or pickup.
 */
public class SapRedirectConsignmentByDeliveryModeAction extends AbstractProceduralAction<SapConsignmentProcessModel>
{
	
	private static final Logger LOG = Logger.getLogger(SapRedirectConsignmentByDeliveryModeAction.class);
	@Override
	public void executeAction(SapConsignmentProcessModel process) throws Exception {
		LOG.info(String.format("Process: %s in step %s",process.getCode(), getClass().getSimpleName()));
			
	}
	
}

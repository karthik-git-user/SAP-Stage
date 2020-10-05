/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.inbound.events;

import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 * Event for setting service order completion
 */
public class SetCompletionEvent extends AbstractEvent
{
	
	private final String message;
	private final SAPOrderModel sapOrder;
	
	public SetCompletionEvent(final String message, final SAPOrderModel sapOrder)
	{
		super();
		this.message = message;
		this.sapOrder = sapOrder;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}


	/**
	 * @return the sapOrder
	 */
	public SAPOrderModel getSapOrder()
	{
		return sapOrder;
	}

}

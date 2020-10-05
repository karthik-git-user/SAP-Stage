/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.inbound.events;

import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class SetCompletionEventListener extends AbstractEventListener<SetCompletionEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(SetCompletionEventListener.class);
	public static final String ORDER_ACTION_EVENT_NAME = "OrderActionEvent";
	public static final String UNDERSCORE = "_";
	public static final String CONSIGNMENT_PROCESS_ENDED = "consignmentProcessEnded";
	private BusinessProcessService businessProcessService;

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

	@Override
	protected void onEvent(final SetCompletionEvent e)
	{
		LOG.info("SetCompletionEventListner is called");
		e.getSapOrder().getOrder().getOrderProcess().stream().forEach(orderProcess -> {
			if ("OrderProcess".equals(orderProcess.getItemtype()))
			{
				final String eventId = new StringBuilder().append(orderProcess.getCode()).append(UNDERSCORE)
						.append(ORDER_ACTION_EVENT_NAME).toString();

				final BusinessProcessEvent businessProcessevent = BusinessProcessEvent.builder(eventId)
						.withChoice(CONSIGNMENT_PROCESS_ENDED).build();

				getBusinessProcessService().triggerEvent(businessProcessevent);

			}
		});
	}

}

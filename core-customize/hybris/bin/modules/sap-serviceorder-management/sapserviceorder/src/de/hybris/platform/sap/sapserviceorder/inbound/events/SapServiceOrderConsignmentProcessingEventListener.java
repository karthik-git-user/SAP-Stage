/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.inbound.events;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.events.ConsignmentProcessingEvent;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapserviceorder.constants.SapserviceorderConstants;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.warehousing.constants.WarehousingConstants;

public class SapServiceOrderConsignmentProcessingEventListener extends AbstractEventListener<ConsignmentProcessingEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SapServiceOrderConsignmentProcessingEventListener.class);
	private BusinessProcessService businessProcessService;
	private ModelService modelService;

	@Override
	protected void onEvent(ConsignmentProcessingEvent event) {

		ConsignmentModel consignment = event.getProcess().getConsignment();

		if (!SapServiceOrderUtil.isServiceConsignment(consignment) && consignment.getStatus().equals(ConsignmentStatus.SHIPPED)) {
			for (ConsignmentModel relatedConsignment : consignment.getRelatedConsignments()) {
				if (SapServiceOrderUtil.isServiceConsignment(relatedConsignment)) {
					setRequestedServiceStartDateInOrder(relatedConsignment, consignment.getShippingDate());
					LOG.info("Triggering ProcessConsignmentPreFulfillmentEvent for service consignment [{}] as related sales consignment [{}] is shipped", relatedConsignment.getCode(),consignment.getCode());
					String processEvent = relatedConsignment.getCode()
							+ WarehousingConstants.CONSIGNMENT_PROCESS_CODE_SUFFIX
							+ "_ProcessConsignmentPreFulfillmentEvent";
					getBusinessProcessService().triggerEvent(processEvent);
				}
			}
		} else if(SapServiceOrderUtil.isServiceConsignment(consignment) && consignment.getStatus().equals(ConsignmentStatus.SHIPPED)) {
			consignment.setStatus(ConsignmentStatus.SERVICED);
			getModelService().save(consignment);
			LOG.info("Service Order Status is set to SERVICED");
		}

	}
	
	protected void setRequestedServiceStartDateInOrder(ConsignmentModel consignment, Date requestServiceStartDate) {
		OrderModel order = (OrderModel) consignment.getOrder();
		if (order.getRequestedServiceStartDate() == null && order.getStore() != null && order.getStore().getSAPConfiguration() != null)
		{
			SAPConfigurationModel sapConfig = order.getStore().getSAPConfiguration();
			Calendar c = Calendar.getInstance();
			c.setTime(requestServiceStartDate);
			int leadDays = sapConfig.getLeadDays() != null ? sapConfig.getLeadDays() : Integer.parseInt(Config.getParameter(SapserviceorderConstants.DEFAULT_LEAD_DAYS));
			c.add(Calendar.DATE, leadDays);
			order.setRequestedServiceStartDate(c.getTime());
			getModelService().save(order);
		}
	}

	public BusinessProcessService getBusinessProcessService() {
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService) {
		this.businessProcessService = businessProcessService;
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

}

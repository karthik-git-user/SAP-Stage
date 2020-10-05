/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapfsmserviceorder.hook.impl;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapfsmserviceorder.constants.SapfsmserviceorderConstants;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapserviceorder.hook.SapServiceOrderUpdateHook;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import rx.SingleSubscriber;


/**
 *
 */
public class DefaultSapServiceOrderUpdateFSMHook implements SapServiceOrderUpdateHook
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapServiceOrderUpdateFSMHook.class);
	protected static final String OUTBOUND_SERVICE_ORDER_UPDATE_OBJECT = "OutboundUpdateOMSServiceOrder";
	protected static final String OUTBOUND_SERVICE_ORDER_UPDATE_FSM_DESTINATION = "scpiServiceOrderUpdateFSMDestination";
	private OutboundServiceFacade outboundServiceFacade;
	private ConfigurationService configurationService;

	private boolean responseResult;
	
	@Override
	public boolean execute(final SAPCpiOutboundServiceOrderModel outboundOrder)
	{
		if(isFSMintegrationEnabled())
		{
			if(isServiceCallReleasedToFSM(outboundOrder))
			{
				getOutboundServiceFacade()
						.send(outboundOrder, OUTBOUND_SERVICE_ORDER_UPDATE_OBJECT, OUTBOUND_SERVICE_ORDER_UPDATE_FSM_DESTINATION).toSingle()
						.subscribe(new SingleSubscriber<ResponseEntity<Map>>()
						{
							@Override
							public void onError(final Throwable error)
							{
								LOG.error("Could not replicate the update of the service order [{}] to the SAP FSM back end.",
										outboundOrder.getServiceOrderId());
								SapServiceOrderUtil.logOutboundErrorReason(LOG, error);
								setResponseResult(false);
							}
		
							@Override
							public void onSuccess(final ResponseEntity<Map> arg0)
							{
								setResponseResult(true);
								LOG.info("Successfully replicated service order [{}] reschedule to the SAP FSM back end.", outboundOrder.getServiceOrderId());
							}
						});
		
				return isSuccessful();
			}
			else
			{
				LOG.info("Skipping replication of update to SAP FSM because the corresponding service call has not been created yet.");
				return true;
			}
		}
		else
		{
			return true;
		}
	}
	
	protected boolean isServiceCallReleasedToFSM(final SAPCpiOutboundServiceOrderModel outboundOrder)
	{
		return outboundOrder.getSapOrderStatus().equals(SAPOrderStatus.SERVICE_ORDER_IS_RELEASED);
	}
	
	protected boolean isFSMintegrationEnabled()
	{
		return getConfigurationService().getConfiguration().getBoolean(SapfsmserviceorderConstants.SAP_FSM_INTEGRATION_ENABLED, false);
	}

	private boolean isSuccessful()
	{
		return responseResult;
	}

	private void setResponseResult(boolean result)
	{
		this.responseResult = result;
	}

	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapserviceorder.hook.SapServiceOrderUpdateHook;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundService;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import rx.Observable;
import rx.SingleSubscriber;

/**
 * Implementation for Service Order Outbound service
 */
public class DefaultSapCpiServiceOrderOutboundService implements SapCpiServiceOrderOutboundService {
	// Order Outbound
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapCpiServiceOrderOutboundService.class);
	protected static final String OUTBOUND_SERVICE_ORDER_OBJECT = "OutboundOMSServiceOrder";
	protected static final String OUTBOUND_SERVICE_ORDER_UPDATE_OBJECT = "OutboundUpdateOMSServiceOrder";
	
	protected static final String OUTBOUND_SERVICE_ORDER_DESTINATION = "scpiServiceOrderDestination";
	protected static final String OUTBOUND_SERVICE_ORDER_UPDATE_DESTINATION = "scpiServiceOrderUpdateDestination";
	
	private static final String OUTBOUND_CANCEL_SERVICE_ORDER_OBJECT = "OutboundOMSCancelServiceOrder";
	private static final String OUTBOUND_CANCEL_SERVICE_ORDER_DESTINATION = "scpiCancelServiceOrderDestination";
	
	private OutboundServiceFacade outboundServiceFacade;
	protected List<SapServiceOrderUpdateHook> sapServiceOrderUpdateHooks;
	private boolean updateResponseResult;

	@Override
	public Observable<ResponseEntity<Map>> sendServiceOrder(
			final SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		return getOutboundServiceFacade().send(sapCpiOutboundServiceOrderModel, OUTBOUND_SERVICE_ORDER_OBJECT,
				OUTBOUND_SERVICE_ORDER_DESTINATION);
	}

	
	
	
	@Override
	public boolean sendServiceOrderUpdate(SAPCpiOutboundServiceOrderModel outboundOrder)
	{
		updateResponseResult = false;
		if(executeServiceOrderUpdatePreSendHooks(outboundOrder))
		{
			getOutboundServiceFacade().send(outboundOrder, OUTBOUND_SERVICE_ORDER_UPDATE_OBJECT,
				OUTBOUND_SERVICE_ORDER_UPDATE_DESTINATION).toSingle().subscribe(new SingleSubscriber<ResponseEntity<Map>>()
				{
					@Override
					public void onError(final Throwable error)
					{
						LOG.error("Could not replicate the update of the service order [{}] to the SAP S/4HANA back end.",
								outboundOrder.getServiceOrderId());
						SapServiceOrderUtil.logOutboundErrorReason(LOG, error);
					}

					@Override
					public void onSuccess(final ResponseEntity<Map> arg0)
					{
						setResponseSuccess();
						LOG.info("Successfully replicated service order [{}] reschedule to the SAP S/4HANA back end.", outboundOrder.getServiceOrderId());
					}
				});
		}
		return isUpdateSuccessful();
	}

	protected boolean executeServiceOrderUpdatePreSendHooks(
			SAPCpiOutboundServiceOrderModel outboundOrder)
	{
		for(SapServiceOrderUpdateHook updateHook: getSapServiceOrderUpdateHooks())
		{
			if(!updateHook.execute(outboundOrder))
			{
				LOG.error("Service order update hook for the update of the service order [{}] has failed.",
						outboundOrder.getServiceOrderId());
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Observable<ResponseEntity<Map>> sendServiceOrderCancellation(SAPCpiOutboundServiceOrderModel order) {
		return getOutboundServiceFacade().send(order, OUTBOUND_CANCEL_SERVICE_ORDER_OBJECT, OUTBOUND_CANCEL_SERVICE_ORDER_DESTINATION);
	}
	
	protected boolean isUpdateSuccessful()
	{
		return updateResponseResult;
	}

	protected void setResponseSuccess()
	{
		this.updateResponseResult = true;
	}
	
	public List<SapServiceOrderUpdateHook> getSapServiceOrderUpdateHooks() {
		return sapServiceOrderUpdateHooks;
	}

	public void setSapServiceOrderUpdateHooks(
			List<SapServiceOrderUpdateHook> sapServiceOrderUpdateHooks) {
		this.sapServiceOrderUpdateHooks = sapServiceOrderUpdateHooks;
	}

	/**
	 * @return the outboundServiceFacade
	 */
	public OutboundServiceFacade getOutboundServiceFacade() {
		return outboundServiceFacade;
	}

	/**
	 * @param outboundServiceFacade the outboundServiceFacade to set
	 */
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade) {
		this.outboundServiceFacade = outboundServiceFacade;
	}
}

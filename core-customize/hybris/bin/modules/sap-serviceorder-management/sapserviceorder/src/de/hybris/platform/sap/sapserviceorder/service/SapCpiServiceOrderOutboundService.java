/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service;

import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import rx.Observable;


/**
 * Interface for Service Order Outbound Service which will send Service Order to SCPI
 */
public interface SapCpiServiceOrderOutboundService
{

	/**
	 * Method for sending Service Order Create Payload to SCPI
	 * @param sapCpiOutboundServiceOrderModel Outbound service order create payload
	 * @return response
	 */
	public Observable<ResponseEntity<Map>> sendServiceOrder(SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel);
	
	/**
	 * Method for sending Service Order Update Payload to SCPI
	 * @param sapCpiOutboundServiceOrderModel Outbound service order update payload
	 * @return boolean response success
	 */
	public boolean sendServiceOrderUpdate(SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel);
	
	/**
	 * Method for sending Service Order Cancellation to SCPI
	 * @param order service order
	 * @return response
	 */
	public Observable<ResponseEntity<Map>> sendServiceOrderCancellation(SAPCpiOutboundServiceOrderModel order);
	
}

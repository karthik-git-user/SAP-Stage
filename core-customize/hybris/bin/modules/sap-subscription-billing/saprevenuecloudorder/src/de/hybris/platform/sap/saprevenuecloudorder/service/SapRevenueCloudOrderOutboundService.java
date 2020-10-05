/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import de.hybris.platform.core.model.order.OrderModel;
import rx.Observable;

/**
 * Sends order to Revenue Cloud via CPI.
 */
public interface SapRevenueCloudOrderOutboundService {
	
	/**
	 * Sends order to Revenue Cloud
	 * 
	 * @param orderModel OrderModel
	 * @return Observable<ResponseEntity<Map>>
	 */
	public Observable<ResponseEntity<Map>> sendOrder(OrderModel orderModel);
}

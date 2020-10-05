/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationAbapRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest;



/**
 * Http Entity Builder for REST Service calls.
 */
public interface HttpEntityBuilder
{

	/**
	 * Create HTTP entity for reservation
	 *
	 * @param header
	 * @param abap
	 * @return HttpEntity<ReservationAbapRequest>
	 */
	HttpEntity<ReservationAbapRequest> createHttpEntity(HttpHeaders header, ReservationAbapRequest abap);

	/**
	 * Create HTTP entity for sourcing
	 *
	 * @param header
	 * @param abap
	 * @return HttpEntity<SourcingAbapRequest>
	 */
	HttpEntity<SourcingAbapRequest> createHttpEntityForSourcing(HttpHeaders header, SourcingAbapRequest abap);

	/**
	 * Create HTTP entity with String
	 *
	 * @param header
	 * @param string
	 * @return HttpEntity<String>
	 */
	HttpEntity<String> createHttpEntity(HttpHeaders header, String string);

	/**
	 * Create HTTP entity
	 *
	 * @param header
	 * @return HttpEntity
	 */
	HttpEntity createHttpEntity(HttpHeaders header);

}

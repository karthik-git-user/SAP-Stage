/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationAbapRequest;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpEntityBuilder;



/**
 *
 */
@UnitTest
public class DefaultHttpEntityBuilderTest
{

	private final DefaultHttpEntityBuilder classUnderTest = new DefaultHttpEntityBuilder();

	@Test
	public void testReservation()
	{
		final ReservationAbapRequest abap = new ReservationAbapRequest();
		final HttpHeaders header = new HttpHeaders();
		Assert.assertNotNull(classUnderTest.createHttpEntity(header, abap));
	}

	@Test
	public void testSourcing()
	{
		final com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest abap = new com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest();
		final HttpHeaders header = new HttpHeaders();
		Assert.assertNotNull(classUnderTest.createHttpEntityForSourcing(header, abap));
	}

	@Test
	public void testAuthentication()
	{
		final HttpHeaders header = new HttpHeaders();
		Assert.assertNotNull(classUnderTest.createHttpEntity(header, ""));
	}


	@Test
	public void testAvailability()
	{
		final HttpHeaders header = new HttpHeaders();
		Assert.assertNotNull(classUnderTest.createHttpEntity(header));
	}
}

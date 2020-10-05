/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request.ReservationAbapRequest;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.model.enums.Sapoaa_mode;



/**
 *
 */
@UnitTest
public class DefaultReservationRequestMapperTest
{
	private static final String OAA_CONSUMER_ID = "OAA_CON";
	private static final String SALES_CHANNEL = "OAA_CHANNEL";
	private static final String ORDER_ID = "123-456-789";
	private static final String OAA_RESV_STATUS_ORDER = "O";
	private static final String OAA_RESV_STATUS_CART = "C";

	private DefaultReservationRequestMapper classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultReservationRequestMapper();

	}

	@Test
	public void testMapOrderToReservationRequestFallbackOrder()
	{
		//WHEN: No Mode is specified
		final RestServiceConfiguration restConfiguration = EasyMock.createNiceMock(RestServiceConfiguration.class);
		EasyMock.expect(restConfiguration.getConsumerId()).andReturn(OAA_CONSUMER_ID).anyTimes();
		EasyMock.replay(restConfiguration);

		final OrderModel order = new OrderModel();
		order.setCode(ORDER_ID);
		final ReservationAbapRequest rootObject = classUnderTest.mapOrderModelToReservationRequest(order,
				SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER, restConfiguration);

		Assert.assertEquals(ORDER_ID, rootObject.getValues().getReservation().getOrderId());
		Assert.assertEquals(OAA_RESV_STATUS_ORDER, rootObject.getValues().getReservation().getStatus());
		Assert.assertEquals(OAA_CONSUMER_ID, rootObject.getValues().getReservation().getConsumerId());
	}

	@Test
	public void testMapOrderToReservationRequestFallbackCart()
	{
		//WHEN: No Mode and no order Id is specified, Cart is specified as reservation status
		final RestServiceConfiguration restConfiguration = EasyMock.createNiceMock(RestServiceConfiguration.class);
		EasyMock.expect(restConfiguration.getConsumerId()).andReturn(OAA_CONSUMER_ID).anyTimes();
		EasyMock.replay(restConfiguration);

		final OrderModel order = new OrderModel();
		final ReservationAbapRequest rootObject = classUnderTest.mapOrderModelToReservationRequest(order,
				SapoaacommerceservicesConstants.RESERVATION_STATUS_CART, restConfiguration);

		//THEN: Consumer ID Should be fille, Sales Channel should be Null and order id should be null
		Assert.assertNull(rootObject.getValues().getReservation().getOrderId());
		Assert.assertEquals(OAA_RESV_STATUS_CART, rootObject.getValues().getReservation().getStatus());
		Assert.assertEquals(OAA_CONSUMER_ID, rootObject.getValues().getReservation().getConsumerId());
		Assert.assertNull(rootObject.getValues().getReservation().getSalesChannel());
		Assert.assertNull(rootObject.getValues().getReservation().getOrderId());
	}


	@Test
	public void testMapOrderToReservationRequestOAAProfile()
	{
		//WHEN: OAA Mode is specified
		final RestServiceConfiguration restConfiguration = EasyMock.createNiceMock(RestServiceConfiguration.class);
		EasyMock.expect(restConfiguration.getMode()).andReturn(Sapoaa_mode.OAAPROFILE).anyTimes();
		EasyMock.expect(restConfiguration.getConsumerId()).andReturn(OAA_CONSUMER_ID).anyTimes();
		EasyMock.replay(restConfiguration);

		final OrderModel order = new OrderModel();
		order.setCode(ORDER_ID);
		final ReservationAbapRequest rootObject = classUnderTest.mapOrderModelToReservationRequest(order,
				SapoaacommerceservicesConstants.RESERVATION_STATUS_CART, restConfiguration);

		//THEN: Consumer ID Should be filled and Sales Channel should be Null
		Assert.assertNull(rootObject.getValues().getReservation().getOrderId());
		Assert.assertEquals(OAA_RESV_STATUS_CART, rootObject.getValues().getReservation().getStatus());
		Assert.assertEquals(OAA_CONSUMER_ID, rootObject.getValues().getReservation().getConsumerId());
		Assert.assertNull(rootObject.getValues().getReservation().getSalesChannel());
	}

	@Test
	public void testMapOrderToReservationRequestSalesChannel()
	{
		//WHEN: OAA Mode is specified
		final RestServiceConfiguration restConfiguration = EasyMock.createNiceMock(RestServiceConfiguration.class);
		EasyMock.expect(restConfiguration.getMode()).andReturn(Sapoaa_mode.SALESCHANNEL).anyTimes();
		EasyMock.expect(restConfiguration.getSalesChannel()).andReturn(SALES_CHANNEL).anyTimes();
		EasyMock.replay(restConfiguration);

		final OrderModel order = new OrderModel();
		order.setCode(ORDER_ID);
		final ReservationAbapRequest rootObject = classUnderTest.mapOrderModelToReservationRequest(order,
				SapoaacommerceservicesConstants.RESERVATION_STATUS_CART, restConfiguration);

		//THEN: Sales Channel should be filled and Consumer should be empty
		Assert.assertNull(rootObject.getValues().getReservation().getOrderId());
		Assert.assertEquals(OAA_RESV_STATUS_CART, rootObject.getValues().getReservation().getStatus());
		Assert.assertEquals(SALES_CHANNEL, rootObject.getValues().getReservation().getSalesChannel());
		Assert.assertNull(rootObject.getValues().getReservation().getConsumerId());
	}

}

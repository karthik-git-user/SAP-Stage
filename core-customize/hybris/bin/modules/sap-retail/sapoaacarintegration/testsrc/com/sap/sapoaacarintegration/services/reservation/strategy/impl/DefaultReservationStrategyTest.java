/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.strategy.impl;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.sapoaacarintegration.services.reservation.ReservationService;
import com.sap.sapoaacarintegration.services.reservation.exception.ReservationException;
import com.sap.sapoaacarintegration.services.reservation.impl.DefaultReservationService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 *
 */
@UnitTest
public class DefaultReservationStrategyTest
{
	private DefaultReservationStrategy classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultReservationStrategy();
	}

	@Test
	public void deleteReservationTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);

		Assert.assertTrue(classUnderTest.deleteReservation(orderModel));
	}

	@Test
	public void deleteReservationItemTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);

		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.TRUE);

		Assert.assertTrue(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}

	@Test
	public void deleteReservationNoReservationTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.FALSE);

		Assert.assertTrue(classUnderTest.deleteReservation(orderModel));
	}

	@Test
	public void deleteReservationItemNoReservationTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);

		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.FALSE);

		Assert.assertTrue(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}

	@Test
	public void deleteReservationExceptionTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		reservationService.deleteReservation(EasyMock.anyObject(AbstractOrderModel.class));
		EasyMock.expectLastCall().andThrow(new ReservationException());
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);

		Assert.assertFalse(classUnderTest.deleteReservation(orderModel));
	}

	@Test
	public void deleteReservationItemExceptionTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		reservationService.deleteReservationItem(EasyMock.anyObject(AbstractOrderModel.class),
				EasyMock.anyObject(AbstractOrderEntryModel.class));
		EasyMock.expectLastCall().andThrow(new ReservationException());
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);

		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.TRUE);

		Assert.assertFalse(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}

	@Test
	public void deleteReservationOfflineExceptionTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		reservationService.deleteReservation(EasyMock.anyObject(AbstractOrderModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException());
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);

		Assert.assertTrue(classUnderTest.deleteReservation(orderModel));
	}

	@Test
	public void deleteReservationItemOfflineExceptionTest()
	{
		//Mock Reservation Service
		final ReservationService reservationService = EasyMock.createNiceMock(DefaultReservationService.class);
		reservationService.deleteReservationItem(EasyMock.anyObject(AbstractOrderModel.class),
				EasyMock.anyObject(AbstractOrderEntryModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException());
		EasyMock.replay(reservationService);
		classUnderTest.setReservationService(reservationService);

		final AbstractOrderModel orderModel = new AbstractOrderModel();
		orderModel.setGuid("Test");
		orderModel.setSapBackendReservation(Boolean.TRUE);

		final AbstractOrderEntryModel orderEntryModel = new AbstractOrderEntryModel();
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setSapBackendReservation(Boolean.TRUE);

		Assert.assertTrue(classUnderTest.deleteReservationItem(orderModel, orderEntryModel));
	}
}

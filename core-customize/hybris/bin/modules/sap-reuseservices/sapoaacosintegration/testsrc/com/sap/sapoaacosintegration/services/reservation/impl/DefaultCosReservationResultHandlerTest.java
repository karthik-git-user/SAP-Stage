/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.reservation.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.response.ReservationResponse;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosReservationResultHandlerTest
{
	@Mock
	ModelService modelService;

	@InjectMocks
	DefaultCosReservationResultHandler mockDefaultCosReservationResultHandler;

	private final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
	private final ReservationResponse reservationResponse = new ReservationResponse();
	private final List<AbstractOrderEntryModel> listAbstractOrderEntryModel = new ArrayList<>();
	private final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();



	@Before
	public void setUp()
	{
		//AbstractOrderEntryModel
		abstractOrderEntryModel.setEntryNumber(10201);

		//ListAbstractOrderEntryModel
		listAbstractOrderEntryModel.add(abstractOrderEntryModel);

		//AbstractOrderModel
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
	}

	@Test
	public void updateReservationTest()
	{
		//SetUp
		doNothing().when(modelService).save(eq(abstractOrderEntryModel));

		//Execute
		mockDefaultCosReservationResultHandler.updateReservation(abstractOrderModel, reservationResponse);

		//Verify
		Assert.assertEquals(true, abstractOrderModel.getSapBackendReservation());
		Assert.assertEquals(true, abstractOrderModel.getCosReservationExpireFlag());
		for (final AbstractOrderEntryModel result : abstractOrderModel.getEntries())
		{
			Assert.assertEquals(true, result.getSapBackendReservation());
		}
	}

	@Test
	public void deleteReservationTest()
	{
		//SetUp
		doNothing().when(modelService).save(eq(abstractOrderEntryModel));

		//Execute
		mockDefaultCosReservationResultHandler.deleteReservation(abstractOrderModel);

		//Verify
		Assert.assertEquals(false, abstractOrderModel.getSapBackendReservation());
		Assert.assertEquals(true, abstractOrderModel.getCosReservationExpireFlag());
		Assert.assertEquals("", abstractOrderModel.getCosReservationId());
		for (final AbstractOrderEntryModel result : abstractOrderModel.getEntries())
		{
			Assert.assertEquals(false, result.getSapBackendReservation());
		}
	}

	@Test
	public void deleteReservationItemTest()
	{
		//SetUp
		doNothing().when(modelService).save(eq(abstractOrderEntryModel));

		//Execute
		mockDefaultCosReservationResultHandler.deleteReservationItem(abstractOrderEntryModel);

		//Verify
		Assert.assertEquals(false, abstractOrderEntryModel.getSapBackendReservation());
	}


}

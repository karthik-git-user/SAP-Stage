/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.reservation.impl;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 *
 */
@UnitTest
public class DefaultReservationResultHandlerTest
{
	private final DefaultReservationResultHandler classUnderTest = new DefaultReservationResultHandler();


	@Before
	public void setup()
	{
		final ModelService modelService = EasyMock.createNiceMock(ModelService.class);
		EasyMock.replay(modelService);
		classUnderTest.setModelService(modelService);
	}

	@Test
	public void deleteReservationNoItemsTest()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setSapBackendReservation(Boolean.TRUE);

		final ArrayList<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();

		order.setEntries(entries);

		classUnderTest.deleteReservation(order);

		Assert.assertEquals(Boolean.FALSE, order.getSapBackendReservation());
	}

	@Test
	public void deleteReservationWithItemsTest()
	{
		final AbstractOrderModel order = new AbstractOrderModel();
		order.setSapBackendReservation(Boolean.TRUE);

		final ArrayList<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();

		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		orderEntry.setSapBackendReservation(Boolean.TRUE);

		entries.add(orderEntry);
		entries.add(orderEntry);

		order.setEntries(entries);

		classUnderTest.deleteReservation(order);

		Assert.assertEquals(Boolean.FALSE, order.getSapBackendReservation());

		for (final AbstractOrderEntryModel entry : order.getEntries())
		{
			Assert.assertEquals(Boolean.FALSE, entry.getSapBackendReservation());
		}
	}

	@Test
	public void deleteReservationItemTest()
	{
		final AbstractOrderEntryModel orderEntry = new AbstractOrderEntryModel();
		orderEntry.setSapBackendReservation(Boolean.TRUE);

		classUnderTest.deleteReservationItem(orderEntry);
		Assert.assertEquals(Boolean.FALSE, orderEntry.getSapBackendReservation());
	}

}
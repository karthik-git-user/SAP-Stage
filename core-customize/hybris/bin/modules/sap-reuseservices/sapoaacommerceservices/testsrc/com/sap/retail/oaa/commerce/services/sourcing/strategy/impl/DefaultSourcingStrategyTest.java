/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.sourcing.SourcingService;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;


/**
 *
 */
@UnitTest
public class DefaultSourcingStrategyTest
{
	private DefaultSourcingStrategy classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultSourcingStrategy();
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testdoSourcingOffline_PickUp()
	{
		final SourcingService sourcingServiceMock = EasyMock.createNiceMock(SourcingService.class);
		sourcingServiceMock.callRestServiceAndPersistResult(EasyMock.anyObject(AbstractOrderModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException());
		EasyMock.replay(sourcingServiceMock);

		classUnderTest.setSourcingService(sourcingServiceMock);
		Assert.assertNotNull(classUnderTest.getSourcingService());

		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		entry.setDeliveryPointOfService(new PointOfServiceModel());

		final List<AbstractOrderEntryModel> entries = new ArrayList(1);
		entries.add(entry);

		cartModel.setEntries(entries);

		Assert.assertFalse(classUnderTest.doSourcing(cartModel));
	}


	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testdoSourcingOffline_ShipTo()
	{
		final SourcingService sourcingServiceMock = EasyMock.createNiceMock(SourcingService.class);
		sourcingServiceMock.callRestServiceAndPersistResult(EasyMock.anyObject(AbstractOrderModel.class));
		EasyMock.expectLastCall().andThrow(new BackendDownException());
		EasyMock.replay(sourcingServiceMock);

		classUnderTest.setSourcingService(sourcingServiceMock);

		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final List<AbstractOrderEntryModel> entries = new ArrayList(1);
		entries.add(entry);

		cartModel.setEntries(entries);

		Assert.assertTrue(classUnderTest.doSourcing(cartModel));
	}


	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testdoSourcing_True()
	{
		final SourcingService sourcingServiceMock = EasyMock.createNiceMock(SourcingService.class);
		sourcingServiceMock.callRestServiceAndPersistResult(EasyMock.anyObject(AbstractOrderModel.class));
		EasyMock.expectLastCall();
		EasyMock.replay(sourcingServiceMock);

		classUnderTest.setSourcingService(sourcingServiceMock);

		final CartModel cartModel = new CartModel();
		Assert.assertTrue(classUnderTest.doSourcing(cartModel));
	}

	@Test
	@SuppressWarnings("PMD.MethodNamingConventions")
	public void testdoSourcing_False()
	{
		final SourcingService sourcingServiceMock = EasyMock.createNiceMock(SourcingService.class);
		sourcingServiceMock.callRestServiceAndPersistResult(EasyMock.anyObject(AbstractOrderModel.class));
		EasyMock.expectLastCall().andThrow(new SourcingException());
		EasyMock.replay(sourcingServiceMock);

		classUnderTest.setSourcingService(sourcingServiceMock);

		final CartModel cartModel = new CartModel();
		Assert.assertFalse(classUnderTest.doSourcing(cartModel));
	}
}

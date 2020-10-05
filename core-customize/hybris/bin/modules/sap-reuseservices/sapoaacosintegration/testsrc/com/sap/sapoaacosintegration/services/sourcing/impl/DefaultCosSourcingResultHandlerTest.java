/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.sourcing.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.AvailabilityResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.CartItemsResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultCartItemResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultsResponse;
import com.sap.retail.oaa.model.model.ScheduleLineModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosSourcingResultHandlerTest
{
	@Mock
	BaseStoreService baseStoreService;

	@Mock
	ModelService modelService;

	@Mock
	ServiceUtils serviceUtils;

	@Mock
	RestServiceConfiguration restServiceConfiguration;

	@Mock
	GenericDao<PointOfServiceModel> pointOfServiceGenericDao;

	@InjectMocks
	DefaultCosSourcingResultHandler defaultCosSourcingResultHandler;

	private final SourcingResponse sourcingResponse = new SourcingResponse();
	private final AbstractOrderModel abstractOrderModel = new AbstractOrderModel();
	private final MessagesList messages = new MessagesList();
	private final SourcingResultsResponse sourcingResults = new SourcingResultsResponse();
	private final List<SourcingResultResponse> listSourcingResults = new ArrayList<>();
	private final SourcingResultResponse sourcingResultResponse = new SourcingResultResponse();
	private final CartItemsResponse cartItems = new CartItemsResponse();
	private final List<SourcingResultCartItemResponse> listSourcingResultCartItems = new ArrayList<>();
	private final SourcingResultCartItemResponse sourcingResultCartItemResponse = new SourcingResultCartItemResponse();
	private final List<AbstractOrderEntryModel> listAbstractOrderEntryModel = new ArrayList<>();
	private final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
	private final AvailabilityResponse availability = new AvailabilityResponse();
	private final List<AvailabilityItemResponse> availabilityItems = new ArrayList<>();
	private final AvailabilityItemResponse availabilityItemResponse = new AvailabilityItemResponse();
	private final BaseStoreModel baseStoreModel = new BaseStoreModel();
	private final List<PointOfServiceModel> listPointOfServiceModel = new ArrayList<>();
	private final PointOfServiceModel pointOfServiceModel = new PointOfServiceModel();
	private final Logger LOG = Logger.getLogger(DefaultCosSourcingResultHandler.class);
	private final ScheduleLineModel scheduleLineModel = new ScheduleLineModel();
	private final String ITEM_ID = "ID123";
	private final String PURCH_SITE = "PURCH_SITE";



	@Before
	public void setup()
	{
		defaultCosSourcingResultHandler.setModelService(modelService);
		defaultCosSourcingResultHandler.setBaseStoreService(baseStoreService);
		defaultCosSourcingResultHandler.setPointOfServiceGenericDao(pointOfServiceGenericDao);
		defaultCosSourcingResultHandler.setServiceUtils(serviceUtils);

		//Mocks
		doReturn(false).when(serviceUtils).logMessageResponseAndCheckMessageType(eq(LOG), any(MessagesList.class));
		doNothing().when(modelService).save(anyObject());
		doReturn(scheduleLineModel).when(modelService).create(ScheduleLineModel.class);
		doReturn(baseStoreModel).when(baseStoreService).getCurrentBaseStore();
		doReturn(listPointOfServiceModel).when(pointOfServiceGenericDao).find(any(HashMap.class));

		final PointOfServiceModel e;
		//ListPointOfServiceModel
		listPointOfServiceModel.add(pointOfServiceModel);

		//RestServiceConfiguration
		when(restServiceConfiguration.getItemCategory()).thenReturn("ITEM_CATEGORY");

		//AvailabilityItemResponse
		availabilityItemResponse.setQuantity(1000d);
		availabilityItemResponse.setAtpDate("dd-mm-yyyy");

		//AvailabilityItems
		availabilityItems.add(availabilityItemResponse);

		//AvailabilityResponse
		availability.setAvailabilityItems(availabilityItems);

		//SourcingResultCartItemResponse
		sourcingResultCartItemResponse.setExternalId(ITEM_ID);
		sourcingResultCartItemResponse.setAvailability(availability);
		sourcingResultCartItemResponse.setPurchSite(PURCH_SITE);
		sourcingResultCartItemResponse.setSource("SOURCE");

		//ListSourcingResultCartItems
		listSourcingResultCartItems.add(sourcingResultCartItemResponse);

		//CartItemsResponse
		cartItems.setSourcingResultCartItems(listSourcingResultCartItems);

		//SourcingResultResponse
		sourcingResultResponse.setCartItems(cartItems);

		//ListSourcingResults
		listSourcingResults.add(sourcingResultResponse);

		//SourcingResultsResponse
		sourcingResults.setSourcingResult(listSourcingResults);

		//SourcingResponse
		sourcingResponse.setMessages(messages);
		sourcingResponse.setSourcingResults(sourcingResults);
		sourcingResponse.setReservationSuccessfulBoolean(true);

		//AbstractOrderEntryModel
		abstractOrderEntryModel.setCosOrderItemId(ITEM_ID);
		abstractOrderEntryModel.setQuantity((long) 100);

		//ListAbstractOrderEntryModel
		listAbstractOrderEntryModel.add(abstractOrderEntryModel);

		//AbstractOrderModel
		abstractOrderModel.setEntries(listAbstractOrderEntryModel);
	}

	@Test
	public void persistCosSourcingResultInCartTest()
	{
		//Execute
		defaultCosSourcingResultHandler.persistCosSourcingResultInCart(sourcingResponse, abstractOrderModel);

		//Verify
		Assert.assertEquals(true, abstractOrderModel.getSapBackendReservation());
	}

	@Test
	public void persistSourcingResultInCartTest()
	{
		//Execute
		defaultCosSourcingResultHandler.persistSourcingResultInCart(sourcingResponse, abstractOrderModel);

		//Verify
		Assert.assertNotNull(abstractOrderModel.getEntries().get(0).getScheduleLines());
		Assert.assertEquals(true, abstractOrderModel.getEntries().get(0).getSapBackendReservation());
		Assert.assertNotNull(abstractOrderModel.getEntries().get(0).getSapSource());
		Assert.assertNotNull(abstractOrderModel.getEntries().get(0).getSapVendor());
	}

	@Test
	public void populateScheduleLinesForPickupProductTest()
	{
		//Execute
		defaultCosSourcingResultHandler.populateScheduleLinesForPickupProduct(abstractOrderEntryModel);

		//Verify
		Assert.assertEquals((Double) 100d, scheduleLineModel.getConfirmedQuantity());
		Assert.assertNotNull(scheduleLineModel.getConfirmedDate());
		Assert.assertNotNull(abstractOrderEntryModel.getScheduleLines());

	}
}

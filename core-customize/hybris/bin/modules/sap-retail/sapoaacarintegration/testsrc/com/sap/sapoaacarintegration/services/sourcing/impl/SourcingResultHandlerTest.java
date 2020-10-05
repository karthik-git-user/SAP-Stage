/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.Message;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultServiceUtils;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.AvailabilityResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.CartItemsResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultCartItemResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultResponse;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResultsResponse;
import com.sap.retail.oaa.model.model.ScheduleLineModel;


/**
 *
 */
@UnitTest
public class SourcingResultHandlerTest
{
	private final DefaultSourcingResultHandler classUnderTest = new DefaultSourcingResultHandler();
	private final ServiceUtils serviceUtils = new DefaultServiceUtils();

	private static final String source1 = "1000";
	private static final String source2 = "2000";
	private static final String source3 = "3000";
	private static final String vendor1 = "VEND1";
	private static final String sourcingStrategy1 = "/OAA/1DELIVERY";
	private static final String sourcingStrategyNoRestriction = "/OAA/NO_RESTR";
	private static final String sourcingStrategyFallback = "/OAA/FALLBACK";
	private static final double quantity = 1;
	private static final String itemNo = "1234-10";
	private static final String itemNo2 = "1234-20";
	private static final String date1 = "2015-08-15";
	private static final String date2 = "2015-08-18";
	private static final String date3 = "2015-08-24";

	@Before
	public void setup()
	{
		classUnderTest.setServiceUtils(serviceUtils);

		ModelService modelServiceMock;
		BaseStoreService baseStoreServiceMock;
		GenericDao<PointOfServiceModel> pointOfServiceGenericDaoMock;

		modelServiceMock = EasyMock.createNiceMock(ModelService.class);
		baseStoreServiceMock = EasyMock.createNiceMock(BaseStoreService.class);
		pointOfServiceGenericDaoMock = EasyMock.createNiceMock(GenericDao.class);

		classUnderTest.setModelService(modelServiceMock);
		Assert.assertNotNull(classUnderTest.getModelService());

		classUnderTest.setBaseStoreService(baseStoreServiceMock);
		Assert.assertNotNull(classUnderTest.getBaseStoreService());

		classUnderTest.setPointOfServiceGenericDao(pointOfServiceGenericDaoMock);
		Assert.assertNotNull(classUnderTest.getPointOfServiceGenericDao());

		final Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("name", source1);
		paramMap.put("baseStore", null);

		final List<PointOfServiceModel> posModels = new ArrayList<PointOfServiceModel>();
		final PointOfServiceModel posModel = new PointOfServiceModel();
		posModel.setName(source1);
		posModel.setType(PointOfServiceTypeEnum.WAREHOUSE);
		posModels.add(posModel);

		EasyMock.expect(modelServiceMock.create(ScheduleLineModel.class)).andReturn(new ScheduleLineModel()).anyTimes();
		EasyMock.expect(baseStoreServiceMock.getCurrentBaseStore()).andReturn(null).anyTimes();
		EasyMock.expect(pointOfServiceGenericDaoMock.find(paramMap)).andReturn(posModels).anyTimes();

		EasyMock.replay(modelServiceMock, baseStoreServiceMock, pointOfServiceGenericDaoMock);
	}

	private SourcingResponse getResultWithoutErrors()
	{
		final SourcingResponse sourcing = new SourcingResponse();
		final SourcingResultsResponse sourcingResults = new SourcingResultsResponse();
		final List<SourcingResultResponse> sourcingResultsList = new ArrayList<SourcingResultResponse>();
		final SourcingResultResponse sourcingResult1 = new SourcingResultResponse();
		final SourcingResultResponse sourcingResult2 = new SourcingResultResponse();
		final SourcingResultResponse sourcingResult3 = new SourcingResultResponse();
		final CartItemsResponse cartItems1 = new CartItemsResponse();
		final CartItemsResponse cartItems2 = new CartItemsResponse();
		final CartItemsResponse cartItems3 = new CartItemsResponse();

		final List<SourcingResultCartItemResponse> sourcingResultCartItemsList1 = new ArrayList<SourcingResultCartItemResponse>();
		final List<SourcingResultCartItemResponse> sourcingResultCartItemsList2 = new ArrayList<SourcingResultCartItemResponse>();
		final List<SourcingResultCartItemResponse> sourcingResultCartItemsList3 = new ArrayList<SourcingResultCartItemResponse>();
		final SourcingResultCartItemResponse cartItem1 = new SourcingResultCartItemResponse();
		final SourcingResultCartItemResponse cartItem2 = new SourcingResultCartItemResponse();
		final SourcingResultCartItemResponse cartItem3 = new SourcingResultCartItemResponse();
		final AvailabilityResponse availability1 = new AvailabilityResponse();
		final AvailabilityResponse availability2 = new AvailabilityResponse();
		final AvailabilityResponse availability3 = new AvailabilityResponse();
		final List<AvailabilityItemResponse> availabilityItemList1 = new ArrayList<AvailabilityItemResponse>();
		final List<AvailabilityItemResponse> availabilityItemList2 = new ArrayList<AvailabilityItemResponse>();
		final List<AvailabilityItemResponse> availabilityItemList3 = new ArrayList<AvailabilityItemResponse>();
		final AvailabilityItemResponse availabilityItem1 = new AvailabilityItemResponse();
		final AvailabilityItemResponse availabilityItem2 = new AvailabilityItemResponse();
		final AvailabilityItemResponse availabilityItem3 = new AvailabilityItemResponse();

		availabilityItem1.setQuantity(quantity);
		availabilityItem2.setQuantity(quantity);
		availabilityItem3.setQuantity(quantity);
		availabilityItem1.setAtpDate(date1);
		availabilityItem2.setAtpDate(date2);
		availabilityItem3.setAtpDate(date3);

		availabilityItemList1.add(availabilityItem1);
		availabilityItemList2.add(availabilityItem2);
		availabilityItemList3.add(availabilityItem3);

		availability1.setAvailabilityItems(availabilityItemList1);
		availability2.setAvailabilityItems(availabilityItemList2);
		availability3.setAvailabilityItems(availabilityItemList3);

		cartItem1.setAvailability(availability1);
		cartItem2.setAvailability(availability2);
		cartItem3.setAvailability(availability3);

		cartItem1.setExternalId(itemNo);
		cartItem2.setExternalId(itemNo);
		cartItem3.setExternalId(itemNo);
		cartItem1.setSource(source1);
		cartItem2.setSource(source2);
		cartItem3.setSource(source3);

		cartItem2.setPurchSite(source1);


		sourcingResultCartItemsList1.add(cartItem1);
		sourcingResultCartItemsList2.add(cartItem2);
		sourcingResultCartItemsList3.add(cartItem3);

		cartItems1.setSourcingResultCartItems(sourcingResultCartItemsList1);
		cartItems2.setSourcingResultCartItems(sourcingResultCartItemsList2);
		cartItems3.setSourcingResultCartItems(sourcingResultCartItemsList3);

		sourcingResult1.setSourcingStrategyId(sourcingStrategy1);
		sourcingResult2.setSourcingStrategyId(sourcingStrategyNoRestriction);
		sourcingResult3.setSourcingStrategyId(sourcingStrategyFallback);

		sourcingResult1.setCartItems(cartItems1);
		sourcingResult2.setCartItems(cartItems2);
		sourcingResult3.setCartItems(cartItems3);

		sourcingResultsList.add(sourcingResult1);
		sourcingResultsList.add(sourcingResult2);
		sourcingResultsList.add(sourcingResult3);

		sourcingResults.setSourcingResult(sourcingResultsList);
		sourcing.setSourcingResults(sourcingResults);
		sourcing.setReservationSuccessful("X");

		return sourcing;
	}

	private MessagesList getMessages(final String type)
	{
		final MessagesList messages = new MessagesList();
		final List<Message> messageList = new ArrayList<Message>();
		final Message message = new Message();

		message.setType(type);
		message.setMessageText("Unit Test: TestSourcingResultHandler - Ignore!");

		messageList.add(message);
		messages.setMessages(messageList);

		return messages;
	}

	@Test
	public void resultParsing() throws ParseException
	{
		final SourcingResponse sourcing = getResultWithoutErrors();

		final CartModel cart = new CartModel();
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();

		OrderEntryModel entry = new OrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(10));
		entries.add(entry);

		entry = new OrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(20));
		entries.add(entry);


		cart.setEntries(entries);

		classUnderTest.persistSourcingResultInCart(sourcing, cart, null);

		Assert.assertEquals(source1, cart.getEntries().get(0).getSapSource().getName());
		Assert.assertEquals(new Double(quantity), cart.getEntries().get(0).getScheduleLines().get(0).getConfirmedQuantity());
		Assert.assertEquals(serviceUtils.parseStringToDate(date1),
				cart.getEntries().get(0).getScheduleLines().get(0).getConfirmedDate());



	}

	@Test
	public void messageValidation()
	{
		final SourcingResponse sourcing = getResultWithoutErrors();
		sourcing.setMessages(getMessages("E"));

		final CartModel cart = new CartModel();
		final OrderEntryModel entry = new OrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(10));
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entry);

		cart.setEntries(entries);

		assertPersistSourcingResultInCartSourcingException(sourcing, cart, null);

		Assert.assertNull(cart.getEntries().get(0).getScheduleLines());
		Assert.assertNull(cart.getEntries().get(0).getSapSource());

		sourcing.setMessages(getMessages("W"));
		cart.getEntries().remove(0);
		final OrderEntryModel entryW = new OrderEntryModel();
		entryW.setEntryNumber(Integer.valueOf(10));
		cart.getEntries().add(entryW);

		classUnderTest.persistSourcingResultInCart(sourcing, cart, null);

		Assert.assertEquals(source1, cart.getEntries().get(0).getSapSource().getName());
		Assert.assertEquals(new Double(quantity), cart.getEntries().get(0).getScheduleLines().get(0).getConfirmedQuantity());

		sourcing.setMessages(getMessages("I"));
		cart.getEntries().remove(0);
		final OrderEntryModel entryI = new OrderEntryModel();
		entryI.setEntryNumber(Integer.valueOf(10));
		cart.getEntries().add(entryI);

		classUnderTest.persistSourcingResultInCart(sourcing, cart, null);

		Assert.assertEquals(source1, cart.getEntries().get(0).getSapSource().getName());
		Assert.assertEquals(new Double(quantity), cart.getEntries().get(0).getScheduleLines().get(0).getConfirmedQuantity());
	}

	@Test
	public void noData()
	{
		final SourcingResponse sourcing = new SourcingResponse();

		final CartModel cart = new CartModel();
		final OrderEntryModel entry = new OrderEntryModel();
		entry.setEntryNumber(Integer.valueOf(10));
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entry);

		cart.setEntries(entries);

		assertPersistSourcingResultInCartSourcingException(sourcing, cart, null);

		final SourcingResultsResponse sourcingResults = new SourcingResultsResponse();
		sourcing.setSourcingResults(sourcingResults);

		assertPersistSourcingResultInCartSourcingException(sourcing, cart, null);

		final List<SourcingResultResponse> sourcingResultList = new ArrayList<>();
		sourcingResults.setSourcingResult(sourcingResultList);

		assertPersistSourcingResultInCartSourcingException(sourcing, cart, null);

		final SourcingResultResponse sourcingResultResponse = new SourcingResultResponse();
		sourcingResultList.add(sourcingResultResponse);

		assertPersistSourcingResultInCartSourcingException(sourcing, cart, null);

		final CartItemsResponse cartItems = new CartItemsResponse();
		sourcingResultResponse.setCartItems(cartItems);

		assertPersistSourcingResultInCartSourcingException(sourcing, cart, null);

	}

	@Test
	public void getPosForEntryExceptionTest()
	{

		final BaseStoreModel baseStoreModelMock = EasyMock.createNiceMock(BaseStoreModel.class);
		EasyMock.expect(baseStoreModelMock.getName()).andReturn("Test").anyTimes();

		final BaseStoreService baseStoreServiceMock = EasyMock.createNiceMock(BaseStoreService.class);
		EasyMock.expect(baseStoreServiceMock.getCurrentBaseStore()).andReturn(baseStoreModelMock).anyTimes();
		classUnderTest.setBaseStoreService(baseStoreServiceMock);

		EasyMock.replay(baseStoreModelMock, baseStoreServiceMock);

		try
		{
			classUnderTest.getPosForEntry(source2);
			Assert.fail("SourcingException expected, but no Exception was thrown.");
		}
		catch (final SourcingException e)
		{
			Assert.assertNotNull(e.getMessage());
		}
	}


	public void testResultMappingForVendorItems()
	{
		//TODO Write Test
	}

	private void assertPersistSourcingResultInCartSourcingException(final SourcingResponse sourcing, final CartModel cart,
			final RestServiceConfiguration restServiceConfig)
	{
		try
		{
			classUnderTest.persistSourcingResultInCart(sourcing, cart, restServiceConfig);
			Assert.fail("SourcingException expected, but no exception was thrown.");
		}
		catch (final SourcingException e)
		{
			Assert.assertNotNull(e.getMessage());
		}
	}
}
/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.atp.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPBatchResponse;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResponse;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResultItem;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResultItems;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilitiesResponse;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.AvailabilityItemResponse;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.Message;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultServiceUtils;


/**
 *
 */
@UnitTest
public class DefaultATPResultHandlerTest
{
	private static final Date ATP_DATE_1 = new Date(System.currentTimeMillis());
	private static final Date ATP_DATE_2 = new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000);
	private static final String ARTICLE_1 = "Article1";
	private static final String ARTICLE_2 = "Article2";
	private static final Double ATP_QTY_1 = new Double(5.0);
	private static final Double ATP_QTY_2 = new Double(10.0);
	private static final String MESSAGE_TYPE_E = "E";
	private static final String MESSAGE_TESTING_ERROR = "Testing Error Message";

	private DefaultATPResultHandler classUnderTest;
	private ServiceUtils serviceUtils;


	@Before
	public void setup()
	{
		classUnderTest = new DefaultATPResultHandler();
		serviceUtils = new DefaultServiceUtils();

		classUnderTest.setServiceUtils(serviceUtils);
		Assert.assertNotNull(classUnderTest.getServiceUtils());
	}

	@Test
	public void extractATPAvailabilityFromATPResponseExceptionTest()
	{
		final ATPResponse atpResp = new ATPResponse();
		final MessagesList messages = new MessagesList();


		assertExceptionExtractATPAvailabilitesFromATPResponse(null);
		assertExceptionExtractATPAvailabilitesFromATPResponse(atpResp);


		atpResp.setAtpAvailabilities(null);
		assertExceptionExtractATPAvailabilitesFromATPResponse(atpResp);

		// Add a dummy error message to ATPResponse
		messages.setMessages(createDummyErrorMessage());
		atpResp.setMessages(messages);
		atpResp.setAtpAvailabilities(new AvailabilitiesResponse());
		assertExceptionExtractATPAvailabilitesFromATPResponse(atpResp);
	}

	@Test
	public void extractATPAvailabilityFromATPResponseTest()
	{
		// ATP response object
		final ATPResponse atpResp = new ATPResponse();


		final AvailabilitiesResponse availabilities = new AvailabilitiesResponse();
		final List<AvailabilityItemResponse> availList = new ArrayList<>();

		// ATP availability structure
		final AvailabilityItemResponse availItem = new AvailabilityItemResponse();
		availItem.setAtpDate(serviceUtils.formatDateToString(ATP_DATE_1));
		availItem.setQuantity(ATP_QTY_1.doubleValue());
		availList.add(availItem);

		availabilities.setAtpAvailabilities(availList);
		atpResp.setAtpAvailabilities(availabilities);

		Assert.assertNotNull(availabilities.toString());

		try
		{
			final List<ATPAvailability> atpResult = classUnderTest.extractATPAvailabilityFromATPResponse(atpResp);
			Assert.assertNotNull(atpResult);
			Assert.assertNotNull(atpResult.toString());

			Assert.assertEquals(serviceUtils.formatDateToString(ATP_DATE_1),
					serviceUtils.formatDateToString(atpResult.get(0).getAtpDate()));
			Assert.assertEquals(ATP_QTY_1.toString(), atpResult.get(0).getQuantity().toString());
		}
		catch (final ATPException e)
		{
			Assert.fail("No ATPException expected, but it was: " + e.getMessage());
		}
	}

	@Test
	public void extractATPAvailabilityFromATPResponseNoAvailabilityTest()
	{
		final ATPResponse atpResp = new ATPResponse();
		final AvailabilitiesResponse availabilityResponse = new AvailabilitiesResponse();
		atpResp.setAtpAvailabilities(availabilityResponse);

		final List<ATPAvailability> atpResult = classUnderTest.extractATPAvailabilityFromATPResponse(atpResp);
		Assert.assertNotNull(atpResult);
		Assert.assertNotNull(atpResult.toString());

		Assert.assertEquals(Double.toString(0), atpResult.get(0).getQuantity().toString());
	}

	@Test
	public void extractATPProductAvailabilityFromATPBatchResponseTest()
	{
		// ATP Batch response object
		final ATPBatchResponse atpBatchResp = new ATPBatchResponse();
		final ATPResultItems atpResultItems = new ATPResultItems();
		final List<ATPResultItem> atpResultItemsList = new ArrayList<>();
		final ATPResultItem atpResultItem1 = new ATPResultItem();
		final ATPResultItem atpResultItem2 = new ATPResultItem();

		// ATP availability structure
		final AvailabilitiesResponse availabilities = new AvailabilitiesResponse();
		final List<AvailabilityItemResponse> availList = new ArrayList<>();
		final AvailabilityItemResponse availItem1 = new AvailabilityItemResponse();
		availItem1.setAtpDate(serviceUtils.formatDateToString(ATP_DATE_1));
		availItem1.setQuantity(ATP_QTY_1.doubleValue());
		availList.add(availItem1);
		final AvailabilityItemResponse availItem2 = new AvailabilityItemResponse();
		availItem2.setAtpDate(serviceUtils.formatDateToString(ATP_DATE_2));
		availItem2.setQuantity(ATP_QTY_2.doubleValue());
		availList.add(availItem2);
		availabilities.setAtpAvailabilities(availList);

		atpResultItem1.setArticleId(ARTICLE_1);
		atpResultItem1.setAtpAvailabilities(availabilities);
		atpResultItemsList.add(atpResultItem1);
		atpResultItem2.setArticleId(ARTICLE_2);
		atpResultItem2.setAtpAvailabilities(availabilities);
		atpResultItemsList.add(atpResultItem2);
		atpResultItems.setAtpResultItemList(atpResultItemsList);
		atpBatchResp.setAtpResultItems(atpResultItems);

		try
		{
			final List<ATPProductAvailability> atpResult = classUnderTest
					.extractATPProductAvailabilityFromATPBatchResponse(atpBatchResp);
			Assert.assertNotNull(atpResult);
			Assert.assertNotNull(atpResult.toString());

			Assert.assertEquals(ARTICLE_1, atpResult.get(0).getArticleId());
			Assert.assertNull(atpResult.get(0).getSourceId());
			Assert.assertEquals(availList.size(), atpResult.get(0).getAvailabilityList().size());
			Assert.assertEquals(serviceUtils.formatDateToString(ATP_DATE_1),
					serviceUtils.formatDateToString(atpResult.get(0).getAvailabilityList().get(0).getAtpDate()));
			Assert.assertEquals(ATP_QTY_1.toString(), atpResult.get(0).getAvailabilityList().get(0).getQuantity().toString());
			Assert.assertEquals(serviceUtils.formatDateToString(ATP_DATE_2),
					serviceUtils.formatDateToString(atpResult.get(0).getAvailabilityList().get(1).getAtpDate()));
			Assert.assertEquals(ATP_QTY_2.toString(), atpResult.get(0).getAvailabilityList().get(1).getQuantity().toString());

			Assert.assertEquals(ARTICLE_2, atpResult.get(1).getArticleId());
			Assert.assertNull(atpResult.get(1).getSourceId());
			Assert.assertEquals(availList.size(), atpResult.get(1).getAvailabilityList().size());
			Assert.assertEquals(serviceUtils.formatDateToString(ATP_DATE_1),
					serviceUtils.formatDateToString(atpResult.get(1).getAvailabilityList().get(0).getAtpDate()));
			Assert.assertEquals(ATP_QTY_1.toString(), atpResult.get(1).getAvailabilityList().get(0).getQuantity().toString());
			Assert.assertEquals(serviceUtils.formatDateToString(ATP_DATE_2),
					serviceUtils.formatDateToString(atpResult.get(1).getAvailabilityList().get(1).getAtpDate()));
			Assert.assertEquals(ATP_QTY_2.toString(), atpResult.get(1).getAvailabilityList().get(1).getQuantity().toString());
		}
		catch (final ATPException e)
		{
			Assert.fail("No ATPException expected, but it was: " + e.getMessage());
		}
	}

	@Test
	public void extractATPProductAvailabilityFromATPBatchResponseExceptionTest()
	{
		final ATPBatchResponse atpBatchResp = new ATPBatchResponse();
		final MessagesList messages = new MessagesList();

		assertExceptionExtractATPProductAvailabilityFromATPBatchResponse(null);
		assertExceptionExtractATPProductAvailabilityFromATPBatchResponse(atpBatchResp);


		atpBatchResp.setAtpResultItems(null);
		assertExceptionExtractATPProductAvailabilityFromATPBatchResponse(atpBatchResp);

		// Add a dummy error message to ATPResponse
		final ATPResultItems atpResultItems = new ATPResultItems();
		final List<ATPResultItem> atpResultItemList = atpResultItems.getAtpResultItemList();
		atpResultItemList.add(new ATPResultItem());
		messages.setMessages(createDummyErrorMessage());
		atpBatchResp.setMessages(messages);
		atpBatchResp.setAtpResultItems(atpResultItems);
		assertExceptionExtractATPProductAvailabilityFromATPBatchResponse(atpBatchResp);
	}


	private void assertExceptionExtractATPAvailabilitesFromATPResponse(final ATPResponse atpResp)
	{
		try
		{
			classUnderTest.extractATPAvailabilityFromATPResponse(atpResp);
			Assert.fail("ATPException expected for this test case");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e);
			Assert.assertNotNull(e.getMessage());
		}
	}

	private void assertExceptionExtractATPProductAvailabilityFromATPBatchResponse(final ATPBatchResponse atpBatchResp)
	{
		try
		{
			classUnderTest.extractATPProductAvailabilityFromATPBatchResponse(atpBatchResp);
			Assert.fail("ATPException expected for this test case");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e);
			Assert.assertNotNull(e.getMessage());
		}
	}

	private List<Message> createDummyErrorMessage()
	{
		// Create a dummy error message
		final List<Message> errorMsgList = new ArrayList<>();
		final Message errorMsg = new Message();
		errorMsg.setMessageText(MESSAGE_TESTING_ERROR);
		errorMsg.setType(MESSAGE_TYPE_E);
		errorMsgList.add(errorMsg);
		return errorMsgList;
	}
}

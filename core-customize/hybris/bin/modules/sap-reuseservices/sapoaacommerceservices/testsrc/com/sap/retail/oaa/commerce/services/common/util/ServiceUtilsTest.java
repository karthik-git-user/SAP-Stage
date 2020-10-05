/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.util;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.Message;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;
import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultServiceUtils;


/**
 *
 */
@UnitTest
public class ServiceUtilsTest
{
	private static final Logger logger = Logger.getLogger(ServiceUtilsTest.class);
	private final DefaultServiceUtils serviceUtils = new DefaultServiceUtils();


	@Test
	public void convertGuidToBase64Test() throws DecoderException
	{
		final String uuidString = "7ade6a19-e073-49c5-8082-1af99c0873c6";
		final String expBase64String = "et5qGeBzScWAghr5nAhzxg==";

		final String base64 = serviceUtils.convertGuidToBase64(uuidString);

		Assert.assertEquals(expBase64String, base64);
	}

	@Test
	public void addLeadingZerosTest()
	{
		final String expProductCode = "000000000000000123";
		final String actProductCode = serviceUtils.addLeadingZeros("123");
		Assert.assertEquals(expProductCode, actProductCode);
	}

	@Test
	public void removeLeadingZerosTest()
	{
		serviceUtils.setLeadingZeroFormatter("%018d");

		final String productCode1 = "0000000123";
		Assert.assertEquals("123", serviceUtils.removeLeadingZeros(productCode1));

		final String productCode2 = "000456";
		Assert.assertEquals("456", serviceUtils.removeLeadingZeros(productCode2));

		final String productCode3 = "1000456";
		Assert.assertEquals("1000456", serviceUtils.removeLeadingZeros(productCode3));

		final String productCode4 = "001000456";
		Assert.assertEquals("1000456", serviceUtils.removeLeadingZeros(productCode4));
	}

	@Test
	public void formatStringtoIntTest()
	{
		final Integer expInt = Integer.valueOf(1000);
		final Integer actInt = serviceUtils.formatStringtoInt("1000");
		Assert.assertEquals(expInt, actInt);
	}

	@Test
	public void logMessageResponseAndCheckMessageType()
	{
		final MessagesList messages = new MessagesList();

		// Call with empty message body
		Assert.assertFalse(serviceUtils.logMessageResponseAndCheckMessageType(logger, null));
		Assert.assertFalse(serviceUtils.logMessageResponseAndCheckMessageType(logger, messages));

		// Check messages contains error message
		List<Message> msgList = new ArrayList<>();
		Message msg = new Message();
		msg.setMessageText("Error Message");
		msg.setType("E");
		msgList.add(msg);
		messages.setMessages(msgList);
		Assert.assertTrue(serviceUtils.logMessageResponseAndCheckMessageType(logger, messages));

		// Check messages contains no-error message
		msgList = new ArrayList<>();
		msg = new Message();
		msg.setMessageText("Info Message");
		msg.setType("I");
		msgList.add(msg);

		msg = new Message();
		msg.setMessageText("Warning");
		msg.setType("W");
		messages.setMessages(msgList);
		msgList.add(msg);

		Assert.assertNotNull(messages.toString());
		Assert.assertFalse(serviceUtils.logMessageResponseAndCheckMessageType(logger, messages));
	}

	@Test
	public void parseStringToDateTest()
	{
		serviceUtils.setDateFormat("dd.MM.yyyy");
		serviceUtils.setTimeZone("UTC");
		Assert.assertNotNull(serviceUtils.parseStringToDate("01.01.2015"));
		Assert.assertNull(serviceUtils.parseStringToDate("No Date String"));
	}
}

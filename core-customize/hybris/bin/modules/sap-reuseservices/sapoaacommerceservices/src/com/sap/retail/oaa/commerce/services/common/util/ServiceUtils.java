/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.util;

import java.util.Date;

import org.apache.commons.codec.DecoderException;
import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.response.MessagesList;


/**
 * REST Service Utility Class
 */
public interface ServiceUtils
{

	/**
	 * Converts the GUid from a CartModel to an Base64 encoded GUid
	 *
	 * @param cartGuid
	 * @throws DecoderException
	 */
	public String convertGuidToBase64(final String cartGuid) throws DecoderException;

	/**
	 * Adds the Leading zeros to Product Code
	 *
	 * @param productCode
	 * @return product Code with leading Zeros
	 */
	public String addLeadingZeros(String productCode);

	/**
	 * It converts a String to an Integer
	 *
	 * @param str
	 *           String which represents a number
	 * @return Integer representation of the String
	 */
	public Integer formatStringtoInt(final String str);

	/**
	 * Checks if the response messages has errors and logs the message
	 *
	 * @param logger
	 * @param messages
	 * @return true if the response contains error messages
	 */
	public boolean logMessageResponseAndCheckMessageType(final Logger logger, final MessagesList messages);

	/**
	 * Converts a date string into a date object
	 *
	 * @param dateAsString
	 * @return converted date
	 */
	public Date parseStringToDate(final String dateAsString);

	/**
	 * Converts a date into a date string
	 *
	 * @param date
	 * @return converted date as string
	 */
	public String formatDateToString(final Date date);

	/**
	 * Removes the leading zeros from the product code
	 *
	 * @param productCode
	 * @return product Code without leading Zeros
	 */
	public String removeLeadingZeros(final String productCode);

	/**
	 * @param itemNo
	 * @param guid
	 * @return externalId
	 */
	String createExternalIdForItem(String itemNo, String guid);

	/**
	 * @param externalId
	 * @return itemId
	 */
	public String extractItemIdFromExternalId(String externalId);
}

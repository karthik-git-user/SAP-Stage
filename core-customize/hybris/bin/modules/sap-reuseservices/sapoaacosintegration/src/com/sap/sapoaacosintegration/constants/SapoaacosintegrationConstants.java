/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.constants;

/**
 * Global class for all Sapoaacosintegration constants. You can add global constants for your extension into this class.
 */
public final class SapoaacosintegrationConstants extends GeneratedSapoaacosintegrationConstants //NOSONAR
{
	public static final String EXTENSIONNAME = "sapoaacosintegration";
	public static final String ABAP_TRUE = "X";
	public static final String SCP_COS_DESTINATIONID = "scpCOSServiceDestination";
	public static final String SOURCING_RESOURCE_PATH = "sourcing";
	public static final String RESERVATION_ID_STRING = "reservationId";

	public static final String COS_DESTINATIONID = "scpCOSServiceDestination";
	public static final String DESTINATION_NOT_FOUND_MESSAGE = "Provided destination was not found.";
	public static final String AVAILABILITY_RESOURCE_PATH = "availableToSell";
	public static final String COS_ATP_ERROR_MESSAGE = "Error when calling COS ATP web service.";
	public static final String SOURCING_RESPONSE_DATEFORMAT_PROPERTY_KEY = "sapoaacosintegration.sourcing.result.dateFormat";


	public static final String ERROR_WHEN_CALLING_RESERVATION_WEB_SERVICE = "Error when calling Cos reservation web service.";
	public static final String RESERVATION_EXPIRE_TIME_PROPERTY_KEY = "sapoaacosintegration.cosreservation.expiretime";
	public static final Integer RESERVATION_EXPIRE_TIME_FALLBACK_VALUE = 1800;
	public static final Integer RESERVATION_EXPIRE_TIME_FOR_ORDER = 604800;
	public static final String CART_ITEM_ID = "CartId";
	public static final String RESERVATION_RESOURCE_PATH = "reservations";
	public static final String SLASH = "/";


	public static final String COS_DOWN_MESSAGE = "COS service is not responding";
	public static final String SOURCE_TYPE_STORE = "STORE";
	public static final Integer UNIT_MAX_LENGTH = 3;


	public static final String COS_CAC_STRATEGY_ID = "sapcos_cacStrategyId";
	public static final String COS_CAS_STRATEGY_ID = "sapcos_casStrategyId";

	public static final String RESERVATION_STATUS_ORDER = "O";
	public static final String RESERVATION_STATUS_CART = "C";

	public static final String SOURCETYPE_THIRD_PARTY = "THIRD_PARTY";
	public static final String SOURCETYPE_STORE = "STORE";
	public static final String SOURCETYPE_DC = "DC";
	/**
	 * 
	 */
	public static final String RESPONSE_IS_NULL_OR_EMPTY = "Response is either null or empty";


	private SapoaacosintegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}




}
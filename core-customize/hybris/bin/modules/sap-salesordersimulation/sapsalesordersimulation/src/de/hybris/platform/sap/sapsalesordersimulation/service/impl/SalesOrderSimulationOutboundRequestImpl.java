/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.apiregistryservices.model.BasicCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.sap.sapsalesordersimulation.exception.SAPBackEndRuntimeException;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationOutboundRequest;
import de.hybris.platform.servicelayer.session.SessionService;

public class SalesOrderSimulationOutboundRequestImpl implements SalesOrderSimulationOutboundRequest {
	private static final String HTML_TAG = "<html>";
	private static final String COOKIE = "cookie";
	private static final String SET_COOKIE = "set-cookie";
	private static final String BASIC = "Basic ";
	private static final String TOKEN_AND_COOKIES = "tokenAndCookies";
	private static final String ACCEPT = "Accept";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String POST = "POST";
	private static final String GET = "GET";
	private static final String X_CSRF_TOKEN = "X-CSRF-Token";
	private static final String X_CSRF_TOKEN_FETCH = "Fetch";
	private static final String OUTBOUND_SIMULATE_SALES_ORDER_DESTINATION = "sapOrderSimulateDestination";
	private static final String METADATA = "$metadata";
	private static final String SALESORDERSIMULATE = "A_SalesOrderSimulation";
	private static final String AUTHORIZATION = "Authorization";
	private static final String COOKIES = "cookies";

	private static final Logger LOG = LoggerFactory.getLogger(SalesOrderSimulationOutboundRequestImpl.class);
	private DestinationService<ConsumedDestinationModel> destinationService;
	private SessionService sessionService;
	
	@Override
	public String getResponseFromSalesOrderSimulate(String bodyPayload) {
		final ConsumedDestinationModel destinationModel = getDestinationService()
				.getDestinationById(OUTBOUND_SIMULATE_SALES_ORDER_DESTINATION);
		
		
		if (destinationModel != null && destinationModel.getUrl() != null) {
			boolean freshTokenRecieved = false;
			Map<String, String>  tokenAndCookies = getSessionService().getAttribute(TOKEN_AND_COOKIES);
			if (tokenAndCookies == null || tokenAndCookies.isEmpty()) {
				tokenAndCookies = fetchCsrfToken(destinationModel);
				getSessionService().setAttribute(TOKEN_AND_COOKIES,tokenAndCookies);
				freshTokenRecieved = true;
			}
			String responseFromSalesOrderSimulate = getResponseFromOrderSimulate(destinationModel.getUrl() + SALESORDERSIMULATE, tokenAndCookies,
					bodyPayload);
			// there could be possibility of token getting expired and try again with the fresh token
			if ((StringUtils.isEmpty(responseFromSalesOrderSimulate)|| responseFromSalesOrderSimulate.contains(HTML_TAG) ) && !freshTokenRecieved ) {
				LOG.info("The CSRF token expired and fetching new token");
				tokenAndCookies = fetchCsrfToken(destinationModel);
				getSessionService().setAttribute(TOKEN_AND_COOKIES,tokenAndCookies);
				responseFromSalesOrderSimulate = getResponseFromOrderSimulate(destinationModel.getUrl() + SALESORDERSIMULATE, tokenAndCookies,
						bodyPayload);
			}
			return responseFromSalesOrderSimulate;
		} else {
			LOG.error("No Destination model found. Please create  consumed destination model with  destination id 'sapOrderSimulateDestination' ");
			return "";
		}

	}
	
	

	private Map<String, String>  fetchCsrfToken(ConsumedDestinationModel destinationModel) {
		

		BasicCredentialModel basicCredentialModel = (BasicCredentialModel) destinationModel.getCredential();
		final String basicCredentials = basicCredentialModel.getUsername() + ":" + basicCredentialModel.getPassword();
		String basicAuthPayload = BASIC + Base64.getEncoder().encodeToString(basicCredentials.getBytes());
		return getTokenAndCookies(basicAuthPayload, destinationModel.getUrl() + METADATA);

	}

	private Map<String, String> getTokenAndCookies(String basicAuthPayload, String url) {
		HttpURLConnection urlConnection = null;
		Map<String, String> tokenAndCookies = new HashMap<>();
		try {

			URL serverUrl = new URL(url);
			urlConnection = (HttpURLConnection) serverUrl.openConnection();
			urlConnection.setRequestMethod(GET);
			urlConnection.addRequestProperty(AUTHORIZATION, basicAuthPayload);
			urlConnection.addRequestProperty(X_CSRF_TOKEN, X_CSRF_TOKEN_FETCH);
			int status = urlConnection.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				tokenAndCookies.put(X_CSRF_TOKEN, urlConnection.getHeaderField(X_CSRF_TOKEN));
			}
			String cookies = getCookies(urlConnection);
			if (cookies != null) {
				tokenAndCookies.put(COOKIES, cookies);
			}
			LOG.debug("CSRF token fetched for the url ");
		} catch (IOException ioe) {
			LOG.error("Unable to fetch the CSRF token!",ioe);
			throw new SAPBackEndRuntimeException(ioe);
		}
		return tokenAndCookies;
	}

	private final String getCookies(HttpURLConnection conn) {
		Map<String, List<String>> headers = conn.getHeaderFields();
		Iterator<String> keys = headers.keySet().iterator();
		String key;
		while (keys.hasNext()) {
			key = keys.next();
			if (SET_COOKIE.equalsIgnoreCase(key)) {
				StringBuilder cookiesString = new StringBuilder();
				for (String cookie :headers.get(key)) {
					cookiesString.append((String)cookie).append("; ");
				}
				return cookiesString.toString();

			}
		}
		return "";
	}

	private String getResponseFromOrderSimulate(String url, Map<String,String> tokenCookiesMap, String requestBody) {
		HttpURLConnection postURLConnection = null;
		URL serverUrl = null;
		BufferedReader httpResponseReader = null;
		String json = "";
		try {
			serverUrl = new URL(url);
			postURLConnection = (HttpURLConnection) serverUrl.openConnection();
			postURLConnection.setRequestMethod(POST);
			if (tokenCookiesMap.get(X_CSRF_TOKEN) != null) {
				postURLConnection.addRequestProperty(X_CSRF_TOKEN, tokenCookiesMap.get(X_CSRF_TOKEN));
				postURLConnection.addRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
				postURLConnection.addRequestProperty(ACCEPT, APPLICATION_JSON);
				postURLConnection.setDoOutput(true);
				postURLConnection.setDoInput(true);
				postURLConnection.setRequestProperty(COOKIE, tokenCookiesMap.get(COOKIES));
				OutputStreamWriter wr = new OutputStreamWriter(postURLConnection.getOutputStream());
				wr.write(requestBody);
				wr.close();
				httpResponseReader = new BufferedReader(new InputStreamReader(postURLConnection.getInputStream()));
				String lineRead;
				while ((lineRead = httpResponseReader.readLine()) != null) {
					json = lineRead;
				}
			}

		} catch (IOException ioe) {
			
			LOG.error("Error calling the sales order simulation api ...",ioe);
		} finally {
			try {
				if (httpResponseReader != null) {
					httpResponseReader.close();
				}
			} catch (IOException e) {
				LOG.error("Error calling the sales order simulation api ...",e);
				
			}
		}
		return json;
	}





	protected DestinationService<ConsumedDestinationModel> getDestinationService() {
		return destinationService;
	}

	public void setDestinationService(DestinationService<ConsumedDestinationModel> destinationService) {
		this.destinationService = destinationService;
	}

	protected SessionService getSessionService() {
		return sessionService;
	}

	public void setSessionService(SessionService sessionService) {
		this.sessionService = sessionService;
	}
	
	 
	
}

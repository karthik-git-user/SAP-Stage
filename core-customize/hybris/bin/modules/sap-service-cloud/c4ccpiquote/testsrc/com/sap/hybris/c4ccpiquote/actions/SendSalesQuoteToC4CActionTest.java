/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4ccpiquote.actions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Maps;
import com.sap.hybris.c4ccpiquote.model.C4CSalesOrderNotificationModel;
import com.sap.hybris.c4ccpiquote.outbound.actions.SendSalesQuoteToC4CAction;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteConversionService;
import com.sap.hybris.c4ccpiquote.outbound.service.SapCpiOutboundC4CQuoteService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import rx.Observable;


/**
 *
 */
@UnitTest
public class SendSalesQuoteToC4CActionTest
{
	private static final Logger LOG = Logger.getLogger(SendSalesQuoteToC4CActionTest.class);

	private String code = "12345";
	@InjectMocks
	private final SendSalesQuoteToC4CAction action = new SendSalesQuoteToC4CAction();
	@Mock
	private SapCpiOutboundC4CQuoteService sapCpiOutboundC4CQuoteService;
	@Mock
	private QuoteService quoteService;
	@Mock
	private SapCpiOutboundC4CQuoteConversionService sapCpiOutboundC4CQuoteConversinService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteActionWithQuoteCode()
	{
		final QuoteProcessModel process = new QuoteProcessModel();
		process.setQuoteCode(code);

		final QuoteModel quote = mock(QuoteModel.class);
		when(quote.getCode()).thenReturn(code);
		when(quote.getQuoteOrderId()).thenReturn(code);
		final C4CSalesOrderNotificationModel c4CSalesOrderNotificationModel = mock(C4CSalesOrderNotificationModel.class);
		
		
		when(quoteService.getCurrentQuoteForCode(Mockito.anyString())).thenReturn(quote);
		when(sapCpiOutboundC4CQuoteConversinService.convertQuoteToSalesOrderNotification(quote)).thenReturn(c4CSalesOrderNotificationModel);

		final Map<String, Map> map = Maps.newHashMap();
	    final Map<String, String> innerMap = Maps.newHashMap();
	    innerMap.put("responseMessage", "The order has been sent successfully to C4C through SCPI!");
	    innerMap.put("responseStatus", "Success");
	    map.put("C4CSalesOrderNotification", innerMap);
		ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(map, HttpStatus.OK);
		
		
		when(sapCpiOutboundC4CQuoteService.sendOrderNotification(c4CSalesOrderNotificationModel)).thenReturn(Observable.just(objectResponseEntity));
		Transition result;
		try {
			result = action.executeAction(process);
			Assert.assertEquals(Transition.OK, result);
			
		} catch (Exception e) {
			
			LOG.info(e);
			Assert.fail("Exception Occured");
		}

	}

	@Test
	public void testExecuteActionWithOutQuoteCode()
	{
		final QuoteProcessModel process = new QuoteProcessModel();

		Transition result;
		try {
			result = action.executeAction(process);
			Assert.assertEquals(Transition.NOK, result);
			
		} catch (Exception e) {
			
			LOG.info(e);
			Assert.fail("Exception Occured");
		}
	}

}

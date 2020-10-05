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
package com.sap.hybris.sapquoteintegration.outbound.actions;

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
import com.sap.hybris.sapquoteintegration.model.SAPCpiOutboundQuoteModel;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteConversionService;
import com.sap.hybris.sapquoteintegration.outbound.service.SapCpiOutboundQuoteService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.enums.ExportStatus;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import rx.Observable;

import com.sap.hybris.sapquoteintegration.outbound.actions.SapCpiSendQuoteAction;

@UnitTest
public class SapCpiSendQuoteActionTest {
	
	private static final Logger LOG = Logger.getLogger(SapCpiSendQuoteActionTest.class);
	
	private String code = "12345";
	
	@InjectMocks
	private final SapCpiSendQuoteAction action = new SapCpiSendQuoteAction();
	@Mock
	private SapCpiOutboundQuoteConversionService quoteConversionService;
	@Mock
	private QuoteService quoteService;
	@Mock
	private SapCpiOutboundQuoteService sapCpiOutboundQuoteService;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteActionWithQuoteCode() {
		
		final QuoteProcessModel process = new QuoteProcessModel();
		process.setQuoteCode(code);

		final QuoteModel quote = mock(QuoteModel.class);
		when(quote.getCode()).thenReturn(code);
		final SAPCpiOutboundQuoteModel sAPCpiOutboundQuoteModel = mock(SAPCpiOutboundQuoteModel.class);

		when(quoteService.getCurrentQuoteForCode(Mockito.anyString())).thenReturn(quote);
		when(quoteConversionService.convertQuoteToSapCpiQuote(quote))
				.thenReturn(sAPCpiOutboundQuoteModel);

		final Map<String, Map> map = Maps.newHashMap();
	    final Map<String, String> innerMap = Maps.newHashMap();
	    innerMap.put("responseMessage", "The quote has been sent successfully to C4C through SCPI!");
	    innerMap.put("responseStatus", "Success");
	    map.put("SAPC4CCpiOutboundQuote", innerMap);
		ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(map, HttpStatus.OK);
		
		when(quote.getExportStatus()).thenReturn(ExportStatus.EXPORTED);
		when(sapCpiOutboundQuoteService.sendQuote(sAPCpiOutboundQuoteModel)).thenReturn(Observable.just(objectResponseEntity));
		
		Transition result;
		try {
			result = action.executeAction(process);
			Assert.assertEquals(Transition.OK, result);
			
		} catch (Exception e) {
			
			LOG.info(e);
		}

	}

	@Test
	public void testExecuteActionWithOutQuoteCode() {
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

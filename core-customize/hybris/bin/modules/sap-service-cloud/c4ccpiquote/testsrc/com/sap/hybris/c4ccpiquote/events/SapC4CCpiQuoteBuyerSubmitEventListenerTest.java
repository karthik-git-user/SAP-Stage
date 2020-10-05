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
package com.sap.hybris.c4ccpiquote.events;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class SapC4CCpiQuoteBuyerSubmitEventListenerTest
{

	private static final Logger LOG = Logger.getLogger(SapC4CCpiQuoteBuyerSubmitEventListenerTest.class);

	@InjectMocks
	private final SapC4CCpiQuoteBuyerSubmitEventListener listner = new SapC4CCpiQuoteBuyerSubmitEventListener();
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOnEvent()
	{
		final SapC4CCpiQuoteBuyerSubmitEvent event = mock(SapC4CCpiQuoteBuyerSubmitEvent.class);

		final QuoteUserType quoteUserType = mock(QuoteUserType.class);
		when(event.getQuoteUserType()).thenReturn(quoteUserType);

		final QuoteModel quote = mock(QuoteModel.class);
		when(event.getQuote()).thenReturn(quote);
		when(quote.getCode()).thenReturn("DummyCode");

		final QuoteProcessModel quoteBuyerProcessModel = mock(QuoteProcessModel.class);
		when(businessProcessService.createProcess(anyString(), anyString(), any(Map.class))).thenReturn(quoteBuyerProcessModel);

		final BaseStoreModel store = mock(BaseStoreModel.class);
		when(quote.getStore()).thenReturn(store);
		when(store.getUid()).thenReturn("DummyStoreUID");
		try
		{
			listner.onEvent(event);
		}
		catch (final Exception e)
		{
			LOG.info(e);
			Assert.fail("Exception Occured");
		}
	}


}

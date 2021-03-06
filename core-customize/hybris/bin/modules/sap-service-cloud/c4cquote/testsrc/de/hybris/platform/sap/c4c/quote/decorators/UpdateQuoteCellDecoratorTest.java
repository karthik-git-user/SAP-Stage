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
package de.hybris.platform.sap.c4c.quote.decorators;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteHelper;
import de.hybris.platform.sap.c4c.quote.inbound.InboundQuoteVersionControlHelper;

/**
 * JUnit for @UpdateQuoteCellDecorator
 */
@UnitTest
public class UpdateQuoteCellDecoratorTest {
	private static final String VERSION_INPUT = "620001701";
	private static final int VERSION_POSITION_INT = 1;
	private static final int VERSION_OUTPUT = 2;
	private static final Integer VERSION_POSITION = Integer.valueOf(VERSION_POSITION_INT);

	@InjectMocks
	private final UpdateQuoteCellDecorator decorator = new UpdateQuoteCellDecorator();

	@Mock
	private InboundQuoteVersionControlHelper inboundQuoteVersionControlHelper;

	@Mock
	private QuoteModel quoteModel;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void decorateTest() {
		final Map<Integer, String> srcLine = new HashMap<Integer, String>();
		srcLine.put(VERSION_POSITION, VERSION_INPUT);
		when(inboundQuoteVersionControlHelper.getQuoteforCode(VERSION_INPUT)).thenReturn(quoteModel);
		when(quoteModel.getVersion()).thenReturn(new Integer(2));
		String resultVersion = decorator.decorate(VERSION_POSITION_INT, srcLine);
		Assert.assertEquals(Integer.toString(VERSION_OUTPUT), resultVersion);
	}

}

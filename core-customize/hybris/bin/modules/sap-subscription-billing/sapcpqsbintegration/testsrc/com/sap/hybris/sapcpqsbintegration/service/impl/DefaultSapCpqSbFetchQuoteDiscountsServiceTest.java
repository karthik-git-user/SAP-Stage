package com.sap.hybris.sapcpqsbintegration.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqSbFetchQuoteDiscountsServiceTest {
	
	@Mock
	FlexibleSearchService mockFlexibleSearchService;
	
	@InjectMocks
	DefaultSapCpqSbFetchQuoteDiscountsService defaultSapCpqSbFetchQuoteDiscountsService;
	
	private static final String QUOTE_ID = "QUOTE_ID";
	private final QuoteModel quoteModel = new QuoteModel();
	
	@Before
	public void setUp() {
		when(mockFlexibleSearchService.searchUnique(any())).thenReturn(quoteModel);
	}

	@Test
	public void getCurrentQuoteVendorDiscountsTest() {
		QuoteModel result;
		result = defaultSapCpqSbFetchQuoteDiscountsService.getCurrentQuoteVendorDiscounts(QUOTE_ID);
		Assert.assertNotNull(result);
	}
}

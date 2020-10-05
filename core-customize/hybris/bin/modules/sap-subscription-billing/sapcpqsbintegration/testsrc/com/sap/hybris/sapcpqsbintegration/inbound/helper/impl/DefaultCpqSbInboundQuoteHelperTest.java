package com.sap.hybris.sapcpqsbintegration.inbound.helper.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpqsbintegration.constants.SapcpqsbintegrationConstants;
import com.sap.hybris.sapcpqsbintegration.model.CpqPricingParameterModel;
import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;

import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;


@RunWith(MockitoJUnitRunner.class)
public class DefaultCpqSbInboundQuoteHelperTest {
	
	@InjectMocks
	DefaultCpqSbInboundQuoteHelper defaultCpqSbInboundQuoteHelper;
	
	private final QuoteModel inboundQuote = new QuoteModel();
	private final List<QuoteEntryModel> quoteEntryModelList = new ArrayList<>();
	private final QuoteEntryModel quoteEntryModel = new QuoteEntryModel();
	private final List<CpqSubscriptionDetailModel> cpqSubscriptionDetailModelList =  new ArrayList<>();
	private final CpqSubscriptionDetailModel cpqSubscriptionDetailModel = new CpqSubscriptionDetailModel();
	private final List<CpqPricingParameterModel> cpqPricingParameterModelList =  new ArrayList<>();
	private final CpqPricingParameterModel cpqPricingParameterModel = new CpqPricingParameterModel();
	
	@Before
	public void setUp() {
		cpqPricingParameterModelList.add(cpqPricingParameterModel);
		cpqSubscriptionDetailModel.setPricingParameters(cpqPricingParameterModelList);
		cpqSubscriptionDetailModel.setItemId("ITEM_ID");
		cpqSubscriptionDetailModelList.add(cpqSubscriptionDetailModel);
		quoteEntryModel.setCpqSubscriptionDetails(cpqSubscriptionDetailModelList);
		quoteEntryModel.setProductTypeName(SapcpqsbintegrationConstants.SUBSCRIPTION_TYPE);
		quoteEntryModelList.add(quoteEntryModel);
		inboundQuote.setCpqQuoteEntries(quoteEntryModelList);
	}
	
	@Test
	public void processInboundQuoteTest() {
		QuoteModel resultInboundQuote;
		resultInboundQuote = defaultCpqSbInboundQuoteHelper.processInboundQuote(inboundQuote);
		Assert.assertNotNull(resultInboundQuote);
		Assert.assertNotNull(resultInboundQuote.getCpqQuoteEntries());
	}

}

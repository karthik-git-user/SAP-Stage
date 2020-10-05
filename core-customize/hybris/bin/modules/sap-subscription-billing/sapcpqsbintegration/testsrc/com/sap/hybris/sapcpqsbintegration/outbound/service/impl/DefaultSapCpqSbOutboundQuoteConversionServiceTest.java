package com.sap.hybris.sapcpqsbintegration.outbound.service.impl;

import java.util.ArrayList;
import java.util.Date;
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
import com.sap.hybris.sapcpqsbintegration.model.SubscriptionPricingOutboundModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqSbOutboundQuoteConversionServiceTest {

	@InjectMocks
	DefaultSapCpqSbOutboundQuoteConversionService defaultSapCpqSbOutboundQuoteConversionService;
	
	private final QuoteModel quoteModel = new QuoteModel();
	private final List<QuoteEntryModel> quoteEntryModelList = new ArrayList<>();
	private final QuoteEntryModel quoteEntryModel = new QuoteEntryModel();
	private final List<CpqSubscriptionDetailModel> cpqSubscriptionDetailModelList = new ArrayList<>();
	private final CpqSubscriptionDetailModel cpqSubscriptionDetailModel = new CpqSubscriptionDetailModel();
	private final List<CpqPricingParameterModel> cpqPricingParameterModelList = new ArrayList<>();
	
	@Before
	public void setUp() {
		//CpqSubscriptionDetailModel
		cpqSubscriptionDetailModel.setRatePlanId("RATE_PLAN_ID");
		cpqSubscriptionDetailModel.setEffectiveDate(new Date());
		cpqSubscriptionDetailModel.setPricingParameters(cpqPricingParameterModelList);
		
		cpqSubscriptionDetailModelList.add(cpqSubscriptionDetailModel);
		
		//QuoteEntryModel
		quoteEntryModel.setProductTypeName(SapcpqsbintegrationConstants.SUBSCRIPTION_TYPE);
		quoteEntryModel.setCpqSubscriptionDetails(cpqSubscriptionDetailModelList);
		quoteEntryModel.setCpqExternalQuoteEntryId("QUOTE_ENTRY_ID");
		quoteEntryModel.setEntryNumber(10);
		
		
		quoteEntryModelList.add(quoteEntryModel);
		
		//QuoteModel
		quoteModel.setCode("QUOTE_MODEL_CODE");
		quoteModel.setVersion(1);
		quoteModel.setCpqQuoteEntries(quoteEntryModelList);
	}
	
	@Test
	public void createSubscriptionPricingRequestTest() {
		SubscriptionPricingOutboundModel result;
		
		//Execute
		result = defaultSapCpqSbOutboundQuoteConversionService.createSubscriptionPricingRequest(quoteModel);
		
		//Verify
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getRequests());
		Assert.assertNotNull(result.getRequests().get(0));
		Assert.assertNotNull(result.getRequests().get(0).getPricingParameters());
		Assert.assertNotNull(result.getRequests().get(0).getEffectiveAt());
		Assert.assertNotNull(result.getRequests().get(0).getUrl());
		Assert.assertNotNull(result.getRequests().get(0).getId());
		Assert.assertEquals("POST" , result.getRequests().get(0).getMethod());
		Assert.assertEquals(cpqSubscriptionDetailModel.getRatePlanId(), result.getRequests().get(0).getRatePlanId());
	}
}


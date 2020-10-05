package com.sap.hybris.sapcpqsbintegration.outbound.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.sap.hybris.sapcpqsbintegration.model.SubscriptionPricingOutboundModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import rx.Observable;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqSbOutboundQuoteServiceTest {
	
	@Mock
	OutboundServiceFacade outboundServiceFacade;
	
	@Mock
	Observable<ResponseEntity<Map>> mockResponseEntityObservable;
	
	@InjectMocks
	DefaultSapCpqSbOutboundQuoteService defaultSapCpqSbOutboundQuoteService;
	
	private final SubscriptionPricingOutboundModel subscriptionPricingOutboundModel = new SubscriptionPricingOutboundModel(); 
	
	
	@Before
	public void setUp() {
		when(outboundServiceFacade.send(any(), any(), any())).thenReturn(mockResponseEntityObservable);
	}
	
	
	@Test
	public void requestSubscriptionPricingTest() {
		Observable<ResponseEntity<Map>> result = defaultSapCpqSbOutboundQuoteService.requestSubscriptionPricing(subscriptionPricingOutboundModel);
		Assert.assertNotNull(result);
	}

}

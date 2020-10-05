/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sap.ppengine.client.dto.PriceCalculate;
import com.sap.ppengine.client.dto.PriceCalculateResponse;
import com.sap.ppengine.client.impl.PromotionPricingServiceInternal;
import com.sap.ppengine.client.service.PromotionPricingService;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;


@UnitTest
public class DefaultPPSClientTest
{
	private static final String MY_STORE = "myStore";

	private class DefaultPPSClientForTest extends DefaultPPSClient
	{
		@Override
		public PromotionPricingService getPromotionPricingService()
		{
			return pricingPromotionServiceMock;
		}

	}

	private DefaultPPSClient cut;
	@Mock
	private BaseStoreService baseStoreService;
	@Mock
	private HttpMessageConverter<?> jsonConverter;
	@Mock
	private PromotionPricingServiceInternal pricingPromotionServiceMock;
	@Mock
	private RestTemplate restTemplateMock;
	@Mock
	private PPSConfigService configService;

	private BaseStoreModel baseStoreModel;
	private SAPHTTPDestinationModel httpDestination;
	private SAPConfigurationModel sapConfig;

	@Before
	public void setUp()
	{
		httpDestination = new SAPHTTPDestinationModel();
		MockitoAnnotations.initMocks(this);
		baseStoreModel = new BaseStoreModel();
		sapConfig = new SAPConfigurationModel();
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
		sapConfig.setSappps_active(Boolean.TRUE);
		cut = new DefaultPPSClientForTest();
		httpDestination.setTargetURL("myUri");
		cut.setBaseStoreService(baseStoreService);
		baseStoreModel.setUid(MY_STORE);
	}

	@Test
	public void testSetGetBaseStoreService()
	{
		cut = new DefaultPPSClientForTest();
		assertNull(cut.getBaseStoreService());
		cut.setBaseStoreService(baseStoreService);
		assertSame(baseStoreService, cut.getBaseStoreService());
	}

	@Test
	public void testCallPPSRemote()
	{
		final PriceCalculate priceCalculate = new PriceCalculate();
		final PriceCalculateResponse body = new PriceCalculateResponse();
		final MultiValueMap<String, String> headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, "returnedToken");
		final ResponseEntity<PriceCalculateResponse> expectedResponse = new ResponseEntity<PriceCalculateResponse>(body, headers,
				HttpStatus.ACCEPTED);
		Mockito.when(
				restTemplateMock.exchange(Mockito.eq("myUri"), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
						Mockito.eq(PriceCalculateResponse.class))).thenReturn(expectedResponse);

		assertSame(body, cut.callPPS(priceCalculate, sapConfig));
	}

	@Test(expected = SapPPSPricingRuntimeException.class)
	public void testCallPPSRemoteNoSuccess()
	{
		final PriceCalculate priceCalculate = new PriceCalculate();
		final PriceCalculateResponse body = new PriceCalculateResponse();
		final ResponseEntity<PriceCalculateResponse> expectedResponse = new ResponseEntity<PriceCalculateResponse>(body,
				HttpStatus.FORBIDDEN);

		Mockito.when(
				restTemplateMock.exchange(Mockito.eq("myUri"), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
						Mockito.eq(PriceCalculateResponse.class))).thenReturn(expectedResponse);

		cut.callPPS(priceCalculate, sapConfig);
		fail("Exception expected");
	}

	@Test
	public void testCallPPSRemoteException() throws Exception
	{
		final PriceCalculate priceCalculate = new PriceCalculate();

		final RuntimeException exception = new RestClientException("mist!");
		Mockito.when(
				restTemplateMock.exchange(Mockito.eq("myUri"), Mockito.eq(HttpMethod.POST), Mockito.any(HttpEntity.class),
						Mockito.eq(PriceCalculateResponse.class))).thenThrow(exception);

		try
		{
			cut.callPPS(priceCalculate, sapConfig);
		}
		catch (final Exception e)
		{
			assertSame(exception, e);
		}
	}

}

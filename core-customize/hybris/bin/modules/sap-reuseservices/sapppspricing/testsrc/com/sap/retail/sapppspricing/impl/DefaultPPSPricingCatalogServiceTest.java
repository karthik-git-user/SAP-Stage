/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.NetGrossStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.PricingBackend;
import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;
import com.sap.retail.sapppspricing.impl.DefaultPPSPricingCatalogService;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPPSPricingCatalogServiceTest
{
	private DefaultPPSPricingCatalogService cut;

	@Mock
	private PricingBackend pricingBackendMock;
	@Mock
	private NetGrossStrategy netGrossStrategyMock;
	@Mock
	private PPSConfigService configService;

	private ProductModel prod;

	@Before
	public void setUp()
	{
		cut = new DefaultPPSPricingCatalogService();
		prod = new ProductModel();
		MockitoAnnotations.initMocks(this);
		Mockito.when(netGrossStrategyMock.isNet()).thenReturn(false);
		Mockito.when(configService.isPpsActive(prod)).thenReturn(Boolean.TRUE);
		cut.setConfigService(configService);
		cut.setNetGrossStrategy(netGrossStrategyMock);
		cut.setPricingBackend(pricingBackendMock);
	}


	@Test
	public void testSetGetBaseConfigService()
	{
		cut = new DefaultPPSPricingCatalogService();
		assertNull(cut.getConfigService());
		cut.setConfigService(configService);
		assertSame(configService, cut.getConfigService());
	}


	@Test
	public void testSetGetPricingBackend() throws Exception
	{
		cut = new DefaultPPSPricingCatalogService();
		assertNull(cut.getPricingBackend());
		cut.setPricingBackend(pricingBackendMock);
		assertSame(pricingBackendMock, cut.getPricingBackend());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCallPPSSuccess() throws Exception
	{
		final List<PriceInformation> stub = Collections.emptyList();
		Mockito.when(pricingBackendMock.readPriceInformationForProducts(Mockito.any(List.class), Mockito.eq(false))).thenReturn(
				stub);
		final List<PriceInformation> result = cut.getPriceInformationsForProduct(prod);
		assertSame(stub, result);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = SapPPSPricingRuntimeException.class)
	public void testCallPPSFailure() throws Exception
	{
		final List<PriceInformation> stub = Collections.emptyList();
		Mockito.when(pricingBackendMock.readPriceInformationForProducts(Mockito.any(List.class), Mockito.eq(false))).thenThrow(
				new RestClientException(""));
		final List<PriceInformation> result = cut.getPriceInformationsForProduct(prod);
		assertSame(stub, result);
	}
}

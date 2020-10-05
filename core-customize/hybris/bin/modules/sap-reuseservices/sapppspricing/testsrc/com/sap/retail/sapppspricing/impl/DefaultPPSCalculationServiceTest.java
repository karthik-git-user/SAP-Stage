/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.FindDeliveryCostStrategy;
import de.hybris.platform.order.strategies.calculation.FindPaymentCostStrategy;
import de.hybris.platform.order.strategies.calculation.FindPriceStrategy;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.web.client.RestClientException;

import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.PricingBackend;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPPSCalculationServiceTest
{

	private DefaultPPSCalculationService cut;

	@Mock
	private PricingBackend pricingBackendMock;
	@Mock
	private CommonI18NService commonI18NServiceMock;
	@Mock
	private OrderRequiresCalculationStrategy strategyMock;
	@Mock
	private ModelService modelServiceMock;
	@Mock
	private FindDeliveryCostStrategy deliveryCostMock;
	@Mock
	private FindPaymentCostStrategy paymentCostMock;
	@Mock
	private FindPriceStrategy priceMock;
	@Mock
	private PPSConfigService configService;

	private AbstractOrderModel order;

	private class CartEntryModelForTest extends CartEntryModel
	{
		private List<DiscountValue> discountValues;
		private Collection<TaxValue> taxValues = Collections.emptyList();

		@Override
		public void setDiscountValues(final List<DiscountValue> values)
		{
			this.discountValues = values;
		}

		@Override
		public List<DiscountValue> getDiscountValues()
		{
			return this.discountValues;
		}

		@Override
		public void setTaxValues(final Collection<TaxValue> values)
		{
			this.taxValues = values;
		}

		@Override
		public Collection<TaxValue> getTaxValues()
		{
			return this.taxValues;
		}
	}

	private static class RoundAnswer implements Answer<Double>
	{
		@Override
		public Double answer(final InvocationOnMock invocation) throws Throwable
		{
			return (Double) invocation.getArguments()[0];
		}
	}

	@Before
	public void setUp()
	{
		cut = new DefaultPPSCalculationService();
		order = new CartModel();
		MockitoAnnotations.initMocks(this);
		Mockito.when(configService.isPpsActive(order)).thenReturn(Boolean.TRUE);
		cut.setConfigService(configService);
		cut.setModelService(modelServiceMock);
		cut.setFindPaymentCostStrategy(paymentCostMock);
		cut.setFindDeliveryCostStrategy(deliveryCostMock);
		cut.setFindDiscountsStrategies(Collections.emptyList());
		cut.setFindPriceStrategy(priceMock);
		cut.setFindTaxesStrategies(Collections.emptyList());
		cut.setCommonI18NService(commonI18NServiceMock);
		cut.setPricingBackend(pricingBackendMock);
		cut.setOrderRequiresCalculationStrategy(strategyMock);

		Mockito.when(deliveryCostMock.getDeliveryCost(order)).thenReturn(new PriceValue("EUR", 3.2, false));
		Mockito.when(paymentCostMock.getPaymentCost(order)).thenReturn(new PriceValue("EUR", 0.2, false));
		final CartEntryModel entry = new CartEntryModelForTest();
		entry.setQuantity(1L);
		final ProductModel productModel = new ProductModel();
		entry.setProduct(productModel);
		entry.setOrder(order);
		order.setNet(Boolean.FALSE);
		order.setEntries(Collections.singletonList(entry));
		cut.setFindTaxesStrategies(Collections.emptyList());
		final CurrencyModel currencyModel = new CurrencyModel();
		currencyModel.setIsocode("EUR");
		currencyModel.setDigits(Integer.valueOf(2));
		Mockito.when(commonI18NServiceMock.getCurrentCurrency()).thenReturn(currencyModel);
		final RoundAnswer roundAnswer = new RoundAnswer();
		Mockito.when(commonI18NServiceMock.roundCurrency(Mockito.any(Double.class), Mockito.any(Integer.class))).thenAnswer(
				roundAnswer);
		Mockito.when(strategyMock.requiresCalculation(order)).thenReturn(Boolean.TRUE);
	}


	@Test
	public void testSetGetBaseConfigService()
	{
		cut = new DefaultPPSCalculationService();
		assertNull(cut.getConfigService());
		cut.setConfigService(configService);
		assertSame(configService, cut.getConfigService());
	}


	@Test
	public void testSetGetI18NService() throws Exception
	{
		cut = new DefaultPPSCalculationService();
		assertNull(cut.getCommonI18NService());
		cut.setCommonI18NService(commonI18NServiceMock);
		assertSame(commonI18NServiceMock, cut.getCommonI18NService());
	}

	@Test
	public void testSetGetPricingBackend() throws Exception
	{
		cut = new DefaultPPSCalculationService();
		assertNull(cut.getPricingBackend());
		cut.setPricingBackend(pricingBackendMock);
		assertSame(pricingBackendMock, cut.getPricingBackend());
	}

	@Test
	public void testSetGetRequiresRecalcStrategy() throws Exception
	{
		cut = new DefaultPPSCalculationService();
		assertNull(cut.getOrderRequiresCalculationStrategy());
		cut.setOrderRequiresCalculationStrategy(strategyMock);
		assertSame(strategyMock, cut.getOrderRequiresCalculationStrategy());
	}

	@Test(expected = CalculationException.class)
	public void testUpdateOrderFails() throws Exception
	{
		Mockito.doThrow(new RestClientException("")).when(pricingBackendMock)
				.readPricesForCart(Mockito.any(AbstractOrderModel.class));
		cut.updateOrderFromPPS(new CartModel());
	}

	private static class CalculateAnswer implements Answer<Void>
	{
		@Override
		public Void answer(final InvocationOnMock invocation) throws Throwable
		{
			final AbstractOrderModel order = (AbstractOrderModel) invocation.getArguments()[0];
			final AbstractOrderEntryModel entryModel = order.getEntries().get(0);
			entryModel.setBasePrice(Double.valueOf("17.0"));
			final DiscountValue discountValue = new DiscountValue("DISC", 2.5, true, "EUR");
			entryModel.setDiscountValues(Collections.singletonList(discountValue));
			return null;
		}
	}

	// Caution: This test takes its time! We need to start the commerce suite
	// since the Cart Calculation reads parameters from the Config
	// cf. method resetAllValues(AbstractOrderModel)
	@Test
	public void testCalculate() throws Exception
	{
		Registry.activateMasterTenant();
		final CalculateAnswer answer = new CalculateAnswer();
		Mockito.doAnswer(answer).when(pricingBackendMock).readPricesForCart(order);
		cut.calculate(order);
		// 17.9 = 17.0 - 2.5 + 3.2 + 0.2
		assertEquals(Double.valueOf("17.9"), order.getTotalPrice());
	}
}

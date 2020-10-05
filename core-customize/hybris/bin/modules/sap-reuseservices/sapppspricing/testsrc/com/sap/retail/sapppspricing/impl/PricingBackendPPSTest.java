/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;

import com.sap.ppengine.client.dto.ExtendedAmountType;
import com.sap.ppengine.client.dto.LineItemDomainSpecific;
import com.sap.ppengine.client.dto.ObjectFactory;
import com.sap.ppengine.client.dto.PriceCalculate;
import com.sap.ppengine.client.dto.PriceCalculateBase;
import com.sap.ppengine.client.dto.PriceCalculateResponse;
import com.sap.ppengine.client.dto.SaleBase;
import com.sap.ppengine.client.dto.ShoppingBasketBase;
import com.sap.ppengine.client.util.PPSClientBeanAccessor;
import com.sap.ppengine.client.util.RequestHelper;
import com.sap.ppengine.client.util.RequestHelperImpl;
import com.sap.retail.sapppspricing.LineItemPopulator;
import com.sap.retail.sapppspricing.PPSClient;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.PriceCalculateToOrderMapper;


@SuppressWarnings("javadoc")
@UnitTest
public class PricingBackendPPSTest
{
	private PricingBackendPPS cut;
	private DefaultPPSRequestCreator creator = new DefaultPPSRequestCreator();

	@Mock
	private PPSClientBeanAccessor accessorMock;
	@Mock
	private PPSClient ppsClientMock;
	@Mock
	private CommonI18NService commonI18NServiceMock;
	@Mock
	private LineItemPopulator<ProductModel> lineItemPopulatorMock;
	@Mock
	private PriceCalculateToOrderMapper resultToOrderMapperMock;
	@Mock
	private RequestHelper helperMock;
	@Mock
	private PPSConfigService configService;

	private final ProductModel product = new ProductModel();
	private final UnitModel unit = new UnitModel();
	private final CurrencyModel currency = new CurrencyModel();
	private final RequestHelperImpl helperImpl = new RequestHelperImpl();
	private SAPConfigurationModel sapConfig;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		cut = new PricingBackendPPS();
		cut.setHighPrice(99999.99);
		creator = new DefaultPPSRequestCreator();
		cut.setRequestCreator(creator);
		cut.setConfigService(configService);
		sapConfig = new SAPConfigurationModel();
		Mockito.when(configService.isPpsActive(Mockito.any(ProductModel.class))).thenReturn(Boolean.TRUE);
		Mockito.when(configService.isPpsActive(Mockito.any(AbstractOrderModel.class))).thenReturn(Boolean.TRUE);
		Mockito.when(configService.getSapConfig(Mockito.any(ProductModel.class))).thenReturn(sapConfig);

		unit.setCode("PC");
		product.setUnit(unit);
		product.setCode("4711");
		currency.setIsocode("USD");
		product.setCatalogVersion(createCatalogVersionWithSAPConfig());
	}

	@Test
	public void testSetGetAccessor()
	{
		assertNull(cut.getAccessor());
		cut.setAccessor(accessorMock);
		assertSame(accessorMock, cut.getAccessor());
	}

	@Test
	public void testSetGetI18NService() throws Exception
	{
		assertNull(cut.getCommonI18NService());
		cut.setCommonI18NService(commonI18NServiceMock);
		assertSame(commonI18NServiceMock, cut.getCommonI18NService());
	}

	@Test
	public void testSetGetPpsClient()
	{
		assertNull(cut.getPpsClient());
		cut.setPpsClient(ppsClientMock);
		assertSame(ppsClientMock, cut.getPpsClient());
	}

	@Test
	public void testSetGetResultToOrderMappers()
	{
		final List<PriceCalculateToOrderMapper> resultToOrderMappers = new ArrayList<>();
		resultToOrderMappers.add(resultToOrderMapperMock);
		assertNull(cut.getResultToOrderMappers());
		cut.setResultToOrderMappers(resultToOrderMappers);
		assertSame(resultToOrderMappers, cut.getResultToOrderMappers());
	}

	@Test
	public void testReadPricesForCart()
	{
		setMocks();
		// create Order with entries
		final AbstractOrderModel order = new AbstractOrderModel();
		createOrderWithOneEntry(order);
		final PriceCalculateResponse response = new PriceCalculateResponse();
		Mockito.when(ppsClientMock.callPPS(Mockito.any(PriceCalculate.class), Mockito.eq(sapConfig))).thenReturn(response);
		final ArgRecorderAnswer<Void> answerMapper = new ArgRecorderAnswer<Void>();
		Mockito.doAnswer(answerMapper).when(resultToOrderMapperMock).map(response, order);
		prepareData(order);

		try
		{
			cut.readPricesForCart(order);
		}
		catch (final RestClientException e)
		{
			assertFalse(true);
		}
		assertSame(response, answerMapper.getArg(0));
		assertSame(order, answerMapper.getArg(1));

	}

	private void prepareData(final AbstractOrderModel order)
	{
		final List<TaxInformation> taxInfo = new LinkedList<TaxInformation>();
		final double value = new Double(10);
		final String code = "MWST";
		final TaxValue taxValue = new TaxValue(code, value, false, "USD");
		final TaxInformation tax = new TaxInformation(taxValue);
		taxInfo.add(tax);

		final ArgRecorderAnswer<Void> answerPopulator = new ArgRecorderAnswer<Void>();
		Mockito.doAnswer(answerPopulator).when(lineItemPopulatorMock)
				.populate(Mockito.any(LineItemDomainSpecific.class), Mockito.any(ProductModel.class));

		Mockito.when(accessorMock.getHelper()).thenReturn(helperMock);

		final ObjectFactory objectFactory = new ObjectFactory();
		helperImpl.setObjectFactory(objectFactory);
		final GregorianCalendar gregorianCalendar = new GregorianCalendar();
		final PriceCalculate priceCalculate = helperImpl.createCalculateRequestSkeleton("Store1", gregorianCalendar);
		Mockito.when(helperMock.createCalculateRequestSkeleton(Mockito.eq("Store1"), Mockito.any(GregorianCalendar.class)))
				.thenReturn(priceCalculate);
		final LineItemDomainSpecific lineItem = helperImpl.createSaleLineItem(0, product.getCode(), "ST", new BigDecimal(14));

		Mockito.when(
				helperMock.createSaleLineItem(Mockito.eq(0), Mockito.eq(product.getCode()), Mockito.eq("ST"),
						Mockito.any(BigDecimal.class))).thenReturn(lineItem);
	}

	@Test(expected = RuntimeException.class)
	public void testReadPricesForCartFails() throws Exception
	{
		setMocks();
		final AbstractOrderModel order = new AbstractOrderModel();
		createOrderWithOneEntry(order);

		prepareData(order);

		Mockito.when(ppsClientMock.callPPS(Mockito.any(PriceCalculate.class), Mockito.eq(sapConfig))).thenThrow(
				new RestClientException("bla"));
		cut.readPricesForCart(order);
		assertTrue(false);
	}

	@Test
	public void testReadPriceInformationForProductsPriceInfoNotNull()
	{
		final PriceValue priceValue = new PriceValue("USD", Double.parseDouble("17"), false);
		final PriceInformation pinfo = new PriceInformation(priceValue);
		final List<ProductModel> productModels = new ArrayList<>();

		productModels.add(product);

		setHelper();

		setMocks();

		Mockito.when(commonI18NServiceMock.getCurrentCurrency()).thenReturn(currency);

		final List<PriceInformation> result = cut.readPriceInformationForProducts(productModels, true);

		assertEquals(1, result.size());
		assertSame(pinfo, result.get(0));
	}

	@Test
	public void testReadPriceInformationForProductsPriceInfoNull()
	{
		final BigDecimal calculatedPrice = BigDecimal.valueOf(99999.99);
		final PriceValue priceValue = new PriceValue("USD", calculatedPrice.doubleValue(), true);
		final List<ProductModel> productModels = new ArrayList<>();
		productModels.add(product);

		setHelper();
		setMocks();

		final PriceCalculateResponse response = new PriceCalculateResponse();
		final PriceCalculateBase body = new PriceCalculateBase();
		final ShoppingBasketBase shoppingBasket = new ShoppingBasketBase();
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();

		shoppingBasket.getLineItem().add(lineItem);

		body.setShoppingBasket(shoppingBasket);
		response.getPriceCalculateBody().add(body);

		Mockito.when(ppsClientMock.callPPS(Mockito.any(PriceCalculate.class), Mockito.eq(sapConfig))).thenReturn(response);
		Mockito.when(commonI18NServiceMock.getCurrentCurrency()).thenReturn(currency);

		final ArgRecorderAnswer<Void> answerPopulator = new ArgRecorderAnswer<Void>();
		Mockito.doAnswer(answerPopulator).when(lineItemPopulatorMock)
				.populate(Mockito.any(LineItemDomainSpecific.class), Mockito.any(ProductModel.class));

		final List<PriceInformation> result = cut.readPriceInformationForProducts(productModels, true);

		assertEquals(1, result.size());
		assertEquals(priceValue, result.get(0).getPriceValue());
	}

	@Test
	public void testReadPriceInformationForProductsPriceInfoNullExtendedAmountSet()
	{

		final BigDecimal calculatedPrice = new BigDecimal(10);
		final PriceValue priceValue = new PriceValue("USD", calculatedPrice.doubleValue(), true);

		final PriceInformation pinfo = new PriceInformation(priceValue);
		final List<ProductModel> productModels = new ArrayList<>();
		productModels.add(product);

		setHelper();

		setMocks();

		final PriceCalculateResponse response = new PriceCalculateResponse();

		final PriceCalculateBase body = new PriceCalculateBase();
		final ShoppingBasketBase shoppingBasket = new ShoppingBasketBase();
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();
		final SaleBase saleBase = new SaleBase();

		final ExtendedAmountType amount = new ExtendedAmountType();
		amount.setValue(BigDecimal.TEN);
		saleBase.setExtendedAmount(amount);
		lineItem.setSale(saleBase);

		shoppingBasket.getLineItem().add(lineItem);

		body.setShoppingBasket(shoppingBasket);
		response.getPriceCalculateBody().add(body);

		Mockito.when(ppsClientMock.callPPS(Mockito.any(PriceCalculate.class), Mockito.eq(sapConfig))).thenReturn(response);
		Mockito.when(commonI18NServiceMock.getCurrentCurrency()).thenReturn(currency);

		final ArgRecorderAnswer<Void> answerPopulator = new ArgRecorderAnswer<Void>();
		Mockito.doAnswer(answerPopulator).when(lineItemPopulatorMock)
				.populate(Mockito.any(LineItemDomainSpecific.class), Mockito.any(ProductModel.class));

		final List<PriceInformation> result = cut.readPriceInformationForProducts(productModels, true);

		assertEquals(1, result.size());
		assertEquals(pinfo.getPriceValue(), result.get(0).getPriceValue());
	}

	private void setMocks()
	{
		final List<LineItemPopulator<ProductModel>> lineItemPopulators = new ArrayList<>();
		lineItemPopulators.add(lineItemPopulatorMock);
		final List<PriceCalculateToOrderMapper> resultToOrderMappers = new ArrayList<>();
		resultToOrderMappers.add(resultToOrderMapperMock);

		creator.setLineItemPopulators(lineItemPopulators);
		cut.setAccessor(accessorMock);
		creator.setAccessor(accessorMock);
		creator.setConfigService(configService);
		Mockito.when(configService.getBusinessUnitId(Mockito.any(ProductModel.class))).thenReturn("Store1");
		Mockito.when(configService.getBusinessUnitId(Mockito.any(AbstractOrderModel.class))).thenReturn("Store1");
		cut.setPpsClient(ppsClientMock);
		cut.setResultToOrderMappers(resultToOrderMappers);
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("EN");
		Mockito.when(commonI18NServiceMock.getCurrentLanguage()).thenReturn(languageModel);
		cut.setCommonI18NService(commonI18NServiceMock);
		creator.setCommonI18NService(commonI18NServiceMock);
	}

	private void createOrderWithOneEntry(final AbstractOrderModel order)
	{
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final UnitModel unit = new UnitModel();
		unit.setCode("ST");
		entry.setUnit(unit);
		entry.setProduct(product);
		final Long quantity = 14L;
		entry.setQuantity(quantity);
		entries.add(entry);
		order.setEntries(entries);
		final BaseStoreModel store = new BaseStoreModel();
		order.setStore(store);
		order.setNet(Boolean.TRUE);
		order.setCode("WattFuernCode?");
		order.setStore(new BaseStoreModel());
		order.getStore().setSAPConfiguration(sapConfig);
		creator.setCommonI18NService(commonI18NServiceMock);
	}

	private void setHelper()
	{
		final ObjectFactory objectFactory = new ObjectFactory();
		helperImpl.setObjectFactory(objectFactory);
		Mockito.when(accessorMock.getHelper()).thenReturn(helperImpl);
	}

	private CatalogVersionModel createCatalogVersionWithSAPConfig()
	{
		final CatalogVersionModel catalogVersion = new CatalogVersionModel();
		final CatalogModel catalog = new CatalogModel();
		final Collection<BaseStoreModel> baseStores = new LinkedList<BaseStoreModel>();
		final BaseStoreModel baseStore = new BaseStoreModel();
		final SAPConfigurationModel config = new SAPConfigurationModel();

		baseStore.setSAPConfiguration(config);
		baseStores.add(baseStore);
		catalog.setBaseStores(baseStores);
		catalogVersion.setCatalog(catalog);

		catalogVersion.setDefaultCurrency(currency);
		return catalogVersion;
	}

	@Test
	public void testSetGetHighPrice() throws Exception
	{
		cut.setHighPrice(0);
		assertEquals(Double.valueOf(0), Double.valueOf(cut.getHighPrice()));
		cut.setHighPrice(30.0);
		assertEquals(Double.valueOf(30.0), Double.valueOf(cut.getHighPrice()));
	}
//
//	@Test
//	public void testReadPriceInfoFromPPSGetsException1() throws Exception
//	{
//		setHelper();
//		setMocks();
//		Mockito.when(ppsClientMock.callPPS(Mockito.any(), Mockito.eq(sapConfig))).thenThrow(new NullPointerException("Hoppla"));
//		final PriceInformation result = cut.readPriceInfoFromPps(product, false);
//		assertEquals(Double.valueOf(99999.99), Double.valueOf(result.getPriceValue().getValue()));
//	}
//
//	@Test
//	public void testReadPriceInfoFromPPSGetsException2() throws Exception
//	{
//		setHelper();
//		setMocks();
//		cut.setUseHighPrice(false);
//		Mockito.when(ppsClientMock.callPPS(Mockito.any(), Mockito.eq(sapConfig))).thenThrow(new NullPointerException("Hoppla"));
//		final PriceInformation result = cut.readPriceInfoFromPps(product, false);
//		assertNull(result);
//	}

	@Test
	public void testSetGetUseHighPrice()
	{
		assertTrue(cut.isUseHighPrice());
		cut.setUseHighPrice(false);
		assertFalse(cut.isUseHighPrice());
	}

}

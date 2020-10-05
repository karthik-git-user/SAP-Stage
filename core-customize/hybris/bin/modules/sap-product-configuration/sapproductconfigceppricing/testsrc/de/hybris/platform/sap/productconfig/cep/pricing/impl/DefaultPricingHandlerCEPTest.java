/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.cep.pricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.cep.pricing.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.ConditionResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Subtotal;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl.DefaultPricingConfigurationParameterCPS;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPricingHandlerCEPTest
{
	private static final String CURRENCY = "USD";

	private static final String SUBTOTAL_FOR_BASE_PRICE_FLAG = "1";
	private static final String SUBTOTAL_FOR_SELECTED_OPTIONS_FLAG = "2";
	private static final String ANOTHER_SUBTOTAL_FLAG = "X";
	private static final String SUBTOTAL_BASE_PRICE = "1111.11";
	private static final String SUBTOTAL_SELECTED_OPTIONS = "11.11";

	private static final Collection<String> CONDITIONTYPES_FOR_BASEPRICE_COLLECTION = Arrays.asList("PR00", "PR01", "PR02",
			"PR03");
	private static final Collection<String> CONDITIONTYPES_FOR_SELECTEDOPTIONS_COLLECTION = Arrays.asList("VA00", "VA01");
	private static final Collection<String> ANOTHER_CONDITIONTYPES_COLLECTION = Arrays.asList("XX00", "YY00");

	private static final double PR00_VALUE = 111.11;
	private static final double PR01_VALUE = 222.22;
	private static final double PR02_VALUE = 999.99;
	private static final double PR03_VALUE = 999.99;
	private static final double VA00_VALUE = 3.33;
	private static final double VA01_VALUE = 4.44;

	private static final String CONDITIONTYPE_BASE_PRICE = "333.33";
	private static final String CONDITIONTYPE_SELECTED_OPTIONS = "7.77";

	private DefaultPricingHandlerCEP classUnderTest;
	private PricingDocumentResult pricingResult;

	@Mock
	private ConfigModelFactory configModelFactory;

	@Mock
	private DefaultPricingConfigurationParameterCEP pricingConfigurationParameterCEP;

	@Mock
	private DefaultPricingConfigurationParameterCPS pricingConfigurationParameterCPS;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultPricingHandlerCEP(pricingConfigurationParameterCEP);
		classUnderTest.setConfigModelFactory(configModelFactory);
		Mockito.when(configModelFactory.createInstanceOfPriceModel()).thenReturn(new PriceModelImpl());
		fillPricingDocumentResult();
	}


	@Test
	public void testGetPriceForSubTotal()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal(SUBTOTAL_FOR_BASE_PRICE_FLAG, pricingResult);
		assertEquals(createPriceModel(SUBTOTAL_BASE_PRICE), price);
	}

	@Test
	public void testGetPriceForSubTotalRequiredSubtotalDoesNotExist()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal(ANOTHER_SUBTOTAL_FLAG, pricingResult);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForSubTotalSubtotalFlagEmpty()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal("", pricingResult);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForSubTotalSubtotalFlagNull()
	{
		final PriceModel price = classUnderTest.getPriceForSubTotal(null, pricingResult);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForConditionTypes()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(CONDITIONTYPES_FOR_BASEPRICE_COLLECTION, pricingResult);
		assertEquals(createPriceModel(CONDITIONTYPE_BASE_PRICE), price);
	}

	@Test
	public void testGetPriceForConditionTypesRequiredCondTypesDoNotExist()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(ANOTHER_CONDITIONTYPES_COLLECTION, pricingResult);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForConditionTypesCollectionEmpty()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(new ArrayList<String>(), pricingResult);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testGetPriceForConditionTypesCollectionNull()
	{
		final PriceModel price = classUnderTest.getPriceForConditionTypes(null, pricingResult);
		assertEquals(PriceModel.NO_PRICE, price);
	}

	@Test
	public void testCreatePriceModel()
	{
		final PriceModel price = classUnderTest.createPriceModel(CURRENCY, new BigDecimal(SUBTOTAL_BASE_PRICE));
		assertEquals(CURRENCY, price.getCurrency());
		assertEquals(SUBTOTAL_BASE_PRICE, price.getPriceValue().toString());
	}

	@Test
	public void testGetBasePriceConditonType()
	{
		Mockito.when(pricingConfigurationParameterCEP.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONTYPE);
		Mockito.when(pricingConfigurationParameterCEP.getConditionTypesForBasePrice())
				.thenReturn(CONDITIONTYPES_FOR_BASEPRICE_COLLECTION);
		final PriceModel price = classUnderTest.getBasePrice(pricingResult);
		assertEquals(createPriceModel(CONDITIONTYPE_BASE_PRICE), price);
	}

	@Test
	public void testGetBasePriceSubTotal()
	{
		Mockito.when(pricingConfigurationParameterCEP.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.PRICINGSUBTOTAL);
		Mockito.when(pricingConfigurationParameterCEP.getSubTotalForBasePrice()).thenReturn(SUBTOTAL_FOR_BASE_PRICE_FLAG);
		final PriceModel price = classUnderTest.getBasePrice(pricingResult);
		assertEquals(createPriceModel(SUBTOTAL_BASE_PRICE), price);
	}

	@Test
	public void testGetBasePriceConditionFunction()
	{
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameterCPS);
		Mockito.when(pricingConfigurationParameterCEP.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONFUNCTION);
		final PriceModel price = classUnderTest.getBasePrice(pricingResult);
		verify(pricingConfigurationParameterCPS).getTargetForBasePrice();
		assertNotNull(price);
	}

	@Test
	public void testGetSelectedOptionsConditonType()
	{
		Mockito.when(pricingConfigurationParameterCEP.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONTYPE);
		Mockito.when(pricingConfigurationParameterCEP.getConditionTypesForSelectedOptions())
				.thenReturn(CONDITIONTYPES_FOR_SELECTEDOPTIONS_COLLECTION);
		final PriceModel price = classUnderTest.getSelectedOptionsPrice(pricingResult);
		assertEquals(createPriceModel(CONDITIONTYPE_SELECTED_OPTIONS), price);
	}

	@Test
	public void testGetSelectedOptionsSubTotal()
	{
		Mockito.when(pricingConfigurationParameterCEP.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.PRICINGSUBTOTAL);
		Mockito.when(pricingConfigurationParameterCEP.getSubTotalForSelectedOptions())
				.thenReturn(SUBTOTAL_FOR_SELECTED_OPTIONS_FLAG);
		final PriceModel price = classUnderTest.getSelectedOptionsPrice(pricingResult);
		assertEquals(createPriceModel(SUBTOTAL_SELECTED_OPTIONS), price);
	}

	@Test
	public void testGetSelectedOptionsConditionFunction()
	{
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameterCPS);
		Mockito.when(pricingConfigurationParameterCEP.getPricingDetailsMode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONFUNCTION);
		final PriceModel price = classUnderTest.getSelectedOptionsPrice(pricingResult);
		verify(pricingConfigurationParameterCPS).getTargetForSelectedOptions();
		assertNotNull(price);
	}

	private void fillPricingDocumentResult()
	{
		pricingResult = new PricingDocumentResult();
		pricingResult.setDocumentCurrencyUnit(CURRENCY);
		// Subtotals
		pricingResult.setSubTotals(new ArrayList<>());
		final Subtotal subtotalBase = new Subtotal();
		subtotalBase.setFlag(SUBTOTAL_FOR_BASE_PRICE_FLAG);
		subtotalBase.setValue(SUBTOTAL_BASE_PRICE);
		pricingResult.getSubTotals().add(subtotalBase);
		final Subtotal subtotalOptions = new Subtotal();
		subtotalOptions.setFlag(SUBTOTAL_FOR_SELECTED_OPTIONS_FLAG);
		subtotalOptions.setValue(SUBTOTAL_SELECTED_OPTIONS);
		pricingResult.getSubTotals().add(subtotalOptions);
		// Conditions
		pricingResult.setConditions(new ArrayList<>());
		pricingResult.getConditions().add(createCondition("PR00", PR00_VALUE, false, " "));
		pricingResult.getConditions().add(createCondition("PR01", PR01_VALUE, false, " "));
		pricingResult.getConditions().add(createCondition("PR02", PR02_VALUE, true, " "));
		pricingResult.getConditions().add(createCondition("PR03", PR03_VALUE, false, "A"));
		pricingResult.getConditions().add(createCondition("VA00", VA00_VALUE, false, " "));
		pricingResult.getConditions().add(createCondition("VA00", VA01_VALUE, false, " "));
	}

	private ConditionResult createCondition(final String condType, final double value, final boolean statistical,
			final String inactiveFlag)
	{
		final ConditionResult condition = new ConditionResult();
		condition.setConditionType(condType);
		condition.setConditionValue(value);
		condition.setStatistical(statistical);
		condition.setInactiveFlag(inactiveFlag);
		return condition;
	}

	protected PriceModel createPriceModel(final String valuePrice)
	{
		final PriceModel priceModel = new PriceModelImpl();
		priceModel.setPriceValue(new BigDecimal(valuePrice));
		priceModel.setCurrency(CURRENCY);
		return priceModel;
	}
}

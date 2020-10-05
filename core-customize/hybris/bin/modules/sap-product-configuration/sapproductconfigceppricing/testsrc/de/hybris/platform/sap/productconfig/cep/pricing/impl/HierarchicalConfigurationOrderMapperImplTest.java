/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.cep.pricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cep.pricing.model.SAPCpiOutboundOrderS4hcConfigHeaderModel;
import de.hybris.platform.sap.productconfig.cep.pricing.model.SAPCpiOutboundOrderS4hcConfigValuationModel;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class HierarchicalConfigurationOrderMapperImplTest
{

	private static final String EXTERNAL_CONFIGURATION = "{\"externalConfiguration\":{\"kbId\":0,\"kbKey\":{\"logsys\":\"string\",\"name\":\"string\",\"version\":\"string\"},\"consistent\":false,\"complete\":false,\"rootItem\":{\"id\":\"string\",\"objectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"objectKeyAuthor\":\"string\",\"bomPosition\":\"string\",\"bomPositionObjectKey\":{\"id\":\"string\",\"type\":\"string\",\"classType\":\"string\"},\"bomPositionAuthor\":\"string\",\"quantity\":{\"value\":0,\"unit\":\"string\"},\"fixedQuantity\":false,\"salesRelevant\":false,\"consistent\":false,\"complete\":false,\"characteristics\":[{\"id\":\"string\",\"required\":false,\"visible\":false,\"values\":[{\"value\":\"string\",\"author\":\"string\"}]}],\"variantConditions\":[{\"key\":\"string\",\"factor\":0}],\"subItems\":[{}]}},\"unitCodes\":{\"PCE\":\"PCE\"}}";;
	private static final Integer ENTRY_NUMBER = 1;
	private static final String ORDER_ID = "123";
	private static final String CSTIC_NAME = "Cstic";
	private static final String AUTHOR = "4";
	private static final String VALUE_NAME = "Value";
	HierarchicalConfigurationOrderMapperImpl classUnderTest = null;

	private final CPSExternalCharacteristic cstic = new CPSExternalCharacteristic();
	private final List<AbstractOrderEntryModel> sourceEntryList = new ArrayList<>();
	private final Set<SAPCpiOutboundOrderItemModel> targetEntryList = new HashSet<>();
	private final SAPCpiOutboundOrderS4hcConfigHeaderModel configurationHeader = new SAPCpiOutboundOrderS4hcConfigHeaderModel();
	private final List<CPSExternalValue> characteristicValues = new ArrayList<>();
	private final CPSExternalValue characteristicValue = new CPSExternalValue();

	@Mock
	private OrderModel source;
	@Mock
	private SAPCpiOutboundOrderModel target;
	@Mock
	private AbstractOrderEntryModel sourceEntry;
	@Mock
	private SAPCpiOutboundOrderItemModel targetEntry;




	@Before
	public void initialize()
	{
		classUnderTest = new HierarchicalConfigurationOrderMapperImpl();
		MockitoAnnotations.initMocks(this);

		sourceEntryList.add(sourceEntry);
		targetEntryList.add(targetEntry);
		Mockito.when(source.getEntries()).thenReturn(sourceEntryList);
		Mockito.when(sourceEntry.getExternalConfiguration()).thenReturn(EXTERNAL_CONFIGURATION);
		Mockito.when(target.getSapCpiOutboundOrderItems()).thenReturn(targetEntryList);
		Mockito.when(sourceEntry.getEntryNumber()).thenReturn(ENTRY_NUMBER);
		Mockito.when(targetEntry.getEntryNumber()).thenReturn(ENTRY_NUMBER.toString());
		Mockito.when(targetEntry.getOrderId()).thenReturn(ORDER_ID);

		configurationHeader.setSapCpiS4hcConfigValuations(new HashSet<SAPCpiOutboundOrderS4hcConfigValuationModel>());
		configurationHeader.setEntryNumber(ENTRY_NUMBER.toString());
		configurationHeader.setOrderId(ORDER_ID);

		cstic.setValues(characteristicValues);
		characteristicValue.setAuthor(AUTHOR);
		characteristicValue.setValue(VALUE_NAME);
		characteristicValues.add(characteristicValue);

	}

	@Test
	public void testMap()
	{
		classUnderTest.map(source, target);
		Mockito.verify(targetEntry, times(1)).setS4hcConfigHeader(Mockito.any());
	}

	@Test
	public void testMapEntry()
	{
		classUnderTest.mapEntry(sourceEntry, target);
		Mockito.verify(targetEntry, times(1)).setS4hcConfigHeader(Mockito.any());
	}

	@Test
	public void testMapEntryNotConfigurable()
	{
		Mockito.when(sourceEntry.getExternalConfiguration()).thenReturn(null);
		classUnderTest.mapEntry(sourceEntry, target);
		Mockito.verify(targetEntry, times(0)).setS4hcConfigHeader(Mockito.any());
	}

	@Test
	public void testFindTargetEntry()
	{
		final SAPCpiOutboundOrderItemModel targetEntry = classUnderTest.findTargetEntry(sourceEntry, target);
		assertNotNull(targetEntry);
	}

	@Test
	public void testFindTargetEntryNoMatch()
	{
		Mockito.when(targetEntry.getEntryNumber()).thenReturn("3");
		final SAPCpiOutboundOrderItemModel targetEntry = classUnderTest.findTargetEntry(sourceEntry, target);
		assertNull(targetEntry);
	}

	@Test
	public void testCreateConfiguration()
	{
		final SAPCpiOutboundOrderS4hcConfigHeaderModel configuration = classUnderTest.createConfiguration(EXTERNAL_CONFIGURATION,
				targetEntry);
		assertNotNull(configuration);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> configValuations = configuration.getSapCpiS4hcConfigValuations();
		assertNotNull(configValuations);
		assertEquals(1, configValuations.size());
		assertEquals(ENTRY_NUMBER.toString(), configuration.getEntryNumber());
		assertEquals(ORDER_ID, configuration.getOrderId());
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateConfigurationWrongFormat()
	{
		classUnderTest.createConfiguration("Huhu", targetEntry);
	}

	@Test
	public void testAddCharacteristicValues()
	{
		classUnderTest.addCharacteristicValues(cstic, configurationHeader);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> sapCpiS4hcConfigValuations = configurationHeader
				.getSapCpiS4hcConfigValuations();
		assertEquals(1, sapCpiS4hcConfigValuations.size());
	}

	@Test
	public void testAddCharacteristicValuesForNullValues()
	{
		cstic.setValues(null);
		classUnderTest.addCharacteristicValues(cstic, configurationHeader);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> sapCpiS4hcConfigValuations = configurationHeader
				.getSapCpiS4hcConfigValuations();
		assertEquals(0, sapCpiS4hcConfigValuations.size());
	}

	@Test
	public void testAddCharacteristicValue()
	{
		classUnderTest.addCharacteristicValue(characteristicValue, CSTIC_NAME, configurationHeader);
		final Set<SAPCpiOutboundOrderS4hcConfigValuationModel> sapCpiS4hcConfigValuations = configurationHeader
				.getSapCpiS4hcConfigValuations();
		assertEquals(1, sapCpiS4hcConfigValuations.size());
		sapCpiS4hcConfigValuations.forEach(valuation -> checkValuation(valuation));
	}

	@Test
	public void testGetObjectMapper()
	{
		// 2 calls will return the same instance
		assertEquals(classUnderTest.getObjectMapper(), classUnderTest.getObjectMapper());
	}

	protected void checkValuation(final SAPCpiOutboundOrderS4hcConfigValuationModel valuation)
	{
		assertEquals(AUTHOR, valuation.getAuthor());
		assertEquals(CSTIC_NAME, valuation.getCharacteristic());
		assertEquals(ENTRY_NUMBER.toString(), valuation.getEntryNumber());
		assertEquals(ORDER_ID, valuation.getOrderId());
		assertEquals(VALUE_NAME, valuation.getValue());
	}
}

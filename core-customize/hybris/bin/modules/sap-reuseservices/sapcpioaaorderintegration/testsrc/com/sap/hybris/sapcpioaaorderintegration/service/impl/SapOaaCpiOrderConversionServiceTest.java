/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpioaaorderintegration.outbound.impl.SapCpiOaaOrderContributor;
import com.sap.hybris.sapcpioaaorderintegration.outbound.impl.SapCpiOaaOrderEntryContributor;
import com.sap.retail.oaa.model.model.ScheduleLineModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrderItem;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiScheduleLinesOrderItem;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPSalesOrganizationModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapOaaCpiOrderConversionServiceTest {
	
	private static final int ENTRY_NUMBER_INT = 1;

	private static final String ORDER_CODE = "100001";

	private static final String ENTRY_NUMBER = "1";


	@InjectMocks
	private SapOaaCpiOrderConversionService sapOaaCpiOrderConversionService;
	
	@Mock
	private OrderModel orderModel;
	
	@Mock
	private CurrencyModel currencyModel;
	
	@Mock
	private BaseStoreModel baseStoreModel;
	
	@Mock
	private DeliveryModeModel deliveryModeModel;
	
	@Mock
	private SAPSalesOrganizationModel sapSalesOrganization;
	
	@Mock
	private ConsignmentModel consignmentModel;
	
	@Mock
	private WarehouseModel warehouseModel;
		
	@Mock
	private SAPLogicalSystemModel sapLogicalSystemModel;
	
	@Mock
	private SAPHTTPDestinationModel sapHttpDestinationModel;
	
	@Mock
	private SAPConfigurationModel sapConfigurationModel;
	
	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModel;

	@Mock
	private ScheduleLineModel scheduleLineModel;

	@Mock
	private PointOfServiceModel pointOfServiceModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private ProductModel productModel;

	@Mock
	private UnitModel unitModel;

	@Mock
	private LanguageModel languageModel;
	
	@Before
	public void setUp() {
		when(orderModel.getCode()).thenReturn(ORDER_CODE);
		when(orderModel.getDate()).thenReturn(new Date());
		when(orderModel.getCurrency()).thenReturn(currencyModel);
		when(currencyModel.getIsocode()).thenReturn("EUR");
		when(orderModel.getStore()).thenReturn(baseStoreModel);
		when(baseStoreModel.getUid()).thenReturn("apparel-uk");
		when(orderModel.getDeliveryMode()).thenReturn(deliveryModeModel);
		when(deliveryModeModel.getCode()).thenReturn("Ship");
		when(orderModel.getSapSalesOrganization()).thenReturn(sapSalesOrganization);
		when(orderModel.getSapLogicalSystem()).thenReturn("QM7");
		when(sapSalesOrganization.getSalesOrganization()).thenReturn("R100");
		when(sapSalesOrganization.getDistributionChannel()).thenReturn("R3");
		when(sapSalesOrganization.getDivision()).thenReturn("10");
		Set<ConsignmentModel> consignmentModels = new HashSet<ConsignmentModel>();
		consignmentModels.add(consignmentModel);
		when(orderModel.getConsignments()).thenReturn(consignmentModels);
		when(consignmentModel.getWarehouse()).thenReturn(warehouseModel);
		when(warehouseModel.getCode()).thenReturn("ROCCRSI");
		
		
		when(sapLogicalSystemModel.getSenderName()).thenReturn("SENDER");
		when(sapLogicalSystemModel.getSenderPort()).thenReturn("xxx");
		
		when(sapLogicalSystemModel.getSapLogicalSystemName()).thenReturn("QM7");
		when(sapLogicalSystemModel.getSapHTTPDestination()).thenReturn(sapHttpDestinationModel);

		when(sapHttpDestinationModel.getUserid()).thenReturn("USER");
			
		when(baseStoreModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		when(sapConfigurationModel.getSapcommon_transactionType()).thenReturn("TRAN");



	}
	
	@Test
	public void testConvertOrderToSapCpiOrder_SapCpiOrder() {
		
		SapCpiOaaOrderContributor sapOrderContributor = new SapCpiOaaOrderContributor();
		sapOaaCpiOrderConversionService.setSapOrderContributor(sapOrderContributor);
		
		
		try {
			sapOaaCpiOrderConversionService.convertOrderToSapCpiOrder(orderModel);
		} catch (Exception e) {
		}
		
		verify(orderModel).getCode();
	}
	
	@Test
	public void testConvertOrderToSapCpiOrder_SapCpiOrderItem() {
		
		
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(abstractOrderEntryModel);
		when(orderModel.getEntries()).thenReturn(entries);

		when(scheduleLineModel.getConfirmedQuantity()).thenReturn(1.0);
		when(scheduleLineModel.getConfirmedDate()).thenReturn(new Date());

		when(pointOfServiceModel.getName()).thenReturn("M001");
		when(pointOfServiceModel.getSapoaa_cacShippingPoint()).thenReturn("C001");

		final List<ScheduleLineModel> scheduleLineModels = new ArrayList<ScheduleLineModel>();
		scheduleLineModels.add(scheduleLineModel);
		when(abstractOrderEntryModel.getScheduleLines()).thenReturn(scheduleLineModels);
		when(abstractOrderEntryModel.getSapSource()).thenReturn(pointOfServiceModel);
		when(abstractOrderEntryModel.getDeliveryPointOfService()).thenReturn(pointOfServiceModel);
		when(abstractOrderEntryModel.getSapVendor()).thenReturn(null);
		when(abstractOrderEntryModel.getProduct()).thenReturn(productModel);
		when(abstractOrderEntryModel.getUnit()).thenReturn(unitModel);
		when(productModel.getCode()).thenReturn("Shirt");
		when(productModel.getName(new java.util.Locale("EN"))).thenReturn(null);
		when(unitModel.getCode()).thenReturn("Unit1");

		when(orderModel.getSite()).thenReturn(baseSiteModel);
		when(orderModel.getLanguage()).thenReturn(languageModel);
		when(languageModel.getIsocode()).thenReturn("EN");
		when(languageModel.getFallbackLanguages()).thenReturn(new ArrayList());

		when(abstractOrderEntryModel.getEntryNumber()).thenReturn(ENTRY_NUMBER_INT);
		
		SapCpiOrder sapCpiOrder = new SapCpiOrder();

		try {
			SapCpiOaaOrderContributor sapOrderContributor = new SapCpiOaaOrderContributor();
			SapCpiOaaOrderEntryContributor sapCpiOaaOrderEntryContributor = new SapCpiOaaOrderEntryContributor();
			sapCpiOaaOrderEntryContributor.setDatePattern("yyyy-MM-dd HH:mm:ss.S");
			
			sapOaaCpiOrderConversionService.setSapOrderContributor(sapOrderContributor);
			sapOaaCpiOrderConversionService.setSapOrderEntryContributor(sapCpiOaaOrderEntryContributor);
			
			
			sapOaaCpiOrderConversionService.setSapCpiOrderItems(orderModel, sapCpiOrder);
		} catch (Exception e) {
			
		}

		List<SapCpiOrderItem> sapCpiOrderItems =  sapCpiOrder.getSapCpiOrderItems();
		SapCpiOrderItem sapCpiOrderItem = sapCpiOrderItems.get(0);
		
		assertTrue(sapCpiOrderItem.getPlant().equals("M001"));
		assertTrue(sapCpiOrderItem.getCacShippingPoint().equals("C001"));
		assertTrue(sapCpiOrderItem.getProductCode().equals("Shirt"));
		assertTrue(sapCpiOrderItem.getUnit().equals("Unit1"));
		assertTrue(sapCpiOrderItem.getOrderId().equals(ORDER_CODE));
		assertTrue(sapCpiOrderItem.getEntryNumber().equals(ENTRY_NUMBER));
		SapCpiScheduleLinesOrderItem sapCpiScheduleLinesOrderItem = (SapCpiScheduleLinesOrderItem)sapCpiOrderItem.getScheduleLines().get(0);
		assertTrue(sapCpiScheduleLinesOrderItem.getConfirmedQuantity().equals("1.0"));
		
	}
}

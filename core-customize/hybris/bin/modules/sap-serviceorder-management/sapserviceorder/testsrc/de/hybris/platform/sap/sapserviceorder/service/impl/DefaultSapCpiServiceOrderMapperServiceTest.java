/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundPartnerRoleModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundPriceComponentModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderItemModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
public class DefaultSapCpiServiceOrderMapperServiceTest {

	@InjectMocks
	DefaultSapCpiServiceOrderMapperService serviceOrderMapper;
	
	@Mock
	private SAPConfigurationModel sapConfig;
	
	@Mock
	private ModelService modelService;
	
	
	//order literals
	static final String ORDER_CODE = "serviceOnlyOrder";
	static final String PARENT_ORDER_CODE = "parentCommerceOrder";
	static final String B2B_UNIT_UID = "B2BUnitId";
	static final String BUSINESS_PARTNER_ID = "bupa";
	
	static final String SALES_ITEM_PRICE_CONDITION_TYPE = "PPR0";
	static final String SERVICE_ITEM_PRICE_CONDITION_TYPE = "PMV1";
	static final String SERVICE_ORDER_TRANSACTION_TYPE = "SVo2";
	
	static final int YEAR = 2019, MONTH = Calendar.AUGUST , DAY = 1, HOUR = 11, MINUTE = 0, SECOND = 0;
	final Calendar calendar = Calendar.getInstance();
	
	
	//outbound order literals
	static final String REQUEST_START_DATE = "2019-08-01T11:00:00";
	static final String ORDER_CREATOIN_DATE = "2019-08-01T11:00:00Z";
	
	static final String ITEM0_ENTRY = "0";
	static final String ITEM0_QUANTITY = "3";
	static final String ITEM0_VALUE = "10";
	static final String ITEM0_COMPUTED_VALUE = "30.0";
	
	
	@Before
	public void setUp() {
		
		MockitoAnnotations.initMocks(this);
		
		Mockito.when(sapConfig.getServiceOrderTransactionType()).thenReturn(SERVICE_ORDER_TRANSACTION_TYPE);
		Mockito.when(sapConfig.getServiceItemPriceConditionCode()).thenReturn(SERVICE_ITEM_PRICE_CONDITION_TYPE);
		Mockito.when(sapConfig.getSaporderexchange_itemPriceConditionType()).thenReturn(SALES_ITEM_PRICE_CONDITION_TYPE);
		
		Mockito.when(modelService.create(SAPCpiOutboundServiceOrderItemModel.class)).thenReturn(generateServiceOrderItem());
	}
	
	private SAPCpiOutboundServiceOrderItemModel generateServiceOrderItem() {
		SAPCpiOutboundServiceOrderItemModel serviceOrderItem = new SAPCpiOutboundServiceOrderItemModel();
		serviceOrderItem.setSapCpiOutboundPriceComponents(new HashSet<>());
		return serviceOrderItem;
	}

	@Test
	public void mapServiceOrderMainTest() {
		
		OrderModel order = generateInitialOrder();
		SAPCpiOutboundServiceOrderModel outboundOrder = generateInitialOutboundOrder();
		
		serviceOrderMapper.mapServiceOrder(order, outboundOrder);
		
		
		//test service order attributes
		Assert.assertEquals(SERVICE_ORDER_TRANSACTION_TYPE, outboundOrder.getTransactionType());
		Assert.assertEquals(ORDER_CREATOIN_DATE, outboundOrder.getCreationDate());
		Assert.assertEquals(REQUEST_START_DATE, outboundOrder.getRequestedServiceStartDateTime());
		Assert.assertEquals(PARENT_ORDER_CODE, outboundOrder.getCommerceOrderId());
		
		
		//test partner-role attributes
		Optional.ofNullable(outboundOrder.getSapCpiOutboundPartnerRoles()).map(Collection::stream).orElseGet(Stream::empty).forEach(partnerRole -> {
			if(serviceOrderMapper.isContactPerson(partnerRole))
			{
				Assert.assertEquals(BUSINESS_PARTNER_ID, partnerRole.getPartnerId());
			}
		});
		
		
		//test item attributes
		Optional.ofNullable(outboundOrder.getSapCpiOutboundServiceOrderItems()).map(Collection::stream).orElseGet(Stream::empty).forEach(item -> {
			Assert.assertNotNull(item.getSapCpiOutboundPriceComponents());
			Assert.assertEquals(1, item.getSapCpiOutboundPriceComponents().size());
			SAPCpiOutboundPriceComponentModel item0Price = item.getSapCpiOutboundPriceComponents().stream().findFirst().get();
			if(serviceOrderMapper.isItemPriceCondition(item0Price, SERVICE_ITEM_PRICE_CONDITION_TYPE))
			{
				Assert.assertEquals(ITEM0_COMPUTED_VALUE, item0Price.getValue());
			}
		});
	}

	private OrderModel generateInitialOrder() {
		
		OrderModel order = new OrderModel();
		order.setCode(ORDER_CODE);
		calendar.set(YEAR, MONTH, DAY, HOUR, MINUTE, SECOND);
		
		order.setRequestedServiceStartDate(calendar.getTime());
		order.setCreationtime(calendar.getTime());
		
		ConsignmentModel serviceConsignment = new ConsignmentModel();
		OrderModel commerceOrder = new OrderModel();
		commerceOrder.setCode(PARENT_ORDER_CODE);
		serviceConsignment.setOrder(commerceOrder);
		order.setConsignments(new HashSet<>());
		order.getConsignments().add(serviceConsignment);
		
		B2BCustomerModel orderUser = new B2BCustomerModel();
		orderUser.setSapBusinessPartnerID(BUSINESS_PARTNER_ID);
		order.setUser(orderUser);
		
		order.setEntries(new ArrayList<>());	//needs to be instantiated properly for Skills.
		
		return order;
	}

	private SAPCpiOutboundServiceOrderModel generateInitialOutboundOrder() {
		
		SAPCpiOutboundServiceOrderModel outboundOrder = new SAPCpiOutboundServiceOrderModel();
		
		
		outboundOrder.setSapCpiOutboundPartnerRoles(new HashSet<>());
		
		SAPCpiOutboundPartnerRoleModel contactPersonPartnerRole = new SAPCpiOutboundPartnerRoleModel();
		contactPersonPartnerRole.setPartnerRoleCode(PartnerRoles.CONTACT.getCode());
		outboundOrder.getSapCpiOutboundPartnerRoles().add(contactPersonPartnerRole);
		
		SAPCpiOutboundPartnerRoleModel shipToPartnerRole = new SAPCpiOutboundPartnerRoleModel();
		shipToPartnerRole.setPartnerRoleCode(PartnerRoles.SHIP_TO.getCode());
		shipToPartnerRole.setPartnerId(B2B_UNIT_UID);
		outboundOrder.getSapCpiOutboundPartnerRoles().add(shipToPartnerRole);
		
		
		outboundOrder.setSapCpiOutboundOrderItems(new HashSet<>());
		outboundOrder.setSapCpiOutboundPriceComponents(new HashSet<>());
		
		SAPCpiOutboundOrderItemModel outboundItem0 = new SAPCpiOutboundOrderItemModel();
		outboundItem0.setEntryNumber(ITEM0_ENTRY);
		outboundItem0.setQuantity(ITEM0_QUANTITY);
		outboundOrder.getSapCpiOutboundOrderItems().add(outboundItem0);
		
		SAPCpiOutboundPriceComponentModel outboundPrice0 = new SAPCpiOutboundPriceComponentModel();
		outboundPrice0.setEntryNumber(ITEM0_ENTRY);
		outboundPrice0.setValue(ITEM0_VALUE);
		outboundPrice0.setConditionCode(SALES_ITEM_PRICE_CONDITION_TYPE);
		outboundOrder.getSapCpiOutboundPriceComponents().add(outboundPrice0);
		
		return outboundOrder;
	}
}

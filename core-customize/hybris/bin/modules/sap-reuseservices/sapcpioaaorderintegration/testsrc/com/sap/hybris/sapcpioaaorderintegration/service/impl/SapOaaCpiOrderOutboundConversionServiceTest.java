/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.sourcing.SourcingService;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapOaaCpiOrderOutboundConversionServiceTest {
	
	@InjectMocks
	private SapOaaCpiOrderOutboundConversionService cpiOrderOutboundConversionService;
	
	@Mock
	private SourcingService sourcingService;
	
	@Mock
	private BaseSiteService baseSiteService;
	
	@Mock
	private SourcingResponse sourcingResponse;
	
	@Mock
	private BaseSiteModel baseSiteModel;
	
	@Mock
	private BackendDownException BackendDownException;
	
	@Mock
	private OrderModel orderModel;
	
	
	@Test
	public void testConvertOrderToSapCpiOrder_Exception() {
		
		List<SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>> sapCpiOrderExchangeMappers = null;
		
		boolean exceptionThrown = false;
		cpiOrderOutboundConversionService.setSapCpiOrderMappers(sapCpiOrderExchangeMappers);
		try {
			cpiOrderOutboundConversionService.convertOrderToSapCpiOrder(orderModel);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		
	}
}

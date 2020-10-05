/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.service.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import rx.Observable;


/**
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOaaOutboundServiceImplTest
{
	@Mock
	private OutboundServiceFacade outboundServiceFacade;

	@Mock
	private SAPCpiOutboundOrderModel sapCpiOutboundOrderModel;

	@Mock
	private ResponseEntity<Map> responseEntity;

	@Mock
	private Observable<ResponseEntity<Map>> observable;

	@InjectMocks
	private SapCpiOaaOutboundServiceImpl cpiOaaOutboundServiceImpl;

	private static final String OUTBOUND_OAA_ORDER_OBJECT = "OutboundOaaOrder";
	private static final String OUTBOUND_OAA_ORDER_DESTINATION = "scpiOaaOrderDestination";

	@Before
	public void setup()
	{
		when(outboundServiceFacade.send(sapCpiOutboundOrderModel, OUTBOUND_OAA_ORDER_OBJECT, OUTBOUND_OAA_ORDER_DESTINATION)).
				thenReturn(observable);
	}

	@Test
	public void testSendOrder()
	{
		assertTrue(cpiOaaOutboundServiceImpl.sendOrder(sapCpiOutboundOrderModel).equals(observable));
	}

}

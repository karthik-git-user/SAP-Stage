/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapcpiomsreturnsexchange.service.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderCancellationModel;
import de.hybris.platform.sap.sapcpiomsreturnsexchange.service.SapCpiOmsReturnsOutboundConversionService;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;
import rx.Observable;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOmsOutboundReturnServiceImplTest {
	// Return Order Outbound
	private static final String OUTBOUND_RETURN_ORDER_OBJECT = "OutboundOMMReturnOMSReturnOrder";
	private static final String OUTBOUND_RETURN_ORDER_DESTINATION = "scpiReturnOrderDestination";

	// Return Order Cancellation Outbound
	private static final String OUTBOUND_RETURN_ORDER_CANCELLATION_OBJECT = "OutboundCancelOMMReturnOMSReturnOrder";
	private static final String OUTBOUND_RETURN_ORDER_CANCELLATION_DESTINATION = "scpiCancelReturnOrderDestination";

	@Mock
	private SapCpiOmsReturnsOutboundConversionService sapCpiOmsReturnsOutboundConversionService;

	@Mock
	private OutboundServiceFacade outboundServiceFacade;

	@InjectMocks
	private SapCpiOmsOutboundReturnServiceImpl sapCpiOmsOutboundReturnServiceImpl;

	@Test
	public void sendReturnOrder() {

		SAPCpiOutboundReturnOrderModel sapCpiOutboundReturnOrderModel = mock(SAPCpiOutboundReturnOrderModel.class);

		when(outboundServiceFacade.send(sapCpiOutboundReturnOrderModel, OUTBOUND_RETURN_ORDER_OBJECT,
				OUTBOUND_RETURN_ORDER_DESTINATION)).thenReturn(Observable.just(new ResponseEntity<>(HttpStatus.OK)));

		sapCpiOmsOutboundReturnServiceImpl.sendReturnOrder(sapCpiOutboundReturnOrderModel);

		verify(outboundServiceFacade, times(1)).send(sapCpiOutboundReturnOrderModel, OUTBOUND_RETURN_ORDER_OBJECT,
				OUTBOUND_RETURN_ORDER_DESTINATION);
	}

	@Test
	public void sendReturnOrderCancellation() {

		SAPCpiOutboundOrderCancellationModel sapCpiOutboundOrderCancellationModel = mock(
				SAPCpiOutboundOrderCancellationModel.class);

		when(outboundServiceFacade.send(sapCpiOutboundOrderCancellationModel, OUTBOUND_RETURN_ORDER_CANCELLATION_OBJECT,
				OUTBOUND_RETURN_ORDER_CANCELLATION_DESTINATION))
						.thenReturn(Observable.just(new ResponseEntity<>(HttpStatus.OK)));

		sapCpiOmsOutboundReturnServiceImpl.sendReturnOrderCancellation(sapCpiOutboundOrderCancellationModel);

		verify(outboundServiceFacade, times(1)).send(sapCpiOutboundOrderCancellationModel,
				OUTBOUND_RETURN_ORDER_CANCELLATION_OBJECT, OUTBOUND_RETURN_ORDER_CANCELLATION_DESTINATION);
	}

}

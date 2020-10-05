/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sapbillinginvoice.facade.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapbillinginvoicefacades.facade.impl.SapBillingInvoiceFacadeImpl;
import com.sap.hybris.sapbillinginvoicefacades.strategy.SapBillingInvoiceStrategy;
import com.sap.hybris.sapbillinginvoicefacades.utils.SapBillingInvoiceUtils;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;


/**
 *
 */
@UnitTest
public class SapBillingInvoiceFacadeImplTest
{

	@InjectMocks
	SapBillingInvoiceFacadeImpl sapBillingInvoiceFacadeImpl;

	@Mock
	private SapBillingInvoiceService sapBillingInvoiceService;

	@Mock
	private Map<String, SapBillingInvoiceStrategy> handlers;

	@Mock
	private SapBillingInvoiceUtils sapBillingInvoiceUtils;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getPdfTest() throws SapBillingInvoiceUserException
	{
		final byte[] pdfData = new byte[0];

		final String orderCode = "12345";

		final SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setCode(orderCode);
		sapOrder.setSapOrderType(SAPOrderType.SALES);
		sapOrder.setServiceOrderId("so12345");

		final SapBillingInvoiceStrategy sapBillingInvoiceServiceStrategy = Mockito.mock(SapBillingInvoiceStrategy.class);

		given(sapBillingInvoiceFacadeImpl.getServiceOrderBySapOrderCode(orderCode)).willReturn(sapOrder);
		given(sapBillingInvoiceUtils.getSapOrderType(sapOrder)).willReturn("SERVICE");
		given(handlers.get("SERVICE")).willReturn(sapBillingInvoiceServiceStrategy);
		given(sapBillingInvoiceServiceStrategy.getPDFData(sapOrder, "BILL12345")).willReturn(pdfData);

		final byte[] responsePdfData = sapBillingInvoiceFacadeImpl.getPDFData(orderCode, "BILL12345");

		Assert.assertEquals(pdfData, responsePdfData);


	}


}

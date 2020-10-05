/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.service.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.sapbillinginvoiceservices.client.impl.SapBillingInvoiceClientImpl;
import com.sap.hybris.sapbillinginvoiceservices.dao.SapBillingInvoiceDao;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.impl.SapBillingInvoiceServiceImpl;


/**
 *
 */

@UnitTest
public class SapBillingInvoiceServiceImplTest
{

	@InjectMocks
	SapBillingInvoiceServiceImpl sapBillingInvoiceServiceImpl;

	@Mock
	private SapBillingInvoiceDao billingInvoiceDao;

	@Mock
	private SapBillingInvoiceClientImpl sapBillingInvoiceClientImpl;

	@Mock
	private UserService userService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getPdfTest() throws SapBillingInvoiceUserException
	{
		final byte[] pdfData = new byte[0];

		final UserModel user = new UserModel();
		final OrderModel order = new OrderModel();
		order.setUser(user);
		setPk(user, 1);
		final SAPOrderModel sapOrder = new SAPOrderModel();
		sapOrder.setOrder(order);

		final B2BCustomerModel customer = new B2BCustomerModel();
		setPk(customer, 1);
		given(userService.getCurrentUser()).willReturn(customer);

		given(sapBillingInvoiceClientImpl.getPDFData("bill1234", sapOrder)).willReturn(pdfData);

		final byte[] responsePdfData = sapBillingInvoiceServiceImpl.getPDFData(sapOrder, "bill1234");

		Assert.assertEquals(pdfData, responsePdfData);

	}

	public static void setPk(final AbstractItemModel itemModel, final long pk)
	{
		try
		{
			final Field field = itemModel.getItemModelContext().getClass().getDeclaredField("pk");
			field.setAccessible(true);
			field.set(itemModel.getItemModelContext(), PK.fromLong(pk));
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			throw new RuntimeException("Cannot set pk on itemModel: " + itemModel, e);
		}
	}


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.sap.ppengine.client.dto.DiscountBase;
import com.sap.ppengine.client.dto.LineItemDomainSpecific;
import com.sap.ppengine.client.dto.SaleBase;
import com.sap.ppengine.client.util.RequestHelper;
import com.sap.ppengine.client.util.RequestHelperImpl;
import com.sap.retail.sapppspricing.impl.PPSAccessorHelper;


@SuppressWarnings("javadoc")
@UnitTest
public class PPSAccessorHelperTest
{

	public class PPSAccessorHelperForTest extends PPSAccessorHelper
	{

		@Override
		public RequestHelper getHelper()
		{
			return new RequestHelperImpl();
		}
	}

	private PPSAccessorHelper cut;

	@Before
	public void setUp()
	{
		cut = new PPSAccessorHelperForTest();
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testNextNonDiscountItem()
	{
		final List<LineItemDomainSpecific> lineItems = new ArrayList<>();
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();
		final SaleBase sale = new SaleBase();
		lineItem.setSale(sale);
		lineItems.add(lineItem);
		assertSame(sale, cut.nextNonDiscountItem(lineItems, 0).getRight());
	}

	@Test
	public void testNextNonDiscountItemNothingFound()
	{
		final List<LineItemDomainSpecific> lineItems = new ArrayList<>();
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();
		final DiscountBase discount = new DiscountBase();
		lineItem.setDiscount(discount);
		lineItems.add(lineItem);
		assertNull(cut.nextNonDiscountItem(lineItems, 0));
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyacpicustomerexchangemdmb2b.outbound.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BContactModel;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaCpiB2BCustomerDefaultConversionServiceTest
{

	@InjectMocks
	private final GigyaCpiB2BCustomerDefaultConversionService gigyaCpiB2BCustomerDefaultConversionService = new GigyaCpiB2BCustomerDefaultConversionService();

	@Mock
	private B2BCustomerModel b2bCustomerModel;

	@Mock
	private B2BUnitModel b2bUnitModel;

	@Mock
	private SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContactModel;

	@Test
	public void testConvertB2BContactToSapCpiBb2BContact()
	{
		Mockito.when(b2bCustomerModel.getDefaultB2BUnit()).thenReturn(null);
		Mockito.when(b2bCustomerModel.getGyUID()).thenReturn("123");

		sapCpiOutboundB2BContactModel = gigyaCpiB2BCustomerDefaultConversionService
				.convertB2BContactToSapCpiBb2BContact(b2bUnitModel, b2bCustomerModel, StringUtils.EMPTY);

		Assert.assertEquals("123", sapCpiOutboundB2BContactModel.getGigyaUID());
	}

}

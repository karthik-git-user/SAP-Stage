/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.impl.DefaultSAPConfigurationService;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultServiceUtils;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.Values;
import com.sap.retail.oaa.model.enums.Sapoaa_mode;


/**
 *
 */
@UnitTest
public class DefaultSourcingRequestMapperTest extends SourcingRequestMapperTestBase
{
	private static final String SALES_CHANNEL = "OAA_CHANNEL";
	private DefaultSourcingRequestMapper classUnderTest;
	private DefaultSAPConfigurationService sapCoreConfigurationService;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultSourcingRequestMapper();

		classUnderTest.setServiceUtils(new DefaultServiceUtils());

		//Mock get Delivery Mode Configuration
		sapCoreConfigurationService = EasyMock.createMockBuilder(DefaultSAPConfigurationService.class)
				.addMockedMethod("getPropertyAccessCollection").createMock();
		EasyMock.expect(sapCoreConfigurationService.getPropertyAccessCollection(CONFIG_PROPERTY_SAP_DELIVERY_MODES))
				.andReturn(new ArrayList<ConfigurationPropertyAccess>());
		EasyMock.replay(sapCoreConfigurationService);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationService);
	}

	@Test
	public void testMapCartToATPRequestFallback()
	{
		//GIVEN: A Cart with two entries
		final CartModel cart = getCart();

		//WHEN: no Mode is specified
		final RestServiceConfiguration restConfiguration = EasyMock.createNiceMock(RestServiceConfiguration.class);
		EasyMock.expect(restConfiguration.getConsumerId()).andReturn(OAA_CONSUMER_ID).anyTimes();
		EasyMock.expect(restConfiguration.getOaaProfile()).andReturn(OAA_PROFILE).anyTimes();
		EasyMock.replay(restConfiguration);

		try
		{
			final SourcingAbapRequest abapReq = classUnderTest.mapCartModelToSourcingRequest(cart, true, true, restConfiguration);
			assertNotNullObjAndObjToString(abapReq);

			final Values valReq = abapReq.getValues();
			assertNotNullObjAndObjToString(valReq);

			final SourcingRequest sourcingRequest = valReq.getSourcing();
			assertNotNullObjAndObjToString(sourcingRequest);

			//THEN:SalesChannel Should be null and OAA Profile must not be null
			Assert.assertNull(sourcingRequest.getSalesChannel());
			Assert.assertNotNull(sourcingRequest.getOaaProfileId());

			//Cart should match given cart
			assertCart(sourcingRequest.getCart());

		}
		catch (final SourcingException e)
		{
			Assert.fail("No Exception expected, but it was: " + e.getMessage());
		}
	}

	@Test
	public void testMapCartToATPRequestOAAProfile()
	{
		//GIVEN: A Cart with two entries
		final CartModel cart = getCart();

		//WHEN: OAA Profile is specified
		final RestServiceConfiguration restConfiguration = EasyMock.createNiceMock(RestServiceConfiguration.class);
		EasyMock.expect(restConfiguration.getMode()).andReturn(Sapoaa_mode.OAAPROFILE).anyTimes();
		EasyMock.expect(restConfiguration.getConsumerId()).andReturn(OAA_CONSUMER_ID).anyTimes();
		EasyMock.expect(restConfiguration.getOaaProfile()).andReturn(OAA_PROFILE).anyTimes();
		EasyMock.replay(restConfiguration);

		try
		{
			final SourcingAbapRequest abapReq = classUnderTest.mapCartModelToSourcingRequest(cart, true, true, restConfiguration);
			assertNotNullObjAndObjToString(abapReq);

			final Values valReq = abapReq.getValues();
			assertNotNullObjAndObjToString(valReq);

			final SourcingRequest sourcingRequest = valReq.getSourcing();
			assertNotNullObjAndObjToString(sourcingRequest);

			//THEN:SalesChannel Should be null and OAA Profile must not be null
			Assert.assertNull(sourcingRequest.getSalesChannel());
			Assert.assertNotNull(sourcingRequest.getOaaProfileId());

			//Cart should match given cart
			assertCart(sourcingRequest.getCart());

		}
		catch (final SourcingException e)
		{
			Assert.fail("No Exception expected, but it was: " + e.getMessage());
		}
	}

	@Test
	public void testMapCartToATPRequestSalesChannel()
	{
		//GIVEN: A Cart with two entries
		final CartModel cart = getCart();

		//WHEN: OAA Profile is specified
		final RestServiceConfiguration restConfiguration = EasyMock.createNiceMock(RestServiceConfiguration.class);
		EasyMock.expect(restConfiguration.getMode()).andReturn(Sapoaa_mode.SALESCHANNEL).anyTimes();
		EasyMock.expect(restConfiguration.getSalesChannel()).andReturn(SALES_CHANNEL).anyTimes();
		EasyMock.replay(restConfiguration);

		try
		{
			final SourcingAbapRequest abapReq = classUnderTest.mapCartModelToSourcingRequest(cart, true, true, restConfiguration);
			assertNotNullObjAndObjToString(abapReq);

			final Values valReq = abapReq.getValues();
			assertNotNullObjAndObjToString(valReq);

			final SourcingRequest sourcingRequest = valReq.getSourcing();
			assertNotNullObjAndObjToString(sourcingRequest);

			//THEN: OAA Profile Should be null and Sales Channel must not be null
			Assert.assertNull(sourcingRequest.getOaaProfileId());
			Assert.assertNotNull(sourcingRequest.getSalesChannel());

			//Cart should match given cart
			assertCart(sourcingRequest.getCart());

		}
		catch (final SourcingException e)
		{
			Assert.fail("No Exception expected, but it was: " + e.getMessage());
		}
	}

}

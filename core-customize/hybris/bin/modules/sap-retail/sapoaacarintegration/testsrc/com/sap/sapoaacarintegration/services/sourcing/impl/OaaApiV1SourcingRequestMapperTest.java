/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.impl.DefaultSAPConfigurationService;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultServiceUtils;
import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.Values;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultRestServiceConfiguration;


@UnitTest
public class OaaApiV1SourcingRequestMapperTest extends SourcingRequestMapperTestBase
{

	private static final String CONFIG_PROPERTY_SAP_DELIVERY_MODES = "sapDeliveryModes";

	private OaaApiV1SourcingRequestMapper classUnderTest = new OaaApiV1SourcingRequestMapper();

	@Mock
	private ServiceUtils serviceUtilMock;

	@Before
	public void setup()
	{
		classUnderTest = new OaaApiV1SourcingRequestMapper();

		classUnderTest.setServiceUtils(new DefaultServiceUtils());

		//Mock get Delivery Mode Configuration
		final DefaultSAPConfigurationService sapCoreConfigurationService = EasyMock
				.createMockBuilder(DefaultSAPConfigurationService.class).addMockedMethod("getPropertyAccessCollection").createMock();
		EasyMock.expect(sapCoreConfigurationService.getPropertyAccessCollection(CONFIG_PROPERTY_SAP_DELIVERY_MODES))
				.andReturn(new ArrayList<ConfigurationPropertyAccess>());
		EasyMock.replay(sapCoreConfigurationService);

		classUnderTest.setSapCoreConfigurationService(sapCoreConfigurationService);
	}


	@Test
	public void checkHeaderAttributesAndCart()
	{
		//GIVEN: A Cart with two entries
		final CartModel cart = getCart();

		final RestServiceConfiguration restConfiguration = new DefaultRestServiceConfiguration();
		restConfiguration.setConsumerId(OAA_CONSUMER_ID);


		final SourcingAbapRequest abapReq = classUnderTest.mapCartModelToSourcingRequest(cart, true, true, restConfiguration);
		assertNotNullObjAndObjToString(abapReq);

		final Values valReq = abapReq.getValues();
		assertNotNullObjAndObjToString(valReq);

		final SourcingRequest sourcingRequest = valReq.getSourcing();
		assertNotNullObjAndObjToString(sourcingRequest);

		//Cart should match given cart
		assertCart(sourcingRequest.getCart());


		assertEquals(OAA_CONSUMER_ID, sourcingRequest.getConsumerId());
		assertEquals(SapoaacommerceservicesConstants.RESERVATION_STATUS_CART, sourcingRequest.getStatus());

	}

}

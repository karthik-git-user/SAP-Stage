/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.service.impl;

import org.mockito.InjectMocks;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundAddressModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.sap.platform.sapcustomerlookupservice.service.impl.DefaultSapCustomerLookupConversionService;




@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCustomerLookupConversionServiceTest
{

	private static final String CUSTOMER_ID = "SAP123";
	private static final String CUSTOMER_UID = "commerce-1234-121312-1234234-54434";
	private static final String FIRST_NAME = "fname";
	private static final String LAST_NAME = "lname";
	private static final String TITLE_CODE = "Mr";


	private static final String LOGICALSYSTEMNAME = "QE6CLNT910";
	private static final String SENDER_NAME = "HBRGTSM07";
	private static final String SENDER_PORT = "HBRGTSM07";
	private static final String CLIENT = "910";
	private static final String TARGET_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=" + CLIENT;

	@Mock
	private SAPCpiOutboundCustomerModel sapCpiOutboundCMResult;

	@Mock
	private CustomerModel customerModel;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	private AddressModel addressModel;

	@Mock
	private TitleModel titleModel;

	@Mock
	private ModelService modelService;

	@Mock
	private SAPGlobalConfigurationDAO globalConfigurationDAO;
	
	@Mock
	private SAPGlobalConfigurationModel sapGlobalConfiguration;

	
	//Class Under test.
	private DefaultSapCustomerLookupConversionService defaultSapCustomerLookupConversionService_CUT;
	
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomerModel = new SAPCpiOutboundCustomerModel();
		final SAPCpiOutboundConfigModel sapCpiOutboundConfigModel = new SAPCpiOutboundConfigModel();
		final SAPCpiOutboundAddressModel sapCpiOutboundAddressModel = new SAPCpiOutboundAddressModel();
		customerModel = new CustomerModel();
		addressModel = new AddressModel();
		setCustomer(customerModel, addressModel);

		//HTTP Destination
		final SAPHTTPDestinationModel sapHTTPDestinationModel = mock(SAPHTTPDestinationModel.class);
		when(sapHTTPDestinationModel.getTargetURL()).thenReturn(TARGET_URL);

		when(modelService.create(SAPCpiOutboundCustomerModel.class)).thenReturn(sapCpiOutboundCustomerModel);
		when(modelService.create(SAPCpiOutboundAddressModel.class)).thenReturn(sapCpiOutboundAddressModel);
		when(modelService.create(SAPCpiOutboundConfigModel.class)).thenReturn(sapCpiOutboundConfigModel);
		// Logical system
		final SAPLogicalSystemModel defaultLogicalSystem = mock(SAPLogicalSystemModel.class);
		when(defaultLogicalSystem.isDefaultLogicalSystem()).thenReturn(true);
		when(defaultLogicalSystem.getSapLogicalSystemName()).thenReturn(LOGICALSYSTEMNAME);
		when(defaultLogicalSystem.getSenderName()).thenReturn(SENDER_NAME);
		when(defaultLogicalSystem.getSenderPort()).thenReturn(SENDER_PORT);
		when(defaultLogicalSystem.getSapHTTPDestination()).thenReturn(sapHTTPDestinationModel);

		final Set<SAPLogicalSystemModel> sapLogicalSystemModels = new HashSet<SAPLogicalSystemModel>();
		sapLogicalSystemModels.add(defaultLogicalSystem);

		// sap global configuration model
		final SAPGlobalConfigurationModel sapGlobalConfiguration = mock(SAPGlobalConfigurationModel.class);
		when(globalConfigurationDAO.getSAPGlobalConfiguration()).thenReturn(sapGlobalConfiguration);
		when(sapGlobalConfiguration.getSapLogicalSystemGlobalConfig()).thenReturn(sapLogicalSystemModels);	
		when(customerNameStrategy.splitName(customerModel.getName())).thenReturn(new String[]
				{ FIRST_NAME, LAST_NAME });
		defaultSapCustomerLookupConversionService_CUT = new DefaultSapCustomerLookupConversionService();
		ReflectionTestUtils.setField(defaultSapCustomerLookupConversionService_CUT, "modelService", modelService);
		ReflectionTestUtils.setField(defaultSapCustomerLookupConversionService_CUT, "customerNameStrategy", customerNameStrategy);
		ReflectionTestUtils.setField(defaultSapCustomerLookupConversionService_CUT, "globalConfigurationDAO", globalConfigurationDAO);
	}
	
	//Test the method convertCustomerToSapCpiLookupCustomer and check the results.
	@Test
	public void testConvertCustomerToSapCpiLookupCustomer() {
		SAPCpiOutboundCustomerModel sapCpiOutboundCMResult = defaultSapCustomerLookupConversionService_CUT.convertCustomerToSapCpiLookupCustomer(customerModel);
		assertEquals(CUSTOMER_ID, sapCpiOutboundCMResult.getCustomerId());
		assertEquals(CUSTOMER_UID, sapCpiOutboundCMResult.getUid());
		assertEquals(FIRST_NAME, sapCpiOutboundCMResult.getFirstName());
		assertEquals(LAST_NAME, sapCpiOutboundCMResult.getLastName());
		assertNotNull(sapCpiOutboundCMResult.getSapCpiConfig());

		assertEquals(LOGICALSYSTEMNAME, sapCpiOutboundCMResult.getSapCpiConfig().getReceiverName());
		assertEquals(LOGICALSYSTEMNAME, sapCpiOutboundCMResult.getSapCpiConfig().getReceiverPort());
		assertEquals(SENDER_NAME, sapCpiOutboundCMResult.getSapCpiConfig().getSenderName());
		assertEquals(SENDER_PORT, sapCpiOutboundCMResult.getSapCpiConfig().getSenderPort());

	}

	private void setCustomer(final CustomerModel customerModel, final AddressModel addressModel) {
		titleModel = mock(TitleModel.class);
		titleModel.setCode(TITLE_CODE);
		titleModel.setName(TITLE_CODE);
		customerModel.setName(FIRST_NAME + " " + LAST_NAME);
		customerModel.setDefaultShipmentAddress(addressModel);
		customerModel.setTitle(titleModel);
		customerModel.setCustomerID(CUSTOMER_ID);
		customerModel.setUid(CUSTOMER_UID);

	}

}

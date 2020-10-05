/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyacpicustomerexchangemdm.service.impl;

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
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerDestinationService;
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




@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaCpiMDMCustomerDefaultConversionServiceTest
{

	private static final String CUSTOMER_ID = "SAP123";
	private static final String CONSUMER_ID = "SAP1234";
	private static final String COUNTRY = "USA";
	private static final String APARTMENT = "apartment";
	private static final String BUILDING = "building";
	private static final String CELLPHONE = "123456";
	private static final String DISTRICT = "district";
	private static final String EMAIL = "abc@xyz";
	private static final String FAX = "12345";
	private static final String FIRST_NAME = "fname";
	private static final String LAST_NAME = "lname";
	private static final String PHONE1 = "123";
	private static final String PHONE2 = "456";
	private static final String POBOX = "5555";
	private static final String POSTALCODE = "555555";
	private static final String REGION = "RE";
	private static final String STREET_NAME = "streetName";
	private static final String STREET_NUMBER = "streetNUmber";
	private static final String TITLE_CODE = "Mr";
	private static final String TOWN = "town";

	private static final String BASESTORE = "electronics";
	private static final String SESSION_LANGUAGE = "en";

	private static final String LOGICALSYSTEMNAME = "QE6CLNT910";
	private static final String SENDER_NAME = "HBRGTSM07";
	private static final String SENDER_PORT = "HBRGTSM07";
	private static final String CLIENT = "910";
	private static final String TARGET_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=" + CLIENT;

	private static final String GIGYA_UID = "Gigya1234";


	@Mock
	SAPCpiOutboundCustomerModel sapCpiOutboundCustomerModel;

	//	@InjectMocks
	//	SapCpiMDMCustomerDefaultConversionService sapCpiMDMCustomerDefaultConversionService;

	@Mock
	CustomerModel customerModel;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Mock
	AddressModel addressModel;

	@Mock
	TitleModel titleModel;

	@Mock
	private ModelService modelService;

	@Mock
	private SAPGlobalConfigurationDAO globalConfigurationDAO;

	@InjectMocks
	private GigyaCpiMDMCustomerDefaultConversionService gigyaCpiMDMCustomerDefaultConversionService;

	@Mock
	private SapCpiCustomerDestinationService sapCpiCustomerDestinationService;


	@Test
	public void convertCustomerToSapCpiCustomer()
	{
		given(getCustomerNameStrategy().splitName(customerModel.getName())).willReturn(new String[]
		{ FIRST_NAME, LAST_NAME });
		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomerModel = gigyaCpiMDMCustomerDefaultConversionService
				.convertCustomerToSapCpiCustomer(customerModel, addressModel, BASESTORE, SESSION_LANGUAGE);
		assertEquals(SESSION_LANGUAGE, sapCpiOutboundCustomerModel.getSessionLanguage());
		assertEquals(CONSUMER_ID, sapCpiOutboundCustomerModel.getSapConsumerID());
		assertEquals(CUSTOMER_ID, sapCpiOutboundCustomerModel.getCustomerId());
		assertEquals(BASESTORE, sapCpiOutboundCustomerModel.getBaseStore());
		assertEquals(FIRST_NAME, sapCpiOutboundCustomerModel.getFirstName());
		assertEquals(LAST_NAME, sapCpiOutboundCustomerModel.getLastName());
		assertEquals(GIGYA_UID, sapCpiOutboundCustomerModel.getGigyaUID());

		assertNotNull(sapCpiOutboundCustomerModel.getSapCpiConfig());
		assertNotNull(sapCpiOutboundCustomerModel.getSapCpiOutboundAddress());

		assertEquals(LOGICALSYSTEMNAME, sapCpiOutboundCustomerModel.getSapCpiConfig().getReceiverName());
		assertEquals(LOGICALSYSTEMNAME, sapCpiOutboundCustomerModel.getSapCpiConfig().getReceiverPort());
		assertEquals(SENDER_NAME, sapCpiOutboundCustomerModel.getSapCpiConfig().getSenderName());
		assertEquals(SENDER_PORT, sapCpiOutboundCustomerModel.getSapCpiConfig().getSenderPort());

		assertEquals(1, sapCpiOutboundCustomerModel.getSapCpiOutboundAddress().size());

		final SAPCpiOutboundAddressModel sAPCpiOutboundAddressModel = sapCpiOutboundCustomerModel.getSapCpiOutboundAddress()
				.iterator().next();

		assertEquals(EMAIL, sAPCpiOutboundAddressModel.getEmail());
		assertEquals(STREET_NUMBER, sAPCpiOutboundAddressModel.getStreetNumber());
		assertEquals(STREET_NAME, sAPCpiOutboundAddressModel.getStreetName());
		assertEquals(TOWN, sAPCpiOutboundAddressModel.getCity());
		assertEquals(POSTALCODE, sAPCpiOutboundAddressModel.getPostalCode());
		assertEquals(POBOX, sAPCpiOutboundAddressModel.getPobox());
		assertEquals(PHONE1, sAPCpiOutboundAddressModel.getTelNumber());
		assertEquals(CELLPHONE, sAPCpiOutboundAddressModel.getCellphone());
		assertEquals(FIRST_NAME, sAPCpiOutboundAddressModel.getFirstName());
		assertEquals(LAST_NAME, sAPCpiOutboundAddressModel.getLastName());


	}

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final SAPCpiOutboundCustomerModel sapCpiOutboundCustomerModel = new SAPCpiOutboundCustomerModel();
		final SAPCpiOutboundConfigModel sAPCpiOutboundConfigModel = new SAPCpiOutboundConfigModel();
		final SAPCpiOutboundAddressModel sAPCpiOutboundAddressModel = new SAPCpiOutboundAddressModel();
		customerModel = new CustomerModel();
		addressModel = new AddressModel();
		setCustomerAndAddress(customerModel, addressModel);

		//HTTP Destination
		final SAPHTTPDestinationModel sapHTTPDestinationModel = mock(SAPHTTPDestinationModel.class);
		when(sapHTTPDestinationModel.getTargetURL()).thenReturn(TARGET_URL);

		when(getModelService().create(SAPCpiOutboundCustomerModel.class)).thenReturn(sapCpiOutboundCustomerModel);
		when(getModelService().create(SAPCpiOutboundAddressModel.class)).thenReturn(sAPCpiOutboundAddressModel);
		when(getModelService().create(SAPCpiOutboundConfigModel.class)).thenReturn(sAPCpiOutboundConfigModel);
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
		when(sapGlobalConfiguration.getSapLogicalSystemGlobalConfig()).thenReturn(sapLogicalSystemModels);
		when(sapCpiCustomerDestinationService.readSapLogicalSystem()).thenReturn(defaultLogicalSystem);
		when(sapCpiCustomerDestinationService.determineUrlDestination(defaultLogicalSystem)).thenReturn(TARGET_URL);
		when(sapCpiCustomerDestinationService.extractSapClient(defaultLogicalSystem)).thenReturn(CLIENT);

	}


	private void setCustomerAndAddress(final CustomerModel customerModel, final AddressModel addressModel)
	{
		addressModel.setEmail(EMAIL);
		addressModel.setStreetnumber(STREET_NUMBER);
		addressModel.setStreetname(STREET_NAME);
		addressModel.setTown(TOWN);
		final CountryModel country = Mockito.mock(CountryModel.class);
		country.setIsocode(COUNTRY);
		addressModel.setCountry(country);
		addressModel.setPostalcode(POSTALCODE);
		addressModel.setPobox(POBOX);
		addressModel.setPhone1(PHONE1);
		addressModel.setCellphone(PHONE2);
		addressModel.setFirstname(FIRST_NAME);
		addressModel.setLastname(LAST_NAME);
		addressModel.setAppartment(APARTMENT);
		addressModel.setBuilding(BUILDING);
		addressModel.setCellphone(CELLPHONE);
		addressModel.setDistrict(DISTRICT);
		addressModel.setFax(FAX);
		final RegionModel regionModel = mock(RegionModel.class);
		regionModel.setIsocode(REGION);
		regionModel.setIsocodeShort(REGION);
		addressModel.setRegion(regionModel);
		addressModel.setTitle(titleModel);


		final List<AddressModel> addressModelList = new ArrayList<AddressModel>();
		addressModelList.add(addressModel);
		titleModel = mock(TitleModel.class);
		titleModel.setCode(TITLE_CODE);
		titleModel.setName(TITLE_CODE);
		customerModel.setName(FIRST_NAME + " " + LAST_NAME);
		customerModel.setDefaultShipmentAddress(addressModel);
		customerModel.setAddresses(addressModelList);
		customerModel.setTitle(titleModel);
		customerModel.setCustomerID(CUSTOMER_ID);
		customerModel.setSapConsumerID(CONSUMER_ID);
		addressModel.setOwner(customerModel);
		final List<AddressModel> addresses = new ArrayList<>();
		addresses.add(addressModel);
		customerModel.setAddresses(addresses);

		customerModel.setGyUID(GIGYA_UID);
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	public SapCpiCustomerDestinationService getSapCpiCustomerDestinationService()
	{
		return sapCpiCustomerDestinationService;
	}



}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundAddressModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.fest.util.Collections;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sap.platform.sapcustomerlookupservice.actions.CustomerLookupCheckConsentForCustomer;
import com.sap.platform.sapcustomerlookupservice.actions.CustomerMasterLookupAction;
import com.sap.platform.sapcustomerlookupservice.actions.CustomerMasterLookupCheckEmailVerificationForCustomer;
import com.sap.platform.sapcustomerlookupservice.constants.SapcustomerlookupserviceConstants;
import com.sap.platform.sapcustomerlookupservice.service.SapCustomerLookupConversionService;
import com.sap.platform.sapcustomerlookupservice.service.impl.DefaultSapCustomerLookupConversionService;




@IntegrationTest
@RunWith(MockitoJUnitRunner.class)
public class SAPCustomerLookupIntegrationTest	{

	@Mock
	private OutboundServiceFacade mockOutboundServiceFacade;
	
	@Mock
	private SAPCpiOutboundCustomerModel mockSAPCpiOutboundCustomer;
	
	@Mock
	private AddressModel mockAddressModel;

	@Mock
	private TitleModel mockTitleModel;
	
	@Mock
	private CustomerModel mockCustomerModel;
	
	@Mock
	private StoreFrontCustomerProcessModel mockStoreFrontCustomerProcessModel;
	
	@Mock 
	private BaseStoreModel mockBaseStoreModel;
	
	@Mock
	private ConsentTemplateModel mockConsentTemplate;
	
	@Mock
	private CommerceConsentService mockCommerceConsentService;
	
	@Mock
	private SapCustomerLookupConversionService mockLookupConversionService;
	
	private CustomerMasterLookupCheckEmailVerificationForCustomer checkEmailVerificationForCustomer;
	
	private CustomerLookupCheckConsentForCustomer checkConsentForCustomer;
	
	private CustomerMasterLookupAction lookupAction;
	
	private Transition transitionResult;
    
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

	
	@BeforeClass
    public static void beforeClass() {
        Registry.setCurrentTenantByID("junit");
    }
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockCustomerModel = new CustomerModel();
		mockAddressModel = new AddressModel();
		setCustomerAndAddress(mockCustomerModel, mockAddressModel);
		
		when(mockStoreFrontCustomerProcessModel.getCustomer()).thenReturn(mockCustomerModel);
		when(mockStoreFrontCustomerProcessModel.getStore()).thenReturn(mockBaseStoreModel);
		when(mockBaseStoreModel.isCmsEmailVerificationEnabled()).thenReturn(true);
		when(mockBaseStoreModel.isCmsLookupEnabled()).thenReturn(true);

	}
	
	//Execute the Email Verfication Action, verify that it is completed successfully.
	@Test
	public void testCheckEmailVerificationForCustomer() {
		mockCustomerModel.setCmsEmailVerificationTimestamp(new Date());
		checkEmailVerificationForCustomer = new CustomerMasterLookupCheckEmailVerificationForCustomer();
		transitionResult = checkEmailVerificationForCustomer.executeAction(mockStoreFrontCustomerProcessModel);
		
		assertEquals(Transition.OK, transitionResult);
	}
	
	//Execute the Consent check Action, verify that it is completed successfully.
	@Test
	public void testCheckConsentForCustomer() {
		checkConsentForCustomer = new CustomerLookupCheckConsentForCustomer();
		checkConsentForCustomer.setCommerceConsentService(mockCommerceConsentService);
		when(mockCommerceConsentService.getConsentTemplate(eq(SapcustomerlookupserviceConstants.CMS_LOOKUP_ENABLE_CONSENT_ID), 
				eq(Integer.valueOf(1)), any(BaseSiteModel.class))).thenReturn(mockConsentTemplate);
		when(mockCommerceConsentService.hasEffectivelyActiveConsent(eq(mockCustomerModel), eq(mockConsentTemplate))).thenReturn(Boolean.TRUE);
		transitionResult = checkConsentForCustomer.executeAction(mockStoreFrontCustomerProcessModel);
		
		assertEquals(Transition.OK, transitionResult);
	}
	
	//Execute the Lookup Business Process Action, verify that it is completed successfully.
	@Test
	public void testLookupAction() {
		lookupAction = new CustomerMasterLookupAction();
		lookupAction.setSapCustomerLookupConversionService(mockLookupConversionService);
		lookupAction.setOutboundServiceFacade(mockOutboundServiceFacade);
		when(mockLookupConversionService.convertCustomerToSapCpiLookupCustomer(mockCustomerModel)).thenReturn(mockSAPCpiOutboundCustomer);
		
		Map<String, Object> bodyAttributes = new HashMap<String, Object>();
		bodyAttributes.put("timestamp", new Date());
		bodyAttributes.put("version", "1.2");
		bodyAttributes.put("responseStatus", "Success");
		bodyAttributes.put("responseMessage", "test LookUp Action");		
			 
		Map body = Map.of("responseKey", bodyAttributes);
					
		List<ResponseEntity<Map>> lookupOutboundResponseEntities = Arrays.asList(new ResponseEntity<Map>(body, HttpStatus.OK));
		
		when(mockOutboundServiceFacade.send(eq(mockSAPCpiOutboundCustomer), eq(CustomerMasterLookupAction.SAP_CUSTOMER_LOOKUP_OUTBOUND), 
				eq(CustomerMasterLookupAction.SAP_CUSTOMER_LOOKUP_MDM_DESTINATION)) ).thenReturn(Observable.from( lookupOutboundResponseEntities ));
		
		transitionResult = lookupAction.executeAction(mockStoreFrontCustomerProcessModel);
		assertEquals(Transition.OK, transitionResult);
	}

	private void setCustomerAndAddress(final CustomerModel mockCustomerModel, final AddressModel mockAddressModel) {
		mockAddressModel.setEmail(EMAIL);
		mockAddressModel.setStreetnumber(STREET_NUMBER);
		mockAddressModel.setStreetname(STREET_NAME);
		mockAddressModel.setTown(TOWN);
		final CountryModel country = Mockito.mock(CountryModel.class);
		country.setIsocode(COUNTRY);
		mockAddressModel.setCountry(country);
		mockAddressModel.setPostalcode(POSTALCODE);
		mockAddressModel.setPobox(POBOX);
		mockAddressModel.setPhone1(PHONE1);
		mockAddressModel.setCellphone(PHONE2);
		mockAddressModel.setFirstname(FIRST_NAME);
		mockAddressModel.setLastname(LAST_NAME);
		mockAddressModel.setAppartment(APARTMENT);
		mockAddressModel.setBuilding(BUILDING);
		mockAddressModel.setCellphone(CELLPHONE);
		mockAddressModel.setDistrict(DISTRICT);
		mockAddressModel.setFax(FAX);
		final RegionModel regionModel = mock(RegionModel.class);
		regionModel.setIsocode(REGION);
		regionModel.setIsocodeShort(REGION);
		mockAddressModel.setRegion(regionModel);
		mockAddressModel.setTitle(mockTitleModel);


		final List<AddressModel> mockAddressModelList = new ArrayList<AddressModel>();
		mockAddressModelList.add(mockAddressModel);
		mockTitleModel = mock(TitleModel.class);
		mockTitleModel.setCode(TITLE_CODE);
		mockTitleModel.setName(TITLE_CODE);
		mockCustomerModel.setName(FIRST_NAME + " " + LAST_NAME);
		mockCustomerModel.setDefaultShipmentAddress(mockAddressModel);
		mockCustomerModel.setAddresses(mockAddressModelList);
		mockCustomerModel.setTitle(mockTitleModel);
		mockCustomerModel.setCustomerID(CUSTOMER_ID);
		mockCustomerModel.setSapConsumerID(CONSUMER_ID);
		mockAddressModel.setOwner(mockCustomerModel);
		final List<AddressModel> addresses = new ArrayList<>();
		addresses.add(mockAddressModel);
		mockCustomerModel.setAddresses(addresses);
	}



}

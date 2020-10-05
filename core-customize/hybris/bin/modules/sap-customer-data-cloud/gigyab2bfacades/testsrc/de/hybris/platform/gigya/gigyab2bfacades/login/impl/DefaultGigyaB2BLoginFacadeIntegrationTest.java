/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bfacades.login.impl;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;


import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSLogger;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaGroupsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaOrganisationData;
import de.hybris.platform.gigya.gigyab2bservices.auth.GigyaAuthService;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaUserManagementMode;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.ModelInitializationException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;


@IntegrationTest
public class DefaultGigyaB2BLoginFacadeIntegrationTest extends ServicelayerTest {

	private static final String SAMPLE_UID_SIGNATURE = "sample- uid-sig";
	private static final String SAMPLE_UID_SIGNATURE_TS = "sample-uid-sig-ts";
	private static final String SAMPLE_GIGYA_SITE_SECRET_KEY = "sample-gigya-site-secret-key";
	private static final String SAMPLE_GIGYA_USER_KEY = "sample-gigya-user-key";
	private static final int STATUS_CODE_200 = 200;

	// injecting class object here
	@Resource
	@InjectMocks
	private GigyaLoginFacade gigyaLoginFacade;

	@Resource
	@InjectMocks
	private GigyaLoginService gigyaLoginService;
	

	@Resource
	private ModelService modelService;

	@Resource(name = "gigyaConfigGenericDao")
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Resource(name = "gigyaUserGenericDao")
	private GenericDao<CustomerModel> gigyaUserGenericDao;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private GSResponse gsResponse;

	@Mock
	private GSObject gsObject;
	
	@Mock
	private GigyaUserObject gigyaUserObject;
	
	@Mock
	private GigyaAuthService gigyaAuthService; 
	

	private GigyaJsOnLoginInfo jsInfo = new GigyaJsOnLoginInfo();

	private GigyaConfigModel gigyaConfig = new GigyaConfigModel();

	private CMSSiteModel cmsSite = new CMSSiteModel();

	private GSObject gsObjectModel = new GSObject();
	
	private GSObject updatedGsObject = new GSObject();

	private GSObject gsObjectData = new GSObject();
	
	private GSObject updatedGsObjectData = new GSObject();

	private GSResponse gsResponseModel;
	
	private GSResponse updatedGsResponse;
	
	private GSObject groupData = new GSObject();
	
	private GSObject orgData = new GSObject();
	
	private GSArray gigyaOrgData = new GSArray();
	

	@Before
	public void setUp() throws ImpExException, Exception{
		
		ServicelayerTest.createCoreData();
		
		jsInfo.setUIDSignature(SAMPLE_UID_SIGNATURE);
		jsInfo.setSignatureTimestamp(SAMPLE_UID_SIGNATURE_TS);
		gigyaConfig.setGigyaUserKey(SAMPLE_GIGYA_USER_KEY);
		gigyaConfig.setGigyaUserSecret(SAMPLE_GIGYA_USER_KEY);
		gigyaConfig.setMode(GigyaUserManagementMode.RAAS);
		cmsSite.setGigyaConfig(gigyaConfig);
		orgData.put("bpid","MyOrgBPID");
		orgData.put("orgId","MyOrgID");
		gigyaOrgData.add(orgData);
		groupData.put("organizations",gigyaOrgData);
		importCsv("/gigyab2bfacades/testGigyaB2BLoginData.impex", "utf-8");
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testGigyaLoginWithRegisterUser()
			throws InvalidClassException, GSKeyNotFoundException, NullPointerException, DuplicateUidException {
		
		jsInfo.setUID("dummyemail@mail.com");

		gsObjectModel.put("email", "dummyemail@mail.com");
		gsObjectModel.put("firstName", "dummyfirstname");
		gsObjectModel.put("lastName", "dummylastname");
		
		gsObjectData.put("profile", gsObjectModel);
		gsObjectData.put("groups", groupData);
		gsObjectData.put("UID", "dummyemail@mail.com");

		gsResponseModel = new GSResponse("gsResponse", gsObjectData, 1, new GSLogger());
		doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfig(anyString(), any(), any(), anyInt(),
				anyInt());
		doReturn(gsObject).when(gsResponse).getData();
		doReturn(STATUS_CODE_200).when(gsObject).getInt("statusCode");
		doReturn(Integer.parseInt("0")).when(gsObject).getInt("errorCode");
		doReturn(gsResponseModel).when(gigyaService).callRawGigyaApiWithConfigAndObject(anyString(), any(), any(),
				anyInt(), anyInt());
		assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig()));
		B2BCustomerModel customer = (B2BCustomerModel) gigyaUserGenericDao
				.find(Collections.singletonMap(B2BCustomerModel.UID, "dummyemail@mail.com")).get(0);
		assertNotNull(customer);
		assertEquals("dummyfirstname dummylastname", customer.getName());
		getModelService().remove(customer);
	}
	@Test
	public void testGigyaLoginWithUpdateCustomer()
			throws InvalidClassException, GSKeyNotFoundException, NullPointerException {
		
		gsObjectModel.put("email", "email@mail.com");
		gsObjectModel.put("firstName", "First Name");
		gsObjectModel.put("lastName", "Last Name");
		gsObjectData.put("groups", groupData);
		gsObjectData.put("profile", gsObjectModel);
		gsObjectData.put("UID", "email@mail.com");
		

		jsInfo.setUID("email@mail.com");

		gsResponseModel = new GSResponse("gsResponse", gsObjectData, 1, new GSLogger());
		
		
		updatedGsObject.put("email", "email@mail.com");
		updatedGsObject.put("firstName", "Mock First Name");
		updatedGsObject.put("lastName", "Last Name");
		updatedGsObjectData.put("groups", groupData);
		updatedGsObjectData.put("profile", updatedGsObject);
		updatedGsObjectData.put("UID", "email@mail.com");
		updatedGsResponse = new GSResponse("gsResponse", updatedGsObjectData, 1, new GSLogger());
	
		doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfig(anyString(), any(), any(), anyInt(),
				anyInt());
		doReturn(gsObject).when(gsResponse).getData();
		doReturn(STATUS_CODE_200).when(gsObject).getInt("statusCode");
		doReturn(Integer.parseInt("0")).when(gsObject).getInt("errorCode");
		Mockito.when(gigyaService.callRawGigyaApiWithConfigAndObject(anyString(), any(), any(), anyInt(),
				anyInt())).thenReturn(gsResponseModel, updatedGsResponse);
		//Create B2B User
		gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig());
		//Update B2B User
		assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig()));
		B2BCustomerModel customer = (B2BCustomerModel) gigyaUserGenericDao
				.find(Collections.singletonMap(B2BCustomerModel.UID, "email@mail.com")).get(0);
		assertNotNull(customer);
		assertEquals("Mock First Name Last Name", customer.getName());
		getModelService().remove(customer);

	}
	protected ModelService getModelService()
	{
		return modelService;
	}

}

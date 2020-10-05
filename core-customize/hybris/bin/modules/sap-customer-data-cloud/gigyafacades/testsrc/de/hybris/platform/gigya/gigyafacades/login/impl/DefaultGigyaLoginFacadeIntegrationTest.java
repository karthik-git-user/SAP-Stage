/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.login.impl;

import java.io.InvalidClassException;
import java.util.Collections;

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
import org.mockito.MockitoAnnotations;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSLogger;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaUserManagementMode;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

@IntegrationTest
public class DefaultGigyaLoginFacadeIntegrationTest extends ServicelayerTest {

	private static final String SAMPLE_UID = "customer02";
	private static final String SAMPLE_UID_SIGNATURE = "sample- uid-sig";
	private static final String SAMPLE_UID_SIGNATURE_TS = "sample-uid-sig-ts";
	private static final String SAMPLE_GIGYA_USER_KEY = "sample-gigya-user-key";
	private static final int STATUS_CODE_200 = 200;

	// injecting class object here
	@Resource
	@InjectMocks
	private GigyaLoginFacade gigyaLoginFacade;

	@Resource
	@InjectMocks
	private GigyaLoginService gigyaLoginService;

	@Mock
	private GigyaService gigyaService;

	@Mock
	private GSResponse gsResponse;

	@Resource(name = "gigyaConfigGenericDao")
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Resource(name = "gigyaUserGenericDao")
	private GenericDao<CustomerModel> gigyaUserGenericDao;

	@Mock
	private GSObject gsObject;

	@Mock
	private GigyaUserObject gigyaUserObject;

	private GigyaJsOnLoginInfo jsInfo = new GigyaJsOnLoginInfo();

	private GigyaConfigModel gigyaConfig = new GigyaConfigModel();

	private CMSSiteModel cmsSite = new CMSSiteModel();

	private GSObject gsObjectModel = new GSObject();

	private GSObject gsObjectData = new GSObject();

	private GSResponse gsResponseModel;

	@Before
	public void setUp() throws ImpExException {

		jsInfo.setUIDSignature(SAMPLE_UID_SIGNATURE);
		jsInfo.setSignatureTimestamp(SAMPLE_UID_SIGNATURE_TS);
		gigyaConfig.setGigyaUserSecret(SAMPLE_GIGYA_USER_KEY);
		gigyaConfig.setGigyaUserKey(SAMPLE_GIGYA_USER_KEY);
		gigyaConfig.setMode(GigyaUserManagementMode.RAAS);

		cmsSite.setGigyaConfig(gigyaConfig);
		importCsv("/gigyafacades/testGigyaUserData.impex", "utf-8");
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testGigyaLoginWithRegisterUser()
			throws InvalidClassException, GSKeyNotFoundException, NullPointerException, DuplicateUidException {

		jsInfo.setUID(SAMPLE_UID);

		gsObjectModel.put("email", "abcd@mock.com");
		gsObjectModel.put("firstName", "Mock Last Name");
		gsObjectModel.put("lastName", "Mock Last Name");
		gsObjectData.put("profile", gsObjectModel);
		gsObjectData.put("UID", SAMPLE_UID);

		gsResponseModel = new GSResponse("gsResponse", gsObjectData, 1, new GSLogger());

		doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfig(anyString(), any(), any(), anyInt(),
				anyInt());
		doReturn(gsObject).when(gsResponse).getData();
		doReturn(STATUS_CODE_200).when(gsObject).getInt("statusCode");
		doReturn(Integer.parseInt("0")).when(gsObject).getInt("errorCode");
		doReturn(gsResponseModel).when(gigyaService).callRawGigyaApiWithConfigAndObject(anyString(), any(), any(),
				anyInt(), anyInt());
		
		assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig()));
		CustomerModel customer = (CustomerModel) gigyaUserGenericDao
				.find(Collections.singletonMap(CustomerModel.UID, "abcd@mock.com")).get(0);
		assertNotNull(customer);
	}

	@Test
	public void testGigyaLoginWithUpdateCustomer()
			throws InvalidClassException, GSKeyNotFoundException, NullPointerException, ImpExException {

		gsObjectModel.put("email", "newemailid@mock.com");
		gsObjectModel.put("firstName", "Mock Last Name");
		gsObjectModel.put("lastName", "Mock Last Name");
		gsObjectData.put("profile", gsObjectModel);
		gsObjectData.put("UID", "customer01");

		jsInfo.setUID("customer01");

		gsResponseModel = new GSResponse("gsResponse", gsObjectData, 1, new GSLogger());

		doReturn(gsResponse).when(gigyaService).callRawGigyaApiWithConfig(anyString(), any(), any(), anyInt(),
				anyInt());
		doReturn(gsObject).when(gsResponse).getData();
		doReturn(STATUS_CODE_200).when(gsObject).getInt("statusCode");
		doReturn(Integer.parseInt("0")).when(gsObject).getInt("errorCode");
		doReturn(gsResponseModel).when(gigyaService).callRawGigyaApiWithConfigAndObject(anyString(), any(), any(),
				anyInt(), anyInt());

		assertTrue(gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig()));
		CustomerModel customer = (CustomerModel) gigyaUserGenericDao
				.find(Collections.singletonMap(CustomerModel.UID, "newemailid@mock.com")).get(0);
		assertNotNull(customer);
		assertEquals("Mock Last Name Mock Last Name", customer.getName());

	}

}

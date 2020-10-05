/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.atp.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPBatchResponse;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResponse;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.common.util.impl.DefaultServiceUtils;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultRestServiceConfiguration;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpEntityBuilder;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpHeaderProvider;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultURLProvider;


/**
 */
@UnitTest
public class DefaultATPServiceTest
{
	private static final String CART_GUID = "7ade6a19-e073-49c5-8082-1af99c0873c6";
	private static final String CART_GUID_ENCODED = "7ade6a19e07349c580821af99c0873c6";
	private static final String ITEM_ID_1 = "01";
	private static final String ITEM_ID_2 = "02";
	private static final String EXTERNAL_ID_1 = CART_GUID_ENCODED + "-" + ITEM_ID_1;
	private static final String EXTERNAL_ID_2 = CART_GUID_ENCODED + "-" + ITEM_ID_2;
	private static final String TARGET_URL = "http://www.sap.com";
	private static final String PASSWORD = "password";
	private static final String USER = "user";
	private static final String CAR_CLIENT = "carClient";
	private static final String OAA_PROFILE = "oaaProfile";
	private static final String ARTICLE_1 = "Article1";
	private static final String ARTICLE_2 = "Article2";
	private static final String SOURCE_1 = "Source1";
	private static final String SOURCE_2 = "Source2";
	private static final Date ATP_DATE_1 = new Date(System.currentTimeMillis());
	private static final Date ATP_DATE_2 = new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000);
	private static final Double ATP_QTY_1 = new Double(5.0);
	private static final Double ATP_QTY_2 = new Double(10.0);
	private static final String UNIT_PCE = "ST";
	private static final String TARGET_URL_WITH_QUERY_PARAMS = TARGET_URL + "?oaaProfile=" + OAA_PROFILE + "&unit=" + UNIT_PCE
			+ "&externalId=" + EXTERNAL_ID_1;
	private static final String TARGET_URL_WITH_QUERY_PARAMS_EXT_IDS = TARGET_URL + "?oaaProfile=" + OAA_PROFILE + "&unit="
			+ UNIT_PCE + "&externalIds=" + EXTERNAL_ID_1 + "," + EXTERNAL_ID_2;

	private DefaultATPService classUnderTest = null;
	private ServiceUtils serviceUtils;
	private DefaultATPResourcePathBuilder atpResourcePathBuilder;
	private RestServiceConfiguration restServiceConfiguration = new DefaultRestServiceConfiguration();

	@Before
	public void setup() throws SourcingException, URISyntaxException
	{
		classUnderTest = EasyMock.createMockBuilder(DefaultATPService.class).addMockedMethod("initialize").createMock();
		serviceUtils = new DefaultServiceUtils();

		atpResourcePathBuilder = new DefaultATPResourcePathBuilder();
		atpResourcePathBuilder.setServiceUtils(serviceUtils);

		classUnderTest.setAtpResourcePathBuilder(atpResourcePathBuilder);
		Assert.assertNotNull(classUnderTest.getAtpResourcePathBuilder());

		//Mock RestTemplate
		final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);
		restTemplateMock.setMessageConverters(EasyMock.anyObject(List.class));
		EasyMock.expectLastCall();
		EasyMock.replay(restTemplateMock);
		classUnderTest.setRestTemplate(restTemplateMock);
		Assert.assertNotNull(classUnderTest.getRestTemplate());

		// Mock HttpHeaderProvider
		final DefaultHttpHeaderProvider httpHeaderProviderMock = EasyMock.createNiceMock(DefaultHttpHeaderProvider.class);
		EasyMock
				.expect(httpHeaderProviderMock.compileHttpHeader(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
				.andReturn(new HttpHeaders());
		EasyMock.expect(httpHeaderProviderMock.appendCsrfToHeader(EasyMock.anyObject(HttpHeaders.class),
				EasyMock.anyObject(HttpHeaders.class))).andReturn(new HttpHeaders());
		EasyMock.expect(httpHeaderProviderMock.appendCookieToHeader(EasyMock.anyObject(HttpHeaders.class),
				EasyMock.anyObject(HttpHeaders.class))).andReturn(new HttpHeaders());
		EasyMock.replay(httpHeaderProviderMock);
		classUnderTest.setHttpHeaderProvider(httpHeaderProviderMock);
		Assert.assertNotNull(classUnderTest.getHttpHeaderProvider());

		// Mock HttpEntityBuilder
		final HttpEntity entity = new HttpEntity(null, null);
		final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
		EasyMock.expect(httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class))).andReturn(entity).anyTimes();
		EasyMock.replay(httpEntityBuilderMock);
		classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);
		Assert.assertNotNull(classUnderTest.getHttpEntityBuilder());

		// Mock URL Provider
		final DefaultURLProvider urlProviderMock = EasyMock.createNiceMock(DefaultURLProvider.class);
		EasyMock.expect(urlProviderMock.compileURI(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class))).andReturn(new URI(TARGET_URL)).anyTimes();
		EasyMock.replay(urlProviderMock);
		classUnderTest.setUrlProvider(urlProviderMock);
		Assert.assertNotNull(classUnderTest.getUrlProvider());


		restServiceConfiguration = EasyMock.createMockBuilder(DefaultRestServiceConfiguration.class)
				.addMockedMethod("initializeConfiguration").createMock();
		classUnderTest.setRestServiceConfiguration(restServiceConfiguration);
		Assert.assertNotNull(classUnderTest.getRestServiceConfiguration());


		final SessionService sessionService = EasyMock.createNiceMock(SessionService.class);

		classUnderTest.setSessionService(sessionService);

		restServiceConfiguration.setPassword(PASSWORD);
		restServiceConfiguration.setUser(USER);
		restServiceConfiguration.setTargetUrl(TARGET_URL);
		restServiceConfiguration.setSapCarClient(CAR_CLIENT);
		restServiceConfiguration.setOaaProfile(OAA_PROFILE);
	}

	@Test
	public void callRestAvailabilityServiceWithoutProduct()
	{
		List<ATPAvailability> atpAvailModel = null;
		//ATP Product Service
		try
		{
			atpAvailModel = classUnderTest.callRestAvailabilityServiceForProduct(CART_GUID, ITEM_ID_1, new ProductModel());
			Assert.fail("ATPException expected because no product code is set");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpAvailModel);
		}

		//ATP Product/Store Service
		try
		{
			atpAvailModel = classUnderTest.callRestAvailabilityServiceForProductAndSource(CART_GUID, ITEM_ID_1, new ProductModel(),
					"0001");
			Assert.fail("ATPException expected because no product code is set");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpAvailModel);
		}
	}

	@Test
	public void callRestAvailabilityServiceWithoutProductsList()
	{
		List<ProductModel> productList = null;
		List<ATPProductAvailability> atpProductAvailList = null;

		final List<String> itemIdList = new ArrayList<String>();
		itemIdList.add(ITEM_ID_1);

		try
		{
			atpProductAvailList = classUnderTest.callRestAvailabilityServiceForProducts(CART_GUID, itemIdList, UNIT_PCE,
					productList);
			Assert.fail("ATPException expected because no product code is set");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpProductAvailList);
		}

		// No OAA Profile set
		try
		{
			restServiceConfiguration.setOaaProfile(null);
			atpProductAvailList = classUnderTest.callRestAvailabilityServiceForProducts(CART_GUID, itemIdList, UNIT_PCE,
					productList);
			Assert.fail("ATPException expected because the OAA profile is set to null");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpProductAvailList);
		}

		// ATP Products Service empty list
		try
		{
			productList = new ArrayList<>();
			atpProductAvailList = classUnderTest.callRestAvailabilityServiceForProducts(CART_GUID, itemIdList, UNIT_PCE,
					productList);
			Assert.fail("ATPException expected because no products are in the list");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpProductAvailList);
		}
	}


	@Test
	public void callRestAvailabilityServiceWithoutSourcesList()
	{
		List<ATPProductAvailability> atpProductAvailList = null;
		List<String> sourcesList = null;

		final ProductModel productModel = new ProductModel();
		productModel.setCode(ARTICLE_1);

		// ATP Product/ List of Sources Service
		try
		{
			atpProductAvailList = classUnderTest.callRestAvailabilityServiceForProductAndSources(CART_GUID, ITEM_ID_1, productModel,
					sourcesList);
			Assert.fail("ATPException expected because no sources are set");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpProductAvailList);
		}


		sourcesList = new ArrayList<>();
		try
		{
			atpProductAvailList = classUnderTest.callRestAvailabilityServiceForProductAndSources(CART_GUID, ITEM_ID_1, productModel,
					sourcesList);
			Assert.fail("ATPException expected because no sources are set");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpProductAvailList);
		}
	}

	@Test
	public void callRestAvailabilityServiceWithoutSource()
	{
		List<ATPAvailability> atpAvailModel = null;
		final ProductModel productModel = new ProductModel();
		productModel.setCode(ARTICLE_1);

		try
		{
			atpAvailModel = classUnderTest.callRestAvailabilityServiceForProductAndSource(CART_GUID, ITEM_ID_1, productModel, null);
			Assert.fail("ATPException expected because no source code is set");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpAvailModel);
		}

		try
		{
			atpAvailModel = classUnderTest.callRestAvailabilityServiceForProductAndSource(CART_GUID, ITEM_ID_1, new ProductModel(),
					"");
			Assert.fail("ATPException expected because no source code is set");
		}
		catch (final ATPException e)
		{
			Assert.assertNotNull(e.getMessage());
			Assert.assertNull(atpAvailModel);
		}
	}




	@Test
	public void callRestAvailabilityServiceForProduct() throws ATPException, URISyntaxException
	{
		final Double QUANTITY = new Double("10");
		final Date ATP_DATE = ATP_DATE_1;

		final ProductModel productModel = new ProductModel();
		productModel.setCode(ARTICLE_1);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);
		productModel.setUnit(unitModel);

		// aggregated ATP availabilities
		final List<ATPAvailability> availabilityList = new ArrayList<>();
		final ATPAvailability availItem = new ATPAvailability();
		availItem.setAtpDate(ATP_DATE);
		availItem.setQuantity(QUANTITY);
		availabilityList.add(availItem);

		final ResponseEntity<ATPResponse> response = new ResponseEntity<>(new ATPResponse(), HttpStatus.OK);

		final URI targetUri = new URI(TARGET_URL_WITH_QUERY_PARAMS);

		final HttpEntity entity = new HttpEntity(new HttpHeaders());

		// Mock HttpEntityBuilder
		final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
		EasyMock.expect(httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class))).andReturn(entity).anyTimes();
		EasyMock.replay(httpEntityBuilderMock);
		classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);


		// Mock RestTemplate
		final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);
		EasyMock.expect(restTemplateMock.exchange(targetUri, HttpMethod.GET, entity, ATPResponse.class)).andReturn(response);
		EasyMock.replay(restTemplateMock);
		classUnderTest.setRestTemplate(restTemplateMock);


		// Mock DefaultATPResultHandler
		final DefaultATPResultHandler resultHandlerMock = EasyMock.createNiceMock(DefaultATPResultHandler.class);
		EasyMock.expect(resultHandlerMock.extractATPAvailabilityFromATPResponse(EasyMock.anyObject(ATPResponse.class)))
				.andReturn(availabilityList).anyTimes();
		EasyMock.replay(resultHandlerMock);
		classUnderTest.setAtpResultHandler(resultHandlerMock);
		Assert.assertNotNull(classUnderTest.getAtpResultHandler());

		// Call rest service method
		final List<ATPAvailability> actATPResult = classUnderTest.callRestAvailabilityServiceForProduct(CART_GUID, ITEM_ID_1,
				productModel);
		Assert.assertNotNull(actATPResult);
		Assert.assertNotNull(actATPResult.toString());

		assertEqualsATPAvailability(availabilityList, actATPResult);
	}

	@Test
	public void callRestAvailabilityServiceForProductAndSource() throws ATPException, URISyntaxException
	{
		final Double QUANTITY = new Double("10");
		final Date ATP_DATE = ATP_DATE_1;

		final ProductModel productModel = new ProductModel();
		productModel.setCode(ARTICLE_1);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);
		productModel.setUnit(unitModel);

		// aggregated ATP availabilities
		final List<ATPAvailability> availabilityList = new ArrayList<>();
		final ATPAvailability availItem = new ATPAvailability(QUANTITY, ATP_DATE);
		availabilityList.add(availItem);

		final ResponseEntity<ATPResponse> response = new ResponseEntity<>(new ATPResponse(), HttpStatus.OK);

		final URI targetUri = new URI(TARGET_URL_WITH_QUERY_PARAMS);

		final HttpEntity entity = new HttpEntity(new HttpHeaders());

		// Mock HttpEntityBuilder
		final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
		EasyMock.expect(httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class))).andReturn(entity).anyTimes();
		EasyMock.replay(httpEntityBuilderMock);
		classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);


		// Mock RestTemplate
		final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);
		EasyMock.expect(restTemplateMock.exchange(targetUri, HttpMethod.GET, entity, ATPResponse.class)).andReturn(response);
		EasyMock.replay(restTemplateMock);
		classUnderTest.setRestTemplate(restTemplateMock);


		// Mock DefaultATPResultHandler
		final DefaultATPResultHandler resultHandlerMock = EasyMock.createNiceMock(DefaultATPResultHandler.class);
		EasyMock.expect(resultHandlerMock.extractATPAvailabilityFromATPResponse(EasyMock.anyObject(ATPResponse.class)))
				.andReturn(availabilityList).anyTimes();
		EasyMock.replay(resultHandlerMock);
		classUnderTest.setAtpResultHandler(resultHandlerMock);


		// Call rest service method
		final List<ATPAvailability> actATPResult = classUnderTest.callRestAvailabilityServiceForProductAndSource(CART_GUID,
				ITEM_ID_1, productModel, "1000");
		Assert.assertNotNull(actATPResult);
		Assert.assertNotNull(actATPResult.toString());

		assertEqualsATPAvailability(availabilityList, actATPResult);
	}

	@Test
	public void callRestAvailabilityServiceForProductsTest() throws URISyntaxException
	{
		// Build product list
		final List<ProductModel> productList = new ArrayList<>();
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);

		final ProductModel productModel1 = new ProductModel();
		productModel1.setCode(ARTICLE_1);
		productModel1.setUnit(unitModel);
		productList.add(productModel1);

		final ProductModel productModel2 = new ProductModel();
		productModel2.setCode(ARTICLE_2);
		productModel2.setUnit(unitModel);
		productList.add(productModel2);

		// aggregated ATP availabilities
		final List<ATPAvailability> availabilityList = new ArrayList<>();
		final ATPAvailability availItem1 = new ATPAvailability();
		availItem1.setAtpDate(ATP_DATE_1);
		availItem1.setQuantity(ATP_QTY_1);
		availabilityList.add(availItem1);

		final ATPAvailability availItem2 = new ATPAvailability();
		availItem2.setAtpDate(ATP_DATE_2);
		availItem2.setQuantity(ATP_QTY_2);
		availabilityList.add(availItem2);

		// build ATP Batch response object
		final List<ATPProductAvailability> atpProductAvailabilityList = new ArrayList<>();
		final ATPProductAvailability atpProductAvailability1 = new ATPProductAvailability();
		atpProductAvailability1.setArticleId(ARTICLE_1);
		atpProductAvailability1.setAvailabilityList(availabilityList);
		atpProductAvailabilityList.add(atpProductAvailability1);

		final ATPProductAvailability atpProductAvailability2 = new ATPProductAvailability();
		atpProductAvailability2.setArticleId(ARTICLE_2);
		atpProductAvailability2.setAvailabilityList(availabilityList);
		atpProductAvailabilityList.add(atpProductAvailability2);

		final ResponseEntity<ATPBatchResponse> response = new ResponseEntity<>(new ATPBatchResponse(), HttpStatus.OK);

		final URI targetUri = new URI(TARGET_URL_WITH_QUERY_PARAMS_EXT_IDS);

		final HttpEntity entity = new HttpEntity(new HttpHeaders());

		// Mock HttpEntityBuilder
		final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
		EasyMock.expect(httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class))).andReturn(entity).anyTimes();
		EasyMock.replay(httpEntityBuilderMock);
		classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);

		// Mock RestTemplate
		final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);
		EasyMock.expect(restTemplateMock.exchange(targetUri, HttpMethod.GET, entity, ATPBatchResponse.class)).andReturn(response);
		EasyMock.replay(restTemplateMock);
		classUnderTest.setRestTemplate(restTemplateMock);

		// Mock DefaultATPResultHandler
		final DefaultATPResultHandler resultHandlerMock = EasyMock.createNiceMock(DefaultATPResultHandler.class);
		EasyMock
				.expect(
						resultHandlerMock.extractATPProductAvailabilityFromATPBatchResponse(EasyMock.anyObject(ATPBatchResponse.class)))
				.andReturn(atpProductAvailabilityList).anyTimes();
		EasyMock.replay(resultHandlerMock);
		classUnderTest.setAtpResultHandler(resultHandlerMock);

		// Call rest service method
		final List<String> itemIdList = new ArrayList<String>();
		itemIdList.add(ITEM_ID_1);
		itemIdList.add(ITEM_ID_2);

		final List<ATPProductAvailability> actATPResult = classUnderTest.callRestAvailabilityServiceForProducts(CART_GUID,
				itemIdList, UNIT_PCE, productList);
		Assert.assertNotNull(actATPResult);
		Assert.assertNotNull(actATPResult.toString());

		Assert.assertEquals(atpProductAvailabilityList.size(), actATPResult.size());
		assertEqualsATPAvailability(availabilityList, actATPResult.get(0).getAvailabilityList());
		assertEqualsATPAvailability(availabilityList, actATPResult.get(1).getAvailabilityList());
	}


	@Test
	public void callRestAvailabilityServiceForProductAndSourcesTest() throws URISyntaxException
	{
		// Build product and sources List
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UNIT_PCE);

		final ProductModel productModel1 = new ProductModel();
		productModel1.setCode(ARTICLE_1);
		productModel1.setUnit(unitModel);

		final List<String> sourcesList = new ArrayList<>();
		sourcesList.add(SOURCE_1);
		sourcesList.add(SOURCE_2);

		// aggregated ATP availabilities
		final List<ATPAvailability> availabilityList = new ArrayList<>();
		final ATPAvailability availItem1 = new ATPAvailability();
		availItem1.setAtpDate(ATP_DATE_1);
		availItem1.setQuantity(ATP_QTY_1);
		availabilityList.add(availItem1);

		final ATPAvailability availItem2 = new ATPAvailability();
		availItem2.setAtpDate(ATP_DATE_2);
		availItem2.setQuantity(ATP_QTY_2);
		availabilityList.add(availItem2);

		// build ATP Batch response object
		final List<ATPProductAvailability> atpProductAvailabilityList = new ArrayList<>();
		final ATPProductAvailability atpProductAvailability1 = new ATPProductAvailability();
		atpProductAvailability1.setArticleId(ARTICLE_1);
		atpProductAvailability1.setSourceId(SOURCE_1);
		atpProductAvailability1.setAvailabilityList(availabilityList);
		atpProductAvailabilityList.add(atpProductAvailability1);

		final ATPProductAvailability atpProductAvailability2 = new ATPProductAvailability();
		atpProductAvailability2.setArticleId(ARTICLE_1);
		atpProductAvailability2.setSourceId(SOURCE_2);
		atpProductAvailability2.setAvailabilityList(availabilityList);
		atpProductAvailabilityList.add(atpProductAvailability2);

		final ResponseEntity<ATPBatchResponse> response = new ResponseEntity<>(new ATPBatchResponse(), HttpStatus.OK);

		final URI targetUri = new URI(TARGET_URL_WITH_QUERY_PARAMS);

		final HttpEntity entity = new HttpEntity(new HttpHeaders());

		// Mock HttpEntityBuilder
		final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
		EasyMock.expect(httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class))).andReturn(entity).anyTimes();
		EasyMock.replay(httpEntityBuilderMock);
		classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);

		// Mock RestTemplate
		final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);
		EasyMock.expect(restTemplateMock.exchange(targetUri, HttpMethod.GET, entity, ATPBatchResponse.class)).andReturn(response);
		EasyMock.replay(restTemplateMock);
		classUnderTest.setRestTemplate(restTemplateMock);

		// Mock DefaultATPResultHandler
		final DefaultATPResultHandler resultHandlerMock = EasyMock.createNiceMock(DefaultATPResultHandler.class);
		EasyMock
				.expect(
						resultHandlerMock.extractATPProductAvailabilityFromATPBatchResponse(EasyMock.anyObject(ATPBatchResponse.class)))
				.andReturn(atpProductAvailabilityList).anyTimes();
		EasyMock.replay(resultHandlerMock);
		classUnderTest.setAtpResultHandler(resultHandlerMock);

		// Call rest service method
		final List<ATPProductAvailability> actATPResult = classUnderTest.callRestAvailabilityServiceForProductAndSources(CART_GUID,
				ITEM_ID_1, productModel1, sourcesList);
		Assert.assertNotNull(actATPResult);
		Assert.assertNotNull(actATPResult.toString());

		Assert.assertEquals(atpProductAvailabilityList.size(), actATPResult.size());
		Assert.assertEquals(SOURCE_1, actATPResult.get(0).getSourceId());
		Assert.assertEquals(SOURCE_2, actATPResult.get(1).getSourceId());
		assertEqualsATPAvailability(availabilityList, actATPResult.get(0).getAvailabilityList());
		assertEqualsATPAvailability(availabilityList, actATPResult.get(1).getAvailabilityList());
	}

	private void assertEqualsATPAvailability(final List<ATPAvailability> expected, final List<ATPAvailability> actual)
	{
		Assert.assertEquals(expected.size(), actual.size());

		for (int i = 0; i < expected.size(); i++)
		{
			assertEqualsATPAvailabilityItems(expected.get(i), actual.get(i));
		}
	}

	private void assertEqualsATPAvailabilityItems(final ATPAvailability expected, final ATPAvailability actual)
	{
		Assert.assertEquals(serviceUtils.formatDateToString(expected.getAtpDate()),
				serviceUtils.formatDateToString(actual.getAtpDate()));
		Assert.assertEquals(expected.getQuantity().toString(), actual.getQuantity().toString());
	}

}

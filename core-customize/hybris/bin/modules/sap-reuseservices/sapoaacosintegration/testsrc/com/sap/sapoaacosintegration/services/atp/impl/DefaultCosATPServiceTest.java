/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestOperations;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.sapoaacosintegration.exception.COSDownException;
import com.sap.sapoaacosintegration.services.atp.CosATPResourcePathBuilder;
import com.sap.sapoaacosintegration.services.atp.CosATPResultHandler;
import com.sap.sapoaacosintegration.services.atp.response.ArticleResponse;
import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.config.CosConfigurationService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosATPServiceTest
{
	@Mock
	CosATPResultHandler cosAtpResultHandler;

	@Mock
	CosATPResourcePathBuilder cosAtpResourcePathBuilder;

	@Mock
	IntegrationRestTemplateFactory integrationRestTemplateFactory;

	@Mock
	CosConfigurationService configurationService;

	@Mock
	CosServiceUtils cosServiceUtils;

	@Mock
	HttpEntity httpEntity;

	@Mock
	RestOperations restOperations;

	@Mock
	SessionService sessionService;

	@Mock
	ResponseEntity<List<ArticleResponse>> response;

	@InjectMocks
	DefaultCosATPService defaultCosATPService;

	private static String cartGuid = "CID";
	private static String itemId = "ITEM";
	private final String source = "SOURCE";
	private final String productUnit = "PRODUCTUNIT";
	private final List<String> sourcesList = new ArrayList<>();
	private final ProductModel productModel = new ProductModel();
	private final ConsumedDestinationModel destination = new ConsumedDestinationModel();
	private final ArticleResponse articleResponse = new ArticleResponse();
	private final List<ArticleResponse> listArticleResponse = new ArrayList<>();
	private final List<ATPAvailability> listATPAvailability = new ArrayList<>();
	private final List<String> itemIdList = new ArrayList<>();
	private final List<ProductModel> productList = new ArrayList<>();


	@Before
	public void setUp()
	{
		defaultCosATPService.setCosAtpResultHandler(cosAtpResultHandler);
		defaultCosATPService.setCosAtpResourcePathBuilder(cosAtpResourcePathBuilder);
		defaultCosATPService.setIntegrationRestTemplateFactory(integrationRestTemplateFactory);
		defaultCosATPService.setCosServiceUtils(cosServiceUtils);

		//Mocks
		when(cosAtpResourcePathBuilder.prepareRestCallForProduct(any(ProductModel.class))).thenReturn(httpEntity);
		when(cosAtpResourcePathBuilder.prepareRestCallForProductAndSource(any(ProductModel.class), anyString()))
				.thenReturn(httpEntity);
		when(cosServiceUtils.getConsumedDestinationModelById(anyString())).thenReturn(destination);
		when(integrationRestTemplateFactory.create(any(ConsumedDestinationModel.class))).thenReturn(restOperations);
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenReturn(response);
		when(response.getBody()).thenReturn(listArticleResponse);
		when(cosAtpResultHandler.extractATPAvailabilityFromArticleResponse(eq(listArticleResponse)))
				.thenReturn(listATPAvailability);
		doNothing().when(sessionService).setAttribute(anyString(), any(Boolean.class));

		//ConsumedDestinationModel
		destination.setUrl("https://cpfs-dtrt.cfapps.eu10.hana.ondemand.com/test");

		//ProductModel
		productModel.setCode("CODE");

		//ProductList
		productList.add(productModel);

		//SourceList
		sourcesList.add(source);

		//ArticleResponse
		articleResponse.setQuantity(1000d);

		//ListArticleResponse
		listArticleResponse.add(articleResponse);
	}

	@Test
	public void callRestAvailabilityServiceForProductTest()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);

		//Verify
		Assert.assertNotNull(listATPAvailability);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductTest_HttpClientErrorException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductTest_ModelNotFoundException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ModelNotFoundException("Model Not Found"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test(expected = COSDownException.class)
	public void callRestAvailabilityServiceForProductTest_ResourceAccessException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ResourceAccessException("Resource cannot be Accessed"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}


	@Test
	public void callRestAvailabilityServiceForProductAndSourceTest()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProductAndSource(productModel, source);

		//Verify
		Assert.assertNotNull(listATPAvailability);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductAndSourceTest_HttpClientErrorException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductAndSourceTest_ModelNotFoundException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ModelNotFoundException("Model Not Found"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test(expected = COSDownException.class)
	public void callRestAvailabilityServiceForProductAndSourceTest_ResourceAccessException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ResourceAccessException("Resource cannot be Accessed"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test
	public void callRestAvailabilityServiceForProductAndSourceCallTest()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProductAndSource(cartGuid, itemId, productModel,
				source);

		//Verify
		Assert.assertNotNull(listATPAvailability);

	}


	@Test
	public void callRestAvailabilityServiceForProductsTest()
	{
		//SetUp
		List<ATPProductAvailability> listATPProductAvailability;

		//Execute
		listATPProductAvailability = defaultCosATPService.callRestAvailabilityServiceForProducts(cartGuid, itemIdList, productUnit,
				productList);

		//Verify
		Assert.assertNotNull(listATPProductAvailability);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductsTest_HttpClientErrorException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductsTest_ModelNotFoundException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ModelNotFoundException("Model Not Found"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test(expected = COSDownException.class)
	public void callRestAvailabilityServiceForProductsTest_ResourceAccessException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ResourceAccessException("Resource cannot be Accessed"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}


	@Test
	public void callRestAvailabilityServiceForProductAndSourcesTest()
	{
		//SetUp
		List<ATPProductAvailability> listATPProductAvailability;

		//Execute
		listATPProductAvailability = defaultCosATPService.callRestAvailabilityServiceForProductAndSources(cartGuid, itemId,
				productModel, sourcesList);

		//Verify
		Assert.assertNotNull(listATPProductAvailability);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductAndSourcesTest_HttpClientErrorException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}

	@Test(expected = ATPException.class)
	public void callRestAvailabilityServiceForProductAndSourcesTest_ModelNotFoundException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ModelNotFoundException("Model Not Found"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);

	}

	@Test(expected = COSDownException.class)
	public void callRestAvailabilityServiceForProductAndSourcesTest_ResourceAccessException()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;
		when(restOperations.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class),
				any(ParameterizedTypeReference.class))).thenThrow(new ResourceAccessException("Resource cannot be Accessed"));

		//Execute
		listATPAvailability = defaultCosATPService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.sapoaacosintegration.services.atp.response.ArticleResponse;



/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCosATPResultHandlerTest
{
	@Mock
	public ServiceUtils serviceUtils;

	@InjectMocks
	private DefaultCosATPResultHandler defaultCosATPResultHandler;

	private final List<ArticleResponse> listArticleResponse = new ArrayList<ArticleResponse>();
	private final ArticleResponse articleResponse = new ArticleResponse();
	private static final String PRODUCT_ID = "PID";
	private static final String SOURCE_ID = "SID";


	@Before
	public void SetUp()
	{
		//Mocks
		when(serviceUtils.parseStringToDate(anyString())).thenReturn(new Date());
		when(serviceUtils.removeLeadingZeros(anyString())).thenReturn(PRODUCT_ID);

		//ArticleResponse
		articleResponse.setQuantity(Double.valueOf("1"));
		articleResponse.setProductId(PRODUCT_ID);
		articleResponse.setSourceId(SOURCE_ID);
		articleResponse.setUnit("4");
		articleResponse.setAvailableFrom("dd-mm-yyyy");

		//ListArticleResponse
		listArticleResponse.add(articleResponse);
	}

	@Test
	public void extractATPAvailabilityFromArticleResponseTest()
	{
		//SetUp
		List<ATPAvailability> listATPAvailability;

		//Execute
		listATPAvailability = defaultCosATPResultHandler.extractATPAvailabilityFromArticleResponse(listArticleResponse);

		//Verify
		Assert.assertNotNull(listATPAvailability);
		Assert.assertNotNull(listATPAvailability.get(0).getAtpDate());
		Assert.assertEquals((Double) 1d, listATPAvailability.get(0).getQuantity());

	}

	@Test
	public void extractATPProductAvailabilityFromArticleResponseTest()
	{
		//SetUp
		List<ATPProductAvailability> listATPProductAvailability;

		//Execute
		listATPProductAvailability = defaultCosATPResultHandler
				.extractATPProductAvailabilityFromArticleResponse(listArticleResponse);

		//Verify
		Assert.assertNotNull(listATPProductAvailability);
		Assert.assertEquals(SOURCE_ID, listATPProductAvailability.get(0).getSourceId());
		Assert.assertEquals(PRODUCT_ID, listATPProductAvailability.get(0).getArticleId());

	}


}






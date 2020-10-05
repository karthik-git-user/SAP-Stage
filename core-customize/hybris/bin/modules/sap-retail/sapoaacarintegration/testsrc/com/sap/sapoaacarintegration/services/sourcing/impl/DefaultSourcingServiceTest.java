/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.net.URI;
import java.net.URISyntaxException;

import org.easymock.EasyMock;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.SourcingAbapRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultRestServiceConfiguration;
import com.sap.sapoaacarintegration.services.rest.util.impl.AuthenticationResult;
import com.sap.sapoaacarintegration.services.rest.util.impl.AuthenticationService;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpEntityBuilder;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpHeaderProvider;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultURLProvider;




/**
 *
 */
@UnitTest
public class DefaultSourcingServiceTest
{

	@Test
	public void testCallRestService() throws SourcingException, URISyntaxException
	{
		final DefaultSourcingService classUnderTest = EasyMock.createMockBuilder(DefaultSourcingService.class)
				.addMockedMethod("initialize").createMock();

		final String targetUrl = "http://www.sap.com";
		final String password = "password";
		final String user = "username";
		final String sapCarClient = "sapCarClient";


		final AuthenticationService authenticationServiceMock = EasyMock.createNiceMock(AuthenticationService.class);
		EasyMock
				.expect(authenticationServiceMock.execute(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
						EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyObject(String.class)))
				.andReturn(new AuthenticationResult());
		EasyMock.replay(authenticationServiceMock);
		classUnderTest.setAuthenticationService(authenticationServiceMock);


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


		final CartModel cartModel = new CartModel();

		final DefaultSourcingRequestMapper requestMapperMock = EasyMock.createNiceMock(DefaultSourcingRequestMapper.class);
		EasyMock.expect(requestMapperMock.mapCartModelToSourcingRequest(cartModel, false, false,
				classUnderTest.getRestServiceConfiguration())).andReturn(new SourcingAbapRequest());
		EasyMock.replay(requestMapperMock);
		classUnderTest.setCartMapper(requestMapperMock);

		final DefaultSourcingResultHandler sourcingResultHandlerMock = EasyMock.createNiceMock(DefaultSourcingResultHandler.class);
		sourcingResultHandlerMock.persistSourcingResultInCart(EasyMock.anyObject(SourcingResponse.class),
				EasyMock.anyObject(CartModel.class), EasyMock.anyObject(RestServiceConfiguration.class));
		EasyMock.expectLastCall();
		classUnderTest.setSourcingResultHandler(sourcingResultHandlerMock);

		final SessionService sessionService = EasyMock.createNiceMock(SessionService.class);

		classUnderTest.setSessionService(sessionService);

		final HttpEntity<SourcingAbapRequest> entity = new HttpEntity<SourcingAbapRequest>(null, null);
		final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createNiceMock(DefaultHttpEntityBuilder.class);
		EasyMock.expect(httpEntityBuilderMock.createHttpEntityForSourcing(EasyMock.anyObject(HttpHeaders.class),
				EasyMock.anyObject(SourcingAbapRequest.class))).andReturn(entity).anyTimes();
		EasyMock.replay(httpEntityBuilderMock);
		classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);

		final ResponseEntity responseMock = EasyMock.createNiceMock(ResponseEntity.class);
		EasyMock.expect(responseMock.getBody()).andReturn(new SourcingResponse()).anyTimes();
		EasyMock.replay(responseMock);

		final URI targetUri = new URI(targetUrl);


		final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);
		EasyMock.expect(restTemplateMock.exchange(targetUri, HttpMethod.PUT, entity, SourcingResponse.class))
				.andReturn(responseMock).anyTimes();
		EasyMock.replay(restTemplateMock);
		classUnderTest.setRestTemplate(restTemplateMock);

		final DefaultURLProvider urlProviderMock = EasyMock.createNiceMock(DefaultURLProvider.class);
		EasyMock.expect(urlProviderMock.compileURI(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class),
				EasyMock.anyObject(String.class))).andReturn(targetUri).anyTimes();
		EasyMock.replay(urlProviderMock);
		classUnderTest.setUrlProvider(urlProviderMock);


		final DefaultRestServiceConfiguration restServiceConfiguration = EasyMock
				.createMockBuilder(DefaultRestServiceConfiguration.class).addMockedMethod("initializeConfiguration").createMock();
		classUnderTest.setRestServiceConfiguration(restServiceConfiguration);
		restServiceConfiguration.setPassword(password);
		restServiceConfiguration.setUser(user);
		restServiceConfiguration.setTargetUrl(targetUrl);
		restServiceConfiguration.setSapCarClient(sapCarClient);

		classUnderTest.callRestServiceAndPersistResult(cartModel);
	}
}

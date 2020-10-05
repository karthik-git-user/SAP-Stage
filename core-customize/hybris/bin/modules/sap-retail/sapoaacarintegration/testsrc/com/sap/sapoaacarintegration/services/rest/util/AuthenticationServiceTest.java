/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.rest.util;

import de.hybris.bootstrap.annotations.UnitTest;
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

import com.sap.sapoaacarintegration.services.rest.util.impl.AuthenticationService;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpEntityBuilder;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultHttpHeaderProvider;
import com.sap.sapoaacarintegration.services.rest.util.impl.DefaultURLProvider;


/**
 *
 */
@UnitTest
public class AuthenticationServiceTest
{

	private final AuthenticationService classUnderTest = EasyMock.createMockBuilder(AuthenticationService.class)
			.addMockedMethod("initialize").createMock();

	@Test
	public void test() throws URISyntaxException
	{

		final String user = "username";
		final String password = "password";
		final String url = "http://www.sap.com";
		final String servicePath = "/path/to/directory";
		final String client = "800";

		final RestTemplate restTemplateMock = EasyMock.createNiceMock(RestTemplate.class);
		restTemplateMock.setMessageConverters(null);
		EasyMock.expectLastCall();


		final DefaultHttpHeaderProvider httpHeaderProviderMock = EasyMock.createNiceMock(DefaultHttpHeaderProvider.class);
		final HttpHeaders httpHeader = new HttpHeaders();
		EasyMock.expect(httpHeaderProviderMock.compileHttpHeader(user, password)).andReturn(httpHeader);
		EasyMock.replay(httpHeaderProviderMock);


		final DefaultURLProvider urlProviderMock = EasyMock.createNiceMock(DefaultURLProvider.class);
		final URI uri = new URI(url);
		EasyMock.expect(urlProviderMock.compileURI(url, servicePath, client)).andReturn(uri);
		EasyMock.replay(urlProviderMock);


		final HttpEntity httpEntity = new HttpEntity<String>("");

		final DefaultHttpEntityBuilder httpEntityBuilderMock = EasyMock.createMock(DefaultHttpEntityBuilder.class);
		EasyMock
				.expect(
						httpEntityBuilderMock.createHttpEntity(EasyMock.anyObject(HttpHeaders.class), EasyMock.anyObject(String.class)))
				.andReturn(httpEntity);

		final ResponseEntity<String> responseMock = EasyMock.createNiceMock(ResponseEntity.class);
		final HttpHeaders httpResponseHeader = new HttpHeaders();
		EasyMock.expect(responseMock.getHeaders()).andReturn(httpResponseHeader).anyTimes();
		EasyMock.replay(responseMock);



		EasyMock.expect(restTemplateMock.exchange(uri, HttpMethod.GET, httpEntity, String.class)).andReturn(responseMock);
		classUnderTest.setRestTemplate(restTemplateMock);

		EasyMock.replay(httpEntityBuilderMock);
		EasyMock.replay(restTemplateMock);
		final SessionService sessionService = EasyMock.createNiceMock(SessionService.class);

		classUnderTest.setSessionService(sessionService);


		classUnderTest.setRestTemplate(restTemplateMock);
		classUnderTest.setHttpHeaderProvider(httpHeaderProviderMock);
		classUnderTest.setUrlProvider(urlProviderMock);
		classUnderTest.setHttpEntityBuilder(httpEntityBuilderMock);

		classUnderTest.execute(user, password, url, servicePath, client);



	}

}

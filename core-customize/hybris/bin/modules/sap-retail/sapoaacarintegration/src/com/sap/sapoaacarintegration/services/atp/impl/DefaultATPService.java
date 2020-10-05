/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.atp.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import com.sap.retail.oaa.commerce.services.atp.ATPResourcePathBuilder;
import com.sap.retail.oaa.commerce.services.atp.ATPResultHandler;
import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPBatchResponse;
import com.sap.retail.oaa.commerce.services.atp.jaxb.pojos.response.ATPResponse;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.sapoaacarintegration.services.rest.impl.DefaultAbstractRestService;
import com.sap.sapoaacarintegration.services.rest.util.HttpEntityBuilder;
import com.sap.sapoaacarintegration.services.rest.util.HttpHeaderProvider;
import com.sap.sapoaacarintegration.services.rest.util.URLProvider;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * Default ATP service
 */
public class DefaultATPService extends DefaultAbstractRestService implements ATPService
{
	private static final Logger LOG = Logger.getLogger(DefaultATPService.class);

	private static final String ATP_ERROR_MESSAGE = "Error when calling ATP web service.";

	private HttpHeaderProvider httpHeaderProvider;
	private URLProvider urlProvider;
	private HttpEntityBuilder httpEntityBuilder;
	private ATPResultHandler atpResultHandler;
	private ATPResourcePathBuilder atpResourcePathBuilder;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.atp.ATPService#callRestAvailabilityServiceForProduct(java.lang.String,
	 * de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProduct(final String cartGuid, final String itemId,
			final ProductModel product) throws ATPException, BackendDownException
	{
		HttpEntity entity = null;

		try
		{
			initialize();
			validate(product);

			final String resourceServicePath = getAtpResourcePathBuilder().buildRessourceServicePath(product);

			entity = prepareRestCall();
			final MultiValueMap<String, String> uriQueryParameters = getAtpResourcePathBuilder().compileUriQueryParameters(cartGuid,
					itemId, product.getUnit().getCode(), getRestServiceConfiguration());

			return exchangeRestTemplateAndExtractATPResult(resourceServicePath, entity, uriQueryParameters);
		}
		catch (final IllegalArgumentException | URISyntaxException | RestClientException | RestInitializationException e)
		{
			LOG.error(ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.atp.ATPService#callRestAvailabilityServiceForProductAndSource(
	 * de.hybris.platform.core.model.product.ProductModel, java.lang.String)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final String cartGuid, final String itemId,
			final ProductModel product, final String source) throws ATPException, BackendDownException
	{
		HttpEntity entity = null;

		try
		{
			initialize();
			validate(product, source);

			final String resourceServicePath = getAtpResourcePathBuilder().buildRessourceServicePath(product, source);

			entity = prepareRestCall();
			final MultiValueMap<String, String> uriQueryParameters = getAtpResourcePathBuilder().compileUriQueryParameters(cartGuid,
					itemId, product.getUnit().getCode(), getRestServiceConfiguration());

			return exchangeRestTemplateAndExtractATPResult(resourceServicePath, entity, uriQueryParameters);
		}
		catch (final IllegalArgumentException | URISyntaxException | RestClientException | RestInitializationException e)
		{
			LOG.error(ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.atp.ATPService#callRestAvailabilityServiceForProductAndSource(de.hybris.
	 * platform .core.model.product.ProductModel, java.lang.String)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final ProductModel product, final String source)
			throws ATPException, BackendDownException
	{
		return this.callRestAvailabilityServiceForProductAndSource(null, null, product, source);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.atp.ATPService#callRestAvailabilityServiceForProducts(java.util.List)
	 */
	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProducts(final String cartGuid,
			final List<String> itemIdList, final String productUnit, final List<ProductModel> productList)
			throws ATPException, BackendDownException
	{
		HttpEntity entity = null;
		try
		{
			initialize();
			validate(productList);

			final String resourceServicePath = getAtpResourcePathBuilder().buildBatchRessourceServicePath(productList);
			entity = prepareRestCall();
			final MultiValueMap<String, String> uriQueryParameters = getAtpResourcePathBuilder().compileUriQueryParameters(cartGuid,
					itemIdList, productUnit, getRestServiceConfiguration());

			return exchangeRestTemplateAndExtractATPBatchResult(resourceServicePath, entity, uriQueryParameters);
		}
		catch (final IllegalArgumentException | URISyntaxException | RestClientException | RestInitializationException e)
		{
			LOG.error(ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.atp.ATPService#callRestAvailabilityServiceForProductAndSources(de.hybris.
	 * platform.core.model.product.ProductModel, java.util.List)
	 */
	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProductAndSources(final String cartGuid, final String itemId,
			final ProductModel product, final List<String> sourcesList) throws ATPException, BackendDownException
	{
		HttpEntity entity = null;

		try
		{
			initialize();
			validate(product, sourcesList);

			final String resourceServicePath = getAtpResourcePathBuilder().buildBatchRessourceServicePath(product, sourcesList);

			entity = prepareRestCall();
			final MultiValueMap<String, String> uriQueryParameters = getAtpResourcePathBuilder().compileUriQueryParameters(cartGuid,
					itemId, product.getUnit().getCode(), getRestServiceConfiguration());

			return exchangeRestTemplateAndExtractATPBatchResult(resourceServicePath, entity, uriQueryParameters);
		}
		catch (final IllegalArgumentException | URISyntaxException | RestClientException | RestInitializationException e)
		{
			LOG.error(ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}

	}

	/**
	 * @return HttpEntity
	 */
	public HttpEntity prepareRestCall()
	{
		HttpEntity entity;
		final HttpHeaders header = compileHTTPHeader();

		entity = httpEntityBuilder.createHttpEntity(header);
		return entity;
	}


	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param resourceServicePath
	 * @param entity
	 * @param queryParameters
	 * @return list of ATPProductAvailability
	 * @throws ATPException
	 * @throws RestClientException
	 * @throws URISyntaxException
	 */
	private List<ATPProductAvailability> exchangeRestTemplateAndExtractATPBatchResult(final String resourceServicePath,
			final HttpEntity entity, final MultiValueMap<String, String> queryParameters)
			throws ATPException, URISyntaxException, BackendDownException
	{
		final URI compileURI = urlProvider.compileURI(getRestServiceConfiguration().getTargetUrl(), resourceServicePath,
				getRestServiceConfiguration().getSapCarClient());

		final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(compileURI);
		uriBuilder.queryParams(queryParameters);

		final URI finalUri = uriBuilder.build().encode().toUri();

		ResponseEntity<ATPBatchResponse> response = null;
		beforeRestCall();

		try
		{
			response = getRestTemplate().exchange(finalUri, HttpMethod.GET, entity, ATPBatchResponse.class);
		}
		catch (final HttpClientErrorException e)
		{
			checkHttpStatusCode(e);
			LOG.error(ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new BackendDownException(SapoaacommerceservicesConstants.BACKEND_DOWN_MESSAGE, e);
		}

		List<ATPProductAvailability> batchResult = new ArrayList<>();
		if (response != null)
		{
			batchResult = atpResultHandler.extractATPProductAvailabilityFromATPBatchResponse(response.getBody());
		}

		return batchResult;
	}

	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param resourceServicePath
	 * @param entity
	 * @param queryParameters
	 * @return list of ATPAvailability
	 * @throws ATPException
	 * @throws BackendDownException
	 * @throws URISyntaxException
	 */
	private List<ATPAvailability> exchangeRestTemplateAndExtractATPResult(final String resourceServicePath,
			final HttpEntity entity, final MultiValueMap<String, String> queryParameters)
			throws ATPException, URISyntaxException, BackendDownException
	{
		final URI compileURI = urlProvider.compileURI(getRestServiceConfiguration().getTargetUrl(), resourceServicePath,
				getRestServiceConfiguration().getSapCarClient());

		final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(compileURI);
		uriBuilder.queryParams(queryParameters);

		final URI finalUri = uriBuilder.build().encode().toUri();

		ResponseEntity<ATPResponse> response = null;
		beforeRestCall();

		try
		{
			response = getRestTemplate().exchange(finalUri, HttpMethod.GET, entity, ATPResponse.class);
		}
		catch (final HttpClientErrorException e)
		{
			checkHttpStatusCode(e);
			LOG.error(ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new BackendDownException(SapoaacommerceservicesConstants.BACKEND_DOWN_MESSAGE, e);
		}

		List<ATPAvailability> atpResult = new ArrayList<>();
		if (response != null)
		{
			atpResult = atpResultHandler.extractATPAvailabilityFromATPResponse(response.getBody());
		}

		return atpResult;
	}

	/**
	 * Validates if an OAA profile, product and list of sources are set
	 *
	 * @param productModel
	 * @param sourcesList
	 * @throws IllegalArgumentException
	 */
	private void validate(final ProductModel productModel, final List<String> sourcesList)
	{
		this.validate(productModel);
		if (sourcesList == null || sourcesList.isEmpty())
		{
			throw new IllegalArgumentException("Source is not maintained");
		}
	}


	/**
	 * Validates if an OAA profile and product are set
	 *
	 * @param productModel
	 */
	private void validate(final ProductModel productModel)
	{
		this.validateProductModel(productModel);
	}

	/**
	 * Validates if an OAA profile, product and source are set
	 *
	 * @param productModel
	 * @throws IllegalArgumentException
	 */
	private void validate(final ProductModel productModel, final String source)
	{
		this.validate(productModel);
		if (source == null || source.isEmpty())
		{
			throw new IllegalArgumentException("Source is not maintained");
		}
	}

	/**
	 * Validates if an OAA profile and product list are set
	 *
	 * @param productModelList
	 * @throws IllegalArgumentException
	 */
	private void validate(final List<ProductModel> productModelList)
	{
		if (productModelList == null || productModelList.isEmpty())
		{
			throw new IllegalArgumentException("Product list cannot be null or empty");
		}
		else
		{
			for (final ProductModel productModel : productModelList)
			{
				this.validateProductModel(productModel);
			}
		}
	}

	/**
	 * Validates if a product is set
	 *
	 * @param productModel
	 * @throws IllegalArgumentException
	 */
	private void validateProductModel(final ProductModel productModel)
	{
		if (productModel == null || productModel.getCode() == null)
		{
			throw new IllegalArgumentException("Product cannot be null");
		}
	}

	/**
	 * Set HTTP headers for calling availability service
	 *
	 * @return httpHeaders
	 */
	private HttpHeaders compileHTTPHeader()
	{
		final HttpHeaders header = httpHeaderProvider.compileHttpHeader(getRestServiceConfiguration().getUser(),
				getRestServiceConfiguration().getPassword());
		header.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);
		return header;
	}

	/**
	 * @param atpResultHandler
	 */
	public void setAtpResultHandler(final ATPResultHandler atpResultHandler)
	{
		this.atpResultHandler = atpResultHandler;
	}

	/**
	 * @return the atpResultHandler
	 */
	protected ATPResultHandler getAtpResultHandler()
	{
		return atpResultHandler;
	}

	/**
	 * @param httpHeaderProvider
	 */
	public void setHttpHeaderProvider(final HttpHeaderProvider httpHeaderProvider)
	{
		this.httpHeaderProvider = httpHeaderProvider;
	}

	/**
	 * @return the httpHeaderProvider
	 */
	protected HttpHeaderProvider getHttpHeaderProvider()
	{
		return httpHeaderProvider;
	}

	/**
	 * @param urlProvider
	 */
	public void setUrlProvider(final URLProvider urlProvider)
	{
		this.urlProvider = urlProvider;
	}

	/**
	 * @return the urlProvider
	 */
	protected URLProvider getUrlProvider()
	{
		return urlProvider;
	}

	/**
	 * @param httpEntityBuilder
	 *           the httpEntityBuilder to set
	 */
	public void setHttpEntityBuilder(final HttpEntityBuilder httpEntityBuilder)
	{
		this.httpEntityBuilder = httpEntityBuilder;
	}

	/**
	 * @return the httpEntityBuilder
	 */
	protected HttpEntityBuilder getHttpEntityBuilder()
	{
		return httpEntityBuilder;
	}

	/**
	 * @return the atpResourcePathBuilder
	 */
	public ATPResourcePathBuilder getAtpResourcePathBuilder()
	{
		return atpResourcePathBuilder;
	}

	/**
	 * @param atpResourcePathBuilder
	 *           the atpResourcePathBuilder to set
	 */
	public void setAtpResourcePathBuilder(final ATPResourcePathBuilder atpResourcePathBuilder)
	{
		this.atpResourcePathBuilder = atpResourcePathBuilder;
	}

}

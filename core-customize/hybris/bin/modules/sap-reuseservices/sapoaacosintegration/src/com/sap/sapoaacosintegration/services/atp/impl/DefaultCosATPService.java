/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.impl;



import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.exception.COSDownException;
import com.sap.sapoaacosintegration.services.atp.CosATPResourcePathBuilder;
import com.sap.sapoaacosintegration.services.atp.CosATPResultHandler;
import com.sap.sapoaacosintegration.services.atp.response.ArticleResponse;
import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.rest.impl.DefaultAbstractCosRestService;




/**
 * Default COS ATP service
 */
public class DefaultCosATPService extends DefaultAbstractCosRestService implements ATPService
{
	private static final Logger LOG = Logger.getLogger(DefaultCosATPService.class);

	private CosATPResultHandler cosAtpResultHandler;
	private CosATPResourcePathBuilder cosAtpResourcePathBuilder;
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private CosServiceUtils cosServiceUtils;



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProduct(java.lang.String,
	 * de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProduct(final String cartGuid, final String itemId,
			final ProductModel product)
	{
		HttpEntity entity = null;

		try
		{
			validateProduct(product);
			entity = getCosAtpResourcePathBuilder().prepareRestCallForProduct(product);
			return exchangeRestTemplateAndExtractATPResult(entity);
		}
		catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProductAndSource(
	 * de.hybris.platform.core.model.product.ProductModel, java.lang.String)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final String cartGuid, final String itemId,
			final ProductModel product, final String source)
	{
		HttpEntity entity = null;

		try
		{
			validateProductAndSource(product, source);
			entity = getCosAtpResourcePathBuilder().prepareRestCallForProductAndSource(product, source);
			return exchangeRestTemplateAndExtractATPResult(entity);
		}
		catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProductAndSource(de.hybris.
	 * platform .core.model.product.ProductModel, java.lang.String)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final ProductModel product, final String source)
	{
		return this.callRestAvailabilityServiceForProductAndSource(null, null, product, source);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProducts(java.util.List)
	 */
	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProducts(final String cartGuid,
			final List<String> itemIdList, final String productUnit, final List<ProductModel> productList)
	{

		HttpEntity entity = null;

		try
		{
			validateProducts(productList);
			entity = getCosAtpResourcePathBuilder().prepareRestCallForProducts(productList);
			return exchangeRestTemplateAndExtractATPProductResult(entity);


		}
		catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProductAndSources(de.hybris.
	 * platform.core.model.product.ProductModel, java.util.List)
	 */
	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProductAndSources(final String cartGuid, final String itemId,
			final ProductModel product, final List<String> sourcesList)
	{


		try
		{
			validateProductAndSources(product, sourcesList);
			return exchangeRestTemplateAndExtractATPBatchResult(sourcesList, product);
		}
		catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}

	}



	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param product
	 * @param sourcesList
	 *
	 * @return list of ATPProductAvailability
	 *
	 * @throws ATPException
	 * @throws COSDownException
	 *
	 */
	private List<ATPProductAvailability> exchangeRestTemplateAndExtractATPBatchResult(final List<String> sourcesList,
			final ProductModel product)
	{
		ResponseEntity<List<ArticleResponse>> response = null;
		HttpEntity entity;
		ArticleResponse articleResponse = null;
		final List<ArticleResponse> responseList = new ArrayList<>();
		List<ATPProductAvailability> batchResult = null;
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);

			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.AVAILABILITY_RESOURCE_PATH);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);

			//Currently we do single calls for each source - this is a possible performance point to change this to one batch call
			for (final String source : sourcesList)
			{
				entity = getCosAtpResourcePathBuilder().prepareRestCallForProductAndSource(product, source);
				response = restOperations.exchange(finalUri.toString(), HttpMethod.POST, entity,
						new ParameterizedTypeReference<List<ArticleResponse>>()
						{
						});
				if (response != null && response.getBody() != null)
				{
					if (!response.getBody().isEmpty() && null != response.getBody().get(0)
							&& response.getBody().get(0).getQuantity() != null)
					{
						articleResponse = response.getBody().get(0);
						articleResponse.setSourceId(source);
						responseList.add(articleResponse);
					}
				}
				else
				{
					LOG.info(SapoaacosintegrationConstants.RESPONSE_IS_NULL_OR_EMPTY);
				}
			}
			batchResult = getCosAtpResultHandler().extractATPProductAvailabilityFromArticleResponse(responseList);

		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE + "   " + e.getResponseBodyAsString());
			throw new ATPException(e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}


		return batchResult;
	}

	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param entity
	 * @return list of ATPAvailability
	 * @throws ATPException
	 * @throws COSDownException
	 */
	private List<ATPAvailability> exchangeRestTemplateAndExtractATPResult(final HttpEntity entity)
	{
		ResponseEntity<List<ArticleResponse>> response = null;
		final List<ATPAvailability> atpResult = new ArrayList<>();
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);
			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.AVAILABILITY_RESOURCE_PATH);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			response = restOperations.exchange(finalUri.toString(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<List<ArticleResponse>>()
					{
					});


			return response.getBody() != null
					? getCosAtpResultHandler().extractATPAvailabilityFromArticleResponse(response.getBody())
					: atpResult;

		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}

	}

	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param entity
	 * @return list of ATPAvailability
	 * @throws ATPException
	 * @throws COSDownException
	 */
	private List<ATPProductAvailability> exchangeRestTemplateAndExtractATPProductResult(final HttpEntity entity)
	{
		ResponseEntity<List<ArticleResponse>> response = null;
		final List<ATPProductAvailability> atpResult = new ArrayList<>();
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);
			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.AVAILABILITY_RESOURCE_PATH);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			response = restOperations.exchange(finalUri.toString(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<List<ArticleResponse>>()
					{
					});

			return response.getBody() != null
					? getCosAtpResultHandler().extractATPProductAvailabilityFromArticleResponse(response.getBody())
					: atpResult;

		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}

	}

	/**
	 * Validates if a product is set
	 *
	 * @param productModel
	 */
	private void validateProduct(final ProductModel productModel)
	{
		if (productModel == null || productModel.getCode() == null)
		{
			throw new IllegalArgumentException("Product cannot be null");
		}
	}

	/**
	 * Validates if an OAA profile, product and source are set
	 *
	 * @param productModel
	 * @throws IllegalArgumentException
	 */
	private void validateProductAndSource(final ProductModel productModel, final String source)
	{
		this.validateProduct(productModel);
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
	private void validateProducts(final List<ProductModel> productModelList)
	{
		if (productModelList == null || productModelList.isEmpty())
		{
			throw new IllegalArgumentException("Product list cannot be null or empty");
		}
		else
		{
			for (final ProductModel productModel : productModelList)
			{
				this.validateProduct(productModel);
			}
		}
	}

	/**
	 * Validates if product and list of sources are set
	 *
	 * @param productModel
	 * @param sourcesList
	 * @throws IllegalArgumentException
	 */
	private void validateProductAndSources(final ProductModel productModel, final List<String> sourcesList)
	{
		this.validateProduct(productModel);
		if (sourcesList == null || sourcesList.isEmpty())
		{
			throw new IllegalArgumentException("Source is not maintained");
		}
	}



	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory()
	{
		return integrationRestTemplateFactory;
	}

	public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory)
	{
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	public CosATPResultHandler getCosAtpResultHandler()
	{
		return cosAtpResultHandler;
	}


	public void setCosAtpResultHandler(final CosATPResultHandler cosAtpResultHandler)
	{
		this.cosAtpResultHandler = cosAtpResultHandler;
	}


	public CosATPResourcePathBuilder getCosAtpResourcePathBuilder()
	{
		return cosAtpResourcePathBuilder;
	}


	public void setCosAtpResourcePathBuilder(final CosATPResourcePathBuilder cosAtpResourcePathBuilder)
	{
		this.cosAtpResourcePathBuilder = cosAtpResourcePathBuilder;
	}

	/**
	 * @return the cosServiceUtils
	 */
	public CosServiceUtils getCosServiceUtils()
	{
		return cosServiceUtils;
	}

	/**
	 * @param cosServiceUtils
	 *           the cosServiceUtils to set
	 */
	public void setCosServiceUtils(final CosServiceUtils cosServiceUtils)
	{
		this.cosServiceUtils = cosServiceUtils;
	}

}

/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.atp.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.sap.retail.oaa.commerce.services.atp.ATPResourcePathBuilder;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.model.enums.Sapoaa_mode;
import com.sap.sapoaacarintegration.constants.SapoaacarintegrationConstants;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * Default Resource Builder for ATP Service
 */
public class DefaultATPResourcePathBuilder implements ATPResourcePathBuilder
{
	private static final String SERVICE_PATH = "/sap/car/rest/oaa/availability";
	private static final String SERVICE_QUERY_PARAM = "?q=";
	private static final String SERVICE_QUERY_PARAM_SOURCES = "sources?q=";
	private static final String QUERY_PARAM_OAA_PROFILE_ID = "oaaProfile";
	private static final String QUERY_PARAM_SALES_CHANNEL = "salesChannel";
	private static final String QUERY_PARAM_EXTERNAL_ID = "externalId";
	private static final String QUERY_PARAM_EXTERNAL_IDS = "externalIds";
	private static final String QUERY_PARAM_UNIT = "unit";

	private ServiceUtils serviceUtils;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.atp.ATPResourcePathBuilder#compileUriQueryParameters(java.lang.String,
	 * java.util.List, java.lang.String)
	 */
	@Override
	public MultiValueMap<String, String> compileUriQueryParameters(final String cartId, final List<String> itemIdList,
			final String productUnit, final RestServiceConfiguration restConfiguration)
	{
		final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

		addMode(multiValueMap, restConfiguration);

		multiValueMap.set(QUERY_PARAM_UNIT, productUnit);

		if (cartId != null && !cartId.isEmpty())
		{
			multiValueMap.add(QUERY_PARAM_EXTERNAL_IDS, buildExternalIdQueryParam(cartId, itemIdList));
		}
		return multiValueMap;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.sapoaacommerceservices.services.atp.ATPResourcePathBuilder#compileUriQueryParameters(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public MultiValueMap<String, String> compileUriQueryParameters(final String cartId, final String itemId,
			final String productUnit, final RestServiceConfiguration restConfiguration)
	{
		final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

		addMode(multiValueMap, restConfiguration);

		multiValueMap.set(QUERY_PARAM_UNIT, StringUtils.left(productUnit,SapoaacarintegrationConstants.UNIT_MAXLENGTH));

		if (cartId != null && !cartId.isEmpty() && itemId != null && !itemId.isEmpty())
		{
			final String externalId = serviceUtils.createExternalIdForItem(itemId, cartId);
			multiValueMap.set(QUERY_PARAM_EXTERNAL_ID, externalId);
		}
		return multiValueMap;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.atp.ATPResourcePathBuilder#buildBatchRessourceServicePath(de.hybris.platform.
	 * core.model.product.ProductModel, java.util.List)
	 */
	@Override
	public String buildBatchRessourceServicePath(final ProductModel product, final List<String> sourcesList)
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(SERVICE_PATH);
		strBuilder.append('/');
		strBuilder.append(product.getCode());
		strBuilder.append('/');
		strBuilder.append(SERVICE_QUERY_PARAM_SOURCES);

		final Iterator<String> sourcesIterator = sourcesList.iterator();
		while (sourcesIterator.hasNext())
		{
			final String source = sourcesIterator.next();
			strBuilder.append(source);
			if (sourcesIterator.hasNext())
			{
				strBuilder.append(',');
			}
		}
		return strBuilder.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.atp.ATPResourcePathBuilder#buildBatchRessourceServicePath(java.util.List)
	 */
	@Override
	public String buildBatchRessourceServicePath(final List<ProductModel> productList)
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(SERVICE_PATH);
		strBuilder.append(SERVICE_QUERY_PARAM);

		final Iterator<ProductModel> productIterator = productList.iterator();
		while (productIterator.hasNext())
		{
			final ProductModel product = productIterator.next();
			strBuilder.append(product.getCode());
			if (productIterator.hasNext())
			{
				strBuilder.append(',');
			}
		}
		return strBuilder.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.atp.ATPResourcePathBuilder#buildRessourceServicePath(de.hybris.platform.core.
	 * model.product.ProductModel, java.lang.String)
	 */
	@Override
	public String buildRessourceServicePath(final ProductModel product, final String source)
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(SERVICE_PATH);
		strBuilder.append('/');
		strBuilder.append(product.getCode());
		strBuilder.append('/');
		strBuilder.append(source);
		return strBuilder.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.sapoaacommerceservices.services.atp.ATPResourcePathBuilder#buildRessourceServicePath(de.hybris.platform.core.
	 * model.product.ProductModel)
	 */
	@Override
	public String buildRessourceServicePath(final ProductModel product)
	{
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(SERVICE_PATH);
		strBuilder.append('/');
		strBuilder.append(product.getCode());
		return strBuilder.toString();
	}

	/**
	 * Adds Oaa Profile or sales channel based on the mode
	 *
	 * @param multiValueMap
	 * @param restConfiguration
	 */
	private void addMode(final MultiValueMap<String, String> multiValueMap, final RestServiceConfiguration restConfiguration)
	{
		if (restConfiguration.getMode() != null && restConfiguration.getMode().equals(Sapoaa_mode.SALESCHANNEL))
		{
			multiValueMap.set(QUERY_PARAM_SALES_CHANNEL, restConfiguration.getSalesChannel());
		}
		else
		{
			multiValueMap.set(QUERY_PARAM_OAA_PROFILE_ID, restConfiguration.getOaaProfile());
		}
	}

	/**
	 * Assembles the external IDs as query parameter.
	 *
	 * @param cartId
	 * @param itemIdList
	 * @return externalIdQueryParam as string
	 */
	private String buildExternalIdQueryParam(final String cartId, final List<String> itemIdList)
	{
		final StringBuilder strBuilder = new StringBuilder();
		String itemId;
		final Iterator<String> itemIdIterator = itemIdList.iterator();
		while (itemIdIterator.hasNext())
		{
			itemId = itemIdIterator.next();
			if (itemId != null && !itemId.isEmpty())
			{
				if (strBuilder.length() > 0)
				{
					strBuilder.append(',');
				}
				strBuilder.append(serviceUtils.createExternalIdForItem(itemId, cartId));
			}
		}
		return strBuilder.toString();
	}

	/**
	 * @param serviceUtils
	 *           the serviceUtils to set
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}

	/**
	 * @return the serviceUtils
	 */
	protected ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}
}

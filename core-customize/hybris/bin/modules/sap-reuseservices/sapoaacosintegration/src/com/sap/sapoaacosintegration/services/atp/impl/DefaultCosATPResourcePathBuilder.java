/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.impl;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.services.atp.CosATPResourcePathBuilder;
import com.sap.sapoaacosintegration.services.atp.request.Article;
import com.sap.sapoaacosintegration.services.atp.request.ArticleSource;
import com.sap.sapoaacosintegration.services.atp.request.CosSource;
import com.sap.sapoaacosintegration.services.config.CosConfigurationService;



/**
 * Default Resource Builder for ATP Service
 */
public class DefaultCosATPResourcePathBuilder implements CosATPResourcePathBuilder
{
	private ServiceUtils serviceUtils;
	private CosConfigurationService configurationService;




	@Override
	public HttpEntity prepareRestCallForProduct(final ProductModel product)
	{
		final HttpEntity entity;
		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final Article article = new Article();
		article.setArticleId(product.getCode());
		article.setUnit(StringUtils.left(product.getUnit().getCode(), SapoaacosintegrationConstants.UNIT_MAX_LENGTH));
		article.setStrategyId(getConfigurationService().getCosCasStrategyId());
		entity = new HttpEntity<>(article, header);
		return entity;
	}

	@Override
	public HttpEntity prepareRestCallForProducts(final List<ProductModel> productList)
	{
		final HttpEntity entity;
		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final List<Article> articles = new ArrayList<>();
		final Iterator<ProductModel> productIterator = productList.iterator();
		while (productIterator.hasNext())
		{
			final ProductModel product = productIterator.next();
			if (productIterator.hasNext())
			{
				final Article article = new Article();
				article.setArticleId(product.getCode());
				article.setUnit(StringUtils.left(product.getUnit().getCode(), SapoaacosintegrationConstants.UNIT_MAX_LENGTH));
				article.setStrategyId(getConfigurationService().getCosCasStrategyId());
				articles.add(article);
			}
		}

		entity = new HttpEntity<>(articles, header);
		return entity;
	}


	@Override
	public HttpEntity prepareRestCallForProductAndSource(final ProductModel product, final String sourceId)
	{
		HttpEntity entity;
		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final ArticleSource articleSource = new ArticleSource();
		final CosSource source = new CosSource();
		articleSource.setArticleId(product.getCode());
		articleSource.setUnit(StringUtils.left(product.getUnit().getCode(), SapoaacosintegrationConstants.UNIT_MAX_LENGTH));
		source.setSourceId(sourceId);
		source.setSourceType(SapoaacosintegrationConstants.SOURCE_TYPE_STORE);
		articleSource.setSource(source);
		entity = new HttpEntity<>(articleSource, header);
		return entity;
	}

	/**
	 * @return the configurationService
	 */
	public CosConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final CosConfigurationService configurationService)
	{
		this.configurationService = configurationService;
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

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.scpiconnector.service.impl;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.InputStream;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.scpiconnector.service.ScpiconnectorService;

/**
 * Default implementation of {@link ScpiconnectorService}
 */
public class DefaultScpiconnectorService implements ScpiconnectorService
{
	private static final Logger LOG = Logger.getLogger(DefaultScpiconnectorService.class);

	private MediaService mediaService;
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	@Override
	public String getHybrisLogoUrl(final String logoCode)
	{
		final MediaModel media = mediaService.getMedia(logoCode);

		// Keep in mind that with Slf4j you don't need to check if debug is enabled, it is done under the hood.
		LOG.debug("Found media [code: {}]"+media.getCode());

		return media.getURL();
	}

	@Override
	public void createLogo(final String logoCode)
	{
		final Optional<CatalogUnawareMediaModel> existingLogo = findExistingLogo(logoCode);

		final CatalogUnawareMediaModel media = existingLogo.isPresent() ? existingLogo.get()
				: modelService.create(CatalogUnawareMediaModel.class);
		media.setCode(logoCode);
		media.setRealFileName("sap-hybris-platform.png");
		modelService.save(media);

		mediaService.setStreamForMedia(media, getImageStream());
	}

	private final static String FIND_LOGO_QUERY = "SELECT {" + CatalogUnawareMediaModel.PK + "} FROM {"
			+ CatalogUnawareMediaModel._TYPECODE + "} WHERE {" + CatalogUnawareMediaModel.CODE + "}=?code";

	private Optional<CatalogUnawareMediaModel> findExistingLogo(final String logoCode)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(FIND_LOGO_QUERY);
		fQuery.addQueryParameter("code", logoCode);

		try
		{
			return Optional.of(flexibleSearchService.searchUnique(fQuery));
		}
		catch (final SystemException e)
		{
			LOG.error(e);
			return Optional.empty();
		}
	}

	private InputStream getImageStream()
	{
		return DefaultScpiconnectorService.class.getResourceAsStream("/scpiconnector/sap-hybris-platform.png");
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsocc.controllers;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmsocc.data.CMSPageWsDTO;
import de.hybris.platform.cmsocc.jaxb.adapters.pages.PageAdapterUtil;
import de.hybris.platform.cmsocc.jaxb.adapters.pages.PageWsDTOAdapter;
import de.hybris.platform.cmsocc.mapping.CMSDataMapper;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


/**
 * Controller to get cms page data with a list of content slots, each of which contains a list of cms component data
 */
@Controller
@RequestMapping(value = "/{baseSiteId}/cms")
@ApiVersion("v2")
@Api(tags = "Pages")
public class PageController
{
	@Resource(name = "cmsPageFacade")
	private PageFacade pageFacade;

	@Resource(name = "cmsDataMapper")
	protected CMSDataMapper dataMapper;

	private static final Logger LOGGER = LoggerFactory.getLogger(PageController.class);

	@GetMapping(value = "/pages")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Get page data with a list of cms content slots", //
			notes = "Get page data with a list of cms content slots based on pageLabelOrId or code. " +
					"If none is provided then this will retrieve a Homepage cms content slots." +
					"\nContent pages can be filtered by pageLabelOrId while the rest of the page types can be filtered by the code.", //
			nickname = "getPage")
	@ApiBaseSiteIdParam
	public PageAdapterUtil.PageAdaptedData getPage(
			@ApiParam(value = "page type", allowableValues = "ContentPage, ProductPage, CategoryPage, CatalogPage") //
			@RequestParam(required = true, defaultValue = ContentPageModel._TYPECODE) final String pageType,
			@ApiParam(value = "Page Label or Id; if no page has a label that matches the provided label exactly, "
					+ "try to find pages whose label starts with a section of the provided label. Otherwise, try to find the page by id. "
					+ "Note: URL encoding on the page label should be applied when the label contains special characters") //
			@RequestParam(required = false) final String pageLabelOrId,
			@ApiParam(value = "If pageType is ProductPage, code should be product code; if pageType is CategoryPage, code should be category code; "
					+ "if pageType is CatalogPage, code should be catalog code") //
			@RequestParam(required = false) final String code,
			@ApiParam(value = "Response configuration (list of fields, which should be returned in response)", allowableValues = "BASIC, DEFAULT, FULL") //
			@RequestParam(defaultValue = "DEFAULT") final String fields) throws CMSItemNotFoundException
	{
		try
		{
			final AbstractPageData pageData = getPageFacade().getPageData(pageType, pageLabelOrId, code);
			final CMSPageWsDTO page = (CMSPageWsDTO) getDataMapper().map(pageData, fields);

			final PageWsDTOAdapter adapter = new PageWsDTOAdapter();
			return adapter.marshal(page);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	@GetMapping(value = "/pages/{pageId}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@ApiOperation(value = "Get page data for specific page id with a list of cms content slots", //
			notes = "Given a page identifier, return the page data with a list of cms content slots, each of which "
					+ "contains a list of cms component data.", //
			nickname = "getPageById")
	@ApiBaseSiteIdParam
	public PageAdapterUtil.PageAdaptedData getPage( //
			@ApiParam(value = "Page Id", required = true) //
			@PathVariable final String pageId,
			@ApiParam(value = "Response configuration (list of fields, which should be returned in response)", allowableValues = "BASIC, DEFAULT, FULL") //
			@RequestParam(defaultValue = "DEFAULT") final String fields) throws CMSItemNotFoundException
	{
		try
		{
			final AbstractPageData pageData = getPageFacade().getPageData(pageId);
			final CMSPageWsDTO page = (CMSPageWsDTO) getDataMapper().map(pageData, fields);

			final PageWsDTOAdapter adapter = new PageWsDTOAdapter();
			return adapter.marshal(page);
		}
		catch (final ValidationException e)
		{
			LOGGER.info("Validation exception", e);
			throw new WebserviceValidationException(e.getValidationObject());
		}
	}

	protected PageFacade getPageFacade()
	{
		return pageFacade;
	}

	public void setPageFacade(final PageFacade pageFacade)
	{
		this.pageFacade = pageFacade;
	}

	protected CMSDataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final CMSDataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}
}

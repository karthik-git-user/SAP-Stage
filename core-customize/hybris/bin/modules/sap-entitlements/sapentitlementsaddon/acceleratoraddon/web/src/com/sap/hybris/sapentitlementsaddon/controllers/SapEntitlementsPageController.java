/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsaddon.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;
import com.sap.hybris.sapentitlementsintegration.exception.SapEntitlementException;
import com.sap.hybris.sapentitlementsfacades.facade.SapEntitlementFacade;


/**
 * Controller for Entitlements Pages
 */
@Controller
@RequestMapping("/my-account")
public class SapEntitlementsPageController extends AbstractSearchPageController
{
	@Resource(name = "sapEntitlementFacade")
	private SapEntitlementFacade entitlementFacade;
	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;
	private static final Logger LOG = Logger.getLogger(SapEntitlementsPageController.class);
	private static final String ENTITLEMENTS_CMS_PAGE = "entitlements";
	private static final String ENTITLEMENT_DETAILS_CMS_PAGE = "entitlement-details";
	private static final String ENTITLEMENT_NUMBER = "{entitlementNumber:.*}";
	private static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";
	private static final int ENTITLEMENT_MAX_COUNT = 100;

	/*
	 * Display All Entitlements
	 */
	@RequestMapping(value = "/entitlements", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getEntitlements(@Nonnull
	final Model model) throws CMSItemNotFoundException
	{
		List<EntitlementData> entitlements = new ArrayList();
		try
		{
			entitlements = entitlementFacade.getEntitlementsForCurrentCustomer(1, ENTITLEMENT_MAX_COUNT);
		}
		catch (final SapEntitlementException e)
		{
			LOG.error("Error while retrieving entitlements", e);
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ENTITLEMENTS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ENTITLEMENTS_CMS_PAGE));
		model.addAttribute(ENTITLEMENTS_CMS_PAGE, entitlements);
		model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.entitlements"));
		model.addAttribute("metaRobots", "no-index,no-follow");
		return getViewForPage(model);
	}

	/*
	 * Get Entitlement Details
	 */
	@RequestMapping(value = "/entitlement/" + ENTITLEMENT_NUMBER, method = RequestMethod.GET)
	@RequireHardLogIn
	public String entitlementDetail(@PathVariable("entitlementNumber")
	final String entitlementNumber, final Model model) throws CMSItemNotFoundException
	{
		try
		{
			final EntitlementData entitlement = entitlementFacade.getEntitlementForNumber(entitlementNumber);
			model.addAttribute("entitlementData", entitlement);
			final List<Breadcrumb> breadcrumbs = buildEntitlementDetailBreadcrumb(entitlement);
			model.addAttribute("breadcrumbs", breadcrumbs);

		}
		catch (final SapEntitlementException e)
		{
			LOG.error("Error while retrieving entitlements", e);
			//global; message
			return REDIRECT_MY_ACCOUNT;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ENTITLEMENT_DETAILS_CMS_PAGE));
		model.addAttribute("metaRobots", "no-index,no-follow");
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ENTITLEMENT_DETAILS_CMS_PAGE));
		return getViewForPage(model);
	}

	protected List<Breadcrumb> buildEntitlementDetailBreadcrumb(final EntitlementData entitlement)
	{
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.entitlements");
		breadcrumbs.get(breadcrumbs.size() - 1).setUrl("/my-account/entitlements");
		breadcrumbs.add(new Breadcrumb("#",
				entitlement.getEntitlementNumber() != null ? entitlement.getEntitlementNumber().toString() : "", null));
		return breadcrumbs;
	}


}

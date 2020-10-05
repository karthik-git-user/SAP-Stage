/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bloginaddon.handlers;

import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;


/**
 * Before view handler to expose partner ID and org ID of the user if logged in
 */
public class GigyaB2BBeforeViewHandler implements BeforeViewHandlerAdaptee
{

	private BaseSiteService baseSiteService;

	private UserService userService;

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName) throws Exception
	{
		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		if (baseSite != null && baseSite.getGigyaConfig() != null)
		{
			model.addAttribute("partnerId", baseSite.getGigyaConfig().getPartnerId());
		}
		final UserModel user = userService.getCurrentUser();
		if (!userService.isAnonymousUser(user) && user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
			final B2BUnitModel parentUnit = b2bUnitService.getParent(b2bCustomer);

			model.addAttribute("orgId", parentUnit.getGyUID());
		}
		return viewName;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.interceptor;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * DefaultGigyaB2BUnitModelValidateInterceptor which replaces the 'B2BUnitModelValidateInterceptor' and suppresses the
 * check where only 'admins' are allowed to create B2B Unit in Commerce when CreateOrganizationOnLogin flag is set to
 * 'true' in 'GigyaConfig'.
 */
public class DefaultGigyaB2BUnitModelValidateInterceptor implements ValidateInterceptor<B2BUnitModel>
{

	private static final String ERROR_B2BUNIT_ROOT_CREATE_NONADMIN = "error.b2bunit.root.create.nonadmin";
	private static final Logger LOG = Logger.getLogger(DefaultGigyaB2BUnitModelValidateInterceptor.class);

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	private UserService userService;

	private ModelService modelService;

	private L10NService l10NService;

	private BaseSiteService baseSiteService;


	@Override
	public void onValidate(final B2BUnitModel model, final InterceptorContext ctx) throws InterceptorException
	{
		final B2BUnitModel unit = model;

		// check this on newly created models.
		// A b2b Unit without a parent is only allowed to be created by a user belonging to 'admingroup'.
		// This check only happens when 'createOrganizationOnLogin' flag is false in GigyaConfig

		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		if (baseSite != null && baseSite.getGigyaConfig() != null && baseSite.getGigyaConfig().isCreateOrganizationOnLogin())
		{
			// Allow creation of organization on login via CDC screen
		}
		else
		{
			if (ctx.getModelService().isNew(model) && getB2bUnitService().getParent(unit) == null
					&& !getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getAdminUserGroup()))
			{
				throw new InterceptorException(getL10NService().getLocalizedString(ERROR_B2BUNIT_ROOT_CREATE_NONADMIN));
			}
		}

		// ensure all approvers of the unit are members of the b2bapprovergroup
		if (unit.getApprovers() != null)
		{
			final HashSet<B2BCustomerModel> unitApprovers = new HashSet<>(unit.getApprovers());
			if (CollectionUtils.isNotEmpty(unitApprovers))
			{
				final UserGroupModel b2bApproverGroup = getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);

				final Iterator<B2BCustomerModel> iterator = unitApprovers.iterator();
				while (iterator.hasNext())
				{
					final B2BCustomerModel approver = iterator.next();
					if (!getUserService().isMemberOfGroup(approver, b2bApproverGroup))
					{
						// remove approvers who are not in the b2bapprovergroup.
						iterator.remove();
						LOG.warn(String.format("Removed approver %s from unit %s due to lack of membership of group %s",
								approver.getUid(), unit.getUid(), B2BConstants.B2BAPPROVERGROUP));

					}
				}
				unit.setApprovers(unitApprovers);
			}
		}

		//ensures that all of a deactivated unit's subunit's are also deactivated (except in case of new unit).
		if (!unit.getActive().booleanValue() && !ctx.getModelService().isNew(model))
		{
			final Set<B2BUnitModel> childUnits = getB2bUnitService().getB2BUnits(unit);

			for (final B2BUnitModel child : childUnits)
			{
				if (child.getActive().booleanValue())
				{
					child.setActive(Boolean.FALSE);
					getModelService().save(child);
				}
			}
		}
	}


	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}


	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}


	public UserService getUserService()
	{
		return userService;
	}


	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


	public ModelService getModelService()
	{
		return modelService;
	}


	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


	public L10NService getL10NService()
	{
		return l10NService;
	}


	public void setL10NService(final L10NService l10nService)
	{
		l10NService = l10nService;
	}


	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}


	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}

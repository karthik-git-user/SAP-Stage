/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacadescep.user.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.ScimUserMeta;
import de.hybris.platform.scimfacades.user.populators.ScimUserPopulator;
import de.hybris.platform.scimfacadescep.constants.ScimfacadescepConstants;


/**
 *
 */
public class EnhancedScimUserPopulator extends ScimUserPopulator implements Populator<UserModel, ScimUser>
{
	@Override
	public void populate(final UserModel userModel, final ScimUser scimUser)
	{
		super.populate(userModel, scimUser);
		final ScimUserMeta meta = new ScimUserMeta();
		meta.setVersion(ScimfacadescepConstants.META_VERSION);
		meta.setCreated(userModel.getCreationtime());
		meta.setLastModified(userModel.getModifiedtime());
		scimUser.setMeta(meta);
	}
}

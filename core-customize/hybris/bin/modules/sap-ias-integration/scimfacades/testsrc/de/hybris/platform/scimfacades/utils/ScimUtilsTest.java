/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.utils;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.scimfacades.ScimUserEmail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


@UnitTest
public class ScimUtilsTest
{

	@Test
	public void testGetPrimaryEmailWhenEmailsDoesntExist()
	{
		Assert.assertNull(ScimUtils.getPrimaryEmail(null));
	}

	@Test
	public void testGetPrimaryEmailWhenEmailsExist()
	{
		final ScimUserEmail email1 = new ScimUserEmail();
		email1.setPrimary(false);
		email1.setValue("value1");

		final ScimUserEmail email2 = new ScimUserEmail();
		email2.setPrimary(true);
		email2.setValue("value2");

		final List<ScimUserEmail> emails = new ArrayList<>();
		emails.add(email1);
		emails.add(email2);

		final ScimUserEmail primaryEmail = ScimUtils.getPrimaryEmail(emails);
		Assert.assertNotNull(primaryEmail);
		Assert.assertEquals(email2, primaryEmail);
	}

}

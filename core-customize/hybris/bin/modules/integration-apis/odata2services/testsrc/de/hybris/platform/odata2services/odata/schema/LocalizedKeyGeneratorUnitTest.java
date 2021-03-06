/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.schema;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class LocalizedKeyGeneratorUnitTest extends BaseKeyGeneratorUnitTest<LocalizedKeyGenerator>
{
	@Override
	protected LocalizedKeyGenerator createKeyGenerator()
	{
		return new LocalizedKeyGenerator();
	}

	@Override
	protected String getKeyPropertyName()
	{
		return "language";
	}
}

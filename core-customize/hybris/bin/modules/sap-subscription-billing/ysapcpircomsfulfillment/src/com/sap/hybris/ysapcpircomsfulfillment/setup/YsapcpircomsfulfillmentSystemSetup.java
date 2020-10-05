/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.ysapcpircomsfulfillment.setup;

import static com.sap.hybris.ysapcpircomsfulfillment.constants.YsapcpircomsfulfillmentConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.sap.hybris.ysapcpircomsfulfillment.constants.YsapcpircomsfulfillmentConstants;
import com.sap.hybris.ysapcpircomsfulfillment.service.YsapcpircomsfulfillmentService;


@SystemSetup(extension = YsapcpircomsfulfillmentConstants.EXTENSIONNAME)
public class YsapcpircomsfulfillmentSystemSetup
{
	private final YsapcpircomsfulfillmentService ysapcpircomsfulfillmentService;

	public YsapcpircomsfulfillmentSystemSetup(final YsapcpircomsfulfillmentService ysapcpircomsfulfillmentService)
	{
		this.ysapcpircomsfulfillmentService = ysapcpircomsfulfillmentService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		ysapcpircomsfulfillmentService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return YsapcpircomsfulfillmentSystemSetup.class.getResourceAsStream("/ysapcpircomsfulfillment/sap-hybris-platform.png");
	}
}

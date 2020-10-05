/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegrationoms.setup;

import static com.sap.hybris.sapquoteintegrationoms.constants.SapquoteintegrationomsConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.sap.hybris.sapquoteintegrationoms.constants.SapquoteintegrationomsConstants;
import com.sap.hybris.sapquoteintegrationoms.service.SapquoteintegrationomsService;


@SystemSetup(extension = SapquoteintegrationomsConstants.EXTENSIONNAME)
public class SapquoteintegrationomsSystemSetup
{
	private final SapquoteintegrationomsService sapquoteintegrationomsService;

	public SapquoteintegrationomsSystemSetup(final SapquoteintegrationomsService sapquoteintegrationomsService)
	{
		this.sapquoteintegrationomsService = sapquoteintegrationomsService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapquoteintegrationomsService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapquoteintegrationomsSystemSetup.class.getResourceAsStream("/sapquoteintegrationoms/sap-hybris-platform.png");
	}
}

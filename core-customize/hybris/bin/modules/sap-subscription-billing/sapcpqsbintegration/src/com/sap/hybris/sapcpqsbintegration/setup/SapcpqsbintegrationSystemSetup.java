/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbintegration.setup;

import com.sap.hybris.sapcpqsbintegration.constants.SapcpqsbintegrationConstants;
import com.sap.hybris.sapcpqsbintegration.service.SapcpqsbintegrationService;
import de.hybris.platform.core.initialization.SystemSetup;


@SystemSetup(extension = SapcpqsbintegrationConstants.EXTENSIONNAME)
public class SapcpqsbintegrationSystemSetup {
    @SuppressWarnings("unused")
    private final SapcpqsbintegrationService sapcpqsbintegrationService;

    public SapcpqsbintegrationSystemSetup(final SapcpqsbintegrationService sapcpqsbintegrationService) {
        this.sapcpqsbintegrationService = sapcpqsbintegrationService;
    }

}

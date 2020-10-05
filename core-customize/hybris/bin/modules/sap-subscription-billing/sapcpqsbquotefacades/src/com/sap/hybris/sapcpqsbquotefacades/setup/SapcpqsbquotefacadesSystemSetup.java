/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqsbquotefacades.setup;

import com.sap.hybris.sapcpqsbquotefacades.constants.SapcpqsbquotefacadesConstants;
import com.sap.hybris.sapcpqsbquotefacades.service.SapcpqsbquotefacadesService;
import de.hybris.platform.core.initialization.SystemSetup;


@SystemSetup(extension = SapcpqsbquotefacadesConstants.EXTENSIONNAME)
public class SapcpqsbquotefacadesSystemSetup {
    @SuppressWarnings("unused")
    private final SapcpqsbquotefacadesService sapcpqsbquotefacadesService;

    public SapcpqsbquotefacadesSystemSetup(final SapcpqsbquotefacadesService sapcpqsbquotefacadesService) {
        this.sapcpqsbquotefacadesService = sapcpqsbquotefacadesService;
    }


}

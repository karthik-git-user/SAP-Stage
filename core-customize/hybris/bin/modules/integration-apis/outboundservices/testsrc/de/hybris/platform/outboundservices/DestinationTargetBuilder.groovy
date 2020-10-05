/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices

import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.importImpEx

class DestinationTargetBuilder {
    private static final String DEFAULT_ID = "stoutoutboundtest"

    private String destinationId

    static DestinationTargetBuilder destinationTargetBuilder() {
        return new DestinationTargetBuilder()
    }

    DestinationTargetBuilder withId(final String id) {
        destinationId = id
        this
    }

    DestinationTargetModel build() {
        destinationTarget(destinationId)
    }

    private static DestinationTargetModel destinationTarget(String id) {
        def idVal = deriveId(id)
        importImpEx(
                'INSERT_UPDATE DestinationTarget; id[unique = true]',
                "                               ; $idVal")
        getDestinationTargetById(idVal)
    }

    private static String deriveId(String id) {
        id ?: DEFAULT_ID
    }

    private static DestinationTargetModel getDestinationTargetById(String id) {
        IntegrationTestUtil.findAny(DestinationTargetModel, { it.id == id }).orElse(null)
    }
}

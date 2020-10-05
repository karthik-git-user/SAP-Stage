/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancellationReversal {

    @JsonProperty("metaData")
    private MetaData metaData;

    @JsonProperty("metaData")
    public MetaData getMetaData() {
        return metaData;
    }

    @JsonProperty("metaData")
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

}
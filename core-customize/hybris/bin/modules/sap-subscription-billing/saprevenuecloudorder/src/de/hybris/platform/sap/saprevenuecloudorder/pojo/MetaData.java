/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @deprecated since 1905.09
 */
@Deprecated(since = "1905.09", forRemoval = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version"
})
public class MetaData {

    /**
     * The Version Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("version")
    @JsonPropertyDescription("version of the subscription.")
    private String version = "";


    /**
     * The Version Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    /**
     * The Version Schema
     * <p>
     * An explanation about the purpose of this instance.
     * 
     */
    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

}

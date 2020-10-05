/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @deprecated since 1905.09
 */
@Deprecated(since = "1905.09", forRemoval = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatePlan {

    private String id;
    private String source;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "RatePlan{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}

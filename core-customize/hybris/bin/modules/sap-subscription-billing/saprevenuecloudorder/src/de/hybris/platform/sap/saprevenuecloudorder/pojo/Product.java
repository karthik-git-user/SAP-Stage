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
public class Product {

    private String id;
    private String code;
    private String version;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Snapshot {

    private String effectiveDate;
    private PrecedingDocument precedingDocument;
    private List<Item> items;
    private String createdAt;

    
    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public PrecedingDocument getPrecedingDocument() {
        return precedingDocument;
    }

    public void setPrecedingDocument(PrecedingDocument precedingDocument) {
        this.precedingDocument = precedingDocument;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "effectiveDate=" + effectiveDate +
                ", precedingDocument=" + precedingDocument +
                ", items=" + items +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}

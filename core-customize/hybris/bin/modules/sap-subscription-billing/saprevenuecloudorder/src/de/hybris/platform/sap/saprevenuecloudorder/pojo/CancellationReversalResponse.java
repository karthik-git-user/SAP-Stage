/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

public class CancellationReversalResponse {
    private String validUntil;
    private boolean validUntilIsUnlimited;

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isValidUntilIsUnlimited() {
        return validUntilIsUnlimited;
    }

    public void setValidUntilIsUnlimited(boolean validUntilIsUnlimited) {
        this.validUntilIsUnlimited = validUntilIsUnlimited;
    }
}

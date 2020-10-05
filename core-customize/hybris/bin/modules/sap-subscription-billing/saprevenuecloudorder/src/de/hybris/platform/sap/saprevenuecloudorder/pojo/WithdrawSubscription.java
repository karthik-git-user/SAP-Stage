/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "metaData"})
public class WithdrawSubscription {
	@JsonProperty("metaData")
	private MetaData metaData;
	


	@JsonProperty("metaData")
	public MetaData getMetaData() {
		return metaData;
	}

	@JsonProperty("metaData")
	public void setMetaData(final MetaData metaData) {
		this.metaData = metaData;
	}
	
	@Override
    public String toString() {
        return "WithdrawSubscription{" + "metaData='" + metaData  + '}';
    }

}

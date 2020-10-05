/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsintegration.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Entitlement implements Serializable
{

	/** Default serialVersionUID value. */

	private static final long serialVersionUID = 1L;

	@JsonProperty("EntitlementGuid")
	private String entitlementGuid;

	@JsonProperty("EntitlementNo")
	private Integer entitlementNo;

	@JsonProperty("ValidFrom")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date validFrom;

	@JsonProperty("ValidTo")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date validTo;

	@JsonProperty("StatusName")
	private String statusName;

	@JsonProperty("RefDocNo")
	private String refDocNo;

	@JsonProperty("TheRight")
	private TheRight theRight;

	@JsonProperty("Geolocation")
	private String geolocation;

	@JsonProperty("BusinessCategory")
	private BusinessCategory businessCategory;

	@JsonProperty("OfferingID")
	private String offeringID;

	@JsonProperty("EntitlementModelName")
	private String entitlementModelName;

	@JsonProperty("Quantity")
	private Integer quantity;

	@JsonProperty("Uom")
	private Uom uom;

	@JsonProperty("EntitlementTypeName")
	private String entitlementTypeName;

	private final transient Map<String, Object> customAttributes = new HashMap<>();

	@JsonProperty("EntitlementGuid")
	public void setEntitlementGuid(final String entitlementGuid)
	{
		this.entitlementGuid = entitlementGuid;
	}

	@JsonProperty("EntitlementGuid")
	public String getEntitlementGuid()
	{
		return entitlementGuid;
	}

	@JsonProperty("EntitlementNo")
	public void setEntitlementNo(final Integer entitlementNo)
	{
		this.entitlementNo = entitlementNo;
	}

	@JsonProperty("EntitlementNo")
	public Integer getEntitlementNo()
	{
		return entitlementNo;
	}

	@JsonProperty("ValidFrom")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	public void setValidFrom(final Date validFrom)
	{
		this.validFrom = validFrom;
	}

	@JsonProperty("ValidFrom")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	public Date getValidFrom()
	{
		return validFrom;
	}

	@JsonProperty("ValidTo")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	public void setValidTo(final Date validTo)
	{
		this.validTo = validTo;
	}

	@JsonProperty("ValidTo")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	public Date getValidTo()
	{
		return validTo;
	}

	@JsonProperty("StatusName")
	public void setStatusName(final String statusName)
	{
		this.statusName = statusName;
	}

	@JsonProperty("StatusName")
	public String getStatusName()
	{
		return statusName;
	}

	@JsonProperty("RefDocNo")
	public void setRefDocNo(final String refDocNo)
	{
		this.refDocNo = refDocNo;
	}

	@JsonProperty("RefDocNo")
	public String getRefDocNo()
	{
		return refDocNo;
	}

	@JsonProperty("TheRight")
	public void setTheRight(final TheRight theRight)
	{
		this.theRight = theRight;
	}

	@JsonProperty("TheRight")
	public TheRight getTheRight()
	{
		return theRight;
	}

	@JsonProperty("Geolocation")
	public void setGeolocation(final String geolocation)
	{
		this.geolocation = geolocation;
	}

	@JsonProperty("Geolocation")
	public String getGeolocation()
	{
		return geolocation;
	}

	@JsonProperty("BusinessCategory")
	public void setBusinessCategory(final BusinessCategory businessCategory)
	{
		this.businessCategory = businessCategory;
	}

	@JsonProperty("BusinessCategory")
	public BusinessCategory getBusinessCategory()
	{
		return businessCategory;
	}

	@JsonProperty("OfferingID")
	public void setOfferingID(final String offeringID)
	{
		this.offeringID = offeringID;
	}

	@JsonProperty("OfferingID")
	public String getOfferingID()
	{
		return offeringID;
	}

	@JsonProperty("EntitlementModelName")
	public void setEntitlementModelName(final String entitlementModelName)
	{
		this.entitlementModelName = entitlementModelName;
	}

	@JsonProperty("EntitlementModelName")
	public String getEntitlementModelName()
	{
		return entitlementModelName;
	}

	@JsonProperty("Quantity")
	public void setQuantity(final Integer quantity)
	{
		this.quantity = quantity;
	}

	@JsonProperty("Quantity")
	public Integer getQuantity()
	{
		return quantity;
	}

	@JsonProperty("Uom")
	public void setUom(final Uom uom)
	{
		this.uom = uom;
	}

	@JsonProperty("Uom")
	public Uom getUom()
	{
		return uom;
	}

	@JsonProperty("EntitlementTypeName")
	public void setEntitlementTypeName(final String entitlementTypeName)
	{
		this.entitlementTypeName = entitlementTypeName;
	}

	@JsonProperty("EntitlementTypeName")
	public String getEntitlementTypeName()
	{
		return entitlementTypeName;
	}

	@JsonAnySetter
	public void setCustomAttributes(final String key, final Object value)
	{
		this.customAttributes.put(key, value);
	}

	public Map<String, Object> getCustomAttributes()
	{
		return customAttributes;
	}


}

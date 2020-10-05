/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.reservation.jaxb.pojos.request;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Jaxb Pojo for XML creation
 */
@XmlRootElement(namespace = "http://www.sap.com/abapxml", name = "abap")
public class ReservationAbapRequest
{

	private ReservationValuesRequest values;

	public ReservationAbapRequest()
	{
		super();
	}

	public ReservationAbapRequest(final ReservationValuesRequest values)
	{
		super();
		this.values = values;
	}

	@XmlElement(namespace = "http://www.sap.com/abapxml")
	public ReservationValuesRequest getValues()
	{
		return values;
	}

	public void setValues(final ReservationValuesRequest values)
	{
		this.values = values;
	}

	@Override
	public String toString()
	{
		return "Abap [values=" + values.toString() + "]";
	}


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudcustomer.service;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sap.hybris.saprevenuecloudcustomer.dto.Customer;
import com.sap.hybris.scpiconnector.data.ResponseData;

import rx.Observable;


/**
 * Replicates customer data to Revenue Cloud via CPI
 */
public interface SapRevenueCloudCustomerOutboundService
{

	/**
	 * Send customer data to revenue cloud via CPI.
	 * @param customerModel customer model
	 * @param baseStoreUid base store uid
	 * @param sessionLanguage session language
	 * @param addressModel address model
	 * @return Observable of response
	 */
	Observable<ResponseEntity<Map>> sendCustomerData(final CustomerModel customerModel, final String baseStoreUid,
			final String sessionLanguage, final AddressModel addressModel);

	/**
	 * Triggers Customer Update iflow in Cloud Platform Integration which fetches the customer data from Revenue Cloud
	 * and updates in Commerce
	 *
	 * @param customerJson
	 *           Customer Json object
	 *
	 * @throws IOException
	 *            if unable to publish.
	 *
	 * @return {@link ResponseData}
	 *
	 */
	ResponseData publishCustomerUpdate(Customer customerJson) throws IOException;
}

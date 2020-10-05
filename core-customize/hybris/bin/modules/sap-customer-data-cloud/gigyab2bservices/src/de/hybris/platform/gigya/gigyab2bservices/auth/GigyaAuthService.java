/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.auth;

import de.hybris.platform.core.model.user.CustomerModel;


/**
 * Service to assign authorizations to B2B Customer based on the authorizations received from SAP Customer Data Cloud
 */
public interface GigyaAuthService
{

	/**
	 * Method to assign authorisations to the customer after fetching it from SAP CDC.
	 *
	 * @param customer
	 *           The customer model  
	 */
	void assignAuthorisationsToCustomer(CustomerModel customer);

	/**
	 * Method to remove authorisations from the customer.
	 *
	 * @param customer
	 *           The customer model  
	 */
	void removeAuthorisationsOfCustomer(CustomerModel customer);

}

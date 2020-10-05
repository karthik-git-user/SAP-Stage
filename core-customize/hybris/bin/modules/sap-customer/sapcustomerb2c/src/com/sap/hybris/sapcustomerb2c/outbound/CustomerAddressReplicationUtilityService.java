/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;

import java.util.Set;

/**
 * A utility service to check if a customer or an address has to be replicated to the backend system
 */
public interface CustomerAddressReplicationUtilityService {

  /**
   * Varify if customer is changed
   * @param customerModel Customer to be verified
   * @param monitoredAttributes Attributes to be used to determine whether the customer should be replicated
   * @param context Context passed by the intercepter
   * @return true if customer is changed
   */
  boolean isCustomerReplicationRequired(final CustomerModel customerModel, final Set<String> monitoredAttributes, final InterceptorContext context);

  /**
   * Varify if address is changed
   * @param addressModel Address to be verified 
   * @param monitoredAttributes Attributes to be used to determine whether the address should be replicated
   * @param context Context passed by the intercepter
   * @return true if address is changed
   */
  boolean isAddressReplicationRequired(final AddressModel addressModel, final Set<String> monitoredAttributes, final InterceptorContext context);

  /**
   * Find customer default address
   * @param customerModel Customer requiring default address
   * @param addressModel Address to be determined if default
   * @return AddressModel
   */
  AddressModel findDefaultAddress(final CustomerModel customerModel, final AddressModel addressModel);

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * SAP aspect to intercept the DefaultCustomerAccountService to inject SAP customer ID generator
 */
public interface SapCustomerAccountServiceAspect {

  /**
   * Intercept the default implementation of the register method {@link DefaultCustomerAccountService#register(CustomerModel, String)}
   * @param pjp
   * @throws DuplicateUidException
   */
  void interceptRegister(ProceedingJoinPoint pjp) throws DuplicateUidException;


  /**
   * Intercept the default implementation of the registerGuestForAnonymousCheckout method {@link DefaultCustomerAccountService#registerGuestForAnonymousCheckout(CustomerModel, String)}
   * @param pjp
   * @throws DuplicateUidException
   */
  void interceptRegisterGuestForAnonymousCheckout(ProceedingJoinPoint pjp) throws DuplicateUidException;

}

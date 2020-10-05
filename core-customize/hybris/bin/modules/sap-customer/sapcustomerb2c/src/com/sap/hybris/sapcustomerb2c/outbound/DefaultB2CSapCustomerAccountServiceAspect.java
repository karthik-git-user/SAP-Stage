/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.customer.impl.DefaultCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * The default SAP implementation of {@link SapCustomerAccountServiceAspect} to inject SAP customer ID generator
 */
public class DefaultB2CSapCustomerAccountServiceAspect extends DefaultCustomerAccountService implements SapCustomerAccountServiceAspect {

  private PersistentKeyGenerator sapCustomerIdGenerator;

  @Override
  public void interceptRegister(ProceedingJoinPoint pjp) throws DuplicateUidException {
    register((CustomerModel) pjp.getArgs()[0], (String) pjp.getArgs()[1] );
  }

  @Override
  public void interceptRegisterGuestForAnonymousCheckout(ProceedingJoinPoint pjp) throws DuplicateUidException {
    registerGuestForAnonymousCheckout((CustomerModel) pjp.getArgs()[0], (String) pjp.getArgs()[1] );
  }

  @Override
  protected void generateCustomerId(CustomerModel customerModel) {
    customerModel.setCustomerID((String) getSapCustomerIdGenerator().generate());
  }

  protected PersistentKeyGenerator getSapCustomerIdGenerator() {
    return sapCustomerIdGenerator;
  }

  public void setSapCustomerIdGenerator(PersistentKeyGenerator sapCustomerIdGenerator) {
    this.sapCustomerIdGenerator = sapCustomerIdGenerator;
  }

}

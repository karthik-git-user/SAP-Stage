/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;


/**
 * If default shipment address was updated send default shipment address to Data Hub in case of user replication is
 * active and the address is related to a sap consumer. This is indicated by the filled sap contact id.
 */
public class DefaultCustomerInterceptor implements ValidateInterceptor<CustomerModel> {

  private static final Logger LOGGER = Logger.getLogger(DefaultCustomerInterceptor.class);

  private DefaultStoreSessionFacade storeSessionFacade;
  private CustomerExportService customerExportService;
  private BaseStoreService baseStoreService;
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;

  @Override
  public void onValidate(final CustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException {

    if (!getCustomerExportService().isCustomerReplicationEnabled()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("'Replicate Registered Users' flag in 'SAP Global Configuration' is set to 'false'.");
        LOGGER.debug("Customer with customer ID " + customerModel.getCustomerID() + " was not sent to Data Hub.");
      }
      return;
    }

    /*
     * Needed in case of co-deployment of sapcustomerb2c with sapcustomerb2b, because onValidate() will also be called
     * for B2BCustomerModel.
     *
     * Encapsulation into a method (isClassCustomerModel()) needed for mocking purposes; Mockito can't mock final
     * methods like Object.getClass()
     *
     */
    if (!getCustomerExportService().isClassCustomerModel(customerModel)) {
      return;
    }

    // interceptor only used to replicate changes to existing customers
    if (customerModel.getSapContactID() == null) {
      return;
    }

    // check if one of the supported fields was modified
    if (getCustomerAddressReplicationUtilityService().isCustomerReplicationRequired(customerModel, getMonitoredAttributes(), ctx)) {

      final String baseStoreUid = baseStoreService.getCurrentBaseStore() != null
              ? baseStoreService.getCurrentBaseStore().getUid() : null;
      final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
              ? getStoreSessionFacade().getCurrentLanguage().getIsocode() : null;
      getCustomerExportService().sendCustomerData(customerModel, baseStoreUid, sessionLanguage,
              getCustomerAddressReplicationUtilityService().findDefaultAddress(customerModel, customerModel.getDefaultShipmentAddress()));

    }

  }

  protected Set<String> getMonitoredAttributes() {

    Set<String> monitoredAttributes = new HashSet<>();

    monitoredAttributes.add(CustomerModel.UID);
    monitoredAttributes.add(CustomerModel.CUSTOMERID);
    monitoredAttributes.add(CustomerModel.SAPCONTACTID);
    monitoredAttributes.add(CustomerModel.NAME);
    monitoredAttributes.add(CustomerModel.SESSIONLANGUAGE);
    monitoredAttributes.add(CustomerModel.TITLE);
    monitoredAttributes.add(CustomerModel.DEFAULTSHIPMENTADDRESS);

    return monitoredAttributes;

  }

  /**
   * @return storeSessionFacade
   */
  public DefaultStoreSessionFacade getStoreSessionFacade() {
    return storeSessionFacade;
  }

  /**
   * set storeSessionFacade
   *
   * @param storeSessionFacade
   */
  public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade) {
    this.storeSessionFacade = storeSessionFacade;
  }

  /**
   * @return customerExportService
   */
  public CustomerExportService getCustomerExportService() {
    return customerExportService;
  }

  /**
   * set customerExportService
   *
   * @param customerExportService
   */
  public void setCustomerExportService(final CustomerExportService customerExportService) {
    this.customerExportService = customerExportService;
  }

  /**
   * @return baseStoreService
   */
  public BaseStoreService getBaseStoreService() {
    return baseStoreService;
  }

  /**
   * set baseStoreService
   *
   * @param baseStoreService
   */
  public void setBaseStoreService(final BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }


  protected CustomerAddressReplicationUtilityService getCustomerAddressReplicationUtilityService() {
    return customerAddressReplicationUtilityService;
  }

  public void setCustomerAddressReplicationUtilityService(CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService) {
    this.customerAddressReplicationUtilityService = customerAddressReplicationUtilityService;
  }

}
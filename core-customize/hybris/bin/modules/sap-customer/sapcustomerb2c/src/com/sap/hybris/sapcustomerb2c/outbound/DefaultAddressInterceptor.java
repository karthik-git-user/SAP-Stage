/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
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
public class DefaultAddressInterceptor implements ValidateInterceptor<AddressModel> {

  private static final Logger LOGGER = Logger.getLogger(DefaultAddressInterceptor.class);

  private DefaultStoreSessionFacade storeSessionFacade;
  private CustomerExportService customerExportService;
  private BaseStoreService baseStoreService;
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;

  @Override
  public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException {

    if (!getCustomerExportService().isCustomerReplicationEnabled()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("'Replicate Registered Users' flag in 'SAP Global Configuration' is set to 'false'.");
        LOGGER.debug("Address " + addressModel.getPk() + " was not sent to Data Hub.");
      }
      return;
    }

    // we only replicate the address of a CustomerModel
    if (!getCustomerExportService().isClassCustomerModel(addressModel.getOwner())) {
      return;
    }

    final CustomerModel customerModel = ((CustomerModel) addressModel.getOwner());

    // interceptor only used to replicate changes to existing customers
    if (customerModel.getSapContactID() == null) {
      return;
    }

    // check if one of the supported fields was modified
    if (getCustomerAddressReplicationUtilityService().isAddressReplicationRequired(addressModel, getMonitoredAttributes(), ctx)) {
      updateAndSendCustomerData(getCustomerAddressReplicationUtilityService().findDefaultAddress(customerModel, addressModel), customerModel);
    }

  }

  private void updateAndSendCustomerData(AddressModel addressModel, CustomerModel customerModel) {
    final String baseStoreUid = baseStoreService.getCurrentBaseStore() != null
            ? baseStoreService.getCurrentBaseStore().getUid()
            : null;
    final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
            ? getStoreSessionFacade().getCurrentLanguage().getIsocode()
            : null;
    getCustomerExportService().sendCustomerData(customerModel, baseStoreUid, sessionLanguage, addressModel);

  }

  protected Set<String> getMonitoredAttributes() {

    Set<String> monitoredAttributes = new HashSet<>();

    monitoredAttributes.add(AddressModel.COUNTRY);
    monitoredAttributes.add(AddressModel.STREETNUMBER);
    monitoredAttributes.add(AddressModel.STREETNAME);
    monitoredAttributes.add(AddressModel.PHONE1);
    monitoredAttributes.add(AddressModel.FAX);
    monitoredAttributes.add(AddressModel.TOWN);
    monitoredAttributes.add(AddressModel.POSTALCODE);
    monitoredAttributes.add(AddressModel.REGION);

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

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2b.outbound;

import com.sap.hybris.sapcustomerb2c.outbound.CustomerAddressReplicationUtilityService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;


public class DefaultB2BCustomerAddressInterceptor implements ValidateInterceptor<AddressModel> {
  private static final Logger LOGGER = Logger.getLogger(com.sap.hybris.sapcustomerb2b.outbound.DefaultB2BCustomerAddressInterceptor.class.getName());

  private B2BCustomerExportService b2bCustomerExportService;
  private DefaultStoreSessionFacade storeSessionFacade;
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;

  @Override
  public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException {
    if (!getB2bCustomerExportService().isB2BCustomerReplicationEnabled()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("'Replicate B2B Customers' flag in 'SAP Base Store Configuration' is set to 'false'.");
        LOGGER.debug("Address " + addressModel.getPk() + " was not sent to Data Hub.");
      }
      return;
    }

    final ItemModel owner = addressModel.getOwner();

    // we only replicate the address of a B2BCustomerModel
    if (!(owner instanceof B2BCustomerModel)) {
      return;
    }

    final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) owner;
    if (b2bCustomerModel.getCustomerID() == null || b2bCustomerModel.getCustomerID().isEmpty()) {
      return;
    }

    if (getCustomerAddressReplicationUtilityService().isAddressReplicationRequired(addressModel, getMonitoredAttributes(), ctx)) {

      final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
              ? getStoreSessionFacade().getCurrentLanguage().getIsocode() : "en";

      LOGGER.debug("The modified B2B customer default address will be sent to Data Hub!");
      getB2bCustomerExportService().prepareAndSend(b2bCustomerModel, sessionLanguage);

    }

  }

  protected Set<String> getMonitoredAttributes() {

    Set<String > monitoredAttributes = new HashSet<>();

    monitoredAttributes.add(AddressModel.COUNTRY);
    monitoredAttributes.add(AddressModel.PHONE1);

    return monitoredAttributes;

  }

  public DefaultStoreSessionFacade getStoreSessionFacade() {
    return storeSessionFacade;
  }

  public void setStoreSessionFacade(final DefaultStoreSessionFacade storeSessionFacade) {
    this.storeSessionFacade = storeSessionFacade;
  }

  public B2BCustomerExportService getB2bCustomerExportService() {
    return b2bCustomerExportService;
  }

  public void setB2bCustomerExportService(final B2BCustomerExportService b2bCustomerExportService) {
    this.b2bCustomerExportService = b2bCustomerExportService;
  }

  protected CustomerAddressReplicationUtilityService getCustomerAddressReplicationUtilityService() {
    return customerAddressReplicationUtilityService;
  }

  public void setCustomerAddressReplicationUtilityService(CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService) {
    this.customerAddressReplicationUtilityService = customerAddressReplicationUtilityService;
  }

}

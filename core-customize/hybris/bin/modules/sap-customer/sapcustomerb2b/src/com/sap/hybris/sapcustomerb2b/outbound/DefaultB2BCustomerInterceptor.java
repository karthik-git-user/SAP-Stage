/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2b.outbound;

import com.sap.hybris.sapcustomerb2c.outbound.CustomerAddressReplicationUtilityService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import org.apache.log4j.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;


public class DefaultB2BCustomerInterceptor implements ValidateInterceptor<B2BCustomerModel> {

  private static final Logger LOGGER = Logger.getLogger(com.sap.hybris.sapcustomerb2b.outbound.DefaultB2BCustomerInterceptor.class.getName());
  private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ").withZone(ZoneId.of("UTC"));

  private B2BCustomerExportService b2bCustomerExportService;
  private DefaultStoreSessionFacade storeSessionFacade;
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;
  private PersistentKeyGenerator sapContactIdGenerator;

  @Override
  public void onValidate(final B2BCustomerModel customerModel, final InterceptorContext ctx) throws InterceptorException {

    if (!getB2bCustomerExportService().isB2BCustomerReplicationEnabled()) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("'Replicate B2B Customers' flag in 'SAP Base Store Configuration' is set to 'false'.");
        LOGGER.debug("B2B Customer with customer ID " + customerModel.getCustomerID() + " was not sent to Data Hub.");
      }
      return;
    }

    /* ********** Obsolete Comment ****************
     * This interceptor is used to replicate changes to existing B2B customers. New B2B customers are handled and
     * replicated to Data Hub by B2BCustomerRegistrationEventListener. Therefore check ctx.isNew(customerModel).
     *
     * This interceptor is also called when B2B customers are received from Data Hub and Data Hub always sets the
     * sapIsReplicated flag when sending B2B customers. But sapIsReplicated can't be set from the storefront.
     * Therefore, to avoid replicating B2B customers that have just been received from Data Hub back to Data Hub,
     * check ctx.isModified(customerModel, "sapIsReplicated").
     * ********** Obsolete Comment ****************/

    /*
     * The previous comment is obsolete and left here for reference.
     * This interceptor has been updated to handle new B2B customers.
     */

    if (isB2BCustomerReplicatedFromSapBackend(customerModel, ctx)) {
      LOGGER.debug("The B2B customer has been replicated form SAP backend and will not be sent again to SAP backend!");
      return;
    }

    if (isNewB2BCustomer(customerModel, ctx)) {
      LOGGER.debug("The new B2B customer will be sent to SAP backend!");
      sendB2BCustomerToSapBackend(customerModel);
      return;
    }

    if (getCustomerAddressReplicationUtilityService().isCustomerReplicationRequired(customerModel, getMonitoredAttributes(), ctx)) {

      final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
              ? getStoreSessionFacade().getCurrentLanguage().getIsocode() : "en";

      LOGGER.debug("The modified B2B customer will be sent to SAP backend!");
      getB2bCustomerExportService().prepareAndSend(customerModel, sessionLanguage);

    }

  }

  protected boolean isNewB2BCustomer(B2BCustomerModel customerModel, InterceptorContext ctx) {
    return ctx.isNew(customerModel);
  }

  protected boolean isB2BCustomerReplicatedFromSapBackend(B2BCustomerModel customerModel, InterceptorContext ctx) {
    return ctx.isNew(customerModel) && customerModel.getSapIsReplicated();
  }

  protected void sendB2BCustomerToSapBackend(B2BCustomerModel b2bCustomer) {

    if (b2bCustomer.getCustomerID() == null || b2bCustomer.getCustomerID().isEmpty())
    {
      b2bCustomer.setSapReplicationInfo(String.format("Sent to Data Hub at %s" , ZonedDateTime.now().format(DTF)));
      b2bCustomer.setCustomerID((String) getSapContactIdGenerator().generate());
      b2bCustomer.setLoginDisabled(true);
      final String sessionLanguage = getStoreSessionFacade().getCurrentLanguage() != null
              ? getStoreSessionFacade().getCurrentLanguage().getIsocode() : "en";
      LOGGER.debug("The new B2B customer will be sent to SAP backend!");
      getB2bCustomerExportService().prepareAndSend(b2bCustomer, sessionLanguage);
    }

  }

  protected Set<String> getMonitoredAttributes() {

    Set<String > monitoredAttributes = new HashSet<>();

    monitoredAttributes.add(B2BCustomerModel.UID);
    monitoredAttributes.add(B2BCustomerModel.EMAIL);
    monitoredAttributes.add(B2BCustomerModel.SAPCONSUMERID);
    monitoredAttributes.add(B2BCustomerModel.NAME);
    monitoredAttributes.add(B2BCustomerModel.SESSIONLANGUAGE);
    monitoredAttributes.add(B2BCustomerModel.TITLE);
    monitoredAttributes.add(B2BCustomerModel.DEFAULTSHIPMENTADDRESS);
    monitoredAttributes.add(B2BCustomerModel.GROUPS);
    monitoredAttributes.add(B2BCustomerModel.DEFAULTB2BUNIT);
    monitoredAttributes.add(B2BCustomerModel.DEFAULTSHIPMENTADDRESS);

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

  protected PersistentKeyGenerator getSapContactIdGenerator() {
    return sapContactIdGenerator;
  }

  public void setSapContactIdGenerator(PersistentKeyGenerator sapContactIdGenerator) {
    this.sapContactIdGenerator = sapContactIdGenerator;
  }

}
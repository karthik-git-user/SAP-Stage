/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.strategy;

import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.warehousing.externalfulfillment.strategy.ConsignmentPreFulfillmentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class SapConsignmentPreFulfillmentStrategy implements ConsignmentPreFulfillmentStrategy {

  private static final Logger LOG = LoggerFactory.getLogger(SapConsignmentPreFulfillmentStrategy.class);

  private SAPGlobalConfigurationService sapGlobalConfigurationService;

  /**
   * Perform any required action before sending the consignment to the external system
   *
   * @param consignment
   */
  @Override
  public void perform(ConsignmentModel consignment) {

    LOG.info("SAP consignment pre-fulfillment strategy has been called!");

  }

  /**
   * Check if the consignment can be sent to the external system
   *
   * @param consignment
   * @return
   */
  @Override
  public boolean canProceedAfterPerform(ConsignmentModel consignment) {

    LOG.info("Check if the customer has been replicated to the SAP backend to proceed!");

    final AbstractOrderModel order = consignment.getOrder();


    if (sapGlobalConfigurationService.getProperty("replicateregistereduser").equals(Boolean.TRUE)) {

      final CustomerModel customerModel = ((CustomerModel) order.getUser());
      final boolean isCustomerExported = customerModel.getSapIsReplicated().booleanValue();
      final boolean isGuestUser = isGuestUser(customerModel);
      final boolean isB2B = isB2BCase(order);

      return (isCustomerExported || isGuestUser || isB2B);

    }

    return true;

  }

  protected boolean isB2BCase(AbstractOrderModel orderModel) {

    return (orderModel.getSite() != null) && SiteChannel.B2B.equals(orderModel.getSite().getChannel());

  }

  protected boolean isGuestUser(CustomerModel customerModel) {

    return CustomerType.GUEST.equals(customerModel.getType());

  }

  protected SAPGlobalConfigurationService getSapGlobalConfigurationService() {

    return sapGlobalConfigurationService;

  }

  @Required
  public void setSapGlobalConfigurationService(SAPGlobalConfigurationService sapGlobalConfigurationService) {

    this.sapGlobalConfigurationService = sapGlobalConfigurationService;

  }

}

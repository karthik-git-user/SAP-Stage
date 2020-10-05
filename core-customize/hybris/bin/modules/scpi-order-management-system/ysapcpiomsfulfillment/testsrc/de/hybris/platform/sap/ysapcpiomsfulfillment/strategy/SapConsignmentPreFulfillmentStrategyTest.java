/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.strategy;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapConsignmentPreFulfillmentStrategyTest {


  @InjectMocks
  private SapConsignmentPreFulfillmentStrategy sapConsignmentPreFulfillmentStrategy;
  @Mock
  private SAPGlobalConfigurationService sapGlobalConfigurationService;

  private AbstractOrderModel order;

  @Before
  public void setUp() {

    order = new AbstractOrderModel();
    ConsignmentModel consignment = new ConsignmentModel();
    consignment.setOrder(order);
    order.setConsignments(Sets.newHashSet(consignment));

    CustomerModel customer = new CustomerModel();
    customer.setSapIsReplicated(false);
    order.setUser(customer);

    BaseSiteModel site = new BaseSiteModel();
    order.setSite(site);

    when(sapGlobalConfigurationService.getProperty("replicateregistereduser")).thenReturn(Boolean.TRUE);

  }

  @Test
  public void canProceedAfterPerformForGuestUser() {

    ((CustomerModel) order.getUser()).setType(CustomerType.GUEST);
    order.getSite().setChannel(SiteChannel.B2C);
    ((CustomerModel) order.getUser()).setSapIsReplicated(false);

    assertTrue(sapConsignmentPreFulfillmentStrategy.canProceedAfterPerform(order.getConsignments().stream().findFirst().get()));

  }

  @Test
  public void canProceedAfterPerformForB2CUser() {

    ((CustomerModel) order.getUser()).setType(null);
    order.getSite().setChannel(SiteChannel.B2C);

    ((CustomerModel) order.getUser()).setSapIsReplicated(false);
    assertFalse(sapConsignmentPreFulfillmentStrategy.canProceedAfterPerform(order.getConsignments().stream().findFirst().get()));

    ((CustomerModel) order.getUser()).setSapIsReplicated(true);
    assertTrue(sapConsignmentPreFulfillmentStrategy.canProceedAfterPerform(order.getConsignments().stream().findFirst().get()));

  }

  @Test
  public void canProceedAfterPerformForB2BUser() {

    ((CustomerModel) order.getUser()).setType(null);
    order.getSite().setChannel(SiteChannel.B2B);
    ((CustomerModel) order.getUser()).setSapIsReplicated(false);

    assertTrue(sapConsignmentPreFulfillmentStrategy.canProceedAfterPerform(order.getConsignments().stream().findFirst().get()));

  }

  @Test
  public void canProceedAfterPerformWithReplicationDisabled() {

    when(sapGlobalConfigurationService.getProperty("replicateregistereduser")).thenReturn(Boolean.FALSE);
    assertTrue(sapConsignmentPreFulfillmentStrategy.canProceedAfterPerform(order.getConsignments().stream().findFirst().get()));

  }


}
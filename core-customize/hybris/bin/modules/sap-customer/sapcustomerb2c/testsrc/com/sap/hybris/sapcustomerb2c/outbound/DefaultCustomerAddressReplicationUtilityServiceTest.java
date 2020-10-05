/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.internal.model.impl.ModelValueHistory;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;

import java.util.*;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

public class DefaultCustomerAddressReplicationUtilityServiceTest {

  @Mock
  private CustomerAccountService customerAccountService;
  @Mock
  private UserService userService;
  @Mock
  private AddressModel addressModel;
  @Mock
  private AddressModel defaultAddressModel;
  @Mock
  private CustomerModel customerModel;
  @Mock
  private InterceptorContext interceptorContextCustomer;
  @Mock
  private InterceptorContext interceptorContextAddress;
  @Mock
  private ModelValueHistory modelValueHistory;
  @Mock
  private ItemModelContextImpl itemModelContextImpl;

  @InjectMocks
  private DefaultCustomerAddressReplicationUtilityService defaultCustomerAddressReplicationUtilityService;

  @Before
  public void setUp() {

    MockitoAnnotations.initMocks(this);

    final Map<String, Set<Locale>> customerDirtyAttributes = new HashMap<>();
    customerDirtyAttributes.put(CustomerModel.NAME, Sets.newSet(Locale.CANADA, Locale.ENGLISH));

    final Map<String, Set<Locale>> addressDirtyAttributes = new HashMap<>();
    addressDirtyAttributes.put(AddressModel.COUNTRY, Sets.newSet(Locale.CANADA, Locale.ENGLISH));

    given(userService.getCurrentUser()).willReturn(customerModel);
    given(interceptorContextCustomer.getDirtyAttributes(any())).willReturn(customerDirtyAttributes);
    given(interceptorContextAddress.getDirtyAttributes(any())).willReturn(addressDirtyAttributes);

    given(itemModelContextImpl.getValueHistory()).willReturn(modelValueHistory);
    given(customerModel.getItemModelContext()).willReturn(itemModelContextImpl);
    given(addressModel.getItemModelContext()).willReturn(itemModelContextImpl);

  }

  @Test
  public void isCustomerReplicationRequiredReturnTrue() {

    given(modelValueHistory.getOriginalValue(CustomerModel.NAME)).willReturn("SAP");
    given(customerModel.getProperty(CustomerModel.NAME)).willReturn("Hybris");
    assertThat(defaultCustomerAddressReplicationUtilityService.isCustomerReplicationRequired(customerModel, Sets.newSet(CustomerModel.NAME), interceptorContextCustomer)).isTrue();

  }

  @Test
  public void isCustomerReplicationRequiredReturnFalse() {

    given(modelValueHistory.getOriginalValue(CustomerModel.NAME)).willReturn("SAP");
    given(customerModel.getProperty(CustomerModel.NAME)).willReturn("SAP");
    assertThat(defaultCustomerAddressReplicationUtilityService.isCustomerReplicationRequired(customerModel, Sets.newSet(CustomerModel.NAME), interceptorContextCustomer)).isFalse();

  }

  @Test
  public void isAddressReplicationRequiredReturnTrue() {

    given(modelValueHistory.getOriginalValue(AddressModel.COUNTRY)).willReturn("Canada");
    given(addressModel.getProperty(AddressModel.COUNTRY)).willReturn("Germany");
    assertThat(defaultCustomerAddressReplicationUtilityService.isAddressReplicationRequired(addressModel, Sets.newSet(AddressModel.COUNTRY), interceptorContextAddress)).isTrue();

  }

  @Test
  public void isAddressReplicationRequiredReturnFalse() {

    given(modelValueHistory.getOriginalValue(AddressModel.COUNTRY)).willReturn("Canada");
    given(addressModel.getProperty(AddressModel.COUNTRY)).willReturn("Canada");
    assertThat(defaultCustomerAddressReplicationUtilityService.isAddressReplicationRequired(addressModel, Sets.newSet(AddressModel.COUNTRY), interceptorContextAddress)).isFalse();

  }

  @Test
  public void findDefaultAddressForNewlyCreatedAddress() {

    given(customerAccountService.getAddressBookEntries(any())).willReturn(null);
    given(customerAccountService.getDefaultAddress(any())).willReturn(null);
    assertThat(defaultCustomerAddressReplicationUtilityService.findDefaultAddress(customerModel, addressModel)).isEqualTo(addressModel);

  }

  @Test
  public void findDefaultAddressAfterSettingDefaultFlagToTrue() {

    given(defaultAddressModel.getVisibleInAddressBook()).willReturn(true);
    given(addressModel.getVisibleInAddressBook()).willReturn(true);
    given(customerAccountService.getAddressBookEntries(any())).willReturn(Arrays.asList(addressModel, defaultAddressModel));
    given(customerAccountService.getDefaultAddress(any())).willReturn(defaultAddressModel);
    assertThat(defaultCustomerAddressReplicationUtilityService.findDefaultAddress(customerModel, defaultAddressModel)).isEqualTo(defaultAddressModel);

  }

  @Test
  public void findDefaultAddressAfterSettingDefaultFlagToFalse() {

    given(defaultAddressModel.getVisibleInAddressBook()).willReturn(true);
    given(addressModel.getVisibleInAddressBook()).willReturn(true);
    given(customerAccountService.getAddressBookEntries(any())).willReturn(Arrays.asList(addressModel, defaultAddressModel));
    given(customerAccountService.getDefaultAddress(any())).willReturn(null);
    assertThat(defaultCustomerAddressReplicationUtilityService.findDefaultAddress(customerModel, defaultAddressModel)).isEqualTo(addressModel);

  }


}
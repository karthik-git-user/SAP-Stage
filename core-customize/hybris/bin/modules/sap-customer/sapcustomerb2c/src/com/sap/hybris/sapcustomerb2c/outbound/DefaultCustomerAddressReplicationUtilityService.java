/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.internal.model.impl.ModelValueHistory;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Default implementation of {@link CustomerAddressReplicationUtilityService}
 */
public class DefaultCustomerAddressReplicationUtilityService implements CustomerAddressReplicationUtilityService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomerAddressReplicationUtilityService.class);

  private CustomerAccountService customerAccountService;
  private UserService userService;

  @Override
  public boolean isCustomerReplicationRequired(CustomerModel customerModel, Set<String> monitoredAttributes, InterceptorContext context) {

    final boolean needsUpdate = isModelChanged(customerModel, context, monitoredAttributes);

    if (needsUpdate) {
      LOGGER.debug("The customer [{}] will be replicated!", customerModel.getCustomerID());
    } else {
      LOGGER.debug("The customer [{}] will not be replicated!", customerModel.getCustomerID());
    }

    return needsUpdate;

  }

  @Override
  public boolean isAddressReplicationRequired(AddressModel addressModel, Set<String> monitoredAttributes, InterceptorContext context) {

    CustomerModel customerModel = (CustomerModel) addressModel.getOwner();
    AddressModel defaultAddress = determineDefaultAddress(customerModel, addressModel);

    final boolean needsUpdate = isModelChanged(defaultAddress, context, monitoredAttributes);

    if (needsUpdate) {
      LOGGER.debug("The address [{},{}] needs to be replicated!", addressModel.getPk(), addressModel.getLine1());
    } else {
      LOGGER.debug("The address [{},{}] will not be replicated!", addressModel.getPk(), addressModel.getLine1());
    }

    return needsUpdate;

  }

  @Override
  public AddressModel findDefaultAddress(CustomerModel customerModel, AddressModel addressModel) {

    AddressModel defaultShipmentAddress = determineDefaultAddress(customerModel, addressModel);

    if (defaultShipmentAddress != null) {
      LOGGER.debug("The default address [{},{}] will be replicated!", defaultShipmentAddress.getPk(), defaultShipmentAddress.getLine1());
    }

    return defaultShipmentAddress;

  }

  protected AddressModel determineDefaultAddress(CustomerModel customerModel, AddressModel addressModel) {

    final UserModel currentUser = getUserService().getCurrentUser();

    AddressModel defaultShipmentAddress = readDefaultAddress(customerModel);

    if (!Objects.equals(currentUser, customerModel) && Objects.equals(addressModel, defaultShipmentAddress)) {
      defaultShipmentAddress = addressModel;
    }

    return Objects.nonNull(defaultShipmentAddress) ? defaultShipmentAddress : addressModel;

  }

  protected AddressModel readDefaultAddress(CustomerModel customerModel) {

    AddressModel defaultShipmentAddress = null;

    final AddressModel defaultAddress = getCustomerAccountService().getDefaultAddress(customerModel);

    if (Objects.nonNull(defaultAddress)) {

      defaultShipmentAddress = defaultAddress;

    } else if (Objects.nonNull(customerModel) && Objects.nonNull(customerModel.getAddresses())) {

      final List<AddressModel> addresses = getCustomerAccountService().getAddressBookEntries(customerModel);

      if (CollectionUtils.isNotEmpty(addresses)) {
        defaultShipmentAddress = addresses.get(0);
      }

    }

    return defaultShipmentAddress;

  }

  protected boolean isModelChanged(final ItemModel itemModel, final InterceptorContext ctx, final Set<String> monitoredAttributes) {

    final Set<String> attributeSet = ctx.getDirtyAttributes(itemModel).keySet();

    return ctx.isNew(itemModel) ||
            attributeSet.stream()
                    .filter(itemAttribute -> monitoredAttributes.contains(itemAttribute))
                    .filter(attribute -> isModelAttributeChanged(itemModel, attribute)).count() > 0;

  }

  protected boolean isModelAttributeChanged(ItemModel itemModel, String itemProperty) {

    final ItemModelContextImpl context = (ItemModelContextImpl) itemModel.getItemModelContext();
    final ModelValueHistory history = context.getValueHistory();
    final boolean isChanged = !Objects.equals(itemModel.getProperty(itemProperty), history.getOriginalValue(itemProperty));

    if (isChanged) {
      LOGGER.debug("The [{}] attribute [{}], has been updated from [{}] to [{}]!", itemModel.getItemtype(), itemProperty, history.getOriginalValue(itemProperty), itemModel.getProperty(itemProperty));
    }
    return isChanged;

  }

  protected CustomerAccountService getCustomerAccountService() {
    return customerAccountService;
  }

  public void setCustomerAccountService(CustomerAccountService customerAccountService) {
    this.customerAccountService = customerAccountService;
  }

  protected UserService getUserService() {
    return userService;
  }

  public void setUserService(UserService userService) {
    this.userService = userService;
  }

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchange.service.impl;

import com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerConversionService;
import de.hybris.platform.sap.sapcpicustomerexchange.service.SapCpiCustomerDestinationService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.ADDRESSUSAGE_DE;
import static com.sap.hybris.sapcustomerb2c.constants.Sapcustomerb2cConstants.OBJTYPE_KNA1;

/**
 * SapCpiCustomerDefaultConversionService
 */
public class SapCpiCustomerDefaultConversionService implements SapCpiCustomerConversionService {

  private ModelService modelService;
  private CustomerNameStrategy customerNameStrategy;
  private SapCpiCustomerDestinationService sapCpiCustomerDestinationService;

  @Override
  public SAPCpiOutboundCustomerModel convertCustomerToSapCpiCustomer(CustomerModel customerModel, AddressModel addressModel, String baseStoreUid, String sessionLanguage) {

    final SAPCpiOutboundCustomerModel sapCpiOutboundCustomer = getModelService().create(SAPCpiOutboundCustomerModel.class);
    final SAPLogicalSystemModel logicalSystem = getSapCpiCustomerDestinationService().readSapLogicalSystem();

    // Configuration
    final SAPCpiOutboundConfigModel config = getModelService().create(SAPCpiOutboundConfigModel.class);

    config.setReceiverName(logicalSystem.getSapLogicalSystemName());
    config.setReceiverPort(logicalSystem.getSapLogicalSystemName());
    config.setSenderName(logicalSystem.getSenderName());
    config.setSenderPort(logicalSystem.getSenderPort());

    config.setUrl(getSapCpiCustomerDestinationService().determineUrlDestination(logicalSystem));
    config.setClient(getSapCpiCustomerDestinationService().extractSapClient(logicalSystem));

    config.setUsername(logicalSystem.getSapHTTPDestination().getUserid());
    sapCpiOutboundCustomer.setSapCpiConfig(config);

    // Customer
    final String[] names = getCustomerNameStrategy().splitName(customerModel.getName());
    sapCpiOutboundCustomer.setUid(customerModel.getUid());
    sapCpiOutboundCustomer.setCustomerId(customerModel.getCustomerID());
    sapCpiOutboundCustomer.setContactId(customerModel.getSapContactID());
    sapCpiOutboundCustomer.setFirstName(names[0]);
    sapCpiOutboundCustomer.setLastName(names[1]);
    sapCpiOutboundCustomer.setSessionLanguage(sessionLanguage);

    final String title = customerModel.getTitle() != null ? customerModel.getTitle().getName() : null;
    sapCpiOutboundCustomer.setTitle(title);
    
    sapCpiOutboundCustomer.setBaseStore(baseStoreUid);
    sapCpiOutboundCustomer.setObjType(OBJTYPE_KNA1);
    sapCpiOutboundCustomer.setAddressUsage(ADDRESSUSAGE_DE);

    if (addressModel == null) {
      sapCpiOutboundCustomer.setCountry(Sapcustomerb2cConstants.COUNTRY_DE);
      return sapCpiOutboundCustomer;
    }

    // Address
    final String countryIsoCode = addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : null;
    sapCpiOutboundCustomer.setCountry(countryIsoCode);
    sapCpiOutboundCustomer.setStreet(addressModel.getStreetname());
    sapCpiOutboundCustomer.setPhone(addressModel.getPhone1());
    sapCpiOutboundCustomer.setFax(addressModel.getFax());
    sapCpiOutboundCustomer.setTown(addressModel.getTown());
    sapCpiOutboundCustomer.setPostalCode(addressModel.getPostalcode());
    sapCpiOutboundCustomer.setStreetNumber(addressModel.getStreetnumber());

    final String regionIsoCode = addressModel.getRegion() != null ? addressModel.getRegion().getIsocodeShort() : null;
    sapCpiOutboundCustomer.setRegion(regionIsoCode);

    return sapCpiOutboundCustomer;

  }

  protected ModelService getModelService() {
    return modelService;
  }

  public void setModelService(ModelService modelService) {
    this.modelService = modelService;
  }

  protected CustomerNameStrategy getCustomerNameStrategy() {
    return customerNameStrategy;
  }

  public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
    this.customerNameStrategy = customerNameStrategy;
  }

  protected SapCpiCustomerDestinationService getSapCpiCustomerDestinationService() {
    return sapCpiCustomerDestinationService;
  }

  public void setSapCpiCustomerDestinationService(SapCpiCustomerDestinationService sapCpiCustomerDestinationService) {
    this.sapCpiCustomerDestinationService = sapCpiCustomerDestinationService;
  }

}

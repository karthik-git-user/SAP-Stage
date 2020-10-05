/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.impl;

import com.sap.hybris.sapcustomerb2c.outbound.CustomerAddressReplicationUtilityService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BContactModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.SapCpiB2BCustomerConversionService;
import de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.SapCpiB2BCustomerDestinationService;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.sap.hybris.sapcustomerb2b.constants.Sapcustomerb2bConstants.*;

/**
 * Class to convert Hybris B2B Customer to SCPI B2B Customer.
 */
public class SapCpiB2BCustomerDefaultConversionService implements SapCpiB2BCustomerConversionService {

  private static final String OBJECT_TYPE = "KNVK";

  private CustomerNameStrategy customerNameStrategy;
  private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
  private BaseStoreService baseStoreService;
  private SapCpiB2BCustomerDestinationService sapCpiB2BCustomerDestinationService;
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;


  @Override
  public SAPCpiOutboundB2BCustomerModel convertB2BCustomerToSapCpiBb2BCustomer(B2BCustomerModel b2bCustomerModel, String sessionLanguage) {

    final SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer = new SAPCpiOutboundB2BCustomerModel();

    // Hybris B2B Unit Maps To SAP B2B Customer
    final B2BUnitModel rootB2BUnit = getB2bUnitService().getRootUnit(b2bCustomerModel.getDefaultB2BUnit());
    sapCpiOutboundB2BCustomer.setUid(rootB2BUnit.getUid());
    sapCpiOutboundB2BCustomer.setAddressUUID(readSapAddressUUID(rootB2BUnit));
    mapOutboundDestination(sapCpiOutboundB2BCustomer);

    // Hybris B2B Customers Maps To SAP B2B Contacts
    final Set<SAPCpiOutboundB2BContactModel> sapCpiOutboundB2BContacts = new HashSet<>();
    final Set<B2BCustomerModel> b2bCustomers = new HashSet<>();
    b2bCustomers.addAll(getB2bUnitService().getB2BCustomers(rootB2BUnit));

    if(!b2bCustomers.contains(b2bCustomerModel)){
      b2bCustomers.add(b2bCustomerModel);
    }

    getB2bUnitService().getB2BUnits(rootB2BUnit).forEach(subB2BUnit -> b2bCustomers.addAll(getB2bUnitService().getB2BCustomers(subB2BUnit)));
    b2bCustomers.forEach(b2bCustomer -> sapCpiOutboundB2BContacts.add(convertB2BContactToSapCpiBb2BContact(rootB2BUnit, b2bCustomer, sessionLanguage)));
    sapCpiOutboundB2BCustomer.setSapCpiOutboundB2BContacts(sapCpiOutboundB2BContacts);

    return sapCpiOutboundB2BCustomer;

  }

  protected String readSapAddressUUID(B2BUnitModel rootB2BUnit) {

    return rootB2BUnit.getAddresses() != null ? rootB2BUnit.getAddresses()
            .stream()
            .findFirst()
            .map(AddressModel::getSapAddressUUID)
            .orElse(null) : null;

  }

  protected SAPCpiOutboundB2BContactModel convertB2BContactToSapCpiBb2BContact(B2BUnitModel b2bUnitModel, B2BCustomerModel b2bCustomerModel, String sessionLanguage) {

    final SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContact = new SAPCpiOutboundB2BContactModel();
    sapCpiOutboundB2BContact.setUid(b2bUnitModel.getUid());

    mapB2BContactInfo(b2bCustomerModel, sessionLanguage, sapCpiOutboundB2BContact);

    return sapCpiOutboundB2BContact;

  }

  protected void mapOutboundDestination(SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer) {

    final SAPCpiOutboundConfigModel sapCpiOutboundConfig = new SAPCpiOutboundConfigModel();
    final SAPLogicalSystemModel logicalSystem = getSapCpiB2BCustomerDestinationService().readSapLogicalSystem();

    sapCpiOutboundConfig.setSenderName(logicalSystem.getSenderName());
    sapCpiOutboundConfig.setSenderPort(logicalSystem.getSenderPort());
    sapCpiOutboundConfig.setReceiverName(logicalSystem.getSapLogicalSystemName());
    sapCpiOutboundConfig.setReceiverPort(logicalSystem.getSapLogicalSystemName());
    sapCpiOutboundConfig.setUsername(logicalSystem.getSapHTTPDestination().getUserid());

    sapCpiOutboundConfig.setUrl(getSapCpiB2BCustomerDestinationService().determineUrlDestination(logicalSystem));
    sapCpiOutboundConfig.setClient(getSapCpiB2BCustomerDestinationService().extractSapClient(logicalSystem));

    sapCpiOutboundB2BCustomer.setSapCpiConfig(sapCpiOutboundConfig);

  }

  protected void mapB2BContactInfo(B2BCustomerModel b2bCustomer, String sessionLanguage, SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContact) {

    final int CUSTOMER_NAME_LENGTH = 2;
    final int CUSTOMER_LNAME = 1;

    if (Objects.isNull(b2bCustomer.getDefaultB2BUnit())) {
      return;
    }

    sapCpiOutboundB2BContact.setEmail(b2bCustomer.getEmail());
    sapCpiOutboundB2BContact.setCustomerId(readCustomerID(b2bCustomer));
    sapCpiOutboundB2BContact.setSessionLanguage(sessionLanguage);
    sapCpiOutboundB2BContact.setObjType(OBJECT_TYPE);

    final String[] names = getCustomerNameStrategy().splitName(b2bCustomer.getName());
    sapCpiOutboundB2BContact.setFirstName(names[0]);
    sapCpiOutboundB2BContact.setLastName((names.length == CUSTOMER_NAME_LENGTH) ? names[CUSTOMER_LNAME] : StringUtils.EMPTY);

    sapCpiOutboundB2BContact.setDefaultB2BUnit(b2bCustomer.getDefaultB2BUnit().getUid().split("_")[0]);
    sapCpiOutboundB2BContact.setTitle(b2bCustomer.getTitle() != null ? b2bCustomer.getTitle().getCode() : null);
    sapCpiOutboundB2BContact.setGroups(mapB2BCustomerFunction(b2bCustomer.getGroups()));

    mapB2BContactAddress(b2bCustomer, sapCpiOutboundB2BContact);

  }

  protected String readCustomerID(B2BCustomerModel b2bCustomer) {
    return StringUtils.isNotBlank(b2bCustomer.getSapBusinessPartnerID()) ? b2bCustomer.getSapBusinessPartnerID() : b2bCustomer.getCustomerID();
  }

  protected void mapB2BContactAddress(B2BCustomerModel b2bCustomer, SAPCpiOutboundB2BContactModel sapCpiOutboundB2BContact) {

    AddressModel defaultShipmentAddress = b2bCustomer.getDefaultShipmentAddress();

    if(defaultShipmentAddress == null){
      defaultShipmentAddress = getCustomerAddressReplicationUtilityService().findDefaultAddress(b2bCustomer, null);
    }

    if (defaultShipmentAddress != null) {
      sapCpiOutboundB2BContact.setCountry(defaultShipmentAddress.getCountry() != null ? defaultShipmentAddress.getCountry().getIsocode() : StringUtils.EMPTY);
      sapCpiOutboundB2BContact.setPhone(defaultShipmentAddress.getPhone1());
    } else {
      sapCpiOutboundB2BContact.setCountry(StringUtils.EMPTY);
      sapCpiOutboundB2BContact.setPhone(StringUtils.EMPTY);
    }

  }


  private String mapB2BCustomerFunction(final Set<PrincipalGroupModel> groups) {

    final List<String> groupUIDs = groups.stream().map(PrincipalGroupModel::getUid).collect(Collectors.toList());

    if (groupUIDs.containsAll(Arrays.asList(B2BADMINGROUP, B2BCUSTOMERGROUP))) {
      return HEADOFPURCHASING;
    } else if (groupUIDs.contains(B2BADMINGROUP)) {
      return EXECUTIVEBOARD;
    } else {
      return groupUIDs.contains(B2BCUSTOMERGROUP) ? BUYER :
              StringUtils.EMPTY;
    }

  }


  protected CustomerNameStrategy getCustomerNameStrategy() {
    return customerNameStrategy;
  }

  public void setCustomerNameStrategy(CustomerNameStrategy customerNameStrategy) {
    this.customerNameStrategy = customerNameStrategy;
  }

  protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService() {
    return b2bUnitService;
  }

  public void setB2bUnitService(B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService) {
    this.b2bUnitService = b2bUnitService;
  }

  protected BaseStoreService getBaseStoreService() {
    return baseStoreService;
  }

  public void setBaseStoreService(BaseStoreService baseStoreService) {
    this.baseStoreService = baseStoreService;
  }

  protected SapCpiB2BCustomerDestinationService getSapCpiB2BCustomerDestinationService() {
    return sapCpiB2BCustomerDestinationService;
  }

  public void setSapCpiB2BCustomerDestinationService(SapCpiB2BCustomerDestinationService sapCpiB2BCustomerDestinationService) {
    this.sapCpiB2BCustomerDestinationService = sapCpiB2BCustomerDestinationService;
  }

  protected CustomerAddressReplicationUtilityService getCustomerAddressReplicationUtilityService() {
    return customerAddressReplicationUtilityService;
  }

  public void setCustomerAddressReplicationUtilityService(CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService) {
    this.customerAddressReplicationUtilityService = customerAddressReplicationUtilityService;
  }
}
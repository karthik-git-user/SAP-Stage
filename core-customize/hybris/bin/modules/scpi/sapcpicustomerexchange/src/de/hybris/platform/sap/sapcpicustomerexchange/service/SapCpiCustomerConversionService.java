/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchange.service;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundCustomerModel;

/**
 * SapCpiCustomerConversionService
 */
public interface SapCpiCustomerConversionService {

  /**
   * convertCustomerToSapCpiCustomer
   * @param customerModel CustomerModel
   * @param addressModel AddressModel
   * @param baseStoreUid String
   * @param sessionLanguage String
   * @return SAPCpiOutboundCustomerModel
   */
  SAPCpiOutboundCustomerModel convertCustomerToSapCpiCustomer(CustomerModel customerModel, AddressModel addressModel,
                                                              String baseStoreUid, String sessionLanguage);

}

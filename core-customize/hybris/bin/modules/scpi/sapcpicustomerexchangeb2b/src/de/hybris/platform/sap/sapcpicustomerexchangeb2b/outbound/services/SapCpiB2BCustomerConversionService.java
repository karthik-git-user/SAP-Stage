/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;

/**
 * Convert Hybris B2B Customer to SCPI B2B Customer.
 */
public interface SapCpiB2BCustomerConversionService {

  /**
   * Convert Hybris B2B Customer to SCPI B2B Customers.
   *
   * @param b2bCustomerModel B2BCustomerModel
   * @param sessionLanguage String
   * @return SAPCpiOutboundB2BCustomerModel
   */
  SAPCpiOutboundB2BCustomerModel convertB2BCustomerToSapCpiBb2BCustomer(B2BCustomerModel b2bCustomerModel, String sessionLanguage);

}

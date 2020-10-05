/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services;

import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;

/**
 * Handles IDoc, SOAP, and MDM endpoints for B2B customer outbound replication
 */
public interface SapCpiB2BCustomerDestinationService {

  /**
   * Read SAP logical system from back-office configuration
   * @return SAPLogicalSystemModel
   */
  SAPLogicalSystemModel readSapLogicalSystem();
  /**
   * Determine the URL destination based on the target system supported destination IDoc, SOAP, or MDM
   * @param sapLogicalSystem SAP Backend System
   * @return URL destination
   */
  String determineUrlDestination(SAPLogicalSystemModel sapLogicalSystem);

  /**
   * Return sap-client for IDoc destination
   * @param sapLogicalSystem  SAP Backend System
   * @return sap-client
   */
  String extractSapClient(SAPLogicalSystemModel sapLogicalSystem);

}

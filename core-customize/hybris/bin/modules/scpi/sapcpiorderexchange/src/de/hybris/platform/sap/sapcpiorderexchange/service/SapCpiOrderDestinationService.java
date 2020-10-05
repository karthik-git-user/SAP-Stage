/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service;

import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Handles IDoc endpoint for outbound order replication
 */
public interface SapCpiOrderDestinationService {

  /**
   * Read the default SAP logical system from back-office configuration
   * @return SAPLogicalSystemModel
   */
  SAPLogicalSystemModel readSapLogicalSystem();

  /**
   * Read SAP logical system from back-office configuration given base store and warehouse
   *
   * @param baseStoreModel  Store associated with order
   * @param warehouse Warehouse code
   * @return SAPLogicalSystemModel
   */
  SAPLogicalSystemModel readSapLogicalSystem(BaseStoreModel baseStoreModel, String warehouse);

  /**
   * Read SAP logical system from back-office configuration given the logical system name
   * @param sapLogicalSystemName  SAP Backend System name
   * @return SAPLogicalSystemModel
   */

  SAPLogicalSystemModel readSapLogicalSystem(final String sapLogicalSystemName);

    /**
     * Return sap-client for IDoc destination
     * @param sapLogicalSystem  SAP Backend System
     * @return sap-client
     */
  String extractSapClient(SAPLogicalSystemModel sapLogicalSystem);

  /**
   * Determine the URL destination based on the target system supported destination IDoc, SOAP, or MDM
   * @param sapLogicalSystem  SAP Backend System
   * @return URL destination
   */
  String determineUrlDestination(SAPLogicalSystemModel sapLogicalSystem);


}

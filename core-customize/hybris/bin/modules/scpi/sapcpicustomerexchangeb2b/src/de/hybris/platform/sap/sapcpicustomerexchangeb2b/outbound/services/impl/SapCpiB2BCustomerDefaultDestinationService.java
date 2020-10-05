/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.impl;

import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.SapCpiB2BCustomerDestinationService;
import de.hybris.platform.sap.sapmodel.enums.SapSystemType;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

/**
 *  Determine which SAP Logical Backend to send B2B Customer data to.
 */
public class SapCpiB2BCustomerDefaultDestinationService implements SapCpiB2BCustomerDestinationService {

  private static final String MDM_LOGICAL_SYSTEM = "MDM";
  private static final String SAP_CLIENT = "sap-client=";
  private static final String IDOC = "/sap/bc/srt/idoc";
  private static final String SOAP = "/sap/bc/srt/scs/sap";

  private boolean mdmEnabled;
  private SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO;

  @Override
  public SAPLogicalSystemModel readSapLogicalSystem() {

    final Set<SAPLogicalSystemModel> logicalSystems = getSapCoreSAPGlobalConfigurationDAO().getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig();
    Objects.requireNonNull(logicalSystems, "No logical system is maintained in back-office for B2B customer replication to SCPI!");

    return isMdmEnabled()
            ? logicalSystems.stream().filter(ls -> ls.getSapLogicalSystemName().contentEquals(MDM_LOGICAL_SYSTEM))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No logical system named MDM is maintained in back-office for B2B customer replication to MDM through SCPI!"))
            : logicalSystems.stream().filter(ls -> ls.isDefaultLogicalSystem())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No logical system is maintained in back-office for B2B customer replication to SCPI!"));

  }

  @Override
  public String determineUrlDestination(SAPLogicalSystemModel sapLogicalSystem) {

    final String targetURL = sapLogicalSystem.getSapHTTPDestination().getTargetURL();

    // SAP S/4HANA
    if (SapSystemType.SAP_S4HANA.equals(sapLogicalSystem.getSapSystemType()) &&
            targetURL.contains(IDOC) &&
            !sapLogicalSystem.getSapLogicalSystemName().contentEquals(MDM_LOGICAL_SYSTEM)) {

      return targetURL.replace(IDOC, SOAP);

    } else {

      // SAP ERP or MDM
      return targetURL;

    }

  }

  @Override
  public String extractSapClient(SAPLogicalSystemModel sapLogicalSystem) {

    final int TARGET_URL_PIECES = 2;
    final int SYSTEM_PREFIX = 3;

    if (isMdmEnabled() || SapSystemType.SAP_S4HANA.equals(sapLogicalSystem.getSapSystemType())) {
      return StringUtils.EMPTY;
    }

    String targetURL = determineUrlDestination(sapLogicalSystem);
    final String[] sapClientSplit = targetURL.split(SAP_CLIENT);

    if (sapClientSplit.length == TARGET_URL_PIECES && StringUtils.isNotBlank(sapClientSplit[1]) && sapClientSplit[1].strip().matches("\\d\\d\\d")) {
      return sapClientSplit[1].strip().substring(0, SYSTEM_PREFIX);
    } else {
      throw new IllegalArgumentException(String.format("The HTTP destination [%s] does not maintain sap-client properly for B2B customer IDoc destination!", targetURL));
    }

  }

  protected boolean isMdmEnabled() {
    return mdmEnabled;
  }

  public void setMdmEnabled(boolean mdmEnabled) {
    this.mdmEnabled = mdmEnabled;
  }

  protected SAPGlobalConfigurationDAO getSapCoreSAPGlobalConfigurationDAO() {
    return sapCoreSAPGlobalConfigurationDAO;
  }

  public void setSapCoreSAPGlobalConfigurationDAO(SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO) {
    this.sapCoreSAPGlobalConfigurationDAO = sapCoreSAPGlobalConfigurationDAO;
  }

}

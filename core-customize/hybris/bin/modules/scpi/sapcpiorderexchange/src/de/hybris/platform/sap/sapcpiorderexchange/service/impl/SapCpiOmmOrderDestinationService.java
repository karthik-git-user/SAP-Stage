/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service.impl;

import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.sapcpiorderexchange.exceptions.SapCpiOmmOrderConversionServiceException;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderDestinationService;
import de.hybris.platform.sap.sapmodel.enums.SapSystemType;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

/**
 * Determine which SAP Logical Backend to send OMM Order data to.
 */
public class SapCpiOmmOrderDestinationService implements SapCpiOrderDestinationService {

  private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmmOrderDestinationService.class);
  private final static String SAP_CLIENT = "sap-client=";
  private static final String IDOC = "/sap/bc/srt/idoc";
  private static final String SOAP = "/sap/bc/srt/scs/sap";

  private SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO;
  private SapPlantLogSysOrgService sapPlantLogSysOrgService;
  private FlexibleSearchService flexibleSearchService;


  @Override
  public SAPLogicalSystemModel readSapLogicalSystem() {

    final Set<SAPLogicalSystemModel> logicalSystems = getSapCoreSAPGlobalConfigurationDAO().getSAPGlobalConfiguration().getSapLogicalSystemGlobalConfig();
    Objects.requireNonNull(logicalSystems, "No logical system is maintained in back-office for order replication to SCPI!");

    return logicalSystems.stream()
            .filter(ls -> ls.isDefaultLogicalSystem())
            .findFirst()
            .orElseThrow(() -> new SapCpiOmmOrderConversionServiceException("No logical system is maintained in back-office for OMM order replication to SCPI!"));

  }

  @Override
  public SAPLogicalSystemModel readSapLogicalSystem(String sapLogicalSystemName) {

    final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("SELECT {o:pk} FROM {SAPLogicalSystem AS o} WHERE { o.sapLogicalSystemName} = ?sapLogicalSystemName");

    flexibleSearchQuery.addQueryParameter("sapLogicalSystemName", sapLogicalSystemName);

    final SAPLogicalSystemModel sapLogicalSystem = getFlexibleSearchService().searchUnique(flexibleSearchQuery);

    if (sapLogicalSystem == null) {
      throw new SapCpiOmmOrderConversionServiceException("No logical system is maintained in back-office for OMS order cancellation replication to SCPI!");
    }

    return sapLogicalSystem;

  }


  @Override
  public SAPLogicalSystemModel readSapLogicalSystem(BaseStoreModel baseStoreModel, String plantCode) {

    SAPLogicalSystemModel sapLogicalSystemForPlant = getSapPlantLogSysOrgService().getSapLogicalSystemForPlant(baseStoreModel, plantCode);
    Objects.requireNonNull(sapLogicalSystemForPlant.getSapHTTPDestination(), "No Logical system is maintained in back-office for OMS order replication to SCPI!");

    return sapLogicalSystemForPlant;

  }

  @Override
  public String determineUrlDestination(SAPLogicalSystemModel sapLogicalSystem) {

    final String targetURL = sapLogicalSystem.getSapHTTPDestination().getTargetURL();

    // SAP ERP
    if (SapSystemType.SAP_ERP.equals(sapLogicalSystem.getSapSystemType()) && targetURL.contains(SOAP)) {

      final String idocURL = targetURL.replace(SOAP, IDOC);
      LOG.info("Convert the URL destination from SOAP [{}] to IDoc [{}]!", targetURL, idocURL);
      return targetURL.replace(SOAP, IDOC);

    } else {

      // SAP S/4HANA  or SAP S/4HANA CE
      return targetURL;

    }

  }

  @Override
  public String extractSapClient(SAPLogicalSystemModel sapLogicalSystem) {

    final int TARGET_URL_PIECES = 2;
    final int SYSTEM_PREFIX = 3;

    final String targetURL = determineUrlDestination(sapLogicalSystem);
    final String[] sapClientSplit = targetURL.split(SAP_CLIENT);

    if (sapClientSplit.length == TARGET_URL_PIECES && StringUtils.isNotBlank(sapClientSplit[1]) && sapClientSplit[1].strip().matches("\\d\\d\\d")) {
      return sapClientSplit[1].strip().substring(0, SYSTEM_PREFIX);
    } else {
      throw new SapCpiOmmOrderConversionServiceException(String.format("The HTTP destination [%s] does not maintain sap-client properly for order IDoc destination!", targetURL));
    }

  }


  protected SAPGlobalConfigurationDAO getSapCoreSAPGlobalConfigurationDAO() {
    return sapCoreSAPGlobalConfigurationDAO;
  }

  public void setSapCoreSAPGlobalConfigurationDAO(SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO) {
    this.sapCoreSAPGlobalConfigurationDAO = sapCoreSAPGlobalConfigurationDAO;
  }

  protected SapPlantLogSysOrgService getSapPlantLogSysOrgService() {
    return sapPlantLogSysOrgService;
  }

  public void setSapPlantLogSysOrgService(SapPlantLogSysOrgService sapPlantLogSysOrgService) {
    this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
  }

  public FlexibleSearchService getFlexibleSearchService() {
    return flexibleSearchService;
  }

  public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
    this.flexibleSearchService = flexibleSearchService;
  }

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchange.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapmodel.enums.SapSystemType;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiCustomerDefaultDestinationServiceTest {

  private static final String IDOC_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=910";
  private static final String SOAP_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/scs/sap?sap-client=910";
  private static final String WRONG_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/scs/sap?";
  private static final String SAP_CLIENT = "910";
  private static final String NOT_MDM = "LOGICAL_SYSTEM_NAME";
  private static final String MDM = "MDM";

  @InjectMocks
  private SapCpiCustomerDefaultDestinationService sapCpiCustomerDefaultDestinationService;
  @Mock
  private SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO;

  private SAPLogicalSystemModel defaultLogicalSystem;

  @Before
  public void setUp() {

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL(IDOC_URL);

    defaultLogicalSystem = new SAPLogicalSystemModel();
    defaultLogicalSystem.setSapLogicalSystemName("QE6CLNT910");
    defaultLogicalSystem.setSenderName("HBRGTSM07");
    defaultLogicalSystem.setSenderPort("HBRGTSM07");
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);

    Set<SAPLogicalSystemModel> sapLogicalSystemModels = new HashSet<>();
    sapLogicalSystemModels.add(defaultLogicalSystem);

    SAPGlobalConfigurationModel sapGlobalConfiguration = new SAPGlobalConfigurationModel();
    sapGlobalConfiguration.setSapLogicalSystemGlobalConfig(sapLogicalSystemModels);

    when(sapCoreSAPGlobalConfigurationDAO.getSAPGlobalConfiguration()).thenReturn(sapGlobalConfiguration);

  }

  @Test
  public void readSapLogicalSystem() {

    defaultLogicalSystem.setDefaultLogicalSystem(true);
    sapCpiCustomerDefaultDestinationService.setMdmEnabled(false);
    assertNotNull(sapCpiCustomerDefaultDestinationService.readSapLogicalSystem());

  }

  @Test(expected = IllegalArgumentException.class)
  public void readSapLogicalSystemWithNoDefaultLogicalSystem() {

    defaultLogicalSystem.setDefaultLogicalSystem(false);
    sapCpiCustomerDefaultDestinationService.setMdmEnabled(false);
    assertNotNull(sapCpiCustomerDefaultDestinationService.readSapLogicalSystem());

  }

  @Test
  public void readMdmSapLogicalSystemWithTheRightName() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(true);
    defaultLogicalSystem.setSapLogicalSystemName(MDM);
    assertNotNull(sapCpiCustomerDefaultDestinationService.readSapLogicalSystem());

  }

  @Test(expected = IllegalArgumentException.class)
  public void readMdmSapLogicalSystemWithTheWrongName() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(true);
    defaultLogicalSystem.setSapLogicalSystemName(NOT_MDM);
    assertNotNull(sapCpiCustomerDefaultDestinationService.readSapLogicalSystem());

  }

  @Test
  public void determineUrlDestinationForERP() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(false);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_ERP);
    assertEquals(IDOC_URL, sapCpiCustomerDefaultDestinationService.determineUrlDestination(defaultLogicalSystem));

  }

  @Test
  public void determineUrlDestinationForS4HANA() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(false);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_S4HANA);
    assertEquals(SOAP_URL, sapCpiCustomerDefaultDestinationService.determineUrlDestination(defaultLogicalSystem));

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(true);
    defaultLogicalSystem.setSapLogicalSystemName(NOT_MDM);
    assertEquals(SOAP_URL, sapCpiCustomerDefaultDestinationService.determineUrlDestination(defaultLogicalSystem));

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(true);
    defaultLogicalSystem.setSapLogicalSystemName(MDM);
    assertNotEquals(SOAP_URL, sapCpiCustomerDefaultDestinationService.determineUrlDestination(defaultLogicalSystem));

  }

  @Test
  public void determineUrlDestinationForMDM() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(true);
    defaultLogicalSystem.setSapLogicalSystemName(MDM);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_S4HANA);
    assertEquals(IDOC_URL, sapCpiCustomerDefaultDestinationService.determineUrlDestination(defaultLogicalSystem));

  }


  @Test
  public void extractSapClientForERP() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(false);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_ERP);
    assertEquals(SAP_CLIENT, sapCpiCustomerDefaultDestinationService.extractSapClient(defaultLogicalSystem));

  }

  @Test(expected = IllegalArgumentException.class)
  public void extractSapClientForERPWithWrongURL() {

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL(WRONG_URL);
    defaultLogicalSystem = new SAPLogicalSystemModel();
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(false);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_ERP);

    sapCpiCustomerDefaultDestinationService.extractSapClient(defaultLogicalSystem);

  }

  @Test
  public void extractSapClientForS4HANA() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(false);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_S4HANA);
    assertEquals(StringUtils.EMPTY, sapCpiCustomerDefaultDestinationService.extractSapClient(defaultLogicalSystem));

  }

  @Test
  public void extractSapClientForMDM() {

    sapCpiCustomerDefaultDestinationService.setMdmEnabled(true);
    defaultLogicalSystem.setSapSystemType(null);
    assertEquals(StringUtils.EMPTY, sapCpiCustomerDefaultDestinationService.extractSapClient(defaultLogicalSystem));

  }

}
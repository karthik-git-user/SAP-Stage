/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchange.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpiorderexchange.exceptions.SapCpiOmmOrderConversionServiceException;
import de.hybris.platform.sap.sapmodel.enums.SapSystemType;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiOmmOrderDestinationServiceTest {

  private static final String IDOC_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=910";
  private static final String SOAP_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/scs/sap?sap-client=910";
  private static final String WRONG_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/scs/sap?";
  private static final String SAP_CLIENT = "910";

  @InjectMocks
  private SapCpiOmmOrderDestinationService sapCpiOmmOrderDestinationService;
  @Mock
  private SAPGlobalConfigurationDAO sapCoreSAPGlobalConfigurationDAO;
  @Mock
  private SapPlantLogSysOrgService sapPlantLogSysOrgService;
  @Mock
  private FlexibleSearchService flexibleSearchService;

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
    when(flexibleSearchService.searchUnique(any())).thenReturn(defaultLogicalSystem);
    when(sapPlantLogSysOrgService.getSapLogicalSystemForPlant(any(), any())).thenReturn(defaultLogicalSystem);

  }

  @Test
  public void readSapLogicalSystemNotNull() {

    defaultLogicalSystem.setDefaultLogicalSystem(true);
    assertNotNull(sapCpiOmmOrderDestinationService.readSapLogicalSystem());

  }

  @Test(expected = SapCpiOmmOrderConversionServiceException.class)
  public void readSapLogicalSystemIsNull() {

    defaultLogicalSystem.setDefaultLogicalSystem(false);
    sapCpiOmmOrderDestinationService.readSapLogicalSystem();

  }

  @Test
  public void readSapLogicalSystemWithGivenName() {

    assertNotNull(sapCpiOmmOrderDestinationService.readSapLogicalSystem("QE6CLNT910"));

  }

  @Test
  public void readSapLogicalSystemGivenBaseStoreAndWarehouse() {

    assertNotNull(sapCpiOmmOrderDestinationService.readSapLogicalSystem(new BaseStoreModel(), "1000"));

  }

  @Test
  public void extractSapClient() {

    assertEquals(SAP_CLIENT, sapCpiOmmOrderDestinationService.extractSapClient(defaultLogicalSystem));

  }

  @Test(expected = SapCpiOmmOrderConversionServiceException.class)
  public void extractSapClientWithWrongURL() {

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL(WRONG_URL);
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);
    sapCpiOmmOrderDestinationService.extractSapClient(defaultLogicalSystem);

  }
  // IDoc -> ERP : Do nothing
  @Test
  public void determineUrlDestinationWithIDocUrlForErpSystem() {

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL(IDOC_URL);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_ERP);
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);
    assertEquals(IDOC_URL, sapCpiOmmOrderDestinationService.determineUrlDestination(defaultLogicalSystem));

  }

  // SOAP -> ERP : Convert SOAP to IDoc
  @Test
  public void determineUrlDestinationWithSoapUrlForErpSystem() {

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL(SOAP_URL);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_ERP);
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);
    assertEquals(IDOC_URL, sapCpiOmmOrderDestinationService.determineUrlDestination(defaultLogicalSystem));

  }

  // IDoc -> S/4HANA : Do nothing
  @Test
  public void determineUrlDestinationWithIDocUrlForS4hanaSystem() {

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL(IDOC_URL);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_S4HANA);
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);
    assertEquals(IDOC_URL, sapCpiOmmOrderDestinationService.determineUrlDestination(defaultLogicalSystem));

  }

  // SOAP -> S/4HANA Cloud Edition : Do nothing
  @Test
  public void determineUrlDestinationWithSoapUrlForS4hanaCloudEditionSystem() {

    SAPHTTPDestinationModel sapHTTPDestinationModel = new SAPHTTPDestinationModel();
    sapHTTPDestinationModel.setTargetURL(SOAP_URL);
    defaultLogicalSystem.setSapSystemType(SapSystemType.SAP_S4HANA);
    defaultLogicalSystem.setSapHTTPDestination(sapHTTPDestinationModel);
    assertEquals(SOAP_URL, sapCpiOmmOrderDestinationService.determineUrlDestination(defaultLogicalSystem));

  }

}
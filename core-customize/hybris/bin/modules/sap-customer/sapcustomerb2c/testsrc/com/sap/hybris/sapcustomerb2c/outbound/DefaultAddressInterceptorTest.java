/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2c.outbound;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.sap.hybris.sapcustomerb2c.CustomerConstantsUtils.CONTACT_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


/**
 * JUnit Test for class defaultAddressInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 */
@UnitTest
public class DefaultAddressInterceptorTest {

  @InjectMocks
  private DefaultAddressInterceptor defaultAddressInterceptor;
  @Mock
  private AddressModel addressModel;
  @Mock
  private CustomerModel customerModel;
	@Mock
  private InterceptorContext ctx;
  @Mock
  private BaseStoreService baseStoreService;
  @Mock
  private DefaultStoreSessionFacade storeSessionFacade;
  @Mock
  private CustomerExportService customerExportService;
  @Mock
  private LanguageData languageData;
  @Mock
  private BaseStoreModel baseStore;
  @Mock
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;

  @Before
  public void setUp() throws Exception {

    MockitoAnnotations.initMocks(this);

  }


  @Test
  public void testSuccessfulReplication() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(1)).getCurrentBaseStore();
    verify(storeSessionFacade, times(1)).getCurrentLanguage();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testCustomerReplicationDisabled() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.FALSE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, never()).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, never()).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testAddressOwnerNotCustomerModel() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.FALSE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, never()).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testSapContactIDNull() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(null);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testUnsupportedFieldModified() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(false);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

  @Test
  public void testExportSessionLanguage() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
    given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn("DE");
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(1)).getCurrentBaseStore();
    verify(languageData, times(1)).getIsocode();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, null, "DE", addressModel);

  }

  @Test
  public void testExportBaseStoreUid() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    given(baseStoreService.getCurrentBaseStore()).willReturn(baseStore);
    given(baseStore.getUid()).willReturn("ELECTRONICS");
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, times(2)).getCurrentBaseStore();
    verify(baseStore, times(1)).getUid();
    verify(customerExportService, times(1)).sendCustomerData(customerModel, "ELECTRONICS", null, addressModel);

  }

  @Test
  public void testNotShipmentAddress() throws InterceptorException {

    // given
    given(addressModel.getOwner()).willReturn(customerModel);
    given(customerExportService.isCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerExportService.isClassCustomerModel(addressModel.getOwner())).willReturn(Boolean.TRUE);
    given(customerModel.getSapContactID()).willReturn(CONTACT_ID);
    given(customerModel.getDefaultShipmentAddress()).willReturn(addressModel);

    // when
    defaultAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(customerExportService, times(1)).isCustomerReplicationEnabled();
    verify(customerExportService, times(1)).isClassCustomerModel(addressModel.getOwner());
    verify(customerModel, times(1)).getSapContactID();
    verify(baseStoreService, never()).getCurrentBaseStore();
    verify(storeSessionFacade, never()).getCurrentLanguage();
    verify(customerExportService, never()).sendCustomerData(customerModel, null, null, addressModel);

  }

}
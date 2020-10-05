/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2b.outbound;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.sap.hybris.sapcustomerb2c.outbound.CustomerAddressReplicationUtilityService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * JUnit Test for class DefaultCustomerInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 */
@UnitTest
public class DefaultB2BCustomerAddressInterceptorTest {
  private static final String CUSTOMER_ID_10013 = "0000010013";
  private static final String CUSTOMER_ID_10017 = "0000010017";
  private static final String PUBLIC_KEY = "0000010017|0000010017|BUS1006001|null";

  @InjectMocks
  private DefaultB2BCustomerAddressInterceptor defaultCustomerAddressInterceptor;
  @Mock
  private AddressModel addressModel;
  @Mock
  private B2BCustomerModel b2bCustomerModel;
  @Mock
  private InterceptorContext ctx;
  @Mock
  private B2BCustomerExportService b2bCustomerExportService;
  @Mock
  private DefaultStoreSessionFacade storeSessionFacade;

  @Mock
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Check if the interceptor call the customerExportService
   * <ul>
   * <li>phone number is modified</li>
   * </ul>
   *
   * @throws InterceptorException
   */
  @Test
  public void testExportDataIfPhoneIsModified() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(addressModel.getOwner()).willReturn(b2bCustomerModel);
    given(addressModel.getPublicKey()).willReturn(PUBLIC_KEY);
    given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID_10017);
    given(storeSessionFacade.getCurrentLanguage()).willReturn(null);

    given(b2bCustomerModel.getDefaultShipmentAddress()).willReturn(addressModel);
    defaultCustomerAddressInterceptor.setB2bCustomerExportService(b2bCustomerExportService);
    defaultCustomerAddressInterceptor.setStoreSessionFacade(storeSessionFacade);

    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(true);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when
    defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel, "en");
  }

  /**
   * Check if the interceptor does not call the customerExportService
   * <ul>
   * <li>phone number is not modified</li>
   * </ul>
   *
   * @throws InterceptorException
   */
  @Test
  public void testNoExportDataIfPhoneIsNotodified() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(addressModel.getOwner()).willReturn(b2bCustomerModel);
    given(addressModel.getPublicKey()).willReturn(PUBLIC_KEY);
    given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID_10017);
    given(customerAddressReplicationUtilityService.isAddressReplicationRequired(any(), any(), any())).willReturn(false);
    given(customerAddressReplicationUtilityService.findDefaultAddress(any(), any())).willReturn(addressModel);

    // when

    defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
  }

  /**
   * Check if the interceptor does not call the customerExportService
   * <ul>
   * <li>generated public key is set not equal addressModel.getPublicKey()</li>
   * </ul>
   *
   * @throws InterceptorException
   */
  @Test
  public void testNoExportDataIfPublicKeyIsInvalid() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(addressModel.getOwner()).willReturn(b2bCustomerModel);
    given(addressModel.getPublicKey()).willReturn(PUBLIC_KEY);
    given(b2bCustomerModel.getCustomerID()).willReturn(CUSTOMER_ID_10013);

    // when
    defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);
    // then
    verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
  }

  /**
   * Check if the interceptor does not call the customerExportService
   * <ul>
   * <li>customerId is set to space</li>
   * </ul>
   *
   * @throws InterceptorException
   */
  @Test
  public void testNoExportDataIfCustomerIDIsSpace() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(addressModel.getOwner()).willReturn(b2bCustomerModel);
    given(b2bCustomerModel.getCustomerID()).willReturn("");

    // when
    defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

    //then
    verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
  }

  /**
   * Check if the interceptor does not call the customerExportService
   * <ul>
   * <li>customerId is set to null</li>
   * </ul>
   *
   * @throws InterceptorException
   */
  @Test
  public void testNoExportDataIfCustomerIDIsNull() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(addressModel.getOwner()).willReturn(b2bCustomerModel);
    given(b2bCustomerModel.getCustomerID()).willReturn(null);

    // when
    defaultCustomerAddressInterceptor.onValidate(addressModel, ctx);

    // then
    verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
  }

}
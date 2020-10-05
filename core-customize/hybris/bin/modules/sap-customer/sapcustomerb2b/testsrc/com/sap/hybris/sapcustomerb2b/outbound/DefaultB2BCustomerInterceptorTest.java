/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2b.outbound;

import com.sap.hybris.sapcustomerb2c.outbound.CustomerAddressReplicationUtilityService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.keygenerator.impl.PersistentKeyGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * JUnit Test for class DefaultCustomerInterceptor check if the CustomerExportService will only be called in a specific
 * situation.
 */
@UnitTest
public class DefaultB2BCustomerInterceptorTest {
  @InjectMocks
  private DefaultB2BCustomerInterceptor defaultCustomerInterceptor;
  @Mock
  private B2BCustomerModel b2bCustomerModel;
  @Mock
  private InterceptorContext ctx;
  @Mock
  private DefaultStoreSessionFacade storeSessionFacade;
  @Mock
  private B2BCustomerExportService b2bCustomerExportService;
  @Mock
  private CustomerAddressReplicationUtilityService customerAddressReplicationUtilityService;
  @Mock
  private PersistentKeyGenerator sapContactIdGenerator;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testB2BCustomerReplicationDisabled() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.FALSE);
    given(customerAddressReplicationUtilityService.isCustomerReplicationRequired(any(), any(), any())).willReturn(true);
    // when
    defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
    // then
    verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
  }

  @Test
  public void testCustomerIsNew() throws InterceptorException {
    // given
    given(ctx.isNew(b2bCustomerModel)).willReturn(true);
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    // when
    defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
    // then
    verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel, "en");
  }

  @Test
  public void testValidatorIsBeingCalledFromDataHub() throws InterceptorException {
    // given
    given(ctx.isNew(b2bCustomerModel)).willReturn(false);
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerAddressReplicationUtilityService.isCustomerReplicationRequired(any(), any(), any())).willReturn(false);
    // when
    defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
    // then
    verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
  }

  // we only replicate to Data Hub  when changes are detected
  @Test
  public void testUnsupportedField() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerAddressReplicationUtilityService.isCustomerReplicationRequired(any(), any(), any())).willReturn(false);
    // when
    defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
    // then
    verify(b2bCustomerExportService, times(0)).prepareAndSend(b2bCustomerModel, "en");
  }

  @Test
  public void testExportNameModified() throws InterceptorException {
    // given
    given(b2bCustomerExportService.isB2BCustomerReplicationEnabled()).willReturn(Boolean.TRUE);
    given(customerAddressReplicationUtilityService.isCustomerReplicationRequired(any(), any(), any())).willReturn(true);
    // when
    defaultCustomerInterceptor.onValidate(b2bCustomerModel, ctx);
    // then
    verify(b2bCustomerExportService, times(1)).prepareAndSend(b2bCustomerModel, "en");
  }

}
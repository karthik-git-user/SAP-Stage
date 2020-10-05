/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.events;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.model.process.QuoteProcessModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
public class SapCpqCpiQuoteCancelEventListenerTest {

    @InjectMocks
    private SapCpqCpiQuoteCancelEventListener quoteCancelEventListener = new SapCpqCpiQuoteCancelEventListener();

    @Mock
    private ModelService modelService;

    @Mock
    private BusinessProcessService businessProcessService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testOnEvent() {
        BaseStoreModel baseStore = new BaseStoreModel();
        baseStore.setUid("powertools");
        QuoteModel quote = new QuoteModel();
        quote.setCode("12345");
        quote.setStore(baseStore);
        UserModel user = new UserModel();

        SapCpqCpiQuoteCancelEvent event = new SapCpqCpiQuoteCancelEvent(quote, user, QuoteUserType.BUYER);
        QuoteProcessModel quoteCancelledProcessModel = new QuoteProcessModel();
        doNothing().when(modelService).refresh(Mockito.any());
        doNothing().when(modelService).save(Mockito.any());
        when(businessProcessService.createProcess(Mockito.anyString(), Mockito.anyString(), Mockito.anyMap()))
                .thenReturn(quoteCancelledProcessModel);

        doNothing().when(businessProcessService).startProcess(Mockito.any(QuoteProcessModel.class));

        quoteCancelEventListener.onEvent(event);

        verify(businessProcessService, times(1)).createProcess(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyMap());
        verify(businessProcessService, times(1)).startProcess(Mockito.any(QuoteProcessModel.class));

    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapDigitalPaymentServiceTest {

    @InjectMocks
    private DefaultSapDigitalPaymentService defaultSapDigitalPaymentService;

    @Test
    public void createPollRegisteredCardProcess(){
        defaultSapDigitalPaymentService.createPollRegisteredCardProcess(null);
    }
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2b.integration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@IntegrationTest(replaces = de.hybris.platform.commercefacades.consent.impl.DefaultConsentFacadeIntegrationTest.class)
public class SapDefaultConsentFacadeIntegrationTest {
	   @Test
	   public void replaceTestGivenAndWithdrawConsentWorkflow() {
	      assertEquals(1,1);
	   }
}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcustomerb2b.integration;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.dao.impl.DefaultPagedB2BCustomerDaoIntegrationTest;
import org.junit.Assert;
import org.junit.Test;


/*
    Replacement test for GRIFFIN-3201 until the test functionality can be replaced properly or the conflict handled
 */
@IntegrationTest(replaces = DefaultPagedB2BCustomerDaoIntegrationTest.class)
public class ReplaceDefaultPagedB2BCustomerDaoIntegrationTest {

    @Test
    public void replaceTest(){
        Assert.assertTrue(true);
    }

}

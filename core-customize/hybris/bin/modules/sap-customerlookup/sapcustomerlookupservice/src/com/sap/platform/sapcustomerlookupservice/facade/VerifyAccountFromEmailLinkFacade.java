/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.facade;

import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;

/**
 * This interface is used to verify the registered user using email link
 */
public interface VerifyAccountFromEmailLinkFacade {
    /**
     * Verify the link clicked by customer to verify account.
     *
     * @param token - pass parameter getting from email
     * @return whether token is valid or not. Return true if token is valid
     * @throws TokenInvalidatedException - Invalid token exception
     */
    boolean verifyEmail(String token) throws TokenInvalidatedException;
}

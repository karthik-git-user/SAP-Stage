/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomerlookupservice.token;

import de.hybris.platform.commerceservices.customer.TokenInvalidatedException;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;

import java.util.Date;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class DefaultSecureTokenServiceForCMS {

    private static final Logger LOG = Logger.getLogger(DefaultSecureTokenServiceForCMS.class);
    private long tokenValiditySeconds;
    private SecureTokenService secureTokenService;
    private ModelService modelService;

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public SecureTokenService getSecureTokenService() {
        return secureTokenService;
    }


    public void setSecureTokenService(SecureTokenService secureTokenService) {
        this.secureTokenService = secureTokenService;
    }

    public void setTokenValiditySeconds(long tokenValiditySeconds) {
        this.tokenValiditySeconds = tokenValiditySeconds;
    }

    public String generateAndSaveSecureToken(CustomerModel customerModel) {
        validateParameterNotNullStandardMessage("customerModel", customerModel);
        final long timeStamp = tokenValiditySeconds > 0L ? new Date().getTime() : 0L;
        final SecureToken data = new SecureToken(customerModel.getUid(), timeStamp);
        final String cmsLookupSecureToken = getSecureTokenService().encryptData(data);
        customerModel.setCmsLookupSecureToken(cmsLookupSecureToken);
        modelService.save(customerModel);
        return cmsLookupSecureToken;

    }

    public boolean checkValidityOfToken(final String token) throws TokenInvalidatedException {
        try {
            final SecureToken data = getSecureTokenService().decryptData(token);

            final long delta = new Date().getTime() - data.getTimeStamp();
            LOG.info("Delta calculated for validity =" + delta + " Date stamp= " + new Date().getTime() + "and token stamp = "
                    + data.getTimeStamp());
            return (delta / 1000 < tokenValiditySeconds);
        } catch (final IllegalArgumentException e) {
            LOG.error("Token is missing in the request or user has already validated the token. Error message= " + e.getMessage());
            throw new TokenInvalidatedException("Token verification failed for customer", e);
        }
    }
}






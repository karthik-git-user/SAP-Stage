/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import java.io.IOException;

public class DigitalPaymentsEncryptUtil {
    private static final Logger LOG = Logger.getLogger(DigitalPaymentsEncryptUtil.class);

    private SecureTokenService secureTokenService;
    private UserService userService;
    private static ObjectMapper mapper = new ObjectMapper();

    public String encryptWithUser(String data){
        //Bind data with User
        String userId = getUserService().getCurrentUser().getUid();
        Pair pair = new Pair(data, userId);

        //Encrypt pair
        String strData;
        try {
            strData = mapper.writeValueAsString(pair);
            SecureToken secureToken = new SecureToken(strData, System.currentTimeMillis());
            return getSecureTokenService().encryptData(secureToken);
        } catch (JsonProcessingException e) {
            LOG.error(e);
        }
        return null;
    }

    public String decryptWithUser(String encryptedKey){
        //Decrypt Pair
        SecureToken secureToken = getSecureTokenService().decryptData(encryptedKey);
        String strPair = secureToken.getData();

        Pair pair;
        try {
            //Convert data to pair
            pair = mapper.readValue(strPair, Pair.class);
            String uid = getUserService().getCurrentUser().getUid();

            //Check if token belong to current user
            if(!StringUtils.equalsIgnoreCase(uid, pair.getUser())){
                LOG.error("Something fishy. Looks like token doesn't belong to user");
                return null;
            }

            return pair.getValue();
        } catch (IOException e) {
            LOG.error(e);
        }

        return null;
    }

    protected SecureTokenService getSecureTokenService() {
        return secureTokenService;
    }

    public void setSecureTokenService(SecureTokenService secureTokenService) {
        this.secureTokenService = secureTokenService;
    }

    protected UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    static class Pair{
        String value;
        String user;

        Pair(){ }

        Pair(String value, String user) {
            this.value = value;
            this.user = user;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }
    }
}

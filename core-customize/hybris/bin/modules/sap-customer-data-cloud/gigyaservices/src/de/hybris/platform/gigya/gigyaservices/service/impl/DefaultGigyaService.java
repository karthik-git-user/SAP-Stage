/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.service.impl;


import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.auth.GSAuthRequest;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSRequest;
import com.gigya.socialize.GSResponse;

/**
 * Default implementation of GigyaService
 */
public class DefaultGigyaService implements GigyaService {

	private static final Logger LOG = Logger.getLogger(DefaultGigyaService.class);
	
	private UserService userService;

	@Override
	public GSResponse callRawGigyaApiWithConfig(final String method, final Map<String, Object> params,
			final GigyaConfigModel conf, final int maxTries, final int tryNum) {
		if (params != null) {
			try {				
				final ObjectMapper objectMapper = new ObjectMapper();
				final GSObject gigyaParams = new GSObject(objectMapper.writeValueAsString(params));
				return callRawGigyaApiWithConfigAndObject(method, gigyaParams, conf, maxTries, tryNum);
			} catch (final GigyaApiException e) {
				LOG.error(e.getMessage() + ", errorCode=" + e.getGigyaErrorCode(), e);
				throw e;
			} catch (final Exception e) {
				LOG.error(e.getMessage(), e);
				throw new GigyaApiException(e.getMessage());
			}
		} else {
			throw new GigyaApiException("Parameters are empty!");
		}
	}

	@Override
	public GSResponse callRawGigyaApiWithConfigAndObject(final String method, final GSObject gigyaParams,
			final GigyaConfigModel gigyaConfig, final int maxTries, int tryNum) {
		
		final GSRequest gsRequest;
		final String gigyaApiKey = gigyaConfig.getGigyaApiKey();
		final String gigyaSiteSecret = gigyaConfig.getGigyaSiteSecret();
		final String gigyaPrivateKey = gigyaConfig.getGigyaPrivateKey();
		final String gigyaUserKey = gigyaConfig.getGigyaUserKey();
		final String gigyaUserSecret = gigyaConfig.getGigyaUserSecret();		
		
		// Connect with Gigya's User Key and Private Key
		if (StringUtils.isNotEmpty(gigyaPrivateKey)) {
			
			// Remove 'userKey' as the connection is through private key
			gigyaParams.remove("userKey");
					
			gsRequest = new GSAuthRequest(gigyaUserKey, gigyaPrivateKey, gigyaApiKey, method);
						
			// Set Params to GS Request
			gsRequest.setParams(gigyaParams);			
		
		}
		// Connect with Gigya's User Key and User Secret
		else if(StringUtils.isNotEmpty(gigyaUserSecret)) {
			gsRequest = new GSRequest(gigyaApiKey, gigyaUserSecret, null, method, gigyaParams, true, gigyaUserKey);
		}		
		/**
		 * Connect with Gigya's User Key and Partner Secret.
		 *
		 * @deprecated Use either privateKey (recommended) or userSecret instead of Partner key.
		 */
		else {
			gsRequest = new GSRequest(gigyaApiKey, gigyaSiteSecret, method, gigyaParams, true);
		}
		
		// Set Data center		
		gsRequest.setAPIDomain(determineApiDC(gigyaConfig));
		
		// Send request
		final GSResponse res = gsRequest.send();
		
		// Parse response
		if (res.getErrorCode() != 0) {
			if (tryNum > maxTries) {
				throw new GigyaApiException(res.getErrorMessage(), res.getErrorCode());
			} else {
				tryNum++;
				callRawGigyaApiWithConfigAndObject(method, gigyaParams, gigyaConfig, maxTries, tryNum);
			}
		}
		
		return res;
	}
	
	private String determineApiDC(GigyaConfigModel conf) {
		
		final String gigyaConfigDC = conf.getGigyaDataCenter();
		final Boolean isSiteGlobal = conf.getIsSiteGlobal();
		final UserModel currentUser = userService.getCurrentUser();
		
		if(BooleanUtils.isTrue(isSiteGlobal) && currentUser instanceof CustomerModel) {
			final String gigyaUserDataCenter = ((CustomerModel) currentUser).getGyDataCenter();
			return ((StringUtils.isBlank(gigyaUserDataCenter)) ? gigyaConfigDC : gigyaUserDataCenter);
		}
		else {
			return gigyaConfigDC;
		}
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
}

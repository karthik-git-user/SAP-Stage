/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * Validates mandatory information of gigya config exists
 */
public class GigyaConfigValidateInterceptor implements ValidateInterceptor<GigyaConfigModel>
{

	@Override
	public void onValidate(final GigyaConfigModel gigyaConfig, final InterceptorContext context) throws InterceptorException
	{

		if (StringUtils.isEmpty(gigyaConfig.getGigyaApiKey())) {
			throw new InterceptorException("Gigya API key missing.");
		}

		if (!(StringUtils.isNotEmpty(gigyaConfig.getGigyaUserKey()) && (StringUtils.isNotEmpty(gigyaConfig.getGigyaUserSecret()) || StringUtils.isNotEmpty(gigyaConfig.getGigyaPrivateKey()))
				|| StringUtils.isNotEmpty(gigyaConfig.getGigyaSiteSecret()))) {
			throw new InterceptorException("Either gigya site secret or gigya user key and gigya user secret / private key must exist.");
		}
		
		// Global site validations
		if(BooleanUtils.isTrue(gigyaConfig.getIsSiteGlobal())) {			
			globalSiteValidations(gigyaConfig);
		}
		// Non-global site validations
		else {
			nonGlobalSiteValidations(gigyaConfig);
		}
	}
	
	private void globalSiteValidations(final GigyaConfigModel gigyaConfig) throws InterceptorException {
		
		final GigyaSessionType gigyaSessionType = getGigyaSessionType(gigyaConfig);
		
		if (StringUtils.isBlank(gigyaConfig.getGigyaPrivateKey())) {
			throw new InterceptorException("Private key is mandatory for a global site.");
		}
		
		if(gigyaSessionType != null && gigyaSessionType.equals(GigyaSessionType.SLIDING)) {
			throw new InterceptorException("SLIDING session is not supported for a global site.");
		}
	}
	
	private void nonGlobalSiteValidations(final GigyaConfigModel gigyaConfig) throws InterceptorException {
		
		final GigyaSessionType gigyaSessionType = getGigyaSessionType(gigyaConfig);
		
		if(StringUtils.isEmpty(gigyaConfig.getGigyaUserSecret()) && (gigyaSessionType != null && gigyaSessionType.equals(GigyaSessionType.SLIDING))) {
			throw new InterceptorException("User secret is mandatory to use SLIDING session for a non-global site.");
		}
	}
	
	private GigyaSessionType getGigyaSessionType(final GigyaConfigModel gigyaConfig) {
		
		final GigyaSessionConfigModel gigyaSessionConfig = gigyaConfig.getGigyaSessionConfig();
		return ((gigyaSessionConfig == null) ? null : gigyaSessionConfig.getSessionType());		
	}

}

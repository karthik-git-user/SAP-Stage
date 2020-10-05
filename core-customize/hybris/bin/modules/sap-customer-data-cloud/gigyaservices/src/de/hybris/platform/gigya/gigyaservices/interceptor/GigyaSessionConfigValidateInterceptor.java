/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class GigyaSessionConfigValidateInterceptor  implements ValidateInterceptor<GigyaSessionConfigModel>{

	@Override
	public void onValidate(GigyaSessionConfigModel gigyaSessionConfig, InterceptorContext interceptorContext) throws InterceptorException {
		
		final Set<GigyaConfigModel> gigyaConifgs = gigyaSessionConfig.getGigyaConfigs();
		
		for(GigyaConfigModel gigyaConifg : gigyaConifgs) {
			
			// Sliding Type validation
			if(gigyaSessionConfig.getSessionType().equals(GigyaSessionType.SLIDING)) {
				
				// If global site
				if(BooleanUtils.isTrue(gigyaConifg.getIsSiteGlobal())) {
					throw new InterceptorException(gigyaConifg.getCode() + " doesn't qualify for sliding session.");
				}
				else if(StringUtils.isEmpty(gigyaConifg.getGigyaUserSecret())) {
					throw new InterceptorException(gigyaConifg.getCode() + " doesn't qualify for sliding session.");
				}
			}
		}
		
	}

}

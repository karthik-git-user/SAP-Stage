/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

public class GigyaConfigPrepareInterceptor implements PrepareInterceptor<GigyaConfigModel> {

	@Override
	public void onPrepare(GigyaConfigModel gigyaConfig, InterceptorContext context) throws InterceptorException {
		
		// Remove whitespace from PrivateKey
		if (StringUtils.isNotEmpty(gigyaConfig.getGigyaPrivateKey())) {
			removeWhitespaceFromPK(gigyaConfig);
		}
		
	}
	
	private void removeWhitespaceFromPK(GigyaConfigModel gigyaConfig) {
		final String gigyaPrivateKey = gigyaConfig.getGigyaPrivateKey();
		
		final String[] searchListPK = {"-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----"};
		final String[] replacementListPK = {"", ""};
		
		gigyaConfig.setGigyaPrivateKey(StringUtils.deleteWhitespace(StringUtils.replaceEach(gigyaPrivateKey, searchListPK, replacementListPK)));
		
	}

}

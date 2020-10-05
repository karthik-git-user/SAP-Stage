/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionLead;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.util.Config;

public class GigyaSessionConfigPrepareInterceptor implements PrepareInterceptor<GigyaSessionConfigModel> {
	
	private static final String DEFAULT_SESSION_TIMEOUT = "default.session.timeout";
	private static final int DEFAULT_SESSION_TIMEOUT_VALUE = 3600;

	@Override
	public void onPrepare(GigyaSessionConfigModel gigyaSessionConfig, InterceptorContext interceptorContext)
			throws InterceptorException {

		if (GigyaSessionLead.COMMERCE.equals(gigyaSessionConfig.getSessionLead())) {
			gigyaSessionConfig.setSessionType(GigyaSessionType.FIXED);
			gigyaSessionConfig.setSessionDuration(Config.getInt(DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT_VALUE));
		}

		if (GigyaSessionLead.GIGYA.equals(gigyaSessionConfig.getSessionLead())) {
			setDefaultSessionType(gigyaSessionConfig);
			setDefaultSessionDurations(gigyaSessionConfig);
		}
	}

	private void setDefaultSessionType(GigyaSessionConfigModel gigyaSessionConfig) {
		if (gigyaSessionConfig.getSessionType() == null) {
			gigyaSessionConfig.setSessionType(GigyaSessionType.FIXED);
		}
	}

	private void setDefaultSessionDurations(GigyaSessionConfigModel gigyaSessionConfig) {
		switch (gigyaSessionConfig.getSessionType()) {
		case BROWSERCLOSED:
			gigyaSessionConfig.setSessionDuration(-1);
			break;
		case SLIDING:
			if (gigyaSessionConfig.getSessionDuration() == 0) {
				gigyaSessionConfig.setSessionDuration(Config.getInt(DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT_VALUE));
			}
			break;
		case FIXED:
			if (gigyaSessionConfig.getSessionDuration() == 0) {
				gigyaSessionConfig.setSessionDuration(Config.getInt(DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT_VALUE));
			}
			break;
		default:
			gigyaSessionConfig.setSessionDuration(Config.getInt(DEFAULT_SESSION_TIMEOUT, DEFAULT_SESSION_TIMEOUT_VALUE));
			break;
		}
	}
}

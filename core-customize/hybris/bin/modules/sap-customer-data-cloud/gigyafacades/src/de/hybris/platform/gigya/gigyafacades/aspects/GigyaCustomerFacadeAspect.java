/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Required;

import com.gigya.socialize.GSKeyNotFoundException;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import wiremock.org.apache.commons.lang3.StringUtils;

/**
 * Class responsible for enhancing logic of CustomerFacade so that CDC specific
 * functionality could be triggered
 */
@Aspect
public class GigyaCustomerFacadeAspect {

	private GigyaLoginFacade gigyaLoginFacade;

	private BaseSiteService baseSiteService;

	private UserService userService;

	@Around("execution(public void *..customer.*.updateFullProfile(..))")
	public void updateFullProfile(final ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();

		// Schedule sync from CDC if the customer updates info from CDC screen set
		BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		if (baseSite != null && baseSite.getGigyaConfig() != null) {
			updateCDCUser(baseSite);
		}
	}

	private void updateCDCUser(BaseSiteModel baseSite) throws GSKeyNotFoundException {
		UserModel currentUser = userService.getCurrentUser();
		if (currentUser instanceof CustomerModel && StringUtils.isNoneEmpty(((CustomerModel) currentUser).getGyUID())) {
			gigyaLoginFacade.updateUser(baseSite.getGigyaConfig(), currentUser);
		}
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	public GigyaLoginFacade getGigyaLoginFacade() {
		return gigyaLoginFacade;
	}

	@Required
	public void setGigyaLoginFacade(GigyaLoginFacade gigyaLoginFacade) {
		this.gigyaLoginFacade = gigyaLoginFacade;
	}

	public UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}

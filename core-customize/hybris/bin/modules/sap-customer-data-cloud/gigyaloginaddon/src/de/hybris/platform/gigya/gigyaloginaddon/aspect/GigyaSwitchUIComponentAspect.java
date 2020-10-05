/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import de.hybris.platform.site.BaseSiteService;

/**
 * Class responsible for loading UI changes specific for SAP CDC integration
 */
@Aspect
public class GigyaSwitchUIComponentAspect
{

	public static final String GIGYALOGINADDON_ADDON_PREFIX = "addon:/gigyaloginaddon/";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String FORWARD_PREFIX = "forward:";
	public static final String ADDON_PREFIX = "addon:";

	private BaseSiteService baseSiteService;

	/**
	 * Method which checks if UI component for SAP CDC integration should be applied
	 * 
	 * @param pjp
	 * @return uiComponenet name
	 * @throws Throwable
	 */
	public String applyUIChanges(final ProceedingJoinPoint pjp) throws Throwable
	{
		String uiComponent = pjp.proceed().toString();
		if (checkUiComponentData(uiComponent) || baseSiteService.getCurrentBaseSite().getGigyaConfig() == null)
		{
			return uiComponent;
		}

		final StringBuilder prefix = new StringBuilder(GIGYALOGINADDON_ADDON_PREFIX);
		prefix.append(uiComponent);
		uiComponent = prefix.toString();
		return uiComponent;

	}

	/**
	 * Method to apply UI changes around the doLogin method
	 * 
	 * @param joinPoint
	 * @return uiComponent name
	 * @throws Throwable
	 */
	@Around("execution(public String *..controllers.pages.*.doLogin(..))")
	public String doLogin(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

	/**
	 * Method to apply UI changes around the doCheckoutLogin method
	 * 
	 * @param joinPoint
	 * @return uiComponent name
	 * @throws Throwable
	 */
	@Around("execution(public String *..controllers.pages.*.doCheckoutLogin(..))")
	public String doCheckoutLogin(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

	private boolean checkUiComponentData(final String uiComponent)
	{
		return uiComponent.contains(REDIRECT_PREFIX) || uiComponent.contains(FORWARD_PREFIX)
				|| uiComponent.contains(ADDON_PREFIX) || uiComponent.contains(GIGYALOGINADDON_ADDON_PREFIX);
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}

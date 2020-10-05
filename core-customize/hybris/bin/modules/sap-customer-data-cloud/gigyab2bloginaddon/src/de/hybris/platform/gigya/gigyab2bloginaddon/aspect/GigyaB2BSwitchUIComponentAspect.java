/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bloginaddon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import de.hybris.platform.site.BaseSiteService;

/**
 * Class responsible for loading UI changes specific for SAP CDC integration
 */
@Aspect
public class GigyaB2BSwitchUIComponentAspect
{

	public static final String GIGYAB2BLOGINADDON_ADDON_PREFIX = "addon:/gigyab2bloginaddon";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String FORWARD_PREFIX = "forward:";
	public static final String COMMERCEORGADDON_PREFIX = "addon:/commerceorgaddon";

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

		if (uiComponent.contains(COMMERCEORGADDON_PREFIX)
				&& baseSiteService.getCurrentBaseSite().getGigyaConfig() != null)
		{
			uiComponent = uiComponent.substring(COMMERCEORGADDON_PREFIX.length());
		}

		if (uiComponent.contains(REDIRECT_PREFIX) || uiComponent.contains(FORWARD_PREFIX)
				|| uiComponent.contains(GIGYAB2BLOGINADDON_ADDON_PREFIX)
				|| baseSiteService.getCurrentBaseSite().getGigyaConfig() == null)
		{
			return uiComponent;
		}

		final StringBuilder prefix = new StringBuilder(GIGYAB2BLOGINADDON_ADDON_PREFIX);
		prefix.append(uiComponent);
		uiComponent = prefix.toString();
		return uiComponent;
	}

	/**
	 * Method to apply UI changes around the manageUnits method
	 * 
	 * @param joinPoint
	 * @return uiComponent name
	 * @throws Throwable
	 */
	@Around("execution(public String *..controllers.pages.*.manageUnits(..))")
	public String manageUnits(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

	/**
	 * Method to apply UI changes around the unitDetails method
	 * 
	 * @param joinPoint
	 * @return uiComponent name
	 * @throws Throwable
	 */
	@Around("execution(public String *..controllers.pages.*.unitDetails(..))")
	public String unitDetails(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

	/**
	 * Method to apply UI changes around the editUnit method
	 * 
	 * @param joinPoint
	 * @return uiComponent name
	 * @throws Throwable
	 */
	@Around("execution(public String *..controllers.pages.*.editUnit(..))")
	public String editUnit(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

	/**
	 * Method to apply UI changes around the manageUsers method
	 * 
	 * @param joinPoint
	 * @return uiComponent name
	 * @throws Throwable
	 */
	@Around("execution(public String *..controllers.pages.*.manageUsers(..))")
	public String manageUsers(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
	}

	/**
	 * Method to apply UI changes around the manageUserDetail method
	 * 
	 * @param joinPoint
	 * @return uiComponent name
	 * @throws Throwable
	 */
	@Around("execution(public String *..controllers.pages.*.manageUserDetail(..))")
	public String manageUserDetail(final ProceedingJoinPoint joinPoint) throws Throwable
	{
		return applyUIChanges(joinPoint);
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

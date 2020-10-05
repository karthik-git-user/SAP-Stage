/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.constants;

import de.hybris.platform.util.Config;

import java.util.Set;

import com.google.common.collect.Sets;


/**
 * Global class for all Gigyaservices constants. You can add global constants for your extension into this class.
 */
public final class GigyaservicesConstants extends GeneratedGigyaservicesConstants
{
	public static final String EXTENSIONNAME = "gigyaservices";

	private GigyaservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "gigyaservicesPlatformLogo";

	protected static final Set<String> EXTRA_FIELDS = Sets.newHashSet("languages", "address", "phones", "education", "honors",
			"publications", "patents", "certifications", "professionalHeadline", "bio", "industry", "specialties", "work", "skills",
			"religion", "politicalView", "interestedIn", "relationshipStatus", "hometown", "favorites", "followersCount",
			"followingCount", "username", "locale", "verified", "timezone", "likes", "samlData");

	public static final int MAX_RETRIES = Config.getInt("gigyaservices.max.retries", 2);

	public static final int TRY_NUM = Config.getInt("gigyaservices.initial.retry.num", 1);

	public static final String GIGYA_PROFILE = "profile";
}

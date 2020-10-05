/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.gigya.socialize.Base64;
import com.gigya.socialize.SigUtils;

import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;

/**
 * Utility class to generate gigya session expiration cookie's value generator
 */
public final class GigyaCookieValueGenerator {

	private static final Logger LOG = Logger.getLogger(GigyaCookieValueGenerator.class);

	private static final String HASHING_ALGORITHM_NAME = "HmacSHA1";
	private static final String SEPARATOR = "_";

	private GigyaCookieValueGenerator() {
		// To suppress object creation of utility class
	}

	public static String generateCookieValue(GigyaConfigModel gigyaConfig, Cookie gltCookie, int timeoutInSeconds) {
		String cookieValue = StringUtils.EMPTY;
		try {
			cookieValue = getCookieValue(gigyaConfig, gltCookie, timeoutInSeconds, gltCookie.getValue());
		} catch (InvalidKeyException | UnsupportedEncodingException e) {
			LOG.error("Exception while creating cookie", e);
		}

		return cookieValue;
	}

	private static String getCookieValue(GigyaConfigModel gigyaConfig, Cookie gltCookie, int timeoutInSeconds,
			String gltCookieValue) throws InvalidKeyException, UnsupportedEncodingException {
		if (StringUtils.isNoneEmpty(gigyaConfig.getGigyaSiteSecret())) {
			return SigUtils.getDynamicSessionSignature(gltCookie.getValue(), 300, gigyaConfig.getGigyaSiteSecret());
		} else {
			final String expirationTimeUnixFormat = String.valueOf((new Date().getTime() / 1000L) + timeoutInSeconds);
			final String loginToken = URLDecoder.decode(gltCookieValue, "UTF-8").split("\\|")[0];
			final String unsignedExpiration = new StringBuilder(loginToken).append(SEPARATOR).append(expirationTimeUnixFormat)
					.append(SEPARATOR).append(gigyaConfig.getGigyaUserKey()).toString();
			final String signedExpiration = createSignedExpirationValue(unsignedExpiration,
					Base64.decode(gigyaConfig.getGigyaUserSecret()));

			return new StringBuilder().append(expirationTimeUnixFormat).append(SEPARATOR)
					.append(gigyaConfig.getGigyaUserKey()).append(SEPARATOR).append(signedExpiration).toString();
		}
	}

	private static String createSignedExpirationValue(String unsignedValue, byte[] keyInBytes)
			throws UnsupportedEncodingException, InvalidKeyException {
		final byte[] unsignedValueInBytes = unsignedValue.getBytes("UTF-8");
		final SecretKeySpec signingKey = new SecretKeySpec(keyInBytes, HASHING_ALGORITHM_NAME);
		Mac mac = null;
		try {
			mac = Mac.getInstance(HASHING_ALGORITHM_NAME);
		} catch (NoSuchAlgorithmException e) {
			LOG.error(e);
			return null;
		}
		mac.init(signingKey);
		final byte[] rawHmacInBytes = mac.doFinal(unsignedValueInBytes);
		return Base64.encodeToString(rawHmacInBytes, true);
	}

}

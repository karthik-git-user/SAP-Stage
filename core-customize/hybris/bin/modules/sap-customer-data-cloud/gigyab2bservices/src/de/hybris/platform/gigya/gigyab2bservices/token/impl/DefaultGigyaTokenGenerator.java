/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.token.impl;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;

import com.google.common.primitives.Bytes;

import de.hybris.platform.gigya.gigyab2bservices.constants.Gigyab2bservicesConstants;
import de.hybris.platform.gigya.gigyab2bservices.token.GigyaTokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Default implementation of Gigya Token Generator which generates a token to
 * fetch authorizations from CDC
 */
public class DefaultGigyaTokenGenerator implements GigyaTokenGenerator
{

	private static final int HS256_MIN_LEN_IN_BYTES = 32;
	private static final int DEFAULT_PADDING = 0;

	@Override
	public String generate(final String cliendId, final String secret, final int timeoutInSeconds)
	{
		if (StringUtils.isNotEmpty(cliendId) && StringUtils.isNotEmpty(secret))
		{
			final Map<String, Object> header = new TreeMap();
			header.put(Gigyab2bservicesConstants.ALGORITHM, Gigyab2bservicesConstants.ALGORITHM_HS256);
			header.put(Gigyab2bservicesConstants.TOKEN_TYPE, Gigyab2bservicesConstants.TOKEN_TYPE_JWT);

			final long expirationTimeUnixFormat = (new Date().getTime() / 1000L) + timeoutInSeconds;

			final Map<String, Object> claims = new HashMap<>();
			claims.put(Claims.ISSUER, cliendId);
			claims.put(Claims.EXPIRATION, expirationTimeUnixFormat);

			return Jwts.builder().setHeaderParams(header).setClaims(claims).signWith(createSigningKey(secret))
					.compact();
		}
		return null;
	}

	private SecretKey createSigningKey(final String secret)
	{
		// ensure decoded secret is of 32 bytes
		byte[] paddedDecodedSecret = Bytes.ensureCapacity(Base64.getDecoder().decode(secret), HS256_MIN_LEN_IN_BYTES,
				DEFAULT_PADDING);

		return new SecretKeySpec(paddedDecodedSecret, SignatureAlgorithm.HS256.getJcaName());
	}
}

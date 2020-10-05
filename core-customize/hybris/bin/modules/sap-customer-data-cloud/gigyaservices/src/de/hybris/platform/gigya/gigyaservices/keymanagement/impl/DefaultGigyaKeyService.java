/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.keymanagement.impl;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaservices.api.exception.GigyaApiException;
import de.hybris.platform.gigya.gigyaservices.constants.GigyaservicesConstants;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJWTHeader;
import de.hybris.platform.gigya.gigyaservices.jalo.GigyaPublicKey;
import de.hybris.platform.gigya.gigyaservices.keymanagement.GigyaKeyService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaPublicKeyModel;
import de.hybris.platform.gigya.gigyaservices.service.GigyaService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * 
 * Default implementation of GigyaKeyService
 */
public class DefaultGigyaKeyService implements GigyaKeyService {
	
	private static final Logger LOG = Logger.getLogger(DefaultGigyaKeyService.class);
	private static final int STATUS_CODE_200 = 200;
	
	private GigyaService gigyaService;
	
	private ModelService modelService;
	
	private GenericDao<GigyaPublicKeyModel> gigyaPublicKeyGenericDao;
	
	@Override
	public PublicKey retreivePublicKeyOfASite(GigyaConfigModel gigyaConfig, final String checkKeyId) {
		
		GigyaPublicKeyModel gigyaPublicKey = gigyaConfig.getGigyaPublicKey();
		String modulus = (gigyaPublicKey == null ? null : gigyaPublicKey.getModulus());
		String exponent = (gigyaPublicKey == null ? null : gigyaPublicKey.getExponent());
		
		// Verify if Public Key is valid
		if (!(gigyaPublicKey != null && StringUtils.equals(gigyaPublicKey.getKeyId(), checkKeyId))) {
			
			final GigyaPublicKeyModel storedGigyaPublicKey = findPublicKeybyKeyId(checkKeyId);
			
			if(storedGigyaPublicKey == null) {
				gigyaPublicKey = new GigyaPublicKeyModel();
				callRawGigyaApiForPublicKey(gigyaPublicKey, gigyaConfig);
				modulus = gigyaPublicKey.getModulus();
				exponent = gigyaPublicKey.getExponent();
			}
			else {
				gigyaConfig.setGigyaPublicKey(storedGigyaPublicKey);
				modelService.save(gigyaConfig);
				modulus = storedGigyaPublicKey.getModulus();
				exponent = storedGigyaPublicKey.getExponent();
			}				
		}
		return generatePublicKey(modulus, exponent);		
	}

	@Override
	public PublicKey generatePublicKey(final String modulus, final String exponent) {
		
		final String[] searchListModulus = {"-", "_"};
		final String[] replacementListModulus = {"+", "/"};

		final String decodedModulus = StringUtils.replaceEach(modulus, searchListModulus, replacementListModulus);
		
		byte[] n = Base64.getDecoder().decode(decodedModulus.getBytes());
        byte[] e = Base64.getDecoder().decode(exponent.getBytes());
        PublicKey generatedPublicKey = null;
        
        try {
        	
	        BigInteger nBigInt = new BigInteger(1, n);
	        BigInteger eBigInt = new BigInteger(1, e);
	        RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(nBigInt, eBigInt);
	        KeyFactory fact = KeyFactory.getInstance("RSA");
	        generatedPublicKey = fact.generatePublic(rsaPubKey);
			
		} catch (Exception e1) {
			LOG.error("Error generating public key." + e1);
		}
        
        return generatedPublicKey;
	}

	@Override
	public Boolean validateToken(final GigyaConfigModel gigyaConfig, final String idToken) {
		
		final String[] jwtString = idToken.split("\\.");
		final String encodedJwtHeader = jwtString[0];
		final String encodedJwtPayload = jwtString[1];
		final String tokenData = encodedJwtHeader + "." + encodedJwtPayload;
		String encodedKeySignature = jwtString[2];
		
		final String[] searchListKS = {"-", "_"};
		final String[] replacementListKS = {"+", "/"};
		encodedKeySignature = StringUtils.replaceEach(encodedKeySignature, searchListKS, replacementListKS);
		
		byte[] keySignature = Base64.getDecoder().decode(encodedKeySignature.getBytes());
		
		try {
						
			final GigyaJWTHeader jwtHeader = mapJWTHeader(encodedJwtHeader);
			
			PublicKey publicJWTKey = retreivePublicKeyOfASite(gigyaConfig, jwtHeader.getKid());
		
			Signature rsaSig = Signature.getInstance("SHA256withRSA");		
			rsaSig.initVerify(publicJWTKey);        
			rsaSig.update(tokenData.getBytes("UTF-8"));
			return rsaSig.verify(keySignature);
			
		} catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | UnsupportedEncodingException e) {			
			LOG.error("Error validating ID Token." + e);
		}
		
		return false;
	}
	
	private GigyaJWTHeader mapJWTHeader(String encodedJwtHeader) {
		
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		byte[] decodedJWTHeader = Base64.getDecoder().decode(encodedJwtHeader);
		try {
			return mapper.readValue(decodedJWTHeader, GigyaJWTHeader.class);
		} catch (IOException e) {
			LOG.error("Error parsing ID Token." + e);		
		}
		return null;
	}
	
	public GigyaPublicKeyModel findPublicKeybyKeyId(final String keyId) {
		final List<GigyaPublicKeyModel> gigyaPublicKeys = gigyaPublicKeyGenericDao.find(Collections.singletonMap(GigyaPublicKeyModel.KEYID, keyId));
		return CollectionUtils.isNotEmpty(gigyaPublicKeys) ? gigyaPublicKeys.get(0) : null;
	}
	
	private void callRawGigyaApiForPublicKey(final GigyaPublicKeyModel gigyaPublicKey, final GigyaConfigModel gigyaConfig) {
		
		final LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		
		try {
			final GSObject data = gigyaService.callRawGigyaApiWithConfig("accounts.getJWTPublicKey", params,
					gigyaConfig, GigyaservicesConstants.MAX_RETRIES, GigyaservicesConstants.TRY_NUM).getData();
			
			if (data.getInt("statusCode") == STATUS_CODE_200 && data.getInt("errorCode") == 0) {
				gigyaPublicKey.setModulus(data.getString("n"));
				gigyaPublicKey.setExponent(data.getString("e"));
				gigyaPublicKey.setKeyId(data.getString("kid"));
				
				gigyaConfig.setGigyaPublicKey(gigyaPublicKey);
				modelService.saveAll(gigyaPublicKey, gigyaConfig);
			}
		} catch (GSKeyNotFoundException | InvalidClassException | GigyaApiException e) {
			LOG.error("Error fetching public key from Gigya" + e);
		}
	}
	
	public void setGigyaService(GigyaService gigyaService) {
		this.gigyaService = gigyaService;
	}
	
	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}

	public void setGigyaPublicKeyGenericDao(GenericDao<GigyaPublicKeyModel> gigyaPublicKeyGenericDao) {
		this.gigyaPublicKeyGenericDao = gigyaPublicKeyGenericDao;
	}

}

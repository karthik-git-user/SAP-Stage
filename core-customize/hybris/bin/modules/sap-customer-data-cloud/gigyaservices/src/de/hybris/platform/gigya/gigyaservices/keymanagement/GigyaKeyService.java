/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.keymanagement;

import java.security.PublicKey;

import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;

/**
 * 
 * Service to invoke key management service using gigya's java classes
 */
public interface GigyaKeyService {
	
	/**
	 * Method to retrieve public key of a CMSSite
	 * 
	 * @param gigyaConfig
	 *           The Gigya Config model
	 * 
	 * @param checkKeyId
	 * 			Key ID from ID Token
	 * 
	 * @return PublickKey of Site
	 */
	PublicKey retreivePublicKeyOfASite(GigyaConfigModel gigyaConfig, String checkKeyId);
	
	/**
	 * Method to generate public key from modulus and exponent
	 * 
	 * @param modulus 
	 * 			Modulus of Public Key
	 * 
	 * @param exponent
	 * 			Exponent of Public Key
	 * 
	 * @return PublicKey 
	 */
	PublicKey generatePublicKey(String modulus, String exponent);
	
	/**
	 * Method to validate idToken of Gigya
	 * 
	 * @param gigyaConfig
	 *           The Gigya Config model
	 *           
	 * @param idToken 
	 * 			ID Token sent by Gigya
	 * 
	 * @return Boolean 
	 */
	Boolean validateToken(GigyaConfigModel gigyaConfig, String idToken);
	
}

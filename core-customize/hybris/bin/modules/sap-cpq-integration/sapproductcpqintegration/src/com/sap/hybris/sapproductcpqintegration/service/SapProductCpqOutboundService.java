/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapproductcpqintegration.service;

import org.springframework.http.ResponseEntity;

import de.hybris.platform.core.model.media.MediaModel;

/**
 * Interface for sending product media object to callidus cloud.
 */
public interface SapProductCpqOutboundService
{
	/**
	 * preparing and sending restTemplate post request for replicating product image to CPQ.
	 * @param mediaModel MediaModel Object
	 * @return ResponseEntity<String>
	 */
	public ResponseEntity<String> sendProductMedia(final MediaModel mediaModel);
}

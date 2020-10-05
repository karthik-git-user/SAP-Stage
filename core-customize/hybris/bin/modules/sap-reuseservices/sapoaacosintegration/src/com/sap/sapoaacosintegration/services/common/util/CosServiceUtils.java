/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.common.util;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.storelocator.GPS;
import de.hybris.platform.storelocator.data.AddressData;


/**
 * Service utility for COS
 */
public interface CosServiceUtils
{

	/**
	 * @return {@link String}
	 */
	String generateItemNumber();


	/**
	 * @param addressData
	 * @return {@link GPS}
	 */
	GPS fetchDestinationCoordinates(AddressData addressData);


	/**
	 * @param {@link
	 * 			 String}
	 * @return {@link ConsumedDestinationModel}
	 */
	ConsumedDestinationModel getConsumedDestinationModelById(final String destinationId);
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.strategies.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryModeLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.sap.sapserviceorder.constants.SapserviceorderConstants;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.util.Config;

public class DefaultSapServiceDeliveryModeLookupStrategy extends DefaultDeliveryModeLookupStrategy{
	
	private static final Logger LOG = Logger.getLogger(DefaultSapServiceDeliveryModeLookupStrategy.class);
	
	@Override
	public List<DeliveryModeModel> getSelectableDeliveryModesForOrder(AbstractOrderModel abstractOrderModel) {
		
		List<DeliveryModeModel> deliveryModes = new ArrayList<>(super.getSelectableDeliveryModesForOrder(abstractOrderModel));
		String deliveryModeCodeForService = Config.getParameter(SapserviceorderConstants.SERVICE_DELIVERY);
		
		boolean isService = abstractOrderModel.getEntries().stream().allMatch(SapServiceOrderUtil::isServiceEntry);
		
		if(isService) {
			Optional<DeliveryModeModel> serviceDelivery = deliveryModes.stream().filter(dm -> deliveryModeCodeForService.equalsIgnoreCase(dm.getCode())).findAny();
			deliveryModes.clear();
			if(serviceDelivery.isPresent()) {
				deliveryModes.add(serviceDelivery.get());
			}
			else {
				LOG.info("Delivery mode configuration for service is missing");
			}
			
		} else {
			deliveryModes.removeIf(dm -> deliveryModeCodeForService.equalsIgnoreCase(dm.getCode()));
		}
		
		return deliveryModes;
	}

}

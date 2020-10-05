/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing.impl;

import java.util.Collection;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringUtils;

import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.request.CartItem;
import com.sap.retail.oaa.commerce.services.common.jaxb.pojos.request.CartItems;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.commerce.services.sourcing.exception.SourcingException;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.CartRequest;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.request.DeliveryAddressRequest;
import com.sap.sapoaacarintegration.constants.SapoaacarintegrationConstants;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.sapmodel.jalo.SAPDeliveryMode;


/**
 *
 */
public abstract class SourcingRequestMapperBase
{

	/** ABAP true flag */
	protected static final String ABAP_TRUE = "X";
	/** SAP delivery modes configuration property */
	protected static final String CONFIG_PROPERTY_SAP_DELIVERY_MODES = "sapDeliveryModes";
	/** SAP access delivery mode configuration property */
	protected static final String CONFIG_PROPERTY_ACCESS_DELIVERY_MODE = "deliveryMode";
	//
	private SAPConfigurationService sapCoreConfigurationService;
	protected ServiceUtils serviceUtils;

	/**
	 * @param sapCoreConfigurationService
	 *           the sapCoreConfigurationService to set
	 */
	public void setSapCoreConfigurationService(final SAPConfigurationService sapCoreConfigurationService)
	{
		this.sapCoreConfigurationService = sapCoreConfigurationService;
	}

	/**
	 * @return the sapCoreConfigurationService
	 */
	protected SAPConfigurationService getSapCoreConfigurationService()
	{
		return sapCoreConfigurationService;
	}

	/**
	 * Converts an hybris delivery mode into a SAP shipping condition
	 *
	 * @param deliveryMode
	 * @return the code for the sap shipping condition
	 */
	protected String convertHybrisDelModeToSapShippingMethod(final DeliveryModeModel deliveryMode)
	{

		final Collection<ConfigurationPropertyAccess> collection = sapCoreConfigurationService
				.getPropertyAccessCollection(CONFIG_PROPERTY_SAP_DELIVERY_MODES);

		//In case delivery Method is not set, or delivery method has no code return null
		if (deliveryMode == null || deliveryMode.getCode() == null)
		{
			return null;
		}

		//Loop over SAP delivery mapping and return value mapped to hybris delivery mode
		for (final ConfigurationPropertyAccess configurationPropertyAccess : collection)
		{
			final ConfigurationPropertyAccess hybrisDeliveryMode = configurationPropertyAccess
					.getPropertyAccess(CONFIG_PROPERTY_ACCESS_DELIVERY_MODE);
			final String hybrisDeliveryModeCode = hybrisDeliveryMode.getProperty(DeliveryMode.CODE);

			if (deliveryMode.getCode().equals(hybrisDeliveryModeCode))
			{
				return configurationPropertyAccess.getProperty(SAPDeliveryMode.DELIVERYVALUE);
			}
		}
		return null;
	}

	/**
	 * @param serviceUtils
	 *           the serviceUtils to set
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}

	/**
	 * @return the serviceUtils
	 */
	protected ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}

	/**
	 * Maps CartModel to CartItems.
	 *
	 * @param cartModel
	 * @return CartItems
	 */
	protected CartItems setAllCartItems(final AbstractOrderModel cartModel)
	{
		final CartItems cartItems = new CartItems();
		for (final AbstractOrderEntryModel entryModel : cartModel.getEntries())
		{
			final CartItem cartItem = new CartItem();
			cartItem.setArticleId(entryModel.getProduct().getCode());
			cartItem
					.setExternalId(serviceUtils.createExternalIdForItem(entryModel.getEntryNumber().toString(), cartModel.getGuid()));
			cartItem.setQuantity(entryModel.getQuantity().toString());
			cartItem.setUnitIso(StringUtils.left(entryModel.getUnit().getCode(),SapoaacarintegrationConstants.UNIT_MAXLENGTH)); //Only 3 letter iso code
			cartItem.setUnit(entryModel.getUnit().getSapCode());
			cartItem.setItemTotalPrice(entryModel.getTotalPrice());
			cartItem.setCurrencyIsoCode(cartModel.getCurrency().getIsocode());

			//Set source in case of click and collect
			if (entryModel.getDeliveryPointOfService() != null)
			{
				cartItem.setSource(entryModel.getDeliveryPointOfService().getName());
				cartItem.setSourcePreselected(ABAP_TRUE);
			}
			cartItems.addItem(cartItem);
		}
		return cartItems;
	}

	/**
	 * @param abstractOrderModel
	 * @param cart
	 * @param reservationStatus
	 */
	protected void setCart(final AbstractOrderModel abstractOrderModel, final CartRequest cart, final String reservationStatus)
	{
		try
		{
			cart.setExternalId(serviceUtils.convertGuidToBase64(abstractOrderModel.getGuid()));
		}
		catch (final DecoderException e)
		{
			throw new SourcingException(e);
		}


		if (reservationStatus.equals(SapoaacommerceservicesConstants.RESERVATION_STATUS_ORDER))
		{
			cart.setOrderId(abstractOrderModel.getCode());
		}



		cart.setShippingMethod(convertHybrisDelModeToSapShippingMethod(abstractOrderModel.getDeliveryMode()));
		cart.setTotalPrice(abstractOrderModel.getTotalPrice());
		cart.setDeliveryCost(abstractOrderModel.getDeliveryCost());
		cart.setCurrencyIsoCode(abstractOrderModel.getCurrency().getIsocode());
		cart.setItems(setAllCartItems(abstractOrderModel));

		if (abstractOrderModel.getDeliveryAddress() != null)
		{
			cart.setDeliveryAddress(mapModelToDeliveryAddressRequest(abstractOrderModel));
		}
	}

	/**
	 * Maps abstractOrderModel to DeliveryAddressRequest.
	 *
	 * @param abstractOrderModel
	 * @return DeliveryAddressRequest
	 */
	private DeliveryAddressRequest mapModelToDeliveryAddressRequest(final AbstractOrderModel abstractOrderModel)
	{
		final DeliveryAddressRequest deliveryAddress = new DeliveryAddressRequest();
		deliveryAddress.setCountry(abstractOrderModel.getDeliveryAddress().getCountry().getSapCode());
		deliveryAddress.setCity2(abstractOrderModel.getDeliveryAddress().getDistrict());
		deliveryAddress.setPoBox(abstractOrderModel.getDeliveryAddress().getPobox());
		deliveryAddress.setPostCode1(abstractOrderModel.getDeliveryAddress().getPostalcode());
		if (abstractOrderModel.getDeliveryAddress().getRegion() != null)
		{
			deliveryAddress.setRegion(abstractOrderModel.getDeliveryAddress().getRegion().getIsocodeShort());
		}
		deliveryAddress.setStreet(abstractOrderModel.getDeliveryAddress().getStreetname());
		deliveryAddress.setHouseNum1(abstractOrderModel.getDeliveryAddress().getStreetnumber());
		deliveryAddress.setCity1(abstractOrderModel.getDeliveryAddress().getTown());
		return deliveryAddress;
	}
}

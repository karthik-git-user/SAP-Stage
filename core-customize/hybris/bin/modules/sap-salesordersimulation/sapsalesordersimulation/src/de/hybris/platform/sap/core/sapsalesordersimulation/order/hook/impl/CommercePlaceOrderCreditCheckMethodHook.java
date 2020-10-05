/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.core.sapsalesordersimulation.order.hook.impl;

import org.apache.log4j.Logger;

import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.localization.Localization;

public class CommercePlaceOrderCreditCheckMethodHook implements CommercePlaceOrderMethodHook {
	private UserService userService;
	private SalesOrderSimulationService salesOrderSimulationService ;
	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	private static final Logger LOGGER = Logger.getLogger(CommercePlaceOrderCreditCheckMethodHook.class.getName());

	@Override
	public void afterPlaceOrder(CommerceCheckoutParameter parameter, CommerceOrderResult orderModel)
			throws InvalidCartException {
		// not implemented
		
	}

	@Override
	public void beforePlaceOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {
		final CartModel cart = parameter.getCart();
		UserModel userModel = getUserService().getCurrentUser();
	
		if (getSapSimulateSalesOrderEnablementService().isCreditCheckActive() && isB2BOrder(cart) && getSalesOrderSimulationService().checkCreditLimitExceeded(cart,userModel) ) {
			LOGGER.error(String.format("Credit limit is exceeded for the cart %s  ", cart.getCode()));			
			throw new EntityValidationException(Localization.getLocalizedString("cart.creditcheck.exceeded"));
		}
		
	}

	@Override
	public void beforeSubmitOrder(CommerceCheckoutParameter parameter, CommerceOrderResult result)
			throws InvalidCartException {
		// not implemented
		
	}
	
	protected boolean isB2BOrder(final CartModel cartModel)
	{
		return cartModel.getSite().getChannel() == SiteChannel.B2B;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public SalesOrderSimulationService getSalesOrderSimulationService() {
		return salesOrderSimulationService;
	}

	public void setSalesOrderSimulationService(SalesOrderSimulationService salesOrderSimulationService) {
		this.salesOrderSimulationService = salesOrderSimulationService;
	}

	public SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}
	
	

}

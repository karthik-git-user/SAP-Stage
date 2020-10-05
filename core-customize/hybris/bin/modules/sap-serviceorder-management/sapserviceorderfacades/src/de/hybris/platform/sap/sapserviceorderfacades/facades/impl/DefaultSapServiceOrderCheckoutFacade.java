/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorderfacades.facades.impl;

import java.util.Calendar;
import java.util.Date;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapserviceorder.constants.SapserviceorderConstants;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundBuilderService;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundService;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.sap.sapserviceorderfacades.facades.SapServiceOrderCheckoutFacade;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;


/**
 * Facade implementation for Service order checkout
 */
public class DefaultSapServiceOrderCheckoutFacade implements SapServiceOrderCheckoutFacade
{


	private BaseStoreService baseStoreService;
	private ModelService modelService;
	private CartService cartService;
	private CustomerAccountService customerAccountService;
	private UserService userService;
	private SapCpiServiceOrderOutboundBuilderService<ConsignmentModel,SAPCpiOutboundServiceOrderModel> sapCpiServiceOrderUpdateOutboundBuilderService;
	private SapCpiServiceOrderOutboundService sapCpiServiceOrderOutboundService;

	
	@Override
	public Integer getLeadDaysForService()
	{

		final BaseStoreModel store = getCartService().getSessionCart().getStore();
		final BaseStoreModel baseStore = getBaseStoreService().getBaseStoreForUid(store.getUid());

		return baseStore.getSAPConfiguration() != null && baseStore.getSAPConfiguration().getLeadDays() != null
				? baseStore.getSAPConfiguration().getLeadDays()
				: Integer.parseInt(Config.getParameter(SapserviceorderConstants.DEFAULT_LEAD_DAYS));
	}



	@Override
	public void updateCartWithServiceScheduleDate(final Date date)
	{

		final CartModel cart = getCartService().getSessionCart();
		cart.setRequestedServiceStartDate(date);
		getModelService().save(cart);

	}

	@Override
	public Boolean containsServiceProductInCart()
	{

		final CartModel cart = getCartService().getSessionCart();
		Boolean containsServiceProducts = false;
		if (cart != null && cart.getEntries() != null && !cart.getEntries().isEmpty())
		{
			containsServiceProducts = cart.getEntries().stream().anyMatch(SapServiceOrderUtil::isServiceEntry);
		}
		return containsServiceProducts;
	}
	
	@Override
	public boolean rescheduleServiceRequestDate(final String orderCode, final Date date)
	{

		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode((CustomerModel) getUserService().getCurrentUser(), orderCode,
				baseStoreModel);
		orderModel.setRequestedServiceStartDate(date);
		getModelService().save(orderModel);
		ConsignmentModel consignment = orderModel.getConsignments().stream().filter(cons -> cons.getSapOrder()!=null && SAPOrderType.SERVICE.equals(cons.getSapOrder().getSapOrderType())).findFirst().get();
		final SAPCpiOutboundServiceOrderModel ouboundOrder = getSapCpiServiceOrderUpdateOutboundBuilderService().build(consignment);
		
		return sapCpiServiceOrderOutboundService.sendServiceOrderUpdate(ouboundOrder);
	}


	@Override
	public Date getRequestedServiceDate()
	{

		final CartModel cart = getCartService().getSessionCart();
		return cart.getRequestedServiceStartDate();

	}

	@Override
	public Date getServiceLeadDate()
	{

		final Integer leadDays = getLeadDaysForService();
		final Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.DATE, leadDays);
		return c.getTime();
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}



	public CartService getCartService()
	{
		return cartService;
	}



	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}



	public ModelService getModelService()
	{
		return modelService;
	}



	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}



	public CustomerAccountService getCustomerAccountService() {
		return customerAccountService;
	}



	public void setCustomerAccountService(CustomerAccountService customerAccountService) {
		this.customerAccountService = customerAccountService;
	}



	public UserService getUserService() {
		return userService;
	}



	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public SapCpiServiceOrderOutboundService getSapCpiServiceOrderOutboundService() {
		return sapCpiServiceOrderOutboundService;
	}



	public void setSapCpiServiceOrderOutboundService(SapCpiServiceOrderOutboundService sapCpiServiceOrderOutboundService) {
		this.sapCpiServiceOrderOutboundService = sapCpiServiceOrderOutboundService;
	}



	public SapCpiServiceOrderOutboundBuilderService<ConsignmentModel,SAPCpiOutboundServiceOrderModel> getSapCpiServiceOrderUpdateOutboundBuilderService() {
		return sapCpiServiceOrderUpdateOutboundBuilderService;
	}



	public void setSapCpiServiceOrderUpdateOutboundBuilderService(
			SapCpiServiceOrderOutboundBuilderService<ConsignmentModel,SAPCpiOutboundServiceOrderModel> sapCpiServiceOrderUpdateOutboundBuilderService) {
		this.sapCpiServiceOrderUpdateOutboundBuilderService = sapCpiServiceOrderUpdateOutboundBuilderService;
	}


}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.orderexchange.constants.PartnerRoles;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundPartnerRoleModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundPriceComponentModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderItemModel;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Provides mapping from {@link OrderModel} to {@link SAPCpiOutboundOrderModel}.
 *
 * @param <SOURCE> the source object should of type OrderModel
 * @param <TARGET> the target object should of type SAPCpiOutboundOrderModel
 */
public class DefaultSapCpiServiceOrderMapperService implements SapCpiOrderMapperService<OrderModel, SAPCpiOutboundServiceOrderModel> {

	private static final String REQUEST_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static final String CREATION_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private SAPConfigurationModel sapConfig;
	private ModelService modelService;
	
	/**
	   * Performs mapping from source to target.
	   *
	   * @param source Order Model
	   * @param target SAP CPI Outbound Order Model
	   */
	@Override
	public void map(OrderModel orderModel, SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		
		sapConfig = orderModel.getStore().getSAPConfiguration();
		mapServiceOrder(orderModel, sapCpiOutboundServiceOrderModel);
	}

	protected void mapServiceOrder(OrderModel orderModel, SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		
		sapCpiOutboundServiceOrderModel.setTransactionType(sapConfig.getServiceOrderTransactionType());
		
		if(orderModel.getRequestedServiceStartDate() != null) {
			DateFormat dateFormat = new SimpleDateFormat(REQUEST_DATE_FORMAT);
			String requestedDate = dateFormat.format(orderModel.getRequestedServiceStartDate());
			sapCpiOutboundServiceOrderModel.setRequestedServiceStartDateTime(requestedDate);
		}
		
		String orderCreationDate = new SimpleDateFormat(CREATION_DATE_FORMAT).format(orderModel.getCreationtime());
		sapCpiOutboundServiceOrderModel.setCreationDate(orderCreationDate);
		
		sapCpiOutboundServiceOrderModel.setCommerceOrderId(Optional.ofNullable(orderModel.getConsignments()).map(Collection::stream)
				.orElseGet(Stream::empty).findFirst().get().getOrder().getCode());
		
		
		mapPartnerRoles(orderModel, sapCpiOutboundServiceOrderModel);
		mapOrderItem(orderModel, sapCpiOutboundServiceOrderModel);
	}

	protected void mapPartnerRoles(OrderModel orderModel, SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		
		Optional.ofNullable(sapCpiOutboundServiceOrderModel.getSapCpiOutboundPartnerRoles()).map(Collection::stream).orElseGet(Stream::empty).forEach(partnerRole -> {
			if(isContactPerson(partnerRole))
			{
				B2BCustomerModel b2bContact = (B2BCustomerModel) orderModel.getUser();
				partnerRole.setPartnerId(b2bContact.getSapBusinessPartnerID());
			}
		});	
	}
	
	protected boolean isContactPerson(SAPCpiOutboundPartnerRoleModel partnerRole) {
		
		return PartnerRoles.CONTACT.getCode().equals(partnerRole.getPartnerRoleCode());
	}
	
	protected void mapOrderItem(OrderModel orderModel, SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		
		initializeSapCpiOutboundServiceOrderItems(sapCpiOutboundServiceOrderModel);
		mapOrderItemDescription(orderModel,sapCpiOutboundServiceOrderModel);
		mapOrderItemPrices(sapCpiOutboundServiceOrderModel);
	}

	protected void initializeSapCpiOutboundServiceOrderItems(
			SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		sapCpiOutboundServiceOrderModel.setSapCpiOutboundServiceOrderItems(new HashSet<>());
		
		sapCpiOutboundServiceOrderModel.getSapCpiOutboundOrderItems().stream().forEach(item -> {
			SAPCpiOutboundServiceOrderItemModel serviceItem = getModelService().create(SAPCpiOutboundServiceOrderItemModel.class);
			serviceItem.setUnit(item.getUnit());
			serviceItem.setEntryNumber(String.valueOf(Integer.valueOf(item.getEntryNumber()) + 1));
			serviceItem.setProductCode(item.getProductCode());
			serviceItem.setQuantity(item.getQuantity());
			
			sapCpiOutboundServiceOrderModel.getSapCpiOutboundServiceOrderItems().add(serviceItem);
		});
		
	}

	/**
	 * Maps order item description
	 * @param orderModel order object
	 * @param sapCpiOutboundServiceOrderModel outbound order object
	 */
	protected void mapOrderItemDescription(OrderModel orderModel,
			SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		
		Map<String, SAPCpiOutboundServiceOrderItemModel> items = getServiceOrderItemsAsMap(sapCpiOutboundServiceOrderModel);
		
		orderModel.getEntries().stream().forEach(entry -> {
			SAPCpiOutboundServiceOrderItemModel outboundItem = items.get(String.valueOf(entry.getEntryNumber() + 1));
			outboundItem.setDescription(entry.getProduct().getName()); 
		});
		
	}
	
	/**
	 * Skills mapping
	 * @deprecated handled skills in different way
	 * @param orderModel order
	 * @param sapCpiOutboundServiceOrderModel outbound order
	 */
	@Deprecated(since = "2003", forRemoval = true)
	protected void mapOrderItemSkills(OrderModel orderModel, SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {

		Map<String, SAPCpiOutboundServiceOrderItemModel> items = getServiceOrderItemsAsMap(sapCpiOutboundServiceOrderModel);
		orderModel.getEntries().stream().forEach(entry -> { 
			SAPCpiOutboundServiceOrderItemModel outboundItem = items.get(entry.getEntryNumber().toString());
			outboundItem.setSkills(entry.getProduct().getSkillsDescription());
		});
		
		Optional<AbstractOrderEntryModel> orderEntry = orderModel.getEntries().stream().filter(oe -> oe.getProduct().getSkillSummary() != null && oe.getProduct().getSkillSummary().length() <= 40).findFirst();
		if(orderEntry.isPresent()){
			String skillSummary = orderEntry.get().getProduct().getSkillSummary();
			sapCpiOutboundServiceOrderModel.setSkillSummary(skillSummary);
		}
		
	}


	protected void mapOrderItemPrices(SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		final String itemPriceConditionCode = sapConfig.getSaporderexchange_itemPriceConditionType();
		final String serviceItemPriceConditionType = sapConfig.getServiceItemPriceConditionCode();
		
		initializeServiceOrderItemsPriceComponents(sapCpiOutboundServiceOrderModel);
		Map<String, SAPCpiOutboundServiceOrderItemModel> items = getServiceOrderItemsAsMap(sapCpiOutboundServiceOrderModel);
		
		Optional.ofNullable(sapCpiOutboundServiceOrderModel.getSapCpiOutboundPriceComponents()).map(Collection::stream)
				.orElseGet(Stream::empty).forEach(priceComponent -> 
				{
					priceComponent.setEntryNumber(String.valueOf(Integer.valueOf(priceComponent.getEntryNumber()) + 1));
					if (isItemPriceCondition(priceComponent, itemPriceConditionCode))
					{
						priceComponent.setConditionCode(serviceItemPriceConditionType);
						priceComponent.setValue(getComputedPriceValue(items, priceComponent));
						setItemPrice(items, priceComponent);
					}
				});
		sapCpiOutboundServiceOrderModel.getSapCpiOutboundPriceComponents().clear();
	}
	protected void initializeServiceOrderItemsPriceComponents(SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		Set<SAPCpiOutboundServiceOrderItemModel> itemsSet = sapCpiOutboundServiceOrderModel.getSapCpiOutboundServiceOrderItems();
		
		Optional.ofNullable(itemsSet).map(Collection::stream).orElseGet(Stream::empty).forEach(item -> 
			item.setSapCpiOutboundPriceComponents(new HashSet<SAPCpiOutboundPriceComponentModel>())
		);
		
	}

	//enable access to the order items with entryNumber as key
	protected Map<String, SAPCpiOutboundServiceOrderItemModel> getServiceOrderItemsAsMap(SAPCpiOutboundServiceOrderModel sapCpiOutboundServiceOrderModel) {
		Set<SAPCpiOutboundServiceOrderItemModel> itemsSet = sapCpiOutboundServiceOrderModel.getSapCpiOutboundServiceOrderItems();
		
		Map<String, SAPCpiOutboundServiceOrderItemModel> items = new HashMap<>();
		Optional.ofNullable(itemsSet).map(Collection::stream)
		.orElseGet(Stream::empty).forEach(item -> items.put(item.getEntryNumber() , item));
		return items;
	}

	protected boolean isItemPriceCondition(SAPCpiOutboundPriceComponentModel priceComponent,
			final String itemPriceConditionCode) {
		return (!"-1".equals(priceComponent.getEntryNumber())) && itemPriceConditionCode.equals(priceComponent.getConditionCode());
	}
	
	protected String getComputedPriceValue(Map<String, SAPCpiOutboundServiceOrderItemModel> items,
			SAPCpiOutboundPriceComponentModel priceComponent) {
		String entryNumber = priceComponent.getEntryNumber();
		double quantity = Double.parseDouble(items.get(entryNumber).getQuantity());
		double value = Double.parseDouble(priceComponent.getValue());
		
		return String.valueOf(quantity * value);
	}

	protected void setItemPrice(Map<String, SAPCpiOutboundServiceOrderItemModel> items,
			SAPCpiOutboundPriceComponentModel priceComponent) {
		String entryNumber = priceComponent.getEntryNumber();
		items.get(entryNumber).getSapCpiOutboundPriceComponents().add(priceComponent);
	}

	public ModelService getModelService() {
		return modelService;
	}

	public void setModelService(ModelService modelService) {
		this.modelService = modelService;
	}
}

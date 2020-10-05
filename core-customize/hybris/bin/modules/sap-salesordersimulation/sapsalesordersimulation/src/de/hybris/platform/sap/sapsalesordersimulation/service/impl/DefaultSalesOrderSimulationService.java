/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.b2b.model.FutureStockModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.core.sapsalesordersimulation.enums.ProceduresSubtotal;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.sap.sapmodel.services.impl.SAPDefaultUnitService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationOutboundRequest;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;
import de.hybris.platform.sapsalesordersimulation.dto.CreditData;
import de.hybris.platform.sapsalesordersimulation.dto.PricingData;
import de.hybris.platform.sapsalesordersimulation.dto.PricingElementData;
import de.hybris.platform.sapsalesordersimulation.dto.PricingElementsData;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulateItemData;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulateItemRequestData;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulatesItemsData;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulationData;
import de.hybris.platform.sapsalesordersimulation.dto.SalesOrderSimulationRequestData;
import de.hybris.platform.sapsalesordersimulation.dto.ScheduleLineData;
import de.hybris.platform.sapsalesordersimulation.dto.ScheduleLinesData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

/**
 * Sales order simulation API to fetch Price, Stock and credit check 
 */
public class DefaultSalesOrderSimulationService implements SalesOrderSimulationService {
	private static final int ORDER_ENTRY_MULTIPLIER = 10;
	private static final String SECOND_SCHEDULE_LINE_NO = "2";
	private static final String FIRST_SCHEDULE_LINE_NO = "1";
	private static final String QUANTITY = "1";
	private static final String BULK_QUANTITY = "9999999";
	private static final String DEFAULT_ITEM_NUMBER = "10";
	private static final String DEFAULT_PLANT = "defaultPlant";
	private static final String PRICE_INFO_MAP = "priceInfoMap";
	private static final String SAP_PRICING_CONDTIONS_MAP = "sapPricingCondtionsMap";
	private static final String TAX_MAP = "taxMap";
	private static final String DISCOUNT_MAP = "discountMap";
	private static final String STOCK_INFO_MAP = "stockInfoMap";
	private static final String ERROR_MSG = "Error parsing response from sales order simulate api";
	private static final String ORDER = "OR";
	private static final String DEFUALT_CURRENCY = "USD";
	private static final String SIMULATED_VIA_O_DATA_API = "Simulated via OData API";
	
	public static final String NETAMOUNT = "NetAmount";
	public static final String TAXAMOUNT = "TaxAmount";
	public static final String COSTAMOUNT = "CostAmount";
	public static final String SUBTOTAL1AMOUNT = "Subtotal1Amount";
	public static final String SUBTOTAL2AMOUNT = "Subtotal2Amount";
	public static final String SUBTOTAL3AMOUNT = "Subtotal3Amount";
	public static final String SUBTOTAL4AMOUNT = "Subtotal4Amount";
	public static final String SUBTOTAL5AMOUNT = "Subtotal5Amount";
	public static final String SUBTOTAL6AMOUNT = "Subtotal6Amount";
	
	public static final String CONF_PROP_PRICE_SUBTOTAL = "salesordersimulate_pricesub";
	public static final String CONF_PROP_DISCOUNTS_SUBTOTAL = "salesordersimulate_discountsub";
	public static final String CONF_PROP_TAXES_SUBTOTAL = "salesordersimulate_taxessub";
	public static final String CONF_PROP_DELIVERY_SUBTOTAL = "salesordersimulate_deliverysub";
	public static final String CONF_PROP_PAYMENT_COST_SUBTOTAL = "salesordersimulate_paymentsub";
	

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSalesOrderSimulationService.class);
	private SalesOrderSimulationOutboundRequest salesOrderSimulationOutboundRequest;
	private DeliveryService deliveryService;
	private UserService userService;
	private B2BUnitService b2bUnitService;
	private BaseStoreService baseStoreService;
	private CommonI18NService commonI18NService;
	private ModuleConfigurationAccess moduleConfigurationAccess;
	private SAPDefaultUnitService sapUnitService;
	private PriceDataFactory priceDataFactory;
	private SalesOrderSimulationUtil salesordersimulationUtil;
	
	@Override
	public List<PriceInformation> getPriceDetailsForProduct(ProductModel productModel) {
		
		ObjectMapper mapper = new ObjectMapper();
		boolean isCreditCheckRequired = false;
		SalesOrderSimulationRequestData requestData = getPriceStockRequestData(productModel, isCreditCheckRequired);
		requestData.setItems(getItemLevelDetails(productModel));
		try {

			String response = getSalesOrderSimulationOutboundRequest()
					.getResponseFromSalesOrderSimulate(mapper.writeValueAsString(requestData));
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			SalesOrderSimulationData salesOrderSimulationResponse = mapper.readValue(response,
					SalesOrderSimulationData.class);
			final Map<String, Object> simulationDetailsMap = processResponseData(salesOrderSimulationResponse, productModel);
			return ((Map<String, List<PriceInformation>>)simulationDetailsMap.get(PRICE_INFO_MAP)).get(productModel.getCode());
		} catch (IOException e) {
			LOG.error(ERROR_MSG,e);
		}
		return new ArrayList<>();
	}

	protected List<SalesOrderSimulateItemRequestData> getItemLevelDetails(ItemModel itemModel) {
		List<SalesOrderSimulateItemRequestData> items = new ArrayList<>();
		if (itemModel instanceof ProductModel) {
			SalesOrderSimulateItemRequestData salesOrderSimulateItemData = getSalesOrderSimulateItemRequestData("",
					DEFAULT_ITEM_NUMBER, ((ProductModel) itemModel).getCode(), QUANTITY);
			items.add(salesOrderSimulateItemData);
		} else if (itemModel instanceof AbstractOrderModel) {
			AbstractOrderModel orderModel = (AbstractOrderModel) itemModel;
			for (AbstractOrderEntryModel abstractOderEntryModel : orderModel.getEntries()) {
				SalesOrderSimulateItemRequestData orderItemData = getSalesOrderSimulateItemRequestData(
						orderModel.getCode(), (abstractOderEntryModel.getEntryNumber() + 1) * ORDER_ENTRY_MULTIPLIER + "",
						abstractOderEntryModel.getProduct().getCode(), abstractOderEntryModel.getQuantity() + "");
				orderItemData.setPricingElements(new ArrayList<>());
				items.add(orderItemData);
			}
		}
		return items;

	}
	
	protected List<SalesOrderSimulateItemRequestData> getItemLevelDetailsWithBulkQuantity(ItemModel itemModel) {		
		List<SalesOrderSimulateItemRequestData> items = new ArrayList<>();		
		if (itemModel instanceof ProductModel) {		
			SalesOrderSimulateItemRequestData salesOrderSimulateItemData = getSalesOrderSimulateItemRequestData("",		
					DEFAULT_ITEM_NUMBER, ((ProductModel) itemModel).getCode(), BULK_QUANTITY);		
			items.add(salesOrderSimulateItemData);		
		} 		
		return items;		
	}

	protected SalesOrderSimulateItemRequestData getSalesOrderSimulateItemRequestData(String salesOrder, String itemNumber,
			String material, String qty) {
		SalesOrderSimulateItemRequestData salesOrderSimulateItemData = new SalesOrderSimulateItemRequestData();
		salesOrderSimulateItemData.setSalesOrder(salesOrder);
		salesOrderSimulateItemData.setSalesOrderItem(itemNumber);
		salesOrderSimulateItemData.setMaterial(material);
		salesOrderSimulateItemData.setRequestedQuantity(qty);
		salesOrderSimulateItemData.setPricingElements(new ArrayList<>());
		salesOrderSimulateItemData.setScheduleLines(new ArrayList<>());
		return salesOrderSimulateItemData;
	}

	

	protected SalesOrderSimulationRequestData getPriceStockRequestData(ItemModel itemModel,
			boolean isCreditCheckRequired) {

		String salesOrg = null;
		String distributionChannel = null;
		String division = null;
		String orderType =null;
		BaseStoreModel baseStore = getBaseStore(itemModel);
		if (baseStore != null && baseStore.getSAPConfiguration() != null) {
			orderType = baseStore.getSAPConfiguration().getSapcommon_transactionType();
			salesOrg = baseStore.getSAPConfiguration().getSapcommon_salesOrganization();
			distributionChannel = baseStore.getSAPConfiguration().getSapcommon_distributionChannel();
			division = baseStore.getSAPConfiguration().getSapcommon_division();
		}
		SalesOrderSimulationRequestData requestData=	getSalesOrderSimulationRequestData(getCurrency(itemModel), isCreditCheckRequired, salesOrg,
				distributionChannel, division,getUserModel(itemModel),orderType );
		requestData.setPricingElements(getHeaderLevelPricingElementData(itemModel));
		return requestData;

	}

	protected SalesOrderSimulationRequestData getSalesOrderSimulationRequestData(String currency,
			boolean isCreditCheckRequired, String salesOrg, String distributionChannel, String division,UserModel userModel,String orderType) {

		SalesOrderSimulationRequestData requestData = new SalesOrderSimulationRequestData();
		final String soldToParty = getSalesordersimulationUtil().getSoldToParty(userModel);
		
		requestData.setSoldToParty(soldToParty);
		requestData.setSalesOrderType((orderType!=null)?orderType:ORDER);

		requestData.setSalesOrganization(salesOrg);
		requestData.setDistributionChannel(distributionChannel);
		requestData.setDivision(division);
		requestData.setPurchaseOrderByCustomer(SIMULATED_VIA_O_DATA_API);

		requestData.setPricing(new PricingData());
		requestData.setCurrency(currency);
		if (isCreditCheckRequired) {
			requestData.setCreditDetails(new CreditData());
		}
		return requestData;

	}

	protected List<SalesOrderSimulationRequestData> getStockRequestData(ItemModel itemModel,
			Collection<WarehouseModel> warehouses) {

		List<SalesOrderSimulationRequestData> salesOrderSimulationRequestData = new ArrayList<>();
		Set<SAPPlantLogSysOrgModel> sapPlantLogSysOrgs = null;
		final String orderType = getBaseStore(itemModel).getSAPConfiguration().getSapcommon_transactionType();
		if (itemModel instanceof AbstractOrderModel) {
			sapPlantLogSysOrgs = ((AbstractOrderModel) itemModel).getStore().getSAPConfiguration()
					.getSapPlantLogSysOrg();
		} else {

			sapPlantLogSysOrgs = getBaseStore(itemModel).getSAPConfiguration().getSapPlantLogSysOrg();
		}
		for (SAPPlantLogSysOrgModel sapPlantDetails : sapPlantLogSysOrgs) {
			if (isValidPlant(warehouses, sapPlantDetails)) {
				String plantSalesOrg = sapPlantDetails.getSalesOrg().getSalesOrganization();
				String plantDistributionChannel = sapPlantDetails.getSalesOrg().getDistributionChannel();
				String plantDivision = sapPlantDetails.getSalesOrg().getDivision();
				
				SalesOrderSimulationRequestData requestData = getSalesOrderSimulationRequestData(getCurrency(itemModel),
						false, plantSalesOrg, plantDistributionChannel, plantDivision,null,orderType);
				List<SalesOrderSimulateItemRequestData> items = getItemLevelDetailsWithBulkQuantity(itemModel);
				requestData.setItems(items);
				salesOrderSimulationRequestData.add(requestData);
			}
		}
		return salesOrderSimulationRequestData;

	}

	private boolean isValidPlant(Collection<WarehouseModel> warehouses, SAPPlantLogSysOrgModel sapPlantDetails) {
		boolean vaildPlant = true;

		if (sapPlantDetails == null || sapPlantDetails.getPlant() == null) {
			vaildPlant = false;
		}

		if (warehouses != null && !warehouses.isEmpty()
				&& warehouses.stream().noneMatch(o -> o.getCode().equals(sapPlantDetails.getPlant().getCode()))) {
			vaildPlant = false;
		}
		return vaildPlant;
	}

	private String getCurrency(ItemModel itemModel) {
		String currency = null;
		if (itemModel instanceof AbstractOrderModel) {
			currency = ((AbstractOrderModel) itemModel).getCurrency().getSapCode();
		} else {
			currency = getCommonI18NService().getCurrentCurrency().getSapCode();
		}
		currency = (currency != null) ? currency : DEFUALT_CURRENCY;
		return currency;
	}
	private UserModel getUserModel(ItemModel itemModel) {

		if (itemModel instanceof AbstractOrderModel) {
			return ((AbstractOrderModel) itemModel).getUser();
			
		}  else {
			return getUserService().getCurrentUser();
		}
	
	}

	private BaseStoreModel getBaseStore(ItemModel itemModel) {
		BaseStoreModel currentBaseStore = baseStore(itemModel);
		return currentBaseStore;
	}

	protected List<PricingElementData> getHeaderLevelPricingElementData(ItemModel itemModel) {
		List<PricingElementData> pricingElements = null;
		if (itemModel instanceof AbstractOrderModel && ((AbstractOrderModel) itemModel).getDeliveryMode() != null) {
			pricingElements = new ArrayList<>();
			PricingElementData pricingElementData = deliveryPricingCondition(itemModel);
			if (pricingElementData != null) {
				pricingElements.add(pricingElementData);
			}

		}
		return pricingElements;
	}

	protected PricingElementData deliveryPricingCondition(ItemModel itemModel) {
		DeliveryModeModel deliveryModel = ((AbstractOrderModel) itemModel).getDeliveryMode();
		String shippingConditionCode = ((AbstractOrderModel) itemModel).getStore().getSAPConfiguration().getSaporderexchange_deliveryCostConditionType();
		if (StringUtils.isNotEmpty(shippingConditionCode)) {
			PricingElementData pricingElementData = new PricingElementData();
			final PriceValue deliveryCost = getDeliveryService().getDeliveryCostForDeliveryModeAndAbstractOrder(
					deliveryModel, (AbstractOrderModel) itemModel);
			if(deliveryCost!=null) {
				pricingElementData.setConditionType(shippingConditionCode);
				pricingElementData.setConditionRateValue(deliveryCost.getValue() + "");
			}
			return pricingElementData;
		}
		return new PricingElementData();
		
	}

	

	@Override
	public Map<String, List<PriceInformation>> getPriceDetailsForProducts(List<ProductModel> productModels) {
		SalesOrderSimulationRequestData requestData = getPriceStockRequestData(productModels.iterator().next(), false);
		List<SalesOrderSimulateItemRequestData> items = new ArrayList<>();
		int salesOrderItemNumber = 1;
		for (ProductModel productModel : productModels) {
			SalesOrderSimulateItemRequestData salesOrderSimulateItemData = getSalesOrderSimulateItemRequestData("",
					(salesOrderItemNumber++) * ORDER_ENTRY_MULTIPLIER + "", productModel.getCode(), QUANTITY);
			items.add(salesOrderSimulateItemData);
		}
		requestData.setItems(items);
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		String response;
		SalesOrderSimulationData salesOrderSimulationResponse = null;
		try {
			response = getSalesOrderSimulationOutboundRequest()
					.getResponseFromSalesOrderSimulate(mapper.writeValueAsString(requestData));
			salesOrderSimulationResponse = mapper.readValue(response, SalesOrderSimulationData.class);
		} catch (IOException e) {
			LOG.error(ERROR_MSG,e);
		}

		final Map<String, Object> stockPriceMap = processResponseData(salesOrderSimulationResponse, null);

		return (Map<String, List<PriceInformation>>) stockPriceMap.get(PRICE_INFO_MAP);
	}

	@Override
	public void setCartDetails(AbstractOrderModel cartModel) { 
		if(cartModel.getEntries().isEmpty()) {
			return;
		}
		boolean isCreditCheckRequired = false;
		SalesOrderSimulationRequestData requestData = getPriceStockRequestData(cartModel, isCreditCheckRequired);
		List<SalesOrderSimulateItemRequestData> items = getItemLevelDetails(cartModel);
		requestData.setItems(items);
		try {
			ObjectMapper mapper = new ObjectMapper();
			String response = getSalesOrderSimulationOutboundRequest()
					.getResponseFromSalesOrderSimulate(mapper.writeValueAsString(requestData));
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			SalesOrderSimulationData salesOrderSimulationResponse = mapper.readValue(response,
					SalesOrderSimulationData.class);
			processResponseData(salesOrderSimulationResponse, cartModel);

		} catch (IOException e) {
			LOG.error(ERROR_MSG,e);
		}
	}

	@Override
	public Boolean checkCreditLimitExceeded(ItemModel cartModel, UserModel user) {
		boolean isCreditCheckRequired = true;
		SalesOrderSimulationRequestData requestData = getPriceStockRequestData(cartModel, isCreditCheckRequired);
		List<SalesOrderSimulateItemRequestData> items = getItemLevelDetails(cartModel);
		requestData.setItems(items);
		try {
			ObjectMapper mapper = new ObjectMapper();
			String response = getSalesOrderSimulationOutboundRequest()
					.getResponseFromSalesOrderSimulate(mapper.writeValueAsString(requestData));
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			SalesOrderSimulationData salesOrderSimulationResponse = mapper.readValue(response,
					SalesOrderSimulationData.class);
			if (salesOrderSimulationResponse.getResult().getCreditDetails() != null) {
				String creditStatus = salesOrderSimulationResponse.getResult().getCreditDetails()
						.getCreditCheckStatus();
				return "B".equalsIgnoreCase(creditStatus);
				
			}

		} catch (IOException e) {
			LOG.error(ERROR_MSG,e);
		}
		return false;
	}

	protected Map<String, Object> processResponseData(SalesOrderSimulationData salesOrderSimulationData,
			ItemModel itemModel) {
		if (salesOrderSimulationData == null || salesOrderSimulationData.getResult() == null) {
			return new HashMap<>();
			
		}
		Map<String, Object> itemInfoMap = new HashMap<>();
		Map<String, List<PriceInformation>> productPriceMap = new HashMap<>();
		Map<String, List<StockLevelModel>> stockLevelMap = new HashMap<>();
		Map<String, DiscountValue> discountMap = new HashMap<>();
		Map<String, TaxValue> taxMap = new HashMap<>();
		Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap = new HashMap<>();

		itemInfoMap.put(PRICE_INFO_MAP, productPriceMap);
		itemInfoMap.put(STOCK_INFO_MAP, stockLevelMap);
		itemInfoMap.put(DISCOUNT_MAP, discountMap);
		itemInfoMap.put(TAX_MAP, taxMap);
		itemInfoMap.put(SAP_PRICING_CONDTIONS_MAP, sapPricingCondtionsMap);

		final String salesOrg = salesOrderSimulationData.getResult().getSalesOrganization();
		final String distrChannel = salesOrderSimulationData.getResult().getDistributionChannel();
		final String division = salesOrderSimulationData.getResult().getDivision();

		try {
			double deliveryCost = 0;
			double paymentCost = 0;
			List<SalesOrderSimulateItemData> items = null;
			SalesOrderSimulatesItemsData itemsData = salesOrderSimulationData.getResult().getItems();
			if (itemsData != null) {
				items = itemsData.getSalesOrderItems();
				for (int i = 0; i < items.size(); i++) {
					SalesOrderSimulateItemData itemData = items.get(i);
					setUnitForProduct(itemModel, itemData);
					setItemDetails(itemInfoMap, itemData, salesOrg, distrChannel, division,itemModel );
					deliveryCost += Double.parseDouble(getDeliverySubtotal(itemData,itemModel));
					paymentCost += Double.parseDouble(getPaymentCost(itemData,itemModel));
				}
			}
			if (itemModel instanceof AbstractOrderModel) {
				setOrderModel(itemModel,itemInfoMap,deliveryCost,paymentCost);
			} 
		} catch (Exception e) {
			LOG.error("Unable to get the Stock and price details...",e);
			return itemInfoMap;
		}
		return itemInfoMap;

	}

	private void setUnitForProduct(ItemModel itemModel, SalesOrderSimulateItemData item) {
		if (itemModel instanceof ProductModel) {
			((ProductModel) itemModel).setUnit(getSapUnitService().getUnitForSAPCode(item.getRequestedQuantityUnit()));
		}
	}

	

	private void setItemDetails(Map<String, Object> itemInfoMap, SalesOrderSimulateItemData item, String salesOrg,
			String distrChannel, String division,ItemModel itemModel) {
		Map<String, List<PriceInformation>> productPriceMap = (Map<String, List<PriceInformation>>) itemInfoMap
				.get(PRICE_INFO_MAP);
		Map<String, List<StockLevelModel>> stockLevelMap = (Map<String, List<StockLevelModel>>) itemInfoMap
				.get(STOCK_INFO_MAP);
		Map<String, DiscountValue> discountMap = (Map<String, DiscountValue>) itemInfoMap.get(DISCOUNT_MAP);
		Map<String, TaxValue> taxMap = (Map<String, TaxValue>) itemInfoMap.get(TAX_MAP);
		Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap = (Map<String, List<SAPPricingConditionModel>>) itemInfoMap
				.get(SAP_PRICING_CONDTIONS_MAP);
		List<PriceInformation> priceList = new ArrayList<>();
		String material = item.getMaterial();
		String salesOrderItemNumber = item.getSalesOrderItem();
		String currency = item.getTransactionCurrency();
		String requestedQuantity = item.getRequestedQuantity();
		Double quantity = Double.valueOf(requestedQuantity);

		String taxAmount = getTaxAmount(item,itemModel);

		setPriceDetails(productPriceMap, priceList, material, currency, quantity, item,itemModel);

		setStockDetails(stockLevelMap, item.getScheduleLines(), material, salesOrg, distrChannel, division);
		if (itemModel instanceof AbstractOrderModel) {
			setPricingConditions(sapPricingCondtionsMap, item.getPricingElements(), salesOrderItemNumber,itemModel);
		}
		setDiscounts(discountMap, salesOrderItemNumber, currency,  item,quantity,itemModel);

		setTaxValues(taxMap, salesOrderItemNumber, currency, quantity, taxAmount);
	}

	private void setTaxValues(Map<String, TaxValue> taxMap, String salesOrderItemNumber, String currency,
			Double quantity, String taxAmount) {
		Double tax = Double.parseDouble(taxAmount) / quantity;
		final TaxValue taxValue = new TaxValue(generateCode("DISC", salesOrderItemNumber, ""), tax, true, tax,
				currency);
		taxMap.put(salesOrderItemNumber, taxValue);
	}

	

	private void setPriceDetails(Map<String, List<PriceInformation>> productPriceMap, List<PriceInformation> priceList,
			String material, String currency, Double quantity, SalesOrderSimulateItemData item,ItemModel itemModel) {
		String subTotal1 = getSubTotal(item,itemModel);
		priceList.add(new PriceInformation(new PriceValue(currency, Double.valueOf(subTotal1) / quantity, true)));
		productPriceMap.put(material, priceList);
	}

	

	private void setStockDetails(Map<String, List<StockLevelModel>> stockLevelMap, ScheduleLinesData shceduleLinesData,
			String material, String salesOrg, String distrChannel, String division) {
		if(shceduleLinesData==null || shceduleLinesData.getSalesOrderScheduleLines()==null) {
			return;
		}
		List<StockLevelModel> stockLevelModels = new ArrayList<>();
		int totalStock = 0;
		for (ScheduleLineData scheduleLineData : shceduleLinesData.getSalesOrderScheduleLines()) {

			if (FIRST_SCHEDULE_LINE_NO.equals(scheduleLineData.getScheduleLine())
					|| SECOND_SCHEDULE_LINE_NO.equals(scheduleLineData.getScheduleLine())) {
				totalStock += (int) Math.round(Double.parseDouble(scheduleLineData.getConfdOrderQtyByMatlAvailCheck()));
			}

		}
		StockLevelModel stockModel = new StockLevelModel();
		stockModel.setProductCode(material);
		
		
		if ((getCurrentAvailableStock(stockLevelMap.get(material))+totalStock)  > 0) {
			stockModel.setInStockStatus(InStockStatus.NOTSPECIFIED);
		} else {
			stockModel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		}
		stockModel.setAvailable(totalStock);
		stockModel.setWarehouse(getSapPlant(salesOrg, distrChannel, division));

		if (stockLevelMap.get(material) != null) {
			if(totalStock>0) {
				stockLevelMap.get(material).add(stockModel);
			}
		} else {
			stockLevelModels.add(stockModel);
			stockLevelMap.put(material, stockLevelModels);
		}

	}

	private int getCurrentAvailableStock(List<StockLevelModel> stockList) {
		int availableStocks=0;
		if(stockList!=null) {
			for (StockLevelModel stock : stockList) {
				availableStocks +=stock.getAvailable();
			}
		}
		return availableStocks;
	}

	private WarehouseModel getSapPlant(String salesOrg, String distrChannel, String division) {
		Set<SAPPlantLogSysOrgModel> sapPlantLogSysOrgs = baseStoreService.getCurrentBaseStore().getSAPConfiguration()
				.getSapPlantLogSysOrg();

		SAPPlantLogSysOrgModel sapPlant = sapPlantLogSysOrgs.stream()
				.filter(sapPlantDetail -> (salesOrg.equals(sapPlantDetail.getSalesOrg().getSalesOrganization())
						&& distrChannel.equals(sapPlantDetail.getSalesOrg().getDistributionChannel())
						&& division.equals(sapPlantDetail.getSalesOrg().getDivision())))
				.findAny().orElse(null);
		if (sapPlant != null) {
			return sapPlant.getPlant();
		}
		return null;
	}

	private void setDiscounts(Map<String, DiscountValue> discountMap, String salesOrderItemNumber, String currency,
			SalesOrderSimulateItemData item,Double quantity,ItemModel itemModel) {
		Double totalDiscounts = getDiscounts(item,itemModel);
		Double discount = totalDiscounts/quantity;
		final DiscountValue discountValue = new DiscountValue(generateCode("DISC", salesOrderItemNumber, ""), discount,
				true, discount, currency);
		discountMap.put(salesOrderItemNumber, discountValue);
	}

	

	protected String generateCode(String prefix, String entryNumber, String code) {
		return prefix + entryNumber + code;

	}

	protected void setPricingConditions(Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap,
			PricingElementsData pricingElementsData, String salesOrderItemNumber,ItemModel itemModel) {
		List<PricingElementData> pricingElements = pricingElementsData.getSalesOrderPricingElements();
		List<SAPPricingConditionModel> sapPricingConditions = new ArrayList<>();
		
		for (PricingElementData pricingElementData : pricingElements) {
			NumberFormat formatter = new DecimalFormat("#0.00");
			
			if (isPricingCoditionToBeConsidered(pricingElementData,itemModel)) {
				SAPPricingConditionModel sapPricingConditionModel = new SAPPricingConditionModel();
				sapPricingConditionModel.setStepNumber(pricingElementData.getPricingProcedureStep());
				sapPricingConditionModel.setConditionCounter(pricingElementData.getPricingProcedureCounter());
				sapPricingConditionModel.setConditionType(pricingElementData.getConditionType());
				sapPricingConditionModel.setCurrencyKey(pricingElementData.getTransactionCurrency());
				sapPricingConditionModel.setConditionPricingUnit(pricingElementData.getConditionQuantity());
				sapPricingConditionModel.setConditionUnit(pricingElementData.getConditionQuantityUnit());
				sapPricingConditionModel.setConditionRate(  formatter.format(Double.parseDouble (pricingElementData.getConditionRateValue())));
				sapPricingConditionModel.setConditionValue(formatter.format(Double.parseDouble (pricingElementData.getConditionAmount())));
				sapPricingConditionModel.setConditionCalculationType(pricingElementData.getConditionCalculationType());
				sapPricingConditions.add(sapPricingConditionModel);
		
			}
		}

		sapPricingCondtionsMap.put(salesOrderItemNumber, sapPricingConditions);
	}

	protected boolean isPricingCoditionToBeConsidered(PricingElementData pricingElement,ItemModel itemModel) {
		String pricingConditionCode = ((AbstractOrderModel) itemModel).getStore().getSAPConfiguration().getSaporderexchange_itemPriceConditionType();
		String conditionType = pricingElement.getConditionType();
		double conditionAmount = Double.parseDouble( pricingElement.getConditionAmount());
		return StringUtils.isNotEmpty(conditionType) && (pricingConditionCode.equals(conditionType)||conditionAmount<0) ;
	}

	protected StockData getStockData(List<StockLevelModel> stockLevelList) {
		StockData stockData = new StockData();

		Long totalStock = 0L;
		for (StockLevelModel stockLevelModel : stockLevelList) {
			totalStock += stockLevelModel.getAvailable();
		}
		if (totalStock > 0) {
			stockData.setStockLevel(totalStock);
			stockData.setStockLevelStatus(StockLevelStatus.INSTOCK);
		}
		return stockData;
	}


	protected void setOrderModel(ItemModel itemModel, Map<String, Object> itemInfoMap ,double deliveryCost, double paymentCost) {
		
		Map<String, List<PriceInformation>> productPriceMap =(Map<String, List<PriceInformation>> )itemInfoMap.get(PRICE_INFO_MAP);
		Map<String, DiscountValue> discountMap = (Map<String, DiscountValue>)itemInfoMap.get(DISCOUNT_MAP);
		Map<String, TaxValue> taxMap = (Map<String, TaxValue>)itemInfoMap.get(TAX_MAP);
		Map<String, List<SAPPricingConditionModel>> sapPricingCondtionsMap  = (Map<String, List<SAPPricingConditionModel>> )itemInfoMap.get(SAP_PRICING_CONDTIONS_MAP);
		Map<String, List<StockLevelModel>> stockLevelMap = (Map<String, List<StockLevelModel>>) itemInfoMap.get(STOCK_INFO_MAP);
		double tax = 0;
		double discount = 0;
		if (((AbstractOrderModel) itemModel).getDeliveryMode() != null) {
			((AbstractOrderModel) itemModel).setDeliveryCost(deliveryCost);
		}
		double totalOrderPrice = 0;
		for (AbstractOrderEntryModel orderEntry : ((AbstractOrderModel) itemModel).getEntries()) {
			final int orderEntryNumber = (orderEntry.getEntryNumber() + 1) * ORDER_ENTRY_MULTIPLIER;
				List<PriceInformation> priceInfoList = productPriceMap.get(orderEntry.getProduct().getCode());
				if (priceInfoList != null && !priceInfoList.isEmpty()) {
					PriceInformation priceInformation = priceInfoList.get(0);
					double price = priceInformation.getValue().getValue();
					orderEntry.setBasePrice(price);
					orderEntry.setTotalPrice(price * orderEntry.getQuantity());
					totalOrderPrice += price * orderEntry.getQuantity();
				}
				TaxValue taxValue = taxMap.get(orderEntryNumber + "");
				if (taxValue != null) {
					tax += taxValue.getValue();
				}
				orderEntry.setTaxValues(Arrays.asList(taxValue));
	
				DiscountValue discountValue = discountMap.get(orderEntryNumber + "");
				if (discountValue != null) {
					discount += discountValue.getValue();
				}
				orderEntry.setDiscountValues(Arrays.asList(discountValue));
				if (itemModel instanceof OrderModel && orderEntry.getSapPricingConditions().isEmpty()) {
					List<SAPPricingConditionModel> sapPricingConditions = sapPricingCondtionsMap.get(orderEntryNumber + "");
					for (SAPPricingConditionModel sapPricingCodition : sapPricingConditions) {
						sapPricingCodition.setOrderEntry(orderEntry);
						sapPricingCodition.setOrder(((AbstractOrderModel) itemModel).getCode());
						sapPricingCodition.setConditionUnit(orderEntry.getProduct().getUnit().getCode());
					}
					orderEntry.setSapPricingConditions(new HashSet<SAPPricingConditionModel>(sapPricingConditions));
				}
			
			orderEntry.getProduct()
					.setStockLevels(new HashSet<StockLevelModel>(stockLevelMap.get(orderEntry.getProduct().getCode())));
			
		}

		((AbstractOrderModel) itemModel).setDeliveryCost(deliveryCost);
		((AbstractOrderModel) itemModel).setPaymentCost(paymentCost);
		((AbstractOrderModel) itemModel).setTotalDiscounts(discount);
		((AbstractOrderModel) itemModel).setTotalTax(tax);
		((AbstractOrderModel) itemModel).setTotalPrice(totalOrderPrice + deliveryCost-discount);
		((AbstractOrderModel) itemModel).setSubtotal(totalOrderPrice);
	}
	
	protected Double getDiscounts(SalesOrderSimulateItemData item,ItemModel itemModel) {

		
		ProceduresSubtotal priceSubtotal = baseStore(itemModel).getSAPConfiguration().getSalesordersimulate_pricesub();
		ProceduresSubtotal discountSubTotal = baseStore(itemModel).getSAPConfiguration().getSalesordersimulate_discountsub();
		String subTotal1 = getAmount(item,priceSubtotal);
		String subTotal2 = getAmount(item,discountSubTotal);
		
		Double discount = 0.0;
		if (!subTotal1.isEmpty() && !subTotal2.isEmpty()) {
			discount = Double.parseDouble(subTotal1) - Double.parseDouble(subTotal2);
		}
		return discount;
	}

	
	private String getTaxAmount(SalesOrderSimulateItemData item,ItemModel itemModel) {
		ProceduresSubtotal taxSubtotal = baseStore(itemModel).getSAPConfiguration().getSalesordersimulate_taxessub();
		return getAmount(item,taxSubtotal);
		
	}
	private String getSubTotal(SalesOrderSimulateItemData item,ItemModel itemModel) {
		ProceduresSubtotal priceSubtotal = baseStore(itemModel).getSAPConfiguration().getSalesordersimulate_pricesub();
		return getAmount(item, priceSubtotal);	
	}
	private String getPaymentCost(SalesOrderSimulateItemData item,ItemModel itemModel) {
		ProceduresSubtotal paymentSubtotal = baseStore(itemModel).getSAPConfiguration().getSalesordersimulate_paymentsub();
		return getAmount(item,paymentSubtotal);
	}

	private String getDeliverySubtotal(SalesOrderSimulateItemData item,ItemModel itemModel) {
		ProceduresSubtotal deliverySubtotal = baseStore(itemModel).getSAPConfiguration().getSalesordersimulate_deliverysub();
		return getAmount(item,deliverySubtotal);
	}


	private String getAmount(SalesOrderSimulateItemData item, ProceduresSubtotal subtotal) {
		switch(subtotal.getCode() ){
			case NETAMOUNT:
				return item.getNetAmount();
			case TAXAMOUNT:
				return item.getTaxAmount();
			case COSTAMOUNT:
				return item.getCostAmount();
			case SUBTOTAL1AMOUNT:
				return item.getSubtotal1Amount();
			case SUBTOTAL2AMOUNT:
				return item.getSubtotal2Amount();
			case SUBTOTAL3AMOUNT:
				return item.getSubtotal3Amount();
			case SUBTOTAL4AMOUNT:
				return item.getSubtotal4Amount();
			case SUBTOTAL5AMOUNT:
				return item.getSubtotal5Amount();
			case SUBTOTAL6AMOUNT:
				return item.getSubtotal6Amount();
			default:
				return "";
			
		}
	}

	
	private BaseStoreModel baseStore(ItemModel itemModel) {
		BaseStoreModel baseStore=null;
		if(itemModel instanceof AbstractOrderModel) {
			baseStore = ((AbstractOrderModel) itemModel).getStore();
		} else {
			baseStore =  getBaseStoreService().getCurrentBaseStore();
		}
		return baseStore;
	}
	
	
	
	protected SalesOrderSimulationOutboundRequest getSalesOrderSimulationOutboundRequest() {
		return salesOrderSimulationOutboundRequest;
	}

	public void setSalesOrderSimulationOutboundRequest(
			SalesOrderSimulationOutboundRequest salesOrderSimulationOutboundRequest) {
		this.salesOrderSimulationOutboundRequest = salesOrderSimulationOutboundRequest;
	}

	protected DeliveryService getDeliveryService() {
		return deliveryService;
	}

	public void setDeliveryService(DeliveryService deliveryService) {
		this.deliveryService = deliveryService;
	}

	protected UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public B2BUnitService getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}
	public ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		return moduleConfigurationAccess;
	}


	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	protected SAPDefaultUnitService getSapUnitService() {
		return sapUnitService;
	}

	public void setSapUnitService(SAPDefaultUnitService sapUnitService) {
		this.sapUnitService = sapUnitService;
	}
	

	public PriceDataFactory getPriceDataFactory() {
		return priceDataFactory;
	}

	public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
		this.priceDataFactory = priceDataFactory;
	}

	
	
	public SalesOrderSimulationUtil getSalesordersimulationUtil() {
		return salesordersimulationUtil;
	}

	public void setSalesordersimulationUtil(SalesOrderSimulationUtil salesordersimulationUtil) {
		this.salesordersimulationUtil = salesordersimulationUtil;
	}

	@Override
	public Map<String, SapProductAvailability> getStockLevels(ProductModel productModel,
			Collection<WarehouseModel> warehouses) {
		Map<String, SapProductAvailability> stockLevelMap = new HashMap<>();
		try {
			getStockLevlesForPlants(productModel, stockLevelMap, warehouses);
		} catch (IOException e) {
			LOG.error(ERROR_MSG,e);
		}
		return stockLevelMap;
	}
	
	@Override
	public SapProductAvailability getStockAvailability(ProductModel productModel, BaseStoreModel baseStore) {
		final Map<String, SapProductAvailability> stockLevelMap = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		try {
			baseStore = (baseStore!=null)?baseStore:getBaseStoreService().getCurrentBaseStore();
			if (baseStore.getSAPConfiguration() != null) {
				final String orderType = baseStore.getSAPConfiguration().getSapcommon_transactionType();
				final String salesOrg = baseStore.getSAPConfiguration().getSapcommon_salesOrganization();
				final String distributionChannel = baseStore.getSAPConfiguration().getSapcommon_distributionChannel();
				final String division = baseStore.getSAPConfiguration().getSapcommon_division();

				final SalesOrderSimulationRequestData requestData = getSalesOrderSimulationRequestData(
						getCurrency(productModel), false, salesOrg, distributionChannel, division, null,orderType);
				List<SalesOrderSimulateItemRequestData> items = getItemLevelDetailsWithBulkQuantity(productModel);
				requestData.setItems(items);
					String stockDetails = getSalesOrderSimulationOutboundRequest()
							.getResponseFromSalesOrderSimulate(mapper.writeValueAsString(requestData));

					SalesOrderSimulationData responseData = mapper.readValue(stockDetails, SalesOrderSimulationData.class);
					for (SalesOrderSimulateItemData item : responseData.getResult().getItems().getSalesOrderItems()) {
						setStockLevelDetails(stockLevelMap, item.getScheduleLines(), item.getMaterial(), salesOrg, distributionChannel,
								division,DEFAULT_PLANT);
					}
				}
		} catch (IOException e) {
			LOG.error(ERROR_MSG,e);
		}
		return stockLevelMap.get(DEFAULT_PLANT);
		
	}

	private void getStockLevlesForPlants(ProductModel productModel,
			Map<String, SapProductAvailability>  stockLevelMap, Collection<WarehouseModel> warehouses) throws IOException {
		
		final List<SalesOrderSimulationRequestData> stockFetchRequests = getStockRequestData(productModel, warehouses);
		setStockInformation(stockLevelMap, stockFetchRequests);
	}
	
	private void setStockInformation(Map<String, SapProductAvailability>  stockLevelMap,
			List<SalesOrderSimulationRequestData> stockFetchRequests)
			throws IOException  {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		for (SalesOrderSimulationRequestData request : stockFetchRequests) {
			String stockDetails = getSalesOrderSimulationOutboundRequest()
					.getResponseFromSalesOrderSimulate(mapper.writeValueAsString(request));

			SalesOrderSimulationData responseData = mapper.readValue(stockDetails, SalesOrderSimulationData.class);
 			String salesOrg = responseData.getResult().getSalesOrganization();
			String distrChannel = responseData.getResult().getDistributionChannel();
			String division = responseData.getResult().getDivision();
			for (SalesOrderSimulateItemData item : responseData.getResult().getItems().getSalesOrderItems()) {
				setStockLevelDetails(stockLevelMap, item.getScheduleLines(), item.getMaterial(), salesOrg, distrChannel,
						division,item.getPlant());
			}
		}
	}
	private void setStockLevelDetails(Map<String, SapProductAvailability> stockLevelMap, ScheduleLinesData shceduleLinesData,
			String material, String salesOrg, String distrChannel, String division,String plant) {
		SapProductAvailabilityImpl productAvailabiilty = (SapProductAvailabilityImpl)stockLevelMap.get(plant);
		long totalStock =  (productAvailabiilty!=null)? productAvailabiilty.getCurrentStockLevel() :0L;
		List<FutureStockModel> futureStockAvailability = new ArrayList<>();
		for (ScheduleLineData scheduleLineData : shceduleLinesData.getSalesOrderScheduleLines()) {
			if (FIRST_SCHEDULE_LINE_NO.equals(scheduleLineData.getScheduleLine()) || SECOND_SCHEDULE_LINE_NO.equals(scheduleLineData.getScheduleLine()) ) {
				 totalStock  += Math.round(Double.parseDouble(scheduleLineData.getConfdOrderQtyByMatlAvailCheck()));
			}
			
			FutureStockModel stockModel = new FutureStockModel();
			stockModel.setProductCode(material);
			String confirmedDeliveryDate = scheduleLineData.getConfirmedDeliveryDate();
			String requestedDeliveryDate = scheduleLineData.getRequestedDeliveryDate();
			String deliveryDateString = (confirmedDeliveryDate != null) ? confirmedDeliveryDate : requestedDeliveryDate;
			String availability = scheduleLineData.getConfdOrderQtyByMatlAvailCheck();
			Date deliveryDate = new Date(Long.parseLong(deliveryDateString.replaceAll(".*?(\\d+).*", "$1")));
			Date todaysDate = new Date();
			if (deliveryDate.after(todaysDate)) {
				stockModel.setDate(deliveryDate);
				stockModel.setQuantity(Integer.valueOf((int) Double.parseDouble(availability)));
				futureStockAvailability.add(stockModel);

			}
			
		}
		StockLevelModel stockModel = new StockLevelModel();
		stockModel.setProductCode(material);
		
		if ((totalStock)>0) {
			stockModel.setInStockStatus(InStockStatus.NOTSPECIFIED);
		} else {
			stockModel.setInStockStatus(InStockStatus.FORCEOUTOFSTOCK);
		}
		stockModel.setAvailable((int)totalStock);
		WarehouseModel warehouse = getSapPlant(salesOrg, distrChannel, division);
		stockModel.setWarehouse(warehouse);
		
		final SapProductAvailability sapProductAvaialbility = new SapProductAvailabilityImpl((long) totalStock,futureStockAvailability,stockModel);
		stockLevelMap.put(plant, sapProductAvaialbility);
	}


}

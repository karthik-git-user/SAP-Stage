/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import com.sap.ppengine.client.dto.ExtendedAmountType;
import com.sap.ppengine.client.dto.ItemDomainSpecific;
import com.sap.ppengine.client.dto.PriceCalculate;
import com.sap.ppengine.client.dto.PriceCalculateResponse;
import com.sap.ppengine.client.util.PPSClientBeanAccessor;
import com.sap.retail.sapppspricing.PPSClient;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.PPSRequestCreator;
import com.sap.retail.sapppspricing.PriceCalculateToOrderMapper;
import com.sap.retail.sapppspricing.PricingBackend;
import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.PriceValue;

/**
 * Implementation of {@link PricingBackend} performing the calls for catalog or
 * basket pricing against an embedded or remote PPS. Takes care for request
 * creation as well as mapping the response to the corresponding hybris objects
 */
public class PricingBackendPPS implements PricingBackend {
	private double highPrice;
	private boolean useHighPrice = true;
	private static final Logger LOG = LoggerFactory.getLogger(PricingBackendPPS.class);
	private PPSClient ppsClient;

	private PPSClientBeanAccessor accessor;
	private List<PriceCalculateToOrderMapper> resultToOrderMappers;
	private PPSRequestCreator requestCreator;
	private CommonI18NService commonI18NService;
	private PPSConfigService configService;

	@Override
	public void readPricesForCart(final AbstractOrderModel order) {
		LOG.debug("entering readPricesForCart()");
		if (order.getEntries().isEmpty()) {
			return;
		}
		final PriceCalculate priceCalculate = getRequestCreator().createRequestForCart(order);

		try {
			final PriceCalculateResponse response = getPpsClient().callPPS(priceCalculate,
					order.getStore().getSAPConfiguration());
			for (final PriceCalculateToOrderMapper mapper : getResultToOrderMappers()) {
				mapper.map(response, order);
			}
		} catch (final SapPPSPricingRuntimeException e) {
			throw new SapPPSPricingRuntimeException("Calculation for AbstractOrder " + order.getCode() + " failed" + e.getMessage());
		}
		LOG.debug("exiting readPricesForCart method ");
	}

	@Override
	public List<PriceInformation> readPriceInformationForProducts(final List<ProductModel> productModels,
			final boolean isNet) {
		LOG.debug("entering readPriceInformationForProducts()");
		final String expectedCurrencyCode = getCommonI18NService().getCurrentCurrency().getIsocode();

		final List<PriceInformation> result = new ArrayList<>(productModels.size());
			final Map<ProductModel, PriceInformation> ppsPinfos = readPriceInfosFromPps(productModels, isNet);
			if (ppsPinfos != null) {
				for (final Entry<ProductModel, PriceInformation> ppsPinfo : ppsPinfos.entrySet()) {
					PriceInformation pinfo = ppsPinfo.getValue();
					final ProductModel prod = ppsPinfo.getKey();
					final String actualCurrencyCode = pinfo.getPriceValue().getCurrencyIso();
					if (!expectedCurrencyCode.equals(actualCurrencyCode)) {
						LOG.warn("Unexpected currency code {} for item {}, setting to {}", actualCurrencyCode,
								prod.getCode(), expectedCurrencyCode);
						pinfo = new PriceInformation(
								new PriceValue(expectedCurrencyCode, pinfo.getPriceValue().getValue(), true));
					}
					result.add(pinfo);

				}
				return result;
			}
		return Collections.emptyList();
	}

	protected Map<ProductModel, PriceInformation> readPriceInfosFromPps(final List<ProductModel> prods,
			final boolean isNet) {
		final Map<ProductModel, PriceInformation> result = new HashMap<>();
		final SAPConfigurationModel sapConfig = CollectionUtils.isEmpty(prods) ? null
				: getConfigService().getSapConfig(prods.get(0));
		for (final ProductModel prod : prods) {
			PriceInformation pinfo = null;
			final PriceCalculate priceCalculate = getRequestCreator().createRequestForCatalog(prod, isNet);
			double calculatedPrice;
			String currencyCode = "";
			try {
				final PriceCalculateResponse response = getPpsClient().callPPS(priceCalculate, sapConfig);
				final ItemDomainSpecific lineItemContent = getAccessor().getHelper().getLineItemContent(
						response.getPriceCalculateBody().get(0).getShoppingBasket().getLineItem().get(0));
				if (lineItemContent != null) {
					final ExtendedAmountType extendedAmount = lineItemContent.getExtendedAmount();
					calculatedPrice = extendedAmount.getValue().doubleValue();
					currencyCode = extendedAmount.getCurrency();
				} else {
					throw new SapPPSPricingRuntimeException("Line item missing in response");
				}
			} catch (final SapPPSPricingRuntimeException e) {
				LOG.error("Could not determine catalog price for product {}" , prod.getCode() , e);
				return null;
			}
				// Special handling as long as currency code is not returned by PPS - null is
				// not allowed for a PriceValue
				pinfo = new PriceInformation(
						new PriceValue(currencyCode == null ? "" : currencyCode, calculatedPrice, true));
			
			result.put(prod, pinfo);
		}
		return result;
	}

	public PPSClient getPpsClient() {
		return ppsClient;
	}

	public void setPpsClient(final PPSClient ppsClient) {
		this.ppsClient = ppsClient;
	}

	public PPSClientBeanAccessor getAccessor() {
		return accessor;
	}

	public void setAccessor(final PPSClientBeanAccessor accessor) {
		this.accessor = accessor;
	}

	public List<PriceCalculateToOrderMapper> getResultToOrderMappers() {
		return resultToOrderMappers;
	}

	public void setResultToOrderMappers(final List<PriceCalculateToOrderMapper> resultToOrderMappers) {
		this.resultToOrderMappers = resultToOrderMappers;
		Collections.sort(this.resultToOrderMappers, new OrderedComparator());
		if (LOG.isDebugEnabled()) {
			LOG.debug("List of PriceCalculate result mappers after sorting {}", this.resultToOrderMappers);
		}
	}

	public PPSRequestCreator getRequestCreator() {
		return requestCreator;
	}

	public void setRequestCreator(final PPSRequestCreator creator) {
		this.requestCreator = creator;
	}

	private static class OrderedComparator implements Comparator<Ordered> {
		@Override
		public int compare(final Ordered arg0, final Ordered arg1) {
			return Integer.valueOf(arg0.getOrder()).compareTo(Integer.valueOf(arg1.getOrder()));
		}
	}

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(final double highPrice) {
		this.highPrice = highPrice;
	}

	public PPSConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(final PPSConfigService configService) {
		this.configService = configService;
	}

	public boolean isUseHighPrice() {
		return useHighPrice;
	}

	public void setUseHighPrice(final boolean useHighPrice) {
		this.useHighPrice = useHighPrice;
	}

}

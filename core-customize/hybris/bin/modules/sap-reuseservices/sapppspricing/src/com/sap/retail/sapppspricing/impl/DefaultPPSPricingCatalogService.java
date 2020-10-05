/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.RestClientException;

import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.PricingBackend;

import de.hybris.platform.commerceservices.price.impl.NetPriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;

/**
 * Catalog price calculation via PPS if set to active
 */
public class DefaultPPSPricingCatalogService extends NetPriceService {
	private PricingBackend pricingBackend;
	private PPSConfigService configService;
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPPSConfigService.class);

	@Override
	public List<PriceInformation> getPriceInformationsForProduct(final ProductModel model) {
		try {

			return getConfigService().isPpsActive(model)
					? getPricingBackend().readPriceInformationForProducts(Collections.singletonList(model),
							getNetGrossStrategy().isNet())
					: super.getPriceInformationsForProduct(model);

		} catch (final RestClientException e) {
			LOG.error("Could not connect to rest client ", e);
			return Collections.emptyList();
		}
	}

	public PricingBackend getPricingBackend() {
		return pricingBackend;
	}

	public void setPricingBackend(final PricingBackend pricingBackend) {
		this.pricingBackend = pricingBackend;
	}

	public PPSConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(final PPSConfigService configService) {
		this.configService = configService;
	}

}

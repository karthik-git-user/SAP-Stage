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
package de.hybris.platform.sap.sapsalesordersimulation.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.acceleratorfacades.productcarousel.impl.DefaultProductCarouselFacade;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;
/**
 * 
 * fetches the live price for the product Carousel 
 *
 */
public class SAPProductCarouselFacade extends DefaultProductCarouselFacade {
	private SalesOrderSimulationService salesOrderSimulationService;
	private PriceDataFactory priceDataFactory;
	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	/**
	 * Fetches list of products for a given product carousel component when not in
	 * preview (i.e., no cmsTicketId in present in the session).
	 *
	 * @param component The product carousel component model
	 * @return List<ProductData> list of available products
	 */
	@Override
	protected List<ProductData> fetchProductsForNonPreviewMode(final ProductCarouselComponentModel component) {

		if (getSapSimulateSalesOrderEnablementService().isCatalogPricingEnabled()) {
			return fetchLivePriceStockForProducts(component);
		} else {
			return super.fetchProductsForNonPreviewMode(component);
		}

	}

	protected List<ProductData> fetchLivePriceStockForProducts(final ProductCarouselComponentModel component) {
		List<ProductModel> allProducts = new ArrayList<>();

		allProducts.addAll(component.getProducts());
		for (final CategoryModel categoryModel : component.getCategories()) {

			allProducts.addAll(categoryModel.getProducts());

		}

		final List<ProductData> products = new ArrayList<>();

		for (final ProductModel productModel : allProducts) {
			products.add(getProductFacade().getProductForCodeAndOptions(productModel.getCode(),
					Arrays.asList(ProductOption.BASIC)));
		}

		Map<String, List<PriceInformation>> priceInfoMap = getSalesOrderSimulationService()
				.getPriceDetailsForProducts(allProducts);
		if (priceInfoMap != null) {
			for (final ProductData productData : products) {
				PriceDataType type = PriceDataType.BUY;
				if (allProducts.stream().anyMatch(o -> o.getCode().equals(productData.getCode())
						&& CollectionUtils.isEmpty(o.getVariants()))) {
					type = PriceDataType.FROM;
				}

				List<PriceInformation> priceInfo = priceInfoMap.get(productData.getCode());
				if (priceInfo != null && !priceInfo.isEmpty() && priceInfo.get(0) != null) {

					PriceInformation priceValue = priceInfo.get(0);
					final PriceData priceData = getPriceDataFactory().create(type,
							BigDecimal.valueOf(priceValue.getPriceValue().getValue()),
							priceValue.getPriceValue().getCurrencyIso());
					productData.setPrice(priceData);

				}
			}
		}
		return products;
	}

	public PriceDataFactory getPriceDataFactory() {
		return priceDataFactory;
	}

	public void setPriceDataFactory(PriceDataFactory priceDataFactory) {
		this.priceDataFactory = priceDataFactory;
	}

	protected SalesOrderSimulationService getSalesOrderSimulationService() {
		return salesOrderSimulationService;
	}

	public void setSalesOrderSimulationService(SalesOrderSimulationService salesOrderSimulationService) {
		this.salesOrderSimulationService = salesOrderSimulationService;
	}

	protected SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}

	
	
	
}

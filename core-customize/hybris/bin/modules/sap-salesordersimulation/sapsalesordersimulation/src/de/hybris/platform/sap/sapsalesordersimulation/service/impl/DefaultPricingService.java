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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.sapsalesordersimulation.cache.service.CacheAccess;
import de.hybris.platform.sap.sapsalesordersimulation.service.PricingService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Fetch price related to product with configuration of cache.
 * if cache is enabled , the pricing would be fetched from cache otherwise API call would fetch the pricing from backend
 */
public class DefaultPricingService implements PricingService {
	private CommonI18NService commonI18NService;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private B2BUnitService b2bUnitService;
	private CacheAccess sapPricingCacheRegion;
	private SalesOrderSimulationService salesOrderSimulationService;
	private ModuleConfigurationAccess moduleConfigurationAccess;
	private SalesOrderSimulationUtil salesordersimulationUtil;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPricingService.class);
	public static final String CONF_PROP_IS_CACHED_CATALOG_PRICE = "saplivecacheprice";

	public static final String CACHEKEY_SAP_PRICING = "SAP_PRICING";
	@Override
	public List<PriceInformation> getPriceForProduct(ProductModel product) {
		List<PriceInformation> priceInformation =  new ArrayList();
		final boolean isPriceCacheEnabled = getModuleConfigurationAccess().getProperty(CONF_PROP_IS_CACHED_CATALOG_PRICE);
		if (isPriceCacheEnabled) {
			priceInformation = (List<PriceInformation>) getSapPricingCacheRegion().get(getPriceCacheKey(product));
		}
				
		if(priceInformation==null || priceInformation.isEmpty() ) {
			priceInformation = getSalesOrderSimulationService().getPriceDetailsForProduct(product);
			try {
				if (isPriceCacheEnabled) {
					getSapPricingCacheRegion().put(getPriceCacheKey(product), priceInformation);
				}
			} catch (SAPHybrisCacheException e) {
				LOG.error("Unable to add the price information to SapPricinigCacheRegion..",e);
			}
		}
			
		return priceInformation;
	}
	
	protected String getPriceCacheKey(final ProductModel productModel)
	{
		final StringBuilder sapPricingCacheKey = new StringBuilder();
		final String currentCustomerId = getSalesordersimulationUtil().getSoldToParty(null);
		sapPricingCacheKey.append(CACHEKEY_SAP_PRICING);
		sapPricingCacheKey.append(productModel.getCode());
		sapPricingCacheKey.append(currentCustomerId);
		sapPricingCacheKey.append(getCommonI18NService().getCurrentCurrency().getIsocode().toUpperCase(Locale.ENGLISH));
		sapPricingCacheKey.append(getCommonI18NService().getCurrentLanguage().getIsocode().toUpperCase(Locale.ENGLISH));
		return sapPricingCacheKey.toString();
	}
	
	protected String getCurrentCustomerID() {

		final UserModel userModel= getUserService().getCurrentUser();

		final B2BCustomerModel b2bCustomer = (userModel instanceof B2BCustomerModel) ? (B2BCustomerModel) userModel : null;
		// if b2bcustomer is null then go for reference customer
		if (b2bCustomer == null) {
			return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcommon_referenceCustomer();
		}
		final B2BUnitModel parent = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);

		return parent.getUid();

	}
	

	public CommonI18NService getCommonI18NService() {
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) {
		this.commonI18NService = commonI18NService;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public B2BUnitService getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}

	

	public SalesOrderSimulationService getSalesOrderSimulationService() {
		return salesOrderSimulationService;
	}

	public void setSalesOrderSimulationService(SalesOrderSimulationService salesOrderSimulationService) {
		this.salesOrderSimulationService = salesOrderSimulationService;
	}

	public CacheAccess getSapPricingCacheRegion() {
		return sapPricingCacheRegion;
	}

	public void setSapPricingCacheRegion(CacheAccess sapPricingCacheRegion) {
		this.sapPricingCacheRegion = sapPricingCacheRegion;
	}

	public ModuleConfigurationAccess getModuleConfigurationAccess() {
		return moduleConfigurationAccess;
	}

	public void setModuleConfigurationAccess(ModuleConfigurationAccess moduleConfigurationAccess) {
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	protected SalesOrderSimulationUtil getSalesordersimulationUtil() {
		return salesordersimulationUtil;
	}

	public void setSalesordersimulationUtil(SalesOrderSimulationUtil salesordersimulationUtil) {
		this.salesordersimulationUtil = salesordersimulationUtil;
	}
	
	

}

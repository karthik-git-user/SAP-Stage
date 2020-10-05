/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.sap.retail.sapppspricing.PPSConfigService;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotions.impl.DefaultPromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

/**
 * Promotions service simply doing nothing in case PPS is set to active
 */
public class DefaultPPSPromotionsService extends DefaultPromotionsService{
	private transient PPSConfigService configService;

	/**
	 * SAP promotions are already calculated from backend
	 */
	@Override
	public PromotionOrderResults updatePromotions(final Collection<PromotionGroupModel> promotionGroups,
			final AbstractOrderModel order) {
		if (getConfigService().isPpsActive(order)) {
			return createEmptyPromotionOrderResult();

		} else {
			return super.updatePromotions(promotionGroups, order);
		}
	}

	private PromotionOrderResults createEmptyPromotionOrderResult() {
		return new PromotionOrderResults(null, null, Collections.<PromotionResult>emptyList(), 0.0);
	}

	/**
	 * SAP promotions are already calculated from backend
	 */
	@Override
	public PromotionOrderResults updatePromotions(final Collection<PromotionGroupModel> promotionGroups,
			final AbstractOrderModel order, final boolean evaluateRestrictions,
			final PromotionsManager.AutoApplyMode productPromotionMode,
			final PromotionsManager.AutoApplyMode orderPromotionMode, final Date date) {
		if (getConfigService().isPpsActive(order)) {
			return createEmptyPromotionOrderResult();
		} else {
			return super.updatePromotions(promotionGroups, order, evaluateRestrictions, productPromotionMode,
					orderPromotionMode, date);
		}
	}

	/**
	 * Remove Hybris promotions from product detail page as SAP promotions are
	 * calculated in the PPS
	 */
	@Override
	public List<ProductPromotionModel> getProductPromotions(final Collection<PromotionGroupModel> promotionGroups,
			final ProductModel product, final boolean evaluateRestrictions, final Date date) {
		if (getConfigService().isPpsActive(product)) {
			return Collections.emptyList();
		} else {
			return super.getProductPromotions(promotionGroups, product, evaluateRestrictions, date);
		}
	}


	@SuppressWarnings("javadoc")
	public PPSConfigService getConfigService() {
		return configService;
	}

	@SuppressWarnings("javadoc")
	public void setConfigService(final PPSConfigService configService) {
		this.configService = configService;
	}

}

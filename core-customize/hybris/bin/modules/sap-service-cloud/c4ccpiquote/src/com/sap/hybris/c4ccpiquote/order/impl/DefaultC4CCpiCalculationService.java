/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4ccpiquote.order.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.c4ccpiquote.strategy.impl.DefaultC4CCpiQuoteRequiresCalculationStrategy;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.order.strategies.calculation.OrderRequiresCalculationStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.TaxValue;

/**
* Calculations Service implementation C4C Quote Calculation
*/
public class DefaultC4CCpiCalculationService extends DefaultCalculationService {

    private transient OrderRequiresCalculationStrategy orderRequireCalculationStrategy;
    private transient DefaultC4CCpiQuoteRequiresCalculationStrategy quoteRequiresCalculationStrategy;
    private transient CommonI18NService commonI18NServiceInC4C;

    @Override
    public void calculate(AbstractOrderModel order) throws CalculationException {

        if (orderRequireCalculationStrategy.requiresCalculation(order)) {

            if (quoteRequiresCalculationStrategy.shouldCalculateAllValues(order)) {
                super.calculate(order);
            } else {

                final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = new LinkedHashMap<TaxValue, Map<Set<TaxValue>, Double>>(
                        order.getEntries().size() * 2);

                final List<TaxValue> relativeTaxValue = new LinkedList<TaxValue>();
                resetAdditionalCosts(order, relativeTaxValue);

                calculateTotals(order, false, taxValueMap);

            }
        }

    }

    @Override
    public void recalculate(AbstractOrderModel order) throws CalculationException {

        if (quoteRequiresCalculationStrategy.shouldCalculateAllValues(order)) {
            super.recalculate(order);
        } else {

            final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = new LinkedHashMap<TaxValue, Map<Set<TaxValue>, Double>>(
                    order.getEntries().size() * 2);

            final List<TaxValue> relativeTaxValue = new LinkedList<TaxValue>();
            resetAdditionalCosts(order, relativeTaxValue);

            calculateTotals(order, false, taxValueMap);

        }

    }

    @Override
    public void calculateTotals(final AbstractOrderModel order, final boolean recalculate) throws CalculationException {

        if (quoteRequiresCalculationStrategy.shouldCalculateAllValues(order)) {
            super.calculateTotals(order, recalculate);
        } else {
            final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = new LinkedHashMap<TaxValue, Map<Set<TaxValue>, Double>>(
                    order.getEntries().size() * 2);

            calculateTotals(order, recalculate, taxValueMap);
        }
    }

    @Override
    protected void calculateTotals(AbstractOrderModel order, boolean recalculate,
            Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap) throws CalculationException {

        if (quoteRequiresCalculationStrategy.shouldCalculateAllValues(order)) {
            super.calculateTotals(order, recalculate, taxValueMap);
        } else {

            if (recalculate || orderRequireCalculationStrategy.requiresCalculation(order)) {
                final CurrencyModel curr = order.getCurrency();
                final int digits = curr.getDigits().intValue();
                // subtotal
                final double subtotal = order.getSubtotal().doubleValue();
                // discounts
                final double totalDiscounts = calculateDiscountValues(order, recalculate);
    			final double roundedTotalDiscounts = commonI18NServiceInC4C.roundCurrency(totalDiscounts, digits);
    			order.setTotalDiscounts(Double.valueOf(roundedTotalDiscounts));

                // set total
                final double total = subtotal + order.getPaymentCost().doubleValue()
                        + order.getDeliveryCost().doubleValue() - roundedTotalDiscounts;
                final double totalRounded = commonI18NServiceInC4C.roundCurrency(total, digits);
                order.setTotalPrice(Double.valueOf(totalRounded));

                setCalculatedStatus(order);
                saveOrder(order);
            }
        }

    }

    @Required
    public void setOrderRequireCalculationStrategy(OrderRequiresCalculationStrategy orderRequireCalculationStrategy) {
        this.orderRequireCalculationStrategy = orderRequireCalculationStrategy;
    }

    @Required
    public void setQuoteRequiresCalculationStrategy(
            DefaultC4CCpiQuoteRequiresCalculationStrategy quoteRequiresCalculationStrategy) {
        this.quoteRequiresCalculationStrategy = quoteRequiresCalculationStrategy;
    }
    
    @Required
	public void setCommonI18NServiceInC4C(final CommonI18NService commonI18NService)
	{
		this.commonI18NServiceInC4C = commonI18NService;
	}

}
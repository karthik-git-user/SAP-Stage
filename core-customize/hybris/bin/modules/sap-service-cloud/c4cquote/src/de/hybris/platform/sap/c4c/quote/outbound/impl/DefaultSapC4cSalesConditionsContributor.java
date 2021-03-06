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
package de.hybris.platform.sap.c4c.quote.outbound.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.sap.c4c.quote.model.ExternalDiscountValueModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SalesConditionCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.SaporderexchangeConstants;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSalesConditionsContributor;
import de.hybris.platform.util.TaxValue;

public class DefaultSapC4cSalesConditionsContributor extends DefaultSalesConditionsContributor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSapC4cSalesConditionsContributor.class);
    private static final String PERCENTAGE_DISCOUNT = "P1";
    private static final String DH_BATCH_ID = "dh_batchId";
    private static final String TAX = "tax";

    @Override
    protected void createOrderDiscountRows(final OrderModel order, final List<Map<String, Object>> result) {
        List<ExternalDiscountValueModel> discounts = order.getExternalDiscountValues();
        int conditionCounter = getConditionCounterStartOrderDiscount();
        for (ExternalDiscountValueModel discountValue : emptyIfNull(discounts)) {

            final Map<String, Object> row = new HashMap<>();
            row.put(OrderCsvColumns.ORDER_ID, order.getCode());
            row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, SaporderexchangeConstants.HEADER_ENTRY);
            row.put(SalesConditionCsvColumns.CONDITION_VALUE, discountValue.getValue());
            row.put(SalesConditionCsvColumns.CONDITION_COUNTER, conditionCounter++);

            row.put(SalesConditionCsvColumns.CONDITION_CODE, discountValue.getCode());
            String discountType = discountValue.getType();
            if (StringUtils.isNotEmpty(discountType) && PERCENTAGE_DISCOUNT.equals(discountType)) {
                row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.FALSE);
            } else {
                row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
                row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());

            }
            if (discountValue.getCode().startsWith(PROMOTION_DISCOUNT_CODE_PREFIX)) {
                // Add the promotional discounts that are generated by the
                // promotion rule engine
                row.put(SalesConditionCsvColumns.CONDITION_CODE,
                        determinePromotionExternalDiscountCode(order, discountValue));

            } else {
                // Add other kinds of discounts
                row.put(SalesConditionCsvColumns.CONDITION_CODE, discountValue.getCode());

            }
            getBatchIdAttributes().forEach(row::putIfAbsent);
            row.put(DH_BATCH_ID, order.getCode());

            result.add(row);

        }

    }

    @Override
    protected void createProductDiscountRows(final OrderModel order, final List<Map<String, Object>> result,
            final AbstractOrderEntryModel entry) {
        List<ExternalDiscountValueModel> discountList = entry.getExternalDiscountValues();
        int conditionCounter = getConditionCounterStartProductDiscount();

        for (final ExternalDiscountValueModel disVal : emptyIfNull(discountList)) {

            final Map<String, Object> row = new HashMap<>();

            row.put(OrderCsvColumns.ORDER_ID, order.getCode());
            row.put(SalesConditionCsvColumns.CONDITION_ENTRY_NUMBER, entry.getEntryNumber());
            row.put(SalesConditionCsvColumns.CONDITION_COUNTER, conditionCounter++);
            row.put(SalesConditionCsvColumns.CONDITION_UNIT_CODE, entry.getUnit().getCode());
            row.put(SalesConditionCsvColumns.CONDITION_PRICE_QUANTITY, entry.getQuantity());
            row.put(SalesConditionCsvColumns.CONDITION_VALUE, disVal.getValue());
            String discountType = disVal.getType();
            if (StringUtils.isNotEmpty(discountType) && PERCENTAGE_DISCOUNT.equals(discountType)) {
                row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.FALSE);
            } else {
                row.put(SalesConditionCsvColumns.ABSOLUTE, Boolean.TRUE);
                row.put(SalesConditionCsvColumns.CONDITION_CURRENCY_ISO_CODE, order.getCurrency().getIsocode());

            }
            if (disVal.getCode().startsWith(PROMOTION_DISCOUNT_CODE_PREFIX)) {
                // Add the promotional discounts that are generated by the
                // promotion rule engine
                row.put(SalesConditionCsvColumns.CONDITION_CODE, determinePromotionExternalDiscountCode(order, disVal));
            } else {
                // Add other kinds of discounts
                row.put(SalesConditionCsvColumns.CONDITION_CODE, disVal.getCode());
            }

            getBatchIdAttributes().forEach(row::putIfAbsent);
            row.put(DH_BATCH_ID, order.getCode());

            result.add(row);
        }
    }

    @Override
    protected void createTaxRows(final OrderModel order, final List<Map<String, Object>> result,
            final AbstractOrderEntryModel entry) {

        if (order.getTotalTax() != null && order.getQuoteReference() != null
                && order.getQuoteReference().getC4cQuoteId() != null) {
            final double totalTax = order.getTotalTax().doubleValue();
            final double entryTaxValue = totalTax / order.getEntries().size();

            final String taxCode = TAX + order.getCode();
            final TaxValue tax = new TaxValue(taxCode, entryTaxValue, true, order.getCurrency().toString());
            final List<TaxValue> taxValues = new ArrayList<>();
            taxValues.add(tax);
            entry.setTaxValues(taxValues);
        }
        super.createTaxRows(order, result, entry);
    }

    // determine sap code corresponding to hybris promotion code
    private String determinePromotionExternalDiscountCode(OrderModel order, ExternalDiscountValueModel discountValue) {

        AbstractPromotionActionModel abstractAction = order.getAllPromotionResults().stream()
                .flatMap(pr -> pr.getActions().stream())
                .filter(action -> action.getGuid().equals(discountValue.getCode())).collect(Collectors.toList())
                .stream().map(Optional::ofNullable).findFirst().flatMap(Function.identity()).orElse(null);

        if (abstractAction instanceof AbstractRuleBasedPromotionActionModel) {

            final AbstractRuleModel rule = (AbstractRuleModel) getRuleService()
                    .getRuleForCode(((AbstractRuleBasedPromotionActionModel) abstractAction).getRule().getCode());

            if (rule != null) {

                if (rule.getSapConditionType() != null) {
                    return rule.getSapConditionType();
                } else {
                    LOGGER.warn(
                            "The promotion rule with code {} is missing the SAP Condition Type; therefore, the promotion discount has not been sent to SAP-ERP!",
                            rule.getCode());
                }
                return null;
            }

        }

        LOGGER.warn(
                "The promotion rule with discount value {} is not configured properly; therefore, the promotion discount has not been sent to SAP-ERP!",
                discountValue);
        return null;

    }
}
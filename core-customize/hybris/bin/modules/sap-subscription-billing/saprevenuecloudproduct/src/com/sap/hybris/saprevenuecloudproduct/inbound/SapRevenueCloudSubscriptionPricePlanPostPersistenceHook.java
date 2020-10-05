/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.inbound;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class SapRevenueCloudSubscriptionPricePlanPostPersistenceHook implements PostPersistHook {

    private static final Logger LOG = LoggerFactory.getLogger(SapRevenueCloudSubscriptionPricePlanPostPersistenceHook.class);

    private ModelService modelService;

    @Override
    public void execute(ItemModel item) {
        LOG.info("The persistence hook  saprcSubscriptionPricePlanPostPersistenceHook is invoked");
        ZonedDateTime utcStartOfDay = LocalDate.now().atStartOfDay(ZoneId.of("UTC"));
        Date currentDate = Date.from(utcStartOfDay.toInstant());

        if (item instanceof SubscriptionPricePlanModel) {
            /*
             * Price comes from iFlow e.g. [3s]
             * We find the product with the price plan
             * and find all the price plans of that product e.g. [1s,2s,3s,4p]
             * filter only subscription plans e.g. [1s, 2s, 3s]
             * remove the incoming subscription price plan e.g.[1s, 2s]
             * get Active price e.g. [2s]
             * set endDate of active plan to current date
             * and save the model
             */
            SubscriptionPricePlanModel incomingPricePlan = (SubscriptionPricePlanModel) item;
            Optional<SubscriptionPricePlanModel> activePricePlanOpt = incomingPricePlan.getProduct().getEurope1Prices().stream()
                    .filter(SubscriptionPricePlanModel.class::isInstance)
                    .map(SubscriptionPricePlanModel.class::cast)
                    .filter(pricePlan -> !StringUtils.equals(incomingPricePlan.getPricePlanId(), pricePlan.getPricePlanId()))
                    .filter(pricePlan -> pricePlan.getEndTime() == null || pricePlan.getEndTime().after(currentDate))
                    .findFirst();

            if (activePricePlanOpt.isPresent()) {
                SubscriptionPricePlanModel activePricePlan = activePricePlanOpt.get();
                LOG.info(String.format("Found active subscription price plan id:[%s] startDate:[%s] endDate:[%s]", activePricePlan.getPricePlanId(), activePricePlan.getStartTime(), activePricePlan.getEndTime()));
                activePricePlan.setEndTime(currentDate);
                modelService.save(activePricePlan);
            } else {
                LOG.error("Unable to find any active subscription price plan for the product: [%s]",incomingPricePlan.getProduct().getName() );
            }



        }

    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.populators;


import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import org.apache.commons.lang3.StringUtils;

public class DefaultSapSubscriptionProductPopulator implements Populator<ProductModel, ProductData> {

    @Override
    public void populate(ProductModel source, ProductData target) {
        String subscriptionCode = source.getSubscriptionCode();
        subscriptionCode = StringUtils.defaultIfBlank(subscriptionCode, StringUtils.EMPTY);
        target.setSubscriptionCode(subscriptionCode);
    }


}

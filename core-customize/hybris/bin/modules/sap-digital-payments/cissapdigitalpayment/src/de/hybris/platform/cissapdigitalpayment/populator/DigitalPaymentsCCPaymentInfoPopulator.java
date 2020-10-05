/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsPollModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.util.Assert;


/**
 * Populates {@link CCPaymentInfoData} with {@link CisSapDigitalPaymentPollRegisteredCardResult Model}.
 */
public class DigitalPaymentsCCPaymentInfoPopulator<S extends DigitalPaymentsPollModel, T extends CCPaymentInfoData>
        implements Populator<S, T> {

    @Override
    public void populate(final S source, final T target)
            throws ConversionException {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");


        if (null != source.getPaytCardByDigitalPaymentSrvc()) {
            //Set the ID is also as the Token
            target.setId(source.getPaytCardByDigitalPaymentSrvc());
            //This subscription ID set is getting overridden while calling createSubscription method in CustomerAccountService
            target.setSubscriptionId(source.getPaytCardByDigitalPaymentSrvc());
        }

        target.setCardType(source.getPaymentCardType());
        target.setExpiryMonth(source.getPaymentCardExpirationMonth());
        target.setExpiryYear(source.getPaymentCardExpirationYear());
        target.setCardNumber(source.getPaymentCardMaskedNumber());
        target.setAccountHolderName(source.getPaymentCardHolderName());

    }

}

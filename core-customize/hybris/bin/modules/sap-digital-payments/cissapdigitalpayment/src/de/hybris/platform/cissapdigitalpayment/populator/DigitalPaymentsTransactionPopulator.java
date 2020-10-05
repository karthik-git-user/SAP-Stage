/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsTransactionModel;
import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsTransactionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DigitalPaymentsTransactionPopulator<S extends DigitalPaymentsTransactionModel, T extends DigitalPaymentsTransactionData> implements Populator<S, T> {
    @Override
    public void populate(S source, T target) throws ConversionException {
        target.setDateTime(source.getDigitalPaymentDateTime());
        target.setResultCode(source.getDigitalPaytTransResult());
        target.setResultDescription(source.getDigitalPaytTransRsltDesc());
        target.setSessionId(source.getDigitalPaymentTransaction());
    }
}

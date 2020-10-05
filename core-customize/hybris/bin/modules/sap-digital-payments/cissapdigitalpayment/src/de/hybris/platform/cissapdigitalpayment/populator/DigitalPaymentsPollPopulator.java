/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsPollModel;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsTransactionModel;
import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsPollData;
import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsTransactionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.math.NumberUtils;

public class DigitalPaymentsPollPopulator<S extends DigitalPaymentsPollModel, T extends DigitalPaymentsPollData> implements Populator<S, T> {

    private Converter<DigitalPaymentsTransactionModel, DigitalPaymentsTransactionData> transactionConverter;

    @Override
    public void populate(S source, T target) throws ConversionException {
        target.setCardHolderName(source.getPaymentCardHolderName());
        target.setCardToken(source.getPaytCardByDigitalPaymentSrvc());

        String strExpirationMonth = source.getPaymentCardExpirationMonth();
        if(strExpirationMonth!=null){
            target.setExpirationMonth(NumberUtils.toInt(strExpirationMonth));
        }

        String strExpirationYear = source.getPaymentCardExpirationYear();
        if(strExpirationYear != null){
            target.setExpirationYear((NumberUtils.toInt(strExpirationYear)));
        }

        target.setCardType(source.getPaymentCardType());
        target.setMaskedCardNumber(source.getPaymentCardMaskedNumber());

        DigitalPaymentsTransactionModel transactionModel = source.getDigitalPaymentTransaction();
        DigitalPaymentsTransactionData transactionData = transactionConverter.convert(transactionModel);
        target.setTransaction(transactionData);
    }

    public Converter<DigitalPaymentsTransactionModel, DigitalPaymentsTransactionData> getTransactionConverter() {
        return transactionConverter;
    }

    public void setTransactionConverter(Converter<DigitalPaymentsTransactionModel, DigitalPaymentsTransactionData> transactionConverter) {
        this.transactionConverter = transactionConverter;
    }
}

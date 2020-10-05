/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsRegistrationModel;
import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsRegistrationData;
import de.hybris.platform.cissapdigitalpayment.util.DigitalPaymentsEncryptUtil;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DigitalPaymentsRegistrationPopulator<S extends DigitalPaymentsRegistrationModel, T extends DigitalPaymentsRegistrationData> implements Populator<S, T> {

    private DigitalPaymentsEncryptUtil digitalPaymentsEncryptUtil;

    @Override
    public void populate(S source, T target) throws ConversionException {

        //Encrypt Digital Payments Session Id
        String sessionId = source.getPaymentCardRegistrationSession();
        String encryptedSessionId = getDigitalPaymentsEncryptUtil().encryptWithUser(sessionId);

        //Set Data
        target.setSecureSessionId(encryptedSessionId);
        target.setUrl(source.getPaymentCardRegistrationURL());
    }

    public DigitalPaymentsEncryptUtil getDigitalPaymentsEncryptUtil() {
        return digitalPaymentsEncryptUtil;
    }

    public void setDigitalPaymentsEncryptUtil(DigitalPaymentsEncryptUtil digitalPaymentsEncryptUtil) {
        this.digitalPaymentsEncryptUtil = digitalPaymentsEncryptUtil;
    }
}

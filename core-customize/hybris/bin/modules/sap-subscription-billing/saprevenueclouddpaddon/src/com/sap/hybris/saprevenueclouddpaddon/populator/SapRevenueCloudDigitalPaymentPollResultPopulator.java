/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenueclouddpaddon.populator;

import com.sap.hybris.saprevenueclouddpaddon.constants.SaprevenueclouddpaddonConstants;
import com.sap.hybris.saprevenueclouddpaddon.data.SapRcDigitalPaymentPollResultData;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentTransactionResult;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


public class SapRevenueCloudDigitalPaymentPollResultPopulator implements Populator<CisSapDigitalPaymentPollRegisteredCardResult, SapRcDigitalPaymentPollResultData> {

    Map<String, String> sapDigiPayPollCardStatusMap;

    @Override
    public void populate(CisSapDigitalPaymentPollRegisteredCardResult resultModel,
                         SapRcDigitalPaymentPollResultData resultData) throws ConversionException
    {
        CisSapDigitalPaymentTransactionResult cisSapDigitalPaymentTransactionResult = resultModel.getCisSapDigitalPaymentTransactionResult();
        if(cisSapDigitalPaymentTransactionResult!=null){
            String transactionStatus = cisSapDigitalPaymentTransactionResult.getDigitalPaytTransResult();
            resultData.setTransResult(getSapDigiPayPollCardStatusMap().getOrDefault(transactionStatus, SaprevenueclouddpaddonConstants.STAT_UNKNOWN));
        }

    }

    /**
     * @return sapDigiPayPollCardStatusMap
     */
    public Map<String, String> getSapDigiPayPollCardStatusMap() {
        return sapDigiPayPollCardStatusMap;
    }

    public void setSapDigiPayPollCardStatusMap(Map<String, String> sapDigiPayPollCardStatusMap) {
        this.sapDigiPayPollCardStatusMap = sapDigiPayPollCardStatusMap;
    }
}

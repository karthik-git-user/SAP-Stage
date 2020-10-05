/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.facade.impl;

import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsPollModel;
import de.hybris.platform.cissapdigitalpayment.client.model.DigitalPaymentsRegistrationModel;
import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsPollData;
import de.hybris.platform.cissapdigitalpayment.data.DigitalPaymentsRegistrationData;
import de.hybris.platform.cissapdigitalpayment.facade.SapDigitalPaymentFacade;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.cissapdigitalpayment.util.DigitalPaymentsEncryptUtil;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 *
 * Default implementation of {@link SapDigitalPaymentFacade}
 *
 */
public class DefaultSapDigitalPaymentFacade implements SapDigitalPaymentFacade
{


	private SapDigitalPaymentService sapDigitalPaymentService;
	private Converter<DigitalPaymentsPollModel, DigitalPaymentsPollData> pollModelConverter;
	private Converter<DigitalPaymentsRegistrationModel, DigitalPaymentsRegistrationData> registrationModelConverter;
	private DigitalPaymentsEncryptUtil digitalPaymentsEncryptUtil;

	@Override
	public String getCardRegistrationUrl()
	{
		return getSapDigitalPaymentService().getCardRegistrationUrl();
	}


	@Override
	public void createPollRegisteredCardProcess(final String sessionId)
	{
		getSapDigitalPaymentService().createPollRegisteredCardProcess(sessionId);

	}

	@Override
	public DigitalPaymentsPollData pollAndSave(String encryptedSessionId) {

		//Decrypt Session Id
		String sessionId = getDigitalPaymentsEncryptUtil().decryptWithUser(encryptedSessionId);

		//Process
		DigitalPaymentsPollModel pollModel = getSapDigitalPaymentService().pollAndSave(sessionId);
		return getPollModelConverter().convert(pollModel);
	}

	@Override
	public DigitalPaymentsRegistrationData getRegistrationUrl() {
		DigitalPaymentsRegistrationModel registrationModel = getSapDigitalPaymentService().getRegistrationUrl();
		return getRegistrationModelConverter().convert(registrationModel);
	}

	/**
	 * @return the sapDigitalPaymentService
	 */
	public SapDigitalPaymentService getSapDigitalPaymentService()
	{
		return sapDigitalPaymentService;
	}

	/**
	 * @param sapDigitalPaymentService
	 *           the sapDigitalPaymentService to set
	 */
	public void setSapDigitalPaymentService(final SapDigitalPaymentService sapDigitalPaymentService)
	{
		this.sapDigitalPaymentService = sapDigitalPaymentService;
	}

	public Converter<DigitalPaymentsPollModel, DigitalPaymentsPollData> getPollModelConverter() {
		return pollModelConverter;
	}

	public void setPollModelConverter(Converter<DigitalPaymentsPollModel, DigitalPaymentsPollData> pollModelConverter) {
		this.pollModelConverter = pollModelConverter;
	}

	public Converter<DigitalPaymentsRegistrationModel, DigitalPaymentsRegistrationData> getRegistrationModelConverter() {
		return registrationModelConverter;
	}

	public void setRegistrationModelConverter(Converter<DigitalPaymentsRegistrationModel, DigitalPaymentsRegistrationData> registrationModelConverter) {
		this.registrationModelConverter = registrationModelConverter;
	}

	public DigitalPaymentsEncryptUtil getDigitalPaymentsEncryptUtil() {
		return digitalPaymentsEncryptUtil;
	}

	public void setDigitalPaymentsEncryptUtil(DigitalPaymentsEncryptUtil digitalPaymentsEncryptUtil) {
		this.digitalPaymentsEncryptUtil = digitalPaymentsEncryptUtil;
	}
}

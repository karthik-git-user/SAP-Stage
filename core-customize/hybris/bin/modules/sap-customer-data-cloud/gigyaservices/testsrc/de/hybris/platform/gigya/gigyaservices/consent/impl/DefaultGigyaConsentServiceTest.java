/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.consent.impl;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.consent.dao.ConsentDao;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaConsentServiceTest {

	@InjectMocks
	private DefaultGigyaConsentService gigyaConsentService = new DefaultGigyaConsentService();

	@Mock
	private ModelService modelService;

	@Mock
	private EventService eventService;

	@Mock
	private CommerceConsentService commerceConsentService;

	@Mock
	private CustomerModel customer;

	@Mock
	private ConsentTemplateModel consentTemplate;

	@Mock
	private ConsentModel consent;

	private Date lastConsentModified = Date.from(Instant.parse("2019-01-29T07:22:09.7772992Z"));

	@Mock
	private ConsentDao consentDao;

	@Test
	public void testGiveConsentWhenConsentDoesntExist() {
		Mockito.when(consentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate)).thenReturn(null,
				consent);
		Mockito.when(modelService.create(ConsentModel._TYPECODE)).thenReturn(consent);

		gigyaConsentService.giveConsent(customer, consentTemplate, lastConsentModified);

		Mockito.verify(consent).setConsentGivenDate(Mockito.any());
		Mockito.verify(modelService).save(consent);
		Mockito.verify(eventService).publishEvent(Mockito.any());
	}

	@Test
	public void testGiveConsentWhenConsentExists() {
		Mockito.when(consentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate)).thenReturn(consent);

		gigyaConsentService.giveConsent(customer, consentTemplate, lastConsentModified);

		Mockito.verify(consent).setConsentWithdrawnDate(null);
		Mockito.verify(consent).setConsentGivenDate(Mockito.any());
		Mockito.verify(modelService).save(consent);
		Mockito.verify(eventService).publishEvent(Mockito.any());
	}

	@Test
	public void testGiveConsentWhenConsentLastConsentModifiedDateIsNotNull() {
		
		Mockito.when(consentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate)).thenReturn(null,
				consent);
		Mockito.when(modelService.create(ConsentModel._TYPECODE)).thenReturn(consent);

		Mockito.when(consent.getConsentGivenDate()).thenReturn(Date.from(Instant.parse("2020-01-29T07:22:09.7772992Z")));

		gigyaConsentService.giveConsent(customer, consentTemplate, lastConsentModified);

		Mockito.verify(consent).setConsentGivenDate(Mockito.any());
		Mockito.verify(modelService).save(consent);
		Mockito.verify(eventService).publishEvent(Mockito.any());
	}
	
	@Test
	public void testGiveConsentWhenConsentIsUpdate() {
		Mockito.when(consentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate)).thenReturn(consent);

		Mockito.when(consent.getConsentGivenDate()).thenReturn(lastConsentModified);
		
		gigyaConsentService.giveConsent(customer, consentTemplate, lastConsentModified);

		Mockito.verify(consent).setConsentWithdrawnDate(null);
	}

	@Test
	public void testWithdrawConsentWhenConsentDoesntExists() {
		Mockito.when(consentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate)).thenReturn(null);

		gigyaConsentService.withdrawConsent(customer, consentTemplate, lastConsentModified);

		Mockito.verifyZeroInteractions(modelService);
		Mockito.verifyZeroInteractions(eventService);
	}

	@Test
	public void testWithdrawConsentWhenConsentExists() {
		Mockito.when(consentDao.findConsentByCustomerAndConsentTemplate(customer, consentTemplate)).thenReturn(consent);
		Mockito.when(consent.getConsentWithdrawnDate()).thenReturn(null);

		gigyaConsentService.withdrawConsent(customer, consentTemplate, lastConsentModified);

		Mockito.verify(consent).setConsentWithdrawnDate(Mockito.any());
		Mockito.verify(modelService).save(consent);
		Mockito.verify(eventService).publishEvent(Mockito.any());
	}

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyafacades.consent.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.gigya.socialize.GSObject;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.consent.ConsentModel;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.gigya.gigyaservices.consent.GigyaConsentService;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultGigyaConsentFacadeTest {

	private static final String SAMPLE_PREFERENCE_OBJ_VALID = "{\n" + "    \"terms\": {\n"
			+ "      \"generalTerms\": {\n" + "        \"isConsentGranted\": true,\n"
			+ "        \"docDate\": \"2018-06-14T00:00:00Z\",\n"
			+ "        \"lastConsentModified\": \"2019-01-29T07:07:49.5977404Z\",\n" + "        \"tags\": [],\n"
			+ "        \"customData\": [],\n" + "        \"entitlements\": []\n" + "      },\n"
			+ "      \"mandatory\": {\n" + "        \"electronics\": {\n" + "          \"isConsentGranted\": true,\n"
			+ "          \"docVersion\": 1.0,\n"
			+ "          \"lastConsentModified\": \"2019-01-29T07:00:41.5976217Z\",\n" + "          \"tags\": [],\n"
			+ "          \"customData\": [],\n" + "          \"entitlements\": []\n" + "        }\n" + "      },\n"
			+ "      \"test\": {\n" + "        \"isConsentGranted\": false,\n" + "        \"docVersion\": 1.0,\n"
			+ "        \"lastConsentModified\": \"2019-01-29T07:22:09.7772992Z\",\n" + "        \"tags\": [],\n"
			+ "        \"customData\": [],\n" + "        \"entitlements\": []\n" + "      }\n" + "    }\n" + "  }";

	private static final String SAMPLE_PREFERENCE_OBJ_INVALID = "{\n" + "    \"terms\": {\n"
			+ "      \"generalTerms1\": {\n" + "        \"isConsentGranted\": true,\n"
			+ "        \"docDate\": \"2018-06-14T00:00:00Z\",\n"
			+ "        \"lastConsentModified\": \"2019-01-29T07:07:49.5977404Z\",\n" + "        \"tags\": [],\n"
			+ "        \"customData\": [],\n" + "        \"entitlements\": []\n" + "      },\n" + "      \"test1\": {\n"
			+ "        \"isConsentGranted\": true,\n" + "        \"docVersion\": 1.0,\n"
			+ "        \"lastConsentModified\": \"2019-01-29T07:22:09.7772992Z\",\n" + "        \"tags\": [],\n"
			+ "        \"customData\": [],\n" + "        \"entitlements\": []\n" + "      }\n" + "    }\n" + "  }";
	private static final String SAMPLE_PREFERENCE_OBJ_VALID_VERSION = "{\n" + "    \"terms\": {\n"
			+ "      \"mandatory\": {\n" + "        \"electronics\": {\n" + "          \"isConsentGranted\": true,\n"
			+ "          \"docVersion\": 1.0,\n"
			+ "          \"lastConsentModified\": \"2019-01-29T07:00:41.5976217Z\",\n" + "          \"tags\": [],\n"
			+ "          \"customData\": [],\n" + "          \"entitlements\": []\n" + "        }\n" + "      },\n"
			+ "      \"test\": {\n" + "        \"isConsentGranted\": false,\n" + "        \"docVersion\": 1.0,\n"
			+ "        \"lastConsentModified\": \"2019-01-29T07:22:09.7772992Z\",\n" + "        \"tags\": [],\n"
			+ "        \"customData\": [],\n" + "        \"entitlements\": []\n" + "      }\n" + "    }\n" + "  }";

	@InjectMocks
	private DefaultGigyaConsentFacade gigyaConsentFacade = new DefaultGigyaConsentFacade();

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private GigyaConsentService gigyaConsentService;

	@Mock
	private CustomerModel customer;

	@Mock
	private EmployeeModel employee;

	@Mock
	private GSObject preferences;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private ConsentTemplateModel firstTemplate;

	@Mock
	private ConsentTemplateModel secondTemplate;

	@Mock
	private ConsentTemplateModel thirdTemplate;

	@Mock
	private ConsentModel firstConsent;

	@Mock
	private ConsentModel secondConsent;

	@Mock
	private ConsentModel thirdConsent;

	@Test
	public void testSynchronizeConsentsWhenPreferencesAreNull() {
		gigyaConsentFacade.synchronizeConsents(null, customer);

		Mockito.verifyZeroInteractions(gigyaConsentService);
		Mockito.verifyZeroInteractions(baseSiteService);
		Mockito.verifyZeroInteractions(gigyaConsentService);
	}

	@Test
	public void testSynchronizeConsentsWhenEmployeeModelIsSent() {
		gigyaConsentFacade.synchronizeConsents(preferences, employee);

		Mockito.verifyZeroInteractions(gigyaConsentService);
		Mockito.verifyZeroInteractions(baseSiteService);
		Mockito.verifyZeroInteractions(gigyaConsentService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSynchronizeConsentsWhenBaseSiteDoesntExist() {
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(null);
		Mockito.when(gigyaConsentService.getConsentTemplates(null)).thenThrow(new IllegalArgumentException());

		gigyaConsentFacade.synchronizeConsents(preferences, customer);
	}

	@Test
	public void testSynchronizeConsentsWhenConsentTemplateForSitesDoNotExist() {
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);

		Mockito.when(gigyaConsentService.getConsentTemplates(baseSiteModel)).thenReturn(null);

		gigyaConsentFacade.synchronizeConsents(preferences, customer);

		Mockito.verify(gigyaConsentService, Mockito.times(0)).giveConsent(Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(gigyaConsentService, Mockito.times(0)).withdrawConsent(Mockito.any(), Mockito.any(),
				Mockito.any());
	}

	@Test
	public void testSynchronizeConsentsWhenConsentTemplateForSitesDoExistWithInvalidInfo() throws Exception {
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);

		List<ConsentTemplateModel> consentTemplates = new ArrayList<>();
		consentTemplates.add(firstTemplate);
		consentTemplates.add(secondTemplate);
		Mockito.when(gigyaConsentService.getConsentTemplates(baseSiteModel)).thenReturn(consentTemplates);

		Mockito.when(firstTemplate.getId()).thenReturn("terms.generalTerms");
		Mockito.when(secondTemplate.getId()).thenReturn("terms.mandatory.electronics");

		GSObject gsObject = new GSObject(SAMPLE_PREFERENCE_OBJ_INVALID);

		gigyaConsentFacade.synchronizeConsents(gsObject, customer);

		Mockito.verify(gigyaConsentService, Mockito.times(0)).giveConsent(Mockito.any(), Mockito.any(), Mockito.any());
		Mockito.verify(gigyaConsentService, Mockito.times(0)).withdrawConsent(Mockito.any(), Mockito.any(),
				Mockito.any());
	}

	/*
	 * Create a new entry for missing consent and updates the existing entry for
	 * existing consent for both given and withdrawn
	 */
	@Test
	public void testSynchronizeConsentsWhenConsentTemplateForSitesDoExistWithValidInfoAndNoActiveConsent()
			throws Exception {
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);

		List<ConsentTemplateModel> consentTemplates = new ArrayList<>();
		consentTemplates.add(firstTemplate);
		consentTemplates.add(secondTemplate);
		consentTemplates.add(thirdTemplate);
		Mockito.when(gigyaConsentService.getConsentTemplates(baseSiteModel)).thenReturn(consentTemplates);

		Mockito.when(firstTemplate.getId()).thenReturn("terms.generalTerms");
		Mockito.when(secondTemplate.getId()).thenReturn("terms.test");
		Mockito.when(thirdTemplate.getId()).thenReturn("terms.mandatory.electronics");
		
		Mockito.when(firstTemplate.getVersion()).thenReturn(0);
		Mockito.when(secondTemplate.getVersion()).thenReturn(1);
		Mockito.when(thirdTemplate.getVersion()).thenReturn(1);

		GSObject gsObject = new GSObject(SAMPLE_PREFERENCE_OBJ_VALID);

		Mockito.when(gigyaConsentService.getActiveConsent(customer, firstTemplate)).thenReturn(null);
		Mockito.when(gigyaConsentService.getActiveConsent(customer, secondTemplate)).thenReturn(secondConsent);
		Mockito.when(gigyaConsentService.getActiveConsent(customer, thirdTemplate)).thenReturn(thirdConsent);

		Mockito.when(thirdConsent.getConsentWithdrawnDate()).thenReturn(new Date());

		gigyaConsentFacade.synchronizeConsents(gsObject, customer);

		
		Mockito.verify(gigyaConsentService, Mockito.times(0)).giveConsent(customer, firstTemplate,
	    Date.from(Instant.parse("2019-01-29T07:07:49.5977404Z")));
		 
		Mockito.verify(gigyaConsentService).withdrawConsent(customer, secondTemplate,
				Date.from(Instant.parse("2019-01-29T07:22:09.7772992Z")));
		Mockito.verify(gigyaConsentService).giveConsent(customer, thirdTemplate,
				Date.from(Instant.parse("2019-01-29T07:00:41.5976217Z")));
	}
	
	/*
	 * Create a new entry for consent with an updated version 
	 * Consent Templates with previous version exists in commerce
	 */
	@Test
	public void testSynchronizationConsentsWhenVersionOfConsentTemplateAndConsentDiffers() throws Exception{
		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSiteModel);
		
		List<ConsentTemplateModel> consentTemplates = new ArrayList<ConsentTemplateModel>();
		consentTemplates.add(firstTemplate);
		consentTemplates.add(secondTemplate);
		Mockito.when(gigyaConsentService.getConsentTemplates(baseSiteModel)).thenReturn(consentTemplates);
		Mockito.when(firstTemplate.getId()).thenReturn("terms.mandatory.electronics");
		Mockito.when(secondTemplate.getId()).thenReturn("terms.test");
		Mockito.when(firstTemplate.getVersion()).thenReturn(1); //docVersion of terms.mandatory would be 1
		Mockito.when(secondTemplate.getVersion()).thenReturn(2); //docVersion of terms.test would be 1
		GSObject gsObject = new GSObject(SAMPLE_PREFERENCE_OBJ_VALID_VERSION);
		gigyaConsentFacade.synchronizeConsents(gsObject, customer);
		
		Mockito.verify(gigyaConsentService).giveConsent(customer, firstTemplate, 
				Date.from(Instant.parse("2019-01-29T07:00:41.5976217Z")));
		Mockito.verify(gigyaConsentService, Mockito.times(0)).giveConsent(customer, secondTemplate,
			    Date.from(Instant.parse("2019-01-29T07:00:41.5976217Z")));
		
		
	}

}

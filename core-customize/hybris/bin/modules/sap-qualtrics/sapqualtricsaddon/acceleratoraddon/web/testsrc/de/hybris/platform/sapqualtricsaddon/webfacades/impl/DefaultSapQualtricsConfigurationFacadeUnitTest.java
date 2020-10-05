/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapqualtricsaddon.webfacades.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sapqualtricsaddon.SapQualtricsConfigurationData;
import de.hybris.platform.sapqualtricsaddon.webfacades.impl.DefaultSapQualtricsConfigurationFacade;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapQualtricsConfigurationFacadeUnitTest {
	
	
	@Mock
	private CommerceConsentService commerceConsentService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private BaseSiteService baseSiteService;
	
	@Mock
	private DestinationService<ConsumedDestinationModel> destinationService;
	
	@InjectMocks
	private DefaultSapQualtricsConfigurationFacade sapQualtricsConfigurationFacade;
	
	
	CustomerModel someCustomer;
	BaseSiteModel someBaseSite;
	ConsentTemplateModel someTemplate;
	
	ConsumedDestinationModel someDestination;
	
	
	
	
	@Before
	public void setUp() 
	{
		someCustomer = new CustomerModel();
		someBaseSite = new BaseSiteModel();
		someTemplate = new ConsentTemplateModel();
		someDestination = new ConsumedDestinationModel();
		Map<String,String> props = new HashMap<>();
		
		someDestination.setAdditionalProperties(props);
				
		when(userService.getCurrentUser()).thenReturn(someCustomer);
		when(baseSiteService.getCurrentBaseSite()).thenReturn(someBaseSite);
		when(commerceConsentService.getLatestConsentTemplate("QUALTRICS_ENABLE",someBaseSite)).thenReturn(someTemplate);
		
	}
	
	@Test
	public void testRetrieveQualtricsConfigurationWhereNoneIsMaintained() 
	{
		when(destinationService.getDestinationsByDestinationTargetId("qualtricsDestinationTarget")).thenReturn(null);
		
		assertThatThrownBy(() -> sapQualtricsConfigurationFacade.getQualtricsConfiguration())
		.isInstanceOf(NullPointerException.class);
		
	}
	
	
	@Test
	public void testThrowErrorIfTheConfigurationIsPresentButProjectIdIsEmpty() 
	{
		Map<String,String> props = new HashMap<>();
		props.put("projectId", null);
		props.put("brandId", null);
		someDestination.setAdditionalProperties(props);
		when(destinationService.getDestinationsByDestinationTargetId("qualtricsDestinationTarget")).thenReturn(Arrays.asList(someDestination));
		
		assertThatThrownBy(() -> sapQualtricsConfigurationFacade.getQualtricsConfiguration())
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Cannot create Qualtrics ContentId for null or empty projectID");
	}
	
	@Test
	public void testThrowErrorIfTheConfigurationIsPresentButBrandIdIsEmpty() 
	{
		Map<String,String> props = new HashMap<>();
		props.put("projectId", "some_project_ID");
		someDestination.setAdditionalProperties(props);
		
		when(destinationService.getDestinationsByDestinationTargetId("qualtricsDestinationTarget")).thenReturn(Arrays.asList(someDestination));
		
		assertThatThrownBy(() -> sapQualtricsConfigurationFacade.getQualtricsConfiguration())
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Cannot create Qualtrics intercept URL for null or empty Brand ID");
	}
	
	@Test
	public void testVerifyQualtricsConfigurationReturnValueFormat()
	{
		Map<String,String> props = new HashMap<>();
		props.put("projectId", "Proj_Id");
		props.put("brandId", "brand_id");
		someDestination.setAdditionalProperties(props);
		
		when(destinationService.getDestinationsByDestinationTargetId("qualtricsDestinationTarget")).thenReturn(Arrays.asList(someDestination));
		
		List<SapQualtricsConfigurationData> configData = sapQualtricsConfigurationFacade.getQualtricsConfiguration();
		assertThat(configData).hasSize(1);
		assertThat(configData.get(0).getProjectId()).isEqualTo("QSI_S_Proj_Id");
		assertThat(configData.get(0).getContentId()).isEqualTo("Proj_Id");
		assertThat(configData.get(0).getProjectUrl()).isEqualTo("https://projid-brand_id.siteintercept.qualtrics.com/WRSiteInterceptEngine/?Q_ZID=Proj_Id");
	}
	
	
	@Test
	public void testCheckIfConsentIsProperlyCaptured()
	{
		when(commerceConsentService.hasEffectivelyActiveConsent(someCustomer, someTemplate)).thenReturn(true);
		boolean consentGiven = sapQualtricsConfigurationFacade.isLoggedInCustomerConsentGiven();
		assertThat(consentGiven).isTrue();
	}
	
	
	
	

}

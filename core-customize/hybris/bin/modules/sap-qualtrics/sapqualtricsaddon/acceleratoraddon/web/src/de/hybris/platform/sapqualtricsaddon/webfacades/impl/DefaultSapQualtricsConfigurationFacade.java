/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapqualtricsaddon.webfacades.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.google.common.base.Preconditions;

import de.hybris.platform.apiregistryservices.jalo.DestinationTarget;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.consent.CommerceConsentService;
import de.hybris.platform.commerceservices.model.consent.ConsentTemplateModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sapqualtricsaddon.SapQualtricsConfigurationData;
import de.hybris.platform.sapqualtricsaddon.constants.SapqualtricsaddonConstants;
import de.hybris.platform.sapqualtricsaddon.constants.SapqualtricsaddonWebConstants;
import de.hybris.platform.sapqualtricsaddon.webfacades.SapQualtricsConfigurationFacade;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

/**
 * 
 * Default implementation for {@link SapQualtricsConfigurationFacade}.
 *
 */
public class DefaultSapQualtricsConfigurationFacade implements SapQualtricsConfigurationFacade {

	private CommerceConsentService commerceConsentService;
	private UserService userService;
	private BaseSiteService baseSiteService;
	private DestinationService<ConsumedDestinationModel> destinationService;
	

	/**
	 * Get all the {@link ConsumedDestinationModel} with {@link DestinationTargetModel} id qualtricsDestinationTarget , formats it to fit into
	 * qualtrics javascript template. Reads all the {@link ConsumedDestination} with {@code DestinationTarget} id 'qualtricsDestinationTarget'.
	 * Multiple such configurations are can be handled. If projectId or brandId is missing, the method throws {@link IllegalArgumentException}.
	 *  
	 *  @return {@link List<SapQualtricsConfigurationData>} list of qualtrics configurations with content ID, project ID and project URL
	 */
	@Override
	public List<SapQualtricsConfigurationData> getQualtricsConfiguration() {

		final List<SapQualtricsConfigurationData> qcDataList = new ArrayList<>();
		List<ConsumedDestinationModel> qcds = destinationService.getDestinationsByDestinationTargetId(SapqualtricsaddonWebConstants.QUALTRICS_DESTINATION_TARGET_ID);
		for(ConsumedDestinationModel qcd : qcds) 
		{
			final SapQualtricsConfigurationData qcData = new SapQualtricsConfigurationData();
			Map<String,String> props = qcd.getAdditionalProperties();
			if(MapUtils.isNotEmpty(props)) {
				String projectId = props.get(SapqualtricsaddonWebConstants.QUALTRICS_PROJECT_ID);
				String brandId = props.get(SapqualtricsaddonWebConstants.QUALTRICS_BRAND_ID);
				qcData.setContentId(getQualtricsSiteInterceptContentId(projectId));
				qcData.setProjectId(getQualtricsSiteInterceptProjectId(projectId));
				qcData.setProjectUrl(getQualtricsSiteInterceptProjectUrl(projectId,brandId));
			}
			qcData.setActive(qcd.isActive());
			qcDataList.add(qcData);
		}
		
		return qcDataList;

	}
	
	/**
	 * Check if the logged in customer has given consent for sending Qualtrics feedback surveys. If there is no consent given by the customer or
	 * if the customer has withdrawn the consent, return false. If there is an active consent available for the logged in customer, return true.
	 * 
	 * @return logged in customer consent status
	 */
	@Override
	public boolean isLoggedInCustomerConsentGiven() {
		UserModel currentUser = userService.getCurrentUser();

		BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		ConsentTemplateModel qualtricsConsentTemplate = commerceConsentService.getLatestConsentTemplate(SapqualtricsaddonWebConstants.QUALTRICS_CONSENT_ID, baseSite);
		
		return commerceConsentService.hasEffectivelyActiveConsent((CustomerModel) currentUser, qualtricsConsentTemplate);
	}


	/**
	 * 
	 * @param projectId Project ID
	 * @return Qualtrics ContentID for the page
	 */
	protected String getQualtricsSiteInterceptContentId(final String projectId) {
		Preconditions.checkArgument(projectId != null,
				"Cannot create Qualtrics ContentId for null or empty projectID");
		return projectId;
	}

	/**
	 * 
	 * @param projectId Project ID
	 * @return Formatted project ID
	 */
	protected String getQualtricsSiteInterceptProjectId(final String projectId) {
		Preconditions.checkArgument(projectId != null,
				"Cannot create Qualtrics Project ID for null or empty projectID");
		return String.format("QSI_S_%s", projectId);

	}

	/**
	 * 
	 * @param projectId project ID
	 * @param brandId   brand ID
	 * @return project URL for Qualtrrics App/Website Feedback project
	 */
	protected String getQualtricsSiteInterceptProjectUrl(final String projectId, final String brandId) {
		Preconditions.checkArgument(projectId != null ,
				"Cannot create Qualtrics intercept URL for null or empty Project ID");
		Preconditions.checkArgument(brandId != null ,
				"Cannot create Qualtrics intercept URL for null or empty Brand ID");

		return String.format("https://%s-%s.siteintercept.qualtrics.com/WRSiteInterceptEngine/?Q_ZID=%s",
				projectId.replaceAll("_", "").toLowerCase(), brandId, projectId);
	}




	/**
	 * @param commerceConsentService the commerceConsentService to set
	 */
	public void setCommerceConsentService(CommerceConsentService commerceConsentService) {
		this.commerceConsentService = commerceConsentService;
	}



	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}



	/**
	 * @param baseSiteService the baseSiteService to set
	 */
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @param destinationService the destinationService to set
	 */
	public void setDestinationService(DestinationService<ConsumedDestinationModel> destinationService) {
		this.destinationService = destinationService;
	}
	
	
	
	

	
}

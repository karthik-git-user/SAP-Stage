/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sapqualtricsaddon.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.apiregistryservices.jalo.ConsumedDestination;
import de.hybris.platform.apiregistryservices.jalo.DestinationTarget;
import de.hybris.platform.sapqualtricsaddon.SapQualtricsConfigurationData;
import de.hybris.platform.sapqualtricsaddon.constants.SapqualtricsaddonWebConstants;
import de.hybris.platform.sapqualtricsaddon.model.SAPQualtricsSiteComponentModel;
import de.hybris.platform.sapqualtricsaddon.webfacades.SapQualtricsConfigurationFacade;

/**
 * {@link SapQualtricsSiteComponentRenderer} class fills the qualtrics configuration. Loads the all the {@code ConsumedDestination}
 * with {@code DestinationTarget} with ID 'qualtricsDestinationTarget'.Multiple such qualtrics configurations can be handled by the renderer.
 * If there are no configurations available, empty values fill be assigned to the variables. If any of the configuration values are missing, 
 * the caller will throw {@link IllegalArgumentException} error which is handled.
 * 
 */
public class SapQualtricsSiteComponentRenderer
		extends DefaultAddOnCMSComponentRenderer<SAPQualtricsSiteComponentModel> {

	private SapQualtricsConfigurationFacade sapQualtricsConfigurationFacade;
	

	private static final Logger LOG = Logger.getLogger(SapQualtricsSiteComponentRenderer.class);

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext,
			final SAPQualtricsSiteComponentModel component) 
	{
		final List<String> contentIds = new ArrayList<>();
		final Map<String, Object> variables = super.getVariablesToExpose(pageContext, component);
		ObjectMapper om = new ObjectMapper();
		
		
		try 
		{
			fillEmptyVariableValues(variables);
			
			final List<SapQualtricsConfigurationData> qualtricsConfigListData = sapQualtricsConfigurationFacade.getQualtricsConfiguration();
			qualtricsConfigListData.forEach(data -> contentIds.add(data.getContentId()));
			variables.put(SapqualtricsaddonWebConstants.QUALTRICS_CONTENT_ID, contentIds);
			
			variables.put(SapqualtricsaddonWebConstants.QUALTRICS_CONSENT, sapQualtricsConfigurationFacade.isLoggedInCustomerConsentGiven());
			variables.put(SapqualtricsaddonWebConstants.QUALTRICS_CONFIGURATION, om.writeValueAsString(qualtricsConfigListData));
			
			
		} 
		catch (IllegalArgumentException ex)
		{
			fillEmptyVariableValues(variables);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Some of the Qualtrics configurations are empty" + ex.getMessage(), ex);
			}
			LOG.error("Some of the qualtrics configurations are empty");
			
		}
		catch (JsonProcessingException ex) 
		{
			
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Converting qualtrics configuration data to list failed" + ex.getMessage(), ex);
			}
			LOG.error("Error while converting qualtrics configuration data");
			
		}

		return variables;

	}
	
	/**
	 * 
	 * @param variables Accepts a {@link Map<String,Object} and fill with empty values. 
	 * This ensure the jsp will not break and will load with empty value.
	 */
	private void fillEmptyVariableValues(Map<String, Object> variables) {
		variables.put(SapqualtricsaddonWebConstants.QUALTRICS_CONTENT_ID, "");
		variables.put(SapqualtricsaddonWebConstants.QUALTRICS_CONSENT, "");
		variables.put(SapqualtricsaddonWebConstants.QUALTRICS_CONFIGURATION, "");
	}
	

	/**
	 * @param sapQualtricsConfigurationFacade the sapQualtricsConfigurationFacade to
	 *                                        set
	 */
	public void setSapQualtricsConfigurationFacade(
			final SapQualtricsConfigurationFacade sapQualtricsConfigurationFacade) 
	{
		this.sapQualtricsConfigurationFacade = sapQualtricsConfigurationFacade;
	}

}

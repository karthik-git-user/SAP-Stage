/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.renderers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.gigya.gigyaloginaddon.model.GigyaRaasComponentModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionLead;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

public class GigyaRaasComponentRenderer extends DefaultAddOnCMSComponentRenderer<GigyaRaasComponentModel> {

	private static final Logger LOG = Logger.getLogger(GigyaRaasComponentRenderer.class);
	private static final String SESSION_EXPIRATION = "sessionExpiration";
	private static final int DEFAULT_SESSION_TIMEOUT_VALUE = 3600;

	private UserService userService;

	private ConfigurationService configurationService;

	private BaseSiteService baseSiteService;

	@Override
	protected Map<String, Object> getVariablesToExpose(final PageContext pageContext,
			final GigyaRaasComponentModel component) {
		final Map<String, Object> variables = super.getVariablesToExpose(pageContext, component);

		final HashMap<String, Object> raasConfig = new HashMap<>();
		final ObjectMapper mapper = getHtmlSafeObjectMapper();

		raasConfig.put("screenSet", component.getScreenSet());
		raasConfig.put("startScreen", component.getStartScreen());
		raasConfig.put("profileEdit", component.getProfileEdit());
		setSessionExpiration(raasConfig);

		if (component.getEmbed()) {
			raasConfig.put("containerID", component.getContainerID());
			variables.put("containerID", component.getContainerID());
		}

		if (StringUtils.isNotBlank(component.getAdvancedConfiguration())) {
			HashMap<String, Object> advConfig = new HashMap<>();
			try {
				advConfig = mapper.readValue(component.getAdvancedConfiguration(), HashMap.class);
				raasConfig.putAll(advConfig);
			} catch (final IOException e) {
				LOG.error("Exception in converting json string to map" + e);
			}
		}

		final boolean isAnonymousUser = userService.isAnonymousUser(userService.getCurrentUser());
		try {
			variables.put("id", component.getUid().replaceAll("[^A-Za-z0-9]", ""));
			variables.put("gigyaRaas", mapper.writeValueAsString(raasConfig));

			final Boolean show;
			if (isAnonymousUser) {
				show = component.getShowAnonymous();
			} else {
				show = component.getShowLoggedIn();
			}
			variables.put("show", show);
			variables.put("profileEdit", component.getProfileEdit());
		} catch (final IOException e) {
			LOG.error("Exception in converting map to json string" + e);
		}

		variables.put("authenticated", !isAnonymousUser);
		return variables;
	}

	/**
	 * Sets session expiration based on 'gigya.default.session.management' property
	 */
	private void setSessionExpiration(final HashMap<String, Object> raasConfig) {

		final BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
		if (baseSite != null) {
			final GigyaConfigModel gigyaConfig = baseSite.getGigyaConfig();
			if (gigyaConfig != null) {
				configureSessionExpiration(raasConfig, gigyaConfig);
			}
		}
	}

	private void configureSessionExpiration(final HashMap<String, Object> raasConfig,
			final GigyaConfigModel gigyaConfig) {
		final GigyaSessionConfigModel sessionConfig = gigyaConfig.getGigyaSessionConfig();
		if (sessionConfig != null && GigyaSessionLead.GIGYA == sessionConfig.getSessionLead()) {
			switch (sessionConfig.getSessionType()) {
			case FIXED:
				raasConfig.put(SESSION_EXPIRATION, sessionConfig.getSessionDuration());
				break;
			case SLIDING:
				raasConfig.put(SESSION_EXPIRATION, -1);
				break;
			case BROWSERCLOSED:
				raasConfig.put(SESSION_EXPIRATION, 0);
				break;
			default:
				break;
			}
		} else {
			raasConfig.put(SESSION_EXPIRATION, configurationService.getConfiguration().getInt("default.session.timeout",
					DEFAULT_SESSION_TIMEOUT_VALUE));
		}
	}

	/**
	 * Method that returns an instance of safe ObjectMapper which escapes additional
	 * unsafe characters
	 * 
	 * @return ObjectMapper instance
	 */
	private ObjectMapper getHtmlSafeObjectMapper() {
		final ObjectMapper mapper = new ObjectMapper();
		mapper.getFactory().setCharacterEscapes(new HtmlSafeCharacterEscapes());
		mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
		return mapper;
	}

	/**
	 * HTML Safe Character Escapes class which ensures that certain additional
	 * characters are escaped.
	 */
	private static class HtmlSafeCharacterEscapes extends CharacterEscapes {

		private static final long serialVersionUID = 1027372137351135038L;

		private int[] asciiEscapes;

		public HtmlSafeCharacterEscapes() {
			asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
			// enable additional characters for escape
			asciiEscapes['<'] = CharacterEscapes.ESCAPE_STANDARD;
			asciiEscapes['>'] = CharacterEscapes.ESCAPE_STANDARD;
			asciiEscapes['&'] = CharacterEscapes.ESCAPE_STANDARD;
		}

		@Override
		public int[] getEscapeCodesForAscii() {
			return asciiEscapes;
		}

		@Override
		public SerializableString getEscapeSequence(int ch) {
			return null;
		}
	}

	public UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}

}

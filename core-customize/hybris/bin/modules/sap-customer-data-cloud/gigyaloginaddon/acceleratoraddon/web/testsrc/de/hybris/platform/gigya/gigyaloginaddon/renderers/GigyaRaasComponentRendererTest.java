/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.renderers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.PageContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaloginaddon.model.GigyaRaasComponentModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionLead;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSessionType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaSessionConfigModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaRaasComponentRendererTest {

	@InjectMocks
	private final GigyaRaasComponentRenderer renderer = new GigyaRaasComponentRenderer();

	@Mock
	private UserService userService;

	@Mock
	private CMSComponentService cmsComponentService;

	@Mock
	private GigyaRaasComponentModel gigyaRaasComponent;

	@Mock
	private UserModel user;

	@Mock
	private PageContext pageContext;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private GigyaSessionConfigModel sessionConfig;

	@Test
	public void testGetVariablesToExpose() {
		renderer.setCmsComponentService(cmsComponentService);
		Mockito.when(cmsComponentService.getReadableEditorProperties(Mockito.any()))
				.thenReturn(new ArrayList<String>());
		Mockito.when(gigyaRaasComponent.getUid()).thenReturn("sample-uid");
		Mockito.when(gigyaRaasComponent.getScreenSet()).thenReturn("screen-set");
		Mockito.when(gigyaRaasComponent.getStartScreen()).thenReturn("start-screen");
		Mockito.when(gigyaRaasComponent.getProfileEdit()).thenReturn(Boolean.TRUE);

		Mockito.when(gigyaRaasComponent.getEmbed()).thenReturn(Boolean.FALSE);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(userService.isAnonymousUser(user)).thenReturn(Boolean.TRUE);
		Mockito.when(gigyaRaasComponent.getShowAnonymous()).thenReturn(Boolean.TRUE);

		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(gigyaConfig.getGigyaSessionConfig()).thenReturn(sessionConfig);
		Mockito.when(sessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.when(sessionConfig.getSessionType()).thenReturn(GigyaSessionType.SLIDING);

		final Map<String, Object> variables = renderer.getVariablesToExpose(pageContext, gigyaRaasComponent);

		Assert.assertNotNull(variables);
		Assert.assertNotNull(variables.get("gigyaRaas"));
		Assert.assertEquals(
				"{\"screenSet\":\"screen-set\",\"profileEdit\":true,\"sessionExpiration\":-1,\"startScreen\":\"start-screen\"}",
				variables.get("gigyaRaas"));
		Assert.assertEquals("sampleuid", variables.get("id"));
		Assert.assertEquals(Boolean.TRUE, variables.get("show"));
		Assert.assertEquals(Boolean.TRUE, variables.get("profileEdit"));
	}

	@Test
	public void testGetVariablesToExposeWithVulnerableData() {
		renderer.setCmsComponentService(cmsComponentService);
		Mockito.when(cmsComponentService.getReadableEditorProperties(Mockito.any()))
				.thenReturn(new ArrayList<String>());
		Mockito.when(gigyaRaasComponent.getUid()).thenReturn("sample-uid");
		Mockito.when(gigyaRaasComponent.getScreenSet()).thenReturn("<script>alert(1);</script>");
		Mockito.when(gigyaRaasComponent.getStartScreen()).thenReturn("& \" ' \\");
		Mockito.when(gigyaRaasComponent.getProfileEdit()).thenReturn(Boolean.TRUE);

		Mockito.when(gigyaRaasComponent.getEmbed()).thenReturn(Boolean.FALSE);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(userService.isAnonymousUser(user)).thenReturn(Boolean.TRUE);
		Mockito.when(gigyaRaasComponent.getShowAnonymous()).thenReturn(Boolean.TRUE);

		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(gigyaConfig.getGigyaSessionConfig()).thenReturn(sessionConfig);
		Mockito.when(sessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.when(sessionConfig.getSessionType()).thenReturn(GigyaSessionType.SLIDING);

		final Map<String, Object> variablesToExpose = renderer.getVariablesToExpose(pageContext, gigyaRaasComponent);

		Assert.assertNotNull(variablesToExpose);
		Assert.assertNotNull(variablesToExpose.get("gigyaRaas"));
		Assert.assertEquals(
				"{\"screenSet\":\"\\u003Cscript\\u003Ealert(1);\\u003C/script\\u003E\",\"profileEdit\":true,\"sessionExpiration\":-1,\"startScreen\":\"\\u0026 \\\" ' \\\\\"}",
				variablesToExpose.get("gigyaRaas"));
		Assert.assertEquals("sampleuid", variablesToExpose.get("id"));
		Assert.assertEquals(Boolean.TRUE, variablesToExpose.get("show"));
		Assert.assertEquals(Boolean.TRUE, variablesToExpose.get("profileEdit"));
	}

	@Test
	public void testGetVariablesToExposeWithNonAsciiCharacters() {
		renderer.setCmsComponentService(cmsComponentService);
		Mockito.when(cmsComponentService.getReadableEditorProperties(Mockito.any()))
				.thenReturn(new ArrayList<String>());
		Mockito.when(gigyaRaasComponent.getUid()).thenReturn("sample-uid");
		Mockito.when(gigyaRaasComponent.getScreenSet()).thenReturn("<script>alert(1);</script>");
		Mockito.when(gigyaRaasComponent.getStartScreen()).thenReturn("äöüßÄÖÜ");
		Mockito.when(gigyaRaasComponent.getProfileEdit()).thenReturn(Boolean.TRUE);

		Mockito.when(gigyaRaasComponent.getEmbed()).thenReturn(Boolean.FALSE);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(userService.isAnonymousUser(user)).thenReturn(Boolean.TRUE);
		Mockito.when(gigyaRaasComponent.getShowAnonymous()).thenReturn(Boolean.TRUE);

		Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
		Mockito.when(baseSite.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(gigyaConfig.getGigyaSessionConfig()).thenReturn(sessionConfig);
		Mockito.when(sessionConfig.getSessionLead()).thenReturn(GigyaSessionLead.GIGYA);
		Mockito.when(sessionConfig.getSessionType()).thenReturn(GigyaSessionType.SLIDING);

		final Map<String, Object> variablesToExpose = renderer.getVariablesToExpose(pageContext, gigyaRaasComponent);

		Assert.assertNotNull(variablesToExpose);
		Assert.assertNotNull(variablesToExpose.get("gigyaRaas"));
		Assert.assertEquals(
				"{\"screenSet\":\"\\u003Cscript\\u003Ealert(1);\\u003C/script\\u003E\",\"profileEdit\":true,\"sessionExpiration\":-1,\"startScreen\":\"\\u00E4\\u00F6\\u00FC\\u00DF\\u00C4\\u00D6\\u00DC\"}",
				variablesToExpose.get("gigyaRaas"));
		Assert.assertEquals("sampleuid", variablesToExpose.get("id"));
		Assert.assertEquals(Boolean.TRUE, variablesToExpose.get("show"));
		Assert.assertEquals(Boolean.TRUE, variablesToExpose.get("profileEdit"));
	}
}

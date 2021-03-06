/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.segmentation.swagger;

import static com.hybris.ymkt.segmentation.constants.SapymktsegmentationwebservicesConstants.EXTENSIONNAME;

import de.hybris.platform.cmswebservices.constants.CmswebservicesConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.ClientCredentialsGrant;
import springfox.documentation.service.OAuth;
import springfox.documentation.service.ResourceOwnerPasswordCredentialsGrant;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Component
public class SwaggerConfig
{
	public static final String AUTHORIZATION_SCOPE_PROPERTY = EXTENSIONNAME + ".oauth.scope";
	public static final String LICENSE_URL_PROPERTY = EXTENSIONNAME + ".license.url";
	public static final String TERMS_OF_SERVICE_URL_PROPERTY = EXTENSIONNAME + ".terms.of.service.url";
	public static final String LICENSE_PROPERTY = EXTENSIONNAME + ".licence";
	public static final String DOCUMENTATION_DESC_PROPERTY = EXTENSIONNAME + ".documentation.desc";
	public static final String DOCUMENTATION_TITLE_PROPERTY = EXTENSIONNAME + ".documentation.title";
	public static final String API_VERSION = "1.0.0";

	public static final String AUTHORIZATION_URL = "/authorizationserver/oauth/token";
	public static final String CLIENT_CREDENTIAL_AUTHORIZATION_NAME = "oauth2_client_credentials";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Bean
	public Docket apiDocumentation()
	{
		return new Docket(DocumentationType.SWAGGER_2)//
				.apiInfo(apiInfo())//
				.select()//
				.paths(PathSelectors.any())//
				.build()//
				.securitySchemes(Arrays.asList(clientCredentialFlow(), passwordFlow()))//
				.securityContexts(Arrays.asList(oauthSecurityContext()))//
				.tags(new Tag("Sample", "Sample Methods"));
	}

	protected ApiInfo apiInfo()
	{
		return new ApiInfoBuilder()//
				.title(getPropertyValue(DOCUMENTATION_TITLE_PROPERTY))//
				.description(getPropertyValue(DOCUMENTATION_DESC_PROPERTY))//
				.termsOfServiceUrl(getPropertyValue(TERMS_OF_SERVICE_URL_PROPERTY))//
				.license(getPropertyValue(LICENSE_PROPERTY))//
				.licenseUrl(getPropertyValue(LICENSE_URL_PROPERTY))//
				.version(API_VERSION)//
				.build();
	}

	protected OAuth passwordFlow()
	{
		return new OAuth(CmswebservicesConstants.PASSWORD_AUTHORIZATION_NAME, //
				Arrays.asList(new AuthorizationScope(getPropertyValue(AUTHORIZATION_SCOPE_PROPERTY), "")), //
				Arrays.asList(new ResourceOwnerPasswordCredentialsGrant(AUTHORIZATION_URL)));
	}

	protected OAuth clientCredentialFlow()
	{
		return new OAuth(CLIENT_CREDENTIAL_AUTHORIZATION_NAME, //
				Arrays.asList(new AuthorizationScope(getPropertyValue(AUTHORIZATION_SCOPE_PROPERTY), "")), //
				Arrays.asList(new ClientCredentialsGrant(AUTHORIZATION_URL)));
	}

	protected String getPropertyValue(final String propertyName)
	{
		return this.configurationService.getConfiguration().getString(propertyName);
	}

	protected SecurityContext oauthSecurityContext()
	{
		return SecurityContext.builder().securityReferences(securityReferences()).forPaths(PathSelectors.any()).build();
	}

	protected List<SecurityReference> securityReferences()
	{
		final AuthorizationScope[] as = {};
		final SecurityReference sr1 = new SecurityReference(CmswebservicesConstants.PASSWORD_AUTHORIZATION_NAME, as);
		final SecurityReference sr2 = new SecurityReference(CLIENT_CREDENTIAL_AUTHORIZATION_NAME, as);
		return Arrays.asList(sr1, sr2);
	}

}
/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bservices.auth.impl;

import de.hybris.platform.b2b.company.B2BCommerceUserService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.gigya.gigyab2bservices.auth.GigyaAuthService;
import de.hybris.platform.gigya.gigyab2bservices.constants.Gigyab2bservicesConstants;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaActionsAccessData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAssetsData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAuthData;
import de.hybris.platform.gigya.gigyab2bservices.data.GigyaAuthRequestData;
import de.hybris.platform.gigya.gigyab2bservices.token.GigyaTokenGenerator;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.site.BaseSiteService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 * Default implementation of GigyaAuthService which calls the SAP CDC's Authorization API to fetch Authorizations for a
 * Customer belonging to an organization
 */
public class DefaultGigyaAuthService implements GigyaAuthService
{

	private static final Logger LOG = Logger.getLogger(DefaultGigyaAuthService.class);
	private static final List<String> USER_AUTHORIZATIONS = Arrays.asList("b2bcustomergroup", "b2bmanagergroup", "b2bapprovergroup",
			"b2badmingroup");

	private B2BCommerceUserService b2bCommerceUserService;

	private Converter<CustomerModel, GigyaAuthRequestData> gigyaAuthRequestConverter;

	private BaseSiteService baseSiteService;

	private GigyaTokenGenerator gigyaTokenGenerator;

	private UserService userService;

	private RestTemplate restTemplate;

	@Override
	public void assignAuthorisationsToCustomer(final CustomerModel customer)
	{
		final ResponseEntity response = fetchAuthorizations(customer);

		final GigyaAuthData authData = (GigyaAuthData) response.getBody();
		final GigyaAssetsData assetData = authData.getAssets();

		if (validateAssetDataExists(authData, assetData) && validateFunctionalRolesAndActionsExist(assetData))
		{
			final List<GigyaActionsAccessData> accessList = assetData.getAssetTemplates().getCommerceFunctionalRoles().getActions()
					.getAccessList();
			customer.setGroups(getUpdatedGroupsForCustomer(accessList, customer));
		}
	}

	private Set<PrincipalGroupModel> getUpdatedGroupsForCustomer(final List<GigyaActionsAccessData> accessList,
			final CustomerModel customer)
	{
		final Set<PrincipalGroupModel> groups = new HashSet(customer.getGroups());
		accessList.forEach(accessItem -> {
			if (accessItem.getAttributes().containsKey(Gigyab2bservicesConstants.ROLE_NAME_ATTR))
			{
				final List<Object> roles = accessItem.getAttributes().get(Gigyab2bservicesConstants.ROLE_NAME_ATTR);
				roles.forEach(role -> {

					try
					{
						final UserGroupModel userGroup = getUserService().getUserGroupForUID((String) role);
						groups.add(userGroup);
					}
					catch (final Exception e)
					{
						LOG.error("No such usergroup " + role, e);
					}
				});
			}
		});
		return groups;
	}

	private ResponseEntity fetchAuthorizations(final CustomerModel customer)
	{
		// Make a call to CDC for default b2b unit to get authorizations for it
		final BaseSiteModel baseSite = getBaseSiteService().getCurrentBaseSite();
		final GigyaConfigModel gigyaConfig = baseSite.getGigyaConfig();

		final String token = getGigyaTokenGenerator().generate(gigyaConfig.getAuthRequestKey(), gigyaConfig.getAuthRequestSecret(),
				gigyaConfig.getAuthRequestTokenValidity());

		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		headers.add("Content-Type", ContentType.APPLICATION_JSON.toString());

		final HttpEntity<String> entity = createHttpEntity(customer, headers);
		try
		{
			return getRestTemplate().exchange(gigyaConfig.getAuthorizationUrl(), HttpMethod.POST, entity, GigyaAuthData.class);
		}
		catch (final RestClientException e)
		{
			LOG.error("Exception in fetching authorizations from CDC", e);
			throw e;
		}
	}

	protected HttpEntity createHttpEntity(final CustomerModel customer, final HttpHeaders headers)
	{
		return new HttpEntity(getGigyaAuthRequestConverter().convert(customer), headers);
	}

	private boolean validateAssetDataExists(final GigyaAuthData authData, final GigyaAssetsData assetData)
	{
		return authData != null && assetData != null && assetData.getAssetTemplates() != null;
	}

	private boolean validateFunctionalRolesAndActionsExist(final GigyaAssetsData assetData)
	{
		return assetData.getAssetTemplates().getCommerceFunctionalRoles() != null
				&& assetData.getAssetTemplates().getCommerceFunctionalRoles().getActions() != null && CollectionUtils
						.isNotEmpty(assetData.getAssetTemplates().getCommerceFunctionalRoles().getActions().getAccessList());
	}

	@Override
	public void removeAuthorisationsOfCustomer(final CustomerModel customer)
	{
		ServicesUtil.validateParameterNotNull(customer, "Customer cannot be null.");

		final Set<PrincipalGroupModel> groups = customer.getGroups().stream()
				.filter(item -> !USER_AUTHORIZATIONS.contains(item.getUid())).collect(Collectors.toSet());
		customer.setGroups(groups);
	}


	public Converter<CustomerModel, GigyaAuthRequestData> getGigyaAuthRequestConverter()
	{
		return gigyaAuthRequestConverter;
	}

	public void setGigyaAuthRequestConverter(final Converter<CustomerModel, GigyaAuthRequestData> gigyaAuthRequestConverter)
	{
		this.gigyaAuthRequestConverter = gigyaAuthRequestConverter;
	}

	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public GigyaTokenGenerator getGigyaTokenGenerator()
	{
		return gigyaTokenGenerator;
	}

	public void setGigyaTokenGenerator(final GigyaTokenGenerator gigyaTokenGenerator)
	{
		this.gigyaTokenGenerator = gigyaTokenGenerator;
	}

	public B2BCommerceUserService getB2bCommerceUserService()
	{
		return b2bCommerceUserService;
	}

	public void setB2bCommerceUserService(final B2BCommerceUserService b2bCommerceUserService)
	{
		this.b2bCommerceUserService = b2bCommerceUserService;
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public RestTemplate getRestTemplate()
	{
		return restTemplate;
	}

	public void setRestTemplate(final RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}

}

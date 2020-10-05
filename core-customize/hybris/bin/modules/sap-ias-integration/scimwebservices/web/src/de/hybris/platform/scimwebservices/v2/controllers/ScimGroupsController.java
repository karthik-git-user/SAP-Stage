/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimwebservices.v2.controllers;

import de.hybris.platform.scimfacadescep.ScimGroup;
import de.hybris.platform.scimfacadescep.ScimGroupList;
import de.hybris.platform.scimfacadescep.group.ScimGroupFacade;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.util.YSanitizer;
import de.hybris.platform.scimfacadescep.utils.ScimGroupUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

@RestController
@Api(value = "/Groups", tags = "Scim Groups")
@RequestMapping(value = "/Groups")
public class ScimGroupsController {
	private static final Logger LOG = Logger.getLogger(ScimGroupsController.class);

	@Resource(name = "scimGroupFacade")
	private ScimGroupFacade scimGroupFacade;


	@ApiOperation(nickname = "createScimGroup",value = "Create group in Commerce", response = ScimGroup.class, notes = "Endpoint to create a scim group in Commerce", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = ScimGroup.class)})
	@PostMapping(produces = {"application/json"}, consumes = {"application/json"})
	public ScimGroup createGroup(
			@ApiParam(value = "The ScimGroup that contains information about the group") @RequestBody final ScimGroup scimGroup)
	{
		LOG.debug("ScimGroupController.createGroup entry. GroupID=" + sanitize(scimGroup.getId()));
		validate(sanitize(scimGroup.getId()));
		scimGroup.setId(sanitize(scimGroup.getId()));
		return scimGroupFacade.createGroup(scimGroup);
	}

	@ApiOperation(nickname = "replaceScimGroup", value = "Update group in Commerce", response = ScimGroup.class, notes = "Endpoint to update details of a scim group in Commerce for which Id is provided", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = ScimGroup.class)})
	@PutMapping(value = {"/{groupId}"}, produces = {"application/json"}, consumes = {"application/json"})
	public ScimGroup updateGroup(@ApiParam(value = "Group ID of the Group", required = true) @PathVariable final String groupId,
			@ApiParam(value = "The ScimGroup that contains the information to be updated.") @RequestBody final ScimGroup scimGroup)
	{
		LOG.debug("ScimGroupsController.updateGroup entry. GroupID=" + scimGroup.getId());

		if (!StringUtils.equals(groupId, scimGroup.getId()))
		{
			throw new ScimException("Mismatch in group ids supplied for update " + groupId);
		}

		return scimGroupFacade.updateGroup(sanitize(groupId), scimGroup);
	}

	@ApiOperation(nickname = "getScimGroup", value = "Get group from Commerce", response = ScimGroup.class, notes = "Endpoint to get the details of scim group along with members from Commerce according to the group-id provided", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = ScimGroup.class)})
	@GetMapping(value = {"/{groupId}"}, produces = {"application/json"})
	public ScimGroup getGroup(@ApiParam(value = "Group ID of the Scim Group to fetch.", required = true) @PathVariable final String groupId)
	{

		LOG.debug("ScimGroupsController.getGroup entry. GroupID=" + sanitize(groupId));

		return scimGroupFacade.getGroup(sanitize(groupId));
	}

	@ApiOperation(nickname = "getScimGroups", value = "Get all scim groups from commerce", response = ScimGroupList.class, notes = "Endpoint to retrieve all existing scim groups along with their members from Commerce.", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ApiResponses({@ApiResponse(code = 200, message = "OK", response = ScimGroupList.class)})
	@GetMapping(produces = {"application/json"})
	public ScimGroupList getGroups(@ApiParam(value = "The starting point for fetch of scim groups from Commerce on a page.") @RequestParam(value = "startIndex", defaultValue = "1") final int startIndex,
			@ApiParam(value = "Total number of scim groups to be fetched from Commerce on a page.") @RequestParam(value = "count", defaultValue = "0") final int count,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		LOG.debug("ScimGroupsController.getGroups entry");
		final List<ScimGroup> groups = scimGroupFacade.getGroups();
		
		ScimGroupList scimPaginationData = new ScimGroupList();
		scimPaginationData.setItemsPerPage(count);
		scimPaginationData.setStartIndex(startIndex);
		scimPaginationData.setResources(groups);
		if(groups != null) {
			scimPaginationData.setTotalResults(groups.size());
		} else {
			scimPaginationData.setTotalResults(0);
		}
		final int totalResults = scimPaginationData.getTotalResults();
		if(count > totalResults) {
			scimPaginationData.setItemsPerPage(totalResults);
		}

		return ScimGroupUtils.searchScimGroupPageData(scimPaginationData);
	}
	
	@ApiOperation(nickname = "removeScimGroup", value = "Delete scim group from Commerce", notes = "Endpoint to delete scim group from Commerce for which id is provided", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{groupId}", method = RequestMethod.DELETE)
	public void deleteGroup(@ApiParam(value = "Group ID of the Scim Group to delete.", required = true) @PathVariable final String groupId)
	{
		LOG.debug("ScimGroupsController.deleteUser entry. " + sanitize(groupId));

		scimGroupFacade.deleteGroup(sanitize(groupId));
	}

	@ApiOperation(nickname = "updateScimGroup", value = "Patch update scim group in commerce", response = ScimGroup.class, notes = "Endpoint to update scim group in Commerce with the details provided", authorizations = {
			@Authorization(value = "oauth2_client_credentials") })
	@ResponseStatus(value = HttpStatus.OK)
	@RequestMapping(value = "/{groupId}", method = RequestMethod.PATCH, produces = {"application/json"}, consumes = {"application/json"})
	public ScimGroup patchGroup(@ApiParam(value = "Group ID of the Group", required = true) @PathVariable final String groupId,
			@ApiParam(value = "The ScimGroup that contains information about the group") @RequestBody final ScimGroup scimGroup)
	{
		LOG.debug("ScimGroupsController.patchGroup entry.");

		return scimGroupFacade.updateGroup(sanitize(groupId), scimGroup);
	}

	/**
	 * Validates the object by using the passed validator
	 *
	 * @param object
	 *           the object to be validated
	 * @param objectName
	 *           the object name
	 * @param validator
	 *           validator which will validate the object
	 */
	protected void validate(final String id)
	{
		Pattern p = Pattern.compile("[^A-Za-z0-9]");
	    Matcher m = p.matcher(id);
		if(id == null || id.trim().isEmpty() || m.find()) {
			throw new WebserviceValidationException(new InterceptorException("Group with Id=" + id + "is invalid"));
		}
	}

	/**
	 * Method to sanitize the input string
	 *
	 * @param input
	 *           the input string
	 * @return String sanitized string
	 */
	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}
}

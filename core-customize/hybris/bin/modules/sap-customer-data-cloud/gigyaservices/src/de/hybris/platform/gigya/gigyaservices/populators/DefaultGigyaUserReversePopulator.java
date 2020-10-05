/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.populators;

import java.io.InvalidClassException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaSyncDirection;
import de.hybris.platform.gigya.gigyaservices.enums.GyAttributeType;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.gigya.gigyaservices.model.GigyaFieldMappingModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

public class DefaultGigyaUserReversePopulator implements Populator<GSResponse, CustomerModel>
{

	private static final Logger LOG = Logger.getLogger(DefaultGigyaUserReversePopulator.class);

	private GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao;

	private ModelService modelService;

	@Override
	public void populate(final GSResponse gsResponse, final CustomerModel gigyaUser)
	{
		if (gsResponse.hasData())
		{
			final GSObject data = gsResponse.getData();
			gigyaUser.setGyIsOriginGigya(true);
			final List<GigyaFieldMappingModel> allMappings = gigyaFieldMappingGenericDao.find();
			allMappings.stream()
					.filter(mapping -> isSyncDirectionFromG2H(mapping.getSyncDirection())
							&& !GyAttributeType.COMPLEX.equals(mapping.getHybrisType())
							&& isValidForApiKey(gigyaUser.getGyApiKey(), mapping.getGigyaConfig()))
					.forEach(mapping -> handleAttributeMapping(gigyaUser, data, mapping));
		}
	}

	/**
	 * Handles mapping of attributes between gigya and hybris
	 *
	 * @param gigyaUser
	 * @param data
	 * @param mapping
	 */
	protected void handleAttributeMapping(final CustomerModel gigyaUser, final GSObject data,
			final GigyaFieldMappingModel mapping)
	{
		GSObject object = data;
		if (mapping.getGigyaAttributeName().contains("."))
		{
			final String[] levels = mapping.getGigyaAttributeName().split("\\.");
			for (int i = 0; i < levels.length - 1; i++)
			{
				object = getMappedObject(object, levels, i);
			}
			if (object.containsKey(levels[levels.length - 1]))
			{
				handleAttribute(mapping, gigyaUser, object, levels[levels.length - 1]);
			}
		}
		else
		{
			if (object.containsKey(mapping.getGigyaAttributeName()))
			{
				handleAttribute(mapping, gigyaUser, object, mapping.getGigyaAttributeName());
			}
		}
	}

	protected boolean isSyncDirectionFromG2H(final GigyaSyncDirection syncDirection)
	{
		return (GigyaSyncDirection.G2H.equals(syncDirection) || GigyaSyncDirection.BOTH.equals(syncDirection));
	}

	protected boolean isValidForApiKey(final String apiKey, final GigyaConfigModel gigyaConfig)
	{
		return gigyaConfig != null && StringUtils.equals(apiKey, gigyaConfig.getGigyaApiKey());
	}

	/**
	 * Gets the mapped object
	 *
	 * @param object
	 * @param levels
	 * @param i
	 * @param GSObject
	 */
	protected GSObject getMappedObject(GSObject object, final String[] levels, final int i)
	{
		if (object.containsKey(levels[i]))
		{
			try
			{
				object = object.getObject(levels[i]);
			}
			catch (final GSKeyNotFoundException e)
			{
				LOG.error(e);
			}
		}
		return object;
	}

	protected void handleAttribute(final GigyaFieldMappingModel mapping, final CustomerModel gigyaUser,
			final GSObject profile, final String gigyaAttributeName)
	{
		try
		{
			switch (mapping.getHybrisType())
			{
			case INTEGER:
				modelService.setAttributeValue(gigyaUser, mapping.getHybrisAttributeName(),
						profile.getInt(gigyaAttributeName));
				break;
			case BOOLEAN:
				modelService.setAttributeValue(gigyaUser, mapping.getHybrisAttributeName(),
						profile.getBool(gigyaAttributeName));
				break;
			case LONG:
				modelService.setAttributeValue(gigyaUser, mapping.getHybrisAttributeName(),
						profile.getLong(gigyaAttributeName));
				break;
			case DATE:
				handleSettingDateAttribute(mapping, gigyaUser, profile, gigyaAttributeName);
				break;
			case STRING:
				modelService.setAttributeValue(gigyaUser, mapping.getHybrisAttributeName(),
						profile.getString(gigyaAttributeName));
				break;
			case COMPLEX:
				break;
			default:
				modelService.setAttributeValue(gigyaUser, mapping.getHybrisAttributeName(),
						profile.get(gigyaAttributeName));
			}
		}
		catch (InvalidClassException | ParseException | GSKeyNotFoundException e)
		{
			LOG.error(e);
		}
	}

	/**
	 * Set date attribute according to the configured date formate
	 *
	 * @param mapping            the gigya field mapping
	 * @param gigyaUser          the customer model
	 * @param profile            the gs object
	 * @param gigyaAttributeName the gigya attribute name
	 * @throws ParseException         when parsing fails
	 * @throws GSKeyNotFoundException when gs key us not found
	 *
	 */
	protected void handleSettingDateAttribute(final GigyaFieldMappingModel mapping, final CustomerModel gigyaUser,
			final GSObject profile, final String gigyaAttributeName) throws ParseException, GSKeyNotFoundException
	{
		final SimpleDateFormat formatter;

		formatter = new SimpleDateFormat(Config.getString("gigya.dateFormat", "yyyy-MM-dd'T'HH:mm:ssZ"));

		modelService.setAttributeValue(gigyaUser, mapping.getHybrisAttributeName(),
				formatter.parse(profile.getString(gigyaAttributeName)));
	}

	public GenericDao<GigyaFieldMappingModel> getGigyaFieldMappingGenericDao()
	{
		return gigyaFieldMappingGenericDao;
	}

	@Required
	public void setGigyaFieldMappingGenericDao(final GenericDao<GigyaFieldMappingModel> gigyaFieldMappingGenericDao)
	{
		this.gigyaFieldMappingGenericDao = gigyaFieldMappingGenericDao;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
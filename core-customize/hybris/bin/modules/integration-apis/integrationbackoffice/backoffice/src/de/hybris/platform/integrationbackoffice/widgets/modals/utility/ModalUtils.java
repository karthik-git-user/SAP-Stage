/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.utility;

import de.hybris.platform.integrationbackoffice.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemAttributeDTO;
import de.hybris.platform.integrationbackoffice.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationbackoffice.utility.QualifierNameUtils;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.zkoss.zul.Filedownload;

public final class ModalUtils
{
	private ModalUtils()
	{
		throw new IllegalStateException("Utility class");
	}

	public static boolean isAlphaNumericName(final String serviceName)
	{
		return QualifierNameUtils.isAlphaNumericName(serviceName);
	}

	public static boolean isServiceNameUnique(final String serviceName, final ReadService readService)
	{
		for (final IntegrationObjectModel integrationObject : readService.getIntegrationObjectModels())
		{
			if (integrationObject.getCode().equalsIgnoreCase(serviceName))
			{
				return false;
			}
		}
		return true;
	}

	public static boolean isAliasUnique(final String alias, final List<AbstractListItemDTO> attributes)
	{
		for (final AbstractListItemDTO listItemDTO : attributes)
		{
			boolean matchingAlias = listItemDTO.getAlias().equalsIgnoreCase(alias);
			boolean matchingSourceOfTruthName;

			if (listItemDTO instanceof ListItemAttributeDTO)
			{
				matchingSourceOfTruthName = ((ListItemAttributeDTO) listItemDTO).getAttributeDescriptor()
				                                                                .getQualifier()
				                                                                .equalsIgnoreCase(alias);
			}
			else
			{
				matchingSourceOfTruthName = ((ListItemClassificationAttributeDTO) listItemDTO).getClassificationAttributeCode()
				                                                                              .equalsIgnoreCase(alias);
			}

			if ((matchingAlias || matchingSourceOfTruthName))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Calls the appropriate method to download the file in the given format using the browser's download functionality
	 *
	 * @param fileContent Content of file in byte[] format
	 * @param fileFormat  Format that the file is saved as
	 * @param fileName    Name of the file
	 */
	public static void executeMediaDownload(final byte[] fileContent, final String fileFormat, final String fileName)
	{
		InputStream stream = new ByteArrayInputStream(fileContent);
		Filedownload.save(stream, fileFormat, fileName);
	}

	public static void executeReportDownload(InputStream stream, final String fileFormat, final String fileName)
	{
		Filedownload.save(stream, fileFormat, fileName);
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapimpeximportadapter.facades;

import java.io.InputStream;


/**
 * Interface for impex import facade.
 * 
 * @deprecated since 1811, please use {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 */
@Deprecated(since = "1811", forRemoval= true )
public interface ImpexImportFacade
{
	/**
	 * Creates and imports Impex from the input stream
	 *
	 * @param inputStream
	 *           - impex payload in input stream format
	 */
	void createAndImportImpexMedia(final InputStream inputStream);
}

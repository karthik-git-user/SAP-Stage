/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacarintegration.services.sourcing;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;


class MySchemaOutputResolver extends SchemaOutputResolver
{

	@Override
	public Result createOutput(final String namespaceURI, final String suggestedFileName) throws IOException
	{
		final File file = new File(suggestedFileName);
		final StreamResult result = new StreamResult(file);
		result.setSystemId(file.toURI().toURL().toString());
		return result;
	}

}
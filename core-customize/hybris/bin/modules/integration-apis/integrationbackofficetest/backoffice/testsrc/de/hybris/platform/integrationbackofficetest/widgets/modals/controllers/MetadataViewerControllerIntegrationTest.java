/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackofficetest.widgets.modals.controllers;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.core.Registry;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modals.controllers.IntegrationObjectMetadataViewerController;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2services.odata.schema.entity.PluralizingEntitySetNameGenerator;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

public class MetadataViewerControllerIntegrationTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private TypeService typeService;
	@Resource
	private PluralizingEntitySetNameGenerator defaultPluralizingEntitySetNameGenerator;

	private final IntegrationObjectMetadataViewerController metadataViewerController = new IntegrationObjectMetadataViewerController();

	@Before
	public void setUp() throws Exception
	{
		final GenericApplicationContext applicationContext = (GenericApplicationContext) Registry.getApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

		final AbstractBeanDefinition validationDefinition = BeanDefinitionBuilder.rootBeanDefinition(ReadService.class)
		                                                                         .getBeanDefinition();
		beanFactory.registerBeanDefinition("readService", validationDefinition);
		final ReadService readService = (ReadService) Registry.getApplicationContext().getBean("readService");
		readService.setTypeService(typeService);
		metadataViewerController.setReadService(readService);
		metadataViewerController.setPluralizer(defaultPluralizingEntitySetNameGenerator);

		importCsv("/test/impex/BasicJSONTest.csv", "UTF-8");
		importCsv("/test/impex/ProductIOClassificationTest.csv", "UTF-8");
		importCsv("/test/impex/ProductIOClassificationEnumReferenceAndMultivalued.csv", "UTF-8");
	}

	@Test
	public void testGenerateEndpointPath()
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'BasicJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		metadataViewerController.setSelectedIntegrationObject(integrationObjectModel);

		assertEquals("https://<your-host-name>/odata2webservices/BasicJSONTest/Products",
				metadataViewerController.generateEndpointPath());
	}

	public static String loadFileAsString(final String fileLocation) throws IOException
	{
		final ClassLoader classLoader = MetadataViewerControllerIntegrationTest.class.getClassLoader();
		final URL url = classLoader.getResource(fileLocation);
		File file = null;
		if (url != null)
		{
			file = new File(url.getFile());
		}

		return Files.readString(Paths.get(file.getPath()));
	}
}
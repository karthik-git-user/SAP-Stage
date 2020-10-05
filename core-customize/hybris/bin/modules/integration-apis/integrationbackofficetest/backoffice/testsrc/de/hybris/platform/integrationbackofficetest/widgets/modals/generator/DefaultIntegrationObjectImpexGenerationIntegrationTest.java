package de.hybris.platform.integrationbackofficetest.widgets.modals.generator;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.widgets.modals.generator.DefaultIntegrationObjectImpexGenerator;
import de.hybris.platform.integrationbackoffice.widgets.modals.generator.IntegrationObjectImpexGenerator;
import de.hybris.platform.integrationbackofficetest.widgets.modals.controllers.MetadataViewerControllerIntegrationTest;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

public class DefaultIntegrationObjectImpexGenerationIntegrationTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;
	private IntegrationObjectImpexGenerator integrationObjectImpexGenerator;

	@Before
	public void setUp() throws ImpExException
	{
		integrationObjectImpexGenerator = new DefaultIntegrationObjectImpexGenerator();
		importCsv("/test/impex/ProductIOClassificationTest.csv", "UTF-8");
		importCsv("/test/impex/ProductIOClassificationEnumReferenceAndMultivalued.csv", "UTF-8");
		importCsv("/test/impex/ItemTypeMatchImpexTestPreset.csv", "UTF-8");
		importCsv("/test/impex/ItemTypeMatchImpexTest.csv", "UTF-8");
	}

	@Test
	public void testImpexStringWithClassificationAttributes() throws IOException
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'ProductIOClassificationTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);
		assertEquals("ProductIOClassificationTest", integrationObjectModel.getCode());

		final String actual = integrationObjectImpexGenerator.generateImpex(integrationObjectModel).replaceAll("\\s", "");
		final String expected = loadFileAsString("test/impex/ProductIOClassificationTestExpected.csv").replaceAll("\\s", "");

		assertEquals(expected, actual);
	}

	@Test
	public void testImpexItemTypeMatch() throws IOException
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'ItemTypeMatchImpexTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);
		assertEquals("ItemTypeMatchImpexTest", integrationObjectModel.getCode());

		final String actual = integrationObjectImpexGenerator.generateImpex(integrationObjectModel).replaceAll("\\s", "");
		final String expected = loadFileAsString("test/impex/ItemTypeMatchImpexTest.csv").replaceAll("\\s", "");

		assertEquals(expected, actual);
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

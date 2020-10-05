package de.hybris.platform.integrationbackofficetest.widgets.modals.generator;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modals.generator.DefaultIntegrationObjectJsonGenerator;
import de.hybris.platform.integrationservices.config.ReadOnlyAttributesConfiguration;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class DefaultIntegrationObjectJsonGeneratorIntegrationTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private ReadOnlyAttributesConfiguration defaultIntegrationServicesConfiguration;
	@Resource
	private TypeService typeService;
	private DefaultIntegrationObjectJsonGenerator jsonGenerator;
	private Gson gson;

	@Before
	public void setUp() throws ImpExException
	{
		final GenericApplicationContext applicationContext = (GenericApplicationContext) Registry.getApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

		final AbstractBeanDefinition validationDefinition = BeanDefinitionBuilder.rootBeanDefinition(ReadService.class)
		                                                                         .getBeanDefinition();
		beanFactory.registerBeanDefinition("readService", validationDefinition);

		final ReadService readService = (ReadService) Registry.getApplicationContext().getBean("readService");
		readService.setTypeService(typeService);
		jsonGenerator = new DefaultIntegrationObjectJsonGenerator(readService, defaultIntegrationServicesConfiguration);
		gson = new GsonBuilder().setPrettyPrinting().create();

		importCsv("/test/impex/BasicJSONTest.csv", "UTF-8");
		importCsv("/test/impex/MapJSONTest.csv", "UTF-8");
		importCsv("/test/impex/CollectionJSONTest.csv", "UTF-8");
		importCsv("/test/impex/MapNotSupportedJSONTest.csv", "UTF-8");
		importCsv("/test/impex/CircularDependencyJSONTest.csv", "UTF-8");
		importCsv("/test/impex/CircularDependencyComplexJSONTest.csv", "UTF-8");
		importCsv("/test/impex/ProductIOClassificationTest.csv", "UTF-8");
		importCsv("/test/impex/ProductIOClassificationEnumReferenceAndMultivalued.csv", "UTF-8");
		importCsv("/test/impex/ReadOnlyJSONTest.csv", "UTF-8");
	}

	@Test
	public void basicJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/BasicJSONTestExpected.json");
		jsonObject.put("numberOfReviews", 123); //to fix Integer formatting

		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'BasicJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("BasicJSONTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);


		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void mapJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/MapJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'MapJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("MapJSONTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void collectionJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/CollectionJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'CollectionJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);
		assertEquals("CollectionJSONTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void mapOfMapsNotSupportedJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/MapNotSupportedJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'MapNotSupportedJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("MapNotSupportedJSONTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void circularDependencyJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/CircularDependencyJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'CircularDependencyJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("CircularDependencyJSONTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void circularDependencyComplexJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/CircularDependencyComplexJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'CircularDependencyComplexJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("CircularDependencyComplexJSONTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void classificationJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/ProductIOClassificationJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'ProductIOClassificationTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("ProductIOClassificationTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void classificationCollectionEnumReferenceJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/ProductIOClassificationEnumReferenceAndMultivalued.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'ProductIOClassificationEnumReferenceAndMultivalued')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("ProductIOClassificationEnumReferenceAndMultivalued", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	@Test
	public void readOnlyJsonTest() throws FileNotFoundException
	{
		final Map<String, Object> jsonObject = loadPayload("test/json/ReadOnlyJSONTestExpected.json");
		final String expectedJsonString = gson.toJson(jsonObject);

		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'ReadOnlyJSONTest')");
		final IntegrationObjectModel integrationObjectModel = search.getResult().get(0);

		assertEquals("ReadOnlyJSONTest", integrationObjectModel.getCode());

		final String actualJsonString = jsonGenerator.generateJson(integrationObjectModel);

		assertEquals(expectedJsonString, actualJsonString);
	}

	public static Map<String, Object> loadPayload(final String payloadLocation) throws FileNotFoundException
	{
		final ClassLoader classLoader = DefaultIntegrationObjectJsonGeneratorIntegrationTest.class.getClassLoader();
		final URL url = classLoader.getResource(payloadLocation);
		if (url != null)
		{
			final File file = new File(url.getFile());
			final Gson gson = new Gson();
			final JsonReader reader = new JsonReader(new FileReader(file));
			return gson.fromJson(reader, Map.class);
		}
		return null;
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackofficetest.widgets.modals.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.hybris.platform.audit.internal.config.DefaultAuditConfigService;
import de.hybris.platform.audit.view.AuditViewService;
import de.hybris.platform.auditreport.service.ReportViewConverterStrategy;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.integrationbackoffice.widgets.modals.builders.IntegrationObjectAuditReportBuilder;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


public class IntegrationObjectAuditReportBuilderIntegrationTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private AuditViewService auditViewService;
	@Resource
	private RendererService rendererService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private UserService userService;
	@Resource
	private List<ReportViewConverterStrategy> reportViewConverterStrategies;
	@Resource
	private DefaultAuditConfigService auditConfigService;

	private IntegrationObjectAuditReportBuilder auditReportBuilder = new IntegrationObjectAuditReportBuilder();

	@Before
	public void setUp() throws Exception
	{
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("integrationbackofficetest-integrationobject-audit.xml").getFile());
		String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);

		importCsv("/test/impex/CompileSubtypeSetTestImpex007.csv", "UTF-8");
		importCsv("/test/impex/AuditReportTest008.csv", "UTF-8");
		importCsv("/impex/essentialdata-DefaultAuditReportBuilderTemplate.impex", "UTF-8");

		auditConfigService.storeConfiguration("IOReport", content);
		auditReportBuilder.setAuditViewService(auditViewService);
		auditReportBuilder.setCommonI18NService(commonI18NService);
		auditReportBuilder.setRendererService(rendererService);
		auditReportBuilder.setReportViewConverterStrategies(reportViewConverterStrategies);
		auditReportBuilder.setUserService(userService);
		auditReportBuilder.setConfigName("IOReport");
		auditReportBuilder.setIsDownload(false);
	}

	@Test
	public void generateAndCompareAuditReportTest() throws IOException
	{
		// generate first audit report from one integration object
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'CompileSubtypeSetTestImpex007')");
		IntegrationObjectModel integrationObjectModel = search.getResult().get(0);
		Map<String, InputStream> reportGenerateRes = auditReportBuilder.generateAuditReport(integrationObjectModel);
		byte[] arr1 = null;
		InputStream is1 = null;
		for (InputStream inputStream : reportGenerateRes.values())
		{
			arr1 = inputStream.readAllBytes();
			is1 = inputStream;
		}

		// generate second audit report from same integration object as the first one
		final SearchResult<IntegrationObjectModel> searchCopy = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'CompileSubtypeSetTestImpex007')");
		IntegrationObjectModel integrationObjectModelCopy = searchCopy.getResult().get(0);
		Map<String, InputStream> reportGenerateResCopy = auditReportBuilder.generateAuditReport(integrationObjectModelCopy);
		byte[] arr2 = null;
		InputStream is2 = null;
		for (InputStream inputStream : reportGenerateResCopy.values())
		{
			arr2 = inputStream.readAllBytes();
			is2 = inputStream;
		}

		// generate third audit report from different integration object
		final SearchResult<IntegrationObjectModel> searchAnother = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'AuditReportTest008')");
		IntegrationObjectModel integrationObjectModelAnother = searchAnother.getResult().get(0);
		Map<String, InputStream> reportGenerateResAnother = auditReportBuilder.generateAuditReport(integrationObjectModelAnother);
		byte[] arr3 = null;
		for (InputStream inputStream : reportGenerateResAnother.values())
		{
			arr3 = inputStream.readAllBytes();
		}

		assertEquals(integrationObjectModel, integrationObjectModelCopy);
		assertEquals(arr1.length, arr2.length);
		assertTrue(IOUtils.contentEquals(is1, is2));
		assertNotEquals(integrationObjectModel, integrationObjectModelAnother);
		assertNotEquals(arr1.length, arr3.length);
		assertFalse(Arrays.equals(arr1, arr3));

		// get json object from data just fetched
		String htmlContent = new String(arr3);
		htmlContent = htmlContent.substring(htmlContent.indexOf("<script>") + 8, htmlContent.indexOf("</script>"));
		htmlContent = htmlContent.substring(htmlContent.indexOf("=") + 1, htmlContent.lastIndexOf(";"));
		htmlContent = htmlContent.substring(htmlContent.indexOf("[") + 1, htmlContent.lastIndexOf("]"));
		final JsonParser parser = new JsonParser();
		final JsonObject jsonObjectNew = parser.parse(htmlContent).getAsJsonObject();

		// get json object from baseline
		ClassLoader classLoader = getClass().getClassLoader();
		String htmlPath = "test/text/htmlContent.html";
		File file = new File(classLoader.getResource(htmlPath).getFile());
		String htmlContentBaseline = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
		htmlContentBaseline = htmlContentBaseline.substring(htmlContentBaseline.indexOf("<script>") + 8,
				htmlContentBaseline.indexOf("</script>"));
		htmlContentBaseline = htmlContentBaseline.substring(htmlContentBaseline.indexOf("=") + 1,
				htmlContentBaseline.lastIndexOf(";"));
		htmlContentBaseline = htmlContentBaseline.substring(htmlContentBaseline.indexOf("[") + 1,
				htmlContentBaseline.lastIndexOf("]"));
		JsonObject jsonObjectBaseline = parser.parse(htmlContentBaseline).getAsJsonObject();
		try
		{
			assertEquals(jsonObjectNew.get("changingUser").toString(), jsonObjectBaseline.get("changingUser").toString());
			assertEquals(jsonObjectNew.getAsJsonObject("payload")
			                          .getAsJsonObject("IntegrationObject")
			                          .get("code").toString(), jsonObjectBaseline.getAsJsonObject("payload")
			                                                                     .getAsJsonObject("IntegrationObject")
			                                                                     .get("code").toString());
			JsonArray itemList = jsonObjectNew.getAsJsonObject("payload").getAsJsonObject("IntegrationObject")
			                                  .getAsJsonArray("IntegrationObjectItems");
			JsonArray itemBaselineList = jsonObjectNew.getAsJsonObject("payload").getAsJsonObject("IntegrationObject")
			                                          .getAsJsonArray("IntegrationObjectItems");
			assertEquals(itemList.size(), itemBaselineList.size());
			if (itemList.get(0).getAsJsonObject().get("code") == itemBaselineList.get(0).getAsJsonObject().get("code"))
			{
				assertEquals(itemList.get(0).toString(), itemBaselineList.get(0).toString());
				System.out.println("1: " + itemList.get(0).toString());
			}
			else
			{
				assertEquals(itemList.get(0).toString(), itemBaselineList.get(1).toString());
				System.out.println("2: " + itemList.get(0).toString());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
}

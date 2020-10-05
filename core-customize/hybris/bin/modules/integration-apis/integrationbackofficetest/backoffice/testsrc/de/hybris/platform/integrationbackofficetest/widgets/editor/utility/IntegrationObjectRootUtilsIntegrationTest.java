/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackofficetest.widgets.editor.utility;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.IntegrationObjectRootUtils;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.assertEquals;

public class IntegrationObjectRootUtilsIntegrationTest extends ServicelayerTest
{

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private IntegrationObjectModel integrationObjectModel;

	@Before
	public void setup() throws ImpExException
	{
		importCsv("/test/impex/SingleRootTestImpex001.csv", "UTF-8");
		importCsv("/test/impex/NoRootTestImpex002.csv", "UTF-8");
		importCsv("/test/impex/MultiRootTestImpex003.csv", "UTF-8");
		importCsv("/test/impex/SingleRootCircularDepTestImpex004.csv", "UTF-8");
	}

	@Test
	public void getIntegrationObjectSingleBooleanRoot()
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'SingleRootTestImpex001')");
		integrationObjectModel = search.getResult().get(0);

		final String expectedRoot = "OrgUnit";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals("SingleRootTestImpex001", integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);
	}

	@Test
	public void getIntegrationObjectNoBooleanRoot()
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'NoRootTestImpex002')");
		integrationObjectModel = search.getResult().get(0);

		final String expectedRoot = "Category";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals("NoRootTestImpex002", integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);
	}

	@Test
	public void getIntegrationObjectMultiBooleanRoot()
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'MultiRootTestImpex003')");
		integrationObjectModel = search.getResult().get(0);

		integrationObjectModel.getItems().forEach(item -> {
			if (item.getCode().equals("Product"))
			{
				item.setRoot(true);
			}
		});

		final String expectedRoot = "Category";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals("MultiRootTestImpex003", integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);
	}

	@Test
	public void getIntegrationObjectSingleBooleanRootCircularDep()
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'SingleRootCircularDepTestImpex004')");
		integrationObjectModel = search.getResult().get(0);

		final String expectedRoot = "Order";
		final String actualRoot = IntegrationObjectRootUtils.resolveIntegrationObjectRoot(integrationObjectModel)
		                                                    .getRootItem()
		                                                    .getCode();

		assertEquals("SingleRootCircularDepTestImpex004", integrationObjectModel.getCode());
		assertEquals(expectedRoot, actualRoot);
	}
}

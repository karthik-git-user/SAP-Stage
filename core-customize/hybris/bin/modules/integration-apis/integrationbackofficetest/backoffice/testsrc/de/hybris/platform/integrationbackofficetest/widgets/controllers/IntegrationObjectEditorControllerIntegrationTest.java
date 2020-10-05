/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackofficetest.widgets.controllers;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.editor.controllers.IntegrationObjectEditorController;
import de.hybris.platform.integrationbackoffice.widgets.editor.data.SubtypeData;
import de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationObjectEditorControllerIntegrationTest extends ServicelayerTest
{
	@Resource
	private FlexibleSearchService flexibleSearchService;
	@Resource
	private TypeService typeService;

	private ReadService readService;
	private IntegrationObjectModel integrationObjectModel;
	private IntegrationObjectEditorController controller = new IntegrationObjectEditorController();

	@Before
	public void setUp() throws Exception
	{
		final GenericApplicationContext applicationContext = (GenericApplicationContext) Registry.getApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();

		final AbstractBeanDefinition validationDefinition = BeanDefinitionBuilder.rootBeanDefinition(ReadService.class)
		                                                                         .getBeanDefinition();
		beanFactory.registerBeanDefinition("readService", validationDefinition);
		readService = (ReadService) Registry.getApplicationContext().getBean("readService");
		readService.setTypeService(typeService);
		controller.setReadService(readService);

		importCsv("/test/impex/PopulateAttributesMapTestImpex006.csv", "UTF-8");
		importCsv("/test/impex/CompileSubtypeSetTestImpex007.csv", "UTF-8");
	}

	@Test
	public void readServiceTest()
	{
		assertTrue(Objects.nonNull(readService));
	}

	@Test
	public void compileSubtypeDataSetTest()
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'CompileSubtypeSetTestImpex007')");
		integrationObjectModel = search.getResult().get(0);

		final Map<ComposedTypeModel, List<AbstractListItemDTO>> convertedMap = EditorUtils.convertIntegrationObjectToDTOMap(
				readService, integrationObjectModel);
		controller.compileSubtypeDataSet(convertedMap);

		final SubtypeData subtypeData = (SubtypeData) controller.getSubtypeDataSet().toArray()[0];

		assertEquals("CompileSubtypeSetTestImpex007", integrationObjectModel.getCode());
		assertEquals(1, controller.getSubtypeDataSet().size());
		assertEquals("User", subtypeData.getBaseType().getCode());
		assertEquals("Customer", subtypeData.getSubtype().getCode());
		assertEquals("Order", subtypeData.getParentNodeType().getCode());
		assertEquals("user", subtypeData.getAttributeAlias());
	}

	@Test
	public void findSubtypeMatchTest()
	{
		final SearchResult<IntegrationObjectModel> search = flexibleSearchService.search(
				"SELECT PK FROM {IntegrationObject} WHERE (p_code = 'CompileSubtypeSetTestImpex007')");
		integrationObjectModel = search.getResult().get(0);

		final Map<ComposedTypeModel, List<AbstractListItemDTO>> convertedMap = EditorUtils.convertIntegrationObjectToDTOMap(
				readService, integrationObjectModel);
		controller.compileSubtypeDataSet(convertedMap);

		ComposedTypeModel order = integrationObjectModel.getRootItem().getType();
		SubtypeData subtypeData = (SubtypeData) controller.getSubtypeDataSet().toArray()[0];
		ComposedTypeModel attributeType = (ComposedTypeModel) subtypeData.getBaseType();
		String qualifier = "user";
		ComposedTypeModel expectedSubtype = (ComposedTypeModel) subtypeData.getSubtype();

		ComposedTypeModel actualSubtype = controller.findSubtypeMatch(order, qualifier, attributeType);

		assertEquals("CompileSubtypeSetTestImpex007", integrationObjectModel.getCode());
		assertEquals(expectedSubtype, actualSubtype);
	}
}
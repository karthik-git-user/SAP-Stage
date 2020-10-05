/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.sap.ppengine.client.dto.LineItemDomainSpecific;
import com.sap.ppengine.client.dto.MerchandiseHierarchyCommonData;
import com.sap.ppengine.client.dto.ObjectFactory;
import com.sap.ppengine.client.dto.SaleBase;
import com.sap.ppengine.client.util.RequestHelper;
import com.sap.ppengine.client.util.RequestHelperImpl;


@SuppressWarnings("javadoc")
@UnitTest
public class MerchandiseHierarchyLineItemPopulatorTest
{
	private MerchandiseHierarchyLineItemPopulator cut;
	private Collection<CategoryModel> superCats;
	private ClassificationClassModel superCat;
	private final List<CategoryModel> merchandiseCatLevel2List = new ArrayList<>();
	private ClassificationClassModel merchandiseCatLevel2;
	private ClassificationSystemModel catalog;
	private ClassificationSystemVersionModel catVersion;

	class cutExtendedForTest extends MerchandiseHierarchyLineItemPopulator
	{
		@Override
		public RequestHelper getHelper()
		{
			return new RequestHelperImpl();
		}

		@Override
		public ObjectFactory getObjectFactory()
		{
			return new ObjectFactory();
		}

	}


	@Before
	public void setUp()
	{
		cut = new cutExtendedForTest();
		MockitoAnnotations.initMocks(this);
		catalog = new ClassificationSystemModel();
		catVersion = new ClassificationSystemVersionModel();
		catVersion.setCatalog(catalog);
		superCats = new LinkedList<CategoryModel>();
		superCat = new ClassificationClassModel();
		merchandiseCatLevel2 = new ClassificationClassModel();
		merchandiseCatLevel2.setCatalogVersion(catVersion);
		merchandiseCatLevel2List.add(merchandiseCatLevel2);
		superCat.setSupercategories(merchandiseCatLevel2List);
		superCats.add(superCat);

	}

	@Test
	public void testIsErpMerchCat1() throws Exception
	{
		final CategoryModel cat = new CategoryModel();
		assertFalse(cut.isErpMerchCat(cat));
	}

	@Test
	public void testSetGetHierarchyId() throws Exception
	{
		assertEquals("1", cut.getHierarchyId());
		cut.setHierarchyId("bla");
		assertEquals("bla", cut.getHierarchyId());
	}

	@Test
	public void testIsErpMerchCat2() throws Exception
	{
		final ClassificationClassModel cat = new ClassificationClassModel();

		catalog.setId("4712");
		cat.setCatalogVersion(catVersion);
		cut.setMerchGroupCatalogId("4711");

		catVersion.setActive(false);
		assertFalse(cut.isErpMerchCat(cat));

		catVersion.setActive(true);
		assertFalse(cut.isErpMerchCat(cat));

		cut.setMerchGroupCatalogId("4712");
		assertTrue(cut.isErpMerchCat(cat));
	}

	@Test
	public void testIsSureErpMerchCat1() throws Exception
	{
		final ClassificationClassModel cat = new ClassificationClassModel();

		catalog.setId("4712");
		cat.setCatalogVersion(catVersion);
		cut.setMerchGroupCatalogId("4711");

		catVersion.setActive(false);
		assertFalse(cut.isSureErpMerchCat(cat));

		catVersion.setActive(true);
		assertFalse(cut.isSureErpMerchCat(cat));

		cut.setMerchGroupCatalogId("4712");
		assertFalse(cut.isSureErpMerchCat(cat));

		cat.setSupercategories(merchandiseCatLevel2List);
		assertTrue(cut.isSureErpMerchCat(cat));
	}

	@Test
	public void testDetermineBaseMaterialGroupNoCategoriesMap() throws Exception
	{
		final ProductModel product = new ProductModel();
		assertNull(cut.determineBaseMaterialGroup(product));
	}

	@Test
	public void testDetermineBaseMaterialGroupFalseCategory() throws Exception
	{
		final ProductModel product = createProductWithBaseMaterialGroup();

		cut.setMerchGroupCatalogId("4711");

		assertNull(cut.determineBaseMaterialGroup(product));
	}

	private ProductModel createProductWithBaseMaterialGroup()
	{
		final ProductModel product = new ProductModel();

		catalog.setId("4712");
		catVersion.setActive(true);
		superCat.setCatalogVersion(catVersion);
		product.setSupercategories(superCats);
		return product;
	}

	@Test
	public void testDetermineBaseMaterialGroupRightCategory() throws Exception
	{
		final ProductModel product = createProductWithBaseMaterialGroup();
		cut.setMerchGroupCatalogId("4712");
		assertEquals(superCat, cut.determineBaseMaterialGroup(product));
	}

	@Test
	public void testDetermineBaseMaterialGroupVariantProduct() throws Exception
	{
		final ProductModel product = createProductWithBaseMaterialGroup();
		final VariantProductModel variant = new VariantProductModel();
		variant.setBaseProduct(product);
		cut.setMerchGroupCatalogId("4712");
		final CategoryModel result = cut.determineBaseMaterialGroup(variant);
		assertEquals(result, superCat);
	}


	@Test
	public void testPopulateNoERPBaseCatAssigned() throws Exception
	{
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();
		final ProductModel product = new ProductModel();
		final SaleBase sale = new SaleBase();
		lineItem.setSale(sale);

		cut.populate(lineItem, product);

		assertTrue(lineItem.getMerchandiseHierarchy().isEmpty());
		assertTrue(cut.getHelper().getLineItemContent(lineItem).getMerchandiseHierarchy().isEmpty());
	}

	@Test
	public void testPopulateERPBaseCatAssigned() throws Exception
	{
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();
		final ProductModel product = new ProductModel();
		catalog.setId("merchCatId");

		catVersion.setActive(true);
		superCat.setCatalogVersion(catVersion);
		superCat.setCode("MerchGroupCode");

		cut.setMerchGroupCatalogId("merchCatId");

		product.setSupercategories(superCats);
		final SaleBase sale = new SaleBase();
		lineItem.setSale(sale);

		cut.populate(lineItem, product);

		assertEquals("1", lineItem.getMerchandiseHierarchy().get(0).getID());
		assertEquals("MerchGroupCode", lineItem.getMerchandiseHierarchy().get(0).getValue());

	}

	@Test
	public void testPopulateERPBaseCatAssignedWithoutHierarchy() throws Exception
	{
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();
		final ProductModel product = new ProductModel();

		final ClassificationClassModel superCat1;
		final ClassificationClassModel superCat2;

		catalog.setId("merchCatId");

		catVersion.setActive(true);

		superCats = new LinkedList<CategoryModel>();
		superCat1 = new ClassificationClassModel();
		superCat2 = new ClassificationClassModel();

		superCat1.setCatalogVersion(catVersion);
		superCat1.setCode("MerchGroupCode");

		superCat2.setCatalogVersion(catVersion);
		superCat2.setCode("CharProfileId");

		superCats.add(superCat1);
		superCats.add(superCat2);

		cut.setMerchGroupCatalogId("merchCatId");

		product.setSupercategories(superCats);
		final SaleBase sale = new SaleBase();
		lineItem.setSale(sale);

		cut.populate(lineItem, product);

		assertEquals("1", lineItem.getMerchandiseHierarchy().get(0).getID());
		assertEquals("MerchGroupCode", lineItem.getMerchandiseHierarchy().get(0).getValue());

		assertEquals("1", lineItem.getMerchandiseHierarchy().get(1).getID());
		assertEquals("CharProfileId", lineItem.getMerchandiseHierarchy().get(1).getValue());
	}

	@Test
	public void testPopulateERPBaseCatAndCharProfileAssigned() throws Exception
	{
		final LineItemDomainSpecific lineItem = new LineItemDomainSpecific();
		final ProductModel product = new ProductModel();

		final Collection<CategoryModel> superCats;
		final ClassificationClassModel superCat2;

		catalog.setId("merchCatId");

		catVersion.setActive(true);

		superCats = new LinkedList<CategoryModel>();
		superCat2 = new ClassificationClassModel();

		superCat.setCatalogVersion(catVersion);
		superCat.setCode("MerchGroupCode");

		superCat2.setCatalogVersion(catVersion);
		superCat2.setCode("CharProfileId");

		superCats.add(superCat2);
		superCats.add(superCat);

		cut.setMerchGroupCatalogId("merchCatId");

		product.setSupercategories(superCats);
		final SaleBase sale = new SaleBase();
		lineItem.setSale(sale);

		cut.populate(lineItem, product);

		assertEquals(2, lineItem.getMerchandiseHierarchy().size());
		assertEquals("1", lineItem.getMerchandiseHierarchy().get(0).getID());
		assertEquals("MerchGroupCode", lineItem.getMerchandiseHierarchy().get(0).getValue());
	}


	@Test
	public void testAddParentMerchGroupsEmpty()
	{
		final List<MerchandiseHierarchyCommonData> lineItemMerchHierarchies = new LinkedList<MerchandiseHierarchyCommonData>();
		final List<CategoryModel> supercategories = new LinkedList<CategoryModel>();

		cut.addParentMerchGroups(lineItemMerchHierarchies, supercategories);
		assertTrue(lineItemMerchHierarchies.isEmpty());
	}

	@Test
	public void testAddParentMerchGroupsWith2Categories()
	{
		final List<MerchandiseHierarchyCommonData> lineItemMerchHierarchies = new LinkedList<MerchandiseHierarchyCommonData>();
		final List<CategoryModel> supercategories0 = new LinkedList<CategoryModel>();
		final List<CategoryModel> supercategories1 = new LinkedList<CategoryModel>();
		final List<CategoryModel> supercategories2 = new LinkedList<CategoryModel>();
		final ClassificationClassModel superCat1 = new ClassificationClassModel();
		final ClassificationClassModel superCat2 = new ClassificationClassModel();
		final ClassificationSystemModel catalog = new ClassificationSystemModel();
		catalog.setId("merchCatId");

		final ClassificationSystemVersionModel catVersion = new ClassificationSystemVersionModel();
		catVersion.setCatalog(catalog);
		catVersion.setActive(true);
		superCat1.setCatalogVersion(catVersion);
		superCat1.setCode("MerchGroupCode1");

		superCat2.setCatalogVersion(catVersion);
		superCat2.setCode("MerchGroupCode2");

		superCat1.setSupercategories(supercategories1);
		superCat2.setSupercategories(supercategories2);

		supercategories1.add(superCat2);
		supercategories0.add(superCat1);

		cut.setMerchGroupCatalogId("merchCatId");


		cut.addParentMerchGroups(lineItemMerchHierarchies, supercategories0);
		assertEquals(2, lineItemMerchHierarchies.size());
	}

	@Test
	public void testSetGetOrder()
	{
		final int order = 1;
		cut.setOrder(order);
		assertEquals(order, cut.getOrder());
	}


}

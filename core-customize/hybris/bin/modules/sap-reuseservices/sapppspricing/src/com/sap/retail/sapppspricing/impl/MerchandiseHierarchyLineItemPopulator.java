/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.ppengine.client.dto.LineItemDomainSpecific;
import com.sap.ppengine.client.dto.MerchandiseHierarchyCommonData;
import com.sap.retail.sapppspricing.LineItemPopulator;


/**
 * Line line item populator adding merchandise hierarchy information
 *
 */
public class MerchandiseHierarchyLineItemPopulator extends DefaultPPSClientBeanAccessor implements
		LineItemPopulator<ProductModel>
{
	private String hierarchyId = "1";
	private static final Logger LOG = LoggerFactory.getLogger(MerchandiseHierarchyLineItemPopulator.class);
	private String merchGroupCatalogId;
	private int order = 0;

	@Override
	public void populate(final LineItemDomainSpecific lineItem, final ProductModel product)
	{
		final List<MerchandiseHierarchyCommonData> lineItemMerchHierarchies = lineItem.getMerchandiseHierarchy();
		List<CategoryModel> baseCatCandidates;

		final CategoryModel baseCat = determineBaseMaterialGroup(product);
		if (baseCat == null)
		{
			// No unambiguous base merchandise category found. As a next approach, do not require parent categories for the merch group.
			// This may also return characteristics profiles though. In case we only find one candidate this must be a merch group.
			// In case 2 candidates are found, one if the char profile, one the merch group. Not possible to distinguish these!
			// To be on the safe side, provide both candidates as merch groups. Since there can be no merch group with the same ID as a char profile,
			// this at worst adds only a minor overhead to the price calculation but does not lead to wrong results.
			baseCatCandidates = determineBaseMaterialGroupCandidates(product);
			if (baseCatCandidates.isEmpty())
			{
				LOG.debug("Product {} has no erp base cat assigned", product.getCode());
				return;
			}
		}
		else
		{
			baseCatCandidates = Collections.singletonList(baseCat);
		}
		for (final CategoryModel baseCatCandidate : baseCatCandidates)
		{
			final MerchandiseHierarchyCommonData hierarchyCommonData = asDto(baseCatCandidate);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Adding hierarchy information for product {}", product.getCode());
				LOG.debug("Base cat ID={}", baseCatCandidate.getCode());
			}
			lineItemMerchHierarchies.add(hierarchyCommonData);
			addParentMerchGroups(lineItemMerchHierarchies, baseCatCandidate.getSupercategories());
		}
	}

	void addParentMerchGroups(final List<MerchandiseHierarchyCommonData> lineItemMerchHierarchies,
			final List<CategoryModel> supercategories)
	{
		List<CategoryModel> cats = supercategories;
		while (cats != null && !cats.isEmpty())
		{
			boolean erpCatFound = false;
			for (final CategoryModel cat : cats)
			{
				if (isErpMerchCat(cat))
				{
					lineItemMerchHierarchies.add(asDto(cat));
					cats = cat.getSupercategories();
					erpCatFound = true;
					break;
				}
			}
			if (!erpCatFound)
			{
				return; // Avoid endless loops
			}
		}
	}

	private MerchandiseHierarchyCommonData asDto(final CategoryModel cat)
	{
		final MerchandiseHierarchyCommonData hierarchyCommonData = getObjectFactory().createMerchandiseHierarchyCommonData();
		hierarchyCommonData.setID(getHierarchyId());
		hierarchyCommonData.setValue(cat.getCode());
		return hierarchyCommonData;
	}

	protected CategoryModel determineBaseMaterialGroup(final ProductModel product)
	{
		if (product instanceof VariantProductModel)
		{
			return determineBaseMaterialGroup(((VariantProductModel) product).getBaseProduct());
		}
		final Collection<CategoryModel> supercategories = product.getSupercategories();
		if (supercategories == null)
		{
			return null;
		}
		for (final CategoryModel cat : supercategories)
		{
			if (isSureErpMerchCat(cat))
			{
				return cat;
			}
		}
		return null;
	}

	protected List<CategoryModel> determineBaseMaterialGroupCandidates(final ProductModel product)
	{
		if (product instanceof VariantProductModel)
		{
			return determineBaseMaterialGroupCandidates(((VariantProductModel) product).getBaseProduct());
		}
		final Collection<CategoryModel> supercategories = product.getSupercategories();
		if (supercategories == null)
		{
			return Collections.emptyList();
		}
		final List<CategoryModel> result = new ArrayList<>();
		for (final CategoryModel cat : supercategories)
		{
			if (isErpMerchCat(cat))
			{
				result.add(cat);
			}
		}
		return result;
	}


	protected boolean isErpMerchCat(final CategoryModel cat)
	{
		if (!(cat instanceof ClassificationClassModel))
		{
			return false;
		}
		final CatalogVersionModel catalogVersion = cat.getCatalogVersion();
		if (!Boolean.TRUE.equals(catalogVersion.getActive()))
		{
			return false;
		}
		final String catalogId = catalogVersion.getCatalog().getId();
		
		return getMerchGroupCatalogId().equals(catalogId);
		
	}

	/**
	 *
	 * @param cat
	 * @return true if category is a base merchandise category for sure. Does not consider the case that it has no
	 *         parents. In that case the category might be a base MC or a characteristics profile! Then later on
	 *         additional logic is needed.
	 */
	protected boolean isSureErpMerchCat(final CategoryModel cat)
	{
		return isErpMerchCat(cat) && cat.getSupercategories() != null && !cat.getSupercategories().isEmpty();
	}

	@SuppressWarnings("javadoc")
	public String getMerchGroupCatalogId()
	{
		return merchGroupCatalogId;
	}

	@SuppressWarnings("javadoc")
	public void setMerchGroupCatalogId(final String merchGroupCatalogId)
	{
		this.merchGroupCatalogId = merchGroupCatalogId;
	}

	@Override
	public int getOrder()
	{
		return order;
	}

	@SuppressWarnings("javadoc")
	public void setOrder(final int order)
	{
		this.order = order;
	}

	/**
	 * @return the hierarchyId
	 */
	public String getHierarchyId()
	{
		return hierarchyId;
	}

	/**
	 * @param hierarchyId
	 *           the hierarchyId to set
	 */
	public void setHierarchyId(final String hierarchyId)
	{
		this.hierarchyId = hierarchyId;
	}

}

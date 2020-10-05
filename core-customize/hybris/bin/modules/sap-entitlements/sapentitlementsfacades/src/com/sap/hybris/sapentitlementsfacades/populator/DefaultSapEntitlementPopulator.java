/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsfacades.populator;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;
import com.sap.hybris.sapentitlementsfacades.data.EntitlementData;

/**
 * Populator to convert Entitlement to EntitlementData.
 */
public class DefaultSapEntitlementPopulator implements Populator<Entitlement, EntitlementData>
{
	private ProductFacade productFacade;
	private static final Logger LOG = Logger.getLogger(DefaultSapEntitlementPopulator.class);

	@Override
	public void populate(final Entitlement entitlement, final EntitlementData entitlementData) throws ConversionException
	{
		entitlementData.setId(entitlement.getEntitlementGuid());
		entitlementData.setEntitlementNumber(entitlement.getEntitlementNo());
		entitlementData.setName(entitlement.getEntitlementModelName());
		entitlementData.setOrderNumber(entitlement.getRefDocNo());
		final String unit = entitlement.getUom() != null ? entitlement.getUom().getValue() : "";
		entitlementData.setQuantity(entitlement.getQuantity() + " " + unit);
		entitlementData.setRegion(entitlement.getGeolocation());
		entitlementData.setStatus(entitlement.getStatusName());
		entitlementData.setValidFrom(entitlement.getValidFrom());
		entitlementData.setValidTo(entitlement.getValidTo());
		entitlementData.setType(entitlement.getEntitlementTypeName());
		final String right = entitlement.getTheRight() != null ? entitlement.getTheRight().getValue() : "";
		entitlementData.setRight(right);
		final String productCode = entitlement.getOfferingID();
		final List<ProductOption> options = new ArrayList<ProductOption>();
		options.add(ProductOption.BASIC);
		ProductData product = new ProductData();
		try
		{
			product = productFacade.getProductForCodeAndOptions(productCode, options);
		}
		catch (final UnknownIdentifierException e)
		{
			product.setCode(productCode);
			if (LOG.isDebugEnabled())
			{
				LOG.error(e);
			}
		}
		entitlementData.setProduct(product);
	}


	/**
	 * @param productFacade
	 *           the productFacade to set
	 */
	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}


}

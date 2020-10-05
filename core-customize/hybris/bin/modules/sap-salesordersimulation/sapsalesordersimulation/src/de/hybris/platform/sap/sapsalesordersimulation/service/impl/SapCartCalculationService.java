/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.impl.DefaultCalculationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.PricingService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SalesOrderSimulationService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;


/**
 * SapCartCalculationService
 */
public class SapCartCalculationService extends DefaultCalculationService
{
	private transient SalesOrderSimulationService salesOrderSimulationService ;
	private transient SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	private transient PricingService pricingService;
	


	public SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}


	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}

	@Override
	public void calculate(final AbstractOrderModel order) throws CalculationException
	{
		calculateOrder(order);
		super.calculate(order);
	}

	protected void calculateOrder(final AbstractOrderModel order) {
		if(isCalculationRequired(order)) {
			getSalesOrderSimulationService().setCartDetails( order);
		}
	}

	private boolean isCalculationRequired(final AbstractOrderModel order) {
		return !order.getEntries().isEmpty() && getSapSimulateSalesOrderEnablementService().isCartPricingEnabled() ;
	}
	
	@Override
	public void recalculate(final AbstractOrderModel order) throws CalculationException
	{
		
		calculateOrder(order);
		super.recalculate(order);
	}
	
	@Override
	public void calculateTotals(final AbstractOrderModel order, final boolean recalculate) throws CalculationException
	{
		calculateOrder(order);
		super.calculateTotals(order, recalculate);
	}
	
	@Override
	protected void resetAllValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled())
		{
			return;	
		}
		super.resetAllValues(entry);
	}
	
	@Override
	protected Map resetAllValues(AbstractOrderModel order)
			throws CalculationException {
		
		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled())
		{

			final Map<TaxValue, Map<Set<TaxValue>, Double>> taxValueMap = calculateSubtotal(
					order, false);

			final Collection<TaxValue> relativeTaxValues = new LinkedList<>();
			for (final Map.Entry<TaxValue, ?> e : taxValueMap.entrySet()) {
				final TaxValue taxValue = e.getKey();
				if (!taxValue.isAbsolute()) {
					relativeTaxValues.add(taxValue);
				}
			}

			return taxValueMap;
		}

		return super.resetAllValues(order);
		

	}
	
	@Override
	protected void resetAdditionalCosts(AbstractOrderModel order,
			Collection<TaxValue> relativeTaxValues) {
		
		if (!(getSapSimulateSalesOrderEnablementService().isCartPricingEnabled()))
		{
			super.resetAdditionalCosts(order, relativeTaxValues);
				
		}
		
	}
	
	@Override
	protected List<DiscountValue> findDiscountValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if (!(getSapSimulateSalesOrderEnablementService().isCartPricingEnabled() ))
		{
			return super.findDiscountValues(entry);
			
		}
		return entry.getDiscountValues();
	}

	@Override
	protected PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
	{
		if(getSapSimulateSalesOrderEnablementService().isCatalogPricingEnabled()) {
			final List<PriceInformation> priceInformations = getPricingService().getPriceForProduct(entry.getProduct())  ;
			
			PriceInformation priceInfo = null;
			if (! priceInformations.isEmpty()) {
				priceInfo =	priceInformations.iterator().next();
			}
			return (priceInfo!= null)? priceInfo.getPriceValue():null;
			
			
		} else {
			return super.findBasePrice(entry);
		}
	}

	protected SalesOrderSimulationService getSalesOrderSimulationService() {
		return salesOrderSimulationService;
	}

	public void setSalesOrderSimulationService(SalesOrderSimulationService salesOrderSimulationService) {
		this.salesOrderSimulationService = salesOrderSimulationService;
	}

	protected PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	
	
	

}

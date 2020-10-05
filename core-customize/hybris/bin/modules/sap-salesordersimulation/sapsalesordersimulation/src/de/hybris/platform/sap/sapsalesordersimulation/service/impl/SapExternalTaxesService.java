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

import de.hybris.platform.commerceservices.externaltax.impl.DefaultExternalTaxesService;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;

/**
 * calculate sap external Taxes
 */
public class SapExternalTaxesService extends DefaultExternalTaxesService {
	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	@Override
	public boolean calculateExternalTaxes(AbstractOrderModel abstractOrder) {
		if (getSapSimulateSalesOrderEnablementService().isCatalogPricingEnabled()) {
			// since the taxes are already calculated from ERP backend, no need to process taxes
			return true;
		} else {
			return super.calculateExternalTaxes( abstractOrder);
		}
	}
	
	protected SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}

}

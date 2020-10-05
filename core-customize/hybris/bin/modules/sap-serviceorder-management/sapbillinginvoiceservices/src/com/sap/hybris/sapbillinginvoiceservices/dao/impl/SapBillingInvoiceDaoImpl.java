/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.dao.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbillinginvoiceservices.dao.SapBillingInvoiceDao;


/**
 * Data Access Object implementation class for looking up items related to SAP Order
 *
 */
public class SapBillingInvoiceDaoImpl extends DefaultGenericDao<OrderModel>  implements SapBillingInvoiceDao{

	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceDaoImpl.class);

	private static final String SAP_ORDER_QUERY_BY_ID = "SELECT {SAPOrder.pk} from {SAPOrder as SAPOrder} where {SAPOrder.code}= ?sapOrderCode";

	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;

	public PaginatedFlexibleSearchService getPaginatedFlexibleSearchService() {
		return paginatedFlexibleSearchService;
	}

	public void setPaginatedFlexibleSearchService(final PaginatedFlexibleSearchService paginatedFlexibleSearchService) {
		this.paginatedFlexibleSearchService = paginatedFlexibleSearchService;
	}

	public SapBillingInvoiceDaoImpl() {
		super("Order");
	}

	@Override
	public SAPOrderModel getServiceOrderBySapOrderCode(final String sapOrderCode)
	{
		LOG.info("Start of get service order from sap order code " + sapOrderCode + "");
		final Map attr = new HashMap(1);
		attr.put("sapOrderCode", sapOrderCode);
		final StringBuilder sql = new StringBuilder();
		sql.append(SAP_ORDER_QUERY_BY_ID);
		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().putAll(attr);
		final SearchResult result = getFlexibleSearchService().search(query);
		final List b2bServiceOrders = result.getResult();
		LOG.info("End of get service order from sap order code " + sapOrderCode + "");
		return ((b2bServiceOrders.isEmpty()) ? null : (SAPOrderModel) b2bServiceOrders.get(0));
	}

}

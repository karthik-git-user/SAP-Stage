/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.hybris.sapbillinginvoiceservices.client.impl.SapBillingInvoiceClientImpl;
import com.sap.hybris.sapbillinginvoiceservices.dao.SapBillingInvoiceDao;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;
import com.sap.hybris.sapbillinginvoiceservices.service.SapBillingInvoiceService;


/**
 * Service Implementation for interacting with DAO and Client.
 *
 */
public class SapBillingInvoiceServiceImpl implements SapBillingInvoiceService{

	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceServiceImpl.class);

	private SapBillingInvoiceDao billingInvoiceDao;

	private SapBillingInvoiceClientImpl sapBillingInvoiceClientImpl;

	private UserService userService;

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(final UserService userService) {
		this.userService = userService;
	}




	public SapBillingInvoiceClientImpl getSapBillingInvoiceClientImpl()
	{
		return sapBillingInvoiceClientImpl;
	}

	public void setSapBillingInvoiceClientImpl(final SapBillingInvoiceClientImpl sapBillingInvoiceClientImpl)
	{
		this.sapBillingInvoiceClientImpl = sapBillingInvoiceClientImpl;
	}

	public SapBillingInvoiceDao getBillingInvoiceDao()
	{
		return billingInvoiceDao;
	}

	public void setBillingInvoiceDao(final SapBillingInvoiceDao billingInvoiceDao)
	{
		this.billingInvoiceDao = billingInvoiceDao;
	}

	@Override
	public SAPOrderModel getServiceOrderBySapOrderCode(final String sapOrderCode)
	{
		return getBillingInvoiceDao().getServiceOrderBySapOrderCode(sapOrderCode);
	}



	@Override
	public byte[] getPDFData(final SAPOrderModel sapOrderData, final String billingDocumentId)
			throws SapBillingInvoiceUserException
	{
		LOG.info("Start of check for customer validation to view the billing document");
		final CustomerModel customer = ((CustomerModel) getUserService().getCurrentUser());
		if (customer instanceof B2BCustomerModel) {
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;

			if (!b2bCustomer.getPk().equals(sapOrderData.getOrder().getUser().getPk()))
			{
				LOG.error("Error Invalid user trying to access the invoice");
				throw new SapBillingInvoiceUserException("Invalid user trying to access the invoice");
			}

		}

		LOG.info("End of check for customer validation to view the billing document");

		return getSapBillingInvoiceClientImpl().getPDFData(billingDocumentId, sapOrderData);

	}

	@Override
	public  Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(final SAPOrderModel serviceOrderData) {
		return getSapBillingInvoiceClientImpl().getBusinessDocumentFromS4ServiceOrderCode(serviceOrderData);
	}

	@Override
	public Map<String, Object> getBusinessDocumentFromS4SSapOrderCode(final SAPOrderModel sapOrderData) {
		return getSapBillingInvoiceClientImpl().getBusinessDocumentFromS4SAPOrderCode(sapOrderData);
	}

}

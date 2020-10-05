/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sap.hybris.sapbillinginvoicefacades.facade.impl.SapBillingInvoiceFacadeImpl;
import com.sap.hybris.sapbillinginvoiceservices.exception.SapBillingInvoiceUserException;


/**
 * Controller defines routes to manage service order within My Company section.
 */
@Controller
@RequestMapping("/my-account/manage-billing-invoice/")
public class BillingInvoiceOrderDetailPageController extends AbstractSearchPageController
{

	private static final String ALPHANUMERIC_REGEX = "[a-zA-Z0-9]++";
	private static final String SERVICE_ORDER_CODE_PATH_VARIABLE_PATTERN = "{serviceOrderCode:.*}";
	private static final String SERVICE_ORDER_BILLING_CODE_PATH_VARIABLE_PATTERN = "{billingDocId:.*}";

	@Resource(name = "sapBillingInvoiceOrderFacade")
	private SapBillingInvoiceFacadeImpl serviceOrderFacadeImpl;

	private static final Logger LOG = Logger.getLogger(BillingInvoiceOrderDetailPageController.class);


	@RequestMapping(value = "/billingInvoice/" + SERVICE_ORDER_CODE_PATH_VARIABLE_PATTERN + "/"
			+ SERVICE_ORDER_BILLING_CODE_PATH_VARIABLE_PATTERN, method =
	{ RequestMethod.GET })
	@RequireHardLogIn
	public void billingInvoiceDownload(@PathVariable("serviceOrderCode")
	final String sapOrderCode, @PathVariable("billingDocId")
	final String billingDocId, final Model model, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectModel)
	{
		try
		{

			LOG.info("Start of Get PDF for saporder with Id " + sapOrderCode + " and Billing Id " + billingDocId + "");

			if(billingDocId==null || !billingDocId.matches(ALPHANUMERIC_REGEX)) {
				throw new IOException("Invalid billingDocId");
			}
			
			fetchBillingDocAndProcessResponse(sapOrderCode, billingDocId, response);

			LOG.info("End of Get PDF for saporder with Id " + sapOrderCode + " and Billing Id " + billingDocId + "");
		}
		catch (final SapBillingInvoiceUserException e)
		{
			LOG.error("Authorization Issue::", e);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		catch (final IOException e) {
			LOG.error("Invalid billingDocId", e);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		catch (final Exception e)
		{
			LOG.error("Unexpected Error Occured while trying to fetch invoice data :", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		}

	}


	protected void fetchBillingDocAndProcessResponse(final String sapOrderCode, final String billingDocId, final HttpServletResponse response)
			throws SapBillingInvoiceUserException, IOException {
		byte[] pdfData;
		final String pdfName = billingDocId.concat("_invoice.pdf");

		pdfData = serviceOrderFacadeImpl.getPDFData(sapOrderCode, billingDocId);

		if (!StringUtils.isEmpty(pdfName))
		{
			response.setHeader("Content-Disposition", "inline;filename=" + pdfName);
		}
		response.setDateHeader("Expires", -1);
		response.setContentType("application/pdf");
		response.getOutputStream().write(pdfData);
		response.getOutputStream().close();
		response.getOutputStream().flush();
	}

}

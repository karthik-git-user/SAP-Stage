/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapbillinginvoiceservices.client.impl;

import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapmodel.enums.SapSystemType;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPlantLogSysOrgModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.hybris.sapbillinginvoiceservices.constants.SapbillinginvoiceservicesConstants;


/**
 * Client to connect to target system via SCPI
 */
public class SapBillingInvoiceClientImpl
{


	private static final String BIILING_DOCTEXT = "billingDocumentArray";
	private static final String URI_EXCEPTIONMSG = "Error converting target Url";

	private static final String SUCCESS = "success";
	private static final String RESPONSE_STATUS = "extBillingResponseStatusCode";

	private static final String RESPONSE_VALUE = "extBillingResponseValue";

	private static final String DECODE_BYTES = "decodedBytes";

	private static final String EXT_BIILING_OUTBOUND_OBJECT = "ExtBillingInvoice";
	private static final String EXT_BIILING_OUTBOUND_DESTINATION = "extBillingCPIDestination";

	private static final Logger LOG = Logger.getLogger(SapBillingInvoiceClientImpl.class.getName());

	private SapPlantLogSysOrgService sapPlantLogSysOrgService;

	private OutboundServiceFacade outboundServiceFacade;

	private ConfigurationService configurationService;

	public OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public SapPlantLogSysOrgService getSapPlantLogSysOrgService()
	{
		return sapPlantLogSysOrgService;
	}

	public void setSapPlantLogSysOrgService(final SapPlantLogSysOrgService sapPlantLogSysOrgService)
	{
		this.sapPlantLogSysOrgService = sapPlantLogSysOrgService;
	}


	public Map<String, Object> getBusinessDocumentFromS4SAPOrderCode(final SAPOrderModel sapOrderData)
	{

		LOG.info("Start of Get billing documents for sales order from S4CM");

		final String targetSuffixUrl = getConfigurationService().getConfiguration()
				.getString(SapbillinginvoiceservicesConstants.BILLING_DOCUMENT_ITEM_POST_FIX_URL)
				.replace("{SapOrderCode}", sapOrderData.getCode());

		LOG.info("End of Get billing documents for sales order from S4CM");

		return callS4forBillingDocuments(sapOrderData, targetSuffixUrl);
	}

	public SAPCpiOutboundConfigModel getSAPCpiOutboundConfigModelForExtBilling(final SAPOrderModel sapOrder,
			final String targetSuffixUrl, final SapSystemType sapSystemType)
	{

		final SAPCpiOutboundConfigModel extBillingCpiConfigModel = new SAPCpiOutboundConfigModel();

		final ConsignmentModel consigment = sapOrder.getConsignments().iterator().next();

		final SAPPlantLogSysOrgModel sapPlantLogSysOrg = getSapPlantLogSysOrgService().getSapPlantLogSysOrgForPlant(
				sapOrder.getOrder().getStore(),

				consigment.getWarehouse().getCode());


		if (sapSystemType.equals(sapPlantLogSysOrg.getLogSys().getSapSystemType()))
		{
			LOG.info("Target system http destination "
					+ sapPlantLogSysOrg.getLogSys().getSapHTTPDestination().getHttpDestinationName() + "");

			URL url = null;
			try
			{

				url = new URL(sapPlantLogSysOrg.getLogSys().getSapHTTPDestination().getTargetURL());

			}
			catch (final MalformedURLException e)
			{
				LOG.error(URI_EXCEPTIONMSG, e);
			}

			String targetUrl = "";

			if (url != null)
			{
				targetUrl = constructTargetUrl(url, targetSuffixUrl);
			}



			final String userName = sapPlantLogSysOrg.getLogSys().getSapHTTPDestination().getUserid();
			extBillingCpiConfigModel.setUrl(targetUrl);
			extBillingCpiConfigModel.setUsername(userName);
		}

		return extBillingCpiConfigModel;
	}


	public Map<String, Object> getBusinessDocumentFromS4ServiceOrderCode(final SAPOrderModel serviceOrderModelData)
	{

		LOG.info("Start of Get billing documents for service order from S4CM");

		final String targetSuffixUrl = getConfigurationService().getConfiguration()
				.getString(SapbillinginvoiceservicesConstants.BILLING_DOCUMENT_SUFFIX_FIX_URL)
				.replace("{serviceOrderCode}", serviceOrderModelData.getServiceOrderId());

		LOG.info("End of Get billing documents for service order from S4CM");

		return callS4forBillingDocuments(serviceOrderModelData, targetSuffixUrl);
	}

	public Map<String, Object> callS4forBillingDocuments(final SAPOrderModel sapOrderModelData, final String targetSuffixUrl)
	{
		final Map<String, Object> parametersMap = new HashMap<>();
		parametersMap.put(BIILING_DOCTEXT, new JsonArray());

		final SAPCpiOutboundConfigModel extBillingCpiConfigModel = getSAPCpiOutboundConfigModelForExtBilling(sapOrderModelData,
				targetSuffixUrl, SapSystemType.SAP_S4HANA);

		if (!extBillingCpiConfigModel.getUrl().isEmpty())
		{
			getOutboundServiceFacade().send(extBillingCpiConfigModel, EXT_BIILING_OUTBOUND_OBJECT, EXT_BIILING_OUTBOUND_DESTINATION)
					.subscribe(responseEntityMap -> {

						if (isSentSuccessfully(responseEntityMap))
						{
							LOG.info(
									"Successful to get billing document form SCPI ");

							final String result = getPropertyValue(responseEntityMap, RESPONSE_VALUE);
							final JsonObject payload = new JsonParser().parse(result).getAsJsonObject();
							final JsonObject d = payload.get("d").getAsJsonObject();
							final JsonArray results = d.get("results").getAsJsonArray();
							final JsonArray billingDocumentArray = results;

							parametersMap.put(BIILING_DOCTEXT, billingDocumentArray);
						}
						else
						{
							LOG.info("Failed to get billing document for service order ");
						}

					}, error -> LOG.error(error));
		}
		return parametersMap;
	}

	public boolean isSentSuccessfully(final ResponseEntity<Map> responseEntityMap)
	{
		return (SUCCESS.equalsIgnoreCase(getPropertyValue(responseEntityMap, RESPONSE_STATUS))
				&& responseEntityMap.getStatusCode().is2xxSuccessful()) ? Boolean.TRUE : Boolean.FALSE;
	}

	public String getPropertyValue(final ResponseEntity<Map> responseEntityMap, final String property)
	{
		final Object next = responseEntityMap.getBody().keySet().iterator().next();
		final String responseKey = next.toString();
		final Object propertyValue = ((HashMap) responseEntityMap.getBody().get(responseKey)).get(property);
		return propertyValue.toString();
	}



	public byte[] getPDFData(final String billingDocumentId, final SAPOrderModel serviceOrderModelData)
	{
		LOG.info("Start of get PDF from S4CM");

		final Map<String, Object> parametersMap = new HashMap<>();
		parametersMap.put(DECODE_BYTES, new byte[0]);


		final String targetSuffixUrl = getConfigurationService().getConfiguration()
				.getString(SapbillinginvoiceservicesConstants.BILLING_DOCUMENT_PDF_SUFFIX_FIX_URL)
				.replace("{billingDocumentId}", billingDocumentId);

		final SAPCpiOutboundConfigModel extBillingCpiConfigModel = getSAPCpiOutboundConfigModelForExtBilling(serviceOrderModelData,
				targetSuffixUrl, SapSystemType.SAP_S4HANA);

		if (!extBillingCpiConfigModel.getUrl().isEmpty())
		{
			getOutboundServiceFacade().send(extBillingCpiConfigModel, EXT_BIILING_OUTBOUND_OBJECT, EXT_BIILING_OUTBOUND_DESTINATION)
					.subscribe(responseEntityMap -> {

						if (isSentSuccessfully(responseEntityMap))
						{
							LOG.info("Successful to get invoice PDF for Billing document ID " + billingDocumentId);

							final String result = getPropertyValue(responseEntityMap, RESPONSE_VALUE);
							final JsonObject resultPayload = new JsonParser().parse(result).getAsJsonObject();

							final JsonObject d = resultPayload.get("d").getAsJsonObject();
							final JsonObject pdfData = d.get("GetPDF").getAsJsonObject();
							final String billingDocumentBinaryAsString = pdfData.get("BillingDocumentBinary").getAsString();
							parametersMap.put(DECODE_BYTES,
										Base64.decodeBase64(billingDocumentBinaryAsString.getBytes(StandardCharsets.UTF_8)));


						}
						else
						{
							LOG.info("Failed to get invoice PDF for Billing document ID " + billingDocumentId);
						}


					}, error -> LOG.error(error));
		}

		LOG.info("End of get PDF from S4CM");

		return (byte[]) parametersMap.get(DECODE_BYTES);
	}

	private String constructTargetUrl(final URL url, final String targetUrl)
	{

		return url.getProtocol() + "://" + url.getAuthority() + targetUrl;

	}

}

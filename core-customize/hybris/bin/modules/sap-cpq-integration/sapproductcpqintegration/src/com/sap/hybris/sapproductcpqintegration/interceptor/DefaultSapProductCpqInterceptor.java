/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapproductcpqintegration.interceptor;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.hybris.sapproductcpqintegration.constants.SapproductcpqintegrationConstants;
import com.sap.hybris.sapproductcpqintegration.exception.DefaultSapProductCpqIntegrationException;
import com.sap.hybris.sapproductcpqintegration.service.SapProductCpqOutboundService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;

/**
 * 
 * Interceptor that replicate product image .
 * 
 */
public class DefaultSapProductCpqInterceptor implements ValidateInterceptor<ProductModel> {

	private static final Logger LOGGER = LogManager.getLogger(DefaultSapProductCpqInterceptor.class);

	private SAPGlobalConfigurationService sapGlobalConfigurationService;
	private SapProductCpqOutboundService sapProductCpqOutboundService;

	/**
	 * Get media product media object and send formdata request to callidus.
	 * 
	 * @param productModel product object
	 * @param ctx          interceptor context
	 */
	@Override
	public void onValidate(final ProductModel productModel, final InterceptorContext ctx) throws InterceptorException {
		boolean sapGlobalConfigurationExists = getSapGlobalConfigurationService().sapGlobalConfigurationExists();
		if (sapGlobalConfigurationExists) {
			Object enabled = getSapGlobalConfigurationService()
					.getProperty(SapproductcpqintegrationConstants.CPQ_PRODUCT_REPLICATION_ENABLED);
			if (enabled != null && (java.lang.Boolean) enabled) {
				try {
					if ((ctx.isModified(productModel, ProductModel.PICTURE) && productModel.getPicture() != null)
							&& productModel.getCatalogVersion().getVersion()
									.equals(SapproductcpqintegrationConstants.CATALOG_VERSION_ONLINE)) {

						final ResponseEntity<String> responseEntity = getSapProductCpqOutboundService()
								.sendProductMedia(productModel.getPicture());
						if (responseEntity != null) {
							handleResponse(responseEntity, productModel);
						}
					}
				} catch (DefaultSapProductCpqIntegrationException | IOException exception) {
					LOGGER.error("Error occured during image replication for product " + productModel.getCode()
							+ " from Commerce To Callidus " + exception);
				}
			} 
		}
	}

	private void handleResponse(ResponseEntity<String> responseEntity, ProductModel productModel) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		try (JsonParser parser = mapper.getFactory().createParser(responseEntity.getBody())) {
			JsonNode root = mapper.readValue(parser, JsonNode.class);

			if (responseEntity.getStatusCodeValue() == SapproductcpqintegrationConstants.CPQ_SUCCESS_RESPONSE) {
				LOGGER.debug("Image replication successful for product {} from Commerce To CPQ", productModel.getCode());
			} else {
				JsonNode path = root.get(0).path(SapproductcpqintegrationConstants.CPQ_ERROR_MESSAGE);
				LOGGER.error("Problem in replicating image for product {} from Commerce To CPQ {}", productModel.getCode(), path);
			}
		}

	}

	public SapProductCpqOutboundService getSapProductCpqOutboundService() {
		return sapProductCpqOutboundService;
	}

	public void setSapProductCpqOutboundService(SapProductCpqOutboundService sapProductCpqOutboundService) {
		this.sapProductCpqOutboundService = sapProductCpqOutboundService;
	}

	/**
	 * @return the sapGlobalConfigurationService
	 */
	public SAPGlobalConfigurationService getSapGlobalConfigurationService() {
		return sapGlobalConfigurationService;
	}

	/**
	 * @param sapGlobalConfigurationService the sapGlobalConfigurationService to set
	 */
	public void setSapGlobalConfigurationService(SAPGlobalConfigurationService sapGlobalConfigurationService) {
		this.sapGlobalConfigurationService = sapGlobalConfigurationService;
	}

}

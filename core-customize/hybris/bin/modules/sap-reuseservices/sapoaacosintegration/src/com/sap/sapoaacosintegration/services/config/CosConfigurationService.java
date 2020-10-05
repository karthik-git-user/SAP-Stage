/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.config;

/**
 * Configuration service for COS
 */
public interface CosConfigurationService
{

	/**
	 * @return {@link String}
	 */
	String getCosCacStrategyId();

	/**
	 * @return {@link String}
	 */
	String getCosCasStrategyId();

}

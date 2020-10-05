/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorder.service;

/**
 * Interface for building objects from SOURCE to TARGET
 *
 * @param <SOURCE> source object
 * @param <TARGET> target object
 */
public interface SapCpiServiceOrderOutboundBuilderService<SOURCE,TARGET> {
	
	/**
	   * Builds target from source.
	   *
	   * @param source Payload source model
	   * @return TARGET target source model
	   */
	TARGET build(SOURCE source);
}
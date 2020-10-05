/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.clients;

import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;

/**
 * Api Client for Subscription Billing
 */
public interface SubscriptionBillingApiClient {

    /**
     * Calls Api with post method
     * @param uriComponents uri that need to be called
     * @param entity entity that needs to be posted
     */
    void postEntity(UriComponents uriComponents, Object entity);

    /**
     * Calls Api with post method
     * @param uriComponents uri that need to be called
     * @param entity entity that needs to be posted
     *
     * @return Expected response Type
     */
    <T> T postEntity(UriComponents uriComponents, Object entity, Class<T> response);

    /**
     * Calls API and returns parsed response
     * @param uriComponents uri that need to be called
     * @param clazz Class of Expected response
     * @param <T> Expected Response
     * @return Result from API
     */
    <T> T getEntity(UriComponents uriComponents, Class<T> clazz);

    /**
     * Calls API And returns Raw Response
     * @param uriComponents uri component that needs to be called
     * @param clazz Expected Response Class
     * @param <T> Expected Response Class
     * @return {@link ResponseEntity} Raw response
     */
    <T> ResponseEntity<T> getRawEntity(UriComponents uriComponents, Class<T> clazz);



}

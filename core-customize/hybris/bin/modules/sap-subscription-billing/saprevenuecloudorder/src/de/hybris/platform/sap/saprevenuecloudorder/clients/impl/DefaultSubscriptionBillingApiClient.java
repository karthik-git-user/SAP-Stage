/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saprevenuecloudorder.clients.impl;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.sap.saprevenuecloudorder.exception.SapSubscriptionConfigurationException;
import de.hybris.platform.sap.saprevenuecloudorder.exception.SapSubscriptionSystemException;
import de.hybris.platform.sap.saprevenuecloudorder.clients.SubscriptionBillingApiClient;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class DefaultSubscriptionBillingApiClient implements SubscriptionBillingApiClient {

    private static final Logger LOG = Logger.getLogger(DefaultSubscriptionBillingApiClient.class);

    private DestinationService<ConsumedDestinationModel> destinationService;
    private IntegrationRestTemplateFactory integrationRestTemplateFactory;

    private static final String SUBSCRIPTION_BILLING_DESTINATION_ID = "sapSubscriptionBilling";

    public void postEntity(UriComponents uriComponents, Object entity) {
        postEntity(uriComponents, entity, Void.class);
    }

    public <T> T postEntity(UriComponents uriComponents, Object entity, Class<T> response){

        //Fetch Auth Token
        final ConsumedDestinationModel destinationModel = destinationService.getDestinationById(SUBSCRIPTION_BILLING_DESTINATION_ID);
        if(destinationModel==null){
            throw new SapSubscriptionConfigurationException(String.format("Missing Destination model [%s]", SUBSCRIPTION_BILLING_DESTINATION_ID));
        }
        RestOperations restOperations = buildRestOperations(destinationModel);

        //Build Url
        URI uri = UriComponentsBuilder.fromHttpUrl(destinationModel.getUrl())
                .uriComponents(uriComponents)
                .build()
                .toUri();

        //Call API
        try {
            LOG.info(String.format("Calling post uri: %s", uri.toString()));
            ResponseEntity<T> responseEntity = restOperations.postForEntity(uri, entity, response);
            return responseEntity.getBody();
        }
        catch (HttpClientErrorException ex){
            LOG.error(String.format("Client Exception occurred while calling uri %s", uri.toString()));
            throw ex;
        }
        catch (HttpServerErrorException ex){
            LOG.error(ex);
            throw new SapSubscriptionSystemException("Unable to perform operation. Check server logs for more details.");
        }
        catch (RestClientException ex) {
            LOG.error(ex);
            throw new SapSubscriptionConfigurationException("Unable to perform operation. Check configuration or logs for more details.");
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new SapSubscriptionSystemException( "Unknown Exception" );
        }
    }

    public <T> T getEntity(UriComponents uriComponents, Class<T> clazz) {
        ResponseEntity<T> response = getRawEntity(uriComponents, clazz);
        return response.getBody();
    }

    public <T> ResponseEntity<T> getRawEntity(UriComponents uriComponents, Class<T> clazz) {

        //Fetch Auth Token
        final ConsumedDestinationModel destinationModel = destinationService.getDestinationById(SUBSCRIPTION_BILLING_DESTINATION_ID);
        if(destinationModel==null){
            throw new SapSubscriptionConfigurationException(String.format("Missing Destination model [%s]", SUBSCRIPTION_BILLING_DESTINATION_ID));
        }
        RestOperations restOperations = buildRestOperations(destinationModel);

        //Build Url
        URI uri = UriComponentsBuilder.fromUriString(destinationModel.getUrl())
                .uriComponents(uriComponents)
                .build()
                .toUri();

        //Call API
        try {
            LOG.info(String.format("Calling uri: %s", uri.toString()));
            return restOperations.getForEntity(uri, clazz);
        }
        catch (HttpClientErrorException ex){
            LOG.error(String.format("Client Exception occurred while calling uri %s", uri.toString()));
            throw ex;
        }
        catch (HttpServerErrorException ex){
            LOG.error(ex);
            throw new SapSubscriptionSystemException("Unable to perform operation. Check server logs for more details.");
        }
        catch (RestClientException ex) {
            LOG.error(ex);
            throw new SapSubscriptionConfigurationException("Unable to perform operation. Check configuration or logs for more details.");
        }
        catch (Exception ex){
            LOG.error(ex);
            throw new SapSubscriptionSystemException( "Unknown Exception" );
        }
    }

    /**
     * Building RestOperations include Fetching OAuth Credentials only as of 1905.11
     * @param destinationModel Consumed Destination Model which contains Authentication details
     * @return {@link RestOperations} Object which contains OAuth token
     */
    private RestOperations buildRestOperations(ConsumedDestinationModel destinationModel){

        return integrationRestTemplateFactory.create(destinationModel);
    }

    //<editor-fold desc="Getters and Setters">

    public void setDestinationService(DestinationService<ConsumedDestinationModel> destinationService) {
        this.destinationService = destinationService;
    }

    public void setIntegrationRestTemplateFactory(IntegrationRestTemplateFactory integrationRestTemplateFactory) {
        this.integrationRestTemplateFactory = integrationRestTemplateFactory;
    }

    //</editor-fold>
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync;

import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import rx.Observable;

public class TestOutboundFacade extends ExternalResource implements OutboundServiceFacade {
    private static final Logger LOG = Log.getLogger(TestOutboundFacade.class);
    private static final URI SOME_URI = createUri();
    private static final ResponseEntity<Map> DEFAULT_RESPONSE = ResponseEntity.created(SOME_URI).build();

    private final Collection<Invocation> invocations;
    private ResponseEntity<Map> responseEntity;
    private Queue<ResponseEntity<Map>> responseQueue;

    public TestOutboundFacade() {
        responseEntity = DEFAULT_RESPONSE;
        invocations = Collections.synchronizedList(new ArrayList<>());
        responseQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * Creates new instance of this test facade, which will respond with HTTP 201 Created to all requests it receives.
     * @return new test facade instance.
     */
    public static TestOutboundFacade respondWithCreated() {
        return new TestOutboundFacade().respondWith(ResponseEntity.created(SOME_URI));
    }

    /**
     * Creates new instance of this test facade, which will respond with HTTP 404 Not Found to all requests it receives.
     * @return new test facade instance.
     */
    public static TestOutboundFacade respondWithNotFound() {
        return new TestOutboundFacade().respondWith(ResponseEntity.notFound());
    }

    @SafeVarargs
    public final <T extends ResponseEntity.HeadersBuilder> TestOutboundFacade respondWith(final T... builders) {
            final List<ResponseEntity<Map>> entities = Stream.of(builders)
                                                             .map(this::createResponse)
                                                             .collect(Collectors.toList());
            responseQueue.addAll(entities);
            if (! entities.isEmpty()) {
                responseEntity = entities.get(entities.size() - 1);
            }
            return this;
    }

    private ResponseEntity<Map> createResponse(ResponseEntity.HeadersBuilder builder)
    {
    	return builder.build();
    }

    @Override
    public Observable<ResponseEntity<Map>> send(final ItemModel itemModel, final String integrationObjectCode, final String destination) {
        LOG.info("Sending {} item {} to {}", integrationObjectCode, itemModel, destination);
        invocations.add(new Invocation(itemModel, integrationObjectCode, destination));
        final var response = nextResponse();
        LOG.info("Responding with {}", response);
        return Observable.just(response);
    }

    private ResponseEntity<Map> nextResponse() {
        final var response = responseQueue.poll();
        return response != null ? response : responseEntity;
    }

    public int invocations() {
        return invocations.size();
    }

    @Override
    protected void after() {
        invocations.clear();
    }

    /**
     * Retrieves items captured by this facade.
     * @param dest consumed destination, to which the items should have been sent.
     * @param ioCode code of IntegrationObject used for the items sent.
     * @return a collection of items send to the specified destination with the specified IntegrationObject code or an empty
     * collection, if no items were sent with the specified parameters.
     */
    Collection<ItemModel> itemsFromInvocationsTo(ConsumedDestinationModel dest, String ioCode) {
        return itemsFromInvocationsTo(dest.getId(), ioCode);
    }

    /**
     * Retrieves items captured by this facade.
     * @param dest specifies consumed destination ID, to which the items should have been sent.
     * @param ioCode code of IntegrationObject used for the items sent.
     * @return a collection of items send to the specified destination with the specified IntegrationObject code or an empty
     * collection, if no items were sent with the specified parameters.
     */
    Collection<ItemModel> itemsFromInvocationsTo(String dest, String ioCode) {
        return invocations.stream()
                .filter(inv -> inv.matches(dest, ioCode))
                .map(Invocation::getItemModel)
                .collect(Collectors.toList());
    }

	private static URI createUri()
	{
		try
		{
			return new URI("//does.not/matter");
		}
		catch (final URISyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}

    private static class Invocation {
        private final String destination;
        private final String integrationObject;
        private final ItemModel item;

        Invocation(ItemModel it, String ioCode, String dest) {
            destination = dest;
            integrationObject = ioCode;
            item = it;
        }

        ItemModel getItemModel() {
            return item;
        }

        boolean matches(String dest, String ioCode) {
            return destination.equals(dest) && integrationObject.equals(ioCode);
        }
    }
}

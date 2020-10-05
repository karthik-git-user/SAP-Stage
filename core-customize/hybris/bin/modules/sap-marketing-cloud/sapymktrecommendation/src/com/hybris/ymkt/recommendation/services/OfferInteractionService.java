/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendation.services;

import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.core.MediaType;

import org.apache.olingo.odata2.api.client.batch.BatchChangeSet;
import org.apache.olingo.odata2.api.client.batch.BatchChangeSetPart;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext.Interaction;
import com.hybris.ymkt.recommendation.dao.OfferInteractionContext.Offer;
import com.hybris.ymkt.recommendationbuffer.model.SAPOfferInteractionModel;
import com.hybris.ymkt.recommendationbuffer.service.RecommendationBufferService;


/**
 * This services is used to send offer interactions to SAP Marketing
 */
public class OfferInteractionService
{
	protected static final String COMMUNICATION_MEDIUM = "ONLINE_SHOP";

	private static final Logger LOG = LoggerFactory.getLogger(OfferInteractionService.class);

	protected static final int MAX_FAILURE = 3;
	protected static final int READTIMEOUT = 300000;

	protected ModelService modelService;
	protected ODataService oDataService;
	protected int readBatchSize;
	protected RecommendationBufferService recommendationBufferService;
	protected UserContextService userContextService;

	/**
	 * Helper method that will bundle all the interactions for the property "Interactions" in the payload we send to ymkt
	 *
	 * @param model
	 * @return Interaction Map
	 */
	protected Map<String, Object> buildInteractionMap(final SAPOfferInteractionModel model)
	{
		final Map<String, String> offerMap = new HashMap<>();
		offerMap.put("MarketinOffer", model.getOfferId());
		offerMap.put("MarketingOfferContent", model.getOfferContentItemId());
		offerMap.put("OfferRecommendationScenario", model.getOfferRecommendationScenarioId());

		final Map<String, Object> interactionMap = new HashMap<>();
		interactionMap.put("CommunicationMedium", COMMUNICATION_MEDIUM);
		interactionMap.put("InteractionTimeStampUTC", model.getTimeStamp());
		interactionMap.put("InteractionType", model.getInteractionType());
		interactionMap.put("InteractionIsAnonymous", model.getContactId().isEmpty());
		interactionMap.put("InteractionContactId", model.getContactId());
		interactionMap.put("InteractionContactOrigin", model.getContactIdOrigin());
		interactionMap.put("InteractionOffers", offerMap);

		return interactionMap;
	}

	// converts offer interaction map from #buildInteractionMap into ChangeSet part
	private BatchChangeSetPart convertToChangeSetPart(final Map<String, Object> interaction)
	{
		try
		{
			final Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", MediaType.APPLICATION_JSON);
			headers.put("Accept", MediaType.APPLICATION_JSON);

			final EdmEntityType entityTypeInteraction = this.oDataService.getEntitySet("Interactions").getEntityType();
			final String body = this.oDataService.convertMapToJSONString(entityTypeInteraction, interaction);

			return BatchChangeSetPart.method("POST") //
					.uri("./Interactions") //
					.body(body) //
					.headers(headers) //
					.build();
		}
		catch (EdmException | IOException e)
		{
			throw new IllegalStateException("Error converting interaction " + interaction + " to changeset part.", e);
		}
	}

	/**
	 * Saves the interaction in the database table with itemType SAPOfferInteraction
	 *
	 * @param offerInteractionContext
	 *           {@link OfferInteractionContext}
	 */
	public void saveOfferInteraction(final OfferInteractionContext offerInteractionContext)
	{
		try
		{
			final Interaction interaction = offerInteractionContext.getInteractions().get(0); // we limit to 1 interaction
			final Offer offer = interaction.getOffers().get(0); // we limit to 1 offer

			interaction.setContactId(this.userContextService.getUserId());
			interaction.setContactIdOrigin(this.userContextService.getUserOrigin());

			final SAPOfferInteractionModel offerInteractionModel = this.modelService.create(SAPOfferInteractionModel.class);

			offerInteractionModel.setTimeStamp(offerInteractionContext.getTimestamp());
			offerInteractionModel.setContactId(interaction.getContactId());
			offerInteractionModel.setContactIdOrigin(interaction.getContactIdOrigin());
			offerInteractionModel.setInteractionType(interaction.getInteractionType());
			offerInteractionModel.setOfferId(offer.getId());
			offerInteractionModel.setOfferContentItemId(offer.getContentItemId());
			offerInteractionModel.setOfferRecommendationScenarioId(offer.getRecommendationScenarioId());

			this.modelService.save(offerInteractionModel);
		}
		catch (final ModelSavingException e)
		{
			LOG.error("An error occurred while saving Offer Interaction with {}", offerInteractionContext, e);
		}
	}

	protected boolean sendOfferInteraction(final List<SAPOfferInteractionModel> interactionModels)
	{
		final BatchChangeSet changeSet = BatchChangeSet.newBuilder().build();
		Objects.requireNonNull(interactionModels).stream() //
				.map(this::buildInteractionMap) //
				.map(this::convertToChangeSetPart) //
				.forEach(changeSet::add);

		final String boundary = "batch";
		final URL url = oDataService.createURL("$batch");
		final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST", url);
		request.getRequestProperties().put("Accept", MediaType.APPLICATION_JSON);
		request.getRequestProperties().put("Content-Type", "multipart/mixed;boundary=" + boundary);
		request.getRequestProperties().put("Content-Encoding", "gzip");
		request.setReadTimeout(READTIMEOUT);

		try (final InputStream batchRequest = EntityProvider.writeBatchRequest(Collections.singletonList(changeSet), boundary))
		{
			final byte[] payload = this.oDataService.bufferStream(batchRequest);
			final byte[] payloadGZIP = this.compressGZIP(payload);
			request.setPayload(payloadGZIP);
			this.oDataService.executeWithRetry(request);
		}
		catch (final IOException e)
		{
			LOG.error("Error posting {} interactions.", interactionModels.size(), e);
			return false;
		}
		return true;
	}

	/**
	 * Send all offer interaction records via OData service.
	 */
	public void sendOfferInteractions()
	{
		int offerInteractionListSize;
		int successCounter = 0;
		int failureCounter = 0;
		long readTime = 0;
		long sendTime = 0;
		long deleteTime = 0;

		do
		{
			final long readStartTime = System.currentTimeMillis();
			final List<SAPOfferInteractionModel> offerInteractions = recommendationBufferService.getOfferInteractions(readBatchSize);
			readTime += System.currentTimeMillis() - readStartTime;

			final long sendStartTime = System.currentTimeMillis();
			final boolean iterationSuccess = this.sendOfferInteraction(offerInteractions);
			sendTime += System.currentTimeMillis() - sendStartTime;

			if (iterationSuccess)
			{
				final long deleteStartTime = System.currentTimeMillis();
				offerInteractions.forEach(modelService::remove);
				deleteTime += System.currentTimeMillis() - deleteStartTime;

				successCounter++;
			}
			else
			{
				failureCounter++;
			}

			offerInteractionListSize = offerInteractions.size();
		}
		while (offerInteractionListSize == readBatchSize && failureCounter <= MAX_FAILURE);

		LOG.info("Send Offer Interactions: Successful={}, Failed={}, RetrieveTime={}ms, SendTime={}ms, DeleteTime={}ms",
				successCounter, failureCounter, readTime, sendTime, deleteTime);
	}

	private byte[] compressGZIP(final byte[] payload) throws IOException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			try (GZIPOutputStream gzos = new GZIPOutputStream(baos))
			{
				gzos.write(payload);
			}
			return baos.toByteArray();
		}
	}

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setODataService(ODataService oDataService)
	{
		this.oDataService = oDataService;
	}

	@Required
	public void setReadBatchSize(int readBatchSize)
	{
		LOG.debug("readBatchSize={}", readBatchSize);
		this.readBatchSize = readBatchSize;
	}

	@Required
	public void setRecommendationBufferService(RecommendationBufferService recommendationBufferService)
	{
		this.recommendationBufferService = recommendationBufferService;
	}

	@Required
	public void setUserContextService(UserContextService userContextService)
	{
		this.userContextService = userContextService;
	}

}

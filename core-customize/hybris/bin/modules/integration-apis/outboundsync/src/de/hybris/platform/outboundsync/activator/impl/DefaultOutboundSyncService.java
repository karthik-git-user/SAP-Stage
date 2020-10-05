/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.outboundsync.activator.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer;
import de.hybris.platform.outboundsync.activator.OutboundSyncService;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;
import de.hybris.platform.outboundsync.dto.OutboundItemDTOGroup;
import de.hybris.platform.outboundsync.events.AbortedOutboundSyncEvent;
import de.hybris.platform.outboundsync.events.CompletedOutboundSyncEvent;
import de.hybris.platform.outboundsync.job.OutboundItemFactory;
import de.hybris.platform.outboundsync.retry.RetryUpdateException;
import de.hybris.platform.outboundsync.retry.SyncRetryService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import rx.Observable;

/**
 * Default implementation of {@link OutboundSyncService} that uses {@link OutboundServiceFacade} for sending changes to the
 * destinations.
 */
public class DefaultOutboundSyncService implements OutboundSyncService
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundSyncService.class);

	private ModelService modelService;
	private OutboundItemFactory outboundItemFactory;
	private OutboundServiceFacade outboundServiceFacade;
	private OutboundItemConsumer outboundItemConsumer;
	private SyncRetryService syncRetryService;
	private EventService eventService;

	@Override
	public void sync(final Collection<OutboundItemDTO> outboundItemDTOs)
	{
		final OutboundItemDTOGroup outboundItemDTOGroup = OutboundItemDTOGroup.from(outboundItemDTOs, getOutboundItemFactory());
		final CronJobModel cronJob = getCronJob(outboundItemDTOGroup);
		if (isCronJobAborting(cronJob))
		{
			publishAbortEvent(cronJob, outboundItemDTOGroup);
		}
		else if (!isCronJobAborted(cronJob))
		{
			synchronizeItem(outboundItemDTOGroup);
		}
	}

	private void publishAbortEvent(final CronJobModel cronJob, final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		getEventService().publishEvent(new AbortedOutboundSyncEvent(cronJob.getPk(), outboundItemDTOGroup.getOutboundItemDTOs().size()));
	}

	private void synchronizeItem(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		final Long rootItemPk = outboundItemDTOGroup.getRootItemPk();
		LOG.debug("Synchronizing changes in item with PK={}", rootItemPk);

		final ItemModel itemModel = findItemByPk(PK.fromLong(rootItemPk));
		if (itemModel != null)
		{
			synchronizeItem(outboundItemDTOGroup, itemModel);
		}
	}

	private void synchronizeItem(final OutboundItemDTOGroup dtoGroup, final ItemModel itemModel)
	{
		try
		{
			final String integrationObjectCode = dtoGroup.getIntegrationObjectCode();
			final String destinationCode = dtoGroup.getDestinationId();
			final Observable<ResponseEntity<Map>> outboundResponse =
					getOutboundServiceFacade().send(itemModel, integrationObjectCode, destinationCode);
			outboundResponse.subscribe(r -> handleResponse(r, dtoGroup), e -> handleError(e, dtoGroup));
		}
		catch (final RuntimeException e)
		{
			handleError(e, dtoGroup);
		}
	}

	private boolean isCronJobAborting(final CronJobModel cronJob)
	{
		return cronJob != null && Boolean.TRUE.equals(cronJob.getRequestAbort());
	}

	private boolean isCronJobAborted(final CronJobModel cronJob)
	{
		return cronJob != null && cronJob.getStatus() == CronJobStatus.ABORTED;
	}

	private CronJobModel getCronJob(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		final CronJobModel cronJobModel = (CronJobModel) findItemByPk(outboundItemDTOGroup.getCronJobPk());
		getModelService().refresh(cronJobModel);
		return cronJobModel;
	}

	protected void handleError(final Throwable throwable, final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		LOG.error("Failed to send item with PK={}", outboundItemDTOGroup.getRootItemPk(), throwable);
		handleError(outboundItemDTOGroup);
	}

	protected void handleError(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		LOG.warn("The item with PK={} couldn't be synchronized", outboundItemDTOGroup.getRootItemPk());
		try
		{
			eventService.publishEvent(new CompletedOutboundSyncEvent(outboundItemDTOGroup.getCronJobPk(), false,
					outboundItemDTOGroup.getOutboundItemDTOs().size()));
			if (getSyncRetryService().handleSyncFailure(outboundItemDTOGroup))
			{
				consumeChanges(outboundItemDTOGroup);
			}
		}
		// Due to the observable.onerror flow, we'll never get to this catch block. The plan is to get rid of the Observable in
		// the facade invocation, so this code block will then be correct
		catch (final RetryUpdateException e)
		{
			LOG.debug("Retry could not be updated", e);
		}
	}

	protected void handleResponse(final ResponseEntity<Map> responseEntity, final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		if (isSuccessResponse(responseEntity))
		{
			handleSuccessfulSync(outboundItemDTOGroup);
		}
		else
		{
			handleError(outboundItemDTOGroup);
		}
	}

	protected ItemModel findItemByPk(final PK pk)
	{
		try
		{
			if (pk != null)
			{
				return getModelService().get(pk);
			}
			LOG.debug("Not finding the item because the PK provided is null");
		}
		catch (final ModelLoadingException e)
		{
			LOG.warn("The item with PK={} was not found. Caused by {}", pk, e);
		}
		return null;
	}

	protected void handleSuccessfulSync(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		LOG.debug("The product with PK={} has been synchronized", outboundItemDTOGroup.getRootItemPk());
		try
		{
			getSyncRetryService().handleSyncSuccess(outboundItemDTOGroup);
			consumeChanges(outboundItemDTOGroup);
			eventService.publishEvent(new CompletedOutboundSyncEvent(outboundItemDTOGroup.getCronJobPk(), true, outboundItemDTOGroup.getOutboundItemDTOs().size()));
		}
		catch (final RetryUpdateException e)
		{
			LOG.debug("Retry could not be updated", e);
		}
	}

	private boolean isSuccessResponse(final ResponseEntity<Map> responseEntity)
	{
		return responseEntity.getStatusCode() == HttpStatus.CREATED || responseEntity.getStatusCode() == HttpStatus.OK;
	}

	protected void consumeChanges(final OutboundItemDTOGroup outboundItemDTOGroup)
	{
		outboundItemDTOGroup.getOutboundItemDTOs().forEach(getOutboundItemConsumer()::consume);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	private EventService getEventService()
	{
		return eventService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected OutboundItemFactory getOutboundItemFactory()
	{
		return outboundItemFactory;
	}

	@Required
	public void setOutboundItemFactory(final OutboundItemFactory factory)
	{
		outboundItemFactory = factory;
	}

	protected OutboundServiceFacade getOutboundServiceFacade()
	{
		return outboundServiceFacade;
	}

	@Required
	public void setOutboundServiceFacade(final OutboundServiceFacade outboundServiceFacade)
	{
		this.outboundServiceFacade = outboundServiceFacade;
	}

	protected OutboundItemConsumer getOutboundItemConsumer()
	{
		return outboundItemConsumer;
	}

	@Required
	public void setOutboundItemConsumer(final OutboundItemConsumer outboundItemConsumer)
	{
		this.outboundItemConsumer = outboundItemConsumer;
	}

	protected SyncRetryService getSyncRetryService()
	{
		return syncRetryService;
	}

	@Required
	public void setSyncRetryService(final SyncRetryService syncRetryService)
	{
		this.syncRetryService = syncRetryService;
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}
}


/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.core.PK;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.model.OutboundSyncCronJobModel;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

/**
 * Implementation of the {@link OutboundSyncJobRegister} that is used by default when no custom implementation is injected.
 */
public class DefaultOutboundSyncJobRegister implements OutboundSyncJobRegister, OutboundSyncStateObserver
{
	private static final Logger LOG = Log.getLogger(DefaultOutboundSyncJobRegister.class);

	private final Map<PK, OutboundSyncJobStateAggregator> allRunningJobs;
	private final ModelService modelService;

	public DefaultOutboundSyncJobRegister(@NotNull final ModelService service)
	{
		modelService = service;
		allRunningJobs = new ConcurrentHashMap<>();
	}

	@Override
	public @NotNull OutboundSyncJob getNewJob(@NotNull final OutboundSyncCronJobModel jobModel)
	{
		unregister(jobModel);
		return getJob(jobModel);
	}

	@Override
	public Optional<OutboundSyncJob> getJob(@NotNull final PK jobPk)
	{
		final OutboundSyncJobStateAggregator aggregator = allRunningJobs.get(jobPk);
		if (aggregator == null)
		{
			return findJobModel(jobPk)
					.map(this::getJob);
		}
		return Optional.of(aggregator);
	}

	private Optional<OutboundSyncCronJobModel> findJobModel(final @NotNull PK jobPk)
	{
		LOG.debug("Searching for a job model corresponding to {} PK", jobPk);
		try
		{
			final Object item = modelService.get(jobPk);
			return Optional.ofNullable(item)
			               .filter(OutboundSyncCronJobModel.class::isInstance)
			               .map(OutboundSyncCronJobModel.class::cast);
		}
		catch (final ModelLoadingException e)
		{
			return Optional.empty();
		}
	}

	@Override
	public @NotNull OutboundSyncJob getJob(@NotNull final OutboundSyncCronJobModel jobModel)
	{
		return getRegisteredJob(jobModel);
	}

	private OutboundSyncJobStateAggregator getRegisteredJob(final @NotNull OutboundSyncCronJobModel job)
	{
		return allRunningJobs.computeIfAbsent(job.getPk(), pk -> createJobStateAggregator(job));
	}

	protected OutboundSyncJobStateAggregator createJobStateAggregator(final @NotNull OutboundSyncCronJobModel job)
	{
		LOG.debug("Registering new running job {}", job);
		final OutboundSyncJobStateAggregator aggregator = OutboundSyncJobStateAggregator.create(job);
		aggregator.addStateObserver(this);
		return aggregator;
	}

	@Override
	public void stateChanged(@NotNull final OutboundSyncCronJobModel model, @NotNull final OutboundSyncState state)
	{
		if (state.isAllItemsProcessed())
		{
			unregister(model);
		}
		persistJobState(model, state);
	}

	private void unregister(final OutboundSyncCronJobModel model)
	{
		LOG.debug("Unregistering job {}", model);
		allRunningJobs.remove(model.getPk());
	}

	protected void persistJobState(@NotNull final OutboundSyncCronJobModel job, @NotNull final OutboundSyncState state)
	{
		modelService.refresh(job);
		job.setStartTime(state.getStartTime());
		job.setResult(state.getCronJobResult());
		job.setStatus(state.getCronJobStatus());
		if (state.isAllItemsProcessed())
		{
			job.setEndTime(state.getEndTime());
			job.setRequestAbort(null);
		}
		LOG.info("Persisting job {} state {}", job, state);
		modelService.save(job);
	}
}

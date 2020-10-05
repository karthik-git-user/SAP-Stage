/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaloginaddon.setup.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.addonsupport.setup.impl.DefaultAddonSampleDataImportService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncCronJobModel;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Sample data import service to import sample data for SAP CDC integration.
 */
public class GigyaLoginAddonSampleDataImportService extends DefaultAddonSampleDataImportService
{

	private static final String CONENT_CATALOG_LOG_MESSAGE = "-ContentCatalog to ";
	private static final String CDC_CONENT_CATALOG = "-cdcContentCatalog";

	private List<String> catalogs;

	private ModelService modelService;

	private CronJobService cronJobService;

	@Override
	protected void importContentCatalog(final SystemSetupContext context, final String importRoot,
			final String catalogName)
	{
		if (catalogs.contains(catalogName))
		{
			// 1 - create new content catalog for cdc by replication from existing
			// electronics or powertools catalog
			replicateContentCatalog(context, importRoot, catalogName);
		}

		// 2 - import content catalog from impex
		super.importContentCatalog(context, importRoot, catalogName);

		if (catalogs.contains(catalogName))
		{
			// 3 - synchronize cdc ContentCatalog:staged->online
			synchronizeContentCatalog(context, catalogName + "-cdc", true);
		}
	}

	private void replicateContentCatalog(final SystemSetupContext context, final String importRoot,
			final String catalogName)
	{
		logInfo(context, "Begin importing Content Catalog [" + catalogName + CDC_CONENT_CATALOG + "]");

		importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/catalog.impex",
				false);

		final CatalogVersionModel catalog = getCatalogVersionService()
				.getCatalogVersion(catalogName + CDC_CONENT_CATALOG, "Staged");

		List<SyncItemJobModel> synItemsJobs = catalog.getIncomingSynchronizations();
		if (CollectionUtils.isNotEmpty(synItemsJobs))
		{
			SyncItemJobModel job = synItemsJobs.get(0);

			SyncItemCronJobModel syncCronJob = getModelService().create(CatalogVersionSyncCronJobModel.class);
			syncCronJob.setJob(job);
			syncCronJob.setLogToDatabase(false);
			syncCronJob.setLogToFile(false);
			syncCronJob.setForceUpdate(false);
			getModelService().save(syncCronJob);

			synchronizeCDCContentCatalog(context, syncCronJob, catalogName);
		}

	}

	private void synchronizeCDCContentCatalog(final SystemSetupContext context, final SyncItemCronJobModel syncCronJob,
			final String catalogName)
	{
		logInfo(context, "Begin synchronizing Content Catalog [" + catalogName + CONENT_CATALOG_LOG_MESSAGE
				+ catalogName + CDC_CONENT_CATALOG + "] - synchronizing");

		cronJobService.performCronJob(syncCronJob, true);

		logInfo(context, "Synchronization complete for catalog [" + catalogName + CONENT_CATALOG_LOG_MESSAGE
				+ catalogName + CDC_CONENT_CATALOG + "]");
		
		
		final CronJobResult result = syncCronJob.getResult();
		final CronJobStatus status = syncCronJob.getStatus();

		PerformResult syncCronJobResult = new PerformResult(result, status);
		if (isSyncRerunNeeded(syncCronJobResult))
		{
			logInfo(context, "Catalog [" + catalogName + CONENT_CATALOG_LOG_MESSAGE + catalogName + CDC_CONENT_CATALOG
					+ "] sync has issues.");
		}

		logInfo(context, "Completed synchronizing Content Catalog [" + catalogName + CONENT_CATALOG_LOG_MESSAGE
				+ catalogName + CDC_CONENT_CATALOG + "]");
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	public List<String> getCatalogs()
	{
		return catalogs;
	}

	public void setCatalogs(List<String> catalogs)
	{
		this.catalogs = catalogs;
	}

	public CronJobService getCronJobService()
	{
		return cronJobService;
	}

	public void setCronJobService(CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

}

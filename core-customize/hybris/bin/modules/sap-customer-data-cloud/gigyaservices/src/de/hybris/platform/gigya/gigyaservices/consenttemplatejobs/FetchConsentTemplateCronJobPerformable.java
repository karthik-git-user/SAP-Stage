/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.consenttemplatejobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.gigya.gigyaservices.model.FetchConsentTemplateCronJobModel;
import de.hybris.platform.outboundservices.facade.impl.DefaultOutboundServiceFacade;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

public class FetchConsentTemplateCronJobPerformable extends AbstractJobPerformable<FetchConsentTemplateCronJobModel> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FetchConsentTemplateCronJobPerformable.class);
	private DefaultOutboundServiceFacade outboundServiceFacade;

	@Override
	public PerformResult perform(FetchConsentTemplateCronJobModel consentTemplateCronJob) {
		BaseSiteModel baseSiteConfiguration= new BaseSiteModel();
		baseSiteConfiguration.setGigyaConfig(consentTemplateCronJob.getBaseSite().getGigyaConfig());
		baseSiteConfiguration.setUid(consentTemplateCronJob.getBaseSite().getUid());
		this.outboundServiceFacade.send(baseSiteConfiguration, "OutboundBaseSite", "cdc-fetchconsenttemplatedestination")
			.subscribe(

				responseEntityMap -> {
					consentTemplateCronJob.setResult(CronJobResult.SUCCESS);
					LOGGER.info("Replication Of Consent Template from CDC to Commerce executed successfully");
				},
				// onError
				error -> {
					consentTemplateCronJob.setResult(CronJobResult.ERROR);
					LOGGER.error("Replication of Consent Template from CDC to Commerce failed", error);
				});

		return new PerformResult(consentTemplateCronJob.getResult(), CronJobStatus.FINISHED);
		
	}



	public DefaultOutboundServiceFacade getOutboundServiceFacade() {
		return outboundServiceFacade;
	}

	@Required
	public void setOutboundServiceFacade(DefaultOutboundServiceFacade outboundServiceFacade) {
		this.outboundServiceFacade = outboundServiceFacade;
	}
}

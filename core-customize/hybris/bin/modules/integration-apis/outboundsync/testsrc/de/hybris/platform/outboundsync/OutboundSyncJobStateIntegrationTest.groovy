/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.core.PK
import de.hybris.platform.core.Registry
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.cronjob.model.CronJobModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade
import de.hybris.platform.outboundsync.activator.impl.DefaultOutboundSyncService
import de.hybris.platform.outboundsync.job.impl.OutboundSyncCronJobPerformable
import de.hybris.platform.outboundsync.util.OutboundSyncTestUtil
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.cronjob.CronJobService
import org.junit.Rule
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.EventualCondition.eventualCondition
import static de.hybris.platform.outboundservices.ConsumedDestinationBuilder.consumedDestinationBuilder

@IntegrationTest
@Issue('https://jira.hybris.com/browse/STOUT-3404')
class OutboundSyncJobStateIntegrationTest extends ServicelayerSpockSpecification {
    private static final def IO = 'OutboundSyncJobState'
    private static final def SOME_URI = new URI('//does.not/matter')
    private static final def CATALOG = 'Test'
    private static final def CATALOG_VERSION = 'OutboundSyncJobState'

    @Resource
    private CronJobService cronJobService
    @Resource(name = 'outboundSyncService')
    private DefaultOutboundSyncService outboundSyncService
    @Resource(name = 'outboundServiceFacade')
    private OutboundServiceFacade outboundServiceFacade

    TestItemChangeDetector changeDetector = new TestItemChangeDetector()
    @Rule
    TestOutboundFacade outboundDestination = TestOutboundFacade.respondWithCreated()

    OutboundSyncJob contextJob

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                               ; $IO",
                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code) ; root[default = false]',
                "                                   ; $IO                                   ; Category           ; Category   ; true",
                '$integrationItem = integrationObjectItem(integrationObject(code), code)[unique = true]',
                '$attributeName = attributeName[unique = true]',
                '$attributeDescriptor = attributeDescriptor(enclosingType(code), qualifier)',
                'INSERT_UPDATE IntegrationObjectItemAttribute; $integrationItem; $attributeName ; $attributeDescriptor',
                "                                            ; $IO:Category    ; code           ; Category:code")
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Catalog; id[unique=true]',
                "                     ; $CATALOG",
                'INSERT_UPDATE CatalogVersion; version[unique=true]; catalog(id)[unique=true]',
                "                            ; $CATALOG_VERSION    ; $CATALOG")
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeSafely CatalogModel, { it.id == CATALOG }
        IntegrationTestUtil.removeAll IntegrationObjectModel
    }

    def setup() {
        importCsv '/impex/essentialdata-outboundsync.impex', 'UTF-8'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE OutboundChannelConfiguration; code[unique = true]; integrationObject(code); destination',
                "                                          ; outboundCategories ; $IO                    ; ${contextDestination()}")
        changeDetector.createChangeStream 'outboundCategories', 'Category'
        contextJob = new OutboundSyncJob()
        outboundSyncService.outboundServiceFacade = outboundDestination
    }

    def cleanup() {
        outboundSyncService.outboundServiceFacade = outboundServiceFacade
        changeDetector.after() // consume all items before removing stream container
        IntegrationTestUtil.removeAll CategoryModel
        OutboundSyncTestUtil.cleanup()
    }

    @Test
    def 'outboundsync job is immediately FINISHED when there are no changes'() {
        when: 'the job is executed without changes present'
        contextJob.start()

        then: 'its status is changed to FINISHED'
        with(contextJob) {
            status == CronJobStatus.FINISHED
            result == CronJobResult.SUCCESS
        }
    }

    @Test
    def 'outboundsync job has RUNNING status while changes are being sent'() {
        given: 'at least one change is present'
        categoriesChanged('some-category')

        when: 'the job is executed'
        contextJob.start()

        then: 'its status is changed to RUNNING'
        with(contextJob) {
            assert status == CronJobStatus.RUNNING
            assert result == CronJobResult.UNKNOWN
        }

        cleanup:
        eventualCondition().expect {
            assert contextJob.refresh().status == CronJobStatus.FINISHED
        }
    }

    @Test
    def 'outboundsync job finishes successfully when all changes are sent'() {
        given: 'changes are present'
        categoriesChanged('category1', 'category2')
        and:
        def job = contextCronJob()

        when: 'the job is executed'
        cronJobService.performCronJob(job, true)

        then: 'its status is changed to FINISHED with SUCCESS result'
        eventualCondition().expect {
            def currentJob = contextCronJob()
            LoggerFactory.getLogger(Specification).info('Context Job: {}, {}, {}', currentJob, currentJob.status, currentJob.result)
            with currentJob, {
                assert status == CronJobStatus.FINISHED
                assert result == CronJobResult.SUCCESS
            }
        }
    }

    @Test
    def 'outboundsync job fails when at least one change is not sent'() {
        given: 'changes are present'
        categoriesChanged('willFail', 'willPass')
        and: 'responses from the sync destination for the changes are failure and then success'
        outboundDestination.respondWith(ResponseEntity.badRequest(), ResponseEntity.created(SOME_URI))
        and:
        OutboundSyncJob job = new OutboundSyncJob()

        when: 'the job is executed'
        job.start()

        then: 'its status is changed to FINISHED with ERROR'
        eventualCondition().expect {
            with(job.refresh()) {
                assert status == CronJobStatus.FINISHED
                assert result == CronJobResult.ERROR
            }
        }
    }

    @Test
    def 'outboundsync job reports error when something unexpected happens'() {
        setup: 'the job is misconfigured'
        def jobPerformable = Registry.applicationContext.getBean 'defaultOutboundSyncCronJobPerformable', OutboundSyncCronJobPerformable
        def eventServiceBackup = jobPerformable.eventService
        jobPerformable.eventService = null
        and: 'a change is present'
        categoriesChanged('some-category')

        when: 'the job is executed'
        contextJob.start()

        then: 'its status is changed to FINISHED with ERROR'
        with(contextJob) {
            assert status == CronJobStatus.FINISHED
            assert result == CronJobResult.FAILURE
        }
        and: 'the change is not consumed'
        !changeDetector.allCurrentChanges.empty

        cleanup: 'restore the itemChangeSender'
        jobPerformable.eventService = eventServiceBackup
    }

    @Test
    @Unroll
    def "outboundsync job can be aborted with #res result when change #condition"() {
        given: 'a change is present'
        categoriesChanged('some-category')
        and: "the change #condition"
        outboundDestination.respondWith(response)

        when: 'the job is kicked off'
        contextJob.start()
        and: 'the job is aborted'
        contextJob.abort()

        then: 'job status remains RUNNING while the queued change is not processed'
        contextJob.refresh().status == CronJobStatus.RUNNING

        then: 'eventually job status is changed to ABORTED'
        eventualCondition().expect {
            with(contextJob) {
                assert status == CronJobStatus.ABORTED
                assert result == res
                assert !changeDetector.allCurrentChanges.empty
                assert outboundDestination.invocations() == 0
            }
        }

        where:
        response                         | condition           | res
        ResponseEntity.created(SOME_URI) | 'sent successfully' | CronJobResult.SUCCESS
        ResponseEntity.badRequest()      | 'failed to send'    | CronJobResult.ERROR
    }

    @Test
    def 'outboundsync job does not start again when it is already running'() {
        given: 'a change is present'
        categoriesChanged('category1')
        and: 'the job is executed'
        contextJob.start()
        and: 'another change is added'
        categoriesChanged('category2')

        when: 'the job is started again'
        contextJob.start()

        then: 'after the job finishes only first change is processed, the second change is not'
        eventualCondition().expect {
            assert contextJob.refresh().status == CronJobStatus.FINISHED
            assert changeDetector.allCurrentChanges.size() == 1
            assert outboundDestination.invocations() == 1
        }
    }

    private static PK contextDestination() {
        consumedDestinationBuilder().withId('outboundsync-job-state').build().pk
    }

    private static CronJobModel contextCronJob() {
        OutboundSyncTestUtil.outboundCronJob()
    }

    private static void categoriesChanged(String... codes) {
        def impexLines = ['INSERT_UPDATE Category; code[unique = true]; catalogVersion(version, catalog(id))']
        impexLines.addAll codes.collect({ "                      ; $it; $CATALOG_VERSION:$CATALOG" })
        IntegrationTestUtil.importImpEx(impexLines as String[])
    }

    private static class OutboundSyncJob {
        private static final def LOG = LoggerFactory.getLogger Specification
        private static final def cronJobService = Registry.applicationContext.getBean 'cronJobService', CronJobService
        private CronJobModel job

        OutboundSyncJob() {
            job = contextCronJob()
        }

        private OutboundSyncJob(CronJobModel model) {
            job = model
        }

        void start() {
            cronJobService.performCronJob(job, true)
        }

        void abort() {
            cronJobService.requestAbortCronJob(job)
        }

        OutboundSyncJob refresh() {
            def fresh = IntegrationTestUtil.refresh(job)
            LOG.info('Context Job: {}, {}, {}', fresh, fresh.status, fresh.result)
            new OutboundSyncJob(fresh)
        }

        CronJobStatus getStatus() {
            job.status
        }

        CronJobResult getResult() {
            job.result
        }
    }
}

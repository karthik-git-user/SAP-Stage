/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job.impl.handlers

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.cronjob.enums.CronJobResult
import de.hybris.platform.cronjob.enums.CronJobStatus
import de.hybris.platform.outboundsync.events.AbortedOutboundSyncEvent
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.outboundsync.job.impl.OutboundSyncState.Builder.initialOutboundSyncState

@UnitTest
class AbortedOutboundSyncEventHandlerUnitTest extends Specification {
    private static final PK SOME_JOB_PK = PK.fromLong(12)
    private static final int TOTAL_ITEMS = 34
    private static final int ERROR_COUNT = 10
    private static final int SUCCESS_COUNT = 22

    def handler = AbortedOutboundSyncEventHandler.createHandler()

    @Test
    def 'getHandledEventClass returns AbortedOutboundSyncEvent'() {
        expect:
        handler.getHandledEventClass() == AbortedOutboundSyncEvent
    }

    @Test
    def 'only postAbortItems count changes while total number of items is unknown'() {
        given: 'initial state with all attributes initialized'
        def initialState = initialOutboundSyncState()
                .withSuccessCount(3)
                .withErrorCount(2)
                .withUnprocessedCount(1)
                .build()
        and: 'an event with one item processed'
        def event = new AbortedOutboundSyncEvent(SOME_JOB_PK, 1)

        when:
        def updatedState = handler.handle(event, initialState)

        then: 'only postAbortCount is changed'
        with(updatedState) {
            totalItemsRequested.empty
            startTime == initialState.startTime
            cronJobStatus == initialState.cronJobStatus
            endTime == initialState.endTime
            successCount == initialState.successCount
            errorCount == initialState.errorCount
            unprocessedCount == initialState.unprocessedCount + event.changesProcessed
        }
    }

    @Test
    def 'only postAbortItems count changes while not all items processed'() {
        given: 'initial state with all attributes initialized'
        def initialState = initialOutboundSyncState()
                .withTotalItems(8)
                .withSuccessCount(3)
                .withErrorCount(2)
                .withUnprocessedCount(1)
                .build()
        and: 'an event with one item processed'
        def event = new AbortedOutboundSyncEvent(SOME_JOB_PK, 1)

        when:
        def updatedState = handler.handle(event, initialState)

        then: 'only postAbortCount is changed'
        with(updatedState) {
            totalItemsRequested == initialState.totalItemsRequested
            startTime == initialState.startTime
            cronJobStatus == initialState.cronJobStatus
            endTime == initialState.endTime
            successCount == initialState.successCount
            errorCount == initialState.errorCount
            unprocessedCount == initialState.unprocessedCount + event.changesProcessed
        }
    }

    @Test
    @Unroll
    def 'job result is #expectedResult and status is ABORTED when current state has #errorCount errors and all items are processed'() {
        given:
        def initialState = outboundSyncState()
                .withTotalItems(TOTAL_ITEMS)
                .withSuccessCount(successCount)
                .withErrorCount(errorCount)
                .build()
        and:
        def event = new AbortedOutboundSyncEvent(SOME_JOB_PK, 2)

        when:
        def updatedState = handler.handle(event, initialState)

        then:
        with(updatedState) {
            totalItemsRequested == initialState.totalItemsRequested
            startTime == initialState.startTime
            cronJobStatus == CronJobStatus.ABORTED
            endTime >= startTime
            successCount == initialState.successCount
            errorCount == initialState.errorCount
            unprocessedCount == event.changesProcessed
        }

        where:
        errorCount  | successCount  | expectedResult
        ERROR_COUNT | SUCCESS_COUNT | CronJobResult.ERROR
        0           | 32            | CronJobResult.SUCCESS
    }

    def outboundSyncState() {
        initialOutboundSyncState().withSuccessCount(SUCCESS_COUNT)
    }
}

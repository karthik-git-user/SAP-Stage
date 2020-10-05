/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.processor

import com.google.common.collect.Lists
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.IntegrationAttributeException
import de.hybris.platform.integrationservices.exception.InvalidAttributeValueException
import de.hybris.platform.odata2services.config.ODataServicesConfiguration
import de.hybris.platform.odata2services.odata.InvalidDataException
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException
import de.hybris.platform.odata2services.odata.persistence.InvalidEntryDataException
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory
import de.hybris.platform.odata2services.odata.persistence.PersistenceRuntimeApplicationException
import de.hybris.platform.odata2services.odata.persistence.PersistenceService
import de.hybris.platform.odata2services.odata.persistence.StorageRequest
import de.hybris.platform.odata2services.odata.persistence.StorageRequestFactory
import de.hybris.platform.odata2services.odata.persistence.exception.InvalidPropertyValueException
import de.hybris.platform.odata2services.odata.persistence.exception.ItemNotFoundException
import de.hybris.platform.odata2services.odata.processor.reader.EntityReader
import de.hybris.platform.odata2services.odata.processor.reader.EntityReaderRegistry
import de.hybris.platform.servicelayer.exceptions.ModelSavingException
import org.apache.olingo.odata2.api.batch.BatchHandler
import org.apache.olingo.odata2.api.batch.BatchRequestPart
import org.apache.olingo.odata2.api.batch.BatchResponsePart
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.apache.olingo.odata2.api.edm.EdmException
import org.apache.olingo.odata2.api.edm.EdmProperty
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties
import org.apache.olingo.odata2.api.ep.entry.ODataEntry
import org.apache.olingo.odata2.api.exception.ODataException
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.processor.ODataRequest
import org.apache.olingo.odata2.api.processor.ODataResponse
import org.apache.olingo.odata2.api.uri.KeyPredicate
import org.apache.olingo.odata2.api.uri.PathInfo
import org.apache.olingo.odata2.api.uri.UriInfo
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo
import org.assertj.core.util.Maps
import org.junit.Test
import spock.lang.Specification

import javax.ws.rs.core.MediaType
import java.util.stream.Collectors

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROPERTY_NAME
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_JSON
import static org.apache.olingo.odata2.api.commons.HttpContentType.APPLICATION_XML

@UnitTest
class DefaultODataProcessorUnitTest extends Specification {

	private static final String CONTENT = 'BodyContent'
	private static final String ENTITY_REQUESTED = 'Products'
	private static final String INTEGRATION_KEY = 'defaultKey'

	def oDataProcessor = Spy(DefaultODataProcessor)

	def entry = Stub(ODataEntry)
	def edmEntityType = Stub(EdmEntityType)
	def edmEntitySet = GroovySpy(EdmEntitySet)
	def storageRequest = Stub(StorageRequest)
	def storageRequestFactory = Stub(StorageRequestFactory)
	def persistenceService = Mock(PersistenceService)
	def entityProviderWriteProperties = Stub(EntityProviderWriteProperties)
	def oDataResponse = Stub(ODataResponse)
	def itemLookupRequestFactory = Mock(ItemLookupRequestFactory)
	def entityReaderRegistry = Stub(EntityReaderRegistry)
	def responseBuilder = Mock(ODataResponse.ODataResponseBuilder)
	def oDataContext = Stub(ODataContext)
	def oDataServicesConfiguration = Stub(ODataServicesConfiguration)

	def setup() {
		entry.getProperties() >> ['TestName': 'TestValue']

		storageRequest.getIntegrationKey() >> INTEGRATION_KEY
		storageRequest.getAcceptLocale() >> { new Locale("en") }

		storageRequestFactory.create(_, _, _, _) >> storageRequest

		oDataServicesConfiguration.getBatchLimit() >> 2

		oDataContext.getPathInfo() >> Stub(PathInfo)

		oDataProcessor.getoDataResponseBuilder(_) >> responseBuilder
		oDataProcessor.writeProperties() >> entityProviderWriteProperties
		oDataProcessor.readEntry(_, _, _) >> entry
		oDataProcessor.writeEntry(_, _, _, entityProviderWriteProperties) >> oDataResponse

		oDataProcessor.setStorageRequestFactory(storageRequestFactory)
		oDataProcessor.setPersistenceService(persistenceService)
		oDataProcessor.setEntityReaderRegistry(entityReaderRegistry)
		oDataProcessor.setItemLookupRequestFactory(itemLookupRequestFactory)
		oDataProcessor.setContext(oDataContext)
		oDataProcessor.setODataServicesConfiguration(oDataServicesConfiguration)
	}

	@Test
	def "create entity success returns ODataResponse"() throws ODataException {
		given:
		persistenceService.createEntityData(_ as StorageRequest) >> entry

		when:
		final ODataResponse oDataResponse = oDataProcessor.createEntity(
				stubUriInfo('Staged|Default|ProductCode'),
				stubRequestBody(CONTENT),
				APPLICATION_XML, APPLICATION_XML)

		then:
		1 * responseBuilder.header("Content-Language", _) >> responseBuilder
		1 * responseBuilder.build() >> oDataResponse
		oDataResponse != null

	}

	@Test
	def "readEntity sets response content language header"() {
		given:
		def itemLookupRequest = Stub(ItemLookupRequest) {
			getAcceptLocale() >> new Locale("fr")
		}
		givenoDataResponseIsReturned(itemLookupRequest)

		when:
		oDataProcessor.readEntity(Stub(UriInfo), MediaType.APPLICATION_JSON)

		then:
		1 * responseBuilder.header("Content-Language", "fr") >> responseBuilder
		1 * responseBuilder.build()
	}

	@Test
	def "readEntitySet returns response"() {
		given:
		def itemLookupRequest = Stub(ItemLookupRequest) {
			getAcceptLocale() >> new Locale("fr")
		}
		givenoDataResponseIsReturned(itemLookupRequest)

		when:
		def response = oDataProcessor.readEntitySet(Stub(UriInfo), MediaType.APPLICATION_JSON)

		then:
		1 * responseBuilder.header("Content-Language", "fr") >> responseBuilder
		1 * responseBuilder.build() >> oDataResponse
		response == oDataResponse
	}

	@Test
	def "create entity sets response content language header"() {
		given:
		persistenceService.createEntityData(_ as StorageRequest) >> entry

		when:
		oDataProcessor.createEntity(
				stubUriInfo('Staged|Default|ProductCode'),
				stubRequestBody(CONTENT),
				APPLICATION_XML, APPLICATION_XML)

		then:
		1 * responseBuilder.header("Content-Language", "en") >> responseBuilder
		1 * responseBuilder.build()
	}

	@Test
	def "countEntitySet returns response"() {
		given:
		def itemLookupRequest = Stub(ItemLookupRequest) {
			getAcceptLocale() >> new Locale("en")
		}
		givenoDataResponseIsReturned(itemLookupRequest)

		when:
		def response = oDataProcessor.countEntitySet(
				stubUriInfo('Staged|Default|ProductCode'), APPLICATION_JSON)

		then:
		1 * responseBuilder.header("Content-Language", "en") >> responseBuilder
		1 * responseBuilder.build() >> oDataResponse
		response == oDataResponse
	}

	@Test
	def "an exception is thrown when EntityReader cannot find applicable reader on read entity"() {
		given:
		entityReaderRegistry.getReader(_ as UriInfo) >> { throw new InternalProcessingException() }

		when:
		oDataProcessor.readEntity(Stub(UriInfo), APPLICATION_XML)

		then:
		thrown(RetrievalErrorRuntimeException)
	}

	@Test
	def "an exception is thrown when EntityReader cannot find applicable reader on read entity set"() {
		given:
		entityReaderRegistry.getReader(_ as UriInfo) >> { throw new ItemNotFoundException() }

		when:
		oDataProcessor.readEntitySet(Stub(UriInfo), APPLICATION_XML)

		then:
		thrown(RetrievalErrorRuntimeException)
	}

	@Test
	def "the exception is handled when an exception occurs creating an entity"() {
		given:
		givenoDataResponseIsReturned(Stub(ItemLookupRequest))
		persistenceService.createEntityData(_ as StorageRequest) >> entry

		when:
		oDataProcessor.createEntity(
				stubUriInfo('Staged|Default|ProductCode'),
				stubRequestBody(CONTENT),
				APPLICATION_XML, APPLICATION_XML)

		then:
		oDataProcessor.readEntry(_, _, _) >> { throw new RuntimeException('Something went wrong') }
		def e = thrown(ODataPayloadProcessingException)
		e.code == 'odata_error'
	}

	@Test
	def "the exception is handled when an exception occurs parsing the batch request"() {
		given:
		oDataProcessor.parseBatchRequest(_, _, _) >> { throw new RuntimeException('Something went wrong') }

		when:
		oDataProcessor.executeBatch(
				Stub(BatchHandler),
				APPLICATION_XML,
				stubRequestBody(CONTENT))

		then:
		def e = thrown(ODataPayloadProcessingException)
		e.code == 'odata_error'
	}


	@Test
	def "the exception is handled when an exception occurs creating an entity in the persistence service"() {
		given:
		givenoDataResponseIsReturned(Stub(ItemLookupRequest))
		persistenceService.createEntityData(_) >> { throw new RuntimeException() }

		when:
		oDataProcessor.createEntity(
				stubUriInfo('Staged|Default|ProductCode'),
				stubRequestBody(CONTENT),
				APPLICATION_XML, APPLICATION_XML)

		then:
		def e = thrown(PersistenceRuntimeApplicationException)
		e.code == 'runtime_error'
		e.integrationKey == INTEGRATION_KEY
	}

	@Test
	def "the exception is handled when the persistence service throws a InvalidDataException while creating an entity"() {
		given:
		givenoDataResponseIsReturned(Stub(ItemLookupRequest))
		persistenceService.createEntityData(_) >> { throw new InvalidDataException("Test exception message", "test_code", "EntityType") }

		when:
		oDataProcessor.createEntity(
				stubUriInfo('Staged|Default|ProductCode'),
				stubRequestBody(CONTENT),
				APPLICATION_XML, APPLICATION_XML)

		then:
		def e = thrown(InvalidEntryDataException)
		e.code == 'test_code'
		e.integrationKey == INTEGRATION_KEY
	}

	@Test
	def "the exception is handled when the persistence service throws a ModelServiceException while creating an entity"() {
		given:
		givenoDataResponseIsReturned(Stub(ItemLookupRequest))
		persistenceService.createEntityData(_) >> { throw new ModelSavingException() }

		when:
		oDataProcessor.createEntity(
				stubUriInfo('Staged|Default|ProductCode'),
				stubRequestBody(CONTENT),
				APPLICATION_XML, APPLICATION_XML)

		then:
		def e = thrown(PersistenceRuntimeApplicationException)
		e.code == 'runtime_error'
		e.integrationKey == INTEGRATION_KEY
	}


	@Test
	def "the exception is handled when a batch exceeds the batch limit"() {
		given:
		def exceptionCause = 'Something went wrong'
		oDataProcessor.parseBatchRequest(_, _, _) >> {
			Lists.newArrayList(Stub(BatchRequestPart), Stub(BatchRequestPart), Stub(BatchRequestPart))
		}

		when:
		oDataProcessor.executeBatch(
				Stub(BatchHandler),
				APPLICATION_XML,
				stubRequestBody(CONTENT))

		then:
		def e = thrown(BatchLimitExceededException)
		e.message == String.format(
				"The number of integration objects sent in the " +
						"request has exceeded the 'odata2services.batch.limit' setting currently set to 2",
				exceptionCause)
		e.code == 'batch_limit_exceeded'
	}

	@Test
	def "an exception is thrown when the persistence service throws a #exception while creating an entity"() {
		given:
		givenoDataResponseIsReturned(Stub(ItemLookupRequest))
		persistenceService.createEntityData(_) >> { throw exception }

		when:
		oDataProcessor.createEntity(
				stubUriInfo('Staged|Default|ProductCode'),
				stubRequestBody(CONTENT),
				APPLICATION_XML, APPLICATION_XML)

		then:
		thrown exceptionThrown

		where:
		exception                            | exceptionThrown
		Stub(IntegrationAttributeException)  | IntegrationAttributeException
		Stub(InvalidAttributeValueException) | InvalidPropertyValueException
	}

	@Test
	def "processor parses batch successfully"() {
		given:
		def part1 = Stub(BatchRequestPart)
		def part2 = Stub(BatchRequestPart)

		def batchHandler = Mock(BatchHandler) {
			handleBatchPart(part1) >> Stub(BatchResponsePart)
			handleBatchPart(part2) >> Stub(BatchResponsePart)
		}
		def parts = Lists.newArrayList(part1, part2)
		oDataProcessor.parseBatchRequest(_, _, _) >> { parts }

		when:
		oDataProcessor.executeBatch(batchHandler, "multipart/mixed", stubRequestBody(CONTENT))

		then:
		1 * oDataProcessor.writeBatchResponse(_ as List) >> Stub(ODataResponse)

		1 * batchHandler.handleBatchPart(part1)
		1 * batchHandler.handleBatchPart(part2)
	}

	@Test
	def "processor parses change set in transaction"() {
		given:
		def request1 = Stub(ODataRequest) {
			getStatus() >> HttpStatusCodes.CREATED
		}
		def request2 = Stub(ODataRequest) {
			getStatus() >> HttpStatusCodes.CREATED
		}
		def response1 = Stub(ODataResponse)
		def response2 = Stub(ODataResponse)

		def batchHandler = Mock(BatchHandler) {
			handleRequest(request1) >> Stub(BatchResponsePart)
			handleRequest(request2) >> Stub(BatchResponsePart)
		}

		when:
		oDataProcessor.executeChangeSet(batchHandler, Lists.newArrayList(request1, request2))

		then:
		1 * oDataProcessor.beginTransaction() >> { void }
		1 * oDataProcessor.executeInTransaction(_, _) >> Lists.newArrayList(response1, response2)
		1 * oDataProcessor.commitTransaction() >> { void }

		1 * oDataProcessor.partFromResponses(_ as List) >> { args ->
			assert args[0].size == 2
			assert args[0].containsAll(response1, response2)
		}
	}

	@Test
	def "delete entity when an EdmException is thrown"()
	{
		given:
		def anyUriInfo = Stub(DeleteUriInfo)
		def anyOdataContext = Stub(ODataContext)
		itemLookupRequestFactory.create(anyUriInfo , anyOdataContext) >> Stub(ItemLookupRequest)
		persistenceService.deleteItem(_) >> { throw Stub(EdmException) }

		when:
		def response = oDataProcessor.deleteEntity(null, null)

		then:
		response.entity == ""
		response.status == HttpStatusCodes.INTERNAL_SERVER_ERROR
	}

	@Test
	def "delete entity success"()
	{
		given:
		def uriInfo = Stub(DeleteUriInfo)
		def anyOdataContext = Stub(ODataContext)
		itemLookupRequestFactory.create(uriInfo , anyOdataContext) >> Stub(ItemLookupRequest)
		persistenceService.deleteItem(_) >> { }

		when:
		def response = oDataProcessor.deleteEntity(uriInfo, null)

		then:
		response.entity == ""
		response.status == HttpStatusCodes.OK
		1 * itemLookupRequestFactory.create(uriInfo, _)
		1* persistenceService.deleteItem(_)
	}

	def givenoDataResponseIsReturned(final ItemLookupRequest itemLookupRequest) {
		itemLookupRequestFactory.create(_, _, _) >> itemLookupRequest

		def oDataResponse = Stub(ODataResponse)

		def entityReader = Stub(EntityReader) {
			read(_ as ItemLookupRequest) >> oDataResponse
		}

		entityReaderRegistry.getReader(_ as UriInfo) >> entityReader
	}

	def stubUriInfo(integrationKey) throws EdmException {

		edmEntityType.getName() >> ENTITY_REQUESTED
		edmEntitySet.getEntityType() >> edmEntityType
		edmEntitySet.getName() >> ENTITY_REQUESTED

		def postUriInfo = Stub(UriInfo) {
			getStartEntitySet() >> edmEntitySet
			getKeyPredicates() >> stubKeyPredicates(Maps.newHashMap(INTEGRATION_KEY_PROPERTY_NAME, integrationKey))
		}

		postUriInfo
	}

	def stubRequestBody(bodyContent) {
		new ByteArrayInputStream(bodyContent.getBytes())
	}

	def stubKeyPredicates(final Map<String, String> keys) {

		def keyPredicateList = keys.entrySet().stream().map({ entry ->
			def property = Stub(EdmProperty) {
				getName() >> entry.getKey()
			}

			def keyPredicate = Stub(KeyPredicate) {
				getLiteral() >> entry.getValue()
				getProperty() >> property
			}
			keyPredicate
		}).collect(Collectors.toList())

		keyPredicateList
	}
}
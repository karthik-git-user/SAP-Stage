/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyauserdeleteservices.jobs;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.amazon.media.services.S3StorageServiceFactory;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.configuration.Configuration;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;


/**
 * Test class for CloseCustomerAccountCronJobPerformable class
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CloseCustomerAccountCronJobPerformableTest
{

	private static final String SAMPLE_CSV_CONTENT_FIRST_FILE = "\"callID\";\"authType\";\"@timestamp\";\"errCode\";\"errMessage\";\"endpoint\";\"userKey\";\"httpReq\";\"ip\";\"params\";\"uid\";\"apikey\";\"userAgent\";\"userKeyDetails\"\n"
			+ "\"abc\";\"authType\";\"2019-09-23T06:12:24.221Z\";\"0\";\"OK\";\"event-name\";\"errCode\";\"{\"\"errMessage\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\";\"data\";\"{\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"false\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\";\"123\";\"data\";\"{\"\"os\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"0.0\"\",\"\"data\"\":\"\"data\"\"}\";\"{\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\"\n"
			+ "\"abc\";\"authType\";\"2019-09-23T06:12:24.221Z\";\"0\";\"OK\";\"event-name\";\"errCode\";\"{\"\"errMessage\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\";\"data\";\"{\"\"data\"\":\"\"\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"false\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\";\"321\";\"data\";\"{\"\"os\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"0.0\"\",\"\"data\"\":\"\"data\"\"}\";\"{\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\"";

	private static final String SAMPLE_CSV_CONTENT_SECOND_FILE = "\"callID\";\"authType\";\"@timestamp\";\"errCode\";\"errMessage\";\"endpoint\";\"userKey\";\"httpReq\";\"ip\";\"params\";\"uid\";\"apikey\";\"userAgent\";\"userKeyDetails\"\n"
			+ "\"abc\";\"authType\";\"2019-09-23T06:12:24.221Z\";\"0\";\"OK\";\"event-name\";\"errCode\";\"{\"\"errMessage\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\";\"data\";\"{\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"false\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\";\"098\";\"data\";\"{\"\"os\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"0.0\"\",\"\"data\"\":\"\"data\"\"}\";\"{\"\"data\"\":\"\"data\"\",\"\"data\"\":\"\"data\"\"}\"\n";


	@InjectMocks
	private final CloseCustomerAccountCronJobPerformable closeCustomerAccountCronJobPerformable = new CloseCustomerAccountCronJobPerformable()
	{

		@Override
		protected String getFormatterCurrentDate()
		{
			return "2019-10-07-14-03-35";
		}

	};

	@Mock
	private S3StorageServiceFactory s3StorageServiceFactory;

	@Mock
	private GigyaLoginService gigyaLoginService;

	@Mock
	private EventService eventService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Mock
	private ModelService modelService;

	@Mock
	private AmazonS3 s3Service;

	@Mock
	private S3Object s3Object;

	@Mock
	private Configuration configuration;

	@Mock
	private CustomerModel firstCustomer;

	@Mock
	private CustomerModel secondCustomer;

	@Mock
	private CustomerModel thirdCustomer;

	private CronJobModel cronJob;

	@Mock
	private HttpRequestBase httpRequestBase;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private BaseSiteModel baseSite;

	@Mock
	private ObjectListing objectListing;

	@Mock
	private S3ObjectSummary firstS3ObjectSummary;

	@Mock
	private S3ObjectSummary secondS3ObjectSummary;

	@Before
	public void setUp()
	{
		Mockito.when(s3StorageServiceFactory.getS3Service(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(s3Service);
		Mockito.when(s3Service.getObject(Mockito.any())).thenReturn(s3Object);
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getString("gigyauserdeleteservices.s3.config.objectnames.prefix")).thenReturn("test");

		Mockito.when(configuration.getString("gigyauserdeleteservices.s3.config.bucketId")).thenReturn("bucketId");
		Mockito.when(configuration.getString("gigyauserdeleteservices.s3.config.processed.folder.name")).thenReturn("processed");
		cronJob = new CronJobModel();
	}

	@Test
	public void testCloseAccountJobWithMultipleCustomers()
	{
		Mockito.when(s3Service.listObjects("bucketId", "test")).thenReturn(objectListing);
		Mockito.when(objectListing.getObjectSummaries()).thenReturn(Collections.singletonList(firstS3ObjectSummary));
		Mockito.when(firstS3ObjectSummary.getBucketName()).thenReturn("bucketId");
		Mockito.when(firstS3ObjectSummary.getKey()).thenReturn("test1.csv");

		final InputStream sampleInputStream = new ByteArrayInputStream(SAMPLE_CSV_CONTENT_FIRST_FILE.getBytes());
		final S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(sampleInputStream, httpRequestBase);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid("123")).thenReturn(firstCustomer);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid("321")).thenReturn(secondCustomer);
		Mockito.when(gigyaConfigGenericDao.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, "")))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getSites()).thenReturn(Collections.singletonList(baseSite));
		Mockito.when(s3Object.getObjectContent()).thenReturn(s3ObjectInputStream);
		Mockito.when(s3Object.getKey()).thenReturn("test1.csv");

		final PerformResult result = closeCustomerAccountCronJobPerformable.perform(cronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Mockito.verify(firstCustomer).setDeactivationDate(Date.from(Instant.parse("2019-09-23T06:12:24.221Z")));
		Mockito.verify(modelService).save(firstCustomer);
		Mockito.verify(secondCustomer).setDeactivationDate(Date.from(Instant.parse("2019-09-23T06:12:24.221Z")));
		Mockito.verify(modelService).save(secondCustomer);
		Mockito.verify(eventService, Mockito.times(2)).publishEvent(Mockito.any());
		Mockito.verify(s3Service).copyObject("bucketId", "test1.csv", "bucketId",
				"processed/test1_processed_2019-10-07-14-03-35.csv");
	}

	@Test
	public void testCloseAccountJobWithMultipleCustomersAndFiles()
	{
		Mockito.when(s3Service.listObjects("bucketId", "test")).thenReturn(objectListing);
		Mockito.when(objectListing.getObjectSummaries()).thenReturn(Arrays.asList(firstS3ObjectSummary, secondS3ObjectSummary));
		Mockito.when(firstS3ObjectSummary.getBucketName()).thenReturn("bucketId");
		Mockito.when(firstS3ObjectSummary.getKey()).thenReturn("test1.csv");

		Mockito.when(secondS3ObjectSummary.getBucketName()).thenReturn("bucketId");
		Mockito.when(secondS3ObjectSummary.getKey()).thenReturn("test2.csv");

		final InputStream firstInputStream = new ByteArrayInputStream(SAMPLE_CSV_CONTENT_FIRST_FILE.getBytes());
		final S3ObjectInputStream firstS3ObjectInputStream = new S3ObjectInputStream(firstInputStream, httpRequestBase);

		final InputStream secondInputStream = new ByteArrayInputStream(SAMPLE_CSV_CONTENT_SECOND_FILE.getBytes());
		final S3ObjectInputStream secondS3ObjectInputStream = new S3ObjectInputStream(secondInputStream, httpRequestBase);

		Mockito.when(gigyaLoginService.findCustomerByGigyaUid("123")).thenReturn(firstCustomer);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid("321")).thenReturn(secondCustomer);
		Mockito.when(gigyaLoginService.findCustomerByGigyaUid("098")).thenReturn(thirdCustomer);
		Mockito.when(gigyaConfigGenericDao.find(Collections.singletonMap(GigyaConfigModel.GIGYAAPIKEY, "")))
				.thenReturn(Collections.singletonList(gigyaConfig));
		Mockito.when(gigyaConfig.getSites()).thenReturn(Collections.singletonList(baseSite));
		Mockito.when(s3Object.getObjectContent()).thenReturn(firstS3ObjectInputStream, secondS3ObjectInputStream);
		Mockito.when(s3Object.getKey()).thenReturn("test1.csv", "test2.csv");

		final PerformResult result = closeCustomerAccountCronJobPerformable.perform(cronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());

		Mockito.verify(firstCustomer).setDeactivationDate(Date.from(Instant.parse("2019-09-23T06:12:24.221Z")));
		Mockito.verify(modelService).save(firstCustomer);
		Mockito.verify(secondCustomer).setDeactivationDate(Date.from(Instant.parse("2019-09-23T06:12:24.221Z")));
		Mockito.verify(modelService).save(secondCustomer);
		Mockito.verify(thirdCustomer).setDeactivationDate(Date.from(Instant.parse("2019-09-23T06:12:24.221Z")));
		Mockito.verify(modelService).save(thirdCustomer);
		Mockito.verify(eventService, Mockito.times(3)).publishEvent(Mockito.any());
		Mockito.verify(s3Service).copyObject("bucketId", "test1.csv", "bucketId",
				"processed/test1_processed_2019-10-07-14-03-35.csv");
		Mockito.verify(s3Service).copyObject("bucketId", "test2.csv", "bucketId",
				"processed/test2_processed_2019-10-07-14-03-35.csv");
	}

	@Test
	public void testCloseAccountJobWhenNoFilesExist()
	{
		Mockito.when(s3Service.listObjects("bucketId", "test")).thenReturn(null);

		final PerformResult result = closeCustomerAccountCronJobPerformable.perform(cronJob);

		Assert.assertEquals(CronJobResult.SUCCESS, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

	@Test
	public void testCloseAccountJobWhenErrorOccurs()
	{
		Mockito.when(s3Service.listObjects("bucketId", "test")).thenReturn(objectListing);
		Mockito.when(objectListing.getObjectSummaries()).thenReturn(Collections.singletonList(firstS3ObjectSummary));
		Mockito.when(firstS3ObjectSummary.getBucketName()).thenReturn("bucketId");
		Mockito.when(firstS3ObjectSummary.getKey()).thenReturn("test1.csv");

		Mockito.when(s3Object.getObjectContent()).thenThrow(new RuntimeException());

		final PerformResult result = closeCustomerAccountCronJobPerformable.perform(cronJob);

		Assert.assertEquals(CronJobResult.FAILURE, result.getResult());
		Assert.assertEquals(CronJobStatus.FINISHED, result.getStatus());
	}

}

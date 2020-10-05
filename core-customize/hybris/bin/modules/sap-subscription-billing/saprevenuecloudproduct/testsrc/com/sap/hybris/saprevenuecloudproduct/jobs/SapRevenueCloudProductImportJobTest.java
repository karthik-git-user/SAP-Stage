package com.sap.hybris.saprevenuecloudproduct.jobs;

import com.sap.hybris.saprevenuecloudproduct.model.SapRevenueCloudProductCronjobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class SapRevenueCloudProductImportJobTest {

    @Mock
    OutboundServiceFacade mockOutboundServiceFacade;

    @Mock
    ResponseEntity<Map> mockResponseEntity;


    @InjectMocks
    SapRevenueCloudProductImportJob sapRevenueCloudProductImportJob = new SapRevenueCloudProductImportJob();

    SapRevenueCloudProductCronjobModel job = new SapRevenueCloudProductCronjobModel();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        job.setCode("SapRevenueCloudProductCronjob");

    }

    @Test
    public void perform_success(){

        Observable<ResponseEntity<Map>> t = Observable.from(new ResponseEntity[]{mockResponseEntity});;
        when(mockOutboundServiceFacade.send(eq(job),any(String.class),any(String.class))).thenReturn(t);
        PerformResult performResult = sapRevenueCloudProductImportJob.perform(job);

        Assert.assertEquals(performResult.getResult() , CronJobResult.SUCCESS);
        Assert.assertEquals(performResult.getStatus() , CronJobStatus.FINISHED);

    }
}

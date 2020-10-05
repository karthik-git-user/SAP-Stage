package com.sap.hybris.saprevenuecloudproduct.jobs;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.poi.ss.formula.functions.T;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class SapRevenueCloudBusinessConfigImportJobTest {

    @Mock
    OutboundServiceFacade mockOutboundServiceFacade;

    @Mock
    ResponseEntity<Map> mockResponseEntity;

    @InjectMocks
    SapRevenueCloudBusinessConfigImportJob sapRevenueCloudBusinessConfigImportJob = new SapRevenueCloudBusinessConfigImportJob();
    CronJobModel job = new CronJobModel();

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        job.setCode("sapRevenueCloudBusinessConfigImportCronJob");
    }

    @Test
    public void perform_success(){

        Observable<ResponseEntity<Map>> t = Observable.from(new ResponseEntity[]{mockResponseEntity});;
        when(mockOutboundServiceFacade.send(eq(job),any(String.class),any(String.class))).thenReturn(t);
        PerformResult performResult = sapRevenueCloudBusinessConfigImportJob.perform(job);

        Assert.assertEquals(performResult.getResult() , CronJobResult.SUCCESS);
        Assert.assertEquals(performResult.getStatus() , CronJobStatus.FINISHED);

    }

}

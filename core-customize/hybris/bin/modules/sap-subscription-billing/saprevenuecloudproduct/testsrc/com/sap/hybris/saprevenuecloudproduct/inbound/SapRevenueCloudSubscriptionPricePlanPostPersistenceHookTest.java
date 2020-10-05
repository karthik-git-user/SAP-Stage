package com.sap.hybris.saprevenuecloudproduct.inbound;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import org.junit.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

public class SapRevenueCloudSubscriptionPricePlanPostPersistenceHookTest {

    @Mock
    private ModelService modelService;

    @Mock
    private ProductModel productModel;

    private final ItemModel itemModel = new ItemModel();
    private final SubscriptionPricePlanModel incomingPricePlanModel = new SubscriptionPricePlanModel();
    private final SubscriptionPricePlanModel incomingPricePlanModel1 = new SubscriptionPricePlanModel();


    @InjectMocks
    SapRevenueCloudSubscriptionPricePlanPostPersistenceHook  sapRevenueCloudSubscriptionPricePlanPostPersistenceHook = new SapRevenueCloudSubscriptionPricePlanPostPersistenceHook();


    @Test
    public void execute(){

        //scenario 1 : Disabling Old Rate Plan
        ZonedDateTime utcStartOfDay = LocalDate.now().atStartOfDay(ZoneId.of("UTC"));
        Date currentDate = Date.from(utcStartOfDay.toInstant());
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.YEAR, 1);
        Date newDate = c.getTime();

        //Incoming price plan
        incomingPricePlanModel.setPricePlanId("cbe4abdb-8724-4c8d-9586-b586e80ecebd");
        incomingPricePlanModel.setStartTime(new Date());
        incomingPricePlanModel.setEndTime(newDate);

        //Existing price plan
        SubscriptionPricePlanModel existingPricePlanModel = new SubscriptionPricePlanModel();
        existingPricePlanModel.setEndTime(newDate);
        existingPricePlanModel.setPricePlanId("abc4abdb-8724-4c8d-9586-b586e80ecebd");

        MockitoAnnotations.initMocks(this);
        List<PriceRowModel> priceRowModelList =  List.of(existingPricePlanModel, incomingPricePlanModel);
        Mockito.doReturn(priceRowModelList).when(productModel).getEurope1Prices();
        incomingPricePlanModel.setProduct(productModel);

        //Scenario 2: When there is only one rateplan
        incomingPricePlanModel1.setPricePlanId("cbe4abdb-8724-4c8d-9586-b586e80ecebd");
        incomingPricePlanModel1.setStartTime(new Date());
        incomingPricePlanModel1.setEndTime(newDate);

        MockitoAnnotations.initMocks(this);
        List<PriceRowModel> priceRowModelList1 =  List.of( incomingPricePlanModel1);
        Mockito.doReturn(priceRowModelList1).when(productModel).getEurope1Prices();
        incomingPricePlanModel1.setProduct(productModel);




        //Scenario1
        sapRevenueCloudSubscriptionPricePlanPostPersistenceHook.execute(incomingPricePlanModel);
        doNothing().when(modelService).save(any(SubscriptionPricePlanModel.class));
        assertEquals(existingPricePlanModel.getEndTime(), currentDate);

        //Scenario 2
        sapRevenueCloudSubscriptionPricePlanPostPersistenceHook.execute(incomingPricePlanModel1);
        doNothing().when(modelService).save(any(SubscriptionPricePlanModel.class));
        assertEquals(incomingPricePlanModel1.getEndTime(), newDate);

    }
}

package com.sap.hybris.sapcpqsbquotefacades.populator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpqsbintegration.model.CpqPricingParameterModel;
import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;
import com.sap.hybris.sapcpqsbintegration.service.SapCpqSbFetchQuoteDiscountsService;
import com.sap.hybris.sapcpqsbquotefacades.service.SapSubscriptionBillingEffectivePriceService;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sapcpqsbintegration.data.CpqPricingParameterData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.OneTimeChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.PerUnitUsageChargeData;
import de.hybris.platform.subscriptionfacades.data.RecurringChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeData;
import de.hybris.platform.subscriptionfacades.data.UsageChargeEntryData;
import de.hybris.platform.subscriptionfacades.data.VolumeUsageChargeData;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.PerUnitUsageChargeModel;
import de.hybris.platform.subscriptionservices.model.RecurringChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.UsageChargeModel;
import de.hybris.platform.subscriptionservices.model.VolumeUsageChargeModel;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqSubscriptionPricePlanPopulatorTest {

	@Mock
	Converter < OneTimeChargeEntryModel,
			OneTimeChargeEntryData > oneTimeChargeEntryConverter;
	@Mock
	Converter < RecurringChargeEntryModel,
			RecurringChargeEntryData > recurringChargeEntryConverter;
	@Mock
	Converter < PerUnitUsageChargeModel,
			PerUnitUsageChargeData > perUnitUsageChargeConverter;
	@Mock
	Converter < VolumeUsageChargeModel,
			VolumeUsageChargeData > volumeUsageChargeConverter;
	@Mock
	Converter < UsageChargeModel,
			UsageChargeData > usageChargeConverter;
	@Mock
	Converter < CpqPricingParameterModel,
			CpqPricingParameterData > cpqPricingPrameterConverter;
	@Mock
	SapSubscriptionBillingEffectivePriceService sapSubscriptionBillingEffectivePriceService;
	@Mock
	SapCpqSbFetchQuoteDiscountsService sapCpqSbFetchQuoteDiscounts;


	@InjectMocks
	DefaultSapCpqSubscriptionPricePlanPopulator <AbstractOrderEntryModel,OrderEntryData > defaultSapCpqSubscriptionPricePlanPopulator;

	private static final String MODEL_ID = "UID123";
	private static final String MODEL_NAME = "USER_OWNER";
	private static final double MODEL_PRICE = 1234d;
	private static final String SUBS_CODE = "subs_code";
	private static final String CONTRACT_LEN = "one year";
	private static final String SUBS_PLAN_ID = "subs_plan_id";
	private static final String PRICING_CODE = "pricing_code";
	private static final String SUBS_BILL_ID = "subs_bill_id";
	private static final String USAGE_CHRG_ID = "usage_charge_id";
	private static final String USAGE_CHRG_NAME = "usage_charge_name";
	private static final String PER_UNIT_CHRG_DATA_NAME = "pucd_name";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Date effectiveDate;


	private static List<CpqSubscriptionDetailModel> cpqSubscriptionDetailModelList = new ArrayList<>();
	private static List<OneTimeChargeEntryModel> oneTimeChargeEntryModelList = new ArrayList<>();
	private static List<CpqPricingParameterModel> pricingParametersList = new ArrayList<>();
	private static List<RecurringChargeEntryModel> recurringChargeEntryModelList = new ArrayList<>();
	private static List<UsageChargeModel> volumeUsageChargeModelList = new ArrayList<>();
	private static List<UsageChargeEntryModel> usageChargeEntryModelList = new ArrayList<>();
	private static List<UsageChargeEntryData> usageChargeEntryDataList = new ArrayList<>();

	private AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
	private OrderEntryData orderEntryData = new OrderEntryData();
	private SubscriptionPricePlanModel subscriptionPricePlanModel =new SubscriptionPricePlanModel();
	private static PerUnitUsageChargeData perUnitUsageChargeData = new PerUnitUsageChargeData();
	private static VolumeUsageChargeData volumeUsageChargeData = new VolumeUsageChargeData();
	private static RecurringChargeEntryData recurringChargeEntryData = new RecurringChargeEntryData();
	private static VolumeUsageChargeModel volumeUsageChargeModel = new VolumeUsageChargeModel();
	private static OneTimeChargeEntryData oneTimeChargeEntryData = new OneTimeChargeEntryData();
	private static QuoteModel quoteModel = new QuoteModel();
	private static CpqPricingParameterData cpqPricingParameterData= new CpqPricingParameterData();
	private static UsageChargeData usageChargeData = new UsageChargeData();
	private static UsageChargeEntryData usageChargeEntryData = new UsageChargeEntryData();
	private static Calendar calendar = Calendar.getInstance();

	@Before
	public void setUp(){

		try {
			effectiveDate = sdf.parse(sdf.format(new Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Required data creation
		PriceData priceData = new PriceData();
		priceData.setMaxQuantity(10L);
		priceData.setMinQuantity(10L);
		priceData.setPriceType(PriceDataType.BUY);
		priceData.setValue(new BigDecimal("1287123"));


		recurringChargeEntryData.setPrice(priceData);
		recurringChargeEntryData.setSubscriptionBillingId(SUBS_BILL_ID);

		OneTimeChargeEntryModel oneTimeChargeEntryModel = new OneTimeChargeEntryModel();
		RecurringChargeEntryModel recurringChargeEntryModel = new RecurringChargeEntryModel();


		UsageChargeEntryModel usageChargeEntryModel = new UsageChargeEntryModel();
		usageChargeEntryModel.setCreationtime(effectiveDate);
		usageChargeEntryModel.setPrice(112345d);
		usageChargeEntryModel.setId("ucem_id");
		usageChargeEntryModelList.add(usageChargeEntryModel);

		usageChargeEntryData.setFixedPrice(priceData);
		usageChargeEntryData.setPrice(priceData);
		usageChargeEntryDataList.add(usageChargeEntryData);

		perUnitUsageChargeData.setFromDate(effectiveDate);
		perUnitUsageChargeData.setName(PER_UNIT_CHRG_DATA_NAME);
		perUnitUsageChargeData.setNetAmount(priceData);
		perUnitUsageChargeData.setSubscriptionBillingId(SUBS_BILL_ID);
		priceData.setValue(new BigDecimal("88888123"));


		volumeUsageChargeData.setFromDate(effectiveDate);
		volumeUsageChargeData.setName(PER_UNIT_CHRG_DATA_NAME);
		volumeUsageChargeData.setNetAmount(priceData);
		volumeUsageChargeData.setSubscriptionBillingId(SUBS_BILL_ID);
		volumeUsageChargeData.setUsageChargeEntries(usageChargeEntryDataList);


		volumeUsageChargeModel.setId(USAGE_CHRG_ID);
		volumeUsageChargeModel.setSubscriptionBillingId(SUBS_BILL_ID);
		volumeUsageChargeModel.setUsageChargeEntries(usageChargeEntryModelList);

		CpqSubscriptionDetailModel cpqSubscriptionDetailModel = new CpqSubscriptionDetailModel();
		ProductModel product = new ProductModel();

		CpqPricingParameterModel cpqPricingParameterModel= new CpqPricingParameterModel();

		cpqPricingParameterModel.setCode(PRICING_CODE);
		cpqPricingParameterModel.setCreationtime(effectiveDate);
		pricingParametersList.add(cpqPricingParameterModel);


		oneTimeChargeEntryModel.setId(MODEL_ID);
		oneTimeChargeEntryModel.setPrice(MODEL_PRICE);
		oneTimeChargeEntryModelList.add(oneTimeChargeEntryModel);
		volumeUsageChargeModelList.add(volumeUsageChargeModel);
		recurringChargeEntryModel.setSubscriptionBillingId(SUBS_BILL_ID);
		recurringChargeEntryModelList.add(recurringChargeEntryModel);

		product.setSubscriptionCode(SUBS_CODE);

		calendar.setTime(effectiveDate);
		calendar.add(Calendar.YEAR, 1);

		cpqSubscriptionDetailModel.setOneTimeChargeEntries(oneTimeChargeEntryModelList);
		cpqSubscriptionDetailModel.setEffectiveDate(effectiveDate);
		cpqSubscriptionDetailModel.setContractEndDate(calendar.getTime());
		cpqSubscriptionDetailModel.setMinimumContractEndDate(calendar.getTime());
		cpqSubscriptionDetailModel.setContractLength(CONTRACT_LEN);
		cpqSubscriptionDetailModel.setMinimumContractLength(CONTRACT_LEN);
		cpqSubscriptionDetailModel.setSubscriptionPricePlanId(SUBS_PLAN_ID);
		cpqSubscriptionDetailModel.setPricingParameters(pricingParametersList);

		cpqSubscriptionDetailModelList.add(cpqSubscriptionDetailModel);
		abstractOrderEntryModel.setCpqSubscriptionDetails(cpqSubscriptionDetailModelList);
		abstractOrderEntryModel.setProduct(product);

		subscriptionPricePlanModel.setOneTimeChargeEntries(oneTimeChargeEntryModelList);
		subscriptionPricePlanModel.setRecurringChargeEntries(recurringChargeEntryModelList);
		subscriptionPricePlanModel.setUsageCharges(volumeUsageChargeModelList);

		//mock
		when(sapSubscriptionBillingEffectivePriceService.getSubscriptionEffectivePricePlan(any(ProductModel.class),any(Date.class))).thenReturn(subscriptionPricePlanModel);
		when( recurringChargeEntryConverter.convert(any())).thenReturn(recurringChargeEntryData);
		when( perUnitUsageChargeConverter.convert(any())).thenReturn(perUnitUsageChargeData);
		when( volumeUsageChargeConverter.convert(any())).thenReturn(volumeUsageChargeData);
		when( oneTimeChargeEntryConverter.convert(any())).thenReturn(oneTimeChargeEntryData);
		when( usageChargeConverter.convert(any())).thenReturn(usageChargeData);
		when( cpqPricingPrameterConverter.convert(any())).thenReturn(cpqPricingParameterData);
		when( sapCpqSbFetchQuoteDiscounts.getCurrentQuoteVendorDiscounts(any())).thenReturn(quoteModel);
	}




	@Test
	public void testPopulate() {

		//execute
		defaultSapCpqSubscriptionPricePlanPopulator.populate(abstractOrderEntryModel,orderEntryData);

		//Verify
		Assert.assertEquals(effectiveDate,abstractOrderEntryModel.getCpqSubscriptionDetails().get(0).getEffectiveDate());
		Assert.assertEquals(calendar.getTime(),abstractOrderEntryModel.getCpqSubscriptionDetails().get(0).getContractEndDate());
		Assert.assertEquals(CONTRACT_LEN,abstractOrderEntryModel.getCpqSubscriptionDetails().get(0).getContractLength());
		Assert.assertEquals(SUBS_PLAN_ID,abstractOrderEntryModel.getCpqSubscriptionDetails().get(0).getSubscriptionPricePlanId());
		Assert.assertEquals(PRICING_CODE,abstractOrderEntryModel.getCpqSubscriptionDetails().get(0).getPricingParameters().get(0).getCode());
	}



}
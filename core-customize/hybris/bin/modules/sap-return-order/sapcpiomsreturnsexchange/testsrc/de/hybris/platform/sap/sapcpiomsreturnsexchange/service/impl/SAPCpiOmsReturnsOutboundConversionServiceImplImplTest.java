package de.hybris.platform.sap.sapcpiomsreturnsexchange.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.returnsexchange.constants.ReturnOrderEntryCsvColumns;
import com.sap.hybris.returnsexchange.model.SapReturnOrderReasonModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.core.configuration.global.dao.SAPGlobalConfigurationDAO;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.sapcpiomsreturnsexchange.model.ExtendedSAPHTTPDestinationModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderModel;
import de.hybris.platform.sap.sapcpireturnsexchange.model.SAPCpiOutboundReturnOrderPriceComponentModel;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPPricingConditionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SAPCpiOmsReturnsOutboundConversionServiceImplImplTest {

	@Mock
	private SAPGlobalConfigurationDAO globalConfigurationDAO;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private ModelService modelService;

	@InjectMocks
	private SapCpiOmsReturnsOutboundConversionServiceImpl sapCpiOmsReturnsOutboundConversionServiceImpl;

	@Mock
	private RawItemContributor<ReturnRequestModel> returnOrderContributor;

	@Mock
	RawItemContributor<ReturnRequestModel> returnOrderSalesConditionsContributor;

	@Mock
	RawItemContributor<ReturnRequestModel> returnOrderEntryContributor;

	@Mock
	RawItemContributor<ReturnRequestModel> returnOrderPartnerContributor;

	@Mock
	private RawItemContributor<ReturnRequestModel> cancelReturnOrderEntryContributor;

	@Mock
	private ReturnRequestModel returnRequestModel;
	private Set<ConsignmentEntryModel> consignments;

	private static final String DIVISION = "00";
	private static final String DISTRIBUTION_CHANNEL = "10";
	private static final String SALESORGANIZATION = "110";
	private static final String ITM_CND_TYPE = "ITM_CND_TYPE";
	private static final String DLVR_CND_TYPE = "DLVR_CND_TYPE";
	private static final String PYMNT_CND_TYPE = "PYMNT_CND_TYPE";
	private static final String RETURN_REASON = "RETURN_REASON";

	private static final String LOGICALSYSTEMNAME = "QE6CLNT910";
	private static final String SENDER_NAME = "HBRGTSM07";
	private static final String SENDER_PORT = "HBRGTSM07";
	private static final String TARGET_URL = "http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=910";
	private static final String TARGET_RETURN_URL = "\"http://ldai1qe6.wdf.sap.corp:44300/sap/bc/srt/idoc?sap-client=910";

	private static final String RMA = "RMA01";
	private static final String RETURN_CODE = "RE01";
	private static final String CURRENCY = "USD";
	private static final String BASE_STORE = "ELECTRONICS";
	private static final String RETURN_ORDER_PROCESS_TYPE = "RETYP";
	private static final String RETURN_REQUEST_TYPE = "ReturnRequest";
	private static final String USER_ID = "HYBCOMM";
	private static final String SAP_ORDER_CODE = "SC01";
	private static final String PRODUCT_CODE = "PR01";

	private static final String CONDITION_TYPE = "TA";
	private static final String CONDITION_RATE = "RATE";
	private static final String CONDITION_UNIT = "UNIT";
	private static final String CONDITION_PRICING_UNIT = "PRICING_UNIT";
	private static final String CURRENCY_KEY = "CURRENCY_KEY";
	private static final String CONDITION_COUNTER = "COUNTER";
	private static final String SAPRETURN_REFUND = "DAMAGED";
	private static final String CANCELLATION_CODE = "CANCELLATION_CODE";

	private boolean TRUE = true;

	private int ENTRY_NUMBER = 1;
	private PK ENTRY_PK = PK.fromLong(9999l);
	private double AMOUNT = 10.10;
	private double PRICE_QUANTITY = 10.10;
	private static final String UNIT_CODE = "PCE";

	@Test
	public void convertReturnOrderToSapCpiOutboundReturnOrder() {

		SAPCpiOutboundReturnOrderModel SAPCpiOutboundReturnOrderModel = sapCpiOmsReturnsOutboundConversionServiceImpl
				.convertReturnOrderToSapCpiOutboundReturnOrder(returnRequestModel, consignments);

		commonAssertionMethod(SAPCpiOutboundReturnOrderModel);
		assertNull(SAPCpiOutboundReturnOrderModel.getCancellationCode());

	}

	@Test
	public void convertCancelReturnOrderToSapCpiOutboundReturnOrder() {

		SAPCpiOutboundReturnOrderModel SAPCpiOutboundReturnOrderModel = sapCpiOmsReturnsOutboundConversionServiceImpl
				.convertCancelReturnOrderToSapCpiOutboundReturnOrder(returnRequestModel, consignments);

		commonAssertionMethod(SAPCpiOutboundReturnOrderModel);
		assertEquals(CANCELLATION_CODE, SAPCpiOutboundReturnOrderModel.getCancellationCode());

	}

	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(this);

		Map<String, Object> row = new HashMap<>();

		// Consignement Entry Model

		final ConsignmentModel consignmentModel = mock(ConsignmentModel.class);
		final ConsignmentEntryModel consignmentEntryModel = mock(ConsignmentEntryModel.class);
		when(consignmentEntryModel.getConsignment()).thenReturn(consignmentModel);
		consignments = Collections.singleton(consignmentEntryModel);

		Set<ConsignmentModel> setConsignmentModel = new HashSet<>();
		setConsignmentModel.add(consignmentModel);

		// Return Order Extended Destination
		SAPHTTPDestinationModel sapReturnOrderHTTPDestinationModel = mock(SAPHTTPDestinationModel.class);
		when(sapReturnOrderHTTPDestinationModel.getTargetURL()).thenReturn(TARGET_RETURN_URL);
		when(sapReturnOrderHTTPDestinationModel.getUserid()).thenReturn(USER_ID);

		// ComposedTypeModel
		ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		when(composedTypeModel.getCode()).thenReturn(RETURN_REQUEST_TYPE);

		// ExtendedSAPHTTPDestinationModel
		ExtendedSAPHTTPDestinationModel extendedSAPHTTPDestinationModel = mock(ExtendedSAPHTTPDestinationModel.class);
		when(extendedSAPHTTPDestinationModel.getObjectType()).thenReturn(composedTypeModel);
		when(extendedSAPHTTPDestinationModel.getHttpDestination()).thenReturn(sapReturnOrderHTTPDestinationModel);
		when(extendedSAPHTTPDestinationModel.getTargetURL()).thenReturn(TARGET_RETURN_URL);
		when(extendedSAPHTTPDestinationModel.getUserid()).thenReturn(USER_ID);

		// Http Destination
		SAPHTTPDestinationModel sapHTTPDestinationModel = mock(SAPHTTPDestinationModel.class);
		when(sapHTTPDestinationModel.getTargetURL()).thenReturn(TARGET_URL);
		Set<ExtendedSAPHTTPDestinationModel> setExtendedSAPHTTPDestinationModel = new HashSet<>();
		setExtendedSAPHTTPDestinationModel.add(extendedSAPHTTPDestinationModel);
		when(sapHTTPDestinationModel.getObjSpecificHTTPDestination()).thenReturn(setExtendedSAPHTTPDestinationModel);

		// Logical system
		SAPLogicalSystemModel defaultLogicalSystem = mock(SAPLogicalSystemModel.class);
		when(defaultLogicalSystem.isDefaultLogicalSystem()).thenReturn(true);
		when(defaultLogicalSystem.getSapLogicalSystemName()).thenReturn(LOGICALSYSTEMNAME);
		when(defaultLogicalSystem.getSenderName()).thenReturn(SENDER_NAME);
		when(defaultLogicalSystem.getSenderPort()).thenReturn(SENDER_PORT);
		when(defaultLogicalSystem.getSapHTTPDestination()).thenReturn(sapHTTPDestinationModel);

		Set<SAPLogicalSystemModel> sapLogicalSystemModels = new HashSet<>();
		sapLogicalSystemModels.add(defaultLogicalSystem);

		// sap global configuration model
		SAPGlobalConfigurationModel sapGlobalConfiguration = mock(SAPGlobalConfigurationModel.class);
		when(sapGlobalConfiguration.getSapLogicalSystemGlobalConfig()).thenReturn(sapLogicalSystemModels);
		when(globalConfigurationDAO.getSAPGlobalConfiguration()).thenReturn(sapGlobalConfiguration);

		// SapReturnOrderReasonModel
		SapReturnOrderReasonModel sapReturnOrderReasonModel = mock(SapReturnOrderReasonModel.class);
		when(sapReturnOrderReasonModel.getRefundReason()).thenReturn(RefundReason.DAMAGEDINTRANSIT);
		when(sapReturnOrderReasonModel.getSapReturnReasonCode()).thenReturn(SAPRETURN_REFUND);
		Set<SapReturnOrderReasonModel> setSapReturnOrderReasonModel = new HashSet<SapReturnOrderReasonModel>();
		setSapReturnOrderReasonModel.add(sapReturnOrderReasonModel);

		// sapConfigurationModel
		SAPConfigurationModel sapConfigurationModel = mock(SAPConfigurationModel.class);
		when(sapConfigurationModel.getSapcommon_division()).thenReturn(DIVISION);
		when(sapConfigurationModel.getSapcommon_distributionChannel()).thenReturn(DISTRIBUTION_CHANNEL);
		when(sapConfigurationModel.getSapcommon_salesOrganization()).thenReturn(SALESORGANIZATION);
		when(sapConfigurationModel.getReturnOrderProcesstype()).thenReturn(RETURN_ORDER_PROCESS_TYPE);

		when(sapConfigurationModel.getSaporderexchange_itemPriceConditionType()).thenReturn(ITM_CND_TYPE);
		when(sapConfigurationModel.getSaporderexchange_deliveryCostConditionType()).thenReturn(DLVR_CND_TYPE);
		when(sapConfigurationModel.getSaporderexchange_paymentCostConditionType()).thenReturn(PYMNT_CND_TYPE);
		when(sapConfigurationModel.getReturnOrderReason()).thenReturn(RETURN_REASON);
		when(sapConfigurationModel.getSapReturnReasons()).thenReturn(setSapReturnOrderReasonModel);

		// Store Model
		BaseStoreModel storeModel = mock(BaseStoreModel.class);
		when(storeModel.getSAPConfiguration()).thenReturn(sapConfigurationModel);
		when(storeModel.getUid()).thenReturn(BASE_STORE);

		// Currency Model
		CurrencyModel currencyModel = mock(CurrencyModel.class);
		when(currencyModel.getIsocode()).thenReturn(CURRENCY);

		// SAP Order Model
		SAPOrderModel sapOrderModel = mock(SAPOrderModel.class);
		when(sapOrderModel.getConsignments()).thenReturn(setConsignmentModel);
		when(sapOrderModel.getCode()).thenReturn(SAP_ORDER_CODE);
		Set<SAPOrderModel> setSapOrderModel = new HashSet<>();
		setSapOrderModel.add(sapOrderModel);

		// SAPPricingConditionModel
		SAPPricingConditionModel sapPricingConditionModel = mock(SAPPricingConditionModel.class);
		when(sapPricingConditionModel.getConditionType()).thenReturn(CONDITION_TYPE);
		when(sapPricingConditionModel.getConditionRate()).thenReturn(CONDITION_RATE);
		when(sapPricingConditionModel.getConditionUnit()).thenReturn(CONDITION_UNIT);
		when(sapPricingConditionModel.getConditionPricingUnit()).thenReturn(CONDITION_PRICING_UNIT);
		when(sapPricingConditionModel.getCurrencyKey()).thenReturn(CURRENCY_KEY);
		when(sapPricingConditionModel.getConditionCounter()).thenReturn(CONDITION_COUNTER);
		Set<SAPPricingConditionModel> setSapPricingConditionModel = new HashSet<>();
		setSapPricingConditionModel.add(sapPricingConditionModel);

		// Product Model
		ProductModel productModel = mock(ProductModel.class);
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(productModel.getPriceQuantity()).thenReturn(PRICE_QUANTITY);

		// UnitModel
		UnitModel unitModel = mock(UnitModel.class);
		when(unitModel.getCode()).thenReturn(UNIT_CODE);

		// AbstractOrderEntryModel
		AbstractOrderEntryModel abstractOrderEntryModel = mock(AbstractOrderEntryModel.class);
		when(abstractOrderEntryModel.getSapPricingConditions()).thenReturn(setSapPricingConditionModel);
		when(abstractOrderEntryModel.getProduct()).thenReturn(productModel);
		when(abstractOrderEntryModel.getEntryNumber()).thenReturn(ENTRY_NUMBER);
		when(abstractOrderEntryModel.getPk()).thenReturn(ENTRY_PK);
		when(abstractOrderEntryModel.getUnit()).thenReturn(unitModel);
		// when(abstractOrderEntryModel.getAmount()).thenReturn(ENTRY_PK);

		List<AbstractOrderEntryModel> listAbstractOrderEntryModel = new ArrayList<>();
		listAbstractOrderEntryModel.add(abstractOrderEntryModel);

		// Order Model
		OrderModel orderModel = mock(OrderModel.class);
		when(orderModel.getCurrency()).thenReturn(currencyModel);
		when(orderModel.getDeliveryMode()).thenReturn(null);
		when(orderModel.getStore()).thenReturn(storeModel);
		when(orderModel.getSapOrders()).thenReturn(setSapOrderModel);
		when(orderModel.getEntries()).thenReturn(listAbstractOrderEntryModel);

		// Price Model
		SAPCpiOutboundReturnOrderPriceComponentModel sapCpiOrderPriceComponent = mock(
				SAPCpiOutboundReturnOrderPriceComponentModel.class);
		List<Map<String, Object>> sapCpiReturnOrderPriceComponents = new ArrayList<>();
		// sapCpiReturnOrderPriceComponents.add(sapCpiOrderPriceComponent);
		// Set<SAPCpiOutboundPriceComponentModel> setSapCpiOutboundPriceComponentModel =
		// new HashSet<>(sapCpiReturnOrderPriceComponents);

		// Return Entry Model
		// ReturnEntryModel returnEntryModel = mock(ReturnEntryModel.class);
		RefundEntryModel returnEntryModel = mock(RefundEntryModel.class);

		when(returnEntryModel.getOrderEntry()).thenReturn(abstractOrderEntryModel);
		when(returnEntryModel.getAmount()).thenReturn(BigDecimal.valueOf(AMOUNT));
		when(returnEntryModel.getReason()).thenReturn(RefundReason.DAMAGEDINTRANSIT);

		List<ReturnEntryModel> listReturnEntryModel = new ArrayList<ReturnEntryModel>();
		listReturnEntryModel.add(returnEntryModel);

		// Return Request
		when(returnRequestModel.getRMA()).thenReturn(RMA);
		when(returnRequestModel.getCode()).thenReturn(RETURN_CODE);
		when(returnRequestModel.getCreationtime()).thenReturn(new Date());
		when(returnRequestModel.getOrder()).thenReturn(orderModel);
		when(returnRequestModel.getSapLogicalSystem()).thenReturn(LOGICALSYSTEMNAME);
		when(returnRequestModel.getRefundDeliveryCost()).thenReturn(TRUE);
		when(returnRequestModel.getReturnEntries()).thenReturn(listReturnEntryModel);
		when(returnRequestModel.getReasonCodeCancellation()).thenReturn(CANCELLATION_CODE);

		returnOrderToRow(returnRequestModel, row);

		when(returnOrderContributor.createRows(returnRequestModel)).thenReturn(Arrays.asList(row));
		when(returnOrderSalesConditionsContributor.createRows(returnRequestModel))
				.thenReturn(sapCpiReturnOrderPriceComponents);
		doNothing().when(modelService).save(any());

		// Consignment Model
		when(consignmentModel.getOrder()).thenReturn(orderModel);

	}

	private void returnOrderToRow(ReturnRequestModel returnRequest, Map<String, Object> row) {
		row.put(OrderCsvColumns.ORDER_ID, returnRequest.getCode());
		row.put(OrderCsvColumns.DATE, returnRequest.getCreationtime());
		row.put(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, returnRequest.getOrder().getCurrency().getIsocode());
		final DeliveryModeModel deliveryMode = returnRequest.getOrder().getDeliveryMode();
		row.put(OrderCsvColumns.DELIVERY_MODE, deliveryMode != null ? deliveryMode.getCode() : "");
		row.put(OrderCsvColumns.BASE_STORE, returnRequest.getOrder().getStore().getUid());
		row.put(ReturnOrderEntryCsvColumns.RMA, returnRequest.getRMA());

	}

	private void commonAssertionMethod(SAPCpiOutboundReturnOrderModel SAPCpiOutboundReturnOrderModel) {
		assertEquals(LOGICALSYSTEMNAME, SAPCpiOutboundReturnOrderModel.getSapCpiConfig().getReceiverName());
		assertEquals(LOGICALSYSTEMNAME, SAPCpiOutboundReturnOrderModel.getSapCpiConfig().getReceiverPort());
		assertEquals(SENDER_NAME, SAPCpiOutboundReturnOrderModel.getSapCpiConfig().getSenderName());
		assertEquals(SENDER_PORT, SAPCpiOutboundReturnOrderModel.getSapCpiConfig().getSenderPort());
		assertEquals(DIVISION, SAPCpiOutboundReturnOrderModel.getDivision());
		assertEquals(DISTRIBUTION_CHANNEL, SAPCpiOutboundReturnOrderModel.getDistributionChannel());
		assertEquals(SALESORGANIZATION, SAPCpiOutboundReturnOrderModel.getSalesOrganization());
		assertEquals(RETURN_CODE, SAPCpiOutboundReturnOrderModel.getOrderId());
		assertEquals(CURRENCY, SAPCpiOutboundReturnOrderModel.getCurrencyIsoCode());
		assertEquals(BASE_STORE, SAPCpiOutboundReturnOrderModel.getBaseStoreUid());

	}

}

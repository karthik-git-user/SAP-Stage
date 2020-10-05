package com.sap.hybris.sapcpqsbintegration.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqSbOrderConversionServiceTest {

	@Mock
    private CommonI18NService commonI18NService;

	@Mock
	private CMSSiteService cmsSiteService;
	
	@Mock
	private SubscriptionCommercePriceService commercePriceService;
	
	@InjectMocks
	DefaultSapCpqSbOrderConversionService defaultSapCpqSbOrderConversionService;
	
	private final OrderModel orderModel = new OrderModel();
	private final SAPCpiOutboundOrderModel sapCpiOutboundOrder = new SAPCpiOutboundOrderModel();
	private final Set<SAPCpiOutboundOrderItemModel> sapCpiOutboundOrderItemModelSet = new HashSet<>();
	private final SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItemModel = new SAPCpiOutboundOrderItemModel();
	private final List<AbstractOrderEntryModel> abstractOrderEntryModelList = new ArrayList<>();
	private final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
	private final List<CpqSubscriptionDetailModel> cpqSubscriptionDetailModelList = new ArrayList<>();
	private final CpqSubscriptionDetailModel cpqSubscriptionDetailModel = new CpqSubscriptionDetailModel();
	private final ProductModel productModel = new ProductModel();
	private final BaseStoreModel baseStoreModel = new BaseStoreModel();
	private final CustomerModel customerModel = new CustomerModel();
	private final CreditCardPaymentInfoModel paymentInfoModel = new CreditCardPaymentInfoModel();
	private final UnitModel unitModel = new UnitModel();


	
	@Before
	public void setUp() {
		//UnitModel
		unitModel.setCode("CODE");
		
		//PaymentInfoModel
		paymentInfoModel.setSubscriptionId("SUBSCRIPTION_ID");
		
		//ProductModel
		productModel.setSubscriptionCode("CODE");
		productModel.setCode("CODE");
		
		//CpqSubscriptionDetailModel
		cpqSubscriptionDetailModel.setContractLength("2");
		cpqSubscriptionDetailModel.setMinimumContractLength("4");
		cpqSubscriptionDetailModel.setContractStartDate(new Date());
		cpqSubscriptionDetailModelList.add(cpqSubscriptionDetailModel);
		
		//AbstractOrderEntryModel
		abstractOrderEntryModel.setQuantity(10l);
		abstractOrderEntryModel.setEntryNumber(10);
		abstractOrderEntryModel.setUnit(unitModel);
		abstractOrderEntryModel.setCpqSubscriptionDetails(cpqSubscriptionDetailModelList);
		abstractOrderEntryModel.setProduct(productModel);
		abstractOrderEntryModelList.add(abstractOrderEntryModel);
		
		//BaseStoreModel
		baseStoreModel.setUid("UID");
		
		//CustomerModel
		customerModel.setCustomerID("CUSTOMER_ID");
		customerModel.setRevenueCloudCustomerId("REVENUE_CLOUD_ID");
		customerModel.setUid("UID");
		
		//OrderModel
		orderModel.setEntries(abstractOrderEntryModelList);
		orderModel.setStore(baseStoreModel);
		orderModel.setCode("CODE");
		orderModel.setUser(customerModel);
		orderModel.setPaymentInfo(paymentInfoModel);
		
		//SapCpiOutboundOrderItemModel
		sapCpiOutboundOrderItemModel.setProductCode("CODE");
		sapCpiOutboundOrderItemModelSet.add(sapCpiOutboundOrderItemModel);
		
		//SapCpiOutboundOrder
		sapCpiOutboundOrder.setSapCpiOutboundOrderItems(sapCpiOutboundOrderItemModelSet);
	}
	
	@Test
	public void convertOrderToSapCpiOrderTest() {
		SAPCpiOutboundOrderModel result;
		result = defaultSapCpqSbOrderConversionService.convertOrderToSapCpiOrder(orderModel);
		
		//Verify
		result.getSapCpiOutboundOrderItems().forEach(item -> {
			Assert.assertNotNull(item.getSubscriptionValidFrom());
			Assert.assertNull(item.getPricePlanId());
			Assert.assertNotNull(item.getCpqSubscriptionDetails());
			Assert.assertEquals("CODE", item.getProductName());
			Assert.assertNull(item.getProductCode());

		});
	}
}

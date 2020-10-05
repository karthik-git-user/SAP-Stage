package com.sap.hybris.sapcpqsbintegration.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqSbCreateCartFromQuoteStrategyTest {

	@InjectMocks
	DefaultSapCpqSbCreateCartFromQuoteStrategy defaultSapCpqSbCreateCartFromQuoteStrategy;
	
	private final QuoteModel quoteModel = new QuoteModel();
	private final CartModel cartModel = new CartModel();
	private final List<AbstractOrderEntryModel> abstractOrderEntryModelList = new ArrayList<>();
	private final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
	private final List<CpqSubscriptionDetailModel> cpqSubscriptionDetailModelList = new ArrayList<>();
	private final CpqSubscriptionDetailModel cpqSubscriptionDetailModel = new CpqSubscriptionDetailModel();
	private final ProductModel productModel = new ProductModel();
	
	@Before
	public void setUp() {
 		cpqSubscriptionDetailModelList.add(cpqSubscriptionDetailModel);
		abstractOrderEntryModel.setCpqSubscriptionDetails(cpqSubscriptionDetailModelList);
		productModel.setSubscriptionCode("SUBSCRIPTION_CODE");
		abstractOrderEntryModel.setProduct(productModel);
		abstractOrderEntryModelList.add(abstractOrderEntryModel);
		cartModel.setEntries(abstractOrderEntryModelList);
	}
	
	@Test
	public void postProcessTest() {
		defaultSapCpqSbCreateCartFromQuoteStrategy.postProcess(quoteModel, cartModel);
		
		Assert.assertNull(cartModel.getEntries().get(0).getCpqSubscriptionDetails().get(0).getOneTimeChargeEntries());
		Assert.assertNull(cartModel.getEntries().get(0).getCpqSubscriptionDetails().get(0).getRecurringChargeEntries());
		Assert.assertNull(cartModel.getEntries().get(0).getCpqSubscriptionDetails().get(0).getUsageCharges());
	}
}

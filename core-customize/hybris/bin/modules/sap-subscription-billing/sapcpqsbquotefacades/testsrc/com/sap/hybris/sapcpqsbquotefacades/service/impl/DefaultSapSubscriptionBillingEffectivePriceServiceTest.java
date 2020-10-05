package com.sap.hybris.sapcpqsbquotefacades.service.impl;

import org.junit.Assert;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpqsbquotefacades.factory.SapSubscriptionBillingExtendedPriceFactory;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.model.ItemModelInternalContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

@RunWith(MockitoJUnitRunner.class)
public class DefaultSapSubscriptionBillingEffectivePriceServiceTest  {

	@Mock
	ItemModelInternalContext itemModelInternalContext;
	@Mock
	SubscriptionPricePlanModel priceRowModel;
	@Mock
	ModelService modelService;
	@Mock
	SapSubscriptionBillingExtendedPriceFactory sapSubscriptionBillingExtendedPriceFactory;

	@InjectMocks
	DefaultSapSubscriptionBillingEffectivePriceService defaultSapSubscriptionBillingEffectivePriceService;


	private final Product productItem = new Product();
	private final ProductModel productModel = new ProductModel();
	private final Date effectiveDate = new Date();
	private final static String Product_Model_Name = "mocked_product_model";

	@Before
	public void setUp() throws Exception
	{
		//Mock
		when(sapSubscriptionBillingExtendedPriceFactory.getPriceRow(any(Product.class),any(Date.class))).thenReturn(new PriceRow());
		when(modelService.getSource(any())).thenReturn(productItem);
		when(modelService.get(any(PriceRow.class))).thenReturn(priceRowModel);
		when(priceRowModel.getName()).thenReturn(Product_Model_Name);
	}

	@Test
	public void testGetSubscriptionEffectivePricePlan() {

		//Execute
		SubscriptionPricePlanModel subscriptionPricePlanModel = defaultSapSubscriptionBillingEffectivePriceService.getSubscriptionEffectivePricePlan(productModel,effectiveDate);
		//Verify
		Assert.assertNotNull(subscriptionPricePlanModel);
		Assert.assertEquals(Product_Model_Name,subscriptionPricePlanModel.getName());
	}

}

package com.sap.hybris.sapcpqsbintegration.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpqsbintegration.model.CpqSubscriptionDetailModel;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.RequoteStrategy;
import de.hybris.platform.commerceservices.order.strategies.QuoteActionValidationStrategy;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.model.ModelService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapCpqSbCpiQuoteServiceTest {
	 
	@Mock
	ModelService mockModelService;
	
	@Mock
	QuoteService mockQuoteService;
	
	@Mock
	QuoteActionValidationStrategy mockQuoteActionValidationStrategy;
	
	@Mock
	RequoteStrategy mockRequoteStrategy;
	
	@InjectMocks
	DefaultSapCpqSbCpiQuoteService defaultSapCpqSbCpiQuoteService;
	
	private final QuoteModel quoteModel = new QuoteModel();
	private final QuoteModel updatedQuoteModel = new QuoteModel();
	private final UserModel userModel = new UserModel();
	private final QuoteState quoteState = QuoteState.BUYER_ACCEPTED;
	private final List<AbstractOrderEntryModel> abstractOrderEntryModelList = new ArrayList<>();
	private final AbstractOrderEntryModel abstractOrderEntryModel = new AbstractOrderEntryModel();
	private final List<CpqSubscriptionDetailModel> cpqSubscriptionDetailModelList = new ArrayList<>();
	private final CpqSubscriptionDetailModel cpqSubscriptionDetailModel = new CpqSubscriptionDetailModel();
	private final ProductModel productModel = new ProductModel();
	
	@Before
	public void setUp() {
		productModel.setSubscriptionCode("SUBSCRIPTION_CODE");
		
		//CpqSubscriptionDetailModel
		cpqSubscriptionDetailModelList.add(cpqSubscriptionDetailModel);

		//AbstractOrderEntryModel
		abstractOrderEntryModel.setProduct(productModel);
		abstractOrderEntryModel.setCpqSubscriptionDetails(cpqSubscriptionDetailModelList);
		
		abstractOrderEntryModelList.add(abstractOrderEntryModel);
		
		//QuoteModel
		updatedQuoteModel.setEntries(abstractOrderEntryModelList);
		quoteModel.setEntries(abstractOrderEntryModelList);
		
		//setUp
		when(mockQuoteService.createQuoteSnapshot(any(), any())).thenReturn(updatedQuoteModel);
		when(mockRequoteStrategy.requote(any())).thenReturn(quoteModel);
		doNothing().when(mockModelService).save(any());
		doNothing().when(mockModelService).refresh(any());
		doNothing().when(mockQuoteActionValidationStrategy).validate(any(), any(), any());
		

	}
	
	@Test
	public void createQuoteSnapshotWithStateTest() {
		QuoteModel result;
		
		//Execute
		result = defaultSapCpqSbCpiQuoteService.createQuoteSnapshotWithState(quoteModel, quoteState);
		
		//Verify
		Assert.assertNotNull(result);
		Assert.assertNull(result.getEntries().get(0).getCpqSubscriptionDetails().get(0).getOneTimeChargeEntries());
		Assert.assertNull(result.getEntries().get(0).getCpqSubscriptionDetails().get(0).getRecurringChargeEntries());
		Assert.assertNull(result.getEntries().get(0).getCpqSubscriptionDetails().get(0).getUsageCharges());
	}
	
	@Test
	public void requoteTest() {
		QuoteModel result;
		
		//Execute
		result = defaultSapCpqSbCpiQuoteService.requote(quoteModel, userModel);
		
		//Verify
		Assert.assertNotNull(result);
		Assert.assertNull(result.getTotalDiscounts());
		Assert.assertNull(result.getEntries().get(0).getCpqSubscriptionDetails().get(0).getOneTimeChargeEntries());
		Assert.assertNull(result.getEntries().get(0).getCpqSubscriptionDetails().get(0).getRecurringChargeEntries());
		Assert.assertNull(result.getEntries().get(0).getCpqSubscriptionDetails().get(0).getUsageCharges());
	}
}

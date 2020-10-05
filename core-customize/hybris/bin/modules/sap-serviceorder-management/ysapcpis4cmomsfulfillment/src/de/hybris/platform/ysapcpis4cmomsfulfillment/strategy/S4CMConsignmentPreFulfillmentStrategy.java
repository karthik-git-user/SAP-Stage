/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ysapcpis4cmomsfulfillment.strategy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.sap.core.configuration.global.SAPGlobalConfigurationService;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapserviceorder.constants.SapserviceorderConstants;
import de.hybris.platform.sap.sapserviceorder.service.SapProductReferenceService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;
import de.hybris.platform.warehousing.externalfulfillment.strategy.ConsignmentPreFulfillmentStrategy;


/**
 * Implementation for Service Order Pre-fulfillment 
 */
public class S4CMConsignmentPreFulfillmentStrategy implements ConsignmentPreFulfillmentStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(S4CMConsignmentPreFulfillmentStrategy.class);
	private SAPGlobalConfigurationService sapGlobalConfigurationService;
	private ModelService modelService;
	private SapProductReferenceService sapProductReferenceService;

	@Override
	public void perform(final ConsignmentModel consignment)
	{
		LOG.info("S4CM consignment pre-fulfillment strategy has been called!");
	}

	@Override
	public boolean canProceedAfterPerform(final ConsignmentModel consignment)
	{
		LOG.info("Check if the customer has been replicated to the SAP S4CM backend to proceed!");
		final AbstractOrderModel order = consignment.getOrder();
		boolean canProceed = true;
		
		canProceed = checkCustomer(order);
		
		canProceed = canProceed && checkOrderForRelatedSalesOrderConsignments(consignment);
		
		return canProceed;
	}

	protected boolean checkOrderForRelatedSalesOrderConsignments(final ConsignmentModel consignment) {
		boolean canProceed;
		if(consignment.isIsRelatedConsignmentProcessed()) {
			LOG.info("Checking if all related consignments are shipped");
			canProceed = checkIfAllRelatedConsignmentsAreShipped(consignment);
			
		} else {
			
			List<ProductModel> relatedProducts = getRelatedProducts(consignment);
			
			if(relatedProducts.isEmpty()) {
				LOG.info("No Product References exists. Proceeding with Service Order Replication external system");
				canProceed = true;
			} else {
				LOG.info("Getting related consignments for the service consignment [{}]", consignment.getCode());
				List<ConsignmentModel> relatedConsignments = getRelatedConsignments(consignment, relatedProducts);
				
				consignment.setRelatedConsignments(relatedConsignments);
				consignment.setIsRelatedConsignmentProcessed(true);
				modelService.save(consignment);
				canProceed = relatedConsignments.isEmpty();
			}
			
			if(canProceed) {
				setRequestedServiceStartDateInOrder(consignment, consignment.getOrder().getCreationtime());
			}
			
		}
		if (!canProceed) {
			LOG.info("Consignment [{}] is waiting for related sales order to ship", consignment.getCode());
		}
		return canProceed;
	}

	protected List<ConsignmentModel> getRelatedConsignments(final ConsignmentModel consignment, List<ProductModel> relatedProducts) {
		
		List<ConsignmentModel> relatedConsignments = new ArrayList<>();
		OrderProcessModel orderProcess = consignment.getConsignmentProcesses().iterator().next().getParentProcess();
		
		OrderModel parentOrder = orderProcess.getOrder();
		for(ConsignmentModel parentConsignment : parentOrder.getConsignments()) {
			for(ConsignmentEntryModel pce :parentConsignment.getConsignmentEntries()) {
				ProductModel product = pce.getOrderEntry().getProduct();
				if(relatedProducts.contains(product)) {
					LOG.info("Order contains related sales consignment [{}] for service consignment [{}]",parentConsignment.getCode(),consignment.getCode());
					relatedConsignments.add(parentConsignment);
					List<ConsignmentModel> parentRelatedConsignments = new ArrayList<>();
					addRelatedConsignments(parentConsignment, parentRelatedConsignments);
					parentRelatedConsignments.add(consignment);
					parentConsignment.setRelatedConsignments(parentRelatedConsignments);
					modelService.save(parentConsignment);
				}
			}
		}
		return relatedConsignments;
	}

	protected void addRelatedConsignments(ConsignmentModel parentConsignment, List<ConsignmentModel> parentRelatedConsignments) {
		if(parentConsignment.getRelatedConsignments() != null) {
			parentRelatedConsignments.addAll(parentConsignment.getRelatedConsignments());
		}
	}

	protected List<ProductModel> getRelatedProducts(final ConsignmentModel consignment) {
		List<ProductModel> relatedProducts = new ArrayList<>();
		
		for(ConsignmentEntryModel ce :consignment.getConsignmentEntries()) {
			ProductModel product = ce.getOrderEntry().getProduct();
			LOG.info("Getting related product references for [{}]",product.getCode());
			List<ProductReferenceModel> productReferences = getSapProductReferenceService().getProductReferences(product);
			productReferences.stream().forEach(prodRef -> relatedProducts.add(prodRef.getSource()));

		}
		return relatedProducts;
	}

	protected boolean checkIfAllRelatedConsignmentsAreShipped(final ConsignmentModel consignment) {
		boolean isRelatedConsignmentsCompleted = true;
		for(ConsignmentModel con : consignment.getRelatedConsignments()) {
			if(!con.getStatus().equals(ConsignmentStatus.SHIPPED)) {
				isRelatedConsignmentsCompleted = false;
				break;
			} 
		}
		
		return isRelatedConsignmentsCompleted;
	}

	protected void setRequestedServiceStartDateInOrder(ConsignmentModel consignment, Date requestServiceStartDate) {
		OrderModel order = (OrderModel) consignment.getOrder();
		if (order.getRequestedServiceStartDate() == null && order.getStore() != null && order.getStore().getSAPConfiguration() != null)
		{
			SAPConfigurationModel sapConfig = order.getStore().getSAPConfiguration();
			Calendar c = Calendar.getInstance();
			c.setTime(requestServiceStartDate);
			int leadDays = sapConfig.getLeadDays() != null ? sapConfig.getLeadDays() : Integer.parseInt(Config.getParameter(SapserviceorderConstants.DEFAULT_LEAD_DAYS));
			c.add(Calendar.DATE, leadDays);
			order.setRequestedServiceStartDate(c.getTime());
			modelService.save(order);
		}
	}

	protected boolean checkCustomer(final AbstractOrderModel order) {
		boolean canProceed = true;
		if (sapGlobalConfigurationService.getProperty("replicateregistereduser").equals(Boolean.TRUE))
		{
			final CustomerModel customerModel = ((CustomerModel) order.getUser());
			if (customerModel != null)
			{
				final boolean isCustomerExported = customerModel.getSapIsReplicated().booleanValue();
				final boolean isGuestUser = isGuestUser(customerModel);
				final boolean isB2B = isB2BCase(order);

				canProceed = (isCustomerExported || isGuestUser || isB2B);
			}

		}
		return canProceed;
	}

	protected boolean isB2BCase(final AbstractOrderModel orderModel)
	{

		return (orderModel.getSite() != null) && SiteChannel.B2B.equals(orderModel.getSite().getChannel());

	}

	protected boolean isGuestUser(final CustomerModel customerModel)
	{

		return CustomerType.GUEST.equals(customerModel.getType());

	}


	public SAPGlobalConfigurationService getSapGlobalConfigurationService()
	{
		return sapGlobalConfigurationService;
	}

	public void setSapGlobalConfigurationService(final SAPGlobalConfigurationService sapGlobalConfigurationService)
	{
		this.sapGlobalConfigurationService = sapGlobalConfigurationService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public SapProductReferenceService getSapProductReferenceService() {
		return sapProductReferenceService;
	}

	public void setSapProductReferenceService(SapProductReferenceService sapProductReferenceService) {
		this.sapProductReferenceService = sapProductReferenceService;
	}


}

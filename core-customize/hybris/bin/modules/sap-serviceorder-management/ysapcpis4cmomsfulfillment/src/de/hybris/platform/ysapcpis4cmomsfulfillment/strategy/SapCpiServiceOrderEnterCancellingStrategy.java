/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ysapcpis4cmomsfulfillment.strategy;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.RESPONSE_MESSAGE;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.getPropertyValue;
import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.isSentSuccessfully;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.sapcpiorderexchangeoms.enums.SAPOrderType;
import de.hybris.platform.sap.sapserviceorder.exceptions.SapServiceOrderCancelOutboundException;
import de.hybris.platform.sap.sapserviceorder.model.SAPCpiOutboundServiceOrderModel;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundBuilderService;
import de.hybris.platform.sap.sapserviceorder.service.SapCpiServiceOrderOutboundService;
import de.hybris.platform.sap.sapserviceorder.util.SapServiceOrderUtil;
import de.hybris.platform.sap.ysapcpiomsfulfillment.cancellation.SapCpiOmsEnterCancellingStrategy;
import rx.SingleSubscriber;

public class SapCpiServiceOrderEnterCancellingStrategy extends SapCpiOmsEnterCancellingStrategy{
	
	 private static final Logger LOG = LoggerFactory.getLogger(SapCpiServiceOrderEnterCancellingStrategy.class);

	  private SapCpiServiceOrderOutboundService sapCpiServiceOrderOutboundService;
	  private SapCpiServiceOrderOutboundBuilderService<OrderCancelRecordEntryModel,List<SAPCpiOutboundServiceOrderModel>> serviceOrderCancelBuilderService;
	  
	  private Throwable cancelReplicateError;
	  
	  /**
	   * Change OrderStatus of an order after cancel operation
	   *
	   * @param orderCancelRecordEntry
	   * @param saveOrderModel
	   */
	  @Override
	  public void changeOrderStatusAfterCancelOperation(OrderCancelRecordEntryModel orderCancelRecordEntry, boolean saveOrderModel) {

		  boolean serviceCancel = isService(orderCancelRecordEntry);
		  if(!serviceCancel) {
			  super.changeOrderStatusAfterCancelOperation(orderCancelRecordEntry, saveOrderModel);
		  }else {
			  sendServiceCancellationToScpi(serviceOrderCancelBuilderService.build(orderCancelRecordEntry));
			  changeServiceOrderStatusAfterCancelOperation(orderCancelRecordEntry, saveOrderModel);
			  
		  }

	  }
	  
	  /**
	   * Check if all the entries are service items
	   * @param orderCancelRecordEntry
	   * @return true if all the entries are service items false otherwise
	   */
	  protected boolean isService(OrderCancelRecordEntryModel orderCancelRecordEntry) {
		  return orderCancelRecordEntry.getOrderEntriesModificationEntries().stream().allMatch(
				  oe -> SapServiceOrderUtil.isServiceEntry(oe.getOrderEntry()));
	  }
	  
	  /**
	   * Changes order status to cancelling
	   * @param orderCancelRecordEntry
	   * @param saveOrderModel
	   */
	  protected void changeServiceOrderStatusAfterCancelOperation(OrderCancelRecordEntryModel orderCancelRecordEntry, boolean saveOrderModel){
		  OrderModel order = orderCancelRecordEntry.getModificationRecord().getOrder();
		  order.setStatus(OrderStatus.CANCELLING);
		  order.getConsignments().stream()
		  .filter(consignment -> consignment.getSapOrder()!= null && SAPOrderType.SERVICE.equals(consignment.getSapOrder().getSapOrderType()))
		  .forEach(consignment -> {
			  consignment.setStatus(ConsignmentStatus.CANCELLING);
			  if(consignment.getSapOrder()!=null && orderCancelRecordEntry.getOrderEntriesModificationEntries().iterator().hasNext()) {
				  consignment.getSapOrder().setCancelReason(orderCancelRecordEntry.getOrderEntriesModificationEntries().iterator().next().getNotes());
				  getModelService().save(consignment.getSapOrder());
			  }
			  getModelService().save(consignment);
		  });
			if (saveOrderModel) {
				getModelService().save(orderCancelRecordEntry.getModificationRecord().getOrder());
			}
	  }


	  /**
	   * Send consignment cancellation to the SAP backend through SCPI
	   *
	   * @param sapCpiOutboundOrderCancellation
	   * @param orderCancelRecordEntry
	   */
	  protected void sendServiceCancellationToScpi(List<SAPCpiOutboundServiceOrderModel> outboundCancelServiceOrders) {

		  setCancelReplicateError(null);
		  outboundCancelServiceOrders.forEach(cancelServiceOrder -> {
			  sapCpiServiceOrderOutboundService.sendServiceOrderCancellation(cancelServiceOrder).toSingle().subscribe(new SingleSubscriber<ResponseEntity<Map>>() {
		
		      @Override
		      public void onSuccess(ResponseEntity<Map> mapResponseEntity) {
		
		        if (isSentSuccessfully(mapResponseEntity)) {
		
		          LOG.info("The OMS service order {} cancellation request has been successfully sent to the SAP backend through SCPI!{}",
		        		  cancelServiceOrder.getCommerceOrderId() + ':' + cancelServiceOrder.getOrderId(), getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));
		
		        } else {
		
		          LOG.error("The OMS service order {} cancellation request has not been sent to the SAP backend! {}",
		        		  cancelServiceOrder.getCommerceOrderId() + ':' + cancelServiceOrder.getOrderId(), getPropertyValue(mapResponseEntity, RESPONSE_MESSAGE));
		        }
		
		      }
		
		      @Override
		      public void onError(Throwable throwable) {
		    	setCancelReplicateError(throwable);
		        LOG.error("The OMS service order {} cancellation request has not been sent to the SAP backend through SCPI!",
		        		cancelServiceOrder.getCommerceOrderId() + ':' + cancelServiceOrder.getOrderId(), throwable);
		
		      }
		
		    });
		  
		 });
		 if(getCancelReplicateError() != null)
		 {
			 throw new SapServiceOrderCancelOutboundException("Failed to replicate the service cancel request.", getCancelReplicateError());
		 }
	  }

	public SapCpiServiceOrderOutboundService getSapCpiServiceOrderOutboundService() {
		return sapCpiServiceOrderOutboundService;
	}

	public void setSapCpiServiceOrderOutboundService(SapCpiServiceOrderOutboundService sapCpiServiceOrderOutboundService) {
		this.sapCpiServiceOrderOutboundService = sapCpiServiceOrderOutboundService;
	}

	public SapCpiServiceOrderOutboundBuilderService<OrderCancelRecordEntryModel,List<SAPCpiOutboundServiceOrderModel>> getServiceOrderCancelBuilderService() {
		return serviceOrderCancelBuilderService;
	}


	public void setServiceOrderCancelBuilderService(SapCpiServiceOrderOutboundBuilderService<OrderCancelRecordEntryModel,List<SAPCpiOutboundServiceOrderModel>> serviceOrderCancelBuilderService) {
		this.serviceOrderCancelBuilderService = serviceOrderCancelBuilderService;
	}

	protected Throwable getCancelReplicateError() {
		return cancelReplicateError;
	}

	protected void setCancelReplicateError(Throwable cancelReplicateError) {
		this.cancelReplicateError = cancelReplicateError;
	}

}

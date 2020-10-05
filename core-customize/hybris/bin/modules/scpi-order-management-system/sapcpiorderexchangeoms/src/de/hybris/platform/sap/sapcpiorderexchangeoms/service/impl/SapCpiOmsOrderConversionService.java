/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpiorderexchangeoms.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.sap.orderexchange.constants.OrderCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.OrderEntryCsvColumns;
import de.hybris.platform.sap.orderexchange.constants.PartnerCsvColumns;
import de.hybris.platform.sap.sapcpiadapter.data.*;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderConversionService;
import de.hybris.platform.sap.sapcpiorderexchangeoms.exceptions.SapCpiOmsOrderConversionServiceException;
import de.hybris.platform.sap.sapmodel.model.SAPLogicalSystemModel;
import de.hybris.platform.sap.sapmodel.services.SapPlantLogSysOrgService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SAP CPI OMS Order conversion service converts OrderModel to SapCpiOrder and OrderCancelRecordEntryModel to List<SapCpiOrderCancellation>
 */
public class SapCpiOmsOrderConversionService extends SapCpiOmmOrderConversionService {

  private static final Logger LOG = LoggerFactory.getLogger(SapCpiOmsOrderConversionService.class);

  @Override
  public SapCpiOrder convertOrderToSapCpiOrder(OrderModel orderModel) {

    SapCpiOrder sapCpiOmsOrder = new SapCpiOrder();

    getSapOrderContributor().createRows(orderModel).stream().findFirst().ifPresent(row -> {

      sapCpiOmsOrder.setSapCpiConfig(mapOrderConfigInfo(orderModel));

      sapCpiOmsOrder.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
      sapCpiOmsOrder.setBaseStoreUid(mapAttribute(OrderCsvColumns.BASE_STORE, row));
      sapCpiOmsOrder.setCreationDate(mapDateAttribute(OrderCsvColumns.DATE, row));
      sapCpiOmsOrder.setCurrencyIsoCode(mapAttribute(OrderCsvColumns.ORDER_CURRENCY_ISO_CODE, row));
      sapCpiOmsOrder.setPaymentMode(mapAttribute(OrderCsvColumns.PAYMENT_MODE, row));
      sapCpiOmsOrder.setDeliveryMode(mapAttribute(OrderCsvColumns.DELIVERY_MODE, row));

      sapCpiOmsOrder.setSalesOrganization(mapAttribute(OrderCsvColumns.SALES_ORGANIZATION, row));
      sapCpiOmsOrder.setDistributionChannel(mapAttribute(OrderCsvColumns.DISTRIBUTION_CHANNEL, row));
      sapCpiOmsOrder.setChannel(mapAttribute(OrderCsvColumns.CHANNEL, row));
      sapCpiOmsOrder.setDivision(mapAttribute(OrderCsvColumns.DIVISION, row));

      sapCpiOmsOrder.setPurchaseOrderNumber(mapAttribute(OrderCsvColumns.PURCHASE_ORDER_NUMBER, row));
      sapCpiOmsOrder.setTransactionType(orderModel.getStore().getSAPConfiguration().getSapcommon_transactionType());

      orderModel.getStore()
              .getSAPConfiguration()
              .getSapDeliveryModes()
              .stream()
              .filter(entry -> entry.getDeliveryMode().getCode().contentEquals(orderModel.getDeliveryMode().getCode()))
              .findFirst()
              .ifPresent(entry -> sapCpiOmsOrder.setShippingCondition(entry.getDeliveryValue()));

      sapCpiOmsOrder.setSapCpiOrderItems(mapOrderItems(orderModel));
      sapCpiOmsOrder.setSapCpiPartnerRoles(mapOrderPartners(orderModel));
      sapCpiOmsOrder.setSapCpiOrderAddresses(mapOrderAddresses(orderModel));
      sapCpiOmsOrder.setSapCpiOrderPriceComponents(mapOrderPrices(orderModel));
      sapCpiOmsOrder.setSapCpiCreditCardPayments(mapCreditCards(orderModel));

    });

    if (LOG.isDebugEnabled()) {
      LOG.debug(String.format("SCPI OMS order object: %n %s", ReflectionToStringBuilder.toString(sapCpiOmsOrder, ToStringStyle.MULTI_LINE_STYLE)));
    }

    return sapCpiOmsOrder;
  }

  @Override
  protected SapCpiConfig mapOrderConfigInfo(OrderModel orderModel) {

    String plantCode = orderModel.getConsignments().stream().findFirst().get().getWarehouse().getCode();
    SAPLogicalSystemModel sapLogicalSystem = getSapCpiOrderDestinationService().readSapLogicalSystem(orderModel.getStore(), plantCode);

    SapCpiConfig sapCpiConfig = new SapCpiConfig();
    sapCpiConfig.setSapCpiTargetSystem(mapTargetSystem(sapLogicalSystem));

    return sapCpiConfig;

  }

  @Override
  protected List<SapCpiOrderItem> mapOrderItems(OrderModel orderModel) {


    final List<SapCpiOrderItem> sapCpiOrderItems = new ArrayList<>();

    getSapOrderEntryContributor().createRows(orderModel).forEach(row -> {

      final SapCpiOrderItem sapCpiOrderItem = new SapCpiOrderItem();

      sapCpiOrderItem.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
      sapCpiOrderItem.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));
      sapCpiOrderItem.setQuantity(mapAttribute(OrderEntryCsvColumns.QUANTITY, row));
      sapCpiOrderItem.setProductCode(mapAttribute(OrderEntryCsvColumns.PRODUCT_CODE, row));
      sapCpiOrderItem.setUnit(mapAttribute(OrderEntryCsvColumns.ENTRY_UNIT_CODE, row));
      sapCpiOrderItem.setProductName(mapAttribute(OrderEntryCsvColumns.PRODUCT_NAME, row));

      sapCpiOrderItem.setNamedDeliveryDate(mapDateAttribute(OrderEntryCsvColumns.EXPECTED_SHIPPING_DATE, row));
      sapCpiOrderItem.setPlant(mapAttribute(OrderEntryCsvColumns.WAREHOUSE, row));
      sapCpiOrderItem.setItemCategory(mapAttribute(OrderEntryCsvColumns.ITEM_CATEGORY, row));

      sapCpiOrderItems.add(sapCpiOrderItem);

    });

    return sapCpiOrderItems;

  }

  @Override
  protected List<SapCpiPartnerRole> mapOrderPartners(OrderModel orderModel) {

    final List<SapCpiPartnerRole> sapCpiPartnerRoles = new ArrayList<>();

    getSapPartnerContributor().createRows(orderModel).forEach(row -> {

      SapCpiPartnerRole sapCpiPartnerRole = new SapCpiPartnerRole();

      sapCpiPartnerRole.setOrderId(mapAttribute(OrderCsvColumns.ORDER_ID, row));
      sapCpiPartnerRole.setDocumentAddressId(mapAttribute(PartnerCsvColumns.DOCUMENT_ADDRESS_ID, row));
      sapCpiPartnerRole.setPartnerId(mapAttribute(PartnerCsvColumns.PARTNER_CODE, row));
      sapCpiPartnerRole.setPartnerRoleCode(mapAttribute(PartnerCsvColumns.PARTNER_ROLE_CODE, row));

      sapCpiPartnerRole.setEntryNumber(mapAttribute(OrderEntryCsvColumns.ENTRY_NUMBER, row));

      sapCpiPartnerRoles.add(sapCpiPartnerRole);

    });

    return sapCpiPartnerRoles;
  }

  @Override
  public List<SapCpiOrderCancellation> convertCancelOrderToSapCpiCancelOrder(OrderCancelRecordEntryModel orderCancelRecordEntryModel) {

    List<SapCpiOrderCancellation> sapCpiOrderCancellations = new ArrayList<>();

    getSapOrderCancelRequestContributor().createRows(orderCancelRecordEntryModel)
            .stream()
            .collect(Collectors.groupingBy(row -> row.get(OrderCsvColumns.ORDER_ID))).entrySet().forEach(

            rowSet -> {

              final SapCpiOrderCancellation sapCpiOrderCancellation = new SapCpiOrderCancellation();
              final List<SapCpiOrderCancellationItem> sapCpiOrderCancellationItems = new ArrayList<>();

              rowSet.getValue().stream().findFirst().ifPresent(row -> {
                        sapCpiOrderCancellation.setRejectionReason(row.get(OrderEntryCsvColumns.REJECTION_REASON).toString());
                        sapCpiOrderCancellation.setOrderId(row.get(OrderCsvColumns.ORDER_ID).toString());
                        sapCpiOrderCancellation.setSapCpiConfig(mapOrderCancellationConfigInfo(row.get(OrderCsvColumns.LOGICAL_SYSTEM).toString()));
                      }
              );

              rowSet.getValue().stream().forEach(row -> {

                SapCpiOrderCancellationItem sapCpiOrderCancellationItem = new SapCpiOrderCancellationItem();
                sapCpiOrderCancellationItem.setEntryNumber(row.get(OrderEntryCsvColumns.ENTRY_NUMBER).toString());
                sapCpiOrderCancellationItem.setProductCode(row.get(OrderEntryCsvColumns.PRODUCT_CODE).toString());
                sapCpiOrderCancellationItems.add(sapCpiOrderCancellationItem);

              });

              sapCpiOrderCancellation.setSapCpiOrderCancellationItems(sapCpiOrderCancellationItems);

              sapCpiOrderCancellations.add(sapCpiOrderCancellation);

              if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("SCPI cancel order object: %n %s", ReflectionToStringBuilder.toString(sapCpiOrderCancellation, ToStringStyle.MULTI_LINE_STYLE)));
              }
            }
    );

    return sapCpiOrderCancellations;
  }


  protected SapCpiConfig mapOrderCancellationConfigInfo(String sapLogicalSystemName) {

    SapCpiConfig sapCpiConfig = new SapCpiConfig();
    sapCpiConfig.setSapCpiTargetSystem(mapTargetSystem(getSapCpiOrderDestinationService().readSapLogicalSystem(sapLogicalSystemName)));

    return sapCpiConfig;

  }

  protected SapCpiTargetSystem mapTargetSystem(SAPLogicalSystemModel sapLogicalSystem) {

    final SapCpiTargetSystem sapCpiTargetSystem = new SapCpiTargetSystem();

    sapCpiTargetSystem.setSenderName(sapLogicalSystem.getSenderName());
    sapCpiTargetSystem.setSenderPort(sapLogicalSystem.getSenderPort());

    sapCpiTargetSystem.setReceiverName(sapLogicalSystem.getSapLogicalSystemName());
    sapCpiTargetSystem.setReceiverPort(sapLogicalSystem.getSapLogicalSystemName());

    Objects.requireNonNull(sapLogicalSystem.getSapHTTPDestination(), "No HTTP destination is maintained in back-office for OMS order or OMS order cancellation replication to SCPI!");

    sapCpiTargetSystem.setUsername(sapLogicalSystem.getSapHTTPDestination().getUserid());

    sapCpiTargetSystem.setUrl(getSapCpiOrderDestinationService().determineUrlDestination(sapLogicalSystem));
    sapCpiTargetSystem.setClient(getSapCpiOrderDestinationService().extractSapClient(sapLogicalSystem));

    return sapCpiTargetSystem;

  }

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment;

public class SapCpiOmsFulfillmentUtil {


  public static final String CONSIGNMENT_SUBMISSION_CONFIRMATION_EVENT = "_ConsignmentSubmissionConfirmationEvent";
  public static final String CONSIGNMENT_ACTION_EVENT = "_ConsignmentActionEvent";
  public static final String PACK_CONSIGNMENT = "packConsignment";
  public static final String CANCEL_CONSIGNMENT = "cancelConsignment";

  public static final String CONFIRM_SHIP_CONSIGNMENT = "confirmShipConsignment";

  private SapCpiOmsFulfillmentUtil() {
    throw new IllegalStateException("Utility class cannot be instantiated!");
  }

}

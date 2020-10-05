/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment.cancellation;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ordercancel.OrderCancelIntegrationTest;
import de.hybris.platform.sap.saporderexchangeoms.cancellation.SapOmsOrderCancelIntegrationTest;

@IntegrationTest(replaces = SapOmsOrderCancelIntegrationTest.class)
public class SapCpiOmsOrderCancelIntegrationTest extends OrderCancelIntegrationTest
{
	//Restore the default test class because the default order process order-process is used instead of the SAP OMS order process sap-oms-order-process
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapcpiomsfulfillment;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.ysapcpiomsfulfillment.cancellation.SapCpiOmsEnterCancellingStrategyTest;
import de.hybris.platform.sap.ysapcpiomsfulfillment.inbound.helper.SapCipOmsInboundHelperTest;
import de.hybris.platform.sap.ysapcpiomsfulfillment.inbound.helper.SapCipOmsInboundOrderHelperTest;
import de.hybris.platform.sap.ysapcpiomsfulfillment.strategy.SapConsignmentPreFulfillmentStrategyTest;
import de.hybris.platform.sap.ysapcpiomsfulfillment.strategy.SapSendConsignmentToExternalFulfillmentSystemStrategyTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@UnitTest
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                SapCipOmsInboundHelperTest.class,
                SapCipOmsInboundOrderHelperTest.class,
                SapConsignmentPreFulfillmentStrategyTest.class,
                SapSendConsignmentToExternalFulfillmentSystemStrategyTest.class,
                SapCpiOmsEnterCancellingStrategyTest.class
        })
public class SapCpiOmsFulfillmentTestSuite {
  // To run the test suite
}
/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.caltool.apic.ws.client.facade.WsSeriesStatisticsAsync;

/**
 * Testcases for function version filters within WebService client.<br>
 * <b>Usage notes</b>:
 * <ul>
 * <li>Add additional methods with additional testcases. For a testcase, the function name, function version, excepted
 * result (for example 24.0) and parameter name to test are needed.</li>
 * <li>Add parameters using the addParameters method. Multiple parameters can be passed.</li>
 * <li>Create a filter using {@link FilterFactory#createFunctionFilter(String, String)}</li>
 * <li>Add filters using the addFilters method. Multiple filters can be passed.</li>
 * <li>Call runWebService() to gather results service</li>
 * <li>Call assertResult(String, String). Pass the parameter name to be checked and the excepted value.</li>
 * </ul>
 *
 * @author imi2si
 * @since 1.14
 */
public class SystemConstantFilterTest extends AbstractSeriesStatisticsTest {

  /**
   * Clears the filters and parameters before each test to ensure only the filters and parameters created in one test
   * are passed to the webservice.
   */
  @Before
  public void setAttributes() {
    this.webServiceClient = new WsSeriesStatisticsAsync();
  }

  /**
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testWithSysConsFilter() throws ClassNotFoundException, IOException {
    this.webServiceClient.addParameters("ACCI_tiDebESPDef_C");
    this.webServiceClient.addFilters(FilterFactory.createSysConstFilter("ACCD_PANALIN_CUR_X", 25D));

    runServiceAndWait();

    double returnValue = ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFiles()[0].getCalDataPhy())
        .getAtomicValuePhy().getDValue();

    assertTrue("Value with sys constants filter is " + returnValue + ", excepted : >0", returnValue > 0);


  }

}

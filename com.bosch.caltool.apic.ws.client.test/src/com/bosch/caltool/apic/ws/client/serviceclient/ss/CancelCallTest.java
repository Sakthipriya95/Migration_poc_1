/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.bosch.caltool.apic.ws.client.facade.WsSeriesStatisticsAsync;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;

/**
 * Testcases for base components filters within WebService client.<br>
 * <b>Usage notes</b>:
 * <ul>
 * <li>Add additional methods with additional testcases. For a testcase, the BC name, BC version, excepted result (for
 * example 24.0) and parameter name to test are needed.</li>
 * <li>Add parameters using the addParameters method. Multiple parameters can be passed.</li>
 * <li>Create a filter using {@link FilterFactory#createBaseCompFilter(String, String)}</li>
 * <li>Add filters using the addFilters method. Multiple filters can be passed.</li>
 * <li>Call runWebService() to gatrher results service</li>
 * <li>Call assertResult(String, String). Pass the parameter name to be checked and the excepted value.</li>
 * </ul>
 *
 * @author imi2si
 * @since 1.14
 */
public class CancelCallTest extends AbstractSeriesStatisticsTest {

  /**
   * Clears the filters and parameters before each test to ensure only the filters and parameters created in one test
   * are passed to the webservice.
   */
  @Before
  public void clearAttributes() {
    this.webServiceClient = new WsSeriesStatisticsAsync();
  }

  /**
   * Test with one BC and one parameter.<br>
   * Passed: BaseComponent HESrv with version 2.5.0_2.1.0 parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 24.0
   */
  @Test
  public void webServiceCallShouldBeCancelled() {
    setWsName("CancelWsCall");
    addParamPropFile("ManyParameters");
    runService();
    wait(10);
    assertTrue(cancelWebService());
  }

  /**
   *
   */
  @Test
  public void secondCancelRequestIgnored() {
    setWsName("CancelWsCall");
    addParamPropFile("ManyParameters");
    runService();
    wait(10);
    assertTrue(cancelWebService());

    // Try to abort the session two more times
    assertFalse(cancelWebService());
    assertFalse(cancelWebService());
  }

  /**
   * After the service is cancelled, it may take a few seconds before the session really stops. That's why the test has
   * to wait until the series statistics call has finished.
   */
  @Test
  public void requestShouldBeBrokenAfterCancel() {
    setWsName("CancelWsCall");
    addParamPropFile("ManyParameters");
    runService();
    wait(10);
    assertTrue(cancelWebService());

    // request Should Be Broken After Cancel
    waitForCompletion();
    assertTrue("Excepted: Session marked as broken after cancellation. Result: Not broken.",
        getSeriesStatisticsCallback().isBroken());
    assertNotNull("Excepted: Exception that says 'Session cancelled due to user request'. Result: Exception null.",
        getSeriesStatisticsCallback().getParameterException());
  }

  private boolean cancelWebService() {
    boolean result = false;

    APICWebServiceClient icdmWsClient = new APICWebServiceClient();
    try {
      result = icdmWsClient.cancelSession(super.webServiceClient.getSessionId());
    }
    catch (Exception e) {
      assertNull("Unexcepted exception when cancelling webservice", e);
    }

    return result;
  }
}

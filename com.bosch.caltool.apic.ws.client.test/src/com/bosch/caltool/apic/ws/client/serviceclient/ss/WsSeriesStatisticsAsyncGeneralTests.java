/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.caltool.apic.ws.client.ParameterStatisticCallbackHandler;
import com.bosch.caltool.apic.ws.client.facade.WsSeriesStatisticsAsync;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter;

/**
 * @author BNE4COB
 */
public class WsSeriesStatisticsAsyncGeneralTests extends AbstractSeriesStatisticsTest {

  /**
   * Clears the filters and parameters before each test to ensure only the filters and parameters created in one test
   * are passed to the webservice.
   */
  @Before
  public void clearAttributes() {
    this.webServiceClient = new WsSeriesStatisticsAsync();
  }

  /**
   * Test for an invalid parameter name
   */
  @Test
  public void testNonExistingParameter() {

    this.webServiceClient.addParameters("Test_with_a_nonsense_parameter");

    runServiceAndWait();

    ParameterStatisticCallbackHandler callback = getSeriesStatisticsCallback();

    assertTrue("call back broken", callback.isBroken());
    assertNotNull("call back exception available", callback.getParameterException());

    LOG.info("Returned Message: {}", callback.getParameterException().getMessage());
    assertEquals("Verify error message", "no statistics found for the passed labels",
        callback.getParameterException().getMessage());
  }

  /**
   * Test, if a split iteration were no parameters are found leads to an error. If the split limit is 10 and 12 labels
   * are passed from which 11 have no result, there's one iteration were no label is returned. That must not lead to an
   * error. This problems seems to happen only if no filter is passed
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testSplitNoMatch() throws IOException, ClassNotFoundException {
    this.webServiceClient.addParameters("Test1", "Test2", "Test3", "Test4", "Test5", "Test6", "Test7", "Test8", "Test9",
        "Test10", "Test11", "ACCI_mVehTare_C");

    runServiceAndWait();

    int numberOfFiles = this.webServiceClient.getFiles().length;
    assertTrue("Webservice ended with errors ", numberOfFiles == 1);
  }

  /**
   * Test, if the filtered attribut is only returned once. If an attribute is part of the filter, it might be returned
   * once for every iteration of the split list. The filtered attribute might only returned once.
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testSplitSingleValue() throws IOException, ClassNotFoundException {

    this.webServiceClient.addParameters("Test1", "Test2", "Test3", "Test4", "Test5", "Test6", "Test7", "Test8", "Test9",
        "Test10", "Test11", "ACCI_vMinHalt_C");

    this.webServiceClient.addFilters(
        FilterFactory.createParamFilter("ACCI_vMinHalt_C", ISeriesStatisticsFilter.ValueType.MAX_VALUE, 0.48D));

    runServiceAndWait();

    int numberOfFiles = this.webServiceClient.getFiles().length;
    assertTrue("Number of files wrong. Excepted 1, got " + numberOfFiles, numberOfFiles == 1);

  }

  /**
   * Test, if filtering works. The test is done with parameter ACCI_tiDebESPDef_C, which can be found in iCDM project
   * "X_Test_Henze_Bugatti_Galibier, A2L TY15B1 : 003-006550...".
   * <p>
   * The parameter has 3 different values:<br>
   * Value 200 in 6780 datasets<br>
   * Value 0 in 1921 datasets<br>
   * Value 1000 in 462 datasets
   * <p>
   * Without filter, the return Peak Value will be 200<br>
   * Wit filter min value = 500, the return Peak Value will be 1000<br>
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testNoFilter() throws ClassNotFoundException, IOException {
    // First Test without filter
    this.webServiceClient.addParameters("ACCI_tiDebESPDef_C", "ACCI_tiDebClth_C");

    runServiceAndWait();

    assertFalse(this.webServiceClient.getCallbackHandler().isBroken());
    assertTrue(this.webServiceClient.getCallbackHandler().getFiles().length == 2);

    // Without filter, 200 is excepted as return value
    double returnValue =
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("ACCI_tiDebESPDef_C").getCalDataPhy())
            .getAtomicValuePhy().getDValue(); //
    assertTrue("Value without filter is " + returnValue + ", excepted 200", returnValue == 200D);

    LOG.info(" ACCI_tiDebESPDef_C w/o Value: " +
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("ACCI_tiDebESPDef_C").getCalDataPhy())
            .getAtomicValuePhy().getDValue());
    LOG.info(" ACCI_tiDebClth_C w/o Value: " +
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("ACCI_tiDebClth_C").getCalDataPhy())
            .getAtomicValuePhy().getDValue());
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.calmodel.caldataphy.CalDataPhyValue;
import com.bosch.caltool.apic.ws.client.facade.WsSeriesStatisticsAsync;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.DefaultSeriesStatisticsFilter;
import com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.ISeriesStatisticsFilter;

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
public class ParameterFilterTest extends AbstractSeriesStatisticsTest {

  /**
   * Clears the filters and parameters before each test to ensure only the filters and parameters created in one test
   * are passed to the webservice.
   */
  @Before
  public void setAttributes() {
    this.webServiceClient = new WsSeriesStatisticsAsync();
  }


  /**
   * With filter, 1000 is excepted as return value
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testWithLabelFilterSingle() throws ClassNotFoundException, IOException {
    this.webServiceClient.addParameters("ACCI_tiDebESPDef_C", "ACCI_tiDebClth_C");
    this.webServiceClient.addFilters(
        FilterFactory.createParamFilter("ACCI_tiDebESPDef_C", ISeriesStatisticsFilter.ValueType.MIN_VALUE, 800D));

    runServiceAndWait();

    double returnValue =
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("ACCI_tiDebESPDef_C").getCalDataPhy())
            .getAtomicValuePhy().getDValue();

    assertTrue("Value with parameter filter is " + returnValue + ", excepted 1000", returnValue == 1000D);
    LOG.info(" ACCI_tiDebESPDef_C with Filter Value: " +
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("ACCI_tiDebESPDef_C").getCalDataPhy())
            .getAtomicValuePhy().getDValue());
    LOG.info(" ACCI_tiDebClth_C with Filter Value: " +
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("ACCI_tiDebClth_C").getCalDataPhy())
            .getAtomicValuePhy().getDValue());


  }

  /**
   * Third test with filter (11_0614T.A2L in PIDC M281)
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testWithLabelFilterTwo() throws ClassNotFoundException, IOException {
    this.webServiceClient.addParameters("CWBGLWM", "DPABEFO");
    this.webServiceClient
        .addFilters(FilterFactory.createParamFilter("CWBGLWM", ISeriesStatisticsFilter.ValueType.MAX_VALUE, 2D));
    this.webServiceClient
        .addFilters(FilterFactory.createParamFilter("CWBGLWM", ISeriesStatisticsFilter.ValueType.MIN_VALUE, 2D));

    runServiceAndWait();


    // "CWBGLWM", "DPABEFO", PIUDC M281, CWBGLWM Min+Max Value = 2
    double returnValue =
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("CWBGLWM").getCalDataPhy())
            .getAtomicValuePhy().getDValue();
    assertTrue("CWBGLWM with parameter filter is " + returnValue + ", excepted 2.0", returnValue == 2D);

    returnValue = ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("DPABEFO").getCalDataPhy())
        .getAtomicValuePhy().getDValue();
    assertTrue("DPABEFO with parameter filter is " + returnValue + ", excepted 200", returnValue == 200D);

  }

  /**
   * Fourth test with filter (11_0614T.A2L in PIDC M281)
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testWithLabelFilterTwo2() throws ClassNotFoundException, IOException {
    this.webServiceClient.addParameters("CWBGLWM", "DPABEFO");
    this.webServiceClient
        .addFilters(FilterFactory.createParamFilter("CWBGLWM", ISeriesStatisticsFilter.ValueType.MAX_VALUE, 64D));
    this.webServiceClient
        .addFilters(FilterFactory.createParamFilter("CWBGLWM", ISeriesStatisticsFilter.ValueType.MIN_VALUE, 64D));

    runServiceAndWait();

    double returnValue =
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("CWBGLWM").getCalDataPhy())
            .getAtomicValuePhy().getDValue();
    assertTrue("CWBGLWM with parameter filter is " + returnValue + ", excepted 64", returnValue == 64D);

    returnValue = ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("DPABEFO").getCalDataPhy())
        .getAtomicValuePhy().getDValue();
    assertTrue("DPABEFO with parameter filter is " + returnValue + ", excepted 200", returnValue == 200D);


  }

  /**
   * Fifth test with filter (11_0614T.A2L in PIDC M281)
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testWithLabelFilterTwoEmptyFilterValue() throws ClassNotFoundException, IOException {
    this.webServiceClient.addParameters("CWBGLWM", "DPABEFO");
    this.webServiceClient
        .addFilters(new DefaultSeriesStatisticsFilter("DPABEFO", ISeriesStatisticsFilter.DataType.PARAMETER_FILTER,
            ISeriesStatisticsFilter.ValueType.MAX_VALUE, new AtomicValuePhy("")));
    this.webServiceClient
        .addFilters(new DefaultSeriesStatisticsFilter("DPABEFO", ISeriesStatisticsFilter.DataType.PARAMETER_FILTER,
            ISeriesStatisticsFilter.ValueType.MIN_VALUE, new AtomicValuePhy("")));

    runServiceAndWait();


    double returnValue =
        ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("CWBGLWM").getCalDataPhy())
            .getAtomicValuePhy().getDValue();
    LOG.info("CWBGLWM " + returnValue);
    // assertTrue("CWBGLWM with parameter filter is " + returnValue + ", excepted 64", returnValue == 64D);

    returnValue = ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFile("DPABEFO").getCalDataPhy())
        .getAtomicValuePhy().getDValue();
    LOG.info("DPABEFO " + returnValue);
    assertTrue("DPABEFO with parameter filter is " + returnValue + ", excepted 200", returnValue == 200D);


  }

  /**
   * Test, if it is possible to filter for a String value. The AtomicValuePhy can either be a String- Value or a
   * Double-Value (which is the more common case)
   *
   * @throws IOException error in processing response
   * @throws ClassNotFoundException error in processing response
   */
  @Test
  public void testStringLabelWoFilter() throws ClassNotFoundException, IOException {
    this.webServiceClient.addParameters("Rail_stHighPresGovMode_C");

    runServiceAndWait();

    // Without filter, 200 is excepted as return value
    // Check Return Value for Label with String-Value but without filter
    String returnValue = ((CalDataPhyValue) this.webServiceClient.getCallbackHandler().getFiles()[0].getCalDataPhy())
        .getAtomicValuePhy().getSValue();
    LOG.info("Rail_stHighPresGovMode_C - return :{}", returnValue);
    assertEquals("Testing value without filter", "RAIL_PCV", returnValue);

  }

}

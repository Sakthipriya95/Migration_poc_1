/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.apic.ws.client.facade.WsSeriesStatisticsAsync;
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
public class FilterInCalDataHistoryTest extends AbstractSeriesStatisticsTest {

  /**
   * Clears the filters and parameters before each test to ensure only the filters and parameters created in one test
   * are passed to the webservice.
   */
  @Before
  public void setAttributes() {
    this.webServiceClient = new WsSeriesStatisticsAsync();
  }

  /**
   * Tests, if a function filter appears in the CalDataHistory object. <br>
   * Passed: Function ABKVP with version 2.11.0. Parameter to check: InjVlv_iThresMax_C<br>
   * Excepted: CalDataHistory contains filter String "SeriesStatistics Filters used for this label:
   * A2LFunctionDatasetFilter: Function ABKVP = 2.11.0"
   *
   * @see FunctionFilterTest#filterTwoFunctionTest2Part1()
   */
  @Test
  public void filterOneFunction() {
    addParameters("InjVlv_iThresMax_C");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "2.11.0"));
    runServiceAndWait();
    showFilterInHistory("InjVlv_iThresMax_C");
    assertFilter("InjVlv_iThresMax_C", "ABKVP = 2.11.0");
  }

  /**
   * Tests, if a BC filters appears in the CalDataHistory object. <br>
   * Passed: BaseComponent HESrv with version 2.5.0_2.1.0 parameter to check: UBSQBKVMX<br>
   * Excepted:
   */
  @Test
  public void filterOneBaseComp() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("HESrv", "2.5.0_2.1.0"));
    runServiceAndWait();
    showFilterInHistory("UBSQBKVMX");
    assertFilter("UBSQBKVMX", "HESrv = 2.5.0_2.1.0");
  }

  /**
   * Tests, if a BC filters appears in the CalDataHistory object. <br>
   * Passed: BaseComponent HESrv with version 2.5.0_2.1.0 parameter to check: UBSQBKVMX<br>
   * Excepted:
   */
  @Test
  public void filterTwoParameters() {
    addParameters("CWBGLWM");
    addParameters("DPABEFO");
    addFilters(FilterFactory.createParamFilter("CWBGLWM", ISeriesStatisticsFilter.ValueType.MIN_VALUE, 2D));
    addFilters(FilterFactory.createParamFilter("CWBGLWM", ISeriesStatisticsFilter.ValueType.MAX_VALUE, 2D));
    runServiceAndWait();
    showFilterInHistory("CWBGLWM");
    assertFilter("CWBGLWM", "CWBGLWM >= 2.0");
    assertFilter("CWBGLWM", "CWBGLWM <= 2.0");
  }

  /**
   * Tests, if a parameter condition with a different min/max value appears in the CalDataHistory. <br>
   * Passed: BaseComponent HESrv with version 2.5.0_2.1.0 parameter to check: UBSQBKVMX<br>
   * Excepted:
   */
  @Test
  public void filterParameterDiffMinMax() {
    addParameters("ACCI_tiDebESPDef_C");
    addFilters(FilterFactory.createParamFilter("ACCI_tiDebESPDef_C", ISeriesStatisticsFilter.ValueType.MIN_VALUE, 0D),
        FilterFactory.createParamFilter("ACCI_tiDebESPDef_C", ISeriesStatisticsFilter.ValueType.MAX_VALUE, 1000D));
    runServiceAndWait();
    showFilterInHistory("ACCI_tiDebESPDef_C");
    assertFilter("ACCI_tiDebESPDef_C", "ACCI_tiDebESPDef_C >= 0.0");
    assertFilter("ACCI_tiDebESPDef_C", "ACCI_tiDebESPDef_C <= 1000.0");
  }

  private void showFilterInHistory(final String parameter) {
    LOG.info(getFilterInformation(getCalData(parameter)));
  }

  private void assertFilter(final String parameter, final String exceptedFilterString) {
    String filterText = getFilterInformation(getCalData(parameter));

    assertTrue("Excepted filter \"" + exceptedFilterString + "\" not part of filtertext: \"" + filterText + "\"",
        filterText.indexOf(exceptedFilterString) != -1);
  }

  private CalData getCalData(final String parameter) {
    CalData calData = null;
    try {
      calData = this.webServiceClient.getCallbackHandler().getFile(parameter);

    }
    catch (ClassNotFoundException | IOException e) {
      assertNull("Error when fetching parameter " + parameter, e);
    }

    return calData;
  }

  private String getFilterInformation(final CalData calData) {
    StringBuffer allFilters = new StringBuffer();
    for (HistoryEntry entry : calData.getCalDataHistory().getHistoryEntryList()) {
      allFilters.append(extractFilterText(entry.getRemark().getValue()));
    }

    return allFilters.toString();
  }

  private String extractFilterText(final String text) {
    StringBuffer allFilters = new StringBuffer();

    Pattern p = Pattern.compile("\\bSeriesStatistics Filters used for this label:.*");
    Matcher m = p.matcher(text);
    while (m.find()) {
      allFilters.append(m.group());
    }
    return allFilters.toString();
  }
}

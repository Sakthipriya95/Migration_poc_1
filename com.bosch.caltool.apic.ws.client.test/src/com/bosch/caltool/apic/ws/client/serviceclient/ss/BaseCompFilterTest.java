/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import org.junit.Before;
import org.junit.Test;

import com.bosch.caltool.apic.ws.client.facade.WsSeriesStatisticsAsync;

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
public class BaseCompFilterTest extends AbstractSeriesStatisticsTest {

  /**
   * Default constructor
   */
  public BaseCompFilterTest() {}

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
  public void filterOneBaseCompTest1() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("HESrv", "2.5.0_2.1.0"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "24.0");
  }

  /**
   * Test with one BC and one parameter.<br>
   * Passed: BaseComponent InjSyG with version 18.7.0_KEFICO parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 16.0
   */
  @Test
  public void filterOneBaseCompTest2() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("InjSyG", "18.7.0_KEFICO"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "16.0");
  }

  /**
   * In this testcase, two BCs are passed which have no common file ids. Thats why a NullObject should be returned.<br>
   * Passed: BaseComponent HESrv with version 2.5.0_2.1.0 and Base Component TDev with version 20.6.0_1.5.0 parameter to
   * check: UBSQBKVMX<br>
   * Excepted: Returning a NullObject - no statistics found
   */
  @Test
  public void filterTwoBaseCompNoRes() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("HESrv", "2.5.0_2.1.0"));
    addFilters(FilterFactory.createBaseCompFilter("TDev", "20.6.0_1.5.1"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "NullObject");
  }

  /**
   * In this testcase, two BCs are passed which have common file ids.<br>
   * Passed: BaseComponent AirDvP with version 7.4.1 and Base Component AirMod with version 7.7.0_2.1.0 parameter to
   * check: UBSQBKVMX. Second test case is with different version 7.9.0 and 7.20.0 which should lead to a different
   * result.<br>
   * Excepted: Peak Value of 16.0
   */
  @Test
  public void filterTwoBaseCompDiffVersions() {
    // First test with two versions
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("AirDvP", "7.4.1"));
    addFilters(FilterFactory.createBaseCompFilter("AirMod", "7.7.0_2.1.0"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "16.0");

    // Second test with two versions
    clearAttributes();
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("AirDvP", "7.9.0"));
    addFilters(FilterFactory.createBaseCompFilter("AirMod", "7.20.0"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "24.0");
  }

  /**
   * In this testcase, two BCs are passed which have common file ids.<br>
   * Passed: BaseComponent AirDvP with version 7.4.1 and Base Component AirMod with version 7.7.0_2.1.0 parameter to
   * check: UBSQBKVMX. Second test case is with different version 7.9.0 and 7.20.0 which should lead to a different
   * result.<br>
   * Excepted: Peak Value of 16.0
   */
  @Test
  public void filterTwoBaseCompWildcard() {
    // First test one wildcard
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("AirDvP", "7.24.0%"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "16.0");

    // Second test with one wildcard at a latter position of the version
    clearAttributes();
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createBaseCompFilter("AirDvP", "7.24.0_T%"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "24.0");
  }

}

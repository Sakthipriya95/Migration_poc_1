/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.serviceclient.ss;

import org.junit.Before;
import org.junit.Test;

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
public class FunctionFilterTest extends AbstractSeriesStatisticsTest {

  /**
   * Clears the filters and parameters before each test to ensure only the filters and parameters created in one test
   * are passed to the webservice.
   */
  @Before
  public void setAttributes() {
    this.webServiceClient = new WsSeriesStatisticsAsync();
  }

  /**
   * Test with one FC and one parameter.<br>
   * Passed: Function ABKVP with version 2.11.0 parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 16.0
   */
  @Test
  public void filterOneVersionTest1() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "2.11.0"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "16.0");
  }

  /**
   * Test with one FC and one parameter.<br>
   * Passed: Function ABKVP with version 2.12.0 parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 16.0
   */
  @Test
  public void filterOneVersionTest2() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "2.12.0"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "16.0");
  }

  /**
   * Test with one FC and one parameter.<br>
   * Passed: Function ABKVP with version 10.21 parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 24.0
   */
  @Test
  public void filterOneVersionTest3() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "10.21"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "24.0");
  }

  /**
   * Test with one FC (two versions) and one parameter.<br>
   * Passed: Function ABKVP with version 10.21 and 10.40 parameter to check: UBSQBKVMX<br>
   * Excepted: Returning no result, because an or logic is not supported. The user should rather work with a wildcard in
   * such a case
   */
  @Test
  public void filterOneFunctionTwoVersions() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "10.21"));
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "10.40"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "NullObject");
  }

  /**
   * Test with two FC and one parameter. This testcase consideres only files that contain both function (Only the
   * intersection of fil ids over all functions is considered).<br>
   * Passed: Function ABKVP with version 2.11.0 and ACCECU_Acc with version 3.26.0. Parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 16.0
   */
  @Test
  public void filterTwoFunction() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "2.11.0"));
    addFilters(FilterFactory.createFunctionFilter("ACCECU_Acc", "3.26.0"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "16.0");
  }

  /**
   * TestSet with method filterTwoFunctionTest2Part1 and filterTwoFunctionTest2Part2<br>
   * filterTwoFunctionTest2Part1() tests for one function, filterTwoFunctionTest2Part2 for the same and an additional
   * function to prove both filters are used. <br>
   * Passed: Function ABKVP with version 2.11.0 . Parameter to check: InjVlv_iThresMax_C<br>
   * Excepted: Returning a peak value of 23.0
   *
   * @see FunctionFilterTest#filterTwoFunctionTest2Part2()
   */
  @Test
  public void filterTwoFunctionTest2Part1() {
    addParameters("InjVlv_iThresMax_C");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "2.11.0"));
    runServiceAndWait();
    assertResult("InjVlv_iThresMax_C", "23.0");
  }

  /**
   * TestSet with method filterTwoFunctionTest2Part1 and filterTwoFunctionTest2Part2<br>
   * filterTwoFunctionTest2Part1() tests for one function, filterTwoFunctionTest2Part2 for the same and an additional
   * function to prove both filters are used. <br>
   * Passed: Function ABKVP with version 2.11.0 and ACCECU_Acc with version 3.26.0. Parameter to check:
   * InjVlv_iThresMax_C<br>
   * Excepted: Returning a NullObject
   *
   * @see FunctionFilterTest#filterTwoFunctionTest2Part1()
   */
  @Test
  public void filterTwoFunctionTest2Part2() {
    addParameters("InjVlv_iThresMax_C");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "2.11.0"));
    addFilters(FilterFactory.createFunctionFilter("ACCECU_Acc", "3.20.1"));
    runServiceAndWait();
    assertResult("InjVlv_iThresMax_C", "NullObject");
  }

  /**
   * Test a filter with a wildcard <br>
   * Passed: Function ABKVP with version 10.*. Parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 16.0
   */
  @Test
  public void filterOneFunctionWildcard() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "10.*"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "16.0");
  }

  /**
   * Test a filter with a non existing function version. <br>
   * Passed: Function ABKVP with version 100.21. Parameter to check: UBSQBKVMX<br>
   * Excepted: Returning a peak value of 16.0
   */
  @Test
  public void filterNoneExistingVersion() {
    addParameters("UBSQBKVMX");
    addFilters(FilterFactory.createFunctionFilter("ABKVP", "100.21"));
    runServiceAndWait();
    assertResult("UBSQBKVMX", "NullObject");
  }
}

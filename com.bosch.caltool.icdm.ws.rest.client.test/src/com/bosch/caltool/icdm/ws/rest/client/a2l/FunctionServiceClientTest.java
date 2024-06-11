/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestClientTest;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dja7cob Test class for service to fetch work package resources
 */
public class FunctionServiceClientTest extends AbstractRestClientTest {

  /**
   * Test method for {@link FunctionServiceClient#getSearchFunctions(String) }.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetSearchFunctions() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    Set<Function> functions = service.getSearchFunctions("bba");
    assertFalse("Response should be null or empty", (functions == null) || functions.isEmpty());
    testOutput(functions);
  }

  /**
   * Test method for {@link FunctionServiceClient#getSearchFunctions(String) }.Negative test
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetSearchFunctionsNegative() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    Set<Function> functions = service.getSearchFunctions("/");
    assertTrue("Response should be empty", functions.isEmpty());
  }

  /**
   * Test method for {@link FunctionServiceClient#getMissingFunctions(Set,Set) }.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMissingFunctions() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    Set<String> function = new HashSet<>();
    Set<String> a2lfunction = new HashSet<>();
    function.add("DC_MDMAX");
    function.add("SR_DISP");
    function.add("TQCNV72150_FCT_TQCNV72150_10MS_FCT");
    a2lfunction.add("DC_MDMAX");
    a2lfunction.add("SR_DISP");
    a2lfunction.add("ENGSTRT_THRESPINJRELS");
    a2lfunction.add("COALTSTAH_81728_FCT_S817285_F03_MA__ES_DE_DEMARRAGE");
    for (int i = 1; i < 11; i++) {
      function.add("TEST" + i);
      a2lfunction.add("TESTa2l" + i);
    }
    Set<String> functions = service.getMissingFunctions(function, a2lfunction);
    assertFalse("Response should not be null or empty", functions.isEmpty());
    for (String func : functions) {
      LOG.info("Invalid Test Function Name: {}", func);
    }
    LOG.info("Invalid Function list size = " + functions.size());
  }

  /**
   * Test method for {@link FunctionServiceClient#getMissingFunctions(Set,Set) }.Negative test
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetMissingFunctionsNegative() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    Set<String> function = new HashSet<>();
    Set<String> a2lfunction = new HashSet<>();
    function.add("DC_MDMAX");
    function.add("SR_DISP");
    function.add("TQCNV72150_FCT_TQCNV72150_10MS_FCT");
    a2lfunction.add("DC_MDMAX");
    a2lfunction.add("SR_DISP");
    a2lfunction.add("ENGSTRT_THRESPINJRELS");
    a2lfunction.add("COALTSTAH_81728_FCT_S817285_F03_MA__ES_DE_DEMARRAGE");
    Set<String> functions = service.getMissingFunctions(function, a2lfunction);
    assertTrue("Response should be null or empty", functions.isEmpty());
  }

  /**
   * Test method for {@link FunctionServiceClient#getInvalidFunctions(List) }.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetInvalidFunctions() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    List<String> function = new ArrayList<>();
    function.add("DC_MDMAX");
    function.add("SR_DISP");
    function.add("TQCNV72150_FCT_TQCNV72150_10MS_FCT");
    for (int i = 1; i < 11; i++) {
      function.add("TEST" + i);
    }
    List<String> functions = service.getInvalidFunctions(function);
    assertFalse("Response should not be null or empty", functions.isEmpty());
    for (String func : functions) {
      LOG.info("Invalid Test Function Name: {}", func);
    }
    LOG.info("Invalid Function list size : {} ", functions.size());
  }

  /**
   * Test method for {@link FunctionServiceClient#getInvalidFunctions(List) }.
   *
   * @throws ApicWebServiceException exception
   */
  @Test
  public void testGetInvalidFunctionsNegative() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    List<String> function = new ArrayList<>();
    function.add("DC_MDMAX");
    function.add("SR_DISP");
    function.add("TQCNV72150_FCT_TQCNV72150_10MS_FCT");
    List<String> functions = service.getInvalidFunctions(function);
    assertTrue("Response should be null or empty", functions.isEmpty());
  }

  /**
   * Test method for {@link FunctionServiceClient#getAllUserFunctions()}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetAllUserFunctions() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    Set<Function> functions = service.getAllUserFunctions();
    assertFalse("Response should be null or empty", (functions == null) || functions.isEmpty());
    testOutput(functions);
  }

  /**
   * Test method for {@link FunctionServiceClient#getFunctionsByName(List)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetFunctionsByName() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    List<String> nameSet = new ArrayList<>();
    nameSet.add("BBKR");
    nameSet.add("DC_MDMAX");
    Map<String, Function> functionMap = service.getFunctionsByName(nameSet);
    assertFalse("Response should not be null or empty", (functionMap == null) || functionMap.isEmpty());
    testOutput(functionMap.values());
  }

  /**
   * Test method for {@link FunctionServiceClient#getFunctionsByName(List)}.
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetFunctionsByNameNegative() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    List<String> nameSet = new ArrayList<>();
    nameSet.add("/");
    Map<String, Function> functionMap = service.getFunctionsByName(nameSet);
    assertTrue("Response should be null or empty", (functionMap == null) || functionMap.isEmpty());
  }

  /**
   * Test method for {@link FunctionServiceClient#getFunctionsByParamName(String)}. // T_FUNCTIONS ,T_FUNCTIONVERSIONS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetFunctionsByParamName() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    String paramName = "CTRKNLDG";
    SortedSet<Function> functionSet = service.getFunctionsByParamName(paramName);
    assertFalse("Response should be null or empty", (functionSet == null) || functionSet.isEmpty());
    testOutput(functionSet);
  }

  /**
   * Test method for {@link FunctionServiceClient#getFunctionsByParamName(String)}. // T_FUNCTIONS ,T_FUNCTIONVERSIONS
   *
   * @throws ApicWebServiceException service call error
   */
  @Test
  public void testGetFunctionsByParamNameNegative() throws ApicWebServiceException {
    FunctionServiceClient service = new FunctionServiceClient();
    String paramName = "CTR";
    SortedSet<Function> functionSet = service.getFunctionsByParamName(paramName);
    assertTrue("Response should be null or empty", (functionSet == null) || functionSet.isEmpty());
  }

  /**
   * @param Set <Function> Set
   */
  private void testOutput(final Collection<Function> funcCol) {

    LOG.debug("Response size  = {}", funcCol.size());
    for (Function func : funcCol) {
      testOutput("Test Response item", func);
    }
  }

  /**
   * @param string
   * @param Function
   */
  private void testOutput(final String string, final Function func) {
    assertNotNull(string + ": object not null", func);
    LOG.debug("{}: Function Id = {}; {}", string, func.getId(), func.getName());
    assertFalse(string + ": Name not empty", (func.getName() == null) || func.getName().isEmpty());
  }

}

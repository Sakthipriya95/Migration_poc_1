/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;

import com.bosch.checkssd.datamodel.constantfunctions.ArrayFunc;
import com.bosch.checkssd.datamodel.constantfunctions.AxsvalFunc;
import com.bosch.checkssd.datamodel.constantfunctions.CaseFunc;
import com.bosch.checkssd.datamodel.constantfunctions.FIncrDecrFunc;
import com.bosch.checkssd.datamodel.constantfunctions.IFFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MPMaxMinFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MaxAxsPtFunc;
import com.bosch.checkssd.datamodel.constantfunctions.MaxMinFunc;
import com.bosch.checkssd.datamodel.constantfunctions.NumFunc;
import com.bosch.checkssd.datamodel.constantfunctions.SrcFunc;
import com.bosch.checkssd.datamodel.constantfunctions.VblkFunc;
import com.bosch.checkssd.datamodel.mathterm.IMathTermElement;
import com.bosch.checkssd.datamodel.rule.IFRule;
import com.bosch.checkssd.datamodel.util.SSDModelUtils;

/**
 * @author TUD1COB
 */
public class ConstantFunctionsListTest {

  /**
   * Test MaxMinFunc
   */
  @Test
  public void testErrorFetchingUsers1() {
    IMathTermElement element;
    element = new MaxMinFunc(new SSDModelUtils());
    Assert.assertEquals("MINOF", new ConstantFunctionsList().getFunctionName(element));
  }

  /**
   * Test FIncrDecrFunc
   */
  @Test
  public void testGetFunctionName_FIncrDecrFunc() {
    IMathTermElement element = new FIncrDecrFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("FINCR", functionName);
  }

  /**
   * Test NumFunc
   */
  @Test
  public void testGetFunctionName_NumFunc() {
    IMathTermElement element = new NumFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("NUM", functionName);
  }

  /**
   * Test MaxAxsPtFunc
   */
  @Test
  public void testGetFunctionName_MaxAxsPtFunc() {
    IMathTermElement element = new MaxAxsPtFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("MAXAXISPTS", functionName);
  }

  /**
   * Test SrcFunc
   */
  @Test
  public void testGetFunctionName_SrcFunc() {
    IMathTermElement element = new SrcFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("SRC", functionName);
  }

  /**
   * Test MPMaxMinFunc
   */
  @Test
  public void testGetFunctionName_MPMaxMinFunc() {
    IMathTermElement element = new MPMaxMinFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("MPMIN", functionName);
  }

  /**
   * Test AxsvalFunc
   */
  @Test
  public void testGetFunctionName_AxsvalFunc() {
    IMathTermElement element = new AxsvalFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("AXISVAL", functionName);
  }

  /**
   * Test ArrayFunc
   */
  @Test
  public void testGetFunctionName_ArrayFunc() {
    IMathTermElement element = new ArrayFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("ARRAY", functionName);
  }

  /**
   * Test VblkFunc
   */
  @Test
  public void testGetFunctionName_VblkFunc() {
    IMathTermElement element = new VblkFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("VBLK", functionName);
  }

  /**
   * Test IFFunc
   */
  @Test
  public void testGetFunctionName_IFFunc() {
    IMathTermElement element = new IFFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("IF", functionName);
  }

  /**
   * Test CaseFunc
   */
  @Test
  public void testGetFunctionName_CaseFunc() {
    IMathTermElement element = new CaseFunc(new SSDModelUtils());
    String functionName = new ConstantFunctionsList().getFunctionName(element);
    Assert.assertEquals("CASE", functionName);
  }

  /**
   * Test EmptyFunctionName
   */
  @Test
  public void testEmptyFunctionName() {
    String functionName =
        new ConstantFunctionsList().getFunctionName((IMathTermElement) (new IFRule(new SSDModelUtils()).getCompTrem()));
    assertNotNull(functionName);
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;

/**
 * @author say8cob
 */
public class A2lObjectIdentifierValidatorTest extends JUnitTest {

  /**
   *
   */
  private static final String NO_VALIDATION_FAILED = "No validation failed";

  /**
   *
   */
  private static final String[] VALID_NAMES = {
      "DFES_xClsHealTrg_CA",
      "DFES_xClsDltTrg_CA",
      "DFRAESTAB[0]",
      "DiaBas_swtSrv9InfoTyp12Sup_C[0]",
      "DIUMPR_ctGen[0][.pEnvMin_C",
      "DIUMPR_ctGen[0]].l500MD_C",
      "DIUMPR_ctGen[[0][Bosch]].l500MD_C",
      "DIUMPR_ctGen[[0]]Cust.l500MDInc_C",
      "DIUMPR_ctGen[0].vVehMin_C",
      "DIUMPR_ctGen[12].123vVehMin_C",
      "DIUMPR_Ctl.FId_atevspl_C",
      "DIUMPR_Ctl.FID_CTFUELTSTUCK_C",
      "DFES_Cls.DFC_PFltRgnNoCompl_C",
      "DFES_Cls.DFC_BstCtlGovDvtMaxCSERS_C",
      "DIUMPR_ctGen[1][2]",
      "DIUMPR_ctGen[[1][2]]",
      "DIUMPR_ctGen[[1].[2]]",
      "DIUMPR_ctGen[ab.cd][cu.st]",
      "DIUMPR_ctGen[][]",
      "DIUMPR_ctGen[1][2].123vVehMin_C" };

  private static final String[] INVALID_NAMES = {
      "000DFES_xClsHealTrg_CA.DFC_BstCtlGovDvt00MaxCSERS_C",
      "000DFES_xClsHealTrg_CA",
      "DIUMPR_ct$%^Gen###[0]",
      "DIUMPR_ctGen[@#$]",
      "DIUMPR[_ctGenACBD#].12geddf" };

  private final Map<String, Boolean> resultMap = new LinkedHashMap<>();

  /**
   * Reset test results
   */
  @Before
  public void resetResults() {
    this.resultMap.clear();
  }

  /**
   * Test alias Name valid cases
   */
  @Test
  public void testValidCase() {
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    for (String name : VALID_NAMES) {
      result = validator.isValidName(name);
      addResult(name, result.cardinality() == 0);
    }
    verifyFailures();
  }

  /**
   * Test alias Name invalid cases
   */
  @Test
  public void testInvalidCase() {
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    for (String name : INVALID_NAMES) {
      result = validator.isValidName(name);
      // Negative Test : result = false => Test passes
      addResult(name, result.cardinality() != 0);
    }
    verifyFailures();
  }

  private void addResult(final String testInput, final boolean result) {
    this.resultMap.put(testInput, result);
  }

  private void verifyFailures() {
    StringBuilder result = new StringBuilder();
    this.resultMap.entrySet().stream().forEach(e -> result.append(e.getKey()).append("\t:")
        .append(e.getValue().booleanValue() ? "Pass" : "Fail").append('\n'));

    boolean failed = this.resultMap.values().stream().anyMatch(i -> !i);
    String resStr = result.toString();

    if (failed) {
      TESTER_LOGGER.error("Some tests failed : \n" + resStr);
      fail("Some tests failed : " + resStr);
    }

    TESTER_LOGGER.info("Test Results : \n{}", resStr);
  }

  /**
   * Exclude special characters to make the name valid
   */
  @Test
  public void testValidExcludeCharacter() {
    Map<String, char[]> inputNameMap = new HashMap<>();
    inputNameMap.put("DFES_@xClsHealTrg_CA", new char[] { '@' });
    inputNameMap.put("DFES_@$x*ClsHealTrg_CA", new char[] { '@', '$', '*' });
    inputNameMap.put("DFES_@_0$x<xClsHealTrg_CA", new char[] { '@', '$', '<' });
    inputNameMap.put("DFES_@#x*xClsHealTrg_*CA", new char[] { '@', '#', '*' });
    inputNameMap.put("DFES_!{x*xClsHealTrg$_CA", new char[] { '!', '{', '*', '$' });
    inputNameMap.put("DiaBas_swtSrv9InfoTyp####12Sup_C[0]", new char[] { '#' });

    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    for (Entry<String, char[]> entry : inputNameMap.entrySet()) {
      result = validator.isValidName(entry.getKey(), entry.getValue());
      addResult(entry.getKey(), result.cardinality() == 0);
    }
    verifyFailures();
  }

  /**
   * Miss some special characters from excluding to make the name invalid
   */
  @Test
  public void testInValidExcludeCharacter() {
    Map<String, char[]> inputNameMap = new HashMap<>();
    inputNameMap.put("DFES_@xClsHealTrg_CA", new char[] { '$' });
    inputNameMap.put("DFES_@$x*ClsHealTrg_CA", new char[] { '@', '$' });
    inputNameMap.put("DFES_@_0$x<xClsHealTrg_CA", new char[] { '<' });
    inputNameMap.put("DFES_@#x*xClsHealTrg_*CA", new char[] { '@', '*' });
    inputNameMap.put("DFES_!{x*xClsHealTrg$_CA", new char[] { '!', '{' });
    inputNameMap.put("DiaBas_swtSrv9InfoTyp####12Sup_C[0]", new char[] { '$' });

    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    for (Entry<String, char[]> entry : inputNameMap.entrySet()) {
      result = validator.isValidName(entry.getKey(), entry.getValue());
      addResult(entry.getKey(), result.cardinality() != 0);
    }
    verifyFailures();
  }

  /**
   * Replace special characters with underscore
   */
  @Test
  public void testReplaceSpecCharWithUnderscore() {
    String[] nameWithSpecChar = {
        "DIUMPR_ct$%^Gen###[0]",
        "DFES_@$x*ClsHealTrg_CA",
        "DFES_@_0$x<xClsHealTrg_CA",
        "DFES_@#x*xClsHealTrg_*CA",
        "DFES_!{x*xClsHealTrg$_CA",
        "DiaBas_swtSrv9InfoTyp####12Sup_C[0]" };
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName;
    for (String name : nameWithSpecChar) {
      result = validator.isValidName(name);
      assertTrue(result.cardinality() != 0);
      modifiedName = validator.replaceInvalidChars(name);
      result = validator.isValidName(modifiedName);
      assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
    }
  }

  /**
   * Replace German Special Sign 'ß' should be replaced with 'ss'
   */
  @Test
  public void testReplaceGermanSpecialSign1() {
    String inputName = "DFES__xCls$%^Healß___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("ß is replaced with ss", "DFES__xCls_Healss___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * Replace German Special Sign 'ü' should be replaced with 'ue'
   */
  @Test
  public void testReplaceGermanSpecialSign2() {
    String inputName = "DFES__xCls$%^Healü___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("ü is replaced with ue", "DFES__xCls_Healue___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * Replace German Special Sign 'Ü' should be replaced with 'Ue'
   */
  @Test
  public void testReplaceGermanSpecialSign3() {
    String inputName = "DFES__xCls$%^HealÜ___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("Ü is replaced with Ue", "DFES__xCls_HealUe___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * Replace German Special Sign 'ö' should be replaced with 'oe'
   */
  @Test
  public void testReplaceGermanSpecialSign4() {
    String inputName = "DFES__xCls$%^Healö___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("ö is replaced with oe", "DFES__xCls_Healoe___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * Replace German Special Sign 'Ö' should be replaced with 'Oe'
   */
  @Test
  public void testReplaceGermanSpecialSign5() {
    String inputName = "DFES__xCls$%^HealÖ___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("Ö is replaced with Oe", "DFES__xCls_HealOe___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * Replace German Special Sign 'ä' should be replaced with 'ae'
   */
  @Test
  public void testReplaceGermanSpecialSign6() {
    String inputName = "DFES__xCls$%^Healä___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("ä is replaced with ae", "DFES__xCls_Healae___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * Replace German Special Sign 'Ä' should be replaced with 'Ae'
   */
  @Test
  public void testReplaceGermanSpecialSign7() {
    String inputName = "DFES__xCls$%^HealÄ___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("Ä is replaced with Ae", "DFES__xCls_HealAe___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * If the input has multiple underscore then it will be maintained
   */
  @Test
  public void testUnderscore() {
    String inputName = "DFES__xCls$%^Heal___Trg_CA";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("Underscore is retained", "DFES__xCls_Heal___Trg_CA", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }

  /**
   * Replace German Special Sign 'Ä' should be replaced with 'Ae'
   */
  @Test
  public void testReplaceBrackets() {
    String inputName = "DFES_xClsHealTrg_CA(Bosch)";
    A2lObjectIdentifierValidator validator = new A2lObjectIdentifierValidator();
    BitSet result;
    String modifiedName = validator.replaceInvalidChars(inputName);
    assertEquals("( is replaced with [", "DFES_xClsHealTrg_CA[Bosch]", modifiedName);
    result = validator.isValidName(modifiedName);
    assertEquals(NO_VALIDATION_FAILED, 0, result.cardinality());
  }
}

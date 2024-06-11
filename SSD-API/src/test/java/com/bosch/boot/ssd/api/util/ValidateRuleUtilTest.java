/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author TUD1COB
 */
public class ValidateRuleUtilTest {

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase7() {
    byte bytenum = 7;
    String expected = "IF";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase8() {
    byte bytenum = 8;
    String expected = "CASE";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase36() {
    byte bytenum = 36;
    String expected = "LBYTE";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase35() {
    byte bytenum = 35;
    String expected = "HBYTE";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase23() {
    byte bytenum = 23;
    String expected = "BIT";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase24() {
    byte bytenum = 24;
    String expected = "LBIT";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase25() {
    byte bytenum = 25;
    String expected = "HBIT";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase26() {
    byte bytenum = 26;
    String expected = "LBIT32";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase27() {
    byte bytenum = 27;
    String expected = "HBIT32";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase15() {
    byte bytenum = 15;
    String expected = "SRC";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase16() {
    byte bytenum = 16;
    String expected = "MAP";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase32() {
    byte bytenum = 32;
    String expected = "AXISCMP";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase30() {
    byte bytenum = 30;
    String expected = "GMAP";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase31() {
    byte bytenum = 31;
    String expected = "FMAP";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase28() {
    byte bytenum = 28;
    String expected = ValidationConstants.VALVERB;
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase33() {
    byte bytenum = 33;
    String expected = "VBLKVERB";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase69() {
    byte bytenum = 69;
    String expected = "ASCMP";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase13() {
    byte bytenum = 13;
    String expected = "FINCR";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase14() {
    byte bytenum = 14;
    String expected = "FDECR";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase18() {
    byte bytenum = 18;
    String expected = "VBLK";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase29() {
    byte bytenum = 29;
    String expected = "MAPCMP";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase34() {
    byte bytenum = 34;
    String expected = "DINHCHECK";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase63() {
    byte bytenum = 63;
    String expected = "BITVBLK";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordCase68() {
    byte bytenum = 68;
    String expected = "ERROR";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetLabelRuleKeyWordDefault() {
    byte bytenum = 100;
    String expected = "";
    String result = ValidateRuleUtil.getLabelRuleKeyWord(bytenum);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseIF() {
    String msg = "IF";
    String expected = "IF";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseCASE() {
    String msg = "CASE";
    String expected = "CASE";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseLBYTE() {
    String msg = "LBYTE";
    String expected = "LBYTE";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordDefault() {
    String msg = "UNKNOWN";
    String expected = "";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseMPMIN() {
    String msg = "MPMIN";
    String expected = "MPMIN";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseMPMAX() {
    String msg = "MPMAX";
    String expected = "MPMAX";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseMINOF() {
    String msg = "MINOF";
    String expected = "MINOF";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseMAXOF() {
    String msg = "MAXOF";
    String expected = "MAXOF";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseMAXAXISPTS() {
    String msg = "MAXAXISPTS";
    String expected = "MAXAXISPTS";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseAXISVAL() {
    String msg = "AXISVAL";
    String expected = "AXISVAL";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseNUM() {
    String msg = "NUM";
    String expected = "NUM";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetKeywordCaseVERSION() {
    String msg = "VERSION";
    String expected = "VERSION";
    String result = ValidateRuleUtil.getKeyword(msg);
    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testHBYTE() {
    String labelRulKwyWrd = "HBYTE";
    assertEquals("HBYTE", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testBIT() {
    String labelRulKwyWrd = "BIT";
    assertEquals("BIT", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testLBIT() {
    String labelRulKwyWrd = "LBIT";
    assertEquals("LBIT", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testHBIT() {
    String labelRulKwyWrd = "HBIT";
    assertEquals("HBIT", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testLBIT32() {
    String labelRulKwyWrd = "LBIT32";
    assertEquals("LBIT32", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testHBIT32() {
    String labelRulKwyWrd = "HBIT32";
    assertEquals("HBIT32", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testSRC() {
    String labelRulKwyWrd = "SRC";
    assertEquals("SRC", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testMAP() {
    String labelRulKwyWrd = "MAP";
    assertEquals("MAP", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testAXISCMP() {
    String labelRulKwyWrd = "AXISCMP";
    assertEquals("AXISCMP", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testGMAP() {
    String labelRulKwyWrd = "GMAP";
    assertEquals("GMAP", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testFMAP() {
    String labelRulKwyWrd = "FMAP";
    assertEquals("FMAP", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testVALVERB() {
    String labelRulKwyWrd = "VALVERB";
    assertEquals("VALVERB", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testVBLKVERB() {
    String labelRulKwyWrd = "VBLKVERB";
    assertEquals("VBLKVERB", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testASCMP() {
    String labelRulKwyWrd = "ASCMP";
    assertEquals("ASCMP", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testFINCR() {
    String labelRulKwyWrd = "FINCR";
    assertEquals("FINCR", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testFDECR() {
    String labelRulKwyWrd = "FDECR";
    assertEquals("FDECR", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testVBLK() {
    String labelRulKwyWrd = "VBLK";
    assertEquals("VBLK", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testMAPCMP() {
    String labelRulKwyWrd = "MAPCMP";
    assertEquals("MAPCMP", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testDINHCHECK() {
    String labelRulKwyWrd = "DINHCHECK";
    assertEquals("DINHCHECK", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testBITVBLK() {
    String labelRulKwyWrd = "BITVBLK";
    assertEquals("BITVBLK", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }

  /**
   *
   */
  @Test
  public void testERROR() {
    String labelRulKwyWrd = "ERROR";
    assertEquals("ERROR", ValidateRuleUtil.getKeyword(labelRulKwyWrd));
  }
}

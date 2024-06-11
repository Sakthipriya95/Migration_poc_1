/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author TUD1COB
 */
public class ValidationConstantsTest {

  /**
   * Test Constants
   */
  @Test
  public void testConstants() {
    Assert.assertEquals("CTRLFOR", ValidationConstants.CTRLFOR);
    Assert.assertEquals("SCMP", ValidationConstants.SCMP);
    Assert.assertEquals("LN", ValidationConstants.LN);
    Assert.assertEquals("INT", ValidationConstants.INT);
    Assert.assertEquals("EXP", ValidationConstants.EXP);
    Assert.assertEquals("MINMP", ValidationConstants.MINMP);
    Assert.assertEquals("MAXMP", ValidationConstants.MAXMP);
    Assert.assertEquals("LOW", ValidationConstants.LOW);
    Assert.assertEquals("HIGH", ValidationConstants.HIGH);
    Assert.assertEquals("INCR", ValidationConstants.INCR);
    Assert.assertEquals("DECR", ValidationConstants.DECR);
    Assert.assertEquals("MIN", ValidationConstants.MIN);
    Assert.assertEquals("MAX", ValidationConstants.MAX);
    Assert.assertEquals("BITQ", ValidationConstants.BITQ);
    Assert.assertEquals("ASCMP", ValidationConstants.ASCMP);
    Assert.assertEquals("ABS", ValidationConstants.ABS);
    Assert.assertEquals("BIT", ValidationConstants.BIT);
    Assert.assertEquals("NUM", ValidationConstants.NUM);
    Assert.assertEquals("Simple syntax check result", ValidationConstants.VALIDATION_RESULT);
    Assert.assertEquals("LBIT", ValidationConstants.LBIT);
    Assert.assertEquals("HBIT", ValidationConstants.HBIT);
    Assert.assertEquals("LBIT32", ValidationConstants.LBIT32);
    Assert.assertEquals("HBIT32", ValidationConstants.HBIT32);
    Assert.assertEquals("MINOF", ValidationConstants.MINOF);
    Assert.assertEquals("MAXOF", ValidationConstants.MAXOF);
    Assert.assertEquals("FINCR", ValidationConstants.FINCR);
    Assert.assertEquals("FDECR", ValidationConstants.FDECR);
    Assert.assertEquals("In Line No ", ValidationConstants.IN_LINE_NUMBER);
    Assert.assertEquals(" Improper syntax found ", ValidationConstants.IMPROPER_SYNTAX_FOUND);
    Assert.assertEquals("\nProper Syntax for ", ValidationConstants.PROPER_SYNTAX);
    Assert.assertEquals("The label \"", ValidationConstants.LABEL);
    Assert.assertEquals(" is not analysable\n", ValidationConstants.NOT_ANLAYSABLE);
    Assert.assertEquals("MAXAXISPTS", ValidationConstants.MAXAXISPTS);
    Assert.assertEquals("SRC", ValidationConstants.SRC);
    Assert.assertEquals("MAP", ValidationConstants.MAP);
    Assert.assertEquals("MPMIN", ValidationConstants.MPMIN);
    Assert.assertEquals("MPMAX", ValidationConstants.MPMAX);
    Assert.assertEquals("GMAP", ValidationConstants.GMAP);
    Assert.assertEquals("FMAP", ValidationConstants.FMAP);
    Assert.assertEquals("MAPCMP", ValidationConstants.MAPCMP);
    Assert.assertEquals("AXISCMP", ValidationConstants.AXISCMP);
    Assert.assertEquals("AXISVAL", ValidationConstants.AXISVAL);
    Assert.assertEquals("BITVBLK", ValidationConstants.BITVBLK);
    Assert.assertEquals("VALVERB", ValidationConstants.VALVERB);
    Assert.assertEquals("VBLKVERB", ValidationConstants.VBLKVERB);
    Assert.assertEquals("FIDCHECK", ValidationConstants.FIDCHECK);
    Assert.assertEquals("ARRAY", ValidationConstants.ARRAY);
    Assert.assertEquals("MATRIX", ValidationConstants.MATRIX);
    Assert.assertEquals("VBLK", ValidationConstants.VBLK);
    Assert.assertEquals("VERSION", ValidationConstants.VERSION);
    Assert.assertEquals("IF", ValidationConstants.IF);
    Assert.assertEquals("CASE", ValidationConstants.CASE);
    Assert.assertEquals("IFRULE", ValidationConstants.IFRULE);
    Assert.assertEquals("CASERULE", ValidationConstants.CASERULE);
    Assert.assertEquals("VBLK", ValidationConstants.VBLKRULE);
    Assert.assertEquals("CTRLIF", ValidationConstants.CTRLIF);
    Assert.assertEquals("CTRLCASE", ValidationConstants.CTRLCASE);
    Assert.assertEquals("DINHCHECK", ValidationConstants.DINHCHECK);
    Assert.assertEquals(" but number of values available in array is ", ValidationConstants.ARRAYSIZE);
    Assert.assertEquals("CTRLUSECASECONTACT", ValidationConstants.CTRLUSECASECONTACT);
    Assert.assertEquals("FINCR", ValidationConstants.FINCRRULE);
    Assert.assertEquals("FDECR", ValidationConstants.FDECRRULE);
    Assert.assertEquals(258, ValidationConstants.CASEERR_INT);
    Assert.assertEquals("CHECKWINDOW", ValidationConstants.CHECKWINDOW);
  }
}


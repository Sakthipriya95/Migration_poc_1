/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;


/**
 * ValidateRuleUtil
 *
 * @author SMN6KOR
 */
public class ValidateRuleUtil {


  private ValidateRuleUtil() {
    // default private constructor
  }

  /**
   * @param bytenum byte
   * @return String
   */
  public static String getLabelRuleKeyWord(final Byte bytenum) {
    String labelRulKwyWrd = "";

    switch (bytenum) {
      case 7:
        labelRulKwyWrd = "IF";
        break;
      case 8:
        labelRulKwyWrd = "CASE";
        break;
      case 36:
        labelRulKwyWrd = ValidationConstants.LBYTE;
        break;
      case 35:
        labelRulKwyWrd = ValidationConstants.HBYTE;
        break;
      case 23:
        labelRulKwyWrd = "BIT";
        break;
      case 24:
        labelRulKwyWrd = "LBIT";
        break;
      case 25:
        labelRulKwyWrd = "HBIT";
        break;
      case 26:
        labelRulKwyWrd = ValidationConstants.LBIT32;
        break;
      case 27:
        labelRulKwyWrd = ValidationConstants.HBIT32;
        break;
      case 15:
        labelRulKwyWrd = "SRC";
        break;
      case 16:
        labelRulKwyWrd = "MAP";
        break;
      case 32:
        labelRulKwyWrd = ValidationConstants.AXISCMP;
        break;
      case 30:
        labelRulKwyWrd = "GMAP";
        break;
      case 31:
        labelRulKwyWrd = "FMAP";
        break;
      case 28:
        labelRulKwyWrd = ValidationConstants.VALVERB;
        break;
      case 33:
        labelRulKwyWrd = ValidationConstants.VBLKVERB;
        break;
      case 69:
        labelRulKwyWrd = ValidationConstants.ASCMP;
        break;
      case 13:
        labelRulKwyWrd = ValidationConstants.FINCR;
        break;
      case 14:
        labelRulKwyWrd = ValidationConstants.FDECR;
        break;
      case 18:
        labelRulKwyWrd = "VBLK";
        break;
      case 29:
        labelRulKwyWrd = ValidationConstants.MAPCMP;
        break;
      case 34:
        labelRulKwyWrd = ValidationConstants.DINHCHECK;
        break;
      // CSSD-97
      case 63:
        labelRulKwyWrd = ValidationConstants.BITVBLK;
        break;
      case 68:
        labelRulKwyWrd = ValidationConstants.ERROR;
        break;
      default:
        break;
    }

    return labelRulKwyWrd;
  }

  /**
   * @param msg String
   * @return String
   */
  public static String getKeyword(final String msg) {
    String labelRulKwyWrd = null;

    switch (msg) {
      case "IF":
        labelRulKwyWrd = "IF";
        break;
      case "CASE":
        labelRulKwyWrd = "CASE";
        break;
      case ValidationConstants.LBYTE:
        labelRulKwyWrd = ValidationConstants.LBYTE;
        break;
      case ValidationConstants.HBYTE:
        labelRulKwyWrd = ValidationConstants.HBYTE;
        break;
      case "BIT":
        labelRulKwyWrd = "BIT";
        break;
      case "LBIT":
        labelRulKwyWrd = "LBIT";
        break;
      case "HBIT":
        labelRulKwyWrd = "HBIT";
        break;
      case ValidationConstants.LBIT32:
        labelRulKwyWrd = ValidationConstants.LBIT32;
        break;
      case ValidationConstants.HBIT32:
        labelRulKwyWrd = ValidationConstants.HBIT32;
        break;
      case "SRC":
        labelRulKwyWrd = "SRC";
        break;
      case "MAP":
        labelRulKwyWrd = "MAP";
        break;
      case ValidationConstants.AXISCMP:
        labelRulKwyWrd = ValidationConstants.AXISCMP;
        break;
      case "GMAP":
        labelRulKwyWrd = "GMAP";
        break;
      case "FMAP":
        labelRulKwyWrd = "FMAP";
        break;
      case "VALVERB":
        labelRulKwyWrd = "VALVERB";
        break;
      case ValidationConstants.VBLKVERB:
        labelRulKwyWrd = ValidationConstants.VBLKVERB;
        break;
      case ValidationConstants.ASCMP:
        labelRulKwyWrd = ValidationConstants.ASCMP;
        break;
      case ValidationConstants.FINCR:
        labelRulKwyWrd = ValidationConstants.FINCR;
        break;
      case ValidationConstants.FDECR:
        labelRulKwyWrd = ValidationConstants.FDECR;
        break;
      case "VBLK":
        labelRulKwyWrd = "VBLK";
        break;
      case ValidationConstants.MAPCMP:
        labelRulKwyWrd = ValidationConstants.MAPCMP;
        break;
      case ValidationConstants.DINHCHECK:
        labelRulKwyWrd = ValidationConstants.DINHCHECK;
        break;
      case ValidationConstants.BITVBLK:
        labelRulKwyWrd = ValidationConstants.BITVBLK;
        break;
      case ValidationConstants.ERROR:
        labelRulKwyWrd = ValidationConstants.ERROR;
        break;
      case "MPMIN":
        labelRulKwyWrd = "MPMIN";
        break;
      case "MPMAX":
        labelRulKwyWrd = "MPMAX";
        break;
      case "MINOF":
        labelRulKwyWrd = "MINOF";
        break;
      case "MAXOF":
        labelRulKwyWrd = "MAXOF";
        break;
      case "MAXAXISPTS":
        labelRulKwyWrd = "MAXAXISPTS";
        break;
      case "AXISVAL":
        labelRulKwyWrd = "AXISVAL";
        break;
      case "NUM":
        labelRulKwyWrd = "NUM";
        break;
      case "VERSION":
        labelRulKwyWrd = "VERSION";
        break;
      default:
        labelRulKwyWrd = "";
        break;
    }

    return labelRulKwyWrd;
  }


}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

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
import com.bosch.checkssd.datamodel.rule.BitVBLKRule;
import com.bosch.checkssd.datamodel.rule.CaseRule;
import com.bosch.checkssd.datamodel.rule.GMapFMapRule;
import com.bosch.checkssd.datamodel.rule.IFRule;
import com.bosch.checkssd.datamodel.rule.MapCmpRule;
import com.bosch.checkssd.datamodel.rule.ValVerbRule;
import com.bosch.checkssd.datamodel.rule.VblkRule;
import com.bosch.checkssd.datamodel.rule.VblkVerbRule;

/**
 * ConstantFunctionsList
 *
 * @author SMN6KOR
 */
public class ConstantFunctionsList {

  /**
   * @param iMathTermElement element
   * @return String
   */
  public String getFunctionName(final IMathTermElement iMathTermElement) {
    String functionName = "";
    if (iMathTermElement instanceof MaxMinFunc) {
      functionName = "MINOF";
    }
    else if (iMathTermElement instanceof FIncrDecrFunc) {
      functionName = "FINCR";
    }
    else if (iMathTermElement instanceof NumFunc) {
      functionName = "NUM";
    }
    else if (iMathTermElement instanceof MaxAxsPtFunc) {
      functionName = "MAXAXISPTS";
    }
    else if (iMathTermElement instanceof SrcFunc) {
      functionName = "SRC";
    }
    else if (iMathTermElement instanceof MPMaxMinFunc) {
      functionName = "MPMIN";
    }

    else if (iMathTermElement instanceof AxsvalFunc) {
      functionName = "AXISVAL";
    }

    else if (iMathTermElement instanceof ArrayFunc) {
      functionName = "ARRAY";
    }
    else if (iMathTermElement instanceof VblkFunc) {
      functionName = "VBLK";
    }
    else if (iMathTermElement instanceof IFFunc) {
      functionName = "IF";
    }
    else if (iMathTermElement instanceof CaseFunc) {
      functionName = "CASE";
    }
    else {
      functionName = checkOtherFunctionNames(iMathTermElement);
    }
    return functionName;
  }

  /**
   * @param iMathTermElement
   * @return
   */
  private String checkOtherFunctionNames(final IMathTermElement iMathTermElement) {
    String functionName = "";
    if (iMathTermElement instanceof IFRule) {
      functionName = "IFRULE";
    }
    else if (iMathTermElement instanceof GMapFMapRule) {
      functionName = "GMAP";
    }
    else if (iMathTermElement instanceof MapCmpRule) {
      functionName = "MAPCMP";
    }
    else if (iMathTermElement instanceof BitVBLKRule) {
      functionName = "BITVBLK";
    }
    else if (iMathTermElement instanceof ValVerbRule) {
      functionName = "VALVERB";
    }
    else if (iMathTermElement instanceof VblkVerbRule) {
      functionName = "VBLKVERB";
    }
    else if (iMathTermElement instanceof CaseRule) {
      functionName = "CASERULE";
    }
    else if (iMathTermElement instanceof VblkRule) {
      functionName = "VBLKRULE";
    }
    return functionName;
  }

}

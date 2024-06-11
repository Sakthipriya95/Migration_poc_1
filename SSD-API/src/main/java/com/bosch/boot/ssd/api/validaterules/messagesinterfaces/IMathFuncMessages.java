/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.validaterules.messagesinterfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.boot.ssd.api.util.ValidationConstants;
import com.bosch.checkssd.datamodel.mathterm.IMathTermElement;
import com.bosch.checkssd.datamodel.util.SSDDataConstants;

/**
 * IMathFuncMessages
 *
 * @author SMN6KOR
 */
public interface IMathFuncMessages extends IPrintMessages {


  /**
   * @param functionName
   * @param mathFunction
   * @param errorNumber
   * @param iMathTermElement
   * @param lineNo
   * @param error
   */


  default void printMathFuncErrors(final String functionName, final IMathTermElement iMathTermElement, final int lineNo,
      final int errorNumber) {
    String funcName = functionName;
    if (funcName == null) {
      funcName = "default";
    }

    switch (funcName) {
      case ValidationConstants.SCMP:
      case ValidationConstants.LN:
      case ValidationConstants.INT:
      case ValidationConstants.EXP:
      case ValidationConstants.ABS:
      case ValidationConstants.LOW:
      case ValidationConstants.HIGH:
      case ValidationConstants.MINMP:
      case ValidationConstants.MAXMP:
      case ValidationConstants.INCR:
      case ValidationConstants.DECR:
      case ValidationConstants.MIN:
      case ValidationConstants.MAX:
      case ValidationConstants.BITQ:
      case ValidationConstants.ASCMP:
        if (errorNumber == SSDDataConstants.FP_UNBEKCHAR) {
          printMathFunctionErrors(funcName, iMathTermElement, lineNo, errorNumber);
        }
        break;
      default:
        break;
    }


  }

  /**
   * @param funcName
   * @param mathFunction
   * @param iMathTermElement
   * @param lineNo
   * @param errorNumber
   */
  default void printMathFunctionErrors(final String funcName, final IMathTermElement iMathTermElement, final int lineNo,
      final int errorNumber) {
    String errorMessage = getInstance().getExistingErrors();

    Map<String, String> lblAndTypeMap = getLabelNameAndType(iMathTermElement);

    if (lblAndTypeMap.size() == 1) {

      displayError(funcName, lineNo, errorMessage, lblAndTypeMap);

    }
    else if (lblAndTypeMap.size() > 1) {
      Map<String, String> wrongLblType = new HashMap<>();
      String expectedType = getCorrectLabelType(funcName);
      for (Entry<String, String> entry : lblAndTypeMap.entrySet()) {
        if (entry.getValue().contains(expectedType)) {
          lblAndTypeMap.remove(entry.getKey());
        }
        else {
          wrongLblType.put(entry.getKey(), entry.getValue());
        }
      }
      if (!wrongLblType.isEmpty()) {
        displayError(funcName, lineNo, errorMessage, wrongLblType);
      }
    }
    else {
      String description = ValidationConstants.IMPROPER_SYNTAX_FOUND + "for " + funcName +
          ValidationConstants.PROPER_SYNTAX + funcName + " :";
      setMessageDesc(
          getInstance().getExistingErrors() + ValidationConstants.IN_LINE_NUMBER + lineNo + ":" + description);
      getInstance().getLineNoAndError().put(lineNo, description);

      getMathFuncSyntax(funcName, errorNumber, lineNo);
    }

  }

  /**
   * @param funcName
   * @param lineNo
   * @param errorMessage
   * @param lblAndTypeMap
   */
  default void displayError(final String funcName, final int lineNo, final String errorMessage,
      final Map<String, String> lblAndTypeMap) {
    String labelName;
    for (Entry<String, String> entry : lblAndTypeMap.entrySet()) {
      labelName = entry.getKey();
      String labelType = lblAndTypeMap.get(labelName);
      if (labelType.isEmpty()) {
        labelType = " not appropriate";
      }
      String labelCanUse = "";
      labelCanUse = getLabelTypeName(funcName);
      String description = "\"" + labelName + "\"" + " is " + labelType + " label. In mathFunction " + funcName +
          ", only " + labelCanUse + " can be used as parameter! \n";
      setMessageDesc(errorMessage + ValidationConstants.IN_LINE_NUMBER + lineNo + " :" + description);
      getInstance().getLineNoAndError().put(lineNo, description);

    }
  }

  /**
   * @param funcName
   * @return - label type
   */
  default String getCorrectLabelType(final String funcName) {

    switch (funcName) {
      case ValidationConstants.LN:
      case ValidationConstants.INT:
      case ValidationConstants.EXP:
      case ValidationConstants.ABS:
      case ValidationConstants.LOW:
      case ValidationConstants.HIGH:
      case ValidationConstants.INCR:
      case ValidationConstants.DECR:
      case ValidationConstants.BITQ:
      case ValidationConstants.MIN:
      case ValidationConstants.MAX:
      case ValidationConstants.SCMP:
      case ValidationConstants.BIT:
        return "VALUE";
      case ValidationConstants.MINMP:
      case ValidationConstants.MAXMP:
        return "CURVE:MAP";
      default:
        break;
    }
    return "VALUE";
  }

  /**
   * @param funcName
   * @return
   */
  @SuppressWarnings("javadoc")
  default String getLabelTypeName(final String funcName) {
    String labelCanUse;

    if (funcName.equals(ValidationConstants.INCR) || funcName.equals(ValidationConstants.DECR)) {
      labelCanUse = "Value type labels/Constant";
    }
    else if (funcName.equals(ValidationConstants.MINMP) || funcName.equals(ValidationConstants.MAXMP)) {
      labelCanUse = "Map/Curve type labels";
    }
    else if (funcName.equals(ValidationConstants.BITQ) || funcName.equals(ValidationConstants.SCMP)) {
      labelCanUse = "Value type labels";
    }
    else {
      labelCanUse = "Value type labels/Constant/any mathTerm";
    }
    return labelCanUse;
  }

  /**
   * @param mathFunctionName
   * @param errorNumber
   * @param lineNo
   * @param mathFunction
   */
  default void getMathFuncSyntax(final String mathFunctionName, final int errorNumber, final int lineNo) {
    String errorMessage = getInstance().getExistingErrors();
    String currentSyntax = lineNo + "";
    switch (mathFunctionName) {
      case ValidationConstants.LN:
      case ValidationConstants.INT:
      case ValidationConstants.EXP:
      case ValidationConstants.ABS:
      case ValidationConstants.LOW:
      case ValidationConstants.HIGH:
        currentSyntax = currentSyntax + " KONSTANTE C36," + mathFunctionName +
            "(MathTerm) where MathTerm represents numerical values , mathematical functions and constants\n";
        break;
      case ValidationConstants.MINMP:
      case ValidationConstants.MAXMP:
        currentSyntax = currentSyntax + " KONSTANTE C36," + mathFunctionName +
            "(Label) where Label represents curve /Map type labels\n";
        break;
      case ValidationConstants.INCR:
        currentSyntax = currentSyntax + " KONSTANTE C36, INCR(Value-Type Label) \n" +
            " or \nKONSTANTE C36, INCR(Value-Type Label,number)\n number = 1, 2,..\n";
        break;
      case ValidationConstants.DECR:
        currentSyntax = currentSyntax + " KONSTANTE C36, DECR(Value-Type Label) \n" +
            " or \nKONSTANTE C36, DECR(Value-Type Label,number)\n number = 1, 2,.. \n";
        break;
      case ValidationConstants.MIN:
        currentSyntax = currentSyntax +
            " KONSTANTE C36, MIN(MathTerm1, MathTerm2) \n Where MathTerm can be value-type label , numerical number, constant\n";
        break;
      case ValidationConstants.MAX:
        currentSyntax = currentSyntax +
            " KONSTANTE C36, MAX(MathTerm1, MathTerm2) \n Where MathTerm can be value-type label , numerical number ,constant \n";
        break;
      case ValidationConstants.BITQ:
        currentSyntax = currentSyntax +
            " KONSTANTE C36, BITQ(Label ,Num) where Label represent Value Type label and Num should be greater than 0\n";
        break;
      case ValidationConstants.SCMP:
        currentSyntax = currentSyntax + " KONSTANTE C36, SCMP(Value-Type Label Name,\"TEST\") \n";
        break;
      default:
        break;

    }
    if (!errorMessage.contains(currentSyntax)) {
      setMessageDesc(errorMessage + currentSyntax);
      getInstance().getLineNoAndError().put(lineNo, "Invalid syntax found . Proper Syntax :" + currentSyntax);

    }


  }


}

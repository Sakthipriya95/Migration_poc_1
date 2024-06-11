/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.validaterules.messagesinterfaces;

import java.math.BigDecimal;

import com.bosch.boot.ssd.api.util.ValidationConstants;
import com.bosch.checkssd.datamodel.SSDStatement;
import com.bosch.checkssd.datamodel.constant.ConstMatrix;
import com.bosch.checkssd.datamodel.constant.ConstText;
import com.bosch.checkssd.datamodel.constant.ConstTextArray;
import com.bosch.checkssd.datamodel.constant.ConstTextMatrix;
import com.bosch.checkssd.datamodel.constant.ConstantArray;
import com.bosch.checkssd.datamodel.constantfunctions.MPMaxMinFunc;
import com.bosch.checkssd.datamodel.constantfunctions.SrcFunc;
import com.bosch.checkssd.datamodel.constantfunctions.VblkFunc;
import com.bosch.checkssd.datamodel.util.SSDDataConstants;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;

/**
 * IConstantFuncMessages
 *
 * @author SMN6KOR
 */
public interface IConstantFuncMessages extends IPrintMessages {

  /**
   * Method to handle the array and matrix rules and displays the errors
   *
   * @param stmt
   */
  default void handleArrayMatrixConstant(final SSDStatement stmt) {
    if (stmt != null) {

      if ((stmt instanceof ConstantArray) && (stmt.getError() == ReportMessages.P_CNTMTRX)) {
        handleConstantArray(stmt);
      }
      else if ((stmt instanceof ConstMatrix) && (stmt.getError() == ReportMessages.P_CNTMTRX)) {
        handleConstantMatrix(stmt);
      }
      else if (stmt instanceof ConstText) {
        handleConstText(stmt);
      }
      else if ((stmt instanceof ConstTextArray) && (stmt.getError() == ReportMessages.P_CNTMTRX)) {
        handleConstantTextArray(stmt);
      }
      else if ((stmt instanceof ConstTextMatrix) && (stmt.getError() == ReportMessages.P_CNTMTRX)) {
        handleConstantTextMatrix(stmt);
      }
    }
  }

  /**
   * Method to handle the constant function and displays the errors
   *
   * @param functionName
   * @param errorNumber
   * @param labelType
   * @param labelName
   * @param lineNo
   * @return String
   */
  default boolean printConstFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo) {
    boolean isConstFunction = false;
    String error = getInstance().getExistingErrors();
    switch (functionName) {
      case ValidationConstants.IF:
        isConstFunction = true;
        printIFFuncErrors(error, lineNo, errorNumber);
        break;
      case ValidationConstants.CASE:
        isConstFunction = true;
        printCASEFuncErrors(error, lineNo, errorNumber);
        break;
      case ValidationConstants.NUM:
        isConstFunction = true;
        printNumFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;

      case ValidationConstants.MAXAXISPTS:
        isConstFunction = true;
        printMaxAxisPtsErrors(functionName, errorNumber, labelName, labelType, lineNo, error);

        break;
      case ValidationConstants.MINOF:
      case ValidationConstants.MAXOF:
        isConstFunction = true;
        printMINOFMAXOFErrors(errorNumber, lineNo, error);
        break;
      case ValidationConstants.FINCR:
      case ValidationConstants.FDECR:
        isConstFunction = true;
        printFINCRDECRErrors(errorNumber, lineNo, error);
        break;

      case ValidationConstants.SRC:
        isConstFunction = true;
        printSrcConstFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.MAP:
        isConstFunction = true;
        printMAPFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.AXISVAL:
        isConstFunction = true;
        printAXISVALFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.VBLK:
        isConstFunction = true;
        printVBLKFuncErrors(errorNumber, lineNo, error);
        break;
      case ValidationConstants.ARRAY:
        isConstFunction = true;
        printArrayFuncErrors(error, lineNo, errorNumber);
        break;
      case ValidationConstants.MATRIX:
        isConstFunction = true;
        printMatrixFuncErrors(error, errorNumber, lineNo);
        break;
      case ValidationConstants.MPMIN:
      case ValidationConstants.MPMAX:
        isConstFunction = true;
        printMPMINMPMAXErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.VERSION:
        isConstFunction = true;
        setMessageDesc(error + " Proper syntax of VERSION is VERSION(functionname) \n");
        break;
      case "":
        isConstFunction = true;
        if (errorNumber == ReportMessages.P_TABTYPE) {
          setMessageDesc(error + "Wrong usage of label " + "\"" + labelName + "\"" +
              " Given syntax is not applicable for the label type  " + "\"" + labelType + "\"" + " label. \n");
          getInstance().getLineNoAndError().put(lineNo, "Wrong usgae of label " + "\"" + labelName + "\"" +
              " Given syntax is not applicable for the label type  " + "\"" + labelType + "\"" + " label. \n");
        }
        break;
      default:
        break;
    }
    return isConstFunction;
  }


  /**
   * Method to print the errors associated with SRC function
   *
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printSrcConstFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {

    String errorContent = error;
    String currentError = lineNo + ":";
    String properSyntax = "SRC syntax: SRC ( Curve/ Map label name ( Axis[Num] ) )  " + "where Axis= X or Y ," +
        "Num = 1,2,.last axis point \n Num should not be 0\n ";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
            functionName + ",Only curve and map type labels are allowed! \n";
        break;
      case ReportMessages.P_LABELNOTAVAILABLE:
        currentError = currentError + "Given label \"+labelName+\" is not available !";
        break;
      case ReportMessages.P_SRCENTRY:
        currentError = currentError + "Axis \"Z\" is not allowed in SRC fucntion . Axis should be X or Y \n ";
        break;
      case ReportMessages.P_SRCENTRY + 1:
        currentError =
            currentError + "Missing of index number ! or Missing of index specification \"[]\" !  \n " + properSyntax;
        break;
      case ReportMessages.P_SRCENTRY + 2:
        currentError = currentError + "Index number should not be 0 .It should be 1 or any postive number ! \n ";
        break;
      case ReportMessages.P_SRCENTRY + 3:
        currentError = currentError + " Missing of index specification \"[]\" ! \n " + properSyntax;
        break;
      case ReportMessages.P_SRCENTRY + 4:
        currentError = currentError + " Missing of axis specification !  \n " + properSyntax;
        break;
      case ReportMessages.P_FORMULA:
        currentError = currentError + " Missing of variable specification !  \n " + properSyntax;
        break;
      default:
        break;
    }
    if (!errorContent.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(errorContent + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }

  /**
   * Method to print the errors associated with MINOF/MAXOF function
   *
   * @param errorNumber
   * @param lineNo
   * @param error
   */
  default void printMINOFMAXOFErrors(final int errorNumber, final int lineNo, final String error) {
    String properSyntax = "Proper syntax: MINOF/MAXOF ( MathTerm -1, MathTerm-2, MathTerm-3, ....., MathTerm-n )\n ";
    String currentError = lineNo + ":";
    if (errorNumber >= ReportMessages.P_MINMAXERR) {
      currentError = ": In MINOF\\MAXOF function , Missing of parameter for MINOF/MAXOF function " + properSyntax;
    }
    else if (errorNumber == SSDDataConstants.FP_UNBEKCHAR) {
      currentError = ": In MINOF\\MAXOF function , " + "Possible error :\n" +
          "1.MINOF/MAXOF Parameter should be numerical values,variables,mathematical functions,constants or Value type label.\n" +
          " 2.Syntax is not ended properly\n";
    }
    if (!error.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(error + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }

  /**
   * Method to print the errors associated with FDECR/FDECR function
   *
   * @param lineNo
   * @param error
   * @param errorNumber
   * @param labelType
   * @param labelName
   */
  default void printFINCRDECRErrors(final int errorNumber, final int lineNo, final String error) {
    String currentError = lineNo + ":";
    String properSyntax = " \nFINCR/FDECR syntax:FINCR ( MathTerm,Value type Label , Num , UP/DOWN)\n" +
        "Note :\n 1.\"Num\" should be greater than 0 \n 2.Mathterm can be value type labels/Constant/numerical values \n";
    switch (errorNumber) {

      case ReportMessages.P_INDECERR + 3:
        currentError = currentError + "Value type label is missing ! \n" + properSyntax;
        break;
      case ReportMessages.P_INDECERR + 2:
        currentError = currentError +
            "Error :Label Variable defined is not proper .\n Label variable should be value type variable ! \n" +
            properSyntax;
        break;
      case SSDDataConstants.FP_UNBEKCHAR:
        currentError = currentError + "Only Value Type labels , constant are allowed in FINCR\\DECR \n" + properSyntax;
        break;
      case ReportMessages.P_INDECERR + 1:
        currentError = currentError + "MathTerm is missing !\n " + properSyntax;
        break;
      default:
        break;

    }
    if (!error.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(error + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }

  }

  /**
   * Method to print the errors associated with MaxAxisPts function
   *
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printMaxAxisPtsErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String errorMsg = error;
    String properSyntax =
        "\"MAXAXISPTS\" syntax : MAXAXISPTS ( curve/ map/axispts labelName ( Axis ) ) " + " Where Axis = X or Y\n ";
    String currentError = lineNo + ":";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
            functionName + ",Value type label /Value block labels are not allowed! \n";
        break;
      case ReportMessages.P_MAPFORM:
        currentError = currentError + "Label name specification is missing!.\n" + properSyntax;
        break;
      case ReportMessages.P_SRCCONLAB:
        currentError = "Possible errors :" + "1.Axis specification is missing or Axis specified is \"Z\"! \n" +
            "2.Usage of constant as Variable or Variable defined not available in LDB \n " + properSyntax;
        break;
      default:
        break;
    }
    if (!errorMsg.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(errorMsg + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }

  }

  /**
   * Method to print the errors associated with MaP function
   *
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printMAPFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String properSyntax = "\nMAP syntax: MAP (Map/Curve Variable (x-Mathterm, y-Mathterm))" +
        " where x/y -Mathterm represents numerical values , mathematical functions and constants\nNote : y-Mathterm is not required for Curve label";
    String err = error;
    String currentError = lineNo + ":";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
            functionName + ", only MAP/CURVE type label are allowed! \n";
        break;
      case ReportMessages.P_MAPFORM + 1:
        currentError = currentError + "No parameter found for MAP function. \n" + properSyntax;
        break;
      case ReportMessages.P_MAPCONLAB:

        currentError = currentError + "MathTerm is not proper!" + properSyntax;
        break;
      default:
        break;

    }
    if (!err.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(err + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }

  /**
   * Method to print the errors associated with MPMIN /MPMAX function
   *
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printMPMINMPMAXErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String errMessage = error;
    String properSyntax =
        "Proper syntax: MPMAX/MPMIN ( Variable ( X-Range, Y-Range ) )for all /all maps.\n Note: Y-Range is not required for Curve labels";
    String currentError = lineNo + ":";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
            "MPMIN/MPMAX/MAP" + ", only MAP/CURVE/VBLK type label are allowed! \n";
        break;
      case 893:
      case ReportMessages.P_MAPFORM + 1:
        currentError = currentError + "No parameter found for function.\n";
        break;
      case ReportMessages.P_MAPCONLAB:
        currentError =
            currentError + "Label specified is not correct! " + "Only curve and Map is allowed in this function\n";
        break;
      case ReportMessages.P_MAPFORM + 3:
        currentError = currentError + "Y-Range is not specified for MAP labels! \n" + properSyntax;
        break;
      case ReportMessages.P_MAPFORM + 4:
        currentError = currentError + "Y-Range is not applicable for curve labels! \n" + properSyntax;

        break;
      case ReportMessages.P_MAPFORM + 2:
        currentError = currentError + "X-Range is missing / not proper! " +
            "X-Range can be numerical values,variables,mathematical functions,constants or Value type label or keyword \"ALL\" \n ";
        break;
      default:
        break;

    }
    if (!errMessage.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(errMessage + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }


  /**
   * Method to print the errors associated with AXISVAL function
   *
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printAXISVALFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String content = error;
    String properSyntax =
        "AXISVAL Syntax: AXISVAL ( Variable ( field-MathTerm, axis =  axis-Mathterm) )for all map \n" +
            "AXISVAL ( Variable ( field-MathTerm ) ) for all curves " + " where Axis= X or Y  \n ";
    String currentError = lineNo + ":";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
            functionName + ", only MAP/CURVE type label are allowed! \n";
        break;
      case ReportMessages.P_AXVALFORM + 2:
      case ReportMessages.P_AXVALFORM + 4:
        currentError = currentError + "Possible Errors :" +
            "1.Axis specification is missing! / Usage of Z as axis is not allowed \n" + properSyntax;
        break;
      case ReportMessages.P_AXVALFORM + 3:
        currentError = currentError + "Possible Errors : \n1.Axis-MathTerm is missing. \n" + properSyntax;
        break;
      case ReportMessages.P_AXVALFORM:
        currentError = currentError + "Error: Variable is Missing !\n" + properSyntax;
        break;
      case ReportMessages.P_AXVALFORM + 5:
        currentError = currentError + "Error : Axis-MathTerm is not proper.  \n" + properSyntax;
        break;
      case ReportMessages.P_AXVALFORM + 1:
        currentError = currentError + "Error : Field-MathTerm is not proper.  \n " + properSyntax;
        break;
      default:
        break;
    }
    if (!content.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(content + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }


  /**
   * Method to print the errors associated with VBLK function
   *
   * @param errorNumber
   * @param lineNo
   * @param error
   */
  default void printVBLKFuncErrors(final int errorNumber, final int lineNo, final String error) {
    String err = error;
    String currentError = lineNo + ":";
    String properSyntax = "  VBLK syntax: VBLK ( Variable, x ) for array of values / VBLK labels \n " +
        "NOTE :x = 1 ... max = position in the array \n ";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "Only for VBLK labels allowed in \"VBLK\" function . ";
        break;
      case ReportMessages.P_VBLKNUM:
        currentError = currentError + " Possible Errors  : " + "1.Missing of \"x\" specification in the rule! \n " +
            "2.Value of \"x\" is  0 or negative number!\n" + properSyntax;
        break;
      case ReportMessages.P_VALBLK:
        currentError =
            currentError + "Variable is not specified  or Specified varible is not proper .\n" + properSyntax;
        break;
      default:
        break;
    }
    if (!err.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(err + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }

  /**
   * Method to print the errors associated with VBLK function
   *
   * @param error
   * @param lineNo
   * @param errorNo
   */
  default void printCASEFuncErrors(final String error, final int lineNo, final int errorNo) {
    String err = error;
    String currentError = lineNo + ":";

    String properSyntax = "CASE Syntax:CASE (Variable (Value-1 : MathTerm-1, ..., Value-X : MathTerm-X) )\n" +
        " Note : Variable and Value: Mathterm pair are mandatory for CASE function   \n";
    switch (errorNo) {
      case ReportMessages.P_CASELABEL:
        currentError = "Possible Errors :\n 1.Missing of Variable  in CASE rule !\n" +
            "2.Variable - should be value type label or a constant. \n" +
            "3.It is compulsory to have one Value-x : mathterm pair as parameter of case \n" + properSyntax;
        break;
      case ReportMessages.P_LABELNOTAVAILABLE:
        currentError =
            "Error : Label specified in CASE rule is not available in LDB \n" + "Make sure of Syntax !" + properSyntax;
        break;
      case ReportMessages.P_CASEFORM + 1:
        currentError = properSyntax;
        break;
      default:
        break;
    }
    if (!err.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(err + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError);

    }
  }


  /**
   * Method to print the errors associated with IF function
   *
   * @param error
   * @param lineNo
   * @param errorNumber
   */
  default void printIFFuncErrors(final String error, final int lineNo, final int errorNumber) {
    if (errorNumber > 0) {
      String currentError =
          " Make sure IF syntax is correct" + "IF syntax: IF ( condition, True-MathTerm , False-MathTerm) \n" +
              "Note :1.condition should be  numerical values evaluation.  \n " +
              "2.All paramter of IF function are mandatory\n";
      setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + lineNo + ':' + currentError);
      getInstance().getLineNoAndError().put(lineNo, error + currentError);

    }
  }


  /**
   * Method to print the errors associated with NUM function
   *
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printNumFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String errorMsg = error;
    String currentError = lineNo + ":";
    String properSyntax = " Proper Syntax : NUM ( curve / map / axispts label name ( Axis ) ) /\n " +
        "NUM ( Variable ) for VAL_BLK/MEASUREMENT \nWhere Axis = X or Y\n";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
            functionName + ",Value type label  are not allowed! \n";
        break;
      case ReportMessages.P_VALBLK:
        currentError = currentError +
            " Axis specification is missing or specified Axis is wrong or Variable is missing! \n" + properSyntax;
        break;
      case ReportMessages.P_MAPFORM:
        currentError = currentError + "Label name is missing! \n" + properSyntax;
        break;
      case ReportMessages.P_SRCCONLAB:
      case ReportMessages.P_LABELNOTAVAILABLE:
        currentError = currentError + "Label specified in NUM rule is not available in LDB !\n";
        break;
      default:
        break;
    }
    if (!errorMsg.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(errorMsg + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }

  /**
   * Method to print the errors associated with ARRAY function
   *
   * @param error
   * @param lineNo
   * @param errorNumber
   */
  default void printArrayFuncErrors(final String error, final int lineNo, final int errorNumber) {
    String errorMsg = error;
    String lineNoText = ValidationConstants.IN_LINE_NUMBER + lineNo + " :";
    switch (errorNumber) {
      case ReportMessages.P_CNTMTRX:
        setMessageDesc(
            errorMsg + lineNoText + " In \"ARRAY\" function , values given is lesser than values specified !  \n");
        getInstance().getLineNoAndError().put(lineNo,
            " In \"ARRAY\" function , values given is lesser than values specified !  \n");
        break;
      case ReportMessages.P_CNTMTRX + 1:
        setMessageDesc(
            errorMsg + lineNoText + " In \"ARRAY\" function , values given is greater than values specified !  \n");
        getInstance().getLineNoAndError().put(lineNo,
            " In \"ARRAY\" function , values given is greater than values specified !  \n");

        break;
      case ReportMessages.P_MTRXLABEL:
        setMessageDesc(
            errorMsg + lineNoText + "Array Syntax: ARRAY ( ArrayName ( x ) ) where \"x\" = 1 to max array position." +
                "Error :\"x\" is missing !  \n");
        getInstance().getLineNoAndError().put(lineNo,
            "Array Syntax: ARRAY ( ArrayName ( x ) ) where \"x\" = 1 to max array position." +
                "Error :\"x\" is missing !  \n");

        break;
      case ReportMessages.P_INVALMTRX:
        setMessageDesc(errorMsg + lineNoText + "Array function trying to fetch value from non-exist index!");
        getInstance().getLineNoAndError().put(lineNo, "Array function trying to fetch value from non-exist index!");
        break;
      case ReportMessages.P_EVALARRY:
        setMessageDesc(errorMsg + lineNoText + "Array defined in Array function is empty!");
        getInstance().getLineNoAndError().put(lineNo, "Array defined in Array function is empty!");
        break;
      case ReportMessages.P_TABTYPE:
        setMessageDesc(errorMsg + lineNoText + "Array defined in Array function is not exist !");
        getInstance().getLineNoAndError().put(lineNo, "Array defined in Array function is not exist !");

        break;
      default:
        break;
    }
  }


  /**
   * Method to print the errors associated with MATRIX function
   *
   * @param error
   * @param errorNumber
   * @param lineNo
   */
  default void printMatrixFuncErrors(final String error, final int errorNumber, final int lineNo) {

    String errorMsg = error + ValidationConstants.IN_LINE_NUMBER + lineNo + " :";
    switch (errorNumber) {
      case ReportMessages.P_CNTMTRX:
        setMessageDesc(errorMsg + " In \"Matrix\" function ,values given is lesser than values specified !  \n");
        getInstance().getLineNoAndError().put(lineNo,
            " In \"Matrix\" function ,values given is lesser than values specified !  \n");

        break;
      case ReportMessages.P_CNTMTRX + 1:
        setMessageDesc(errorMsg + " In \"Matrix\" function ,values given is greater than values specified !  \n");
        getInstance().getLineNoAndError().put(lineNo,
            " In \"Matrix\" function ,values given is greater than values specified !  \n");

        break;
      case ReportMessages.P_MTRXLABEL:
        setMessageDesc(errorMsg +
            "Matrix Syntax:  MATRIX ( MatrixName/ TextMatrixName ( x, y ) ) where \"x\" and \"y\" = 1 to max array position." +
            "Error :\"x\" is missing !  \n");
        getInstance().getLineNoAndError().put(lineNo,
            "Matrix Syntax:  MATRIX ( MatrixName/ TextMatrixName ( x, y ) ) where \"x\" and \"y\" = 1 to max array position." +
                "Error :\"x\" is missing !  \n");

        break;
      case ReportMessages.P_INVALMTRX:
        setMessageDesc(errorMsg + "Matrix function trying to fetch value from non-exist indexes!");
        getInstance().getLineNoAndError().put(lineNo, "Matrix function trying to fetch value from non-exist indexes!");
        break;
      case ReportMessages.P_EVALMTRX:
        setMessageDesc(errorMsg + "Matrix defined in matrix function is empty!");
        getInstance().getLineNoAndError().put(lineNo, "Matrix defined in matrix function is empty!");

        break;
      case ReportMessages.P_FORMULA:
        setMessageDesc(errorMsg + "Matrix defined in Matrix function is not exist !");
        getInstance().getLineNoAndError().put(lineNo, "Matrix defined in Matrix function is not exist !");
        break;
      default:
        break;
    }


  }


  /**
   * Method to print the errors associated with TEXTMATRIX function
   *
   * @param stmt
   */

  default void handleConstantTextMatrix(final SSDStatement stmt) {
    String error = getInstance().getInstance().getExistingErrors();
    double xNum =
        Double.parseDouble(((ConstTextMatrix) stmt).getxNumber().getMathElement().get(0).getMathTermElement());
    double yNum =
        Double.parseDouble(((ConstTextMatrix) stmt).getyNumber().getMathElement().get(0).getMathTermElement());
    double totNum = xNum * yNum;
    if ((stmt.getError() == ReportMessages.P_CNTMTRX) ||
        (BigDecimal.valueOf(totNum) != BigDecimal.valueOf(((ConstTextMatrix) stmt).getTempValList().size()))) {
      getInstance().setError(true);
      String errorContent = ", Matrix size specified is  " +
          ((ConstTextMatrix) stmt).getxNumber().getMathElement().get(0).getMathTermElement() + ',' +
          ((ConstTextMatrix) stmt).getyNumber().getMathElement().get(0).getMathTermElement() +
          ValidationConstants.ARRAYSIZE + ((ConstTextMatrix) stmt).getTempValList().size() +
          "\n NOTE:\n In TEXTMATRIX , only textual values should be given as matrix values.\n The numbers and the values in an array or a matrix are separated by the comma ','\n";
      setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + stmt.getLineNo() + errorContent);
      getInstance().getLineNoAndError().put(stmt.getLineNo(), errorContent);

    }
  }


  /**
   * Method to print the errors associated with TEXTARRAY function
   *
   * @param stmt
   */


  default void handleConstantTextArray(final SSDStatement stmt) {
    String error = getInstance().getExistingErrors();
    String errorContent = ", Array size specified is  " +
        ((ConstTextArray) stmt).getArrSize().getMathElement().get(0).getMathTermElement() +
        ValidationConstants.ARRAYSIZE + ((ConstTextArray) stmt).getTempValList().size() + "\n NOTE:\n In TEXTARRAY , " +
        "only textual values should be given as array values.\nThe numbers and the values in an array or a matrix are separated by the comma ','\n";
    getInstance().getLineNoAndError().put(stmt.getLineNo(), errorContent);

    setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + stmt.getLineNo() + errorContent);
  }


  /**
   * Method to print the errors associated with CONSTTEXT function
   *
   * @param stmt
   */


  default void handleConstText(final SSDStatement stmt) {
    int errorNumber = 0;
    if (stmt.getError() > 0) {
      errorNumber = stmt.getError();
    }
    else if ((((ConstText) stmt).getConstFunc() != null) && (((ConstText) stmt).getConstFunc().getErr() > 0)) {
      errorNumber = ((ConstText) stmt).getConstFunc().getErr();
    }
    String funcName = "";
    /*
     * Checks whether stmt is VBLK function and errornumber is greater than 0
     */
    if ((((ConstText) stmt).getConstFunc() instanceof VblkFunc) && (errorNumber > 0)) {
      getInstance().setError(true);
      setMessageDesc(getInstance().getExistingErrors());
      funcName = ValidationConstants.VBLK;
      printConstFuncErrors(ValidationConstants.VBLK, errorNumber, "", "", stmt.getLineNo());
    }
    /*
     * Checks whether stmt is MPMaxMinFunc function and errornumber is greater than 0
     */
    if ((((ConstText) stmt).getConstFunc() instanceof MPMaxMinFunc) && (errorNumber > 0)) {
      getInstance().setError(true);
      setMessageDesc(getInstance().getExistingErrors());
      funcName = ValidationConstants.MPMIN;
      printConstFuncErrors(ValidationConstants.MPMIN, errorNumber, "", "", stmt.getLineNo());
    }
    /*
     * Checks whether stmt is SrcFunc function and errornumber is greater than 0
     */
    if ((((ConstText) stmt).getConstFunc() instanceof SrcFunc) && (errorNumber > 0)) {
      getInstance().setError(true);
      setMessageDesc(getInstance().getExistingErrors());
      funcName = ValidationConstants.SRC;

    }

    printConstFuncErrors(funcName, errorNumber, "", "", stmt.getLineNo());
  }


  /**
   * Method to print the errors associated with constant matrix function
   *
   * @param stmt
   */


  default void handleConstantMatrix(final SSDStatement stmt) {
    String error = getInstance().getExistingErrors();
    String errorContent = ", Matrix size specified is  " +
        ((ConstMatrix) stmt).getxNumber().getMathElement().get(0).getMathTermElement() + ',' +
        ((ConstMatrix) stmt).getyNumber().getMathElement().get(0).getMathTermElement() + ValidationConstants.ARRAYSIZE +
        ((ConstMatrix) stmt).getTempValList().size() +
        "\n NOTE:\n In Matrix , only integer/double values should be given as matrix values . \n In TEXTMATRIX , " +
        "only textual values should be given as matrix values.\n The numbers and the values in an array or a matrix are separated by the comma ','\n";
    setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + stmt.getLineNo() + errorContent);
    getInstance().getLineNoAndError().put(stmt.getLineNo(), errorContent);

  }


  /**
   * Method to print the errors associated with constant array function
   *
   * @param stmt
   */


  default void handleConstantArray(final SSDStatement stmt) {
    String error = getInstance().getExistingErrors();
    String errorContent = ", Array size specified is  " +
        ((ConstantArray) stmt).getxNumber().getMathElement().get(0).getMathTermElement() +
        ValidationConstants.ARRAYSIZE + ((ConstantArray) stmt).getTempValList().size() +
        "\n NOTE:\n In ARRAY , only integer/doubl values should be given as array values ." +
        "\nThe numbers and the values in an array or a matrix are separated by the comma ','\n";
    setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + stmt.getLineNo() + errorContent);
    getInstance().getLineNoAndError().put(stmt.getLineNo(), errorContent);
  }


}

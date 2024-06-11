/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.validaterules.messagesinterfaces;


import com.bosch.boot.ssd.api.util.ValidationConstants;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;

/**
 * ILblFuncMessages
 *
 * @author SMN6KOR
 */
public interface ILblFuncMessages extends IPrintMessages {


  /**
   * @param functionName
   * @param errorNumber
   * @param labelType
   * @param labelName
   * @param lineNo
   * @return status of label function handling
   */

  default boolean printLblFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo) {
    boolean isLblFunction = false;
    String error = getInstance().getExistingErrors();
    switch (functionName) {

      case ValidationConstants.IF:
        isLblFunction = true;
        printIFRULEErrors(error, lineNo, errorNumber);
        break;
      case ValidationConstants.CASE:
        isLblFunction = true;
        printCASERULEErrors(error, errorNumber, lineNo, labelName, labelType);
        break;
      case ValidationConstants.SRC:
        isLblFunction = true;
        printSRCRULEErrors(errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.BIT:
      case ValidationConstants.LBIT:
      case ValidationConstants.HBIT:
      case ValidationConstants.LBIT32:
      case ValidationConstants.HBIT32:
        isLblFunction = true;
        printBITFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.BITVBLK:
        isLblFunction = true;
        printBITVBLKFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.VALVERB:
        isLblFunction = true;
        printVALVERBFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.FINCRRULE:
      case ValidationConstants.FDECRRULE:
        isLblFunction = true;
        printFINCRFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.GMAP:
        isLblFunction = true;
        printGMAPFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.FMAP:
        isLblFunction = true;
        printFMAPFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.AXISCMP:
        isLblFunction = true;
        printAXISCMPFuncErrors(functionName, errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.VBLKRULE:
        isLblFunction = true;
        printVBLKRuleErrors(errorNumber, lineNo, error);
        break;
      case ValidationConstants.VBLKVERB:
        isLblFunction = true;
        printVBLKVERBFuncErrors(errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.MAP:
        isLblFunction = true;
        printMAPFuncErrors(errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.MAPCMP:
        isLblFunction = true;
        printMAPCMPFuncErrors(errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.DINHCHECK:
        isLblFunction = true;
        printDINHCHECKErrors(errorNumber, labelName, labelType, lineNo, error);
        break;
      case ValidationConstants.FIDCHECK:
        isLblFunction = true;
        printFIDCHECKFuncErrors(error, lineNo);
        break;
      case ValidationConstants.CHECKWINDOW:
        isLblFunction = true;
        printCHECKWindowErrors(error, errorNumber, labelName, labelType, lineNo);
        break;
      // case ValidationConstants.ASCMP:
      // isLblFunction = true
      // printASCMPFuncErrors(errorNumber, labelName, labelType, lineNo, error)
      // break
      case "":
        isLblFunction = true;
        if (errorNumber == ReportMessages.P_TABTYPE) {
          setMessageDesc(error + " Error in usage of label " + "\"" + labelName + "\"" +
              " Given syntax is not applicable for the label type  " + "\"" + labelType + "\"" + " label. \n");
        }
        break;
      default:
        break;
    }
    return isLblFunction;
  }


  /**
   * @param error
   * @param lineNo
   * @param labelType
   * @param labelName
   * @param errorNumber
   */
  default void printCHECKWindowErrors(final String error, final int errorNumber, final String labelName,
      final String labelType, final int lineNo) {
    String errorMsg = error;
    String currentMsg;

    if (errorNumber == ReportMessages.P_TABTYPE) {
      currentMsg = "\"" + labelName + "\"" + " is " + labelType + " type label. \n" +
          "Only Curve labels allowed for \"CheckWindow\" function \n ";
      if (!errorMsg.contains(currentMsg)) {
        setMessageDesc(errorMsg + ValidationConstants.IN_LINE_NUMBER + lineNo + ":" + currentMsg);
        getInstance().getLineNoAndError().put(lineNo, currentMsg);
      }
    }

  }

  /**
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printFINCRFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String currentMsg = lineNo + ":";
    String properSyntax = "Proper Syntax: FINCR ( MathTerm, Variable, Num, UP/DOWN )\n ";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentMsg = "\"" + labelName + "\"" + " is " + labelType + " type label. \n" +
            "Only Value type labels are allowed in \"FINCR/FDECR\"\n ";
        break;
      case ReportMessages.P_INDECERR + 1:
        currentMsg = " Error : MathTerm is missing ! " + properSyntax;
        break;
      case ReportMessages.P_INDECERR + 2:
        currentMsg = " Error : Rule's First Parameter is not proper/correct ! " + properSyntax;
        break;
      case ReportMessages.P_INDECERR + 3:
        currentMsg = " Error : Variable is missing or specified variable is not available in LDB ! " + properSyntax;
        break;
      case ReportMessages.P_INDECERR + 4:
        currentMsg = " Error : second parameter is wrong ! It should be Value type label\n ";
        break;
      default:
        break;

    }
    if (!error.contains(currentMsg) && (currentMsg.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentMsg = currentMsg.substring(currentMsg.indexOf(':') + 1, currentMsg.length());
      setMessageDesc(error + lineNumberText + currentMsg);
      getInstance().getLineNoAndError().put(lineNo, currentMsg.replaceAll("\\n", ""));

    }
  }

  /**
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printMAPFuncErrors(final int errorNumber, final String labelName, final String labelType,
      final int lineNo, final String error) {
    String errorMsg = error;
    String currentError = lineNo + ":";
    String mapProperSyntax = "MAP Syntax :" +
        "MAP ( Z-Comparison, X-Range, Y-Range) for all Map/Curve type labels \n Y-Range is not required for Curve type labels\n ";

    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " type label. \n" +
            "\"MAP\" function should be used only for CURVE, MAP , Axis points labels.\n ";
        break;
      case ReportMessages.P_MAPFORM + 3:
        currentError = currentError +
            "Y-Range is missing/Not proper ! Y-Range should be MathTerm (Constant/Value type label/Textual values/Keyword \"ALL\")\n" +
            mapProperSyntax;
        break;
      case ReportMessages.P_MAPFORM + 2:
        currentError = currentError +
            "X-Range is missing / Not proper! X-Range should be MathTerm (Constant/Value type label/Textual values/Keyword \"ALL\")\n" +
            mapProperSyntax;
        break;
      case ReportMessages.P_MAPFORM + 1:
        currentError = currentError +
            "Z-Comparsion of MAP rule should be MathTerm(Constant/Value type label/Math function/Textual values) !\n";
        break;
      case ReportMessages.P_MAPFORM + 4:
        currentError = currentError + "Function parameter is missing !\n " + mapProperSyntax;
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
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printMAPCMPFuncErrors(final int errorNumber, final String labelName, final String labelType,
      final int lineNo, final String error) {
    String errorMsg = error;
    String properSyntax = "    MAPCMP Syntax: " + "MAPCMP ( Z-Comparison-Map, X-Range, Y-Range ) for map\n" +
        "MAPCMP ( Z-Comparison-Curve/VBLK, X-Range ) for curve and VBLK labels respectively\n";
    String currentError = lineNo + ":";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " type label. \n" +
            "\"MAPCMP\" function should be used only for CURVE, MAP , Axis points labels.\n ";
        break;
      case ReportMessages.P_MAPFORM:
        currentError = currentError + "Z-Comparsion label in MAPCMP rule is not available in LDB\n " + properSyntax;
        break;
      case ReportMessages.P_MAPFORM + 2:
        currentError = currentError + "X-Range is not available in MAPCMP rule .\n" + properSyntax;
        break;
      case ReportMessages.P_MAPTYP + 1:
        currentError = currentError + "Z-Comparsion term in MAPCMP rule should be CURVE or MAP or VBLK label.\n";
        break;
      case ReportMessages.P_MAPFORM + 3:
        currentError = currentError + "Y-Range is missing !\n" + properSyntax;
        break;
      case ReportMessages.P_MAPFORM + 5:
        currentError = currentError + "MAP/Curve/VBLK label should have comparsion label same as its type\n";
        break;
      case ReportMessages.P_MAPCMP + 1:
        currentError = currentError + "Z-Comparsion term is not proper/misssing !\n" + properSyntax;
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
   * @param error
   * @param error2
   * @param lineNo
   * @param labelType
   * @param labelName
   * @param errorNumber
   */
  default void printSRCRULEErrors(final int errorNumber, final String labelName, final String labelType,
      final int lineNo, final String error) {
    String errorMsg = error;
    String currentError = lineNo + ":";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " type label. \n" +
            "\"SRC\" function should be used only for CURVE, MAP , Axis points labels.\n ";
        break;
      case ReportMessages.P_SRCAXIS:
        currentError = currentError + "Usage of Y and Z as axis for curve and axis points label is not allowed !\n" +
            " Usage of  Z as axis for MAP is not allowed !\n ";
        break;
      case ReportMessages.P_SRCENTRY:
        currentError = currentError + "Index specified is not proper !\n Note :1. Index should not be 0 and 255 \n " +
            "2. Index specified should not be greater than number of axis points of label  \n   ";
        break;
      case ReportMessages.P_MAPFORM:
        currentError = currentError + "Comparsion-term is missing ! \n SRC Syntax:" +
            " SRC ( Axis[num] = \"Comparsion-term\" ) \n";
        break;
      case ReportMessages.P_SRCFORM:
        currentError = currentError + "Make sure that Map specified in SRC rule has textual values !\n";
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
   * @param error
   * @param lineNo
   */
  default void printFIDCHECKFuncErrors(final String error, final int lineNo) {

    String description = "\"FIDCHECK\" function should be used only for Value block type labels \n " +
        "Proper syntax for FIDCHECK is \n#FID \"FId_Verbal\" FIDCHECK ( DINH_FId.ArrayLabel-1 ( \"Def-Verbal-1\" )," +
        " \\...\nDINH_FId.ArrayLabel-2 ( \"Def-Verbal-2\" ) , \\...\nDINH_FId.ArrayLabel-3 ( \"Def-Verbal-3\" ) ,\\...\n" +
        "DINH_FId.ArrayLabel-4 ( \"Def-Verbal-4\" ))\n #FID \"FId_Verbal\" FIDCHECK ( DINH_FId.ArrayLabel-5 ( \"Def-Verbal-1\" ) || ( \"Def-Verbal-2\")\n";
    setMessageDesc(error + description);
    getInstance().getLineNoAndError().put(lineNo, description);

  }

  /**
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printDINHCHECKErrors(final int errorNumber, final String labelName, final String labelType,
      final int lineNo, final String error) {
    String errorMsg = error;
    String currentError = lineNo + ":";
    if (errorNumber == ReportMessages.P_TABTYPE) {
      currentError = "\"" + labelName + "\"" + " is " + labelType + " type label. \n" +
          "\"DINHCHECK\" function should be used only for Value block type labels.\n ";

    }
    else if (errorNumber == ReportMessages.P_NOTVALIDDINHCHECK) {
      currentError = "FID's is not specified in the rule! Only VBLK labels allowed in DINCHECK \n " +
          "DINHCHECK syntax:DINHCHECK ( DINH_Lim.Label, \\...\nFid_Verbal-1 ( Def-Verbal-1 ), \\...\nFId_Verbal-i ( Def-Verbal-i ) )\n";

    }
    if (!errorMsg.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(errorMsg + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }


  /**
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printVBLKVERBFuncErrors(final int errorNumber, final String labelName, final String labelType,
      final int lineNo, final String error) {
    String err4 = error + ValidationConstants.IN_LINE_NUMBER + lineNo + ":";
    if (errorNumber == ReportMessages.P_TABTYPE) {
      String description = "\"" + labelName + "\"" + " is " + labelType + " type label. \n" +
          "Only Value block type labels allowed in \"VBLKVERB\"\n ";
      setMessageDesc(err4 + description);
      getInstance().getLineNoAndError().put(lineNo, description);

    }
  }

  /**
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printBITFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String lineNoStr = ValidationConstants.IN_LINE_NUMBER + lineNo + " :";
    String currentError = "";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = "\"" + labelName + "\"" + " is " + labelType + " label. In Function " + functionName +
            ", only Value type label can be used as parameter! \n";
        break;
      case ReportMessages.P_BITCOLON:
        currentError = "Error :\n Parameter for " + functionName + " is empty !\n ";
        getBITFuncSyntax(functionName, error + currentError, lineNo);
        break;
      case ReportMessages.P_BITCOLON + 1:
        currentError = "Error :\n Bit position specified is not allowed for label ";

        break;
      case ReportMessages.P_EVALBIT + 1:
        currentError = "Error :\n Bit  specified is not proper for label ";
        getBITFuncSyntax(functionName, error + lineNoStr + currentError, lineNo);
        break;
      default:
        break;
    }
    if (!error.contains(currentError)) {
      setMessageDesc(error + lineNoStr + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError);

    }

  }

  /**
   * @param functionName
   * @param error
   * @param lineNo
   */
  default void getBITFuncSyntax(final String functionName, final String error, final int lineNo) {
    String properSyntax = "Proper syntax:";
    String currentError = "";
    if (functionName.equals(ValidationConstants.BIT)) {
      currentError = " BIT ( Bit-No: Value) ) \n " +
          "Where bit-no = 0,1,..highest bit depending on the size of the value (byte, word, long)\n";
    }
    else if (functionName.equals(ValidationConstants.LBIT)) {
      currentError = "LBIT ( Bit-No: Value, Bit-No: Value, ...) ) \n where bit-no = 0,1,..,7\n";
    }
    else if (functionName.equals(ValidationConstants.HBIT)) {
      currentError = "HBIT ( Bit-No: Value, Bit-No: Value, ...) ) where bit-no = 8,9,..,15\n";
    }
    else if (functionName.equals(ValidationConstants.LBIT32)) {
      currentError = "LBIT32 ( Bit-No: Value, Bit-No: Value, ...) ) where bit-no = 16,17,..,23\n";
    }
    else if (functionName.equals(ValidationConstants.HBIT32)) {
      currentError = "HBIT32 ( Bit-No: Value, Bit-No: Value, ...) ) where bit-no = 24,25,..,31\n";
    }
    setMessageDesc(error + properSyntax + currentError);
    getInstance().getLineNoAndError().put(lineNo, currentError);

  }

  /**
   * @param errorNumber
   * @param lineNo
   * @param error
   */
  default void printVBLKRuleErrors(final int errorNumber, final int lineNo, final String error) {
    String errorMsg = error;
    String currentError = lineNo + ":";
    if (errorNumber == ReportMessages.P_TABTYPE) {
      currentError = currentError + "Only VBLK labels are allowed in \"VBLK\" function\n";
    }
    else if ((errorNumber == ReportMessages.P_VBLKPOS) || (errorNumber > ReportMessages.P_VBLKPOS)) {
      currentError = currentError + " Possible Errors  :1.value of \"x\" is  0 or negative number! \n" +
          "2.VBLK rule should have atleast one pair of i:Comparison term\n" +
          "3. Usgae of ; to seperate the rule parameter is prohibited" +
          " VBLK syntax: VBLK ( i: Comparison-1 , i: Comparison-2) for array of numerical values i = 1...max \n";

    }
    if (!errorMsg.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(errorMsg + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }

  /**
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printAXISCMPFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String errorContent = error;

    String currentError = lineNo + ":";
    switch (errorNumber) {

      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label.In" + functionName +
            ", only Axis pt label are allowed! \n";
        break;
      case ReportMessages.P_AXCOMP:
        currentError = currentError + "The label " + labelName +
            "doesnot have same axis points as its comparsion axis point label !\n";
        break;
      case ReportMessages.P_MAPTYP + 1:
      case ReportMessages.P_AXPCMP + 1:
        currentError =
            currentError + "Comparsion term is not proper or not available in LDB! \n It should be Axis point label \n";
        break;
      case ReportMessages.P_MAPTYP + 2:
        currentError = currentError + "Comparsion term should be Axis point label \n";
        break;
      case ReportMessages.P_AXPCMP + 2:
        currentError = currentError + "Comparsion term is not provided ." +
            " Proper syntax: AXISCMP ( Comparison-AxisPts ) for array of axis points\n";
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
   * @param error
   * @param errorNumber
   * @param lineNo
   */
  default void printIFRULEErrors(final String error, final int lineNo, final int errorNumber) {
    String err2 = error + ValidationConstants.IN_LINE_NUMBER + lineNo + ":";
    String errorDescription = "";
    switch (errorNumber) {
      case ReportMessages.P_IFCOND:
        errorDescription =
            "Condition specified in the IF function is not proper!\n Make sure label (if used) is VALUE-Type label \n ";
        break;
      case ReportMessages.P_TABTYPE:
        errorDescription = "Label used in IF label is not VALUE type label. \n Only VALUE type is allowed in IF rule\n";

        break;
      case ReportMessages.P_IFFORM + 1:
        errorDescription = "True-MathTerm of IF Rule parameter is not proper ! \n";
        break;
      case ReportMessages.P_IFFORM + 2:
        errorDescription = "False-MathTerm of IF Rule parameter is not proper ! \n";
        break;
      case ReportMessages.P_LAB_NOT_FOUND:
        errorDescription = "Label specified in the rule is not available in LDB\n";

        break;
      default:

        break;

    }
    setMessageDesc(err2 + errorDescription);
    getInstance().getLineNoAndError().put(lineNo, errorDescription);

  }


  /**
   * @param error
   * @param labelType
   * @param labelName
   * @param lineNo
   * @param errorNumber
   */
  default void printCASERULEErrors(final String error, final int errorNumber, final int lineNo, final String labelName,
      final String labelType) {
    String err2 = error;
    String currentError = "";
    String properSyntax =
        "\nCASE Syntax : Value-Type label /Constant," + "CASE(Variable(Value: Comparsion MathTerm) )\n ";
    String secondCondition = "Note: It's compulsory to have one Value-x : mathterm pair  \n";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = "\"" + labelName + "\"" + " is " + labelType + " label. In Case Function " +
            ", only Value type label are allowed! \n";
        break;
      case ReportMessages.P_CASELABEL + 1:
        currentError = "Error : Variable is missing ! " + properSyntax;
        break;
      case ValidationConstants.CASEERR_INT:
        currentError = "Error : Value paremater of CASE rule defined is not proper !\n" + secondCondition;
        break;
      case ValidationConstants.CASEERR_INT + 1:
        currentError = "Error : Comparsion mathTerm defined is not proper !\n";
        break;
      case ReportMessages.P_CASELABEL:
        currentError = "Error : In CASE rule , Variable should be value type label  ! " + properSyntax;
        break;
      default:

        break;

    }
    if (!err2.contains(currentError) && (currentError.length() > 15)) {
      setMessageDesc(err2 + ValidationConstants.IN_LINE_NUMBER + lineNo + ":" + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError);

    }

  }

  /**
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printBITVBLKFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String err2 = error;
    String currentError = "";
    String lineNoText = ValidationConstants.IN_LINE_NUMBER + lineNo + ":";
    switch (errorNumber) {
      case ReportMessages.P_TABTYPE:
        currentError = "\"" + labelName + "\"" + " is " + labelType + " label. In Function " + functionName +
            ", only Value block label are allowed! \n";
        if (!error.contains(currentError)) {
          setMessageDesc(err2 + lineNoText + currentError);
          getInstance().getLineNoAndError().put(lineNo, currentError);

        }
        break;
      case ReportMessages.P_BITCOLON:
        currentError = "Error :\n Parameter for " + functionName + " is empty !\n";
        if (!error.contains(currentError)) {
          setMessageDesc(err2 + lineNoText + currentError);
          getInstance().getLineNoAndError().put(lineNo, currentError);

          getBITFuncSyntax(functionName, err2 + currentError, lineNo);

        }

        break;
      case ReportMessages.P_BITCOLON + 1:
        currentError = " Error :\n Bit position specified is not allowed for label " + labelName + " which is " +
            labelType + " type label!\n ";
        if (!error.contains(currentError)) {
          setMessageDesc(err2 + lineNoText + currentError);
          getInstance().getLineNoAndError().put(lineNo, currentError);

        }
        break;
      default:
        break;

    }

  }

  /**
   * @param functionName
   * @param errorNumber
   * @param labelName
   * @param labelType
   * @param lineNo
   * @param error
   */
  default void printVALVERBFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String errorContent = error;

    String currentError = lineNo + ":";
    if (errorNumber == ReportMessages.P_TABTYPE) {
      currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
          functionName + ", only Value type label are allowed! \n";
    }
    else if (errorNumber == ReportMessages.P_CONVTYPE) {
      currentError = currentError + "Make sure Label defined in rule has textual values ! \n";
    }
    if (!errorContent.contains(currentError) && (currentError.length() > 17)) {
      String lineNumberText = ValidationConstants.IN_LINE_NUMBER + lineNo + ':';
      currentError = currentError.substring(currentError.indexOf(':') + 1, currentError.length());
      setMessageDesc(errorContent + lineNumberText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError.replaceAll("\\n", ""));

    }
  }

  /**
   * @param functionName
   * @param error
   * @param lineNo
   * @param labelType
   * @param labelName
   * @param errorNumber
   * @param GMAPOrFMAP
   * @param fun
   */
  default void printGMAPFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String errorContent = error;
    String currentError = lineNo + ":";

    String properSyntax = " \nGMAP Syntax:GMAP" + "( [x]= Comparison-1,) for group curve \n" +
        "GMAP ( [x][y] =Comparison-1) for group map" + " where x /y= 1...max\n ";
    switch (errorNumber) {
      case ReportMessages.P_GMAPCHAR + 1:
        currentError = currentError + "GMAP function Parameter is missing !" + properSyntax;
        break;
      case ReportMessages.P_TABTYPE:
        currentError = currentError + "\"" + labelName + "\"" + " is " + labelType + " label. In Function " +
            functionName + ", only Group map and Group curve label are allowed! \n";
        break;
      case ReportMessages.P_GMAPENTRY:
        currentError = currentError + "Error :  Y axis specification is not allowed for curve label !\n" + properSyntax;

        break;
      case ReportMessages.P_GMAPENTRY + 1:
        currentError = currentError +
            "Error :  X or Y axis value specified is greater than number of values in label specified !\n" +
            properSyntax;

        break;
      case ReportMessages.P_GMAPENTRY + 2:
        currentError =
            currentError + "Error :  Y axis specification is not mentioned in rule for MAP  !\n" + properSyntax;
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
   * @param functionName
   * @param error
   * @param lineNo
   * @param labelType
   * @param labelName
   * @param errorNumber
   * @param GMAPOrFMAP
   * @param fun
   */
  default void printFMAPFuncErrors(final String functionName, final int errorNumber, final String labelName,
      final String labelType, final int lineNo, final String error) {
    String err3 = error;
    String lineNoText = ValidationConstants.IN_LINE_NUMBER + lineNo + ":";

    String currentError = "";
    String properSyntax = " \n Proper syntax for FMAP is FMAP" + "( [x]= Comparison-1,) for fixed curve \n" +
        "GMAP ( [x][y] =Comparison-1) for fixed map" + " where x /y= 1...max\n ";
    switch (errorNumber) {
      case ReportMessages.P_GMAPCHAR + 1:
        currentError = "Error : Parameter for FMAP function is missing !" + properSyntax;
        break;
      case ReportMessages.P_TABTYPE:
        currentError = "\"" + labelName + "\"" + " is " + labelType + " label. In Function " + functionName +
            ", only fixed map and fixed curve label are allowed! \n";
        break;
      case ReportMessages.P_GMAPENTRY:
        currentError = "Error :  Y axis specification is not allowed for curve label !\n" + properSyntax;

        break;
      case ReportMessages.P_GMAPENTRY + 1:
        currentError = "Error :  X or Y axis value specified is greater than number of values in label specified !\n" +
            properSyntax;

        break;
      case ReportMessages.P_GMAPENTRY + 2:
        currentError = "Error :  Y axis specification is not mentioned in rule for MAP  !\n" + properSyntax;
        break;
      default:
        break;
    }
    if (!err3.contains(currentError) && (currentError.length() > 16)) {
      setMessageDesc(err3 + lineNoText + currentError);
      getInstance().getLineNoAndError().put(lineNo, currentError);

    }
  }
}

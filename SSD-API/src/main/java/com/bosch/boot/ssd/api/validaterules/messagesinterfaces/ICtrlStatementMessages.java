/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.validaterules.messagesinterfaces;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.bosch.boot.ssd.api.util.ValidationConstants;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.checkssd.datamodel.SSDStatement;
import com.bosch.checkssd.datamodel.constant.ConstValue;
import com.bosch.checkssd.datamodel.control.CtrlFidCheck;
import com.bosch.checkssd.datamodel.control.CtrlFor;
import com.bosch.checkssd.datamodel.control.CtrlIf;
import com.bosch.checkssd.datamodel.control.CtrlLabel;
import com.bosch.checkssd.datamodel.control.UseCaseContactInfo;
import com.bosch.checkssd.datamodel.mathterm.IMathTermElement;
import com.bosch.checkssd.datamodel.rule.CaseRule;
import com.bosch.checkssd.reports.reportMessage.ReportMessages;

/**
 * ICtrlStatementMessages
 *
 * @author SMN6KOR
 */
public interface ICtrlStatementMessages extends IPrintMessages {


  /**
   * Method to handle the ctrl stmt and print its respective errors
   *
   * @param stmt
   */

  default void handleControlStmt(final SSDStatement stmt) {
    int errorNumber = 0;
    String funcName = "";
    /*
     * Checks whether stmt is FIDCHEeck rule to handle its error
     */
    if (stmt instanceof CtrlFidCheck) {
      handleFIDCheck(stmt);
    }
    /*
     * Checks whether stmt is IF rule to handle its error
     */
    else if (stmt instanceof CtrlIf) {
      funcName = handleCtrlIf(stmt);
    }
    /*
     * Checks whether stmt is CASE rule to handle its error
     */
    else if (stmt instanceof CaseRule) {
      getErrorNo(stmt);
      funcName = ValidationConstants.CTRLCASE;
    }
    /*
     * Checks whether stmt is FOR rule to handle its error
     */
    else if (stmt instanceof CtrlFor) {
      printCtrlFORErrors(stmt);
      if (((CtrlFor) stmt).getStatements() != null) {
        for (SSDStatement stmts : ((CtrlFor) stmt).getStatements()) {
          getInstance().getErrorInfo(stmts);
        }
      }

    }
    /*
     * Checks whether stmt is #Label rule to handle its error
     */
    else if (stmt instanceof CtrlLabel) {
      printCtrlLabelErrors(stmt);
    }
    /*
     * Checks whether stmt is usecase and contact rule to handle its error
     */
    if (stmt instanceof UseCaseContactInfo) {
      handleContactUseCaseInfo(stmt);
    }
    else if (stmt instanceof ConstValue) {
      IMathTermElement iMathTermElement = ((ConstValue) stmt).getMathTerm().getMathElement().get(0);
      Map<String, String> lblAndTypeMap = getLabelNameAndType(iMathTermElement);
      String lblName = "";
      String lblType = "";
      if (lblAndTypeMap.size() == 1) {
        lblName = (String) lblAndTypeMap.keySet().toArray()[0];
        lblType = lblAndTypeMap.get(lblName);
      }
      displayCtrlStmtErrors(funcName, errorNumber, lblName, lblType, stmt);
    }
  }

  /**
   * @param stmt
   * @return - function name
   */
  default String handleCtrlIf(final SSDStatement stmt) {
    String funcName;
    getErrorNo(stmt);
    funcName = ValidationConstants.CTRLIF;
    if (((CtrlIf) stmt).getIfStatements() != null) {
      for (SSDStatement stmts : ((CtrlIf) stmt).getIfStatements()) {

        getInstance().getErrorInfo(stmts);
      }

    }
    if (((CtrlIf) stmt).getElseStatements() != null) {
      for (SSDStatement stmts : ((CtrlIf) stmt).getElseStatements()) {
        getInstance().getErrorInfo(stmts);
      }
    }
    return funcName;
  }

  /**
   * Method to print the error with respect to #Label statement
   *
   * @param stmt
   */
  default void printCtrlLabelErrors(final SSDStatement stmt) {

    int errorNumber = ((CtrlLabel) stmt).getError();
    String errorMsg = getInstance().getExistingErrors();

    if (errorNumber == ReportMessages.P_CTRLLABELERROR_1) {
      String err = "No Statements available inside #Label rule block \n ";
      if (!errorMsg.contains(err)) {
        setMessageDesc(errorMsg + err);
        getInstance().getLineNoAndError().put(stmt.getLineNo(), err);
      }

    }
    else if (errorNumber == ReportMessages.P_LABELNOTAVAILABLE) {
      String err = "Label specified in #Label rule block is not available in LDB \n";
      if (!errorMsg.contains(err)) {
        setMessageDesc(errorMsg + err);
        getInstance().getLineNoAndError().put(stmt.getLineNo(), err);
      }
    }
    /*
     * Handling of statement inside the #Label statement
     */
    for (SSDStatement substmt : ((CtrlLabel) stmt).getSsdStmtList()) {
      getInstance().getErrorInfo(substmt);
    }
  }

  /**
   * Method to print the error with respect to usecase and contant statement
   *
   * @param stmt
   */
  default void handleContactUseCaseInfo(final SSDStatement stmt) {
    UseCaseContactInfo statement = ((UseCaseContactInfo) stmt);
    if ((statement.getUcCnt() == 0) &&
        ((((UseCaseContactInfo) stmt).getCtrlEndCnt() == 0) && (statement.getEndContCnt() == 0))) {
      getInstance().setError(true);
      String error = getInstance().getExistingErrors();
      String err = "Missing of \"#endusecase\" and \"#endcontact\" for the rule";
      setMessageDesc(error + err + " starting at line no " + statement.getLineNo() + "\n");
      getInstance().getLineNoAndError().put(stmt.getLineNo(), err);
    }
    else if ((statement.getUcCnt() == 0) &&
        ((((UseCaseContactInfo) stmt).getCtrlEndCnt() == 0) || (statement.getEndContCnt() == 0))) {
      printNoEndCaseError(statement);
    }
    else if ((statement.getUcCnt() == 1) &&
        ((((UseCaseContactInfo) stmt).getCtrlEndCnt() == 0) && (statement.getEndContCnt() == 0))) {
      getInstance().setError(true);
      String error = getInstance().getExistingErrors();
      String err = "\n Possible errors are :\n" +
          "1.Missing of \"#endusecase\" and \"#endcontact\" for \"#usecase\" and \"#contact\" specified \n" +
          "2.Too many usecase and contact specified \n";
      setMessageDesc(error + "In Line no: " + statement.getLineNo() + err);
      getInstance().getLineNoAndError().put(stmt.getLineNo(), err);

    }
    else if ((statement.getUcCnt() == 1) &&
        ((((UseCaseContactInfo) stmt).getCtrlEndCnt() == 0) || (statement.getEndContCnt() == 0))) {
      printNoEndCaseError(statement);
    }
    else if (statement.getUcCnt() == -1) {
      getInstance().setError(true);
      String error = getInstance().getExistingErrors();
      String err = "Too many  \"#endusecase\" and \"#endcontact\" for the rule";
      setMessageDesc(error + err + " starting at line no " + statement.getLineNo() + "\n");
      getInstance().getLineNoAndError().put(stmt.getLineNo(), err);
    }
    /*
     * handling of statemnt inside the use case and contact
     */
    for (SSDStatement substmt : statement.getStmtList()) {
      getInstance().getErrorInfo(substmt);
    }

  }

  /**
   * Handling of the error with respective to the no end use case and contact
   *
   * @param statement
   */
  default void printNoEndCaseError(final UseCaseContactInfo statement) {
    if (statement.getCtrlEndCnt() == 0) {
      getInstance().setError(true);
      String error = getInstance().getExistingErrors();
      String err = "Missing of \"#endusecase\"";
      setMessageDesc(error + err + " for the rule starting at line no " + statement.getLineNo() + "\n");
      getInstance().getLineNoAndError().put(statement.getLineNo(), err);
    }
    if (statement.getEndContCnt() == 0) {
      getInstance().setError(true);
      String error = getInstance().getExistingErrors();
      String err = "Missing of \"#endcontact\"";
      getInstance().getLineNoAndError().put(statement.getLineNo(), err);
      setMessageDesc(error + err + " for the rule starting at line no " + statement.getLineNo() + "\n");
    }
  }

  /**
   * Method to handle ctrl statement and print the respective errors
   *
   * @param funcName
   * @param errorNumber
   * @param lblName
   * @param lblType
   * @param stmt
   * @param lineNo
   */
  default void displayCtrlStmtErrors(final String funcName, final int errorNumber, final String lblName,
      final String lblType, final SSDStatement stmt) {
    String error = getInstance().getExistingErrors();

    switch (funcName) {
      case ValidationConstants.CTRLIF:
        printCtrlIFErrors(error, errorNumber, stmt.getLineNo());
        break;


      case "":
        if (errorNumber == ReportMessages.P_TABTYPE) {
          String err = "Error in usage of label " + "\"" + lblName + "\"" +
              " Given syntax is not applicable for the label type  " + "\"" + lblType + "\"" + " label. \n";
          setMessageDesc(error + ValidationConstants.IN_LINE_NUMBER + stmt.getError() + ": " + err);
          getInstance().getLineNoAndError().put(stmt.getLineNo(), err);
        }
        break;

      default:
        break;

    }

  }

  /**
   * @param error
   * @param stmt
   */
  default void printCtrlFORErrors(final SSDStatement stmt) {
    String error = getInstance().getExistingErrors();
    if (error
        .contains("Hint : Errors asscociated with rule listed under #FOR will not displayed in this SSD version \n")) {
      error = error + "\n" +
          "Hint : Errors asscociated with rule listed under #FOR will not displayed in this SSD version \n";
      setMessageDesc(error);
    }

    if (stmt.getError() == ReportMessages.P_CTRLFORERROR_1) {
      setMessageDesc(error + "In line no: " + stmt.getLineNo() + ",  Missing of Min/Max limit in #For statement !\n");
      getInstance().getLineNoAndError().put(stmt.getLineNo(), "Missing of Min/Max limit in #For statement !\n");

    }
    else if (stmt.getError() == ReportMessages.P_CTRLFORERROR_2) {
      setMessageDesc(error + "In line no: " + stmt.getLineNo() + ",  Max limit in #For should not be Negative !\n");
      getInstance().getLineNoAndError().put(stmt.getLineNo(), "Max limit in #For should not be Negative !\n");

    }
    for (SSDStatement substmt : ((CtrlFor) stmt).getStatements()) {
      getInstance().getErrorInfo(substmt);
    }

  }

  /**
   * @param stmt
   */
  default void handleFIDCheck(final SSDStatement stmt) {
    List<String> errorLabelList = new CopyOnWriteArrayList<>();
    if (!errorLabelList.isEmpty()) {
      for (String labelName : errorLabelList) {
        String lblName = labelName;
        if (labelName.contains(";")) {
          lblName = labelName.substring(0, labelName.indexOf(';'));
        }
        Characteristic lblCharacteristic =
            getInstance().getLabelUtils().getCharacteristics(lblName, getInstance().getUtilInst());
        if ((lblCharacteristic != null) && lblCharacteristic.getType().equals("VALUE_BLOCK") &&
            stmt.getCurLine().contains(lblName)) {
          getInstance().setError(true);
          String error = getInstance().getExistingErrors();
          setMessageDesc(
              error + " The label" + lblName + "is not VBLK label . FIDCheck is used only for VBLK labels!\n");
          getInstance().getLineNoAndError().put(stmt.getLineNo(),
              " The label" + lblName + "is not VBLK label . FIDCheck is used only for VBLK labels!\n");

        }
      }
    }
  }

  /**
   * @param stmt
   * @return error number
   */
  default int getErrorNo(final SSDStatement stmt) {
    int errorNumber = 0;
    if (stmt.getError() > 0) {
      errorNumber = stmt.getError();
    }
    else if ((stmt instanceof CtrlFidCheck) && (((CtrlFidCheck) stmt).getError() > 0)) {
      errorNumber = ((CtrlFidCheck) stmt).getError();
    }
    return errorNumber;
  }


  /**
   * @param error
   * @param errorNumber
   * @param lineNo
   */
  default void printCtrlIFErrors(final String error, final int errorNumber, final int lineNo) {


    if (errorNumber == (ReportMessages.P_IFFORM + 1)) {
      String description = "Possible Errors : 1.Condition is missing\n" + "2.Condition is not proper\n" +
          " Proper syntax of IF is #IF ( Comparison condition )\nStatements\n#ENDIF\n#IF ( Comparison condition )\nStatements\n#ELSE\nStatements\n #ENDIF\n " +
          "Note :1.Comparison condition  - should be mathametical expression or value type label or a constant." +
          "2.It is compulsory to have Comparison condition as parameter of IF \n statement can be any valid rule ";
      setMessageDesc(error + description);
      getInstance().getLineNoAndError().put(lineNo, description);

    }


  }

}

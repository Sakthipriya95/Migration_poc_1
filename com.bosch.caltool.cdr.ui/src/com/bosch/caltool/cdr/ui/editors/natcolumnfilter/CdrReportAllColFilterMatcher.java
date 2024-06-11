/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.natcolumnfilter;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * this class is to filter all columns based on the text entered
 *
 * @author mkl2cob
 */
public class CdrReportAllColFilterMatcher implements Matcher<A2LParameter> {

  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;

  /**
   * CdrReportData
   */
  private final CdrReportDataHandler cdrRprtData;


  /**
   * @param cdrReportData CdrReportData
   */
  public CdrReportAllColFilterMatcher(final CdrReportDataHandler cdrReportData) {
    this.cdrRprtData = cdrReportData;
  }

  /**
   * @param textToFilter filter text
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || CommonUIConstants.EMPTY_STRING.equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter;

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition.
    if (subTexts.length == 0) {
      return;
    }

    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();
    for (String curString : subTexts) {
      if (!CommonUIConstants.EMPTY_STRING.equals(curString)) {
        sbRegExp.append(".*");
        sbRegExp.append(Pattern.quote(curString));
      }
      sbRegExp.append(".*");
    }
    regExp = sbRegExp.toString();

    this.pattern1 = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);

    // Word level matching is not required if filter text starts with '*'.
    if (regExp.startsWith(".*")) {
      this.pattern2Search = false;
    }
    else {
      this.pattern2Search = true;
      this.pattern2 = Pattern.compile(".* " + regExp, Pattern.CASE_INSENSITIVE);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final A2LParameter arg0) {


    if (matchParamAndFuncName(arg0)) {
      // return true if text matches with paramter name or function name
      return true;
    }

    if (matchParamTypeCW(arg0)) {
      // return true if the text matches with code word string or type string
      return true;
    }
    if (matchFuncVersionResp(arg0)) {
      // return true if text matches with function version or responsibility string
      return true;
    }
    if (matchLatestA2lVersCmtRvwdFlag(arg0)) {
      // return true if text matches with latest a2l version or 'true' or 'false' with respect to review
      return true;
    }
    if (matchQnaireRespVersStatus(arg0)) {
      // return true if text matches with Question Resp version
      return true;
    }
    if (matchResponsibilityTrueFalse(arg0)) {
      // return true if text matches with Responsibility
      return true;
    }
    if (matchRespTypeTrueFalse(arg0)) {
      // return true if text matches with Resp Type
      return true;
    }
    return (matchLatestFuncVers(arg0));
  }

  /**
   * @param arg0
   * @return
   */
  private boolean matchLatestFuncVers(final A2LParameter arg0) {
    return matchText(this.cdrRprtData.getLatestFunctionVersion(arg0.getName()));
  }

  private boolean matchQnaireRespVersStatus(final A2LParameter arg0) {
    return matchText(this.cdrRprtData.getQnaireRespVersStatus(arg0.getParamId(), false));
  }

  /**
   * @param arg0
   * @return true if the typed text matches latest a2l version, reviewed flag
   */
  private boolean matchLatestA2lVersCmtRvwdFlag(final A2LParameter arg0) {
    return matchText(this.cdrRprtData.getLatestA2LVersion(arg0.getName())) ||
        matchedReviewedTrueFalse(arg0.getName()) || matchText(this.cdrRprtData.getLatestReviewComment(arg0.getName()));
  }

  /**
   * @param arg0
   * @return true if the typed text matches function version , responsibility
   */
  private boolean matchFuncVersionResp(final A2LParameter arg0) {
    // ICDM-1901
    boolean isMatchedFlag = false;
    if (null != arg0.getDefFunction()) {
      isMatchedFlag = matchText(arg0.getDefFunction().getFunctionVersion());
    }
    if (!isMatchedFlag) {
      isMatchedFlag = matchText(this.cdrRprtData.getWpName(arg0.getParamId()));
    }
    return isMatchedFlag;
  }

  /**
   * @param arg0
   * @return true if the typed text matches paramter type and code word
   */
  private boolean matchParamTypeCW(final A2LParameter arg0) {
    return matchText(arg0.getCodeWordString()) || matchText(arg0.getType());
  }

  /**
   * @param arg0
   * @return true if the typed text matches parameter and function name
   */
  private boolean matchParamAndFuncName(final A2LParameter arg0) {
    // ICDM-1901
    boolean isMatchedText;
    isMatchedText = matchText(arg0.getName());
    if (!isMatchedText && (null != arg0.getDefFunction())) {
      isMatchedText = matchText(arg0.getDefFunction().getName());
    }
    return isMatchedText;
  }

  /**
   * @param a2lParamName String
   * @return true if the a2l parameter is reviewed
   */
  private boolean matchedReviewedTrueFalse(final String a2lParamName) {
    // check if the text matches with the reviewed flag true or false
    // ICDM-2585 (Parent Task ICDM-2412)-2
    return matchText(this.cdrRprtData.isReviewedStr(a2lParamName));
  }

  private boolean matchResponsibilityTrueFalse(final A2LParameter arg0) {
    return matchText(this.cdrRprtData.getRespName(arg0.getName()));
  }

  private boolean matchRespTypeTrueFalse(final A2LParameter arg0) {
    return matchText(this.cdrRprtData.getRespType(arg0.getName()));
  }

  /**
   * @param text
   * @return true if the pattern matches the text
   */
  private final boolean matchText(final String text) {

    if (text == null) {
      return false;
    }

    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }

    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }

    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(text);

    return matcher2.matches();
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.natcolumnfilter;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author mkl2cob
 */
public class CompHexWithCdfAllColMatcher<E extends CompHexWithCDFParam> implements Matcher<E> {


  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;
  private final CompHexWithCDFxDataHandler compHexDataHdlr;

  /**
   * @param compHexDataHdlr handler
   */
  public CompHexWithCdfAllColMatcher(final CompHexWithCDFxDataHandler compHexDataHdlr) {
    this.compHexDataHdlr = compHexDataHdlr;
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
  public boolean matches(final E arg0) {


    if (matchParamAndFuncName(arg0)) {
      // return true if text matches with paramter name or function name
      return true;
    }

    if (matchParamType(arg0)) {
      // return true if the text matches param type
      return true;
    }
    if (matchFuncVersion(arg0)) {
      // return true if the text matches function version
      return true;
    }
    if (matchWp(arg0)) {
      // return true if the text matches Workpackage Name
      return true;
    }
    if (matchLatestA2LAndFuncVersion(arg0)) {
      // return true if the text matches function version and a2l version
      return true;
    }
    if (matchRespType(arg0)) {
      // return true if the text matches Resp Type
      return true;
    }
    if (matchResp(arg0)) {
      // return true if the text matches Responsibility
      return true;
    }
    if (matchCDFCalData(arg0)) {
      // return true if the text matches CDF file cal data object
      return true;
    }
    if (matchHEXCalData(arg0)) {
      // return true if the text matches with HEX file cal data
      return true;
    }
    return (matchCompliResult(arg0));
  }


  /**
   * @param arg0
   * @return if the compli result is matched
   */
  private boolean matchCompliResult(final E arg0) {
    return matchText(this.compHexDataHdlr.getResult(arg0));
  }

  private boolean matchWp(final E arg0) {
    return matchText(this.compHexDataHdlr.getCdrReportData().getWpName(arg0.getParamName()));
  }

  private boolean matchRespType(final E arg0) {
    return matchText(this.compHexDataHdlr.getCdrReportData().getRespType(arg0.getParamName()));
  }


  /**
   * @param arg0
   * @return
   */
  private boolean matchResp(final E arg0) {
    return matchText(this.compHexDataHdlr.getCdrReportData().getRespName(arg0.getParamName()));
  }

  /**
   * @param arg0
   * @return
   */
  private boolean matchLatestA2LAndFuncVersion(final E arg0) {
    boolean isMatchedText;
    isMatchedText = matchText(arg0.getLatestA2lVersion());
    if (!isMatchedText && (null != arg0.getFuncName())) {
      isMatchedText = matchText(arg0.getLatestFunctionVersion());
    }
    return isMatchedText;
  }

  /**
   * @param arg0
   * @return
   */
  private boolean matchFuncVersion(final E arg0) {
    return matchText(arg0.getFuncVers());
  }

  /**
   * @param arg0
   * @return
   */
  private boolean matchHEXCalData(final E arg0) {
    if (arg0.getHexCalDataPhySimpleDispValue() == null) {
      return false;
    }
    return matchText(arg0.getHexCalDataPhySimpleDispValue());
  }

  /**
   * @param arg0
   * @return
   */
  private boolean matchCDFCalData(final E arg0) {
    if (arg0.getCdfxCalDataPhySimpleDispValue() == null) {
      return false;
    }
    return matchText(arg0.getCdfxCalDataPhySimpleDispValue());
  }

  /**
   * @param arg0
   * @return true if the typed text matches paramter type and code word
   */
  private boolean matchParamType(final E arg0) {
    return matchText(arg0.getParamType().getText());
  }

  /**
   * @param arg0
   * @return true if the typed text matches parameter and function name
   */
  private boolean matchParamAndFuncName(final E arg0) {

    boolean isMatchedText;
    isMatchedText = matchText(arg0.getParamName());
    if (!isMatchedText && (null != arg0.getFuncName())) {
      isMatchedText = matchText(arg0.getFuncName());
    }
    return isMatchedText;
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

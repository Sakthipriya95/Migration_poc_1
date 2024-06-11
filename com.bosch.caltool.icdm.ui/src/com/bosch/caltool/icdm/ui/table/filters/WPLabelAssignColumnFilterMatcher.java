/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.table.filters;


import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * Column Filter Class
 *
 * @author apj4cob
 */
public class WPLabelAssignColumnFilterMatcher implements Matcher<Object> {

  /**
   * First pattern
   */
  private Pattern pattern1;
  /**
   * Second pattern
   */
  private Pattern pattern2;
  /**
   * Pattern to search
   */
  private boolean pattern2Search;
  private final A2LWPInfoBO a2lWpInfoBo;

  /**
   * @param a2lWpInfoBo A2LWPInfoBO
   */
  public WPLabelAssignColumnFilterMatcher(final A2LWPInfoBO a2lWpInfoBo) {
    super();
    this.a2lWpInfoBo = a2lWpInfoBo;
  }

  /**
   * @param textToFilter String
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {
    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter.replace("\n", "").replace("\r", "");

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition
    if (subTexts.length == 0) {
      return;
    }

    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();
    for (String curString : subTexts) {
      if (!"".equals(curString)) {
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
  public boolean matches(final Object element) {
    if (element instanceof A2LWpParamInfo) {
      final A2LWpParamInfo a2lWpParamInfo = (A2LWpParamInfo) element;
      if (matchText(a2lWpParamInfo.getParamName())) {
        return true;
      }
      if (matchText(a2lWpParamInfo.getFuncName())) {
        return true;
      }
      if (matchText(a2lWpParamInfo.getFunctionVer())) {
        return true;
      }
      if (matchText(a2lWpParamInfo.getBcName())) {
        return true;
      }
      return matchWpCols(a2lWpParamInfo);
    }
    return false;
  }

  /**
   * @param a2lWpParamInfo A2LWpParamInfo
   * @return boolean
   */
  public boolean matchWpCols(final A2LWpParamInfo a2lWpParamInfo) {
    // Workpackage Name
    if (matchText(this.a2lWpInfoBo.getWPName(a2lWpParamInfo))) {
      return true;
    }
    // Responsibility Type
    if (matchText(this.a2lWpInfoBo.getWpRespType(a2lWpParamInfo))) {
      return true;
    }
    // Responsibility
    if (matchText(this.a2lWpInfoBo.getWpRespUser(a2lWpParamInfo))) {
      return true;
    }
    // Inherited
    if (matchText(this.a2lWpInfoBo.getWpRespInherited(a2lWpParamInfo))) {
      return true;
    }
    // Name at customer
    return (matchText(this.a2lWpInfoBo.getWpNameCust(a2lWpParamInfo)));
  }


  /**
   * Match text
   *
   * @param input input text
   * @return true if match
   */
  private final boolean matchText(final String input) {
    String text = input;
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


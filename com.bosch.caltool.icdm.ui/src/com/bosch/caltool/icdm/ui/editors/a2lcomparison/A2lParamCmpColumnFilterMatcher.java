/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author bru2cob
 */
class A2lParamCmpColumnFilterMatcher<E extends A2lParamCompareRowObject> implements Matcher<E> {

  // Pattern for search
  private Pattern pattern1;

  // Boolean for pattern search
  private boolean pattern2Search;

  // Pattern 2
  private Pattern pattern2;

  /**
   * Method which converts the user entered filter text to regex where applicable
   *
   * @param textToFilter filter text
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    // Text to filter is empty OR
    // Set pattern is false
    if ((textToFilter == null) || CommonUIConstants.EMPTY_STRING.equals(textToFilter) || !setPattern) {
      return;
    }
    // Matcher text
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
  public boolean matches(final E compareRowObject) {

    if (matchText(compareRowObject.getParamName())) {
      return true;
    }
    for (int colIndex = 3; colIndex < compareRowObject.getA2lColumnDataMapper().getColumnIndexFlagMap()
        .size(); colIndex++) {
      if (matchText(compareRowObject.getFuncName(colIndex))) {
        return true;
      }
      if (matchText(compareRowObject.getFuncVers(colIndex))) {
        return true;
      }
      if (matchText(compareRowObject.getBcName(colIndex))) {
        return true;
      }
      if (matchText(compareRowObject.getWpName(colIndex))) {
        return true;
      }
      if (matchText(compareRowObject.getRespName(colIndex))) {
        return true;
      }
      if (matchText(compareRowObject.getNameAtCust(colIndex))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Method which checks whether the passed in text matches against the patterns created in this class
   *
   * @param text
   * @return boolean
   */
  private final boolean matchText(final String text) {

    if (text == null) {
      return false;
    }
    // Matcher for patterns
    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }

    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }

    // Matcher for patterns
    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(text);

    return matcher2.matches();
  }
}

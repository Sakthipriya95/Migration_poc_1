/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.apic.PidcRMCharacterMapping;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author gge6cob
 * @param <E>
 */
public class RiskEvalColumnFilterMatcher<E> implements Matcher<E> {


  /**
   * Number of dynamic cols
   */
  private static final int END_COL_COUNT = 10;
  /**
   * Start index of fc2wp mapping objects
   */
  private static final int START_INDEX = 6;
  /**
   * Pattern
   */
  private Pattern pattern1;
  /**
   * Pattern to search
   */
  private boolean pattern2Search;
  /**
   * Pattern
   */
  private Pattern pattern2;

  /**
   * Set filter text
   *
   * @param textToFilter filter text
   * @param setPattern pattern
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter.replace("\n", "").replace("\r", "");

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition.
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
  public boolean matches(final E element) {
    if (element instanceof PidcRMCharacterMapping) {
      // get the row object
      PidcRMCharacterMapping data = (PidcRMCharacterMapping) element;

      // check if character matches with the entered text
      if (matchText(data.getProjectCharacter())) {
        return true;
      }

      if (data.isRelevantYes() && matchText(data.getRbSoftwareShare())) {
        return true;
      }
      int colIndex = START_INDEX;
      if (data.getRiskImpactMap() != null) {
        while (colIndex <= END_COL_COUNT) {
          if (matchText(data.getRiskImpactMap().get(colIndex))) {
            return true;
          }
          colIndex++;
        }
      }

    }
    return false;
  }

  /**
   * @param text String
   * @return true if the text matches with the entered string
   */
  private final boolean matchText(final String text) {

    if (text == null) {
      return false;
    }

    // check if the pattern 1 matches with the text
    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }

    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }

    // check if the pattern 2 matches with the text
    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(text);

    return matcher2.matches();
  }

}

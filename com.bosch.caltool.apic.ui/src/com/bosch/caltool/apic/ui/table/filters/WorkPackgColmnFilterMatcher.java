/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author elm1cob
 */
public class WorkPackgColmnFilterMatcher implements Matcher<Object> {

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

  /**
   *
   */
  public WorkPackgColmnFilterMatcher() {
    super();
  }

  /**
   * @param textToFilter String
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {
    // text null or empty or no pattern
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
    // A2l Work package
    if (element instanceof A2lWorkPackage) {
      final A2lWorkPackage pidcWorkPkg = (A2lWorkPackage) element;

      // name match
      if (matchText(pidcWorkPkg.getName())) {
        return true;
      }
      // Customer name match
      if (matchText(pidcWorkPkg.getNameCustomer())) {
        return true;
      }
      // Description match
      if (matchText(pidcWorkPkg.getDescription())) {
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

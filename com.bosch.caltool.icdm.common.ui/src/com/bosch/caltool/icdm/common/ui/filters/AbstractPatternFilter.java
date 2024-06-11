/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.icdm.common.ui.actions.ResetFiltersAction;

/**
 * Parent class for all viewer filters.
 * 
 * @author bne4cob
 */
public abstract class AbstractPatternFilter extends PatternFilter {

  private ResetFiltersAction resetFilters;
  /**
   * Holds the filter text.
   */
  private transient String filterText = "";
  /**
   * Pattern for normal matching.
   */
  private transient Pattern pattern1;

  /**
   * Pattern for word level matching.
   */
  private transient Pattern pattern2;

  /**
   * Whether search with pattern2 is required or not.
   */
  private transient boolean pattern2Search;


  /**
   * @return the resetFilters
   */
  public ResetFiltersAction getResetFilters() {
    return this.resetFilters;
  }


  /**
   * @param resetFilters the resetFilters to set
   */
  public void setResetFilters(final ResetFiltersAction resetFilters) {
    this.resetFilters = resetFilters;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPattern(final String patternString) {
    setFilterText(patternString);
    super.setPattern(patternString);
  }

  /**
   * @return the filterText
   */
  public String getFilterText() {
    return this.filterText;
  }


  /**
   * ICDM-2122
   * 
   * @param filterText the filterText to set
   */
  public void setFilterText(final String filterText) {
    this.filterText = filterText;

    // TODO: reset filter not implemented

    if ((filterText == null) || "".equals(filterText)) {
      return;
    }

    String regExp = filterText;

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition.
    if (subTexts.length == 0) {
      this.filterText = "";
      return;
    }

    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();
    for (String curString : subTexts) {
      if (!"".equals(curString)) {
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
   * Checks whether the current text matches the filter text. ICDM-2122
   * 
   * @param text text to match
   * @return true if matches
   */
  @Override
  protected final boolean wordMatches(final String text) {
    if (text == null) {
      return false;
    }

    // In a patternFilter class wordMatched method is always called even when this.pattern1 is not initialised
    if (this.pattern1 == null) {
      return true;
    }

    final Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }

    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }

    final Matcher matcher2 = this.pattern2.matcher(text);

    return matcher2.matches();
  }

}

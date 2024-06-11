/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.Viewer;

import com.bosch.caltool.icdm.common.ui.actions.ResetFiltersAction;

/**
 * Parent class for all viewer filters.
 *
 * @author bne4cob
 */
public abstract class AbstractViewerFilter extends org.eclipse.jface.viewers.ViewerFilter {

  /**
   * Holds the filter text.
   */
  private transient String filterText = "";

  /**
   * Defines dummy filter text
   */
  // ICDM-107
  public static final String DUMMY_FILTER_TXT = "Dummy Filter Text";

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

  private ResetFiltersAction resetFilters;

  /**
   * Validates and sets the filter text. <br>
   * Note : The classes that override this method should call the protected method
   * <code> setFilterText(String filterText, boolean setPattern)</code> from this method explicitly.
   *
   * @param textToFilter the text to be used to filter
   */
  public void setFilterText(final String textToFilter) {
    setFilterText(textToFilter, true);
  }

  /**
   * @param textToFilter the text to be used to filter
   * @param setPattern whether patterns are to be initialised for comparision
   */
  // ICDM-107
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    this.filterText = textToFilter;

    enableResetFilter();

    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter;

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
      //removed space from regexp
      this.pattern2 = Pattern.compile(".*" + regExp, Pattern.CASE_INSENSITIVE);
    }

  }

  @Override
  public final boolean select(final Viewer viewer, final Object parentElement, final Object element) {
    enableResetFilter();

    if ((this.filterText == null) || ("".equals(this.filterText))) {
      return true;
    }
    return selectElement(element);
  }

  /**
   * Enable reset filter button
   */
  private void enableResetFilter() {
    if (null != getResetFiltersAction()) {
      getResetFiltersAction().enableResetFilterAction();
    }
  }

  /**
   * Returns whether the current object can be selected or not.
   *
   * @param element the object to check
   * @return true if the required attributes of the object matches the filter text, else false
   */
  protected abstract boolean selectElement(final Object element);

  /**
   * Checks whether the current text matches the filter text.
   *
   * @param text text to match
   * @return true if matches
   */
  protected final boolean matchText(final String text) {

    if (text == null) {
      return false;
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

  /**
   * @param resetFilterAction ResetFiltersAction instance
   */
  public void setResetFiltersAction(final ResetFiltersAction resetFilterAction) {
    this.resetFilters = resetFilterAction;
  }


  /**
   * @return the clearAllFilters
   */
  public ResetFiltersAction getResetFiltersAction() {
    return this.resetFilters;
  }


  /**
   * @return the filterText
   */
  public String getFilterText() {
    return this.filterText;
  }
}

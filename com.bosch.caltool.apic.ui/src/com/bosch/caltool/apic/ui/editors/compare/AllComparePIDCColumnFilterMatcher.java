/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * All column filter matcher used for compare editor This class implements Matcher which is used in the Determination of
 * values to be filtered.
 *
 * @author jvi6cob
 */
public class AllComparePIDCColumnFilterMatcher<E extends CompareRowObject> implements Matcher<E> {

  // Pattern for search
  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;

  /**
   * Method which converts the user entered filter text to regex where applicable
   *
   * @param textToFilter filter text
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

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

    // ICdm-590
    final Attribute attribute = compareRowObject.getAttribute();
    if (matchText(attribute.getNameEng()) || matchText(attribute.getNameGer())) {
      return true;
    }

    return matchText(attribute.getDescriptionEng()) || matchText(attribute.getDescriptionGer());

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
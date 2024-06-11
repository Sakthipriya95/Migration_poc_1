/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.table.filters;

import java.util.List;
import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixAttributeClientBO;
import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixUseCaseItem;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * This class is for the type filter that will filter rows by matching text in all columns
 *
 * @author mkl2cob
 */
public class AllFMColumnFilterMatcher<E> implements Matcher<E> {

  /**
   * Pattern instance
   */
  private Pattern pattern1;

  /**
   * boolean to indicate whether we need to search with patterns or not
   */
  private boolean pattern2Search;

  /**
   * Pattern instance
   */
  private Pattern pattern2;

  /**
   * setting the pattern for search
   *
   * @param textToFilter String
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if (CommonUtils.isEmptyString(textToFilter) || !setPattern) {
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
      if (!CommonUtils.isEmptyString(curString)) {
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
    if (element instanceof FocusMatrixAttributeClientBO) {
      // get the row object
      FocusMatrixAttributeClientBO focusMatrixAttribute = (FocusMatrixAttributeClientBO) element;

      final Attribute attribute = focusMatrixAttribute.getAttribute();

      // check if attr name matches with the entered text
      if (matchText(attribute.getNameEng()) || matchText(attribute.getNameGer())) {
        return true;
      }

      // check if attr desc matches with the entered text
      if (matchText(attribute.getDescriptionEng()) || matchText(attribute.getDescriptionGer())) {
        return true;
      }

      List<FocusMatrixUseCaseItem> fmUseCaseItemList = focusMatrixAttribute.getFmUseCaseItemList();
      for (FocusMatrixUseCaseItem focusMatrixUseCaseItem : fmUseCaseItemList) {
        if (matchText(focusMatrixUseCaseItem.getFilterText(attribute).getFilterTxt())) {
          return true;
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

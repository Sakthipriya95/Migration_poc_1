/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.table.filters;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseEditorRowAttr;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author jvi6cob
 */
public class AllUCColumnFilterMatcher<E> implements Matcher<E> {


  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;

  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
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
  public boolean matches(final E element) {

    if (element instanceof UseCaseEditorRowAttr) {
      UseCaseEditorRowAttr useCaseNatInput = (UseCaseEditorRowAttr) element;
      // ICdm-590
      final AttributeClientBO attributeBO = useCaseNatInput.getAttributeBO();
      if (matchText(attributeBO.getAttribute().getNameEng()) || matchText(attributeBO.getAttribute().getNameGer())) {
        return true;
      }

      if (matchText(attributeBO.getAttribute().getDescriptionEng()) ||
          matchText(attributeBO.getAttribute().getDescriptionGer())) {
        return true;
      }

      // ICDM-2228
      if (matchText(useCaseNatInput.getAttrCreatedDateFormatted())) {
        return true;
      }
      // match Attribute Class
      if (matchText(useCaseNatInput.getAttrClassName())) {
        return true;
      }

    }

    return false;
  }

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

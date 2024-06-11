/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author RDP2COB
 */
public class DataAssessmentWprkpackageColumnFilterMatcher<E extends DaWpResp> implements Matcher<E> {

  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E element) {
    if (element instanceof DaWpResp) {
      final DaWpResp dataAssessmentWorkpackage = element;

      if (matchText(dataAssessmentWorkpackage.getHexRvwEqualFlag())) {
        return true;
      }
      if (matchText(dataAssessmentWorkpackage.getWpReadyForProductionFlag())) {
        return true;
      }
      if (matchText(WpRespType.getType(dataAssessmentWorkpackage.getA2lRespType()).getDispName())) {
        return true;
      }
      if (matchText(dataAssessmentWorkpackage.getParameterReviewedFlag())) {
        return true;
      }
      if (matchText(CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP
          .getTypeByDbCode(dataAssessmentWorkpackage.getQnairesAnsweredFlag()).getUiType())) {
        return true;
      }
      if (matchText(dataAssessmentWorkpackage.getA2lRespName())) {
        return true;
      }
      if (matchText(dataAssessmentWorkpackage.getA2lWpName())) {
        return true;
      }
      if (matchText(dataAssessmentWorkpackage.getWpFinishedFlag())) {
        return true;
      }

    }
    return false;
  }


  /**
   * @param textToFilter text entered in type filter
   * @param setPattern boolean
   */
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

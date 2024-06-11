/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author ajk2cob
 */
public class DataAssessmentQnaireResColumnFilterMatcher<E extends DataAssessmentQuestionnaires> implements Matcher<E> {

  /**
   * Pattern 1
   */
  private Pattern pattern1;
  /**
   * Pattern 2
   */
  private Pattern pattern2;
  /**
   * word level pattern matching
   */
  private boolean pattern2Search;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E element) {
    if (element != null) {
      final DataAssessmentQuestionnaires dataAssessmentBaselineDetails = element;

      return filterColumnData(dataAssessmentBaselineDetails);
    }
    return false;
  }

  /**
   * @param dataAssessmentBaselineDetails
   */
  private boolean filterColumnData(final DataAssessmentQuestionnaires dataAssessmentBaselineDetails) {
    boolean isColumDataMatch = false;
    if (matchText(dataAssessmentBaselineDetails.getA2lWpName())) {
      return !isColumDataMatch;
    }
    if (matchText(dataAssessmentBaselineDetails.getA2lRespName())) {
      return !isColumDataMatch;
    }
    if (matchText(WpRespType.getType(dataAssessmentBaselineDetails.getA2lRespType()).getDispName())) {
      return !isColumDataMatch;
    }
    if (matchText(dataAssessmentBaselineDetails.getQnaireRespName())) {
      return !isColumDataMatch;
    }
    if (matchText(CommonUtils.getBooleanCode(dataAssessmentBaselineDetails.isQnaireReadyForProd()))) {
      return !isColumDataMatch;
    }
    if (matchText(CommonUtils.getBooleanCode(dataAssessmentBaselineDetails.isQnaireBaselineExisting()))) {
      return !isColumDataMatch;
    }
    if (matchText(Integer.toString(dataAssessmentBaselineDetails.getQnairePositiveAnsCount()))) {
      return !isColumDataMatch;
    }
    if (matchText(Integer.toString(dataAssessmentBaselineDetails.getQnaireNegativeAnsCount()))) {
      return !isColumDataMatch;
    }
    if (matchText(Integer.toString(dataAssessmentBaselineDetails.getQnaireNeutralAnsCount()))) {
      return !isColumDataMatch;
    }
    if (matchText(dataAssessmentBaselineDetails.getQnaireRespVersName())) {
      return !isColumDataMatch;
    }
    if (matchText(dataAssessmentBaselineDetails.getQnaireRvdUserDisplayName())) {
      return !isColumDataMatch;
    }
    if (matchText(dataAssessmentBaselineDetails.getQnaireReviewedDate())) {
      return !isColumDataMatch;
    }
    if (matchText(dataAssessmentBaselineDetails.getQnaireBaselineLinkDisplayText())) {
      return !isColumDataMatch;
    }
    return isColumDataMatch;
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

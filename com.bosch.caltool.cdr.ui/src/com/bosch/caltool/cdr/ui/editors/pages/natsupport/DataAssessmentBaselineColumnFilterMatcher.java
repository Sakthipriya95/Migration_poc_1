/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author ajk2cob
 */
public class DataAssessmentBaselineColumnFilterMatcher<E extends DaDataAssessment> implements Matcher<E> {

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
      final DaDataAssessment dataAssessmentBaselineDetails = element;

      if (matchText(dataAssessmentBaselineDetails.getBaselineName())) {
        return true;
      }
      if (matchText(dataAssessmentBaselineDetails.getVariantName())) {
        return true;
      }
      if (matchText(CDRConstants.FILE_ARCHIVAL_STATUS
          .getTypeByDbCode(dataAssessmentBaselineDetails.getFileArchivalStatus()).toString())) {
        return true;
      }
      if (matchText(dataAssessmentBaselineDetails.getDescription())) {
        return true;
      }
      if (matchText(dataAssessmentBaselineDetails.getCreatedDate())) {
        return true;
      }
      if (matchText(DataAssmntReportDataHandler
          .getTypeOfAssessmentDisplayText(dataAssessmentBaselineDetails.getTypeOfAssignment()))) {
        return true;
      }
      if (matchText(CommonUtilConstants.EMPTY_STRING)) {
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

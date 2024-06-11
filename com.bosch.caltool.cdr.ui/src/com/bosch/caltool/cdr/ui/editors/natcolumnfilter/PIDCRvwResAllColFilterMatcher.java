/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.natcolumnfilter;

import java.util.regex.Pattern;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewResultData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * ICDM-1764 all column matcher for pidc review results editor
 *
 * @author mkl2cob
 */
public class PIDCRvwResAllColFilterMatcher<E extends ReviewVariantModel> implements Matcher<E> {

  private Pattern pattern1;
  private boolean pattern2Search;
  private Pattern pattern2;

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E arg0) {
    if (matchDateA2LVers(arg0)) {
      // return true if text matches with result date,a2l version,variant name
      return true;
    }
    if (matchDescTypeStatus(arg0)) {
      // return true if text matches with review description , type , status
      return true;
    }
    if (matchScopeWPNameLock(arg0)) {
      // return true if text matches with review scope and scope name
      return true;
    }
    try {
      if (matchCalEnggAndAuditor(arg0)) {
        // return true if text matches with Calibration Engineer and Auditor
        return true;
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    // return true if text matches with parent review and rule set
    return (matchBaseParentRvwAndRuleSet(arg0));
  }

  /**
   * @param arg0
   * @return true if text matches with parent review and rule sets
   */
  private boolean matchBaseParentRvwAndRuleSet(final E arg0) {
    return matchText(computeBaseRvw(arg0.getReviewResultData())) || matchText(getParentReview(arg0)) ||
        matchText(getRulesetName(arg0));
  }

  /**
   * @param arg0
   * @return
   */
  private String getRulesetName(final E arg0) {
    return null == arg0.getReviewResultData().getRuleSetName() ? "" : arg0.getReviewResultData().getRuleSetName();
  }

  /**
   * @param arg0
   * @return
   */
  private String getParentReview(final E arg0) {
    return null == arg0.getReviewResultData().getParentReview() ? "" : arg0.getReviewResultData().getParentReview();
  }

  /**
   * @param arg0
   * @return true if text matches with Calibration Engineer and Auditor
   * @throws ApicWebServiceException
   */
  private boolean matchCalEnggAndAuditor(final E arg0) throws ApicWebServiceException {
    return matchText(
        null == arg0.getReviewResultData().getCalEngineer() ? "" : arg0.getReviewResultData().getCalEngineer()) ||
        matchText(null == arg0.getReviewResultData().getAuditor() ? "" : arg0.getReviewResultData().getAuditor());
  }

  /**
   * @param arg0
   * @return true if text matches with review scope and scope name
   */
  private boolean matchScopeWPNameLock(final E arg0) {
    return matchText(arg0.getReviewResultData().getCdrSourceType().toString()) ||
        matchText(arg0.getReviewResultData().getCdrReviewResult().getGrpWorkPkg()) ||
        matchText(CDRConstants.REVIEW_LOCK_STATUS
            .getType(arg0.getReviewResultData().getCdrReviewResult().getLockStatus()).toString());
  }

  /**
   * @param arg0
   * @return true if text matches with review description , type , status
   */
  private boolean matchDescTypeStatus(final E arg0) {
    return matchText(arg0.getReviewResultData().getCdrReviewResult().getCreatedDate()) ||
        matchText(arg0.getReviewResultData().getPverName()) ||
        matchText(arg0.getReviewResultData().getPidcVariantName());
  }

  /**
   * @param arg0
   * @return true if text matches with result date,a2l version,variant name
   */
  private boolean matchDateA2LVers(final E arg0) {
    return matchText(arg0.getReviewResultData().getPverName()) ||
        matchText(arg0.getReviewResultData().getCdrReviewResult().getDescription()) ||
        matchText(CDRConstants.REVIEW_TYPE.getType(arg0.getReviewResultData().getCdrReviewResult().getReviewType())
            .toString()) ||
        matchText(CDRConstants.REVIEW_STATUS.getType(arg0.getReviewResultData().getCdrReviewResult().getRvwStatus())
            .toString());
  }

  /**
   * @param textToFilter filter text
   * @param setPattern boolean
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || CommonUIConstants.EMPTY_STRING.equals(textToFilter) || !setPattern) {
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
   * @param text
   * @return true if the pattern matches the text
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

  /**
   * @param cdrResult CDRResult
   * @return
   */
  private String computeBaseRvw(final ReviewResultData cdrResult) {
    String result = "";
    if (cdrResult.getParentReview() == null) {
      // if the result does not have a parent review
      result = "";
    }
    else {
      // if the result has a parent review
      if (cdrResult.getBaseReview() != null) {
        result = cdrResult.getBaseReview();
      }
    }
    return result;
  }

}

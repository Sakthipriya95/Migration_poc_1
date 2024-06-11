/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_LOCK_STATUS;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author mkl2cob
 */
public class CdrReportToolBarFilters {

  /**
   * CdrReportData
   */
  private final CdrReportDataHandler cdrRprtData;

  // ICDM-2439
  /**
   * paramter - compliance
   */
  private boolean complianceParam = true;

  // ICDM-2439
  /**
   * paramter - non compliance
   */
  private boolean nonComplianceParam = true;

  /**
   * paramter review filter
   */
  private boolean paramRvwd = true;

  /**
   * parameters not reviewed filter
   */
  private boolean paramNotRvwd = true;
  // ICDM-2045
  /**
   * parameter latest fun
   */
  private boolean paramLatestFunc = true;

  /**
   * filter flag to check locked latest review
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  private boolean lockedLatestRvw = true;

  /**
   * filter flag to check un-locked latest review
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  private boolean unLockedLatestRvw = true;

  /**
   * filter flag to check start type latest review
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  private boolean startTypeLatestRvw = true;

  /**
   * filter flag to check official type latest review
   */
  // ICDM-2585 (Parent Task ICDM-2412)
  private boolean officialTypeLatestRvw = true;


  /**
   * parameter does not match latest fun
   */
  private boolean paramNoLatestFunc = true;


  /**
   * parameter never reviewed
   */
  private boolean paramNeverRvwd = true;


  private boolean readOnlyParam = true;

  private boolean notReadOnlyParams = true;

  private boolean dependantCharParam = true;

  private boolean noDependantCharParam = true;

  private boolean qSSDParams = true;

  private boolean nonQSSDParams = true;

  // for rules predefined filter
  private boolean showFullFilledRules = true;
  private boolean showNotFullFilledRules = true;
  private boolean showUnDefinedRules = true;
  private boolean wpFinished = true;
  private boolean wpNotFinished = true;


  /**
   * @param cdrReportData CdrReportData
   */
  public CdrReportToolBarFilters(final CdrReportDataHandler cdrReportData) {
    this.cdrRprtData = cdrReportData;
  }

  /**
   * @author mkl2cob
   * @param <E>
   */
  private class RvwReportToolBarMatcher<E> implements Matcher<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final E arg0) {
      A2LParameter a2lParam = (A2LParameter) arg0;

      if (!filterMandatValues(a2lParam)) {
        return false;
      }

      if (!filterNonReadVal(a2lParam)) {
        return false;
      }
      if (!filterNoDepandantCharParams(a2lParam)) {
        return false;
      }
      // ICDM-2045
      if (!filterFuncVerMatch(a2lParam)) {
        return false;
      }
      // Filter based on review rules
      if (!filterReviewBasedOnRules(a2lParam)) {
        return false;
      }

      // ICDM-2439
      if (!filterComplianceParam(a2lParam)) {
        return false;
      }

      // ICDM-2439
      if (!filterNonComplianceParam(a2lParam)) {
        return false;
      }
      if (!filterQSSDParam(a2lParam)) {
        return false;
      }
      if (!filterNonQSSDParam(a2lParam)) {
        return false;
      }

      // ICDM-2585 (Parent Task ICDM-2412)
      return filterLatestRvw(a2lParam);

    }

    /**
     * @param a2lParam
     * @return
     */
    private boolean filterNonQSSDParam(final A2LParameter a2lParam) {
      // non qSSD Filter is inactive then don't show non QSSD parameters
      return !(!CdrReportToolBarFilters.this.nonQSSDParams && !a2lParam.isQssdParameter());
    }

    /**
     * @param a2lParam
     * @return
     */
    private boolean filterQSSDParam(final A2LParameter a2lParam) {
      // qSSD Filter is inactive then don't show QSSD parameters
      return !(!CdrReportToolBarFilters.this.qSSDParams && a2lParam.isQssdParameter());
    }

    /**
     * @param a2lParam
     * @return
     */
    private boolean filterNonReadVal(final A2LParameter a2lParam) {
      if (!CdrReportToolBarFilters.this.readOnlyParam && a2lParam.getCharacteristic().isReadOnly()) {
        return false;
      }
      return CdrReportToolBarFilters.this.notReadOnlyParams || a2lParam.getCharacteristic().isReadOnly();
    }

    /**
     * @param a2lParam
     * @return
     */
    private boolean filterNoDepandantCharParams(final A2LParameter a2lParam) {
      if (!CdrReportToolBarFilters.this.dependantCharParam &&
          a2lParam.getCharacteristic().isDependentCharacteristic()) {
        return false;
      }
      return CdrReportToolBarFilters.this.noDependantCharParam ||
          a2lParam.getCharacteristic().isDependentCharacteristic();
    }

    // ICDM-2439
    /**
     * @param a2lParam
     * @return
     */
    private boolean filterComplianceParam(final A2LParameter a2lParam) {
      return CdrReportToolBarFilters.this.complianceParam || !a2lParam.isComplianceParam();
    }

    // ICDM-2439
    /**
     * @param a2lParam
     * @return
     */
    private boolean filterNonComplianceParam(final A2LParameter a2lParam) {
      return CdrReportToolBarFilters.this.nonComplianceParam || a2lParam.isComplianceParam();
    }

    // ICDM-2585 (Parent Task ICDM-2412)
    private boolean filterLockedLatestRvw(final A2LParameter a2lParam) {
      ReviewDetails reviewDetails = CdrReportToolBarFilters.this.cdrRprtData.getReviewDetailsLatest(a2lParam.getName());
      if (null == reviewDetails) {
        return true;
      }
      REVIEW_LOCK_STATUS lockStatus = REVIEW_LOCK_STATUS.getType(reviewDetails.getLockStatus());
      return CdrReportToolBarFilters.this.lockedLatestRvw || (lockStatus == REVIEW_LOCK_STATUS.NO);
    }

    // ICDM-2585 (Parent Task ICDM-2412)
    private boolean filterUnLockedLatestRvw(final A2LParameter a2lParam) {
      ReviewDetails reviewDetails = CdrReportToolBarFilters.this.cdrRprtData.getReviewDetailsLatest(a2lParam.getName());
      if (null == reviewDetails) {
        return true;
      }
      REVIEW_LOCK_STATUS lockStatus = REVIEW_LOCK_STATUS.getType(reviewDetails.getLockStatus());
      return CdrReportToolBarFilters.this.unLockedLatestRvw || (lockStatus == REVIEW_LOCK_STATUS.YES);
    }

    // ICDM-2585 (Parent Task ICDM-2412)
    private boolean filterStartTypeLatestRvw(final A2LParameter a2lParam) {
      ReviewDetails reviewDetails = CdrReportToolBarFilters.this.cdrRprtData.getReviewDetailsLatest(a2lParam.getName());
      if (null == reviewDetails) {
        return true;
      }
      REVIEW_TYPE rvwType = CDRConstants.REVIEW_TYPE.getType(reviewDetails.getReviewType());
      return CdrReportToolBarFilters.this.startTypeLatestRvw || (rvwType != CDRConstants.REVIEW_TYPE.START);
    }

    // ICDM-2585 (Parent Task ICDM-2412)
    private boolean filterLatestRvw(final A2LParameter a2lParam) {

      // ICDM-2585 (Parent Task ICDM-2412)
      if (!filterLockedLatestRvw(a2lParam)) {
        return false;
      }

      // ICDM-2585 (Parent Task ICDM-2412)
      if (!filterUnLockedLatestRvw(a2lParam)) {
        return false;
      }

      // ICDM-2585 (Parent Task ICDM-2412)
      if (!filterStartTypeLatestRvw(a2lParam)) {
        return false;
      }

      if (!filterWpFinished(a2lParam)) {
        return false;
      }

      if (!filterWpNotFinished(a2lParam)) {
        return false;
      }

      return filterOfficialTypeLatestRvw(a2lParam);
    }

    // ICDM-2585 (Parent Task ICDM-2412)
    private boolean filterOfficialTypeLatestRvw(final A2LParameter a2lParam) {

      ReviewDetails reviewDetails = CdrReportToolBarFilters.this.cdrRprtData.getReviewDetailsLatest(a2lParam.getName());
      if (null == reviewDetails) {
        return true;
      }
      REVIEW_TYPE rvwType = CDRConstants.REVIEW_TYPE.getType(reviewDetails.getReviewType());
      return CdrReportToolBarFilters.this.officialTypeLatestRvw || (rvwType != CDRConstants.REVIEW_TYPE.OFFICIAL);
    }

    /**
     * @param a2lParam
     * @return
     */
    private boolean filterFuncVerMatch(final A2LParameter a2lParam) {
      if (!CdrReportToolBarFilters.this.paramLatestFunc &&
          CdrReportToolBarFilters.this.cdrRprtData.isLatestVersMatch(a2lParam)) {
        return false;
      }
      return CdrReportToolBarFilters.this.paramNoLatestFunc ||
          CdrReportToolBarFilters.this.cdrRprtData.isLatestVersMatch(a2lParam);
    }

    /**
     * @param a2lParam
     * @return
     */
    private boolean filterMandatValues(final A2LParameter a2lParam) {

      // ICDM-2585 (Parent Task ICDM-2412)-2
      String reviewedStr = CdrReportToolBarFilters.this.cdrRprtData.isReviewedStr(a2lParam.getName());

      if (!CdrReportToolBarFilters.this.paramRvwd && ApicConstants.REVIEWED.equals(reviewedStr)) {
        return false;
      }

      if (!CdrReportToolBarFilters.this.paramNotRvwd && ApicConstants.NOT_REVIEWED.equals(reviewedStr)) {
        return false;
      }

      return CdrReportToolBarFilters.this.paramNeverRvwd ||
          CommonUtils.isNotEqual(ApicConstants.NEVER_REVIEWED, reviewedStr);
    }

    private boolean filterReviewBasedOnRules(final A2LParameter a2lParam) {
      String latestReviewResult = CdrReportToolBarFilters.this.cdrRprtData.getLatestReviewResult(a2lParam.getName());
      // to display the Never Reviwed parameters
      if (null == latestReviewResult) {
        return true;
      }
      // for fulfilled rules
      if ((!isShowFullFilledRules()) && CDRConstants.RESULT_FLAG.OK.getUiType().equals(latestReviewResult)) {
        return false;
      }
      // for not fulfilled rules
      if ((!isShowNotFullFilledRules()) && isResultInvalid(latestReviewResult)) {
        return false;
      }
      // for undefined rules
      return isShowUnDefinedRules() ||
          CommonUtils.isNotEqual(CDRConstants.RESULT_FLAG.NOT_REVIEWED.getUiType(), latestReviewResult);
    }

    /** for WP finished params */
    private boolean filterWpFinished(final A2LParameter a2lParam) {

      String latestReviewResult =
          CdrReportToolBarFilters.this.cdrRprtData.getWpFinishedRespStatus(a2lParam.getParamId());

      return CdrReportToolBarFilters.this.wpFinished ||
          (CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType().equals(latestReviewResult));

    }

    /** for WP not finished params */

    private boolean filterWpNotFinished(final A2LParameter a2lParam) {

      String latestReviewResult =
          CdrReportToolBarFilters.this.cdrRprtData.getWpFinishedRespStatus(a2lParam.getParamId());

      return CdrReportToolBarFilters.this.wpNotFinished ||
          CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType().equals(latestReviewResult);


    }

    /**
     * @param latestReviewResult
     * @return
     */
    private boolean isResultInvalid(final String latestReviewResult) {
      return isResultHogOrLowOrNotok(latestReviewResult) ||
          CDRConstants.RESULT_FLAG.SHAPE.getUiType().equals(latestReviewResult) ||
          CDRConstants.RESULT_FLAG.CHECKED.getUiType().equals(latestReviewResult);
    }

    /**
     * @param latestReviewResult
     * @return
     */
    private boolean isResultHogOrLowOrNotok(final String latestReviewResult) {
      return (CDRConstants.RESULT_FLAG.HIGH.getUiType().equals(latestReviewResult)) ||
          CDRConstants.RESULT_FLAG.LOW.getUiType().equals(latestReviewResult) ||
          CDRConstants.RESULT_FLAG.NOT_OK.getUiType().equals(latestReviewResult);
    }
  }


  /**
   * @param paramRvwd the paramRvwd to set
   */
  public void setParamRvwd(final boolean paramRvwd) {
    this.paramRvwd = paramRvwd;
  }

  /**
   * @param paramNotRvwd the paramNotRvwd to set
   */
  public void setParamNotRvwd(final boolean paramNotRvwd) {
    this.paramNotRvwd = paramNotRvwd;
  }


  /**
   * @return the cdrRprtData
   */
  public CdrReportDataHandler getCdrRprtData() {
    return this.cdrRprtData;
  }

  // ICDM-2045
  /**
   * @param paramLatestFunc the paramLatestFunc to set
   */
  public void setParamLatestFunc(final boolean paramLatestFunc) {
    this.paramLatestFunc = paramLatestFunc;
  }


  /**
   * @param paramNoLatestFunc the paramNoLatestFunc to set
   */
  public void setParamNoLatestFunc(final boolean paramNoLatestFunc) {
    this.paramNoLatestFunc = paramNoLatestFunc;
  }

  /**
   * @return Matcher
   */
  public Matcher<A2LParameter> getToolBarMatcher() {
    return new RvwReportToolBarMatcher<>();
  }


  /**
   * @param paramNeverRvwd paramNeverRvwd
   */
  public void setParamNeverRvwd(final boolean paramNeverRvwd) {
    this.paramNeverRvwd = paramNeverRvwd;

  }

  // ICDM-2439
  /**
   * @param complianceParam the complianceParam to set
   */
  public void setComplianceParam(final boolean complianceParam) {
    this.complianceParam = complianceParam;
  }

  // ICDM-2439
  /**
   * @param nonComplianceParam the nonComplianceParam to set
   */
  public void setNonComplianceParam(final boolean nonComplianceParam) {
    this.nonComplianceParam = nonComplianceParam;
  }

  // ICDM-2585 (Parent Task ICDM-2412)
  /**
   * Set the locked latest review flag
   *
   * @param lockedLatestRvw the locked latest reivew flag
   */
  public void setLockedLatestRvw(final boolean lockedLatestRvw) {
    this.lockedLatestRvw = lockedLatestRvw;
  }

  // ICDM-2585 (Parent Task ICDM-2412)
  /**
   * Set the un-locked latest review flag
   *
   * @param unLockedLatestRvw the un-locked latest reivew flag
   */
  public void setUnLockedLatestRvw(final boolean unLockedLatestRvw) {
    this.unLockedLatestRvw = unLockedLatestRvw;
  }

  // ICDM-2585 (Parent Task ICDM-2412)
  /**
   * Set the start type latest review flag
   *
   * @param startTypeLatestRvw the start type latest reivew flag
   */
  public void setStartTypeLatestRvw(final boolean startTypeLatestRvw) {
    this.startTypeLatestRvw = startTypeLatestRvw;
  }

  // ICDM-2585 (Parent Task ICDM-2412)
  /**
   * Set the official type latest review flag
   *
   * @param officialTypeLatestRvw the official type latest reivew flag
   */
  public void setOfficialTypeLatestRvw(final boolean officialTypeLatestRvw) {
    this.officialTypeLatestRvw = officialTypeLatestRvw;
  }


  /**
   * @return the readOnlyParam
   */
  public boolean isReadOnlyParam() {
    return this.readOnlyParam;
  }


  /**
   * @param readOnlyParam the readOnlyParam to set
   */
  public void setReadOnlyParam(final boolean readOnlyParam) {
    this.readOnlyParam = readOnlyParam;
  }


  /**
   * @param notReadOnlyParams notReadOnlyParams
   */
  public void setNotReadOnlyParam(final boolean notReadOnlyParams) {
    this.notReadOnlyParams = notReadOnlyParams;

  }


  /**
   * @return boolean
   */
  public boolean isqSSDParams() {
    return this.qSSDParams;
  }


  /**
   * @param qSSDParams boolean
   */
  public void setqSSDParams(final boolean qSSDParams) {
    this.qSSDParams = qSSDParams;
  }


  /**
   * @return boolean
   */
  public boolean isNonQSSDParams() {
    return this.nonQSSDParams;
  }


  /**
   * @param nonQSSDParams boolean
   */
  public void setNonQSSDParams(final boolean nonQSSDParams) {
    this.nonQSSDParams = nonQSSDParams;
  }


  /**
   * @return the showNotFullFilledRules
   */
  public boolean isShowNotFullFilledRules() {
    return this.showNotFullFilledRules;
  }


  /**
   * @param showNotFullFilledRules the showNotFullFilledRules to set
   */
  public void setShowNotFullFilledRules(final boolean showNotFullFilledRules) {
    this.showNotFullFilledRules = showNotFullFilledRules;
  }


  /**
   * @return the showFullFilledRules
   */
  public boolean isShowFullFilledRules() {
    return this.showFullFilledRules;
  }


  /**
   * @param showFullFilledRules the showFullFilledRules to set
   */
  public void setShowFullFilledRules(final boolean showFullFilledRules) {
    this.showFullFilledRules = showFullFilledRules;
  }


  /**
   * @return the showUnDefinedRules
   */
  public boolean isShowUnDefinedRules() {
    return this.showUnDefinedRules;
  }


  /**
   * @param showUnDefinedRules the showUnDefinedRules to set
   */
  public void setShowUnDefinedRules(final boolean showUnDefinedRules) {
    this.showUnDefinedRules = showUnDefinedRules;
  }

  /**
   * @return the dependantCharParam
   */
  public boolean isDependantCharParam() {
    return this.dependantCharParam;
  }

  /**
   * @param dependantCharParam the dependantCharParam to set
   */
  public void setDependantCharParam(final boolean dependantCharParam) {
    this.dependantCharParam = dependantCharParam;
  }

  /**
   * @return the noDependantCharParam
   */
  public boolean isNoDependantCharParam() {
    return this.noDependantCharParam;
  }

  /**
   * @param noDependantCharParam the noDependantCharParam to set
   */
  public void setNoDependantCharParam(final boolean noDependantCharParam) {
    this.noDependantCharParam = noDependantCharParam;
  }


  /**
   * @return the wpFinished
   */
  public boolean isWpFinished() {
    return this.wpFinished;
  }


  /**
   * @param wpFinished the wpFinished to set
   */
  public void setWpFinished(final boolean wpFinished) {
    this.wpFinished = wpFinished;
  }


  /**
   * @return the wpNotFinished
   */
  public boolean isWpNotFinished() {
    return this.wpNotFinished;
  }


  /**
   * @param wpNotFinished the wpNotFinished to set
   */
  public void setWpNotFinished(final boolean wpNotFinished) {
    this.wpNotFinished = wpNotFinished;
  }


}

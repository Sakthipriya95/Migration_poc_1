/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CompliResValues;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author mkl2cob
 */
public class CompHexWithCdfToolBarFilter {

  /**
   * true if the match latest function version predefined filter is set
   */
  private boolean matchLatestFuncVers = true;
  /**
   * true if the dont match latest function version predefined filter is set
   */
  private boolean notLatestFuncVers = true;
  /**
   * QSSD Flag initially True since the tool bar action is checked
   */
  private boolean qSSDFlag = true;
  /**
   * non QSSD Flag initially True since the tool bar action is checked
   */
  private boolean nonQSSDFlag = true;
  /**
   * non compliance Flag initially True since the tool bar action is checked
   */
  private boolean complianceFlag = true;

  /**
   * nonComplianceFlag Flag initially True since the tool bar action is checked
   */

  private boolean nonComplianceFlag = true;
  /**
   * Result COMPLI Flag initially True since the tool bar action is checked
   */
  private boolean resultCompliFlag = true;
  /**
   * Result OK Flag initially True since the tool bar action is checked
   */
  private boolean okFlag = true;
  /**
   * Result NOTOK Flag initially True since the tool bar action is checked
   */
  private boolean notApplicableFlag = true;


  private boolean readOnlyFlag = true;


  private boolean notReadOnlyFlag = true;

  private boolean dependantCharParam = true;

  private boolean noDependantCharParam = true;

  private boolean blackListFlag = true;
  private boolean nonBlackListFlag = true;

  private boolean wpFinishedFlag = true;
  private boolean wpNotFinishedFlag = true;
  private final CompHexWithCDFxDataHandler rprtData;

  /**
   * @param reportData reportData
   */
  public CompHexWithCdfToolBarFilter(final CompHexWithCDFxDataHandler reportData) {
    this.rprtData = reportData;
  }

  private class CompHexWithCDFToolBarFilterMatcher<E> implements Matcher<E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final E arg0) {
      CompHexWithCDFParam param = (CompHexWithCDFParam) arg0;
      if (!filterFuncVerMatch(param)) {
        return false;
      }
      if (!filterCompMatch(param)) {
        return false;
      }
      if (!filterQSSDParam(param)) {
        return false;
      }
      // check if the parameter review results matches unpressed filter
      if (!checkReviewResults(param)) {
        return false;
      }
      if (!checkReadOnly(param)) {
        return false;
      }
      if (!checkDependantChar(param)) {
        return false;
      }

      if (!filterWpFinished(param)) {
        return false;
      }

      if (!filterWpNotFinished(param)) {
        return false;
      }
      return checkBlackList(param);
    }


    /**
     * @param param
     * @return
     */
    private boolean checkDependantChar(final CompHexWithCDFParam param) {
      if ((!CompHexWithCdfToolBarFilter.this.dependantCharParam) && (param.isDependantCharacteristic())) {
        return false;
      }
      return CompHexWithCdfToolBarFilter.this.noDependantCharParam || param.isDependantCharacteristic();
    }

    /** for WP not finished params */

    private boolean filterWpNotFinished(final CompHexWithCDFParam param) {
      String latestReviewResult = CompHexWithCdfToolBarFilter.this.rprtData.getCdrReportData()
          .getWpFinishedRespStatuswithName(param.getParamName());

      return CompHexWithCdfToolBarFilter.this.wpNotFinishedFlag ||
          (CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType().equals(latestReviewResult));


    }

    /** for WP finished params */
    private boolean filterWpFinished(final CompHexWithCDFParam param) {
      String latestReviewResult = CompHexWithCdfToolBarFilter.this.rprtData.getCdrReportData()
          .getWpFinishedRespStatuswithName(param.getParamName());

      return CompHexWithCdfToolBarFilter.this.wpFinishedFlag ||
          (CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType().equals(latestReviewResult));
    }


    /**
     * @param param
     * @return
     */
    private boolean checkBlackList(final CompHexWithCDFParam param) {
      if ((!CompHexWithCdfToolBarFilter.this.blackListFlag) && (param.isBlackList())) {
        return false;
      }

      return CompHexWithCdfToolBarFilter.this.nonBlackListFlag || param.isBlackList();
    }


    /**
     * @param param
     * @return
     */
    private boolean checkReadOnly(final CompHexWithCDFParam param) {
      if ((!CompHexWithCdfToolBarFilter.this.readOnlyFlag) && (param.isReadOnly())) {
        return false;
      }
      return CompHexWithCdfToolBarFilter.this.notReadOnlyFlag || param.isReadOnly();
    }

    /**
     * @param param
     * @return
     */
    private boolean filterFuncVerMatch(final CompHexWithCDFParam param) {
      if (!CompHexWithCdfToolBarFilter.this.matchLatestFuncVers &&
          CommonUtils.isEqual(param.getLatestFunctionVersion(), param.getFuncVers())) {
        return false;
      }
      return CompHexWithCdfToolBarFilter.this.notLatestFuncVers ||
          CommonUtils.isEqual(param.getLatestFunctionVersion(), param.getFuncVers());
    }

    /**
     * @param param
     * @return
     */
    private boolean filterCompMatch(final CompHexWithCDFParam param) {
      if ((!CompHexWithCdfToolBarFilter.this.complianceFlag) && (param.isCompli())) {
        return false;
      }
      return CompHexWithCdfToolBarFilter.this.nonComplianceFlag || param.isCompli();
    }

    /**
     * @param param CompHexWithCDFParam
     * @return
     */
    private boolean filterQSSDParam(final CompHexWithCDFParam param) {
      if ((!CompHexWithCdfToolBarFilter.this.qSSDFlag) && (param.isQssdParameter())) {
        return false;
      }
      return CompHexWithCdfToolBarFilter.this.nonQSSDFlag || param.isQssdParameter();
    }


    /**
     * @param param
     */
    private boolean checkReviewResults(final CompHexWithCDFParam param) {
      /**
       * Result compli flag Action Unchecked then do not show Result Compli/shape
       */
      if ((!CompHexWithCdfToolBarFilter.this.resultCompliFlag) &&
          (!param.getCompliResult().getUiValue().equals(CompliResValues.NA.getUiValue()) &&
              !param.getCompliResult().getUiValue().equals(CompliResValues.OK.getUiValue()))) {
        return false;
      }
      /**
       * Result Ok flag Action Unchecked then do not show Result Ok
       */
      if ((!CompHexWithCdfToolBarFilter.this.okFlag) &&
          (param.getCompliResult().getUiValue().equals(CompliResValues.OK.getUiValue()))) {
        return false;
      }
      /**
       * Result NotOk flag Action Unchecked then do not show no rule
       */

      return CompHexWithCdfToolBarFilter.this.notApplicableFlag ||
          CommonUtils.isNotEqual(param.getCompliResult().getUiValue(), CompliResValues.NA.getUiValue());
    }

  }


  /**
   * @param complianceFlag the complianceFlag to set
   */
  public void setComplianceFlag(final boolean complianceFlag) {
    this.complianceFlag = complianceFlag;
  }

  /**
   * @param nonComplianceFlag the nonComplianceFlag to set
   */
  public void setNonComplianceFlag(final boolean nonComplianceFlag) {
    this.nonComplianceFlag = nonComplianceFlag;
  }

  /**
   * @return Matcher
   */
  public Matcher<CompHexWithCDFParam> getToolBarMatcher() {
    return new CompHexWithCDFToolBarFilterMatcher<>();
  }

  /**
   * @param matchLatestFuncVers boolean
   */
  public void setParamLatestFunc(final boolean matchLatestFuncVers) {
    this.matchLatestFuncVers = matchLatestFuncVers;

  }

  /**
   * @param notLatestFuncVers boolean
   */
  public void setParamNoLatestFunc(final boolean notLatestFuncVers) {
    this.notLatestFuncVers = notLatestFuncVers;

  }


  /**
   * @return the resultCompliFlag
   */
  public boolean isResultCompliFlag() {
    return this.resultCompliFlag;
  }


  /**
   * @param resultCompliFlag the resultCompliFlag to set
   */
  public void setResultCompliFlag(final boolean resultCompliFlag) {
    this.resultCompliFlag = resultCompliFlag;
  }


  /**
   * @return the okFlag
   */
  public boolean isOkFlag() {
    return this.okFlag;
  }


  /**
   * @param okFlag the okFlag to set
   */
  public void setOkFlag(final boolean okFlag) {
    this.okFlag = okFlag;
  }


  /**
   * @return the NA
   */
  public boolean isNotApplicableFlag() {
    return this.notApplicableFlag;
  }


  /**
   * @param naFlag the NA to set
   */
  public void setNotApplicableFlag(final boolean naFlag) {
    this.notApplicableFlag = naFlag;
  }

  /**
   * @param readOnlyFlag boolean
   */
  public void setReadOnlyFlag(final boolean readOnlyFlag) {
    this.readOnlyFlag = readOnlyFlag;

  }

  /**
   * @param notReadOnlyFlag notReadOnlyFlag
   */
  public void setNotReadOnlyFlag(final boolean notReadOnlyFlag) {
    this.notReadOnlyFlag = notReadOnlyFlag;

  }


  /**
   * @return boolean
   */
  public boolean isqSSDFlag() {
    return this.qSSDFlag;
  }


  /**
   * @param qSSDFlag boolean
   */
  public void setqSSDFlag(final boolean qSSDFlag) {
    this.qSSDFlag = qSSDFlag;
  }


  /**
   * @return boolean
   */
  public boolean isNonQSSDFlag() {
    return this.nonQSSDFlag;
  }


  /**
   * @param nonQSSDFlag boolean
   */
  public void setNonQSSDFlag(final boolean nonQSSDFlag) {
    this.nonQSSDFlag = nonQSSDFlag;
  }

  /**
   * @param blackListFlag boolean
   */
  public void setBlackListFlag(final boolean blackListFlag) {
    this.blackListFlag = blackListFlag;
  }

  /**
   * @param nonBlackListFlag boolean
   */
  public void setNonBlackListFlag(final boolean nonBlackListFlag) {
    this.nonBlackListFlag = nonBlackListFlag;
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
   * @param wpFinishedFlag the wpFinishedFlag to set
   */
  public void setWpFinishedFlag(boolean wpFinishedFlag) {
    this.wpFinishedFlag = wpFinishedFlag;
  }


  /**
   * @param wpNotFinishedFlag the wpNotFinishedFlag to set
   */
  public void setWpNotFinishedFlag(boolean wpNotFinishedFlag) {
    this.wpNotFinishedFlag = wpNotFinishedFlag;
  }
}

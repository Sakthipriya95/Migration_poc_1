/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;


import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author mga1cob This class provides toolbar filter for the ReviewResultsPage
 */
// ICDM -550
public class ReviewResultToolBarFilters extends AbstractViewerFilter {

  /**
   * Curve Flag initially True since the tool bar action is checked
   */
  private boolean curveFlag = true;
  /**
   * Value Flag initially True since the tool bar action is checked
   */
  private boolean valueFlag = true;
  /**
   * Value Block Flag initially True since the tool bar action is checked
   */
  private boolean valueBlkFlag = true;
  /**
   * Map Flag initially True since the tool bar action is checked
   */
  private boolean mapFlag = true;
  /**
   * Ascii Flag initially True since the tool bar action is checked
   */
  private boolean asciiFlag = true;
  /**
   * Axis Point Flag initially True since the tool bar action is checked
   */
  private boolean axisFlag = true;
  /**
   * Screw Flag initially True since the tool bar action is checked
   */
  private boolean screwFlag = true;
  /**
   * Nail Flag initially True since the tool bar action is checked
   */
  private boolean nailFlag = true;
  /**
   * Rivet Flag initially True since the tool bar action is checked
   */
  private boolean rivetFlag = true;
  /**
   * Undefined class Flag initially True since the tool bar action is checked
   */
  private boolean noClassFlag = true;
  /**
   * Reviewed Flag initially True since the tool bar action is checked
   */
  private boolean reviewedFlag = true;
  /**
   * Not Reviewed Flag initially True since the tool bar action is checked
   */
  private boolean notReviewedFlag = true;
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
  private boolean notOkFlag = true;
  /**
   * Review undefined Flag initially True since the tool bar action is checked
   */
  private boolean undefinedFlag = true;

  /**
   * Review Secondary Result Ok Flag initially since the toolbar action is checked
   */
  // Task 236307
  private boolean secResultOkFlag = true;

  /**
   * Review Secondary Result not Ok Flag initially since the toolbar action is checked
   */
  // Task 236307
  private boolean secResultNotOkFlag = true;

  /**
   * Review Secondary Result not applicable Flag initially since the toolbar action is checked
   */
  // Task 236307
  private boolean secResultNAFlag = true;

  /**
   * Review Secondary Result Checked Flag initially since the toolbar action is checked
   */
  // Task 236307
  private boolean secResultCheckedFlag = true;

  // ICDM-807
  /**
   * Result changeMarkFlag Flag initially True since the tool bar action is checked
   */
  private boolean changeMarkFlag = true;
  /**
   * Result NOTOK Flag initially True since the tool bar action is checked
   */
  private boolean noChangeMarkFlag = true;
  // ICDM-1197
  /**
   * Result history Flag initially True since the tool bar action is checked
   */
  private boolean histFlag = true;
  /**
   * Result no history Flag initially True since the tool bar action is checked
   */
  private boolean noHistFlag = true;

  // ICDM-2439
  /**
   * complianceFlag Flag initially True since the tool bar action is checked
   */
  private boolean complianceFlag = true;

  // ICDM-2439
  /**
   * nonComplianceFlag Flag initially True since the tool bar action is checked
   */
  private boolean nonComplianceFlag = true;

  /**
   * Black List Flag initially True since the tool bar action is checked
   */
  private boolean blackListFlag = true;
  /**
   * Non Black List Flag initially True since the tool bar action is checked
   */
  private boolean nonBlackListFlag = true;
  /**
   * QSSD Flag initially True since the tool bar action is checked
   */
  private boolean qSSDFlag = true;
  /**
   * Non QSSD Flag initially True since the tool bar action is checked
   */
  private boolean nonQSSDFlag = true;
  private final ReviewResultClientBO resultData;
  /**
   * Read only param
   */
  private boolean readOnly = true;
  /**
   * Not read only param
   */
  private boolean notReadOnly = true;

  /**
   * Dependent param
   */
  private boolean dependent = true;
  /**
   * Not Dependent param
   */
  private boolean notDependent = true;

  /**
   * Constructor
   *
   * @param resultData Review Result Client BO
   */
  public ReviewResultToolBarFilters(final ReviewResultClientBO resultData) {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
    this.resultData = resultData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean selectElement(final Object element) {
    if (element instanceof CDRResultParameter) {
      final CDRResultParameter resultParam = (CDRResultParameter) element;

      // check if the parameter type matches unpressed filter
      if (!checkParameterType(resultParam)) {
        return false;
      }

      // check if the parameter class matches unpressed filter
      if (!checkPClass(resultParam)) {
        return false;
      }

      // check if the parameter reviewed flag matches unpressed filter
      if (!checkReviewedFlag(resultParam)) {
        return false;
      }

      // check if the parameter review results matches unpressed filter
      if (!checkReviewResults(resultParam)) {
        return false;
      }

      // check if the parameter secondary review results matches unpressed filter
      if (!checkSecondaryReviewResults(resultParam)) {
        return false;
      }

      // check if the parameter change marker unpressed filter
      if (!checkCheckValue(resultParam)) {
        return false;
      }

      if (!checkOtherFlags(resultParam)) {
        return false;
      }

    } // if element is CDRResultParam
    return true;
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean checkOtherFlags(final CDRResultParameter resultParam) {

    // check if the parameter history flag matches unpressed filter
    if (!checkHistoryFlag(resultParam)) {
      return false;
    }
    // ICDM-2439
    if (!checkComplianceFlag(resultParam)) {
      return false;
    }
    // ICDM-2439
    if (!checkNonComplianceFlag(resultParam)) {
      return false;
    }
    if (!checkIsBlackList(resultParam)) {
      return false;
    }
    if (!checkIsNonBlackList(resultParam)) {
      return false;
    }
    if (!checkIsQSSD(resultParam)) {
      return false;
    }
    if (!checkIsNonQSSD(resultParam)) {
      return false;
    }
    if (!checkReadOnlyFlag(resultParam)) {
      return false;
    }

    if (!checkNotReadOnlyFlag(resultParam)) {
      return false;
    }
    if (!checkDependentFlag(resultParam)) {
      return false;
    }

    if (!checkNotDependentFlag(resultParam)) {
      return false;
    }
    return true;
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean checkIsNonQSSD(final CDRResultParameter resultParam) {
    // if non qSSD Filter is inactive then don't select non QSSD Parameters
    return !(!isNonQSSDFlag() && !this.resultData.isQssdParameter(resultParam));
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean checkIsQSSD(final CDRResultParameter resultParam) {
    // if qSSD Filter is inactive then don't select QSSD Parameters
    return !(!isqSSDFlag() && this.resultData.isQssdParameter(resultParam));
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean checkNotReadOnlyFlag(final CDRResultParameter resultParam) {
    if (!this.notReadOnly && (!this.resultData.isReadOnly(resultParam))) {
      return false;
    }
    return true;
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean checkReadOnlyFlag(final CDRResultParameter resultParam) {
    if ((!this.readOnly) && (this.resultData.isReadOnly(resultParam))) {
      return false;
    }
    return true;
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean checkNotDependentFlag(final CDRResultParameter resultParam) {
    return !(!this.notDependent && (!this.resultData.isDependentParam(resultParam)));
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean checkDependentFlag(final CDRResultParameter resultParam) {
    return !((!this.dependent) && (this.resultData.isDependentParam(resultParam)));
  }

  // ICDM-2439
  /**
   * @param resultParam
   * @return
   */
  private boolean checkNonComplianceFlag(final CDRResultParameter resultParam) {
    /**
     * Show Non Compliance parameters flag Action Unchecked then do not show
     */
    if (!this.nonComplianceFlag && (!this.resultData.isComplianceParameter(resultParam))) {
      return false;
    }
    return true;
  }

  // ICDM-2439
  /**
   * @param resultParam
   * @return
   */
  private boolean checkComplianceFlag(final CDRResultParameter resultParam) {
    /**
     * Show Compliance parameters flag Action Unchecked then do not show
     */
    if ((!this.complianceFlag) && (this.resultData.isComplianceParameter(resultParam))) {
      return false;
    }
    return true;
  }

  /**
   * @param param
   * @return
   */
  private boolean checkIsNonBlackList(final CDRResultParameter resultParam) {
    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!this.nonBlackListFlag && !this.resultData.isBlackList(resultParam)) {
      return false;
    }
    return true;
  }


  /**
   * @param param
   * @return
   */
  private boolean checkIsBlackList(final CDRResultParameter resultParam) {
    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!this.blackListFlag && this.resultData.isBlackList(resultParam)) {
      return false;
    }
    return true;
  }

  /**
   * ICDM-807
   *
   * @param resultParam
   * @return
   */
  private boolean checkCheckValue(final CDRResultParameter resultParam) {
    /**
     * Result change marker unchecked then do not show parameters with changed marker
     */
    if ((!this.changeMarkFlag) && validateResData(resultParam)) {
      return false;
    }
    /**
     * Result no change marker unchecked then do not show parameters with changed marker
     */
    if ((!this.noChangeMarkFlag) && !validateResData(resultParam)) {
      return false;
    }
    return true;
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean validateResData(final CDRResultParameter resultParam) {
    return validateResChange(resultParam) || validateBitwiseChange(resultParam) ||
        (this.resultData.isRefValChanged(resultParam)) || this.resultData.isScoreChanged(resultParam);
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean validateBitwiseChange(final CDRResultParameter resultParam) {
    return (this.resultData.isLowerLimitChanged(resultParam)) || (this.resultData.isUpperLimitChanged(resultParam)) ||
        (this.resultData.isBitwiseFlagChanged(resultParam)) || this.resultData.isBitwiseLimitChanged(resultParam);
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean validateResChange(final CDRResultParameter resultParam) {
    return (this.resultData.isSecondaryResultChanged(resultParam)) || (this.resultData.isResultChanged(resultParam)) ||
        (this.resultData.isCheckedValueChanged(resultParam));
  }

  /**
   * @param resultParam
   */
  private boolean checkReviewResults(final CDRResultParameter resultParam) {
    /**
     * Result compli flag Action Unchecked then do not show Result Compli
     */
    if ((!this.resultCompliFlag) && validateResultData(resultParam)) {
      return false;
    }
    /**
     * Result Ok flag Action Unchecked then do not show Result Ok
     */
    if ((!this.okFlag) && (this.resultData.getResult(resultParam).equals(CDRConstants.RESULT_FLAG.OK.getUiType()))) {
      return false;
    }
    /**
     * Result NotOk flag Action Unchecked then do not show Result NotOk,high,low
     */
    if ((!isNotOkFlag()) &&
        ((this.resultData.getResult(resultParam).equals(CDRConstants.RESULT_FLAG.NOT_OK.getUiType())) ||
            (this.resultData.getResult(resultParam).equals(CDRConstants.RESULT_FLAG.HIGH.getUiType())) ||
            (this.resultData.getResult(resultParam).equals(CDRConstants.RESULT_FLAG.LOW.getUiType())))) {
      return false;
    }
    /**
     * Result Undefined flag Action Unchecked then do not show Result undefined
     */
    if ((!isUndefinedFlag()) &&
        (this.resultData.getResult(resultParam).equals(CDRConstants.RESULT_FLAG.NOT_REVIEWED.getUiType()))) {
      return false;
    }

    return true;
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean validateResultData(final CDRResultParameter resultParam) {
    return (this.resultData.getResult(resultParam).equals(CDRConstants.COMPLI_RESULT_FLAG.NO_RULE.getUiType())) ||
        (this.resultData.getResult(resultParam).equals(CDRConstants.COMPLI_RESULT_FLAG.CSSD.getUiType())) ||
        (this.resultData.getResult(resultParam).equals(CDRConstants.COMPLI_RESULT_FLAG.SSD2RV.getUiType())) ||
        (this.resultData.getResult(resultParam).equals(CDRConstants.RESULT_FLAG.SHAPE.getUiType()));
  }


  // Task 236307
  /**
   * @param resultParam resultParam
   * @return the filtered secondary review result
   */
  private boolean checkSecondaryReviewResults(final CDRResultParameter resultParam) {
    /**
     * Secondary Result Ok flag Action Unchecked then do not show Result Ok
     */
    if ((!isSecResultOkFlag()) &&
        (this.resultData.getCustomSecondaryResult(resultParam).equals(CDRConstants.RESULT_FLAG.OK.getUiType()))) {
      return false;
    }

    /**
     * Secondary Result Not Ok flag Action Unchecked then do not show Result not Ok
     */
    if ((!isSecResultNotOkFlag()) &&
        (this.resultData.getCustomSecondaryResult(resultParam).equals(CDRConstants.RESULT_FLAG.NOT_OK.getUiType()))) {
      return false;
    }

    /**
     * Secondary Result not applicable flag Action Unchecked then do not show Result N/A
     */
    if ((!isSecResultNAFlag()) && ("N/A".equals(this.resultData.getCustomSecondaryResult(resultParam)))) {
      return false;
    }

    /**
     * Secondary Result Checked flag Action Unchecked then do not show Result Checked
     */
    if ((!isSecResultCheckedFlag()) &&
        (this.resultData.getCustomSecondaryResult(resultParam).equals(CDRConstants.RESULT_FLAG.CHECKED.getUiType()))) {
      return false;
    }

    return true;
  }

  /**
   * @param resultParam
   */
  private boolean checkReviewedFlag(final CDRResultParameter resultParam) {
    /**
     * Review completed flag Action Unchecked then do not show Review completed
     */
    if ((!this.reviewedFlag) && isReviewed(resultParam)) {
      return false;
    }
    /**
     * Review not completed flag Action Unchecked then do not show Review not completed
     */
    if ((!this.notReviewedFlag) && !isReviewed(resultParam)) {
      return false;
    }
    return true;
  }

  /**
   * In case of a start review all scores greater than 0 should be considered as "reviewed"
   *
   * @param resultParam
   */
  private boolean isReviewed(final CDRResultParameter resultParam) {
    boolean doShow = false;
    if (this.resultData.getResultBo().getReviewType() == REVIEW_TYPE.START) {
      if (this.resultData.getScore(resultParam) != DATA_REVIEW_SCORE.S_0) {
        doShow = true;
      }
    }
    else if (this.resultData.isReviewed(resultParam)) {
      doShow = true;
    }
    return doShow;
  }

  /**
   * @param resultParam
   */
  private boolean checkHistoryFlag(final CDRResultParameter resultParam) {
    /**
     * History flag Action Unchecked then do not show parameters having history
     */
    if ((!isHistFlag()) && (this.resultData.hasHistory(resultParam))) {
      return false;
    }
    /**
     * not History flag Action Unchecked then do not show parameters not having history
     */
    if ((!isNoHistFlag()) && (!this.resultData.hasHistory(resultParam))) {
      return false;
    }
    return true;
  }


  /**
   * @param resultParam
   */
  private boolean checkPClass(final CDRResultParameter resultParam) {
    /**
     * Screw Action Unchecked then do not show Screw Class
     */
    if (!this.screwFlag && (ParameterClass.SCREW == ParameterClass
        .getParamClass(this.resultData.getFunctionParameter(resultParam).getpClassText()))) {
      return false;
    }
    /**
     * Nail Action Unchecked then do not show Nail Class
     */
    if (!this.nailFlag && (ParameterClass.NAIL == ParameterClass
        .getParamClass(this.resultData.getFunctionParameter(resultParam).getpClassText()))) {
      return false;
    }
    /**
     * //ICDM-916 stat Rivet Rivet Action Unchecked then do not show Rivet Class
     */
    if (!this.rivetFlag && ((ParameterClass.RIVET == ParameterClass
        .getParamClass(this.resultData.getFunctionParameter(resultParam).getpClassText())) ||
        (ParameterClass.STATRIVET == ParameterClass
            .getParamClass(this.resultData.getFunctionParameter(resultParam).getpClassText())))) {
      return false;
    }
    /**
     * Undefined Class Action Unchecked then do not show Undefined Class
     */
    if (!isNoClassFlag() &&
        (null == ParameterClass.getParamClass(this.resultData.getFunctionParameter(resultParam).getpClassText()))) {
      return false;
    }

    return true;

  }

  /**
   * @param resultParam
   */
  private boolean checkParameterType(final CDRResultParameter resultParam) {
    /**
     * Curve Action Unchecked then do not show Curve
     */
    if (isCurveActionUnchecked(resultParam)) {
      return false;
    }
    /**
     * Value Action Unchecked then do not show Value
     */
    if (isValueActionUnchecked(resultParam)) {
      return false;
    }
    /**
     * Value Block Action Unchecked then do not show Value Block
     */
    if (isValueBlkActionUnchecked(resultParam)) {
      return false;
    }

    /**
     * Map Action Unchecked then do not show Map
     */
    if (isMapActionUnchecked(resultParam)) {
      return false;
    }
    /**
     * Ascii Action Unchecked then do not show Ascii
     */
    if (isAsciiActionUnchecked(resultParam)) {
      return false;
    }
    /**
     * Axis Point Action Unchecked then do not show Axis Point
     */
    if (isAxisActionUnchecked(resultParam)) {
      return false;
    }
    return true;
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean isAxisActionUnchecked(final CDRResultParameter resultParam) {
    return !this.axisFlag &&
        ParameterType.AXIS_PTS.getText().equals(this.resultData.getFunctionParameter(resultParam).getType());
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean isAsciiActionUnchecked(final CDRResultParameter resultParam) {
    return !this.asciiFlag &&
        ParameterType.ASCII.getText().equals(this.resultData.getFunctionParameter(resultParam).getType());
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean isMapActionUnchecked(final CDRResultParameter resultParam) {
    return !this.mapFlag &&
        ParameterType.MAP.getText().equals(this.resultData.getFunctionParameter(resultParam).getType());
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean isValueBlkActionUnchecked(final CDRResultParameter resultParam) {
    return !this.valueBlkFlag &&
        ParameterType.VAL_BLK.getText().equals(this.resultData.getFunctionParameter(resultParam).getType());
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean isValueActionUnchecked(final CDRResultParameter resultParam) {
    return !this.valueFlag &&
        ParameterType.VALUE.getText().equals(this.resultData.getFunctionParameter(resultParam).getType());
  }

  /**
   * @param resultParam
   * @return
   */
  private boolean isCurveActionUnchecked(final CDRResultParameter resultParam) {
    return !this.curveFlag &&
        ParameterType.CURVE.getText().equals(this.resultData.getFunctionParameter(resultParam).getType());
  }

  /**
   * @param curveFlag the curveFlag to set
   */
  public final void setCurveFlag(final boolean curveFlag) {
    this.curveFlag = curveFlag;
  }


  /**
   * @param valueFlag the valueFlag to set
   */
  public final void setValueFlag(final boolean valueFlag) {
    this.valueFlag = valueFlag;
  }


  /**
   * @param valueBlkFlag the valueBlkFlag to set
   */
  public final void setValueBlkFlag(final boolean valueBlkFlag) {
    this.valueBlkFlag = valueBlkFlag;
  }


  /**
   * @param mapFlag the mapFlag to set
   */
  public final void setMapFlag(final boolean mapFlag) {
    this.mapFlag = mapFlag;
  }


  /**
   * @param asciiFlag the asciiFlag to set
   */
  public final void setAsciiFlag(final boolean asciiFlag) {
    this.asciiFlag = asciiFlag;
  }


  /**
   * @param axisFlag the axisFlag to set
   */
  public final void setAxisFlag(final boolean axisFlag) {
    this.axisFlag = axisFlag;
  }


  /**
   * @param screwFlag the screwFlag to set
   */
  public final void setScrewFlag(final boolean screwFlag) {
    this.screwFlag = screwFlag;
  }


  /**
   * @param nailFlag the nailFlag to set
   */
  public final void setNailFlag(final boolean nailFlag) {
    this.nailFlag = nailFlag;
  }


  /**
   * @param rivetFlag the rivetFlag to set
   */
  public final void setRivetFlag(final boolean rivetFlag) {
    this.rivetFlag = rivetFlag;
  }

  /**
   * @param reviewedFlag the reviewedFlag to set
   */
  public final void setReviewedFlag(final boolean reviewedFlag) {
    this.reviewedFlag = reviewedFlag;
  }


  /**
   * @param notReviewedFlag the notReviewedFlag to set
   */
  public final void setNotReviewedFlag(final boolean notReviewedFlag) {
    this.notReviewedFlag = notReviewedFlag;
  }


  /**
   * @param okFlag the okFlag to set
   */
  public final void setOkFlag(final boolean okFlag) {
    this.okFlag = okFlag;
  }


  /**
   * @param undefinedFlag undefinedFlag to set
   */
  public void setUndefinedFlag(final boolean undefinedFlag) {
    this.undefinedFlag = undefinedFlag;
  }


  /**
   * @return the undefinedFlag
   */
  public boolean isUndefinedFlag() {
    return this.undefinedFlag;
  }

  // ICDM-632
  /**
   * @return the notOkFlag
   */
  public boolean isNotOkFlag() {
    return this.notOkFlag;
  }

  /**
   * ICDM-807
   *
   * @param changeMarkFlag the changeMarkFlag to set
   */
  public void setChangeMarkFlag(final boolean changeMarkFlag) {
    this.changeMarkFlag = changeMarkFlag;
  }


  /**
   * ICDM-807
   *
   * @param noChangeMarkFlag the noChangeMarkFlag to set
   */
  public void setNoChangeMarkFlag(final boolean noChangeMarkFlag) {
    this.noChangeMarkFlag = noChangeMarkFlag;
  }

  /**
   * @param notOkFlag notOkFlag to set
   */
  public void setNotOkFlag(final boolean notOkFlag) {
    this.notOkFlag = notOkFlag;
  }

  /**
   * @return the undefinedClassFlag
   */
  public boolean isNoClassFlag() {
    return this.noClassFlag;
  }

  /**
   * @param noClassFlag the undefinedClassFlag to set
   */
  public void setNoClassFlag(final boolean noClassFlag) {
    this.noClassFlag = noClassFlag;
  }

  /**
   * @param histFlag hist flag
   */
  public void setHistoryFlag(final boolean histFlag) {
    this.histFlag = histFlag;

  }

  /**
   * @param noHistFlag no hist flag
   */
  public void setNoHistoryFlag(final boolean noHistFlag) {
    this.noHistFlag = noHistFlag;
  }

  /**
   * @return the histFlag
   */
  public boolean isHistFlag() {
    return this.histFlag;
  }

  /**
   * @return the noHistFlag
   */
  public boolean isNoHistFlag() {
    return this.noHistFlag;
  }

  /**
   * iCDM-848 <br>
   *
   * @return Matcher
   */
  @SuppressWarnings("rawtypes")
  public Matcher getToolBarMatcher() {
    return new CDRResultParamMatcher<CDRResultParameter>();
  }

  // ICDM-2439
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
   * @return the secResultOkFlag
   */
  public boolean isSecResultOkFlag() {
    return this.secResultOkFlag;
  }


  /**
   * @return the secResultNotOkFlag
   */
  public boolean isSecResultNotOkFlag() {
    return this.secResultNotOkFlag;
  }


  /**
   * @return the secResultNAOkFlag
   */
  public boolean isSecResultNAFlag() {
    return this.secResultNAFlag;
  }


  /**
   * @return the secResultCheckedFlag
   */
  public boolean isSecResultCheckedFlag() {
    return this.secResultCheckedFlag;
  }


  /**
   * @param secResultOkFlag the secResultOkFlag to set
   */
  public void setSecResultOkFlag(final boolean secResultOkFlag) {
    this.secResultOkFlag = secResultOkFlag;
  }


  /**
   * @param secResultNotOkFlag the secResultNotOkFlag to set
   */
  public void setSecResultNotOkFlag(final boolean secResultNotOkFlag) {
    this.secResultNotOkFlag = secResultNotOkFlag;
  }


  /**
   * @param secResultNAOkFlag the secResultNAOkFlag to set
   */
  public void setSecResultNAFlag(final boolean secResultNAOkFlag) {
    this.secResultNAFlag = secResultNAOkFlag;
  }


  /**
   * @param secResultCheckedFlag the secResultCheckedFlag to set
   */
  public void setSecResultCheckedFlag(final boolean secResultCheckedFlag) {
    this.secResultCheckedFlag = secResultCheckedFlag;
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

  private class CDRResultParamMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof CDRResultParameter) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * @param readOnly readOnly
   */
  public void setReadOnlyFlag(final boolean readOnly) {
    this.readOnly = readOnly;

  }

  /**
   * @param notReadOnly notReadOnly
   */
  public void setNotReadOnlyFlag(final boolean notReadOnly) {
    this.notReadOnly = notReadOnly;

  }


  /**
   * @return the readOnly
   */
  public boolean isReadOnly() {
    return this.readOnly;
  }


  /**
   * @return the notReadOnly
   */
  public boolean isNotReadOnly() {
    return this.notReadOnly;
  }


  /**
   * @param blackListFlag the blackListFlag to set
   */
  public void setBlackListFlag(final boolean blackListFlag) {
    this.blackListFlag = blackListFlag;
  }


  /**
   * @param nonBlackListFlag the nonBlackListFlag to set
   */
  public void setNonBlackListFlag(final boolean nonBlackListFlag) {
    this.nonBlackListFlag = nonBlackListFlag;
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
   * @return the dependent
   */
  public boolean isDependent() {
    return this.dependent;
  }


  /**
   * @return the notDependent
   */
  public boolean isNotDependent() {
    return this.notDependent;
  }

  /**
   * @param dependent dependent
   */
  public void setDependentFlag(final boolean dependent) {
    this.dependent = dependent;

  }

  /**
   * @param notDependent notDependent
   */
  public void setNotDependentFlag(final boolean notDependent) {
    this.notDependent = notDependent;

  }

}

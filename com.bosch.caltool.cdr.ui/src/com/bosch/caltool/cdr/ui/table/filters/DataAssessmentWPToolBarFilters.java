/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author AJK2COB
 */
public class DataAssessmentWPToolBarFilters extends AbstractViewerFilter {

  private boolean isRespTypeRobertBosch = true;
  private boolean isRespTypeCustomer = true;
  private boolean isRespTypeOther = true;


  private boolean isOverallWPProdReady = true;
  private boolean isOverallWPNotProdReady = true;

  private boolean isWPFinished = true;
  private boolean isWPNotFinished = true;

  private boolean isQnaireAnsweredAndBaselined = true;
  private boolean isQnaireNotAnsweredAndBaselined = true;

  private boolean isParameterReviewed = true;
  private boolean isParameterNotReviewed = true;

  private boolean isHEXDataReviewsEqual = true;
  private boolean isHEXDataReviewsNotEqual = true;

  /**
   * Constructor
   */
  public DataAssessmentWPToolBarFilters() {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * @return matcher
   */
  public Matcher getToolBarMatcher() {
    return new DataAssessmentWPToolBarMatcher<DaWpResp>();
  }

  private class DataAssessmentWPToolBarMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */


    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof DaWpResp) {
        return selectElement(element);
      }
      return true;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    DaWpResp dataAssessmentWorkpackage = (DaWpResp) element;
    boolean flag = true;

    if (!filterWPReadyForProdFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterWPNotReadyForProdFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterWPFinishedFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterWPNotFinishedFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterQnaireAnsweredAndBaselinedFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterQnaireNotAnsweredAndBaselinedFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterParameterReviewedFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterParameterNotReviewedFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterHEXDataReviewsEqualFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterHEXDataReviewsNotEqualFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    if (!filterRespTypeFlagVal(dataAssessmentWorkpackage)) {
      flag = false;
    }

    return flag;
  }

  /**
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterRespTypeFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    if ((!this.isRespTypeCustomer) && (!dataAssessmentWorkpackage.getA2lRespType().equals(WpRespType.RB.getCode()) &&
        !dataAssessmentWorkpackage.getA2lRespType().equals(WpRespType.OTHERS.getCode()))) {
      return false;
    }
    if ((!this.isRespTypeOther) && (dataAssessmentWorkpackage.getA2lRespType().equals(WpRespType.OTHERS.getCode()))) {
      return false;
    }
    return this.isRespTypeRobertBosch ||
        CommonUtils.isNotEqual(dataAssessmentWorkpackage.getA2lRespType(), WpRespType.RB.getCode());
  }

  /**
   * Overall wp prod ready
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterWPReadyForProdFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isOverallWPProdReady() ||
        !(CommonUtils.getBooleanType(dataAssessmentWorkpackage.getWpReadyForProductionFlag())));

  }

  /**
   * Overall wp not prod ready
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterWPNotReadyForProdFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isOverallWPNotProdReady() ||
        CommonUtils.getBooleanType(dataAssessmentWorkpackage.getWpReadyForProductionFlag()));
  }

  /**
   * WP finished
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterWPFinishedFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isWPFinished() || !(CommonUtils.getBooleanType(dataAssessmentWorkpackage.getWpFinishedFlag())));
  }

  /**
   * WP not finished
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterWPNotFinishedFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isWPNotFinished() || CommonUtils.getBooleanType(dataAssessmentWorkpackage.getWpFinishedFlag()));
  }

  /**
   * Qnaire answered and baselined
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterQnaireAnsweredAndBaselinedFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isQnaireAnsweredAndBaselined() ||
        !(CommonUtils.getBooleanType(dataAssessmentWorkpackage.getQnairesAnsweredFlag())));
  }

  /**
   * Qnaire not answered and baselined
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterQnaireNotAnsweredAndBaselinedFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isQnaireNotAnsweredAndBaselined() ||
        CommonUtils.getBooleanType(dataAssessmentWorkpackage.getQnairesAnsweredFlag()));
  }

  /**
   * Parameter reviewed
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterParameterReviewedFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isParameterReviewed() ||
        !(CommonUtils.getBooleanType(dataAssessmentWorkpackage.getParameterReviewedFlag())));
  }

  /**
   * Parameter not reviewed
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterParameterNotReviewedFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isParameterNotReviewed() ||
        CommonUtils.getBooleanType(dataAssessmentWorkpackage.getParameterReviewedFlag()));
  }

  /**
   * HEX Data Reviews Equal
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterHEXDataReviewsEqualFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isHEXDataReviewsEqual() || !(CommonUtils.getBooleanType(dataAssessmentWorkpackage.getHexRvwEqualFlag())));
  }

  /**
   * HEX Data Reviews Not Equal
   *
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterHEXDataReviewsNotEqualFlagVal(final DaWpResp dataAssessmentWorkpackage) {
    return (isHEXDataReviewsNotEqual() || CommonUtils.getBooleanType(dataAssessmentWorkpackage.getHexRvwEqualFlag()));
  }

  /**
   * @return the isOverallWPProdReady
   */
  public boolean isOverallWPProdReady() {
    return this.isOverallWPProdReady;
  }


  /**
   * @param isOverallWPProdReady the isOverallWPProdReady to set
   */
  public void setOverallWPProdReady(final boolean isOverallWPProdReady) {
    this.isOverallWPProdReady = isOverallWPProdReady;
  }


  /**
   * @return the isOverallWPNotProdReady
   */
  public boolean isOverallWPNotProdReady() {
    return this.isOverallWPNotProdReady;
  }


  /**
   * @param isOverallWPNotProdReady the isOverallWPNotProdReady to set
   */
  public void setOverallWPNotProdReady(final boolean isOverallWPNotProdReady) {
    this.isOverallWPNotProdReady = isOverallWPNotProdReady;
  }


  /**
   * @return the isWPFinished
   */
  public boolean isWPFinished() {
    return this.isWPFinished;
  }


  /**
   * @param isWPFinished the isWPFinished to set
   */
  public void setWPFinished(final boolean isWPFinished) {
    this.isWPFinished = isWPFinished;
  }


  /**
   * @return the isWPNotFinished
   */
  public boolean isWPNotFinished() {
    return this.isWPNotFinished;
  }


  /**
   * @param isWPNotFinished the isWPNotFinished to set
   */
  public void setWPNotFinished(final boolean isWPNotFinished) {
    this.isWPNotFinished = isWPNotFinished;
  }


  /**
   * @return the isQnaireAnsweredAndBaselined
   */
  public boolean isQnaireAnsweredAndBaselined() {
    return this.isQnaireAnsweredAndBaselined;
  }


  /**
   * @param isQnaireAnsweredAndBaselined the isQnaireAnsweredAndBaselined to set
   */
  public void setQnaireAnsweredAndBaselined(final boolean isQnaireAnsweredAndBaselined) {
    this.isQnaireAnsweredAndBaselined = isQnaireAnsweredAndBaselined;
  }


  /**
   * @return the isQnaireNotAnsweredAndBaselined
   */
  public boolean isQnaireNotAnsweredAndBaselined() {
    return this.isQnaireNotAnsweredAndBaselined;
  }


  /**
   * @param isQnaireNotAnsweredAndBaselined the isQnaireNotAnsweredAndBaselined to set
   */
  public void setQnaireNotAnsweredAndBaselined(final boolean isQnaireNotAnsweredAndBaselined) {
    this.isQnaireNotAnsweredAndBaselined = isQnaireNotAnsweredAndBaselined;
  }


  /**
   * @return the isParameterReviewed
   */
  public boolean isParameterReviewed() {
    return this.isParameterReviewed;
  }


  /**
   * @param isParameterReviewed the isParameterReviewed to set
   */
  public void setParameterReviewed(final boolean isParameterReviewed) {
    this.isParameterReviewed = isParameterReviewed;
  }


  /**
   * @return the isParameterNotReviewed
   */
  public boolean isParameterNotReviewed() {
    return this.isParameterNotReviewed;
  }


  /**
   * @param isParameterNotReviewed the isParameterNotReviewed to set
   */
  public void setParameterNotReviewed(final boolean isParameterNotReviewed) {
    this.isParameterNotReviewed = isParameterNotReviewed;
  }


  /**
   * @return the isHEXDataReviewsEqual
   */
  public boolean isHEXDataReviewsEqual() {
    return this.isHEXDataReviewsEqual;
  }


  /**
   * @param isHEXDataReviewsEqual the isHEXDataReviewsEqual to set
   */
  public void setHEXDataReviewsEqual(final boolean isHEXDataReviewsEqual) {
    this.isHEXDataReviewsEqual = isHEXDataReviewsEqual;
  }


  /**
   * @return the isHEXDataReviewsNotEqual
   */
  public boolean isHEXDataReviewsNotEqual() {
    return this.isHEXDataReviewsNotEqual;
  }


  /**
   * @param isHEXDataReviewsNotEqual the isHEXDataReviewsNotEqual to set
   */
  public void setHEXDataReviewsNotEqual(final boolean isHEXDataReviewsNotEqual) {
    this.isHEXDataReviewsNotEqual = isHEXDataReviewsNotEqual;
  }


  /**
   * @return the isRespTypeRobertBosch
   */
  public boolean isRespTypeRobertBosch() {
    return this.isRespTypeRobertBosch;
  }


  /**
   * @param isRespTypeRobertBosch the isRespTypeRobertBosch to set
   */
  public void setRespTypeRobertBosch(final boolean isRespTypeRobertBosch) {
    this.isRespTypeRobertBosch = isRespTypeRobertBosch;
  }


  /**
   * @return the isRespTypeNotRobertBosch
   */
  public boolean isRespTypeCustomer() {
    return this.isRespTypeCustomer;
  }


  /**
   * @return the isRespTypeOther
   */
  public boolean isRespTypeOther() {
    return this.isRespTypeOther;
  }


  /**
   * @param isRespTypeOther the isRespTypeOther to set
   */
  public void setRespTypeOther(final boolean isRespTypeOther) {
    this.isRespTypeOther = isRespTypeOther;
  }


  /**
   * @param isRespTypeCustomer the isRespTypeCustomer to set
   */
  public void setRespTypeCustomer(final boolean isRespTypeCustomer) {
    this.isRespTypeCustomer = isRespTypeCustomer;
  }

}

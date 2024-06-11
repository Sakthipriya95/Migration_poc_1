/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author say8cob
 */
public class DataAssessmentQnaireToolBarFilters extends AbstractViewerFilter {

  private boolean isRespTypeRobertBosch = true;
  private boolean isRespTypeCustomer = true;
  private boolean isRespTypeOther = true;


  private boolean isQnaireReadyForProdFinished = true;
  private boolean isQnaireNotReadyForProdFinished = true;

  private boolean isQnaireBaselined = true;
  private boolean isQnaireNotBaselined = true;

  /**
   * Constructor
   */
  public DataAssessmentQnaireToolBarFilters() {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * @return matcher
   */
  public Matcher getToolBarMatcher() {
    return new DataAssessmentQnaireToolBarMatcher<DataAssessmentQuestionnaires>();
  }

  private class DataAssessmentQnaireToolBarMatcher<E> implements Matcher<E> {

    /** Singleton instance of TrueMatcher. */


    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      if (element instanceof DataAssessmentQuestionnaires) {
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
    DataAssessmentQuestionnaires dataAssessmentQnaire = (DataAssessmentQuestionnaires) element;
    boolean flag = true;

    if (!filterQnaireReadyForProdFinishedFlagVal(dataAssessmentQnaire)) {
      flag = false;
    }
    if (!filterQnaireNotReadyForProdFinishedFlagVal(dataAssessmentQnaire)) {
      flag = false;
    }
    if (!filterQnaireBaselinedFlagVal(dataAssessmentQnaire)) {
      flag = false;
    }
    if (!filterQnaireNotBaselinedFlagVal(dataAssessmentQnaire)) {
      flag = false;
    }
    if (!filterRespTypeFlagVal(dataAssessmentQnaire)) {
      flag = false;
    }

    return flag;
  }

  /**
   * QnaireReadyForProd
   *
   * @param DataAssessmentQuestionnaires
   * @return
   */
  private boolean filterQnaireReadyForProdFinishedFlagVal(final DataAssessmentQuestionnaires assessmentQuestionnaires) {
    return (isQnaireReadyForProdFinished() || !(assessmentQuestionnaires.isQnaireBaselineExisting()));
  }

  /**
   * QnaireNotReadyForProd
   *
   * @param DataAssessmentQuestionnaires
   * @return
   */
  private boolean filterQnaireNotReadyForProdFinishedFlagVal(
      final DataAssessmentQuestionnaires assessmentQuestionnaires) {
    return (isQnaireNotReadyForProdFinished() || (assessmentQuestionnaires.isQnaireReadyForProd()));
  }

  /**
   * Qnaire Baselined
   *
   * @param DataAssessmentQuestionnaires
   * @return
   */
  private boolean filterQnaireBaselinedFlagVal(final DataAssessmentQuestionnaires assessmentQuestionnaires) {
    return (isQnaireBaselined() || !(assessmentQuestionnaires.isQnaireBaselineExisting()));
  }

  /**
   * Qnaire Not Baselined
   *
   * @param DataAssessmentQuestionnaires
   * @return
   */
  private boolean filterQnaireNotBaselinedFlagVal(final DataAssessmentQuestionnaires assessmentQuestionnaires) {
    return (isQnaireNotBaselined() || (assessmentQuestionnaires.isQnaireBaselineExisting()));
  }

  /**
   * @param dataAssessmentWorkpackage
   * @return
   */
  private boolean filterRespTypeFlagVal(final DataAssessmentQuestionnaires dataAssessmentQnaire) {
    if ((!this.isRespTypeCustomer) && (!dataAssessmentQnaire.getA2lRespType().equals(WpRespType.RB.getCode()) &&
        !dataAssessmentQnaire.getA2lRespType().equals(WpRespType.OTHERS.getCode()))) {
      return false;
    }
    if ((!this.isRespTypeOther) && (dataAssessmentQnaire.getA2lRespType().equals(WpRespType.OTHERS.getCode()))) {
      return false;
    }
    return this.isRespTypeRobertBosch ||
        CommonUtils.isNotEqual(dataAssessmentQnaire.getA2lRespType(), WpRespType.RB.getCode());
  }


  /**
   * @return the isRespTypeRobertBosch
   */
  public boolean isRespTypeRobertBosch() {
    return this.isRespTypeRobertBosch;
  }


  /**
   * @return the isRespTypeCustomer
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
   * @param isRespTypeRobertBosch the isRespTypeRobertBosch to set
   */
  public void setRespTypeRobertBosch(final boolean isRespTypeRobertBosch) {
    this.isRespTypeRobertBosch = isRespTypeRobertBosch;
  }


  /**
   * @param isRespTypeCustomer the isRespTypeCustomer to set
   */
  public void setRespTypeCustomer(final boolean isRespTypeCustomer) {
    this.isRespTypeCustomer = isRespTypeCustomer;
  }


  /**
   * @param isRespTypeOther the isRespTypeOther to set
   */
  public void setRespTypeOther(final boolean isRespTypeOther) {
    this.isRespTypeOther = isRespTypeOther;
  }


  /**
   * @return the isQnaireReadyForProdFinished
   */
  public boolean isQnaireReadyForProdFinished() {
    return this.isQnaireReadyForProdFinished;
  }


  /**
   * @param isQnaireReadyForProdFinished the isQnaireReadyForProdFinished to set
   */
  public void setQnaireReadyForProdFinished(final boolean isQnaireReadyForProdFinished) {
    this.isQnaireReadyForProdFinished = isQnaireReadyForProdFinished;
  }


  /**
   * @return the isQnaireNotReadyForProdFinished
   */
  public boolean isQnaireNotReadyForProdFinished() {
    return this.isQnaireNotReadyForProdFinished;
  }


  /**
   * @param isQnaireNotReadyForProdFinished the isQnaireNotReadyForProdFinished to set
   */
  public void setQnaireNotReadyForProdFinished(final boolean isQnaireNotReadyForProdFinished) {
    this.isQnaireNotReadyForProdFinished = isQnaireNotReadyForProdFinished;
  }


  /**
   * @return the isQnaireBaselined
   */
  public boolean isQnaireBaselined() {
    return this.isQnaireBaselined;
  }


  /**
   * @param isQnaireBaselined the isQnaireBaselined to set
   */
  public void setQnaireBaselined(final boolean isQnaireBaselined) {
    this.isQnaireBaselined = isQnaireBaselined;
  }


  /**
   * @return the isQnaireNotBaselined
   */
  public boolean isQnaireNotBaselined() {
    return this.isQnaireNotBaselined;
  }


  /**
   * @param isQnaireNotBaselined the isQnaireNotBaselined to set
   */
  public void setQnaireNotBaselined(final boolean isQnaireNotBaselined) {
    this.isQnaireNotBaselined = isQnaireNotBaselined;
  }
}

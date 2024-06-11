/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;

/**
 * @author mkl2cob
 */
public class QuesRespStatusToolBarFilters extends AbstractViewerFilter {

  /**
   * not answered flag
   */
  private boolean notAnsFlag = true;

  /**
   * positive answered flag
   */
  private boolean positiveAnsFlag = true;


  /**
   * negative answered flag
   */
  private boolean negativeAnsFlag = true;


  /**
   * Not baselined Qnaire Resp flag
   */
  private boolean notBaselinedQnaireRespFlag = true;


  /**
   * Constructor
   */
  public QuesRespStatusToolBarFilters() {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof QnaireRespStatusData) {
      QnaireRespStatusData ansObj = (QnaireRespStatusData) element;
      if (isNotAnsStatusActionUnchecked(ansObj)) {
        return false;
      }
      if (isNegativeAnsStatusActionUnchecked(ansObj)) {
        return false;
      }
      if (isPositiveAnsStatusActionUnchecked(ansObj)) {
        return false;
      }
      if (isNotBaselinedQnaireRespActionChecked(ansObj)) {
        return false;
      }
    }
    return true;
  }


  /**
   * @param qnaireData
   * @return
   */
  private boolean isPositiveAnsStatusActionUnchecked(final QnaireRespStatusData qnaireData) {
    return !this.positiveAnsFlag && CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireData.getStatus());
  }

  /**
   * @param qnaireData
   * @return
   */
  private boolean isNegativeAnsStatusActionUnchecked(final QnaireRespStatusData qnaireData) {
    return !this.negativeAnsFlag &&
        (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireData.getStatus()) ||
            CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(qnaireData.getStatus()));
  }

  /**
   * @param question
   * @return boolean
   */
  private boolean isNotAnsStatusActionUnchecked(final QnaireRespStatusData qnaireData) {
    return !this.notAnsFlag && (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(qnaireData.getStatus()) ||
        CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(qnaireData.getStatus()));
  }

  /**
   * @param ansObj
   * @return
   */
  private boolean isNotBaselinedQnaireRespActionChecked(final QnaireRespStatusData qnaireData) {
    return !isNotBaselinedQnaireRespFlag() &&
        CommonUtils.isEqual(CDRConstants.WORKING_SET_REV_NUM, qnaireData.getRevisionNum());
  }

  /**
   * @return the notAnsFlag
   */
  public boolean isNotAnsFlag() {
    return this.notAnsFlag;
  }


  /**
   * @return the positiveAnsFlag
   */
  public boolean isPositiveAnsFlag() {
    return this.positiveAnsFlag;
  }


  /**
   * @return the negativeAnsFlag
   */
  public boolean isNegativeAnsFlag() {
    return this.negativeAnsFlag;
  }

  /**
   * @param notAnsFlag the notAnsFlag to set
   */
  public void setNotAnsFlag(final boolean notAnsFlag) {
    this.notAnsFlag = notAnsFlag;
  }


  /**
   * @param positiveAnsFlag the positiveAnsFlag to set
   */
  public void setPositiveAnsFlag(final boolean positiveAnsFlag) {
    this.positiveAnsFlag = positiveAnsFlag;
  }


  /**
   * @param negativeAnsFlag the negativeAnsFlag to set
   */
  public void setNegativeAnsFlag(final boolean negativeAnsFlag) {
    this.negativeAnsFlag = negativeAnsFlag;
  }

  /**
   * @return the notBaselinedQnaireRespFlag
   */
  public boolean isNotBaselinedQnaireRespFlag() {
    return this.notBaselinedQnaireRespFlag;
  }

  /**
   * @param notBaselinedQnaireRespFlag the notBaselinedQnaireRespFlag to set
   */
  public void setNotBaselinedQnaireRespFlag(final boolean notBaselinedQnaireRespFlag) {
    this.notBaselinedQnaireRespFlag = notBaselinedQnaireRespFlag;
  }

}

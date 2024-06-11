/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import com.bosch.caltool.icdm.client.bo.qnaire.QnaireDefBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * NAT table input converter for Question details page
 *
 * @author dmo5cob
 */
public class ReviewQuestionaireNatInputToColConverter extends AbstractNatInputToColumnConverter {

  private final QnaireDefBO qnaireDefBo;

  /**
   * @param qnaireDefBo QnaireDefBo
   */
  public ReviewQuestionaireNatInputToColConverter(final QnaireDefBO qnaireDefBo) {
    this.qnaireDefBo = qnaireDefBo;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {

    Object result = null;
    if (evaluateObj instanceof Question) {
      result = getColumnData((Question) evaluateObj, colIndex);

    }
    return result;

  }


  /**
   * @param evaluateObj Question instance
   * @param colIndex column index
   * @return Object
   */
  public Object getColumnData(final Question evaluateObj, final int colIndex) {
    Object result;

    switch (colIndex) {
      // set the question number
      case 0:
        result = this.qnaireDefBo.getQuestionNumber(evaluateObj.getId());
        break;
      // set question name
      case 1:
        result = CommonUtils.checkNull(evaluateObj.getName());
        break;
      // set question desc
      case 2:
        result = CommonUtils.checkNull(evaluateObj.getDescription());
        break;
      // set measurement value
      case 3:
        result = CommonUtils.checkNull(this.qnaireDefBo.getMeasurementUIString(evaluateObj.getId()));
        break;
      case 4:
        // set series value
        result = CommonUtils.checkNull(this.qnaireDefBo.getSeriesUIString(evaluateObj.getId()));
        break;
      case 5:
        // set link
        result = CommonUtils.checkNull(this.qnaireDefBo.getLinkUIString(evaluateObj.getId()));
        break;
      case 6:
        // set remark
        result = CommonUtils.checkNull(this.qnaireDefBo.getRemarkUIString(evaluateObj.getId()));
        break;
      case 7:
        // set open points
        result = CommonUtils.checkNull(this.qnaireDefBo.getOpenPointsUIString(evaluateObj.getId()));
        break;
      case 8:
        // set measure
        result = CommonUtils.checkNull(this.qnaireDefBo.getMeasureUIString(evaluateObj.getId()));
        break;
      case 9:
        // set responsible
        result = CommonUtils.checkNull(this.qnaireDefBo.getResponsibleUIString(evaluateObj.getId()));
        break;
      case 10:
        // set completion date
        result = CommonUtils.checkNull(this.qnaireDefBo.getCompletionDateUIString(evaluateObj.getId()));
        break;
      case 11:
        // set result
        result = CommonUtils.checkNull(this.qnaireDefBo.getResultUIString(evaluateObj.getId()));
        break;
      case 12:
        // set is deleted
        result = CommonUtils.getDisplayText(evaluateObj.getDeletedFlag());// Deleted
        break;
      default:
        result = "";
        break;
    }
    return result;
  }


}

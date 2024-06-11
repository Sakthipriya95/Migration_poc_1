/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswerOpl;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


// TODO: Auto-generated Javadoc
/**
 * input to column data converter.
 *
 * @author mkl2cob
 */
public class QnaireRespNatInputToColConverter extends AbstractNatInputToColumnConverter {

  /** The qnaire resp data handler. */
  private final QnaireRespEditorDataHandler qnaireRespDataHandler;


  /**
   * Instantiates a new qnaire resp nat input to col converter.
   *
   * @param qnaireRespDataHandler the qnaire resp data handler
   */
  public QnaireRespNatInputToColConverter(final QnaireRespEditorDataHandler qnaireRespDataHandler) {
    this.qnaireRespDataHandler = qnaireRespDataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    if (evaluateObj instanceof RvwQnaireAnswer) {
      RvwQnaireAnswer ansObj = (RvwQnaireAnswer) evaluateObj;
      switch (colIndex) {
        // question number value
        case CommonUIConstants.COLUMN_INDEX_0:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getQuestionNumber(ansObj.getQuestionId()));
          break;
        // question value
        case CommonUIConstants.COLUMN_INDEX_1:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getNameByLanguage(ansObj.getQuestionId()));
          break;
        // question description value
        case CommonUIConstants.COLUMN_INDEX_2:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getDescriptionByLanguage(ansObj));
          break;
        // answer isseries value
        case CommonUIConstants.COLUMN_INDEX_3:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getSeriesUIString(ansObj));
          break;
        // answer is measurement value
        case CommonUIConstants.COLUMN_INDEX_4:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getMeasurementUIString(ansObj));
          break;
        // answer link value
        case CommonUIConstants.COLUMN_INDEX_5:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getLinkUIString(ansObj));
          break;
        // changes done to show NA in the cell of column 6(showing that the Open Issue field is not relevant for the
        // question)
        case CommonUIConstants.COLUMN_INDEX_6:
          try {
            result = CommonUtils.checkNull(this.qnaireRespDataHandler.getOpenPointsUIString(ansObj));
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
          }
          break;
        // answer remark value
        case CommonUIConstants.COLUMN_INDEX_7:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getRemarksUIString(ansObj));
          break;
        // answer result value
        case CommonUIConstants.COLUMN_INDEX_8:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler
              .getQuestionResultOptionUIString(ansObj.getQuestionId(), ansObj.getSelQnaireResultOptID()));
          break;
        // answer result value
        case CommonUIConstants.COLUMN_INDEX_9:
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getCalculatedResults(ansObj));
          break;
        default:
          result = "";
      }
    }
    else if (evaluateObj instanceof RvwQnaireAnswerOpl) {
      RvwQnaireAnswerOpl oplObject = (RvwQnaireAnswerOpl) evaluateObj;

      if ((CommonUIConstants.COLUMN_INDEX_6 == colIndex) &&
          this.qnaireRespDataHandler.getAllRvwAnswerMap().containsKey(oplObject.getRvwAnswerId())) {
        try {
          // answer open points value
          RvwQnaireAnswer rvwQnaireAnswer =
              this.qnaireRespDataHandler.getAllRvwAnswerMap().get(oplObject.getRvwAnswerId());
          result = CommonUtils.checkNull(this.qnaireRespDataHandler.getOpenPointUIString(rvwQnaireAnswer, oplObject));
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
    return result;

  }

}

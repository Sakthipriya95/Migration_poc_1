/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;

/**
 * ICDM-2497
 *
 * @author mkl2cob
 */
public class DaCompHEXLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * DataLayer
   */
  private final DataLayer bodyDataLayer;
  /**
   * IRowDataProvider<DaCompareHexParam>
   */
  private final IRowDataProvider<DaCompareHexParam> bodyDataProvider;
  private final DataAssmntReportDataHandler dataAssmntReportDataHandler;

  /**
   * @param bodyDataLayer DataLayer
   * @param bodyDataProvider IRowDataProvider<DaCompareHexParam>
   * @param dataAssmntReportDataHandler CdrReportDataHandler
   */
  public DaCompHEXLabelAccumulator(final DataLayer bodyDataLayer,
      final IRowDataProvider<DaCompareHexParam> bodyDataProvider,
      final DataAssmntReportDataHandler dataAssmntReportDataHandler) {
    super(bodyDataLayer);
    this.bodyDataLayer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;
    this.dataAssmntReportDataHandler = dataAssmntReportDataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    int columnIndex = this.bodyDataLayer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (CommonUtils.isNotNull(overrides)) {
      overrides.forEach(configLabels::addLabel);
    }
    // get the row object
    DaCompareHexParam daCompHexParam = this.bodyDataProvider.getRowObject(rowPosition);

    // Compli/Shape result label
    getCompliShapeLabel(configLabels, columnPosition, daCompHexParam);

    // for parameter type
    if (columnPosition == CommonUIConstants.TYPE_INDEX) {
      configLabels.addLabel(CDRConstants.PARAM_TYPE);
    }

    // for latest function version column
    if ((columnPosition == CommonUIConstants.LATEST_FUNC_VERS_INDEX) && daCompHexParam.isNeverReviewed() &&
        (CommonUtils.isNotEqual(daCompHexParam.getFuncVers(), daCompHexParam.getLatestFunctionVersion()))) {
      configLabels.addLabel(CDRConstants.FUNC_VERS_DIFF);
    }
    // for questionnaire status column
    if (columnPosition == CommonUIConstants.QNAIRE_STATUS) {
      setConfigLabelForQnaireStatusColumn(configLabels, daCompHexParam);
    }
    // for the reviewed column
    if (columnPosition == CommonUIConstants.REVIEWED_INDEX) {
      setConfigLabelsForReviewedColumn(configLabels, daCompHexParam);
    }

    // for Review result description column
    if (columnPosition == CommonUIConstants.REVIEW_DESCRIPTION_INDEX) {
      setConfigLabelsForReviewDescCol(configLabels, daCompHexParam);
    }

    // for Compli Result column
    if (columnPosition == CommonUIConstants.COMPLI_RESULT_INDEX) {
      configLabels.addLabel(CDRConstants.COMPLI_RESULT);
    }

    // for equal column
    if ((columnPosition == CommonUIConstants.EQUAL_INDEX)) {
      // set the tick image or no image
      configLabels.addLabel(daCompHexParam.isEqual() ? CDRConstants.TICK : CDRConstants.REVIEW_FLAG_NO);
    }

    // for equal column
    if ((columnPosition == CommonUIConstants.REVIEW_SCORE_INDEX) && (daCompHexParam.getReviewScore() != null)) {
      String score = daCompHexParam.getReviewScore();
      // set color for scores
      DATA_REVIEW_SCORE reviewScore = (score == null) || score.isEmpty() ? null : DATA_REVIEW_SCORE.getType(score);
      setColorsForScores(configLabels, reviewScore);

    }
    if (columnPosition == CommonUIConstants.WP_FINISHED_INDEX) {
      setConfigLabelsForWpFinishedColumn(configLabels, daCompHexParam);
    }
  }

  private void setConfigLabelsForReviewedColumn(final LabelStack configLabels,
      final DaCompareHexParam compHexWithCDFParam) {
    String isRevieweStr = compHexWithCDFParam.isReviewed() ? CDRConstants.TICK : CDRConstants.REVIEW_FLAG_NO;
    configLabels.addLabel(compHexWithCDFParam.isNeverReviewed() ? ApicConstants.NEVER_REVIEWED : isRevieweStr);
  }

  private void setConfigLabelsForReviewDescCol(final LabelStack configLabels,
      final DaCompareHexParam compHexWithCDFParam) {
    if (!compHexWithCDFParam.isNeverReviewed()) {
      configLabels.addLabel("REVIEW_RESULT_HYPERLINK");
    }
  }

  private void setConfigLabelsForWpFinishedColumn(final LabelStack configLabels,
      final DaCompareHexParam compHexWithCDFParam) {
    String wpFinishedRespStatus = compHexWithCDFParam.getWpFinishedStatus();

    if (CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType().equals(wpFinishedRespStatus) ||
        CommonUtils.isEqual(CommonUtilConstants.BOOLEAN_MODE.YES.getBinaryValue(), wpFinishedRespStatus)) {
      configLabels.addLabel(CDRConstants.TICK);
    }
    else if (CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType().equals(wpFinishedRespStatus)) {
      configLabels.addLabel(CDRConstants.REVIEW_FLAG_NO);
    }
  }

  /**
   * @param configLabels
   * @param rowObject
   */
  private void setConfigLabelForQnaireStatusColumn(final LabelStack configLabels, final DaCompareHexParam rowObject) {
    String qnaireRespVersStatus = rowObject.getQnaireStatus();

    if (CDRConstants.DA_QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_ALL);
    }
    else if (CDRConstants.DA_QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(qnaireRespVersStatus) ||
        CDRConstants.DA_QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(qnaireRespVersStatus) ||
        ApicConstants.EMPTY_STRING.equals(qnaireRespVersStatus) ||
        CommonUtils.isEqual(CDRConstants.DA_QS_STATUS_TYPE.NO_BASELINE.getDbType(), qnaireRespVersStatus)) {
      // same "not all answered" icon will be displayed for wp and resp contains questionnaire with no baseline
      // same "not all answered" icon will be displayed for Answers that doesnt allow to finish WP
      // only the tooltip differs
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_REMOVE);
    }
    else if (CDRConstants.DA_QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus) ||
        CDRConstants.DA_QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_EXCLAMATION);
    }
    else if (CDRConstants.DA_QS_STATUS_TYPE.N_A.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_NA);
    }
    else if (CDRConstants.DA_QS_STATUS_TYPE.NO_QNAIRE.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_OK);
    }
  }

  /**
   * @param configLabels
   * @param reviewScore
   */
  private void setColorsForScores(final LabelStack configLabels, final DATA_REVIEW_SCORE reviewScore) {
    // for code no retun red color back ground
    if (reviewScore != null) {
      if (reviewScore.getRating() == 0) {
        configLabels.addLabel(CDRConstants.SCORE_RATING_0);
      }
      else if (reviewScore.getRating() == 25) {
        configLabels.addLabel(CDRConstants.SCORE_RATING_25);
      }
      else if (reviewScore.getRating() == 50) {
        configLabels.addLabel(CDRConstants.SCORE_RATING_50);
      }
      else if (reviewScore.getRating() == 75) {
        configLabels.addLabel(CDRConstants.SCORE_RATING_75);
      }
    }
  }

  /**
   * Gets the compli shape label.
   *
   * @param configLabels the config labels
   * @param columnPosition the column position
   * @param rowPosition the row position
   * @param rowObject
   */
  private void getCompliShapeLabel(final LabelStack configLabels, final int columnPosition,
      final DaCompareHexParam rowObject) {

    if (columnPosition == CommonUIConstants.SSD_CLASS_INDEX) {
      configLabels.addLabel(CDRConstants.MULTI_IMAGE_PAINTER);
      if (rowObject.isCompli()) {
        configLabels.addLabel(CDRConstants.COMPLI);
      }
      if (rowObject.isReadOnly()) {
        configLabels.addLabel(CDRConstants.READ_ONLY);
      }
      if (rowObject.isDependantCharacteristic()) {
        configLabels.addLabel(CDRConstants.DEPENDENT_CHARACTERISTICS);
      }
      if (rowObject.isBlackList()) {
        configLabels.addLabel(CDRConstants.BLACK_LIST_LABEL);
      }
      if (rowObject.isQssdParameter()) {
        configLabels.addLabel(CDRConstants.QSSD_LABEL);
      }
    }
  }
}

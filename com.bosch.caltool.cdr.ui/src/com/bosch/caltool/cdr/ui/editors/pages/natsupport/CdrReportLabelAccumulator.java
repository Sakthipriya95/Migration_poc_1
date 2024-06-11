/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.CdrReportListPage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;


/**
 * ICDM-1700
 *
 * @author mkl2cob
 */
public class CdrReportLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * ILayer
   */
  private final ILayer layer;

  /**
   * Row data provider
   */
  private final IRowDataProvider<A2LParameter> bodyDataProvider;

  /**
   * CdrReportData instance
   */
  private final CdrReportDataHandler cdrReportData;

  /**
   * @param layer ILayer
   * @param bodyDataProvider IRowDataProvider<A2LParameter>
   * @param cdrReportData CdrReportData
   */
  public CdrReportLabelAccumulator(final ILayer layer, final IRowDataProvider<A2LParameter> bodyDataProvider,
      final CdrReportDataHandler cdrReportData) {
    super(layer);
    this.layer = layer;
    this.bodyDataProvider = bodyDataProvider;
    this.cdrReportData = cdrReportData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // get the column index
    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (overrides != null) {
      for (String configLabel : overrides) {
        configLabels.addLabel(configLabel);
      }
    }
    // get the row object out of the dataprovider
    // ICDM-2439
    if (columnPosition == CommonUIConstants.SSD_CLASS_COL_INDEX) {
      setConfigLabelForSSDClassColumn(configLabels, rowPosition);
    }
    if (columnPosition == CommonUIConstants.PARAM_TYPE_COL_INDEX) {
      configLabels.addLabel(CDRConstants.PARAM_TYPE);
    }
    // for the reviewed column
    if (columnPosition == CommonUIConstants.RVWD_COL_INDEX) {
      configLabelsForReviewedCol(configLabels, rowPosition);
    }

    if (CommonUtils.isEqual(columnPosition, CommonUIConstants.RVW_DESCRIPTION_COL_INDEX)) {
      configLabelsForReviewDesc(configLabels, rowPosition);
    }
    /** Adding config labels for WP_FINISHED Column */

    if (columnPosition == CommonUIConstants.WP_FINISHED_COL_INDEX) {
      configLabelsForFinishedCol(configLabels, rowPosition);
    }

    // for questionnaire status column
    if (columnPosition == CommonUIConstants.RVW_QNAIRE_STATUS_COL_INDEX) {
      setConfigLabelForQnaireStatusColumn(configLabels, rowPosition);
    }
    // For all columns >9
    // Labels configured for Dynamic Columns
    // Dynamic Column count depends on the no. of Reviews done
    if (columnPosition >= CdrReportListPage.STATIC_COL_INDEX) {
      A2LParameter rowObject = this.bodyDataProvider.getRowObject(rowPosition);

      DATA_REVIEW_SCORE reviewScore =
          this.cdrReportData.getReviewScore(rowObject.getName(), columnPosition - CdrReportListPage.STATIC_COL_INDEX);
      setColorsForScores(configLabels, columnPosition, rowObject, reviewScore);

    }


  }

  /**
   * @param configLabels
   * @param rowPosition
   */
  private void configLabelsForFinishedCol(final LabelStack configLabels, final int rowPosition) {
    A2LParameter rowObject = this.bodyDataProvider.getRowObject(rowPosition);

    String wpFinishedRespStatus = this.cdrReportData.getWpFinishedRespStatus(rowObject.getParamId());

    if (CDRConstants.WP_RESP_STATUS_TYPE.FINISHED.getDbType().equals(wpFinishedRespStatus)) {
      // If the Parameter is finished, setting the tick icon as label
      configLabels.addLabel(CDRConstants.TICK);
    }
    else if (CDRConstants.WP_RESP_STATUS_TYPE.NOT_FINISHED.getDbType().equals(wpFinishedRespStatus)) {
      configLabels.addLabel(CDRConstants.REVIEW_FLAG_NO);
    }
  }

  private void configLabelsForReviewDesc(final LabelStack configLabels, final int rowPosition) {
    A2LParameter rowObject = this.bodyDataProvider.getRowObject(rowPosition);
    // ICDM-2585 (Parent Task ICDM-2412)-2
    String reviewedStr = this.cdrReportData.isReviewedStr(rowObject.getName());
    if (!ApicConstants.NEVER_REVIEWED.equals(reviewedStr)) {
      configLabels.addLabel("REVIEW_RESULT_HYPERLINK");
    }
  }

  /**
   * @param configLabels
   * @param rowPosition
   */
  private void configLabelsForReviewedCol(final LabelStack configLabels, final int rowPosition) {
    A2LParameter rowObject = this.bodyDataProvider.getRowObject(rowPosition);
    // ICDM-2585 (Parent Task ICDM-2412)-2
    String reviewedStr = this.cdrReportData.isReviewedStr(rowObject.getName());
    if (ApicConstants.REVIEWED.equals(reviewedStr)) {
      // If the Parameter is Reviewed, setting the tick icon as label
      configLabels.addLabel(CDRConstants.TICK);
    }
    else if (ApicConstants.NOT_REVIEWED.equals(reviewedStr)) {
      configLabels.addLabel(CDRConstants.REVIEW_FLAG_NO);
    }
  }

  /**
   * @param configLabels
   * @param rowPosition
   */
  private void setConfigLabelForSSDClassColumn(final LabelStack configLabels, final int rowPosition) {
    A2LParameter a2lParamObject = this.bodyDataProvider.getRowObject(rowPosition);
    configLabels.addLabel(CDRConstants.MULTI_IMAGE_PAINTER);
    // Checking the Parameter type and
    // Appending labels for all the Parameter types
    if (a2lParamObject.isComplianceParam()) {
      configLabels.addLabel(CDRConstants.COMPLI);
    }
    if (a2lParamObject.getCharacteristic().isReadOnly()) {
      configLabels.addLabel(CDRConstants.READ_ONLY);
    }
    if (a2lParamObject.getCharacteristic().isDependentCharacteristic()) {
      configLabels.addLabel(CDRConstants.DEPENDENT_CHARACTERISTICS);
    }
    if (a2lParamObject.isQssdParameter()) {
      configLabels.addLabel(CDRConstants.QSSD_LABEL);
    }
    if (a2lParamObject.isBlackList()) {
      configLabels.addLabel(CDRConstants.BLACK_LIST_LABEL);
    }
  }

  /**
   * @param configLabels
   * @param rowPosition
   */
  private void setConfigLabelForQnaireStatusColumn(final LabelStack configLabels, final int rowPosition) {
    A2LParameter rowObject = this.bodyDataProvider.getRowObject(rowPosition);
    String qnaireRespVersStatus = this.cdrReportData.getQnaireRespVersStatus(rowObject.getParamId(), false);
    // Setting the labels based on the Questionnaire response Answer Status
    if (CDRConstants.QS_STATUS_TYPE.ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_ALL);
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALLOWED_FINISHED_WP.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_REMOVE);
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ALL_POSITIVE.getDbType().equals(qnaireRespVersStatus) ||
        CDRConstants.QS_STATUS_TYPE.NOT_ALLOW_NEGATIVE.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_EXCLAMATION);
    }
    else if (CDRConstants.QS_STATUS_TYPE.NOT_ANSWERED.getDbType().equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_REMOVE);
    }
    else if (CommonUtils.isEqual(CDRConstants.RVW_QNAIRE_STATUS_N_A, qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_NA);
    }
    else if (ApicConstants.EMPTY_STRING.equals(qnaireRespVersStatus) ||
        CommonUtils.isEqual(CDRConstants.NOT_BASELINED_QNAIRE_RESP, qnaireRespVersStatus)) {
      // same "not all answered" icon will be displayed for wp and resp contains questionnaire with no baseline
      // only the tooltip differs
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_REMOVE);
    }
    else if (CDRConstants.NO_QNAIRE_STATUS.equals(qnaireRespVersStatus)) {
      configLabels.addLabel(CDRConstants.RVW_QNAIRE_RESP_STATUS_OK);
    }
  }

  /**
   * @param configLabels
   * @param columnPosition
   * @param rowObject
   * @param reviewScore
   */
  private void setColorsForScores(final LabelStack configLabels, final int columnPosition, final A2LParameter rowObject,
      final DATA_REVIEW_SCORE reviewScore) {
    // for code no retun red color back ground
    if (CommonUtils.isNotNull(reviewScore) && CommonUtils.isNotNull(
        this.cdrReportData.getReviewScore(rowObject.getName(), columnPosition - CdrReportListPage.STATIC_COL_INDEX))) {
      // Setting the Label based on the Review Score of Params
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
}

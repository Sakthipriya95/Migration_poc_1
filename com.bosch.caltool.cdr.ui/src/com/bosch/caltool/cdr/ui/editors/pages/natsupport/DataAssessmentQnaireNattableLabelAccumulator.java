/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentQuestionnaireResultsPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;

/**
 * This class extends {@link ColumnOverrideLabelAccumulator} which is used for registration/addition of labels for a
 * given column.
 *
 * @author ajk2cob
 */
public class DataAssessmentQnaireNattableLabelAccumulator extends ColumnOverrideLabelAccumulator {


  private final DataLayer bodyDataLayer;

  /**
   * @param bodyDataLayer ILayer instance
   */
  public DataAssessmentQnaireNattableLabelAccumulator(final DataLayer bodyDataLayer) {
    super(bodyDataLayer);
    this.bodyDataLayer = bodyDataLayer;
  }

  /**
   * This method is used to add labels to a cell
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    IRowDataProvider<DataAssessmentQuestionnaires> bodyDataProvider =
        (IRowDataProvider<DataAssessmentQuestionnaires>) this.bodyDataLayer.getDataProvider();

    DataAssessmentQuestionnaires daAssessQnaire = bodyDataProvider.getRowObject(rowPosition);
    // condition to apply the configuration only to column 12
    if (columnPosition == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_LINK) {
      configLabels.addLabel("HYPERLINK");
    }

    if (columnPosition == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING) {
      configLabels.addLabel(
          daAssessQnaire.isQnaireBaselineExisting() ? CommonUIConstants.LABEL_FOR_YES : CommonUIConstants.LABEL_FOR_NO);
    }

    if (columnPosition == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_ASSESSMENT_PROD_READY) {
      configLabels.addLabel(
          daAssessQnaire.isQnaireReadyForProd() ? CommonUIConstants.LABEL_FOR_YES : CommonUIConstants.LABEL_FOR_NO);
    }

  }
}

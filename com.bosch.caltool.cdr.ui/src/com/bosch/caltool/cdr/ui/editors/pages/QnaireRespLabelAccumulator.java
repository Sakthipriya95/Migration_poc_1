/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.util.function.IntPredicate;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;


/**
 * Accumulator for questionaire response editor
 *
 * @author bru2cob
 */
public class QnaireRespLabelAccumulator extends ColumnOverrideLabelAccumulator {


  /**
   * NAT table Data Layer
   */
  private final ILayer layer;
  private final QnaireRespEditorDataHandler dataHandler;

  /**
   * Constructor
   *
   * @param layer data layer
   */
  public QnaireRespLabelAccumulator(final ILayer layer, final QnaireRespEditorDataHandler editorInp) {
    super(layer);
    this.layer = layer;
    this.dataHandler = editorInp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    // get the row object out of the dataprovider
    @SuppressWarnings("unchecked")
    IRowDataProvider<RvwQnaireAnswer> bodyDataProvider =
        (IRowDataProvider<RvwQnaireAnswer>) ((DataLayer) this.layer).getDataProvider();
    Object cellValue = bodyDataProvider.getDataValue(columnPosition, rowPosition);
    Object rowObject = bodyDataProvider.getRowObject(rowPosition);
    if (rowObject instanceof GroupByObject) {
      super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    }
    // check instance of review qnaire ans
    else if (rowObject instanceof RvwQnaireAnswer) {
      RvwQnaireAnswer ansObj = (RvwQnaireAnswer) rowObject;
      if (!this.dataHandler.isQuestionVisible(ansObj)) {
        configLabels.addLabel("INVISIBLE");
      }
      // add label for heading rows
      else if (this.dataHandler.checkHeading(ansObj)) {
        configLabels.addLabel("NONE");
      }
      // add label for series rows
      else if ((columnPosition == CommonUIConstants.COLUMN_INDEX_3) && canModify(ansObj) &&
          this.dataHandler.showSeriesMaturity(ansObj.getQuestionId())) {
        configLabels.addLabel("COMBO_SERIES");
      }
      // add label for measurement rows
      else if ((columnPosition == CommonUIConstants.COLUMN_INDEX_4) && canModify(ansObj) &&
          this.dataHandler.showMeasurement(ansObj.getQuestionId())) {
        configLabels.addLabel("COMBO_MEASUREMENT");
      }
    }
    // To change the color of Questionnaire response result status
    changeColorOfResultStatus(configLabels, columnPosition, rowObject);

    // Configuration to gray out non-editable columns QNo,Question,Notes regarding Question,calculated result and not
    // relevant cells for the question
    if (CommonUIConstants.CELL_VALUE_NOT_APPLICABLE.equals(cellValue) || isNonEditableColumn().test(columnPosition)) {
      configLabels.addLabel("NOT_APPLICABLE");
    }
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

  }

  /**
   * @param configLabels
   * @param columnPosition
   * @param rowObject
   */
  private void changeColorOfResultStatus(final LabelStack configLabels, final int columnPosition,
      final Object rowObject) {
    if ((columnPosition == CommonUIConstants.COLUMN_INDEX_9) && (rowObject instanceof RvwQnaireAnswer)) {
      RvwQnaireAnswer ansObj = (RvwQnaireAnswer) rowObject;
      if (CdrUIConstants.FINISHED_POSITIVE_ANS_LABEL.equals(this.dataHandler.getCalculatedResults(ansObj))) {
        configLabels.addLabel(CdrUIConstants.FINISHED_POSITIVE_ANS_LABEL);
      }
      if (CdrUIConstants.FINISHED_NEUTRAL_ANS_LABEL.equals(this.dataHandler.getCalculatedResults(ansObj))) {
        configLabels.addLabel(CdrUIConstants.FINISHED_NEUTRAL_ANS_LABEL);
      }
      if (CdrUIConstants.FINISHED_NEGATIVE_ANS_LABEL.equals(this.dataHandler.getCalculatedResults(ansObj)) &&
          ApicConstants.CODE_YES
              .equals(this.dataHandler.getQnaireDefHandler().getQnaireVersion().getNoNegativeAnsAllowedFlag())) {
        configLabels.addLabel(CdrUIConstants.FINISHED_NO_NEG_ANS_ALLOWED);
      }
      else if (CdrUIConstants.FINISHED_NEGATIVE_ANS_LABEL.equals(this.dataHandler.getCalculatedResults(ansObj))) {
        configLabels.addLabel(CdrUIConstants.FINISHED_NEGATIVE_ANS_LABEL);
      }
    }
  }

  /**
   * @param columnPosition
   * @return
   */
  private IntPredicate isNonEditableColumn() {
    return colIndex -> (colIndex == CommonUIConstants.COLUMN_INDEX_0) ||
        (colIndex == CommonUIConstants.COLUMN_INDEX_1) || (colIndex == CommonUIConstants.COLUMN_INDEX_2) ||
        (colIndex == CommonUIConstants.COLUMN_INDEX_9);
  }

  /**
   * Check for access rights to modify
   *
   * @param ansObj
   * @return
   */
  private boolean canModify(final RvwQnaireAnswer ansObj) {
    return !this.dataHandler.checkHeading(ansObj) && this.dataHandler.isModifiable();
  }
}

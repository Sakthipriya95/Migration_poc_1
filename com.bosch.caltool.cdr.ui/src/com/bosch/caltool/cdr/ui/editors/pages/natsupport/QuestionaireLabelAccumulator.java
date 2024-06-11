package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.ui.IEditorInput;

import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditorInput;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.Question;

/**
 * This class provides labels for body datalayer of the GridLayer of the NatTable
 *
 * @author dmo5cob
 */
public class QuestionaireLabelAccumulator extends ColumnOverrideLabelAccumulator {


  /**
   * static cols index
   */
  private static final int CONFIG_COLS_INDEX = 2;
  /**
   * IRowDataProvider<FocusMatrixAttribute> instance
   */
  private final IRowDataProvider<Question> bodyDataProvider;
  private final ReviewQuestionaireEditorInput editorInput;

  /**
   * @param bodyDataLayer ILayer instance
   * @param bodyDataProvider IRowDataProvider instance
   * @param iEditorInput
   */
  public QuestionaireLabelAccumulator(final ILayer bodyDataLayer, final IRowDataProvider<Question> bodyDataProvider,
      final IEditorInput editorInput) {
    super(bodyDataLayer);
    this.bodyDataProvider = bodyDataProvider;
    this.editorInput = (ReviewQuestionaireEditorInput) editorInput;

  }

  /**
   * Labels which are to be configured in each cell
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    Object rowObject = this.bodyDataProvider.getRowObject(rowPosition);
    if (rowObject instanceof GroupByObject) {
      super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    }
    else if (rowObject instanceof Question) {

      // get the row object out of the dataprovider
      Question question = (Question) rowObject;


      // if a heading
      if (question.getHeadingFlag()) {
        configLabels.addLabel("NONE");
      }
      boolean deletedFlag = ((Question) rowObject).getDeletedFlag();
      if (deletedFlag) {
        configLabels.addLabel("DELVALUE");
      }

      if ((columnPosition > CONFIG_COLS_INDEX) && (columnPosition < 13)) {
        if (columnPosition == CommonUIConstants.COLUMN_INDEX_3) {
          if (deletedFlag ||
              !CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getMeasurementRelevantFlag())) {
            configLabels.addLabel("COMBO_MEASUREMENT_READONLY");
          }
          else {
            configLabels.addLabel("COMBO_MEASUREMENT");
          }
        }
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_4) {
          if (deletedFlag ||
              !CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getSeriesRelevantFlag())) {
            configLabels.addLabel("COMBO_SERIES_READONLY");
          }
          else {
            configLabels.addLabel("COMBO_SERIES");
          }
        }
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_5) {
          if (deletedFlag ||
              !CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getLinkRelevantFlag())) {
            configLabels.addLabel("COMBO_LINK_READONLY");
          }
          else {
            configLabels.addLabel("COMBO_LINK");
          }
        }
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_6) {
          if (deletedFlag ||
              !CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getRemarkRelevantFlag())) {
            configLabels.addLabel("COMBO_REMARK_READONLY");
          }
          else {
            configLabels.addLabel("COMBO_REMARK");
          }
        }
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_7) {
          if (deletedFlag ||
              !CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getOpenPointsRelevantFlag())) {
            configLabels.addLabel("COMBO_OP_READONLY");
          }
          else {
            configLabels.addLabel("COMBO_OP");
          }
        }
        //
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_8) {
          if (!deletedFlag &&
              CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getMeasureRelaventFlag())) {
            configLabels.addLabel("COMBO_MEASURE");
          }
          else {
            configLabels.addLabel("COMBO_MEASURE_READONLY");
          }
        }
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_9) {
          if (!deletedFlag &&
              CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getResponsibleRelaventFlag())) {
            configLabels.addLabel("COMBO_RESPONSIBLE");
          }
          else {
            configLabels.addLabel("COMBO_RESPONSIBLE_READONLY");
          }
        }
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_10) {
          if (!deletedFlag &&
              CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getCompletionDateRelaventFlag())) {
            configLabels.addLabel("COMBO_DATE");
          }
          else {
            configLabels.addLabel("COMBO_DATE_READONLY");
          }
        }
        else if (columnPosition == CommonUIConstants.COLUMN_INDEX_11) {
          if (deletedFlag ||
              !CommonUtils.getBooleanType(this.editorInput.getSelQuestionareVersion().getResultRelevantFlag())) {
            configLabels.addLabel("COMBO_RESULT_READONLY");
          }
          else {
            configLabels.addLabel("COMBO_RESULT");
          }
        }
      }

    }
  }
}

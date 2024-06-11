package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;

/**
 * This class provides labels for body datalayer of the GridLayer of the NatTable
 */
public class ReviewResultLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * NAT table Data Layer
   */
  private final ILayer layer;
  private final ReviewResultClientBO resultData;
  private boolean isModifiable;

  /**
   * Constructor
   *
   * @param layer data layer
   * @param paramListPage Review Result Param List Page
   */
  public ReviewResultLabelAccumulator(final ILayer layer, final ReviewResultParamListPage paramListPage) {
    super(layer);
    this.layer = layer;
    this.resultData = ((ReviewResultEditorInput) (paramListPage.getEditorInput())).getResultData();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // get the row object out of the dataprovider
    @SuppressWarnings("unchecked")
    IRowDataProvider<CDRResultParameter> bodyDataProvider =
        (IRowDataProvider<CDRResultParameter>) ((DataLayer) this.layer).getDataProvider();
    Object rowObject = bodyDataProvider.getRowObject(rowPosition);
    if (rowObject instanceof GroupByObject) {
      super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    }
    else if (rowObject instanceof CDRResultParameter) {

      CDRResultParameter resParam = (CDRResultParameter) rowObject;
      // ICDM-2439
      if (columnPosition == ReviewResultParamListPage.SSD_CLASS_COL_NUMBER) {
        configLabelsForSsdColumn(configLabels, resParam);
      }

      if (columnPosition == ReviewResultParamListPage.PARAMETER_COL_NUMBER) {
        configLabelsForParameterColumn(configLabels, resParam);
      }
      else if (columnPosition == ReviewResultParamListPage.BITWISE_COL_NUMBER) {
        if (this.resultData.isBitwiseFlagChanged(resParam)) {
          configLabels.addLabel(CdrUIConstants.BITWISE_FLAG_DIFF_LABEL);
        }
      }
      else if (columnPosition == ReviewResultParamListPage.LOWER_LIMIT_COL_NUMBER) {
        if (this.resultData.isLowerLimitChanged(resParam)) {
          configLabels.addLabel(CdrUIConstants.LOW_LIMIT_DIFF_LABEL);
        }
      }
      else {
        configLabelsForColumns(configLabels, columnPosition, resParam);
      }

    }
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
  }

  /**
   * @param configLabels
   * @param columnPosition
   * @param resParam
   */
  private void configLabelsForColumns(final LabelStack configLabels, final int columnPosition,
      final CDRResultParameter resParam) {
    if (columnPosition == ReviewResultParamListPage.REF_VAL_COL_NUMBER) {
      if (this.resultData.isRefValChanged(resParam)) {
        configLabels.addLabel(CdrUIConstants.REF_VALUE_DIFF_LABEL);
      }
    } // ICDM-2151
    else if (columnPosition == ReviewResultParamListPage.UPPER_LIMIT_COL_NUMBER) {
      if (this.resultData.isUpperLimitChanged(resParam)) {
        configLabels.addLabel(CdrUIConstants.UPPER_LIMIT_DIFF_LABEL);
      }
    } // ICDM-2151
    else if (columnPosition == ReviewResultParamListPage.BIT_LIMIT_COL_NUMBER) {
      if (this.resultData.isBitwiseLimitChanged(resParam)) {
        configLabels.addLabel(CdrUIConstants.BITWISE_LIMIT_DIFF_LABEL);
      }
    }
    else if (columnPosition == ReviewResultParamListPage.READY_FOR_SERIES_COL_NUMBER) {
      if (this.resultData.isReadyForSeriesFlagChanged(resParam)) {
        configLabels.addLabel(CdrUIConstants.READY_FOR_SERIES_DIFF_LABEL);
      }
    }
    // ICDM-2151
    else {
      configLabelsForOtherColumns(configLabels, columnPosition, resParam);
    }
  }

  /**
   * @param configLabels
   * @param columnPosition
   * @param resParam
   */
  private void configLabelsForOtherColumns(final LabelStack configLabels, final int columnPosition,
      final CDRResultParameter resParam) {
    if (columnPosition == ReviewResultParamListPage.EXACT_MATCH_COL_NUMBER) {
      if (this.resultData.isExactMatchFlagChanged(resParam)) {
        configLabels.addLabel(CdrUIConstants.EXACT_MATCH_DIFF_LABEL);
      }
    } // ICDM-2151
    else if (columnPosition == ReviewResultParamListPage.CHECK_VALUE_COL_NUMBER) {
      configLabelsForCheckValColumn(configLabels, resParam);
    }
    // iCDM-2151
    else if (columnPosition == ReviewResultParamListPage.CHECK_VALUE_UNIT_COL_NUMBER) {
      if (this.resultData.isCheckValueRefValueUnitDifferent(resParam)) {
        configLabels.addLabel(CdrUIConstants.CHK_VALUE_UNIT_DIFF_LABEL);
      }
    }
    // ICDM-2151
    else if (columnPosition == ReviewResultParamListPage.RESULT_COL_NUMBER) {
      if (this.resultData.isResultChanged(resParam)) {
        configLabels.addLabel(CdrUIConstants.RESULT_DIFF_LABEL);
      }
    }
    else if (columnPosition == ReviewResultParamListPage.SEC_RESULT_COL_NUMBER) {
      if (this.resultData.isSecondaryResultChanged(resParam)) {
        configLabels.addLabel(CdrUIConstants.SEC_RESULT_DIFF_LABEL);
      }

    }
    // ICDM-2151
    else if (columnPosition == ReviewResultParamListPage.SCORE_COL_NUMBER) {
      configLabelsForScoreColumn(configLabels, resParam);
    }
    // Task -729547
    else if (columnPosition == ReviewResultParamListPage.COMMENT_COL_NUMBER) {
      configLabelsForCommentColumn(configLabels, resParam);
    }
  }

  /**
   * @param configLabels
   * @param resParam
   */
  private void configLabelsForCheckValColumn(final LabelStack configLabels, final CDRResultParameter resParam) {
    if (this.resultData.isCheckedValueChanged(resParam)) {
      configLabels.addLabel(CdrUIConstants.CHK_VALUE_DIFF_LABEL);
    }
    if (this.resultData.hasHistory(resParam)) {
      configLabels.addLabel(CdrUIConstants.HISTORY_YES_LABEL);
    }
  }

  /**
   * @param configLabels
   * @param resParam
   */
  private void configLabelsForScoreColumn(final LabelStack configLabels, final CDRResultParameter resParam) {
    if (this.resultData.isScoreChanged(resParam)) {
      configLabels.addLabel(CdrUIConstants.SCORE_DIFF_LABEL);
    }

    if (this.isModifiable && !this.resultData.getResultBo().isResultLocked()) {
      // Even if the review is modifiable if the review svore is 0 and it is compliant then editing is impossible.
      if (this.resultData.isShapeFail(resParam)) {
        configLabels.addLabel("COMBO_SCORE_READONLY");
      }
      else {
        configLabels.addLabel("COMBO_SCORE");
      }
    }
    else {
      configLabels.addLabel("COMBO_SCORE_READONLY");
    }
  }

  // Task - 729547
  /**
   * @param configLabels
   * @param resParam
   */
  private void configLabelsForCommentColumn(final LabelStack configLabels, final CDRResultParameter resParam) {

    if (this.isModifiable && !this.resultData.getResultBo().isResultLocked()) {
      //When the review is modifiable the combobox is editable
      configLabels.addLabel("COMBO_COMMENT");
    }
    else {
      configLabels.addLabel("COMBO_COMMENT_READONLY");
    }
  }


  /**
   * @param configLabels
   * @param resParam
   */
  private void configLabelsForParameterColumn(final LabelStack configLabels, final CDRResultParameter resParam) {
    if (this.resultData.isChecked(resParam)) {
      configLabels.addLabel(CdrUIConstants.REVIEWED_LABEL);
    }
    else if ((ApicUtil.compare(this.resultData.getResult(resParam), CDRConstants.RESULT_FLAG.OK.getUiType()) != 0)) {
      configLabels.addLabel(CdrUIConstants.NOT_OK_LABEL);
    }
    else {
      configLabels.addLabel(CdrUIConstants.NOT_REVIEWED_LABEL);
    }
  }

  /**
   * @param configLabels
   * @param resParam
   */
  private void configLabelsForSsdColumn(final LabelStack configLabels, final CDRResultParameter resParam) {
    configLabels.addLabel(CdrUIConstants.MULTI_IMAGE_PAINTER);
    if (this.resultData.isComplianceParameter(resParam)) {
      configLabels.addLabel(CdrUIConstants.COMPLIANCE_LABEL);
    }
    if (this.resultData.isReadOnly(resParam)) {
      configLabels.addLabel(CdrUIConstants.READ_ONLY_LABEL);
    }
    if (this.resultData.isBlackList(resParam)) {
      configLabels.addLabel(CdrUIConstants.BLACK_LIST_LABEL);
    }
    if (this.resultData.isQssdParameter(resParam)) {
      configLabels.addLabel(CdrUIConstants.QSSD_LABEL);
    }
    if (this.resultData.isDependentParam(resParam)) {
      configLabels.addLabel(CDRConstants.DEPENDENT_CHARACTERISTICS);
    }
  }

  /**
   * @return the isModifiable
   */
  public boolean isModifiable() {
    return this.isModifiable;
  }


  /**
   * @param isModifiable the isModifiable to set
   */
  public void setModifiable(final boolean isModifiable) {
    this.isModifiable = isModifiable;
  }
}

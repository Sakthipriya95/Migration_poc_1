/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentQuestionnaireResultsPage;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentQuestionnaires;
import com.bosch.caltool.nattable.CustomFilterGridLayer;

/**
 * @author ajk2cob
 */
public class DataAssessmentQnaireResultsNatToolTip extends NatTableContentTooltip {


  private final DataAssessmentQuestionnaireResultsPage page;

  /**
   * @param natTable natTable
   * @param tooltipRegions tooltipRegions
   * @param dataAssessmentQuestionnaireResultsPage
   * @param questionnaireDetails Questionnaire Details
   */
  public DataAssessmentQnaireResultsNatToolTip(final NatTable natTable, final String[] tooltipRegions,
      final DataAssessmentQuestionnaireResultsPage dataAssessmentQuestionnaireResultsPage) {
    super(natTable, tooltipRegions);
    this.page = dataAssessmentQuestionnaireResultsPage;
  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {

    ILayerCell cell = this.natTable.getCellByPosition(this.natTable.getColumnPositionByX(event.x),
        this.natTable.getRowPositionByY(event.y));

    if ((cell != null) && !cell.getConfigLabels().hasLabel(GridRegion.FILTER_ROW)) {
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());

      if (isVisibleContentPainter(painter)) {
        int row = LayerUtil.convertRowPosition(this.natTable, this.natTable.getRowPositionByY(event.y),
            ((CustomFilterGridLayer<DataAssessmentQuestionnaires>) this.natTable.getLayer()).getDummyDataLayer());
        Object rowObject = this.page.getQuestionnaireResultsFilterGridLayer().getBodyDataProvider().getRowObject(row);

        if (rowObject instanceof DataAssessmentQuestionnaires) {
          final DataAssessmentQuestionnaires compareRowObject = (DataAssessmentQuestionnaires) rowObject;
          return getQnaireResLinkTootip(cell, cell.getColumnIndex(), compareRowObject);
        }
      }
    }
    return null;
  }


  /**
   * @param event
   * @param col
   * @param cell
   * @param row
   * @param col
   * @param cell
   * @return
   */
  private String getQnaireResLinkTootip(final ILayerCell cell, final int col,
      final DataAssessmentQuestionnaires compareRowObject) {

    String tooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());
    if (cell.getConfigLabels().hasLabel(ApicConstants.CONFIG_LABEL_HYPERLINK) &&
        !cell.getConfigLabels().getLabels().contains(GridRegion.COLUMN_HEADER)) {
      if (CommonUtils.isNotNull(compareRowObject.getQnaireBaselineLink())) {
        tooltipValue = compareRowObject.getQnaireBaselineLink().getDisplayText();
      }
      // return tooltip if not null
      if (tooltipValue.length() > 0) {
        return tooltipValue;
      }
    }
    return getTooltipForProdReadyAndBaselineExisitingClmn(cell, col, compareRowObject, tooltipValue);
  }


  /**
   * @param cell
   * @param col
   * @param compareRowObject
   * @param tooltipValue
   * @return
   */
  private String getTooltipForProdReadyAndBaselineExisitingClmn(final ILayerCell cell, final int col,
      final DataAssessmentQuestionnaires compareRowObject, final String tooltipValue) {
    if (cell.getConfigLabels().hasLabel(GridRegion.COLUMN_HEADER) && !CommonUtils.isEmptyString(tooltipValue)) {
      return tooltipValue;
    }
    if (col == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_ASSESSMENT_PROD_READY) {
      return getBooleanText(compareRowObject.isQnaireReadyForProd());
    }

    if (col == DataAssessmentQuestionnaireResultsPage.COLUMN_NUM_QUESTIONNAIRE_RESULTS_BASELINE_EXISTING) {
      return getBooleanText(compareRowObject.isQnaireBaselineExisting());
    }
    return tooltipValue;
  }

  private String getBooleanText(final boolean booleanValue) {
    return booleanValue ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
  }

}

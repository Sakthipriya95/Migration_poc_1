/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.CellDisplayConversionUtils;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.tooltip.NatTableContentTooltip;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractGroupByNatFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;


/**
 * @author dmo5cob Change the tool tip for Nat table Items.
 */
public class QuestionnaireNatToolTip extends NatTableContentTooltip {


  /**
   *
   */
  private static final String FINISHED_MARKER_TOOLTIP = ",question is completely answered.";
  /**
   *
   */
  private static final String NOT_FINISHED_MARKER_TOOLTIP =
      "Question is not yet answered. Fill out all mandatory items of this question:\n";
  private final AbstractGroupByNatFormPage natPage;

  /**
   * @param natTable natTable
   * @param tooltipRegions tooltipRegions
   * @param natPage AbstractGroupByNatFormPage
   */
  public QuestionnaireNatToolTip(final NatTable natTable, final String[] tooltipRegions,
      final AbstractGroupByNatFormPage natPage) {
    super(natTable, tooltipRegions);
    this.natPage = natPage;
  }

  /**
   * {@inheritDoc} Overriding the GetText method for Changing the Contants.
   */
  @Override
  protected String getText(final Event event) {
    int col = this.natTable.getColumnPositionByX(event.x);
    int row = this.natTable.getRowPositionByY(event.y);
    // get the cell
    ILayerCell cell = this.natTable.getCellByPosition(col, row);
    Object rowObject = null;
    QnaireRespSummaryPage qnaireRespSummaryPage = null;


    if (cell != null) {
      if (QuestionnaireNatToolTip.this.natPage instanceof QnaireRespSummaryPage) {
        qnaireRespSummaryPage = (QnaireRespSummaryPage) QuestionnaireNatToolTip.this.natPage;
        rowObject =
            qnaireRespSummaryPage.getQuestionsFilterGridLayer().getBodyDataProvider().getRowObject(cell.getRowIndex());
      }
      // if the registered cell painter is the PasswordCellPainter, there will be no tooltip
      ICellPainter painter = this.natTable.getConfigRegistry().getConfigAttribute(CellConfigAttributes.CELL_PAINTER,
          DisplayMode.NORMAL, cell.getConfigLabels().getLabels());
      if (isVisibleContentPainter(painter)) {
        // get tooltip value
        String cellTooltipValue = CellDisplayConversionUtils.convertDataType(cell, this.natTable.getConfigRegistry());

        Optional<QnaireRespSummaryPage> qnaireSummryPageAvail = Optional.ofNullable(qnaireRespSummaryPage);
        if (qnaireSummryPageAvail.isPresent()) {
          int selColNum = LayerUtil.convertColumnPosition(this.natTable, this.natTable.getColumnPositionByX(event.x),
              qnaireSummryPageAvail.get().getQuestionsFilterGridLayer().getBodyDataLayer());
          if (selColNum == CommonUIConstants.COLUMN_INDEX_9) {
            return CommonUtils.isEmptyString(cellTooltipValue) ? null : getToolTipForResultCol(rowObject,
                cellTooltipValue, (QnaireRespEditorDataHandler) (qnaireSummryPageAvail.get().getDataHandler()));
          }
        }
        // If the tool tip length >0 just return.
        if (cellTooltipValue.length() > 0) {
          return cellTooltipValue;
        }
      }
    }
    return null;
  }

  private String getToolTipForResultCol(final Object rowObject, final String tooltipValue,
      final QnaireRespEditorDataHandler dataHandler) {

    StringBuilder toolTip = new StringBuilder();


    if (rowObject instanceof RvwQnaireAnswer) {
      RvwQnaireAnswer rvwQnaireAnswer = (RvwQnaireAnswer) rowObject;
      if (tooltipValue.startsWith(CDRConstants.FINISHED_MARKER)) {
        toolTip.append(tooltipValue).append(FINISHED_MARKER_TOOLTIP);
      }
      else if (CDRConstants.NOT_FINISHED_MARKER.equals(tooltipValue)) {

        toolTip.append(NOT_FINISHED_MARKER_TOOLTIP + getNotFilledCols(rvwQnaireAnswer, dataHandler));


      }
      return toolTip.toString();

    }

    return "";

  }

  /**
   * @param rvwQnaireAnswer
   * @param dataHandler
   * @return
   */
  private String getNotFilledCols(final RvwQnaireAnswer rvwQnaireAnswer,
      final QnaireRespEditorDataHandler dataHandler) {
    StringJoiner notFilledCol = new StringJoiner("\n");

    if (dataHandler.isMeasurementMandatory(rvwQnaireAnswer) &&
        CommonUtils.isEmptyString(rvwQnaireAnswer.getMeasurement())) {
      notFilledCol
          .add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_MEASURABLE_Y_N));
    }
    if (dataHandler.isSeriesMandatory(rvwQnaireAnswer) && CommonUtils.isEmptyString(rvwQnaireAnswer.getSeries())) {
      notFilledCol
          .add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_SERIES_MAT_Y_N));
    }
    if (dataHandler.isRemarksMandatory(rvwQnaireAnswer) && CommonUtils.isEmptyString(rvwQnaireAnswer.getRemark())) {
      notFilledCol.add(CommonUiUtils.getMessage(ApicConstants.REVIEW_QUESTIONNAIRES_GRP, ApicConstants.KEY_REMARK));
    }
    if (dataHandler.isLinkMandatory(rvwQnaireAnswer) &&
        CommonUtils.isNullOrEmpty(dataHandler.getLinks(rvwQnaireAnswer))) {
      notFilledCol.add(CDRConstants.LINK_COL_NAME);
    }
    if (dataHandler.isResultMandatory(rvwQnaireAnswer) &&
        CommonUtils.isEmptyString(rvwQnaireAnswer.getSelQnaireResultAssement())) {
      notFilledCol.add(CDRConstants.ANSWER_COL_NAME);
    }

    return notFilledCol.toString();
  }
}


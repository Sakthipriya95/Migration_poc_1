/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;


/**
 * Column header style configuration
 * 
 * @author mkl2cob
 */
public class QnaireRespColHeaderLabelAccumulator extends ColumnOverrideLabelAccumulator {


  /**
   * QuestionDetailsPage instance
   */
  private final QnaireRespSummaryPage page;

  /**
   * @param layer ILayer
   * @param page QuestionDetailsPage
   */
  public QnaireRespColHeaderLabelAccumulator(final ILayer layer, final QnaireRespSummaryPage page) {
    super(layer);
    this.page = page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    // enable column header styling (font..)
    if (rowPosition == (this.page.getQuestionsFilterGridLayer().getColumnHeaderDataProvider().getRowCount() - 1)) {
      configLabels.addLabel(CdrUIConstants.COL_HEADER_LABEL);
    }

  }

}

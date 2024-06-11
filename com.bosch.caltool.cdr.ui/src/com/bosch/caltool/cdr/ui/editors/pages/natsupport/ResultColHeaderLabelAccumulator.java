/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;


/**
 * CDR Result Column header style configuration
 *
 * @author adn1cob
 */
public class ResultColHeaderLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * ParametersRulePage instance
   */
  private final ReviewResultParamListPage page;

  /**
   * @param layer ILayer
   * @param page ParametersRulePage
   */
  public ResultColHeaderLabelAccumulator(final ILayer layer, final ReviewResultParamListPage page) {
    super(layer);
    this.page = page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    // enable column herader styling (font..)
    if (rowPosition == (this.page.getCustomFilterGridLayer().getColumnHeaderDataProvider().getRowCount() - 1)) {
      configLabels.addLabel(CdrUIConstants.COL_HEADER_LABEL);
    }

  }
}

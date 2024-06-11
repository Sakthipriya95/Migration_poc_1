/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

/**
 * @author bru2cob
 */
public class ParamNatColHeaderLabelAccumulator extends ColumnOverrideLabelAccumulator {

  ParamNatTable paramNatTable;

  /**
   * @param columnHeaderDataLayer
   * @param paramNatTable
   */
  public ParamNatColHeaderLabelAccumulator(final DefaultColumnHeaderDataLayer columnHeaderDataLayer,
      final ParamNatTable paramNatTable) {
    super(columnHeaderDataLayer);
    this.paramNatTable = paramNatTable;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    // enable column herader styling (font..)
    if (rowPosition == (this.paramNatTable.getCustomFilterGridLayer().getColumnHeaderDataProvider().getRowCount() -
        1)) {
      configLabels.addLabel("COL_HEADER");
    }

  }
}

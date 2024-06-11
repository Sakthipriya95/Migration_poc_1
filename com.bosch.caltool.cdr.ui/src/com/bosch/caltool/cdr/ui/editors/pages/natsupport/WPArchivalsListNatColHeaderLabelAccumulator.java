/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.WPArchivalsListPage;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;


/**
 * @author ukt1cob
 */
public class WPArchivalsListNatColHeaderLabelAccumulator extends ColumnOverrideLabelAccumulator {


  /**
   * WPArchivalsListPage
   */
  private final WPArchivalsListPage wpArchivalListPage;

  /**
   * @param layer ILayer
   * @param wpArchivalListPage WPArchivalsListPage
   */
  public WPArchivalsListNatColHeaderLabelAccumulator(final ILayer layer, final WPArchivalsListPage wpArchivalListPage) {
    super(layer);
    this.wpArchivalListPage = wpArchivalListPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    // enable column header styling (font..)
    if (rowPosition == (this.wpArchivalListPage.getWpArchivalFilterGridLayer().getColumnHeaderDataProvider()
        .getRowCount() - 1)) {
      configLabels.addLabel(CdrUIConstants.COL_HEADER_LABEL);
    }
  }

}

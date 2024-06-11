/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.ReviewListPage;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;


/**
 * @author mkl2cob
 */
public class ReviewListNatColHeaderLabelAccumulator extends ColumnOverrideLabelAccumulator {


  /**
   * PIDCRvwResultsDtlsPage instance
   */
  private final ReviewListPage page;

  /**
   * @param layer ILayer
   * @param page ParametersRulePage
   */
  public ReviewListNatColHeaderLabelAccumulator(final ILayer layer, final ReviewListPage page) {
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
    if (rowPosition == (this.page.getDataRprtFilterGridLayer().getColumnHeaderDataProvider().getRowCount() - 1)) {
      configLabels.addLabel(CdrUIConstants.COL_HEADER_LABEL);
    }
  }

}

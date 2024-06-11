/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;


/**
 * ICDM-1764
 *
 * @author mkl2cob
 */
public class PIDCRvwResLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * ILayer
   */
  private final ILayer layer;


  /**
   * Constructor
   *
   * @param layer ILayer
   */
  public PIDCRvwResLabelAccumulator(final ILayer layer) {
    super(layer);
    this.layer = layer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {

    // get the body data provider
    IRowDataProvider<ReviewVariantModel> bodyDataProvider =
        (IRowDataProvider<ReviewVariantModel>) ((DataLayer) this.layer).getDataProvider();
    // get the row object out of the dataprovider
    Object rowObject = bodyDataProvider.getRowObject(rowPosition);
    if (rowObject instanceof GroupByObject) {
      super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
    }
    else if (rowObject instanceof ReviewVariantModel) {
      // accumulate the label CDRConstants.REVIEW_RESULTS_IMAGE if the cell belongs to first column
      if (columnPosition == CommonUIConstants.COLUMN_INDEX_0) {
        configLabels.addLabel(CDRConstants.REVIEW_RESULTS_IMAGE);
      }
    }

  }
}

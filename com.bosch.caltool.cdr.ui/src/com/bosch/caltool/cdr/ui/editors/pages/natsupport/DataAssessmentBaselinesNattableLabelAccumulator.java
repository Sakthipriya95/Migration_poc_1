/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.model.cdr.CDRConstants;

/**
 * This class extends {@link ColumnOverrideLabelAccumulator} which is used for registration/addition of labels for a
 * given column.
 *
 * @author ajk2cob
 */
public class DataAssessmentBaselinesNattableLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * @param bodyDataLayer ILayer instance
   */
  public DataAssessmentBaselinesNattableLabelAccumulator(final ILayer bodyDataLayer) {
    super(bodyDataLayer);
  }

  /**
   * This method is used to add labels to a cell
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    // condition to apply the configuration only to column 0
    if (columnPosition == 0) {
      configLabels.addLabel(CDRConstants.BASELINE_HYPERLINK);
    }
    if (columnPosition == 5) {
      configLabels.addLabel(CDRConstants.FILE_HYPERLINK);
    }
    if (columnPosition == 7) {
      configLabels.addLabel(CDRConstants.SHARE_POINT_UPLOAD);
    }
  }

}

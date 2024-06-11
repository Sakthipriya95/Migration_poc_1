/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages.natsupport;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author elm1cob
 */
public class WorkPkgsLabelAccumulator extends ColumnOverrideLabelAccumulator {

  private final ILayer layer;
  private final IRowDataProvider<?> bodyDataProvider;

  /**
   * @param bodyDataLayer ILayer
   * @param bodyDataProvider IRowDataProvider
   */
  public WorkPkgsLabelAccumulator(final ILayer bodyDataLayer, final IRowDataProvider<?> bodyDataProvider) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    int columnIndex = this.layer.getColumnIndexByPosition(columnPosition);
    List<String> overrides = getOverrides(Integer.valueOf(columnIndex));
    if (overrides != null) {
      for (String configLabel : overrides) {
        configLabels.addLabel(configLabel);
      }
    }
    Object rowObject = this.bodyDataProvider.getRowObject(rowPosition);
    if ((rowObject instanceof A2lWorkPackage) && checkDefaultWp(rowObject)) {
      configLabels.addLabel(ApicUiConstants.CONFIG_LABEL_DISABLE);
      return;
    }
    switch (columnPosition) {
      case ApicUiConstants.COLUMN_INDEX_0:
        configLabels.addLabel(ApicUiConstants.CONFIG_LABEL_WP_NAME);
        break;
      case ApicUiConstants.COLUMN_INDEX_1:
        configLabels.addLabel(ApicUiConstants.CONFIG_LABEL_DESC);
        break;
      case ApicUiConstants.COLUMN_INDEX_2:
        configLabels.addLabel(ApicUiConstants.CONFIG_LABEL_WP_NAME_CUST);
        break;
      default:
        break;
    }
  }

  /**
   * @param rowObject
   * @return
   */
  private boolean checkDefaultWp(final Object rowObject) {
    A2lWorkPackage a2lWpRowObj = (A2lWorkPackage) rowObject;
    if (a2lWpRowObj.getName() != null) {
      return a2lWpRowObj.getName().equals(ApicConstants.DEFAULT_A2L_WP_NAME);
    }
    return false;
  }

}

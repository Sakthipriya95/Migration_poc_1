/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.pages.natsupport;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;

// ICDM-2439
/**
 * @author dja7cob
 */
public class ParamPageLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * ILayer
   */
  private final ILayer layer;

  /**
   * Row data provider
   */
  private final IRowDataProvider<A2LParameter> bodyDataProvider;

  /**
   * @param layer ILayer
   * @param bodyDataProvider IRowDataProvider<A2LParameter>
   */
  public ParamPageLabelAccumulator(final ILayer layer, final IRowDataProvider<A2LParameter> bodyDataProvider) {
    super(layer);
    this.layer = layer;
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
    // get the row object out of the dataprovider
    // label to display icon for compliance parameter
    if (columnPosition == CommonUIConstants.COLUMN_INDEX_0) {
      Object rowObject = this.bodyDataProvider.getRowObject(rowPosition);
      if (rowObject instanceof A2LParameter) {
        A2LParameter a2lParamObject = (A2LParameter) rowObject;
        // if compilant or read only then make the change
        configLabels.addLabel(CDRConstants.MULTI_IMAGE_PAINTER);
        // if black list or read only then make the change
        if (!a2lParamObject.isInCalMemory()) {
          configLabels.addLabel(CDRConstants.NOT_IN_CALMEMORY_LABEL);
        }
        if (a2lParamObject.isComplianceParam()) {
          configLabels.addLabel(CDRConstants.COMPLI);
        }
        if (a2lParamObject.getCharacteristic().isReadOnly()) {
          configLabels.addLabel(CDRConstants.READ_ONLY);
        }
        if (a2lParamObject.getCharacteristic().isDependentCharacteristic()) {
          configLabels.addLabel(CDRConstants.DEPENDENT_CHARACTERISTICS);
        }
        if (a2lParamObject.isBlackList()) {
          configLabels.addLabel(CDRConstants.BLACK_LIST_LABEL);
        }
        if (a2lParamObject.isQssdParameter()) {
          configLabels.addLabel(CDRConstants.QSSD_LABEL);
        }
      }
    }
  }

}

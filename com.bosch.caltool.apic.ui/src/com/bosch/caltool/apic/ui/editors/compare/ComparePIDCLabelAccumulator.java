/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.compare;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectAttributeUtil;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;


/**
 * This class extends {@link ColumnOverrideLabelAccumulator} which is used for registration/addition of labels for a
 * given column.
 *
 * @author jvi6cob
 */
public class ComparePIDCLabelAccumulator extends ColumnOverrideLabelAccumulator {

  /**
   * Instance variable which holds a reference to bodyDataLayer
   */
  private final ILayer layer;
  /**
   * Instance variable which holds a reference to bodyDataProvider
   */
  private final IRowDataProvider<CompareRowObject> bodyDataProvider;
  /**
   * project attribute util instance
   */
  private final ProjectAttributeUtil compareEditorUtil = new ProjectAttributeUtil();

  /**
   * @param bodyDataLayer ILayer instance
   * @param bodyDataProvider IRowDataProvider instance
   */
  public ComparePIDCLabelAccumulator(final ILayer bodyDataLayer,
      final IRowDataProvider<CompareRowObject> bodyDataProvider) {
    super(bodyDataLayer);
    this.layer = bodyDataLayer;
    this.bodyDataProvider = bodyDataProvider;

  }


  /**
   * This method is used to add labels to a cell
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

    if (columnPosition == 0) {
      configLabels.addLabel("BALL");
    }
    CompareRowObject compareRowObject = this.bodyDataProvider.getRowObject(rowPosition);
    // get the attribute object
    IProjectAttribute pidcAttribute =
        compareRowObject.getColumnDataMapper().getColumnIndexPIDCAttrMap().get(columnIndex);

    if (pidcAttribute != null) {
      AbstractProjectAttributeBO projAttrHandler =
          this.compareEditorUtil.getProjectAttributeHandler(pidcAttribute, compareRowObject.getColumnDataMapper()
              .getProjectHandlerMap().get(this.compareEditorUtil.getID(pidcAttribute)));
      // attr visible / invisible
      if (!projAttrHandler.isVisible()) {
        configLabels.addLabel("INVISIBLE");
      }
      else {
        configLabels.removeLabel("INVISIBLE");
      }
    }

  }


}
/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views.providers;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;


/**
 * @author MSP5COB
 */
public class ARCTableLabelProvider extends ColumnLabelProvider {

  private int count = 0;

  private final List<String> uniqueParamList = new ArrayList<>();


  /**
   * This method resets the row counter, used when invoking filters
   */
  public void resetRowCounter() {
    this.uniqueParamList.clear();
    this.count = 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(final ViewerCell cell) {
    String cdrResultParameterName = ((CDRResultParameter) cell.getElement()).getName();
    int columnIndex = cell.getColumnIndex();
    if (columnIndex == 0) {
      // If Column Index is 0, then it is row count column
      // This method is invoked multiple times for same record
      // To avoid duplicate row count only unique records are considered
      if (!this.uniqueParamList.contains(cdrResultParameterName)) {
        this.uniqueParamList.add(cdrResultParameterName);
        this.count++;
        // Converting the count to String and setting to cell
        cell.setText(this.count + "");
      }
    }
    else {
      cell.setText(cdrResultParameterName);
    }
  }

}
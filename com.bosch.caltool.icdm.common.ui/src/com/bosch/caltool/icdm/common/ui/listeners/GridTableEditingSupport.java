/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.listeners;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterBaseComponents;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterFunction;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFilterParameter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.emr.EMRWizardData;


/**
 * This class is for providing editing support in GridTable ICDM-886
 *
 * @author bru2cob
 */
public class GridTableEditingSupport extends EditingSupport {

  // ICDM-1011
  /**
   * Parameter filter table max column number
   */
  private static final int PARAM_MAX_COL = 4;
  /**
   * Parameter filter table min column number
   */
  private static final int PARAM_MIN_COL = 3;
  /**
   * BC filter table version column number
   */
  private static final int BC_VER_COL = 2;
  /**
   * FC filter table version column number
   */
  private static final int FC_VER_COL = 1;
  /**
   * PIDC validity column number
   */
  private static final int CODEX_FILE_DESC_COL = 1;

  /**
   * column index
   */
  private final int columnIndex;
  /**
   * GridTableViewer instance
   */
  private final GridTableViewer viewer;


  /**
   * Constructor
   *
   * @param viewer gridtable
   * @param column_index col index
   */
  public GridTableEditingSupport(final GridTableViewer viewer, final int column_index) {
    super(viewer);
    this.viewer = viewer;
    this.columnIndex = column_index;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CellEditor getCellEditor(final Object element) {
    if (element instanceof A2LFilterParameter) {
      // when the element is A2LFilterParameter
      if ((this.columnIndex == PARAM_MIN_COL) || (this.columnIndex == PARAM_MAX_COL)) {
        return new TextCellEditor(this.viewer.getGrid());
      }
    }
    // ICDM-1011
    else if (element instanceof A2LFilterBaseComponents) {
      // when the element is A2LFilterBaseComponents
      if (this.columnIndex == BC_VER_COL) {
        return new TextCellEditor(this.viewer.getGrid());
      }
    }
    else if (element instanceof A2LFilterFunction) {
      // when the element is A2LFilterFunction
      if (this.columnIndex == FC_VER_COL) {
        return new TextCellEditor(this.viewer.getGrid());
      }
    }
    else if (element instanceof EMRWizardData) {
      // when the element is CodexWizardData
      if (this.columnIndex == CODEX_FILE_DESC_COL) {
        return new TextCellEditor(this.viewer.getGrid());
      }
    }
    return null;
  }

  /**
   * returns true if the text editor is editable {@inheritDoc}
   */
  @Override
  protected boolean canEdit(final Object element) {
    boolean flag = false;

    if (element instanceof A2LFilterParameter) {
      if ((this.columnIndex == PARAM_MIN_COL) || (this.columnIndex == PARAM_MAX_COL)) {
        // ICDM-934
        A2LFilterParameter a2lParam = (A2LFilterParameter) element;
        if ((a2lParam.getCalData() != null) && a2lParam.getCalData().getCalDataPhy().isText()) {
          flag = false;
        }
        else {
          flag = true;
        }
      }
    }
    else if (element instanceof A2LFilterFunction) {
      if (this.columnIndex == FC_VER_COL) {
        flag = true;
      }
    }
    // ICDM-1011
    else if (element instanceof A2LFilterBaseComponents) {
      if (this.columnIndex == BC_VER_COL) {
        flag = true;
      }
    }
    else if (element instanceof EMRWizardData) {
      // when the element is CodexWizardData
      if (this.columnIndex == CODEX_FILE_DESC_COL) {
        flag = true;
      }
    }

    return flag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getValue(final Object element) {

    if (element instanceof A2LFilterParameter) {
      if (this.columnIndex == PARAM_MIN_COL) {
        return ((A2LFilterParameter) element).getMinValue();
      }
      else if (this.columnIndex == PARAM_MAX_COL) {
        return ((A2LFilterParameter) element).getMaxValue();
      }
    }
    // ICDM-1011
    else if (element instanceof A2LFilterBaseComponents) {
      if (this.columnIndex == BC_VER_COL) {
        return ((A2LFilterBaseComponents) element).getBcVersion();
      }
    }
    else if (element instanceof A2LFilterFunction) {
      if (this.columnIndex == FC_VER_COL) {
        return ((A2LFilterFunction) element).getFunctionVersion();
      }
    }
    else if (element instanceof EMRWizardData) {
      // when the element is CodexWizardData
      if (this.columnIndex == CODEX_FILE_DESC_COL) {
        return ((EMRWizardData) element).getDescripton();
      }
    }

    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setValue(final Object element, final Object value) {

    if (element instanceof A2LFilterParameter) {
      if (this.columnIndex == PARAM_MIN_COL) {
        editingMinCol(element, value);
      }
      else if (this.columnIndex == PARAM_MAX_COL) {
        editingMaxCol(element, value);
      }
    }
    // ICDM-1011
    else if (element instanceof A2LFilterBaseComponents) {
      if (this.columnIndex == BC_VER_COL) {
        editingBCVerCol(element, value);
      }
    }
    else if (element instanceof A2LFilterFunction) {
      if (this.columnIndex == FC_VER_COL) {
        editingFunctionVersion(element, value);
      }
    }
    else if (element instanceof EMRWizardData) {
      // when the element is CodexWizardData
      if (this.columnIndex == CODEX_FILE_DESC_COL) {
        editCodexFileDescription(element, value);
      }
    }

  }

  /**
   * @param element
   * @param value
   */
  private void editCodexFileDescription(final Object element, final Object value) {
    final String desc = String.valueOf(value);
    ((EMRWizardData) element).setDescripton(desc);
    this.viewer.refresh();
  }

  /**
   * @param element bc
   * @param value edited value
   */
  // ICDM-1011
  private void editingBCVerCol(final Object element, final Object value) {
    final String version = String.valueOf(value);
    if (CommonUtils.isEmptyString(version)) {
      CDMLogger.getInstance().infoDialog("Please enter valid version!",
          com.bosch.caltool.icdm.common.ui.Activator.PLUGIN_ID);
    }
    else {
      ((A2LFilterBaseComponents) element).setBcVersion(version);
      this.viewer.refresh();
    }
  }

  /**
   * editing min col
   *
   * @param element param
   * @param value value typed
   */
  private void editingMinCol(final Object element, final Object value) {
    final String minVal = String.valueOf(value);
    // ICDM-934
    if ("-".equals(minVal) || CommonUtils.isValidDouble(minVal)) {
      // update min value and refresh the table
      ((A2LFilterParameter) element).setMinValue(minVal);
      this.viewer.refresh();
    }
    else {
      CDMLogger.getInstance().infoDialog("Please enter valid number!",
          com.bosch.caltool.icdm.common.ui.Activator.PLUGIN_ID);
    }

  }

  /**
   * editing min col
   *
   * @param element param
   * @param value value typed
   */
  private void editingFunctionVersion(final Object element, final Object value) {
    final String version = String.valueOf(value);

    ((A2LFilterFunction) element).setFunctionVersion(version);
    this.viewer.refresh();

  }

  /**
   * editing max col
   *
   * @param element param
   * @param value value typed
   */
  private void editingMaxCol(final Object element, final Object value) {
    final String maxVal = String.valueOf(value);
    // ICDM-934
    if ("-".equals(maxVal) || CommonUtils.isValidDouble(maxVal)) {
      // update max value and refresh the table
      ((A2LFilterParameter) element).setMaxValue(maxVal);
      this.viewer.refresh();
    }
    else {
      CDMLogger.getInstance().infoDialog("Please enter valid number!",
          com.bosch.caltool.icdm.common.ui.Activator.PLUGIN_ID);
    }

  }


}

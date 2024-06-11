/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.natsupport;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.edit.command.EditCellCommand;
import org.eclipse.nebula.widgets.nattable.filterrow.command.ClearFilterCommand;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.action.SelectCellAction;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.sort.command.SortColumnCommand;
import org.eclipse.nebula.widgets.nattable.tree.action.TreeExpandCollapseAction;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.swt.events.MouseEvent;

import com.bosch.caltool.icdm.model.a2l.A2LWpRespExt;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.nattable.CustomFilterGridLayer;
import com.bosch.caltool.nattable.CustomNATTable;


/**
 * This class is to enable default behaviour of nattable after adding double click feature in the cell <br>
 * 1. sorting when single clicking on column header cell <br>
 * 2. column filtering <br>
 * 3. select cell action when single clicking on any data layer cells in nattable<br>
 *
 * @author svj7cob
 */
// 226387
public class CustomSortFilterGroupEnabler extends SelectCellAction {


  /**
   * Column header in review result editor consists of 2 rows(row 1 and row 2)
   */
  private static final int COLUMN_HEADER_ROW_IN_RVW_RESULT = 2;
  /**
   * Column filter row in review result editor row 3
   */
  private static final int COLUMN_FILTER_ROW_IN_RVW_RESULT = 3;

  /**
   * Column header row position
   */
  private static final int COLUMN_HEADER_ROW_POS = 1;

  /**
   * Column filter row position
   */
  private static final int COLUMN_FILTER_ROW_POS = 2;

  /**
   * the binding registry for the Key
   */
  private final UiBindingRegistry uiBindingRegistry;

  /**
   * is grouping needed in nattable
   */
  private final boolean isGroupingNeeded;

  /**
   * is review result editor
   */
  private boolean isRvwResultEditor;

  /**
   * Nat table instance
   */
  private final CustomNATTable natTable;

  /**
   * NAT table grid layer
   */
  @SuppressWarnings("rawtypes")
  private final CustomFilterGridLayer paramFilterGridLayer;
  /**
   * If column filter has combo box filter enabled
   */
  private boolean isComboRowFilterEnabled;


  /**
   * @param uiBindingRegistryLocal the ui binding
   * @param isGroupingNeededVar true if column grouping needed
   * @param natTableVar the nattable
   * @param paramFilterGridLayerVar the grid filter layer
   */
  public CustomSortFilterGroupEnabler(final UiBindingRegistry uiBindingRegistryLocal, final boolean isGroupingNeededVar,
      final CustomNATTable natTableVar,
      @SuppressWarnings("rawtypes") final CustomFilterGridLayer paramFilterGridLayerVar) {
    this.uiBindingRegistry = uiBindingRegistryLocal;
    this.isGroupingNeeded = isGroupingNeededVar;
    this.natTable = natTableVar;
    this.paramFilterGridLayer = paramFilterGridLayerVar;
  }

  /**
   * if the row is header , then row pos=1, column sorting process<br>
   * if the row is column filter, then row pos=2, column filtering process<br>
   * if the row is cdr result param, then row pos=2[if the F3 pressed and column filter hidden], pos=3, row grouping or
   * cdr result param cell
   */
  @Override
  public void run(final NatTable natTableVar, final MouseEvent mouseEvent) {
    if (null != this.uiBindingRegistry) {
      int rowPozition = natTableVar.getRowPositionByY(mouseEvent.y);
      if ((rowPozition == COLUMN_HEADER_ROW_POS) || checkIsHeaderInRvwResult(rowPozition)) {
        int columnPozitionLocal = ((NatEventData) mouseEvent.data).getColumnPosition();

        // applying sorting
        natTableVar.doCommand(new SortColumnCommand(natTableVar, columnPozitionLocal, false));
      }
      else if ((rowPozition == COLUMN_FILTER_ROW_POS) || checkIsFilterRowInRvwResult(rowPozition)) {
        int columnPozition = natTableVar.getColumnPositionByX(mouseEvent.x);
        Object dataValueByPosition = natTableVar.getDataValueByPosition(columnPozition, rowPozition);
        if ("null".equals(String.valueOf(dataValueByPosition)) || this.isComboRowFilterEnabled) {

          // applying column filtering
          natTableVar.doCommand(new EditCellCommand(natTableVar, natTableVar.getConfigRegistry(),
              natTableVar.getCellByPosition(columnPozition, rowPozition)));
        }
        else {
          NatEventData natEventDataVar = (NatEventData) mouseEvent.data;

          // clear column filtering
          natTableVar.doCommand(new ClearFilterCommand(natTableVar, natEventDataVar.getColumnPosition()));
        }
      }
      else {
        if (this.isGroupingNeeded) {
          if (isRowParamAllowed(rowPozition)) {
            executeSelectCellCommand(natTableVar, mouseEvent);
          }
          else {
            // row expand/collapse action
            new TreeExpandCollapseAction().run(natTableVar, mouseEvent);
          }
        }
        else {
          // if grouping not needed, use select cell action
          executeSelectCellCommand(natTableVar, mouseEvent);
        }
      }
      natTableVar.redraw();
    }
  }

  /**
   * @param rowPozition
   * @return
   */
  private boolean checkIsHeaderInRvwResult(final int rowPozition) {
    return isRvwResultEditor() &&
        ((rowPozition == COLUMN_HEADER_ROW_IN_RVW_RESULT) || (rowPozition == COLUMN_HEADER_ROW_POS));
  }

  /**
   * @param rowPozition
   * @return
   */
  private boolean checkIsFilterRowInRvwResult(final int rowPozition) {
    return isRvwResultEditor() && (rowPozition == COLUMN_FILTER_ROW_IN_RVW_RESULT);
  }

  /**
   * This method executes select cell command
   *
   * @param natTableVar the given nattable
   * @param mouseEvent the mouse event
   */
  private void executeSelectCellCommand(final NatTable natTableVar, final MouseEvent mouseEvent) {
    super.run(natTableVar, mouseEvent);

    // select cell action
    natTableVar.doCommand(new SelectCellCommand(natTableVar, getGridColumnPosition(), getGridRowPosition(),
        isWithShiftMask(), isWithControlMask()));
  }


  /**
   * Checks if the row object is either cdr result param or a2l wp responsiblity param
   *
   * @param rowPozitionByY the y position
   * @return true if the row object is either cdr result param or a2l wp responsiblity param
   */
  private boolean isRowParamAllowed(final int rowPozitionByY) {
    final SelectionLayer selectionLayer =
        CustomSortFilterGroupEnabler.this.paramFilterGridLayer.getBodyLayer().getSelectionLayer();
    int rowPosition = LayerUtil.convertRowPosition(this.natTable, rowPozitionByY, selectionLayer);
    Object rowObject =
        CustomSortFilterGroupEnabler.this.paramFilterGridLayer.getBodyDataProvider().getRowObject(rowPosition);
    // cdr result parameter as in rvw result editor, a2l wp responsiblity as in a2l editor
    return (rowObject instanceof CDRResultParameter) || (rowObject instanceof A2LWpRespExt);
  }

  /**
   * @return the isComboRowFilterEnabled
   */
  public boolean isComboRowFilterEnabled() {
    return this.isComboRowFilterEnabled;
  }


  /**
   * @param isComboRowFilterEnabled the isComboRowFilterEnabled to set
   */
  public void setComboRowFilterEnabled(final boolean isComboRowFilterEnabled) {
    this.isComboRowFilterEnabled = isComboRowFilterEnabled;
  }


  /**
   * @return the isRvwResultEditor
   */
  public final boolean isRvwResultEditor() {
    return this.isRvwResultEditor;
  }


  /**
   * @param isRvwResultEditor the isRvwResultEditor to set
   */
  public final void setRvwResultEditor(final boolean isRvwResultEditor) {
    this.isRvwResultEditor = isRvwResultEditor;
  }
}

package com.bosch.caltool.icdm.common.ui.dragdrop;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

/**
 * The CustomDragListener class provides event notification to the application for DragSource events.
 *
 * @author jvi6cob
 */
public class CustomDragListener implements DragSourceListener {

  private final Viewer viewer;

  private ISelection selection;

  /**
   * RowSelectionProvider is passed in when drag is initiated on a Nat Table Viewer
   */
  private ISelectionProvider natTableRowSelectionProvider;

  private SelectionLayer selectionLayer;

  private NatTable natTable;

  /**
   * Constructor which accepts a viewer
   *
   * @param viewer Viewer
   */
  public CustomDragListener(final Viewer viewer) {
    this.viewer = viewer;
  }

  /**
   * Constructor which accepts a natTable Row SelectionProvider
   *
   * @param natTableRowSelectionProvider ISelectionProvider
   * @param selectionLayer SelectionLayer
   * @param natTable NatTable </br>
   *          </br>
   *          SelectionLayer and NatTable are required for limiting the drag source selection to body part of the
   *          natTable
   */
  public CustomDragListener(final ISelectionProvider natTableRowSelectionProvider, final SelectionLayer selectionLayer,
      final NatTable natTable) {
    this.natTableRowSelectionProvider = natTableRowSelectionProvider;
    this.selectionLayer = selectionLayer;
    this.natTable = natTable;
    this.viewer = null;
  }

  @Override
  public void dragFinished(final DragSourceEvent event) {
    // TO-DO
  }

  @Override
  public void dragSetData(final DragSourceEvent event) {
    LocalSelectionTransfer.getTransfer().setSelection(this.selection);
  }

  @Override
  public void dragStart(final DragSourceEvent event) {
    // TODO: Drag can be canceled based on selection.This can be implemented once all draggable items are identified
    event.doit = false;
    ISelection currentSelection = null;
    if (this.viewer != null) {
      currentSelection = this.viewer.getSelection();
    }
    else if (this.natTableRowSelectionProvider != null) {
      currentSelection = this.natTableRowSelectionProvider.getSelection();

    }
    if ((currentSelection != null) && !currentSelection.isEmpty()) {
      event.doit = true;
      this.selection = currentSelection;
    }
    // If drag is started from a NatTable
    // The below checks ensure the drag source area is confined to the nat body layer
    if (((this.selectionLayer != null) && (this.selectionLayer.getSelectionModel().getSelectedRowCount() == 0)) ||
        ((this.natTable != null) && !this.natTable.getRegionLabelsByXY(event.x, event.y).hasLabel(GridRegion.BODY))) {
      event.doit = false;
    }
  }

}
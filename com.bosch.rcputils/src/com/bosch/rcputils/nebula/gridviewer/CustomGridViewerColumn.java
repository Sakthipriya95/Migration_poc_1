/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.nebula.gridviewer;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


/**
 * GridViewer Column doesnt have the ability to remove listeners. Failure to remove listeners results in Multiple
 * executions of commands for a single checkbox operation in UseCaseAttributesPage This is a Custom class which provides
 * the ability to remove listeners.
 * 
 * @author jvi6cob
 */
public class CustomGridViewerColumn extends ViewerColumn {

  /**
   * ColumnViewer instance
   */
  private final ColumnViewer columnViewer;
  /**
   * GridColumn instance
   */
  private final GridColumn gridColumn;
  /**
   * CheckEditingSupport instance
   */
  private CheckEditingSupport chkEditingSupport;
  /**
   * Column resize listener
   */
  protected Listener colResizeLstnr;
  /**
   * 
   */
  private Listener checkListener;


  /**
   * Create a new column
   * 
   * @param gridViewer the viewer the column belongs to
   * @param style the style used to create the column for style bits GridColumn
   */
  public CustomGridViewerColumn(final GridTableViewer gridViewer, final int style) {
    this(gridViewer, style, -1);
  }

  /**
   * Create a new column
   * 
   * @param viewer Viewer in which column should appear
   * @param style the style used to create the column for style
   */
  public CustomGridViewerColumn(final GridTreeViewer viewer, final int style) {
    this(viewer, style, -1);
  }

  /**
   * Create a new column
   * 
   * @param viewer Viewer in which column should appear
   * @param style style used to create the column for style
   * @param index the index of the newly created column
   */
  public CustomGridViewerColumn(final GridTableViewer viewer, final int style, final int index) {
    this(viewer, createColumn((Grid) viewer.getControl(), style, index));
  }

  /**
   * @param viewer the viewer the column belongs to
   * @param column the column the viewer is attached to
   */
  public CustomGridViewerColumn(final GridTableViewer viewer, final GridColumn column) {
    this((ColumnViewer) viewer, column);
  }

  /**
   * @param viewer the viewer the column belongs to
   * @param column the column the viewer is attached to
   */
  public CustomGridViewerColumn(final GridTreeViewer viewer, final GridColumn column) {
    this((ColumnViewer) viewer, column);
  }

  /**
   * Create a new column
   * 
   * @param viewer Viewer in which column should appear
   * @param style the style used to create the column for style bits
   * @param index the index of the newly created column
   */
  public CustomGridViewerColumn(final GridTreeViewer viewer, final int style, final int index) {
    this(viewer, createColumn((Grid) viewer.getControl(), style, index));
  }

  /**
   * @param viewer
   * @param column
   */
  CustomGridViewerColumn(final ColumnViewer viewer, final GridColumn column) {
    super(viewer, column);
    this.columnViewer = viewer;
    this.gridColumn = column;
    setColResizeListener();
  }

  /**
   * @param table
   * @param style
   * @param index
   * @return
   */
  private static GridColumn createColumn(final Grid table, final int style, final int index) {
    if (index >= 0) {
      return new GridColumn(table, style, index);
    }
    return new GridColumn(table, style);
  }

  /**
   * Returns the underlying column.
   * 
   * @return the underlying Nebula column
   */
  public GridColumn getColumn() {
    return this.gridColumn;
  }

  /** {@inheritDoc} */
  @Override
  public void setEditingSupport(final EditingSupport editingSupport) {
    if (editingSupport instanceof CheckEditingSupport) {
      if (this.chkEditingSupport == null) {
        onCheckEditingSupport();
      }
      this.chkEditingSupport = (CheckEditingSupport) editingSupport;
    }
    else {
      super.setEditingSupport(editingSupport);
    }
  }

  /**
   * @param colIndex
   * @param event
   */
  private void handleEventIfCheckBox(final int colIndex, final Event event) {
    if ((event.detail == SWT.CHECK) && (event.index == colIndex)) {
      final GridItem item = (GridItem) event.item;
      final Object element = item.getData();
      CustomGridViewerColumn.this.chkEditingSupport.setValue(element, Boolean.valueOf(item.getChecked(colIndex)));
    }
  }

  /**
   * 
   */
  private void setColResizeListener() {
    if (this.colResizeLstnr == null) {
      this.colResizeLstnr = new Listener() {

        /**
         * {@inheritDoc}
         */
        public void handleEvent(final Event event) {
          handleEventOnColResize();
        }
      };
      this.gridColumn.addListener(SWT.Resize, this.colResizeLstnr);
      this.gridColumn.addListener(SWT.Hide, this.colResizeLstnr);
      this.gridColumn.addListener(SWT.Show, this.colResizeLstnr);
    }
  }

  /**
   * Editing support
   */
  private void onCheckEditingSupport() {
    final int colIndex = getColumn().getParent().indexOf(getColumn());
    this.checkListener = new Listener() {

      /**
       * {@inheritDoc}
       */
      public void handleEvent(final Event event) {
        handleEventIfCheckBox(colIndex, event);
      }
    };
    getColumn().getParent().addListener(SWT.Selection, this.checkListener);
  }

  private void unSetColumnResizeListener() {
    if (this.colResizeLstnr != null) {
      this.gridColumn.removeListener(SWT.Resize, this.colResizeLstnr);
      this.colResizeLstnr = null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void handleDispose() {
    unSetColumnResizeListener();
    super.handleDispose();
  }

  /**
   * 
   */
  private void handleEventOnColResize() {
    boolean autoPreferredSize = false;

    if (CustomGridViewerColumn.this.columnViewer instanceof GridTreeViewer) {
      autoPreferredSize = ((GridTreeViewer) CustomGridViewerColumn.this.columnViewer).getAutoPreferredHeight();
    }
    if (CustomGridViewerColumn.this.columnViewer instanceof GridTableViewer) {
      autoPreferredSize = ((GridTableViewer) CustomGridViewerColumn.this.columnViewer).getAutoPreferredHeight();
    }
    if (autoPreferredSize && CustomGridViewerColumn.this.gridColumn.getWordWrap()) {
      final Grid grid = CustomGridViewerColumn.this.gridColumn.getParent();
      for (int index = 0; index < grid.getItemCount(); index++) {
        grid.getItem(index).pack();
      }
      grid.redraw();
    }
  }

  /**
   * *
   */
  public void removeCheckListener() {
    if (this.checkListener != null) {
      getColumn().getParent().removeListener(SWT.Selection, this.checkListener);
    }
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.rcputils.nebula.gridviewer;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;


/**
 * @author mga1cob
 */
public final class GridTableViewerUtil {

  /**
   * sorting direction
   */
  private static final int DIRECTION_UP = 1;
  /**
   * sorting direction
   */
  private static final int DIRECTION_DOWN = 0;
  /**
   * GridTableViewerUtil instance
   */
  private static GridTableViewerUtil gridTabUtil;

  /**
   * The private constructor
   */
  private GridTableViewerUtil() {
    // The private constructor
  }

  /**
   * This method returns GridTableViewerUtil instance
   * 
   * @return GridTableViewerUtil
   */
  public static GridTableViewerUtil getInstance() {
    if (gridTabUtil == null) {
      gridTabUtil = new GridTableViewerUtil();
    }
    return gridTabUtil;
  }

  /**
   * @param style defines style constant Example: SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
   * @param composite instance
   * @return GridTableViewer instance
   */
  public GridTableViewer createCustomGridTableViewer(final Composite composite, final int style) {
    final GridTableViewer gridTabViewer = new CustomGridTableViewer(composite, style);
    gridTabViewer.getGrid().setLinesVisible(true);
    gridTabViewer.getGrid().setHeaderVisible(true);
    gridTabViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    return gridTabViewer;
  }

  /**
   * @param style defines style constant Example: SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
   * @param composite instance
   * @param colIndices List of col indices for multiline tooltip
   * @return GridTableViewer instance
   */
  public GridTableViewer createCustomGridTableViewer(final Composite composite, final int style,
      final int[] colIndices) {
    final GridTableViewer gridTabViewer = new CustomGridTableViewer(composite, style, colIndices);
    gridTabViewer.getGrid().setLinesVisible(true);
    gridTabViewer.getGrid().setHeaderVisible(true);
    gridTabViewer.getGrid().setLayoutData(GridDataUtil.getInstance().getGridData());
    return gridTabViewer;
  }

  /**
   * @param style defines style constant Example: SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
   * @param composite instance
   * @param layoutData instance
   * @return GridTableViewer instance
   */
  public GridTableViewer createGridTableViewer(final Composite composite, final int style, final GridData layoutData) {
    final GridTableViewer gridTabViewer = new GridTableViewer(composite, style);
    gridTabViewer.getGrid().setLinesVisible(true);
    gridTabViewer.getGrid().setHeaderVisible(true);
    gridTabViewer.getGrid().setLayoutData(layoutData);
    return gridTabViewer;
  }

  /**
   * @param style defines style constant Example: SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
   * @param composite instance
   * @return GridTableViewer instance
   */
  public GridTableViewer createGridTableViewer(final Composite composite, final int style) {
    final GridTableViewer gridTabViewer = new GridTableViewer(composite, style);
    gridTabViewer.getGrid().setLinesVisible(true);
    gridTabViewer.getGrid().setHeaderVisible(true);
    return gridTabViewer;
  }

  /**
   * @param composite instance
   * @return GridTableViewer instance
   */
  public GridTableViewer createGridTableViewer(final Composite composite) {
    final GridTableViewer gridTabViewer =
        new GridTableViewer(composite, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    gridTabViewer.getGrid().setLinesVisible(true);
    gridTabViewer.getGrid().setHeaderVisible(true);
    return gridTabViewer;
  }

  /**
   * @param composite instance
   * @param layoutData instance
   * @return GridTableViewer instance
   */
  public GridTableViewer createGridTableViewer(final Composite composite, final GridData layoutData) {
    final GridTableViewer gridTabViewer =
        new GridTableViewer(composite, SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    gridTabViewer.getGrid().setLinesVisible(true);
    gridTabViewer.getGrid().setHeaderVisible(true);
    gridTabViewer.getGrid().setLayoutData(layoutData);
    return gridTabViewer;
  }

  /**
   * This method deletes the selected items from GridTableViewer
   * 
   * @param gridTableViewer instance
   */
  public void deleteSelectedItem(final GridTableViewer gridTableViewer) {
    final Grid grid = gridTableViewer.getGrid();
    grid.remove(grid.getSelectionIndices());
    grid.redraw();
    grid.setRedraw(true);
  }

  /**
   * This method returns selected column index on mouse click of the tableviewer
   * 
   * @param event instance
   * @param gridTabViewer instance
   * @return int returns selected column index
   */
  public int getTabColIndex(final MouseEvent event, final GridTableViewer gridTabViewer) {
    final Point point = new Point(event.x, event.y);
    // Determine which row was selected
    final GridItem item = gridTabViewer.getGrid().getItem(point);
    int columnIndex = -1;
    if (item != null) {
      // Determine which column was selected
      for (int i = 0, n = gridTabViewer.getGrid().getColumnCount(); i < n; i++) {
        final Rectangle rect = item.getBounds(i);
        // This is the selected column
        if (rect.contains(point)) {
          columnIndex = i;
          break;
        }
      }
    }
    return columnIndex;
  }

  /**
   * This method returns selected column index on mouse click of the tableviewer
   * 
   * @param event instance
   * @param treeVrwr instance
   * @return int returns selected column index
   */
  public int getTabColIndex(final MouseEvent event, final CustomTreeViewer treeVrwr) {
    final Point point = new Point(event.x, event.y);
    // Determine which row was selected
    final TreeItem item = treeVrwr.getTree().getItem(point);
    int columnIndex = -1;
    if (item != null) {
      // Determine which column was selected
      for (int i = 0, n = treeVrwr.getTree().getColumnCount(); i < n; i++) {
        final Rectangle rect = item.getBounds(i);
        // This is the selected column
        if (rect.contains(point)) {
          columnIndex = i;
          break;
        }
      }
    }
    return columnIndex;
  }

  /**
   * This method returns selected column index on mouse click of the treeviewer
   * 
   * @param event instance
   * @param gridTabViewer instance
   * @return int returns selected column index
   */
  // ICDM-1135
  public int getTreeColIndex(final MouseEvent event, final CheckboxTreeViewer gridTabViewer) {
    final Point point = new Point(event.x, event.y);
    // Determine which row was selected
    final TreeItem item = gridTabViewer.getTree().getItem(point);
    int columnIndex = -1;
    if (item != null) {
      int colCount = gridTabViewer.getTree().getColumnCount();
      // Determine which column was selected
      for (int i = 0; i < colCount; i++) {
        final Rectangle rect = item.getBounds(i);
        // This is the selected column
        if (rect.contains(point)) {
          columnIndex = i;
          break;
        }
      }
    }
    return columnIndex;
  }

  /**
   * This method returns SelectionAdapter instance
   * 
   * @param column instance
   * @param index defines column index
   * @param viewerSorter defines ViewerSorter
   * @param tabViewer GridTabViewer instance
   * @return SelectionAdapter instance
   */
  public SelectionAdapter getSelectionAdapter(final GridColumn column, final int index,
      final AbstractViewerSorter viewerSorter, final GridTableViewer tabViewer) {
    final SelectionAdapter selectionAdapter = new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        viewerSorter.setColumn(index);
        final int direction = viewerSorter.getDirection();
        for (int i = 0; i < tabViewer.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == 0) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == 1) {
              column.setSort(SWT.UP);
            }
          }
          // This below condition is required only in case of GridTableViewer Columns sorting, it will update the sorted
          // directions to the columns, incase of normal jface TableViewer the below condition is not required
          if (i != index) {
            tabViewer.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        tabViewer.refresh();
      }
    };
    return selectionAdapter;
  }

  // ICDM-1070
  /**
   * This method returns SelectionAdapter instance
   * 
   * @param column instance
   * @param index defines column index
   * @param viewerSorter defines ViewerSorter
   * @param tabViewer GridTreeViewer instance
   * @return SelectionAdapter instance
   */
  public SelectionAdapter getSelectionAdapter(final TreeColumn column, final int index,
      final AbstractViewerSorter viewerSorter, final TreeViewer tabViewer) {
    final SelectionAdapter selectionAdapter = new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        viewerSorter.setColumn(index);
        final int direction = viewerSorter.getDirection();
        for (int i = 0; i < tabViewer.getTree().getColumnCount(); i++) {
          if (i == index) {
            if (direction == DIRECTION_DOWN) {
              column.getParent().setSortDirection(SWT.DOWN);
            }
            else if (direction == DIRECTION_UP) {
              column.getParent().setSortDirection(SWT.UP);
            }
          }
          if (i != index) {
            tabViewer.getTree().getColumn(i).getParent().setSortDirection(SWT.NONE);
          }
        }
        tabViewer.refresh();
      }
    };
    return selectionAdapter;
  }


  /**
   * This method provides the selection to, the item should be selected in the GridTableViewer
   * 
   * @param tableViewer instance
   * @param elementToSelect instance
   */
  public void setSelection(final GridTableViewer tableViewer, final Object elementToSelect) {
    final GridItem[] gridItems = tableViewer.getGrid().getItems();
    int index = 0;
    for (GridItem gridItem : gridItems) {
      final Object data = gridItem.getData();
      if (data.equals(elementToSelect)) {
        // Get the index of grid item
        index = tableViewer.getGrid().getIndexOfItem(gridItem);
        break;
      }
    }
    tableViewer.getGrid().setSelection(index);
    tableViewer.getGrid().showSelection();
  }
}

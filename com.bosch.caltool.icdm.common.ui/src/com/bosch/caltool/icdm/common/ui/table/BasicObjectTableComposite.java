/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.icdm.common.ui.sorters.BasicObjectViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.BasicObjectViewerTypeFilter;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;


/**
 * Table composite for Basic Objects. Shows name and description columns. Type filter and sorting is also available.
 *
 * @author rgo7cob
 * @param <D> instance of <code>IBasicObject</code>
 */
// TODO extend composite
public class BasicObjectTableComposite<D extends IBasicObject> {

  /**
   * Column width - Name column
   */
  private static final int COLWIDTH_QRE_NAME = 150;
  /**
   * Column width - Description column
   */
  private static final int COLWIDTH_QRE_DESC = 200;
  /**
   * form form
   */
  protected final Form form;
  /**
   * questabViewer questabViewer
   */
  protected GridTableViewer tabViewer;
  /**
   * search txt
   */
  protected Text typeFilterTxbBx;
  /**
   * sorter questabViewer
   */
  protected BasicObjectViewerSorter<D> tabSorter;
  /**
   * formToolkit formToolkit
   */
  protected final FormToolkit formToolkit;
  /**
   * filter
   */
  protected BasicObjectViewerTypeFilter<D> typeFilter;


  /**
   * New instance of this composite
   *
   * @param form form
   * @param formToolkit formToolkit
   */
  public BasicObjectTableComposite(final Form form, final FormToolkit formToolkit) {
    this.form = form;
    this.formToolkit = formToolkit;
  }

  /**
   * @return the table viewer
   */
  public GridTableViewer createTable() {
    createCustomSorter();
    if (this.tabSorter == null) {
      this.tabSorter = new BasicObjectViewerSorter<>();
    }
    // create type filter
    createTypeFilterTextBox();

    // create table
    this.tabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());

    // create columns
    createColumns();

    this.tabViewer.addFilter(this.typeFilter);

    // set comparator
    this.tabViewer.setComparator(this.tabSorter);

    return this.tabViewer;
  }

  /**
   * Define custom sorter
   */
  protected void createCustomSorter() {
    // Override the method to define custom sorter
  }

  /**
   * Create the table columns
   */
  private void createColumns() {
    // create the name column
    final GridViewerColumn nameColumn = new GridViewerColumn(this.tabViewer, SWT.NONE);
    nameColumn.getColumn().setText("Name");
    nameColumn.getColumn().setWidth(COLWIDTH_QRE_NAME);
    ColumnViewerToolTipSupport.enableFor(this.tabViewer, ToolTip.NO_RECREATE);
    // create name col
    nameColumn.setLabelProvider(getNameColLabelProvider());
    // add sel listener for name column
    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), 0, this.tabSorter, this.tabViewer));


    // create Description column
    final GridViewerColumn descColumn = new GridViewerColumn(this.tabViewer, SWT.NONE);
    descColumn.getColumn().setText("Description");
    descColumn.getColumn().setWidth(COLWIDTH_QRE_DESC);
    ColumnViewerToolTipSupport.enableFor(this.tabViewer, ToolTip.NO_RECREATE);
    descColumn.setLabelProvider(getDescColumnLabelProvider());
    // add selection listener for description column
    descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descColumn.getColumn(), 1, this.tabSorter, this.tabViewer));

    createAdditionalColumns();
  }

  /**
   * @return label provider for description column
   */
  protected ColumnLabelProvider getDescColumnLabelProvider() {
    return new ColumnLabelProvider() {

      /**
       * Get text for Description column
       */
      @Override
      public String getText(final Object element) {
        return (element instanceof IBasicObject) ? ((IBasicObject) element).getDescription() : "";
      }

    };
  }

  /**
   * @return label provider for name column
   */
  protected ColumnLabelProvider getNameColLabelProvider() {
    return new ColumnLabelProvider() {

      /**
       * Get text for name column
       */
      @Override
      public String getText(final Object element) {
        return (element instanceof IBasicObject) ? ((IBasicObject) element).getName() : "";
      }

    };
  }

  /**
   * Define additional columns
   */
  protected void createAdditionalColumns() {
    // Override the method to define additional columns
  }

  /**
   * Create type filter
   */
  private void createTypeFilterTextBox() {
    createCustomFilter();
    if (null == this.typeFilter) {
      // create filter
      this.typeFilter = new BasicObjectViewerTypeFilter<>();
    }

    // create search text box
    this.typeFilterTxbBx = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "");

    // text box modify listsner
    this.typeFilterTxbBx.addModifyListener(event -> {
      final String text = BasicObjectTableComposite.this.typeFilterTxbBx.getText().trim();
      BasicObjectTableComposite.this.typeFilter.setFilterText(text);
      BasicObjectTableComposite.this.tabViewer.refresh();
    });
  }

  /**
   * Define custom filter
   */
  protected void createCustomFilter() {
    // Override the method to define custom filter

  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.cdr.ui.sorters.QuestionareSorter;
import com.bosch.caltool.cdr.ui.table.filters.QuestionarieFilter;
import com.bosch.caltool.icdm.common.ui.table.BasicObjectTableComposite;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;


/**
 * Class for creating qusetionnaire table viewer
 *
 * @author bru2cob
 */
public class QuestionnaireTabViewer extends BasicObjectTableComposite<Questionnaire> {


  /**
   * Rule set table width
   */
  private static final int COLWIDTH_QRE_NAME = 150;


  /**
   * @param form form
   * @param formToolkit formToolkit
   */
  public QuestionnaireTabViewer(final Form form, final FormToolkit formToolkit) {
    super(form, formToolkit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createCustomSorter() {
    this.tabSorter = new QuestionareSorter();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createAdditionalColumns() {
    // create division column
    final GridViewerColumn divColumn = new GridViewerColumn(this.tabViewer, SWT.NONE);
    divColumn.getColumn().setText("Division");
    divColumn.getColumn().setWidth(COLWIDTH_QRE_NAME);
    ColumnViewerToolTipSupport.enableFor(this.tabViewer, ToolTip.NO_RECREATE);
    // set col label provider
    divColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * Get Division name
       */
      @Override
      public String getText(final Object element) {
        return element instanceof Questionnaire ? CommonUtils.checkNull(((Questionnaire) element).getDivName()) : "";
      }

    });
    // add selection listener for description column
    divColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(divColumn.getColumn(), 2, this.tabSorter, this.tabViewer));
  }

  /**
   * Set the type filter {@inheritDoc}
   */
  @Override
  protected void createCustomFilter() {
    this.typeFilter = new QuestionarieFilter();
  }

  /**
   * @return typeFilterTxbBx
   */
  public Text getFilterTxt() {
    return this.typeFilterTxbBx;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ColumnLabelProvider getNameColLabelProvider() {
    return new ColumnLabelProvider() {

      /**
       * Get text for name column
       */
      @Override
      public String getText(final Object element) {
        return (element instanceof Questionnaire) ? ((Questionnaire) element).getNameSimple() : "";
      }

    };
  }

}
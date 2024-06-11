/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.cdr.ui.views.providers.OutlineQnaireRespTreeViewContentProvider;
import com.bosch.caltool.cdr.ui.views.providers.OutlineQnaireRespTreeViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;


/**
 * This class is used to create the Outline Page for the questionnaire response
 *
 * @author svj7cob
 */
// iCDM-1991
public class QnaireRespOutlinePage extends AbstractPage {

  private final QnaireRespEditorDataHandler dataHandler;
  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * the composite
   */
  private Composite composite;
  /**
   * the viewer
   */
  private TreeViewer viewer;

  /**
   * Initialise the values
   *
   * @param dataHandler Qnaire Resp Editor Data Handler
   */
  public QnaireRespOutlinePage(final QnaireRespEditorDataHandler dataHandler) {
    super();
    this.dataHandler = dataHandler;
  }

  /**
   * This method creates the initial control part
   */
  @Override
  public void createControl(final Composite parent) {
    addHelpAction();
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    // Build the UI
    createComposite();

    getSite().setSelectionProvider(this.viewer);
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {

    GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);
    // Create filters
    PatternFilter filter = new PatternFilter();
    FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);

    this.viewer.setContentProvider(new OutlineQnaireRespTreeViewContentProvider(this.dataHandler, true));

    // Set Label provider for the tree
    this.viewer.setLabelProvider(new OutlineQnaireRespTreeViewLabelProvider(this.dataHandler));
    this.viewer.setInput("");

    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);
    // initially the treeviewer expands all
    this.viewer.expandAll();
  }

  /**
   * Applies styles to GridData
   *
   * @param gridData
   */
  private void applyGridDataStyles(final GridData gridData) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    // Create GridLayout
    GridLayout gridLayout = new GridLayout();
    this.composite = new Composite(this.top, SWT.NONE);
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);
  }


  /**
   * This method refresh the tree viewer
   */
  @Override
  public void refreshTreeViewer(final boolean deselectAll) {
    this.viewer.refresh();
    if (deselectAll) {
      this.viewer.getTree().deselectAll();
    }

  }

  /**
   * gets the control
   */
  @Override
  public Control getControl() {
    return this.top;
  }

  /**
   * sets the focus
   */
  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();
  }


  /**
   * gets the viewer
   *
   * @return the viewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }

  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }

}

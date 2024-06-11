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

import com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditorInput;
import com.bosch.caltool.cdr.ui.views.providers.OutlineQuestionnareTreeViewContentProvider;
import com.bosch.caltool.cdr.ui.views.providers.OutlineQuestionnareTreeViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;


/**
 * @author dmo5cob
 */
public class ReviewQuestionnaireOutlinePage extends AbstractPage {

  private final ReviewQuestionaireEditorInput editorInput;
  /**
   * Declare UI components and controls
   */
  private Composite top;
  private Composite composite;
  private TreeViewer viewer;

  /**
   * @param editorInput Review Questionaire Editor Input
   */
  public ReviewQuestionnaireOutlinePage(final ReviewQuestionaireEditorInput editorInput) {
    this.editorInput = editorInput;
  }

  @Override
  public void createControl(final Composite parent) {
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    addHelpAction();
    // Build the UI
    createComposite();


    // add selection provider
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
    // ICDM 542
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);

    // Set Content provider for the tree
    this.viewer.setContentProvider(new OutlineQuestionnareTreeViewContentProvider(this.editorInput));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new OutlineQuestionnareTreeViewLabelProvider(this.editorInput.getQnaireDefBo()));
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("");


    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);

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
   * {@inheritDoc}
   */
  @Override
  public void refreshTreeViewer(final boolean deselectAll) {
    this.viewer.refresh();
    if (deselectAll) {
      this.viewer.getTree().deselectAll();
    }

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Control getControl() {
    return this.top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();

  }


  /**
   * @return the viewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editorInput.getQuestionnaireEditorDataHandler();
  }

  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }
}

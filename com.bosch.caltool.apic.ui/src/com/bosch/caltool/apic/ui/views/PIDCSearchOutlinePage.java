/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

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

import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;


/**
 * ICDM-1135
 *
 * @author bru2cob
 */
public class PIDCSearchOutlinePage extends AbstractPage {

  /**
   * Tree expand level
   */
  private static final int EXPAND_LEVEL = 2;
  /**
   * Declare UI components and controls
   */
  private Composite top;
  private Composite composite;
  private TreeViewer viewer;
  private final OutLineViewDataHandler dataHandler;

  /**
   * @param dataHandler view data handler
   */
  public PIDCSearchOutlinePage(final OutLineViewDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }


  @Override
  public void createControl(final Composite parent) {
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    addHelpAction();
    // Build the UI
    createComposite();
    // add context menu
    PIDCSearchOutlinePgContextMenu contextMenu = new PIDCSearchOutlinePgContextMenu(this.viewer, this.dataHandler);
    contextMenu.hookContextMenu();

    // add selection provider
    getSite().setSelectionProvider(this.viewer);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  @Override
  public Control getControl() {
    return this.top;
  }

  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();
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
    // set auto expand level
    this.viewer.setAutoExpandLevel(EXPAND_LEVEL);
    // Icdm-195
    // Set Content provider for the tree
    this.viewer.setContentProvider(new OutlinePIDCTreeViewContentProvider(null, this.dataHandler));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new OutlinePIDCTreeViewLabelProvider());
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("ROOT"); // iCDM-296

    new CommonActionSet().addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);

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
    if (this.viewer != null) {
      this.viewer.refresh();
      if (deselectAll) {
        this.viewer.getTree().deselectAll();
      }
    }
  }
}

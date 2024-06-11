/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.views.providers.ReviewResultOutlineTreeContentProvider;
import com.bosch.caltool.cdr.ui.views.providers.ReviewResultOutlineTreeLabelProvider;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRResultFunction;
import com.bosch.caltool.icdm.model.cdr.RvwResultWPandRespModel;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;

/**
 * @author say8cob
 */
public class ReviewResultOutlinePage extends AbstractPage {


  /**
   * Declare UI components and controls
   */
  private Composite top;
  private Composite composite;
  private TreeViewer viewer;

  private final ReviewResultEditorInput editorInput;

  private ReviewResultOutlineTreeContentProvider reviewResultOutlineTreeContentProvider;

  /**
   * Constructor
   *
   * @param a2lEditor
   */
  public ReviewResultOutlinePage(final ReviewResultEditorInput a2lEditor) {
    this.editorInput = a2lEditor;
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

  @Override
  public Control getControl() {
    return this.top;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editorInput.getDataHandler();
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
    // ICDM 542
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);

    // set auto expand level //ICDM-265
    this.viewer.setAutoExpandLevel(1);

    // Set Content provider for the tree

    this.reviewResultOutlineTreeContentProvider = new ReviewResultOutlineTreeContentProvider(this.editorInput);
    this.viewer.setContentProvider(this.reviewResultOutlineTreeContentProvider);
    // Set Label provider for the tree

    this.viewer.setLabelProvider(new ReviewResultOutlineTreeLabelProvider(this.editorInput.getDataHandler()));
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("ROOT");

    addFncRightClickMenu();
    // expand the first level for A2L file node alone
    // ICDM-2272
    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);
  }

  /**
   * Method to set outline seletion based on review result selected in pidc tree node and based on review result
   * selected in pidc tree node and review result list page
   *
   * @param isPidcTreeNode
   */
  public void setReviewResultOutlinePageSelection(final boolean isPidcTreeNode) {

    getViewer().expandToLevel(4);
    getViewer().setSelection(new StructuredSelection(ApicConstants.WP_RESPONSIBILITY));

    if (isPidcTreeNode) {
      if ((this.editorInput.getParentA2lWorkpackage() != null) &&
          (this.editorInput.getParentA2lResponsible() != null)) {
        // Setting selection in outline view for Responsible
        Object[] wpBasedOnResp = this.reviewResultOutlineTreeContentProvider
            .getWPBasedOnResp(this.editorInput.getParentA2lResponsible().getName());
        setOutlineViewSelection(wpBasedOnResp);
      }
      else if (CommonUtils.isNotNull(this.editorInput.getParentA2lWorkpackage())) {
        // Setting selection in outline view for Workpackage
        setOutlineViewSelection(this.reviewResultOutlineTreeContentProvider.getA2lWorkPackagesNode());
      }
    }
  }

  /**
   * @param a2lWorkpackagesNode
   */
  private void setOutlineViewSelection(final Object[] a2lWorkpackagesNode) {
    for (Object object : a2lWorkpackagesNode) {
      RvwResultWPandRespModel resultWPandRespModel = (RvwResultWPandRespModel) object;
      if (resultWPandRespModel.getA2lWorkPackage().equals(this.editorInput.getParentA2lWorkpackage())) {
        // Setting selection in outline view for pidc tree node
        getViewer().setSelection(new StructuredSelection(resultWPandRespModel));
      }
    }
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
    if ((this.viewer != null) && !this.viewer.getControl().isDisposed()) {
      this.viewer.refresh();
      if (deselectAll) {
        this.viewer.getTree().deselectAll();
      }
    }
  }

  /**
   * CNS UI Refresh
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }


  /**
   * Right click context menu for functions tree
   */
  private void addFncRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    IMenuListener iMenuListener = mgr -> {
      final IStructuredSelection selection = (IStructuredSelection) ReviewResultOutlinePage.this.viewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (!selection.isEmpty()) && (firstElement instanceof CDRResultFunction)) {
        final ReviewActionSet reviewActionSet = new ReviewActionSet();
        reviewActionSet.addReviewParamEditor(menuMgr, firstElement, null,
            ReviewResultOutlinePage.this.editorInput.getResultData().getCDRFunction((CDRResultFunction) firstElement),
            true);
      }
    };
    menuMgr.addMenuListener(iMenuListener);

    final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);

  }

  /**
   * @return TreeViewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }

}

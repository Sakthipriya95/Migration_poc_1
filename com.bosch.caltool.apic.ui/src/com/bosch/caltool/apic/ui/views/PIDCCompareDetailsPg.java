/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.listeners.PIDCDetailsPageListener;
import com.bosch.caltool.apic.ui.views.providers.PIDCDetailsViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.PIDCDetailsViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.common.ui.actions.CollapseAllAction;
import com.bosch.caltool.icdm.common.ui.actions.ExpandAllAction;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author bru2cob
 */
// ICDM-1620
public class PIDCCompareDetailsPg extends AbstractPage implements PIDCDetailsPageListener {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * Main composite
   */
  private Composite composite;
  /**
   * TreeViewer instance
   */
  private TreeViewer viewer;

  /**
   * PIDC
   */
  private PidcVersion editorPidcVer;

  /**
   * @param editorPidcVer the editorPidcVer to set
   */
  public void setEditorPidcVer(final PidcVersion editorPidcVer) {
    this.editorPidcVer = editorPidcVer;
  }

  /**
   * View part
   */
  private final PIDCDetailsViewPart detailsview;

  private final AbstractProjectObjectBO projObjBO;

  /**
   * Boolean for variant compare
   */
  private final boolean isVarCompare;
  private int varIndex;
  private int varCount;
  private int subVarIndex;
  private PidcVariant[] pidVars;
  private PidcSubVariant[] pidSubVars;
  private final boolean isVerCompare;

  /**
   * @param projObjBO
   * @param pidcDetailsViewPart - piddetailsview instance
   * @param isVarCompare true if variants compare
   * @param isVerCompare
   */
  public PIDCCompareDetailsPg(final AbstractProjectObjectBO projObjBO, final PIDCDetailsViewPart pidcDetailsViewPart,
      final boolean isVarCompare, final boolean isVerCompare) {
    super();
    this.projObjBO = projObjBO;
    this.editorPidcVer = this.projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion();
    this.detailsview = pidcDetailsViewPart;
    this.isVarCompare = isVarCompare;
    this.isVerCompare = isVerCompare;
    PIDCActionSet.registerDetailsListener(this.editorPidcVer.getId(), this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    addHelpAction();
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    // Build the UI
    createComposite();
    // add selection provider
    getSite().setSelectionProvider(this.viewer);
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
   * This method initializes composite
   */
  private void createComposite() {

    final GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);
    // Create filters
    final PatternFilter filter = new PatternFilter();
    final FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER | SWT.MULTI, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();

    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(CommonUIConstants.DEF_TREE_COLLAPSE_LEVEL);

    ColumnViewerToolTipSupport.enableFor(this.viewer);
    // Set Content provider for the tree
    this.viewer.setContentProvider(new PIDCDetailsViewContentProvider(this.projObjBO));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new PIDCDetailsViewLabelProvider(this.projObjBO));
    // create root node in Content provider
    this.viewer.setInput(this.editorPidcVer.getId());

    final Separator separator = new Separator();
    IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
    toolBarManager.add(separator);
    IAction expandAllAction = new ExpandAllAction(this.viewer, CommonUIConstants.DEF_TREE_EXPAND_LEVEL);
    toolBarManager.add(expandAllAction);
    IAction collapseAllAction = new CollapseAllAction(this.viewer, CommonUIConstants.DEF_TREE_COLLAPSE_LEVEL);
    toolBarManager.add(collapseAllAction);

    final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

    hookContextMenu();
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
    final GridLayout gridLayout = new GridLayout();
    this.composite = new Composite(this.top, SWT.NONE);
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);
  }

  /**
   * Refresh the page, after applying menu filters
   *
   * @param includeDeleted true to refresh page including deleted variants ans subvariants
   */
  public void refreshPage() {
    // create contents
    this.viewer.setContentProvider(new PIDCDetailsViewContentProvider(this.projObjBO));
    this.viewer.setLabelProvider(new PIDCDetailsViewLabelProvider(this.projObjBO));
    this.viewer.setInput(this.editorPidcVer.getId());
    // refresh for virtual nodes
    new PidcDetailsNodeHandler((PidcVersionBO) this.projObjBO).refreshNodes();

    // refersh tree viewer
    this.viewer.refresh(this.editorPidcVer);
    this.viewer.refresh();
  }

  /**
   * Refresh virtual nodes in pidc details structure
   */
  public void refreshNodes() {
    new PidcDetailsNodeHandler((PidcVersionBO) this.projObjBO).refreshNodes();
    this.viewer.refresh();
  }

  /**
   * Build contect menu on tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void menuAboutToShow(final IMenuManager manager) {
        fillContextMenu(manager);

      }
    });
    final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(this.detailsview.getSite().getId(), menuMgr, this.viewer);
  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {
    // Get the current selection and add actions to it
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection == null) || (selection.getFirstElement() == null)) {
      return;
    }
    int selSize = selection.size();
    if ((selSize > 1) || checkVirtualNode(selection)) {
      addMultipleSelectOption(manager, selection, selSize);
    }
    else {
      addSingleSelcOption(manager, selection);
    }
  }

  /**
   * @param manager
   * @param selection
   */
  private void addSingleSelcOption(final IMenuManager manager, final IStructuredSelection selection) {
    if (this.isVarCompare && ((selection.getFirstElement() instanceof PidcVariant) ||
        ((selection.getFirstElement() instanceof PIDCDetailsNode) &&
            ((PIDCDetailsNode) selection.getFirstElement()).isVariantNode()))) {
      final PidcVariant pidVar = CommonUiUtils.getInstance().getPidcVariantObj(selection.getFirstElement());
      List<IProjectObject> pidcVers = new ArrayList<>();
      pidcVers.add(pidVar);
      final PIDCActionSet actionSet = new PIDCActionSet();
      actionSet.setAddToComparePIDCAction(manager, pidcVers);
    }
    else if (!this.isVarCompare && !this.isVerCompare && (selection.getFirstElement() instanceof PidcSubVariant)) {
      final PidcSubVariant pidSubVar = (PidcSubVariant) selection.getFirstElement();
      List<IProjectObject> pidcSubVers = new ArrayList<>();
      pidcSubVers.add(pidSubVar);
      final PIDCActionSet actionSet = new PIDCActionSet();
      actionSet.setAddToComparePIDCAction(manager, pidcSubVers);
    }
  }

  /**
   * For virtual node and multiple var/subvar set value option is added
   *
   * @param manager menu manager
   * @param selection sel
   * @param selSize size
   */
  private void addMultipleSelectOption(final IMenuManager manager, final IStructuredSelection selection,
      final int selSize) {
    this.varIndex = 0;
    this.varCount = 0;
    this.subVarIndex = 0;
    this.pidVars = new PidcVariant[selSize];
    this.pidSubVars = new PidcSubVariant[selSize];
    boolean allVirtualNode = initializeVarArraySize(selection);
    Iterator<?> iterator = selection.iterator();
    setSelectedElements(allVirtualNode, iterator);
    if ((this.varIndex == selSize) || ((this.varCount != 0) && (this.varIndex == this.varCount))) {
      addVarSetValOpt(manager, this.pidVars);
    }
    else if (this.subVarIndex == selSize) {
      addSubVarSetValOpt(manager, this.pidSubVars);
    }
  }

  /**
   * @param allVirtualNode
   * @param iterator
   */
  private void setSelectedElements(final boolean allVirtualNode, final Iterator<?> iterator) {
    while (iterator.hasNext()) {
      Object selElement = iterator.next();
      // If its virtual node
      if ((selElement instanceof PIDCDetailsNode) && !((PIDCDetailsNode) selElement).isVariantNode() &&
          allVirtualNode) {
        this.varIndex = getVirtualNodeVars(this.varIndex, this.pidVars, selElement);
      }
      else if ((selElement instanceof PidcVariant) || (selElement instanceof PIDCDetailsNode)) {
        this.varIndex = setSelVars(this.varIndex, this.pidVars, selElement);
      }
      else if (selElement instanceof PidcSubVariant) {
        this.subVarIndex = setSelSubVars(this.subVarIndex, this.pidSubVars, selElement);
      }
    }
  }

  /**
   * @param selection
   * @param allVirtualNode
   * @return
   */
  private boolean initializeVarArraySize(final IStructuredSelection selection) {
    boolean allVirtualNode = false;
    if ((selection.getFirstElement() instanceof PIDCDetailsNode) && checkVirtualNode(selection)) {
      allVirtualNode = true;
      Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
        this.varCount += getVarsCount(iterator);
      }
      this.pidVars = new PidcVariant[this.varCount];
    }
    return allVirtualNode;
  }

  /**
   * Adds the selected sub-varaints to the array
   *
   * @param subVarIndex array index
   * @param pidSubVars sub-vars
   * @param selElement selc element
   * @return array index
   */
  private int setSelSubVars(final int subVarIndex, final PidcSubVariant[] pidSubVars, final Object selElement) {
    int index = subVarIndex;
    PidcSubVariant pidSubVar = (PidcSubVariant) selElement;
    if (!pidSubVar.isDeleted()) {
      pidSubVars[index] = pidSubVar;
      index++;
    }
    return index;
  }

  /**
   * Adds set value option to sub-var
   *
   * @param manager manager
   * @param pidSubVars sub-vars
   */
  private void addSubVarSetValOpt(final IMenuManager manager, final PidcSubVariant[] pidSubVars) {
    boolean enableFlag = true;
    PidcVariant var = this.projObjBO.getPidcDataHandler().getVariantMap().get(pidSubVars[0].getPidcVariantId());
    for (PidcSubVariant subVar : pidSubVars) {
      if (var.compareTo(this.projObjBO.getPidcDataHandler().getVariantMap().get(subVar.getPidcVariantId())) != 0) {
        enableFlag = false;
      }
    }

    if (enableFlag) {
      setRightClickActionsForMultipleSubVariant(manager, pidSubVars);
    }
  }

  /**
   * right click action when multiple sub-variants selected
   *
   * @param manager : manager instance
   * @param pidSubVars : selected pid sub-variants
   */
  private void setRightClickActionsForMultipleSubVariant(final IMenuManager manager,
      final PidcSubVariant[] pidSubVars) {
    PIDCActionSet actionSet = new PIDCActionSet();
    List<IProjectObject> subVars = new ArrayList<>();
    for (IProjectObject subVar : pidSubVars) {
      subVars.add(subVar);
    }
    if (this.isVarCompare || this.isVerCompare) {
      actionSet.setCompareAction(manager, subVars, this.projObjBO);
    }
    else {
      actionSet.setAddToComparePIDCAction(manager, subVars);
    }

  }

  /**
   * Adds the selected varaints to the array
   *
   * @param varIndex array index
   * @param pidVars vars
   * @param selElement selc element
   * @return array index
   */
  private int setSelVars(final int varIndex, final PidcVariant[] pidVars, final Object selElement) {
    int index = varIndex;
    PidcVariant pidVar = CommonUiUtils.getInstance().getPidcVariantObj(selElement);
    if ((pidVar != null) && !pidVar.isDeleted()) {
      pidVars[index] = pidVar;
      index++;
    }
    return index;
  }

  /**
   * Returns the number of variants in the virtual node
   *
   * @param iterator iterator
   * @return varscount
   */
  private int getVarsCount(final Iterator<?> iterator) {
    int varCount = 0;
    Object selElement = iterator.next();
    PIDCDetailsNode virtualNode = (PIDCDetailsNode) selElement;
    varCount += virtualNode.getVirtualNodeVars().size();
    return varCount;
  }


  /**
   * Adds the set value option to variant
   *
   * @param manager manager
   * @param pidVars vars
   */
  private void addVarSetValOpt(final IMenuManager manager, final PidcVariant[] pidcVars) {
    setRightClickActionsForMultipleVariant(manager, pidcVars);
  }

  /**
   * @param varIndex index
   * @param pidVars varaints
   * @param selElement selc element
   * @return
   */
  private int getVirtualNodeVars(final int varIndex, final PidcVariant[] pidVars, final Object selElement) {
    int index = varIndex;
    PIDCDetailsNode virtualNode = (PIDCDetailsNode) selElement;
    Set<PidcVariant> nodes = virtualNode.getVirtualNodeVars();
    for (PidcVariant pidVar : nodes) {
      pidVars[index] = pidVar;
      index++;
    }
    return index;
  }

  /**
   * Checks whether all the selected nodes are virtual nodes
   *
   * @param selection selected nodes
   * @return true if all the selection are virtual nodes
   */
  private boolean checkVirtualNode(final IStructuredSelection selection) {
    Iterator<?> iterator = selection.iterator();
    boolean flag = true;
    while (iterator.hasNext()) {
      Object selElement = iterator.next();
      if ((selElement instanceof PidcVersion) || (selElement instanceof PidcVariant) ||
          (selElement instanceof PidcSubVariant)) {
        flag = false;
        break;
      }
      if (selElement instanceof PIDCDetailsNode) {
        PIDCDetailsNode virtualNode = (PIDCDetailsNode) selElement;
        if (virtualNode.isVariantNode()) {
          flag = false;
          break;
        }
      }

    }
    return flag;
  }

  /**
   * right click action when multiple variants selected
   *
   * @param manager : manager instance
   * @param pidVars : selected pid variants
   */
  private void setRightClickActionsForMultipleVariant(final IMenuManager manager, final PidcVariant[] pidVars) {
    PIDCActionSet actionSet = new PIDCActionSet();
    List<IProjectObject> pidcVars = new ArrayList<>();
    for (IProjectObject var : pidVars) {
      pidcVars.add(var);
    }
    if (this.isVarCompare) {
      actionSet.setAddToComparePIDCAction(manager, pidcVars);
    }
    else {
      actionSet.setCompareAction(manager, pidcVars, this.projObjBO);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshTreeViewer(final boolean deselectAll) {
    // TODO Auto-generated method stub

  }


}

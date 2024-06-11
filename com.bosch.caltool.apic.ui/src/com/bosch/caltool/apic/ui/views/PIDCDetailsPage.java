/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.AssignVarNamesAction;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCCopyActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCNameCopyAction;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.listeners.PIDCDetailsPageListener;
import com.bosch.caltool.apic.ui.sorter.PIDCDetailsTreeElementsComparer;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.views.providers.PIDCDetailsViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.PIDCDetailsViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.actions.CollapseAllAction;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.ExpandAllAction;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ICDMClipboard;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author adn1cob
 */
public class PIDCDetailsPage extends AbstractPage implements PIDCDetailsPageListener {

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
   * View part
   */
  private final PIDCDetailsViewPart detailsview;

  private PIDCAttrPage pidcPage;

  private final PidcEditorDataHandler dataHandler;

  private final CurrentUserBO currentUser = new CurrentUserBO();

  private PidcVariant selectedPidcVariant;

  private PidcSubVariant selectedPidcSubVariant;
  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();


  /**
   * @param dataHandler - data handler for selected pidc
   * @param pidcDetailsViewPart - piddetailsview instance
   */
  public PIDCDetailsPage(final PidcEditorDataHandler dataHandler, final PIDCDetailsViewPart pidcDetailsViewPart) {
    super();
    this.detailsview = pidcDetailsViewPart;
    this.dataHandler = dataHandler;
    // need to change this
    PidcVersion editorPidcVer = dataHandler.getPidcVersion();
    PIDCActionSet.registerDetailsListener(editorPidcVer.getId(), this);
  }


  private PidcVersionBO getPidcVersionBO() {
    return this.dataHandler.getPidcVersionBO();
  }

  private PidcVersion getPidcVersion() {
    return this.dataHandler.getPidcVersion();
  }

  /**
   * {@inheritDoc}
   */
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

    // set the comparator
    this.viewer.setComparer(new PIDCDetailsTreeElementsComparer());

    ColumnViewerToolTipSupport.enableFor(this.viewer);
    // Set Content provider for the tree
    this.viewer.setContentProvider(new PIDCDetailsViewContentProvider(getPidcVersionBO()));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new PIDCDetailsViewLabelProvider(getPidcVersionBO()));
    // create root node in Content provider
    // iCDM-208, include project revision
    this.viewer.setInput(getPidcVersion().getId());
    // ICDM-1166
    final Separator separator = new Separator();
    IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
    toolBarManager.add(separator);
    IAction expandAllAction = new ExpandAllAction(this.viewer, CommonUIConstants.DEF_TREE_EXPAND_LEVEL);
    toolBarManager.add(expandAllAction);
    IAction collapseAllAction = new CollapseAllAction(this.viewer, CommonUIConstants.DEF_TREE_COLLAPSE_LEVEL);
    toolBarManager.add(collapseAllAction);

    final Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

    // ICDM-796
    this.viewer.addSelectionChangedListener(event -> {
      // ICDM-865
      PIDCDetailsPage.this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof PidcVersion) {
        this.selectedPidcVariant = null;
        this.selectedPidcSubVariant = null;
        PidcVersion selectedPidcVer = (PidcVersion) selected;
        PIDCDetailsPage.this.expReportService.setExportService(!selectedPidcVer.isDeleted());
      }
      else if (selected instanceof PidcVariant) {
        this.selectedPidcSubVariant = null;
        this.selectedPidcVariant = (PidcVariant) selected;
        PIDCDetailsPage.this.expReportService.setExportService(false);
      }
      else if (selected instanceof PidcSubVariant) {
        this.selectedPidcVariant = null;
        this.selectedPidcSubVariant = (PidcSubVariant) selected;
        PIDCDetailsPage.this.expReportService.setExportService(false);
      }
      else if (selected instanceof PIDCDetailsNode) {
        this.selectedPidcSubVariant = null;
        this.selectedPidcVariant = ((PIDCDetailsNode) selected).getPidcVariant();
        PIDCDetailsPage.this.expReportService.setExportService(false);
      }
      else {
        PIDCDetailsPage.this.expReportService.setExportService(false);
      }
    });
    // ICDM-121
    addKeyListenerToViewer();

    hookContextMenu();


    // set selection to variant/subvariant when selected object is not null
    // selected object refers to var/subvar which has undefined mand/uc attrs
    // used when loading the pidc editor
    if (this.pidcPage.isFilterUndefinedAttr() &&
        (this.dataHandler.getPidcVersionBO().getSelectedObjForFiltering() != null)) {
      if (this.dataHandler.getPidcVersionBO().getSelectedObjForFiltering() instanceof PidcVariant) {
        this.selectedPidcVariant = (PidcVariant) this.dataHandler.getPidcVersionBO().getSelectedObjForFiltering();
      }
      else if (this.dataHandler.getPidcVersionBO().getSelectedObjForFiltering() instanceof PidcSubVariant) {
        this.selectedPidcSubVariant = (PidcSubVariant) this.dataHandler.getPidcVersionBO().getSelectedObjForFiltering();
      }
      refreshUI(null);
      this.pidcPage.pidcDetNodeSelection(this.dataHandler.getPidcVersionBO().getSelectedObjForFiltering());
    }


  }


  /**
   * This method adds key listner on pidc details page treeviewer. It checks for 'F2' key is pressed on
   * PIDC/PIDCVariant/PIDCSubVariant Node
   */
  // ICDM-121
  private void addKeyListenerToViewer() {
    this.viewer.getTree().addKeyListener(new KeyAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void keyPressed(final KeyEvent event) {
        // ICDM-705
        if (PIDCDetailsPage.this.viewer.getTree().getSelectionCount() == CommonUIConstants.SINGLE_SELECTION) {
          // key bindings doesnot work during multi select
          final IStructuredSelection selection = (IStructuredSelection) PIDCDetailsPage.this.viewer.getSelection();
          final PIDCActionSet actionset = new PIDCActionSet();
          final PIDCCopyActionSet copyActionSet = new PIDCCopyActionSet();
          Object selectedObject;
          // Get the user info

          try {
            NodeAccess nodeAccess = PIDCDetailsPage.this.currentUser.getNodeAccessRight(getPidcVersion().getPidcId());
            if ((selection != null) && ((selectedObject = selection.getFirstElement()) != null)) {
              renameDeleteDetailsNode(event, selectedObject, actionset, nodeAccess);
              copyPasteDetailsNode(event, copyActionSet, selectedObject);
            }
          }
          catch (ApicWebServiceException ex) {
            CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
          }
        }
      }
    });
  }


  /**
   * ICDM-705
   *
   * @param event
   * @param selectedObject
   * @param actionset
   * @param currentUserRight
   */
  private void renameDeleteDetailsNode(final KeyEvent event, final Object selectedObject, final PIDCActionSet actionset,
      final NodeAccess currentUserRight) {


    if ((currentUserRight != null) && currentUserRight.isOwner() && getPidcVersionBO().isModifiable()) {
      if (event.keyCode == CommonUIConstants.KEY_RENAME) {
        renameDetailsNode(selectedObject, actionset);
      }
      else if (event.keyCode == CommonUIConstants.KEY_DELETE) {
        deleteDetailsNode(selectedObject, actionset);
      }
    }
    else {
      if ((event.keyCode == CommonUIConstants.KEY_RENAME) || (event.keyCode == CommonUIConstants.KEY_DELETE)) {
        // if the user does not have access to delete or rename
        CDMLogger.getInstance().warnDialog("Insufficient privileges to do this operation!", Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * ICDM-705
   *
   * @param selection IStructuredSelection
   * @param actionset PIDCActionSet
   */
  private void deleteDetailsNode(final Object selectedObject, final PIDCActionSet actionset) {
    PIDCActionSet actionSetNew = new PIDCActionSet();
    if ((selectedObject instanceof PidcVersion) && !getPidcVersion().isDeleted()) {
      actionSetNew.deleteAction(getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc(),
          ApicUiConstants.DELETE_ACTION);
    }
    else if ((selectedObject instanceof PidcVariant) ||
        ((selectedObject instanceof PIDCDetailsNode) && ((PIDCDetailsNode) selectedObject).isVariantNode())) {
      final PidcVariant pidVar = CommonUiUtils.getInstance().getPidcVariantObject(selectedObject);
      if (!getPidcVersion().isDeleted() && !pidVar.isDeleted()) {
        actionset.deleteVarAction(pidVar, ApicUiConstants.DELETE_ACTION, getPidcVersion());
      }
    }
    else if (selectedObject instanceof PidcSubVariant) {
      final PidcSubVariant pidSubVar = (PidcSubVariant) selectedObject;
      PidcVariant pidcVariant =
          getPidcVersionBO().getPidcDataHandler().getVariantMap().get(pidSubVar.getPidcVariantId());
      if (!getPidcVersion().isDeleted() && !pidcVariant.isDeleted() && !pidSubVar.isDeleted()) {
        actionset.deleteSubVarAction(pidSubVar, ApicUiConstants.DELETE_ACTION);
      }
    }
  }

  /**
   * ICDM-705
   *
   * @param selection IStructuredSelection
   * @param actionset PIDCActionSet
   */
  private void renameDetailsNode(final Object selectedObject, final PIDCActionSet actionset) {
    PIDCActionSet pidcActionSetNew = new PIDCActionSet();
    try {
      if ((selectedObject instanceof PidcVersion) && !getPidcVersion().isDeleted()) {
        final PidcVersion pidcVer = (PidcVersion) selectedObject;
        pidcActionSetNew.renamePIDC(pidcVer, getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc());
      }
      else if ((selectedObject instanceof PidcVariant) ||
          ((selectedObject instanceof PIDCDetailsNode) && ((PIDCDetailsNode) selectedObject).isVariantNode())) {
        final PidcVariant pidVar = CommonUiUtils.getInstance().getPidcVariantObject(selectedObject);
        if (!pidVar.isDeleted() && !getPidcVersion().isDeleted()) {
          actionset.renameVariant(pidVar, getPidcVersionBO());
        }
      }
      // ICDM-121
      else if (selectedObject instanceof PidcSubVariant) {
        final PidcSubVariant pidSubVar = (PidcSubVariant) selectedObject;
        PidcVariant pidcVariant =
            getPidcVersionBO().getPidcDataHandler().getVariantMap().get(pidSubVar.getPidcVariantId());
        if (!pidSubVar.isDeleted() && !pidcVariant.isDeleted() && !getPidcVersion().isDeleted()) {
          actionset.renameSubVariant(pidSubVar, getPidcVersionBO());
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
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
    final GridLayout gridLayout = new GridLayout();
    this.composite = new Composite(this.top, SWT.NONE);
    this.composite.setLayoutData(gridData);
    this.composite.setLayout(gridLayout);
  }

  /**
   * Build contect menu on tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(PIDCDetailsPage.this::fillContextMenu);
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
    // ICDM-270
    if ((selSize > 1) || checkVirtualNode(selection)) {
      addMultipleSelectOption(manager, selection, selSize);
    }
    else {
      if (selection.getFirstElement() instanceof PidcVersion) {
        final PidcVersion pidcVer = (PidcVersion) selection.getFirstElement();
        // Set Right click for the selected item
        setRightClickActionsForPIDC(manager, pidcVer);
      }
      else if ((selection.getFirstElement() instanceof PidcVariant) ||
          ((selection.getFirstElement() instanceof PIDCDetailsNode) &&
              ((PIDCDetailsNode) selection.getFirstElement()).isVariantNode())) {
        final PidcVariant pidVar = CommonUiUtils.getInstance().getPidcVariantObject(selection.getFirstElement());
        setRightClickActionsForVariant(manager, pidVar, selection);
      }
      // ICDM-121
      else if (selection.getFirstElement() instanceof PidcSubVariant) {
        final PidcSubVariant pidSubVar = (PidcSubVariant) selection.getFirstElement();
        setRightClickActionsForSubVariant(manager, pidSubVar);
      }
      // ICDM 395
      else if (selection.getFirstElement() instanceof PIDCDetailsNode) {
        // For value type, undefined type details nodes
        final PIDCDetailsNode pidcNode = (PIDCDetailsNode) selection.getFirstElement();
        // Set Right click for the selected item
        setRightClickActionsForValPDetNode(manager, pidcNode);

      }
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
    int varIndex = 0;
    int varCount = 0;
    int subVarIndex = 0;
    PidcVariant[] pidVars = new PidcVariant[selSize];
    PidcSubVariant[] pidSubVars = new PidcSubVariant[selSize];
    // ICDM-912
    boolean allVirtualNode = false;
    if ((selection.getFirstElement() instanceof PIDCDetailsNode) && checkVirtualNode(selection)) {
      allVirtualNode = true;
      Iterator<?> iterator = selection.iterator();
      while (iterator.hasNext()) {
        varCount += getVarsCount(iterator);
      }
      pidVars = new PidcVariant[varCount];
    }
    Iterator<?> iterator = selection.iterator();
    while (iterator.hasNext()) {
      Object selElement = iterator.next();
      // ICDM-912
      // If its virtual node

      if ((selElement instanceof PIDCDetailsNode) && !((PIDCDetailsNode) selElement).isVariantNode() &&
          allVirtualNode) {
        varIndex = getVirtualNodeVars(varIndex, pidVars, selElement);
      }
      else if ((selElement instanceof PidcVariant) || (selElement instanceof PIDCDetailsNode)) {
        varIndex = setSelVars(varIndex, pidVars, selElement);
      }
      else if (selElement instanceof PidcSubVariant) {
        subVarIndex = setSelSubVars(subVarIndex, pidSubVars, selElement);
      }

    }
    if ((varIndex == selSize) || ((varCount != 0) && (varIndex == varCount))) {
      addVarSetValOpt(manager, selection, selSize, pidVars);

    }
    else if (subVarIndex == selSize) {
      addSubVarSetValOpt(manager, pidSubVars);
    }
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
   * Adds set value option to sub-var
   *
   * @param manager manager
   * @param pidSubVars sub-vars
   */
  private void addSubVarSetValOpt(final IMenuManager manager, final PidcSubVariant[] pidSubVars) {
    boolean enableFlag = true;
    PidcVariant var = getPidcVersionBO().getPidcDataHandler().getVariantMap().get(pidSubVars[0].getPidcVariantId());
    for (PidcSubVariant subVar : pidSubVars) {
      if (var.compareTo(getPidcVersionBO().getPidcDataHandler().getVariantMap().get(subVar.getPidcVariantId())) != 0) {
        enableFlag = false;
      }
    }

    if (enableFlag) {
      setRightClickActionsForMultipleSubVariant(manager, pidSubVars);
    }
  }

  /**
   * Adds the set value option to variant
   *
   * @param manager manager
   * @param selection selc
   * @param selSize size
   * @param pidVars vars
   */
  private void addVarSetValOpt(final IMenuManager manager, final IStructuredSelection selection, final int selSize,
      final PidcVariant[] pidVars) {
    setRightClickActionsForMultipleVariant(manager, pidVars);
    manager.add(new Separator());
    if ((selSize == 1) && (selection.getFirstElement() instanceof PIDCDetailsNode)) {
      // For value type, undefined type details nodes
      final PIDCDetailsNode pidcNode = (PIDCDetailsNode) selection.getFirstElement();
      // Set Right click for the selected item
      setRightClickActionsForValPDetNode(manager, pidcNode);
    }
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

  // ICDM-912
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
    actionSet.addValue(manager, pidVars, null, getPidcVersion(), this.pidcPage);
    List<IProjectObject> pidcVars = new ArrayList<>();
    for (IProjectObject var : pidVars) {
      pidcVars.add(var);
    }

    // set compare action
    actionSet.setCompareAction(manager, pidcVars, getPidcVersionBO());

    // set webflow action
    if (isMulVarWebFlowActive()) {
      PIDCActionSet actionSet1 = new PIDCActionSet();
      actionSet1.addWebFlowJobForMulVar(manager, pidVars, this.pidcPage.getPidcVersionBO());
    }
  }

  /**
   * @return
   */
  private boolean isMulVarWebFlowActive() {
    CommonDataBO dataBo = new CommonDataBO();
    try {
      return dataBo.getParameterValue(CommonParamKey.WEB_FLOW_JOB_MUL_VAR_ACTIVE)
          .equalsIgnoreCase(ApicConstants.CODE_YES);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return false;
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
    actionSet.addValue(manager, null, pidSubVars, getPidcVersion(), this.pidcPage);
    List<IProjectObject> subVars = new ArrayList<>();
    for (IProjectObject subVar : pidSubVars) {
      subVars.add(subVar);
    }
    actionSet.setCompareAction(manager, subVars, getPidcVersionBO());
  }

  /**
   * Set right click actions for the selected item
   *
   * @param manager
   * @param pidcVersion
   */
  private void setRightClickActionsForPIDC(final IMenuManager manager, final PidcVersion pidcVersion) {

    final PIDCActionSet actionSet = new PIDCActionSet();
    final CommonActionSet cmnActionSet = new CommonActionSet();

    final PIDCActionSet newActionSet = new PIDCActionSet();

    // ICDM-395
    /* Add Variant Action */
    actionSet.setCreateVariantAction(manager, pidcVersion, "Add Variant", this.viewer, null, getPidcVersionBO());
    /* Add a separator */
    manager.add(new Separator());

    IAction assignAction = new AssignVarNamesAction(this.pidcPage.getPidcVersionBO(), this.viewer);
    manager.add(assignAction);

    // Copy PIDC name to clipboard
    PIDCNameCopyAction copyNameAction = new PIDCNameCopyAction(pidcVersion.getName(), "Copy PIDC name to Clipboard");
    manager.add(copyNameAction);
    /* Add a separator */
    manager.add(new Separator());

    CommonDataBO commonDataBo = new CommonDataBO();
    String isWebFlowJobActive = "";
    try {
      isWebFlowJobActive = commonDataBo.getParameterValue(CommonParamKey.WEB_FLOW_JOB_CREATION_ACTIVE);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    if (!pidcVersion.isDeleted() && (getPidcVersionBO().getVariantsSet().isEmpty() || isAllVariantsDeleted()) &&
        (ApicConstants.CODE_YES.equalsIgnoreCase(isWebFlowJobActive))) {

      /* Add a separator */
      manager.add(new Separator());
      /* Start webflow job Action */
      actionSet.setPidcWebflowJobAction(manager, pidcVersion, ApicUiConstants.START_WEB_FLOW_JOB);
    }

    /* Add a separator */
    manager.add(new Separator());
    // ICDM-197 Reorganize the menus

    /* Add to Favorite action */
    newActionSet.setAddFavoritesAction(manager, pidcVersion);

    /* Remove Pidc from Favorite action */
    newActionSet.setRemoveFavoritesAction(manager, pidcVersion);

    /* Add a separator */
    manager.add(new Separator());
    // Icdm-1101 - Copy link of PIDC to Clip Board
    actionSet.copytoClipBoard(manager, pidcVersion);
    // ICDM-1232 - Send link of PIDC as email
    actionSet.sendPidcVersionLinkInOutlook(manager, pidcVersion);

    /* Add a separator */
    manager.add(new Separator());

    // ICDM-100
    // Export action
    if (!pidcVersion.isDeleted()) {
      actionSet.setExportAction(manager, pidcVersion, ApicUiConstants.EXPORT_ACTION);
      actionSet.setImportPIDCAction(manager, pidcVersion, "Import Excel", this.viewer);
    }

    // transfer to vCDM action
    newActionSet.setTransfer2vCDMAction(manager, getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo());
    /* Add a separator */
    manager.add(new Separator());
    /* Add to scratchpad action */
    cmnActionSet.setAddToScrachPadAction(manager, pidcVersion);

    // ICDM-157
    /* Add Copy action */
    final PIDCCopyActionSet copyActionSet = new PIDCCopyActionSet();
    copyActionSet.setCopyAction(manager, pidcVersion, ApicUiConstants.COPY, getPidcVersionBO());
    // ICDM-157
    /* Add Paste action */
    copyActionSet.setPasteAction(manager, pidcVersion, ApicUiConstants.PASTE, this.viewer, getPidcVersionBO());
    /* Add a separator */
    manager.add(new Separator());

    /* Rename action */
    newActionSet.addRenamePidcAction(manager, pidcVersion,
        getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc());

    if (getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc().isDeleted()) {
      /* UnDelete Action */
      newActionSet.setDeleteAction(manager, pidcVersion,
          getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc(),
          ApicUiConstants.UN_DELETE_PIDC_ACTION);
    }
    else {
      /* Delete Action */
      newActionSet.setDeleteAction(manager, pidcVersion,
          getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc(), ApicUiConstants.DELETE_PIDC_ACTION);
    }

    /* Add a separator */
    manager.add(new Separator());

    /* Add Structure attribute */
    actionSet.addNewStructureAttr(manager, 0, getPidcVersionBO());

  }

  /*
   * Set right click actions for value type PIDC Details Node
   * @param manager IMenuManager
   */
  private void setRightClickActionsForValPDetNode(final IMenuManager manager, final PIDCDetailsNode pidcNode) {

    final PidcVersion pidcVer = pidcNode.getPidcVersion();
    final PIDCActionSet actionSet = new PIDCActionSet();

    if (pidcNode.canAddVariants(getPidcVersionBO())) {
      actionSet.setCreateVariantAction(manager, pidcVer, "Add Variant", this.viewer, pidcNode, getPidcVersionBO());
      // Add a separator
      manager.add(new Separator());
    }

    // Add 'Create Node', 'Delete Node' actions
    actionSet.addNewStructureAttr(manager, pidcNode.getLevel(), getPidcVersionBO());
    actionSet.deleteStructureAttr(manager, pidcNode, getPidcVersionBO());
  }

  /**
   * Set right click actions for the selected item
   *
   * @param manager
   * @param selection
   * @param pidcVersion
   */
  private void setRightClickActionsForVariant(final IMenuManager manager, final PidcVariant pidVar,
      final IStructuredSelection selection) {

    final PIDCActionSet actionSet = new PIDCActionSet();

    final CommonActionSet cmnActionSet = new CommonActionSet();

    final PIDCCopyActionSet copyActionSet = new PIDCCopyActionSet();

    // ICDM-121
    /* Add Sub-Variant action */
    actionSet.setCreateSubVarAction(manager, pidVar, ApicUiConstants.ADD_SUB_VARIANT, this.viewer, getPidcVersionBO(),
        getPidcVersion(), selection);


    CommonDataBO commonDataBo = new CommonDataBO();
    String isWebFlowJobActive = "";
    try {
      isWebFlowJobActive = commonDataBo.getParameterValue(CommonParamKey.WEB_FLOW_JOB_CREATION_ACTIVE);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    if (!pidVar.isDeleted() && (ApicConstants.CODE_YES.equalsIgnoreCase(isWebFlowJobActive))) {
      /* Add a separator */
      manager.add(new Separator());
      /* Start webflow job Action */
      actionSet.setVarWebflowJobAction(manager, pidVar, getPidcVersion(), ApicUiConstants.START_WEB_FLOW_JOB);
    }

    /* Add a separator */
    manager.add(new Separator());

    /* Add to scratchpad action */
    cmnActionSet.setAddToScrachPadAction(manager, pidVar);

    // ICDM-150
    // Add copy variant action
    copyActionSet.setCopyAction(manager, pidVar, ApicUiConstants.COPY, getPidcVersionBO());

    // ICDM-157
    /* Paste Variant action */
    copyActionSet.setPasteAction(manager, pidVar, ApicUiConstants.PASTE, this.viewer, getPidcVersionBO());

    /* Add a separator */
    manager.add(new Separator());

    // Copy variant name to clipboard
    PIDCNameCopyAction copyNameAction = new PIDCNameCopyAction(pidVar.getName(), "Copy Variant name to Clipboard");
    manager.add(copyNameAction);

    /* Add a separator */
    manager.add(new Separator());

    /* Rename action */
    actionSet.setRenameVarAction(manager, pidVar, ApicUiConstants.RENAME_ACTION, getPidcVersionBO());

    if (pidVar.isDeleted()) {
      // UnDelete Action
      actionSet.setDeleteVarAction(manager, pidVar, ApicUiConstants.UN_DELETE_ACTION, getPidcVersionBO());
    }
    else { /* Delete Action */
      actionSet.setDeleteVarAction(manager, pidVar, ApicUiConstants.DELETE_ACTION, getPidcVersionBO());
    }

    /* Add a separator */
    manager.add(new Separator());

    // Copy link of PIDC variant to Clip Board
    actionSet.copyVarLinktoClipBoard(manager, pidVar);
    actionSet.sendPidcVariantLinkInOutlook(manager, pidVar);
  }

  /**
   * Set right click actions for the selected item
   *
   * @param manager
   * @param pidSubVar
   */
  private void setRightClickActionsForSubVariant(final IMenuManager manager, final PidcSubVariant pidSubVar) {

    final CommonActionSet cmnActionSet = new CommonActionSet();

    final PIDCCopyActionSet copyActionSet = new PIDCCopyActionSet();

    final PIDCActionSet actionSet = new PIDCActionSet();

    /* Add to Scratchpad action */
    cmnActionSet.setAddToScrachPadAction(manager, pidSubVar);

    // ICDM-150
    // Add copy variant action
    copyActionSet.setCopyAction(manager, pidSubVar, ApicUiConstants.COPY, getPidcVersionBO());

    // ICDM-121
    /* Paste Sub-Variant action */
    copyActionSet.setPasteAction(manager, pidSubVar, ApicUiConstants.PASTE, this.viewer, getPidcVersionBO());

    /* Add a separator */
    manager.add(new Separator());

    /* Rename sub-variant action */
    actionSet.setRenameSubVarAction(manager, pidSubVar, ApicUiConstants.RENAME_ACTION, getPidcVersionBO());

    if (pidSubVar.isDeleted()) {
      // UnDelete Action
      actionSet.setDeleteSubVarAction(manager, pidSubVar, ApicUiConstants.UN_DELETE_ACTION, getPidcVersionBO());
    }
    else { /* Delete Action */
      actionSet.setDeleteSubVarAction(manager, pidSubVar, ApicUiConstants.DELETE_ACTION, getPidcVersionBO());
    }

    manager.add(new Separator());
    actionSet.copySubVarLinktoClipBoard(manager, pidSubVar);
    actionSet.sendPidcSubVariantLinkInOutlook(manager, pidSubVar);
  }


  /**
   * @return the editorPIDCard version
   */
  public PidcVersion getEditorPidcVersion() {
    return getPidcVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshTreeViewer(final boolean deselectAll) {
    if (PIDCDetailsPage.this.viewer != null) {
      PIDCDetailsPage.this.viewer.refresh();
      if (deselectAll) {
        PIDCDetailsPage.this.viewer.getTree().deselectAll();
      }
    }

  }

  /**
   * Refresh the PID Details tree for the new REVISION of the pidc
   *
   * @param pidcId projectID card
   * @param revision pro_rev_id
   */
  public void refreshTreeViewer(final Long pidcId, final Long revision) {
    // get latest filter preference to refresh the tree
    if (PIDCDetailsPage.this.viewer != null) {
      PIDCDetailsPage.this.viewer.setInput(pidcId + ":" + revision);
      PIDCDetailsPage.this.viewer.refresh();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    PIDCActionSet.removeDetailsListener(getPidcVersion().getId(), this);
    super.dispose();
  }

  /**
   * ICDM-705
   *
   * @param event KeyEvent
   * @param copyActionSet PIDCCopyActionSet
   * @param selectedObject Selected object
   * @throws ApicWebServiceException
   */
  private void copyPasteDetailsNode(final KeyEvent event, final PIDCCopyActionSet copyActionSet,
      final Object selectedObject)
      throws ApicWebServiceException {
    if (event.stateMask == CommonUIConstants.KEY_CTRL) {
      if (event.keyCode == CommonUIConstants.KEY_COPY) {
        ICDMClipboard.getInstance().setCopiedObject(selectedObject);
      }
      else if (event.keyCode == CommonUIConstants.KEY_PASTE) {
        // Get the user info
        final CurrentUserBO currUser = new CurrentUserBO();
        Object copiedObject = ICDMClipboard.getInstance().getCopiedObject();
        PidcVersionBO pidcVersionBO = getPidcVersionBO();
        if ((selectedObject instanceof PidcVersion) && currUser.canCreatePIDC() &&
            copyActionSet.isPasteAllowed(copiedObject, selectedObject, pidcVersionBO)) {
          copyActionSet.pasteAction(selectedObject, PIDCDetailsPage.this.viewer, pidcVersionBO);
        }
        else if ((selectedObject instanceof PIDCDetailsNode) && (copiedObject instanceof PIDCDetailsNode) &&
            copyActionSet.isPasteAllowed(((PIDCDetailsNode) copiedObject).getPidcVariant(),
                ((PIDCDetailsNode) selectedObject).getPidcVariant(), pidcVersionBO)) {
          copyActionSet.pasteAction(((PIDCDetailsNode) selectedObject).getPidcVariant(), PIDCDetailsPage.this.viewer,
              pidcVersionBO);
        }
        else if (copyActionSet.isPasteAllowed(copiedObject, selectedObject, pidcVersionBO)) {
          copyActionSet.pasteAction(selectedObject, PIDCDetailsPage.this.viewer, pidcVersionBO);
        }

        else {
          // if the user does not have access to paste
          CDMLogger.getInstance().warnDialog("Paste Not Allowed!", Activator.PLUGIN_ID);
        }
      }
    }

  }

  // iCDM-911
  /**
   * Refresh the page, after applying menu filters
   */
  public void refreshPage() {
    // create contents
    this.viewer.setContentProvider(new PIDCDetailsViewContentProvider(getPidcVersionBO()));
    this.viewer.setLabelProvider(new PIDCDetailsViewLabelProvider(getPidcVersionBO()));
    this.viewer.setInput(getPidcVersion().getId());
    // refersh tree viewer
    this.viewer.refresh(getPidcVersion());
    this.viewer.refresh();
  }

  // iCDM-911
  /**
   * Refresh virtual nodes in pidc details structure
   */
  public void refreshNodes() {
    this.viewer.refresh();
  }

  // ICDM-2408
  /**
   * @param pidcVersion
   * @return
   */
  private boolean isAllVariantsDeleted() {

    for (PidcVariant pidcVar : getPidcVersionBO().getVariantsSet()) {
      if (!pidcVar.isDeleted()) {
        return false;
      }
    }
    return true;
  }


  /**
   * @return the viewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }


  /**
   * @param viewer the viewer to set
   */
  public void setViewer(final TreeViewer viewer) {
    this.viewer = viewer;
  }

  /**
   * @return the pidcPage
   */
  public PIDCAttrPage getPidcPage() {
    return this.pidcPage;
  }


  /**
   * @param pidcPage the pidcPage to set
   */
  public void setPidcPage(final PIDCAttrPage pidcPage) {
    this.pidcPage = pidcPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    // get all expanded elements
    Object[] elements = this.viewer.getExpandedElements();
    TreePath[] treePaths = this.viewer.getExpandedTreePaths();

    // refresh for virtual nodes
    getPidcVersionBO().refreshNodes(PIDCDetailsViewPart.isDeletedNodesDisplayEnabled());
    // refersh tree viewer
    this.viewer.refresh();

    // set expanded elements and treepaths
    for (Object element : elements) {
      this.viewer.setExpandedState(element, true);
    }
    for (TreePath treePath : treePaths) {
      this.viewer.setExpandedState(treePath, true);
    }
    // set selection
    setPidcVariantSelection();
    setPidcSubVariantSelection();
  }


  /**
   * function to set pidc sub variant selection after tree refresh
   */
  private void setPidcSubVariantSelection() {
    if (null != this.selectedPidcSubVariant) {
      if (PIDCDetailsViewPart.isVirtualStructureDisplayEnabled() &&
          (null != getPidcVersionBO().getPidcDetailsNodeHandler().getRootVirtualNodes())) {
        for (PIDCDetailsNode pidcDetailsNode : getPidcVersionBO().getPidcDetailsNodeHandler().getRootVirtualNodes()) {
          for (PIDCDetailsNode childDetailsNode : pidcDetailsNode.getVisibleChildNodes()) {
            for (PidcSubVariant pidcSubVariant : childDetailsNode.getSubVariants()) {
              if ((null != pidcSubVariant) && pidcSubVariant.getId().equals(this.selectedPidcSubVariant.getId())) {
                this.viewer.expandToLevel(pidcDetailsNode, 2);
                this.viewer.expandToLevel(childDetailsNode, 2);
                this.viewer.setSelection(new StructuredSelection(pidcSubVariant));
              }
            }
          }
        }
      }
      else {
        getViewer().setExpandedState(this.dataHandler.getPidcVersionBO().getPidcDataHandler().getVariantMap()
            .get(this.selectedPidcSubVariant.getPidcVariantId()), true);
        this.viewer.setSelection(new StructuredSelection(
            getPidcVersionBO().getPidcDataHandler().getSubVariantMap().get(this.selectedPidcSubVariant.getId())));

      }
    }
  }

  /**
   * function to set pidc variant selection after tree refresh
   */
  private void setPidcVariantSelection() {
    if (null != this.selectedPidcVariant) {
      if (PIDCDetailsViewPart.isVirtualStructureDisplayEnabled() &&
          (null != getPidcVersionBO().getPidcDetailsNodeHandler().getRootVirtualNodes())) {
        for (PIDCDetailsNode detailsNode : getPidcVersionBO().getPidcDataHandler().getPidDetNodeMap().values()) {
          if ((detailsNode.getPidcVariant() != null) &&
              CommonUtils.isEqual(detailsNode.getPidcVariant().getId(), this.selectedPidcVariant.getId())) {
            this.viewer.setSelection(new StructuredSelection(detailsNode));
          }
        }
      }
      else {
        this.viewer.setSelection(new StructuredSelection(
            getPidcVersionBO().getPidcDataHandler().getVariantMap().get(this.selectedPidcVariant.getId())));
      }
    }
  }
}
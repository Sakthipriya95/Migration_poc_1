/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.part.ISetSelectionTarget;

import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.TreeViewFlagValueProvider;
import com.bosch.caltool.icdm.client.bo.cdr.RvwResEditorInputData;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorInputData;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkedWithEditorView;
import com.bosch.caltool.icdm.common.ui.listeners.LinkWithEditorPartListener;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.sorter.PIDCTreeElementsComparer;
import com.bosch.caltool.icdm.common.ui.table.filters.PidcNameFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * Project ID card Tree
 *
 * @author adn1cob
 */
public class PIDTreeViewPart extends AbstractViewPart implements ILinkedWithEditorView, ISetSelectionTarget {

  /**
   * Unique ID for this View Part.
   */
  public static final String VIEW_ID = "com.bosch.caltool.icdm.common.ui.views.PIDTreeViewPart";

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Action instance for TreeViewer
   */
  private TreeViewer viewer;

  // Link with editor, listeners
  private IPartListener2 linkWithEditorPartListener;
  // Action to link with editor
  private Action linkWithEditorAction;
  // initialize from preference from preference store
  private boolean isLinkEnabled = isLinkWithEditorPrefEnabled();

  private PidcTreeNode selectedTreeNode;
  private String oldActiveVerNodeId;
  private PidcTreeNodeHandler treeHandler;

  /**
   * @return the linkingActive
   */
  public boolean isLinkingEnabled() {
    return this.isLinkEnabled;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets .Composite)
   */
  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    // Build the UI
    createComposite();

    // Context menu actions
    hookContextMenu();

    String grpName = "PIDC_TREE";
    String name = "PID_CARD_TAB";
    // iCDM-530
    setTitleToolTip(CommonUiUtils.getMessage(grpName, name));


    // iCDM-1241
    // add listeners
    this.linkWithEditorPartListener = new LinkWithEditorPartListener(this);
    this.linkWithEditorAction = new Action("Link with Editor", IAction.AS_CHECK_BOX) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        toggleLinking(isChecked());
      }
    };
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.LINK_EDITOR_16X16);
    this.linkWithEditorAction.setImageDescriptor(imageDesc);
    getViewSite().getActionBars().getToolBarManager().add(this.linkWithEditorAction);
    // set previous state
    this.linkWithEditorAction.setChecked(this.isLinkEnabled);
    getSite().getPage().addPartListener(this.linkWithEditorPartListener);

  }

  @Override
  public void editorActivated(final IEditorPart activeEditor) {
    if (!isLinkingEnabled() || !getViewSite().getPage().isPartVisible(this) || (activeEditor == null)) {
      return;
    }
    IEditorInput editorInput = activeEditor.getEditorInput();
    if (editorInput instanceof ILinkSelectionProvider) {
      ILinkSelectionProvider linkSelectionProvider = (ILinkSelectionProvider) editorInput;
      Object editorInputLinkedObj = linkSelectionProvider.getEditorInputSelection();
      if (CommonUtils.isNotNull(editorInputLinkedObj)) {
        PidcTreeNode linkedNode = new PidcTreeNode();
        if (editorInputLinkedObj instanceof PidcVersion) {
          PidcVersion currPidcVer = (PidcVersion) editorInputLinkedObj;
          linkedNode = getTreeHandler().getPidcVerIdTreenodeMap().get(currPidcVer.getId());
          if (null == linkedNode) {
            PidcVersionServiceClient pidcVerSer = new PidcVersionServiceClient();
            Set<Long> pidcIdSet = new HashSet<>();
            pidcIdSet.add(currPidcVer.getPidcId());
            try {
              Map<Long, PidcVersionInfo> verMap = pidcVerSer.getActiveVersionsWithStructure(pidcIdSet);
              PidcVersionInfo verInfo = verMap.values().iterator().next();
              PidcTreeNode activeVerNode =
                  getTreeHandler().getPidcVerIdTreenodeMap().get(verInfo.getPidcVersion().getId());
              PidcTreeNode otherVerTitleNode = getTreeHandler().createOtherVerTitleNode(activeVerNode);
              getTreeHandler().getOtherPidcVerNodes(otherVerTitleNode);
              linkedNode = getTreeHandler().getPidcVerIdTreenodeMap().get(currPidcVer.getId());
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
            }
          }
        }
        else if (editorInputLinkedObj instanceof PidcA2LBO) {
          PidcA2LBO pidcA2lBo = (PidcA2LBO) editorInputLinkedObj;
          linkedNode = getPidcA2lLinkNode(pidcA2lBo.getPidcVersion(), pidcA2lBo.getPidcA2l());
        }
        else if (editorInputLinkedObj instanceof RvwResEditorInputData) {
          linkedNode = this.treeHandler.getReviewResultNode(editorInputLinkedObj);
        }
        else if (editorInputLinkedObj instanceof QnaireRespEditorInputData) {
          linkedNode = getQnaireRespNode(editorInputLinkedObj);

        }
        else if (editorInputLinkedObj instanceof PidcTreeNode) {
          PidcTreeNode selNode = (PidcTreeNode) editorInputLinkedObj;
          if (selNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
            linkedNode = getPidcA2lLinkNode(selNode.getPidcVersion(), selNode.getPidcA2l());
          }
        }
        if (linkedNode != null) {
          StructuredSelection structuredSelection = new StructuredSelection(linkedNode);
          this.viewer.setSelection(structuredSelection, true);
        }
      }
    }
  }

  /**
   * @param editorInputLinkedObj
   * @return
   */
  private PidcTreeNode getQnaireRespNode(final Object editorInputLinkedObj) {
    PidcTreeNode linkedNode = null;
    QnaireRespEditorInputData qnaireRespEditorInputData = (QnaireRespEditorInputData) editorInputLinkedObj;

    Long pidcVersId = qnaireRespEditorInputData.getPidcVersion().getId();
    PidcTreeNode pidcVersionNode = getTreeHandler().getPidcVerIdTreenodeMap().get(pidcVersId);
    if (null == pidcVersionNode) {
      PidcVersionServiceClient pidcVerSer = new PidcVersionServiceClient();
      Set<Long> pidcVerIdSet = new HashSet<>();
      pidcVerIdSet.add(pidcVersId);
      try {
        Map<Long, PidcVersionInfo> verMap = pidcVerSer.getActiveVersWithStrByOtherVerId(pidcVerIdSet);
        PidcVersionInfo verInfo = verMap.values().iterator().next();
        PidcTreeNode activeVerNode = getTreeHandler().getPidcVerIdTreenodeMap().get(verInfo.getPidcVersion().getId());
        PidcTreeNode otherVerTitleNode = getTreeHandler().createOtherVerTitleNode(activeVerNode);
        getTreeHandler().getOtherPidcVerNodes(otherVerTitleNode);
        pidcVersionNode = getTreeHandler().getPidcVerIdTreenodeMap().get(pidcVersId);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    StringBuilder linkNodeId = new StringBuilder();
    if (null != pidcVersionNode) {
      linkNodeId.append(pidcVersionNode.getNodeId());
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
      linkNodeId.append(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType());
      linkNodeId.append(pidcVersId);
      Long pidcVarId = ApicConstants.NO_VARIANT_ID;


      if (CommonUtils.isNotNull(qnaireRespEditorInputData.getSecondaryPidcVariant())) {
        pidcVarId = qnaireRespEditorInputData.getSecondaryPidcVariant().getId();
      }
      else if (CommonUtils.isNotNull(qnaireRespEditorInputData.getPidcVariant())) {
        pidcVarId = qnaireRespEditorInputData.getPidcVariant().getId();
      }
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.QNAIRE_VAR_PVER_PREFIX + pidcVarId +
          PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.RESP_PREFIX +
          qnaireRespEditorInputData.getRvwQnaireResponse().getA2lRespId() + PidcTreeNodeHandler.LEVEL_SEPARATOR +
          PidcTreeNodeHandler.RESP_WP_PREFIX + qnaireRespEditorInputData.getRvwQnaireResponse().getA2lWpId() +
          PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.QNAIRE_RESP_PVER_PREFIX +
          qnaireRespEditorInputData.getRvwQnaireRespVersion().getQnaireRespId());
      Map<String, PidcTreeNode> nodeIdNodeMap = getTreeHandler().getNodeIdNodeMap();
      linkedNode = nodeIdNodeMap.get(linkNodeId.toString());
      // if nodeid is not available in nodeIdNodeMap
      if (CommonUtils.isNull(linkedNode)) {
        StringBuilder rvwQnaireResp = new StringBuilder();
        // expanding the pidc level
        this.viewer.expandToLevel(nodeIdNodeMap.get(pidcVersionNode.getNodeId()), 1);
        // expanding the review questionnaire level
        rvwQnaireResp.append(pidcVersionNode.getNodeId());
        rvwQnaireResp.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
        rvwQnaireResp.append(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType());
        rvwQnaireResp.append(pidcVersId);
        this.viewer.expandToLevel(nodeIdNodeMap.get(rvwQnaireResp.toString()), 1);
        // expanding the qnaire response variant level
        rvwQnaireResp
            .append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.QNAIRE_VAR_PVER_PREFIX + pidcVarId);
        this.viewer.expandToLevel(nodeIdNodeMap.get(rvwQnaireResp.toString()), 1);
        // expanding the qnaire a2l responsibility level
        rvwQnaireResp.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.RESP_PREFIX +
            qnaireRespEditorInputData.getRvwQnaireResponse().getA2lRespId());
        this.viewer.expandToLevel(nodeIdNodeMap.get(rvwQnaireResp.toString()), 1);
        // expanding the qnaire a2l workpackage level
        rvwQnaireResp.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.RESP_WP_PREFIX +
            qnaireRespEditorInputData.getRvwQnaireResponse().getA2lWpId());
        this.viewer.expandToLevel(nodeIdNodeMap.get(rvwQnaireResp.toString()), 1);
        // getting pidctreenode from nodeIdNodeMap
        linkedNode = nodeIdNodeMap.get(linkNodeId.toString());
      }
    }
    return linkedNode;
  }


  /**
   * @param pidcA2lBo
   * @return
   */
  private PidcTreeNode getPidcA2lLinkNode(final PidcVersion pidcVer, final PidcA2l pidcA2l) {
    PidcTreeNode pidcVersionNode = getTreeHandler().getPidcVerIdTreenodeMap().get(pidcVer.getId());
    if (null == pidcVersionNode) {
      PidcVersionServiceClient pidcVerSer = new PidcVersionServiceClient();
      Set<Long> pidcVerIdSet = new HashSet<>();
      pidcVerIdSet.add(pidcVer.getId());
      try {
        Map<Long, PidcVersionInfo> verMap = pidcVerSer.getActiveVersWithStrByOtherVerId(pidcVerIdSet);
        PidcVersionInfo verInfo = verMap.values().iterator().next();
        PidcTreeNode activeVerNode = getTreeHandler().getPidcVerIdTreenodeMap().get(verInfo.getPidcVersion().getId());
        PidcTreeNode otherVerTitleNode = getTreeHandler().createOtherVerTitleNode(activeVerNode);
        getTreeHandler().getOtherPidcVerNodes(otherVerTitleNode);
        pidcVersionNode = getTreeHandler().getPidcVerIdTreenodeMap().get(pidcVer.getId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    StringBuilder linkNodeId = new StringBuilder();
    PidcTreeNode linkedNode = null;
    if (null != pidcVersionNode) {
      linkNodeId.append(pidcVersionNode.getNodeId());
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
      linkNodeId.append(PidcTreeNodeHandler.SDOM_PVER_PREFIX);
      linkNodeId.append(pidcA2l.getSdomPverName());
      String sdomPverNodeId = linkNodeId.toString();
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
      linkNodeId.append(PidcTreeNodeHandler.PIDC_A2L_PREFIX);
      linkNodeId.append(pidcA2l.getId());
      Map<String, PidcTreeNode> nodeIdNodeMap = getTreeHandler().getNodeIdNodeMap();
      if (null == nodeIdNodeMap.get(linkNodeId.toString())) {
        getTreeHandler().getPidcVerNodeChildren(pidcVersionNode);
        getTreeHandler().getA2lNodeList(nodeIdNodeMap.get(sdomPverNodeId), PIDTreeViewPart.displayInactiveA2LFiles());
      }
      linkedNode = nodeIdNodeMap.get(linkNodeId.toString());
    }
    return linkedNode;
  }


  /**
   * @param isEnabled true if enabled
   */
  protected void toggleLinking(final boolean isEnabled) {
    this.isLinkEnabled = isEnabled;
    // store the preference to preference store
    PlatformUI.getPreferenceStore().setValue(CommonUtils.LINK_WITH_EDITOR_PREF,
        isEnabled ? ApicConstants.CODE_YES : ApicConstants.CODE_NO);
    // activate editor based on preference
    if (isEnabled) {
      editorActivated(getSite().getPage().getActiveEditor());
    }
  }

  /**
   * Get LinkWithEditor preference from store
   *
   * @return
   */
  private static boolean isLinkWithEditorPrefEnabled() {
    // get the preference from store
    String pref = PlatformUI.getPreferenceStore().getString(CommonUtils.LINK_WITH_EDITOR_PREF);
    // enable link for the first time OR enabled in previous start
    return (pref == null) || CommonUtils.isEqual(pref, ApicConstants.CODE_YES);
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {

    final GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);
    // Create filters
    // iCDM-736, initial changes
    FavoritesViewPart favViewPart = (FavoritesViewPart) WorkbenchUtils.getView(FavoritesViewPart.VIEW_ID);
    if (null == this.treeHandler) {
      if ((null != favViewPart) && (null != favViewPart.getPidcTreeHandler())) {
        this.treeHandler = favViewPart.getPidcTreeHandler();
      }
      else {
        this.treeHandler = new PidcTreeNodeHandler(true);
      }
    }

    PidcNameFilter filter = new PidcNameFilter();
    final FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER | SWT.MULTI, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);

    // set auto expand level
    this.viewer.setAutoExpandLevel(2);
    // set the comparator
    this.viewer.setComparer(new PIDCTreeElementsComparer());
    // Set Content provider for the tree
    // iCDM-1982
    this.viewer.setContentProvider(new PIDTreeViewContentProvider(this.treeHandler, new TreeViewFlagValueProvider(true, true, false, true, true, true, true)));

    // Set Label provider for the tree
    this.viewer.setLabelProvider(new PIDTreeViewLabelProvider(this.treeHandler, this.viewer));

    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("");
    // ICDM 1042
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);
    /**
     * add selection listener to tree
     */
    this.viewer.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selectedNode = selection.getFirstElement();

      if (selectedNode instanceof PidcTreeNode) {
        PIDTreeViewPart.this.selectedTreeNode = (PidcTreeNode) selectedNode;
        PIDTreeViewPart.this.treeHandler.setSelectedNode(PIDTreeViewPart.this.selectedTreeNode);

        if (PIDTreeViewPart.this.selectedTreeNode.getNodeType()
            .equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION)) {
          PIDTreeViewPart.this.oldActiveVerNodeId = PIDTreeViewPart.this.selectedTreeNode.getParentNodeId();
          PIDTreeViewPart.this.treeHandler.setOldActiveVerNodeId(PIDTreeViewPart.this.oldActiveVerNodeId);
        }
        CommonUiUtils.getInstance().updateExcelExportServiceState(PIDTreeViewPart.this.selectedTreeNode);
      }

    });

    CommonActionSet actionSet = new CommonActionSet();
    for (IDoubleClickListener listener : actionSet.getPidcTreeListeners()) {
      this.viewer.addDoubleClickListener(listener);
    }

    actionSet.addCommonTreeActions(this.viewer, getViewSite().getActionBars().getToolBarManager(), 2);
    // ICDM-121
    addKeyListenerToViewer();

    // ICDM -588
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

  }


  /**
   * This method adds key listner on PIDC treeviewer. It checks for 'F2','DEL','CTRL+C','CTRL+V' keys pressed on PIDC
   * Node.
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

        CommonActionSet actionSet = new CommonActionSet();
        actionSet.getKeyListenerToViewer(
            ((IStructuredSelection) (PIDTreeViewPart.this.viewer.getSelection())).getFirstElement(), event.keyCode,
            event.stateMask, PIDTreeViewPart.this.viewer);

      }
    });

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

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
   */
  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();
  }

  /**
   * Build the context menu for tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(PIDTreeViewPart.this::fillContextMenu);
    Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, this.viewer);
  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {
    // Get the current selection and add actions to it
    IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      Object firstElement = selection.getFirstElement();
      CommonActionSet actionSet = new CommonActionSet();
      if (firstElement instanceof PidcTreeNode) {
        // Set Right click for the selected item
        PidcTreeNode treeNodeElement = (PidcTreeNode) firstElement;
        setRightClickActionsForPIDCTreeNode(manager, actionSet, treeNodeElement);
      }
    }
  }

  /**
   * Set right click actions for the selected item
   *
   * @param manager
   * @param actionSet
   * @param pidcVersion
   */
  private void setRightClickActionsForPIDCTreeNode(final IMenuManager manager, final CommonActionSet actionSet,
      final PidcTreeNode pidcTreeNode) {
    actionSet.getPidcTreeNodeRightClickActions(manager, this.viewer, pidcTreeNode);
  }

  /**
   * Refresh the page, after applying menu filters
   */
  public void refreshPage() {
    this.viewer.setInput("");
    this.viewer.refresh();
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
  public void selectReveal(final ISelection arg0) {
    // TODO Auto-generated method stub
  }

  /**
   * @return data handler
   */
  @Override
  public PidcTreeNodeHandler getDataHandler() {
    return getTreeHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {

    // get all expanded elements
    Object[] elements = this.viewer.getExpandedElements();
    TreePath[] treePaths = this.viewer.getExpandedTreePaths();
    ITreeSelection structuredSelection = this.viewer.getStructuredSelection();
    // refresh tree viewer
    this.viewer.refresh();

    // set all expanded elements
    this.viewer.setExpandedElements(elements);
    this.viewer.setExpandedTreePaths(treePaths);

    this.viewer.setSelection(structuredSelection);


  }

  /**
   * @return the selectedTreeNode
   */
  public PidcTreeNode getSelectedTreeNode() {
    return this.selectedTreeNode;
  }

  /**
   * @param selectedTreeNode the selectedTreeNode to set
   */
  public void setSelectedTreeNode(final PidcTreeNode selectedTreeNode) {
    this.selectedTreeNode = selectedTreeNode;
  }

  /**
   * @return the treeHandler
   */
  public PidcTreeNodeHandler getTreeHandler() {
    return this.treeHandler;
  }


  /**
   * @param treeHandler the treeHandler to set
   */
  public void setTreeHandler(final PidcTreeNodeHandler treeHandler) {
    this.treeHandler = treeHandler;
  }

  /**
   * @return true, if nodes without pidc cannot be displayed, else false
   */
  public static boolean hideEmptyNodes() {
    return CommonUtils.isEqual(ApicConstants.CODE_YES,
        PlatformUI.getPreferenceStore().getString(CommonUIConstants.PIDC_TREE_HIDE_EMPTY_NODES));
  }

  /**
   * @return true if inactive a2lfiles to be displayed
   */
  public static boolean displayInactiveA2LFiles() {
    return CommonUtils.isEqual(ApicConstants.CODE_YES,
        PlatformUI.getPreferenceStore().getString(CommonUIConstants.PIDC_TREE_DISPLAY_INACTIVE_A2L_FILES));
  }

  /**
   * @return true if deleted Qnaire Response to be displayed
   */
  public static boolean isDisplayDeletedQnaireResponseEnabled() {
    return CommonUtils.isEqual(ApicConstants.CODE_YES,
        PlatformUI.getPreferenceStore().getString(CommonUIConstants.PIDC_TREE_DISPLAY_DELETED_QNAIRE_RESP));
  }

  /**
   * @return true if deleted pidc variant to be displayed
   */
  public static boolean isDisplayDeletedPIDCVariantEnabled() {
    return CommonUtils.isEqual(ApicConstants.CODE_YES,
        PlatformUI.getPreferenceStore().getString(CommonUIConstants.PIDC_TREE_DISPLAY_DELETED_PIDC_VARIANT));
  }
}

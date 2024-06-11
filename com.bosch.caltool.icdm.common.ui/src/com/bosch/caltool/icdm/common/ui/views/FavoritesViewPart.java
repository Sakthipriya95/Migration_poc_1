package com.bosch.caltool.icdm.common.ui.views;

import java.util.Map;

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
import com.bosch.caltool.icdm.client.bo.apic.FavouritesTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.cdr.RvwResEditorInputData;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorInputData;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkedWithEditorView;
import com.bosch.caltool.icdm.common.ui.listeners.LinkWithEditorPartListener;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.sorter.FavTreeElementsComparer;
import com.bosch.caltool.icdm.common.ui.table.filters.PidcNameFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * Favorites PID Tree
 *
 * @author adn1cob
 */
public class FavoritesViewPart extends AbstractViewPart implements ILinkedWithEditorView, ISetSelectionTarget {

  /**
   * Unique ID for this View Part.
   */
  public static final String VIEW_ID = "com.bosch.caltool.icdm.common.ui.views.FavoritesViewPart";

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * TreeViewer instance
   */
  private TreeViewer viewer;

  /**
   * Holds the selected ProjectID card
   */
  private PidcVersion selectedPidcVer;

  private FavouritesTreeNodeHandler treeHandler;

  // Link with editor, listeners
  private IPartListener2 linkWithEditorPartListener;
  // Action to link with editor
  private Action linkWithEditorAction;
  // initialize from preference from preference store
  private boolean isLinkEnabled = isLinkWithEditorPrefEnabled();


  /**
   * Selected tree node
   */
  private PidcTreeNode selectedTreeNode;
  private PidcTreeNodeHandler pidcTreeHandler;

  /**
   * @return the linkingActive
   */
  public boolean isLinkingEnabled() {
    return this.isLinkEnabled;
  }

  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());

    // Build the UI
    createComposite();

    // Build Context Menu for the tree
    hookContextMenu();

    // iCDM-530
    setTitleToolTip("List of Favorite Project ID Cards");

    // add listeners
    this.linkWithEditorPartListener = new LinkWithEditorPartListener(this);
    // create action for linking with editor
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
   * Gets the favorites tree viewer
   *
   * @return TreeViewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {

    final GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);
    // Set Content provider for the tree
    PIDTreeViewPart pidViewPart = (PIDTreeViewPart) WorkbenchUtils.getView(PIDTreeViewPart.VIEW_ID);
    if (null != pidViewPart) {
      if (null != pidViewPart.getTreeHandler()) {
        this.pidcTreeHandler = pidViewPart.getTreeHandler();
      }
      else {
        this.pidcTreeHandler = new PidcTreeNodeHandler(true);
        pidViewPart.setTreeHandler(this.pidcTreeHandler);
      }
    }
    else {
      this.pidcTreeHandler = new PidcTreeNodeHandler(true);
    }
    // Create filters
    PidcNameFilter filter = new PidcNameFilter();
    final FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER | SWT.MULTI, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(1);
    this.viewer.setComparer(new FavTreeElementsComparer());

    this.treeHandler = new FavouritesTreeNodeHandler(this.pidcTreeHandler);
    this.viewer.setContentProvider(new FavoritesTreeViewContentProvider(this.pidcTreeHandler, this.treeHandler, true, 
            true, false, true, true));

    // Set Label provider for the tree
    this.viewer.setLabelProvider(new FavoritesTreeViewLabelProvider(this.viewer));
    // ICDM 1042
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);
    // Call to build tree using setInput(), only if user is available
    try {
      if (new CurrentUserBO().getUser() != null) {
        this.viewer.setInput("");
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    // Handle double clicks
    this.viewer.addSelectionChangedListener(event -> {
      // Icdm-796
      // ICDM-865
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selected = selection.getFirstElement();
      if (selected instanceof PidcTreeNode) {
        FavoritesViewPart.this.selectedTreeNode = (PidcTreeNode) selected;
        CommonUiUtils.getInstance().updateExcelExportServiceState(FavoritesViewPart.this.selectedTreeNode);
      }

    });

    CommonActionSet actionSet = new CommonActionSet();
    for (IDoubleClickListener listener : actionSet.getPidcTreeListeners()) {
      this.viewer.addDoubleClickListener(listener);
    }

    actionSet.addCommonTreeActions(this.viewer, getViewSite().getActionBars().getToolBarManager(), 1);

    // ICDM-121
    addKeyListenerToViewer();

    // ICDM -588
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

  }


  /**
   * This method adds key listner on PIDC treeviewer. It checks for 'F2' key is pressed on PIDC Node.
   */
  // ICDM-121
  private void addKeyListenerToViewer() {
    this.viewer.getTree().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {
        CommonActionSet actionSet = new CommonActionSet();
        actionSet.getKeyListenerToViewer(
            ((IStructuredSelection) (FavoritesViewPart.this.viewer.getSelection())).getFirstElement(), event.keyCode,
            event.stateMask, FavoritesViewPart.this.viewer);

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

  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();
  }

  /**
   * Build contect menu on tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(FavoritesViewPart.this::fillContextMenu);
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
   * @param manager
   * @param actionSet
   * @param treeNodeElement
   */
  private void setRightClickActionsForPIDCTreeNode(final IMenuManager manager, final CommonActionSet actionSet,
      final PidcTreeNode pidcTreeNode) {
    actionSet.getPidcTreeNodeRightClickActions(manager, this.viewer, pidcTreeNode);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {

    // get all expanded elements
    Object[] elements = this.viewer.getExpandedElements();
    TreePath[] treePaths = this.viewer.getExpandedTreePaths();

    // refresh tree viewer
    this.viewer.refresh();

    // set all expanded elements
    this.viewer.setExpandedElements(elements);
    this.viewer.setExpandedTreePaths(treePaths);

  }

  /**
   * Get the selected pidc
   *
   * @return the selectedPIDCard version
   */
  public PidcVersion getSelectedPidcVersion() {
    return this.selectedPidcVer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectReveal(final ISelection selection) {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
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
          linkedNode = this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(currPidcVer.getId());
        }
        else {
          Map<String, PidcTreeNode> nodeIdNodeMap = this.pidcTreeHandler.getNodeIdNodeMap();
          if (editorInputLinkedObj instanceof PidcA2LBO) {
            PidcA2LBO pidcA2lBo = (PidcA2LBO) editorInputLinkedObj;
            PidcTreeNode pidcVersionNode =
                this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(pidcA2lBo.getPidcVersion().getId());
            StringBuilder linkNodeId = new StringBuilder();
            if (null != pidcVersionNode) {
              linkNodeId.append(pidcVersionNode.getNodeId());
              linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
              linkNodeId.append(PidcTreeNodeHandler.SDOM_PVER_PREFIX);
              linkNodeId.append(pidcA2lBo.getPidcA2l().getSdomPverName());
              String sdomPverNodeId = linkNodeId.toString();
              linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
              linkNodeId.append(PidcTreeNodeHandler.PIDC_A2L_PREFIX);
              linkNodeId.append(pidcA2lBo.getPidcA2l().getId());
              if (null == nodeIdNodeMap.get(linkNodeId.toString())) {
                this.pidcTreeHandler.getPidcVerNodeChildren(pidcVersionNode);
                this.pidcTreeHandler.getA2lNodeList(nodeIdNodeMap.get(sdomPverNodeId),
                    PIDTreeViewPart.displayInactiveA2LFiles());
              }
              linkedNode = nodeIdNodeMap.get(linkNodeId.toString());
            }
          }
          else if (editorInputLinkedObj instanceof RvwResEditorInputData) {
            linkedNode = this.pidcTreeHandler.getReviewResultNode(editorInputLinkedObj);
          }
          else if (editorInputLinkedObj instanceof QnaireRespEditorInputData) {
            linkedNode = getQnaireRespNode(editorInputLinkedObj, nodeIdNodeMap);
          }
          else if (editorInputLinkedObj instanceof PidcTreeNode) {
            PidcTreeNode selNode = (PidcTreeNode) editorInputLinkedObj;
            if (selNode.getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
              PidcTreeNode pidcVersionNode =
                  this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(selNode.getPidcVersion().getId());
              StringBuilder linkNodeId = new StringBuilder();
              if (null != pidcVersionNode) {
                linkNodeId.append(pidcVersionNode.getNodeId());
                linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
                linkNodeId.append(PidcTreeNodeHandler.SDOM_PVER_PREFIX);
                linkNodeId.append(selNode.getPidcA2l().getSdomPverName());
                String sdomPverNodeId = linkNodeId.toString();
                linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
                linkNodeId.append(PidcTreeNodeHandler.PIDC_A2L_PREFIX);
                linkNodeId.append(selNode.getPidcA2l().getId());
                if (null == nodeIdNodeMap.get(linkNodeId.toString())) {
                  this.pidcTreeHandler.getPidcVerNodeChildren(pidcVersionNode);
                  this.pidcTreeHandler.getA2lNodeList(nodeIdNodeMap.get(sdomPverNodeId),
                      PIDTreeViewPart.displayInactiveA2LFiles());
                }
                linkedNode = nodeIdNodeMap.get(linkNodeId.toString());
              }
            }
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
   * @param nodeIdNodeMap
   * @return
   */
  private PidcTreeNode getQnaireRespNode(final Object editorInputLinkedObj,
      final Map<String, PidcTreeNode> nodeIdNodeMap) {
    PidcTreeNode linkedNode = null;
    QnaireRespEditorInputData qnaireRespEditorInputData = (QnaireRespEditorInputData) editorInputLinkedObj;
    Long pidcVersId = qnaireRespEditorInputData.getPidcVersion().getId();
    PidcTreeNode pidcVersionNode = this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(pidcVersId);
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
   * @return true if Display of Inactive A2L Files is enabled
   */
  public static boolean isDisplayInactiveA2LEnabled() {
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
   * @return true if deleted PIDC Variant to be displayed
   */
  public static boolean isDisplayDeletedPIDCVariantEnabled() {
    return CommonUtils.isEqual(ApicConstants.CODE_YES,
        PlatformUI.getPreferenceStore().getString(CommonUIConstants.PIDC_TREE_DISPLAY_DELETED_PIDC_VARIANT));
  }

  /**
   * Refresh the page, after applying menu filters
   */
  public void refreshPage() {
    // refersh tree viewer

    if (this.pidcTreeHandler == null) {
      this.pidcTreeHandler = new PidcTreeNodeHandler();
    }
    if (this.treeHandler == null) {
      this.treeHandler = new FavouritesTreeNodeHandler(this.pidcTreeHandler);
    }

    this.viewer.setContentProvider(new FavoritesTreeViewContentProvider(this.pidcTreeHandler, this.treeHandler, true, 
            true, false, true, true));
    this.viewer.setInput("");
    this.viewer.refresh();
  }

  /**
   * @return data handler
   */
  @Override
  public FavouritesTreeNodeHandler getDataHandler() {
    return getTreeHandler();
  }

  /**
   * @return
   */
  public FavouritesTreeNodeHandler getTreeHandler() {
    return this.treeHandler;
  }

  /**
   * @return
   */
  public PidcTreeNodeHandler getPidcTreeHandler() {
    return this.pidcTreeHandler;
  }

  /**
   * @param treeHandler the treeHandler to set
   */
  public void setTreeHandler(final FavouritesTreeNodeHandler treeHandler) {
    this.treeHandler = treeHandler;
  }

  /**
   * @return the selectedTreeNode
   */
  public PidcTreeNode getSelectedTreeNode() {
    return this.selectedTreeNode;
  }

}

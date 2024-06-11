/**
 *
 */
package com.bosch.caltool.apic.ui.views;

import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewLabelProvider;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.AttrGroupClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrSuperGroupClientBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItem;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseFavNodesMgr;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author adn1cob
 */
public class PIDCOutlinePage extends AbstractPage {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * the parent level composite for this page
   */
  private Composite composite;
  /**
   * the tree viewer for the outline page
   */
  private TreeViewer viewer;
  // ICDM-1040
  /**
   * Pidcard version associated with the outline page
   */
  private final PidcVersion pidcVer;
  private final OutLineViewDataHandler dataHandler;
  // usecase item selected to create link to usecase for pidc version
  private final Long ucItemId;

  private final boolean projUseCase;
  /**
   * Usecase item to be selected in outline view which is part of uc link
   */
  private Object itemToBeSelected;


  /**
   * Constructor
   *
   * @param pidcVer PIDCard version
   * @param ucItemId to identify ucitem selected to create link to usecase for pidc version
   * @param dataHandler Outline View Data Handler
   * @param projUseCase used when editor is opened using project use case link
   */
  public PIDCOutlinePage(final PidcVersion pidcVer, final Long ucItemId, final OutLineViewDataHandler dataHandler,
      final boolean projUseCase) {
    // getting the pid card for which the outline is created
    this.pidcVer = pidcVer;
    this.ucItemId = ucItemId;
    this.dataHandler = dataHandler;
    this.projUseCase = projUseCase;
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
    hookContextMenu();
    // add drag & drop listeners
    addDragDropListeners();// ICDM-1031

    // add selection provider
    getSite().setSelectionProvider(this.viewer);

  }

  /**
   * ICDM-1031 Adds drag & drop listener to the tree view control
   */
  private void addDragDropListeners() {
    // this line is to handle the drag& drop support
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    // add drag listener
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

    // add drop listener
    final DropTarget target =
        new DropTarget(this.viewer.getControl(), DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

      @Override
      public void dragOver(final DropTargetEvent event) {
        if ((event.item.getData() instanceof com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode) ||
            (event.item.getData() instanceof com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode)) {
          event.detail = DND.DROP_DEFAULT;
        }
        else {
          event.detail = DND.DROP_NONE;
        }
        // this line is to enable automatic scroll in the tree viewer during drag
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
      }


      @Override
      public void dragEnter(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void dragOperationChanged(final DropTargetEvent event) {
        if (event.detail == DND.DROP_DEFAULT) {
          event.detail = DND.DROP_COPY;
        }
      }

      @Override
      public void drop(final DropTargetEvent event) {
        if (event.data == null) {
          // if no data, drop detail is set to none
          event.detail = DND.DROP_NONE;
          return;
        }
        dropEventForUsecaseObjects(event);
      }
    });
  }

  /**
   * @param event
   */
  private void dropEventForUsecaseObjects(final DropTargetEvent event) {
    CommonActionSet actionSet = new CommonActionSet();
    final Object dragData = event.data;
    final IStructuredSelection strucSelec = (StructuredSelection) dragData;
    try {
      if (strucSelec.getFirstElement() instanceof IUseCaseItemClientBO) {
        // consider only if the drag data is AbstractUseCaseItem
        if (event.item.getData() instanceof com.bosch.caltool.icdm.client.bo.uc.ProjFavUcRootNode) {
          // for project usecases
          // display message when the user does not have owner rights
          if (!new CurrentUserBO().hasNodeOwnerAccess(PIDCOutlinePage.this.pidcVer.getPidcId())) {
            CDMLogger.getInstance().infoDialog("Not Enough Privileges!", Activator.PLUGIN_ID);
            return;
          }
          // ICDM-1031
          UseCaseFavNodesMgr ucFavMgr =
              new UseCaseFavNodesMgr(PIDCOutlinePage.this.pidcVer, PIDCOutlinePage.this.dataHandler.getUcDataHandler());
          PIDCOutlinePage.this.dataHandler.getUcDataHandler().setUcFavMgr(ucFavMgr);
          actionSet.movToProjFav((IUseCaseItemClientBO) strucSelec.getFirstElement(), PIDCOutlinePage.this.viewer,
              PIDCOutlinePage.this.pidcVer, PIDCOutlinePage.this.dataHandler.getUcDataHandler());

        }
        else if (event.item.getData() instanceof com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode) {
          // // ICDM-1029
          // // for private usecases
          UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(new CurrentUserBO().getUser(),
              PIDCOutlinePage.this.dataHandler.getUcDataHandler());
          PIDCOutlinePage.this.dataHandler.getUcDataHandler().setUcFavMgr(ucFavMgr);
          actionSet.moveToUserFav((IUseCaseItemClientBO) strucSelec.getFirstElement(), PIDCOutlinePage.this.viewer,
              PIDCOutlinePage.this.dataHandler.getUcDataHandler());
        }
      }

    }
    catch (ApicWebServiceException | IcdmException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  // ICDM-766
  /**
   * Build the context menu for tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(PIDCOutlinePage.this::fillContextMenu);
    final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
  }

  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {
    final CommonActionSet actionSet = new CommonActionSet();

    // Get the current selection and add actions to it
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      FavUseCaseItem favUCItem = null;
      // displaying the additional links in context menu
      if (selection.getFirstElement() instanceof IUseCaseItemClientBO) {
        useCaseItemContextMenuItem(manager, actionSet, selection);
      }
      // ICDM-930
      else if (selection.getFirstElement() instanceof AttrGroup) {
        actionSet.addLinkAction(manager, (new AttrGroupClientBO((AttrGroup) selection.getFirstElement())).getLinks());
      } // ICDM-929
      else if (selection.getFirstElement() instanceof AttrSuperGroup) {
        actionSet.addLinkAction(manager,
            (new AttrSuperGroupClientBO((AttrSuperGroup) selection.getFirstElement())).getLinks());
      } // ICDM-1031
      else if (selection.getFirstElement() instanceof FavUseCaseItemNode) {
        favUseCaseItemContextMenuItems(manager, actionSet, selection, favUCItem);
      }
    }
  }

  /**
   * @param manager
   * @param actionSet
   * @param selection
   */
  private void useCaseItemContextMenuItem(final IMenuManager manager, final CommonActionSet actionSet,
      final IStructuredSelection selection) {
    FavUseCaseItem favUCItem;
    boolean isPrivateUCItem = false;
    // add link context menu for usecase ,usecase section
    if (selection.getFirstElement() instanceof UseCaseSectionClientBO) {

      actionSet.addLinkAction(manager, ((UseCaseSectionClientBO) selection.getFirstElement()).getLinks());
    }
    else if (selection.getFirstElement() instanceof UsecaseClientBO) {
      actionSet.addLinkAction(manager, ((UsecaseClientBO) selection.getFirstElement()).getLinks());
    }
    boolean projUseCaseFlag = false;
    boolean privateUseCaseFlag = false;

    IUseCaseItemClientBO ucItem = (IUseCaseItemClientBO) selection.getFirstElement();
    if (ucItem instanceof UsecaseClientBO) {
      // add 'Open' use case action for common use cases
      actionSet.openUseCaseAction(manager, (UsecaseClientBO) ucItem);
      manager.add(new Separator());
    }
    isPrivateUCItem = checkIfPrivateUCItem(selection);

    // check whether the selection is done from Project UC section or Common UC section
    favUCItem = exploreParents(selection);

    if (null != this.pidcVer) {
      projUseCaseFlag = isEnableProjUseCase(selection);
      // add move to proj usecase fav,user usecase fav context menu
      actionSet.addMovToProjFav(manager, ucItem, this.viewer, this.pidcVer, this.dataHandler.getUcDataHandler(),
          projUseCaseFlag);
    }
    privateUseCaseFlag = isEnablePrivateUseCase(selection);
    actionSet.addMovToUserFav(manager, ucItem, this.viewer, this.dataHandler.getUcDataHandler(), privateUseCaseFlag);
    // context menu option to create and send in mail link to uc for pidc version
    manager.add(new Separator());
    createUcLinkContextMenu(manager, actionSet, ucItem, favUCItem, isPrivateUCItem);
  }

  /**
   * @param manager
   * @param actionSet
   * @param selection
   * @param favUCItem
   */
  private void favUseCaseItemContextMenuItems(final IMenuManager manager, final CommonActionSet actionSet,
      final IStructuredSelection selection, FavUseCaseItem favUCItem) {
    boolean isPrivateUCItem;
    FavUseCaseItemNode favUcNode = (FavUseCaseItemNode) selection.getFirstElement();
    isPrivateUCItem = checkIfPrivateUCItem(selection);
    // get the favUCItem only in Project UC Selection
    if (((TreeSelection) selection).getPaths()[0].getSegment(0) instanceof ProjFavUcRootNode) {
      // if the selected uc item is the usecase favorite object itself, directly get the object
      favUCItem = favUcNode.getFavUcItem();

      // selected uc item is a virtual node, use case favorite object is present only in child nodes
      if (null == favUCItem) {
        favUCItem = exploreChildren(favUcNode);
      }
    }

    // add link context menu for usecase, usecase section
    if (favUcNode.getUseCaseItem() instanceof UsecaseClientBO) {
      UsecaseClientBO usecaseClientBO = (UsecaseClientBO) (favUcNode.getUseCaseItem());
      // add 'Open' use case action for private and project use cases
      actionSet.openUseCaseAction(manager, usecaseClientBO);
      manager.add(new Separator());
      // context menu option to create and send in mail link to uc for pidc version
      actionSet.addLinkAction(manager, usecaseClientBO.getLinks());
      manager.add(new Separator());
      createUcLinkContextMenu(manager, actionSet, usecaseClientBO, favUCItem, isPrivateUCItem);
    }
    else if (favUcNode.getUseCaseItem() instanceof UseCaseSectionClientBO) {
      UseCaseSectionClientBO useCaseSectionClientBO = (UseCaseSectionClientBO) (favUcNode.getUseCaseItem());
      actionSet.addLinkAction(manager, useCaseSectionClientBO.getLinks());
      // context menu option to create and send in mail link to uc for pidc version
      manager.add(new Separator());
      createUcLinkContextMenu(manager, actionSet, useCaseSectionClientBO, favUCItem, isPrivateUCItem);
    }
    else if (favUcNode.getUseCaseItem() instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO useCaseGrpClientBO = (UseCaseGroupClientBO) (favUcNode.getUseCaseItem());
      createUcLinkContextMenu(manager, actionSet, useCaseGrpClientBO, favUCItem, isPrivateUCItem);
    }
    // add delete context menu if this is a fav node
    if (favUcNode.getFavUcItem() != null) {
      if (CommonUtils.isNotNull(favUcNode.getPIDCId()) && (this.pidcVer != null)) {
        // context menu for project usecases
        actionSet.addDelFavUcAction(manager, favUcNode, this.viewer, this.pidcVer.getPidcId(), this.pidcVer.getId());
      }
      else { // context menu for private usecases
        actionSet.addDelFavUcAction(manager, favUcNode, this.viewer, null, null);
      }
    }
  }


  /**
   * @param selection
   * @return
   */
  private boolean checkIfPrivateUCItem(final IStructuredSelection selection) {
    return (((TreeSelection) selection).getPaths()[0].getSegment(0) instanceof UserFavUcRootNode);
  }

  /**
   * Method to find the usecase favorite object from the parent nodes of the selected uc item
   *
   * @param ucItem
   * @param selection
   * @return
   */
  private FavUseCaseItem exploreParents(final IStructuredSelection selection) {

    TreePath[] selUcItemTreePaths = ((TreeSelection) selection).getPaths();
    // check whether the selection is done from Project Use Cases or Private Use Cases section

    if (selUcItemTreePaths[0].getSegment(0) instanceof ProjFavUcRootNode) {
      // iterate backward till we find a FavUseCaseItemNode with UseCaseFavorite Object
      for (int i = selUcItemTreePaths[0].getSegmentCount() - 1; i >= 0; i--) {

        Object segmentObject = selUcItemTreePaths[0].getSegment(i);

        if ((segmentObject instanceof FavUseCaseItemNode) &&
            (null != ((FavUseCaseItemNode) segmentObject).getFavUcItem())) {
          return ((FavUseCaseItemNode) segmentObject).getFavUcItem();

        }
      }
    }

    return null;
  }

  /**
   * Method to find the usecase favorite object from the child nodes of the selected uc item
   *
   * @param favUcNode
   * @return
   */
  private FavUseCaseItem exploreChildren(final FavUseCaseItemNode favUcNode) {
    SortedSet<FavUseCaseItemNode> childFavNodes = favUcNode.getChildFavNodes();
    FavUseCaseItem favUseCaseItem = null;

    for (FavUseCaseItemNode favUseCaseItemNode : childFavNodes) {
      if (favUseCaseItemNode.getFavUcItem() != null) {
        return favUseCaseItemNode.getFavUcItem();
      }

    }
    for (FavUseCaseItemNode favUseCaseItemNode : childFavNodes) {
      favUseCaseItem = exploreChildren(favUseCaseItemNode);
    }

    return favUseCaseItem;
  }

  /**
   * @param manager
   * @param actionSet
   * @param ucItem
   */
  private void createUcLinkContextMenu(final IMenuManager manager, final CommonActionSet actionSet,
      final IUseCaseItemClientBO ucItem, final FavUseCaseItem favUseCaseItem, final boolean isPrivateUCItem) {
    if (null != this.pidcVer) {
      actionSet.copytoClipBoardUCLink(manager, this.pidcVer.getId(),
          favUseCaseItem == null ? resolveModelObj(ucItem) : favUseCaseItem.getUseCaseFav(), ucItem.getID(),
          isPrivateUCItem);
      actionSet.sendUsecaseLinkInOutlook(manager,
          favUseCaseItem == null ? (IModel) resolveModelObj(ucItem) : favUseCaseItem.getUseCaseFav(),
          this.pidcVer.getId(), ucItem.getID(), isPrivateUCItem);
    }
  }

  /**
   * To resolve IModel type object from the objects in outline page
   *
   * @param obj
   * @return
   */
  private Object resolveModelObj(final Object obj) {
    Object ucObj = null;
    if (obj instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO grp = (UseCaseGroupClientBO) obj;
      ucObj = grp.getUseCaseGroup();
    }
    else if (obj instanceof UsecaseClientBO) {
      UsecaseClientBO uc = (UsecaseClientBO) obj;
      ucObj = uc.getUseCase();
    }
    else if (obj instanceof UseCaseSectionClientBO) {
      UseCaseSectionClientBO uc = (UseCaseSectionClientBO) obj;
      ucObj = uc.getUseCaseSection();
    }
    return ucObj;
  }

  /**
   * @param selection
   * @param privateUseCaseFlag
   * @return
   */
  private boolean isEnablePrivateUseCase(final IStructuredSelection selection) {
    UseCaseFavNodesMgr ucFavMgr = null;
    boolean privateUseCaseFlag = false;
    try {
      ucFavMgr = new UseCaseFavNodesMgr(new CurrentUserBO().getUser(), this.dataHandler.getUcDataHandler());
      this.dataHandler.getUcDataHandler().setUcFavMgr(ucFavMgr);
      privateUseCaseFlag =
          this.dataHandler.getUcDataHandler().isValidInsert((IUseCaseItemClientBO) selection.getFirstElement(), false);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    catch (IcdmException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      privateUseCaseFlag = false;
    }
    return privateUseCaseFlag;
  }

  /**
   * @param selection
   * @return
   */
  private boolean isEnableProjUseCase(final IStructuredSelection selection) {
    boolean projUseCaseFlag;
    UseCaseFavNodesMgr ucFavMgr = new UseCaseFavNodesMgr(this.pidcVer, this.dataHandler.getUcDataHandler());
    this.dataHandler.getUcDataHandler().setUcFavMgr(ucFavMgr);
    try {
      projUseCaseFlag =
          this.dataHandler.getUcDataHandler().isValidInsert((IUseCaseItemClientBO) selection.getFirstElement(), false);
    }
    catch (IcdmException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      projUseCaseFlag = false;
    }
    return projUseCaseFlag;
  }

  @Override
  public Control getControl() {
    return this.top;
  }

  @Override
  public void setFocus() {
    // To prevent
    // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
    // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
    // PlatformUI_getWorkbench()_getActiveWorkbenchWindow()_getShell()_setFocus()
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

    // set auto expand level
    this.viewer.setAutoExpandLevel(2);
    // Icdm-195

    OutlinePIDCTreeViewContentProvider outlinePIDCTreeViewContentProvider =
        new OutlinePIDCTreeViewContentProvider(this.pidcVer, this.dataHandler);
    this.viewer.setContentProvider(outlinePIDCTreeViewContentProvider);
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new OutlinePIDCTreeViewLabelProvider());

    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("ROOT"); // iCDM-296
    // ucitemid map will give uc item id to uc item object this will be used to filter usecase related attribute when
    // pidc editor is opened using link to uc for pidc versn

    setOutlineSelectionForUsecase(this.ucItemId);
    new CommonActionSet().addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);
  }

  /**
   * @param ucItemId
   */
  public void setOutlineSelectionForUsecase(final Long ucItemIdLcl) {
    ConcurrentMap<Long, ConcurrentMap<Long, FavUseCaseItemNode>> projUcFavNodeMap =
        this.dataHandler.getUcDataHandler().getProjUcFavNodeMap();
    ConcurrentMap<Long, FavUseCaseItemNode> nodeMap = projUcFavNodeMap.get(this.pidcVer.getPidcId());

    FavUseCaseItemNode favUcItemNodeToBeSelected = null;
    if (!nodeMap.isEmpty() && (null != ucItemIdLcl)) {
      favUcItemNodeToBeSelected = nodeMap.get(ucItemIdLcl);
    }
    IUseCaseItemClientBO useCaseItemClientBO =
        this.dataHandler.getUcDataHandler().getIUseCaseItemClientBOObject(ucItemIdLcl);

    if (null != useCaseItemClientBO) {
      // To fetch all the tree hirarichy objects of the selected ucItemId
      List<IUseCaseItemClientBO> usecaseObjectHirarichyList =
          this.dataHandler.getUcDataHandler().getUsecaseObjectHirarichyList(ucItemIdLcl);
      outlineItemSelectionForUsecases(nodeMap, favUcItemNodeToBeSelected, useCaseItemClientBO,
          usecaseObjectHirarichyList);
    }
  }

  /**
   * @param nodeMap
   * @param favUcItemNodeToBeSelected
   * @param useCaseItemClientBO
   * @param usecaseObjectHirarichyList
   */
  private void outlineItemSelectionForUsecases(final ConcurrentMap<Long, FavUseCaseItemNode> nodeMap,
      final FavUseCaseItemNode favUcItemNodeToBeSelected, final IUseCaseItemClientBO useCaseItemClientBO,
      final List<IUseCaseItemClientBO> usecaseObjectHirarichyList) {
    // sets selection in outline view
    if (this.projUseCase) {
      if (favUcItemNodeToBeSelected != null) {
        int i = 0;
        while (CommonUtils.isNotEqual(favUcItemNodeToBeSelected.getID(), usecaseObjectHirarichyList.get(i).getID())) {
          this.viewer.setExpandedState(nodeMap.get(usecaseObjectHirarichyList.get(i).getID()), true);
          i++;
        }
        this.viewer.setExpandedState(favUcItemNodeToBeSelected, true);
        this.viewer.setSelection(new StructuredSelection(favUcItemNodeToBeSelected), true);
        setItemToBeSelected(favUcItemNodeToBeSelected);
      }
      else {
        setSelectionInUcChildNodes(usecaseObjectHirarichyList, nodeMap, useCaseItemClientBO);
        setItemToBeSelected(useCaseItemClientBO);
      }
    }
    else {
      for (IUseCaseItemClientBO iUseCaseItemClientBO : usecaseObjectHirarichyList) {
        // Expanding the usecaseItem in the outline treeview

        this.viewer.setExpandedState(iUseCaseItemClientBO, true);
      }
      this.viewer.setSelection(new StructuredSelection(useCaseItemClientBO), true);
      setItemToBeSelected(useCaseItemClientBO);
    }
  }


  /**
   * @param usecaseObjectHirarichyList
   * @param nodeMap
   * @param useCaseItemClientBO
   */
  private void setSelectionInUcChildNodes(final List<IUseCaseItemClientBO> usecaseObjectHirarichyList,
      final ConcurrentMap<Long, FavUseCaseItemNode> nodeMap, final IUseCaseItemClientBO useCaseItemClientBO) {
    int i = 0;
    for (IUseCaseItemClientBO iUseCaseItemClientBO : usecaseObjectHirarichyList) {
      for (FavUseCaseItemNode favNode : nodeMap.values()) {
        if (favNode.getID().equals(iUseCaseItemClientBO.getID())) {
          this.viewer.setExpandedState(favNode, true);
          i++;
        }

      }
    }
    while (CommonUtils.isNotEqual(useCaseItemClientBO.getID(), usecaseObjectHirarichyList.get(i).getID())) {
      this.viewer.setExpandedState(usecaseObjectHirarichyList.get(i), true);
      i++;
    }
    this.viewer.setExpandedState(useCaseItemClientBO, true);
    this.viewer.setSelection(new StructuredSelection(useCaseItemClientBO));
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
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshTreeViewer(final boolean deselectAll) {
    if (PIDCOutlinePage.this.viewer != null) {
      PIDCOutlinePage.this.viewer.refresh();
      if (deselectAll) {
        // deselects all elements in the tree
        PIDCOutlinePage.this.viewer.getTree().deselectAll();
      }
    }

  }


  /**
   * @return the viewer
   */
  public final TreeViewer getViewer() {
    return this.viewer;
  }

  /**
   * @param itemToBeSelected item to be selected in outline view
   */
  public void setItemToBeSelected(final Object itemToBeSelected) {
    this.itemToBeSelected = itemToBeSelected;

  }


  /**
   * @return the selUcItem
   */
  public final Object getItemToBeSelected() {
    return this.itemToBeSelected;
  }

}

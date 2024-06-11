package com.bosch.caltool.usecase.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ISetSelectionTarget;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGrpParentBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseRootNode;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseTreeDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkSelectionProvider;
import com.bosch.caltool.icdm.common.ui.listeners.ILinkedWithEditorView;
import com.bosch.caltool.icdm.common.ui.listeners.LinkWithEditorPartListener;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseGroup;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.usecase.ui.Activator;
import com.bosch.caltool.usecase.ui.actions.UseCaseActionSet;
import com.bosch.caltool.usecase.ui.actions.UseCaseTreeViewActionSet;
import com.bosch.caltool.usecase.ui.dialogs.EditUseCaseDialog;
import com.bosch.caltool.usecase.ui.dialogs.EditUseCaseGroupDialog;
import com.bosch.caltool.usecase.ui.views.providers.UseCaseTreeViewContentProvider;
import com.bosch.caltool.usecase.ui.views.providers.UseCaseTreeViewLabelProvider;


/**
 * @author adn1cob
 */
public class UseCaseTreeViewPart extends AbstractViewPart implements ILinkedWithEditorView, ISetSelectionTarget {

  /**
   * UseCaseRootNode
   */
  private UseCaseRootNode useCaseRootNode;
  /**
   * ID - UseCaseTreeViewPart
   */
  public static final String UC_TREE_ID = "com.bosch.caltool.usecase.ui.views.UseCaseTreeViewPartOld";

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * Composite
   */
  private Composite composite;
  /**
   * Action instance for TreeViewer
   */
  private TreeViewer viewer;

  /**
   * UsecaseClientBO
   */
  protected UsecaseClientBO selectedUseCase;


  private IAction showUpdateAction;

  private IAction showDeleteAction;

  // ICDM-796
  /*
   * CommandState instance
   */
  CommandState expReportService = new CommandState();

  /**
   * selected uc
   */
  protected UseCaseGroupClientBO selectedUcGroup;
  /**
   * selected uc node
   */
  protected UseCaseRootNode selectedUCNode;

  /**
   * constant for Ok result in Dialog open
   */
  public static final int OK_STATUS = 0;

  private final UseCaseTreeDataHandler ucTreeDataHandler = new UseCaseTreeDataHandler();
  /**
   * AttrGroupModel
   */

  // initialize from preference from preference store
  private boolean isLinkEnabled = isLinkWithEditorPrefEnabled();
  
 private  UsecaseEditorModel useCaseEditorDataInput;


  @Override
  public void createPartControl(final Composite parent) {
    addHelpAction();
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    // Build the UI
    createComposite();
    hookContextMenu();
    // iCDM-530
    setTitleToolTip("Use Case explorer");
    // add listeners
    IPartListener2 linkWithEditorPartListener = new LinkWithEditorPartListener(this);
    // create action for linking with editor
    Action linkWithEditorAction = new Action("Link with Editor", IAction.AS_CHECK_BOX) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        toggleLinking(isChecked());
      }
    };
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.LINK_EDITOR_16X16);
    linkWithEditorAction.setImageDescriptor(imageDesc);
    getViewSite().getActionBars().getToolBarManager().add(linkWithEditorAction);
    // set previous state
    linkWithEditorAction.setChecked(this.isLinkEnabled);
    getSite().getPage().addPartListener(linkWithEditorPartListener);

    getSite().setSelectionProvider(this.viewer);
  }

  /**
   * Build the context menu for tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(this::fillContextMenu);
    
    final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);

    getSite().registerContextMenu(menuMgr, this.viewer);

  }


  /**
   * Fills the context menu
   *
   * @param manager
   */
  private void fillContextMenu(final IMenuManager manager) {

    final UseCaseTreeViewActionSet actionSet = new UseCaseTreeViewActionSet();
    final Separator separator = new Separator();

    // Get the current selection and add actions to it
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null)) {
      if (selection.getFirstElement() instanceof UseCaseRootNode) {
        actionSet.addUseCaseRootMenu(manager);
        manager.add(separator);
        SortedSet<IUseCaseItemClientBO> useCaseItems = new TreeSet<>();
        useCaseItems.addAll(((UseCaseRootNode) selection.getFirstElement()).getUseCaseGroups(false));
        actionSet.addExportMenu(manager, useCaseItems);

      }
      if (selection.getFirstElement() instanceof UseCaseGroupClientBO) {
        actionSet.addUseCaseGroupMenu(manager, (UseCaseGroupClientBO) selection.getFirstElement());
        manager.add(separator);
        // ICDM-783
        SortedSet<IUseCaseItemClientBO> useCaseItems = new TreeSet<>();
        useCaseItems.add((UseCaseGroupClientBO) selection.getFirstElement());
        actionSet.addExportMenu(manager, useCaseItems);
      }
      // ICDM-783
      if (selection.getFirstElement() instanceof UsecaseClientBO) {
        SortedSet<IUseCaseItemClientBO> useCaseItems = new TreeSet<>();
        useCaseItems.add((UsecaseClientBO) selection.getFirstElement());
        actionSet.addExportMenu(manager, useCaseItems);
      }

    }
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
    final FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(2);


    this.useCaseRootNode = new UseCaseRootNode(this.ucTreeDataHandler.getUcTreeGrpModel());
    // Set Content provider for the tree
    this.viewer.setContentProvider(new UseCaseTreeViewContentProvider(this.useCaseRootNode));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new UseCaseTreeViewLabelProvider());
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("");
    // iCDM-350
    ColumnViewerToolTipSupport.enableFor(this.viewer);
    // moved to a new method icdm-358
    createSelectionListenerForTreeViewer();
    CommonActionSet actionSet = new CommonActionSet();
    actionSet.addCommonTreeActions(this.viewer, getViewSite().getActionBars().getToolBarManager(), 1);

    final IDoubleClickListener useCaseDoubleClickListner =  event -> {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selectedNode = selection.getFirstElement();

        if (selectedNode instanceof UsecaseClientBO) {
          UsecaseClientBO selUseCase = (UsecaseClientBO) selectedNode;
          // ICDM-343
          showUseCaseDetailsView();
          UseCaseTreeViewActionSet useCaseActionSet = new UseCaseTreeViewActionSet();
          try {
            useCaseEditorDataInput = new UseCaseServiceClient().getUseCaseEditorData(selUseCase.getId());
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          }
          useCaseActionSet.openUseCaseEditor(selUseCase, useCaseEditorDataInput);
        }
      
    };
    this.viewer.addDoubleClickListener(useCaseDoubleClickListner);
    // iCDM-293
    getSite().setSelectionProvider(UseCaseTreeViewPart.this.viewer); // ICDM 293

    // ICDM-705
    addKeyListenerToViewer();

    createToolBar();

  }

  /**
   * ICDM-705 This method adds key listner on Usecase treeviewer. It checks for 'F2','DEL','CTRL+C','CTRL+V' keys
   * pressed on Usecase Item.
   */
  private void addKeyListenerToViewer() {
    this.viewer.getTree().addKeyListener(new KeyAdapter() {

      @Override
      public void keyPressed(final KeyEvent event) {

        final Object selected =
            ((IStructuredSelection) (UseCaseTreeViewPart.this.viewer.getSelection())).getFirstElement();
        if (((selected instanceof UsecaseClientBO) && ((UsecaseClientBO) selected).isModifiable()) ||
            ((selected instanceof UseCaseGroupClientBO) && ((UseCaseGroupClientBO) selected).isModifiable())) {
          callUpdateOrDeleteCommand(event);
        }
        else {
          if ((event.keyCode == CommonUIConstants.KEY_RENAME) || (event.keyCode == CommonUIConstants.KEY_DELETE)) {
            // if the user does not have access to delete or rename
            CDMLogger.getInstance().warnDialog("Insufficient privileges to do this operation!", Activator.PLUGIN_ID);
          }
        }
      }

      /**
       * @param event
       */
      private void callUpdateOrDeleteCommand(final KeyEvent event) {
        // if the seletected object is usecase or usecase section & if it is modifiable
        if (CommonUIConstants.KEY_DELETE == event.keyCode) {
          // delete key pressed
          deleteUCItem();
        }
        if (CommonUIConstants.KEY_RENAME == event.keyCode) {
          // rename key pressed
          updateUCItem();
        }
      }
    });

  }

  /**
   * icdm-358 Method moved
   */
  private void createSelectionListenerForTreeViewer() {
    this.viewer.addSelectionChangedListener( event-> {
        // ICDM-796
        // ICDM-865
        UseCaseTreeViewPart.this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();

        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        final Object selected = selection.getFirstElement();
        // added conditions for delete
        // ICDM-796
        if (selected instanceof UsecaseClientBO) {
          UseCaseTreeViewPart.this.selectedUCNode = null;
          UseCaseTreeViewPart.this.selectedUcGroup = null;
          UseCaseTreeViewPart.this.selectedUseCase = (UsecaseClientBO) selected;
          // Changes made to Control the Update and delete buttons based on isModifiable() Method of UC Item
          controlButtons(UseCaseTreeViewPart.this.selectedUseCase);
          UseCaseTreeViewPart.this.expReportService.setExportService(true);
        }
        else if (selected instanceof UseCaseGroupClientBO) {
          UseCaseTreeViewPart.this.selectedUseCase = null;
          UseCaseTreeViewPart.this.selectedUCNode = null;
          UseCaseTreeViewPart.this.selectedUcGroup = (UseCaseGroupClientBO) selected;
          // Changes made to Control the Update and delete buttons based on isModifiable() Method of UC Item
          controlButtons(UseCaseTreeViewPart.this.selectedUcGroup);
          UseCaseTreeViewPart.this.expReportService.setExportService(true);

        }
        else if (selected instanceof UseCaseRootNode) {
          UseCaseTreeViewPart.this.selectedUseCase = null;
          UseCaseTreeViewPart.this.selectedUcGroup = null;
          UseCaseTreeViewPart.this.selectedUCNode = (UseCaseRootNode) selected;
          UseCaseTreeViewPart.this.showUpdateAction.setEnabled(false);
          UseCaseTreeViewPart.this.showDeleteAction.setEnabled(false);
          UseCaseTreeViewPart.this.expReportService.setExportService(true);
        }

    });
  }

  private void createToolBar() {

    final IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    this.showUpdateAction = createUpdateAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.showUpdateAction);
    this.showUpdateAction.setEnabled(false);

    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    this.showDeleteAction = createDeleteAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.showDeleteAction);
    this.showDeleteAction.setEnabled(false);

    new CommonActionSet().addCommonTreeActions(this.viewer, getViewSite().getActionBars().getToolBarManager(), 2);

  }

  /**
   * @return
   */
  public IAction createDeleteAction() {

     Action deleteAction = new Action("Delete") {

      @Override
      public void run() {
        deleteUCItem();
      }
    };
    // Set image for edit action
    deleteAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    return deleteAction;
  }

  /**
   * Delete the UC item
   */
  private void deleteUCItem() {
    // ICDM-358 Changes
    final IStructuredSelection selection = (IStructuredSelection) UseCaseTreeViewPart.this.viewer.getSelection();
    final Object selectedObject = selection.getFirstElement();

    // Not implemented
    if (selectedObject instanceof UseCaseGroupClientBO) {
      UseCaseGroupServiceClient ucGrpServiceClient = new UseCaseGroupServiceClient();
      UseCaseGroupClientBO ucgClientBO = (UseCaseGroupClientBO) selectedObject;
      UseCaseGroup cloneUcGrp = ucgClientBO.getUseCaseGroup().clone();
      cloneUcGrp.setDeleted(true);
      try {
        ucGrpServiceClient.update(cloneUcGrp);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

    }

    else if (selectedObject instanceof UsecaseClientBO) {
      UseCaseServiceClient ucServiceClient = new UseCaseServiceClient();
      UsecaseClientBO ucClientBO = (UsecaseClientBO) selectedObject;
      UseCase cloneUsecase = ucClientBO.getUseCase().clone();
      cloneUsecase.setDeleted(true);
      try {
        ucServiceClient.update(cloneUsecase);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

    }
  }

  /**
   * @param actionSet actionSet icdm-358
   * @param ucGroup ucGroup
   */
  protected void refreshUseCaseForGroupDel(final UseCaseActionSet actionSet, final UseCaseGroupClientBO ucGroup) {

    // // When a Group Refersh all elements in Use case Details
    final SortedSet<UsecaseClientBO> useCaseSet = ucGroup.getUseCaseSet(true);
    if (!useCaseSet.isEmpty()) {
      for (UsecaseClientBO uc : useCaseSet) {
        actionSet.refreshUsecaseDetails(uc, true);
      }
    }
    else {
      // Call this method recursively
      SortedSet<UseCaseGroupClientBO> childGrps = ucGroup.getChildGroupSet(true);
      if (!childGrps.isEmpty()) {
        for (UseCaseGroupClientBO grp : childGrps) {
          refreshUseCaseForGroupDel(actionSet, grp);
        }
      }
    }

  }

  /**
   * @return IAction
   */
  public IAction createUpdateAction() {

    Action updateAction = new Action("Edit") {

      @Override
      public void run() {
        updateUCItem();
      }

    };
    // Set image for edit action
   updateAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    return updateAction;
  }

  /**
   * ICDM-705 Renaming of UCItems
   */
  private void updateUCItem() {
    // ICDM-358 Changes
    final IStructuredSelection selection = (IStructuredSelection) UseCaseTreeViewPart.this.viewer.getSelection();
    final Object selectedObject = selection.getFirstElement();
    if (selectedObject instanceof UseCaseGroupClientBO) {
      final EditUseCaseGroupDialog editUcGrpDlg =
          new EditUseCaseGroupDialog(Display.getDefault().getActiveShell(), (UseCaseGroupClientBO) selectedObject);
      editUcGrpDlg.open();

    }
    else if (selectedObject instanceof UsecaseClientBO) {
      final EditUseCaseDialog editUcDlg = new EditUseCaseDialog(Display.getDefault().getActiveShell(),
          (UsecaseClientBO) selectedObject, UseCaseTreeViewPart.this.viewer);
      editUcDlg.open();

    }

    // ICDM-139 Changes End
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
   *
   */
  public void showUseCaseDetailsView() {
    try {
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .showView("com.bosch.caltool.usecase.ui.views.UseCaseDetailsViewPart");
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();

  }


  /**
   * @return the selectedUcGroup
   */
  public UseCaseGroupClientBO getSelectedUcGroup() {
    return this.selectedUcGroup;
  }


  /**
   * @return the selectedUCNode
   */
  public UseCaseRootNode getSelectedUCNode() {
    return this.selectedUCNode;
  }


  /**
   * @return the selectedUseCase
   */
  public UsecaseClientBO getSelectedUseCase() {
    return this.selectedUseCase;
  }

  /**
   * New method to Control the Update and the delete button.
   *
   * @param item
   */
  private void controlButtons(final IUseCaseItemClientBO item) {
    // If the Use case Item is modifiable then Enable the Delete and the Update Button.
    UseCaseTreeViewPart.this.showDeleteAction.setEnabled(item.isModifiable());
    UseCaseTreeViewPart.this.showUpdateAction.setEnabled(item.isModifiable());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.ucTreeDataHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    // Refresh the tree
    this.useCaseRootNode = new UseCaseRootNode(this.ucTreeDataHandler.getUcTreeGrpModel());
    this.viewer.setContentProvider(new UseCaseTreeViewContentProvider(this.useCaseRootNode));
    this.viewer.setLabelProvider(new UseCaseTreeViewLabelProvider());
    this.viewer.setInput("");

    // TODO selection is lost
    // set expanded state
    // get all expanded elements
    Object[] elements = this.viewer.getExpandedElements();
    TreePath[] treePaths = this.viewer.getExpandedTreePaths();

    // refresh tree viewer
    this.viewer.refresh();

    // set all expanded elements
    this.viewer.setExpandedElements(elements);
    this.viewer.setExpandedTreePaths(treePaths);
    // enable or disable buttons based on the selection
    enableDisableButtons();


  }

  /**
   *
   */
  private void enableDisableButtons() {
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    final Object selected = selection.getFirstElement();
    if (selected instanceof UsecaseClientBO) {
      UsecaseClientBO ucItem = (UsecaseClientBO) selected;
      controlButtons(ucItem);
    }
    else if (selected instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO ucgClientBO = (UseCaseGroupClientBO) selected;
      controlButtons(ucgClientBO);
    }
    else if (selected instanceof UseCaseRootNode) {
      UseCaseTreeViewPart.this.showUpdateAction.setEnabled(false);
      UseCaseTreeViewPart.this.showDeleteAction.setEnabled(false);
      UseCaseTreeViewPart.this.expReportService.setExportService(true);
    }
  }

  /**
   * @return the viewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
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
      if (CommonUtils.isNotNull(editorInputLinkedObj) && (editorInputLinkedObj instanceof UsecaseClientBO)) {
        UsecaseClientBO linkedNode = (UsecaseClientBO) editorInputLinkedObj;

        // To fetch all the tree hirarichy objects of the selected ucItemId
        List<IUseCaseItemClientBO> usecaseObjectHirarichyList = new ArrayList<>();
        new UseCaseGrpParentBO().getUseCaseGroupClientBORecursive(
            linkedNode.getUseCaseGroupClientBO().getUseCaseGroup(), usecaseObjectHirarichyList,
            this.ucTreeDataHandler.getUcTreeGrpModel());
        usecaseObjectHirarichyList.add(linkedNode);


        for (IUseCaseItemClientBO iUseCaseItemClientBO : usecaseObjectHirarichyList) {
          // Expanding the usecaseItem in the usecase explorer tree view
          this.viewer.setExpandedState(iUseCaseItemClientBO, true);
        }

        StructuredSelection structuredSelection = new StructuredSelection(linkedNode);
        this.viewer.setSelection(structuredSelection, true);

      }
    }
  }


  /**
   * @return the linkingActive
   */
  public boolean isLinkingEnabled() {
    return this.isLinkEnabled;
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
   * {@inheritDoc}
   */
  @Override
  public void selectReveal(final ISelection arg0) {
    // TODO Auto-generated method stub

  }

}

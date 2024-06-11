/**
 *
 */
package com.bosch.caltool.apic.ui.views;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.AddAttributeGroupDialog;
import com.bosch.caltool.apic.ui.dialogs.AddAttributeSuperGroupDialog;
import com.bosch.caltool.apic.ui.dialogs.EditAttributeGroupDialog;
import com.bosch.caltool.apic.ui.dialogs.EditAttributeSuperGroupDialog;
import com.bosch.caltool.apic.ui.editors.AttributesEditor;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewContentProvider;
import com.bosch.caltool.apic.ui.views.providers.OutlinePIDCTreeViewLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.AttrGroupClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttrRootNode;
import com.bosch.caltool.icdm.client.bo.apic.AttrSuperGroupClientBO;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.uc.FavUseCaseItemNode;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.OutLineViewDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UserFavUcRootNode;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author adn1cob
 */
public class AttributesOutlinePage extends AbstractPage implements ISelectionListener {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  private Composite composite;
  private TreeViewer viewer;
  private IAction addAction;
  private IAction updateAction;
  private final OutLineViewDataHandler dataHandler;


  /**
   * @param dataHandler OutLineViewDataHandler
   */
  public AttributesOutlinePage(final OutLineViewDataHandler dataHandler) {
    this.dataHandler = dataHandler;
  }

  /**
   * To notify current selection to Dialogs
   */
  private static Object currentSelection;


  @Override
  public void createControl(final Composite parent) {
    addHelpAction();
    // Configure standard layout
    getSite().getPage().addSelectionListener(this);
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    // Build the UI
    createComposite();
    hookContextMenu();
    addDragDropListeners();// ICDM-1029

    // add selection provider
    getSite().setSelectionProvider(this.viewer);
  }

  // ICDM-766
  /**
   * Build the context menu for tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(AttributesOutlinePage.this::fillContextMenu);
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
      if (selection.getFirstElement() instanceof IUseCaseItemClientBO) {
        if (selection.getFirstElement() instanceof UseCaseSectionClientBO) {
          actionSet.addLinkAction(manager, ((UseCaseSectionClientBO) selection.getFirstElement()).getLinks());
        }
        else if (selection.getFirstElement() instanceof UsecaseClientBO) {
          // add 'Open' use case action for common use cases
          actionSet.openUseCaseAction(manager, (UsecaseClientBO) selection.getFirstElement());
          manager.add(new Separator());
          actionSet.addLinkAction(manager, ((UsecaseClientBO) selection.getFirstElement()).getLinks());
        }
        actionSet.addMovToUserFav(manager, (IUseCaseItemClientBO) selection.getFirstElement(), this.viewer,
            this.dataHandler.getUcDataHandler(), true);
      } // ICDM-930
      else if (selection.getFirstElement() instanceof AttrGroup) {
        actionSet.addLinkAction(manager, (new AttrGroupClientBO((AttrGroup) selection.getFirstElement())).getLinks());
      } // ICDM-929
      else if (selection.getFirstElement() instanceof AttrSuperGroup) {
        actionSet.addLinkAction(manager,
            (new AttrSuperGroupClientBO((AttrSuperGroup) selection.getFirstElement())).getLinks());
      } // ICDM-1029
      else if (selection.getFirstElement() instanceof FavUseCaseItemNode) {
        FavUseCaseItemNode favUcNode = (FavUseCaseItemNode) selection.getFirstElement();
        if (favUcNode.getUseCaseItem() instanceof UsecaseClientBO) {
          // add 'Open' use case action for private use cases
          actionSet.openUseCaseAction(manager, (UsecaseClientBO) favUcNode.getUseCaseItem());
          manager.add(new Separator());
          actionSet.addLinkAction(manager, ((UsecaseClientBO) (favUcNode.getUseCaseItem())).getLinks());
        }
        else if (favUcNode.getUseCaseItem() instanceof UseCaseSectionClientBO) {
          actionSet.addLinkAction(manager, ((UseCaseSectionClientBO) (favUcNode.getUseCaseItem())).getLinks());
        }
        if (favUcNode.getFavUcItem() != null) {
          actionSet.addDelFavUcAction(manager, favUcNode, this.viewer, null, null);
        }
      }
    }

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
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {

    final GridData gridData = new GridData();
    // Apply grid data styles
    applyGridDataStyles(gridData);

    final PatternFilter filter = new PatternFilter();
    final FilteredTree tree = new FilteredTree(this.composite, SWT.BORDER, filter, true);
    // Get viewer and set styled layout for tree
    this.viewer = tree.getViewer();
    this.viewer.getTree().setLayoutData(gridData);
    // set auto expand level
    this.viewer.setAutoExpandLevel(2);
    // ICDM 542
    ColumnViewerToolTipSupport.enableFor(this.viewer, ToolTip.NO_RECREATE);

    // Icdm-195
    // Set Content provider for the tree
    this.viewer.setContentProvider(new OutlinePIDCTreeViewContentProvider(null, this.dataHandler));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new OutlinePIDCTreeViewLabelProvider());
    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    this.viewer.setInput("ROOT");


    createToolBar();
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

  private void createToolBar() {

    IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    this.addAction = createAddAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.addAction);
    this.addAction.setEnabled(false);

    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    // ICDM-139 Changes Start
    this.updateAction = createUpdateAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.updateAction);
    this.updateAction.setEnabled(false);

    new CommonActionSet().addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);

    // ICDM-139 Changes End
  }


  /**
   * Opens the Daiolg Box for Addition of Super Group/Group
   */
  private IAction createAddAction() {

    Action retAction = new Action("Add") {

      @Override
      public void run() {

        IStructuredSelection selection = (IStructuredSelection) AttributesOutlinePage.this.viewer.getSelection();
        Object selectedObject = selection.getFirstElement();
        if (selectedObject instanceof AttrRootNode) {
          AddAttributeSuperGroupDialog addAttrDialog =
              new AddAttributeSuperGroupDialog(Display.getDefault().getActiveShell());
          addAttrDialog.open();
        }
        else if (selectedObject instanceof AttrSuperGroup) {
          AddAttributeGroupDialog addAttrDialog = new AddAttributeGroupDialog(Display.getDefault().getActiveShell(),
              AttributesOutlinePage.this, (AttrSuperGroup) selectedObject);
          addAttrDialog.open();
        }

      }
    };
    // Set image for add action
    retAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    return retAction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.dataHandler;
  }

  /**
   * @Opens the Dialog Box for Editing of Super Group/Group
   */
  private IAction createUpdateAction() {

    Action retAction = new Action("Edit") {

      @Override
      public void run() {


        // ICDM-139 Changes Start
        IStructuredSelection selection = (IStructuredSelection) AttributesOutlinePage.this.viewer.getSelection();
        Object selectedObject = selection.getFirstElement();
        if (selectedObject instanceof AttrSuperGroup) {
          EditAttributeSuperGroupDialog editSprGrpDlg = new EditAttributeSuperGroupDialog(
              Display.getDefault().getActiveShell(), AttributesOutlinePage.this, (AttrSuperGroup) selectedObject);
          editSprGrpDlg.open();
        }
        else if (selectedObject instanceof AttrGroup) {
          EditAttributeGroupDialog editAttrGrpDialog = new EditAttributeGroupDialog(
              Display.getDefault().getActiveShell(), AttributesOutlinePage.this, (AttrGroup) selectedObject);
          editAttrGrpDialog.open();
        } // ICDM-139 Changes End
      }
    };
    // Set image for edit action
    retAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    return retAction;
  }

  /**
   * @Method Listener for Outline Page Selection icdm-139
   */
  @Override
  public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {

    IEditorPart editorPart = getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
    if ((part instanceof OutlineViewPart) && (editorPart instanceof AttributesEditor) && (selection != null)) {

      IStructuredSelection istructSelection = (IStructuredSelection) selection;
      Object selectedObject = istructSelection.getFirstElement();
      showAndHideButtons(selectedObject);

    }

  }

  /**
   * @Method to Hide and show the Add /Edit Super Group Buttons ICDM-139
   */
  private void showAndHideButtons(final Object selectedObject) {
    // Icdm-194
    try {
      if ((selectedObject != null) && (selectedObject instanceof AttrRootNode) &&
          (new CurrentUserBO().hasApicWriteAccess())) {
        this.addAction.setEnabled(true);
        this.addAction.setToolTipText("Add Super Group");
        this.updateAction.setEnabled(false);
        currentSelection = null;
      }
      else if ((selectedObject != null) && (selectedObject instanceof AttrSuperGroup) &&
          new AttrSuperGroupClientBO((AttrSuperGroup) selectedObject).isModifiable()) {
        this.addAction.setEnabled(true);
        this.addAction.setToolTipText("Add Group");
        this.updateAction.setEnabled(true);
        this.updateAction.setToolTipText("Edit Super Group");
        currentSelection = selectedObject;
      }
      else if ((selectedObject != null) && (selectedObject instanceof AttrGroup) &&
          new AttrGroupClientBO((AttrGroup) selectedObject).isModifiable()) {
        this.addAction.setEnabled(false);
        this.updateAction.setEnabled(true);
        this.updateAction.setToolTipText("Edit Group");
        currentSelection = selectedObject;
      }
      else {
        this.addAction.setEnabled(false);
        this.updateAction.setEnabled(false);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);
    }

  }

  @Override
  public void dispose() {
    currentSelection = null;
    super.dispose();
  }

  /**
   * @return Viewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
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
   * ICDM-1029 Adds drag & drop listener to the tree view control
   */
  private void addDragDropListeners() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    // add drag listener
    this.viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes, new CustomDragListener(this.viewer));

    // add drop listener
    final DropTarget target =
        new DropTarget(this.viewer.getControl(), DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

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
      public void dragOver(final DropTargetEvent event) {
        if (event.item.getData() instanceof UserFavUcRootNode) {
          event.detail = DND.DROP_DEFAULT;
        }
        else {
          event.detail = DND.DROP_NONE;
        }
        // this line is to enable automatic scroll in the tree viewer during drag
        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
      }

      @Override
      public void drop(final DropTargetEvent event) {
        if ((event.data == null) || !(event.item.getData() instanceof UserFavUcRootNode)) {
          event.detail = DND.ERROR_CANNOT_INIT_DROP;
          return;
        }
        final Object dragData = event.data;
        final IStructuredSelection strucSelec = (StructuredSelection) dragData;
        if (strucSelec.getFirstElement() instanceof IUseCaseItemClientBO) {
          if (event.item.getData() instanceof UserFavUcRootNode) {
            final CommonActionSet actionSet = new CommonActionSet();
            try {
              actionSet.moveToUserFav((IUseCaseItemClientBO) strucSelec.getFirstElement(),
                  AttributesOutlinePage.this.viewer, AttributesOutlinePage.this.dataHandler.getUcDataHandler());
            }
            catch (IcdmException exp) {
              CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
            }
          }
        }
      }


    });
  }


  /**
   * @return the currentSelection
   */
  public static Object getCurrentSelection() {
    return currentSelection;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    refreshTreeViewer(false);
  }

}

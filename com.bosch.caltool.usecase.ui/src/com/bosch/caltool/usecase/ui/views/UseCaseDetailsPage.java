/**
 *
 */
package com.bosch.caltool.usecase.ui.views;

import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.services.ISourceProviderService;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseDetailsDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.uc.UseCase;
import com.bosch.caltool.icdm.model.uc.UseCaseSection;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseSectionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.uc.UseCaseServiceClient;
import com.bosch.caltool.usecase.ui.Activator;
import com.bosch.caltool.usecase.ui.actions.UseCaseActionSet;
import com.bosch.caltool.usecase.ui.actions.UseCaseTreeViewActionSet;
import com.bosch.caltool.usecase.ui.dialogs.EditUseCaseDialog;
import com.bosch.caltool.usecase.ui.dialogs.EditUseCaseSectionDialog;
import com.bosch.caltool.usecase.ui.views.listeners.UseCaseListener;
import com.bosch.caltool.usecase.ui.views.providers.UseCaseDetailsViewContentProvider;
import com.bosch.caltool.usecase.ui.views.providers.UseCaseDetailsViewLabelProvider;

/**
 * @author adn1cob
 */
public class UseCaseDetailsPage extends AbstractPage implements UseCaseListener {

  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * composite
   */
  private Composite composite;
  /**
   * TreeViewer instance
   */
  private TreeViewer viewer;

  private final UseCaseDetailsViewPart detailsview;

  private IAction updateAction;

  // ICDM-796
  /*
   * CommandState instance
   */
  CommandState expReportService = new CommandState();

  /**
   * @return the showUpdateAction icdm-358
   */
  public IAction getUpdateAction() {
    return this.updateAction;
  }

  private IAction deleteAction;

  /**
   * usecase details handler
   */
  private final UseCaseDetailsDataHandler ucDetailsHandler = new UseCaseDetailsDataHandler();

  /**
   * @return the showDeleteAction icdm-358
   */
  public IAction getDeleteAction() {
    return this.deleteAction;
  }


  /**
   * @param editorUseCase - editor instance
   * @param useCaseDetailsViewPart - usecasedetailsview instance
   * @param usecaseEditorModel UsecaseEditorModel
   */
  public UseCaseDetailsPage(final UsecaseClientBO editorUseCase, final UseCaseDetailsViewPart useCaseDetailsViewPart,
      final UsecaseEditorModel usecaseEditorModel) {

    this.ucDetailsHandler.setUcEditorModel(usecaseEditorModel);
    this.ucDetailsHandler.setUseCase(editorUseCase);

    this.detailsview = useCaseDetailsViewPart;

    // Pass Use case Obj
    UseCaseActionSet.registerDetailsListener(editorUseCase, this);
  }

  @Override
  public void createControl(final Composite parent) {
    // Configure standard layout
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    addHelpAction();
    // Build the UI
    createComposite();

    hookContextMenu();
    // add selection provider
    getSite().setSelectionProvider(this.viewer);
  }


  /**
   * Build the context menu for tree
   */
  private void hookContextMenu() {
    // Create the menu manager and fill context menu
    final MenuManager menuMgr = new MenuManager("popupmenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(UseCaseDetailsPage.this::fillContextMenu);
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

    final UseCaseTreeViewActionSet actionSet = new UseCaseTreeViewActionSet();
    final CommonActionSet commonActionSet = new CommonActionSet();

    // Get the current selection and add actions to it
    final IStructuredSelection selection = (IStructuredSelection) this.viewer.getSelection();
    if ((selection != null) && (selection.getFirstElement() != null) && (selection.size() == 1)) {
      if (selection.getFirstElement() instanceof UsecaseClientBO) {
        UsecaseClientBO useCase = (UsecaseClientBO) selection.getFirstElement();
        actionSet.addUseCaseMenu(manager, useCase, this.ucDetailsHandler.getUcEditorModel());
      }
      else if (selection.getFirstElement() instanceof UseCaseSectionClientBO) {
        UseCaseSectionClientBO useCaseSection = (UseCaseSectionClientBO) selection.getFirstElement();
        actionSet.addUseCaseSectionMenu(manager, useCaseSection, this.ucDetailsHandler.getUcEditorModel(),
            this.ucDetailsHandler.getUseCase());
        manager.add(new Separator());
        commonActionSet.addLinkAction(manager, useCaseSection.getLinks());
      }

    }
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
    this.viewer.setAutoExpandLevel(2);

    // Set Content provider for the tree
    this.viewer.setContentProvider(new UseCaseDetailsViewContentProvider(this.ucDetailsHandler.getUseCase()));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new UseCaseDetailsViewLabelProvider());
    // create root node in Content provider
    // input the UseCase id, to display the root node
    this.viewer.setInput(this.ucDetailsHandler.getUseCase().getUseCase().getId());
    // iCDM-350
    ColumnViewerToolTipSupport.enableFor(this.viewer);

    createToolBar();
    this.viewer.addSelectionChangedListener(event -> {
      // Changes For icdm-358
      // ICDM-796
      final ISourceProviderService spService = getService();
      if (spService != null) {
        UseCaseDetailsPage.this.expReportService =
            (CommandState) spService.getSourceProvider(CommandState.VAR_EXPORT_STATUS);
      }
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      enableButtonsBasedOnSelection(selection);
    });
    //
    // // ICDM-705
    addKeyListenerToViewer();
  }


  /**
   * @param selection
   */
  private void enableButtonsBasedOnSelection(final IStructuredSelection selection) {
    if (selection.size() > 1) {
      disableActions();
      return;
    }
    final Object selected = selection.getFirstElement();
    if (selected instanceof UseCaseSectionClientBO) {
      UseCaseDetailsPage.this.expReportService.setExportService(false);
      if (((UseCaseSectionClientBO) selected).isModifiable()) {
        UseCaseDetailsPage.this.updateAction.setEnabled(true);
        UseCaseDetailsPage.this.deleteAction.setEnabled(true);
      }
    }
    // icdm-358 Using methods in model
    else if (selected instanceof UsecaseClientBO) {
      UseCaseDetailsPage.this.expReportService.setExportService(true);
      if (((UsecaseClientBO) selected).isModifiable()) {
        UseCaseDetailsPage.this.deleteAction.setEnabled(true);
        UseCaseDetailsPage.this.updateAction.setEnabled(true);
      }
    }
    else {
      disableActions();
    }
  }


  /**
   *
   */
  private void disableActions() {
    UseCaseDetailsPage.this.expReportService.setExportService(false);
    UseCaseDetailsPage.this.updateAction.setEnabled(false);
    UseCaseDetailsPage.this.deleteAction.setEnabled(false);
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
            ((IStructuredSelection) (UseCaseDetailsPage.this.viewer.getSelection())).getFirstElement();
        if (((selected instanceof UsecaseClientBO) && ((UsecaseClientBO) selected).isModifiable()) ||
            ((selected instanceof UseCaseSectionClientBO) && ((UseCaseSectionClientBO) selected).isModifiable())) {
          deleteRenameUCNode(event);
        }
        else {
          if ((event.keyCode == CommonUIConstants.KEY_RENAME) || (event.keyCode == CommonUIConstants.KEY_DELETE)) {
            // if the user does not have access to delete or rename
            CDMLogger.getInstance().warnDialog("Insufficient privileges to do this operation!", Activator.PLUGIN_ID);
          }
        }
      }
    });

  }

  /**
   * Deletes or renames UC node
   *
   * @param event KeyEvent
   */
  private void deleteRenameUCNode(final KeyEvent event) {
    // if the seletected object is usecase or usecase section & if it is modifiable
    if (CommonUIConstants.KEY_DELETE == event.keyCode) {
      // if delete key is pressed
      deleteUCItem();
    }
    if (CommonUIConstants.KEY_RENAME == event.keyCode) {
      // if rename key is pressed
      updateUCItem();
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
   * @return the editorUseCase
   */
  public UsecaseClientBO getEditorUseCase() {
    return this.ucDetailsHandler.getUseCase();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshTreeViewer(final boolean deselectAll) {
    if (UseCaseDetailsPage.this.viewer != null) {
      UseCaseDetailsPage.this.viewer.refresh();
      if (deselectAll) {
        UseCaseDetailsPage.this.viewer.getTree().deselectAll();
      }
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    UseCaseActionSet.removeDetailsListener(this.ucDetailsHandler.getUseCase().getUseCase().getId(), this);
    super.dispose();
  }

  private void createToolBar() {
    final IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    this.updateAction = showUpdateAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.updateAction);
    this.updateAction.setEnabled(false);

    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    this.deleteAction = showDeleteAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.deleteAction);
    this.deleteAction.setEnabled(false);

    new CommonActionSet().addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 2);

  }

  /**
   * @return
   */
  private IAction showDeleteAction() {

    Action deleteActionInstance;

    deleteActionInstance = new Action("Delete") {

      @Override
      public void run() {
        deleteUCItem();
      }
    };
    // Set image for edit action
    deleteActionInstance.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));

    return deleteActionInstance;
  }

  /**
   * ICDM-705
   */
  private void deleteUCItem() {
    // // ICDM-358 Changes
    final IStructuredSelection selection = (IStructuredSelection) UseCaseDetailsPage.this.viewer.getSelection();
    final Object selectedObject = selection.getFirstElement();

    if (selectedObject instanceof UsecaseClientBO) {
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
    else if (selectedObject instanceof UseCaseSectionClientBO) {
      UseCaseSectionServiceClient ucServiceClient = new UseCaseSectionServiceClient();
      UseCaseSectionClientBO ucClientBO = (UseCaseSectionClientBO) selectedObject;

      UseCaseSection cloneUsecaseSec = ucClientBO.getUseCaseSection().clone();
      cloneUsecaseSec.setDeleted(true);
      try {
        ucServiceClient.update(cloneUsecaseSec);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @return
   */
  private IAction showUpdateAction() {
    Action updateActionInstance;

    updateActionInstance = new Action("Edit") {

      @Override
      public void run() {
        updateUCItem();
      }


    };
    // Set image for edit action
    updateActionInstance.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));

    return updateActionInstance;
  }

  /**
   * ICDM-705
   */
  private void updateUCItem() {
    // ICDM-139 Changes Start
    final IStructuredSelection selection = (IStructuredSelection) UseCaseDetailsPage.this.viewer.getSelection();
    final Object selectedObject = selection.getFirstElement();
    if (selectedObject instanceof UseCaseSectionClientBO) {
      final EditUseCaseSectionDialog editUcDlg = new EditUseCaseSectionDialog(Display.getDefault().getActiveShell(),
          (UseCaseSectionClientBO) selectedObject, UseCaseDetailsPage.this.viewer);
      editUcDlg.open();
    }
    else if (selectedObject instanceof UsecaseClientBO) {
      final EditUseCaseDialog editUcDlg = new EditUseCaseDialog(Display.getDefault().getActiveShell(),
          (UsecaseClientBO) selectedObject, UseCaseDetailsPage.this.viewer);
      editUcDlg.open();
    }
    // ICDM-139 Changes End
  }


  /**
   * // Icdm-796 This method provides the source instance for enabling export state
   *
   * @return source intance
   */
  public ISourceProviderService getService() {
    final IWorkbenchWindow wbWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    ISourceProviderService spService = null;
    if (wbWindow == null) {
      for (IWorkbenchWindow wbw : PlatformUI.getWorkbench().getWorkbenchWindows()) {
        spService = wbw.getService(ISourceProviderService.class);
        if (spService != null) {
          break;
        }
      }
    }
    else {
      spService = wbWindow.getService(ISourceProviderService.class);
    }
    return spService;
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
    return this.ucDetailsHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    // Set Content provider for the tree
    this.viewer.setContentProvider(new UseCaseDetailsViewContentProvider(this.ucDetailsHandler.getUseCase()));
    // Set Label provider for the tree
    this.viewer.setLabelProvider(new UseCaseDetailsViewLabelProvider());
    // create root node in Content provider
    // input the UseCase id, to display the root node
    this.viewer.setInput(this.ucDetailsHandler.getUseCase().getUseCase().getId());
    Map<IModelType, Map<Long, ChangeData<?>>> consChangeData = dce.getConsChangeData();
    for (IModelType modelType : consChangeData.keySet()) {
      if (modelType == MODEL_TYPE.NODE_ACCESS) {
        enableButtonsBasedOnSelection((IStructuredSelection) this.viewer.getSelection());
        break;
      }
    }
  }
}

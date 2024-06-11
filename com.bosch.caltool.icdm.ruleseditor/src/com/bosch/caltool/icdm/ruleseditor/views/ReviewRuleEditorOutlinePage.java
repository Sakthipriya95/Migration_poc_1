/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.views;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.OutlineA2LTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.OutlineA2LTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.AbstractPage;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponentFunctions;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.RuleSetParamInput;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewActionSet;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ruleseditor.pages.ListPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.RuleSetParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * ICDM-2265 outline page
 *
 * @author mkl2cob
 */
public class ReviewRuleEditorOutlinePage extends AbstractPage {

  /**
   *
   */
  private static final String DROP_A2L = "Dropping A2L File ";
  /**
   * Declare UI components and controls
   */
  private Composite top;
  /**
   * Composite
   */
  private Composite composite;
  /**
   * TreeViewer
   */
  private TreeViewer viewer;
  /**
   * ReviewParamEditorInput
   */
  private final ReviewParamEditorInput editorInput;
  /**
   * toolbar action to clear A2L file
   */
  private IAction tBarDelAction;
  private OutlineA2LTreeViewContentProvider contentProvider;

  private A2LEditorDataProvider a2lEditorDp;

  private PidcTreeNode pidcTreeNode;


  private A2LFile a2lFile;
  private final ReviewParamEditor editor;

  /**
   * Constructor
   *
   * @param reviewParamEditorInput ReviewParamEditorInput
   * @param reviewParamEditor
   */
  public ReviewRuleEditorOutlinePage(final ReviewParamEditorInput reviewParamEditorInput,
      final ReviewParamEditor reviewParamEditor) {
    this.editorInput = reviewParamEditorInput;
    this.editor = reviewParamEditor;
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
    // return the top composite
    return this.top;
  }

  @Override
  public void setFocus() {
    // To prevent
    // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
    // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
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

    // add drop listener
    addDropListener();

    if (this.editorInput.getA2lFile() != null) {
      // ICDM-2270
      // create content and label provider only if there is an A2L file to loaded
      // Set Content provider for the tree
      this.contentProvider =
          new OutlineA2LTreeViewContentProvider(this.editorInput.getA2lDataProvider().getA2lWpInfoBO(),
              this.editorInput.getA2lDataProvider().getPidcA2LBO(), true, false, null);


      Map paramMap = ReviewRuleEditorOutlinePage.this.editorInput.getParamMap();

      this.contentProvider.setParamMap(paramMap);
      this.contentProvider.setA2lParamMap(
          ReviewRuleEditorOutlinePage.this.editorInput.getA2lDataProvider().getA2lFileInfoBO().getA2lParamMap(null));
      this.viewer.setContentProvider(this.contentProvider);
      // Set Label provider for the tree
      this.viewer
          .setLabelProvider(new OutlineA2LTreeViewLabelProvider(this.editorInput.getA2lDataProvider().getA2lWpInfoBO(),
              this.editorInput.getA2lDataProvider().getPidcA2LBO(), null));
      // Call to build tree using setInput(), EMPTY string object indicates to
      // create root node in Content provider
      this.viewer.setInput("ROOT");
      // ICDM-2272
      this.viewer.expandToLevel(this.editorInput.getA2lFile(), 1);
    }

    // add right click menu
    addRightClickMenu();
    CommonActionSet cmnActionSet = new CommonActionSet();
    cmnActionSet.addCommonTreeActions(this.viewer, getSite().getActionBars().getToolBarManager(), 1);
    // ICDM-2270
    createToolBar();
    this.viewer.getTree().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) ReviewRuleEditorOutlinePage.this.viewer.getSelection();
        if ((selection != null) && (selection.size() != 0)) {
          // enable the toolbar button
          ReviewRuleEditorOutlinePage.this.tBarDelAction.setEnabled(true);
        }
      }
    });
  }


  private void loadA2lDataWithProgress(final String fileName) {


    try {

      ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, (final IProgressMonitor monitor) -> {

        monitor.beginTask(DROP_A2L + fileName, 100);
        monitor.worked(20);
        try {
          ReviewRuleEditorOutlinePage.this.a2lEditorDp =
              new A2LEditorDataProvider(ReviewRuleEditorOutlinePage.this.pidcTreeNode.getPidcA2l().getId(), true);
          ReviewRuleEditorOutlinePage.this.a2lFile =
              ReviewRuleEditorOutlinePage.this.a2lEditorDp.getPidcA2LBO().getA2lFile();
          monitor.worked(50);
          loadData();
          monitor.worked(90);
          ReviewRuleEditorOutlinePage.this.editorInput.setA2lFile(ReviewRuleEditorOutlinePage.this.a2lFile,
              ReviewRuleEditorOutlinePage.this.pidcTreeNode.getPidcA2l().getId());

        }
        catch (ApicWebServiceException | IcdmException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }

        monitor.worked(100);
        monitor.done();

      });

    }
    catch (InvocationTargetException | InterruptedException exp) {
      // Restore the interrupted state
      Thread.currentThread().interrupt();
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * To load the a2l related data for rules editor outline page
   *
   * @throws ApicWebServiceException
   */
  public void loadData() throws ApicWebServiceException {

    // If A2l data provider is null. Return
    if (((this.editorInput.getA2lDataProvider() == null) && (this.a2lEditorDp == null))) {
      return;
    }
    if (this.a2lEditorDp == null) {
      this.a2lEditorDp = this.editorInput.getA2lDataProvider();
    }


    Map<Long, A2lWpDefnVersion> exisitngWpDefMap = new A2lWpDefinitionVersionServiceClient()
        .getWPDefnVersForPidcA2l(this.a2lEditorDp.getA2lWpInfoBO().getPidcA2lBo().getPidcA2lId());
    boolean isActive = false;
    if (!exisitngWpDefMap.isEmpty()) {
      for (A2lWpDefnVersion wpDefVers : exisitngWpDefMap.values()) {
        if (wpDefVers.isActive()) {
          isActive = true;
        }
      }
      if (!isActive) {
        CDMLogger.getInstance().errorDialog(
            "Active version is not available for the selected A2l. Please set an active version to continue!",
            com.bosch.caltool.icdm.ruleseditor.Activator.PLUGIN_ID);
        return;
      }
    }
    else {
      createA2lWpDefinitionVersion(this.a2lEditorDp.getA2lWpInfoBO().getPidcA2lBo().getPidcA2lId());
    }
    this.a2lEditorDp.getA2lWpInfoBO().loadWPDefnVersionsForA2l();
    if (CommonUtils.isNotEmpty(this.a2lEditorDp.getA2lFileInfoBO().getParamProps())) {
      this.a2lEditorDp.getA2lWpInfoBO().initialiseA2LWParamInfo(
          this.a2lEditorDp.getA2lFileInfoBO().getA2lParamMap(this.a2lEditorDp.getA2lFileInfoBO().getParamProps()));
    }
    A2lWpDefnVersion activeVers = this.a2lEditorDp.getA2lWpInfoBO().getActiveVers();
    if (null != activeVers) {
      this.a2lEditorDp.getA2lWpInfoBO().loadWpMappedToPidcVers();
      this.a2lEditorDp.getA2lWpInfoBO().initializeModelBasedOnWpDefVers(activeVers.getId(), false, false);
    }
  }

  /**
   * create a wp def version working set if it's not available
   *
   * @param pidcA2lId
   * @param a2lwpInfoBO
   * @throws ApicWebServiceException
   */
  private void createA2lWpDefinitionVersion(final Long pidcA2lId) throws ApicWebServiceException {

    A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();

    a2lWpDefnVersion.setVersionName(ApicConstants.WORKING_SET_NAME);
    a2lWpDefnVersion.setActive(false);
    a2lWpDefnVersion.setWorkingSet(true);
    a2lWpDefnVersion.setParamLevelChgAllowedFlag(false);
    a2lWpDefnVersion.setPidcA2lId(pidcA2lId);
    A2lWpDefinitionVersionServiceClient client = new A2lWpDefinitionVersionServiceClient();

    // Creates working set - default Resp Pal, Pidc Resps & Param mapping
    client.create(a2lWpDefnVersion, this.a2lEditorDp.getPidcA2LBO().getPidcA2l());
    CDMLogger.getInstance().info(
        "WorkingSet with default Work Package-Responsibility defintion has been created for the selected A2L file",
        com.bosch.caltool.icdm.ruleseditor.Activator.PLUGIN_ID);


  }

  /**
   * ICDM-2267 drag drop listener
   */
  private void addDropListener() {
    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    DropTarget target = new DropTarget(this.viewer.getControl(), DND.DROP_COPY | DND.DROP_MOVE);
    target.setTransfer(transferTypes);
    target.addDropListener(new DropTargetAdapter() {

      @Override
      public void drop(final DropTargetEvent event) {

        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        StructuredSelection structuredSelection = (StructuredSelection) event.data;
        Object selectedElement = structuredSelection.getFirstElement();
        boolean checkAdd = checkIfAlreadyAdded(selectedElement);
        if (!checkAdd && (selectedElement instanceof PidcTreeNode)) {
          ReviewRuleEditorOutlinePage.this.pidcTreeNode = (PidcTreeNode) selectedElement;


          loadA2lDataWithProgress(ReviewRuleEditorOutlinePage.this.pidcTreeNode.getName());


          // set the new content provider or label provider
          ReviewRuleEditorOutlinePage.this.contentProvider =
              new OutlineA2LTreeViewContentProvider(ReviewRuleEditorOutlinePage.this.a2lEditorDp.getA2lWpInfoBO(),
                  ReviewRuleEditorOutlinePage.this.a2lEditorDp.getPidcA2LBO(), true, false, null);


          ReviewRuleEditorOutlinePage.this.editorInput.setA2LDataProvider(ReviewRuleEditorOutlinePage.this.a2lEditorDp);
          ReviewRuleEditorOutlinePage.this.contentProvider
              .setParamMap(ReviewRuleEditorOutlinePage.this.editorInput.getParamMap());
          ReviewRuleEditorOutlinePage.this.contentProvider
              .setA2lParamMap(ReviewRuleEditorOutlinePage.this.a2lEditorDp.getA2lFileInfoBO().getA2lParamMap(null));
          ReviewRuleEditorOutlinePage.this.viewer.setContentProvider(ReviewRuleEditorOutlinePage.this.contentProvider);
          ReviewRuleEditorOutlinePage.this.viewer.setLabelProvider(
              new OutlineA2LTreeViewLabelProvider(ReviewRuleEditorOutlinePage.this.a2lEditorDp.getA2lWpInfoBO(),
                  ReviewRuleEditorOutlinePage.this.a2lEditorDp.getPidcA2LBO(), null));
          ReviewRuleEditorOutlinePage.this.viewer.setInput("ROOT");
          // ICDM-2272
          ReviewRuleEditorOutlinePage.this.viewer.expandToLevel(ReviewRuleEditorOutlinePage.this.a2lFile, 1);
          ReviewRuleEditorOutlinePage.this.viewer.refresh();
        }
        else {
          // show error message dialog
          MessageDialogUtils.getErrorMessageDialog("Error", "Invalid Drag and Drop");
        }
      }
    });
  }

  /**
   * @param selectedElement
   * @return true if the A2L file is already loaded
   */
  private boolean checkIfAlreadyAdded(final Object selectedElement) {
    if (selectedElement instanceof PidcTreeNode) {
      PidcTreeNode treeNode = (PidcTreeNode) selectedElement;
      A2LFile a2lFileFromEditor = ReviewRuleEditorOutlinePage.this.editorInput.getA2lFile();
      if ((a2lFileFromEditor != null) && treeNode.getPidcA2l().getName().equals(a2lFileFromEditor.getFilename())) {
        // if a2l file is already added to the outline view
        return true;
      }
    }
    return false;
  }

  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection =
          (IStructuredSelection) ReviewRuleEditorOutlinePage.this.viewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      // ICDM-2272
      if (CommonUtils.isNotNull(selection) && !selection.isEmpty()) {
        if (firstElement instanceof A2LFile) {
          addA2lParamActionSet(menuMgr);
        }
        else if ((firstElement instanceof Function) || (firstElement instanceof A2LBaseComponentFunctions)) {
          A2LEditorDataProvider a2lDataProvider = ReviewRuleEditorOutlinePage.this.editorInput.getA2lDataProvider();
          SortedSet<Function> functionList = a2lDataProvider.getA2lFileInfoBO().getAllSortedFunctions();
          ReviewActionSet actionSet = new ReviewActionSet();
          actionSet.addReviewParamEditorNew(menuMgr, firstElement, functionList, null, false);
        }
      }
      Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
      this.viewer.getControl().setMenu(menu);

    });
    Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(null, menuMgr, this.viewer);
  }

  /**
   * @param menuMgr
   */
  private void addA2lParamActionSet(final MenuManager menuMgr) {
    CommonActionSet actionSet = new CommonActionSet();
    actionSet.openA2LEditor(menuMgr,
        ReviewRuleEditorOutlinePage.this.editorInput.getA2lDataProvider().getPidcA2LBO().getPidcA2l());

    Action a2lEditorAction = new Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        boolean openConfirm = MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Confirmation Dialog",
            "Do you want to import rule set parameter from A2L file");
        if (openConfirm) {
          final ProgressMonitorDialog progressDlg = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
          try {
            progressDlg.run(true, true, monitor -> {
              A2LFile a2lFileLcl = ReviewRuleEditorOutlinePage.this.editorInput.getA2lFile();
              ParamCollection selectedObject = ReviewRuleEditorOutlinePage.this.editorInput.getSelectedObject();
              RuleSetParamInput ruleSetParamInput = new RuleSetParamInput();
              ruleSetParamInput.setA2lFileId(a2lFileLcl.getId());
              ruleSetParamInput.setRuleSetId(selectedObject.getId());
              try {
                Set<RuleSetParameter> ruleSetParameters =
                    new RuleSetParameterServiceClient().createRuleSetParamUsingA2lFileID(ruleSetParamInput);
                for (RuleSetParameter ruleSetParameter : ruleSetParameters) {
                  ReviewRuleEditorOutlinePage.this.editorInput.getParamDataProvider().getParamRulesOutput()
                      .getParamMap().put(ruleSetParameter.getName(), ruleSetParameter);
                }
                CDMLogger.getInstance()
                    .infoDialog("Import parameter from A2L is successful.\nTotal Parameters imported from a2l is " +
                        ruleSetParameters.size(), Activator.PLUGIN_ID);
              }
              catch (ApicWebServiceException e) {
                CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
              }
            });
            refreshPage(ReviewRuleEditorOutlinePage.this.editor.getListPage());
          }
          catch (InvocationTargetException | InterruptedException exp) {
            Thread.currentThread().interrupt();
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
    };
    a2lEditorAction.setText("Add Parameter from A2L file");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.UPLOAD_16X16);
    a2lEditorAction.setImageDescriptor(imageDesc);
    a2lEditorAction
        .setEnabled(this.editorInput.getDataProvider().isModifiable(this.editor.getListPage().getCdrFunction()));
    menuMgr.add(a2lEditorAction);
  }

  /**
   * @param abstractFormPage abstractFormPage
   */
  private void refreshPage(final AbstractFormPage abstractFormPage) {
    if (abstractFormPage instanceof ListPage) {
      ListPage page = (ListPage) abstractFormPage;
      page.refreshListPage();
      page.getParamTabSec().getParamTab().updateStatusBar(false);
    }
    if (abstractFormPage instanceof DetailsPage) {
      DetailsPage page = (DetailsPage) abstractFormPage;
      page.getEditor().refreshSelectedParamRuleData();
      page.getFcTableViewer().refresh();
      page.setStatusBarMessage(page.getFcTableViewer());
      ListPage listPage = page.getEditor().getListPage();
      listPage.refreshListPage();
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
    this.viewer.refresh();
    if (deselectAll) {
      this.viewer.getTree().deselectAll();
    }

  }

  /**
   * ICDM-2270 Creates an actions on view tool bar
   */
  private void createToolBar() {

    IToolBarManager mgr = getSite().getActionBars().getToolBarManager();
    Separator separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);

    this.tBarDelAction = createDelAction();
    separator = new Separator(IWorkbenchActionConstants.MB_ADDITIONS);
    mgr.add(separator);
    mgr.add(this.tBarDelAction);
    this.tBarDelAction.setEnabled(false);

  }

  /**
   * ICDM-2270 This method clears the A2L file in outline page
   *
   * @return IAction
   */
  public IAction createDelAction() {

    IAction deleteAction = new Action("Clear A2L contents") {

      @Override
      public void run() {
        IStructuredSelection sel = (IStructuredSelection) ReviewRuleEditorOutlinePage.this.viewer.getSelection();
        if ((sel != null) && !sel.isEmpty()) {
          List<Object> pages = ReviewRuleEditorOutlinePage.this.editor.getPages();
          for (Object page : pages) {
            if (page instanceof AbstractFormPage) {
              AbstractFormPage abspage = (AbstractFormPage) page;
              if (abspage.getResetFiltersAction() != null) {
                abspage.getResetFiltersAction().run();

              }

            }
          }
          ReviewRuleEditorOutlinePage.this.editorInput.setA2LDataProvider(null);
          ReviewRuleEditorOutlinePage.this.editorInput.setA2lFile(null, 0);
          ReviewRuleEditorOutlinePage.this.viewer.setInput(null);
          ReviewRuleEditorOutlinePage.this.viewer.refresh();


          ReviewRuleEditorOutlinePage.this.tBarDelAction.setEnabled(false);
        }
      }
    };
    // Set image for delete action
    deleteAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.REMOVE_16X16));

    return deleteAction;
  }

  /**
   * @return TreeViewer
   */
  public TreeViewer getViewer() {
    return this.viewer;
  }

}

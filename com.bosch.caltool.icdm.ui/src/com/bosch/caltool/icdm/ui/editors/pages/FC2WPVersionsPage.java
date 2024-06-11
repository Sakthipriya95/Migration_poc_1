/**
 *
 */
package com.bosch.caltool.icdm.ui.editors.pages;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.dialogs.FC2WPVersionEditDialog;
import com.bosch.caltool.icdm.ui.dialogs.FC2WPVersionSelDialog;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditor;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditorInput;
import com.bosch.caltool.icdm.ui.sorters.FC2WPVersionsSorter;
import com.bosch.caltool.icdm.ui.views.providers.FC2WPVersionsTableLabelProvider;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;

/**
 * The page to create fc2wp versions
 *
 * @author adn1cob
 */
public class FC2WPVersionsPage extends AbstractFormPage {

  /**
   * Label
   */
  private static final String VERSIONS_LABEL = "Versions";
  /**
   * Col width - Name
   */
  private static final int COL_WIDTH_NAME = 150;
  /**
   * Col width - Description
   */
  private static final int COL_WIDTH_DESC = 250;
  /**
   * Col width - Active
   */
  private static final int COL_WIDTH_ACTIVE = 100;
  /**
   * Col width - Created Date
   */
  private static final int COL_WIDTH_CRE_DATE = 200;
  /**
   * Col width - Created User
   */
  private static final int COL_WIDTH_CRE_USR = 250;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form baseForm;
  /**
   * Versions table viewer
   */
  private GridTableViewer versionsTabViewer;

  /**
   * Editor instance
   */
  private final FC2WPEditor editor;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  private final FC2WPEditorInput editorInput;
  private Set<FC2WPVersion> allVersions;
  private AbstractViewerSorter versionsSorter;
  private FC2WPVersion activeVersion;
  private Action addNewVersionAction;
  private FC2WPMappingWithDetails fc2wpVersMapping;
  private FC2WPEditorInput dataForSelVer;
  private Action editVersionAction;
  private ToolBarManager toolBarManager;

  /**
   * Instantiates a new FC2WP versions page.
   *
   * @param editor FormEditor
   * @param editorInput the editor input
   */
  public FC2WPVersionsPage(final FormEditor editor, final FC2WPEditorInput editorInput) {
    super(editor, VERSIONS_LABEL, VERSIONS_LABEL);
    this.editor = (FC2WPEditor) editor;
    this.editorInput = editorInput;
  }

  @Override
  public void createPartControl(final Composite parent) {
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText(this.editorInput.getFc2wpVersion().getName());
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());
    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // Create scrolled form
    this.formToolkit = managedForm.getToolkit();
    createComposite(this.formToolkit);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * @param managedForm
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    Composite composite = this.nonScrollableForm.getBody();
    composite.setLayout(new GridLayout());
    createSection(toolkit, composite);
    composite.setLayoutData(gridData);
  }

  /**
   * @param toolkit This method initializes section
   * @param composite
   */
  private void createSection(final FormToolkit toolkit, final Composite composite) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = toolkit.createSection(composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText(VERSIONS_LABEL);
    this.section.setDescription("Select a version to enable toolbar actions ");
    this.section.setExpanded(true);
    createForm(toolkit);
    this.section.setLayoutData(gridData);
    this.section.setClient(this.baseForm);
  }

  /**
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.baseForm = toolkit.createForm(this.section);
    this.versionsTabViewer = new GridTableViewer(this.baseForm.getBody(),
        SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

    this.versionsTabViewer.getGrid().setLayoutData(gridData);

    this.versionsTabViewer.getGrid().setLinesVisible(true);
    this.versionsTabViewer.getGrid().setHeaderVisible(true);

    this.baseForm.getBody().setLayout(new GridLayout());
    createToolBarAction();

    // Column Sorter
    this.versionsSorter = new FC2WPVersionsSorter();

    createVersionsTabColumns();

    this.versionsTabViewer.setContentProvider(ArrayContentProvider.getInstance());


    // Invoke TableViewer Column sorters
    invokeColumnSorter();
    addRightClickMenu();
    this.versionsTabViewer.addDoubleClickListener(event -> CommonUiUtils.getInstance().getDisplay().asyncExec(() -> {
      final IStructuredSelection selection =
          (IStructuredSelection) FC2WPVersionsPage.this.versionsTabViewer.getSelection();
      if (!selection.isEmpty() && (selection.getFirstElement() instanceof FC2WPVersion)) {
        FC2WPVersion selFc2WpVer = (FC2WPVersion) selection.getFirstElement();
        // Load the contents for selected FC2WP version using webservice calls
        loadDataForSelVer(selFc2WpVer);

        try {
          if (FC2WPVersionsPage.this.fc2wpVersMapping != null) {
            IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .openEditor(FC2WPVersionsPage.this.dataForSelVer, FC2WPEditor.PART_ID);
            FC2WPEditor a2lEditor = (FC2WPEditor) openEditor;
            a2lEditor.setFocus();
            a2lEditor.setActivePage("FC-WP");
          }
        }
        catch (PartInitException excep) {
          CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
        }
      }
    }));
    this.versionsTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) FC2WPVersionsPage.this.versionsTabViewer.getSelection();
        if (!selection.isEmpty()) {
          // TO-DO
        }
      }
    });

    FC2WPEditor fc2WPEditor = (FC2WPEditor) getEditor();
    SelectionProviderMediator selectionProviderMediator = fc2WPEditor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(this.versionsTabViewer);
    getSite().setSelectionProvider(selectionProviderMediator);

    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.versionsTabViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.versionsTabViewer));


  }

  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      final IStructuredSelection selection =
          (IStructuredSelection) FC2WPVersionsPage.this.versionsTabViewer.getSelection();
      if (!selection.isEmpty()) {
        List<FC2WPVersion> list = new ArrayList<>(selection.toList());
        if (selection.size() > 1) {
          openCompareEditor(list, mgr);
        }
        addCompareWithOtherVersions(list, mgr);
      }

    });
    // Create menu.
    final Menu menu = menuMgr.createContextMenu(this.versionsTabViewer.getGrid());
    this.versionsTabViewer.getGrid().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.versionsTabViewer);
  }

  /**
   * @param list List<FC2WPVersion>
   * @param mgr IMenuManager
   */
  protected void addCompareWithOtherVersions(final List<FC2WPVersion> list, final IMenuManager mgr) {


    Action compareVersAction = new Action("Compare with other FC2WP Versions", SWT.NONE) {

      @Override
      public void run() {
        FC2WPVersionSelDialog dialog = new FC2WPVersionSelDialog(Display.getDefault().getActiveShell(),
            FC2WPVersionsPage.this.editorInput.getFc2wpDef());
        if (dialog.open() == 0) {
          ConcurrentHashMap<FC2WPVersion, FC2WPDef> allVersionsMap = new ConcurrentHashMap<>();
          for (FC2WPVersion vers : list) {
            allVersionsMap.put(vers, FC2WPVersionsPage.this.editorInput.getFc2wpDef());
          }
          // add other FC2WP vers and its definitions to existing selection
          for (FC2WPVersion vers : dialog.getSelVersList()) {
            allVersionsMap.put(vers, dialog.getAllVersionsMap().get(vers));
          }
          list.addAll(dialog.getSelVersList());
          openVersCompareEditor(list, allVersionsMap);
        }
      }

    };
    compareVersAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FC2WP_DEFN_16X16));
    mgr.add(compareVersAction);


  }

  /**
   * @param list List<FC2WPVersion>
   * @param mgr IMenuManager
   */
  protected void openCompareEditor(final List<FC2WPVersion> list, final IMenuManager mgr) {


    Action compareVersAction = new Action("Compare FC2WP Versions", SWT.NONE) {

      @Override
      public void run() {
        ConcurrentHashMap<FC2WPVersion, FC2WPDef> allVersionsMap = new ConcurrentHashMap<>();
        for (FC2WPVersion vers : list) {
          allVersionsMap.put(vers, FC2WPVersionsPage.this.editorInput.getFc2wpDef());
        }
        openVersCompareEditor(list, allVersionsMap);
      }

    };
    compareVersAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FC2WP_DEFN_16X16));
    mgr.add(compareVersAction);


  }

  /**
   * Calls FC2WP webservice method and loads the FC2WP mapping.
   *
   * @param fc2wpDefBo for the selected fc2wp version
   */
  private void loadDataForSelVer(final FC2WPVersion selFc2WpVer) {
    try {
      ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Retrieving FC2WP mapping data for the selected version ...", 100);
        monitor.worked(20);
        FC2WPVersionsPage.this.dataForSelVer =
            new FC2WPEditorInput(FC2WPVersionsPage.this.editorInput.getFc2wpDef(), selFc2WpVer);
        FC2WPVersionsPage.this.fc2wpVersMapping =
            FC2WPVersionsPage.this.editorInput.getFC2WPDefBO().getFc2wpMappingWithDetails();
        monitor.worked(100);
        monitor.done();
      });
    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * invoke column sorter for the versions table
   */
  private void invokeColumnSorter() {
    this.versionsTabViewer.setComparator(this.versionsSorter);
  }

  /**
   * Defines the columns of the TableViewer
   */
  private void createVersionsTabColumns() {

    GridViewerColumn versionNameColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    versionNameColumn.getColumn().setText("Version Name");
    versionNameColumn.getColumn().setWidth(COL_WIDTH_NAME);
    versionNameColumn.setLabelProvider(new FC2WPVersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_0));
    // Add column selection listener
    versionNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        versionNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_0, this.versionsSorter, this.versionsTabViewer));

    GridViewerColumn versionDescColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    versionDescColumn.getColumn().setText("Version Description");
    versionDescColumn.getColumn().setWidth(COL_WIDTH_DESC);
    versionDescColumn.setLabelProvider(new FC2WPVersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_1));
    // Add column selection listener
    versionDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        versionDescColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_1, this.versionsSorter, this.versionsTabViewer));

    createActiveColumn();

    GridViewerColumn dateColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    dateColumn.getColumn().setText("Created Date");
    dateColumn.getColumn().setWidth(COL_WIDTH_CRE_DATE);
    dateColumn.setLabelProvider(new FC2WPVersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_3));
    // Add column selection listener
    dateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        dateColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.versionsSorter, this.versionsTabViewer));

    GridViewerColumn userColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    userColumn.getColumn().setText("Created User");
    userColumn.getColumn().setWidth(COL_WIDTH_CRE_USR);
    userColumn.setLabelProvider(new FC2WPVersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_4));
    // Add column selection listener
    userColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        userColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.versionsSorter, this.versionsTabViewer));

    // Get data from web service call
    this.allVersions = this.editorInput.getFC2WPDefBO().getAllVersions();

    this.versionsTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.versionsTabViewer.setInput(this.allVersions);
    enableDisableAddVersBtn();

  }

  /**
   * Create Active Column
   */
  private void createActiveColumn() {

    GridViewerColumn activeColumn = new GridViewerColumn(this.versionsTabViewer, SWT.CHECK | SWT.CENTER);
    activeColumn.getColumn().setText("Active");
    activeColumn.getColumn().setWidth(COL_WIDTH_ACTIVE);
    activeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof FC2WPVersion) {
          FC2WPVersion version = (FC2WPVersion) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), version.isActive());
          // active version cannot be modified for working set
          gridItem.setCheckable(cell.getVisualIndex(),
              FC2WPVersionsPage.this.editorInput.getFC2WPDefBO().isOwner() && !version.isWorkingSet());
        }
      }
    });
    activeColumn.setEditingSupport(new CheckEditingSupport(activeColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        boolean confirm = checkActiveVersion(arg0, arg1);
        if (confirm) {
          // creating edit command for FC2WP version
          FC2WPVersionsPage.this.editorInput.getFC2WPDefBO().setActiveVersServiceCall((FC2WPVersion) arg0,
              (boolean) arg1);
        }
        else {
          FC2WPVersionsPage.this.versionsTabViewer.update(arg0, null);
        }

      }
    });
    activeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        activeColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.versionsSorter, this.versionsTabViewer));
  }

  /**
   * @param arg0
   * @param arg1
   * @return
   */
  public boolean checkActiveVersion(final Object arg0, final Object arg1) {

    // get the active version
    FC2WPVersionsPage.this.activeVersion = getActiveVersion();
    boolean confirm;
    if ((boolean) arg1) {
      if (FC2WPVersionsPage.this.activeVersion == null) {
        // if active version is null , need not show the confirmation dialog
        confirm = true;
      }
      else {
        // when setting to active, show the confirm dialog that the active version's flag will be set to false
        confirm = MessageDialogUtils.getConfirmMessageDialog("Confirm changing active version",
            "The currently active version " + FC2WPVersionsPage.this.activeVersion.getVersionName() +
                " is set to not active because only one version can be active");
      }

    }
    else {
      // when setting to non active, show the confirm dialog that this version cannot be used
      confirm = MessageDialogUtils.getConfirmMessageDialog("Confirm changing active flag",
          "There's no other active version at the moment. Setting all versions to non active won't allow the user to select this version for their reviews.");
    }
    return confirm;

  }

  /**
   *
   */
  protected void refreshTable() {
    this.allVersions = this.editorInput.getFC2WPDefBO().getAllVersions();
    this.versionsTabViewer.setInput(this.allVersions);
    this.versionsTabViewer.refresh();
    if (this.toolBarManager != null) {
      this.toolBarManager.removeAll();
    }
    if (this.baseForm.getToolBarManager() != null) {
      this.baseForm.getToolBarManager().removeAll();
    }
    createToolBarAction();
    enableDisableAddVersBtn();
    this.baseForm.getBody().pack();
    this.section.layout();
  }

  /**
   * Webservice call to update active flag.
   *
   * @param versn the new active version WS
   * @return the FC2WP version
   */
  protected FC2WPVersion setActiveVersServiceCall(final FC2WPVersion versn) {
    FC2WPVersion retVers = null;
    CDMLogger.getInstance().debug("Updating active FC2WP version using APIC web server... ");

    // create a webservice client
    final FC2WPVersionServiceClient client = new FC2WPVersionServiceClient();
    try {
      versn.setActive(true);
      retVers = client.update(versn);
      CDMLogger.getInstance().debug("FC2WP version :" + versn.getId() + " is set to Active");
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error("Error in setting FC2WP version as active : " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }

    return retVers;
  }

  /**
   * check whether active pidc version needs to be searched each time or can be stored locally
   *
   * @return the active version of the pidc
   */
  public FC2WPVersion getActiveVersion() {
    synchronized (this) {
      for (FC2WPVersion version : this.allVersions) {
        if (version.isActive()) {
          return version;
        }
      }
    }
    return null;
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    boolean editFlag = FC2WPVersionsPage.this.editorInput.getFC2WPDefBO().isOwner();
    this.toolBarManager = new ToolBarManager(SWT.FLAT);

    ToolBar toolbar = this.toolBarManager.createControl(this.section);

    addVersionAction(this.toolBarManager);

    addEditVerionActionToSection(this.toolBarManager);
    this.editVersionAction.setEnabled(editFlag);
    this.toolBarManager.update(true);

    this.section.setTextClient(toolbar);
  }

  /**
   * This method creates non defined filter action
   *
   * @param toolBarMgr ToolBarManager
   */
  private void addVersionAction(final ToolBarManager toolBarMgr) {
    // Create an action to add new version
    this.addNewVersionAction = new Action("Add Version", SWT.NONE) {

      @Override
      public void run() {
        FC2WPVersionEditDialog versionEditDialog =
            new FC2WPVersionEditDialog(Display.getCurrent().getActiveShell(), FC2WPVersionsPage.this.formToolkit,
                FC2WPVersionsPage.this.editorInput.getFc2wpDef().getId(), FC2WPVersionsPage.this.activeVersion,
                FC2WPVersionsPage.this.editorInput.getFC2WPDefBO(), FC2WPVersionsPage.this, false);
        versionEditDialog.open();
        refreshTable();
      }
    };
    // Set the image for add version action
    this.addNewVersionAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarMgr.add(this.addNewVersionAction);
    if (FC2WPVersionsPage.this.editorInput.getFC2WPDefBO().isOwner()) {
      this.addNewVersionAction.setEnabled(true);
    }
  }

  /**
   * Enable disable add vers btn.
   */
  private void enableDisableAddVersBtn() {
    if (CommonUtils.isNotNull(getWorkingSet())) {
      boolean isEnabled = FC2WPVersionsPage.this.editorInput.getFC2WPDefBO().isOwner();
      this.addNewVersionAction.setEnabled(isEnabled);
    }
  }


  /**
   * This method creates non defined filter action
   *
   * @param toolBarMgr
   */
  private void addEditVerionActionToSection(final ToolBarManager toolBarMgr) {
    // Create an action to delete the user
    this.editVersionAction = new Action("Edit", SWT.NONE) {

      @Override
      public void run() {
        if (null != getSelectedVersion()) {
          FC2WPVersionEditDialog versionEditDialog = new FC2WPVersionEditDialog(Display.getCurrent().getActiveShell(),
              FC2WPVersionsPage.this.formToolkit, FC2WPVersionsPage.this.editorInput.getFc2wpDef().getId(),
              getSelectedVersion(), FC2WPVersionsPage.this.editorInput.getFC2WPDefBO(), FC2WPVersionsPage.this, true);
          versionEditDialog.open();
        }
      }
    };

    // Set the image for delete the user
    this.editVersionAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    toolBarMgr.add(this.editVersionAction);
  }

  /**
   * @return FC2WPVersion
   */
  private FC2WPVersion getSelectedVersion() {
    FC2WPVersion version = null;
    IStructuredSelection selection = (IStructuredSelection) this.versionsTabViewer.getSelection();
    Object firstElement = selection.getFirstElement();
    if (firstElement instanceof FC2WPVersion) {
      version = (FC2WPVersion) firstElement;
      if (FC2WPVersionsPage.this.editorInput.getFC2WPDefBO().isOwner()) {
        this.editVersionAction.setEnabled(true);
      }
    }
    return version;
  }

  @Override
  public void setActive(final boolean active) {
    if (active) {
      // Update title while renaming PIDC
    }
  }

  /**
   * Sets the page title
   *
   * @param title String
   */
  public void setTitleText(final String title) {
    if (this.nonScrollableForm != null) {
      this.nonScrollableForm.setText(title);
    }
  }

  /**
   * @return form
   */
  public Form getNonScrollableForm() {
    return this.nonScrollableForm;
  }

  // this method is added to prevent
  // "java.lang.RuntimeException: WARNING: Prevented recursive attempt to activate part
  // org.eclipse.ui.views.PropertySheet while still in the middle of activating part"
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    // TO-DO
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }


  /**
   * @return the versionsTabViewer
   */
  public GridTableViewer getVersionsTabViewer() {
    return this.versionsTabViewer;
  }

  /**
   * @return the working set of a FC2WPVersion
   */
  public FC2WPVersion getWorkingSet() {
    Set<FC2WPVersion> allVersns = this.allVersions;
    for (FC2WPVersion version : allVersns) {
      if ((version.getMajorVersNo() == 0L) &&
          ((version.getMinorVersNo() == null) || (version.getMinorVersNo() == 0L))) {
        // if major version is zero and minor version is null
        return version;
      }
    }
    return null;
  }

  /**
   * @param list
   * @param allVersionsMap
   * @param editorOpened
   * @throws PartInitException
   */
  private void openFC2WPEditor(final List<FC2WPVersion> list,
      final ConcurrentHashMap<FC2WPVersion, FC2WPDef> allVersionsMap)
      throws PartInitException {
    boolean editorOpened = false;
    FC2WPEditorInput input = new FC2WPEditorInput(FC2WPVersionsPage.this.editorInput.getFc2wpDef(), list.get(0));
    input.getFC2WPDefBO().getMappingDetailsForCompareEditor(list);
    Map<FC2WPDef, List<FC2WPVersion>> fc2wpDefMap = new HashMap<>();
    fc2wpDefMap.put(FC2WPVersionsPage.this.editorInput.getFc2wpDef(), list);
    input.getFC2WPDefBO().setFc2wpDefMap(allVersionsMap);
    if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {

      IEditorReference[] editorReferences =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
      for (IEditorReference iEditorReference : editorReferences) {
        IEditorPart editorPart = iEditorReference.getEditor(false);
        if (editorPart instanceof FC2WPEditor) {
          FC2WPEditor existingEditor = (FC2WPEditor) editorPart;
          FC2WPEditorInput editInput = existingEditor.getEditorInput();
          editorOpened = checkIfSameEditor(list, existingEditor, editInput, allVersionsMap);
          if (editorOpened) {
            existingEditor.init(getEditorSite(), editInput);
            break;
          }
        }
      }
    }
    // if editor is not existing then open new editor
    if (!editorOpened) {
      IEditorPart openEditor =
          PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, FC2WPEditor.PART_ID);
      FC2WPEditor a2lEditor = (FC2WPEditor) openEditor;
      a2lEditor.setFocus();
    }
  }

  /**
   * @param list
   * @param mappingDetailList
   * @param editorOpened
   * @param existingEditor
   * @param editInput
   * @param allVersionsMap
   * @return
   * @throws PartInitException
   */
  private boolean checkIfSameEditor(final List<FC2WPVersion> list, final FC2WPEditor existingEditor,
      final FC2WPEditorInput editInput, final ConcurrentHashMap<FC2WPVersion, FC2WPDef> allVersionsMap)
      throws PartInitException {
    boolean editorOpened = false;
    if (editInput.getFc2wpVersion().getId().longValue() == list.get(0).getId().longValue()) {
      editInput.getFC2WPDefBO().setFc2wpDefMap(allVersionsMap);
      existingEditor.getFC2WPNatFormPage().reconstructNatTable();
      editorOpened = true;
      IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .openEditor(editInput, FC2WPEditor.PART_ID);
      FC2WPEditor a2lEditor = (FC2WPEditor) openEditor;
      a2lEditor.setActivePage("FC-WP");
      a2lEditor.setFocus();
    }
    return editorOpened;
  }

  /**
   * @param list
   * @param object
   */
  private void openVersCompareEditor(final List<FC2WPVersion> list,
      final ConcurrentHashMap<FC2WPVersion, FC2WPDef> allVersionsMap) {
    List<FC2WPMappingWithDetails> mappingDetailList = new ArrayList<>();
    Map<Long, FC2WPMappingWithDetails> compfc2wpVerMappingDetvers =
        this.editorInput.getFC2WPDefBO().getMappingDetailsForCompareEditor(list);
    for (FC2WPVersion vers : list) {
      mappingDetailList.add(compfc2wpVerMappingDetvers.get(vers.getId()));
    }

    try {
      if (!mappingDetailList.isEmpty()) {
        openFC2WPEditor(list, allVersionsMap);
      }
    }
    catch (PartInitException excep) {
      CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IClientDataHandler getDataHandler() {
    return this.editor.getEditorInput().getFc2wpEditorDataHandler();
  }

  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (this.versionsTabViewer != null) {
      refreshTable();
    }
  }
}

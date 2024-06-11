/**
 *
 */
package com.bosch.caltool.apic.ui.editors.pages;


import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCVersionLockAction;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.sorter.PIDCVersTabViewerSorter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.apic.ui.views.providers.VersionsTableLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PIDCVersionsDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.dragdrop.CustomDragListener;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * @author adn1cob
 */
public class PidcVersionsPage extends AbstractFormPage {

  private PidcVersion pidcVersion;
  private Composite composite;
  private Section section;
  private Form baseForm;

  private GridTableViewer versionsTabViewer;

  /**
   * Editor instance
   */
  private final PIDCEditor editor;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  private PIDCActionSet actionSet;
  private PidcVersion selectedPidcVersion;

  private final PIDCVersionsDataHandler versionsHandler;

  private PIDCVersionLockAction lockAction;
  private PIDCVersTabViewerSorter tabSorter;
  private Button considerPrvPidcVersBtn;

  /**
   * @param editor FormEditor
   * @param pidVersion PidcVersion
   */
  public PidcVersionsPage(final FormEditor editor, final PidcVersion pidVersion) {
    super(editor, "Versions", Messages.getString("VersionsPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    this.editor = (PIDCEditor) editor;
    this.pidcVersion = pidVersion;
    this.versionsHandler = new PIDCVersionsDataHandler(
        this.editor.getPidcPage().getPidcVersionBO().getPidcDataHandler().getPidcVersionInfo().getPidc());

  }


  @Override
  public void createPartControl(final Composite parent) {
    // ICDM-26/ICDM-168
    // Super class implementation creates a scrolled form by default
    // The scrolled form is used to create a managed form

    // Overrode this by creating an ordinary form without scrollable behaviour and a managed form instantiated without a
    // scrolled form

    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    // ICDM-208
    this.nonScrollableForm.setText(this.pidcVersion.getName().replace("&", "&&"));
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
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    // ICDM-183
    this.section.getDescriptionControl().setEnabled(false);

  }

  /**
   * @param managedForm
   * @param toolkit This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    createSection(toolkit);
    this.composite.setLayoutData(gridData);

  }

  /**
   * @param toolkit This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {

    this.considerPrvPidcVersBtn = new Button(this.composite, SWT.CHECK);
    this.considerPrvPidcVersBtn.setText("Consider reviews of previous versions in reports");
    this.considerPrvPidcVersBtn.setSelection(this.versionsHandler.getPidc().isInclRvwOfOldVers());

    this.considerPrvPidcVersBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        try {
          boolean considerPidcVersSel = PidcVersionsPage.this.considerPrvPidcVersBtn.getSelection();
          if (new CurrentUserBO().hasNodeOwnerAccess(PidcVersionsPage.this.pidcVersion.getPidcId())) {
            boolean isUserUnlockedPidc = checkPIDCLock();
            if (isUserUnlockedPidc) {
              // Refresh the PIDC object before calling update since it could be updated from other places
              PidcVersionsPage.this.versionsHandler.refreshPIDCObject();
              PidcVersionsPage.this.versionsHandler.getPidc().setInclRvwOfOldVers(considerPidcVersSel);
              PidcVersionsPage.this.versionsHandler.updatePidc(PidcVersionsPage.this.versionsHandler.getPidc());
            }
            else {
              PidcVersionsPage.this.considerPrvPidcVersBtn.setSelection(!considerPidcVersSel);
            }
          }
          else {
            CDMLogger.getInstance().infoDialog("Please contact the PIDC owner to update the flag.",
                Activator.PLUGIN_ID);
            PidcVersionsPage.this.considerPrvPidcVersBtn.setSelection(!considerPidcVersSel);
          }
        }
        catch (ApicWebServiceException e1) {
          CDMLogger.getInstance().error(e1.getMessage(), e1, Activator.PLUGIN_ID);
        }

      }

    });

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = toolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.section.setText("Versions");
    this.section.setDescription("Select a version to enable toolbar actions ");
    this.section.setExpanded(true);
    createForm(toolkit);
    this.section.setLayoutData(gridData);
    this.section.setClient(this.baseForm);
  }

  /**
   * @return
   */
  private boolean checkPIDCLock() {
    CurrentUserBO currUser = new CurrentUserBO();
    ApicDataBO apicBo = new ApicDataBO();
    boolean showUnlockPidcDialog = true;
    try {
      if (!apicBo.isPidcUnlockedInSession(this.pidcVersion) &&
          currUser.hasNodeWriteAccess(this.pidcVersion.getPidcId())) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        showUnlockPidcDialog = pidcActionSet.showUnlockPidcDialog(this.pidcVersion);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return showUnlockPidcDialog;
  }

  /**
   * @param toolkit This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.baseForm = toolkit.createForm(this.section);
    createToolBarAction();
    this.versionsTabViewer =
        new GridTableViewer(this.baseForm.getBody(), SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    this.tabSorter = new PIDCVersTabViewerSorter(this.versionsHandler);
    this.versionsTabViewer.getGrid().setLayoutData(gridData);

    this.versionsTabViewer.getGrid().setLinesVisible(true);
    this.versionsTabViewer.getGrid().setHeaderVisible(true);

    this.baseForm.getBody().setLayout(new GridLayout());
    createVersionsTabColumns();
    this.versionsTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    // changes For highlighting the PID version icdm-244


    this.versionsTabViewer.setInput(this.versionsHandler.getPidcVerMap().values());
    // ICDM-208
    addRightClickMenu();
    // ICDM-208
    versionTabViewerListener();
    this.versionsTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection = (IStructuredSelection) PidcVersionsPage.this.versionsTabViewer.getSelection();
        if (!selection.isEmpty()) {
          Object element = selection.getFirstElement();
          if (element instanceof PidcVersion) {
            PidcVersionsPage.this.selectedPidcVersion = (PidcVersion) element;
            // Get the user info
            CurrentUserBO currUser = new CurrentUserBO();
            try {
              enableDisableNewVersionAction(currUser);
              enableDisableLockAction();
              enableDisableEditVersionAction(currUser);

              ApicDataBO apicBo = new ApicDataBO();

              if (!apicBo.isPidcUnlockedInSession(PidcVersionsPage.this.selectedPidcVersion) &&
                  (currUser.hasApicWriteAccess() ||
                      currUser.hasNodeWriteAccess(PidcVersionsPage.this.selectedPidcVersion.getPidcId()))) {
                final PIDCActionSet pidcActionSet = new PIDCActionSet();
                pidcActionSet.showUnlockPidcDialog(PidcVersionsPage.this.selectedPidcVersion);
              }

            }
            catch (ApicWebServiceException exp) {
              CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
            }


          }
        }
      }

      private void enableDisableLockAction() {
        PidcVersionsPage.this.lockAction.setPidcVersion(PidcVersionsPage.this.selectedPidcVersion);
        PidcVersionsPage.this.lockAction.setActionProperties();
      }
    });

    this.versionsTabViewer.setComparator(this.tabSorter);
    PIDCEditor pidcEditor = (PIDCEditor) getEditor();
    SelectionProviderMediator selectionProviderMediator = pidcEditor.getSelectionProviderMediator();
    selectionProviderMediator.addViewer(this.versionsTabViewer);

    getSite().setSelectionProvider(selectionProviderMediator);

    Transfer[] transferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };
    this.versionsTabViewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE, transferTypes,
        new CustomDragListener(this.versionsTabViewer));

  }


  /**
   *
   */
  private void versionTabViewerListener() {
    this.versionsTabViewer.addDoubleClickListener(event -> {
      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      Object selectedNode = selection.getFirstElement();

      if (selectedNode instanceof PidcVersion) {
        PidcVersion pidcVers = (PidcVersion) selectedNode;

        PIDCActionSet actionset = new PIDCActionSet();
        actionset.openPIDCEditor(pidcVers, false);
      }
    });
  }

  /**
   * @param currUser NodeAccessRight
   * @throws ApicWebServiceException
   */
  private void enableDisableEditVersionAction(final CurrentUserBO currUser) throws ApicWebServiceException {
    // ICDM-2354
    getActionSet().getEditAction()
        .setEnabled((currUser != null) && currUser.hasNodeOwnerAccess(getPidcVersion().getPidcId()) &&
            !getPidcVersion().isDeleted() && (null != getSelectedPidcVersion()));
  }


  /**
   * @param currUser
   * @param currentUserRight NodeAccessRight
   * @throws ApicWebServiceException
   */
  private void enableDisableNewVersionAction(final CurrentUserBO currUser) throws ApicWebServiceException {
    // ICDM-2354
    getActionSet().getNewVersionAction()
        .setEnabled((currUser != null) && currUser.hasNodeOwnerAccess(getPidcVersion().getPidcId()) &&
            !getPidcVersion().isDeleted() && (null != getSelectedPidcVersion()));
  }

  /**
   * Defines the columns of the TableViewer
   */
  private void createVersionsTabColumns() {

    GridViewerColumn versionNameColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    versionNameColumn.getColumn().setText("Version Name");
    versionNameColumn.getColumn().setWidth(100);
    versionNameColumn.setLabelProvider(
        new VersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_0, this.pidcVersion, getDataHandler()));
    // Add column selection listener
    versionNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        versionNameColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_0, this.tabSorter, this.versionsTabViewer));

    GridViewerColumn versionDescColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    versionDescColumn.getColumn().setText("Version Description");
    versionDescColumn.getColumn().setWidth(150);
    versionDescColumn.setLabelProvider(
        new VersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_1, this.pidcVersion, getDataHandler()));
    // Add column selection listener
    versionDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        versionDescColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_1, this.tabSorter, this.versionsTabViewer));

    GridViewerColumn statusColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    statusColumn.getColumn().setText("Status");
    statusColumn.getColumn().setWidth(100);
    statusColumn.setLabelProvider(
        new VersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_2, this.pidcVersion, getDataHandler()));
    // Add column selection listener
    statusColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        statusColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_2, this.tabSorter, this.versionsTabViewer));

    GridViewerColumn activeColumn = new GridViewerColumn(this.versionsTabViewer, SWT.CHECK);
    activeColumn.getColumn().setText("Active");
    activeColumn.getColumn().setWidth(100);
    activeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();

        if (element instanceof PidcVersion) {
          PidcVersion vrsn = (PidcVersion) element;
          // Get the user info final
          CurrentUserBO currUser = new CurrentUserBO();
          boolean isModifiable = false;
          try {
            isModifiable = (null != currUser.getApicAccessRight()) &&
                currUser.hasNodeOwnerAccess(getPidcVersion().getPidcId()) && !getPidcVersion().isDeleted();
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }
          if (vrsn.getId().equals(PidcVersionsPage.this.versionsHandler.getActivePidcVersn().getId())) {
            GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
            // if version is active then it should not be possible to uncheck it
            gridItem.setCheckable(cell.getVisualIndex(), false);
          }
          else {
            GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            gridItem.setCheckable(cell.getVisualIndex(), isModifiable);
          }
        }
      }
    });
    activeColumn.setEditingSupport(new CheckEditingSupport(activeColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        PidcVersion pidVersion = (PidcVersion) arg0;
        PidcServiceClient client = new PidcServiceClient();
        Pidc pidc = PidcVersionsPage.this.versionsHandler.getPidc();
        pidc.setProRevId(pidVersion.getProRevId());
        try {
          client.update(pidc);
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }

      }
    });
    // Add column selection listener
    activeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        activeColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.tabSorter, this.versionsTabViewer));

    GridViewerColumn dateColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    dateColumn.getColumn().setText("Created Date");
    dateColumn.getColumn().setWidth(150);
    dateColumn.setLabelProvider(
        new VersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_4, this.pidcVersion, getDataHandler()));
    // Add column selection listener
    dateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        dateColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.tabSorter, this.versionsTabViewer));

    GridViewerColumn userColumn = new GridViewerColumn(this.versionsTabViewer, SWT.NONE);
    userColumn.getColumn().setText("Created User");
    userColumn.getColumn().setWidth(100);
    userColumn.setLabelProvider(
        new VersionsTableLabelProvider(CommonUIConstants.COLUMN_INDEX_5, this.pidcVersion, getDataHandler()));
    // Add column selection listener
    userColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        userColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_5, this.tabSorter, this.versionsTabViewer));
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    ToolBar toolbar = toolBarManager.createControl(this.section);
    this.actionSet = new PIDCActionSet();

    this.actionSet.createNewVersionAction(toolBarManager, this);
    this.actionSet.editVersionDetailsAction(toolBarManager, this);
    final Separator separator = new Separator();
    toolBarManager.add(separator);
    this.lockAction = new PIDCVersionLockAction(this.pidcVersion);
    toolBarManager.add(this.lockAction);
    toolBarManager.update(true);
    this.section.setTextClient(toolbar);
  }

  // ICDM-82
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

  // ICDM-208
  /**
   * This method adds right click menu for tableviewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr = new MenuManager();
    final CommonActionSet cmnActionSet = new CommonActionSet();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(mgr -> {
      // menu to add to scratchpad
      final IStructuredSelection selection =
          (IStructuredSelection) PidcVersionsPage.this.versionsTabViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (selection.size() != 0) && (firstElement instanceof PidcVersion)) {
        PidcVersion pidVer = (PidcVersion) firstElement;
        /* Add to scratchpad action */
        cmnActionSet.setAddToScrachPadAction(mgr, pidVer);
      }
    });
    // Create menu.
    final Menu menu = menuMgr.createContextMenu(this.versionsTabViewer.getGrid());
    this.versionsTabViewer.getGrid().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.versionsTabViewer);
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
    getActionSet().getNewVersionAction().run();
    getActionSet().getEditAction().run();
    getActionSet().getLockAction().run();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

  /**
   * @return PIDCActionSet
   */
  public PIDCActionSet getActionSet() {
    return this.actionSet;
  }


  /**
   * @return the selectedPidcVersion
   */
  public PidcVersion getSelectedPidcVersion() {
    return this.selectedPidcVersion;
  }


  /**
   * @return the PidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @return the versionsTabViewer
   */
  public GridTableViewer getVersionsTabViewer() {
    return this.versionsTabViewer;
  }


  /**
   * @param pidcVersion the PidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCVersionsDataHandler getDataHandler() {
    return this.versionsHandler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    if (null != this.versionsTabViewer) {
      this.versionsTabViewer.setInput(this.versionsHandler.getPidcVerMap().values());
      this.versionsTabViewer.refresh();
      IStructuredSelection structuredSelection = this.versionsTabViewer.getStructuredSelection();
      this.selectedPidcVersion = (PidcVersion) structuredSelection.getFirstElement();
    }
    if ((null != this.pidcVersion) && (null != this.nonScrollableForm)) {
      this.pidcVersion = this.versionsHandler.getPidcVerMap().get(this.pidcVersion.getId());
      this.nonScrollableForm.setText(this.pidcVersion.getName());
    }
    if ((null != this.versionsHandler) && (null != this.lockAction)) {
      this.lockAction.setPidcVersion(this.selectedPidcVersion);
      // refresh lock action
      if (null != this.selectedPidcVersion) {
        this.lockAction.setActionProperties();
      }
      this.considerPrvPidcVersBtn.setSelection(this.versionsHandler.getPidc().isInclRvwOfOldVers());
    }

  }


}

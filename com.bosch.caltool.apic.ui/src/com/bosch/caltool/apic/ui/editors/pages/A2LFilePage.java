/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCA2lToolBarActionSet;
import com.bosch.caltool.apic.ui.actions.PIDCActionSet;
import com.bosch.caltool.apic.ui.actions.SetSSdVersionAction;
import com.bosch.caltool.apic.ui.dialogs.A2LFileVersionDialog;
import com.bosch.caltool.apic.ui.dialogs.A2lFileDetailsDialog;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.PIDCEditorInput;
import com.bosch.caltool.apic.ui.sorter.A2lFileTabViewerSorter;
import com.bosch.caltool.apic.ui.table.filters.A2LFileFilters;
import com.bosch.caltool.apic.ui.table.filters.PIDCA2lFileToolBarFilters;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.a2l.UnmapA2LServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomGridTableViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author bru2cob
 */
public class A2LFilePage extends AbstractFormPage {

  /**
   * Constant for a2lfile page
   */
  private static final String A2L_FILES = "A2L Files";
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  /**
   * Editor instance
   */
  private final PIDCEditor editor;
  /**
   * PIDCard instance
   */
  private PidcVersion pidcVersion;

  private final PidcVersionBO handler;

  private PIDCA2lFileToolBarFilters toolBarFilters;

  /**
   * Define A2lMappingTable columns
   */
  private CustomGridTableViewer pidcA2lMappingTableViewer;

  private ToolBarManager toolbarManager;


  /**
   * @return the handler
   */
  public PidcVersionBO getHandler() {
    return this.handler;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCEditorInput getEditorInput() {
    return (PIDCEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcEditorDataHandler getDataHandler() {
    return getEditorInput().getDataHandler();
  }

  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * Defines AbstractViewerSorter
   */
  private A2lFileTabViewerSorter rightsTabSorter;

  /**
   * @return the rightsTabSorter
   */
  public A2lFileTabViewerSorter getRightsTabSorter() {
    return this.rightsTabSorter;
  }

  /**
   * A2lFileFilters instance
   */
  private A2LFileFilters a2lFileFilters;

  /**
   * @return the a2lFileFilters
   */
  public A2LFileFilters getA2lFileFilters() {
    return this.a2lFileFilters;
  }

  /**
   * selected pidc version
   */
  private Long pidcVersionSel;
  /**
   * Instance of selc a2l file
   */
  private final List<PidcA2lFileExt> selA2LFile = new ArrayList<>();
  /**
   * Instance of a2l mapping table
   */
  private A2LFileMappingTable a2lFileMappingTab;


  private final CurrentUserBO currentUser = new CurrentUserBO();
  private boolean isMappingsAvailable;

  /**
   * @return the a2lFileMappingTab
   */
  public A2LFileMappingTable getA2lFileMappingTab() {
    return this.a2lFileMappingTab;
  }


  /**
   * @return the selA2LFile
   */
  public List<PidcA2lFileExt> getSelA2LFile() {
    return this.selA2LFile;
  }


  /**
   * @return the pidcVersionSelected
   */
  public Long getPidcVersionSelected() {
    return this.pidcVersionSel;
  }

  /**
   * @param pidcVersionSel the pidcVersionSelected to set
   */
  public void setPidcVersionSelected(final Long pidcVersionSel) {
    this.pidcVersionSel = pidcVersionSel;
  }

  /**
   * @param editor instance
   * @param pidcVer instance
   */
  public A2LFilePage(final FormEditor editor, final PidcVersion pidcVer) {
    super(editor, A2L_FILES, A2L_FILES);
    this.editor = (PIDCEditor) editor;
    this.pidcVersion = pidcVer;
    this.handler = ((PIDCEditorInput) editor.getEditorInput()).getPidcVersionBO();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.toolbarManager;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.nonScrollableForm = this.editor.getToolkit().createForm(parent);
    this.nonScrollableForm.setText(this.pidcVersion.getName().replace("&", "&&"));
    // instead of editor.getToolkit().createScrolledForm(parent) in superclass
    // formToolkit is obtained from managed form to create form within section
    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // Create scrolled form
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    this.section.getDescriptionControl().setEnabled(false);

  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    final ToolBarManager toolBarManager = (ToolBarManager) this.nonScrollableForm.getToolBarManager();
    addHelpAction(toolBarManager);
    createSection(toolkit);
    this.composite.setLayoutData(gridData);

  }

  /**
   * This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = SectionUtil.getInstance().createSection(this.composite, toolkit, A2L_FILES);
    this.section.setLayoutData(gridData);
    createForm(toolkit);
    this.section.setClient(this.form);
  }


  /**
   * This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {


    this.rightsTabSorter = new A2lFileTabViewerSorter();
    this.a2lFileFilters = new A2LFileFilters();
    this.form = toolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());

    // create filter text
    this.filterTxt = TextUtil.getInstance().createFilterText(toolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    getFilterTxtSet().add(this.filterTxt);
    // add modify listener to the filter text
    addModifyListenerForFilterTxt();
    // create the table
    this.a2lFileMappingTab = new A2LFileMappingTable(this);
    this.a2lFileMappingTab.createA2lTable();

    this.pidcA2lMappingTableViewer = this.a2lFileMappingTab.getA2lTabViewer();

    this.toolBarFilters = new PIDCA2lFileToolBarFilters();
    this.pidcA2lMappingTableViewer.addFilter(this.toolBarFilters);

    createToolBarAction();

  }

  /**
   * Method to create tool bar filters
   */
  private void createToolBarAction() {
    this.toolbarManager = new ToolBarManager(SWT.FLAT);
    PIDCA2lToolBarActionSet toolBarActionSet =
        new PIDCA2lToolBarActionSet(this, getEditorSite().getActionBars().getStatusLineManager());
    toolBarActionSet.activeA2lFilesFilterAction(this.toolbarManager, this.toolBarFilters,
        this.pidcA2lMappingTableViewer);
    toolBarActionSet.inActiveA2lFilesFilterAction(this.toolbarManager, this.toolBarFilters,
        this.pidcA2lMappingTableViewer);

    final Separator separator = new Separator();
    this.toolbarManager.add(separator);

    this.toolbarManager.update(true);

    Composite toolbarComposite = this.editor.getToolkit().createComposite(this.section);
    toolbarComposite.setBackground(null);
    this.toolbarManager.createControl(toolbarComposite);
    this.section.setTextClient(toolbarComposite);
    addResetAllFiltersAction();
  }

  /**
   * Add reset filter button
   */
  private void addResetAllFiltersAction() {
    getFilterTxtSet().add(this.filterTxt);
    getRefreshComponentSet().add(this.pidcA2lMappingTableViewer);
    addResetFiltersAction();
  }

  /**
   * @return the form
   */
  public Form getForm() {
    return this.form;
  }


  /**
   * Add mouse down listener to the pidc attribute value edit column
   */
  public void addMouseDownListener() {
    this.a2lFileMappingTab.getA2lTabViewer().getGrid().addMouseListener(new MouseAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void mouseDown(final MouseEvent event) {

        final Point point = new Point(event.x, event.y);
        // Determine which row was selected
        final GridItem item = A2LFilePage.this.a2lFileMappingTab.getA2lTabViewer().getGrid().getItem(point);
        if ((item != null) && !item.isDisposed()) {
          final int columnIndex = GridTableViewerUtil.getInstance().getTabColIndex(event,
              A2LFilePage.this.a2lFileMappingTab.getA2lTabViewer());
          if (columnIndex == ApicUiConstants.COLUMN_INDEX_1) {
            // Determine which column was selected
            openVerSelcDialog(point, item, columnIndex, false);

          }
        }
      }
    });
  }

  /**
   * Add mouse down listener to the pidc attribute value edit column
   */
  public void addRightClickListener() {

    // Right click listener
    final MenuManager menuMgr = new MenuManager();
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void menuAboutToShow(final IMenuManager mgr) {
        mapToVersionAction(menuMgr);
        unmapA2lAction(menuMgr);
        menuMgr.add(new Separator());
        assignA2lFileDetailsAction(menuMgr);
        assignSoftwareVersion(menuMgr);
      }

      /**
       * new menu item for Assigning the Software version.
       *
       * @param menuMgr menuMgr
       */
      private void assignSoftwareVersion(final MenuManager menuMgr) {
        // Assign ssd version
        SetSSdVersionAction assignSSDSoftwareVersion = new SetSSdVersionAction(A2LFilePage.this);
        assignSSDSoftwareVersion.setText("Set SSD Software Version");
        menuMgr.add(assignSSDSoftwareVersion);
        DecorationOverlayIcon decoratedImage =
            new DecorationOverlayIcon(ImageManager.getInstance().getRegisteredImage(ImageKeys.A2LFILE_16X16),
                ImageManager.getImageDescriptor(ImageKeys.EDIT_12X12), IDecoration.TOP_LEFT);
        assignSSDSoftwareVersion.setImageDescriptor(decoratedImage);
        assignSSDSoftwareVersion.setEnabled(!A2LFilePage.this.getSelA2LFile().isEmpty());
      }

      /**
       * Unmap a2l from pidc version
       *
       * @param menuMgr
       */
      private void unmapA2lAction(final MenuManager menuMgr) {
        final Action unmappingAction = new Action() {

          @Override
          public void run() {
            try {
              UnmapA2LResponse unmapA2LResponse =
                  new UnmapA2LServiceClient().getRelatedDbEntries(A2LFilePage.this.selA2LFile.get(0).getId());
              UnmapA2lUserDialog unmapDialog =
                  new UnmapA2lUserDialog(A2LFilePage.this.a2lFileMappingTab.getA2lTabViewer().getControl().getShell(),
                      A2LFilePage.this.selA2LFile, unmapA2LResponse);
              unmapDialog.open();
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
            }
          }

        };
        unmappingAction.setText("Request A2L Unmapping");
        unmappingAction
            .setEnabled((A2LFilePage.this.handler.isModifiable() && (A2LFilePage.this.selA2LFile.size() == 1)) &&
                (A2LFilePage.this.selA2LFile.get(0).getPidcVersion() != null));
        final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
        unmappingAction.setImageDescriptor(imageDesc);
        menuMgr.add(unmappingAction);
      }

      /**
       */
      private void mapToVersionAction(final MenuManager menuMgr) {
        // Action for a2l mapping
        final Action a2lMappingAction = new Action() {

          @Override
          public void run() {

            // ICDM-2487 P1.27.101
            showUnlockPidcDialog();
            // ICDM-2354
            ApicDataBO apicBo = new ApicDataBO();
            if (apicBo.isPidcUnlockedInSession(A2LFilePage.this.pidcVersion)) {
              editTabItem(0, true);
            }
          }

        };
        a2lMappingAction.setText("Map to a PIDC Version");
        menuMgr.add(a2lMappingAction);
        final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_16X16);
        a2lMappingAction.setImageDescriptor(imageDesc);

        a2lMappingAction
            .setEnabled(!A2LFilePage.this.getSelA2LFile().isEmpty() && A2LFilePage.this.handler.isModifiable());

      }
    });

    final Menu menu = menuMgr.createContextMenu(this.a2lFileMappingTab.getA2lTabViewer().getControl());
    this.a2lFileMappingTab.getA2lTabViewer().getControl().setMenu(menu);
    // Register menu for extension.
    getSite().registerContextMenu(menuMgr, this.a2lFileMappingTab.getA2lTabViewer());
  }

  private void assignA2lFileDetailsAction(final MenuManager menuMgr) {
    final Action assignA2lDetailsAction = new Action() {

      @Override
      public void run() {
        // dialog to unlock PIDC
        showUnlockPidcDialog();
        // ICDM-2354
        ApicDataBO apicBo = new ApicDataBO();
        if (apicBo.isPidcUnlockedInSession(getPidcVersion())) {
          fillAdditionalDetailsForA2l();
        }
      }
    };
    assignA2lDetailsAction.setText("Select A2L File Name");
    menuMgr.add(assignA2lDetailsAction);
    DecorationOverlayIcon decoratedImage =
        new DecorationOverlayIcon(ImageManager.getInstance().getRegisteredImage(ImageKeys.A2LFILE_16X16),
            ImageManager.getImageDescriptor(ImageKeys.EDIT_12X12), IDecoration.TOP_LEFT);
    assignA2lDetailsAction.setImageDescriptor(decoratedImage);
    assignA2lDetailsAction
        .setEnabled(!A2LFilePage.this.getSelA2LFile().isEmpty() && A2LFilePage.this.handler.isModifiable());
  }


  private void fillAdditionalDetailsForA2l() {
    if (!A2LFilePage.this.handler.isModifiable()) {
      return;
    }
    List<PidcA2lFileExt> selA2LFiles = getSelA2LFile();
    if (selA2LFiles.size() > 1) {
      // Cannot assign alias names for more than 1 A2File
      return;
    }
    for (PidcA2lFileExt selectedFile : selA2LFiles) {
      if (selectedFile.getPidcA2l() == null) {
        CDMLogger.getInstance().errorDialog(
            "Alternate A2l File Names can only be selected for files which have been mapped atleast once.",
            Activator.PLUGIN_ID);
        return;
      }
    }

    // A2l file details dialog
    final A2lFileDetailsDialog detDialog =
        new A2lFileDetailsDialog(this.a2lFileMappingTab.getA2lTabViewer().getControl().getShell(), A2LFilePage.this);
    detDialog.open();
  }

  /**
   * @param columnIndex deines gridviewer column index
   * @param rightClickFlag
   */
  private void editTabItem(final int columnIndex, final boolean rightClickFlag) {

    // ICDM-2487 P1.27.101
    // check for access rights and also check if its column index 1
    if (checkAccessAndColumnInd(columnIndex, rightClickFlag)) {
      Set<Long> pidcA2lIdSet = fetchPidcA2lIds();
      this.isMappingsAvailable = false;
      // Check whether the selected pidcA2l has Review results or Workpackage definitions
      if (CommonUtils.isNotEmpty(pidcA2lIdSet)) {
        PidcA2lServiceClient pidcA2lServiceClient = new PidcA2lServiceClient();
        try {
          pidcA2lServiceClient.getPidcA2lAssignmentValidation(pidcA2lIdSet);
        }
        catch (ApicWebServiceException exp) {
          this.isMappingsAvailable = true;
          StringBuilder logMsg = new StringBuilder(exp.getMessage());

          if (multipleEdit(rightClickFlag, pidcA2lIdSet)) {
            logMsg.append("\n").append("Multiple edit not allowed in this case!");
            CDMLogger.getInstance().errorDialog(logMsg.toString(), exp, Activator.PLUGIN_ID);
            return;
          }
          logMsg.append("\n").append("Parameter to WP mappings can be copied from another A2L file.").append("\n")
              .append("Do you want to proceed?");
          MessageDialog infoMessageDialog = new MessageDialog(new Shell(), "Information", null, logMsg.toString(),
              MessageDialog.QUESTION_WITH_CANCEL, new String[] { "Yes", "No" }, 0);

          int returnVal = infoMessageDialog.open();
          if (returnVal != 0) {
            return;
          }
        }
      }
      // open the a2l file version selection dialog
      final A2LFileVersionDialog dialog =
          new A2LFileVersionDialog(this.a2lFileMappingTab.getA2lTabViewer().getControl().getShell(), A2LFilePage.this);
      dialog.open();
    }
    else {
      showInfoOrErrorDialog();
    }
  }

  /**
   * @param rightClickFlag
   * @param pidcA2lIdSet
   * @return
   */
  private boolean multipleEdit(final boolean rightClickFlag, final Set<Long> pidcA2lIdSet) {
    return rightClickFlag && (pidcA2lIdSet.size() > 1);
  }

  /**
   * @param columnIndex
   * @param rightClickFlag
   * @return
   */
  private boolean checkAccessAndColumnInd(final int columnIndex, final boolean rightClickFlag) {
    return ((columnIndex == ApicUiConstants.COLUMN_INDEX_1) || rightClickFlag) &&
        A2LFilePage.this.handler.isModifiable();
  }

  /**
   * @return
   */
  private Set<Long> fetchPidcA2lIds() {
    Set<Long> pidcA2lIdSet = new HashSet<>();
    for (PidcA2lFileExt a2lFileData : getSelA2LFile()) {
      if (null != a2lFileData.getPidcA2l()) {
        pidcA2lIdSet.add(a2lFileData.getPidcA2l().getId());
      }
    }
    return pidcA2lIdSet;
  }

  /**
   *
   */
  private void showInfoOrErrorDialog() {
    CDMLogger.getInstance().info(ApicUiConstants.EDIT_NOT_ALLOWED, Activator.PLUGIN_ID);

    try {
      NodeAccess nodeAccessRight = this.currentUser.getNodeAccessRight(this.pidcVersion.getPidcId());
      if ((null != this.currentUser.getApicAccessRight()) && (nodeAccessRight != null) && nodeAccessRight.isWrite()) {
        CDMLogger.getInstance().info(ApicUiConstants.NO_WRITE_ACCESS +
            this.handler.getPidcDataHandler().getPidcVersionInfo().getPidc().getName(), Activator.PLUGIN_ID);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method creates filter text
   */
  private void addModifyListenerForFilterTxt() {
    // Listener for filter text
    this.filterTxt.addModifyListener(event -> {
      final String text = A2LFilePage.this.filterTxt.getText().trim();
      A2LFilePage.this.a2lFileFilters.setFilterText(text);
      A2LFilePage.this.a2lFileMappingTab.getA2lTabViewer().refresh();
    });
  }


  /**
   * Opens the version selection dialog
   *
   * @param point point selected
   * @param item grid item
   * @param columnIndex
   */
  private void openVerSelcDialog(final Point point, final GridItem item, final int columnIndex,
      final boolean rightClickFlag) {
    // dialog to select version
    for (int i = 0; i < A2LFilePage.this.a2lFileMappingTab.getA2lTabViewer().getGrid().getColumnCount(); i++) {
      final Rectangle rect = item.getBounds(i);
      if (rect.contains(point)) {
        editTabItem(columnIndex, rightClickFlag);
        break;
      }
    }
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }


  /**
   * @param pidcVersion the pidVersion to set
   */
  public void setPidVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * show unlock Pidc dialog.
   */
  public void showUnlockPidcDialog() {
    // If PIDc version is locked in session,
    // show dialog to unlock PIDC
    ApicDataBO apicBo = new ApicDataBO();
    CurrentUserBO currUser = new CurrentUserBO();
    try {
      if (!apicBo.isPidcUnlockedInSession(A2LFilePage.this.pidcVersion) &&
          (currUser.hasApicWriteAccess() || currUser.hasNodeWriteAccess(A2LFilePage.this.pidcVersion.getPidcId()))) {
        final PIDCActionSet pidcActionSet = new PIDCActionSet();
        pidcActionSet.showUnlockPidcDialog(A2LFilePage.this.pidcVersion);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * for validating whether the user have write access
   *
   * @return boolean value
   * @throws ApicWebServiceException exception
   */
  public boolean validateWriteAccess() throws ApicWebServiceException {
    CurrentUserBO currUser = new CurrentUserBO();
    boolean hasNodeWriteAccess = currUser.hasNodeWriteAccess(A2LFilePage.this.pidcVersion.getPidcId());
    if (currUser.hasApicWriteAccess() || hasNodeWriteAccess) {
      return true;
    }
    CDMLogger.getInstance().errorDialog("You need atleast write access on the PIDC to select a SSD Software Version",
        Activator.PLUGIN_ID);
    return false;
  }

  @Override
  public void refreshUI(final DisplayChangeEvent dce) {
    if (this.a2lFileMappingTab != null) {
      this.a2lFileMappingTab.refreshTable();
    }
  }


  /**
   * @return the isMappingsAvailable
   */
  public boolean isMappingsAvailable() {
    return this.isMappingsAvailable;
  }


  /**
   * @return the toolBarFilters
   */
  public PIDCA2lFileToolBarFilters getToolBarFilters() {
    return this.toolBarFilters;
  }


  /**
   * @param toolBarFilters the toolBarFilters to set
   */
  public void setToolBarFilters(final PIDCA2lFileToolBarFilters toolBarFilters) {
    this.toolBarFilters = toolBarFilters;
  }


}

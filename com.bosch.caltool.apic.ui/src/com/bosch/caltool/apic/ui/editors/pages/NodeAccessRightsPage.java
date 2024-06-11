/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheet;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.AddNewADGroupAccessDialog;
import com.bosch.caltool.apic.ui.dialogs.AddNewUserAccessDialog;
import com.bosch.caltool.apic.ui.sorter.GroupNodeAccessGridTabViewerSorter;
import com.bosch.caltool.apic.ui.sorter.GroupUsersGridTabViewerSorter;
import com.bosch.caltool.apic.ui.sorter.NodeAccessGridTabViewerSorter;
import com.bosch.caltool.apic.ui.table.filters.NodeAccessRightsFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupNodeAccess;
import com.bosch.caltool.icdm.model.general.ActiveDirectoryGroupUser;
import com.bosch.caltool.icdm.model.user.IEditable;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.ActiveDirectoryGroupNodeAccessServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.menus.ContextMenuUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;


/**
 * NodeAccessRights page - applicable for all Editors
 *
 * @author gge6cob
 */
public class NodeAccessRightsPage extends AbstractFormPage {

  /**
   *
   */
  private static final String ALREADY_OWNER_MSG = "You already have owner access. Cannot request for access now.";
  /**
   *
   */
  private static final String ERROR_ACCESS_RIGHTS_CURRENT_USER =
      "Error while fetching the access rights of the current user ";
  /**
   * Column width of grid
   */
  private static final int COLWIDTH_OWNER_GRID = 30;
  /**
   * Column width of grid viewer
   */
  private static final int COLWIDTH_OWNER_GRID_VWR = 80;

  /**
   * Column width of grid
   */
  private static final int COLWIDTH_GRANT_GRID = 30;
  /**
   * Column width of grid viewer
   */
  private static final int COLWIDTH_GRANT_GRID_VWR = 80;
  /**
   * Column width of grid
   */
  private static final int COLWIDTH_WRITE_GRID = 30;
  /**
   * Column width of grid viewer
   */
  private static final int COLWIDTH_WRITE_GRID_VWR = 80;
  /**
   * Column width of grid
   */
  private static final int COLWIDTH_READ_GRID = 30;
  /**
   * Column width of grid viewer
   */
  private static final int COLWIDTH_READ_GRID_VWR = 80;
  /**
   * /** Column width of grid viewer
   */
  private static final int COLWIDTH_DEPT_GRID_VWR = 150;
  /**
   * Column index
   */
  private static final int COLINDEX_DEPT = 2;
  /**
   * Column width of grid viewer
   */
  private static final int COLWIDTH_USERID_GRID_VWR = 100;
  /**
   * Column index
   */
  private static final int COLINDEX_USERID = 1;
  /**
   * Column index
   */
  private static final int COLINDEX_USERNAME = 0;
  /**
   * Column width of grid viewer
   */
  private static final int COLWIDTH_USERNAME_GRID_VWR = 200;

  /**
   * Defines new user action in toolbar
   */
  private Action newUserAction;

  /**
   * Action instance for right click context menu
   */
  private Action rightClickMenuDeleteAction;

  /**
   * Editor instance
   */
  private final FormEditor editor;

  /**
   * Non scrollable form
   */
  private ScrolledForm scrolledForm;

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
   * GridTableViewer for access rights
   */
  private GridTableViewer rightsTabViewer;

  /**
   * Defines delete user action in toolbar
   */
  private Action deleteUserAction;

  /**
   * Defines request access action in toolbar
   */
  private Action requestAccessAction;

  /**
   * Defines AbstractViewerSorter
   */
  private NodeAccessGridTabViewerSorter rightsTabSorter;

  /**
   * Title
   */
  private String titleText = "";

  private final NodeAccessPageDataHandler nodeAccessBO;

  private Long selectedNodeAccessId;

  private IEditable editStatus;
  private NodeAccessRightsFilter nodeAccessFilter;
  private Text filterTxt;

  /*
   * ALM - 727289: Allow adding groups to access rights page of PIDCs
   */
  // New Variables needed for the above requirement
  /**
   * Section instance
   */
  private Section sectionADGrp;

  /**
   * Form instance
   */
  private Form formADGrp;

  /**
   * GridTableViewer for access rights
   */
  private GridTableViewer rightsADGrpTabViewer;

  /**
   * Filter Text for AD Group
   */
  private Text adGroupFilterTxt;

  /**
   * Defines new AD Group action in toolbar
   */
  private Action newADGroupAction;

  /**
   * Defines delete AD Group action in toolbar
   */
  private Action deleteAdGroupAction;

  private String pidcName;

  /**
   * Action instance for right click context menu
   */
  private Action rightClickMenuADGroupDeleteAction;

  /**
   * Filter Text for Group Users table
   */
  private Text filterTxtForGrpUsers;

  /**
   * Group Users table viewer
   */
  private GridTableViewer groupUsersTabViewer;
  private GroupNodeAccessGridTabViewerSorter groupRightsTabSorter;
  private GroupUsersGridTabViewerSorter groupUsersTabSorter;

  private SashForm mainComposite;

  private ActiveDirectoryGroupNodeAccess newlyCreatedAccess;

  /**
   * @return the lockStatus
   */
  public IEditable getEditStatus() {
    return this.editStatus;
  }


  /**
   * @param lockStatus the lockStatus to set
   */
  public void setEditStatus(final IEditable lockStatus) {
    this.editStatus = lockStatus;
  }

  /**
   * @param editor Editor
   * @param ndoeAccessBO NodeAccessBO
   */
  public NodeAccessRightsPage(final FormEditor editor, final NodeAccessPageDataHandler ndoeAccessBO) {
    super(editor, "Rights", CommonUIConstants.ACCESS_RIGHTS_PAGE);
    this.editor = editor;
    this.nodeAccessBO = ndoeAccessBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    this.pidcName = this.editor.getPartName().replace("&", "&&");
    this.scrolledForm = getEditor().getToolkit().createScrolledForm(parent);
    this.scrolledForm.setAlwaysShowScrollBars(true);
    Composite formBody = this.scrolledForm.getBody();
    formBody.setLayout(new GridLayout());
    formBody.setLayoutData(getGridData());
    this.scrolledForm
        .setText(com.bosch.caltool.icdm.common.util.CommonUtils.concatenate(this.pidcName, " - ", "Access Rights"));

    addHelpAction((ToolBarManager) this.scrolledForm.getToolBarManager());
    // Initialize sorter
    this.rightsTabSorter = new NodeAccessGridTabViewerSorter(this.nodeAccessBO);
    this.groupRightsTabSorter = new GroupNodeAccessGridTabViewerSorter();
    this.groupUsersTabSorter = new GroupUsersGridTabViewerSorter();
    if (CommonUtils.isNotEmptyString(this.nodeAccessBO.getTitle())) {
      this.titleText = this.nodeAccessBO.getTitle();
    }
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;

    this.mainComposite = new SashForm(formBody, SWT.VERTICAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(getGridData());

    // set the width of two section

    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);


  }

  /**
   * @return This method defines GridData
   */
  private GridData getGridData() {

    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    return gridData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.scrolledForm;
  }

  /**
   * This method creates form content
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    FormToolkit formToolkit = managedForm.getToolkit();
    createComposite(formToolkit);
    FormText descriptionCtrl = new FormText(this.section, SWT.READ_ONLY);
    descriptionCtrl.setText(this.titleText, false, false);
    descriptionCtrl.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    this.section.setDescriptionControl(descriptionCtrl);
    this.section.getDescriptionControl().setEnabled(true);
  }

  /**
   * This method initializes composite
   *
   * @param formToolkit2
   */
  private void createComposite(final FormToolkit toolkit) {
    createSection(toolkit);
    createAdGroupSection(toolkit);
    this.mainComposite.setWeights(new int[] { 4, 4 });
  }


  /**
   * This method initializes section
   *
   * @param toolkit
   */
  private void createSection(final FormToolkit toolkit) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.section = toolkit.createSection(this.mainComposite, ExpandableComposite.TITLE_BAR);
    this.section.setText("Access Rights (Individual Users)");
    this.section.setExpanded(true);
    this.section.setLayoutData(gridData);

    createForm(toolkit);
    this.section.setClient(this.form);
  }


  /**
   * This method initializes form
   *
   * @param toolkit
   */
  private void createForm(final FormToolkit toolkit) {
    this.form = toolkit.createForm(this.section);
    this.form.getBody().setLayout(new GridLayout());
    createUsersAccessRightViewer(toolkit);
    // add selection listener to properties view
    addSelectionListener();
  }

  /**
   * @param toolkit
   */
  private void createUsersAccessRightViewer(final FormToolkit toolkit) {
    createToolBarForUserAccessAction();
    this.filterTxt = toolkit.createText(this.form.getBody(), null, SWT.SINGLE | SWT.BORDER);
    createFilterTxt();
    this.rightsTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, GridDataUtil.getInstance().getGridData());

    createTabColumns();

    this.rightsTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.rightsTabViewer.setInput(getDataHandler().getNodeAccess());

    addRightClickMenu();
    addFilters();
    // Invoke TableViewer Column sorters
    invokeColumnSorter(this.rightsTabSorter);
    this.rightsTabViewer.getGrid().addFocusListener(new FocusListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void focusLost(final FocusEvent fLost) {
        // Not applicable
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(NodeAccessRightsPage.this.rightsTabViewer);
      }
    });
  }


  /**
   *
   */
  private void addSelectionListener() {
    this.rightsTabViewer.addSelectionChangedListener(event -> {

      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      setSelectedNodeAccesssPropertiesView(selection);
    });
  }

  /**
   * Method to set selection to properties view
   *
   * @param selection from nattable
   */
  private void setSelectedNodeAccesssPropertiesView(final IStructuredSelection selection) {
    IViewPart viewPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
        .findView(com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants.PROPERTIES_VIEW);
    if (viewPart != null) {
      PropertySheet propertySheet = (PropertySheet) viewPart;
      IPropertySheetPage page = (IPropertySheetPage) propertySheet.getCurrentPage();
      if (page != null) {
        page.selectionChanged(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor(),
            selection);
      }
    }
  }


  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter(final NodeAccessGridTabViewerSorter sorter) {
    this.rightsTabViewer.setComparator(sorter);
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    // Filter text for table
    this.filterTxt.setLayoutData(getFilterTxtGridData());
    this.filterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(event -> {
      final String text = NodeAccessRightsPage.this.filterTxt.getText().trim();
      NodeAccessRightsPage.this.nodeAccessFilter.setFilterText(text);
      NodeAccessRightsPage.this.rightsTabViewer.refresh();
    });
    // ICDM-183
    this.filterTxt.setFocus();
  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * Add filters for the table
   */
  private void addFilters() {
    this.nodeAccessFilter = new NodeAccessRightsFilter(getDataHandler());
    // Add TableViewer filter
    this.rightsTabViewer.addFilter(this.nodeAccessFilter);

  }


  /**
   * creates the columns of access rights table viewer
   */
  private void createTabColumns() {
    createUserNameColumn();

    createUserIDColumn();

    createDeptNameColumn();
    if (this.nodeAccessBO.isReadColApplicable()) {
      createReadAccessColumn();
    }

    createWriteAccessColumn();

    createGrantAccessColumn();

    createOwnerAccessColumn();

    ColumnViewerToolTipSupport.enableFor(this.rightsTabViewer, ToolTip.NO_RECREATE);

    this.rightsTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) NodeAccessRightsPage.this.rightsTabViewer.getSelection();
        boolean enableDelete = false;
        NodeAccessRightsPage.this.deleteUserAction.setEnabled(false);
        if ((selection != null) && (!selection.isEmpty())) {
          final Object element = selection.getFirstElement();
          if ((element instanceof NodeAccess) && NodeAccessRightsPage.this.nodeAccessBO.isModifiable()) {
            enableDelete = true;
          }
        }
        NodeAccessRightsPage.this.deleteUserAction.setEnabled(enableDelete);
      }
    });
  }


  /**
   * creates owner access rights column
   */
  private void createOwnerAccessColumn() {
    final GridColumn ownerGridCol = new GridColumn(this.rightsTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    ownerGridCol.setWidth(COLWIDTH_OWNER_GRID);
    ownerGridCol.setText("No");
    ownerGridCol.setSummary(false);
    final GridViewerColumn ownerColumn = new GridViewerColumn(this.rightsTabViewer, ownerGridCol);
    ownerColumn.getColumn().setText("Owner");
    ownerColumn.getColumn().setWidth(COLWIDTH_OWNER_GRID_VWR);
    ownerColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        updateColumn(cell, element);
      }

      /**
       * @param cell
       * @param element
       */
      private void updateColumn(final ViewerCell cell, final Object element) {
        if (element instanceof NodeAccess) {
          final NodeAccess nodeAccess = (NodeAccess) element;
          if (nodeAccess.getUserId() != null) {
            if (nodeAccess.isOwner()) {
              updateIfOwner(cell);
            }
            else {
              final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
              gridItem.setChecked(cell.getVisualIndex(), false);
              isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isOwnerModifiable(), gridItem);
            }
          }
        }
      }

      /**
       * @param cell
       */
      private void updateIfOwner(final ViewerCell cell) {
        final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        gridItem.setChecked(cell.getVisualIndex(), true);
        isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isOwnerModifiable(), gridItem);
        try {
          computeToolTipForRequestAccessRightsOption();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().debug(ERROR_ACCESS_RIGHTS_CURRENT_USER + e.toString(), e, Activator.PLUGIN_ID);
        }
      }
    });
    ownerColumn.setEditingSupport(new CheckEditingSupport(ownerColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        Object arg2 = arg1;
        if ((NodeAccessRightsPage.this.editStatus != null) && (!NodeAccessRightsPage.this.editStatus.canEditRights())) {
          arg2 = !((Boolean) arg1).booleanValue();
        }
        if (enableEdit(arg0, arg2, CommonUIConstants.COLUMN_INDEX_6, NodeAccessRightsPage.this.rightsTabViewer)) {
          setOwnerInfoEditValue(arg0, arg2);
        }
      }
    });

    try {
      computeToolTipForRequestAccessRightsOption();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().debug("Error while fetching the access rights for the current user: " + e.toString(), e,
          Activator.PLUGIN_ID);
    }
    ownerColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        ownerColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_6, this.rightsTabSorter, this.rightsTabViewer));
  }


  /**
   * creates grant access rights column
   */
  private void createGrantAccessColumn() {
    final GridColumn grantGridCol = new GridColumn(this.rightsTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    grantGridCol.setWidth(COLWIDTH_GRANT_GRID);
    grantGridCol.setText("No");
    grantGridCol.setSummary(false);
    final GridViewerColumn grantColumn = new GridViewerColumn(this.rightsTabViewer, grantGridCol);
    grantColumn.getColumn().setText("Grant");
    grantColumn.getColumn().setWidth(COLWIDTH_GRANT_GRID_VWR);
    grantColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {


        final Object element = cell.getElement();

        if (element instanceof NodeAccess) {
          final NodeAccess nodeAccess = (NodeAccess) element;
          if (nodeAccess.isGrant()) {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
          else {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
        }

      }
    });
    grantColumn.setEditingSupport(new CheckEditingSupport(grantColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        Object arg2 = arg1;
        if ((NodeAccessRightsPage.this.editStatus != null) && (!NodeAccessRightsPage.this.editStatus.canEditRights())) {
          arg2 = !((Boolean) arg1).booleanValue();
        }
        if (enableEdit(arg0, arg2, CommonUIConstants.COLUMN_INDEX_5, NodeAccessRightsPage.this.rightsTabViewer)) {
          setGrantEditValue(arg0, arg2);
        }
      }
    });

    grantColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        grantColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_5, this.rightsTabSorter, this.rightsTabViewer));
  }

  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
  }

  /**
   * iCDM-1522 <br>
   * creates read access rights column
   */
  private void createReadAccessColumn() {
    final GridColumn readGridCol = new GridColumn(this.rightsTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    readGridCol.setWidth(COLWIDTH_READ_GRID);
    readGridCol.setText("No");
    readGridCol.setSummary(false);
    final GridViewerColumn readColumn = new GridViewerColumn(this.rightsTabViewer, readGridCol);
    readColumn.getColumn().setText("Read");

    readColumn.getColumn().setWidth(COLWIDTH_READ_GRID_VWR);

    readColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {

        final Object element = cell.getElement();
        if (element instanceof NodeAccess) {
          final NodeAccess nodeAccess = (NodeAccess) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), nodeAccess.isRead());
          // read is not editable, to remove read access, the user can be deleted
          isEditable(cell, false, gridItem);
        }
      }

      @Override
      public String getToolTipText(final Object element) {
        return "To remove READ access for the user, Please DELETE the user from this access rights page!";
      }

    });

    readColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        readColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_3, this.rightsTabSorter, this.rightsTabViewer));
  }


  /**
   * creates write access rights column
   */
  private void createWriteAccessColumn() {
    GridColumn writeGridCol = new GridColumn(this.rightsTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    writeGridCol.setWidth(COLWIDTH_WRITE_GRID);
    writeGridCol.setText(ApicConstants.USED_NO_DISPLAY);
    writeGridCol.setSummary(false);
    GridViewerColumn writeColumn = new GridViewerColumn(this.rightsTabViewer, writeGridCol);
    writeColumn.getColumn().setText("Write");
    writeColumn.getColumn().setWidth(COLWIDTH_WRITE_GRID_VWR);
    writeColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {

        final Object element = cell.getElement();
        if (element instanceof NodeAccess) {
          final NodeAccess nodeAccess = (NodeAccess) element;
          if (nodeAccess.isWrite()) {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
          else {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
        }

      }
    });
    writeColumn.setEditingSupport(new CheckEditingSupport(writeColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {

        Object arg2 = arg1;

        if ((NodeAccessRightsPage.this.editStatus != null) && (!NodeAccessRightsPage.this.editStatus.canEditRights())) {
          arg2 = !((Boolean) arg1).booleanValue();
        }
        if (enableEdit(arg0, arg2, CommonUIConstants.COLUMN_INDEX_4, NodeAccessRightsPage.this.rightsTabViewer)) {
          setWriteEditingValue(arg0, arg2);
        }
      }
    });

    writeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        writeColumn.getColumn(), CommonUIConstants.COLUMN_INDEX_4, this.rightsTabSorter, this.rightsTabViewer));
  }

  /**
   * The confirmation dialog to remove access rights should be displayed based If the user having grant and owner rights
   * and removes access rights for himself
   *
   * @param accessRights node access rights
   * @param selectionValue checked/unchecked
   * @param colNum column number
   * @param accessRightsTabViewer table
   * @return true if rights can be removed
   */


  public boolean enableEdit(final Object accessRights, final Object selectionValue, final int colNum,
      final GridTableViewer accessRightsTabViewer) {
    boolean editOkay = true;
    boolean booleanValue = ((Boolean) selectionValue).booleanValue();
    NodeAccess accessRight = (NodeAccess) accessRights;
    String currentUserName;
    try {
      currentUserName = new CurrentUserBO().getUserName();
      if (CommonUtils.isEqual(new CurrentUserBO().getUserID(), accessRight.getUserId()) && !booleanValue &&
          checkUserAccess(accessRight) && !confirmationDialog()) {
        // if he gives cancel for removing access rights , the checkbox is checked again
        GridItem[] gridItems = accessRightsTabViewer.getGrid().getItems();
        for (GridItem gridItem : gridItems) {
          if (currentUserName.equalsIgnoreCase(gridItem.getText())) {
            gridItem.setCheckable(colNum, true);
            accessRightsTabViewer.refresh();
            editOkay = false;
            break;
          }
        }
      }

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

    return editOkay;
  }


  /**
   * @param accessRight
   * @return
   */
  private boolean checkUserAccess(final NodeAccess accessRight) {
    return accessRight.isOwner() && accessRight.isGrant();
  }

  /**
   * @return
   */
  private boolean confirmationDialog() {
    return MessageDialog.openConfirm(Display.getDefault().getActiveShell(), "Remove access",
        "You are about to remove the access rights for yourself.\n" + "Do you want to continue?");
  }

  /**
   * creates department name column
   */
  private void createDeptNameColumn() {
    final GridViewerColumn deptColumn = GridViewerColumnUtil.getInstance().createGridViewerColumn(this.rightsTabViewer,
        "Department", COLWIDTH_DEPT_GRID_VWR);
    deptColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return element instanceof NodeAccess ? getDataHandler().getUserDepartment(((NodeAccess) element).getId()) : "";
      }
    });

    deptColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(deptColumn.getColumn(), COLINDEX_DEPT, this.rightsTabSorter, this.rightsTabViewer));
  }


  /**
   * creates user id column
   */
  private void createUserIDColumn() {
    final GridViewerColumn userIdColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.rightsTabViewer, "NT-ID", COLWIDTH_USERID_GRID_VWR);
    userIdColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return element instanceof NodeAccess ? getDataHandler().getUserName(((NodeAccess) element).getId()) : "";
      }
    });

    userIdColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(userIdColumn.getColumn(), COLINDEX_USERID, this.rightsTabSorter, this.rightsTabViewer));
  }


  /**
   * create user name columnset
   */
  private void createUserNameColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.rightsTabViewer, "User Name", COLWIDTH_USERNAME_GRID_VWR);
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return element instanceof NodeAccess ? getDataHandler().getUserFullName(((NodeAccess) element).getId()) : "";
      }
    });

    userNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        userNameColumn.getColumn(), COLINDEX_USERNAME, this.rightsTabSorter, this.rightsTabViewer));
  }

  /**
   * This method will check for whether Function is modifiable or not .If it is modifiable it will checks the checkbox
   *
   * @param cell instance
   * @param isModifiable defines Function is modifiable or not
   * @param gridItem instance
   */
  private void isEditable(final ViewerCell cell, final boolean isModifiable, final GridItem gridItem) {
    gridItem.setCheckable(cell.getVisualIndex(), isModifiable);
  }


  /**
   * This method updates the write access for apic user in Node Access Rights table
   *
   * @param arg0
   * @param arg1
   */
  private void setWriteEditingValue(final Object arg0, final Object arg1) {
    final boolean booleanValue = ((Boolean) arg1).booleanValue();
    final NodeAccess accessRight = (NodeAccess) arg0;
    accessRight.setWrite(booleanValue);
    if (this.nodeAccessBO.getCanEditFlag()) {
      try {
        new NodeAccessServiceClient().update(accessRight);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog("Error updating access rights[write] for user : " +
            getDataHandler().getUserName(accessRight.getId()) + " - " + e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    this.rightsTabViewer.refresh();
  }

  /**
   * This method updates the grant access for apic user in Node Access Rights table
   *
   * @param arg0 instance
   * @param arg1 instance
   */
  private void setGrantEditValue(final Object arg0, final Object arg1) {
    final boolean booleanValue = ((Boolean) arg1).booleanValue();
    final NodeAccess accessRight = (NodeAccess) arg0;
    accessRight.setGrant(booleanValue);
    if (this.nodeAccessBO.getCanEditFlag()) {
      try {
        new NodeAccessServiceClient().update(accessRight);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog("Error updating access rights[grant] for user : " +
            getDataHandler().getUserName(accessRight.getId()) + " - " + e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    this.rightsTabViewer.refresh();
  }

  /**
   * This method updates the owner access for apic user in Node Access Rights table
   *
   * @param arg0 instance
   * @param arg1 instance
   */
  private void setOwnerInfoEditValue(final Object arg0, final Object arg1) {
    final boolean booleanValue = ((Boolean) arg1).booleanValue();
    final NodeAccess accessRight = (NodeAccess) arg0;
    accessRight.setOwner(booleanValue);
    if (this.nodeAccessBO.getCanEditFlag()) {
      try {
        new NodeAccessServiceClient().update(accessRight);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog("Error updating access rights[owner] for user : " +
            getDataHandler().getUserName(accessRight.getId()) + " - " + e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    this.rightsTabViewer.refresh();
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarForUserAccessAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.section);

    if (checkIfRequestAccessOptionApplicable()) {
      requestAccessRightsOption(toolBarManager);
    }


    addNewUserAction(toolBarManager);

    addDeleteUserActionToSection(toolBarManager);

    toolBarManager.update(true);

    this.section.setTextClient(toolbar);
  }

  /**
   * @return
   */
  private boolean checkIfRequestAccessOptionApplicable() {
    return CommonUtils.isEqual(this.nodeAccessBO.getNode().getClass(), MODEL_TYPE.USE_CASE.getTypeClass()) ||
        CommonUtils.isEqual(this.nodeAccessBO.getNode().getClass(), MODEL_TYPE.FC2WP_DEF.getTypeClass()) ||
        CommonUtils.isEqual(this.nodeAccessBO.getNode().getClass(), MODEL_TYPE.CDR_RULE_SET.getTypeClass()) ||
        CommonUtils.isEqual(this.nodeAccessBO.getNode().getClass(), MODEL_TYPE.CDR_FUNCTION.getTypeClass()) ||
        CommonUtils.isEqual(this.nodeAccessBO.getNode().getClass(), MODEL_TYPE.QUESTIONNAIRE.getTypeClass()) ||
        CommonUtils.isEqual(this.nodeAccessBO.getNode().getClass(), MODEL_TYPE.PIDC.getTypeClass()) ||
        CommonUtils.isEqual(this.nodeAccessBO.getNode().getClass(), MODEL_TYPE.ALIAS_DEFINITION.getTypeClass());
  }

  /**
   * creates add new user icon in the toolbar and handles the action
   *
   * @param toolBarManager
   */
  private void requestAccessRightsOption(final ToolBarManager toolBarManager) {
    // Create an action to request for access
    try {


      this.requestAccessAction = new Action("Request Access Rights", SWT.NONE) {

        @Override
        public void run() {

          new AccessRightsRequest(NodeAccessRightsPage.this.editor, getDataHandler()).requestAccessRights();
        }
      };
      // Set the image for add user action
      this.requestAccessAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.SEND_MAIL_16X16));
      this.requestAccessAction.setEnabled(true);

      // Check if the user has APIC WRITE and display tooltip information according to the user's access rights
      computeToolTipForRequestAccessRightsOption();
      toolBarManager.add(this.requestAccessAction);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().debug("Error while composing email for requesting access rights: " + e.toString(), e,
          Activator.PLUGIN_ID);
    }
  }


  /**
   * @throws ApicWebServiceException
   */
  private void computeToolTipForRequestAccessRightsOption() throws ApicWebServiceException {
    if (checkIfRequestAccessOptionApplicable()) {
      if (CommonUtils.isEqualIgnoreCase(ApicConstants.APIC_WRITE_ACCESS,
          this.nodeAccessBO.getCurrentUser().getApicAccessRight())) {
        computeTooltipForUsersWithApicWriteAccess();
      }
      else {
        NodeAccess currentUserAccess =
            getDataHandler().getCurrentUser().getNodeAccessRight(getDataHandler().getNodeId());
        if ((currentUserAccess != null) && currentUserAccess.isOwner()) {
          this.requestAccessAction.setToolTipText(ALREADY_OWNER_MSG);
          this.requestAccessAction.setEnabled(false);
        }
        else {
          this.requestAccessAction.setToolTipText("Request Access Rights");
          this.requestAccessAction.setEnabled(true);
        }
        // TODO Check if current user has owner access via groups assigned and set tooltip accordingly
      }
    }
  }


  /**
   * @throws ApicWebServiceException
   */
  private void computeTooltipForUsersWithApicWriteAccess() throws ApicWebServiceException {
    try {
      boolean userPresent = false;
      for (NodeAccess user : getDataHandler().getNodeAccess()) {
        if (CommonUtils.isEqual(user.getUserId(), this.nodeAccessBO.getCurrentUser().getUserID())) {
          userPresent = true;
          if (user.isOwner()) {
            this.requestAccessAction.setToolTipText(ALREADY_OWNER_MSG);
            this.requestAccessAction.setEnabled(false);
            break;
          }
          this.requestAccessAction.setToolTipText("You have APIC WRITE Access. You can give access rights yourself.");
          this.requestAccessAction.setEnabled(false);
        }
        if (!userPresent) {
          this.requestAccessAction.setToolTipText(
              "You have APIC WRITE Access. You can add yourself as a user and give access rights yourself.");
          this.requestAccessAction.setEnabled(false);
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().debug(ERROR_ACCESS_RIGHTS_CURRENT_USER + e.toString(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * creates add new user icon in the toolbar and handles the action
   *
   * @param toolBarManager
   */
  private void addNewUserAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    this.newUserAction = new Action("Add User", SWT.NONE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        if (NodeAccessRightsPage.this.nodeAccessBO.getNodeType().equals(MODEL_TYPE.PIDC)) {
          if (NodeAccessRightsPage.this.editStatus.canEditRights()) {
            AddNewUserAccessDialog addNewUserDialog = new AddNewUserAccessDialog(Display.getCurrent().getActiveShell(),
                NodeAccessRightsPage.this.nodeAccessBO, NodeAccessRightsPage.this);
            addNewUserDialog.open();
          }
        }
        else {
          AddNewUserAccessDialog addNewUserDialog = new AddNewUserAccessDialog(Display.getCurrent().getActiveShell(),
              NodeAccessRightsPage.this.nodeAccessBO, NodeAccessRightsPage.this);
          addNewUserDialog.open();
        }
      }
    };
    // Set the image for add user action
    this.newUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.newUserAction.setEnabled(this.nodeAccessBO.isModifiable());
    toolBarManager.add(this.newUserAction);

  }

  /**
   * creates delete user icon in the toolbar and handles the action
   *
   * @param toolBarManager
   */
  private void addDeleteUserActionToSection(final ToolBarManager toolBarManager) {
    // Create an action to delete the user
    this.deleteUserAction = new Action("Delete User", SWT.NONE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {

        final IStructuredSelection selection =
            (IStructuredSelection) NodeAccessRightsPage.this.rightsTabViewer.getSelection();
        if ((selection != null) && (!selection.isEmpty())) {
          final Object element = selection.getFirstElement();
          if ((element instanceof NodeAccess) && NodeAccessRightsPage.this.nodeAccessBO.isModifiable()) {
            deleteUserNodeAccess(element);
          }
        }

      }
    };
    // Set the image for delete the user
    this.deleteUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteUserAction.setEnabled(false);
    toolBarManager.add(this.deleteUserAction);
  }

  /**
   * This method deletes the Function user
   *
   * @param element
   */
  private void deleteUserNodeAccess(final Object element) {
    final NodeAccess accessRight = (NodeAccess) element;
    try {
      new NodeAccessServiceClient().delete(accessRight);
      refreshUserAccess();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error deleting access rights of user : " +
          getDataHandler().getUserName(accessRight.getId()) + " - " + e.getMessage(), e, Activator.PLUGIN_ID);
    }
    NodeAccessRightsPage.this.deleteUserAction.setEnabled(false);
  }

  /**
   */
  private void refreshData() {
    // Refresh data
    this.newUserAction.setEnabled(this.nodeAccessBO.isModifiable());
    if (this.newADGroupAction != null) {
      this.newADGroupAction.setEnabled(this.nodeAccessBO.isModifiable());
    }
    if (this.rightsTabViewer != null) {
      this.rightsTabViewer.setInput(getDataHandler().getNodeAccess());
      this.rightsTabViewer.refresh();
    }
    if (this.rightsADGrpTabViewer != null) {
      this.rightsADGrpTabViewer.setInput(getDataHandler().getNodeADGroupAccess());
      if (getNewlyCreatedAccess() != null) {
        this.rightsADGrpTabViewer.setSelection(new StructuredSelection(getNewlyCreatedAccess()));
      }
      else {
        this.rightsADGrpTabViewer.setSelection(null);
      }
      this.rightsADGrpTabViewer.refresh();
    }
    if (this.groupUsersTabViewer != null) {
      if (getNewlyCreatedAccess() != null) {
        setInputForGrpUsersTabViewer(getNewlyCreatedAccess().getAdGroup().getId());
      }
      else {
        this.groupUsersTabViewer.setInput(new ArrayList<ActiveDirectoryGroupUser>());
      }
      this.groupUsersTabViewer.refresh();
    }
    checkActionButtonsStatus();
    try {
      computeToolTipForRequestAccessRightsOption();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void checkActionButtonsStatus() {
    if (this.newADGroupAction != null) {
      this.newADGroupAction.setEnabled(this.nodeAccessBO.isModifiable());
    }
    if (this.newUserAction != null) {
      this.newUserAction.setEnabled(this.nodeAccessBO.isModifiable());
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.scrolledForm.getToolBarManager();
  }


  /**
   * @return the rightsTabViewer
   */
  public GridTableViewer getRightsTabViewer() {
    return this.rightsTabViewer;
  }


  /**
   * @return the deleteUserAction
   */
  public Action getDeleteUserAction() {
    return this.deleteUserAction;
  }

  /**
   * @return the newUserAction
   */
  public Action getNewUserAction() {
    return this.newUserAction;
  }

  private void addRightClickMenu() {
    final MenuManager menuMgr =
        ContextMenuUtil.getInstance().addRightClickMenu(NodeAccessRightsPage.this.rightsTabViewer);
    menuMgr.addMenuListener(mgr -> {


      IStructuredSelection selection = (IStructuredSelection) NodeAccessRightsPage.this.rightsTabViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if (((firstElement != null) && (selection.size() != 0)) && (firstElement instanceof NodeAccess)) {
        NodeAccessRightsPage.this.rightClickMenuDeleteAction = new Action() {

          @Override
          public void run() {
            deleteUserNodeAccess(firstElement);
          }
        };
        NodeAccessRightsPage.this.rightClickMenuDeleteAction.setText("Delete");
        NodeAccessRightsPage.this.rightClickMenuDeleteAction
            .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
        menuMgr.add(NodeAccessRightsPage.this.rightClickMenuDeleteAction);
        CurrentUserBO currentUser = new CurrentUserBO();
        try {
          NodeAccessRightsPage.this.rightClickMenuDeleteAction.setEnabled(currentUser.hasApicWriteAccess() ||
              currentUser.hasNodeGrantAccess(NodeAccessRightsPage.this.nodeAccessBO.getNodeId()));
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    });
  }

  /**
   * Sets the page title
   *
   * @param title String
   */
  public void setTitleText(final String title) {
    if ((this.scrolledForm != null) && CommonUtils.isNotEmptyString(title)) {
      this.scrolledForm.setText(title);
    }
  }

  /**
   * @return the selectedNodeAccessId
   */
  public Long getSelectedNodeAccessId() {
    return this.selectedNodeAccessId;
  }

  /**
   * @param selectedNodeAccessId the selectedNodeAccessId to set
   */
  public void setSelectedNodeAccessId(final Long selectedNodeAccessId) {
    this.selectedNodeAccessId = selectedNodeAccessId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NodeAccessPageDataHandler getDataHandler() {
    return this.nodeAccessBO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    refreshData();
  }

  /*
   * List of changes for introducing AD Group based Access Rights for a PIDC
   */
  // ALM - 727289: Allow adding groups to access rights page of PIDCs
  /**
   * @param toolkit
   */
  private void createAdGroupSection(final FormToolkit toolkit) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.sectionADGrp = toolkit.createSection(this.mainComposite, ExpandableComposite.TITLE_BAR);
    this.sectionADGrp.setText("Access Rights (AD Group)");
    this.sectionADGrp.setExpanded(true);
    this.sectionADGrp.setLayoutData(gridData);

    createADGroupForm(toolkit);
    this.sectionADGrp.setClient(this.formADGrp);
  }


  /**
   * This method initializes form
   *
   * @param toolkit
   */
  private void createADGroupForm(final FormToolkit toolkit) {
    this.formADGrp = toolkit.createForm(this.sectionADGrp);
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    this.formADGrp.getBody().setLayout(gridLayout);
    this.formADGrp.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());
    createADGroupAccessRightViewer(toolkit);
    createGroupUsersTabViewer(toolkit);
  }

  /**
   * @param toolkit
   */
  private void createADGroupAccessRightViewer(final FormToolkit toolkit) {
    createToolBarForADGroupAccessAction();
    Composite leftTableComposite = new Composite(this.formADGrp.getBody(), SWT.FILL);
    leftTableComposite.setLayout(new GridLayout());
    leftTableComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.adGroupFilterTxt = toolkit.createText(leftTableComposite, null, SWT.SINGLE | SWT.BORDER);
    createAdGroupFilterTxt();
    this.rightsADGrpTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(leftTableComposite,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, GridDataUtil.getInstance().getGridData());

    createADGroupTabColumns();

    this.rightsADGrpTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.rightsADGrpTabViewer.setInput(getDataHandler().getNodeADGroupAccess());
    this.rightsADGrpTabViewer.refresh();
    addADGroupViewerRightClickMenu();
    this.rightsADGrpTabViewer.addFilter(this.nodeAccessFilter);
    // Invoke TableViewer Column sorters
    invokeGroupColumnSorter(this.groupRightsTabSorter);
    this.rightsADGrpTabViewer.getGrid().addFocusListener(new FocusListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void focusLost(final FocusEvent fLost) {
        // Not applicable
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void focusGained(final FocusEvent fGained) {
        setStatusBarMessage(NodeAccessRightsPage.this.rightsADGrpTabViewer);
      }
    });
  }

  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarForADGroupAccessAction() {

    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.sectionADGrp);

    addNewADGroupAction(toolBarManager);

    addDeleteADGroupActionToSection(toolBarManager);

    toolBarManager.update(true);

    this.sectionADGrp.setTextClient(toolbar);
  }


  /**
   * This method creates filter text
   */
  private void createAdGroupFilterTxt() {
    // Filter text for table
    this.adGroupFilterTxt.setLayoutData(getFilterTxtGridData());
    this.adGroupFilterTxt.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.adGroupFilterTxt.addModifyListener(event -> {
      final String text = NodeAccessRightsPage.this.adGroupFilterTxt.getText().trim();
      NodeAccessRightsPage.this.nodeAccessFilter.setFilterText(text);
      NodeAccessRightsPage.this.rightsADGrpTabViewer.refresh();
    });
    this.adGroupFilterTxt.setFocus();
  }


  /**
   * creates add new AD Group icon in the toolbar and handles the action
   *
   * @param toolBarManager
   */
  private void addNewADGroupAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    this.newADGroupAction = new Action("Add AD Group", SWT.NONE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        if (NodeAccessRightsPage.this.nodeAccessBO.getNodeType().equals(MODEL_TYPE.PIDC)) {
          if (NodeAccessRightsPage.this.editStatus.canEditRights()) {
            setNewlyCreatedAccess(null);
            AddNewADGroupAccessDialog addNewADGroupDialog = new AddNewADGroupAccessDialog(
                Display.getCurrent().getActiveShell(), NodeAccessRightsPage.this.nodeAccessBO,
                NodeAccessRightsPage.this, NodeAccessRightsPage.this.pidcName);
            addNewADGroupDialog.open();
          }
        }
        else {
          setNewlyCreatedAccess(null);
          AddNewADGroupAccessDialog addNewADGroupDialog = new AddNewADGroupAccessDialog(
              Display.getCurrent().getActiveShell(), NodeAccessRightsPage.this.nodeAccessBO, NodeAccessRightsPage.this,
              NodeAccessRightsPage.this.pidcName);
          addNewADGroupDialog.open();
        }
      }
    };
    // Set the image for add user action
    this.newADGroupAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    this.newADGroupAction.setEnabled(this.nodeAccessBO.isModifiable());
    toolBarManager.add(this.newADGroupAction);

  }

  /**
   * creates delete user icon in the toolbar and handles the action
   *
   * @param toolBarManager
   */
  private void addDeleteADGroupActionToSection(final ToolBarManager toolBarManager) {
    // Create an action to delete the user
    this.deleteAdGroupAction = new Action("Delete AD Group", SWT.NONE) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {

        final IStructuredSelection selection =
            (IStructuredSelection) NodeAccessRightsPage.this.rightsADGrpTabViewer.getSelection();
        if ((selection != null) && (!selection.isEmpty())) {
          final Object element = selection.getFirstElement();
          if ((element instanceof ActiveDirectoryGroupNodeAccess)) {
            if (NodeAccessRightsPage.this.nodeAccessBO.getNodeType().equals(MODEL_TYPE.PIDC)) {
              if ((NodeAccessRightsPage.this.editStatus != null) &&
                  NodeAccessRightsPage.this.editStatus.canEditRights() &&
                  NodeAccessRightsPage.this.nodeAccessBO.isModifiable()) {
                deleteGroupNodeAccess(element);
              }
            }
            else {
              deleteGroupNodeAccess(element);
            }
          }
        }
      }
    };
    // Set the image for delete the user
    this.deleteAdGroupAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteAdGroupAction.setEnabled(false);
    toolBarManager.add(this.deleteAdGroupAction);

  }


  /**
   * creates the columns of access rights table viewer
   */
  private void createADGroupTabColumns() {
    createADGroupNameColumn();

    if (this.nodeAccessBO.isReadColApplicable()) {
      createADGroupReadAccessColumn();
    }

    createADGroupWriteAccessColumn();

    createADGroupGrantAccessColumn();

    createADGroupOwnerAccessColumn();

    ColumnViewerToolTipSupport.enableFor(this.rightsADGrpTabViewer, ToolTip.NO_RECREATE);

    this.rightsADGrpTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) NodeAccessRightsPage.this.rightsADGrpTabViewer.getSelection();
        boolean enableDelete = false;
        NodeAccessRightsPage.this.deleteAdGroupAction.setEnabled(false);
        if ((selection != null) && (!selection.isEmpty())) {
          final Object element = selection.getFirstElement();
          if ((element instanceof ActiveDirectoryGroupNodeAccess)) {
            enableDelete = NodeAccessRightsPage.this.nodeAccessBO.isModifiable();
            setInputForGrpUsersTabViewer(((ActiveDirectoryGroupNodeAccess) element).getAdGroup().getId());
          }
        }
        NodeAccessRightsPage.this.deleteAdGroupAction.setEnabled(enableDelete);
      }
    });
  }

  /**
   * create user name columnset
   */
  private void createADGroupNameColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.rightsADGrpTabViewer, "Group Display Name", COLWIDTH_USERNAME_GRID_VWR);
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return element instanceof ActiveDirectoryGroupNodeAccess
            ? "" + ((ActiveDirectoryGroupNodeAccess) element).getAdGroup().getGroupName() : "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTipText(final Object element) {
        return element instanceof ActiveDirectoryGroupNodeAccess
            ? "Common Name : " + ((ActiveDirectoryGroupNodeAccess) element).getAdGroup().getGroupSid() : "";
      }
    });

    userNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        userNameColumn.getColumn(), COLINDEX_USERNAME, this.groupRightsTabSorter, this.rightsADGrpTabViewer));
  }

  /**
   * creates owner access rights column ADGroup
   */
  private void createADGroupOwnerAccessColumn() {
    final GridColumn ownerGridCol = new GridColumn(this.rightsADGrpTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    ownerGridCol.setWidth(COLWIDTH_OWNER_GRID);
    ownerGridCol.setText("No");
    ownerGridCol.setSummary(false);
    final GridViewerColumn ownerColumn = new GridViewerColumn(this.rightsADGrpTabViewer, ownerGridCol);
    ownerColumn.getColumn().setText("Owner");
    ownerColumn.getColumn().setWidth(COLWIDTH_OWNER_GRID_VWR);
    ownerColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        updateColumn(cell, element);
      }

      /**
       * @param cell
       * @param element
       */
      private void updateColumn(final ViewerCell cell, final Object element) {
        if (element instanceof ActiveDirectoryGroupNodeAccess) {
          final ActiveDirectoryGroupNodeAccess nodeAccess = (ActiveDirectoryGroupNodeAccess) element;
          if (nodeAccess.isOwner()) {
            updateIfOwner(cell);
          }
          else {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isOwnerModifiable(), gridItem);
          }
        }
      }

      /**
       * @param cell
       */
      private void updateIfOwner(final ViewerCell cell) {
        final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
        gridItem.setChecked(cell.getVisualIndex(), true);
        isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isOwnerModifiable(), gridItem);
        try {
          computeToolTipForRequestAccessRightsOption();
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().debug(ERROR_ACCESS_RIGHTS_CURRENT_USER + e.toString(), e, Activator.PLUGIN_ID);
        }
      }
    });
    ownerColumn.setEditingSupport(new CheckEditingSupport(ownerColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        Object arg2 = arg1;
        if ((NodeAccessRightsPage.this.editStatus != null) && (!NodeAccessRightsPage.this.editStatus.canEditRights())) {
          arg2 = !((Boolean) arg1).booleanValue();
        }
        if (enableEditADGroup(arg0, arg2, CommonUIConstants.COLUMN_INDEX_4,
            NodeAccessRightsPage.this.rightsADGrpTabViewer)) {
          setOwnerInfoEditValueADGroup(arg0, arg2);
        }
      }
    });

    try {
      computeToolTipForRequestAccessRightsOption();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().debug("Error while fetching the access rights for the current user: " + e.toString(), e,
          Activator.PLUGIN_ID);
    }
    ownerColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(ownerColumn.getColumn(),
            CommonUIConstants.COLUMN_INDEX_4, this.groupRightsTabSorter, this.rightsADGrpTabViewer));
  }


  /**
   * creates grant access rights column ADGroup
   */
  private void createADGroupGrantAccessColumn() {
    final GridColumn grantGridCol = new GridColumn(this.rightsADGrpTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    grantGridCol.setWidth(COLWIDTH_GRANT_GRID);
    grantGridCol.setText("No");
    grantGridCol.setSummary(false);
    final GridViewerColumn grantColumn = new GridViewerColumn(this.rightsADGrpTabViewer, grantGridCol);
    grantColumn.getColumn().setText("Grant");
    grantColumn.getColumn().setWidth(COLWIDTH_GRANT_GRID_VWR);
    grantColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {


        final Object element = cell.getElement();

        if (element instanceof ActiveDirectoryGroupNodeAccess) {
          final ActiveDirectoryGroupNodeAccess nodeAccess = (ActiveDirectoryGroupNodeAccess) element;
          if (nodeAccess.isGrant()) {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
          else {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
        }

      }
    });
    grantColumn.setEditingSupport(new CheckEditingSupport(grantColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {
        Object arg2 = arg1;
        if ((NodeAccessRightsPage.this.editStatus != null) && (!NodeAccessRightsPage.this.editStatus.canEditRights())) {
          arg2 = !((Boolean) arg1).booleanValue();
        }
        if (enableEditADGroup(arg0, arg2, CommonUIConstants.COLUMN_INDEX_3,
            NodeAccessRightsPage.this.rightsADGrpTabViewer)) {
          setGrantEditValueADGroup(arg0, arg2);
        }
      }
    });

    grantColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(grantColumn.getColumn(),
            CommonUIConstants.COLUMN_INDEX_3, this.groupRightsTabSorter, this.rightsADGrpTabViewer));
  }

  /**
   * iCDM-1522 <br>
   * creates read access rights column ADGroup
   */
  private void createADGroupReadAccessColumn() {
    final GridColumn readGridCol = new GridColumn(this.rightsADGrpTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    readGridCol.setWidth(COLWIDTH_READ_GRID);
    readGridCol.setText("No");
    readGridCol.setSummary(false);
    final GridViewerColumn readColumn = new GridViewerColumn(this.rightsADGrpTabViewer, readGridCol);
    readColumn.getColumn().setText("Read");

    readColumn.getColumn().setWidth(COLWIDTH_READ_GRID_VWR);

    readColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {

        final Object element = cell.getElement();
        if (element instanceof ActiveDirectoryGroupNodeAccess) {
          final ActiveDirectoryGroupNodeAccess nodeAccess = (ActiveDirectoryGroupNodeAccess) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), nodeAccess.isRead());
          // read is not editable, to remove read access, the user can be deleted
          isEditable(cell, false, gridItem);
        }
      }

      @Override
      public String getToolTipText(final Object element) {
        return "To remove READ access for the group, Please DELETE the group from this access rights page!";
      }

    });

    readColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(readColumn.getColumn(),
            CommonUIConstants.COLUMN_INDEX_1, this.groupRightsTabSorter, this.rightsADGrpTabViewer));
  }


  /**
   * creates write access rights column ADGroup
   */
  private void createADGroupWriteAccessColumn() {
    GridColumn writeGridCol = new GridColumn(this.rightsADGrpTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    writeGridCol.setWidth(COLWIDTH_WRITE_GRID);
    writeGridCol.setText(ApicConstants.USED_NO_DISPLAY);
    writeGridCol.setSummary(false);
    GridViewerColumn writeColumn = new GridViewerColumn(this.rightsADGrpTabViewer, writeGridCol);
    writeColumn.getColumn().setText("Write");
    writeColumn.getColumn().setWidth(COLWIDTH_WRITE_GRID_VWR);
    writeColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {

        final Object element = cell.getElement();
        if (element instanceof ActiveDirectoryGroupNodeAccess) {
          final ActiveDirectoryGroupNodeAccess nodeAccess = (ActiveDirectoryGroupNodeAccess) element;
          if (nodeAccess.isWrite()) {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
          else {
            final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            isEditable(cell, NodeAccessRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
        }

      }
    });
    writeColumn.setEditingSupport(new CheckEditingSupport(writeColumn.getViewer()) {

      /**
       * {@inheritDoc}
       */
      @Override
      public void setValue(final Object arg0, final Object arg1) {

        Object arg2 = arg1;

        if ((NodeAccessRightsPage.this.editStatus != null) && (!NodeAccessRightsPage.this.editStatus.canEditRights())) {
          arg2 = !((Boolean) arg1).booleanValue();
        }
        if (enableEditADGroup(arg0, arg2, CommonUIConstants.COLUMN_INDEX_2,
            NodeAccessRightsPage.this.rightsADGrpTabViewer)) {
          setWriteEditingValueADGroup(arg0, arg2);
        }
      }
    });

    writeColumn.getColumn()
        .addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(writeColumn.getColumn(),
            CommonUIConstants.COLUMN_INDEX_2, this.groupRightsTabSorter, this.rightsADGrpTabViewer));
  }

  private void addADGroupViewerRightClickMenu() {
    final MenuManager menuMgr =
        ContextMenuUtil.getInstance().addRightClickMenu(NodeAccessRightsPage.this.rightsADGrpTabViewer);
    menuMgr.addMenuListener(mgr -> {


      IStructuredSelection selection =
          (IStructuredSelection) NodeAccessRightsPage.this.rightsADGrpTabViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if (((firstElement != null) && (selection.size() != 0)) &&
          (firstElement instanceof ActiveDirectoryGroupNodeAccess)) {
        NodeAccessRightsPage.this.rightClickMenuADGroupDeleteAction = new Action() {

          @Override
          public void run() {
            deleteGroupNodeAccess(firstElement);
          }
        };
        NodeAccessRightsPage.this.rightClickMenuADGroupDeleteAction.setText("Delete");
        NodeAccessRightsPage.this.rightClickMenuADGroupDeleteAction
            .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
        menuMgr.add(NodeAccessRightsPage.this.rightClickMenuADGroupDeleteAction);
        CurrentUserBO currentUser = new CurrentUserBO();
        try {
          NodeAccessRightsPage.this.rightClickMenuADGroupDeleteAction.setEnabled(currentUser.hasApicWriteAccess() ||
              currentUser.hasNodeGrantAccess(NodeAccessRightsPage.this.nodeAccessBO.getNodeId()));
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    });
  }


  /**
   * @return the rightsADGrpTabViewer
   */
  public GridTableViewer getRightsADGrpTabViewer() {
    return this.rightsADGrpTabViewer;
  }

  /**
   * The confirmation dialog to remove access rights should be displayed based If the user having grant and owner rights
   * and removes access rights for himself
   *
   * @param accessRights node access rights
   * @param selectionValue checked/unchecked
   * @param colNum column number
   * @param accessRightsTabViewer table
   * @return true if rights can be removed
   */


  public boolean enableEditADGroup(final Object accessRights, final Object selectionValue, final int colNum,
      final GridTableViewer accessRightsTabViewer) {
    boolean editOkay = true;
    boolean booleanValue = ((Boolean) selectionValue).booleanValue();
    ActiveDirectoryGroupNodeAccess accessRight = (ActiveDirectoryGroupNodeAccess) accessRights;
    String currentUserName;
    try {
      currentUserName = new CurrentUserBO().getUserName();
      if (!booleanValue && checkADGroupAccess(accessRight)) {
        GridItem[] gridItems = accessRightsTabViewer.getGrid().getItems();
        for (GridItem gridItem : gridItems) {
          if (currentUserName.equalsIgnoreCase(gridItem.getText())) {
            gridItem.setCheckable(colNum, true);
            accessRightsTabViewer.refresh();
            editOkay = false;
            break;
          }
        }
      }

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }

    return editOkay;
  }


  /**
   * @param accessRight
   * @return
   */
  private boolean checkADGroupAccess(final ActiveDirectoryGroupNodeAccess accessRight) {
    return accessRight.isOwner() && accessRight.isGrant();
  }

  /**
   * This method updates the write access for apic user in Node Access Rights table
   *
   * @param arg0
   * @param arg1
   */
  private void setWriteEditingValueADGroup(final Object arg0, final Object arg1) {
    final boolean booleanValue = ((Boolean) arg1).booleanValue();
    final ActiveDirectoryGroupNodeAccess accessRight = (ActiveDirectoryGroupNodeAccess) arg0;
    accessRight.setWrite(booleanValue);
    if (this.nodeAccessBO.getCanEditFlag()) {
      try {
        new ActiveDirectoryGroupNodeAccessServiceClient().update(accessRight);
        refreshUserAccess();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(
            "Error updating access rights[write] for group : " + accessRight.getId() + " - " + e.getMessage(), e,
            Activator.PLUGIN_ID);
      }
    }
    this.rightsADGrpTabViewer.refresh();
  }

  /**
   * This method updates the grant access for apic user in Node Access Rights table
   *
   * @param arg0 instance
   * @param arg1 instance
   */
  private void setGrantEditValueADGroup(final Object arg0, final Object arg1) {
    final boolean booleanValue = ((Boolean) arg1).booleanValue();
    final ActiveDirectoryGroupNodeAccess accessRight = (ActiveDirectoryGroupNodeAccess) arg0;
    accessRight.setGrant(booleanValue);
    if (this.nodeAccessBO.getCanEditFlag()) {
      try {
        new ActiveDirectoryGroupNodeAccessServiceClient().update(accessRight);
        refreshUserAccess();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(
            "Error updating access rights[grant] for Group : " + accessRight.getId() + " - " + e.getMessage(), e,
            Activator.PLUGIN_ID);
      }
    }
    this.rightsTabViewer.refresh();
  }

  /**
   * This method updates the owner access for apic user in Node Access Rights table
   *
   * @param arg0 instance
   * @param arg1 instance
   */
  private void setOwnerInfoEditValueADGroup(final Object arg0, final Object arg1) {
    final boolean booleanValue = ((Boolean) arg1).booleanValue();
    final ActiveDirectoryGroupNodeAccess accessRight = (ActiveDirectoryGroupNodeAccess) arg0;
    accessRight.setOwner(booleanValue);
    if (this.nodeAccessBO.getCanEditFlag()) {
      try {
        new ActiveDirectoryGroupNodeAccessServiceClient().update(accessRight);
        refreshUserAccess();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(
            "Error updating access rights[owner] for group : " + accessRight.getId() + " - " + e.getMessage(), e,
            Activator.PLUGIN_ID);
      }
    }
    this.rightsTabViewer.refresh();
  }

  /**
   * This method deletes the Function user
   *
   * @param element
   */
  private void deleteGroupNodeAccess(final Object element) {
    final ActiveDirectoryGroupNodeAccess accessRight = (ActiveDirectoryGroupNodeAccess) element;
    try {
      new ActiveDirectoryGroupNodeAccessServiceClient().delete(accessRight);
      refreshUserAccess();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(
          "Error deleting access rights of Group : " + accessRight.getAdGroup().getGroupName() + " - " + e.getMessage(),
          e, Activator.PLUGIN_ID);
    }
    NodeAccessRightsPage.this.deleteAdGroupAction.setEnabled(false);
  }


  /**
   * @throws ApicWebServiceException
   */
  private void refreshUserAccess() throws ApicWebServiceException {
    new CurrentUserBO().clearCurrentUserCacheADGroupDelete();
    refreshData();
    computeTooltipForUsersWithApicWriteAccess();
  }

  /**
   * @param toolkit
   */
  private void createGroupUsersTabViewer(final FormToolkit toolkit) {
    Composite rightTableComposite = new Composite(this.formADGrp.getBody(), SWT.FILL);
    rightTableComposite.setLayout(new GridLayout());
    rightTableComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.filterTxtForGrpUsers = toolkit.createText(rightTableComposite, null, SWT.SINGLE | SWT.BORDER);
    createFilterTxtForGroupUsers();
    this.groupUsersTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(rightTableComposite,
        SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, GridDataUtil.getInstance().getGridData());

    createGroupUserTabColumns();

    // Invoke TableViewer Column sorters
    invokeGroupUsersColumnSorter(this.groupUsersTabSorter);
    this.groupUsersTabViewer.addFilter(this.nodeAccessFilter);
    this.groupUsersTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.groupUsersTabViewer.setInput(new ArrayList<ActiveDirectoryGroupUser>());
  }

  /**
   * @param groupId ID
   */
  public void setInputForGrpUsersTabViewer(final long groupId) {
    try {
      List<ActiveDirectoryGroupUser> grpUsersList = getDataHandler().getADGroupUsersByGroupId(groupId);
      this.groupUsersTabViewer.setInput(grpUsersList);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().debug("Error while fetching the access rights for the selected group: " + e.toString(), e,
          Activator.PLUGIN_ID);
    }
  }

  /**
  *
  */
  private void createGroupUserTabColumns() {
    createGroupUserNameColumn();
    createGroupUserIDColumn();
    createGroupUserDeptColumn();
    createIsICDMUserColumn();
  }

  /**
   * create user name columnset
   */
  private void createGroupUserNameColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.groupUsersTabViewer, "User Name", COLWIDTH_USERNAME_GRID_VWR);
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return element instanceof ActiveDirectoryGroupUser ? ((ActiveDirectoryGroupUser) element).getGroupUserName()
            : "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    userNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        userNameColumn.getColumn(), COLINDEX_USERNAME, this.groupUsersTabSorter, this.groupUsersTabViewer));
  }

  /**
   * creates department name column
   */
  private void createIsICDMUserColumn() {
    final GridViewerColumn deptColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.groupUsersTabViewer, "iCDM User?", COLWIDTH_DEPT_GRID_VWR);
    deptColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return isIcdmUser(element) ? ApicConstants.USED_YES_DISPLAY : ApicConstants.USED_NO_DISPLAY;
      }

      /**
       * @param element
       */
      private boolean isIcdmUser(final Object element) {
        if (element instanceof ActiveDirectoryGroupUser) {
          return ((ActiveDirectoryGroupUser) element).getIsIcdmUser().equals("Y");
        }
        return false;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    deptColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(deptColumn.getColumn(), 3, this.groupUsersTabSorter, this.groupUsersTabViewer));
  }


  /**
   * creates user id column
   */
  private void createGroupUserIDColumn() {
    final GridViewerColumn userIdColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.groupUsersTabViewer, "NT-ID", COLWIDTH_USERID_GRID_VWR);
    userIdColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return element instanceof ActiveDirectoryGroupUser ? "" + ((ActiveDirectoryGroupUser) element).getUsername()
            : "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    userIdColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        userIdColumn.getColumn(), COLINDEX_USERID, this.groupUsersTabSorter, this.groupUsersTabViewer));
  }

  /**
   * creates user Dept column
   */
  private void createGroupUserDeptColumn() {
    final GridViewerColumn userDeptColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.groupUsersTabViewer, "Department", COLWIDTH_USERID_GRID_VWR);
    userDeptColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        return element instanceof ActiveDirectoryGroupUser
            ? "" + ((ActiveDirectoryGroupUser) element).getGroupUserDept() : "";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
      }
    });
    userDeptColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        userDeptColumn.getColumn(), COLINDEX_DEPT, this.groupUsersTabSorter, this.groupUsersTabViewer));
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxtForGroupUsers() {
    // Filter text for table
    this.filterTxtForGrpUsers.setLayoutData(getFilterTxtGridData());
    this.filterTxtForGrpUsers.setMessage(Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxtForGrpUsers.addModifyListener(event -> {
      final String text = NodeAccessRightsPage.this.filterTxtForGrpUsers.getText().trim();
      NodeAccessRightsPage.this.nodeAccessFilter.setFilterText(text);
      NodeAccessRightsPage.this.groupUsersTabViewer.refresh();
    });
    this.filterTxtForGrpUsers.setFocus();
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeGroupColumnSorter(final GroupNodeAccessGridTabViewerSorter sorter) {
    this.rightsADGrpTabViewer.setComparator(sorter);
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeGroupUsersColumnSorter(final GroupUsersGridTabViewerSorter sorter) {
    this.groupUsersTabViewer.setComparator(sorter);
  }


  /**
   * @return the newlyCreatedAccess
   */
  public ActiveDirectoryGroupNodeAccess getNewlyCreatedAccess() {
    return this.newlyCreatedAccess;
  }


  /**
   * @param newlyCreatedAccess the newlyCreatedAccess to set
   */
  public void setNewlyCreatedAccess(final ActiveDirectoryGroupNodeAccess newlyCreatedAccess) {
    this.newlyCreatedAccess = newlyCreatedAccess;
  }

}


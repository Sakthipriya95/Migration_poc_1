/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.editors.pages;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.AttributesAccessAddNewUserDialog;
import com.bosch.caltool.apic.ui.dialogs.EditAttributeDialog;
import com.bosch.caltool.apic.ui.sorter.NodeAccessGridTabViewerSorter;
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
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.NodeAccessDetails;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.NodeAccessServiceClient;
import com.bosch.rcputils.menus.ContextMenuUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author adn1cob
 */
public class AttributesRightsPage extends AbstractFormPage {

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
   * GridTableViewer instance
   */
  private GridTableViewer accessRghtsTabViewer;
  /**
   * GridViewerColumn instance for write information
   */
  private GridViewerColumn writeColumn;
  /**
   * GridViewerColumn instance for grant information
   */
  private GridViewerColumn grantColumn;

  /**
   * Defines AbstractViewerSorter
   */
  private NodeAccessGridTabViewerSorter rightsTabSorter;
  /**
   * Defines PIDC not defined attributes action
   */
  private Action addUserAction;
  /**
   * Action instance for Section
   */
  private Action deleteUserAction;
  /**
   * Action instance for right click context menu
   */
  private Action rightClickMenuDeleteAction;
  /**
   * Non scrollable form
   */
  private Form nonScrollableForm;
  private boolean isEnabled;

  private NodeAccessDetails nodeAccessDetails;
  private final NodeAccessPageDataHandler nodeAccessBO;

  private final SortedSet<NodeAccess> nodeAccessToUpdate = new TreeSet<>();
  private final SortedSet<NodeAccess> nodeAccessToCreate = new TreeSet<>();
  private final SortedSet<NodeAccess> nodeAccessToDelete = new TreeSet<>();
  private final String titleText;
  // Map to store ui edit made with respect to write access in case the node access is not yet created in database
  private final Map<Long, Boolean> writeAccessFlagMap = new HashMap<>();
  // Map to store ui edit made with respect to grant access in case the node access is not yet created in database
  private final Map<Long, Boolean> grantEditFlagMap = new HashMap<>();
  // Map to store write access as stored in DB
  private final Map<Long, Boolean> writeAccessInDb = new HashMap<>();
  // Map to store grant access as stored in DB
  private final Map<Long, Boolean> grantAccessInDb = new HashMap<>();
  // To maintain node Access objects which is deleted in ui even before saving it to DB
  private final Set<NodeAccess> nodeAccessRemoved = new HashSet<>();


  /**
   * @param titleText String
   * @param nodeAccessBO NodeAccessBO
   */
  public AttributesRightsPage(final String titleText, final NodeAccessPageDataHandler nodeAccessBO) {
    super("Rights", Messages.getString("RightsPage.label")); //$NON-NLS-1$ //$NON-NLS-2$
    this.nodeAccessBO = nodeAccessBO;
    this.titleText = titleText;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    // Create an ordinary non scrollable form on which widgets are built
    this.nonScrollableForm = new Form(parent, 0);
    this.nonScrollableForm.setText("Attribute : " + this.titleText);
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());

    ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Class<?> getMainUIClass() {
    return EditAttributeDialog.class;
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
    // Load access right data
    createComposite(formToolkit);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   * This method initializes composite
   */
  private void createComposite(final FormToolkit toolkit) {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.composite = this.nonScrollableForm.getBody();
    this.composite.setLayout(new GridLayout());
    createSection(toolkit);
    this.composite.setLayoutData(gridData);

  }

  /**
   * This method initializes section
   */
  private void createSection(final FormToolkit toolkit) {
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    this.section = SectionUtil.getInstance().createSection(this.composite, toolkit, "Access Rights");
    this.section.setLayoutData(gridData);
    createForm(toolkit);
    this.section.setClient(this.form);
  }


  /**
   * This method initializes form
   */
  private void createForm(final FormToolkit toolkit) {

    this.rightsTabSorter = new NodeAccessGridTabViewerSorter(this.nodeAccessBO);
    this.form = toolkit.createForm(this.section);

    this.form.getBody().setLayout(new GridLayout());

    createToolBarAction();

    GridData gdViewer = new GridData(SWT.FILL, SWT.FILL, true, true);

    this.accessRghtsTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL, gdViewer);

    createUserNameColumn();

    createUserIdColumn();

    createDeptColumn();

    createWriteColumn();

    createGrantColumn();

    addSelectionListener();

    disableCheckbox();

    this.accessRghtsTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    if (CommonUtils.isNotEmpty(this.nodeAccessBO.getNodeAccess())) {
      this.accessRghtsTabViewer.setInput(getDataHandler().getNodeAccess());
    }
    fillWriteGrantAccessInDb();
    // Invoke TableViewer Column sorters
    invokeColumnSorter(this.rightsTabSorter);

    // Adds Context menu to GridTableViewer
    if (this.nodeAccessBO.isModifiable()) {
      addRightClickMenu();
    }
  }


  /**
   *
   */
  private void fillWriteGrantAccessInDb() {
    for (NodeAccess nodeAccess : getDataHandler().getNodeAccess()) {
      this.writeAccessInDb.put(nodeAccess.getId(), nodeAccess.isWrite());
      this.grantAccessInDb.put(nodeAccess.getId(), nodeAccess.isGrant());
    }
  }

  /**
   *
   */
  private void addSelectionListener() {
    this.accessRghtsTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection =
            (IStructuredSelection) AttributesRightsPage.this.accessRghtsTabViewer.getSelection();
        if ((selection != null) && (!selection.isEmpty())) {
          Object element = selection.getFirstElement();
          if (element instanceof NodeAccess) {

            if (AttributesRightsPage.this.nodeAccessBO.isModifiable()) {
              AttributesRightsPage.this.deleteUserAction.setEnabled(true);
            }
          }
          else {
            AttributesRightsPage.this.deleteUserAction.setEnabled(false);
          }
        }
        else {
          AttributesRightsPage.this.deleteUserAction.setEnabled(false);
        }
      }
    });
  }

  /**
   *
   */
  private void disableCheckbox() {
    if (this.nodeAccessBO.isModifiable()) {
      this.writeColumn.getColumn().setCellSelectionEnabled(false);
      this.grantColumn.getColumn().setCellSelectionEnabled(false);
    }

  }


  /**
   * This method creates Section ToolBar actions
   */
  private void createToolBarAction() {

    ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    ToolBar toolbar = toolBarManager.createControl(this.section);

    addNewUserAction(toolBarManager);

    addDeleteUserActionToSection(toolBarManager);

    toolBarManager.update(true);

    this.section.setTextClient(toolbar);
  }

  private void createUserNameColumn() {
    GridViewerColumn userNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.accessRghtsTabViewer, "User Name", 200);
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return element instanceof NodeAccess ? getDataHandler().getUserFullName(((NodeAccess) element).getId()) : "";
      }
    });
    // Add column selection listener
    userNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(userNameColumn.getColumn(), 0, this.rightsTabSorter, this.accessRghtsTabViewer));
  }

  private void createUserIdColumn() {
    GridViewerColumn userIdColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.accessRghtsTabViewer, "User Id", 100);

    userIdColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return element instanceof NodeAccess ? getDataHandler().getUserName(((NodeAccess) element).getId()) : "";
      }
    });
    // Add column selection listener
    userIdColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(userIdColumn.getColumn(), 1, this.rightsTabSorter, this.accessRghtsTabViewer));
  }

  private void createDeptColumn() {
    GridViewerColumn deptColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.accessRghtsTabViewer, "Department", 100);
    deptColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        return element instanceof NodeAccess ? getDataHandler().getUserDepartment(((NodeAccess) element).getId()) : "";
      }
    });
    // Add column selection listener
    deptColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(deptColumn.getColumn(), 2, this.rightsTabSorter, this.accessRghtsTabViewer));

  }

  private void createWriteColumn() {
    GridColumn writeGridCol = new GridColumn(this.accessRghtsTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    writeGridCol.setWidth(35);
    writeGridCol.setText(ApicConstants.USED_NO_DISPLAY);
    writeGridCol.setSummary(false);
    this.writeColumn = new GridViewerColumn(this.accessRghtsTabViewer, writeGridCol);
    this.writeColumn.getColumn().setText("Write");
    this.writeColumn.getColumn().setWidth(100);
    this.writeColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        Object element = cell.getElement();
        if (element instanceof NodeAccess) {
          NodeAccess nodeAccess = (NodeAccess) element;
          Boolean flag = AttributesRightsPage.this.writeAccessFlagMap.get(nodeAccess.getId());
          if (((null != flag) && flag) || ((null == flag) && nodeAccess.isWrite())) {
            GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
            isEditable(cell, AttributesRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
          else {
            GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            isEditable(cell, AttributesRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
        }
      }
    });


    this.writeColumn.setEditingSupport(new CheckEditingSupport(this.writeColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        if (enableEdit(arg0, arg1, CommonUIConstants.COLUMN_INDEX_3, AttributesRightsPage.this.accessRghtsTabViewer)) {
          setWriteEditingValue(arg0, arg1);
        }
      }

    });


    this.writeColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.writeColumn.getColumn(), 3, this.rightsTabSorter, this.accessRghtsTabViewer));


  }

  private void createGrantColumn() {

    GridColumn grantGridCol = new GridColumn(this.accessRghtsTabViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    grantGridCol.setWidth(35);
    grantGridCol.setText(ApicConstants.USED_NO_DISPLAY);
    grantGridCol.setSummary(false);
    this.grantColumn = new GridViewerColumn(this.accessRghtsTabViewer, grantGridCol);
    this.grantColumn.getColumn().setText("Grant");
    this.grantColumn.getColumn().setWidth(100);
    this.grantColumn.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void update(final ViewerCell cell) {

        Object element = cell.getElement();
        if (element instanceof NodeAccess) {
          final NodeAccess nodeAccess = (NodeAccess) element;
          Boolean flag = AttributesRightsPage.this.grantEditFlagMap.get(nodeAccess.getId());

          if (((flag != null) && flag) || ((flag == null) && nodeAccess.isGrant())) {
            GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), true);
            isEditable(cell, AttributesRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
          else {
            GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
            gridItem.setChecked(cell.getVisualIndex(), false);
            isEditable(cell, AttributesRightsPage.this.nodeAccessBO.isModifiable(), gridItem);
          }
        }
      }
    });
    this.grantColumn.setEditingSupport(new CheckEditingSupport(this.grantColumn.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        if (enableEdit(arg0, arg1, CommonUIConstants.COLUMN_INDEX_4, AttributesRightsPage.this.accessRghtsTabViewer)) {
          setGrantEditValue(arg0, arg1);
        }
      }
    });
    this.grantColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(this.grantColumn.getColumn(), 4, this.rightsTabSorter, this.accessRghtsTabViewer));
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter(final AbstractViewerSorter sorter) {
    this.accessRghtsTabViewer.setComparator(sorter);
  }

  /**
   * This method creates non defined filter action
   *
   * @param toolBarManager
   */
  private void addNewUserAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    this.addUserAction = new Action(Messages.getString(IMessageConstants.ADDUSER_LABEL), SWT.NONE) {

      @Override
      public void run() {
        if (AttributesRightsPage.this.isEnabled) {
          AttributesAccessAddNewUserDialog addNewUserDialog = new AttributesAccessAddNewUserDialog(
              Display.getCurrent().getActiveShell(), AttributesRightsPage.this, AttributesRightsPage.this.nodeAccessBO);
          addNewUserDialog.open();
        }

      }
    };
    // Set the image for add user action
    this.addUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));

    toolBarManager.add(this.addUserAction);

    if (this.nodeAccessBO.isModifiable()) {
      this.isEnabled = true;
    }

    this.addUserAction.setEnabled(this.isEnabled);
  }


  /**
   * This method creates non defined filter action
   *
   * @param toolBarManager
   */
  private void addDeleteUserActionToSection(final ToolBarManager toolBarManager) {
    // Create an action to delete the user
    this.deleteUserAction = new Action(Messages.getString(IMessageConstants.DELETE_LABEL), SWT.NONE) {

      @Override
      public void run() {
        IStructuredSelection selection =
            (IStructuredSelection) AttributesRightsPage.this.accessRghtsTabViewer.getSelection();
        if ((selection != null) && (!selection.isEmpty())) {
          Object element = selection.getFirstElement();
          if ((element instanceof NodeAccess) && (AttributesRightsPage.this.isEnabled)) {
            deleteAttrAccess(element);
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
   * This method edits PIDC attribute write information
   *
   * @param arg0 instance
   * @param arg1 instance
   */
  private void setWriteEditingValue(final Object arg0, final Object arg1) {
    Boolean booleanValue = (Boolean) arg1;
    final NodeAccess accessRight = (NodeAccess) arg0;
    accessRight.setWrite(booleanValue);
    this.writeAccessFlagMap.put(accessRight.getId(), booleanValue);
    if (booleanValue.equals(false)) {
      if (!this.grantAccessInDb.containsKey(accessRight.getId())) {
        accessRight.setGrant(booleanValue);
      }
      else {
        accessRight.setGrant(this.grantAccessInDb.get(accessRight.getId()));
      }
      this.grantEditFlagMap.put(accessRight.getId(), booleanValue);
    }
    addToUpdateBuffer(accessRight);
    this.accessRghtsTabViewer.refresh();
  }

  /**
   * This method edits PIDC attribute grant information
   *
   * @param arg0 instance
   * @param arg1 instance
   */
  private void setGrantEditValue(final Object arg0, final Object arg1) {
    Boolean booleanValue = (Boolean) arg1;
    final NodeAccess accessRight = (NodeAccess) arg0;
    accessRight.setGrant(booleanValue);
    this.grantEditFlagMap.put(accessRight.getId(), booleanValue);
    if (booleanValue.equals(true)) {
      if (!this.writeAccessInDb.containsKey(accessRight.getId())) {
        accessRight.setWrite(booleanValue);
      }
      else {
        accessRight.setWrite(this.writeAccessInDb.get(accessRight.getId()));
      }
      this.writeAccessFlagMap.put(accessRight.getId(), booleanValue);
    }
    addToUpdateBuffer(accessRight);
    this.accessRghtsTabViewer.refresh();
  }

  /**
   * This method will check for whether PIDC attribute is modifiable or not if it is modifiable it will checks the
   * checkbox
   *
   * @param cell instance
   * @param isModifiable defines PIDC attribute is modifiable or not
   * @param gridItem instance
   */
  private void isEditable(final ViewerCell cell, final boolean isModifiable, final GridItem gridItem) {
    gridItem.setCheckable(cell.getVisualIndex(), isModifiable);
  }


  /**
   * This method adds right click menu to NodeAccesss GridTableViewer
   */
  private void addRightClickMenu() {
    final MenuManager menuMgr =
        ContextMenuUtil.getInstance().addRightClickMenu(AttributesRightsPage.this.accessRghtsTabViewer);
    menuMgr.addMenuListener(mgr -> {
      IStructuredSelection selection =
          (IStructuredSelection) AttributesRightsPage.this.accessRghtsTabViewer.getSelection();
      final Object firstElement = selection.getFirstElement();
      if ((firstElement != null) && (!selection.isEmpty()) && (firstElement instanceof NodeAccess)) {
        AttributesRightsPage.this.rightClickMenuDeleteAction = new Action() {

          @Override
          public void run() {
            deleteAttrAccess(firstElement);
          }
        };
        AttributesRightsPage.this.rightClickMenuDeleteAction.setText("Delete");
        AttributesRightsPage.this.rightClickMenuDeleteAction
            .setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
        menuMgr.add(AttributesRightsPage.this.rightClickMenuDeleteAction);
      }
    });
  }


  /**
   * This method deletes the PIDC user
   *
   * @param element
   */
  private void deleteAttrAccess(final Object element) {
    final NodeAccess accessRight = (NodeAccess) element;
    GridTableViewerUtil.getInstance().deleteSelectedItem(AttributesRightsPage.this.accessRghtsTabViewer);
    addToDeleteBuffer(accessRight);
    AttributesRightsPage.this.deleteUserAction.setEnabled(false);
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
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }


  /**
   * @return the accessRghtsTabViewer
   */
  public GridTableViewer getAccessRghtsTabViewer() {
    return this.accessRghtsTabViewer;
  }


  /**
   * @return the addUserAction
   */
  public Action getAddUserAction() {
    return this.addUserAction;
  }


  /**
   * @return the deleteUserAction
   */
  public Action getDeleteUserAction() {
    return this.deleteUserAction;
  }

  /**
   * Get the full name of the user The full name is the lastName concatenated with the firstName
   *
   * @param user user object
   * @return the users fullName
   */
  public String getFullName(final User user) {
    if (user != null) {
      final StringBuilder fullName = new StringBuilder();
      if (!CommonUtils.isEmptyString(user.getLastName())) {
        fullName.append(user.getLastName()).append(", ");
      }
      if (!CommonUtils.isEmptyString(user.getFirstName())) {
        fullName.append(user.getFirstName());
      }
      if (CommonUtils.isEmptyString(fullName.toString())) {
        fullName.append(user.getName());
      }
      return fullName.toString();
    }
    return ApicConstants.EMPTY_STRING;
  }

  /**
   * Update Grid Data
   */
  private void refreshGrid() {
    // Update grid
    if (CommonUtils.isNotEmpty(getDataHandler().getNodeAccess())) {

      SortedSet<NodeAccess> nodeAccessSet = new TreeSet<>();
      for (NodeAccess nodeAccess : getDataHandler().getNodeAccess()) {
        if (!this.nodeAccessToDelete.contains(nodeAccess) && !this.nodeAccessRemoved.contains(nodeAccess)) {
          nodeAccessSet.add(nodeAccess);
        }
      }
      this.accessRghtsTabViewer.setInput(nodeAccessSet);

    }
    this.accessRghtsTabViewer.refresh();

    // Refresh data
    AttributesRightsPage.this.addUserAction.setEnabled(this.nodeAccessBO.isModifiable());
  }

  /**
   * Add new user - to be saved to database on click of OK
   *
   * @param nodeAccessModel NodeAccess
   * @param user User
   */
  public void addToCreateBuffer(final NodeAccess nodeAccessModel, final User user) {
    this.nodeAccessBO.setAttrNodeAccess(nodeAccessModel);
    this.nodeAccessBO.setAttrUser(user);
    this.nodeAccessToCreate.add(nodeAccessModel);
    refreshGrid();
  }

  private void addToDeleteBuffer(final NodeAccess nodeAccessModel) {
    if (!this.nodeAccessToCreate.contains(nodeAccessModel)) {
      this.nodeAccessToDelete.add(nodeAccessModel);
    }
    // Newly added record node access ,which is not yet created in database
    if (!this.nodeAccessToCreate.isEmpty() && this.nodeAccessToCreate.contains(nodeAccessModel)) {
      this.nodeAccessToCreate.remove(nodeAccessModel);
      this.nodeAccessRemoved.add(nodeAccessModel);
    }
    if (this.nodeAccessToUpdate.contains(nodeAccessModel)) {
      this.nodeAccessToUpdate.remove(nodeAccessModel);
    }
    refreshGrid();
  }

  private void addToUpdateBuffer(final NodeAccess nodeAccessModel) {
    if (!isNodeAccessinCreateBuffer(nodeAccessModel)) {
      this.nodeAccessToUpdate.add(nodeAccessModel);
    }

  }

  // Check if updated node access object is yet to be created in database
  /**
   * @return true if node acces object is present in create buffer
   */
  private boolean isNodeAccessinCreateBuffer(final NodeAccess nodeAccessModel) {
    for (NodeAccess nodeAccess : this.nodeAccessToCreate) {
      Long id = nodeAccess.getId();
      if (id.equals(nodeAccessModel.getId())) {
        this.nodeAccessToCreate.remove(nodeAccess);
        this.nodeAccessToCreate.add(nodeAccessModel);
        return true;
      }
    }
    return false;
  }

  /**
   * @return NodeAccessDetails
   */
  public NodeAccessDetails getNodeAccesssWS() {
    NodeAccessDetails ret = null;
    try {
      ret = new NodeAccessServiceClient().getNodeAccessDetailsByNode(this.nodeAccessBO.getNodeType(),
          this.nodeAccessBO.getNodeId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error in retrieving access rights data : " + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
    return ret;
  }

  /**
   * Save all buffer changes to database
   */
  public void saveUpdate() {
    if (CommonUtils.isNotEmpty(this.nodeAccessToCreate)) {
      this.nodeAccessToCreate.forEach(this::createNodeAccessRight);
    }
    if (CommonUtils.isNotEmpty(this.nodeAccessToUpdate)) {
      this.nodeAccessToUpdate.forEach(this::updateNodeAccessRight);
    }
    if (CommonUtils.isNotEmpty(this.nodeAccessToDelete)) {
      this.nodeAccessToDelete.forEach(this::deleteNodeAccessRight);
    }
  }

  /**
   * Update NodeAccess right
   *
   * @param accessRight NodeAccess
   */
  public void updateNodeAccessRight(final NodeAccess accessRight) {
    try {
      new NodeAccessServiceClient().update(accessRight);
    }
    catch (ApicWebServiceException e) {
      User user = this.nodeAccessDetails.getUserMap().get(accessRight.getUserId());
      CDMLogger.getInstance().errorDialog("Error in updating access rights of user : " + user.getName(), e,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Remove NodeAccess right
   *
   * @param accessRight NodeAccess
   */
  public void deleteNodeAccessRight(final NodeAccess accessRight) {
    try {
      new NodeAccessServiceClient().delete(accessRight);
    }
    catch (ApicWebServiceException e) {
      User user = this.nodeAccessDetails.getUserMap().get(accessRight.getUserId());
      CDMLogger.getInstance().errorDialog("Error in removing access rights of user : " + user.getName(), e,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Remove NodeAccess right
   *
   * @param accessRight NodeAccess
   */
  public void createNodeAccessRight(final NodeAccess accessRight) {
    try {
      new NodeAccessServiceClient().create(accessRight);
    }
    catch (ApicWebServiceException e) {
      User user = this.nodeAccessDetails.getUserMap().get(accessRight.getUserId());
      CDMLogger.getInstance().errorDialog("Error in adding access rights of user : " + user.getName(), e,
          Activator.PLUGIN_ID);
    }
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
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
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
   * {@inheritDoc}
   */
  @Override
  public NodeAccessPageDataHandler getDataHandler() {
    return this.nodeAccessBO;
  }


  /**
   * @return the nodeAccessToUpdate
   */
  public Set<NodeAccess> getNodeAccessToUpdate() {
    return new HashSet<>(this.nodeAccessToUpdate);
  }


  /**
   * @return the nodeAccessToCreate
   */
  public Set<NodeAccess> getNodeAccessToCreate() {
    return new HashSet<>(this.nodeAccessToCreate);
  }

  
  /**
   * @return the nodeAccessToDelete
   */
  public Set<NodeAccess> getNodeAccessToDelete() {
    return new HashSet<>(nodeAccessToDelete);
  }

}
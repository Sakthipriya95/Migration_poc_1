/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.providers.BoschUserGridTableLabelProvider;
import com.bosch.caltool.icdm.common.ui.sorter.BoschUserGridTableSorter;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lRespBoschGroupUser;
import com.bosch.caltool.icdm.model.a2l.A2lRespMaintenanceData;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * The Class AddEditUserDetailsDialog.
 *
 * @author apj4cob
 */
public class AddEditUserDetailsDialog extends AbstractDialog {

  /** The edit flag. */
  private boolean editFlag;

  /** The sel pidc wp resp. */
  private A2lResponsibility selA2lResp;

  /** The top. */
  private Composite top;

  /** The form toolkit. */
  private FormToolkit formToolkit;


  /** The first name. */
  private Text firstName;

  /** The dep name. */
  private Text depName;

  /** The last name. */
  private Text lastName;

  private Section boschUserSection;

  private GridTableViewer boschUserTableViewer;

  private Action deleteUserAction;

  private final SortedSet<A2lRespBoschGroupUser> a2lRespBoschGroupUsers = new TreeSet<>();

  private final SortedSet<A2lRespBoschGroupUser> newlyAddedA2lRespBoschGroupUsers = new TreeSet<>();

  private final List<A2lRespBoschGroupUser> a2lRespBoschGroupUsersToDelete = new ArrayList<>();

  private final SortedSet<User> selectedBoschUserList = new TreeSet<>();

  private BoschUserGridTableSorter sorter;

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  /**
   * ControlDecoration - Last Name
   */
  protected ControlDecoration lastNmeDecortaion;
  /**
   * Checkbox for deleted flag
   */
  private Button deletedButton;

  /** The new and edit form. */
  private Form form;

  /** The new and edit section. */
  private Section section;

  /** The save btn. */
  private Button saveBtn;

  /** The wp resp type . */
  private final WpRespType wpRespType;

  /** The pidc id. */
  private final Long pidcId;

  /** The a 2 l wp info bo. */
  private final A2LWPInfoBO a2lWpInfoBo;

  private Button saveAgainBtn;

  private final ArrayList<A2lResponsibility> savedData = new ArrayList<>();

  private WorkPkgResponsibilityBO wpRespBO;

  /** The Constant COL. */
  private static final int COL = 2;

  /** The Constant TITLE. */
  private static final String TITLE_NON_BOSCH = "Add new Non-Bosch User";

  private static final String TITLE_BOSCH_DEPT = "Add new Bosch Department";

  /** The Constant TITLE. */
  private static final String ADD_DEPARTMENT = "Add Department";

  /** The Constant TITLE. */
  private static final String EDIT_DEPARTMENT = "Edit Department";

  /**
   * Instantiates a new adds the edit user details dialog(in A2lEditor).
   *
   * @param parentShell Shell
   * @param wpRespType the wp resp type id
   * @param selA2lResp Selected A2lResponsibility
   * @param a2lWpInfoBo Dialog
   */
  public AddEditUserDetailsDialog(final Shell parentShell, final WpRespType wpRespType,
      final A2lResponsibility selA2lResp, final A2LWPInfoBO a2lWpInfoBo) {
    super(parentShell);
    this.wpRespType = wpRespType;
    this.a2lWpInfoBo = a2lWpInfoBo;
    this.pidcId = a2lWpInfoBo.getPidcA2lBo().getPidcVersion().getPidcId();
    this.editFlag = (selA2lResp != null);
    this.selA2lResp = selA2lResp;
  }

  /**
   * Instantiates a new adds the edit user details dialog(in A2lEditor).
   *
   * @param parentShell Shell
   * @param wpRespType2 WpRespType
   * @param a2lResp A2lResponsibility
   * @param wpRespBO WorkPkgResponsibilityBO
   */
  public AddEditUserDetailsDialog(final Shell parentShell, final WpRespType wpRespType2,
      final A2lResponsibility a2lResp, final WorkPkgResponsibilityBO wpRespBO) {
    super(parentShell);
    this.wpRespType = wpRespType2;
    this.a2lWpInfoBo = null;
    this.wpRespBO = wpRespBO;
    this.pidcId = wpRespBO.getPidcId();
    this.editFlag = (a2lResp.getId() != null);
    this.selA2lResp = a2lResp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    getDisplayMsg();
    return contents;
  }

  /**
   *
   */
  private void getDisplayMsg() {
    String warningMsg = null;
    try {
      warningMsg = new CommonDataBO().getMessage("A2L", "INVALID_WP_RESP_ERROR");
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    if (isNonBosch()) {
      setTitle("Dialog to create, edit non-Bosch Users");
      setMessage("Atleast one field is mandatory\n" + warningMsg, IMessageProvider.INFORMATION);
    }
    else {
      setTitle("Dialog to create, edit Department Name");
      setMessage("'Department' is mandatory\n" + warningMsg, IMessageProvider.INFORMATION);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    String title = isNonBosch() ? TITLE_NON_BOSCH : TITLE_BOSCH_DEPT;
    newShell.setText(title);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
    return this.top;
  }


  /**
   * Creates the composite.
   */
  private void createComposite() {
    Composite composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    composite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    composite.setLayoutData(gridData);
    createSection(composite);
    if (!isNonBosch()) {
      createBoschUsersSection(composite);
    }
  }


  /**
   * Gets the form toolkit.
   *
   * @return the form toolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * Creates the last name control.
   *
   * @param comp the comp
   */
  private void createLastNameControl(final Composite comp) {
    createLabelControl(comp, "Last Name");
    this.lastName = createTextFileld(comp);
    if (isNonBosch()) {
      this.lastName.setEnabled(true);
      this.lastName.setEditable(true);
    }
    else {
      this.lastName.setEnabled(false);
    }
    this.lastName.addModifyListener(event -> enableButtons());
  }

  /**
   * Creates the label control.
   *
   * @param comp the comp
   * @param lblName the lbl name
   */
  private void createLabelControl(final Composite comp, final String lblName) {
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, lblName);
  }

  /**
   * Creates the new and edit form.
   */
  private void createForm() {
    this.form = this.formToolkit.createForm(this.section);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = COL;
    this.form.getBody().setLayout(gridLayout1);
    this.form.getBody().setLayoutData(gridData);
    createLastNameControl(this.form.getBody());
    createFirstNameControl(this.form.getBody());
    createDepNameControl(this.form.getBody());
    createDeletedCheckBox(this.form.getBody());
    new Label(this.form.getBody(), SWT.NONE);
    new Label(this.form.getBody(), SWT.NONE);
    if (this.editFlag) {
      prepopulate();
    }

  }

  private void createBoschUsersSection(final Composite composite1) {

    this.boschUserSection = SectionUtil.getInstance().createSection(composite1, this.formToolkit, "");
    this.boschUserSection.setExpanded(true);
    this.boschUserSection.setText("List of users in this Bosch Group/Department (optional)");


    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;

    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.boschUserSection.setLayoutData(gridData);
    this.boschUserSection.setLayout(gridLayout);

    this.sorter = new BoschUserGridTableSorter(this.a2lWpInfoBo.getA2lResponsibilityModel());

    createBoschUserTableViewer(this.boschUserSection);

    createToolBarAction();

  }

  /**
   * Create grid table viewer
   */
  private void createBoschUserTableViewer(final Composite composite) {
    this.boschUserTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(composite.getParent(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
        GridDataUtil.getInstance().getHeightHintGridData(200));
    // Create GridViewerColumns
    createGridViewerColumns();
    if (CommonUtils.isNotNull(this.selA2lResp)) {
      Map<Long, A2lRespBoschGroupUser> bshGrpUsrMap =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lBoschGrpUserMap().get(this.selA2lResp.getId());

      if (bshGrpUsrMap != null) {
        this.a2lRespBoschGroupUsers.addAll(bshGrpUsrMap.values());
      }
    }

    this.boschUserTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.boschUserTableViewer
        .setLabelProvider(new BoschUserGridTableLabelProvider(this.a2lWpInfoBo.getA2lResponsibilityModel()));
    this.boschUserTableViewer.setInput(this.a2lRespBoschGroupUsers);
    // Add selection listener
    addTableSelectionListener();
    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
  }


  /**
   * Create the PreDefined Filter for the List page- Icdm-500
   */
  private void createToolBarAction() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.boschUserSection);
    addApicUserAction(toolBarManager);
    deleteApicUserAction(toolBarManager);

    toolBarManager.update(true);

    this.boschUserSection.setTextClient(toolbar);

  }

  /**
   * @param toolBarManager
   */
  private void addApicUserAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    Action addNewUserAction = new Action("Active Directory Search", SWT.NONE) {

      @Override
      public void run() {
        UserSelectionDialog ucNewUserDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            "Add New User", "Add New User", "Add User For Bosch Group/Department", "Select", true, false);
        ucNewUserDialog.open();

        if (CommonUtils.isNotEmpty(ucNewUserDialog.getSelectedMultipleUser())) {
          AddEditUserDetailsDialog.this.selectedBoschUserList.clear();
          AddEditUserDetailsDialog.this.selectedBoschUserList.addAll(ucNewUserDialog.getSelectedMultipleUser());
          addNewlySelectedBoschGrpUsers(AddEditUserDetailsDialog.this.selectedBoschUserList);
          AddEditUserDetailsDialog.this.a2lRespBoschGroupUsers
              .addAll(AddEditUserDetailsDialog.this.newlyAddedA2lRespBoschGroupUsers);
          AddEditUserDetailsDialog.this.boschUserTableViewer
              .setInput(AddEditUserDetailsDialog.this.a2lRespBoschGroupUsers);
          AddEditUserDetailsDialog.this.saveBtn
              .setEnabled(CommonUtils.isNotEmptyString(AddEditUserDetailsDialog.this.depName.getText()));
          AddEditUserDetailsDialog.this.saveAgainBtn
              .setEnabled(CommonUtils.isNotEmptyString(AddEditUserDetailsDialog.this.depName.getText()));
        }
      }
    };
    // Set the image for add user action
    addNewUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FUNC_SEARCH_28X30));
    if (CommonUtils.isNotNull(this.selA2lResp)) {
      addNewUserAction.setEnabled((null != this.selA2lResp.getLDepartment()) &&
          (this.selA2lResp.getLFirstName() == null) && (this.selA2lResp.getLLastName() == null));
    }
    toolBarManager.add(addNewUserAction);

  }

  /**
   * @param toolBarManager
   */
  private void deleteApicUserAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    this.deleteUserAction = new Action("Delete User", SWT.NONE) {

      @Override
      public void run() {
        IStructuredSelection selection =
            (IStructuredSelection) AddEditUserDetailsDialog.this.boschUserTableViewer.getSelection();
        if (!selection.isEmpty()) {
          List<A2lRespBoschGroupUser> selectedItems = selection.toList();
          selectedItems.stream().forEach(selectedItem -> {
            if ((selectedItem.getUserId() != null) &&
                !AddEditUserDetailsDialog.this.newlyAddedA2lRespBoschGroupUsers.contains(selectedItem)) {
              AddEditUserDetailsDialog.this.a2lRespBoschGroupUsersToDelete.add(selectedItem);
            }
          });
          AddEditUserDetailsDialog.this.newlyAddedA2lRespBoschGroupUsers.removeAll(selectedItems);
          AddEditUserDetailsDialog.this.a2lRespBoschGroupUsers.removeAll(selectedItems);
          AddEditUserDetailsDialog.this.boschUserTableViewer.refresh();
          AddEditUserDetailsDialog.this.saveBtn
              .setEnabled(CommonUtils.isNotEmptyString(AddEditUserDetailsDialog.this.depName.getText()));
          AddEditUserDetailsDialog.this.saveAgainBtn
              .setEnabled(CommonUtils.isNotEmptyString(AddEditUserDetailsDialog.this.depName.getText()));
        }
      }
    };
    // Set the image for add user action
    this.deleteUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteUserAction.setEnabled(false);
    toolBarManager.add(this.deleteUserAction);

  }

  private void addNewlySelectedBoschGrpUsers(final SortedSet<User> selectedBoschUserList) {
    for (User user : selectedBoschUserList) {
      if (!bshGrpUserExists(user)) {
        A2lRespBoschGroupUser a2lRespBoschGroupUser = new A2lRespBoschGroupUser();
        a2lRespBoschGroupUser.setA2lRespId(null != this.selA2lResp ? this.selA2lResp.getId() : null);
        a2lRespBoschGroupUser.setUserId(user.getId());
        this.newlyAddedA2lRespBoschGroupUsers.add(a2lRespBoschGroupUser);
        AddEditUserDetailsDialog.this.a2lWpInfoBo.getA2lResponsibilityModel().getUserMap().put(user.getId(), user);
      }
    }
  }

  private boolean bshGrpUserExists(final User user) {
    for (A2lRespBoschGroupUser a2lRespBoschGroupUser : this.a2lRespBoschGroupUsers) {
      if (a2lRespBoschGroupUser.getUserId().equals(user.getId())) {
        return true;
      }
    }
    return false;
  }

  /**
   * This method adds selection listener
   */
  public void addTableSelectionListener() {
    this.boschUserTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        IStructuredSelection selection =
            (IStructuredSelection) AddEditUserDetailsDialog.this.boschUserTableViewer.getSelection();
        AddEditUserDetailsDialog.this.deleteUserAction.setEnabled(!selection.isEmpty());// AddEditUserDetailsDialog.this.boschDept.getSelection()
                                                                                        // && !selection.isEmpty()
      }
    });
  }

  /**
   * This method adds sorter to table
   */
  public void invokeColumnSorter() {
    this.boschUserTableViewer.setComparator(this.sorter);
  }

  /**
   * This method creates gridtable columns
   */
  public void createGridViewerColumns() {
    GridViewerColumn userNameCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.boschUserTableViewer, "User Name", 150);
    userNameCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(userNameCol.getColumn(), 0, this.sorter, this.boschUserTableViewer));
    GridViewerColumn ntIdCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.boschUserTableViewer, "NT-Id", 150);
    ntIdCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(ntIdCol.getColumn(),
        1, this.sorter, this.boschUserTableViewer));
    GridViewerColumn deptCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.boschUserTableViewer, "Department", 120);
    deptCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(deptCol.getColumn(),
        2, this.sorter, this.boschUserTableViewer));
  }

  /**
   * @param composite
   */
  private void createDeletedCheckBox(final Composite composite) {
    this.deletedButton = new Button(composite, SWT.CHECK | SWT.RIGHT);
    this.deletedButton.setText("Deleted");
    this.deletedButton.setEnabled(AddEditUserDetailsDialog.this.editFlag);
    this.deletedButton.setSelection((this.selA2lResp != null) && this.selA2lResp.isDeleted());
    this.deletedButton.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent e) {
        enableSave();
      }

    });
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (isNonBosch()) {
      saveRespUserDetails();
    }
    else {
      saveRespBoschDeptDetails();
    }
    if (AddEditUserDetailsDialog.this.selA2lResp != null) {
      super.okPressed();
    }
  }

  /**
   * @return boolean value
   */
  private boolean isNonBosch() {
    return !"R".equals(this.wpRespType.getCode());
  }

  /**
   * Creates the section.
   *
   * @param composite1 Composite
   */
  public void createSection(final Composite composite1) {
    this.section = SectionUtil.getInstance().createSection(composite1, this.formToolkit, "");
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.section.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout();
    this.section.setLayout(gridLayout);
    createForm();
    this.section.getDescriptionControl().setEnabled(false);
    String sectionName = isNonBosch() ? "User Details" : "Deapartment Name";
    this.section.setText(sectionName);
    this.section.setClient(this.form);
  }

  /**
   * Prepopulate.
   */
  private void prepopulate() {
    if (null != this.selA2lResp.getLFirstName()) {
      this.firstName.setText(this.selA2lResp.getLFirstName());
    }
    if (null != this.selA2lResp.getLLastName()) {
      this.lastName.setText(this.selA2lResp.getLLastName());
    }
    if (null != this.selA2lResp.getLDepartment()) {
      this.depName.setText(this.selA2lResp.getLDepartment());
    }
  }

  /**
   * Save resp user details.
   */
  private void saveRespUserDetails() {
    String fname = (AddEditUserDetailsDialog.this.firstName.getText().isEmpty()) ? null
        : AddEditUserDetailsDialog.this.firstName.getText().trim();
    String dept = (AddEditUserDetailsDialog.this.depName.getText().isEmpty()) ? null
        : AddEditUserDetailsDialog.this.depName.getText().trim();
    String lname = AddEditUserDetailsDialog.this.lastName.getText().trim();
    if (!AddEditUserDetailsDialog.this.editFlag) {
      // create event
      A2lResponsibility respToCreate = new A2lResponsibility();
      respToCreate.setLFirstName(fname);
      respToCreate.setLLastName(lname);
      respToCreate.setLDepartment(dept);
      respToCreate.setRespType(this.wpRespType.getCode());
      respToCreate.setProjectId(this.pidcId);
      respToCreate.setDeleted(false);
      if (null != this.a2lWpInfoBo) {
        if (this.a2lWpInfoBo.checkIfExists(respToCreate) == null) {
          this.selA2lResp = this.a2lWpInfoBo.createA2lResp(respToCreate);
          if (CommonUtils.isNotNull(this.selA2lResp)) {
            this.savedData.add(this.selA2lResp);
          }

        }
        else {
          MessageDialog.openInformation(getParentShell(), TITLE_NON_BOSCH, "User already added!");
        }
      }
      else if (null != this.wpRespBO) {
        createRespForPidcEditor(respToCreate);
      }
    }
    else {
      saveRespUserForEditEvent(fname, dept, lname);
    }
  }

  /**
   * @param fname
   * @param dept
   * @param lname
   */
  private void saveRespUserForEditEvent(final String fname, final String dept, final String lname) {
    // edit event
    A2lResponsibility respToEdit = new A2lResponsibility();
    CommonUtils.shallowCopy(respToEdit, this.selA2lResp);
    respToEdit.setLFirstName(fname);
    respToEdit.setLLastName(lname);
    respToEdit.setLDepartment(dept);
    respToEdit.setDeleted(this.deletedButton.getSelection());
    respToEdit.setRespType(this.wpRespType.getCode());
    this.selA2lResp =
        null != this.a2lWpInfoBo ? this.a2lWpInfoBo.editA2lResp(respToEdit) : this.wpRespBO.editA2lResp(respToEdit);
    if (CommonUtils.isNotNull(this.selA2lResp)) {
      this.savedData.add(this.selA2lResp);
    }
  }

  /**
   * @param respToCreate A2lResponsibility
   */
  public void createRespForPidcEditor(final A2lResponsibility respToCreate) {
    if (!this.wpRespBO.checkIfRespExists(respToCreate)) {
      this.selA2lResp = this.wpRespBO.createA2lResp(respToCreate);
      if (CommonUtils.isNotNull(this.selA2lResp)) {
        this.savedData.add(this.selA2lResp);
      }
    }
    else {
      MessageDialog.openInformation(getParentShell(), TITLE_NON_BOSCH, "User already added!");
    }
  }


  /**
   * Creates the dep name control.
   *
   * @param comp the comp
   */
  private void createDepNameControl(final Composite comp) {
    createLabelControl(comp, "Department");
    this.depName = createTextFileld(comp);
    this.depName.setEnabled(true);
    this.depName.setEditable(true);
    if (!isNonBosch()) {
      ControlDecoration depNameDecortaion = new ControlDecoration(this.depName, SWT.LEFT | SWT.TOP);
      this.decorators.showReqdDecoration(depNameDecortaion, "This field is mandatory.");
    }
    this.depName.addModifyListener(event -> enableSave());
  }

  /**
   * Enable save
   */
  private void enableSave() {
    if (null != this.saveBtn) {
      if (isNonBosch()) {
        enableButtons();
      }
      else {
        this.saveBtn.setEnabled(!((this.depName.getText() != null) && this.depName.getText().trim().isEmpty()));
        this.saveAgainBtn.setEnabled(this.saveBtn.getEnabled());
      }
    }
  }

  /**
   * This method creates text field.
   *
   * @param comp the comp
   * @return Text
   */
  private Text createTextFileld(final Composite comp) {
    final Text text = TextUtil.getInstance().createEditableText(this.formToolkit, comp, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    text.setLayoutData(widthHintGridData);
    return text;
  }

  /**
   * Creates the first name control.
   *
   * @param comp the comp
   */
  private void createFirstNameControl(final Composite comp) {
    createLabelControl(comp, "First Name");
    this.firstName = createTextFileld(comp);

    this.firstName.setEnabled(isNonBosch());

    this.firstName.setEditable(true);
    this.firstName.addModifyListener(event -> enableButtons());
  }


  /**
   * Enable buttons.
   */
  private void enableButtons() {
    if (null != this.saveBtn) {
      this.saveBtn.setEnabled((!CommonUtils.isEmptyString(this.lastName.getText().trim()) ||
          !CommonUtils.isEmptyString(this.firstName.getText().trim()) ||
          !CommonUtils.isEmptyString(this.depName.getText().trim())));
      this.saveAgainBtn.setEnabled(this.saveBtn.getEnabled());
    }
    // For pidc editor
    else if (null != this.saveAgainBtn) {
      this.saveAgainBtn.setEnabled(!((this.lastName.getText() != null) && this.lastName.getText().trim().isEmpty()));
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
    if (null == AddEditUserDetailsDialog.this.wpRespBO) {
      if (isNonBosch()) {
        this.saveBtn = createButton(parent, IDialogConstants.NEXT_ID, "Save and Create another user", true);
      }
      else {
        this.saveBtn = createButton(parent, IDialogConstants.NEXT_ID, "Save and Create another dept", true);
      }
      this.saveBtn.setEnabled(false);
    }
    this.saveAgainBtn = createButton(parent, IDialogConstants.OK_ID, "Save and Close", false);
    this.saveAgainBtn.setEnabled(false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    if (buttonId == IDialogConstants.NEXT_ID) {
      if (isNonBosch()) {
        saveRespUserDetails();
      }
      else {
        saveRespBoschDeptDetails();
      }
      clearFieldsForSaveAndCreate();
    }
    super.buttonPressed(buttonId);
  }

  /**
   *
   */
  private void clearFieldsForSaveAndCreate() {
    this.firstName.setText("");
    this.lastName.setText("");
    this.depName.setText("");
    this.editFlag = false;
    this.deletedButton.setSelection(false);
    this.newlyAddedA2lRespBoschGroupUsers.clear();
    this.a2lRespBoschGroupUsers.clear();
    this.selectedBoschUserList.clear();
    // In case of Non-Bosch Users boschUserTableViewer will be null
    if (!isNonBosch()) {
      this.boschUserTableViewer.setInput(new TreeSet<>());
    }
  }

  /**
   *
   */
  private void saveRespBoschDeptDetails() {
    String dept = (AddEditUserDetailsDialog.this.depName.getText().isEmpty()) ? null
        : AddEditUserDetailsDialog.this.depName.getText().trim();
    if (!AddEditUserDetailsDialog.this.editFlag) {
      createBoschDeptDetails(dept);
    }
    else {
      editBoschDeptDetails(dept);
    }
    if (CommonUtils.isNotNull(this.selA2lResp)) {
      this.savedData.add(this.selA2lResp);
    }

  }

  private boolean createBoschDeptDetails(final String dept) {
    boolean closeDialog = false;

    A2lResponsibility respToCreate = new A2lResponsibility();
    respToCreate.setProjectId(this.a2lWpInfoBo.getPidcId());
    respToCreate.setRespType(WpRespType.RB.getCode());
    respToCreate.setUserId(null);
    respToCreate.setLFirstName(null);
    respToCreate.setLLastName(null);
    respToCreate.setLDepartment(dept);
    if (!this.a2lWpInfoBo.checkIfDepartmentIdExists(respToCreate)) {

      A2lRespMaintenanceData input = new A2lRespMaintenanceData();
      input.setA2lRespToCreate(respToCreate);

      if (CommonUtils.isNotEmpty(this.newlyAddedA2lRespBoschGroupUsers)) {
        input.setBoschUsrsCreationList(new ArrayList<>(this.newlyAddedA2lRespBoschGroupUsers));
      }

      A2lRespMaintenanceData response = this.a2lWpInfoBo.createUpdateA2lRespWithBoschGrpUsers(input);

      if (response != null) {
        this.selA2lResp = response.getA2lRespToCreate();
      }

      closeDialog = (null != this.selA2lResp);
    }
    else {
      MessageDialogUtils.getInfoMessageDialog(ADD_DEPARTMENT,
          "A similar department has already been added! Please try editing the existing record.");
      closeDialog = false;
    }

    return closeDialog;
  }

  /**
   * @param dept
   * @return
   */
  private void editBoschDeptDetails(final String dept) {

    // edit event
    A2lResponsibility respToEdit = new A2lResponsibility();
    CommonUtils.shallowCopy(respToEdit, this.selA2lResp);
    respToEdit.setLFirstName(null);
    respToEdit.setLLastName(null);
    respToEdit.setLDepartment(dept);
    respToEdit.setUserId(null);
    respToEdit.setDeleted(this.deletedButton.getSelection());
    respToEdit.setRespType(this.wpRespType.getCode());

    // to check whether the Department is already mapped to a2l responsibility during edit
    if (this.a2lWpInfoBo.checkIfDepartmentIdExists(respToEdit)) {
      MessageDialogUtils.getInfoMessageDialog(EDIT_DEPARTMENT,
          "Another responsibility with the same department name is available. Kindly search the existing records.");
    }
    else {
      A2lRespMaintenanceData input = new A2lRespMaintenanceData();

      input.setA2lRespToUpdate(respToEdit);

      if (CommonUtils.isNotEmpty(this.newlyAddedA2lRespBoschGroupUsers)) {
        input.setBoschUsrsCreationList(new ArrayList<>(this.newlyAddedA2lRespBoschGroupUsers));
      }
      if (CommonUtils.isNotEmpty(this.a2lRespBoschGroupUsersToDelete)) {
        input.setBoschUsrsDeletionList(new ArrayList<>(this.a2lRespBoschGroupUsersToDelete));
      }

      A2lRespMaintenanceData response = this.a2lWpInfoBo.createUpdateA2lRespWithBoschGrpUsers(input);

      if (response != null) {
        this.selA2lResp = response.getA2lRespUpdated();
      }
    }
  }

  /**
   * Gets the pidc wp resp.
   *
   * @return the pidc wp resp
   */
  public List<A2lResponsibility> getA2lResp() {
    return this.savedData;
  }


  /**
   * To be used for setting User name in Create/Edit Responsibility Diaog for Responsibility Page,of Pidc Editor
   *
   * @return A2lResponsibility
   */
  public A2lResponsibility getSelA2lResp() {
    return this.selA2lResp;
  }
}
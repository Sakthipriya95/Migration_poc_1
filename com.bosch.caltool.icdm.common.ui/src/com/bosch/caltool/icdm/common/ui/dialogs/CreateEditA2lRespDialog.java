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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.apic.WorkPkgResponsibilityBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
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
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author apj4cob
 */
public class CreateEditA2lRespDialog extends AbstractDialog {


  private static final String USER_ALREADY_ADDED = "User already added!";
  /**
   * Top composite
   */
  private Composite top;
  /**
   * Composite instance for the dialog
   */
  private Composite composite;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  /**
   * save button
   */
  private Button saveButton;
  /**
   * clear button
   */
  private Button clearButton;

  private final boolean editFlag;
  /**
   * WP Responsibility BO
   */
  private final WorkPkgResponsibilityBO wpRespBO;
  /**
   * Selected A2lResponsibility
   */
  private A2lResponsibility selA2lResp;

  private Section boschUserSection;

  private final SortedSet<A2lRespBoschGroupUser> a2lRespBoschGroupUsers = new TreeSet<>();

  private final SortedSet<A2lRespBoschGroupUser> newlyAddedA2lRespBoschGroupUsers = new TreeSet<>();

  private final SortedSet<User> selectedBoschUserList = new TreeSet<>();

  private final List<A2lRespBoschGroupUser> a2lRespBoschGroupUsersToDelete = new ArrayList<>();


  /**
   * To be used as label for Bosch radio button
   */
  public static final String ENTER_BOSCH_RESP = "Bosch User";

  /**
   * To be used as label for Bosch Department radio button
   */
  public static final String ENTER_BOSCH_DEPT_RESP = "Bosch Group/Department";

  /**
   * To be used as label for Other radio button
   */
  public static final String ENTER_OTHER_RESP = "Other";

  /**
   * To be used as label for Customer radio button
   */
  public static final String ENTER_CUSTOMER_RESP = "Customer";
  /** The Constant TITLE. */
  private static final String TITLE_NON_BOSCH = "Add new Non-Bosch User";
  /** The Constant TITLE. */
  private static final String TITLE_BOSCH = "Add new Bosch User";
  /** The Constant TITLE. */
  private static final String ADD_DEPARTMENT = "Add Department";
  /** The Constant TITLE. */
  private static final String EDIT_DEPARTMENT = "Edit Department";
  /**
   * Add Bosch Responsible User Dialog
   */
  private AddUserResponsibleDialog addBoschUserAsRespDialog;
  private Section respSection;

  private Button bosch;

  private Button boschDept;

  private Button customer;

  private Button others;
  private Text userName;
  private Button searchButton;

  private Action deleteUserAction;

  private Action addNewUserAction;

  private Button clearUserButton;
  /** The first name. */
  private Text firstName;

  /** The dep name. */
  private Text depName;

  /** The last name. */
  private Text lastName;
  private Label firstNameLabel;
  private Label lastNameLabel;
  private Label deptNameLabel;
  private Label boschUsrNameLabel;
  private WpRespType wpRespType;

  private GridTableViewer boschUserTableViewer;
  private BoschUserGridTableSorter sorter;

  /**
   * @param parentShell Shell
   * @param wpRespBO WorkPkgResponsibilityBO
   * @param selA2lResp A2lResponsibility
   * @param editFlag boolean
   */
  public CreateEditA2lRespDialog(final Shell parentShell, final WorkPkgResponsibilityBO wpRespBO,
      final A2lResponsibility selA2lResp, final boolean editFlag) {
    super(parentShell);
    this.wpRespBO = wpRespBO;
    this.selA2lResp = selA2lResp.clone();
    this.editFlag = editFlag;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    displayDialogMsg();
    return contents;
  }

  /**
   * Display dialog message
   */
  private void displayDialogMsg() {
    // Set the title
    setTitle(this.editFlag ? "Edit " : "Create " + "Responsibility");
    StringBuilder descMsg = new StringBuilder();
    descMsg.append("Enter responsibility details");
    try {
      descMsg.append("\n").append(new CommonDataBO().getMessage("A2L", "INVALID_WP_RESP_ERROR"));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    setMessage(descMsg.toString(), IMessageProvider.INFORMATION);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Responsibility");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);

    createRespSection();
  }


  /**
   * This method intializes section
   */
  public void createRespSection() {

    GridData gridData5 = GridDataUtil.getInstance().getGridData();

    this.respSection =
        this.formToolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.respSection.setExpanded(true);
    this.respSection.setLayoutData(gridData5);
    this.respSection.getDescriptionControl().setEnabled(false);


    createRespForm();
    this.respSection.setClient(this.composite);
    this.respSection.setText("Set Responsibility");
    addRespButtonListeners();
    loadResponsibilityDetails();

  }


  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method creates responsibility form
   */
  private void createRespForm() {

    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.composite = this.formToolkit.createComposite(this.respSection, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 4;

    Group respTypeRadioGrp = new Group(this.composite, SWT.READ_ONLY);
    respTypeRadioGrp.setLayout(gridLayout);
    respTypeRadioGrp.setLayoutData(gridData);

    addBoschRespButton(respTypeRadioGrp);
    addBoschDeptRespButton(respTypeRadioGrp);
    addCustomerRespButton(respTypeRadioGrp);
    addOthersButton(respTypeRadioGrp);

    createUserNameForm(gridLayout);

    createBoschUsersSection();

    this.respSection.setClient(this.composite);
  }

  /**
   * Creates the last name control.
   *
   * @param comp the comp
   */
  private void createLastNameControl(final Composite comp) {
    this.lastNameLabel = this.formToolkit.createLabel(comp, "Last Name ");
    this.lastName = createTextFileld(comp);
    this.lastName.setEnabled(true);
    this.lastName.setEditable(true);
    this.lastName.addModifyListener(event -> enableButtons(true));
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
    this.firstNameLabel = this.formToolkit.createLabel(comp, "First Name ");
    this.firstName = createTextFileld(comp);
    this.firstName.setEnabled(true);
    this.firstName.setEditable(true);
    this.firstName.addModifyListener(event -> enableButtons(true));
  }

  /**
   * Creates the dep name control.
   *
   * @param comp the comp
   */
  private void createDepNameControl(final Composite comp) {
    this.deptNameLabel = this.formToolkit.createLabel(comp, "Department");
    this.depName = createTextFileld(comp);
    this.depName.setEnabled(true);
    this.depName.setEditable(true);
    this.depName.addModifyListener(event -> enableButtons(true));
  }

  private void createBoschUsersSection() {

    this.boschUserSection =
        this.formToolkit.createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.boschUserSection.setExpanded(true);
    this.boschUserSection.setText("List of users in this Bosch Group/Department (optional)");


    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;

    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.boschUserSection.setLayoutData(gridData);
    this.boschUserSection.setLayout(gridLayout);

    this.sorter = new BoschUserGridTableSorter(this.wpRespBO.getA2lRespModel());

    createBoschUserTableViewer(this.boschUserSection);

    createToolBarAction();

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
    this.addNewUserAction = new Action("Active Directory Search", SWT.NONE) {

      @Override
      public void run() {
        UserSelectionDialog ucNewUserDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
            "Add New User", "Add New User", "Add User For Bosch Group/Department", "Select", true, false);
        ucNewUserDialog.open();

        if (CommonUtils.isNotEmpty(ucNewUserDialog.getSelectedMultipleUser())) {
          CreateEditA2lRespDialog.this.selectedBoschUserList.clear();
          CreateEditA2lRespDialog.this.selectedBoschUserList.addAll(ucNewUserDialog.getSelectedMultipleUser());
          addNewlySelectedBoschGrpUsers(CreateEditA2lRespDialog.this.selectedBoschUserList);
          CreateEditA2lRespDialog.this.a2lRespBoschGroupUsers
              .addAll(CreateEditA2lRespDialog.this.newlyAddedA2lRespBoschGroupUsers);
          CreateEditA2lRespDialog.this.boschUserTableViewer
              .setInput(CreateEditA2lRespDialog.this.a2lRespBoschGroupUsers);
          CreateEditA2lRespDialog.this.saveButton
              .setEnabled(CommonUtils.isNotEmptyString(CreateEditA2lRespDialog.this.depName.getText()));
        }
      }
    };
    // Set the image for add user action
    this.addNewUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FUNC_SEARCH_28X30));
    this.addNewUserAction.setEnabled((null != this.selA2lResp.getLDepartment()) &&
        (this.selA2lResp.getLFirstName() == null) && (this.selA2lResp.getLLastName() == null));
    toolBarManager.add(this.addNewUserAction);

  }


  private void addNewlySelectedBoschGrpUsers(final SortedSet<User> selectedBoschUserList) {
    for (User user : selectedBoschUserList) {
      if (!bshGrpUserExists(user)) {
        A2lRespBoschGroupUser a2lRespBoschGroupUser = new A2lRespBoschGroupUser();
        a2lRespBoschGroupUser.setA2lRespId(null != this.selA2lResp ? this.selA2lResp.getId() : null);
        a2lRespBoschGroupUser.setUserId(user.getId());
        this.newlyAddedA2lRespBoschGroupUsers.add(a2lRespBoschGroupUser);
        CreateEditA2lRespDialog.this.wpRespBO.getA2lRespModel().getUserMap().put(user.getId(), user);
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
   * @param toolBarManager
   */
  private void deleteApicUserAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    this.deleteUserAction = new Action("Delete User", SWT.NONE) {

      @Override
      public void run() {
        IStructuredSelection selection =
            (IStructuredSelection) CreateEditA2lRespDialog.this.boschUserTableViewer.getSelection();
        if (!selection.isEmpty()) {
          List<A2lRespBoschGroupUser> selectedItems = selection.toList();
          selectedItems.stream().forEach(selectedItem -> {
            if ((selectedItem.getUserId() != null) &&
                !CreateEditA2lRespDialog.this.newlyAddedA2lRespBoschGroupUsers.contains(selectedItem)) {
              CreateEditA2lRespDialog.this.a2lRespBoschGroupUsersToDelete.add(selectedItem);
            }
          });
          CreateEditA2lRespDialog.this.newlyAddedA2lRespBoschGroupUsers.removeAll(selectedItems);
          CreateEditA2lRespDialog.this.a2lRespBoschGroupUsers.removeAll(selectedItems);
          CreateEditA2lRespDialog.this.boschUserTableViewer.refresh();
          CreateEditA2lRespDialog.this.saveButton
              .setEnabled(CommonUtils.isNotEmptyString(CreateEditA2lRespDialog.this.depName.getText()));
        }
      }
    };
    // Set the image for add user action
    this.deleteUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DELETE_16X16));
    this.deleteUserAction.setEnabled(false);
    toolBarManager.add(this.deleteUserAction);

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

    Map<Long, A2lRespBoschGroupUser> bshGrpUsrMap =
        this.wpRespBO.getA2lRespModel().getA2lBoschGrpUserMap().get(this.selA2lResp.getId());

    if (bshGrpUsrMap != null) {
      this.a2lRespBoschGroupUsers.addAll(bshGrpUsrMap.values());
    }

    this.boschUserTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.boschUserTableViewer.setLabelProvider(new BoschUserGridTableLabelProvider(this.wpRespBO.getA2lRespModel()));
    this.boschUserTableViewer.setInput(this.a2lRespBoschGroupUsers);
    // Add selection listener
    addTableSelectionListener();
    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
  }


  /**
   * @param createSetResp
   * @param gridLayout
   */
  private void createUserNameForm(final GridLayout gridLayout) {

    Composite userSelect = this.formToolkit.createComposite(this.composite, SWT.NONE);
    userSelect.setLayout(gridLayout);
    userSelect.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.boschUsrNameLabel = this.formToolkit.createLabel(userSelect, "User Name ");
    this.userName = this.formToolkit.createText(userSelect, null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.userName.setLayoutData(GridDataUtil.getInstance().getTextGridData());
    this.userName.setEnabled(false);

    this.searchButton = new Button(userSelect, SWT.PUSH);
    this.searchButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.PIDC_SEARCH_16X16));
    this.searchButton.setEnabled(false);

    this.clearUserButton = new Button(userSelect, SWT.PUSH);
    this.clearUserButton.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.CLEAR_HIST_16X16));
    this.clearUserButton.setToolTipText("Clear");
    this.clearUserButton.setEnabled(false);
    createLastNameControl(userSelect);
    this.formToolkit.createLabel(userSelect, " ");
    this.formToolkit.createLabel(userSelect, " ");
    createFirstNameControl(userSelect);
    this.formToolkit.createLabel(userSelect, " ");
    this.formToolkit.createLabel(userSelect, " ");
    createDepNameControl(userSelect);
    this.formToolkit.createLabel(userSelect, " ");
    this.formToolkit.createLabel(userSelect, " ");
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
   * @param respGrp
   */
  private void addCustomerRespButton(final Group respGrp) {

    this.customer = new Button(respGrp, SWT.RADIO);
    this.customer.setText(ENTER_CUSTOMER_RESP);
    this.customer.setEnabled(true);
  }

  /**
   * @param respGrp
   */
  private void addBoschRespButton(final Group respGrp) {
    this.bosch = new Button(respGrp, SWT.RADIO);
    this.bosch.setText(ENTER_BOSCH_RESP);
    this.bosch.setEnabled(true);
  }

  private void addBoschDeptRespButton(final Group respGrp) {
    this.boschDept = new Button(respGrp, SWT.RADIO);
    this.boschDept.setText(ENTER_BOSCH_DEPT_RESP);
    this.boschDept.setEnabled(true);
  }

  /**
   * @param respGrp
   */
  private void addOthersButton(final Group respGrp) {
    this.others = new Button(respGrp, SWT.RADIO);
    this.others.setText(ENTER_OTHER_RESP);
    this.others.setEnabled(true);
  }

  /**
   * Reset Radio buttons
   */
  public void resetRadioButtons() {
    getBoschButton().setSelection(false);
    getBoschButton().setText(ENTER_BOSCH_RESP);
    getCustomerButton().setSelection(false);
    getCustomerButton().setText(ENTER_CUSTOMER_RESP);
    getOthersButton().setSelection(false);
    getOthersButton().setText(ENTER_OTHER_RESP);
  }

  /**
   * Radio button listeners
   */
  private void addRespButtonListeners() {
    boschBtnListener();
    boschDeptBtnListener();
    customerBtnListener();
    otherBtnListener();
    respUserBtnListeners();
  }

  private void loadResponsibilityDetails() {

    resetRadioButtons();

    if (null != this.selA2lResp.getId()) {

      WpRespType wpRespType1 = WpRespType.getType(this.selA2lResp.getRespType());

      switch (wpRespType1) {
        case RB:
          setBoschOrBoschDeptRadioBtn();
          break;
        case CUSTOMER:
          setCustomerRadioButton(this.selA2lResp);
          break;
        case OTHERS:
          setOtherRespRadioButton(this.selA2lResp);
          break;
        default:
          break;
      }
    }
  }

  /**
   *
   */
  private void setBoschOrBoschDeptRadioBtn() {
    if (null != this.selA2lResp.getLDepartment()) {
      setBoschDepartMentRadioButton(this.selA2lResp);
    }
    else {
      setBoschRadioButton(this.selA2lResp);
    }
  }


  /**
   * Set Bosch responsible details
   *
   * @param a2lResp
   */
  private void setBoschRadioButton(final A2lResponsibility a2lResp) {
    getBoschButton().setSelection(true);
    this.wpRespType = WpRespType.RB;
    if (null != a2lResp.getUserId()) {
      getUserNameTextBox()
          .setText(this.wpRespBO.getA2lRespModel().getUserMap().get(a2lResp.getUserId()).getDescription());
    }
    this.firstName.setText("");
    this.lastName.setText("");
    this.depName.setText("");
    enableBoschUserWidget(true);
    enableBoschDepartMentUserWidget(false);
    enableNonBoschUserWidget(false);
  }

  private void setBoschDepartMentRadioButton(final A2lResponsibility a2lResp) {
    getBoschDept().setSelection(true);
    this.wpRespType = WpRespType.RB;
    if (null != a2lResp.getLDepartment()) {
      this.depName.setText(a2lResp.getLDepartment());
    }
    getUserNameTextBox().setText("");
    this.firstName.setText("");
    this.lastName.setText("");
    enableBoschUserWidget(false);
    enableNonBoschUserWidget(false);
    enableBoschDepartMentUserWidget(true);
  }

  /**
   * Set Customer responsible details
   *
   * @param a2lResp
   */
  private void setCustomerRadioButton(final A2lResponsibility a2lResp) {
    getCustomerButton().setSelection(true);
    this.wpRespType = WpRespType.CUSTOMER;
    if (!A2lResponsibilityCommon.isDefaultResponsibility(a2lResp)) {
      prepopulate();
    }
    getUserNameTextBox().setText("");
    enableBoschUserWidget(false);
    enableBoschDepartMentUserWidget(false);
    enableNonBoschUserWidget(true);
  }

  /**
   * Set Others responsible details
   *
   * @param a2lResp
   */
  private void setOtherRespRadioButton(final A2lResponsibility a2lResp) {
    getOthersButton().setSelection(true);
    this.wpRespType = WpRespType.OTHERS;
    if (!A2lResponsibilityCommon.isDefaultResponsibility(a2lResp)) {
      prepopulate();
    }
    getUserNameTextBox().setText("");
    enableBoschUserWidget(false);
    enableBoschDepartMentUserWidget(false);
    enableNonBoschUserWidget(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveButton = createButton(parent, IDialogConstants.OK_ID, "Save", true);
    this.saveButton.setEnabled(false);
    this.clearButton = createButton(parent, IDialogConstants.NEXT_ID, "Clear", true);
    this.clearButton.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * Bosch radio button listener
   */
  private void boschBtnListener() {
    CreateEditA2lRespDialog.this.getBoschButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditA2lRespDialog.this.getBoschButton().getSelection()) {
          if (CreateEditA2lRespDialog.this.editFlag && (null != CreateEditA2lRespDialog.this.selA2lResp.getUserId())) {
            getUserNameTextBox().setText(CreateEditA2lRespDialog.this.wpRespBO.getA2lRespModel().getUserMap()
                .get(CreateEditA2lRespDialog.this.selA2lResp.getUserId()).getDescription());


          }
          CreateEditA2lRespDialog.this.wpRespType = WpRespType.RB;

          CreateEditA2lRespDialog.this.firstName.setText("");

          CreateEditA2lRespDialog.this.lastName.setText("");
          CreateEditA2lRespDialog.this.depName.setText("");
          CreateEditA2lRespDialog.this.enableBoschUserWidget(true);
          enableBoschDepartMentUserWidget(false);
          CreateEditA2lRespDialog.this.enableNonBoschUserWidget(false);
          CreateEditA2lRespDialog.this.saveButton.setEnabled(isRespSelected());
          CreateEditA2lRespDialog.this.boschUserSection.setEnabled(false);
          CreateEditA2lRespDialog.this.addNewUserAction.setEnabled(false);
          CreateEditA2lRespDialog.this.deleteUserAction.setEnabled(false);
        }
      }


    });
  }

  /**
   * Bosch Department radio button listener
   */
  private void boschDeptBtnListener() {
    CreateEditA2lRespDialog.this.getBoschDept().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditA2lRespDialog.this.getBoschDept().getSelection()) {
          if (CreateEditA2lRespDialog.this.editFlag &&
              (null != CreateEditA2lRespDialog.this.selA2lResp.getLDepartment())) {
            CreateEditA2lRespDialog.this.depName.setText(CreateEditA2lRespDialog.this.selA2lResp.getLDepartment());
          }
          CreateEditA2lRespDialog.this.wpRespType = WpRespType.RB;

          CreateEditA2lRespDialog.this.firstName.setText("");
          CreateEditA2lRespDialog.this.getUserNameTextBox().setText("");
          CreateEditA2lRespDialog.this.lastName.setText("");
          CreateEditA2lRespDialog.this.enableBoschUserWidget(false);
          CreateEditA2lRespDialog.this.enableNonBoschUserWidget(false);
          CreateEditA2lRespDialog.this.enableBoschDepartMentUserWidget(true);
          CreateEditA2lRespDialog.this.boschUserSection.setEnabled(true);

          CreateEditA2lRespDialog.this.saveButton.setEnabled(isRespSelected());
          CreateEditA2lRespDialog.this.addNewUserAction.setEnabled(true);

        }
      }


    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    if (buttonId == IDialogConstants.NEXT_ID) {
      getUserNameTextBox().setText("");

      this.firstName.setText("");
      this.lastName.setText("");
      this.depName.setText("");
    }
    super.buttonPressed(buttonId);
  }

  /**
   * @param flag boolean
   */
  public void enableNonBoschUserWidget(final boolean flag) {
    CreateEditA2lRespDialog.this.firstNameLabel.setEnabled(flag);
    CreateEditA2lRespDialog.this.lastNameLabel.setEnabled(flag);
    CreateEditA2lRespDialog.this.deptNameLabel.setEnabled(flag);
    CreateEditA2lRespDialog.this.firstName.setEnabled(flag);
    CreateEditA2lRespDialog.this.lastName.setEnabled(flag);
    CreateEditA2lRespDialog.this.depName.setEnabled(flag);
  }

  /**
   * @param flag boolean
   */
  public void enableBoschUserWidget(final boolean flag) {
    CreateEditA2lRespDialog.this.boschUsrNameLabel.setEnabled(flag);
    CreateEditA2lRespDialog.this.userName.setEnabled(flag);
    CreateEditA2lRespDialog.this.searchButton.setEnabled(flag);
    CreateEditA2lRespDialog.this.clearUserButton.setEnabled(flag);
  }

  /**
   * @param flag boolean
   */
  public void enableBoschDepartMentUserWidget(final boolean flag) {
    CreateEditA2lRespDialog.this.depName.setEnabled(flag);
  }

  /**
   * Customer Radio button listener
   */
  private void customerBtnListener() {
    CreateEditA2lRespDialog.this.getCustomerButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditA2lRespDialog.this.getCustomerButton().getSelection()) {
          CreateEditA2lRespDialog.this.getUserNameTextBox().setText("");
          if (CreateEditA2lRespDialog.this.editFlag) {
            prepopulate();
          }
          CreateEditA2lRespDialog.this.wpRespType = WpRespType.CUSTOMER;

          CreateEditA2lRespDialog.this.getUserNameTextBox().setText("");
          CreateEditA2lRespDialog.this.enableBoschUserWidget(false);
          enableBoschDepartMentUserWidget(false);
          CreateEditA2lRespDialog.this.enableNonBoschUserWidget(true);
          CreateEditA2lRespDialog.this.saveButton.setEnabled(isRespSelected());
          CreateEditA2lRespDialog.this.boschUserSection.setEnabled(false);
          CreateEditA2lRespDialog.this.addNewUserAction.setEnabled(false);
          CreateEditA2lRespDialog.this.deleteUserAction.setEnabled(false);
        }
      }
    });
  }

  /**
   * Others radio button listener
   */
  private void otherBtnListener() {
    CreateEditA2lRespDialog.this.getOthersButton().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        if (CreateEditA2lRespDialog.this.getOthersButton().getSelection()) {
          CreateEditA2lRespDialog.this.getUserNameTextBox().setText("");
          if (CreateEditA2lRespDialog.this.editFlag) {
            prepopulate();
          }
          CreateEditA2lRespDialog.this.wpRespType = WpRespType.OTHERS;

          CreateEditA2lRespDialog.this.getUserNameTextBox().setText("");
          CreateEditA2lRespDialog.this.enableBoschUserWidget(false);
          enableBoschDepartMentUserWidget(false);
          CreateEditA2lRespDialog.this.enableNonBoschUserWidget(true);
          CreateEditA2lRespDialog.this.saveButton.setEnabled(isRespSelected());
          CreateEditA2lRespDialog.this.boschUserSection.setEnabled(false);
          CreateEditA2lRespDialog.this.addNewUserAction.setEnabled(false);
          CreateEditA2lRespDialog.this.deleteUserAction.setEnabled(false);
        }
      }
    });
  }

  /**
   * Open Bosch or nonBosch User dialog
   */
  public void openBoschUsersDialog() {
    if (getBoschButton().getSelection()) {
      this.addBoschUserAsRespDialog = new AddUserResponsibleDialog(Display.getCurrent().getActiveShell(), null);
      this.addBoschUserAsRespDialog.open();
    }
  }

  /**
   * Save resp user details.
   */
  private boolean saveNonBoschRespUserDetails() {
    boolean closeDialog = true;
    String fname = (CreateEditA2lRespDialog.this.firstName.getText() != null)
        ? CreateEditA2lRespDialog.this.firstName.getText().trim() : null;
    String dept = (CreateEditA2lRespDialog.this.depName.getText() != null)
        ? CreateEditA2lRespDialog.this.depName.getText().trim() : null;
    // mandatory
    String lname = CreateEditA2lRespDialog.this.lastName.getText().trim();

    if (!CreateEditA2lRespDialog.this.editFlag) {
      // create event
      A2lResponsibility respToCreate = new A2lResponsibility();
      respToCreate.setLFirstName(fname);
      respToCreate.setLLastName(lname);
      respToCreate.setLDepartment(dept);
      respToCreate.setRespType(this.wpRespType.getCode());
      respToCreate.setProjectId(this.wpRespBO.getPidcId());
      respToCreate.setDeleted(false);

      if (!this.wpRespBO.checkIfRespExists(respToCreate) &&
          !this.wpRespBO.checkIfCustomerOrOthersExists(respToCreate)) {
        this.selA2lResp = this.wpRespBO.createA2lResp(respToCreate);
        closeDialog = null != this.selA2lResp;
      }
      else {
        MessageDialogUtils.getInfoMessageDialog(TITLE_NON_BOSCH, USER_ALREADY_ADDED);
        closeDialog = false;
      }
    }
    else {
      // edit event
      A2lResponsibility respToEdit = new A2lResponsibility();
      CommonUtils.shallowCopy(respToEdit, this.selA2lResp);
      respToEdit.setLFirstName(fname);
      respToEdit.setLLastName(lname);
      respToEdit.setLDepartment(dept);
      respToEdit.setUserId(null);
      respToEdit.setRespType(this.wpRespType.getCode());
      if (!this.wpRespBO.checkIfCustomerOrOthersExists(respToEdit)) {

        A2lRespMaintenanceData a2lRespUpdationData = new A2lRespMaintenanceData();
        a2lRespUpdationData.setA2lRespToUpdate(respToEdit);

        if (CommonUtils.isEqual(this.selA2lResp.getRespType(), WpRespType.RB.getCode()) &&
            CommonUtils.isNotEqual(this.selA2lResp.getRespType(), this.wpRespType.getCode()) &&
            CommonUtils.isNotEmpty(this.a2lRespBoschGroupUsers)) {
          a2lRespUpdationData.getBoschUsrsDeletionList().addAll(this.a2lRespBoschGroupUsers);
        }
        A2lRespMaintenanceData response = this.wpRespBO.createUpdateA2lRespWithBoschGrpUsers(a2lRespUpdationData);

        this.selA2lResp = response.getA2lRespUpdated();

        closeDialog = null != this.selA2lResp;
      }
      else {
        MessageDialogUtils.getInfoMessageDialog(TITLE_NON_BOSCH, USER_ALREADY_ADDED);
        closeDialog = false;
      }
    }
    return closeDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    boolean closeDialog = false;
    if (WpRespType.RB.equals(this.wpRespType)) {
      if (getBoschButton().getSelection()) {
        closeDialog = saveBoschUserDetails();
      }
      else if (getBoschDept().getSelection()) {
        closeDialog = saveBoschDepartmentDetails();
      }
    }
    if (WpRespType.CUSTOMER.equals(this.wpRespType) || WpRespType.OTHERS.equals(this.wpRespType)) {
      closeDialog = saveNonBoschRespUserDetails();
    }
    if (closeDialog) {
      super.okPressed();
    }
  }

  private boolean saveBoschDepartmentDetails() {
    boolean closeDialog = true;
    String dept = (CreateEditA2lRespDialog.this.depName.getText() != null)
        ? CreateEditA2lRespDialog.this.depName.getText().trim() : null;
    if (!CreateEditA2lRespDialog.this.editFlag) {
      closeDialog = createBoschDeptDetails(dept);
    }
    else {
      closeDialog = editBoschDeptDetails(dept);
    }

    return closeDialog;

  }

  /**
   * @param dept
   * @return
   */
  private boolean editBoschDeptDetails(final String dept) {
    boolean closeDialog = false;

    // edit event
    A2lResponsibility respToEdit = new A2lResponsibility();
    CommonUtils.shallowCopy(respToEdit, this.selA2lResp);
    respToEdit.setLFirstName(null);
    respToEdit.setLLastName(null);
    respToEdit.setLDepartment(dept);
    respToEdit.setUserId(null);
    respToEdit.setRespType(this.wpRespType.getCode());

    // to check whether the Department is already mapped to a2l responsibility during edit
    if (this.wpRespBO.checkIfDepartmentIdExists(respToEdit)) {
      MessageDialogUtils.getInfoMessageDialog(EDIT_DEPARTMENT,
          "Another responsibility with the same department name is available. Kindly search the existing records.");
      closeDialog = false;
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

      A2lRespMaintenanceData response = this.wpRespBO.createUpdateA2lRespWithBoschGrpUsers(input);

      if (response != null) {
        this.selA2lResp = response.getA2lRespUpdated();
      }

      closeDialog = (null != this.selA2lResp);
    }
    return closeDialog;
  }

  /**
   * @param dept
   * @return
   */
  private boolean createBoschDeptDetails(final String dept) {
    boolean closeDialog = false;

    A2lResponsibility respToCreate = new A2lResponsibility();
    respToCreate.setProjectId(this.wpRespBO.getPidcId());
    respToCreate.setRespType(WpRespType.RB.getCode());
    respToCreate.setUserId(null);
    respToCreate.setLFirstName(null);
    respToCreate.setLLastName(null);
    respToCreate.setLDepartment(dept);
    if (!this.wpRespBO.checkIfDepartmentIdExists(respToCreate)) {

      A2lRespMaintenanceData input = new A2lRespMaintenanceData();
      input.setA2lRespToCreate(respToCreate);

      if (CommonUtils.isNotEmpty(this.newlyAddedA2lRespBoschGroupUsers)) {
        input.setBoschUsrsCreationList(new ArrayList<>(this.newlyAddedA2lRespBoschGroupUsers));
      }

      A2lRespMaintenanceData response = this.wpRespBO.createUpdateA2lRespWithBoschGrpUsers(input);

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
   *
   */
  public boolean saveBoschUserDetails() {
    boolean closeDialog = true;

    Long userId = (this.addBoschUserAsRespDialog != null) && (this.addBoschUserAsRespDialog.getSelectedUser() != null)
        ? this.addBoschUserAsRespDialog.getSelectedUser().getId() : null;
    if (!CreateEditA2lRespDialog.this.editFlag) {
      A2lResponsibility respToCreate = new A2lResponsibility();
      respToCreate.setProjectId(this.wpRespBO.getPidcId());
      respToCreate.setUserId(userId);
      respToCreate.setRespType(WpRespType.RB.getCode());
      respToCreate.setLFirstName(null);
      respToCreate.setLLastName(null);
      respToCreate.setLDepartment(null);
      if (!this.wpRespBO.checkIfRespExists(respToCreate)) {
        this.selA2lResp = this.wpRespBO.createA2lResp(respToCreate);
        closeDialog = null != this.selA2lResp;
      }
      else {
        MessageDialogUtils.getInfoMessageDialog(TITLE_BOSCH, USER_ALREADY_ADDED);
        closeDialog = false;
      }
    }
    else {
      // edit event
      A2lResponsibility respToEdit = new A2lResponsibility();
      CommonUtils.shallowCopy(respToEdit, this.selA2lResp);
      respToEdit.setLFirstName(null);
      respToEdit.setLLastName(null);

      respToEdit.setUserId(userId);
      respToEdit.setRespType(this.wpRespType.getCode());
      // to check whether the user id is already mapped to a2l responsibility during edit
      if (!this.wpRespBO.checkIfUserIdExists(respToEdit)) {
        this.selA2lResp = this.wpRespBO.editA2lResp(respToEdit);
        closeDialog = null != this.selA2lResp;
      }
      else {
        MessageDialogUtils.getInfoMessageDialog(TITLE_BOSCH, USER_ALREADY_ADDED);
        closeDialog = false;
      }
    }
    return closeDialog;
  }

  /**
   * Open search user dialog based on the selection
   */
  private void respUserBtnListeners() {
    getSearchButton().addSelectionListener(new SelectionAdapter() {

      /**
       * re {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        CreateEditA2lRespDialog.this.openBoschUsersDialog();
        // set selected user in user name text box
        if ((null != CreateEditA2lRespDialog.this.addBoschUserAsRespDialog) &&
            (CreateEditA2lRespDialog.this.addBoschUserAsRespDialog.getSelectedUser() != null)) {
          String name = CreateEditA2lRespDialog.this.addBoschUserAsRespDialog.getSelectedUser().getDescription();
          CreateEditA2lRespDialog.this.getUserNameTextBox().setText(name != null ? name : "");
        }
        CreateEditA2lRespDialog.this.saveButton.setEnabled(isRespSelected());
        CreateEditA2lRespDialog.this.clearButton.setEnabled(isRespSelected());
      }
    });

    CreateEditA2lRespDialog.this.getClearUserButton().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CreateEditA2lRespDialog.this.getUserNameTextBox().setText("");
        CreateEditA2lRespDialog.this.getClearUserButton().setEnabled(false);
        CreateEditA2lRespDialog.this.saveButton.setEnabled(false);
      }
    });
  }

  /**
   * Enable save/clear button method
   *
   * @param flag enable
   */
  public void enableButtons(final boolean flag) {
    if (null != this.saveButton) {
      this.saveButton.setEnabled(flag && isRespSelected());

      this.clearButton.setEnabled(this.saveButton.getEnabled());
    }
  }

  /**
   * @return bosch button
   */
  public Button getBoschButton() {
    return this.bosch;
  }


  /**
   * @return customer button
   */
  public Button getCustomerButton() {
    return this.customer;
  }


  /**
   * @return others button
   */
  public Button getOthersButton() {
    return this.others;
  }


  /**
   * @return userName button
   */
  public Text getUserNameTextBox() {
    return this.userName;
  }


  /**
   * @return search button
   */
  public Button getSearchButton() {
    return this.searchButton;
  }


  /**
   * @return clear button
   */
  public Button getClearButton() {
    return this.clearButton;
  }


  /**
   * @return clear user Button
   */
  public Button getClearUserButton() {
    return this.clearUserButton;
  }

  /**
   * check if responsibility value is entered
   *
   * @return boolean
   */
  public boolean isRespSelected() {
    return (isBoschSelected() || isCustOrOthersSelected());
  }

  /**
   * @return
   */
  private boolean isBoschSelected() {
    return (getBoschButton().getSelection() && (CommonUtils.isNotEmptyString(getUserNameTextBox().getText()))) ||
        (getBoschDept().getSelection() && (CommonUtils.isNotEmptyString(this.depName.getText())));
  }

  /**
   * @return
   */
  private boolean isCustOrOthersSelected() {
    return (getCustomerButton().getSelection() || getOthersButton().getSelection()) &&
        isFirstNameLastNameDeptNotEmpty();
  }

  /**
   * @return
   */
  private boolean isFirstNameLastNameDeptNotEmpty() {
    return (CommonUtils.isNotEmptyString(this.lastName.getText())) ||
        (CommonUtils.isNotEmptyString(this.firstName.getText())) ||
        (CommonUtils.isNotEmptyString(this.depName.getText()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);
  }


  /**
   * This method adds selection listener
   */
  public void addTableSelectionListener() {
    this.boschUserTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        IStructuredSelection selection =
            (IStructuredSelection) CreateEditA2lRespDialog.this.boschUserTableViewer.getSelection();
        CreateEditA2lRespDialog.this.deleteUserAction
            .setEnabled(CreateEditA2lRespDialog.this.boschDept.getSelection() && !selection.isEmpty());
      }
    });
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
   * This method adds sorter to table
   */
  public void invokeColumnSorter() {
    this.boschUserTableViewer.setComparator(this.sorter);
  }


  /**
   * @return the boschDept
   */
  public Button getBoschDept() {
    return this.boschDept;
  }


  /**
   * @param boschDept the boschDept to set
   */
  public void setBoschDept(final Button boschDept) {
    this.boschDept = boschDept;
  }


  /**
   * @return the selA2lResp
   */
  public A2lResponsibility getSelA2lResp() {
    return this.selA2lResp;
  }


  /**
   * @param selA2lResp the selA2lResp to set
   */
  public void setSelA2lResp(final A2lResponsibility selA2lResp) {
    this.selA2lResp = selA2lResp;
  }


}

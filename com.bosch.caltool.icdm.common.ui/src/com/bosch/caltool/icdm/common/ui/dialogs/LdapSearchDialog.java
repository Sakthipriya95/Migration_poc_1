/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
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
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.authentication.ldap.LdapException;
import com.bosch.caltool.authentication.ldap.UserAttributes;
import com.bosch.caltool.authentication.ldap.UserInfo;
import com.bosch.caltool.icdm.common.bo.user.LdapAuthenticationWrapper;
import com.bosch.caltool.icdm.common.ui.sorter.LdapUserGridTableViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.LdapUsersFilter;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Icdm-1127 new Dialog for adding non iCDM users
 *
 * @author rgo7cob
 */
public class LdapSearchDialog extends AbstractDialog {


  private static final String TITLE = "Active Directory Search";

  private static final String FILTER_TEXT = "type filter text";

  /**
   * User name col width
   */
  private static final int USER_NAME_COL_WIDTH = 200;

  /**
   * table grid height
   */
  private static final int TABLE_GRID_HEIGHT = 180;

  /**
   * User id col Width
   */
  private static final int USER_ID_COL_WIDTH = 120;

  /**
   * Dep name col width
   */
  private static final int DEP_COL_WIDTH = 120;

  /**
   * search comp num of colums
   */
  private static final int SEARCH_COMP_COL = 4;

  /**
   * user name column idx
   */
  private static final int USER_NAME_COL_IDX = 0;

  /**
   * user id Col index
   */
  private static final int USER_ID_COL_IDX = 1;

  /**
   * dep name col idx
   */
  private static final int DEP_NAME_COL_IDX = 2;


  private ComboViewer domainComboViewer;

  /**
   * Search Dialog height
   */
  private static final int SEARCH_DIA_HEIGHT = 25;

  /**
   * Search Dialog width
   */
  private static final int SEARCH_DIA_WIDTH = 92;


  /**
   * % to be shown as soon as the Job is started
   */
  private static final int JOB_START_COMP = 30;


  /**
   * % to be shown as soon as the Job is Completed
   */
  private static final int JOB_DONE = 100;


  /**
   * sorting direction
   */
  private static final int DIRECTION_UP = 1;
  /**
   * sorting direction
   */
  private static final int DIRECTION_DOWN = 0;

  /**
   *
   */
  private static final String ALL = "<ALL>";


  private Button searchBtn;

  private Composite top;

  private Composite composite;

  private FormToolkit formToolkit;

  private GridTableViewer newUserTabViewer;

  private LdapUserGridTableViewerSorter newUserTabSorter;

  private Text depName;

  private Text lastName;

  private Text firstName;

  private Section searchSection;

  private Form searchForm;

  private Section resSection;

  private Form resForm;

  private Button addButton;

  /**
   * User selected from Ldap search result table
   */
  protected UserInfo selectedUser;

  private Text userIdTxt;


  private final AddNewUserDialog addNewUserDialog;

  private Text filterTxt;

  private LdapUsersFilter ldapUserFilter;

  private Button clearBtn;

  private Shell shell;


  /**
   * @param activeShell activeShell
   * @param addNewUserDialog addNewUserDialog
   */
  public LdapSearchDialog(final Shell activeShell, final AddNewUserDialog addNewUserDialog) {
    super(activeShell);
    this.addNewUserDialog = addNewUserDialog;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle(TITLE);

    // Set the message
    setMessage(
        "Search users in Bosch active directory by providing the search criteria.\nIf search gets timed out, select a domain or update search condition(s).");
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {

    newShell.setText("Search Active Directory for Users");
    this.shell = newShell;
    super.configureShell(newShell);
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
   * @param top2
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    this.composite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.composite.setLayoutData(gridData);
    createSearchSection();
    createResultSection();

  }


  /**
   * create the Ldap Search result Section
   */
  private void createResultSection() {
    this.resSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "");
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.resSection.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout();
    this.resSection.setLayout(gridLayout);
    createResForm();
    this.resSection.getDescriptionControl().setEnabled(false);
    this.resSection.setText("Search Results");
    this.resSection.setClient(this.resForm);
    createLdapResTable(this.resForm.getBody());
  }


  /**
   * create the Ldap Search result form
   */
  private void createResForm() {
    this.resForm = this.formToolkit.createForm(this.resSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout1 = new GridLayout();
    this.resForm.getBody().setLayout(gridLayout1);
    this.resForm.getBody().setLayoutData(gridData);

  }


  /**
   * create search Section
   */
  private void createSearchSection() {

    this.searchSection = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "");
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.searchSection.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout();
    this.searchSection.setLayout(gridLayout);
    createSearchForm();
    this.searchSection.getDescriptionControl().setEnabled(false);
    this.searchSection.setText("Search Criteria");
    this.searchSection.setDescription("Use '*' for widcard search. For example: 'John*'");
    this.searchSection.setClient(this.searchForm);

    createFirstNameControl(this.searchForm.getBody());
    createLastNameControl(this.searchForm.getBody());
    createDepNameControl(this.searchForm.getBody());
    createDomainNameControl(this.searchForm.getBody());
    createUserIdControl(this.searchForm.getBody());
    getNewLabel(this.searchForm.getBody(), SWT.NONE);
    getNewLabel(this.searchForm.getBody(), SWT.NONE);
    getNewLabel(this.searchForm.getBody(), SWT.NONE);
    getNewLabel(this.searchForm.getBody(), SWT.NONE);
    createSearchButton(this.searchForm.getBody());
    createClearButton(this.searchForm.getBody());

  }

  /**
   * @param comp
   */
  private void createUserIdControl(final Composite comp) {
    createLabelControl(comp, "NT User ID");
    this.userIdTxt = createTextFileld(comp);
    this.userIdTxt.setEnabled(true);
    this.userIdTxt.setEditable(true);

    this.userIdTxt.addModifyListener(event -> enableSearch());
  }


  /**
   * @param parent Serach Form
   */
  private void createSearchButton(final Composite parent) {

    final Composite buttonGroup = new Composite(parent, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = 0;
    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = SEARCH_DIA_WIDTH;
    gridData.heightHint = SEARCH_DIA_HEIGHT;
    buttonGroup.setLayout(layout);
    this.searchBtn = new Button(buttonGroup, SWT.NONE);
    this.searchBtn.setLayoutData(gridData);
    this.searchBtn.setText("Search");
    this.searchBtn.setEnabled(false);

    this.searchBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        LdapSearchDialog.this.newUserTabViewer.setInput("");
        okPressed();
      }
    });
  }


  /**
   * @param parent Serach Form
   */
  private void createClearButton(final Composite parent) {

    final Composite buttonGroup = new Composite(parent, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.makeColumnsEqualWidth = false;
    layout.marginWidth = 0;
    final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
    gridData.widthHint = SEARCH_DIA_WIDTH;
    gridData.heightHint = SEARCH_DIA_HEIGHT;
    buttonGroup.setLayout(layout);
    this.clearBtn = new Button(buttonGroup, SWT.NONE);
    this.clearBtn.setLayoutData(gridData);
    this.clearBtn.setText("Clear All");
    this.clearBtn.setEnabled(false);

    this.clearBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        LdapSearchDialog.this.newUserTabViewer.setInput("");
        LdapSearchDialog.this.firstName.setText("");
        LdapSearchDialog.this.lastName.setText("");
        LdapSearchDialog.this.depName.setText("");
        LdapSearchDialog.this.userIdTxt.setText("");
        LdapSearchDialog.this.domainComboViewer.getCombo().select(0);
      }
    });
  }


  /**
   * create the Search Form
   */
  private void createSearchForm() {
    this.searchForm = this.formToolkit.createForm(this.searchSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = SEARCH_COMP_COL;
    this.searchForm.getBody().setLayout(gridLayout1);
    this.searchForm.getBody().setLayoutData(gridData);

  }


  /**
   * @param comp
   */
  private void createLdapResTable(final Composite comp) {
    createFilterTxt();
    this.newUserTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(comp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(TABLE_GRID_HEIGHT));

    this.newUserTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.newUserTabViewer.setInput(null);
    createGridViewerColumns();
    this.newUserTabViewer.addFilter(this.ldapUserFilter);
    createSelectionListener();
    invokeColumnSorter();
  }


  /**
   *
   */
  private void createSelectionListener() {
    this.newUserTabViewer.addSelectionChangedListener(event -> {
      if (CommonUtils.isNotNull(LdapSearchDialog.this.newUserTabViewer.getInput())) {
        ISelection selection = LdapSearchDialog.this.newUserTabViewer.getSelection();

        if (CommonUtils.isNotNull(selection) &&
            (((IStructuredSelection) selection).getFirstElement() instanceof UserInfo)) {
          LdapSearchDialog.this.selectedUser = (UserInfo) ((IStructuredSelection) selection).getFirstElement();
        }
        LdapSearchDialog.this.addButton.setEnabled(true);
      }
    });

  }


  /**
   * This method adds Columns to the addNewUserTableViewer
   */
  private void createGridViewerColumns() {
    createUserNameColumn();
    createUserIdColumn();
    createDeptColumn();
  }


  /**
   * This method adds user name column to the addNewUserTableViewer
   */
  private void createUserNameColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.newUserTabViewer, "User Name", USER_NAME_COL_WIDTH);
    // Add column selection listener
    userNameColumn.getColumn().addSelectionListener(getSelectionAdapter(userNameColumn.getColumn(), USER_NAME_COL_IDX));
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String paramName = "";
        if (element instanceof UserInfo) {
          UserInfo userInfo = (UserInfo) element;
          paramName = ApicUtil.getFullName(userInfo.getGivenName(), userInfo.getSurName());
        }
        return paramName;
      }
    });
  }

  /**
   * This method adds user id column to the addNewUserTableViewer
   */
  private void createUserIdColumn() {
    final GridViewerColumn userIdColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.newUserTabViewer, "NT User ID", USER_ID_COL_WIDTH);
    // Add column selection listener
    userIdColumn.getColumn().addSelectionListener(getSelectionAdapter(userIdColumn.getColumn(), USER_ID_COL_IDX));

    userIdColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String paramName = "";
        if (element instanceof UserInfo) {
          paramName = ((UserInfo) element).getUserName();
        }
        return paramName;
      }
    });


  }

  /**
   * This method adds department column to the addNewUserTableViewer
   */
  private void createDeptColumn() {
    final GridViewerColumn deptColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.newUserTabViewer, "Department", DEP_COL_WIDTH);
    // Add column selection listener
    deptColumn.getColumn().addSelectionListener(getSelectionAdapter(deptColumn.getColumn(), DEP_NAME_COL_IDX));

    deptColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String paramName = "";
        if (element instanceof UserInfo) {
          paramName = ((UserInfo) element).getDepartment();
        }
        return paramName;
      }
    });
  }

  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index) {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        LdapSearchDialog.this.newUserTabSorter.setColumn(index);
        int direction = LdapSearchDialog.this.newUserTabSorter.getDirection();
        for (int i = 0; i < LdapSearchDialog.this.newUserTabViewer.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == DIRECTION_DOWN) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == DIRECTION_UP) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            LdapSearchDialog.this.newUserTabViewer.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        LdapSearchDialog.this.newUserTabViewer.refresh();
      }
    };
  }

  /**
   * @param comp
   */
  private void createDepNameControl(final Composite comp) {
    createLabelControl(comp, "Department Name");
    this.depName = createTextFileld(comp);
    this.depName.setEnabled(true);
    this.depName.setEditable(true);

    this.depName.addModifyListener(event -> enableSearch());
  }


  /**
   * @param comp
   */
  private void createDomainNameControl(final Composite comp) {
    createLabelControl(comp, "Domain");

    this.domainComboViewer = new ComboViewer(comp, SWT.READ_ONLY);
    this.domainComboViewer.getCombo().setLayoutData(GridDataUtil.getInstance().getGridData());
    this.domainComboViewer.getCombo().setEnabled(true);
    this.domainComboViewer.getCombo().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    fillDomainNameCombo();
    this.domainComboViewer.getCombo().select(0);
    addDomainComboSelectionListener();
  }


  /**
   *
   */
  private void addDomainComboSelectionListener() {
    this.domainComboViewer.addSelectionChangedListener(
        // disable clear button if selection is <ALL>
        selectionChangedEvent -> this.clearBtn
            .setEnabled((this.domainComboViewer.getCombo().getSelectionIndex() != 0) || !areAllTxtBoxesEmpty()));
  }


  /**
   *
   */
  private void fillDomainNameCombo() {
    this.domainComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.domainComboViewer.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        LdapSearchDialog.this.clearBtn.setEnabled(true);
        return (String) element;
      }
    });

    try {
      SortedSet<String> donainNamesSet = new LdapAuthenticationWrapper().getDomainNames();
      this.domainComboViewer.getCombo().add(ALL, 0);
      int index = 1;
      for (String domainNameStr : donainNamesSet) {
        this.domainComboViewer.getCombo().add(domainNameStr, index);
        this.domainComboViewer.getCombo().setData(Integer.toString(index), domainNameStr);
        index++;

      }
    }
    catch (LdapException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }

  }


  /**
   * @param comp
   */
  private void createLastNameControl(final Composite comp) {
    createLabelControl(comp, "Last Name");
    this.lastName = createTextFileld(comp);
    this.lastName.setEnabled(true);
    this.lastName.setEditable(true);
    this.lastName.addModifyListener(event -> enableSearch());

  }


  /**
   * @param composite2
   */
  private void createFirstNameControl(final Composite comp) {
    createLabelControl(comp, "First Name");
    this.firstName = createTextFileld(comp);
    this.firstName.setEnabled(true);
    this.firstName.setEditable(true);

    this.firstName.addModifyListener(event -> enableSearch());
  }


  /**
   * @return
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    Map<UserAttributes, String> userInfoMap = new ConcurrentHashMap<>();
    if (CommonUtils.isNotNull(this.firstName.getText()) && (this.firstName.getText().trim().length() > 0)) {
      userInfoMap.put(UserAttributes.LDAP_ATTR_GIVEN_NAME, this.firstName.getText());
    }
    if (CommonUtils.isNotNull(this.lastName.getText()) && (this.lastName.getText().trim().length() > 0)) {
      userInfoMap.put(UserAttributes.LDAP_ATTR_SURNAME, this.lastName.getText());
    }
    if (CommonUtils.isNotNull(this.depName.getText()) && (this.depName.getText().trim().length() > 0)) {
      userInfoMap.put(UserAttributes.LDAP_ATTR_DEPARTMENT, this.depName.getText());
    }

    if (CommonUtils.isNotNull(this.userIdTxt.getText()) && (this.userIdTxt.getText().trim().length() > 0)) {
      userInfoMap.put(UserAttributes.LDAP_ATTR_USER_NAME, this.userIdTxt.getText());
    }

    runJob(userInfoMap, this.domainComboViewer.getCombo().getText());


  }

  /**
   * @param domainName
   * @param userInfoMap
   */
  private void runJob(final Map<UserAttributes, String> userInfoMap, final String domainName) {

    ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(this.shell);
    try {
      progressDialog.run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
          monitor.beginTask("Fetching the users from Active Directory ...", JOB_DONE);
          monitor.worked(JOB_START_COMP);

          try {
            String domainNameToSearch = ALL.equals(domainName) ? null : domainName;
            Set<UserInfo> ldapUserSet = new LdapAuthenticationWrapper().search(userInfoMap, domainNameToSearch);
            setAsyncInput(ldapUserSet);
          }
          catch (LdapException exp) {
            CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
          }
          monitor.done();
        }
      });
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }


  }


  // Icdm-327
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {

    this.addButton = createButton(parent, IDialogConstants.YES_ID, "Add user", false);
    this.addButton.setEnabled(false);

    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX);
    setBlockOnOpen(false);
  }


  /**
   * @param comp
   * @param string
   */
  private void createLabelControl(final Composite comp, final String lblName) {
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, lblName);

  }

  /**
   * This method creates text field
   *
   * @param caldataComp
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
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.newUserTabSorter = new LdapUserGridTableViewerSorter();
    this.newUserTabViewer.setComparator(this.newUserTabSorter);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void buttonPressed(final int buttonId) {
    try {
      if (buttonId == IDialogConstants.YES_ID) {

        SortedSet<User> allUsers = new UserServiceClient().getAll(true);
        for (User apicUser : allUsers) {
          if (apicUser.getName().equals(this.selectedUser.getUserName())) {
            addToAddNewUserDialog(apicUser);
            this.addNewUserDialog.close();
            return;
          }
        }
        createCommandForApicUsers();
        super.okPressed();
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }

    super.buttonPressed(buttonId);
  }

  /**
   * This method add user to AddNewUserDialog
   *
   * @param apicUser ApicUser
   */
  // ICDM-1909
  private void addToAddNewUserDialog(final User apicUser) {
    // validation for multi user
    if (this.addNewUserDialog.isEnableMultipleSel()) {
      List<User> list = new ArrayList<>();
      list.add(apicUser);
      this.addNewUserDialog.addMultipleUsers(list);
    }
    else {
      this.addNewUserDialog.addUsers(apicUser);
    }
  }

  /**
   * Insert the apicUser
   */
  private void createCommandForApicUsers() {
    User createdUser = null;
    User newUser = new User();
    newUser.setName(this.selectedUser.getUserName());
    newUser.setFirstName(this.selectedUser.getGivenName());
    newUser.setLastName(this.selectedUser.getSurName());
    newUser.setDepartment(this.selectedUser.getDepartment());
    try {
      createdUser = new UserServiceClient().create(newUser);
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().error(ex.getMessage(), ex);
    }
    if (null != createdUser) {
      addToAddNewUserDialog(createdUser);
    }
    this.addNewUserDialog.close();
  }


  /**
   *
   */
  private void enableSearch() {
    if (areAllTxtBoxesEmpty()) {
      LdapSearchDialog.this.searchBtn.setEnabled(false);
      LdapSearchDialog.this.clearBtn.setEnabled(false);
    }
    else {
      LdapSearchDialog.this.searchBtn.setEnabled(true);
      LdapSearchDialog.this.clearBtn.setEnabled(true);
    }

  }

  /**
   *
   */
  private boolean areAllTxtBoxesEmpty() {
    return this.firstName.getText().trim().isEmpty() && this.lastName.getText().trim().isEmpty() &&
        this.depName.getText().trim().isEmpty() && this.userIdTxt.getText().trim().isEmpty();
  }


  /**
   * Create the Filter text
   */
  private void createFilterTxt() {
    this.ldapUserFilter = new LdapUsersFilter();
    this.filterTxt = this.formToolkit.createText(this.resForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    final GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage(FILTER_TEXT);

    this.filterTxt.addModifyListener(event -> {
      final String text = LdapSearchDialog.this.filterTxt.getText().trim();
      LdapSearchDialog.this.ldapUserFilter.setFilterText(text);
      LdapSearchDialog.this.newUserTabViewer.refresh();
    });

  }

  /**
   * This method returns filter text GridData object
   *
   * @return GridData
   */
  private GridData getFilterTxtGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }


  /**
   * @param ldapUserSet
   */
  private void setAsyncInput(final Set<UserInfo> ldapUserSet) {
    Display.getDefault().asyncExec(() -> {
      LdapSearchDialog.this.newUserTabViewer.setInput(ldapUserSet);
      if (ldapUserSet.isEmpty()) {
        CDMLogger.getInstance().errorDialog("The Search criteria did not give result", Activator.PLUGIN_ID);
        LdapSearchDialog.this.addButton.setEnabled(false);
      }
    });
  }


  /**
   * @param parent lable
   * @param style style
   * @return the label of given style
   */
  private Label getNewLabel(final Composite parent, final int style) {
    return new Label(parent, style);
  }

}

package com.bosch.caltool.icdm.common.ui.dialogs; // NOPMD by dmo5cob on 8/8/14 10:24 AM

/**
 * Revision History<br>
 * Version Date UserId Description<br>
 * 0.3.0 24-Apr-2013 MGA1COB ICDM-70: First draft<br>
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.sorter.NewUserGridTableViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.NewUsersFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.providers.NewUserLabelProvider;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * This class provides a dialog to select & add new user
 *
 * @author bne4cob
 */
public abstract class AddNewUserDialog extends AbstractDialog {


  /**
   * Add new user button instance
   */
  private Button okBtn;
  /**
   * GridTableViewer instance for add new user
   */
  private GridTableViewer newUserTabViewer;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
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
   * NewUserGridTableViewerSorter instance for Columns sortting
   */
  private NewUserGridTableViewerSorter newUserTabSorter;
  /**
   * NewUsersFilter instance
   */
  private NewUsersFilter newUserFilters;

  /**
   * Default shell Title
   */
  private String shellTitle = "Add New User";

  /**
   * Default dialog Title
   */
  private String dialogTitle = "Add New User";

  /**
   * Default Message
   */
  private String messageText = "This is to add new user";

  /**
   * Label
   */
  private String okButtonLabel = "Add";

  /**
   * enable mulitple selection or not
   */
  private boolean enableMultipleSel;

  /**
   * Selected ApicUser instance
   */
  private User selectedUser;

  /**
   * Multiple users selected
   */
  private final List<User> selectedUsers = new ArrayList<>();

  protected boolean includeMonicaAuditor;


  /**
   * @param shell the parent shell
   */
  public AddNewUserDialog(final Shell shell) {
    super(shell);
  }

  /**
   * @param shell the parent shell
   * @param shellTitle shell title
   * @param dialogTitle dialog title
   * @param message dialog description
   * @param okBtnLabel ok button label
   * @param enableMultipleSel to enable multiple user selection
   * @param includeMonicaAuditor
   */
  // ICDM-487
  public AddNewUserDialog(final Shell shell, final String shellTitle, final String dialogTitle, final String message,
      final String okBtnLabel, final boolean enableMultipleSel, final boolean includeMonicaAuditor) {
    super(shell);
    // TODO: Rename the below instance vars
    this.shellTitle = shellTitle;
    this.dialogTitle = dialogTitle;
    this.messageText = message;
    this.okButtonLabel = okBtnLabel;
    this.enableMultipleSel = enableMultipleSel;
    this.includeMonicaAuditor = includeMonicaAuditor;
  }


  /**
   * @param selectedUser the selectedUser to set
   */
  public void setSelectedUser(final User selectedUser) {
    this.selectedUser = selectedUser;
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

    // Set the title
    setTitle(this.dialogTitle);

    // Set the message
    setMessage(this.messageText, IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(this.shellTitle);
    super.configureShell(newShell);
    // ICDM-153
    super.setHelpAvailable(true);
  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    try {
      createComposite();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
    }
    return this.top;
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
   * This method initializes composite
   *
   * @throws ApicWebServiceException
   */
  private void createComposite() throws ApicWebServiceException {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes section
   *
   * @throws ApicWebServiceException
   */
  private void createSection() throws ApicWebServiceException {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of users");
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   *
   * @throws ApicWebServiceException
   */
  private void createForm() throws ApicWebServiceException {
    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();

    // Create new users grid tableviewer
    createNewUsersGridTabViewer();

    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();

    // Set input to the addNewUserTableViewer
    setTabViewerInput();

    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();

    // Adds double click selection listener to the addNewUserTableViewer
    addDoubleClickListener();

    // Add filters to the TableViewer
    addFilters();

    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
    this.form.getBody().setLayout(new GridLayout());

    createToolBarAction();
  }


  /**
   * Create the PreDefined Filter for the List page- Icdm-500
   */
  private void createToolBarAction() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);

    final ToolBar toolbar = toolBarManager.createControl(this.section);
    addNonApicUserAction(toolBarManager);

    toolBarManager.update(true);

    this.section.setTextClient(toolbar);

  }

  /**
   * @param toolBarManager
   */
  private void addNonApicUserAction(final ToolBarManager toolBarManager) {
    // Create an action to add new user
    Action addNewUserAction = new Action("Active Directory Search", SWT.NONE) {

      @Override
      public void run() {
        LdapSearchDialog ucNewUserDialog =
            new LdapSearchDialog(Display.getCurrent().getActiveShell(), AddNewUserDialog.this);
        ucNewUserDialog.open();
      }
    };
    // Set the image for add user action
    addNewUserAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.FUNC_SEARCH_28X30));
    toolBarManager.add(addNewUserAction);

  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.newUserTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      /**
       * @param doubleclickevent event
       */
      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          /**
           * {@inheritDoc}
           */
          @Override
          public void run() {
            okPressed();
          }
        });
      }

    });
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.newUserTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) AddNewUserDialog.this.newUserTabViewer.getSelection();
        if ((selection == null) || (selection.isEmpty())) {
          AddNewUserDialog.this.okBtn.setEnabled(false);
          AddNewUserDialog.this.selectedUser = null;
        }
        else {
          if (AddNewUserDialog.this.enableMultipleSel) {
            // TODO: Refactor AddNewUserDialog with setEnabled(!selection.isEmpty())
            AddNewUserDialog.this.selectedUsers.clear();
            final Iterator<User> users = selection.iterator();
            while (users.hasNext()) {
              final User userToAdd = users.next();
              AddNewUserDialog.this.selectedUsers.add(userToAdd);
              AddNewUserDialog.this.okBtn.setEnabled(true);
            }
          }
          else {
            final Object element = selection.getFirstElement();
            if (element instanceof User) {
              final User apicUser = (User) element;
              AddNewUserDialog.this.selectedUser = apicUser;
              AddNewUserDialog.this.okBtn.setEnabled(true);
            }
            else {
              AddNewUserDialog.this.okBtn.setEnabled(false);
              AddNewUserDialog.this.selectedUser = null;
            }
          }
        }

      }
    });
  }

  /**
   * This method sets the input to the addNewUserTableViewer
   */
  protected void setTabViewerInput() {
    setTabViwerWSInput(getAllApicUsers(this.includeMonicaAuditor));
  }

  /**
   * This method sets the input to the addNewUserTableViewer
   *
   * @param sortedSet
   */
  public void setTabViwerWSInput(final SortedSet<User> sortedSet) {
    this.newUserTabViewer.setInput(sortedSet);
  }

  /**
   * @return all apic Users
   */
  public abstract SortedSet<User> getAllApicUsers(boolean includeMonicaAuditor);


  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.newUserTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.newUserTabViewer.setLabelProvider(new NewUserLabelProvider());
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
    final GridViewerColumn userNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.newUserTabViewer, "User Name", 200);
    // Add column selection listener
    userNameColumn.getColumn().addSelectionListener(getSelectionAdapter(userNameColumn.getColumn(), 0));
  }

  /**
   * This method adds user id column to the addNewUserTableViewer
   */
  private void createUserIdColumn() {
    final GridViewerColumn userIdColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.newUserTabViewer, "NT-Id", 120);
    // Add column selection listener
    userIdColumn.getColumn().addSelectionListener(getSelectionAdapter(userIdColumn.getColumn(), 1));
  }

  /**
   * This method adds department column to the addNewUserTableViewer
   */
  private void createDeptColumn() {
    final GridViewerColumn deptColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.newUserTabViewer, "Department", 120);
    // Add column selection listener
    deptColumn.getColumn().addSelectionListener(getSelectionAdapter(deptColumn.getColumn(), 2));
  }

  /**
   * This method creates the addNewUserTableViewer
   *
   * @param gridData
   */
  private void createNewUsersGridTabViewer() {
    if (this.enableMultipleSel) {
      this.newUserTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
          SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
          GridDataUtil.getInstance().getHeightHintGridData(200));
    }
    else {
      this.newUserTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
          SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
          GridDataUtil.getInstance().getHeightHintGridData(200));
    }
    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);
    this.filterTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = AddNewUserDialog.this.filterTxt.getText().trim();
        AddNewUserDialog.this.newUserFilters.setFilterText(text);
        AddNewUserDialog.this.newUserTabViewer.refresh();
      }
    });
    // ICDM-183
    this.filterTxt.setFocus();

  }


  /**
   * This method returns SelectionAdapter instance
   *
   * @param column
   * @param index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index) {
    final SelectionAdapter selectionAdapter = new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        AddNewUserDialog.this.newUserTabSorter.setColumn(index);
        int direction = AddNewUserDialog.this.newUserTabSorter.getDirection();
        for (int i = 0; i < AddNewUserDialog.this.newUserTabViewer.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == ApicConstants.ASCENDING) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == ApicConstants.DESCENDING) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            AddNewUserDialog.this.newUserTabViewer.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        AddNewUserDialog.this.newUserTabViewer.refresh();
      }
    };
    return selectionAdapter;
  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, this.okButtonLabel, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.selectedUser != null) {
      addUsers(this.selectedUser);
    }
    if (this.enableMultipleSel && !this.selectedUsers.isEmpty()) {
      addMultipleUsers(this.selectedUsers);
    }
    super.okPressed();
  }


  /**
   * Add multiple users
   *
   * @param userList users List
   */
  public abstract void addMultipleUsers(final List<User> userList);

  /**
   * Add users
   *
   * @param user selected User
   */
  public abstract void addUsers(final User user);


  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.newUserTabSorter = new NewUserGridTableViewerSorter();
    this.newUserTabViewer.setComparator(this.newUserTabSorter);
  }

  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addFilters() {
    this.newUserFilters = new NewUsersFilter();
    // Add PIDC Attribute TableViewer filter
    this.newUserTabViewer.addFilter(this.newUserFilters);

  }

  /**
   * {@inheritDoc}
   */
  // ICDM-153
  @Override
  protected boolean isResizable() {
    return true;
  }


  /**
   * @return the enableMultipleSel
   */
  public boolean isEnableMultipleSel() {
    return this.enableMultipleSel;
  }
}
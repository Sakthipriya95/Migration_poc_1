/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
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

import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.bo.a2l.A2lResponsibilityCommon;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.sorters.ResponsibleSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.ResponsibilesFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.WpRespType;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * The Class SetResponsibilityDialogNew.
 *
 * @author NIP4COB
 */
public class AddWpRespNonBoschDialog extends AbstractDialog {

  /** The Constant TITLE_BOSCH_DEPT for Bosch+Department */
  private static final String TITLE_BOSCH_DEPT = "Add new Bosch Department Record";

  /** The Constant TITLE_NON_BOSCH for non-Bosch user. */
  private static final String TITLE_NON_BOSCH = "Add New Non-Bosch User";

  /** The Constant TABLE_GRID_HEIGHT. */
  private static final int TABLE_GRID_HEIGHT = 200;

  /** The Constant USER_NAME_COL_WIDTH. */
  private static final int USER_NAME_COL_WIDTH = 200;

  /** First Name Col index. */
  private static final int FIRST_NAME_COL_INDEX = 0;

  /** Last Name Col index. */
  private static final int LAST_NAME_COL_INDEX = 1;

  /** dep name col idx. */
  private static final int DEP_NAME_COL_IDX = 2;

  /** dep name col idx. */
  private static final int USER_TYPE_COL_IDX = 3;

  /** alias name col idx. */
  private static final int ALIAS_NAME_COL_IDX = 4;

  /** Responsibility Type col idx. */
  private static final int RESP_TYP_COL_IDX = 5;

  /** sorting direction. */
  private static final int DIRECTION_UP = 1;

  /** sorting direction. */
  private static final int DIRECTION_DOWN = 0;

  /** The top. */
  private Composite top;

  /** The composite. */
  private Composite composite;

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /** The user section. */
  private Section userSection;

  /** The user form. */
  private Form userForm;

  /** The user tab viewer. */
  private GridTableViewer userTabViewer;

  /** The responsible sorter. */
  private ResponsibleSorter responsibleSorter;

  /** The responsibiles filter. */
  private ResponsibilesFilter responsibilesFilter;

  /** The filter txt. */
  private Text filterTxt;

  /** The a 2 l wp info bo. */
  private final A2LWPInfoBO a2lWpInfoBo;

  /** The is edit. */
  private boolean isEdit;

  /** The wp resp type. */
  private final WpRespType wpRespType;

  /** The selected wp resp. */
  private A2lResponsibility selectedA2lResp;

  /** The pidc user edit action. */
  private Action pidcUserEditAction;

  /** The existing wp resp checked. */
  private boolean existingWpRespChecked;

  /** The existing user comp. */
  private Composite existingUserComp;

  /** The A2L resp map. */
  private Map<Long, A2lResponsibility> a2lRespMap;

  /**
   * The A2L resp
   */
  private final A2lResponsibility a2lResp;

  private Button saveBtn;

  /**
   * Instantiates a new sets the responsibility dialog new.
   *
   * @param parentShell parentShell
   * @param a2lWpInfoBo a2lWpInfoBo
   * @param wpRespType selectedRespType
   * @param a2lResp the A2L resp
   */
  public AddWpRespNonBoschDialog(final Shell parentShell, final A2LWPInfoBO a2lWpInfoBo, final WpRespType wpRespType,
      final A2lResponsibility a2lResp) {
    super(parentShell);
    this.a2lWpInfoBo = a2lWpInfoBo;
    this.wpRespType = wpRespType;
    this.a2lResp = a2lResp;
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
   * set the tiltle and message
   */
  private void getDisplayMsg() {
    String msg;
    if (isNonBosch()) {
      setTitle(TITLE_NON_BOSCH);
      msg =
          "Add a new user which has no Bosch user ID. The tabel shows all Non-Bosch Users currently existing in your PIDC.";
    }
    else {
      setTitle(TITLE_BOSCH_DEPT);
      msg = "Add a new Bosch Department record.";
    }
    setMessage(msg + "\nTo add/edit an entry, use the icons above the table.");
  }

  /**
   *
   */
  private boolean isNonBosch() {
    return !"R".equals(this.wpRespType.getCode());
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
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.TITLE | SWT.MIN | SWT.RESIZE | SWT.MAX | SWT.APPLICATION_MODAL);
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
    createMainComposite();
    return this.top;
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
   * Creates the main composite.
   */
  public void createMainComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    this.composite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
    createExistingUserComposite();
    prePopulate();
  }

  /**
   * Prepopulate.
   */
  private void prePopulate() {
    if (this.a2lResp != null) {
      A2lResponsibility respObj =
          this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().get(this.a2lResp.getId());
      if (this.a2lRespMap.containsKey(respObj.getId())) {
        this.userTabViewer.setSelection(new StructuredSelection(respObj), true);
      }
      this.userTabViewer.refresh();
    }
  }


  /**
   * This method initializes composite2.
   */
  private void createExistingUserComposite() {
    GridData gridData2 = GridDataUtil.getInstance().getGridData();
    this.existingUserComp = getFormToolkit().createComposite(this.composite);
    this.existingUserComp.setLayout(new GridLayout());
    createExistingUserSection();
    this.existingUserComp.setLayoutData(gridData2);
  }


  /**
   * Creates the existing user section.
   */
  private void createExistingUserSection() {
    this.userSection = SectionUtil.getInstance().createSection(this.existingUserComp, this.formToolkit, "");
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.userSection.setLayoutData(gridData);
    GridLayout gridLayout = new GridLayout();
    this.userSection.setLayout(gridLayout);
    createUserForm();
    this.userSection.getDescriptionControl().setEnabled(false);
    this.userSection.setText("List of users");
    this.userSection.setClient(this.userForm);
    createUserTable(this.userForm.getBody());
  }


  /**
   * Creates the user table.
   *
   * @param comp the comp
   */
  private void createUserTable(final Composite comp) {
    createFilterTxt();
    this.userTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(comp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(TABLE_GRID_HEIGHT));
    createGridViewerColumns();
    this.userTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    setInputForTableViewer();
    this.userTabViewer.addFilter(this.responsibilesFilter);
    createSelectionListener();
    addDoubleClickListener();
    invokeColumnSorter();
  }

  /**
   * Invoke column sorter.
   */
  private void invokeColumnSorter() {
    this.responsibleSorter = new ResponsibleSorter();
    this.userTabViewer.setComparator(this.responsibleSorter);
  }

  /**
   * Creates the selection listener.
   */
  private void createSelectionListener() {

    this.userTabViewer.addSelectionChangedListener(arg0 -> {
      if (CommonUtils.isNotNull(AddWpRespNonBoschDialog.this.userTabViewer.getInput())) {
        ISelection selection = AddWpRespNonBoschDialog.this.userTabViewer.getSelection();
        if (CommonUtils.isNotNull(selection) &&
            (((IStructuredSelection) selection).getFirstElement() instanceof A2lResponsibility)) {
          A2lResponsibility selected = (A2lResponsibility) ((IStructuredSelection) selection).getFirstElement();
          AddWpRespNonBoschDialog.this.pidcUserEditAction.setEnabled(selected.getUserId() == null);
          if (AddWpRespNonBoschDialog.this.saveBtn != null) {
            AddWpRespNonBoschDialog.this.saveBtn.setEnabled(true);
          }
        }
      }
    });
  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   */
  private void addDoubleClickListener() {
    this.userTabViewer
        .addDoubleClickListener(event -> Display.getDefault().asyncExec(AddWpRespNonBoschDialog.this::okPressed));
  }

  /**
   * Creates the grid viewer columns.
   */
  private void createGridViewerColumns() {
    if (!"R".equals(this.wpRespType.getCode())) {
      createLastNameColumn();
      createFirstNameColumn();
    }
    else {
      createRespTypeColumn();
    }
    createDeptColumn();
    createDeletedColumn();
    createAliasNameColumn();
  }

  /**
   * Creates the Responsibility Type column.
   */
  private void createRespTypeColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.userTabViewer, "Responsibility Type", USER_NAME_COL_WIDTH);
    // Add column selection listener
    userNameColumn.getColumn().addSelectionListener(getSelectionAdapter(userNameColumn.getColumn(), RESP_TYP_COL_IDX));
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String respType = "";
        if (element instanceof A2lResponsibility) {
          A2lResponsibility obj = (A2lResponsibility) element;
          respType = AddWpRespNonBoschDialog.this.a2lWpInfoBo.getRespTypeName(obj);
        }
        return respType;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        A2lResponsibility obj = (A2lResponsibility) element;
        if (obj.isDeleted()) {
          return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

        }
        return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }
    });
  }

  /**
   * Creates the last name column.
   */
  private void createAliasNameColumn() {
    final GridViewerColumn aliasNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.userTabViewer, "Alias Name", USER_NAME_COL_WIDTH);
    // Add column selection listener
    aliasNameColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(aliasNameColumn.getColumn(), ALIAS_NAME_COL_IDX));
    aliasNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String aliasName = "";
        A2lResponsibility obj = (A2lResponsibility) element;
        aliasName = obj.getAliasName();
        return aliasName;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        A2lResponsibility obj = (A2lResponsibility) element;
        if (obj.isDeleted()) {
          return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

        }
        return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }
    });
  }

  /**
   * Creates the user type column.
   */
  private void createDeletedColumn() {
    final GridViewerColumn userNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerCheckStyleColumn(this.userTabViewer, "Deleted", 75);
    userNameColumn.getColumn().setAlignment(SWT.CENTER);

    // Add column selection listener
    userNameColumn.getColumn().addSelectionListener(getSelectionAdapter(userNameColumn.getColumn(), USER_TYPE_COL_IDX));


    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof A2lResponsibility) {
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), ((A2lResponsibility) element).isDeleted());
          gridItem.setCheckable(cell.getVisualIndex(), false);
          gridItem.setBackground(cell.getVisualIndex(),
              Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        }
      }

    });
  }

  /**
   * Creates the dept column.
   */
  private void createDeptColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.userTabViewer, "Department", USER_NAME_COL_WIDTH);
    // Add column selection listener
    userNameColumn.getColumn().addSelectionListener(getSelectionAdapter(userNameColumn.getColumn(), DEP_NAME_COL_IDX));
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String dept = "";
        if (element instanceof A2lResponsibility) {
          A2lResponsibility obj = (A2lResponsibility) element;
          dept = obj.getLDepartment();
        }
        return dept;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        A2lResponsibility obj = (A2lResponsibility) element;
        if (obj.isDeleted()) {
          return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

        }
        return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }
    });
  }

  /**
   * This method returns SelectionAdapter instance.
   *
   * @param column the column
   * @param index the index
   * @return SelectionAdapter
   */
  private SelectionAdapter getSelectionAdapter(final GridColumn column, final int index) {
    return new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        AddWpRespNonBoschDialog.this.responsibleSorter.setColumn(index);
        int direction = AddWpRespNonBoschDialog.this.responsibleSorter.getDirection();
        for (int i = 0; i < AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == DIRECTION_DOWN) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == DIRECTION_UP) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        AddWpRespNonBoschDialog.this.userTabViewer.refresh();
      }
    };
  }

  /**
   * Creates the last name column.
   */
  private void createLastNameColumn() {
    final GridViewerColumn userNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.userTabViewer, "Last Name", USER_NAME_COL_WIDTH);
    // Add column selection listener
    userNameColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(userNameColumn.getColumn(), LAST_NAME_COL_INDEX));
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String lastName = "";
        A2lResponsibility obj = (A2lResponsibility) element;
        lastName = obj.getLLastName();
        return lastName;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        A2lResponsibility obj = (A2lResponsibility) element;
        if (obj.isDeleted()) {
          return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

        }
        return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }
    });
  }

  /**
   * Creates the first name column.
   */
  private void createFirstNameColumn() {
    final GridViewerColumn userNameColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.userTabViewer, "First Name", USER_NAME_COL_WIDTH);
    // Add column selection listener
    userNameColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(userNameColumn.getColumn(), FIRST_NAME_COL_INDEX));
    userNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        String firstName = "";
        A2lResponsibility obj = (A2lResponsibility) element;
        firstName = obj.getLFirstName();
        return firstName;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Color getForeground(final Object element) {
        A2lResponsibility obj = (A2lResponsibility) element;
        if (obj.isDeleted()) {
          return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_RED);

        }
        return AddWpRespNonBoschDialog.this.userTabViewer.getGrid().getDisplay().getSystemColor(SWT.COLOR_BLACK);
      }
    });
  }

  /**
   * Creates the filter txt.
   */
  private void createFilterTxt() {
    this.responsibilesFilter = new ResponsibilesFilter();
    this.filterTxt = this.formToolkit.createText(this.userForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    final GridData gridData = getFilterTxtGridData();
    this.filterTxt.setLayoutData(gridData);
    this.filterTxt.setMessage("type here");

    this.filterTxt.addModifyListener(event -> {
      final String text = AddWpRespNonBoschDialog.this.filterTxt.getText().trim();
      AddWpRespNonBoschDialog.this.responsibilesFilter.setFilterText(text);
      AddWpRespNonBoschDialog.this.userTabViewer.refresh();
    });
  }

  /**
   * This method returns filter text GridData object.
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
   * Gets the wp resp type.
   *
   * @return the wpRespType
   */
  public WpRespType getWpRespType() {
    return this.wpRespType;
  }


  /**
   * Gets the a 2 l wp info bo.
   *
   * @return the a2lWpInfoBo
   */
  public A2LWPInfoBO getA2lWpInfoBo() {
    return this.a2lWpInfoBo;
  }


  /**
   * Gets the user tab viewer.
   *
   * @return the userTabViewer
   */
  public GridTableViewer getUserTabViewer() {
    return this.userTabViewer;
  }


  /**
   * Checks if is edits the.
   *
   * @return the isEdit
   */
  public boolean isEdit() {
    return this.isEdit;
  }


  /**
   * Sets the edits the.
   *
   * @param isEdit the isEdit to set
   */
  public void setEdit(final boolean isEdit) {
    this.isEdit = isEdit;
  }

  /**
   * Creates the user form.
   */
  private void createUserForm() {
    this.userForm = this.formToolkit.createForm(this.userSection);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    final GridLayout gridLayout1 = new GridLayout();
    this.userForm.getBody().setLayout(gridLayout1);
    this.userForm.getBody().setLayoutData(gridData);
    createToolBarAction();
  }

  /**
   * Creates the tool bar action.
   */
  private void createToolBarAction() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = toolBarManager.createControl(this.userSection);
    addEditAction(toolBarManager);
    addCreateAction(toolBarManager);
    toolBarManager.update(true);
    this.userSection.setTextClient(toolbar);
  }

  /**
   * Adds the create action.
   *
   * @param toolBarManager the tool bar manager
   */
  private void addCreateAction(final ToolBarManager toolBarManager) {
    Action createAction = new Action("Create Responsible", SWT.NONE) {

      @Override
      public void run() {
        AddEditUserDetailsDialog dialog = new AddEditUserDetailsDialog(getParentShell(),
            AddWpRespNonBoschDialog.this.wpRespType, null, AddWpRespNonBoschDialog.this.a2lWpInfoBo);
        dialog.open();

        List<A2lResponsibility> a2lRespFromDialog = dialog.getA2lResp();
        if (!CommonUtils.isNullOrEmpty(a2lRespFromDialog)) {
          a2lRespFromDialog.stream().forEach(element -> AddWpRespNonBoschDialog.this.a2lWpInfoBo
              .getA2lResponsibilityModel().getA2lResponsibilityMap().put(element.getId(), element));
          setInputForTableViewer();
          AddWpRespNonBoschDialog.this.userTabViewer.setSelection(new StructuredSelection(dialog.getA2lResp().get(0)),
              true);
          AddWpRespNonBoschDialog.this.userTabViewer.refresh();
        }
      }
    };
    createAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    toolBarManager.add(createAction);
  }


  /**
   * Adds the edit action.
   *
   * @param toolBarManager the tool bar manager
   */
  private void addEditAction(final ToolBarManager toolBarManager) {
    this.pidcUserEditAction = new Action("Edit Responsible", SWT.NONE) {

      @Override
      public void run() {
        IStructuredSelection selection = AddWpRespNonBoschDialog.this.userTabViewer.getStructuredSelection();
        A2lResponsibility selectedResponsible = (A2lResponsibility) selection.getFirstElement();

        AddWpRespNonBoschDialog.this.isEdit = true;
        AddEditUserDetailsDialog dialog;
        dialog = new AddEditUserDetailsDialog(getParentShell(), AddWpRespNonBoschDialog.this.wpRespType,
            selectedResponsible, AddWpRespNonBoschDialog.this.a2lWpInfoBo);
        dialog.open();

        List<A2lResponsibility> a2lRespFromDialog = dialog.getA2lResp();
        if (!a2lRespFromDialog.isEmpty()) {
          if (null != AddWpRespNonBoschDialog.this.a2lWpInfoBo) {
            a2lRespFromDialog.stream().forEach(element -> AddWpRespNonBoschDialog.this.a2lWpInfoBo
                .getA2lResponsibilityModel().getA2lResponsibilityMap().put(element.getId(), element));
          }
          setInputForTableViewer();
          AddWpRespNonBoschDialog.this.userTabViewer.setSelection(new StructuredSelection(dialog.getA2lResp().get(0)),
              true);
          AddWpRespNonBoschDialog.this.userTabViewer.refresh();
        }
      }

    };
    this.pidcUserEditAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EDIT_16X16));
    this.pidcUserEditAction.setEnabled(false);
    toolBarManager.add(this.pidcUserEditAction);

  }

  /**
   * set input to the table viewer
   */
  private void setInputForTableViewer() {
    if (isNonBosch()) {
      AddWpRespNonBoschDialog.this.userTabViewer.setInput(getInputForTable());
    }
    else {
      AddWpRespNonBoschDialog.this.userTabViewer.setInput(getInputForBoschDept());
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    IStructuredSelection selection = AddWpRespNonBoschDialog.this.userTabViewer.getStructuredSelection();
    if (CommonUtils.isNullOrEmpty(this.a2lRespMap.values())) {
      super.okPressed();
    }
    else {
      if (((selection != null) && (selection.getFirstElement() != null))) {
        AddWpRespNonBoschDialog.this.selectedA2lResp = (A2lResponsibility) selection.getFirstElement();
        if (this.selectedA2lResp.isDeleted()) {
          CDMLogger.getInstance().errorDialog(CommonUIConstants.DELETED_RESP_ERROR_MSG, Activator.PLUGIN_ID);
          this.selectedA2lResp = null;
        }
        else {
          super.okPressed();
        }
      }
      else {
        this.userSection.setDescription("Select a user to set as Responsible");
      }
    }
  }


  /**
   * Gets the selected pidc wp resp.
   *
   * @return selectedWpResp
   */
  public A2lResponsibility getSelectedA2lResp() {
    return this.selectedA2lResp;
  }


  /**
   * Checks if is existing wp resp checked.
   *
   * @return the existingWpRespChecked
   */
  public boolean isExistingWpRespChecked() {
    return this.existingWpRespChecked;
  }


  /**
   * Sets the existing wp resp checked.
   *
   * @param existingWpRespChecked the existingWpRespChecked to set
   */
  public void setExistingWpRespChecked(final boolean existingWpRespChecked) {
    this.existingWpRespChecked = existingWpRespChecked;
  }

  /**
   * Creates the wp resp.
   *
   * @param respToCreate the resp to create
   */
  public void createWpResp(final A2lResponsibility respToCreate) {
    A2lResponsibility createdResp = AddWpRespNonBoschDialog.this.a2lWpInfoBo.createA2lResp(respToCreate);
    if (null != createdResp) {
      AddWpRespNonBoschDialog.this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap()
          .put(createdResp.getId(), createdResp);

      AddWpRespNonBoschDialog.this.userTabViewer.setInput(getInputForTable());
      AddWpRespNonBoschDialog.this.userTabViewer.setSelection(new StructuredSelection(createdResp), true);
    }
  }

  /**
   * Gets the input for table.
   *
   * @return the input for table
   */
  private Collection<A2lResponsibility> getInputForTable() {
    this.a2lRespMap = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().values().stream()
        .filter(resp -> CommonUtils.isEqual(resp.getRespType(), this.wpRespType.getCode()) &&
            !A2lResponsibilityCommon.isDefaultResponsibility(resp))
        .collect(Collectors.toMap(A2lResponsibility::getId, r -> r));
    return this.a2lRespMap.values();
  }

  /**
   * Gets the input for table.
   *
   * @return the input for table
   */
  private Collection<A2lResponsibility> getInputForBoschDept() {
    this.a2lRespMap = this.a2lWpInfoBo.getA2lResponsibilityModel().getA2lResponsibilityMap().values().stream()
        .filter(resp -> CommonUtils.isEqual(resp.getRespType(), this.wpRespType.getCode()) &&
            !A2lResponsibilityCommon.isDefaultResponsibility(resp) && (resp.getUserId() == null))
        .collect(Collectors.toMap(A2lResponsibility::getId, r -> r));
    return this.a2lRespMap.values();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Save button
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "OK", true);
    this.saveBtn.setEnabled(false);

    // Cancel button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

}

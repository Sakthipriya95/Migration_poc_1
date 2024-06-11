/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.LabelList;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.util.A2LDataConstants.LabelType;
import com.bosch.caltool.cdr.ui.actions.RvwWPSelToolBarActionSet;
import com.bosch.caltool.cdr.ui.table.filters.RvwWrkPkgSelToolBarFilter;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.sorters.WPGridTabViewerSorter;
import com.bosch.caltool.icdm.common.ui.table.filters.WPTableFilters;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.providers.WPFormPageTableLabelProvider;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author bru2cob
 */
public class ReviewWrkPkgSelectionDialog extends AbstractDialog {

  /**
   *
   */
  private static final String NUMBER_OF_PARAMETERS = "Number of Parameters";
  /**
   *
   */
  private static final String RESPONSIBILITY = "Responsibility";
  /**
  *
  */
  private static final String RESPONSIBILITY_TYPE = "Responsibility Type";
  /**
   *
   */
  private static final String WORK_PACKAGE_NAME = "Work Package Name";
  /**
   * Constant string
   */
  private static final String SELECT_WORK_PACKAGE = "Select Work Package";
  /**
   * Constant for filter text
   */
  private static final String TYPE_FILTER_TEXT_LABEL = "type filter text";


  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Add new user button instance
   */
  private Button okBtn;
  /**
   * Form instance
   */
  private Form form;
  /**
   * Filter text instance
   */
  private Text filterTxt;
  /**
   * Filter instance
   */
  private WPTableFilters filters;
  /**
   * GridTableViewer instance for selection of variant or workpackage
   */
  private GridTableViewer wrkPkgTableViewer;
  /**
   * instance for Columns sortting
   */
  private WPGridTabViewerSorter tabSorter;

  /**
   * workpackage group name GridViewerColumn instance
   */
  private GridViewerColumn workPkgNameColumn;
  /**
   * workpackage name GridViewerColumn instance
   */
  private GridViewerColumn responsibilityColumn;
  /**
   * workpackage name GridViewerColumn instance
   */
  private GridViewerColumn responsibilityTypeColumn;
  /**
   * workpackage number GridViewerColumn instance
   */
  private GridViewerColumn workPkgLabelCountColumn;


  /**
   * Constant for add_label
   */
  private static final String ADD_LABEL = "Select";
  /**
   * List of selected elemets
   */
  private List<Object> selectedElement;
  /**
   * List of selected wp's
   */
  private List<Object> selWrkPkgElement = new ArrayList<>();
  /**
   * is wp empty boolean var
   */
  private boolean workPckgEmpty;


  private final A2LFileInfo a2lFileInfo;

  private final Map<WpRespModel, List<Long>> workPackageRespMap;

  private RvwWPSelToolBarActionSet toolbarActionSet;

  private RvwWrkPkgSelToolBarFilter toolBarFilter;

  /**
   * @param parentShell parent
   * @param a2lFileInfo A2LFileInfo
   * @param workPackageRespMap workPackageRespMap
   */
  public ReviewWrkPkgSelectionDialog(final Shell parentShell, final A2LFileInfo a2lFileInfo,
      final Map<WpRespModel, List<Long>> workPackageRespMap) {
    super(parentShell);
    this.a2lFileInfo = a2lFileInfo;
    this.workPackageRespMap = workPackageRespMap;
  }

  /**
   * Sets the Dialog Resizable
   *
   * @param newShellStyle newShellStyle
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.DIALOG_TRIM | SWT.MAX);
  }

  @Override
  protected boolean isResizable() {
    return true;
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
    setTitle(SELECT_WORK_PACKAGE);

    // Set the message
    setMessage(SELECT_WORK_PACKAGE, IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(SELECT_WORK_PACKAGE);
    super.configureShell(newShell);
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
    // create composite
    createComposite();
    return this.top;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    // create section
    createSection();
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
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
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        GridDataUtil.getInstance().getGridData(), "List of  Work Packages");
    this.section.setExpanded(true);
    createForm();
    createToolBarAction();
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);

    // Show message to the user
    this.form.setMessage(CommonUiUtils.getMessage("RVW_WORK_PKG_SEL_DIALOG", "INFO_MSG"));

    // Create Filter text
    createFilterTxt();

    // Create new users grid tableviewer
    createVariantWorkpackageGridTabViewer();

    // Set ContentProvider and LabelProvider to addNewUserTableViewer
    setTabViewerProviders();

    // Set input to the addNewUserTableViewer
    setTabViewerInput();

    // Add selection listener to the addNewUserTableViewer
    addTableSelectionListener();

    // Add double click listener for table
    addDoubleClickListener();

    // Add filters to the TableViewer
    addFilters();

    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   *
   */
  private void createToolBarAction() {
    final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
    final ToolBar toolbar = toolBarManager.createControl(this.section);
    this.toolbarActionSet = new RvwWPSelToolBarActionSet();
    this.toolbarActionSet.showBoschRespAction(toolBarManager, this.toolBarFilter, this.wrkPkgTableViewer);
    this.toolbarActionSet.showCustomerRespAction(toolBarManager, this.toolBarFilter, this.wrkPkgTableViewer);
    this.toolbarActionSet.showOthersRespAction(toolBarManager, this.toolBarFilter, this.wrkPkgTableViewer);
    toolBarManager.update(true);
    this.section.setTextClient(toolbar);
  }

  /**
   * ICDM 574-This method defines the activities to be performed when double clicked on the table
   *
   * @param functionListTableViewer2
   */
  private void addDoubleClickListener() {
    this.wrkPkgTableViewer
        .addDoubleClickListener(event -> Display.getDefault().asyncExec(ReviewWrkPkgSelectionDialog.this::okPressed));
  }

  /**
   * Add sorter for the table columns
   */
  private void invokeColumnSorter() {
    this.tabSorter = new WPGridTabViewerSorter();
    this.wrkPkgTableViewer.setComparator(this.tabSorter);
  }

  /**
   * This method adds selection listener to the addNewUserTableViewer
   */
  private void addTableSelectionListener() {
    this.wrkPkgTableViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        ReviewWrkPkgSelectionDialog.this.selWrkPkgElement.removeAll(ReviewWrkPkgSelectionDialog.this.selWrkPkgElement);
        final IStructuredSelection selection =
            (IStructuredSelection) ReviewWrkPkgSelectionDialog.this.wrkPkgTableViewer.getSelection();
        if ((selection != null) && (selection.size() != 0)) {
          List list = selection.toList();
          for (Object object : list) {
            ReviewWrkPkgSelectionDialog.this.selWrkPkgElement.add(object);
            ReviewWrkPkgSelectionDialog.this.okBtn.setEnabled(true);
          }

        }
        else {
          ReviewWrkPkgSelectionDialog.this.selWrkPkgElement = null;
          ReviewWrkPkgSelectionDialog.this.okBtn.setEnabled(false);
        }
      }
    });
  }

  /**
   * @param element selected elements
   */
  protected void setSelectedElement(final List<Object> element) {
    this.selectedElement = element;

  }

  /**
   * This method sets ContentProvider & LabelProvider to the addNewUserTableViewer
   */
  private void setTabViewerProviders() {
    this.wrkPkgTableViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.wrkPkgTableViewer.setLabelProvider(new WPFormPageTableLabelProvider(this.workPackageRespMap));
  }

  /**
   * This method sets the input to the addNewUserTableViewer
   */
  private void setTabViewerInput() {

    if ((this.workPackageRespMap != null) && !this.workPackageRespMap.isEmpty()) {
      this.wrkPkgTableViewer.setInput(this.workPackageRespMap.keySet());
    }

  }

  /**
   * checks if wp is available
   */
  public void checkIfWPAvailable() {
    setWorkPackageEmpty((this.workPackageRespMap != null) && this.workPackageRespMap.isEmpty());
  }

  /**
   * This method adds the filter instance to addNewUserTableViewer
   */
  private void addFilters() {
    // text filter
    this.filters = new WPTableFilters();
    this.filters.setWorkPackageRespMap(this.workPackageRespMap);
    this.wrkPkgTableViewer.addFilter(this.filters);
    // toolbar filter
    this.toolBarFilter = new RvwWrkPkgSelToolBarFilter();
    this.wrkPkgTableViewer.addFilter(this.toolBarFilter);

  }

  /**
   * Creates the buttons for the button bar
   *
   * @param parent the parent composite
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, ReviewWrkPkgSelectionDialog.ADD_LABEL, false);
    this.okBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    if (this.selWrkPkgElement != null) {
      setSelectedElement(this.selWrkPkgElement);
    }
    super.okPressed();
  }

  /**
   * This method creates filter text
   */
  private void createFilterTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), ReviewWrkPkgSelectionDialog.TYPE_FILTER_TEXT_LABEL);

    this.filterTxt.addModifyListener(event -> {
      String text = ReviewWrkPkgSelectionDialog.this.filterTxt.getText().trim();
      ReviewWrkPkgSelectionDialog.this.filters.setFilterText(text);
      ReviewWrkPkgSelectionDialog.this.wrkPkgTableViewer.refresh();
    });

    this.filterTxt.setFocus();

  }

  /**
   * This method creates the variantTableViewer
   *
   * @param gridData
   */
  private void createVariantWorkpackageGridTabViewer() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.heightHint = 200;
    this.wrkPkgTableViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, gridData);

    // Create GridViewerColumns
    createGridViewerColumns();
  }

  /**
   * This method adds Columns to the variantTableViewer
   */
  private void createGridViewerColumns() {
    // create group column
    this.workPkgNameColumn = new GridViewerColumn(this.wrkPkgTableViewer, SWT.NONE);
    this.workPkgNameColumn.getColumn().setText(WORK_PACKAGE_NAME);
    this.workPkgNameColumn.getColumn().setWidth(300);
    this.workPkgNameColumn.getColumn().addSelectionListener(getSelectionAdapter(this.workPkgNameColumn.getColumn(), 0));
    // create wp column
    this.responsibilityColumn = new GridViewerColumn(this.wrkPkgTableViewer, SWT.NONE);
    this.responsibilityColumn.getColumn().setText(RESPONSIBILITY);
    this.responsibilityColumn.getColumn().setWidth(300);
    this.responsibilityColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(this.responsibilityColumn.getColumn(), 1));
    // create responsibility type column
    this.responsibilityTypeColumn = new GridViewerColumn(this.wrkPkgTableViewer, SWT.NONE);
    this.responsibilityTypeColumn.getColumn().setText(RESPONSIBILITY_TYPE);
    this.responsibilityTypeColumn.getColumn().setWidth(150);
    this.responsibilityTypeColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(this.responsibilityTypeColumn.getColumn(), 2));
    // create wp num column
    this.workPkgLabelCountColumn = new GridViewerColumn(this.wrkPkgTableViewer, SWT.NONE);
    this.workPkgLabelCountColumn.getColumn().setText(NUMBER_OF_PARAMETERS);
    this.workPkgLabelCountColumn.getColumn().setWidth(150);
    this.workPkgLabelCountColumn.getColumn()
        .addSelectionListener(getSelectionAdapter(this.workPkgLabelCountColumn.getColumn(), 3));
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
        ReviewWrkPkgSelectionDialog.this.tabSorter.setColumn(index);
        int direction = ReviewWrkPkgSelectionDialog.this.tabSorter.getDirection();
        for (int i = 0; i < ReviewWrkPkgSelectionDialog.this.wrkPkgTableViewer.getGrid().getColumnCount(); i++) {
          if (i == index) {
            if (direction == 0) {
              column.setSort(SWT.DOWN);
            }
            else if (direction == 1) {
              column.setSort(SWT.UP);
            }
          }
          if (i != index) {
            ReviewWrkPkgSelectionDialog.this.wrkPkgTableViewer.getGrid().getColumn(i).setSort(SWT.NONE);
          }
        }
        ReviewWrkPkgSelectionDialog.this.wrkPkgTableViewer.refresh();
      }
    };
  }


  /**
   * @return the selectedElement
   */
  public List<Object> getSelectedElement() {
    return this.selectedElement;
  }

  /**
   * @return all functions of the selected A2l file
   */
  public String[] getAllFunctions() {
    Map<String, Function> allModulesFunctions = this.a2lFileInfo.getAllModulesFunctions();

    final SortedSet<Function> funcListOfLabelType = new TreeSet<>();
    // iterate over all functions
    for (Function function : allModulesFunctions.values()) {
      if (function != null) {
        final LabelList defLabel = function.getLabelList(LabelType.DEF_CHARACTERISTIC);
        if ((defLabel != null) && (!defLabel.isEmpty())) {
          funcListOfLabelType.add(function);
        }
      }
    }
    Iterator<Function> functions = funcListOfLabelType.iterator();
    String[] a2lFunctions = new String[funcListOfLabelType.size()];
    String funcName;
    int index = 0;
    while (functions.hasNext()) {
      Function selFunc = functions.next();
      funcName = selFunc.getName();
      a2lFunctions[index] = funcName;
      index++;
    }
    return a2lFunctions;
  }


  /**
   * @return the isWorkPackageEmpty
   */
  public boolean isWorkPackageEmpty() {
    return this.workPckgEmpty;
  }


  /**
   * @param isWorkPackageEmpty the isWorkPackageEmpty to set
   */
  public void setWorkPackageEmpty(final boolean isWorkPackageEmpty) {
    this.workPckgEmpty = isWorkPackageEmpty;
  }


}

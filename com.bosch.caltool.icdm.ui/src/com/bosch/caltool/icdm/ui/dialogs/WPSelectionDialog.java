/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.ui.sorters.WorkPackageTableSorter;
import com.bosch.caltool.icdm.ui.table.filters.WorkPackageTableFilter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.WorkPackageDivisionServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author dja7cob
 */
public class WPSelectionDialog extends AbstractDialog {

  
  private static final String SELECT_WORK_PACKAGE = "Select Work Package";
  /**
   * AddValidityAttrValDialog Title
   */
  private static final String DIALOG_TITLE = SELECT_WORK_PACKAGE;
  private static final int COLUMN_INDEX_0 = 0;
  private static final int COLUMN_INDEX_1 = 1;
  private static final int COLUMN_INDEX_2 = 2;
  private static final int COLUMN_INDEX_3 = 3;
  private static final int WP_TAB_HEIGHT = 350;

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
   * Button instance
   */
  private Button saveBtn;
  /**
   * Section instance
   */
  private Section section;
  private Form form;
  private final EditFC2WPMappingDialog editFC2WPMappingDialog;
  private Text filterTxt;
  private String selectedText;
  private GridTableViewer wpListTabViewer;
  private final Long fc2wpDivisionId;
  private Long selWpDivId;
  private boolean isSaveSuccess;
  private String selWpName;

  /**
   * @return the selWpDivId
   */
  public Long getSelWpDivId() {
    return this.selWpDivId;
  }


  /**
   * @param selWpDivId the selWpDivId to set
   */
  public void setSelWpDivId(final Long selWpDivId) {
    this.selWpDivId = selWpDivId;
  }

  private WorkPackageTableSorter wpTableSorter;
  private WorkPackageTableFilter wpTableFilter;

  /**
   * @param parentShell
   * @param editFC2WPMappingDialog
   * @param fc2wpDivisionId
   */
  public WPSelectionDialog(final Shell parentShell, final EditFC2WPMappingDialog editFC2WPMappingDialog,
      final Long fc2wpDivisionId) {
    super(parentShell);
    this.editFC2WPMappingDialog = editFC2WPMappingDialog;
    this.fc2wpDivisionId = fc2wpDivisionId;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    Control contents = super.createContents(parent);

    // Set the title
    setTitle("Choose a Work Package");
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    newShell.setLayout(new GridLayout());
    newShell.setLayoutData(gridData);
    super.configureShell(newShell);
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets .Composite)
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.top.setLayoutData(gridData);
    // create composite
    createComposite();
    return this.top;
  }

  @Override
  protected boolean isResizable() {
    return false;
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.saveBtn = createButton(parent, IDialogConstants.OK_ID, "OK", true);
    this.saveBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * ok pressed
   */
  @Override
  protected void okPressed() {
    setSaveSuccess(true);
    super.okPressed();
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  protected FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
    createTableSection();
  }

  /**
  *
  */
  private void createTableSection() {

    this.section = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, SELECT_WORK_PACKAGE);
    this.section.setLayout(new GridLayout());
    // create table form
    createTableForm();
    this.section.setClient(this.form);
  }

  /**
   *
   */
  private void createTableForm() {
    this.form = this.formToolkit.createForm(this.section);
    final GridLayout gridLayout = new GridLayout();
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    this.form.getBody().setLayout(gridLayout);
    this.form.getBody().setLayoutData(gridData);
    // create filter text for the table
    createSearchTxt();
    // craete table to display the list of functions
    createWpListTable();

    // Add listeners for table
    addDoubleClickListener();
    addTableSelectionListener();

    // add filter to the table
    addFilters();
  }


  /**
   *
   */
  private void addFilters() {
    this.wpTableFilter = new WorkPackageTableFilter();
    this.wpListTabViewer.addFilter(this.wpTableFilter);
  }

  /**
  *
  */
  private void createSearchTxt() {
    this.filterTxt = TextUtil.getInstance().createFilterText(this.formToolkit, this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), "Type Filter Text");
    this.filterTxt.addModifyListener(new ModifyListener() {

      /**
       *
       */
      @Override
      public void modifyText(final ModifyEvent event) {
        WPSelectionDialog.this.selectedText = WPSelectionDialog.this.filterTxt.getText().trim();
        WPSelectionDialog.this.wpTableFilter.setFilterText(WPSelectionDialog.this.selectedText);
        WPSelectionDialog.this.wpListTabViewer.refresh();
      }
    });
    this.filterTxt.setFocus();
  }

  /**
   * Create validity value table
   */
  private void createWpListTable() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    gridData.heightHint = WP_TAB_HEIGHT;
    this.wpListTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
    this.wpListTabViewer.getGrid().setLayout(new GridLayout());
    this.wpListTabViewer.getGrid().setLayoutData(gridData);
    this.wpListTabViewer.getGrid().setLinesVisible(true);
    this.wpListTabViewer.getGrid().setHeaderVisible(true);
    this.wpListTabViewer.setContentProvider(new ArrayContentProvider());

    // Create sorter for the table
    this.wpTableSorter = new WorkPackageTableSorter();
    this.wpListTabViewer.setComparator(this.wpTableSorter);
    // Create GridViewerColumns
    createWpNameColumn();
    createWpDescColumn();
    createResourceColumn();
    createGroupColumn();

    Set<WorkPackageDivision> wpSet = new HashSet<>();
    getInpForTable(wpSet);
    // Set input to the table
    this.wpListTabViewer.setInput(wpSet);
  }


  /**
   * @param wpSet
   */
  private void getInpForTable(final Set<WorkPackageDivision> wpSet) {
    Set<WorkPackageDivision> wpServiceSet = new HashSet<>();

    WorkPackageDivisionServiceClient servClient = new WorkPackageDivisionServiceClient();
    try {
      wpServiceSet = servClient.getWPDivisionsByByDivID(this.fc2wpDivisionId, false);
    }
    catch (Exception exp) {
      CDMLogger.getInstance().error("Error fetching work package divisions. " + exp.getMessage(), exp,
          Activator.PLUGIN_ID);
    }

    wpSet.add(createDummyElement());

    wpSet.addAll(wpServiceSet);
    if (!this.editFC2WPMappingDialog.isMultipleUpdate()) {
      for (WorkPackageDivision wpDetail : wpServiceSet) {
        if (wpDetail.getId().equals(this.editFC2WPMappingDialog.getWpDivIdToSave())) {
          wpSet.remove(wpDetail);
        }
      }
    }
  }

  /**
   * @return
   */
  private WorkPackageDivision createDummyElement() {
    WorkPackageDivision dummyWP = new WorkPackageDivision();
    dummyWP.setWpId(null);
    dummyWP.setId(null);
    return dummyWP;
  }


  /**
   * Create column for validity value description
   */
  private void createWpNameColumn() {
    final GridViewerColumn wpNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpListTabViewer, "Work Package Name", 200);
    wpNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(wpNameColumn.getColumn(), COLUMN_INDEX_0, this.wpTableSorter, this.wpListTabViewer));
    // Label provider for validity value name column
    wpNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDivision icdmWp = (WorkPackageDivision) element;
        return icdmWp.getWpName();
      }
    });
  }

  /**
   * Create column for validity value description
   */
  private void createWpDescColumn() {
    final GridViewerColumn wpDescColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.wpListTabViewer, "Work Package Description", 300);
    wpDescColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(wpDescColumn.getColumn(), COLUMN_INDEX_1, this.wpTableSorter, this.wpListTabViewer));
    // Label provider for validity value name column
    wpDescColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDivision icdmWp = (WorkPackageDivision) element;
        return icdmWp.getWpDesc();
      }
    });
  }

  /**
   * Create column for validity value description
   */
  private void createResourceColumn() {
    final GridViewerColumn wpResColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpListTabViewer, "Resource", 100);
    wpResColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(wpResColumn.getColumn(), COLUMN_INDEX_2, this.wpTableSorter, this.wpListTabViewer));
    // Label provider for validity value name column
    wpResColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDivision icdmWp = (WorkPackageDivision) element;
        return icdmWp.getWpResource();
      }
    });
  }

  /**
   * Create column for validity value description
   */
  private void createGroupColumn() {
    final GridViewerColumn wpGrpColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.wpListTabViewer, "Group", 100);
    wpGrpColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(wpGrpColumn.getColumn(), COLUMN_INDEX_3, this.wpTableSorter, this.wpListTabViewer));
    // Label provider for validity value name column
    wpGrpColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        WorkPackageDivision icdmWp = (WorkPackageDivision) element;
        return icdmWp.getWpGroup();
      }
    });
  }

  /**
   * This method add selection listener to valTableViewer
   */
  private void addTableSelectionListener() {
    this.wpListTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * Table selection
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {

        final IStructuredSelection selection =
            (IStructuredSelection) WPSelectionDialog.this.wpListTabViewer.getSelection();
        if (selection != null) {

          final List<WorkPackageDivision> selWpList = getSelWpFromTabViewer();

          if (null != selWpList.get(0)) {
            WPSelectionDialog.this.selWpDivId = selWpList.get(0).getId();
            WPSelectionDialog.this.selWpName = selWpList.get(0).getWpName();
            WPSelectionDialog.this.saveBtn.setEnabled(true);
          }
        }
        else {
          WPSelectionDialog.this.saveBtn.setEnabled(false);
        }
      }
    });
  }

  /**
   * @return Attribute
   */
  protected List<WorkPackageDivision> getSelWpFromTabViewer() {
    List<WorkPackageDivision> selAttrList = new ArrayList<WorkPackageDivision>();
    final IStructuredSelection selection = (IStructuredSelection) this.wpListTabViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final List<IStructuredSelection> elementList = selection.toList();
      if (elementList.get(0) instanceof WorkPackageDivision) {
        selAttrList.addAll((Collection<? extends WorkPackageDivision>) elementList);
      }
    }
    return selAttrList;
  }

  /**
   * @param tableviewer tableviewer
   */
  protected void addDoubleClickListener() {
    this.wpListTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            final IStructuredSelection selection =
                (IStructuredSelection) WPSelectionDialog.this.wpListTabViewer.getSelection();
            if (!selection.isEmpty() && (selection.getFirstElement() instanceof WorkPackageDivision)) {
              WorkPackageDivision selWp = (WorkPackageDivision) selection.getFirstElement();
              WPSelectionDialog.this.selWpDivId = selWp.getId();
              WPSelectionDialog.this.selWpName = selWp.getWpName();
              WPSelectionDialog.this.saveBtn.setEnabled(true);
            }
            okPressed();
          }
        });
      }
    });
  }


  /**
   * @return the isSaveSuccess
   */
  public boolean isSaveSuccess() {
    return this.isSaveSuccess;
  }


  /**
   * @param isSaveSuccess the isSaveSuccess to set
   */
  public void setSaveSuccess(final boolean isSaveSuccess) {
    this.isSaveSuccess = isSaveSuccess;
  }


  /**
   * @return the selWpName
   */
  public String getSelWpName() {
    return this.selWpName;
  }


  /**
   * @param selWpName the selWpName to set
   */
  public void setSelWpName(final String selWpName) {
    this.selWpName = selWpName;
  }
}

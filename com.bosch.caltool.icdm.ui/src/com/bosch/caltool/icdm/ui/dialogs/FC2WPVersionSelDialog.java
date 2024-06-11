/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.sorters.FC2WPVersTableSorter;
import com.bosch.caltool.icdm.ui.table.filters.FC2WPVersTableFilter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPDefinitionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author bru2cob
 */
public class FC2WPVersionSelDialog extends AbstractDialog {

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
  private Text filterTxt;
  private String selectedText;
  private GridTableViewer fc2wpVersTabViewer;
  private FC2WPVersTableSorter fc2wpVersTabSorter;
  private FC2WPVersTableFilter fc2wpTableFilter;
  private final ConcurrentHashMap<FC2WPVersion, FC2WPDef> allVersionsMap =
      new ConcurrentHashMap<FC2WPVersion, FC2WPDef>();


  /**
   * @return the allVersionsMap
   */
  public ConcurrentMap<FC2WPVersion, FC2WPDef> getAllVersionsMap() {
    return this.allVersionsMap;
  }

  private final FC2WPDef fc2wpDef;
  private final List<FC2WPVersion> selVersList = new ArrayList<FC2WPVersion>();

  /**
   * @param parentShell
   * @param fc2wpDef
   */
  public FC2WPVersionSelDialog(final Shell parentShell, final FC2WPDef fc2wpDef) {
    super(parentShell);
    this.fc2wpDef = fc2wpDef;
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
    setTitle("FC2WP Comparison");
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("FC2WP Comparison");
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
    this.saveBtn.setEnabled(true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * ok pressed
   */
  @Override
  protected void okPressed() {
    IStructuredSelection selection =
        (IStructuredSelection) FC2WPVersionSelDialog.this.fc2wpVersTabViewer.getSelection();

    if (!selection.isEmpty()) {
      this.selVersList.addAll(selection.toList());
    }
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

    this.section = SectionUtil.getInstance().createSection(this.composite, this.formToolkit, "Select Work Package");
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
    createFC2WPVersListTable();

    // Add listeners for table
    addDoubleClickListener();

    // add filter to the table
    addFilters();
  }


  /**
   * @return the selVersList
   */
  public List<FC2WPVersion> getSelVersList() {
    return this.selVersList;
  }


  /**
   *
   */
  private void addDoubleClickListener() {
    this.fc2wpVersTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            okPressed();
          }
        });
      }
    });
  }

  /**
   *
   */
  private void createFC2WPVersListTable() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    gridData.heightHint = 350;
    this.fc2wpVersTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(),
        SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
    this.fc2wpVersTabViewer.getGrid().setLayout(new GridLayout());
    this.fc2wpVersTabViewer.getGrid().setLayoutData(gridData);
    this.fc2wpVersTabViewer.getGrid().setLinesVisible(true);
    this.fc2wpVersTabViewer.getGrid().setHeaderVisible(true);
    this.fc2wpVersTabViewer.setContentProvider(new ArrayContentProvider());

    // Create sorter for the table
    this.fc2wpVersTabSorter = new FC2WPVersTableSorter(this);
    this.fc2wpVersTabViewer.setComparator(this.fc2wpVersTabSorter);
    // Create GridViewerColumns
    createNameColumn();
    createDivColumn();
    createVersColumn();


    getInputData();
    // Set input to the table
    SortedSet<FC2WPVersion> vers = new TreeSet<FC2WPVersion>(this.allVersionsMap.keySet());
    this.fc2wpVersTabViewer.setInput(vers);
  }

  /**
   *
   */
  private void getInputData() {

    try {
      // create a webservice client
      final FC2WPDefinitionServiceClient fc2wpClient = new FC2WPDefinitionServiceClient();
      // Load the contents using webservice calls
      Set<FC2WPDef> fc2wpDefSet = fc2wpClient.getAll();
      final FC2WPVersionServiceClient client = new FC2WPVersionServiceClient();
      if ((fc2wpDefSet != null) && !fc2wpDefSet.isEmpty()) {
        fc2wpDefSet.remove(this.fc2wpDef);
        addVersionsToMap(fc2wpDefSet, client);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }


  }

  /**
   * @param fc2wpDefSet
   * @param client
   * @throws ApicWebServiceException
   */
  private void addVersionsToMap(final Set<FC2WPDef> fc2wpDefSet, final FC2WPVersionServiceClient client)
      throws ApicWebServiceException {
    for (FC2WPDef def : fc2wpDefSet) {
      Set<FC2WPVersion> versionsByDefID = client.getVersionsByDefID(def.getId());
      for (FC2WPVersion vers : versionsByDefID) {
        this.allVersionsMap.put(vers, def);
      }
    }
  }

  /**
   *
   */
  private void createVersColumn() {
    final GridViewerColumn wpNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fc2wpVersTabViewer, "FC2WP Name", 200);
    wpNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        wpNameColumn.getColumn(), ApicConstants.COLUMN_INDEX_2, this.fc2wpVersTabSorter, this.fc2wpVersTabViewer));
    // Label provider for validity value name column
    wpNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        FC2WPVersion fc2wpVers = (FC2WPVersion) element;
        return fc2wpVers.getVersionName();
      }
    });
  }

  /**
   *
   */
  private void createDivColumn() {
    final GridViewerColumn wpNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fc2wpVersTabViewer, "Division", 200);
    wpNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        wpNameColumn.getColumn(), ApicConstants.COLUMN_INDEX_1, this.fc2wpVersTabSorter, this.fc2wpVersTabViewer));
    // Label provider for validity value name column
    wpNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        FC2WPVersion fc2wpVers = (FC2WPVersion) element;
        return FC2WPVersionSelDialog.this.allVersionsMap.get(fc2wpVers).getDivisionName();
      }
    });
  }

  /**
   *
   */
  private void createNameColumn() {
    final GridViewerColumn wpNameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.fc2wpVersTabViewer, "FC2WP Version", 200);
    wpNameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance().getSelectionAdapter(
        wpNameColumn.getColumn(), ApicConstants.COLUMN_INDEX_0, this.fc2wpVersTabSorter, this.fc2wpVersTabViewer));
    // Label provider for validity value name column
    wpNameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        FC2WPVersion fc2wpVers = (FC2WPVersion) element;
        String name = fc2wpVers.getName();
        return name.substring(0, name.indexOf('('));
      }
    });
  }

  /**
   *
   */
  private void addFilters() {
    this.fc2wpTableFilter = new FC2WPVersTableFilter(this);
    this.fc2wpVersTabViewer.addFilter(this.fc2wpTableFilter);
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
        FC2WPVersionSelDialog.this.selectedText = FC2WPVersionSelDialog.this.filterTxt.getText().trim();
        FC2WPVersionSelDialog.this.fc2wpTableFilter.setFilterText(FC2WPVersionSelDialog.this.selectedText);
        FC2WPVersionSelDialog.this.fc2wpVersTabViewer.refresh();
      }
    });
    this.filterTxt.setFocus();
  }

}

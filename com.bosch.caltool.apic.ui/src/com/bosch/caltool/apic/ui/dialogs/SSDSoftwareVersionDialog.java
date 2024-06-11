/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.editors.pages.A2LFilePage;
import com.bosch.caltool.apic.ui.sorter.SSDVersionSorter;
import com.bosch.caltool.apic.ui.table.filters.SSDVersionTabFilter;
import com.bosch.caltool.apic.ui.util.Messages;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.cdr.SSDProjectVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author rgo7cob
 */
public class SSDSoftwareVersionDialog extends AbstractDialog {

  /**
   * Value col width
   */
  private static final int ORIGINAL_NAME_COL_WIDTH = 200;
  /**
   * Value table height hint
   */
  private static final int VERSION_TAB_HEIGHT_HINT = 200;
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
   * GridTableViewer instance for add values
   */
  private GridTableViewer versionTabViewer;
  /**
   * Instance of a2l file page
   */
  private final A2LFilePage a2lFilePage;
  /**
   * List of software versions
   */
  private SortedSet<SSDProjectVersion> swVersionListBySwProjId;
  // table sorter
  private AbstractViewerSorter versionTabSorter;
  /**
   * Instance of filter text
   */
  private Text filterTxt;

  /**
   * PIDCAttrValTabFilter PIDC attribute value GridTableViewer filter instance
   */
  private AbstractViewerFilter verTabFilter;
  /**
   * ok button
   */
  private Button okBtn;
  /**
   * Selected SSD project version
   */
  protected SSDProjectVersion selVersionFromTable;
  /**
   * Constant for dummy ID
   */
  public static final Long SW_ID = 0L;

  /**
   * @param shell Shell
   * @param a2lFilePage A2LFilePage
   * @param ssdProjNodeAttrVal ssdProjNodeAttrVal
   */
  public SSDSoftwareVersionDialog(final Shell shell, final A2LFilePage a2lFilePage,
      final AttributeValue ssdProjNodeAttrVal) {
    // calling parent constructor
    super(shell);
    this.a2lFilePage = a2lFilePage;
    try {
      this.swVersionListBySwProjId = new TreeSet<>();
      this.swVersionListBySwProjId.add(createDummyObject());
      SortedSet<SSDProjectVersion> ssdVersions =
          new PidcA2lServiceClient().getSSDServiceHandler(ssdProjNodeAttrVal.getNumValue().longValue());
      this.swVersionListBySwProjId.addAll(ssdVersions);
    }
    catch (Exception exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Create dummy object
   *
   * @return
   */
  private SSDProjectVersion createDummyObject() {
    SSDProjectVersion ssdPrjVrsn = new SSDProjectVersion();
    ssdPrjVrsn.setProjectId(SW_ID);
    ssdPrjVrsn.setVersionDesc(ApicConstants.EMPTY_STRING);
    ssdPrjVrsn.setVersionId(SW_ID);
    ssdPrjVrsn.setVersionName(ApicConstants.EMPTY_STRING);
    ssdPrjVrsn.setVersionNumber(ApicConstants.EMPTY_STRING);
    return ssdPrjVrsn;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Select SSD Software version for A2L");
    // Set the message
    setMessage("A2L Name : " + this.a2lFilePage.getSelA2LFile().get(0).getA2lFile().getFilename(),
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Select SSD Software version");
    // calling parent
    super.configureShell(newShell);

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
    // create composite on parent comp
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
   * This method initializes section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        "List of SSD Software versions", ExpandableComposite.TITLE_BAR);
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // Create Filter text
    createFilterTxt();
    // Create values grid tableviewer
    createVersionGridTabViewer();
    // add double click listener
    addDoubleClickListener();
    // Add filters to the TableViewer
    addFilters();

    // set the layout
    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   * create button for Button Bar
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
    this.okBtn.setEnabled(false);

    // creating cancel button
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
  }

  /**
   * add table filters
   */
  private void addFilters() {
    this.verTabFilter = new SSDVersionTabFilter();
    // Add PIDC Attribute TableViewer filter
    this.versionTabViewer.addFilter(this.verTabFilter);

  }

  /**
   * Create filter text
   */
  private void createFilterTxt() {
    // Filetr text for table
    this.filterTxt = TextUtil.getInstance().createFilterText(getFormToolkit(), this.form.getBody(),
        GridDataUtil.getInstance().getTextGridData(), Messages.getString(IMessageConstants.TYPE_FILTER_TEXT_LABEL));
    this.filterTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent event) {
        final String text = SSDSoftwareVersionDialog.this.filterTxt.getText().trim();
        SSDSoftwareVersionDialog.this.verTabFilter.setFilterText(text);
        SSDSoftwareVersionDialog.this.versionTabViewer.refresh();
      }
    });
    // ICDM-183
    this.filterTxt.setFocus();

  }

  /**
   * This method defines the activities to be performed when double clicked on the table
   */
  private void addDoubleClickListener() {
    // double click listener for table
    this.versionTabViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent doubleclickevent) {
        Display.getDefault().asyncExec(new Runnable() {

          @Override
          public void run() {
            SSDSoftwareVersionDialog.this.selVersionFromTable = getSelVersionFromTable();
            okPressed();
          }
        });
      }

    });
  }

  /**
   * Create versions grid table viewer
   */
  private void createVersionGridTabViewer() {

    // create table viewer
    this.versionTabSorter = new SSDVersionSorter();
    int style;
    style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE;
    this.versionTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(), style,
        GridDataUtil.getInstance().getHeightHintGridData(VERSION_TAB_HEIGHT_HINT));
    // set table content provider
    this.versionTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    // create Version Column
    createVersionCol();
    // create Version number Column
    createVerNumCol();

    // create project id col
    createVersDescCol();
    // set table input
    setTableInput();

    // Invokde GridColumnViewer sorter
    invokeColumnSorter();
    // Selection listener for the table
    addSelListener();

  }

  /**
   * add selection listener to the table
   */
  private void addSelListener() {
    // Selection listener for table
    this.versionTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        SSDSoftwareVersionDialog.this.selVersionFromTable = getSelVersionFromTable();
        SSDSoftwareVersionDialog.this.okBtn.setEnabled(true);
      }


    });

  }

  /**
   * @return
   */
  protected SSDProjectVersion getSelVersionFromTable() {
    // Selected SSD project version
    SSDProjectVersion projVersion = null;
    final IStructuredSelection selection = (IStructuredSelection) this.versionTabViewer.getSelection();
    if ((selection != null) && (selection.size() != 0)) {
      final Object element = selection.getFirstElement();
      if (element instanceof SSDProjectVersion) {
        projVersion = (SSDProjectVersion) element;
      }
    }
    return projVersion;
  }

  /**
   * invoke column sorter
   */
  private void invokeColumnSorter() {
    this.versionTabViewer.setComparator(this.versionTabSorter);

  }

  /**
   * Software Project id col
   */
  private void createVersDescCol() {
    final GridViewerColumn versionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "Software Version description", ORIGINAL_NAME_COL_WIDTH);
    versionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDProjectVersion vcdmA2lFileDetail = (SSDProjectVersion) element;
        return vcdmA2lFileDetail.getVersionDesc();
      }
    });
    versionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionColumn.getColumn(), 2, this.versionTabSorter, this.versionTabViewer));

  }

  /**
   * Set table input
   */
  private void setTableInput() {
    // set table input
    this.versionTabViewer.setInput(this.swVersionListBySwProjId);
  }


  /**
   * Create Original A2L name column
   */
  private void createVerNumCol() {
    final GridViewerColumn versionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "Software version Number", ORIGINAL_NAME_COL_WIDTH);
    versionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDProjectVersion vcdmA2lFileDetail = (SSDProjectVersion) element;
        return vcdmA2lFileDetail.getVersionNumber();
      }
    });
    versionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionColumn.getColumn(), 1, this.versionTabSorter, this.versionTabViewer));
  }

  /**
   * Create Original A2L name column
   */
  private void createVersionCol() {
    final GridViewerColumn versionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "Software version name", ORIGINAL_NAME_COL_WIDTH);
    versionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDProjectVersion vcdmA2lFileDetail = (SSDProjectVersion) element;
        return vcdmA2lFileDetail.getVersionName();
      }
    });

    versionColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(versionColumn.getColumn(), 0, this.versionTabSorter, this.versionTabViewer));
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
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {
    boolean isUnMap = false;
    if (this.selVersionFromTable.getProjectId() == 0L) {
      isUnMap = true;
    }
    updateSSDSoftwareVersion(isUnMap);

    super.okPressed();
  }


  private void updateSSDSoftwareVersion(final boolean isUnMap) {
    List<PidcA2lFileExt> pidcA2lFileExts = this.a2lFilePage.getSelA2LFile();
    Set<PidcA2l> pidcA2lstoUpdate = new HashSet<>();

    for (PidcA2lFileExt pidcA2lFileExt : pidcA2lFileExts) {
      PidcA2l pidcA2l = pidcA2lFileExt.getPidcA2l();
      if (isUnMap) {
        pidcA2l.setSsdSoftwareProjId(null);
        pidcA2l.setSsdSoftwareVersion(null);
        pidcA2l.setSsdSoftwareVersionId(null);
      }
      else {
        pidcA2l.setSsdSoftwareProjId(this.selVersionFromTable.getProjectId());
        pidcA2l.setSsdSoftwareVersionId(this.selVersionFromTable.getVersionId());
        pidcA2l.setSsdSoftwareVersion(this.selVersionFromTable.getVersionName());
      }
      pidcA2lstoUpdate.add(pidcA2l);
    }
    this.a2lFilePage.getHandler().updatePidcA2l(pidcA2lstoUpdate);

  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
  }
}

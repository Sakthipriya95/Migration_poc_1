/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.pages.A2LFilePage;
import com.bosch.caltool.icdm.client.bo.a2l.A2LFileBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.a2l.VCDMA2LFileDetail;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author jvi6cob
 */
public class A2lFileDetailsDialog extends AbstractDialog {

  /**
   * Value col width
   */
  private static final int ORIGINAL_NAME_COL_WIDTH = 200;
  private static final int ORIGINAL_DATE_COL_WIDTH = 120;
  private static final int PST_COL_WIDTH = 120;
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
   * Constant for dummy ID value
   */
  public static final Long VALUE_ID = 0L;

  /**
   * @param shell
   * @param a2lFilePage
   */
  public A2lFileDetailsDialog(final Shell shell, final A2LFilePage a2lFilePage) {
    // calling parent constructor
    super(shell);
    this.a2lFilePage = a2lFilePage;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Select A2L File Name (VCDM)");
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
    newShell.setText("Select A2L File Name");
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
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), "List of A2L File Names",
        ExpandableComposite.TITLE_BAR);
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

    // Create values grid tableviewer
    createVersionGridTabViewer();
    // add double click listener
    addDoubleClickListener();
    // set the layout
    this.form.getBody().setLayout(new GridLayout());

  }

  /**
   * This method defines the activities to be performed when double clicked on the table
   */
  private void addDoubleClickListener() {
    this.versionTabViewer.addDoubleClickListener(event -> Display.getDefault().asyncExec(this::okPressed));
  }

  /**
   * Create versions grid table viewer
   */
  private void createVersionGridTabViewer() {
    int style;
    style = SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE;
    this.versionTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.form.getBody(), style,
        GridDataUtil.getInstance().getHeightHintGridData(VERSION_TAB_HEIGHT_HINT));
    // set table content provider
    this.versionTabViewer.setContentProvider(ArrayContentProvider.getInstance());

    // create Original File Name col
    createOriginalFileNameCol();
    // create Original File Date col
    createOriginalFileDateCol();
    // create PST col
    createPSTCol();
    // set table input
    setTableInput();


  }

  /**
   * Set table input
   */
  private void setTableInput() {

    Set<VCDMA2LFileDetail> vcdmA2lFileDetails = null;
    // Can only be one selected A2LFile
    A2LFile a2lFile = this.a2lFilePage.getSelA2LFile().get(0).getA2lFile();
    A2LFileInfoServiceClient client = new A2LFileInfoServiceClient();

    if (a2lFile != null) {
      try {
        vcdmA2lFileDetails = client.getVCDMA2LFileDetails(a2lFile.getA2lfilechecksum());
        // Add the a2lFile name from TabvA2lFileInfo view if user doesnt want to use alternate a2l file names from VCDM
        VCDMA2LFileDetail fileDetail = new VCDMA2LFileDetail();
        fileDetail.setOriginalFileName(a2lFile.getFilename());
        vcdmA2lFileDetails.add(fileDetail);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        vcdmA2lFileDetails = Collections.emptySet();
      }
    }
    // set table input
    this.versionTabViewer.setInput(new TreeSet<>(vcdmA2lFileDetails));
  }


  /**
   * Create Original A2L name column
   */
  private void createOriginalFileNameCol() {
    final GridViewerColumn versionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "Original A2L Name", ORIGINAL_NAME_COL_WIDTH);
    versionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        VCDMA2LFileDetail vcdmA2lFileDetail = (VCDMA2LFileDetail) element;
        return vcdmA2lFileDetail.getOriginalFileName();
      }
    });
  }

  /**
   * Create Original File Date column
   */
  private void createOriginalFileDateCol() {
    final GridViewerColumn descriptionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "Original File Date", ORIGINAL_DATE_COL_WIDTH);
    descriptionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        VCDMA2LFileDetail vcdmA2lFileDetail = (VCDMA2LFileDetail) element;
        String originalDate = "";

        if (vcdmA2lFileDetail.getOriginalDate() != null) {
          originalDate = vcdmA2lFileDetail.getOriginalDate();
        }
        return originalDate;
      }
    });
  }

  /**
   * Create PST column
   */
  private void createPSTCol() {
    final GridViewerColumn descriptionColumn = GridViewerColumnUtil.getInstance()
        .createGridViewerColumn(this.versionTabViewer, "VCDM PST Name", PST_COL_WIDTH);
    descriptionColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        VCDMA2LFileDetail vcdmA2lFileDetail = (VCDMA2LFileDetail) element;
        return vcdmA2lFileDetail.getPst();
      }
    });
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
    VCDMA2LFileDetail selcVCDMA2LFileDetail = null;
    final IStructuredSelection selection =
        (IStructuredSelection) A2lFileDetailsDialog.this.versionTabViewer.getSelection();
    // get the selected vcdm a2l details
    if (!selection.isEmpty() && (selection.size() == 1)) {
      selcVCDMA2LFileDetail = (VCDMA2LFileDetail) selection.getFirstElement();
    }
    // update the values
    updateVcdmA2lName(selcVCDMA2LFileDetail);
    super.okPressed();
    this.a2lFilePage.getA2lFileMappingTab().getA2lTabViewer()
        .setSelection(this.a2lFilePage.getA2lFileMappingTab().getA2lTabViewer().getSelection());
  }


  /**
   * @param vcdmFileDetail
   */
  public void updateVcdmA2lName(final VCDMA2LFileDetail vcdmFileDetail) {
    PidcA2lFileExt pidcA2lFileExt = this.a2lFilePage.getSelA2LFile().get(0);
    A2LFile selA2lFile = pidcA2lFileExt.getA2lFile();
    String originalFileName = vcdmFileDetail.getOriginalFileName();

    if (new A2LFileBO(selA2lFile).getA2LFileNameFromA2lFileInfo().equals(originalFileName)) {
      originalFileName = null;
    }
    PidcA2l pidcA2l = pidcA2lFileExt.getPidcA2l();
    pidcA2l.setVcdmA2lName(originalFileName);
    pidcA2l.setVcdmA2lDate(vcdmFileDetail.getOriginalDate());
    Set<PidcA2l> pidcA2ls = new HashSet<>();
    pidcA2ls.add(pidcA2l);
    this.a2lFilePage.getHandler().updatePidcA2l(pidcA2ls);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
  }
}

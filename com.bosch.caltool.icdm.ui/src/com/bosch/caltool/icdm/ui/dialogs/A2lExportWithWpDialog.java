/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.FileNameUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFileExportServiceInput;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.sorters.A2lWpDefVersionSorter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileExportServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lVariantGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author and4cob
 */
public class A2lExportWithWpDialog extends AbstractDialog {


  /** Top composite. */
  private Composite top;

  /** FormToolkit instance. */
  private FormToolkit formToolkit;

  /** Composite instance for the dialog. */
  private Composite a2lComposite;

  /** Section instance. */
  private Section a2lSection;

  /** Form instance. */
  private Form a2lForm;

  /**
   * Text field for A2L File Destination Path
   */
  private Text a2lPathTxt;

  /**
   * Export A2L File Button
   */
  private Button exportA2lBtn;

  /**
   * Combo box for selecting the Active Version
   */
  private ComboViewer wpDefnVersComboViewer;

  /**
   * Combo box for selecting the Variant Group
   */
  private ComboViewer variantGrpComboViewer;
  /**
   * File path selected
   */
  protected String filePath;

  /**
   * New A2L File name
   */
  private Text fileNameTxt;

  /**
   * pidc A2l object
   */
  private final PidcA2l pidcA2l;
  /**
   * Select variant grp id
   */
  private final Long varGrpId;

  private String newFileName;

  private Map<Long, A2lWpDefnVersion> a2lWpDefnVersionMap;

  /**
   * constants
   */
  private static final String GROUPS = "_GROUPS_";
  private static final String DEFAULT = "<DEFAULT>";
  private static final String A2L_EXT = ".A2L";
  // prefix to display Version Number
  private static final String VERS = "V";

  private String outputFilePath;

  private final Long a2lWpDefnVersId;

  private A2lVariantGroup a2lVarGroup;

  private boolean respWpFunc;

  private boolean respWp;
  private boolean respFunc;

  /**
   * @param pidcA2l Pidc A2l object
   * @param a2lWpDefnVersId selected wp
   * @param varGrpId selected variant id
   * @param parentShell Shell
   */
  public A2lExportWithWpDialog(final PidcA2l pidcA2l, final Long a2lWpDefnVersId, final Long varGrpId,
      final Shell parentShell) {
    super(parentShell);
    this.pidcA2l = pidcA2l;
    this.varGrpId = varGrpId;
    this.a2lWpDefnVersId = a2lWpDefnVersId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Export A2L File with Work Packages");
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.exportA2lBtn = createButton(parent, IDialogConstants.OK_ID, "Export", true);
    this.exportA2lBtn.setEnabled(validateInputFields());
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
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
    setTitle("Export A2L File with Work Packages");
    // Set the description
    setMessage("Export A2L File " + this.pidcA2l.getName() + " with Work Package information",
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    // create composite
    createComposite();
    parent.layout(true, true);
    return this.top;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER | SWT.TITLE | SWT.RESIZE | SWT.MAX);
  }


  /**
   *
   */
  private void createComposite() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.makeColumnsEqualWidth = true;
    this.a2lComposite = getFormToolkit().createComposite(this.top);
    this.a2lComposite.setLayout(gridLayout);
    createSection();
    this.a2lComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }


  /**
   *
   */
  private void createSection() {
    this.a2lSection =
        getFormToolkit().createSection(this.a2lComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.a2lSection.setExpanded(true);
    this.a2lSection.setText("Input Data to export A2L File");
    this.a2lSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.a2lSection.getDescriptionControl().setEnabled(false);
    createForm();
    this.a2lSection.setClient(this.a2lForm);
  }


  /**
   *
   */
  private void createForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    this.a2lForm = getFormToolkit().createForm(this.a2lSection);
    this.a2lForm.getBody().setLayout(gridLayout);
    final GridData txtGrid = GridDataUtil.getInstance().getTextGridData();
    txtGrid.widthHint = 480;

    // Combo boxes
    createVarVersCombo();

    // Add Destination path field
    createDestinationPathField(txtGrid);

    // Add file name field
    createFileNameField(txtGrid);

    // Add file name field
    createRespWp();
    // Add file name field
    createRespFunc();

    // Add file name field
    createRespwpFunc();


  }


  private void createFileNameField(final GridData txtGrid) {
    getFormToolkit().createLabel(this.a2lForm.getBody(), "New File Name : ");
    this.fileNameTxt = getFormToolkit().createText(this.a2lForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.fileNameTxt.setLayoutData(txtGrid);
    this.newFileName = formatName(this.pidcA2l.getName());
    if (this.newFileName == null) {
      CDMLogger.getInstance().errorDialog("The A2L file is not valid.The File Extension is not in correct format",
          Activator.PLUGIN_ID);

      return;
    }
    getFormToolkit().createLabel(this.a2lForm.getBody(), "");

    if (!hasActiveVersion()) {
      this.fileNameTxt.setText(this.newFileName + A2lExportWithWpDialog.GROUPS + "WorkingSet" +
          FileNameUtil.formatFileName(A2lExportWithWpDialog.DEFAULT, ApicConstants.SPL_CHAR_PTRN) +
          A2lExportWithWpDialog.A2L_EXT);
    }


    this.fileNameTxt.addModifyListener(event -> enableDisableOKBtn());
  }

  private void createRespWp() {
    getFormToolkit().createLabel(this.a2lForm.getBody(), "Add RespWp-Group");
    Button addRespwp = new Button(this.a2lForm.getBody(), SWT.CHECK);
    addRespwp.setText("(Adds Extra A2L-Group RespWp)");
    getFormToolkit().createLabel(this.a2lForm.getBody(), "");

    addRespwp.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        A2lExportWithWpDialog.this.respWp = addRespwp.getSelection();
      }

    });


  }

  private void createRespFunc() {
    getFormToolkit().createLabel(this.a2lForm.getBody(), "Add RespFunc-Group : ");


    Button addRespFunc = new Button(this.a2lForm.getBody(), SWT.CHECK);
    addRespFunc.setText("(Adds Extra A2L-Group RespFunc)");
    getFormToolkit().createLabel(this.a2lForm.getBody(), "");

    addRespFunc.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        A2lExportWithWpDialog.this.respFunc = addRespFunc.getSelection();
      }

    });

  }


  private void createRespwpFunc() {
    getFormToolkit().createLabel(this.a2lForm.getBody(), "Add RespWpFunc-Group : ");


    Button addRespwpFunc = new Button(this.a2lForm.getBody(), SWT.CHECK);
    addRespwpFunc.setText("(Adds Extra A2L-Group RespWpFunc)");
    getFormToolkit().createLabel(this.a2lForm.getBody(), "");

    addRespwpFunc.addSelectionListener(new SelectionAdapter() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent e) {
        A2lExportWithWpDialog.this.respWpFunc = addRespwpFunc.getSelection();
      }

    });

  }


  private void createDestinationPathField(final GridData txtGrid) {
    getFormToolkit().createLabel(this.a2lForm.getBody(), "Destination Path : ");
    this.a2lPathTxt =
        getFormToolkit().createText(this.a2lForm.getBody(), null, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
    this.a2lPathTxt.setLayoutData(txtGrid);
    this.a2lPathTxt.setEditable(false);
    this.filePath = CommonUtils.getUserDirPath();
    this.a2lPathTxt.setText(this.filePath);
    this.a2lPathTxt.addModifyListener(event -> enableDisableOKBtn());

    Button browseA2lBtn = new Button(this.a2lForm.getBody(), SWT.PUSH);
    browseA2lBtn.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));
    browseA2lBtn.setEnabled(true);
    browseA2lBtn.setToolTipText("Select export filepath");
    browseA2lBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
        dialog.setFilterPath(CommonUtils.getUserDirPath());
        A2lExportWithWpDialog.this.filePath = dialog.open();
        A2lExportWithWpDialog.this.a2lPathTxt.setText(A2lExportWithWpDialog.this.filePath);
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // NA
      }
    });
  }

  private Map<Long, A2lWpDefnVersion> doGetWpDefnVersionMap() {
    if (this.a2lWpDefnVersionMap == null) {
      try {
        this.a2lWpDefnVersionMap =
            new A2lWpDefinitionVersionServiceClient().getWPDefnVersForPidcA2l(this.pidcA2l.getId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return this.a2lWpDefnVersionMap;
  }


  /**
   * @param name
   * @return
   */
  private String formatName(final String name) {
    String[] a2lFileExtensions = { ".A2l", ".A2L", ".a2l", ".a2L" };

    for (String fileExtension : a2lFileExtensions) {
      int index = name.lastIndexOf(fileExtension);
      if (index > 0) {
        this.newFileName = name.substring(0, index);
      }
    }
    return this.newFileName;
  }


  /**
   * @param a2lWpDefnVersionMap
   * @return
   */
  private boolean hasActiveVersion() {
    Map<Long, A2lWpDefnVersion> defVerMap = doGetWpDefnVersionMap();
    boolean hasActiveVersion = false;
    if (defVerMap != null) {
      if (null == this.a2lWpDefnVersId) {
        hasActiveVersion = getActiveVersFromDefnVersMap(defVerMap);
      }
      else {
        setFileNameForActiveVersion(defVerMap.get(this.a2lWpDefnVersId));
        hasActiveVersion = true;
      }
    }
    return hasActiveVersion;
  }


  /**
   * @param defVerMap
   */
  private boolean getActiveVersFromDefnVersMap(final Map<Long, A2lWpDefnVersion> defVerMap) {
    for (A2lWpDefnVersion a2lWpDefnVersion : defVerMap.values()) {
      if (a2lWpDefnVersion.isActive()) {
        setFileNameForActiveVersion(a2lWpDefnVersion);
        return true;
      }
    }
    return false;
  }


  /**
   * @param a2lWpDefnVersion
   */
  private void setFileNameForActiveVersion(final A2lWpDefnVersion a2lWpDefnVersion) {
    if (null == this.a2lWpDefnVersId) {
      this.fileNameTxt
          .setText(this.newFileName + A2lExportWithWpDialog.GROUPS + "_" + VERS + a2lWpDefnVersion.getVersionNumber() +
              "_" + FileNameUtil.formatFileName(DEFAULT, ApicConstants.SPL_CHAR_PTRN) + A2L_EXT);
    }
    else {
      String wpDefnVersName = a2lWpDefnVersion.isWorkingSet() ? ApicConstants.WORKING_SET_NAME
          : (VERS + a2lWpDefnVersion.getVersionNumber().toString());
      String varGrpName = null == this.varGrpId ? FileNameUtil.formatFileName(DEFAULT, ApicConstants.SPL_CHAR_PTRN)
          : FileNameUtil.formatFileName(this.a2lVarGroup.getName(), ApicConstants.SPL_CHAR_PTRN).replaceAll("\\s", "");
      this.fileNameTxt
          .setText(this.newFileName + A2lExportWithWpDialog.GROUPS + "_" + wpDefnVersName + "_" + varGrpName + A2L_EXT);
    }
  }


  /**
   *
   */
  protected void enableDisableOKBtn() {
    this.exportA2lBtn.setEnabled(validateInputFields());
  }


  /**
   * @return
   */
  private boolean validateInputFields() {
    return (!"".equals(this.a2lPathTxt.getText().trim()) && !"".equals(this.fileNameTxt.getText().trim()) &&
        (this.wpDefnVersComboViewer.getCombo().getSelectionIndex() != -1) &&
        (this.variantGrpComboViewer.getCombo().getSelectionIndex() != -1));
  }


  private void createVarVersCombo() {

    Composite formBody = this.a2lForm.getBody();

    getFormToolkit().createLabel(formBody, "WP Definition Version : ");
    this.wpDefnVersComboViewer = new ComboViewer(formBody, SWT.READ_ONLY);
    GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    this.wpDefnVersComboViewer.getCombo().setLayoutData(gridData1);

    getFormToolkit().createLabel(this.a2lForm.getBody(), "");

    getFormToolkit().createLabel(formBody, "Variant Group : ");
    this.variantGrpComboViewer = new ComboViewer(formBody, SWT.READ_ONLY);
    GridData gridData2 = new GridData();
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.grabExcessHorizontalSpace = true;
    this.variantGrpComboViewer.getCombo().setLayoutData(gridData2);
    getFormToolkit().createLabel(this.a2lForm.getBody(), "");

    fillWPDefnVersionCombo();
  }

  /**
   *
   */
  private void addPidcLevelA2lVariantGroup() {
    A2lVariantGroup vg = new A2lVariantGroup();
    vg.setId(null);
    vg.setName(DEFAULT);
    this.variantGrpComboViewer.getCombo().add(vg.getName(), 0);
    this.variantGrpComboViewer.getCombo().setData(Integer.toString(0), vg);
    if (null == this.varGrpId) {
      this.variantGrpComboViewer.getCombo().select(0);
    }
  }


  /**
   *
   */
  private void fillWPDefnVersionCombo() {
    this.wpDefnVersComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.wpDefnVersComboViewer.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        String wpDefnVers = "";
        if (element instanceof A2lWpDefnVersion) {
          A2lWpDefnVersion a2lWpDefinitionVersion = (A2lWpDefnVersion) element;
          if (a2lWpDefinitionVersion.isActive()) {
            wpDefnVers = "(Active) ";
          }
          wpDefnVers += a2lWpDefinitionVersion.getName();
        }
        return wpDefnVers;
      }

    });
    Map<Long, A2lWpDefnVersion> doGetWpDefnVersionMap = doGetWpDefnVersionMap();
    A2lWpDefVersionSorter sorter = new A2lWpDefVersionSorter();
    List<A2lWpDefnVersion> a2lwpDefVerList = new ArrayList<>();
    if (doGetWpDefnVersionMap != null) {
      a2lwpDefVerList.addAll(doGetWpDefnVersionMap.values());
      Collections.sort(a2lwpDefVerList, sorter);
      this.wpDefnVersComboViewer.setInput(a2lwpDefVerList);
    }

    if (null == this.a2lWpDefnVersId) {
      for (A2lWpDefnVersion a2lWpDefnVersion : a2lwpDefVerList) {
        if (a2lWpDefnVersion.isActive()) {
          setDefnversSelection(a2lWpDefnVersion);
          break;
        }
      }
    }
    else {
      A2lWpDefnVersion a2lWpDefnVersion = this.a2lWpDefnVersionMap.get(this.a2lWpDefnVersId);
      if (null != a2lWpDefnVersion) {
        setDefnversSelection(a2lWpDefnVersion);
      }
    }
    addWpDefnVersSelectionListener();
  }


  /**
   * @param a2lWpDefnVersion
   */
  private void setDefnversSelection(final A2lWpDefnVersion a2lWpDefnVersion) {
    final ISelection selection = new StructuredSelection(a2lWpDefnVersion);
    this.wpDefnVersComboViewer.setSelection(selection);
    fillVarGrpCombo(a2lWpDefnVersion);
  }


  /**
   * @param a2lWpDefnVersion
   */
  private void fillVarGrpCombo(final A2lWpDefnVersion a2lWpDefnVersion) {
    this.variantGrpComboViewer.getCombo().removeAll();
    addPidcLevelA2lVariantGroup();
    try {
      Map<Long, A2lVariantGroup> a2lVariantGroupMap =
          new A2lVariantGroupServiceClient().getVarGrpForWpDefVer(a2lWpDefnVersion.getId());

      if (a2lVariantGroupMap != null) {
        setValuesToVariantGrpComboViewer(a2lVariantGroupMap);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    addVarGrpSelectionListener();
  }


  /**
   * @param a2lVariantGroupMap
   * @param index
   */
  private void setValuesToVariantGrpComboViewer(final Map<Long, A2lVariantGroup> a2lVariantGroupMap) {
    int index = 1;
    for (Entry<Long, A2lVariantGroup> varGrpMap : a2lVariantGroupMap.entrySet()) {
      this.variantGrpComboViewer.getCombo().add(varGrpMap.getValue().getName(), index);
      this.variantGrpComboViewer.getCombo().setData(Integer.toString(index), varGrpMap.getValue());
      if ((null != this.varGrpId) && (varGrpMap.getKey().equals(this.varGrpId))) {
        this.variantGrpComboViewer.getCombo().select(index);
        this.a2lVarGroup = varGrpMap.getValue();
      }
      index++;
    }
  }


  /**
   *
   */
  private void addVarGrpSelectionListener() {
    this.variantGrpComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      int selIndex = this.variantGrpComboViewer.getCombo().getSelectionIndex();
      A2lVariantGroup currentSelection =
          (A2lVariantGroup) this.variantGrpComboViewer.getCombo().getData(Integer.toString(selIndex));
      updateA2lFileName(currentSelection);

    });


  }


  /**
   * @param element
   */
  private void updateA2lFileName(final Object element) {
    if (element instanceof A2lWpDefnVersion) {
      A2lWpDefnVersion a2lWpDefnVersion = (A2lWpDefnVersion) element;
      if (a2lWpDefnVersion.isWorkingSet()) {
        this.fileNameTxt.setText(this.newFileName + GROUPS + "_" + ApicConstants.WORKING_SET_NAME + "_" +
            FileNameUtil.formatFileName(DEFAULT, ApicConstants.SPL_CHAR_PTRN) + A2L_EXT);
      }
      else {
        this.fileNameTxt.setText(this.newFileName + GROUPS + "_" + VERS + a2lWpDefnVersion.getVersionNumber() + "_" +
            FileNameUtil.formatFileName(DEFAULT, ApicConstants.SPL_CHAR_PTRN) + A2L_EXT);
      }
    }
    else if (element instanceof A2lVariantGroup) {
      A2lVariantGroup a2lVariantGroup = (A2lVariantGroup) element;
      IStructuredSelection selection = (IStructuredSelection) this.wpDefnVersComboViewer.getSelection();
      A2lWpDefnVersion selectedA2lWpDefnVersion = (A2lWpDefnVersion) selection.getFirstElement();
      if (selectedA2lWpDefnVersion.isWorkingSet()) {
        this.fileNameTxt.setText(this.newFileName + GROUPS + "_" + ApicConstants.WORKING_SET_NAME + "_" +
            FileNameUtil.formatFileName(a2lVariantGroup.getName(), ApicConstants.SPL_CHAR_PTRN).replaceAll("\\s", "") +
            A2L_EXT);
      }
      else {
        this.fileNameTxt
            .setText(this.newFileName +
                GROUPS + "_" + VERS + selectedA2lWpDefnVersion.getVersionNumber() + "_" + FileNameUtil
                    .formatFileName(a2lVariantGroup.getName(), ApicConstants.SPL_CHAR_PTRN).replaceAll("\\s", "") +
                A2L_EXT);
      }
    }
  }


  /**
   *
   */
  private void addWpDefnVersSelectionListener() {
    this.wpDefnVersComboViewer.addSelectionChangedListener(selectionChangedEvent -> {
      IStructuredSelection selection = (IStructuredSelection) selectionChangedEvent.getSelection();
      A2lWpDefnVersion currentSelection = (A2lWpDefnVersion) selection.getFirstElement();
      updateA2lFileName(currentSelection);
      fillVarGrpCombo(currentSelection);
      enableDisableOKBtn();
    });
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    boolean ret = exportWithProgressBar();
    if (ret) {
      super.okPressed();
    }
  }

  /**
  *
  */
  private boolean exportWithProgressBar() {
    A2LFileExportServiceInput a2lExportObj = getUserInput();

    ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
    String a2lPath = this.a2lPathTxt.getText();
    String fileName = this.fileNameTxt.getText();

    try {
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Exporting A2L File ...", 100);
        monitor.worked(50);
        exportA2l(a2lExportObj, a2lPath, fileName);
        monitor.worked(100);
        monitor.done();
      });

    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().error("Error in invoking thread to open progress bar for A2L Export !", e);
      Thread.currentThread().interrupt();
    }

    if (this.outputFilePath != null) {
      CDMLogger.getInstance().infoDialog("A2L File exported successfully. Path : " + this.outputFilePath,
          Activator.PLUGIN_ID);
      return true;
    }

    return false;

  }


  /**
   * @param a2lExportObj
   * @param a2lPath
   * @param fileName
   */
  private void exportA2l(final A2LFileExportServiceInput a2lExportObj, final String a2lPath, final String fileName) {
    try {
      this.outputFilePath = new A2LFileExportServiceClient().exportA2lFile(a2lExportObj, a2lPath, fileName);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog("Error in exporting the A2L File : " + e.getMessage(), e,
          Activator.PLUGIN_ID);
    }
  }

  private A2LFileExportServiceInput getUserInput() {
    A2LFileExportServiceInput a2lExportObj = new A2LFileExportServiceInput();

    IStructuredSelection wpDefSelection =
        (IStructuredSelection) A2lExportWithWpDialog.this.wpDefnVersComboViewer.getSelection();
    A2lWpDefnVersion activeA2lWpDefnVersion = (A2lWpDefnVersion) wpDefSelection.getFirstElement();

    int selIndex = this.variantGrpComboViewer.getCombo().getSelectionIndex();
    A2lVariantGroup varGrp =
        (A2lVariantGroup) this.variantGrpComboViewer.getCombo().getData(Integer.toString(selIndex));

    a2lExportObj.setWpDefVersId(activeA2lWpDefnVersion.getId());
    a2lExportObj.setVarGrpId(varGrp.getId());
    a2lExportObj.setRespFunc(this.respFunc);
    a2lExportObj.setWpResp(this.respWp);
    a2lExportObj.setWpRespFunc(this.respWpFunc);
    return a2lExportObj;
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
}

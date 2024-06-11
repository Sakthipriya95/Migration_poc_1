/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.jobs.DataAssessmentReportJob;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.DSTSelectionDialog;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.comphex.CompHexMetaData;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.VcdmDataSetServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.text.TextUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * Data assessment report input dialog
 *
 * @author ajk2cob
 */
public class DataAssessmentReportActionDialog extends AbstractDialog {

  /**
   * PIDC data
   */
  private final PidcTreeNode pidcTreeNode;
  /**
   * pidcA2l
   */

  private final PidcA2l pidcA2l;
  /**
   * PIDC A2L business object
   */
  private final PidcA2LBO pidcA2lBO;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * Composite
   */
  private Composite composite;
  /**
   * Section
   */
  private Section section;
  /**
   * Form
   */
  private Form form;
  /**
   * PIDC variants list
   */
  private TreeSet<PidcVariant> pidcVarSet;
  /**
   * Instance of selected PIDC variant
   */
  protected PidcVariant selctedPidcVariant;
  /**
   * Variant text
   */
  private Text varText;
  /**
   * File text
   */
  private Text hexFileText;
  /**
   * Dst file text
   */
  private Text dstFileText;


  private String dstSource;

  /**
   * Dst file browse button
   */
  private Button dstBrowseBtn;
  /**
   * Selected vcdm revision
   */
  private VCDMDSTRevision selRevision;
  /**
   * Instance of generate button
   */
  private Button generateBtn;
  /**
   * Browse button
   */
  private Button browseBtn;


  /**
   * Instantiates a new data assessment input dialog.
   *
   * @param pidcTreeNode PIDC data
   * @param shell the shell
   */
  public DataAssessmentReportActionDialog(final PidcTreeNode pidcTreeNode, final Shell shell) {
    super(shell);
    this.pidcTreeNode = pidcTreeNode;
    this.pidcA2l = pidcTreeNode.getPidcA2l();
    // Check if the node type is PIDC_A2L_VAR_NODE and assign the selected PIDC variant
    if (CommonUtils.isEqual(pidcTreeNode.getNodeType(), PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE)) {
      this.selctedPidcVariant = pidcTreeNode.getPidcVariant();
    }
    this.pidcA2lBO = new PidcA2LBO(this.pidcA2l.getId(), null);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Data Assessment Input Dialog");
    super.configureShell(newShell);
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

  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Data Assessment Input");

    // Set the message
    setMessage("Generate Data Assessment Report for the selected PIDC variant and HEX file",
        IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.composite = getFormToolkit().createComposite(parent);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create section
    createSection();

    return this.composite;
  }

  /**
   * create section
   */
  private void createSection() {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        "Choose inputs for the Data Assessment Report");
    this.section.setLayout(new GridLayout());
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    // set the client
    this.section.setClient(this.form);
  }

  /**
   * create the form
   */
  private void createForm() {
    this.form = getFormToolkit().createForm(this.section);
    // create the control for a2l file info
    createA2lControl();
    // create the control for variant selection
    createVariantControl();
    // create the control for HEX file name selection
    createFileNameControl();
    // create the controls for dst File DST selection
    createDSTFileControls();
    final GridLayout gridLayout = new GridLayout();
    // 3 columns for the layout
    gridLayout.numColumns = 3;
    gridLayout.verticalSpacing = 10;
    gridLayout.horizontalSpacing = 10;
    gridLayout.makeColumnsEqualWidth = false;
    // set form layout
    this.form.getBody().setLayout(gridLayout);
    // set form data
    this.form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

  }


  /**
   *
   */
  private void createDSTFileControls() {
    LabelUtil.getInstance().createLabel(this.form.getBody(), "vCDM DST");
    this.dstFileText = TextUtil.getInstance().createText(this.form.getBody(), true, "");
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    txtGridData.widthHint = 350;
    this.dstFileText.setLayoutData(txtGridData);
    this.dstFileText.setEditable(false);


    this.dstBrowseBtn = new Button(this.form.getBody(), SWT.NONE);
    this.dstBrowseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));// image for
    // browse
    // button
    this.dstFileText.setEnabled(false);
    this.dstBrowseBtn.setEnabled(false);
    // If a selected PIDC variant exists, prefill the PIDC variant field and enable the DST file field
    if (CommonUtils.isNotNull(this.selctedPidcVariant)) {
      this.varText.setText(this.selctedPidcVariant.getName());
      enableDstfileField();
    }
    // add selection listener
    this.dstBrowseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        boolean canDownloadArtifacts = false;
        try {
          canDownloadArtifacts =
              new CurrentUserBO().canDownloadArtifacts(DataAssessmentReportActionDialog.this.pidcA2l.getProjectId());
          if (canDownloadArtifacts) {
            setVcdmDST();

          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
        }
        if (!canDownloadArtifacts) {
          MessageDialogUtils.getInfoMessageDialog("Insufficient privilege",
              "Insufficient privilege to view vCDM artifacts! ");
          return;
        }
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
        // Not implemented
      }
    });


  }

  /**
   *
   */
  protected void setVcdmDST() {

    List<VCDMApplicationProject> dataSetModels;
    Long vCDMA2LFileId = DataAssessmentReportActionDialog.this.pidcA2lBO.getA2lFileBO().getVCDMA2LFileID();
    dataSetModels = getvCDMDatasets(vCDMA2LFileId);
    if (CommonUtils.isNotEmpty(dataSetModels) && (DataAssessmentReportActionDialog.this.selctedPidcVariant != null)) {

      // selected variant name
      String varName = DataAssessmentReportActionDialog.this.selctedPidcVariant.getName();
      List<VCDMApplicationProject> projHavingSameVar = dataSetModels.stream()
          .filter(dst -> CommonUtils.isNotNull(dst.getVcdmVariants().get(varName))).collect(Collectors.toList());

      if (projHavingSameVar.isEmpty()) {
        CDMLogger.getInstance().infoDialog(CommonUtils.DST_NOT_AVAILABLE, Activator.PLUGIN_ID);
      }
      else {
        DSTSelectionDialog dstSelectionDialog = new DSTSelectionDialog(Display.getDefault().getActiveShell(),
            projHavingSameVar, DataAssessmentReportActionDialog.this.pidcA2lBO.getA2LFileName());
        dstSelectionDialog.open();

        // if any dst is selected assign dst values to relevant fields and disable hex file fields else enable both dst
        // and hex file fields
        if (dstSelectionDialog.getReturnCode() == 0) {
          DataAssessmentReportActionDialog.this.selRevision = dstSelectionDialog.getVcdmdstRevision();
          String dstName = dstSelectionDialog.getSelectedTreePath()
              .substring(dstSelectionDialog.getSelectedTreePath().indexOf(CommonUtils.VCDM_DELIMITER) + 1);
          DataAssessmentReportActionDialog.this.dstSource = dstSelectionDialog.getSelectedTreePath().substring(0,
              dstSelectionDialog.getSelectedTreePath().indexOf(CommonUtils.VCDM_DELIMITER));
          DataAssessmentReportActionDialog.this.dstFileText.setText(dstName);

          disableHexFileField();
        }


        // enable generate button if all relevant fields are selected
        enableDisableGenerateBtn();
      }
    }
    else {
      CDMLogger.getInstance().infoDialog(CommonUtils.DST_NOT_AVAILABLE, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param vCDMA2LFileId Long
   * @return List<VCDMApplicationProject>
   */
  public List<VCDMApplicationProject> getvCDMDatasets(final Long vCDMA2LFileId) {
    A2LFileInfoServiceClient client = new A2LFileInfoServiceClient();
    List<VCDMApplicationProject> dataSetModels;
    try {
      dataSetModels = client.getVcdmDataSets(vCDMA2LFileId);
      return dataSetModels;
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return Collections.emptyList();
    }
  }

  /**
   * create a2l info
   */
  private void createA2lControl() {
    LabelUtil.getInstance().createLabel(this.form.getBody(), "A2L File");
    Text a2lNameText =
        getFormToolkit().createText(this.form.getBody(), this.pidcA2l.getName(), SWT.SINGLE | SWT.BORDER);
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    a2lNameText.setLayoutData(txtGridData);
    a2lNameText.setEditable(false);
    getFormToolkit().createLabel(this.form.getBody(), "");
  }


  /**
   * Create variant controls
   */
  private void createVariantControl() {
    boolean isVariantAvailable = varSelectionNeeded();
    String variantLabel = isVariantAvailable ? "PIDC Variant *" : "PIDC Variant";
    LabelUtil.getInstance().createLabel(this.form.getBody(), variantLabel);

    this.varText = getFormToolkit().createText(this.form.getBody(), "", SWT.SINGLE | SWT.BORDER);
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    this.varText.setLayoutData(txtGridData);
    this.varText.setEditable(false);

    // browse button
    Button btnVariant = new Button(this.form.getBody(), SWT.NONE);
    btnVariant.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));

    // incase of no variant, the varText and btnVariant will be disabled
    this.varText.setEnabled(false);
    btnVariant.setEnabled(false);
    if (isVariantAvailable) {
      // enable the button and the text field if the variants are available
      this.varText.setEnabled(true);
      btnVariant.setEnabled(true);
      btnVariant.setFocus();
    }

    btnVariant.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {
        // open dialog for selecting variant
        final PidcVariantSelectionDialog variantSelDialog = new PidcVariantSelectionDialog(
            Display.getCurrent().getActiveShell(), DataAssessmentReportActionDialog.this.pidcA2l.getId());
        variantSelDialog.open();
        final PidcVariant selectedVariant = variantSelDialog.getSelectedVariant();// variant selected from the dialog
        if (null != selectedVariant) {

          // check if newly selected PIDC variant is same as previous PIDC variant
          if (CommonUtils.isNotEqual(selectedVariant, DataAssessmentReportActionDialog.this.selctedPidcVariant)) {

            // check if Hex field is already selected then Dst feild will be disabled
            if (DataAssessmentReportActionDialog.this.dstFileText.isEnabled()) {
              // clear dst field and enable hex field
              DataAssessmentReportActionDialog.this.dstFileText.setText("");
              enableHexFileField();
            }
            else if (CommonUtils.isEmptyString(DataAssessmentReportActionDialog.this.hexFileText.getText())) {
              enableDstfileField();
            }
          }
          // set the variant text and update the generate btn
          DataAssessmentReportActionDialog.this.varText.setText(selectedVariant.getName());
          DataAssessmentReportActionDialog.this.selctedPidcVariant = selectedVariant;
          // enable disable buttons
          enableDisableGenerateBtn();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not implemented
      }

    });
  }

  /**
   * check if variants are available for the selected PIDC
   *
   * @return
   */
  private boolean varSelectionNeeded() {
    try {
      this.pidcVarSet =
          new TreeSet<>(new PidcVariantServiceClient().getA2lMappedVariants(this.pidcA2lBO.getPidcA2lId()).values());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return CommonUtils.isNotEmpty(this.pidcVarSet);
  }

  /**
   * Create file name controls
   */
  private void createFileNameControl() {
    LabelUtil.getInstance().createLabel(this.form.getBody(), "HEX File ");

    this.hexFileText = getFormToolkit().createText(this.form.getBody(), "", SWT.SINGLE | SWT.BORDER);
    GridData txtGridData = new GridData();
    txtGridData.grabExcessHorizontalSpace = true;
    txtGridData.horizontalAlignment = GridData.FILL;
    this.hexFileText.setLayoutData(txtGridData);
    this.hexFileText.setEditable(false);

    // Adding Drop Listener for hex file textbox
    DropFileListener hexFileDropFileListener = new DropFileListener(this.hexFileText, new String[] { "*.hex" });
    hexFileDropFileListener.addDropFileListener(false);
    hexFileDropFileListener.setEditable(true);
    this.hexFileText.addModifyListener(event -> enableDisableGenerateBtn());

    // browse button
    this.browseBtn = new Button(this.form.getBody(), SWT.NONE);
    this.browseBtn.setImage(ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON));

    if (!varSelectionNeeded()) {
      // focus the hex file browse button for no-variant case
      this.browseBtn.setFocus();
    }
    this.browseBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent selectionEvent) {

        FileDialog hexFileDialog = new FileDialog(Display.getCurrent().getActiveShell());
        hexFileDialog.setText("Choose hex file");
        hexFileDialog.setFilterExtensions(new String[] { "*.hex" });
        hexFileDialog.setFilterNames(new String[] { "HEX File(*.hex)" });
        hexFileDialog.open();
        if (CommonUtils.isNotEmptyString(hexFileDialog.getFileName())) {
          DataAssessmentReportActionDialog.this.hexFileText
              .setText(hexFileDialog.getFilterPath() + File.separator + hexFileDialog.getFileName());
          // after selecting PIDC, if Hex field is selected, then Dst field will be disabled
          disableDstfileField();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not implemented
      }

    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // create button
    this.generateBtn = createButton(parent, IDialogConstants.OK_ID, "Generate", true);
    this.generateBtn.setEnabled(false);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * enable disable generate button
   */
  private void enableDisableGenerateBtn() {
    this.generateBtn.setEnabled(validateFields());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    CompHexMetaData metaData = new CompHexMetaData();
    String hexFilePath;
    if (DataAssessmentReportActionDialog.this.dstFileText.isEnabled() &&
        (DataAssessmentReportActionDialog.this.selRevision != null)) {
      hexFilePath = fetchHexForDst();
      metaData.setHexFromVcdm(true);
      metaData.setSrcHexFilePath(hexFilePath);
      metaData.setVcdmDstSource(getFullDstSource(this.dstFileText.getText()));
      metaData.setVcdmDstVersId(DataAssessmentReportActionDialog.this.selRevision.getDstID());
    }
    else {
      hexFilePath = this.hexFileText.getText();
      metaData.setHexFromVcdm(false);
      metaData.setSrcHexFilePath(hexFilePath);
    }

    // start Data Assessment Report job
    DataAssessmentReportJob job = new DataAssessmentReportJob("Generating Data Assessment Report", this.pidcTreeNode,
        this.pidcA2lBO, this.selctedPidcVariant, metaData);
    CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    job.schedule();
    // ICDM-2608
    Job.getJobManager().resume();
    super.okPressed();
  }

  /**
   * @param text
   * @return
   */
  private String getFullDstSource(final String dstText) {
    String finalDst = "";

    if ((dstText != null) && !dstText.isEmpty()) {
      finalDst = dstText.replaceFirst(CommonUtils.VCDM_DELIMITER, ".");
      finalDst = finalDst.replaceFirst(CommonUtils.VCDM_DELIMITER, ";");
      finalDst = this.dstSource + CommonUtils.VCDM_DELIMITER + finalDst;
    }
    return finalDst;
  }

  /**
   * @return
   */
  private String fetchHexForDst() {

    VcdmDataSetServiceClient client = new VcdmDataSetServiceClient();
    try {
      // uploads the a2l file and returns version number
      return client.loadHexFile(DataAssessmentReportActionDialog.this.selRevision.getDstID(),
          CommonUtils.getICDMTmpFileDirectoryPath());
    }
    catch (Exception ex) {
      CDMLogger.getInstance().error("Error occurred while loading Hex file from vCDM", ex, Activator.PLUGIN_ID);
    }
    return "";

  }

  /**
   * validate fields for generate button
   */
  private boolean validateFields() {
    if (varSelectionNeeded() && "".equals(this.varText.getText())) {
      return false;
    }
    return !("".equals(this.hexFileText.getText()) && "".equals(this.dstFileText.getText()));
  }


  /**
   * enable and disable Hex and Dst fields
   */
  private void enableHexFileField() {
    DataAssessmentReportActionDialog.this.browseBtn.setEnabled(true);
    DataAssessmentReportActionDialog.this.hexFileText.setEnabled(true);
  }

  private void disableHexFileField() {
    DataAssessmentReportActionDialog.this.browseBtn.setEnabled(false);
    DataAssessmentReportActionDialog.this.hexFileText.setEnabled(false);
  }

  private void enableDstfileField() {
    DataAssessmentReportActionDialog.this.dstFileText.setEnabled(true);
    DataAssessmentReportActionDialog.this.dstBrowseBtn.setEnabled(true);
  }

  private void disableDstfileField() {
    DataAssessmentReportActionDialog.this.dstBrowseBtn.setEnabled(false);
    DataAssessmentReportActionDialog.this.dstFileText.setEnabled(false);
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.views.providers.HexFileSelectionRowProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantsInputData;
import com.bosch.caltool.icdm.model.cdr.CompliReviewInputMetaData;
import com.bosch.caltool.icdm.model.cdr.ExcelReportTypeEnum;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CompliReviewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author dmr1cob
 */
public class ImportCompliRvwInputDataDialog extends TitleAreaDialog {

  /**
   * Diallog title
   */
  private static final String DIALOG_TITLE = "Import Input Data";
  /**
   * mandotary field decorator
   */
  private static final String DESC_TXT_MANDATORY = "This field is mandatory.";

  /**
   * Minimum width of the dialog
   */
  private static final int DIALOG_MIN_WIDTH = 100;

  /**
   * Minimum height of the dialog
   */
  private static final int DIALOG_MIN_HEIGHT = 50;

  /**
   * Form toolkit
   */
  private FormToolkit formToolkit;

  /**
   * Execution Id text field
   */
  private Text executionId;

  /**
   * Import Input data button
   */
  private Button importBtn;

  /**
   * Import button Constant
   */
  public static final String IMPORT_BUTTON_CONSTANT = "Import";

  /**
   * Compliance Review Dialog Object
   */
  private final CompliReviewDialog compliRvwDialog;

  /**
   * Json Input Meta data
   */
  private CompliReviewInputMetaData compliRvwInputMetaData;

  /**
   * Directory path of imported input files
   */
  private String importFileDirPath;

  /**
   * Variant and Pidc version details
   */
  protected PidcVariantsInputData pidcVariantsInputData;

  /**
   * @param parentShell Shell
   * @param compliRvwDialog Compliance Review Dialog Object
   */
  public ImportCompliRvwInputDataDialog(final Shell parentShell, final CompliReviewDialog compliRvwDialog) {
    super(parentShell);
    this.compliRvwDialog = compliRvwDialog;
  }

  /**
   * Configures the shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText(DIALOG_TITLE);
    super.configureShell(newShell);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // Set title
    setTitle(DIALOG_TITLE);
    // Set the message
    setMessage("Paste the Execution ID below and click Import");

    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createDialogArea(final Composite parent) {

    final Composite composite = (Composite) super.createDialogArea(parent);
    composite.setLayout(new GridLayout());

    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.minimumWidth = DIALOG_MIN_WIDTH;
    gridData.minimumHeight = DIALOG_MIN_HEIGHT;
    composite.setLayoutData(gridData);

    createMainComposite(composite);

    return composite;
  }

  /**
   * Create the main composite and its contents
   */
  private void createMainComposite(final Composite composite) {

    final Composite mainComposite = getFormToolkit().createComposite(composite);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    mainComposite.setLayout(gridLayout);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    mainComposite.setLayoutData(gridData);
    // Create text field for Execution Id
    createExecutionIdTextField(mainComposite);
  }

  /**
   * Create Execution Id text field
   *
   * @param comp parent composite
   */
  private void createExecutionIdTextField(final Composite comp) {
    createLabelControl(comp, "Execution ID");
    this.executionId = createTextField(comp);
    this.executionId.setEnabled(true);
    this.executionId.setEditable(true);
    this.executionId.addModifyListener(e -> enableOkButton());
    this.executionId
        .setText(null == this.compliRvwDialog.getExecutionId() ? "" : this.compliRvwDialog.getExecutionId());

    ControlDecoration decorator = new ControlDecoration(this.executionId, SWT.LEFT | SWT.TOP);
    decorator.setDescriptionText(DESC_TXT_MANDATORY);
    FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED);
    decorator.setImage(fieldDecoration.getImage());
    decorator.show();
  }

  /**
   * Enable OK button only if execution id is provided
   */
  private void enableOkButton() {
    if (null != this.importBtn) {
      this.importBtn.setEnabled(CommonUtils.isNotEmptyString(this.executionId.getText()));
    }
  }

  /**
   * Creates a text field
   *
   * @param comp parent composite
   * @return Text the new field
   */
  private Text createTextField(final Composite comp) {
    final Text text = TextUtil.getInstance().createEditableText(this.formToolkit, comp, false, "");
    final GridData widthHintGridData = new GridData();
    widthHintGridData.horizontalAlignment = GridData.FILL;
    widthHintGridData.grabExcessHorizontalSpace = true;
    text.setLayoutData(widthHintGridData);
    return text;
  }

  /**
   * Creates a label
   *
   * @param compparent composite
   * @param lblName label text
   */
  private void createLabelControl(final Composite comp, final String lblName) {
    final GridData gridData = new GridData();
    gridData.verticalAlignment = SWT.TOP;
    LabelUtil.getInstance().createLabel(this.formToolkit, comp, lblName);

  }

  /**
   * @return FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Allow resizing
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.importBtn = createButton(parent, IDialogConstants.OK_ID, IMPORT_BUTTON_CONSTANT, false);
    this.importBtn.setEnabled(CommonUtils.isNotEmptyString(this.executionId.getText()));
    createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    String execId = this.executionId.getText();
    Text a2lNameText = ImportCompliRvwInputDataDialog.this.compliRvwDialog.getA2lNameText();
    Text pverNameText = ImportCompliRvwInputDataDialog.this.compliRvwDialog.getPverNameText();
    Text pverVariantText = ImportCompliRvwInputDataDialog.this.compliRvwDialog.getPverVariantText();
    Text pverRevisionText = ImportCompliRvwInputDataDialog.this.compliRvwDialog.getPverRevisionText();
    Text webFlowIdText = ImportCompliRvwInputDataDialog.this.compliRvwDialog.getWebFlowIdText();
    Text pidcVersionText = ImportCompliRvwInputDataDialog.this.compliRvwDialog.getPidcVersionText();
    List<HexFileSelectionRowProvider> hexFileRowProviderList =
        ImportCompliRvwInputDataDialog.this.compliRvwDialog.getHexFileRowProviderList();
    try {
      dialog.run(true, true, monitor -> {

        monitor.beginTask("Importing input data...", 100);
        monitor.setTaskName("Import compli review data");
        monitor.worked(10);
        CompliReviewServiceClient compliRvwServiceClient = new CompliReviewServiceClient();
        PidcVariantServiceClient pidcVariantServiceClient = new PidcVariantServiceClient();
        try {
          ImportCompliRvwInputDataDialog.this.importFileDirPath =
              compliRvwServiceClient.importCompliReviewInputData(execId);
          monitor.worked(20);
          ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData =
              compliRvwServiceClient.readJsonMetaData(ImportCompliRvwInputDataDialog.this.importFileDirPath +
                  File.separator + ApicConstants.COMPLI_INPUT_METADATA_JSON_FILE_NAME);
          monitor.worked(50);
          Map<Long, Long> hexFilePidcElement =
              ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getHexFilePidcElement();
          Collection<Long> elementIds = hexFilePidcElement.values();
          ImportCompliRvwInputDataDialog.this.pidcVariantsInputData =
              pidcVariantServiceClient.getPidcVariantsInputData(new ArrayList<>(elementIds));
        }
        catch (ApicWebServiceException | IOException e) {
          CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }
        monitor.worked(100);
        monitor.done();
      });
      if (null != ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData) {
        // Set A2l File Name
        String a2lFilePath = ImportCompliRvwInputDataDialog.this.importFileDirPath + File.separator +
            ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getA2lFileName();
        a2lNameText.setText(a2lFilePath);
        this.compliRvwDialog.setA2lFilePath(a2lFilePath);

        ImportCompliRvwInputDataDialog.this.compliRvwDialog.getPredecessorCheckbox()
            .setSelection(ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.isPredecessorCheck());

        ExcelReportTypeEnum dataFileOption =
            ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getDatafileoption();
        ImportCompliRvwInputDataDialog.this.compliRvwDialog.getOneFilePerCheckRadio().setSelection(false);
        ImportCompliRvwInputDataDialog.this.compliRvwDialog.getSingleFileWithSumRadio().setSelection(false);
        ImportCompliRvwInputDataDialog.this.compliRvwDialog.getSingleFileWithRedSumRadio().setSelection(false);
        // Null check is added to handle exisiting Data
        if (CommonUtils.isEqual(ExcelReportTypeEnum.ONEFILECHECK, dataFileOption) ||
            CommonUtils.isNull(dataFileOption)) {
          ImportCompliRvwInputDataDialog.this.compliRvwDialog.getOneFilePerCheckRadio().setSelection(true);
        }
        else if (CommonUtils.isEqual(ExcelReportTypeEnum.SINGLEFILEWITHSUMMARY, dataFileOption)) {
          ImportCompliRvwInputDataDialog.this.compliRvwDialog.getSingleFileWithSumRadio().setSelection(true);
        }
        else if (CommonUtils.isEqual(ExcelReportTypeEnum.SINGLEFILEWITHREDUCTIONSUMMARY, dataFileOption)) {
          ImportCompliRvwInputDataDialog.this.compliRvwDialog.getSingleFileWithRedSumRadio().setSelection(true);
        }

        // Set PVER Name
        pverNameText.setText(ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getPverName());

        // Set Pver Variant
        pverVariantText.setText(ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getPverVariant());

        // Set Pver revision
        pverRevisionText.setText(ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getPverRevision());

        // Set Webflow Job Id
        webFlowIdText.setText(ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getWebflowID());

        if (null != this.pidcVariantsInputData.getPidcVersion()) {
          pidcVersionText.setText(this.pidcVariantsInputData.getPidcVersion().getName());
        }

        Map<Long, String> hexfileIdxMap = ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getHexfileIdxMap();
        Map<Long, Long> hexFilePidcElement =
            ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getHexFilePidcElement();
        ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getHexfileIdxMap();

        List<HexFileSelectionRowProvider> hexFileSelectionRowProviderList = new ArrayList<>();
        if (this.pidcVariantsInputData.isVariantAvailable()) {
          if (CommonUtils.isNotEmpty(this.pidcVariantsInputData.getPidcVariantMap())) {
            for (Entry<Long, Long> hexFilePidcElementEntry : hexFilePidcElement.entrySet()) {
              String hexFilePath =
                  this.importFileDirPath + File.separator + hexfileIdxMap.get(hexFilePidcElementEntry.getKey());
              PidcVariant pidcVariant =
                  this.pidcVariantsInputData.getPidcVariantMap().get(hexFilePidcElementEntry.getValue());
              HexFileSelectionRowProvider hexFileSelectionRowProvider =
                  new HexFileSelectionRowProvider(hexFilePath, hexfileIdxMap.get(hexFilePidcElementEntry.getKey()),
                      this.pidcVariantsInputData.getPidcVersion().getName(),
                      this.pidcVariantsInputData.getPidcVersion().getId(), pidcVariant.getName(), pidcVariant.getId());
              hexFileSelectionRowProvider.setIndex(hexFilePidcElementEntry.getKey().intValue());
              hexFileSelectionRowProviderList.add(hexFileSelectionRowProvider);
            }
          }
        }
        else if (null != this.pidcVariantsInputData.getPidcVersion()) {
          for (Entry<Long, Long> hexFilePidcElementEntry : hexFilePidcElement.entrySet()) {
            String hexFilePath =
                this.importFileDirPath + File.separator + hexfileIdxMap.get(hexFilePidcElementEntry.getKey());
            HexFileSelectionRowProvider hexFileSelectionRowProvider =
                new HexFileSelectionRowProvider(hexFilePath, hexfileIdxMap.get(hexFilePidcElementEntry.getKey()),
                    this.pidcVariantsInputData.getPidcVersion().getName(),
                    this.pidcVariantsInputData.getPidcVersion().getId(), null, null);
            hexFileSelectionRowProvider.setIndex(hexFilePidcElementEntry.getKey().intValue());
            hexFileSelectionRowProviderList.add(hexFileSelectionRowProvider);
          }
        }
        hexFileRowProviderList.clear();
        hexFileRowProviderList.addAll(hexFileSelectionRowProviderList);
        this.compliRvwDialog.getTabViewer().setInput(hexFileRowProviderList);
        this.compliRvwDialog.getTabViewer().refresh();
        CompliReviewServiceClient compliRvwServiceClient = new CompliReviewServiceClient();
        // Enabling execute button based on type of pver(SDOM or NON SDOM)
        boolean isNONSDOM = compliRvwServiceClient
            .checkIsNONSDOM(ImportCompliRvwInputDataDialog.this.compliRvwInputMetaData.getPverName());
        this.compliRvwDialog.getGenerateBtn().setEnabled(this.compliRvwDialog.validateFields(isNONSDOM));
        super.close();
      }
    }
    catch (InvocationTargetException | InterruptedException | ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }
  }
}

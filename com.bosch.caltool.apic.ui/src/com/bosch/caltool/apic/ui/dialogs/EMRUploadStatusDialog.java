/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.wizards.EMRResultComposite;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.emr.EMRFileUploadResponse;
import com.bosch.caltool.icdm.model.emr.EmrFile;
import com.bosch.caltool.icdm.model.emr.EmrFileMapping;
import com.bosch.caltool.icdm.model.emr.EmrUploadError;
import com.bosch.caltool.icdm.ws.rest.client.apic.emr.EmrFileServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author mkl2cob
 */
public class EMRUploadStatusDialog extends AbstractDialog {

  /**
   * top composite
   */
  private Composite top;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * EMRResultComposite
   */
  private EMRResultComposite codexVarAssgnComp;
  /**
   * EmrFileMapping
   */
  private final EmrFileMapping selectedObj;

  /**
   * Constructor
   *
   * @param parentShell Shell
   * @param selectedObj EmrFileMapping
   */
  public EMRUploadStatusDialog(final Shell parentShell, final EmrFileMapping selectedObj) {
    super(parentShell);
    this.selectedObj = selectedObj;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set title
    setTitle("Upload Protocol");
    // Set the message
    setMessage("Result of Upload");
    return contents;
  }

  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // Set shell name
    newShell.setText("Check Upload Protocol");
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
    initializeDialogUnits(parent);
    this.top = (Composite) super.createDialogArea(parent);
    final GridLayout layout = new GridLayout();
    this.top.setLayout(layout);

    try {
      Map<Long, List<EmrUploadError>> uploadErrorsMap = new HashMap<Long, List<EmrUploadError>>();
      List<EmrUploadError> emrUploadErrors = getEMRUploadErrors();
      if (CommonUtils.isNotEmpty(emrUploadErrors)) {
        // put errors into the map
        Long emrFileId = this.selectedObj.getEmrFile().getId();
        uploadErrorsMap.put(emrFileId, emrUploadErrors);
        // put file into file map
        Map<Long, EmrFile> emrFileMap = new HashMap<>();
        emrFileMap.put(emrFileId, this.selectedObj.getEmrFile());
        // create EMRFileUploadResponse object
        EMRFileUploadResponse uploadResponse = new EMRFileUploadResponse();
        uploadResponse.setEmrFileErrorMap(uploadErrorsMap);
        uploadResponse.setEmrFileMap(emrFileMap);
        this.codexVarAssgnComp = new EMRResultComposite(false, true, emrFileId);
        this.codexVarAssgnComp.createComposite(this.top, getFormToolkit());
        this.codexVarAssgnComp.setErrorsInput(uploadResponse);
      }
      else {
        this.codexVarAssgnComp = new EMRResultComposite(true, true, null);
        this.codexVarAssgnComp.createComposite(this.top, getFormToolkit());
      }

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    this.top.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.top.layout(true, true);

    return this.top;
  }

  /**
   * @return
   * @throws ApicWebServiceException
   */
  private List<EmrUploadError> getEMRUploadErrors() throws ApicWebServiceException {
    EmrFileServiceClient client = new EmrFileServiceClient();
    List<EmrUploadError> uploadProtocolErrors = client.getUploadProtocol(this.selectedObj.getEmrFile().getId());
    return uploadProtocolErrors;
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
  protected void createButtonsForButtonBar(final Composite parent) {
    // create ok button alone for button bar
    createButton(parent, IDialogConstants.OK_ID, "OK", true);
  }

  /**
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {
    close();
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentBaselineEditor;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditorInput;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DaDataAssessmentServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * Action to view Data Assessment Baselines
 *
 * @author msp5cob
 */
public class DataAssessmentBaselineAction extends Action {

  /**
   *
   */
  private static final String DATA_ASSESSMENT_BASELINES = "Data Assessment Baselines";
  /**
   * PIDC data
   */
  private final PidcTreeNode pidcTreeNode;
  /**
   * A2LFile
   */
  private final PidcA2l pidcA2l;

  /**
   * @param pidcTreeNode PIDC data
   */
  public DataAssessmentBaselineAction(final PidcTreeNode pidcTreeNode) {
    super();
    this.pidcTreeNode = pidcTreeNode;
    this.pidcA2l = pidcTreeNode.getPidcA2l();
    setProperties();
  }

  /**
   * set image, text etc
   */
  private void setProperties() {
    setText(DATA_ASSESSMENT_BASELINES);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.LIST_FILE_28X30);
    setImageDescriptor(imageDesc);
    setEnabled(true);
    if (CommonUtils.isNotNull(this.pidcA2l) && !this.pidcA2l.isActive()) {
      setEnabled(false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    if (userHasAccess(this.pidcTreeNode.getPidc())) {

      Set<DaDataAssessment> baselinesForPidcA2l = null;
      long pidcA2lId = this.pidcA2l.getId();
      try {
        baselinesForPidcA2l = new DaDataAssessmentServiceClient().getBaselinesForPidcA2l(pidcA2lId);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

      if ((baselinesForPidcA2l == null) || baselinesForPidcA2l.isEmpty()) {
        MessageDialogUtils.getInfoMessageDialog(DATA_ASSESSMENT_BASELINES,
            "No DataAssessment Baselines found for A2L File. To View Baselines, Generate Data Assessment Report and Create Baselines.");
      }
      else {
        DataAssessmentReport dataAssessmentReport = new DataAssessmentReport();

        dataAssessmentReport.setDataAssmntBaselines(baselinesForPidcA2l);
        dataAssessmentReport.setPidcA2lId(pidcA2lId);
        dataAssessmentReport.setPidcVersId(this.pidcA2l.getPidcVersId());
        
        DataAssessmentReportEditorInput editorInput = new DataAssessmentReportEditorInput();
        editorInput.setPidcName(this.pidcTreeNode.getPidc().getName());
        editorInput.setA2lFileName(this.pidcA2l.getName());


        DataAssmntReportDataHandler dataAssmntReportDataHandler =
            new DataAssmntReportDataHandler(dataAssessmentReport, null, null, null);
        editorInput.setDataAssmntReportDataHandler(dataAssmntReportDataHandler);

        try {
          IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .openEditor(editorInput, DataAssessmentBaselineEditor.EDITOR_ID);

          DataAssessmentBaselineEditor dataAssessmentBaselineEditor = (DataAssessmentBaselineEditor) openEditor;
          // set focus to the editor opened
          dataAssessmentBaselineEditor.setFocus();

        }
        catch (PartInitException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }

    }
    else {
      MessageDialogUtils.getInfoMessageDialog(DATA_ASSESSMENT_BASELINES,
          "You must have PIDC Write or Owner access to view Data Assessment Baselines.");
    }
  }

  /**
   * Method to check if user is PIDC owner
   *
   * @param pidc
   */
  private boolean userHasAccess(final Pidc pidc) {
    boolean userHasAccess = false;
    CurrentUserBO currentUserBO = new CurrentUserBO();
    try {
      userHasAccess = currentUserBO.hasNodeOwnerAccess(pidc.getId()) || currentUserBO.hasNodeWriteAccess(pidc.getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return userHasAccess;
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.cdr.ui.dialogs.DataAssessmentReportActionDialog;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * Action to generate Data Assessment Report
 *
 * @author ajk2cob
 */
public class DataAssessmentReportAction extends Action {

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
  public DataAssessmentReportAction(final PidcTreeNode pidcTreeNode) {
    super();
    this.pidcTreeNode = pidcTreeNode;
    this.pidcA2l = pidcTreeNode.getPidcA2l();
    setProperties();
  }

  /**
   * set image, text etc
   */
  private void setProperties() {
    setText("Data Assessment Report");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.DATA_ASSESSMENT_16X16);
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
    if (isUserPidcOwner(this.pidcTreeNode.getPidc())) {
      DataAssessmentReportActionDialog dialog =
          new DataAssessmentReportActionDialog(this.pidcTreeNode, Display.getCurrent().getActiveShell());
      dialog.open();
    }
    else {
      MessageDialogUtils.getInfoMessageDialog("Data Assessment Report",
          "You must be a PIDC Owner to generate the Data Assessment Report.");
    }
  }

  /**
   * Method to check if user is PIDC owner
   *
   * @param pidc
   */
  private boolean isUserPidcOwner(final Pidc pidc) {
    boolean isPidcOwner = false;
    try {
      isPidcOwner = new CurrentUserBO().hasNodeOwnerAccess(pidc.getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return isPidcOwner;
  }

}

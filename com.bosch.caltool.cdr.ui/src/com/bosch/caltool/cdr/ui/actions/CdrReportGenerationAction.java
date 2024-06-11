/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.wizards.CdrReportGenerationWizard;
import com.bosch.caltool.cdr.ui.wizards.CdrReportGenerationWizardDialog;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author mkl2cob
 */
public class CdrReportGenerationAction extends Action {

  /**
   * A2LFile instance
   */
  private final PidcA2l pidcA2l;
  /**
   * A2L Resp -will not be null if the action is called to create data review report for resp/WP node
   */
  private final A2lResponsibility a2lResp;
  /**
   * WP Resp -will not be null if the action is called to create data review report for WPnode
   */
  private final A2lWorkPackage a2lWp;
  /**
   * PIDC Variant
   */
  private final PidcVariant pidcVariant;


  /**
   * Constructor
   *
   * @param pidcA2l                   A2LFile
   * @param pidcVariant               pidcVar
   * @param a2lResponsibility         A2L Resp Id
   * @param a2lWorkPackage            A2L WP Id
   * @param isRprtGenFrmA2LStructView isRprtGenFrmA2LStructView
   */
  public CdrReportGenerationAction(final PidcA2l pidcA2l, final PidcVariant pidcVariant,
      final A2lResponsibility a2lResponsibility, final A2lWorkPackage a2lWorkPackage) {
    super();
    this.pidcA2l = pidcA2l;
    this.pidcVariant = pidcVariant;
    this.a2lResp = a2lResponsibility;
    this.a2lWp = a2lWorkPackage;
    setProperties();
  }

  /**
   * set properties for action
   */
  private void setProperties() {
    setText("Generate Data Review Report");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.CDR_PROJECT_REPORT_16X16);
    setImageDescriptor(imageDesc);
    // this action is enabled for all users right now
    setEnabled(true);
    if ((CommonUtils.isNotNull(this.pidcA2l) && !this.pidcA2l.isActive()) ||
        (CommonUtils.isNotNull(this.pidcVariant) && this.pidcVariant.isDeleted())) {
      setEnabled(false);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    try {
      // get the PidcVersion instance
      PidcVersion pidcVers = new PidcVersionServiceClient().getById(this.pidcA2l.getPidcVersId());
      // create wizard instance
      CdrReportGenerationWizard wizard =
          new CdrReportGenerationWizard(this.pidcA2l, pidcVers, this.pidcVariant, this.a2lResp, this.a2lWp);
      // create wizard dialog instance
      CdrReportGenerationWizardDialog rvwReportDialog =
          new CdrReportGenerationWizardDialog(Display.getCurrent().getActiveShell(), wizard);
      rvwReportDialog.setMinimumPageSize(250, 250);
      rvwReportDialog.create();
      // open report dialog
      rvwReportDialog.open();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import org.eclipse.jface.action.Action;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.SSDSoftwareVersionDialog;
import com.bosch.caltool.apic.ui.editors.pages.A2LFilePage;
import com.bosch.caltool.icdm.client.bo.apic.ApicDataBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob new class for set ssd software version to Pidc a2l.
 */
public class SetSSdVersionAction extends Action {

  private final PidcVersionBO pidcVersionBO;

  /**
   * @param a2lFilePage a2lFilePage
   */
  public SetSSdVersionAction(final A2LFilePage a2lFilePage) {
    super();
    this.a2lFilePage = a2lFilePage;
    this.pidcVersionBO = a2lFilePage.getHandler();
  }


  /**
   * a2l file page instance
   */
  private final A2LFilePage a2lFilePage;


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    try {

      // If no write access return.
      if (!this.a2lFilePage.validateWriteAccess()) {
        return;
      }
      // Show the unlock dialog
      this.a2lFilePage.showUnlockPidcDialog();
      ApicDataBO apicBo = new ApicDataBO();
      // If pidc version is unlocked
      if (apicBo.isPidcUnlockedInSession(this.a2lFilePage.getPidcVersion())) {

        AttributeValue attributeValue;
        attributeValue =
            new AttributeValueServiceClient().getById(this.pidcVersionBO.getSSDProjNodeAttr().getValueId());
        if (isMappingAllowed(attributeValue)) {
          // software version dialog for mapping
          final SSDSoftwareVersionDialog detDialog = new SSDSoftwareVersionDialog(
              this.a2lFilePage.getA2lFileMappingTab().getA2lTabViewer().getControl().getShell(), this.a2lFilePage,
              attributeValue);
          detDialog.open();
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }


  /**
   * @param attributeValue
   */
  private boolean isMappingAllowed(final AttributeValue attributeValue) {
    // If the method returns false then do not allow the mapping.
    // Check ssd proj node attr value
    if ((attributeValue == null) || ((attributeValue.getNumValue()) == null)) {
      CDMLogger.getInstance().errorDialog("Please set the Software project attribute value", Activator.PLUGIN_ID);
      return false;
    }
    // check for a2l mapping to pidc version.
    for (PidcA2lFileExt pidcA2lFileExt : this.a2lFilePage.getSelA2LFile()) {
      if (a2lMappingAvailable(pidcA2lFileExt)) {
        CDMLogger.getInstance().errorDialog(
            "A2l files are not mapped to the PIDC version." + "Please map them and assign SSD Software version.",
            Activator.PLUGIN_ID);
        return false;
      }
      // If the pidc a2l is already assigned to a Different SSD project and version.
      else if (isA2lAlreadyAssignToAnotherSSDProj(attributeValue, pidcA2lFileExt)) {
        CDMLogger.getInstance().errorDialog("The A2l file is already mapped to a different SSD project and version",
            Activator.PLUGIN_ID);
        return false;
      }

    }
    // default true
    return true;
  }


  /**
   * @param pidcA2lFileExt A2LFile
   * @return boolean
   */
  private boolean a2lMappingAvailable(final PidcA2lFileExt pidcA2lFileExt) {
    return (pidcA2lFileExt.getPidcA2l() == null) ||
        ((pidcA2lFileExt.getPidcA2l().getA2lFileId() == null) || (null == pidcA2lFileExt.getPidcVersion()));
  }


  /**
   * @param attributeValue AttributeValue
   * @param pidcA2lFileExt A2LFile
   * @return boolean
   */
  private boolean isA2lAlreadyAssignToAnotherSSDProj(final AttributeValue attributeValue,
      final PidcA2lFileExt pidcA2lFileExt) {
    return (pidcA2lFileExt.getPidcA2l().getSsdSoftwareProjId() != null) &&
        (pidcA2lFileExt.getPidcA2l().getSsdSoftwareProjId() != attributeValue.getNumValue().longValue());
  }


}

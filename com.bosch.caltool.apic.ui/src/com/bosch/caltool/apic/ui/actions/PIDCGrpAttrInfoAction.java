/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.dialogs.PIDCGrpdAttrChangesDialog;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.GroupdAttrPredefAttrModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;


/**
 * ICDM-2625 Action to show Grouped Attribute Information
 *
 * @author dja7cob
 */
public class PIDCGrpAttrInfoAction extends Action {

  /**
   * PIDC Version
   */
  private final PidcVersion pidcVersion;
  // Instance of PIDC page
  private final PIDCAttrPage pidcPage;

  /**
   * @param pidcPage PIDCPage
   * @param pidcVer PIDC version
   */
  public PIDCGrpAttrInfoAction(final PIDCAttrPage pidcPage, final PidcVersion pidcVer) {
    this.pidcVersion = pidcVer;
    // Set the icon
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.GROUPED_ATTR_16X16));
    // Set description for the action
    setText("Show Grouped Attributes Change Information");
    this.pidcPage = pidcPage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    Map<Long, PidcVersionAttribute> allPIDCAttrMap = this.pidcPage.getPidcDataHandler().getPidcVersAttrMap();

    PIDCGroupedAttrActionSet actionSet =
        new PIDCGroupedAttrActionSet(this.pidcPage.getPidcDataHandler(), this.pidcPage.getPidcVersionBO());

    // List of grp attr values
    List<GroupdAttrPredefAttrModel> grpAttrValList =
        actionSet.getAllPidcGrpAttrVal(this.pidcVersion, allPIDCAttrMap, null);

    if ((null != grpAttrValList) && !grpAttrValList.isEmpty()) {
      // show the changes i grouped attributes
      PIDCGrpdAttrChangesDialog dialog = new PIDCGrpdAttrChangesDialog(Display.getDefault().getActiveShell(),
          this.pidcPage, null, null, this.pidcVersion, true, allPIDCAttrMap, null, grpAttrValList, null);
      dialog.open();
    }
    else {
      CDMLogger.getInstance().infoDialog(
          "PIDC: " + this.pidcVersion.getName() + " has no changes in Grouped Attributes!", Activator.PLUGIN_ID);
    }
  }
}

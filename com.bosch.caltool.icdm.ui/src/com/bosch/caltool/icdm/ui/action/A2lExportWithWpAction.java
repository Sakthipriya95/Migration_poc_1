/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ui.dialogs.A2lExportWithWpDialog;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author and4cob
 */
public class A2lExportWithWpAction extends Action {

  /**
   * A2lFile element
   */
  private final PidcA2l pidcA2l;
  private final Long varGrpId;
  private final Long a2lWpDefnVersId;

  /**
   * @param pidcA2l selected Element (PIDC file)
   */
  public A2lExportWithWpAction(final PidcA2l pidcA2l) {
    this(pidcA2l, null, null);
  }

  /**
   * @param pidcA2l PidcA2l
   * @param varGrpId ID of the selected variant group
   */
  public A2lExportWithWpAction(final PidcA2l pidcA2l, final Long a2lWpDefnVersId, final Long varGrpId) {
    super();
    this.pidcA2l = pidcA2l;
    this.varGrpId = varGrpId;
    this.a2lWpDefnVersId = a2lWpDefnVersId;
    setProperties();
  }

  /**
   *
   */
  private void setProperties() {
    setText("Export A2L with Work Packages");
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.A2LFILE_16X16));
    setEnabled(true);
    if(CommonUtils.isNotNull(this.pidcA2l) && !this.pidcA2l.isActive()) {
      setEnabled(false);
    }
  }

  @Override
  public void run() {
    try {
      if (new CurrentUserBO().canExportA2L(this.pidcA2l.getProjectId())) {
        A2lExportWithWpDialog exportA2lDialog = new A2lExportWithWpDialog(this.pidcA2l, this.a2lWpDefnVersId,
            this.varGrpId, Display.getCurrent().getActiveShell());
        exportA2lDialog.open();
      }
      else {
        CDMLogger.getInstance().errorDialog("Atleast read access on the PIDC is necessary", Activator.PLUGIN_ID);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    super.run();
  }

}

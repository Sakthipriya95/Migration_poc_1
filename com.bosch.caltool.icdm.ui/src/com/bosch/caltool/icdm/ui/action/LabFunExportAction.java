/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.action;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.calcomp.labfunwriter.LabFunWriterConstants.OUTPUT_FILE_TYPE;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ui.dialogs.LabFunExportDialog;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author dja7cob
 */
public class LabFunExportAction extends Action {

  /**
  *
  */
  private static final String LAB_EXPORT_ERROR =
      "LAB file cannot be exported since parameter mappings are not available for the following work package(s): \n %s";

  /**
  *
  */
  private static final String LAB_MISSING_PARAMS_INFO =
      "Parameter mappings are not available for the following work package(s): \n %s \nProceeding with export of work package(s) with parameter mapping... \n";

  /**
   *
   */
  private final Set<String> labels;

  /**
  *
  */
  private final Set<String> functions;

  /**
  *
  */
  private final OUTPUT_FILE_TYPE exportFileType;

  /**
  *
  */
  private final String errorMsg;

  /**
   * @param labels set of parameters to be exported to LAB file
   * @param functions set of functions to be exported to FUN file
   * @param exportFileType LAB/FUN
   * @param errorMsg ErrorMsg for missing params
   */
  public LabFunExportAction(final Set<String> labels, final Set<String> functions,
      final OUTPUT_FILE_TYPE exportFileType, final String errorMsg) {
    this.labels = labels;
    this.functions = functions;
    this.exportFileType = exportFileType;
    this.errorMsg = errorMsg;
    setActionProperties();
  }

  /**
   *
   */
  private void setActionProperties() {
    if (this.exportFileType == OUTPUT_FILE_TYPE.LAB) {
      setText("Export Parameters as LAB");
    }
    else {
      setText("Export FCs as FUN");
    }
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UPLOAD_LAB_16X16));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {

    if (!CommonUtils.isEmptyString(this.errorMsg)) {
      if (CommonUtils.isNullOrEmpty(this.labels) && CommonUtils.isNullOrEmpty(this.functions)) {
        // Error dialog when none of the selected params/functions can be exported
        MessageDialogUtils.getErrorMessageDialog("iCDM Error", String.format(LAB_EXPORT_ERROR, this.errorMsg));
        return;
      }
      // Info dialog when part of the selection can be exported
      MessageDialogUtils.getInfoMessageDialog("Info", String.format(LAB_MISSING_PARAMS_INFO, this.errorMsg));
    }
    LabFunExportDialog laFunDialog = new LabFunExportDialog(Display.getCurrent().getActiveShell(), this.labels,
        this.functions, null, this.exportFileType);
    laFunDialog.open();
  }
}

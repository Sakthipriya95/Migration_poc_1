/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.dialogs.CalStatusListDialog;
import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;

/**
 * @author say8cob
 *
 */
public class CalibrationStatusListExportAction extends Action {

  private CdrReportEditorInput cdrReportEditorInput;
  
  private CompHexWithCDFxDataHandler compData;
  
  private boolean isCDRExcelExport;
  /**
   * Constructor
   * @param cdrReportEditorInput for Data review report
   * @param compData for Compare hex
   * @param isCDRExcelExport true for data review/ false for compare hex
   */
  public CalibrationStatusListExportAction(final CdrReportEditorInput cdrReportEditorInput,final CompHexWithCDFxDataHandler compData,final boolean isCDRExcelExport) {
    super();
    this.cdrReportEditorInput = cdrReportEditorInput;
    this.compData = compData;
    this.isCDRExcelExport = isCDRExcelExport;
    setProperties();
  }
  
  /**
   * set properties for action
   */
  private void setProperties() {
    setText("Calibration Status List");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.EXCEL_FILE_16X16);
    setImageDescriptor(imageDesc);
    setEnabled(true);// this action is enabled for all users right now
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
      // create wizard dialog instance
      CalStatusListDialog statusListWizardDialog =
          new CalStatusListDialog(Display.getCurrent().getActiveShell(),this.cdrReportEditorInput,this.compData,isCDRExcelExport);
      // open report dialog
      statusListWizardDialog.open();
  }
}

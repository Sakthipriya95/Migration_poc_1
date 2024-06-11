/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.io.File;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.action.Action;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;


/**
 * @author pdh2cob
 */
public class CompHexRevExportPdfAction extends Action {

  final CompHexWithCDFxDataHandler dataHandler;

  /**
   * @param dataHandler - instance of CompHEXWithCDFxData
   */
  public CompHexRevExportPdfAction(final CompHexWithCDFxDataHandler dataHandler) {
    super();
    this.dataHandler = dataHandler;
    setProperties();
  }

  @Override
  public void run() {
    // Open created PDF report if available
    String icdmDir = ClientConfiguration.getDefault().getIcdmTempDirectory();
    String zipDir = icdmDir + ((icdmDir.endsWith("\\") || icdmDir.endsWith("/")) ? "" : File.separator) +
        this.dataHandler.getRefereneId();
    File pdfFile = getReportFile(new File(zipDir));
    if ((pdfFile != null) && CommonUtils.isFileAvailable(pdfFile.getPath())) {
      if (!CommonUtils.checkIfFileOpen(pdfFile)) {
        CommonUiUtils.openFile(pdfFile.getPath());
      }
    }
    else {
      CDMLogger.getInstance().error("Compare Hex : Compliance PDF Report is not available!", Activator.PLUGIN_ID);
    }
  }

  /**
   * Gets the report file.
   *
   * @param directory the directory
   * @return the report file
   */
  public File getReportFile(final File directory) {
    Iterator fileIt = null;
    String[] extensions = { "pdf" };
    fileIt = FileUtils.iterateFiles(directory, extensions, false);

    if ((fileIt == null) || !fileIt.hasNext()) {
      return null;
    }
    while (fileIt.hasNext()) {
      return (File) fileIt.next();
    }
    return null;
  }

  /**
   * set the properties
   */
  private void setProperties() {
    setText("Open PDF report");
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.PDF_EXPORT_16X16));
  }
}

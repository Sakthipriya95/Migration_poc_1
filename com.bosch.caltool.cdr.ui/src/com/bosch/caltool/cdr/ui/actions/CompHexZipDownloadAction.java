/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;


/**
 * @author pdh2cob
 */
public class CompHexZipDownloadAction extends Action {

  final CompHexWithCDFxDataHandler compHexWithCdfxData;

  /**
   * @param compHexWithCdfxData - instance of CompHEXWithCDFxData
   */
  public CompHexZipDownloadAction(final CompHexWithCDFxDataHandler compHexWithCdfxData) {
    super("Exporting Zip file...");
    this.compHexWithCdfxData = compHexWithCdfxData;
    setProperties();
  }


  @Override
  public void run() {
    String icdmDir = ClientConfiguration.getDefault().getIcdmTempDirectory();

    String zipFileName = this.compHexWithCdfxData.getRefereneId() + ".zip";
    String zipFile = icdmDir + ((icdmDir.endsWith("\\") || icdmDir.endsWith("/")) ? "" : File.separator) + zipFileName;
    if (CommonUtils.isFileAvailable(zipFile)) {
      String selectedDir = null;
      DirectoryDialog dialog = new DirectoryDialog(Display.getDefault().getActiveShell());
      dialog.setFilterPath(CommonUtils.getUserDirPath());
      selectedDir = dialog.open();
      if (!CommonUtils.isEmptyString(selectedDir)) {
        try {
          String newZipFile = selectedDir +
              ((selectedDir.endsWith("\\") || selectedDir.endsWith("/")) ? "" : File.separator) + zipFileName;
          FileUtils.copyFile(new File(zipFile), new File(newZipFile));
          CDMLogger.getInstance().info("Hex Compare zip file is saved. Path : " + newZipFile, Activator.PLUGIN_ID);
        }
        catch (IOException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
    }
    else {
      CDMLogger.getInstance().error("Compare Hex : Zip file is not available!", Activator.PLUGIN_ID);
    }
  }

  /**
   * set the properties
   */
  private void setProperties() {
    setText("Download zip file");
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.DOWNLOAD_16X16));
  }
}

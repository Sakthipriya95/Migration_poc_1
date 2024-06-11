/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.action.Action;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;


/**
 * @author pdh2cob
 */
public class CompHexCheckSSDReportAction extends Action {

  /**
   * Constant to maintain total length of excel file limited to 100 characters
   */
  private static final int MAX_EXCEL_FILE_LEN = 100;
  final CompHexWithCDFxDataHandler dataHandler;
  private String checkSSDInternalReport;

  /**
   * Instantiates a new comp hex check SSD report action.
   *
   * @param dataHandler the data handler
   */
  public CompHexCheckSSDReportAction(final CompHexWithCDFxDataHandler dataHandler) {
    super("Exporting CheckSSD Report...");
    this.dataHandler = dataHandler;
    setProperties();
  }


  @Override
  public void run() {
    // Open created checkssd report if available
    String icdmDir = ClientConfiguration.getDefault().getIcdmTempDirectory();
    String zipDir = icdmDir + ((icdmDir.endsWith("\\") || icdmDir.endsWith("/")) ? "" : File.separator) +
        this.dataHandler.getRefereneId();
    File internalReport = findInternalCheckSSDReport(new File(zipDir));
    if (CommonUtils.isNotNull(internalReport)) {
      // checkSSDReportFileName:path location where file has been renamed and replaced if it is lengthy
      if (CommonUtils.isFileAvailable(this.checkSSDInternalReport)) {
        this.dataHandler.setCssdExcelReportPath(this.checkSSDInternalReport);
        CommonUiUtils.openFile(this.checkSSDInternalReport);
      }
      // unzipped file downloaded from server,opens original file, in which case renaming was not required
      else if (CommonUtils.isFileAvailable(internalReport.getPath()) && !CommonUtils.checkIfFileOpen(internalReport)) {
        this.dataHandler.setCssdExcelReportPath(internalReport.getPath());
        CommonUiUtils.openFile(internalReport.getPath());
      }
      else {
        CDMLogger.getInstance().error("Compare Hex : CheckSSD Excel Report file is already open!", Activator.PLUGIN_ID);
      }
    }
    else {
      CDMLogger.getInstance().error("Compare Hex : CheckSSD Excel Report is not available!", Activator.PLUGIN_ID);
    }
  }


  /**
   * Renames excel file if it is lengthy
   *
   * @param excelFile check ssd report file
   * @return String
   */
  public String renameCheckSSDReport(final File excelFile) {
    String newExcelFileName;
    String currentDate =
        new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault(Locale.Category.FORMAT)).format(new Date());

    // Extract file name excluding file extension,this will include hex file name and time stamp
    String checkSSDReportFileName = excelFile.getName().substring(0, excelFile.getName().length() - 4);
    String fileTimeStampExtensn = "";
    if (excelFile.getName().contains(ApicConstants.CSSD_EXTERNAL_REPORT_CODE)) {
      fileTimeStampExtensn = fileTimeStampExtensn + ApicConstants.CSSD_EXTERNAL_REPORT_CODE;
    }
    fileTimeStampExtensn = fileTimeStampExtensn + CDRConstants.SSD_FILE_PATH_SEPARATOR + currentDate + ".xlsx";
    int maxHexFileLen = MAX_EXCEL_FILE_LEN - fileTimeStampExtensn.length();
    newExcelFileName = excelFile.getParent() + File.separator + checkSSDReportFileName.substring(0, maxHexFileLen) +
        fileTimeStampExtensn;
    try {
      Path newFileNamePath = new File(excelFile.getName().replace(excelFile.getName(), newExcelFileName)).toPath();
      Files.move(excelFile.toPath(), newFileNamePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    catch (IOException ex) {
      CDMLogger.getInstance().error(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    return newExcelFileName;
  }

  /**
   * Gets the check ssd internal report file and renames the excel file if it is lenghty.
   *
   * @param directory the directory
   * @return the report file
   */
  public File findInternalCheckSSDReport(final File directory) {
    Iterator fileIt = null;
    String[] extensions = { "xlsx" };
    fileIt = FileUtils.iterateFiles(directory, extensions, true);

    if ((fileIt == null) || !fileIt.hasNext()) {
      return null;
    }
    File internalReport = null;
    while (fileIt.hasNext()) {
      File afile = (File) fileIt.next();
      // check for check ssd internal report
      if (!afile.getName().contains(ApicConstants.CSSD_EXTERNAL_REPORT_CODE)) {
        internalReport = afile;
        // check if renaming is required
        if (afile.getName().length() > MAX_EXCEL_FILE_LEN) {
          // store the path location of internal file so that it can be used to open excel file alongwith CompHex editor
          this.checkSSDInternalReport = renameCheckSSDReport(afile);
        }

      }
      // check if renaming of external report is required
      else if (afile.getName().length() > MAX_EXCEL_FILE_LEN) {
        renameCheckSSDReport(afile);
      }
    }
    if (CommonUtils.isNotNull(internalReport)) {
      return internalReport;
    }
    return directory;
  }

  /**
   * set the properties
   */
  private void setProperties() {
    setText("Open CheckSSD Report");
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EXPORT_16X16));
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.ParamRepeatExcelData;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;


/**
 * ICDM 636 This class is to create an excel report when parameters are repeated in multiple files during review
 *
 * @author mkl2cob
 */
public class ParameterRepeatExport {

  /**
   * Monitor the work in milli second
   */
  private static final int MONITOR_WRK_MILLISEC = 30;

  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;

  /**
   * Cellstyle for header row
   */
  private CellStyle headerCellStyle;

  /**
   * Normal cell style
   */
  private static CellStyle cellStyle;
  /**
   * Font instance
   */
  private Font font;

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor
   */
  public ParameterRepeatExport(final IProgressMonitor monitor) {
    this.monitor = monitor;
  }

  /**
   * @param paramsRepeated List of info about parameters repeated
   * @param filePath String which contains file path
   * @param fileExtn String which contains extension of the file
   */
  public void exportParamRepeatInfo(final List<ParamRepeatExcelData> paramsRepeated, final String filePath,
      final String fileExtn) {
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    createSheet(workbook);

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.font = ExcelCommon.getInstance().createFont(workbook);

    try {

      final String fileFullPath;
      if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
        fileFullPath = filePath;
      }
      else {
        fileFullPath = filePath + "." + fileExtn;
      }

      final FileOutputStream fileOut = new FileOutputStream(fileFullPath);


      this.monitor.worked(MONITOR_WRK_MILLISEC);
      this.monitor.subTask("Exporting review results . . .");

      createParamRepeatSheet(workbook, paramsRepeated);

      workbook.setSelectedTab(0);

      workbook.write(fileOut);
      fileOut.flush();
      fileOut.close();


    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().error(fnfe.getLocalizedMessage(), fnfe, Activator.PLUGIN_ID);
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().error(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method creates the Parameter Repeated worksheet
   *
   * @param workbook Workbook
   * @param paramsRepeated List of info about parameters repeated
   */
  private void createParamRepeatSheet(final Workbook workbook, final List<ParamRepeatExcelData> paramsRepeated) {
    final String[] paramRepeatSheetHeader = { "Function", "Parameter", "File Name" };

    final Sheet paramRepeatSheet = workbook.getSheetAt(0);
    final Row headerRow = ExcelCommon.getInstance().createExcelRow(paramRepeatSheet, 0);
    for (int headerCol = 0; headerCol < (paramRepeatSheetHeader.length); headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, paramRepeatSheetHeader[headerCol], headerCol,
          this.headerCellStyle, this.font);
    }

    int rowCount = 0;
    int maxColidx = 0;
    for (ParamRepeatExcelData parameterData : paramsRepeated) {
      rowCount++;
      final Row row = ExcelCommon.getInstance().createExcelRow(paramRepeatSheet, rowCount);
      for (int col = 0; col < paramRepeatSheetHeader.length; col++) {
        if (col > maxColidx) {
          maxColidx = col;
        }

        switch (paramRepeatSheetHeader[col]) {
          case ExcelClientConstants.FUNCTION:
            createCol(parameterData.getFuncName(), row, col);
            break;

          case ExcelClientConstants.PARAMETER:
            createCol(parameterData.getParamName(), row, col);
            break;

          case ExcelClientConstants.FILE_NAME:
            createCol(parameterData.getFileName(), row, col);
            break;

        }
      }
    }

    for (int colCnt = 0; colCnt <= maxColidx; colCnt++) {
      paramRepeatSheet.autoSizeColumn(colCnt);
    }
  }


  /**
   * @param valueName
   * @param row
   * @param col
   * @param reviewResultSheet
   * @param bgColor yellow background for changed values needed or not
   */
  private void createCol(final String valueName, final Row row, final int col) {
    CellStyle styleToUse = cellStyle;
    if (valueName != null) {
      ExcelCommon.getInstance().createCell(row, valueName, col, styleToUse);
    }
    else {
      ExcelCommon.getInstance().createCell(row, "", col, styleToUse);
    }
  }

  /**
   * This method creates the sheet in the excel report
   *
   * @param workbook
   */
  private void createSheet(final Workbook workbook) {
    final String paramSheetName = "Repeated Parameters";
    workbook.createSheet(paramSheetName);
  }

}

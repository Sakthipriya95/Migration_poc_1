/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.client.bo.fc2wp.FC2WPMappingResult;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMapping;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;

/**
 * @author gge6cob
 */
public class FC2WPReportDataExport {

  /**
   * Count of columns to be frozen
   */
  private static final int FREEZABLE_COL = 1;
  /**
   * second level progress
   */
  private static final int PROGRESS_2 = 40;
  /**
   * first level progress
   */
  private static final int PROGRESS_1 = 30;
  /**
   * Column width
   */
  private static final int COL_WIDTH = 10000;
  /**
   * Column index 1
   */
  private static final int FUNC_LONG_NM_COL = 1;
  /**
   * Column index 2
   */
  private static final int WORKPACKAGE_COL = 2;
  /**
   * Column index 12
   */
  private static final int COMMENTS_COL = 12;
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
  private CellStyle cellStyle;
  /**
   * Font instance
   */
  private Font font;
  private final FC2WPMappingResult result;
  private final SortedSet<FC2WPMapping> fc2wpMapFiltered;

  /**
   * Instantiates a new FC2WP report data export.
   *
   * @param monitor progress monitor
   * @param result the result
   * @param fc2wpMapFiltered FC2WP data available in table aftering filtering
   */
  public FC2WPReportDataExport(final IProgressMonitor monitor, final FC2WPMappingResult result,
      final SortedSet<FC2WPMapping> fc2wpMapFiltered) {
    this.monitor = monitor;
    this.result = result;
    this.fc2wpMapFiltered = fc2wpMapFiltered;
  }

  /**
   * Create data work sheet
   *
   * @param workbook
   * @param cdrData
   */
  private void createFC2WPDataWorkSheet(final Workbook workbook, final Map<Integer, String> headerMap) {
    final Sheet reportSheet = workbook.getSheetAt(0);
    CellStyle localHeaderCellStyle = this.headerCellStyle;

    final Row headerRow = ExcelCommon.getInstance().createExcelRow(reportSheet, 0);
    int headerLength = headerMap.size();

    // Reason for header length minus 1 in the for loop is, to skip the Report data column name in the sheet header
    for (int headerCol = 0; headerCol < (headerLength - 1); headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, headerMap.get(headerCol), headerCol, localHeaderCellStyle,
          this.font);
    }

    int headerCol = 0;
    int totalSize = headerLength;
    String headerName = headerMap.get(headerCol);

    // create the header row cells
    for (headerCol = 0; headerCol < totalSize; headerCol++ ,headerName = headerMap.get(headerCol)) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, headerName, headerCol, localHeaderCellStyle, this.font);
    }
    int rowCount = setExcelInput(headerMap, reportSheet);
    for (int col = 0; col < totalSize; col++) {
      if ((col == FUNC_LONG_NM_COL) || (col == WORKPACKAGE_COL) || (col == COMMENTS_COL)) {
        reportSheet.setColumnWidth(col, COL_WIDTH);
      }
      else {
        reportSheet.autoSizeColumn(col);
      }
    }

    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(reportSheet,
          "A1:" + ExcelCommon.getInstance().getExcelColName(totalSize - 1), headerMap.size());
    }

    // freeze the parameter related cols
    reportSheet.createFreezePane(FREEZABLE_COL, 1);
  }

  /**
   * Set the input for the created data sheet
   *
   * @param cdrData
   * @param cdrReportSheetHeader
   * @param cdrReportSheet
   * @param cdrReportData
   * @return
   */
  private int setExcelInput(final Map<Integer, String> headerMap, final Sheet reportSheet) {
    int rowCount = 0;
    for (FC2WPMapping mapping : this.fc2wpMapFiltered) {
      rowCount++;
      final Row row = ExcelCommon.getInstance().createExcelRow(reportSheet, rowCount);
      createSheetData(headerMap, mapping, row);
    }
    return rowCount;
  }

  /**
   * Creates cell for each row
   *
   * @param cdrReportSheetHeader
   * @param parameter
   * @param row
   */
  private void createSheetData(final Map<Integer, String> headerMap, final FC2WPMapping mapping, final Row row) {
    Map<Integer, String> dataMap = getColumnIndexData(mapping);
    for (int colIndex = 0; colIndex < headerMap.size(); colIndex++) {
      createCol(dataMap.get(colIndex), row, colIndex);
    }
  }

  /**
   * @param mapping
   * @return
   */
  private Map<Integer, String> getColumnIndexData(final FC2WPMapping fc2wpValues) {
    Map<Integer, String> dataMap = new HashMap<>();
    // Get the column index
    int colIndex = 0;
    String key = fc2wpValues.getFunctionName();
    // name
    dataMap.put(colIndex, fc2wpValues.getFunctionName());
    colIndex++;
    // Long name
    dataMap.put(colIndex, fc2wpValues.getFunctionDesc());
    colIndex++;
    // Workpackage
    dataMap.put(colIndex, this.result.getWorkpackage(key));
    colIndex++;
    // Resource
    dataMap.put(colIndex, this.result.getResource(key));
    colIndex++;
    // WP-ID (MCR)
    dataMap.put(colIndex, this.result.getWpIdMCR(key));
    colIndex++;
    // BC
    dataMap.put(colIndex, this.result.getBC(key));
    colIndex++;
    // PT-Type
    dataMap.put(colIndex, this.result.getPTtypeUIString(key));
    colIndex++;
    // Contact1
    dataMap.put(colIndex, this.result.getFirstContactEffective(key));
    colIndex++;
    // Contact2
    dataMap.put(colIndex, this.result.getSecondContactEffective(key));
    colIndex++;
    // Agreed with Coc ( Center of Competence)
    dataMap.put(colIndex, this.result.getIsCoCAgreedUIString(key));
    colIndex++;
    // Agreed on date
    dataMap.put(colIndex, setDateFormat(fc2wpValues.getAgreeWithCocDate()));
    colIndex++;
    // Responsible for Coc agreement
    dataMap.put(colIndex, this.result.getAgreeWithCocRespUserDisplay(key));
    colIndex++;
    // Comments
    dataMap.put(colIndex, fc2wpValues.getComments());
    colIndex++;
    // FC2WP Information
    dataMap.put(colIndex, fc2wpValues.getFc2wpInfo());
    colIndex++;
    // Is function part of ICDM A2L
    dataMap.put(colIndex, this.result.getIsInICDMA2LUIString(key));
    colIndex++;
    // Is FC in SDOM
    dataMap.put(colIndex, this.result.getIsFcInSdomUIString(key));
    colIndex++;
    // Is FC With Params
    dataMap.put(colIndex, this.result.isFcWithParams(key));
    colIndex++;
    // Is Deleted
    dataMap.put(colIndex, this.result.getIsDeletedUIString(key));
    colIndex++;
    try {
      dataMap.put(colIndex, ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, fc2wpValues.getCreatedDate(),
          DateFormat.DATE_FORMAT_12));
      colIndex++;
      // modified date
      if (null != fc2wpValues.getModifiedDate()) {
        dataMap.put(colIndex, ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_15, fc2wpValues.getModifiedDate(),
            DateFormat.DATE_FORMAT_12));
      }
      else {
        dataMap.put(colIndex, fc2wpValues.getModifiedDate());
      }
    }
    catch (ParseException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }

    return dataMap;
  }

  /**
   * @param agreeWithCocDate
   * @return
   */
  private String setDateFormat(final Date date) {
    String formattedDate = "";
    if (date != null) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      formattedDate = ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_12, cal);
    }
    return formattedDate;
  }

  /**
   * Creates column with the specified value
   *
   * @param valueName
   * @param reviewResultSheet
   * @param row
   * @param col
   */
  private void createCol(final Object valueName, final Row row, final int col) {
    final CellStyle styleToUse = this.cellStyle;
    Cell cell;
    // value to be set is null , an empty cell is created
    if (valueName == null) {
      cell = ExcelCommon.getInstance().createCell(row, "", col, styleToUse);
    }
    else {
      cell = ExcelCommon.getInstance().createCell(row, valueName.toString(), col, styleToUse);
    }
    if ((col == FUNC_LONG_NM_COL) || (col == WORKPACKAGE_COL) || (col == COMMENTS_COL)) {
      styleToUse.setWrapText(true);
      cell.setCellStyle(styleToUse);
    }
  }

  /**
   * Export FC2WP report data.
   *
   * @param headerMap the header map
   * @param filePath the file path
   * @param fileExtn the file extn
   */
  public void exportFC2WPReportData(final Map<Integer, String> headerMap, final String filePath,
      final String fileExtn) {

    final ExcelFactory exFactory = ExcelFactory.getFactory(fileExtn);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    // create the first sheet
    workbook.createSheet("FC-WP Mapping Details");

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.font = ExcelCommon.getInstance().createFont(workbook);

    try {

      String fileFullPath;
      if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
        fileFullPath = filePath;
      }
      else {
        fileFullPath = filePath + "." + fileExtn;
      }

      final FileOutputStream fileOut = new FileOutputStream(fileFullPath);

      // set monitor progress-1
      this.monitor.worked(PROGRESS_1);
      this.monitor.subTask("Exporting review results . . .");

      CDMLogger.getInstance().debug("FC2WP Mapping work sheet creation in export started");
      // set monitor progress-2
      this.monitor.worked(PROGRESS_2);

      createFC2WPDataWorkSheet(workbook, headerMap);

      CDMLogger.getInstance().debug("FC2WP Mapping work sheet creation in export completed");

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
}
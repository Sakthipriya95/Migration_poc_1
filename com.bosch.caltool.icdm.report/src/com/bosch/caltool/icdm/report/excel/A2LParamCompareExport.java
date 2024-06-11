/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.eclipse.core.runtime.IProgressMonitor;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.client.bo.a2l.A2LCompareHandler;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.client.bo.a2l.A2lParamCompareRowObject;
import com.bosch.caltool.icdm.client.bo.a2l.IUIConstants;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;

/**
 * @author apj4cob
 */
public class A2LParamCompareExport {

  /**
   * Column after which column corresponding to other A2l for comparision starts
   */
  private static final int COMPARE_START_COL_NO = 7;
  /**
   * To compare other A2l with the first A2l displayed in the ui
   */
  private static final int REF_COL_NUM = 4;

  /**
   * Constant to be used as count of row/column to be freezed
   */
  private static final int FREEZABLE_COL_ROW_COUNT = 2;

  /**
   * Parameter column index in excel sheet
   */
  private static final int PARAMETER_COL_INDEX = 0;

  /**
   * Difference column index in excel sheet
   */
  private static final int DIFF_COL_INDEX = 1;

  /** Cellstyle for header row. */
  private CellStyle headerCellStyle;

  /** Normal cell style. */
  private CellStyle cellStyle;

  /** Font instance. */
  private Font font;
  /**
   * second level progress
   */
  private static final int PROGRESS_2 = 40;
  /**
   * first level progress
   */
  private static final int PROGRESS_1 = 30;
  /**
   * Setting width of Diff column to make column header label visible in excel
   */
  private static final int DIFF_COL_WIDTH = 1500;
  /**
   * Setting width of Parameter Column in excel
   */
  private static final int PARAMETER_COL_WIDTH = 10000;
  /**
   * The progress monitor instance to which progess information is to be set.
   */
  private final IProgressMonitor monitor;

  private final Map<Integer, Cell> a2lFileNameHeaderCellMap = new HashMap<>();
  private Map<Integer, String> headerMap = new HashMap<>();
  /** Cell Style to create cell with yellow background */
  private CellStyle cellStyleWithBgColr;

  private static final int[] COL_WIDTH_ARR = { 3000, 3000, 5000, 10000, 2500, 2500 };

  /**
   * @param monitor IProgressMonitor
   */
  public A2LParamCompareExport(final IProgressMonitor monitor) {
    this.monitor = monitor;
  }

  /**
   * @param propertyToLabelMap to excel column
   * @param filePath - Complete file path for the excel file being exported
   * @param fileExtn - extension of the excel file
   * @param excelData -data to be exported
   * @param a2lParamCompareHandler A2LCompareHandler
   */
  public void exportA2LParamCompare(final Map<Integer, String> propertyToLabelMap, final String filePath,
      final String fileExtn, final Set<A2lParamCompareRowObject> excelData,
      final A2LCompareHandler a2lParamCompareHandler) {
    // create workbook
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileExtn);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();
    // create the first sheet
    workbook.createSheet("A2L Parameter Compare");
    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.cellStyleWithBgColr = ExcelCommon.getInstance().createCellStyle(workbook);
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
      this.headerMap = propertyToLabelMap;
      createA2LCompareExcelSheet(workbook, excelData, a2lParamCompareHandler);
      // set monitor progress-1
      this.monitor.worked(PROGRESS_1);
      this.monitor.subTask("Exporting A2L Parameter Compare Data . . .");
      CDMLogger.getInstance().debug("A2L Parameter Compare export started");
      // set monitor progress-2
      this.monitor.worked(PROGRESS_2);
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
   * @param workbook
   * @param excelData Set<A2lParamCompareRowObject>
   * @param propertyToLabelMap Map<Integer, String>
   * @param a2lCompareHandler A2LCompareHandler
   */
  private void createA2LCompareExcelSheet(final Workbook workbook, final Set<A2lParamCompareRowObject> excelData,
      final A2LCompareHandler a2lCompareHandler) {
    final Sheet reportSheet = workbook.getSheetAt(0);
    CellStyle localHeaderCellStyle = this.headerCellStyle;
    final Row headerRow0 = ExcelCommon.getInstance().createExcelRow(reportSheet, 0);
    // merge the cells in first row to display a2l file name corresponding to grouped column
    final CellRangeAddress region = new CellRangeAddress(0, 0, 0, 1);
    reportSheet.addMergedRegion(region);
    int startColIndex = 2;
    int colCount = startColIndex;
    int count = 0;
    while (colCount < this.headerMap.size()) {
      count++;
      if (colCount == startColIndex) {
        Cell cell = ExcelCommon.getInstance().createCell(headerRow0, "", colCount, localHeaderCellStyle);
        this.a2lFileNameHeaderCellMap.put(startColIndex, cell);
      }
      else {
        ExcelCommon.getInstance().createCell(headerRow0, "", colCount, localHeaderCellStyle);
      }
      if (count == 6) {
        final CellRangeAddress region1 = new CellRangeAddress(0, 0, startColIndex, (startColIndex + count) - 1);
        reportSheet.addMergedRegion(region1);
        reportSheet.groupColumn(startColIndex + 1, (startColIndex + count) - 1);
        count = 0;
        startColIndex = colCount + 1;
      }
      colCount++;
    }
    // create header row
    final Row headerRow = ExcelCommon.getInstance().createExcelRow(reportSheet, 1);
    int headerLength = this.headerMap.size();
    for (int headerCol = 0; headerCol < headerLength; headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, this.headerMap.get(headerCol + 1), headerCol,
          localHeaderCellStyle, this.font);
    }
    int headerCol = 0;
    int totalSize = headerLength;
    String headerName = this.headerMap.get(headerCol + 1);
    // create the header row cells
    for (headerCol = 0; headerCol < totalSize; headerCol++ ,headerName = this.headerMap.get(headerCol + 1)) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, headerName, headerCol, localHeaderCellStyle, this.font);
    }
    int rowCount = setExcelInput(reportSheet, excelData, a2lCompareHandler);
    // Disable autoFilter when there are no rows
    if (rowCount > 0) {
      reportSheet.setAutoFilter(new CellRangeAddress(1, 1, 0, this.headerMap.size() - 1));
    }
    reportSheet.setColumnWidth(0, DIFF_COL_WIDTH);
    reportSheet.setColumnWidth(1, PARAMETER_COL_WIDTH);
    for (int col = 2; col < totalSize; col++) {
      reportSheet.setColumnWidth(col, COL_WIDTH_ARR[(col - 2) % 6]);
    }
    reportSheet.createFreezePane(FREEZABLE_COL_ROW_COUNT, FREEZABLE_COL_ROW_COUNT);
  }

  private int setExcelInput(final Sheet reportSheet, final Set<A2lParamCompareRowObject> excelData,
      final A2LCompareHandler a2lCompareHandler) {
    int rowCount = 1;
    for (A2lParamCompareRowObject rowObject : excelData) {
      rowCount++;
      final Row row = ExcelCommon.getInstance().createExcelRow(reportSheet, rowCount);
      createSheetData(rowObject, row, a2lCompareHandler);
    }
    return rowCount;
  }

  /**
   * @param a2lWpDefVersId wp def version id
   * @param a2lVarGrp A2lVariantGroup
   * @param a2lCompareHandler A2LCompareHandler
   * @return String grouped header column name
   */
  private String getHeaderName(final Long a2lWpDefVersId, final A2lVariantGroup a2lVarGrp,
      final A2LCompareHandler a2lCompareHandler) {
    final StringBuilder headerName = new StringBuilder();
    A2lWpDefnVersion a2lWpDefVer =
        a2lCompareHandler.getA2lWpInfoMap().get(a2lWpDefVersId).getA2lWpDefnVersMap().get(a2lWpDefVersId);
    String a2lFileName = a2lCompareHandler.getA2lWpInfoMap().get(a2lWpDefVersId).getPidcA2lBo().getPidcA2l().getName();
    headerName.append(a2lFileName);
    headerName.append('-');
    headerName.append(a2lWpDefVer.getName());
    // add var grp name if selected
    if (a2lVarGrp != null) {
      headerName.append('-');
      headerName.append(a2lVarGrp.getName());
    }
    return headerName.toString();
  }

  /**
   * @param headerMap
   * @param rowObject
   * @param row
   * @param a2lCompareHandler
   */
  private void createSheetData(final A2lParamCompareRowObject rowObject, final Row row,
      final A2LCompareHandler a2lCompareHandler) {
    // map to populate data in column
    Map<Integer, String> dataMap = new HashMap<>();
    for (int colIndex = 0; colIndex < this.headerMap.size(); colIndex++) {
      // fill a2l file/variant group name in merged cell header
      if (this.a2lFileNameHeaderCellMap.containsKey(colIndex)) {
        Long a2lWpDefVersId = rowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().get(colIndex + 1);
        A2lVariantGroup a2lVarGrp = rowObject.getA2lColumnDataMapper().getColumnIndexA2lVarGrpMap().get(colIndex + 1);
        this.a2lFileNameHeaderCellMap.get(colIndex)
            .setCellValue(getHeaderName(a2lWpDefVersId, a2lVarGrp, a2lCompareHandler));
      }
      if (colIndex == PARAMETER_COL_INDEX) {
        dataMap.put(colIndex, rowObject.getParamName());
      }
      else if (colIndex == DIFF_COL_INDEX) {
        String diff;
        diff = rowObject.isComputedDiff() ? "Yes" : "No";
        dataMap.put(colIndex, diff);
      }
      else if (colIndex > DIFF_COL_INDEX) {
        A2LWPInfoBO a2lWpInfoBo = rowObject.getA2lWpInfoBO(colIndex + 1);
        A2LWpParamInfo paramInfo = rowObject.getParamInfo(colIndex + 1);
        populateRowVal(dataMap, colIndex, a2lWpInfoBo, paramInfo);
      }
      createCol(dataMap.get(colIndex), row, colIndex, rowObject);

    }

  }

  /**
   * @param rowObject A2lParamCompareRowObject
   * @param colIndex column index
   * @param a2lWpInfoBo1 A2l Wp Info BO of reference column
   * @param a2lWpInfoBo2 A2l Wp Info BO of column being compared to check for difference
   * @param paramInfo2 param info object of reference column
   * @param paramInfo1 param info objetc of column being compared
   * @return boolean flag indicating whether cell values
   */
  public boolean fillBGColrForDiffVal(final A2lParamCompareRowObject rowObject, final int colIndex,
      final A2LWPInfoBO a2lWpInfoBo1, final A2LWPInfoBO a2lWpInfoBo2, final A2LWpParamInfo paramInfo2,
      final A2LWpParamInfo paramInfo1) {
    A2lVariantGroup a2lVarGrp1 = rowObject.getA2lColumnDataMapper().getColumnIndexA2lVarGrpMap().get(colIndex + 1);
    A2lVariantGroup a2lVarGrp2 = rowObject.getA2lColumnDataMapper().getColumnIndexA2lVarGrpMap().get(REF_COL_NUM);
    switch (this.headerMap.get(colIndex + 1)) {
      case IUIConstants.FUNCTION_NAME:
        return rowObject.isFuncDiff(paramInfo2, paramInfo1);
      case IUIConstants.FUNCTION_VERS:
        return rowObject.isFuncVerDiff(paramInfo2, paramInfo1);
      case IUIConstants.BC:
        return rowObject.isBCDiff(paramInfo2, paramInfo1);
      case IUIConstants.WORK_PACKAGE:
        return rowObject.isWpDiff(
            rowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().get(colIndex + 1),
            rowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().get(REF_COL_NUM), a2lVarGrp1,
            a2lVarGrp2);
      case IUIConstants.RESPONSIBILTY:
        return rowObject.isRespDiff(
            rowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().get(colIndex + 1),
            rowObject.getA2lColumnDataMapper().getColumnIndexA2lWpDefVersIdMap().get(REF_COL_NUM), a2lVarGrp1,
            a2lVarGrp2);
      case IUIConstants.NAME_AT_CUSTOMER:
        return (rowObject.isNameAtCustDiff(paramInfo2, paramInfo1, a2lWpInfoBo1, a2lWpInfoBo2));
      default:
        return false;
    }
  }

  /**
   * @param dataMap map to populate cell value
   * @param colIndex col index
   * @param a2lWpInfoBo a2l wp info bo to fetch cell value
   * @param paramInfo param info to fetch cell value
   */
  public void populateRowVal(final Map<Integer, String> dataMap, final int colIndex, final A2LWPInfoBO a2lWpInfoBo,
      final A2LWpParamInfo paramInfo) {
    switch (this.headerMap.get(colIndex + 1)) {
      // Get Function name from param info
      case IUIConstants.FUNCTION_NAME:
        dataMap.put(colIndex, getFuncNameFromParamInfo(paramInfo));
        break;
      // Get Function version from param info
      case IUIConstants.FUNCTION_VERS:
        dataMap.put(colIndex, getFuncVersFromParamInfo(paramInfo));
        break;
      // Get BC name from param info
      case IUIConstants.BC:
        dataMap.put(colIndex, getBcNameFromParamInfo(paramInfo));
        break;
      // Get workpackage from param info
      case IUIConstants.WORK_PACKAGE:
        dataMap.put(colIndex, getWpFromParamInfo(a2lWpInfoBo, paramInfo));
        break;
      // Get Responsibility from param info
      case IUIConstants.RESPONSIBILTY:
        dataMap.put(colIndex, getRespFromParamInfo(a2lWpInfoBo, paramInfo));
        break;
      // Get Wp customer name from param info
      case IUIConstants.NAME_AT_CUSTOMER:
        dataMap.put(colIndex, getNameAtCustFromParamInfo(a2lWpInfoBo, paramInfo));
        break;
      default:
    }
  }

  /**
   * @param paramInfo
   * @return
   */
  private String getFuncNameFromParamInfo(final A2LWpParamInfo paramInfo) {
    return null == paramInfo ? "" : paramInfo.getFuncName();
  }


  /**
   * @param paramInfo
   * @return
   */
  private String getFuncVersFromParamInfo(final A2LWpParamInfo paramInfo) {
    return null == paramInfo ? "" : paramInfo.getFunctionVer();
  }

  /**
   * @param paramInfo
   * @return
   */
  private String getBcNameFromParamInfo(final A2LWpParamInfo paramInfo) {
    return null == paramInfo ? "" : paramInfo.getBcName();
  }

  /**
   * @param a2lWpInfoBo
   * @param paramInfo
   * @return
   */
  private String getWpFromParamInfo(final A2LWPInfoBO a2lWpInfoBo, final A2LWpParamInfo paramInfo) {
    return null == paramInfo ? "" : a2lWpInfoBo.getWPName(paramInfo);
  }

  /**
   * @param a2lWpInfoBo
   * @param paramInfo
   * @return
   */
  private String getRespFromParamInfo(final A2LWPInfoBO a2lWpInfoBo, final A2LWpParamInfo paramInfo) {
    return null == paramInfo ? "" : a2lWpInfoBo.getWpRespUser(paramInfo);
  }

  /**
   * @param a2lWpInfoBo
   * @param paramInfo
   * @return
   */
  private String getNameAtCustFromParamInfo(final A2LWPInfoBO a2lWpInfoBo, final A2LWpParamInfo paramInfo) {
    return null == paramInfo ? "" : a2lWpInfoBo.getWpNameCust(paramInfo);
  }

  /**
   * Creates column with the specified value
   *
   * @param valueName
   * @param reviewResultSheet
   * @param row
   * @param colIndex
   */
  private void createCol(final Object valueName, final Row row, final int colIndex,
      final A2lParamCompareRowObject rowObject) {
    final CellStyle styleToUse = this.cellStyle;
    Cell cell;
    // value to be set is null , an empty cell is created
    if (valueName == null) {
      cell = ExcelCommon.getInstance().createCell(row, "", colIndex, styleToUse);
    }
    else {
      cell = ExcelCommon.getInstance().createCell(row, valueName.toString(), colIndex, styleToUse);
    }
    if (colIndex > COMPARE_START_COL_NO) {

      A2LWPInfoBO a2lWpInfoBo1 = rowObject.getA2lWpInfoBO(colIndex);
      A2LWPInfoBO a2lWpInfoBo2 = rowObject.getA2lWpInfoBO(REF_COL_NUM);
      A2LWpParamInfo paramInfo2 = rowObject.getParamInfo(colIndex);
      A2LWpParamInfo paramInfo1 = rowObject.getParamInfo(REF_COL_NUM);
      if (fillBGColrForDiffVal(rowObject, colIndex, a2lWpInfoBo1, a2lWpInfoBo2, paramInfo2, paramInfo1)) {
        this.cellStyleWithBgColr.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
        cell.setCellStyle(this.cellStyleWithBgColr);
      }
      else {
        cell.setCellStyle(this.cellStyle);
      }
    }
    else {
      cell.setCellStyle(this.cellStyle);
    }
  }
}


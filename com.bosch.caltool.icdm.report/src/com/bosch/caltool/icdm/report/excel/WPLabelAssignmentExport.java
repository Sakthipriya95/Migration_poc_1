/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.excel;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO.SortColumns;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWpParamInfo;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;

/**
 * @author NIP4COB
 */
public class WPLabelAssignmentExport {

  /** Cellstyle for header row. */
  private CellStyle headerCellStyle;

  /** Normal cell style. */
  private CellStyle cellStyle;

  /** Font instance. */
  private Font font;


  /**
   * @param a2lwpInfoBO a2lwpInfoBO
   * @param filePath - Complete file path for the excel file being exported
   * @param fileExtn - extension of the excel file
   * @param a2lVariantGroup - selected A2lVariantgroup
   * @param a2lParamInfoSet - data to be exported
   */
  public void exportWPLabel(final A2LWPInfoBO a2lwpInfoBO, final String filePath, final String fileExtn,
      final A2lVariantGroup a2lVariantGroup, final Set<A2LWpParamInfo> a2lParamInfoSet) {
    // create workbook
    SortedSet<A2LWpParamInfo> excelData = loadExcelData(a2lParamInfoSet, a2lwpInfoBO);
    final Workbook workbook = ExcelFactory.getFactory(fileExtn).getExcelFile().createWorkbook();
    workbook.createSheet("PAL");
    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(workbook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(workbook);
    this.font = ExcelCommon.getInstance().createFont(workbook);

    try (OutputStream fileOut = new FileOutputStream(filePath)) {
      CDMLogger.getInstance().debug("Excel Export of WP-Parameter Assignment is started");
      createWPLbelExcelSheet(workbook, a2lwpInfoBO, a2lVariantGroup, excelData);
      workbook.setSelectedTab(0);
      workbook.write(fileOut);
      fileOut.flush();
      CDMLogger.getInstance().debug("Excel Export of WP-Parameter Assignment is complete");
    }
    catch (IOException exp) {
      CDMLogger.getInstance().errorDialog("Excel Export of WP_Parameter Assignment could not be done", exp,
          Activator.PLUGIN_ID);
    }
  }

  /**
   * Load the data for excel
   *
   * @param excelData2
   * @param a2lwpInfoBO
   * @return
   */
  private SortedSet<A2LWpParamInfo> loadExcelData(final Set<A2LWpParamInfo> excelData2, final A2LWPInfoBO a2lwpInfoBO) {
    // sort the data
    SortedSet<A2LWpParamInfo> a2LWpParamInfoSet =
        new TreeSet<>(getA2lWpParamComparator(SortColumns.SORT_PARAM_NAME, a2lwpInfoBO));
    a2LWpParamInfoSet.addAll(excelData2);
    return a2LWpParamInfoSet;
  }

  /**
   * Add the comparator
   *
   * @param sortColName - Sort column Name
   * @param a2lwpInfoBO
   * @return Comparator
   */
  public Comparator<A2LWpParamInfo> getA2lWpParamComparator(final SortColumns sortColName,
      final A2LWPInfoBO a2lwpInfoBO) {
    return (final A2LWpParamInfo param1, final A2LWpParamInfo param2) -> a2lwpInfoBO.compareTo(param1, param2,
        sortColName);
  }

  /**
   * Create wp label excel sheet
   *
   * @param workbook
   * @param a2lwpInfoBO
   * @param a2lVariantGroup
   * @param a2LWpParamInfoSet
   */
  private void createWPLbelExcelSheet(final Workbook workbook, final A2LWPInfoBO a2lwpInfoBO,
      final A2lVariantGroup a2lVariantGroup, final SortedSet<A2LWpParamInfo> a2LWpParamInfoSet) {
    Sheet sheet = workbook.getSheetAt(0);
    final Row headerRow = ExcelCommon.getInstance().createExcelRow(sheet, 0);
    for (int headerCol = 0; headerCol < (WPLabelAssignmentExcelColumn.wpExcelHeader.length); headerCol++) {
      ExcelCommon.getInstance().createHeaderCell(headerRow, WPLabelAssignmentExcelColumn.wpExcelHeader[headerCol],
          headerCol, this.headerCellStyle, this.font);
    }
    int rowCount = 0;
    for (A2LWpParamInfo a2LWpParamInfo : a2LWpParamInfoSet) {
      rowCount++;
      final Row row = ExcelCommon.getInstance().createExcelRow(sheet, rowCount);
      createCols(WPLabelAssignmentExcelColumn.wpExcelHeader, a2LWpParamInfo, row, a2lwpInfoBO, a2lVariantGroup, sheet);
    }
    if (rowCount > 0) {
      ExcelCommon.getInstance().setAutoFilter(sheet, "A1:H", rowCount);
    }
    for (int col = 0; col < WPLabelAssignmentExcelColumn.wpExcelHeader.length; col++) {
      sheet.autoSizeColumn(col);
    }
    sheet.createFreezePane(ApicConstants.COLUMN_INDEX_2, 0);
  }

  /**
   * Create columns in the wp excel sheet
   *
   * @param wpExcelHeader
   * @param a2lWpParamInfo
   * @param row
   * @param a2lwpInfoBO
   * @param a2lVariantGroup
   * @param sheet
   */
  private void createCols(final String[] wpExcelHeader, final A2LWpParamInfo a2lWpParamInfo, final Row row,
      final A2LWPInfoBO a2lwpInfoBO, final A2lVariantGroup a2lVariantGroup, final Sheet sheet) {
    int col = 0;
    for (String element : wpExcelHeader) {
      Cell cell = ExcelCommon.getInstance().createCell(row, a2lWpParamInfo.getParamName(), col, this.cellStyle);
      switch (element) {
        case ExcelClientConstants.PARAMETER_NAME:
          if (a2lWpParamInfo.isDependentParameter()) {
            StringBuilder toolTip = new StringBuilder();
            toolTip.append(ApicConstants.DEPENDENT_PARAM).append(System.lineSeparator());
            a2lWpParamInfo.getDepCharNames()
                .forEach(charName -> toolTip.append(charName).append(System.lineSeparator()));
            ExcelCommon.getInstance().attachTooltip(sheet, cell, toolTip.toString());
          }
          break;
        case ExcelClientConstants.FUNCTION_NAME:
          ExcelCommon.getInstance().createCell(row, a2lWpParamInfo.getFuncName(), col, this.cellStyle);
          break;
        case ExcelClientConstants.FUNCTION_VERSION:
          ExcelCommon.getInstance().createCell(row, a2lWpParamInfo.getFunctionVer(), col, this.cellStyle);
          break;
        case ExcelClientConstants.BC:
          ExcelCommon.getInstance().createCell(row, a2lWpParamInfo.getBcName(), col, this.cellStyle);
          break;
        case ExcelClientConstants.VARIANT_GROUP:
          createVariantGrpCell(row, a2lVariantGroup, col);
          break;
        case ExcelClientConstants.WORKPACKAGE:
          ExcelCommon.getInstance().createCell(row, a2lwpInfoBO.getWPName(a2lWpParamInfo), col, this.cellStyle);
          break;
        case ExcelClientConstants.RESPONSBILITIES:
          ExcelCommon.getInstance().createCell(row, a2lwpInfoBO.getWpRespAliasName(a2lWpParamInfo), col,
              this.cellStyle);
          break;
        case ExcelClientConstants.RESPONSIBILITY_TYPE:
          ExcelCommon.getInstance().createCell(row, a2lwpInfoBO.getRespTypeName(a2lwpInfoBO.getWpResp(a2lWpParamInfo)),
              col, this.cellStyle);
          break;
        case ExcelClientConstants.DEPENDENT_LABEL:
          ExcelCommon.getInstance().createCell(row, a2lWpParamInfo.getDependentLabel(), col, this.cellStyle);
          break;
        case ExcelClientConstants.READ_ONLY_LABEL:
          ExcelCommon.getInstance().createCell(row, a2lWpParamInfo.getReadOnlyLabel(), col, this.cellStyle);
          break;
        default:
          break;
      }
      col++;
    }
  }


  /**
   * @param row
   * @param a2lVariantGroup
   * @param col
   */
  private void createVariantGrpCell(final Row row, final A2lVariantGroup a2lVariantGroup, final int col) {
    if (a2lVariantGroup == null) {
      ExcelCommon.getInstance().createCell(row, "", col, this.cellStyle);
    }
    else {
      ExcelCommon.getInstance().createCell(row, a2lVariantGroup.getName(), col, this.cellStyle);
    }
  }

}

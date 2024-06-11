/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.report.compare;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.report.Activator;
import com.bosch.caltool.icdm.report.common.ExcelCommon;


/**
 * @author jvi6cob
 */
public abstract class AbstractCompareReport {

  /**
   * for loop count
   */
  private static final int FOR_LOOP_COUNT = 9;

  /**
   * header cell index2
   */
  private static final int HEADER_CELL_INDEX2 = 4;

  /**
   * header cell index1
   */
  private static final int HEADER_CELL_INDEX1 = 4;

  /**
   * Cellstyle for header row
   */
  protected CellStyle headerCellStyle;

  /**
   * Normal cell style
   */
  protected CellStyle cellStyle;
  /**
   * Normal cell style
   */
  protected CellStyle redCellStyle;
  /**
   * Font instance
   */
  protected Font font;
  /**
   * Workboo instance
   */
  protected Workbook workBook;

  /**
   * @param obj1 first object to compare
   * @param obj2 second object to compare
   * @param filePath path to save the report
   * @param fileExtn xls or xlx
   * @param compareType compare object Type indicator
   */
  public void generateReport(final Object obj1, final Object obj2, final String filePath, final String fileExtn,
      final String compareType) {

    initialize(fileExtn);

    try {
      final String fileFullPath = filePath + "." + fileExtn;
      final FileOutputStream fileOut = new FileOutputStream(fileFullPath);

      createCompareWorkSheet(obj1, obj2);

      this.workBook.write(fileOut);
      fileOut.flush();
      fileOut.close();
      String info = compareType + ": Report saved successfully to path : ";
      info += filePath + "." + fileExtn;
      CDMLogger.getInstance().info(info, Activator.PLUGIN_ID);

    }
    catch (FileNotFoundException fnfe) {
      CDMLogger.getInstance().errorDialog(fnfe.getLocalizedMessage(), fnfe, Activator.PLUGIN_ID);
    }
    catch (IOException ioe) {
      CDMLogger.getInstance().errorDialog(ioe.getLocalizedMessage(), ioe, Activator.PLUGIN_ID);
    }

  }


  /**
   * Creates a excel worksheet based on the passed in parameters
   * 
   * @param obj1 object1 to be compared
   * @param obj2 object2 to be compared
   */
  protected abstract void createCompareWorkSheet(Object obj1, Object obj2);

  /**
   * @param fileExtn
   */
  private void initialize(final String fileExtn) {
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileExtn);
    final ExcelFile xlFile = exFactory.getExcelFile();
    this.workBook = xlFile.createWorkbook();

    this.headerCellStyle = ExcelCommon.getInstance().createHeaderCellStyle(this.workBook);
    this.cellStyle = ExcelCommon.getInstance().createCellStyle(this.workBook);
    this.redCellStyle = ExcelCommon.getInstance().createRedCellStyle(this.workBook);
    this.font = ExcelCommon.getInstance().createFont(this.workBook);
  }

  /**
   * @param wrkBook
   * @return
   */
  protected CellStyle createCompareHeaderCellStyle(final Workbook wrkBook) {


    CellStyle compCellStyle = wrkBook.createCellStyle();
    compCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
    compCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);


    compCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    compCellStyle.setBorderRight(CellStyle.BORDER_THIN);
    compCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    compCellStyle.setBorderTop(CellStyle.BORDER_THIN);

    compCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    compCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

    return compCellStyle;
  }

  /**
   * Creates two double cell merged columns for each comparable object
   * 
   * @param workbook Workbook
   * @param name1 String
   * @param name2 String
   * @param revision1 String
   * @param revision2 String
   * @param pidcAttrSheet Sheet
   * @param index1 int
   * @param index2 int
   * @param rowNum int
   */
  protected void createHeaderForCompare(final Workbook workbook, final String name1, final String name2,
      final String revision1, final String revision2, final Sheet pidcAttrSheet, final int index1, final int index2,
      final int rowNum) {

    CellStyle localCellStyle = workbook.createCellStyle();
    localCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
    localCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);


    localCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    localCellStyle.setBorderRight(CellStyle.BORDER_THIN);
    localCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    localCellStyle.setBorderTop(CellStyle.BORDER_THIN);
    localCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    localCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

    Row headerRow0 = pidcAttrSheet.createRow(rowNum);
    //
    Cell blankCell1 = headerRow0.createCell(index1 + HEADER_CELL_INDEX1);
    blankCell1.setCellStyle(localCellStyle);


    Cell blankCell2 = headerRow0.createCell(index2 + HEADER_CELL_INDEX2);
    blankCell2.setCellStyle(localCellStyle);
    //
    if (revision1 != null) {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name1 + "(" + revision1 + ")", index1, localCellStyle,
          this.font);
    }
    else {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name1, index1, localCellStyle, this.font);
    }
    if (revision2 != null) {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name2 + "(" + revision2 + ")", index2, localCellStyle,
          this.font);
    }
    else {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name2, index2, localCellStyle, this.font);
    }
    CellRangeAddress region = new CellRangeAddress(rowNum, rowNum, index1, index1 + HEADER_CELL_INDEX1);
    pidcAttrSheet.addMergedRegion(region);
    CellRangeAddress region1 = new CellRangeAddress(rowNum, rowNum, index2, index2 + HEADER_CELL_INDEX2);
    pidcAttrSheet.addMergedRegion(region1);
  }

  /**
   * Creates a four cell merged column common for the comparable objects
   * 
   * @param workbook Workbook
   * @param name String
   * @param revision String
   * @param pidcAttrSheet Sheet
   * @param index int
   * @param rowNum int
   */
  protected void createCommonHeaderForCompare(final Workbook workbook, final String name, final String revision,
      final Sheet pidcAttrSheet, final int index, final int rowNum) {

    final CellStyle localCellStyle = workbook.createCellStyle();
    localCellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    localCellStyle.setBorderRight(CellStyle.BORDER_THIN);
    localCellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    localCellStyle.setBorderTop(CellStyle.BORDER_THIN);
    localCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    localCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    localCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
    localCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

    final Row headerRow0 = pidcAttrSheet.createRow(rowNum);
    // Apply cell style to each cell to avoid incorrect border
    for (int j = index + 1; j <= index + FOR_LOOP_COUNT; j++) {
      final Cell blankCell = headerRow0.createCell(j);
      blankCell.setCellStyle(localCellStyle);
    }
    if (revision == null) {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name, index, localCellStyle, this.font);
    }
    else {
      ExcelCommon.getInstance().createHeaderCell(headerRow0, name + "(" + revision + ")", index, localCellStyle,
          this.font);
    }

    // Create merged region
    final CellRangeAddress region = new CellRangeAddress(rowNum, rowNum, index, index + 9);
    pidcAttrSheet.addMergedRegion(region);

    headerRow0.getCell(index).setCellStyle(localCellStyle);

  }

}

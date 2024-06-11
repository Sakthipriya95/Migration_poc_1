/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.bosch.caltool.excel.ExcelFactory;
import com.bosch.caltool.excel.ExcelFile;
import com.bosch.caltool.icdm.model.cdr.ParamRepeatExcelData;


/**
 * ICDM 636 This class is to create an excel report when parameters are repeated in multiple files during review
 *
 * @author mkl2cob
 */
public class ParameterRepeatExport {

  /**
   * Name of font
   */
  private final String FONT_NAME = "Arial";
  /**
   * Size of font
   */
  private final short FONT_SIZE = 10;

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

  private final String FUNCTION = "Function";

  private final String FILE_NAME = "File Name";

  private final String PARAMETER = "Parameter";

  /**
   * Constructor
   *
   * @param monitor IProgressMonitor
   */
  public ParameterRepeatExport() {}

  /**
   * @param paramsRepeated List of info about parameters repeated
   * @param filePath String which contains file path
   * @param fileExtn String which contains extension of the file
   * @throws IOException
   */
  public String exportParamRepeatInfo(final List<ParamRepeatExcelData> paramsRepeated, final String filePath,
      final String fileExtn)
      throws IOException {
    final String fileType = fileExtn;
    final ExcelFactory exFactory = ExcelFactory.getFactory(fileType);
    final ExcelFile xlFile = exFactory.getExcelFile();
    final Workbook workbook = xlFile.createWorkbook();

    createSheet(workbook);

    this.headerCellStyle = createHeaderCellStyle(workbook);
    cellStyle = createCellStyle(workbook);
    this.font = createFont(workbook);

    final String fileFullPath;
    if (filePath.contains(".xlsx") || filePath.contains(".xls")) {
      fileFullPath = filePath;
    }
    else {
      fileFullPath = filePath + "." + fileExtn;
    }

    final FileOutputStream fileOut = new FileOutputStream(fileFullPath);

    createParamRepeatSheet(workbook, paramsRepeated);

    workbook.setSelectedTab(0);

    workbook.write(fileOut);
    fileOut.flush();
    fileOut.close();
    return fileFullPath;

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
    final Row headerRow = createExcelRow(paramRepeatSheet, 0);
    for (int headerCol = 0; headerCol < (paramRepeatSheetHeader.length); headerCol++) {
      createHeaderCell(headerRow, paramRepeatSheetHeader[headerCol], headerCol, this.headerCellStyle, this.font);
    }

    int rowCount = 0;
    int maxColidx = 0;
    for (ParamRepeatExcelData parameterData : paramsRepeated) {
      rowCount++;
      final Row row = createExcelRow(paramRepeatSheet, rowCount);
      for (int col = 0; col < paramRepeatSheetHeader.length; col++) {
        if (col > maxColidx) {
          maxColidx = col;
        }

        switch (paramRepeatSheetHeader[col]) {
          case FUNCTION:
            createCol(parameterData.getFuncName(), row, col);
            break;

          case PARAMETER:
            createCol(parameterData.getParamName(), row, col);
            break;

          case FILE_NAME:
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
      createCell(row, valueName, col, styleToUse);
    }
    else {
      createCell(row, "", col, styleToUse);
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


  /**
   * Creates a new header cell object using the input parameters
   *
   * @param row excel row
   * @param cellValue value of the cell
   * @param cellColumn cell column index
   * @param cellStyle style of cell
   * @param font font of cell
   * @return the header cell object
   */
  private final Cell createHeaderCell(final Row row, final String cellValue, final int cellColumn,
      final CellStyle cellStyle, final Font font) {

    final Cell cell = row.createCell(cellColumn);
    cell.setCellValue(cellValue);
    cellStyle.setFont(font);
    cell.setCellStyle(cellStyle);
    return cell;
  }

  /**
   * Create a row
   *
   * @param workSheet the worksheet
   * @param rowIndex the row index
   * @return the row object
   */
  private final Row createExcelRow(final Sheet workSheet, final int rowIndex) {
    return workSheet.createRow(rowIndex);
  }

  /**
   * Create the header style
   *
   * @param workbook the workbook
   * @return the header style
   */
  private final CellStyle createHeaderCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();

    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    return cellStyle;
  }

  /**
   * Create the cell style
   *
   * @param workbook the workbook
   * @return the cell style
   */
  private final CellStyle createCellStyle(final Workbook workbook) {
    final CellStyle cellStyle = workbook.createCellStyle();
    cellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    return cellStyle;
  }

  /**
   * Create font style
   *
   * @param workbook workbook
   * @return the font
   */
  private final Font createFont(final Workbook workbook) {
    final Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    font.setFontName(this.FONT_NAME);
    font.setFontHeightInPoints(this.FONT_SIZE);
    return font;
  }

  /**
   * Create the cell using the given inputs
   *
   * @param row the row
   * @param cellValue the value of cell
   * @param cellColumn column index
   * @param cellStyle cell style
   * @return the cell object
   */
  private final Cell createCell(final Row row, final String cellValue, final int cellColumn,
      final CellStyle cellStyle) {

    final Cell cell = row.createCell(cellColumn);
    cell.setCellValue(cellValue);
    cellStyle.setAlignment(CellStyle.ALIGN_LEFT);
    cell.setCellStyle(cellStyle);
    return cell;
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.excelimport;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * A representation of an excel file that holds the filename, sheetname and the POI workbook object
 *
 * @author imi2si
 * @version 1.0
 * @created 07-Feb-2014 09:45:27
 */
public class ExcelFile {

  private String fileName;
  private String sheetName;
  private Workbook excelFileObject;
  private final ILoggerAdapter logger;

  /**
   * @param fileName the full qualified filename of the excelfile
   * @param sheetName the sheetname from which should be imported
   */
  public ExcelFile(final String fileName, final String sheetName, final ILoggerAdapter logger) {
    setFileName(fileName);
    setSheetName(sheetName);
    this.logger = logger;
  }

  /**
   * Constructor that creates an excel file object and determines the sheet name by itself
   *
   * @param fileName the full qualified filename of the excelfile
   */
  public ExcelFile(final String fileName, final ILoggerAdapter logger) {
    setFileName(fileName);
    setSheetname();
    this.logger = logger;
  }

  private void initFile() {
    try {
      this.excelFileObject = WorkbookFactory.create(new File(this.fileName));
    }
    catch (InvalidFormatException | IOException e) {
      this.logger.error(e.getLocalizedMessage(), e);
    }
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return this.fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(final String fileName) {
    this.fileName = fileName;

    // Aater filename is set, file will be initialized immediately
    initFile();
  }

  /**
   * @return the sheetName
   */
  public String getSheetName() {
    return this.sheetName;
  }


  /**
   * If the first sign of the sheet name is a percentage sign, it is replaced and the sheet name as function name is
   * returned. Otherwise the function name is returned
   *
   * @return the function name extracted from the sheet name
   */
  public String getFunctionName() {

    if (this.sheetName.startsWith("%")) {
      return getSheetName().substring(1);
    }

    if (this.sheetName.endsWith("%")) {
      return getSheetName().substring(0, getSheetName().length() - 1);
    }

    return getSheetName();
  }

  /**
   * @param sheetName the sheetName to set
   */
  public void setSheetName(final String sheetName) {
    this.sheetName = sheetName;
  }

  /**
   * Computes the sheetsname in the excel file. The Sheetname equals the function name in iCDM
   */
  private void setSheetname() {

    String functionName = new String();

    // Iterate over all sheets. The first sheet starting with "%" is considered as function name
    for (int loop = 0; loop < getExcelFileObject().getNumberOfSheets(); loop++) {
      if (getExcelFileObject().getSheetAt(loop).getSheetName().startsWith("%") ||
          getExcelFileObject().getSheetAt(loop).getSheetName().endsWith("%") ||
          !getExcelFileObject().getSheetAt(loop).getSheetName().equalsIgnoreCase("Historie")) {
        functionName = getExcelFileObject().getSheetAt(loop).getSheetName();
        break;
      }
    }

    // Only if a function name (means a sheetname starting with "%") is found, the
    // sheetname of the excelobject is set
    if (!functionName.isEmpty()) {
      this.sheetName = functionName;
    }
  }

  /**
   * @return theorg.apache.poi.ss.usermodel.Workbook that represents the imported excel file
   */
  public Workbook getExcelFileObject() {
    return this.excelFileObject;
  }

  /**
   * @return the org.apache.poi.ss.usermodel.Sheet that represents the imported excel sheet
   */
  public Sheet getExcelSheetObject() {
    return getExcelFileObject().getSheetAt(getExcelFileObject().getSheetIndex(getSheetName()));

  }

  public int getColNumberFromText(final int rowNumber, final boolean isMandatory, final String... colText)
      throws IllegalArgumentException {
    int colIndex = -1;
    int colsFound = 0;
    Iterator i = getExcelSheetObject().getRow(rowNumber).iterator();
    String colTextInFile = new String();

    while (i.hasNext()) {
      Cell curCell = (Cell) i.next();

      for (String _colText : colText) {
        String colValue = "";
        if (curCell.getCellType() == Cell.CELL_TYPE_STRING) {
          colValue = curCell.getStringCellValue().toLowerCase();
        }
        else if (curCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
          colValue = String.valueOf(curCell.getNumericCellValue()).toLowerCase();
        }

        if (colValue.startsWith(_colText.toLowerCase())) {
          colIndex = curCell.getColumnIndex();
          colsFound++;
          colTextInFile = _colText;
        }
      }
    }

    if ((colIndex == -1) && isMandatory) {
      throw new IllegalArgumentException(
          "Column starting with text \"" + getColString(colText) + "\" not found in file " + this.fileName);
    }

    if (colsFound > 1) {
      throw new IllegalArgumentException("Column starting with text \"" + colText + "\" found " + colsFound +
          " times. Only one occurence is allowed.");
    }

    // logger.debug("Column name starting with \"" + colText + "\" found in column " + colIndex );
    return colIndex;
  }

  private String getColString(final String... colText) {
    StringBuffer buffer = new StringBuffer();
    for (String entry : colText) {
      buffer.append(entry + ", ");
    }

    return buffer.toString().substring(0, buffer.length());
  }
}
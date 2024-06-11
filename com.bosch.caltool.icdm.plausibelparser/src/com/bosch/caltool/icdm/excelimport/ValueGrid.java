/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.excelimport;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;


/**
 * @author imi2si
 */
public class ValueGrid {

  /**
   * Row number in the excel sheet were import should start (0-based index). This column is included in the import.
   */
  private final int rowNumberStart;
  /**
   * Row number in the excel sheet were import should end (0-based index). This column is included in the import.
   */
  private int rowNumberEnd = -1;
  private final ExcelFile excelFile;
  private ColumnList columnList;
  private final Map<Integer, Row> gridCellValue;
  private ILoggerAdapter logger;

  /**
   * Default constructor for a grid
   *
   * @param logger logger
   * @param excelFile the ExcelFile object from which a row should be imported
   * @param columnList the ColumnList of columns relevant for the import
   * @param rowNumberStart the int of the row (1-based) in the excel file that should be imported
   */
  public ValueGrid(final ILoggerAdapter logger, final ExcelFile excelFile, final ColumnList columnList,
      final int rowNumberStart) {
    this.excelFile = excelFile;
    this.columnList = columnList;
    this.rowNumberStart = rowNumberStart;
    this.gridCellValue = new LinkedHashMap<>();
    setLogger(logger);

    // fill the row object
    createGrid();
  }

  /**
   * COnstructor that considers bounds for start and end of import
   *
   * @param logger logger
   * @param excelFile the ExcelFile object from which a row should be imported
   * @param columnList the ColumnList of columns relevant for the import
   * @param rowNumberStart the starting row (0-based) in the excel file that should be imported
   * @param rowNumberEnd the last row (0-based) in the excel file that should be imported
   */
  public ValueGrid(final ILoggerAdapter logger, final ExcelFile excelFile, final ColumnList columnList,
      final int rowNumberStart, final int rowNumberEnd) {
    this.excelFile = excelFile;
    this.columnList = columnList;
    this.rowNumberStart = rowNumberStart;
    this.rowNumberEnd = rowNumberEnd;
    this.gridCellValue = new LinkedHashMap<>();
    setLogger(logger);

    // fill the row object
    createGrid();
  }

  /**
   * Creates the row based on the excelFile and columnList object
   */
  public void createGrid() {

    // To prepare getting the cell value: Get the sheet as POI-Sheet object
    Sheet sheet = this.excelFile.getExcelSheetObject();

    // The row and starting position within the grid
    org.apache.poi.ss.usermodel.Row poiRow;
    int curRow = this.rowNumberStart;

    // Loop over the rows
    do {
      boolean mandatoryFilled = true;
      poiRow = sheet.getRow(curRow);

      if (poiRow != null) {

        Iterator colIterator = this.columnList.iterator();
        while (colIterator.hasNext()) {
          Column col = (Column) colIterator.next();

          if (col.isMandatory() && ((poiRow.getCell(col.getColumnNumber()) == null) ||
              (poiRow.getCell(col.getColumnNumber()).getCellType() == Cell.CELL_TYPE_BLANK))) {
            curRow++;
            mandatoryFilled = false;
          }
        }

        // Put the row into the map
        if (mandatoryFilled) {
          this.gridCellValue.put(curRow, new Row(getLogger(), this.excelFile, this.columnList, curRow));
        }
      }

      curRow++;

      // Check if the loop should be left
      if ((getRowNumberEnd() == -1) && (poiRow == null)) {
        break;
      }

      if ((getRowNumberEnd() != -1) && (curRow > getRowNumberEnd())) {
        break;
      }

    }
    while (((poiRow != null) && (getRowNumberEnd() == -1)) ||
        ((getRowNumberEnd() != -1) && (curRow < getRowNumberEnd())));
  }

  /**
   * @return a HashMap<Column,AtomicValuePhy> for the whole excel row
   */
  public HashMap<Integer, Row> getGrid() {
    return (HashMap<Integer, Row>) this.gridCellValue;
  }

  /**
   * @param validity a bitset containing the bits that show if valid and/or invalid labels should be shown.<br>
   *          <b>Bit 0 set:</b> Invalid should be shown<br>
   *          <b>Bit 0 not set:</b> Invalid should not be shown<br>
   *          <b>Bit 1 set:</b> Valid should be shown<br>
   *          <b>Bit 1 not set:</b> Valid should not be shown
   * @return a HashMap<Column,AtomicValuePhy> for the whole excel row
   */
  public LinkedHashMap<Integer, Row> getGrid(final BitSet validity) {

    // Return the complete hashmap if bit 0 and 1 is set
    if (validity.get(0) && validity.get(1)) {
      return (LinkedHashMap<Integer, Row>) this.gridCellValue;
    }

    // Otherwise get a subset of the map with just valid or invalid rows is created
    LinkedHashMap<Integer, Row> grid = new LinkedHashMap<>();

    for (Entry<Integer, Row> entry : this.gridCellValue.entrySet()) {
      if (!entry.getValue().isValid() && validity.get(0)) {
        grid.put(entry.getKey(), entry.getValue());
      }

      if (entry.getValue().isValid() && validity.get(1)) {
        grid.put(entry.getKey(), entry.getValue());
      }
    }

    return grid;
  }

  /**
   * @param columnList a ColumnList object that includes all columns that should be imported
   */
  public void setColumnList(final ColumnList columnList) {
    this.columnList = columnList;
  }


  /**
   * @return the ColumnList that includes all currently selected columns
   */
  public ColumnList getColumnList() {
    return this.columnList;
  }


  /**
   * @return the logger
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
  }


  /**
   * @param logger the logger to set
   */
  public void setLogger(final ILoggerAdapter logger) {
    this.logger = logger;
  }

  /**
   * Shows the values of the current row as console (logger) output
   */
  public void showValue() {

    // Iterate over all columns and catch the values
    for (int rowNumber : this.gridCellValue.keySet()) {
      this.gridCellValue.get(rowNumber).showValue();
    }
  }

  /**
   * Shows the values of the current row as console (logger) output
   *
   * @param validity a bitset containing the bits that show if valid and/or invalid labels should be shown.<br>
   *          <b>Bit 0 set:</b> Invalid should be shown<br>
   *          <b>Bit 0 not set:</b> Invalid should not be shown<br>
   *          <b>Bit 1 set:</b> Valid should be shown<br>
   *          <b>Bit 1 not set:</b> Valid should not be shown
   */
  public void showValue(final BitSet validity) {

    // Iterate over all columns and catch the values
    for (int rowNumber : getGrid(validity).keySet()) {
      this.gridCellValue.get(rowNumber).showValue();
    }
  }

  /**
   * @return the rowNumberEnd
   */
  public int getRowNumberEnd() {
    return this.rowNumberEnd;
  }
}

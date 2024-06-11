/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.excelimport;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.caldataphy.AtomicValuePhy;


/**
 * @author imi2si
 * @version 1.0
 * @created 07-Feb-2014 11:26:10
 */
public class Row {


  private static final String COLUMN_WITH_COLUMN_NUM = "Column with column number ";

  private static final String IS_NOT_EXISTING_IN_THE_COLUMN_LIST = " is not existing in the column list.";

  private static final String CAN_T_BE_PARSED_TO_A_NUMBER = " can't be parsed to a number: \"";
  /**
   * String constant for column
   */
  private static final String COLUMN = "; Column: ";
  /**
   * Marks a row as invalid because its values doesn't fit to the data type
   */
  private boolean valid = true;
  /**
   * Row number in the excel sheet (1-based index)
   */
  private final int rowNumber;
  /**
   * Logger for debugging output
   */
  private final ILoggerAdapter logger;
  private final ExcelFile excelFile;
  private ColumnList columnList;
  private final Map<Column, AtomicValuePhy> rowCellValue;
  private final Map<Column, Boolean> rowCellValidity;

  /**
   * Default constructor for a row
   *
   * @param logger logger
   * @param excelFile the ExcelFile object from which a row should be imported
   * @param columnList the ColumnList of columns relevant for the import
   * @param rowNumber the int of the row (1-based) in the excel file that should be imported
   */
  public Row(final ILoggerAdapter logger, final ExcelFile excelFile, final ColumnList columnList, final int rowNumber) {
    this.excelFile = excelFile;
    this.columnList = columnList;
    this.rowNumber = rowNumber;
    this.rowCellValue = new HashMap<>();
    this.rowCellValidity = new HashMap<>();
    this.logger = logger;

    // fill the row object
    createRow();
  }

  /**
   * Creates the row based on the excelFile and columnList object
   */
  public void createRow() {
    getLogger().debug("Trying to parse row " + this.rowNumber);

    // Iterate over all columns and catch the values
    for (Column column : this.columnList) {
      getLogger().debug("Trying to parse column " + column.getColumnNumber() + " in row " + this.rowNumber);

      // Store in Map
      if (column.getColumnNumber() == -1) {
        getLogger().debug("Column not defined: " + column.getColumnName() + ". Empty value will be inserted. ");
        this.rowCellValue.put(column, new AtomicValuePhy(""));
        continue;
      }

      // To prepare getting the cell value: Get the sheet as POI-Sheet object
      Sheet sheet = this.excelFile.getExcelSheetObject();
      AtomicValuePhy value = new AtomicValuePhy("");

      // Variables to store the POI-excel value
      String poiStringExcel = new String();

      // To prepare getting the cell value: Get the row as POI-Sheet object.
      org.apache.poi.ss.usermodel.Row r = sheet.getRow(this.rowNumber);

      // To prepare getting the cell value: Get the cell as POI object.
      Cell c = r.getCell(column.getColumnNumber(), org.apache.poi.ss.usermodel.Row.RETURN_BLANK_AS_NULL);

      try {
        // Try to convert a excel string to a java double, if the column type is a Column.DOUBLE but poi excels delivers
        // CELL_TYPE_STRING because of excel format options
        if ((c.getCellType() == Cell.CELL_TYPE_STRING) && (column.getColumnDataType() == Column.DOUBLE)) {
          value = stringToDouble(column, c);
        }
        else if (c.getCellType() == Cell.CELL_TYPE_STRING) {
          value = new AtomicValuePhy(replaceLineFeed(c.getStringCellValue()));
          this.rowCellValidity.put(column, true);
        }
        else if (c.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
          value = new AtomicValuePhy(c.getBooleanCellValue() ? "TRUE" : "FALSE");
          this.rowCellValidity.put(column, true);
        }
        else if (c.getCellType() == Cell.CELL_TYPE_NUMERIC) {
          value = new AtomicValuePhy(c.getNumericCellValue());
          this.rowCellValidity.put(column, true);
        }
        else if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
          value = formulaToDouble(column, value, c);
        }

        // Store in Map
        this.rowCellValue.put(column, value);
      }
      catch (NullPointerException e) {
        // Cell is empty - throws a null pointer exception
        this.rowCellValue.put(column, new AtomicValuePhy(""));

        // Validity will be set to true. An empty column won't be handled as error
        this.rowCellValidity.put(column, true);
        getLogger().error(e.getLocalizedMessage(), e);
      }
      catch (ClassCastException e) {
        // Validity will be set to false. In this case a number parse exception has happend
        getLogger().error("Row " + (this.rowNumber) + COLUMN + (column.getColumnNumber()) +
            CAN_T_BE_PARSED_TO_A_NUMBER + poiStringExcel + "\"");

        // Nevertheless an value will be inserted into the map, to avoid NullPointerExceptions. Row will be marked as
        // invalid
        this.rowCellValue.put(column, new AtomicValuePhy(poiStringExcel));

        this.rowCellValidity.put(column, false);
        getLogger().error(e.getLocalizedMessage(), e);
      }

      // If the cell is mandatory but empty, the row will be marked as invalid
      if (column.isMandatory() && getValueAt(column).equals("")) {
        this.rowCellValidity.put(column, false);
      }

      // Set validity of the complete row to false, if one of the columns is invalid
      // valid = true is the default value when initializing the row object, thus setting of true is not necessary
      Boolean validity = this.rowCellValidity.get(column);
      if ((validity != null) && !validity.booleanValue()) {
        this.valid = false;
      }
    }
  }

  /**
   * @param column
   * @param value
   * @param c
   * @return
   */
  private AtomicValuePhy formulaToDouble(final Column column, AtomicValuePhy value, final Cell c) {
    if ((c.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING) && (column.getColumnDataType() == Column.DOUBLE)) {
      NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
      ParsePosition pos = new ParsePosition(0);
      double tempDoubleOfString = 0;

      /*
       * Convert the value from a String to a Number. Necessary to check if the String could be converted. If just a
       * part of the String is converted, the object is not null. If it is a complete text, number is null. To avoid a
       * NullPointerException when converting into double, the variable number is checked for existence
       */
      Number number = nf.parse(replaceLineFeed(c.getStringCellValue()), pos);

      // Convert into double if Number is not null
      if (number != null) {
        tempDoubleOfString = number.doubleValue(); // nf.parse(c.getStringCellValue(),pos).doubleValue could
        // retrun null -> NullPointerException when allocated to
      }

      // If length of String != parsing position, the whole number couldn't be transformed into a double. Validity
      // = false
      if (replaceLineFeed(c.getStringCellValue()).length() != pos.getIndex()) {
        getLogger().error("Row " + (this.rowNumber) + COLUMN + (column.getColumnNumber()) +
            CAN_T_BE_PARSED_TO_A_NUMBER + replaceLineFeed(c.getStringCellValue()) + "\"");
        value = new AtomicValuePhy(replaceLineFeed(c.getStringCellValue()));
        this.rowCellValidity.put(column, false);
      }
      // Otherwise store the value
      else {
        value = new AtomicValuePhy(tempDoubleOfString);
        this.rowCellValidity.put(column, true);
      }
    }
    else if (c.getCachedFormulaResultType() == Cell.CELL_TYPE_STRING) {
      value = new AtomicValuePhy(replaceLineFeed(c.getStringCellValue()));
      this.rowCellValidity.put(column, true);
    }
    else if (c.getCachedFormulaResultType() == Cell.CELL_TYPE_NUMERIC) {
      value = new AtomicValuePhy(c.getNumericCellValue());
      this.rowCellValidity.put(column, true);
    }
    else if (c.getCachedFormulaResultType() == Cell.CELL_TYPE_BOOLEAN) {
      value = new AtomicValuePhy(c.getBooleanCellValue() ? "TRUE" : "FALSE");
      this.rowCellValidity.put(column, true);
    }
    return value;
  }

  /**
   * @param column
   * @param c
   * @return
   */
  private AtomicValuePhy stringToDouble(final Column column, final Cell c) {
    AtomicValuePhy value;
    NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);

    ParsePosition pos = new ParsePosition(0);
    double tempDoubleOfString = 0;
    String cellValue = c.getStringCellValue();

    // Check if a wrong decimal separator was passed and correct it
    cellValue = replaceWrongDecimalSep(cellValue);

    /*
     * Convert the value from a String to a Number. Necessary to check if the String could be converted. If just a part
     * of the String is converted, the object is not null. If it is a complete text, number is null. To avoid a
     * NullPointerException when converting into double, the variable number is checked for existence
     */
    Number number = nf.parse(replaceLineFeed(cellValue), pos);

    // Convert into double if Number is not null
    if (number != null) {
      tempDoubleOfString = number.doubleValue(); // nf.parse(c.getStringCellValue(),pos).doubleValue could retrun
      // null -> NullPointerException when allocated to
    }

    // If length of String != parsing position, the whole number couldn't be transformed into a double. Validity =
    // false
    if (replaceLineFeed(c.getStringCellValue()).length() != pos.getIndex()) {
      getLogger().error("Row " + (this.rowNumber) + COLUMN + (column.getColumnNumber()) + CAN_T_BE_PARSED_TO_A_NUMBER +
          replaceLineFeed(c.getStringCellValue()) + "\"");
      value = new AtomicValuePhy(replaceLineFeed(c.getStringCellValue()));
      this.rowCellValidity.put(column, false);
    }
    // Otherwise store the value
    else {
      if (c.getStringCellValue().trim().isEmpty()) {// ICDM-2074
        value = new AtomicValuePhy("");
      }
      else {
        value = new AtomicValuePhy(tempDoubleOfString);
      }
      this.rowCellValidity.put(column, true);
    }
    return value;
  }

  /**
   * @return a HashMap<Column,AtomicValuePhy> for the whole excel row
   */
  public HashMap<Column, AtomicValuePhy> getRow() {
    return (HashMap<Column, AtomicValuePhy>) this.rowCellValue;
  }


  /**
   * @return the logger
   */
  public ILoggerAdapter getLogger() {
    return this.logger;
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
   * Shows the values of the current row as console (logger) output
   */
  public void showValue() {
    getLogger().info("**********************************************************");
    getLogger().info("Row number: " + (this.rowNumber + 1));
    // Iterate over all columns and catch the values
    for (Column column : this.columnList) {
      getLogger().info("Column number/name: " + (column.getColumnNumber() + 1) + " / " + column.getColumnName() +
          "; Cell-Value: " + this.getValueAt(column));
    }
  }

  /**
   * @param column the Column for that the cell value should be returned
   * @return the AtomicValuePhy of the column
   */
  public AtomicValuePhy getAtomicValuePhy(final Column column) {
    return this.rowCellValue.get(column);
  }

  /**
   * @param column the Column for that the cell value should be returned
   * @return the AtomicValuePhy of the column
   */
  public void setAtomicValuePhy(final Column column, final AtomicValuePhy value) {
    this.rowCellValue.put(column, value);
  }

  /**
   * @param columnNumber the column index for that the cell value should be returned
   * @return the AtomicValuePhy of the column
   */
  public AtomicValuePhy getAtomicValuePhy(final int columnNumber) {
    // Get the column out of the column list with the index
    for (Column column : this.columnList) {
      if (column.getColumnNumber() == columnNumber) {
        return this.getAtomicValuePhy(column);
      }
    }

    // Show a logger error
    getLogger().error(COLUMN_WITH_COLUMN_NUM + columnNumber + IS_NOT_EXISTING_IN_THE_COLUMN_LIST);

    // If theres no column with the given index an exception is thrown
    throw new IllegalArgumentException(COLUMN_WITH_COLUMN_NUM + columnNumber + IS_NOT_EXISTING_IN_THE_COLUMN_LIST);
  }

  /**
   * Returns the value depending of the type. Before returning the value it is converted into a string for textual
   * output, that means, only valid values will be returned. If a cell defined as double has a string in it, it is not
   * returned
   *
   * @param column the Column for that the cell value should be returned
   * @return a string value of the cell
   */
  public String getValueAt(final Column column) {

    /*
     * Returns the value depending of the type and converts the value to a string if necessary, because this method
     * should only be used for textual output. In case of the value being empty, the String has to be returned, because
     * the double value is initialized with 0 when creating an ATomicValuePhy
     */
    if (column.getColumnNumber() == -1) {
      return "";
    }

    if ((column.getColumnDataType() == Column.STRING) || this.getAtomicValuePhy(column).getSValue().equals("")) {
      return this.getAtomicValuePhy(column).getSValue();
    }

    return String.valueOf(this.getAtomicValuePhy(column).getDValue());
  }

  /**
   * Returns the value depending of the type. Before returning the value it is converted into a string for textual
   * output, that means, only valid values will be returned. If a cell defined as double has a string in it, it is not
   * returned
   *
   * @param columnNumber the column index for that the cell value should be returned
   * @return a string value of the cell
   * @throws IllegalArgumentException if a column number doesn't exists in the column list
   */
  public String getValueAt(final int columnNumber) {

    // Get the column out of the column list with the index
    for (Column column : this.columnList) {
      if (column.getColumnNumber() == columnNumber) {
        return this.getValueAt(column);
      }
    }

    // Show a logger error
    getLogger().error(COLUMN_WITH_COLUMN_NUM + columnNumber + IS_NOT_EXISTING_IN_THE_COLUMN_LIST);

    // If theres no column with the given index an exception is thrown
    throw new IllegalArgumentException(COLUMN_WITH_COLUMN_NUM + columnNumber + IS_NOT_EXISTING_IN_THE_COLUMN_LIST);
  }

  /**
   * Returns if the row is valid or not. Valid = false is set when at least one column within the row has another
   * datatype than in Excel
   *
   * @return the validity of the row
   */
  public boolean isValid() {
    return this.valid;
  }

  /**
   * Replaces in a given string the line breaks and line feeds with the systems default line separator sign
   *
   * @param value a string
   * @return a string with the replaced line breaks
   */
  public String replaceLineFeed(final String value) {
    String newValue = value.replace("\r", System.getProperty("line.separator"));
    newValue = newValue.replace("\n", System.getProperty("line.separator"));

    return newValue;
  }

  /**
   * Replaces in a given string the line breaks and line feeds with the systems default line separator sign
   *
   * @param value a string
   * @return a string with the replaced line breaks
   */
  private String replaceWrongDecimalSep(final String value) {
    DecimalFormat decFormat = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
    DecimalFormatSymbols decSym = decFormat.getDecimalFormatSymbols();
    char groupSep = decSym.getGroupingSeparator();
    char decSep = decSym.getDecimalSeparator();

    if ((value.indexOf(groupSep) > 0) && (value.indexOf(decSep) == -1)) {
      if ((value.indexOf(groupSep) == 1) && value.startsWith("0")) {
        getLogger().info(
            "Row " + this.rowNumber + ": Value " + value + " seems to contain a wrong decimal separator. Separator \"" +
                groupSep + "\" was replaced by \"" + decSep + "\"");
        return value.replace(groupSep, decSep);
      }
    }

    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuilder text = new StringBuilder();

    for (Entry<Column, AtomicValuePhy> entry : this.rowCellValue.entrySet()) {
      text.append("Column Name: " + entry.getKey().getColumnName() + "; Column Index: " +
          entry.getKey().getColumnNumber() + "; Value: " + entry.getValue().getSValue() + "\r\n");
    }

    return text.toString();
  }
}
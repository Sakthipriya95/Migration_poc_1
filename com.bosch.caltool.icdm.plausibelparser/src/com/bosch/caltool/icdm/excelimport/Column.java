/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.excelimport;


/**
 * @author imi2si
 * @version 1.0
 * @created 07-Feb-2014 10:17:17
 */
public class Column {

  /**
   * Constant value for int columns (Value = 0)
   */
  public static final int INT = 0;
  /**
   * Constant value for string columns (Value = 1)
   */
  public static final int STRING = 1;
  /**
   * Constant value for int columns (Value = 2)
   */
  public static final int DOUBLE = 2;
  /**
   * Stores the data type of an column. One of the values Column.INT, Column.DOUBLE or Column.STRING
   */
  private int columnDataType;
  /**
   * The excel column number (starting with index 1)
   */
  private int columnNumber;
  /**
   * The name of the column
   */
  private String columnName = new String();
  /**
   * Marks the column as mandatory
   */
  private boolean mandatory;


  private Column() {}

  /**
   * Default constructor
   *
   * @param columnNumber the column number of this column in excel (1-based)
   * @param columnDataType an int that represents the data type of the column. Must be one of the values Column.INT,
   *          Column.STRING or Column.DOUBLE
   */
  public Column(final int columnNumber, final int columnDataType) {
    this();
    setColumnNumber(columnNumber);
    setColumnDataType(columnDataType);
    setMandatory(false);
  }

  /**
   * Default constructor with name
   *
   * @param columnNumber the column number of this column in excel (1-based)
   * @param columnDataType an int that represents the data type of the column. Must be one of the values Column.INT,
   *          Column.STRING or Column.DOUBLE
   * @param columnName the excel name of the column. Can be any name to identify the column.
   */
  public Column(final int columnNumber, final int columnDataType, final String columnName) {
    this(columnNumber, columnDataType);
    setColumnName(columnName);
  }

  /**
   * Default constructor with mandatory flag
   *
   * @param columnNumber the column number of this column in excel (1-based)
   * @param columnDataType an int that represents the data type of the column. Must be one of the values Column.INT,
   *          Column.STRING or Column.DOUBLE
   * @param mandatory an boolean that marks the column as mandatory
   */
  public Column(final int columnNumber, final int columnDataType, final boolean mandatory) {
    this();
    setColumnNumber(columnNumber);
    setColumnDataType(columnDataType);
    setMandatory(mandatory);
  }

  /**
   * Default constructor with mandatory flag and name
   *
   * @param columnNumber the column number of this column in excel (1-based)
   * @param columnDataType an int that represents the data type of the column. Must be one of the values Column.INT,
   *          Column.STRING or Column.DOUBLE
   * @param mandatory an boolean that marks the column as mandatory
   */
  public Column(final int columnNumber, final int columnDataType, final boolean mandatory, final String columnName) {
    this(columnNumber, columnDataType, mandatory);
    setColumnName(columnName);
  }

  /**
   * Sets the data type of the column (Possible values are Column.INT, Column.STRING or Column.DOUBLE)
   *
   * @param columnDataType an int that represents the data type of the column. Must be one of the values Column.INT,
   *          Column.STRING or Column.DOUBLE
   */
  public void setColumnDataType(final int columnDataType) {
    this.columnDataType = columnDataType;
  }

  /**
   * Returns the data type of the column (Possible values are Column.INT, Column. STRING or Column.DOUBLE)
   *
   * @return an int that can contain one of the values Column.INT, Column.STRING or Column.DOUBLE
   */
  public int getColumnDataType() {
    return this.columnDataType;
  }

  /**
   * Sets the excel column number (1-based index)
   *
   * @param columnNumber the column number of this column in excel (1-based)
   */
  public void setColumnNumber(final int columnNumber) {
    this.columnNumber = columnNumber;
  }

  /**
   * Returns the excel column number (1-based index)
   *
   * @return an int that represents the column number of this column in excel (1-based)
   */
  public int getColumnNumber() {
    return this.columnNumber;
  }

  /**
   * @return the string column Name
   */
  public String getColumnName() {
    return this.columnName;
  }

  /**
   * @param columnName the string column name to set
   */
  public void setColumnName(final String columnName) {
    this.columnName = columnName;
  }

  /**
   * @return the mandatory
   */
  public boolean isMandatory() {
    return this.mandatory;
  }

  /**
   * @param mandatory the mandatory to set
   */
  public void setMandatory(final boolean mandatory) {
    this.mandatory = mandatory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.columnName == null) ? 0 : this.columnName.hashCode());
    result = (prime * result) + this.columnNumber;
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Column other = (Column) obj;
    if (this.columnName == null) {
      if (other.columnName != null) {
        return false;
      }
    }
    else if (!this.columnName.equals(other.columnName)) {
      return false;
    }
    return this.columnNumber == other.columnNumber;
  }
}
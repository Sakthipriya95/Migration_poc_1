/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.excel;


/**
 * @author bne4cob
 */
public enum ExcelFactory {

  /**
   * Excel 2003 format. Uses HSSF model of Apache POI
   */
  XLS("xls") {

    @Override
    public ExcelFile getExcelFile() {
      return new XLSFile();
    }
  },

  /**
   * Excel 2007 format. Uses XSSF model of Apache POI
   */
  XLSX("xlsx") {

    @Override
    public ExcelFile getExcelFile() {
      return new XLSXFile();
    }
  };

  /**
   * The type of file
   */
  private String fileType;

  /**
   * Constructor
   * 
   * @param fileType file type
   */
  ExcelFactory(final String fileType) {
    this.fileType = fileType;
  }

  /**
   * @return the file type value
   */
  protected String getFileType() {
    return this.fileType;
  }

  /**
   * @return the excel file implementation
   */
  public abstract ExcelFile getExcelFile();

  /**
   * @param literal file type literal
   * @return the excel factory enum value
   */
  public static ExcelFactory getFactory(final String literal) {
    for (ExcelFactory type : ExcelFactory.values()) {
      if (type.fileType.equals(literal)) {
        return type;
      }

    }
    return null;
  }
}

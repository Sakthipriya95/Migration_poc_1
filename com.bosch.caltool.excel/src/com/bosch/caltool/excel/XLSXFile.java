/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * XLSX file . New format(Excel 2007 Workbook(*.xlsx))
 * 
 * @author bne4cob
 */
public class XLSXFile implements ExcelFile {

  /**
   * {@inheritDoc}
   */
  @Override
  public final Workbook createWorkbook() {
    return new XSSFWorkbook();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getFileExtension() {
    return ExcelFactory.XLSX.getFileType();
  }

}

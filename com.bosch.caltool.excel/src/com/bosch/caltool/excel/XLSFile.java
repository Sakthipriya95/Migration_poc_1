/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;


/**
 * XLS file. Old format(Excel 97-2003 Workbook)
 * 
 * @author bne4cob
 */
public class XLSFile implements ExcelFile {

  /**
   * {@inheritDoc}
   */
  @Override
  public final Workbook createWorkbook() {
    return new HSSFWorkbook();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getFileExtension() {
    return ExcelFactory.XLS.getFileType();
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.excel;

import org.apache.poi.ss.usermodel.Workbook;


/**
 * @author bne4cob
 */
public interface ExcelFile {

  /**
   * @return the Excel work book
   */
  Workbook createWorkbook();

  /**
   * @return the extension of this type of file
   */
  String getFileExtension();
}

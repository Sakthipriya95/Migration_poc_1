/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.excelimport.test;

import com.bosch.caltool.icdm.excelimport.Column;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.ExcelFile;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * @author imi2si
 *
 */
public class TestLoader {

  /**
   * @param args default parameter of main method. No meaning in this context.
   */
  public static void main(String[] args) {
    // Reference to the excel file
    ExcelFile excel = new ExcelFile("C:/Archiv/Labelliste_BGLWM_Kai.xls","BGLWM",CDMLogger.getInstance());
    
    // Reference to the column list
    ColumnList colList = new ColumnList();
    
    // Add columns to the column list
    colList.addColumn(new Column(0,Column.STRING));
    colList.addColumn(new Column(1,Column.STRING,true));
    colList.addColumn(new Column(2,Column.STRING));
    colList.addColumn(new Column(3,Column.STRING));
    colList.addColumn(new Column(4,Column.STRING));
    colList.addColumn(new Column(5,Column.DOUBLE));
    colList.addColumn(new Column(6,Column.DOUBLE));
    colList.addColumn(new Column(7,Column.DOUBLE));
    colList.addColumn(new Column(8,Column.STRING));
    colList.addColumn(new Column(9,Column.STRING));
    colList.addColumn(new Column(10,Column.STRING));
    colList.addColumn(new Column(11,Column.STRING));
    colList.addColumn(new Column(12,Column.STRING));
    
    // create a row
    //Row row = new Row(excel,colList,25);
    
    // Show the output of the import
    //row.showValue();
    
    // Create a grid
    //ValueGrid grid = new ValueGrid(excel,colList,2,15);
    
    // BitSet for the output
    /*BitSet bs = new BitSet(2);
    bs.set(0, false);
    bs.set(1, true);
    
    // Show the output of the import
    grid.showValue(bs);*/
    
    //LoadExcelSSD.loadExcelSSD(excel, colList, 2);
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.plausibelparser.cdrruleimport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.Row;


/**
 * @author imi2si
 */
public class PlausibelClasses {

  private final PlausibelFile file;

  /**
   *
   */
  public PlausibelClasses(final PlausibelFile file) {
    this.file = file;
  }

  public Map<String, String> getClasses() {

    ColumnList colList = this.file.getColList();
    Map<String, String> classes = new HashMap<>();

    for (Entry<Integer, Row> entry : this.file.entrySet()) {

      String parameterName =
          entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_2_PARAM)).trim();// icdm-2121

      if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_14_CLASS)).equals("")) {

        String parameterClass =
            entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_14_CLASS));
        classes.put(parameterName, parameterClass);
      }
    }
    return classes;
  }

  public Map<String, String> getHint() {

    ColumnList colList = this.file.getColList();
    Map<String, String> hints = new HashMap<>();

    for (Entry<Integer, Row> entry : this.file.entrySet()) {

      String parameterName =
          entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_2_PARAM)).trim();// icdm-2121

      if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_4_WIEAPPL)).equals("")) {

        String parameterHint =
            entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_4_WIEAPPL));
        hints.put(parameterName, parameterHint);
      }
    }
    return hints;
  }

  public static void main(final String args[]) {

    ILoggerAdapter logger = new Log4JLoggerAdapterImpl("C:\\Temp\\PlausibelLogger.log");

    // pass a logger object and the path to the Plausibel file
    PlausibelFile file;
    try {
      file = new PlausibelFile(logger, "U:\\Files_For_Testcases\\Plausibel\\Labelliste_BGLWM_Kai.xlsx");

      PlausibelClasses classes = new PlausibelClasses(file);

      for (Entry<String, String> entry : classes.getClasses().entrySet()) {
        System.out.println(entry.getKey() + ":" + entry.getValue());
      }
    }
    catch (IOException exp) {
      logger.error(exp.getMessage(), exp);
    }

  }
}

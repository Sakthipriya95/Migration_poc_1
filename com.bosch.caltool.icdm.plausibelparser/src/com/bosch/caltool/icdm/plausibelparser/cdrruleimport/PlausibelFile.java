/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.plausibelparser.cdrruleimport;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.icdm.excelimport.Column;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.ExcelFile;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.caltool.icdm.excelimport.ValueGrid;


/**
 * @author imi2si
 */
public class PlausibelFile {

  private final ILoggerAdapter logger;
  private String filePath;
  private ValueGrid grid;
  private ColumnList colList;
  private String functionName;
  private ExcelFile excel;

  public PlausibelFile(final ILoggerAdapter logger, final String filePath) throws IOException {
    this.logger = logger;
    this.filePath = filePath;

    loadFile();
  }

  /**
   * @param logger
   * @throws IOException
   */
  public PlausibelFile(final ILoggerAdapter logger) throws IOException {
    this.logger = logger;
    loadFile();
  }

  public Set<Entry<Integer, Row>> entrySet() {
    // BitSet for the output; 0=false not show invalid - 1=true show valid
    BitSet bs = new BitSet(2);
    bs.set(0, true);
    bs.set(1, true);

    return this.grid.getGrid(bs).entrySet();
  }

  private void loadFile() throws IOException {
    this.logger.info("Start loading Plausibel file " + this.filePath);
    File file = getFileFromPath();

    this.excel = new ExcelFile(this.logger, file.getAbsolutePath());
    this.functionName = this.excel.getFunctionName();

    this.colList = new ColumnList();

    // Add columns to the column list
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, false, "Appli"), Column.STRING,
        PlausibelImportConstants.COL_1_APPL));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "Parameter", "Name"), Column.STRING,
        true, PlausibelImportConstants.COL_2_PARAM));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "Kurzbeschreibung", "Long Name"),
        Column.STRING, PlausibelImportConstants.COL_3_KURZB));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "Wie appliziert?", "Calibration Hint"),
        Column.STRING, PlausibelImportConstants.COL_4_WIEAPPL));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "optionale Erläuterungen", "Remarks"),
        Column.STRING, PlausibelImportConstants.COL_5_OPTE));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "Erfahrungswerte", "Reference Value"),
        Column.DOUBLE, PlausibelImportConstants.COL_6_ERFWERTE));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "Prüfwert untere Grenze", "Lower Limit"),
        Column.DOUBLE, PlausibelImportConstants.COL_7_PRUFWERTU));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "Prüfwert obere Grenze", "Upper Limit"),
        Column.DOUBLE, PlausibelImportConstants.COL_8_PRUFWERTO));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, true, "Prüfweise", "Review Method"),
        Column.STRING, PlausibelImportConstants.COL_9_PRUFWEISE));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, false, "Prüfstand"), Column.STRING,
        PlausibelImportConstants.COL_10_PRUEFSTAND));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, false, "Effekte"), Column.STRING,
        PlausibelImportConstants.COL_11_EFFEKTE));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, false, "Einzel"), Column.STRING,
        PlausibelImportConstants.COL_12_EINZEL));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, false, "Globale"), Column.STRING,
        PlausibelImportConstants.COL_13_ABKRIT));
    this.colList.addColumn(new Column(this.excel.getColNumberFromText(1, false, "Class"), Column.STRING,
        PlausibelImportConstants.COL_14_CLASS));

    this.grid = new ValueGrid(this.logger, this.excel, this.colList, 2);

    // BitSet for the output; 0=false not show invalid - 1=true show valid
    BitSet bs = new BitSet(2);
    bs.set(0, true);
    bs.set(1, true);
  }


  private File getFileFromPath() throws IOException {
    File file = null;

    file = new File(this.filePath);

    if (!file.exists()) {
      throw new IOException("Plausibel File " + this.filePath + " is not existing.");
    }

    return file;
  }

  /**
   * @return the colList
   */
  public ColumnList getColList() {
    return this.colList;
  }

  /**
   * ICDM-1892
   *
   * @return Set<String> of parameters in the file
   */
  public Set<String> getParamNames() {
    Set<String> paramNameSet = new HashSet<>();
    for (Entry<Integer, Row> entry : entrySet()) {

      String parameterName = entry.getValue().getValueAt(1).trim();// ICDM-2121
      paramNameSet.add(parameterName);
    }
    return paramNameSet;
  }

  /**
   * @return the functionName
   */
  public String getFunctionName() {
    return this.functionName;
  }

  public void close() throws IOException {
    this.excel.closeExcelFile();
  };
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cdrruleimporter.filerepresentation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.icdm.cdrruleimporter.plausibel.PlausibelImporter;
import com.bosch.caltool.icdm.excelimport.Column;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.ExcelFile;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.caltool.icdm.excelimport.ValueGrid;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * @author imi2si
 */
public class BasicFileRepresentation extends AbstractFileRepresentation implements IBasicFile {

  private final ColumnList colList;
  private final ExcelFile excel;

  public BasicFileRepresentation(final ExcelFile excel, final CDRDataProvider attrDataProvider,
      final boolean showValid, final boolean showInvalid) {
    // Reference to the column list
    super(attrDataProvider, showValid, showInvalid);
    this.colList = new ColumnList();
    this.excel = excel;

    // Add columns to the column list
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Appli"), Column.STRING,
        PlausibelImporter.COL_1_APPL));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Parameter"), Column.STRING, true,
        PlausibelImporter.COL_2_PARAM));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Kurzbeschreibung"), Column.STRING,
        PlausibelImporter.COL_3_KURZB));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Wie appliziert?"), Column.STRING,
        PlausibelImporter.COL_4_WIEAPPL));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "optionale Erläuterungen"), Column.STRING,
        PlausibelImporter.COL_5_OPTE));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Erfahrungswerte"), Column.DOUBLE,
        PlausibelImporter.COL_6_ERFWERTE));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Prüfwert untere Grenze"), Column.DOUBLE,
        PlausibelImporter.COL_7_PRUFWERTU));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Prüfwert obere Grenze"), Column.DOUBLE,
        PlausibelImporter.COL_8_PRUFWERTO));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, true, "Prüfweise"), Column.STRING,
        PlausibelImporter.COL_9_PRUFWEISE));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Prüfstand"), Column.STRING,
        PlausibelImporter.COL_10_PRUEFSTAND));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Effekte"), Column.STRING,
        PlausibelImporter.COL_11_EFFEKTE));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Einzel"), Column.STRING,
        PlausibelImporter.COL_12_EINZEL));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Globale"), Column.STRING,
        PlausibelImporter.COL_13_ABKRIT));
    this.colList.addColumn(new Column(excel.getColNumberFromText(1, false, "Class"), Column.STRING,
        PlausibelImporter.COL_14_CLASS));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Integer, Row> getGrid() {

    return (new ValueGrid(this.excel, this.colList, 2)).getGrid(super.bs);
  }


  @Override
  protected Map<String, CDRFuncParameter> getCDRFuncParameters(final Row row) {
    String parameterName = row.getValueAt(this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_B_PARAM));
    Map<String, CDRFuncParameter> allParams =
        ((CDRDataProvider) super.dataProvider).getCDRFuncParameters(parameterName);
    Map<String, CDRFuncParameter> relParams = new HashMap<>();

    for (Entry<String, CDRFuncParameter> param : allParams.entrySet()) {
      if (param.getValue().getName().equalsIgnoreCase(parameterName)) {
        relParams.put(param.getKey(), param.getValue());
      }
    }

    return relParams;
  }

  @Override
  protected CDRRule getCDRRuleFromRow(final Row row, final Entry<String, CDRFuncParameter> paramFunctions) {
    return new BasicExcelRowCDRRuleAdapter(row, paramFunctions);
  }
}

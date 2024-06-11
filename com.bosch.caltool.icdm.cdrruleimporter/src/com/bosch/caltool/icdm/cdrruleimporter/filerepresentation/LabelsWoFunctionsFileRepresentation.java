/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cdrruleimporter.filerepresentation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.icdm.excelimport.Column;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.ExcelFile;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.caltool.icdm.excelimport.ValueGrid;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * @author imi2si
 */
public class LabelsWoFunctionsFileRepresentation extends AbstractFileRepresentation implements ILabelsWoFunctionsFile {

  private final ColumnList colList;
  private final ExcelFile excel;
  private final int numberStartValueColumns;

  public LabelsWoFunctionsFileRepresentation(final ExcelFile excel, final CDRDataProvider attrDataProvider,
      final boolean showValid, final boolean showInvalid, final int numberStartValueColumns) {
    // Reference to the column list
    super(attrDataProvider, showValid, showInvalid);
    this.colList = new ColumnList();
    this.excel = excel;
    this.numberStartValueColumns = numberStartValueColumns;

    // Add columns to the column list
    this.colList.addColumn(new Column(excel.getColNumberFromText(0, true, "Parameter"), Column.STRING, true,
        ILabelsWoFunctionsFile.COL_B_PARAM));
    this.colList.addColumn(new Column(excel.getColNumberFromText(0, true, "Prüfwert"), Column.DOUBLE,
        ILabelsWoFunctionsFile.COL_AE_ERFWERTE));
    this.colList.addColumn(new Column(excel.getColNumberFromText(0, true, "Unterer Wert"), Column.DOUBLE,
        ILabelsWoFunctionsFile.COL_AC_PRUFWERTU));
    this.colList.addColumn(new Column(excel.getColNumberFromText(0, true, "Oberer Wert"), Column.DOUBLE,
        ILabelsWoFunctionsFile.COL_AD_PRUFWERTO));
    this.colList.addColumn(new Column(excel.getColNumberFromText(0, true, "Typ"), Column.STRING,
        ILabelsWoFunctionsFile.COL_AA_TYP));
    this.colList.addColumn(new Column(excel.getColNumberFromText(0, true, "Bezeichnung"), Column.STRING,
        ILabelsWoFunctionsFile.COL_AB_BEZEICHNUNG));

    for (int colNumber = 0; colNumber < numberStartValueColumns; colNumber++) {
      this.colList.addColumn(new Column(excel.getColNumberFromText(0, false, "Standard-Startwerte") + colNumber,
          Column.STRING, ILabelsWoFunctionsFile.COL_AG_START + String.valueOf(colNumber)));
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<Integer, Row> getGrid() {

    Map<Integer, Row> valueGrid = (new ValueGrid(this.excel, this.colList, 1)).getGrid(super.bs);
    Map<Integer, Row> valueGridMerged = new LinkedHashMap<>();
    Row prevRow = null;
    Row curRow = null;
    Column colParam = this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_B_PARAM);
    Column colPru = this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_AE_ERFWERTE);
    Column colPruU = this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_AC_PRUFWERTU);
    Column colPruO = this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_AD_PRUFWERTO);
    Column colBez = this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_AB_BEZEICHNUNG);
    Column colTyp = this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_AA_TYP);

    for (Entry<Integer, Row> entry : valueGrid.entrySet()) {
      curRow = entry.getValue();

      if ((prevRow != null) && prevRow.getValueAt(colParam).equals(curRow.getValueAt(colParam))) {
        for (int cols = 0; cols < this.numberStartValueColumns; cols++) {
          Column colStartValue =
              this.colList.getColumnByName(ILabelsWoFunctionsFile.COL_AG_START + String.valueOf(cols));
          String curValue = curRow.getValueAt(colStartValue);

          AtomicValuePhy prevAtomicValue = prevRow.getAtomicValuePhy(colStartValue);
          AtomicValuePhy curAtomicValue = curRow.getAtomicValuePhy(colStartValue);
          String newValue = null;

          if ((prevAtomicValue != null) && !prevAtomicValue.getSValue().isEmpty()) {
            if ((curAtomicValue != null) && !curAtomicValue.getSValue().isEmpty()) {
              newValue = curAtomicValue.getSValue();
            }
            prevRow.setAtomicValuePhy(colStartValue, new AtomicValuePhy(prevAtomicValue.getSValue() + " " + newValue));
          }
        }
      }
      else {
        valueGridMerged.put(entry.getKey(), curRow);
        prevRow = curRow;
      }
    }

    return valueGridMerged;
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
    return new LabelsWoFunctionsCDRRuleAdapter(row, paramFunctions, this.numberStartValueColumns);
  }
}

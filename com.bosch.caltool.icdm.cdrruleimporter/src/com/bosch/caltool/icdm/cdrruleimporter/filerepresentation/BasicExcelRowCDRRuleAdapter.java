/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cdrruleimporter.filerepresentation;

import java.math.BigDecimal;
import java.util.Map.Entry;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.icdm.excelimport.Column;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * @author imi2si
 */
public class BasicExcelRowCDRRuleAdapter extends CDRRule implements IBasicFile {

  private static final String LINE_SEP = System.getProperty("line.separator");

  private Row row;
  private ColumnList colList;
  private final Entry<String, CDRFuncParameter> cdrFuncParam;

  public BasicExcelRowCDRRuleAdapter(final Row row, final Entry<String, CDRFuncParameter> cdrFuncParam) {
    super();
    this.cdrFuncParam = cdrFuncParam;

    super.setParameterName(cdrFuncParam.getValue().getName());
    super.setLabelFunction(cdrFuncParam.getKey());
    super.setValueType(cdrFuncParam.getValue().getType());

    convertRowToRule(row);
  }

  public void convertRowToRule(final Row row) {
    this.row = row;
    this.colList = row.getColumnList();
    super.setHint(getHint());
    super.setRefValue(getRefValue());
    super.setLowerLimit(getLowerLimit());
    super.setUpperLimit(getUpperLimit());
  }

  @Override
  public String getHint() {
    StringBuffer hint = new StringBuffer("Loaded via PlausibeL Importer");
    hint.append(getColumnValue(IBasicFile.COL_4_WIEAPPL));
    hint.append(getColumnValue(IBasicFile.COL_5_OPTE));
    hint.append(getColumnValue(IBasicFile.COL_10_PRUEFSTAND));
    hint.append(getColumnValue(IBasicFile.COL_11_EFFEKTE));
    hint.append(getColumnValue(IBasicFile.COL_12_EINZEL));
    hint.append(getColumnValue(IBasicFile.COL_13_ABKRIT));

    /*
     * The values for Ref-Value, Upper Limit and Lower Limit may only be added if they are not numbers or the label type
     * is not map, curve and value If the returned value is null, the column is not filled, which means either the value
     * is not a string or the type is not as excepted. In this case the the value is appended to the hint
     */
    if (getRefValue() == null) {
      hint.append(getColumnValue(IBasicFile.COL_6_ERFWERTE));
    }

    if (getLowerLimit() == null) {
      hint.append(getColumnValue(IBasicFile.COL_7_PRUFWERTU));
    }

    if (getUpperLimit() == null) {
      hint.append(getColumnValue(IBasicFile.COL_8_PRUFWERTO));
    }

    return hint.toString();
    // Idee: Globale Variablen in Interfaces verlagern
  }

  @Override
  public BigDecimal getRefValue() {
    if (this.cdrFuncParam.getValue().getType().equalsIgnoreCase("VALUE") && isNumber(IBasicFile.COL_6_ERFWERTE)) {
      return new BigDecimal(getColumnValue(IBasicFile.COL_6_ERFWERTE));
    }
    return null;
  }

  @Override
  public BigDecimal getLowerLimit() {
    if (isNumber(IBasicFile.COL_7_PRUFWERTU)) {
      switch (this.cdrFuncParam.getValue().getType()) {
        case "VALUE":
        case "MAP":
        case "CURVE":
          return new BigDecimal(getColumnValue(IBasicFile.COL_7_PRUFWERTU));
        default:
          return null;
      }
    }
    return null;
  }

  @Override
  public BigDecimal getUpperLimit() {
    if (isNumber(IBasicFile.COL_8_PRUFWERTO)) {
      switch (this.cdrFuncParam.getValue().getType()) {
        case "VALUE":
        case "MAP":
        case "CURVE":
          return new BigDecimal(getColumnValue(IBasicFile.COL_8_PRUFWERTO));
        default:
          return null;
      }
    }
    return null;
  }

  @Override
  public String getReviewMethod() {

    switch (getColumnValue(IBasicFile.COL_9_PRUFWEISE)) {
      case "manuell":
        return "M";
      case "automatisch":
        return "A";
      default:
        return null;
    }
  }

  private boolean isEmpty(final Column column) {
    return this.row.getValueAt(column).isEmpty();
  }

  private boolean isNumber(final String columnName) {
    Column column = getColumn(columnName);
    AtomicValuePhy value = this.row.getAtomicValuePhy(column);

    return ((value.getDValue() != 0.0) || (value.getSValue().equals("0.0") && (value.getDValue() == 0.0))) ? true
        : false;
  }

  private Column getColumn(final String columnName) {
    return this.colList.getColumnByName(columnName);
  }

  private String getColumnValueAsHint(final String columnName) {
    Column column = getColumn(columnName);
    if (!isEmpty(column)) {
      return columnName + ": " + this.row.getValueAt(column) + LINE_SEP;
    }

    return null;
  }

  private String getColumnValue(final String columnName) {
    return this.row.getValueAt(getColumn(columnName));
  }
}

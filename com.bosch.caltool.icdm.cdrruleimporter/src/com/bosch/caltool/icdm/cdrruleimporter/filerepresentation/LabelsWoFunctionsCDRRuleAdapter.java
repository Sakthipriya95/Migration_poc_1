/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cdrruleimporter.filerepresentation;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Map.Entry;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.icdm.excelimport.Column;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.ssd.icdm.model.CDRRule;


public class LabelsWoFunctionsCDRRuleAdapter extends CDRRule implements ILabelsWoFunctionsFile {

  private static final String LINE_SEP = System.getProperty("line.separator");

  private Row row;
  private ColumnList colList;
  private final int numberStartValueColumns;
  private final Entry<String, CDRFuncParameter> cdrFuncParam;

  public LabelsWoFunctionsCDRRuleAdapter(final Row row, final Entry<String, CDRFuncParameter> cdrFuncParam,
      final int numberStartValueColumns) {
    super();
    this.cdrFuncParam = cdrFuncParam;
    this.numberStartValueColumns = numberStartValueColumns;

    super.setDcm2ssd(false);
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

  private int getNumberOfStartValueCols() {
    String curValue;
    String curValueSplit[];
    int numberOfRows = 0;

    for (int cols = 0; cols < this.numberStartValueColumns; cols++) {
      curValue = getColumnValue(ILabelsWoFunctionsFile.COL_AG_START + String.valueOf(cols));

      curValueSplit = curValue.split("\\s");

      if ((curValueSplit != null) && (curValueSplit.length > numberOfRows)) {
        numberOfRows = curValueSplit.length;
      }
    }

    return numberOfRows;
  }

  private int getMaxLengthStartValCol(final int colNumber) {
    String curValue;
    String curValueSplit[];
    int maxLength = 0;

    curValue = getColumnValue(ILabelsWoFunctionsFile.COL_AG_START + String.valueOf(colNumber));

    curValueSplit = curValue.split("\\s");

    for (String element : curValueSplit) {

      if (maxLength < element.length()) {
        maxLength = element.length();
      }
    }

    return maxLength;
  }

  public String getStartValues() {
    StringBuffer startValues = new StringBuffer();
    StringBuffer curLine = new StringBuffer();
    StringBuffer curRowValue = new StringBuffer();
    String curValue;
    String curValueSplit[];
    int numberOfRows = 0;
    boolean containsEmptyOnly;
    boolean curRowIsComment = false;

    numberOfRows = getNumberOfStartValueCols();

    for (int curRow = 0; curRow < numberOfRows; curRow++) {
      containsEmptyOnly = true;
      curLine = new StringBuffer();

      for (int cols = 0; cols < this.numberStartValueColumns; cols++) {
        curRowValue = new StringBuffer();
        curValue = getColumnValue(ILabelsWoFunctionsFile.COL_AG_START + String.valueOf(cols));
        curValueSplit = curValue.split("\\s");

        if ((curRow < curValueSplit.length) && !curValueSplit[curRow].isEmpty()) {

          // Check, if the current row starts with a number. Otherwise the row is handeled as comment
          if (cols == 0) {
            NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
            ParsePosition pos = new ParsePosition(0);

            Number num = nf.parse(curValueSplit[curRow], pos);

            if (pos.getIndex() == 0) {
              curRowIsComment = true;
            }
          }

          if (curRowIsComment) {
            if (!curValueSplit[curRow].equals("null")) {
              curLine.append(curValueSplit[curRow]);
              containsEmptyOnly = false;
            }
          }
          else {
            if (curValueSplit[curRow].equals("null")) {
              curRowValue.append(curValueSplit[curRow]);
            }
            else {
              containsEmptyOnly = false;
              curRowValue.append(curValueSplit[curRow] + " | ");
            }

            // Add the whitespace to ensure same length of cols
            for (int loop = 0; loop < ((getMaxLengthStartValCol(cols) - curRowValue.length()) + 2); loop++) {
              curRowValue.insert(0, " ");
            }

            curLine.append(curRowValue);
          }
        }
      }
      if (!containsEmptyOnly) {

        /*
         * Create a line with "-" as separator for the axis
         */
        if (curRow == 1) {
          for (int loop = 0; loop < 80; loop++) {
            startValues.append("-");
          }
          startValues.append(LINE_SEP);
        }

        startValues.append(curLine);
        if (!curRowIsComment) {
          startValues.append(LINE_SEP);
        }
        else {
          startValues.append(" ");
        }
      }
    }

    /*
     * if (!curValue.isEmpty()) { startValues.append(curValue + " | "); } }
     */

    return startValues.toString();
  }

  public String getPClass() {
    switch (getColumnValue(ILabelsWoFunctionsFile.COL_AA_TYP)) {
      case "Schraube":
        return "S";
      case "Niete":
        return "R";
      case "Nagel":
        return "N";
      default:
        return "-";
    }
  }

  public String getLabelLongName() {
    return getColumnValue(ILabelsWoFunctionsFile.COL_AB_BEZEICHNUNG);
  }

  @Override
  public String getHint() {
    StringBuffer hint = new StringBuffer("Loaded via PlausibeL Importer" + LINE_SEP);

    /*
     * The values for Ref-Value, Upper Limit and Lower Limit may only be added if they are not numbers or the label type
     * is not map, curve and value If the returned value is null, the column is not filled, which means either the value
     * is not a string or the type is not as excepted. In this case the the value is appended to the hint
     */
    if (getRefValue() == null) {
      hint.append(getColumnValueAsHint(ILabelsWoFunctionsFile.COL_AE_ERFWERTE));
    }

    if (getLowerLimit() == null) {
      hint.append(getColumnValueAsHint(ILabelsWoFunctionsFile.COL_AC_PRUFWERTU));
    }

    if (getUpperLimit() == null) {
      hint.append(getColumnValueAsHint(ILabelsWoFunctionsFile.COL_AD_PRUFWERTO));
    }

    String startValues = getStartValues();

    if (!startValues.isEmpty()) {
      hint.append("Start Values:" + LINE_SEP);
      hint.append(getStartValues());
    }


    String returnHint = hint.toString();

    /*
     * returnHint = returnHint.replace("\r", System.getProperty("line.separator")); returnHint =
     * returnHint.replace("\n", System.getProperty("line.separator"));
     */

    return returnHint;
  }

  @Override
  public BigDecimal getRefValue() {

    if (this.cdrFuncParam.getValue().getType().equalsIgnoreCase("VALUE") &&
        isNumber(ILabelsWoFunctionsFile.COL_AE_ERFWERTE) &&
        (getColumnValue(ILabelsWoFunctionsFile.COL_AE_ERFWERTE) != null)) {
      return new BigDecimal(getColumnValue(ILabelsWoFunctionsFile.COL_AE_ERFWERTE));
    }
    return null;

  }

  @Override
  public BigDecimal getLowerLimit() {
    if (isNumber(ILabelsWoFunctionsFile.COL_AC_PRUFWERTU)) {
      switch (this.cdrFuncParam.getValue().getType()) {
        case "VALUE":
        case "MAP":
        case "CURVE":
          return new BigDecimal(getColumnValue(ILabelsWoFunctionsFile.COL_AC_PRUFWERTU));
        default:
          return null;
      }
    }
    return null;
  }

  @Override
  public BigDecimal getUpperLimit() {
    if (isNumber(ILabelsWoFunctionsFile.COL_AD_PRUFWERTO)) {
      switch (this.cdrFuncParam.getValue().getType()) {
        case "VALUE":
        case "MAP":
        case "CURVE":
          return new BigDecimal(getColumnValue(ILabelsWoFunctionsFile.COL_AD_PRUFWERTO));
        default:
          return null;
      }
    }
    return null;
  }

  @Override
  public String getReviewMethod() {
    return "M";
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
      return columnName + ": " + this.row.getAtomicValuePhy(column).getSValue() + LINE_SEP;
    }

    return "";
  }

  private String getColumnValue(final String columnName) {
    return this.row.getValueAt(getColumn(columnName));
  }
}

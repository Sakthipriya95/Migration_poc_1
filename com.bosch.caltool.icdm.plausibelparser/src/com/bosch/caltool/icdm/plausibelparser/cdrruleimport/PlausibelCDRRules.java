/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.plausibelparser.cdrruleimport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.calmodel.caldataphy.AtomicValuePhy;
import com.bosch.caltool.icdm.excelimport.ColumnList;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * @author imi2si
 */
public class PlausibelCDRRules {

  private final PlausibelFile file;
  /**
   * map of parameter name and type
   */
  private final Map<String, String> paramNameType;


  /**
   * @param file PlausibelFile
   * @param paramNameType map of param name and type
   */
  public PlausibelCDRRules(final PlausibelFile file, final Map<String, String> paramNameType) {
    this.file = file;
    this.paramNameType = paramNameType;
  }

  public List<CDRRule> getRules() {

    ColumnList colList = this.file.getColList();
    List<CDRRule> rules = new ArrayList<>();

    for (Entry<Integer, Row> entry : this.file.entrySet()) {

      String parameterName = entry.getValue().getValueAt(1).trim();// icdm-2121

      CDRRule rule = new CDRRule();
      StringBuffer hintText = new StringBuffer();

      rule.setParameterName(parameterName);
      rule.setLabelFunction(this.file.getFunctionName());
      // set the parameter type from the map
      String pType = this.paramNameType.get(parameterName);

      if (pType == null) {// if the parameter does not have type info
        continue;
      }
      rule.setValueType(pType);

      /*
       * Store columns 3,4,9,10,11,12 as variable hintText. These columns have no representation in iCDM and are stored
       * as comment
       */

      fillHintText(colList, entry, hintText);

      AtomicValuePhy refValue =
          entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImportConstants.COL_6_ERFWERTE));

      if (pType.equals("VALUE") && isDoubleValue(refValue)) {
        // In column "Erfahrungswerte" there might by numbers and strings.
        rule.setRefValue(BigDecimal.valueOf(refValue.getDValue()));
      }
      else if (!pType.equals("VALUE") && isDoubleValue(refValue)) {
        if (!refValue.getSValue().isEmpty()) {
          appendLineBreak(hintText);
          hintText.append("!!! Reference value not considered because parameter is not of type VALUE: ")
              .append(refValue.getSValue());
        }
      }
      else {
        if (!refValue.getSValue().isEmpty()) {
          appendLineBreak(hintText);
          hintText.append("!!! Reference Value not recognized as single number. Please add a value manually: ")
              .append(refValue.getSValue());
        }
      }

      refValue = entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImportConstants.COL_7_PRUFWERTU));

      if (isDoubleValue(refValue)) {
        rule.setLowerLimit(BigDecimal.valueOf(refValue.getDValue()));
      }
      else {
        if (!refValue.getSValue().isEmpty()) {
          appendLineBreak(hintText);
          hintText.append("!!! Lower Limit not recognized as single number. Please add a value manually: ")
              .append(refValue.getSValue());
        }
      }

      refValue = entry.getValue().getAtomicValuePhy(colList.getColumnByName(PlausibelImportConstants.COL_8_PRUFWERTO));

      if (isDoubleValue(refValue)) {
        rule.setUpperLimit(BigDecimal.valueOf(refValue.getDValue()));
      }
      else {
        if (!refValue.getSValue().isEmpty()) {
          appendLineBreak(hintText);
          hintText.append("!!! Upper Limit not recognized as single number. Please add a value manually: ")
              .append(refValue.getSValue());
        }
      }

      rule.setHint(hintText.toString());
      setReviewMethod(colList, entry, rule);

      rules.add(rule);
    }
    return rules;
  }

  /**
   * @param colList
   * @param entry
   * @param rule
   */
  private void setReviewMethod(final ColumnList colList, final Entry<Integer, Row> entry, final CDRRule rule) {
    switch (entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_9_PRUFWEISE))) {
      case "manuell":
      case "Manual":
        rule.setReviewMethod("M");
        break;
      case "automatisch":
      case "Automatic":
        rule.setReviewMethod("A");
        break;
      default:
        rule.setReviewMethod("");
    }
  }

  /**
   * @param colList
   * @param entry
   * @param hintText
   */
  private void fillHintText(final ColumnList colList, final Entry<Integer, Row> entry, final StringBuffer hintText) {
    if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_5_OPTE)).equals("")) {
      hintText.append("Optional Hints: ")
          .append(entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_5_OPTE)));
    }


    if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_10_PRUEFSTAND)).equals("")) {
      appendLineBreak(hintText);
      hintText.append("Testbenchs: ")
          .append(entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_10_PRUEFSTAND)));
    }

    if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_11_EFFEKTE)).equals("")) {
      appendLineBreak(hintText);
      hintText.append("Effects: ")
          .append(entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_11_EFFEKTE)));
    }

    if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_12_EINZEL)).equals("")) {
      appendLineBreak(hintText);
      hintText.append("Single Stop Condition: ")
          .append(entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_12_EINZEL)));
    }

    if (!entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_13_ABKRIT)).equals("")) {
      appendLineBreak(hintText);
      hintText.append("Global Stop Condition: ")
          .append(entry.getValue().getValueAt(colList.getColumnByName(PlausibelImportConstants.COL_13_ABKRIT)));
    }
  }

  private boolean isDoubleValue(final AtomicValuePhy atomicValue) {
    final String strValue = atomicValue.getSValue();
    final BigDecimal dblValue = BigDecimal.valueOf(atomicValue.getDValue());
    final BigDecimal zero = BigDecimal.valueOf(0.0);

    return (!zero.equals(dblValue)) || (("0.0").equals(strValue) && (zero.equals(dblValue)));

  }

  private void appendLineBreak(final StringBuffer text) {
    if (text.length() > 0) {
      text.append(System.getProperty("line.separator"));
    }
  }
}

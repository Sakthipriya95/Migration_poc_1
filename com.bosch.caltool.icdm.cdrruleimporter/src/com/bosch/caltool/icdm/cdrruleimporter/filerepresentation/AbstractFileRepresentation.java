/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.cdrruleimporter.filerepresentation;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.excelimport.Row;
import com.bosch.ssd.icdm.model.CDRRule;


public abstract class AbstractFileRepresentation {

  protected AbstractDataProvider dataProvider;
  protected BitSet bs;

  public AbstractFileRepresentation(final AbstractDataProvider dataProvider, final boolean showValid,
      final boolean showInvalid) {
    this.dataProvider = dataProvider;

    // BitSet for the output; 0=false not show invalid - 1=true show valid
    this.bs = new BitSet();
    this.bs.set(0, showValid);
    this.bs.set(1, showInvalid);
  };

  public List<CDRRule> getCDRRules(final boolean showValid, final boolean showInvalid) {
    List<CDRRule> cdrRules = new ArrayList<>();

    for (Entry<Integer, Row> entry : getGrid().entrySet()) {
      // Get Key and Function-Parameter Relation
      Row row = entry.getValue();
      Map<String, CDRFuncParameter> parameterFunctions = getCDRFuncParameters(row);

      // Add the rule to the Arraylist
      for (Entry<String, CDRFuncParameter> paramFunctions : parameterFunctions.entrySet()) {
        cdrRules.add(getCDRRuleFromRow(row, paramFunctions));
      }
    }
    return cdrRules;
  }

  public abstract Map<Integer, Row> getGrid();

  protected abstract Map<String, CDRFuncParameter> getCDRFuncParameters(Row row);

  protected abstract CDRRule getCDRRuleFromRow(Row row, Entry<String, CDRFuncParameter> paramFunctions);
}
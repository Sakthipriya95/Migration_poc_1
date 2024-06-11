/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.nattable.AbstractNatInputToColumnConverter;


/**
 * @author jvi6cob
 */
public class RuleNatInputToColumnConverter extends AbstractNatInputToColumnConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getColumnValue(final Object evaluateObj, final int colIndex) {
    Object result = null;
    if (evaluateObj instanceof RuleDefinition) {
      result = getRuleDetails((RuleDefinition) evaluateObj, colIndex);
    }
    return result;
  }

  /**
   * @param evaluateObj
   * @param colIndex
   * @return
   */
  private Object getRuleDetails(final RuleDefinition ruleDefn, final int colIndex) {
    Object result = null;
    switch (colIndex) {
      case 0:
        result = ruleDefn.getLowerLimit();
        break;
      case 1:
        result = ruleDefn.getUpperLimit();
        break;
      case 2:
        result = ruleDefn.getBitWise();
        break;
      case 3:
        result = ruleDefn.getRefValueDisplayString();
        break;
      case 4:
        result = caseRefVal(ruleDefn);
        break;
      case 5:
        result = (null == ruleDefn.getUnit()) ? ApicConstants.EMPTY_STRING : ruleDefn.getUnit();
        break;
      case 6:
        result = caseReadyForSeries(ruleDefn, result);
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * @param ruleDefn
   * @param result
   * @return
   */
  private Object caseReadyForSeries(final RuleDefinition ruleDefn, Object result) {
    if (ruleDefn.getReviewMethod() != null) {
      if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(ruleDefn.getReviewMethod())) {
        result = ApicConstants.READY_FOR_SERIES.YES.uiType;
      }
      else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(ruleDefn.getReviewMethod())) {
        result = ApicConstants.READY_FOR_SERIES.NO.uiType;
      }
    }
    return result;
  }

  /**
   * @param ruleDefn
   * @return
   */
  private Object caseRefVal(final RuleDefinition ruleDefn) {
    Object result;
    if (ruleDefn.getRefValueDisplayString().isEmpty()) {
      result = "";
    }
    else {
      result = ruleDefn.isExactMatch() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;
    }
    return result;
  }
}

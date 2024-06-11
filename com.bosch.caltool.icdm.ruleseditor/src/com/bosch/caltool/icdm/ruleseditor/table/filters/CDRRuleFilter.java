/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;


/**
 * @author mkl2cob type filter for cdr rule
 */
public class CDRRuleFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) element;
      if ((rule.getLowerLimit() != null) && matchText(rule.getLowerLimit().toString())) {
        return true;
      }
      if ((rule.getUpperLimit() != null) && matchText(rule.getUpperLimit().toString())) {
        return true;
      }
      if (matchText(rule.getBitWiseRule())) {
        return true;
      }
      if (matchText(rule.getRefValueDispString())) {
        return true;
      }
      if (matchText(rule.getReviewMethod())) {
        return true;
      }
      if (matchText(rule.getUnit())) {
        return true;
      }

      // Icdm -1188 - Filter for new Columns
      if (matchText(convertBooleanToStr(rule)) && ((ApicUtil.compare(rule.getHint(), "Dummy Rule Created")) != 0)) {
        return true;
      }

      if (matchText(READY_FOR_SERIES.getType(rule.getReviewMethod()).getUiType()) &&
          ((ApicUtil.compare(rule.getHint(), "Dummy Rule Created")) != 0)) {
        return true;
      }
      if (matchText(rule.getParameterName())) {
        return true;
      }
    }
    return false;
  }


  /**
   * @param rule
   * @return Yes if the the rule has Exact match to ref Value
   */
  private String convertBooleanToStr(final ReviewRule rule) {
    if (rule.isDcm2ssd()) {
      return ApicConstants.USED_YES_DISPLAY;
    }
    return ApicConstants.USED_NO_DISPLAY;

  }
}

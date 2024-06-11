/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.client.bo.cdr.RuleDefinition;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;


/**
 * @author mkl2cob type filter for cdr rule
 */
public class CDRRuleDefFilter extends AbstractViewerFilter {

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof RuleDefinition) {
      RuleDefinition rule = (RuleDefinition) element;
      if ((rule.getLowerLimit() != null) && matchText(rule.getLowerLimit().toString())) {
        return true;
      }
      if ((rule.getUpperLimit() != null) && matchText(rule.getUpperLimit().toString())) {
        return true;
      }
      if (matchText(rule.getRefValueDisplayString())) {
        return true;
      }
      if (matchText(rule.getReviewMethod())) {
        return true;
      }
      if ((rule.getBitWise() != null) && matchText(rule.getBitWise())) {
        return true;
      }
      if (matchText(rule.getUnit())) {
        return true;
      }
      if (matchText(rule.isExactMatch() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO)) {
        return true;
      }

    }
    return false;
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.table.filters;

import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;


/**
 * This class is for the toolbar filters in compare rules wizard page
 *
 * @author MKL2COB
 */
public class CompareParamRulesToolBarFilter extends AbstractViewerFilter {


  /**
   * initially parameter with existing rule should be displayed
   */
  private boolean ruleExist = true;
  /**
   * initially parameter with no existing rule should be displayed
   */
  private boolean noRuleExist = true;

  /**
   * Constructor
   */
  public CompareParamRulesToolBarFilter() {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    setFilterText(filterText, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {
    if (element instanceof CalDataImportComparisonModel) {
      CalDataImportComparisonModel caldataimport = (CalDataImportComparisonModel) element;
      if (!this.ruleExist && (caldataimport.getOldRule() != null)) {
        // if rule exist button is not pressed and the obj has a rule existing
        return false;
      }
      if (!this.noRuleExist && (caldataimport.getOldRule() == null)) {
        // if no rule exist button is not pressed and the obj has no rule existing
        return false;
      }
    }
    return true;
  }

  /**
   * @param selection button selection
   */
  public void setRuleExist(final boolean selection) {
    this.ruleExist = selection;
  }

  /**
   * @param selection
   */
  public void setNoRuleExist(final boolean selection) {
    this.noRuleExist = selection;
  }

}

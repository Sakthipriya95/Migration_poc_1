/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.model.cdr.ConfigBasedParam;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ruleseditor.utils.RuleEditorConstants;


/**
 * @author dmo5cob
 */
public class ConfigParamToolBarFilters extends AbstractViewerFilter {

  /**
   * Parameter included in serach
   */
  private boolean includedInSearch = true;
  /**
   * Parameter not included in serach
   */
  private boolean notIncludedInSearch = true;
  /**
   * defaultRule search
   */
  private boolean defaultRuleSearch = true;
  /**
   * non defaultRule search
   */
  private boolean nonDefaultRuleSearch = true;
  /**
   * rule exists search
   */
  private boolean ruleExistsSearch = true;
  /**
   * no rule exists search
   */
  private boolean noRuleExistsSearch = true;

  /**
   * Constructor
   */
  public ConfigParamToolBarFilters() {
    super();
    setFilterText(AbstractViewerFilter.DUMMY_FILTER_TXT, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFilterText(final String filterText) {
    // ICDM-278
    setFilterText(filterText, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean selectElement(final Object element) {

    if (element instanceof ConfigBasedParam) {
      return ifConfigParamInstance(element);
    }
    else if (element instanceof ReviewRule) {
      return ifReviewRuleInstance(element);
    }
    return true;
  }

  /**
   * @param element
   */
  private boolean ifConfigParamInstance(final Object element) {
    final ConfigBasedParam param = (ConfigBasedParam) element;

    if (!checkIncluded(param)) {
      return false;
    }
    if (!checkNotIncluded(param)) {
      return false;
    }
    return true;
  }

  /**
   * @param element
   */
  private boolean ifReviewRuleInstance(final Object element) {
    final ReviewRule rule = (ReviewRule) element;

    if (!checkDefaultRule(rule)) {
      return false;
    }
    if (!checkNonDefaultRule(rule)) {
      return false;
    }
    if (!checkRuleExists(rule)) {
      return false;
    }
    if (!checkNoRuleExists(rule)) {
      return false;
    }
    return true;
  }

  /**
   * @param param ConfigBasedParam
   * @return
   */
  private boolean checkNotIncluded(final ConfigBasedParam param) {
    if (!this.notIncludedInSearch && !param.isChecked()) {
      return false;
    }
    return true;
  }

  /**
   * @param param
   * @return
   */
  private boolean checkIncluded(final ConfigBasedParam param) {

    if (!this.includedInSearch && param.isChecked()) {
      return false;
    }
    return true;
  }

  /**
   * @param param
   * @param param
   * @return
   */
  private boolean checkDefaultRule(final ReviewRule rule) {
    if (!this.defaultRuleSearch && ReviewRuleUtil.isDefaultRule(rule) &&
        ((null != rule.getHint()) && !rule.getHint().equals(RuleEditorConstants.DUMMY_RULE_HINT))) {
      return false;
    }
    return true;
  }

  /**
   * @param param
   * @param param
   * @return
   */
  private boolean checkNonDefaultRule(final ReviewRule rule) {
    if (!this.nonDefaultRuleSearch && (!rule.getDependencyList().isEmpty() || ((null == rule.getHint()) ||
        ((null != rule.getHint()) && !rule.getHint().equals(RuleEditorConstants.DUMMY_RULE_HINT))))) {
      return false;
    }
    return true;
  }

  /**
   * @param param
   * @return
   */
  private boolean checkRuleExists(final ReviewRule rule) {
    if (!this.ruleExistsSearch &&
        ((null != rule.getHint()) && !rule.getHint().equals(RuleEditorConstants.DUMMY_RULE_HINT))) {
      return false;
    }
    return true;
  }

  /**
   * @param param
   * @return
   */
  private boolean checkNoRuleExists(final ReviewRule rule) {
    if (!this.noRuleExistsSearch &&
        ((null != rule.getHint()) && rule.getHint().equals(RuleEditorConstants.DUMMY_RULE_HINT))) {
      return false;
    }
    return true;
  }

  /**
   * @param includedInSearch the includedInSearch to set
   */
  public void setIncludedInSearch(final boolean includedInSearch) {
    this.includedInSearch = includedInSearch;
  }

  /**
   * @param notIncludedInSearch the notIncludedInSearch to set
   */
  public void setNotIncludedInSearch(final boolean notIncludedInSearch) {
    this.notIncludedInSearch = notIncludedInSearch;
  }


  /**
   * @param defaultRuleSearch the defaultRuleSearch to set
   */
  public void setDefaultRuleSearch(final boolean defaultRuleSearch) {
    this.defaultRuleSearch = defaultRuleSearch;
  }


  /**
   * @param nonDefaultRuleSearch the nonDefaultRuleSearch to set
   */
  public void setNonDefaultRuleSearch(final boolean nonDefaultRuleSearch) {
    this.nonDefaultRuleSearch = nonDefaultRuleSearch;
  }


  /**
   * @param ruleExistsSearch the ruleExistsSearch to set
   */
  public void setRuleExistsSearch(final boolean ruleExistsSearch) {
    this.ruleExistsSearch = ruleExistsSearch;
  }


  /**
   * @param noRuleExistsSearch the noRuleExistsSearch to set
   */
  public void setNoRuleExistsSearch(final boolean noRuleExistsSearch) {
    this.noRuleExistsSearch = noRuleExistsSearch;
  }

}

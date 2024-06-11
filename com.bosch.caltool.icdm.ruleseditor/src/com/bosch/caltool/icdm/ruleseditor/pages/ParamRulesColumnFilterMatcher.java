/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import java.util.regex.Pattern;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;

import ca.odell.glazedlists.matchers.Matcher;

/**
 * @author bru2cob
 */
public class ParamRulesColumnFilterMatcher<E> implements Matcher<E> {

  /**
   * Pattern
   */
  private Pattern pattern1;
  /**
   * Pattern to search
   */
  private boolean pattern2Search;
  /**
   * Pattern
   */
  private Pattern pattern2;
  private final ParameterDataProvider parameterDataProvider;

  /**
   * @param parameterDataProvider
   */
  public ParamRulesColumnFilterMatcher(final ParameterDataProvider parameterDataProvider) {
    this.parameterDataProvider = parameterDataProvider;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matches(final E element) {
    if (element instanceof DefaultRuleDefinition) {
      DefaultRuleDefinition defaultRule = (DefaultRuleDefinition) element;
      if (matchText(defaultRule.getDescription())) {
        return true;
      }
      ReviewRule rule = defaultRule.getReviewRule();
      IParameter param = this.parameterDataProvider.getParameter(rule.getParameterName());
      if (paramMatcher(param)) {
        return true;
      }
      return checkRuleDetails(rule);
    }
    if (element instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) element;
      IParameter param = this.parameterDataProvider.getParameter(rule.getParameterName());
      if (paramMatcher(param)) {
        return true;
      }
      return checkRuleDetails(rule);
    }
    if (element instanceof IParameter) {
      if (element instanceof RuleSetParameter) {

        RuleSetParameter param = (RuleSetParameter) element;
        if (paramMatcher(param)) {
          return true;
        }
        if ((CommonUtils.isNotNull(param.getParamType())) && matchText(param.getParamType())) {
          return true;
        }
        if ((CommonUtils.isNotNull(param.getParamResp())) && matchText(param.getParamResp())) {
          return true;
        }
        if ((CommonUtils.isNotNull(param.getSysElement())) && matchText(param.getSysElement())) {
          return true;
        }
        if ((CommonUtils.isNotNull(param.getHwComponent())) && matchText(param.getHwComponent())) {
          return true;
        }
        if (this.parameterDataProvider.getReviewRule(param) != null) {
          return checkRuleDetails(this.parameterDataProvider.getReviewRule(param));
        }
      }
      else {
        IParameter param = (IParameter) element;
        if (paramMatcher(param)) {
          return true;
        }
        if (this.parameterDataProvider.getReviewRule(param) != null) {
          return checkRuleDetails(this.parameterDataProvider.getReviewRule(param));
        }
      }

    }

    return false;
  }

  /**
   * @param param
   */
  private boolean paramMatcher(final IParameter param) {

    boolean nameMatch = matchText(param.getName()) || matchText(param.getLongName());
    return nameMatch || matchText(param.getpClassText()) ||
        matchText(this.parameterDataProvider.getCodeWordText(param));
  }

  /**
   * @param rule
   */
  private boolean checkRuleDetails(final ReviewRule rule) {
    boolean result = false;
    if (matchText(this.parameterDataProvider.getAttrValString(rule))) {
      result = true;
    }

    if ((rule.getLowerLimit() != null) && matchText(rule.getLowerLimit().toString())) {
      result = true;
    }
    if ((rule.getUpperLimit() != null) && matchText(rule.getUpperLimit().toString())) {
      result = true;
    }
    if (matchText(rule.getBitWiseRule())) {
      result = true;
    }
    if (matchText(rule.getRefValueDispString())) {
      result = true;
    }
    if (matchText(rule.isDcm2ssd() ? CommonUIConstants.EXACT_MATCH_YES : CommonUIConstants.EXACT_MATCH_NO)) {
      result = true;
    }
    if ((rule.getUnit() != null) && matchText(rule.getUnit())) {
      result = true;
    }
    if (matchText(ReviewRuleUtil.getReadyForSeriesUIVal(rule))) {
      result = true;
    }
    if ((rule.getHint() != null) && matchText(rule.getHint().toString())) {
      result = true;
    }
    return result;
  }


  /**
   * Set filter text
   *
   * @param textToFilter filter text
   * @param setPattern pattern
   */
  public void setFilterText(final String textToFilter, final boolean setPattern) {

    if ((textToFilter == null) || "".equals(textToFilter) || !setPattern) {
      return;
    }

    String regExp = textToFilter.replace("\n", "").replace("\r", "");

    final String[] subTexts = regExp.split("\\*");

    // If filter text contains only *, it is equivalent to not providing filter condition.
    if (subTexts.length == 0) {
      return;
    }

    // All special characters in the filter text except '*' are escaped
    final StringBuilder sbRegExp = new StringBuilder();
    for (String curString : subTexts) {
      if (!"".equals(curString)) {
        sbRegExp.append(".*");
        sbRegExp.append(Pattern.quote(curString));
      }
      sbRegExp.append(".*");
    }
    regExp = sbRegExp.toString();

    this.pattern1 = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);

    // Word level matching is not required if filter text starts with '*'.
    if (regExp.startsWith(".*")) {
      this.pattern2Search = false;
    }
    else {
      this.pattern2Search = true;
      this.pattern2 = Pattern.compile(".* " + regExp, Pattern.CASE_INSENSITIVE);
    }

  }

  /**
   * Match text
   *
   * @param text text
   * @return true if match
   */
  private final boolean matchText(String text) {
    if (text == null) {
      return false;
    }
    text = text.replace("\n", "").replace("\r", "");
    final java.util.regex.Matcher matcher1 = this.pattern1.matcher(text);
    if (matcher1.matches()) {
      return true;
    }

    // Word level matching is not required if filter text starts with '*'.
    if (!this.pattern2Search) {
      return false;
    }

    final java.util.regex.Matcher matcher2 = this.pattern2.matcher(text);

    return matcher2.matches();
  }
}

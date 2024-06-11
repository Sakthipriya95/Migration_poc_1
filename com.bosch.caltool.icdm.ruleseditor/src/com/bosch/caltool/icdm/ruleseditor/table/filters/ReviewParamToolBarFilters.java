/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.table.filters;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.filters.AbstractViewerFilter;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;

import ca.odell.glazedlists.matchers.Matcher;


/**
 * @author rgo7cob Class For List Page Tool bar Filters
 */
// ICDM-500
public class ReviewParamToolBarFilters extends AbstractViewerFilter {

  // ICDM-2439
  /**
   * Compliance Flag initially True since the tool bar action is checked
   */
  private boolean complianceFlag = true;
  /**
   * Non Compliance Flag initially True since the tool bar action is checked
   */
  private boolean nonComplianceFlag = true;
  /**
   * Black List Flag initially True since the tool bar action is checked
   */
  private boolean blackListFlag = true;
  /**
   * Non Black List Flag initially True since the tool bar action is checked
   */
  private boolean nonBlackListFlag = true;
  /**
   * QSSD Flag initially True since the tool bar action is checked
   */
  private boolean qSSDFlag = true;
  /**
   * Non QSSD Flag initially True since the tool bar action is checked
   */
  private boolean nonQSSDFlag = true;
  /**
   * Curve Flag initially True since the tool bar action is checked
   */
  private boolean curveFlag = true;
  /**
   * Value Flag initially True since the tool bar action is checked
   */
  private boolean valueFlag = true;
  /**
   * Value Block Flag initially True since the tool bar action is checked
   */

  private boolean valueBlkFlag = true;
  /**
   * Map Flag initially True since the tool bar action is checked
   */
  private boolean mapFlag = true;
  /**
   * Ascii Flag initially True since the tool bar action is checked
   */
  private boolean asciiFlag = true;
  /**
   * Axis Point Flag initially True since the tool bar action is checked
   */
  private boolean axisFlag = true;
  /**
   * Screw Flag initially True since the tool bar action is checked
   */
  private boolean screwFlag = true;
  /**
   * Nail Flag initially True since the tool bar action is checked
   */
  private boolean nailFlag = true;
  /**
   * Rivet Flag initially True since the tool bar action is checked
   */
  private boolean rivetFlag = true;
  /**
   * Undefined Class Flag initially True since the tool bar action is checked
   */
  private boolean noClassFlag = true;
  /**
   * Code Yes Flag initially True since the tool bar action is checked
   */
  private boolean yesFlag = true;
  /**
   * Code No Flag initially True since the tool bar action is checked
   */
  private boolean noFlag = true;
  /**
   * Review Manual Flag initially True since the tool bar action is checked
   */
  private boolean manualFlag = true;
  /**
   * Review Auto initially True since the tool bar action is checked
   */
  private boolean autoFlag = true;
  /**
   * Filter For the Undefined - Review Type Icdm-654 Review -- Undefined True since the tool bar action is checked
   */
  private boolean rvwUndefined = true;
  // iCDM-650
  /**
   * Rule complete
   */
  private boolean ruleComplete = true;
  /**
   * Rule NOT complete
   */
  private boolean ruleInComplete = true;
  /**
   * Parameter with dependency
   */
  private boolean paramWithDepn = true;
  /**
   * Parameter without dependency
   */
  private boolean paramWithOutDepn = true;
  /**
   * no rule exists search
   */
  private boolean noRuleExistsSearch = true;

  private final Map<String, Boolean> paramVisiblilityMap = new HashMap<>();
  private final ParameterDataProvider parameterDataProvider;
  private IParameter parameter;

  /**
   * @param parameterDataProvider parameterDataProvider
   */
  public ReviewParamToolBarFilters(final ParameterDataProvider parameterDataProvider) {
    super();
    this.parameterDataProvider = parameterDataProvider;
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

    if (element instanceof IParameter) {
      this.parameter = (IParameter) element;

      if (!checkDataType(this.parameter)) {
        return false;
      }
      if (!checkParamClass(this.parameter)) {
        return false;
      }
      if (!checkCodeWord(this.parameter)) {
        return false;
      }
      if (!checkReviewType(this.parameterDataProvider.getReviewRule(this.parameter))) {
        return false;
      }
      if (!checkRuleComplete(this.parameterDataProvider.getReviewRule(this.parameter))) {
        return false;
      }
      if (!checkParamDepnExists(this.parameter)) {
        return false;
      }
      if (!checkNoParamDepnExists(this.parameter)) {
        return false;
      }
      if (!checkNoRuleExists(this.parameter)) {
        return false;
      }
      if (!checkIsCompliance(this.parameter)) {
        // ICDM-2439
        return false;
      }
      if (!checkIsNonCompliance(this.parameter)) {
        // ICDM-2439
        return false;
      }
      if (!checkIsBlackList(this.parameter)) {
        return false;
      }
      if (!checkIsNonBlackList(this.parameter)) {
        return false;
      }
      if (!checkIsQSSDParam(this.parameter)) {
        return false;
      }
      if (!checkIsNonQSSDParam(this.parameter)) {
        return false;
      }
    }
    else if (element instanceof ReviewRule) {
      ReviewRule rule = (ReviewRule) element;
      this.parameter = this.parameterDataProvider.getParameter(rule.getParameterName());
      if (!checkReviewType(rule)) {
        return false;
      }
      if (this.parameterDataProvider.hasDependency(this.parameter) && (!checkDepRuleComplete(rule))) {
        return false;
      }
      if (!this.parameterDataProvider.hasDependency(this.parameter) && !checkRuleComplete(rule)) {
        return false;
      }
      if (!this.paramVisiblilityMap.isEmpty()) {
        return (this.paramVisiblilityMap.get(rule.getParameterName()) != null) &&
            this.paramVisiblilityMap.get(rule.getParameterName());
      }
    }
    else if (element instanceof DefaultRuleDefinition) {
      DefaultRuleDefinition defaultRule = (DefaultRuleDefinition) element;
      if (!checkReviewType(defaultRule.getReviewRule())) {
        return false;
      }
      if (!checkRuleComplete(defaultRule.getReviewRule())) {
        return false;
      }
      if (!this.paramVisiblilityMap.isEmpty()) {
        return this.paramVisiblilityMap.get(defaultRule.getReviewRule().getParameterName()) == null &&
            this.paramVisiblilityMap.get(defaultRule.getReviewRule().getParameterName());
      }
    }
    return true;

  }

  /**
   * @param param
   * @return
   */
  private boolean checkIsNonQSSDParam(final IParameter param) {
    /**
     * Non QSSD Action Unchecked then do not show non QSSD Param
     */
    return !(!this.nonQSSDFlag && !param.isQssdFlag());
  }

  /**
   * @param param IParameter
   * @return boolean
   */
  private boolean checkIsQSSDParam(final IParameter param) {
    /**
     * QSSD Action Unchecked then do not show QSSD Param
     */
    return !(!this.qSSDFlag && param.isQssdFlag());
  }

  // ICDM-2439
  /**
   * @param param
   * @return
   */
  private boolean checkIsNonCompliance(final IParameter param) {
    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!this.nonComplianceFlag && !this.parameterDataProvider.isComplianceParam(param)) {
      return false;
    }
    return true;
  }

  // ICDM-2439
  /**
   * @param param
   * @return
   */
  private boolean checkIsCompliance(final IParameter param) {
    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!this.complianceFlag && this.parameterDataProvider.isComplianceParam(param)) {
      return false;
    }
    return true;
  }


  /**
   * @return boolean
   */
  public boolean isqSSDFlag() {
    return this.qSSDFlag;
  }


  /**
   * @param qSSDFlag boolean
   */
  public void setqSSDFlag(final boolean qSSDFlag) {
    this.qSSDFlag = qSSDFlag;
  }


  /**
   * @return boolean non QSSD Flag
   */
  public boolean isNonQSSDFlag() {
    return this.nonQSSDFlag;
  }


  /**
   * @param nonQSSDFlag boolean
   */
  public void setNonQSSDFlag(final boolean nonQSSDFlag) {
    this.nonQSSDFlag = nonQSSDFlag;
  }

  /**
   * @param param
   * @return
   */
  private boolean checkIsNonBlackList(final IParameter param) {
    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!this.nonBlackListFlag && !param.isBlackList()) {
      return false;
    }
    return true;
  }


  /**
   * @param param
   * @return
   */
  private boolean checkIsBlackList(final IParameter param) {
    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!this.blackListFlag && param.isBlackList()) {
      return false;
    }
    return true;
  }

  /**
   * Check Rule exists or not - // iCDM-650
   *
   * @param param param
   */
  private boolean checkRuleComplete(final ReviewRule rule) {
    /**
     * Rule complete action unchecked then do not show the rule
     */
    if (null != rule) {
      if (!this.ruleComplete && (this.parameterDataProvider.isRuleComplete(rule))) {
        return false;
      }
      /**
       * Rule incomplete action unchecked then do not show such rules
       */
      if (!this.ruleInComplete && (!this.parameterDataProvider.isRuleComplete(rule))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check Dependency Rule exists or not - // iCDM-650
   *
   * @param rule ReviewRule
   */
  private boolean checkDepRuleComplete(final ReviewRule rule) {
    int dependencyAttrsetSize = CommonUtils.isNotNull(this.parameterDataProvider.getParamAttrs(this.parameter))
        ? this.parameterDataProvider.getParamAttrs(this.parameter).size() : -1;
    /**
     * Rule complete action unchecked then do not show the complete dependency rule
     */
    if (!this.ruleComplete && ((dependencyAttrsetSize == rule.getDependencyList().size()) &&
        this.parameterDataProvider.isRuleComplete(rule))) {

      return false;
    }
    /**
     * Rule incomplete action unchecked then do not show incomplete dependency rules
     */
    if (!this.ruleInComplete && ((dependencyAttrsetSize != rule.getDependencyList().size()) ||
        !this.parameterDataProvider.isRuleComplete(rule))) {
      return false;
    }

    return true;
  }


  /**
   * Check review type
   *
   * @param rule parameter
   */
  private boolean checkReviewType(final ReviewRule rule) {


    if (rule != null) {
      /**
       * Review Manual Action Unchecked then do not show Manual Review
       */
      if (!this.manualFlag && (ApicConstants.READY_FOR_SERIES.NO.dbType == rule.getReviewMethod())) {
        return false;
      }
      /**
       * Review Automatic Action Unchecked then do not show Automatic Review
       */
      if (!this.autoFlag && (ApicConstants.READY_FOR_SERIES.YES.dbType == rule.getReviewMethod())) {
        return false;
      }

      /**
       * Filter For the Undefined - Review Type Icdm-654 Review Undefined Action Unchecked then do not show Automatic
       * Review
       */
      if (!this.rvwUndefined && (null == rule.getReviewMethod())) {
        return false;
      }

    }
    return true;
  }


  /**
   * Check ParamDepn exists or not
   *
   * @param param param
   */
  private boolean checkParamDepnExists(final IParameter param) {

    if (!this.paramWithDepn && (this.parameterDataProvider.hasDependency(param))) {
      return false;
    }

    return true;
  }

  /**
   * Check Param Depn exists or not
   *
   * @param param param
   */
  private boolean checkNoParamDepnExists(final IParameter param) {

    if (!this.paramWithOutDepn && (!this.parameterDataProvider.hasDependency(param))) {
      return false;
    }

    return true;
  }

  /**
   * Check Rule exists or not
   *
   * @param param param
   */
  private boolean checkNoRuleExists(final IParameter param) {

    if (!this.noRuleExistsSearch && (this.parameterDataProvider.getRuleList(param) == null)) {
      return false;
    }

    return true;
  }

  /**
   * Check code word flag
   *
   * @param param parameter
   */
  private boolean checkCodeWord(final IParameter param) {
    /**
     * Code Yes Action Unchecked then do not show Code Yes
     */
    if (!this.yesFlag && param.getCodeWord().equals(ApicConstants.CODE_YES)) {
      return false;
    }
    /**
     * Code No Action Unchecked then do not show Code No
     */
    if (!this.noFlag && param.getCodeWord().equals(ApicConstants.CODE_NO)) {
      return false;
    }
    return true;
  }

  /**
   * Check the parameter's class
   *
   * @param param parameter
   */
  private boolean checkParamClass(final IParameter param) {
    /**
     * Screw Action Unchecked then do not show Screw Class
     */
    if (!this.screwFlag && (ParameterClass.SCREW == this.parameterDataProvider.getPclass(param))) {
      return false;
    }
    /**
     * Nail Action Unchecked then do not show Nail Class
     */
    if (!this.nailFlag && (ParameterClass.NAIL == this.parameterDataProvider.getPclass(param))) {
      return false;
    }
    /**
     * //ICDM-916 stat Rivet Rivet Action Unchecked then do not show Rivet Class
     */
    if (!this.rivetFlag && ((ParameterClass.RIVET == this.parameterDataProvider.getPclass(param)) ||
        (ParameterClass.STATRIVET == this.parameterDataProvider.getPclass(param)))) {
      return false;
    }
    /**
     * Undefined Class Action Unchecked then do not show Undefined Class
     */
    if (!this.noClassFlag && (null == this.parameterDataProvider.getPclass(param))) {
      return false;
    }
    return true;
  }

  /**
   * Check the parameter' data type
   *
   * @param param parameter
   */
  private boolean checkDataType(final IParameter param) {

    /**
     * Value Action Unchecked then do not show Value
     */
    if (!this.valueFlag && "VALUE".equals(param.getType())) {
      return false;
    }

    /**
     * Ascii Action Unchecked then do not show Ascii
     */
    if (!this.asciiFlag && "ASCII".equals(param.getType())) {
      return false;
    }

    if (!checkComplexDataType(param)) {
      return false;
    }

    return true;
  }

  /**
   * Check the parameter's class
   *
   * @param param parameter
   */
  private boolean checkComplexDataType(final IParameter param) {
    /**
     * Curve Action Unchecked then do not show Curve
     */
    if (!this.curveFlag && "CURVE".equals(param.getType())) {
      return false;
    }

    /**
     * Value Block Action Unchecked then do not show Value Block
     */
    if (!this.valueBlkFlag && "VAL_BLK".equals(param.getType())) {
      return false;
    }

    /**
     * Map Action Unchecked then do not show Map
     */
    if (!this.mapFlag && "MAP".equals(param.getType())) {
      return false;
    }
    /**
     * Axis Point Action Unchecked then do not show Axis Point
     */
    if (!this.axisFlag && "AXIS_PTS".equals(param.getType())) {
      return false;
    }


    return true;
  }


  /**
   * @param curveFlg curveFlg
   */
  public void setCurveFlag(final boolean curveFlg) {
    this.curveFlag = curveFlg;

  }


  /**
   * @param valueFlag the valueFlag to set
   */
  public void setValueFlag(final boolean valueFlag) {
    this.valueFlag = valueFlag;
  }

  /**
   * @param valueBlkFlag valueBlkFlag
   */
  public void setValueBlkFlag(final boolean valueBlkFlag) {
    this.valueBlkFlag = valueBlkFlag;

  }


  /**
   * @param mapFlag mapFlag
   */
  public void setMapFlag(final boolean mapFlag) {
    this.mapFlag = mapFlag;

  }

  /**
   * @param asciiFlag asciiFlag
   */
  public void setAsciiFlag(final boolean asciiFlag) {
    this.asciiFlag = asciiFlag;

  }

  /**
   * @param axisFlag axisFlag
   */
  public void setAxisFlag(final boolean axisFlag) {
    this.axisFlag = axisFlag;

  }


  /**
   * @param screwFlag screwFlag
   */
  public void setScrewFlag(final boolean screwFlag) {
    this.screwFlag = screwFlag;

  }


  /**
   * @param nailFlag nailFlag
   */
  public void setNailFlag(final boolean nailFlag) {
    this.nailFlag = nailFlag;

  }

  /**
   * @param rivetFlag rivetFlag
   */
  public void setRivetFlag(final boolean rivetFlag) {
    this.rivetFlag = rivetFlag;

  }

  /**
   * @param noClassFlag undefinedClassFlag
   */
  public void setNoClassFlag(final boolean noClassFlag) {
    this.noClassFlag = noClassFlag;
  }

  /**
   * @param yesFlag yesFlag
   */
  public void setYesFlag(final boolean yesFlag) {
    this.yesFlag = yesFlag;

  }

  /**
   * @param noFlag noFlag
   */
  public void setNoFlag(final boolean noFlag) {
    this.noFlag = noFlag;

  }


  /**
   * @param manualFlag manualFlag
   */
  public void setManualFlag(final boolean manualFlag) {
    this.manualFlag = manualFlag;

  }

  /**
   * @param autoFlag autoFlag
   */
  public void setAutoFlag(final boolean autoFlag) {
    this.autoFlag = autoFlag;

  }

  /**
   * @param ruleExists CDRRule exists
   */
  public void setRuleCompleteFlag(final boolean ruleExists) {
    this.ruleComplete = ruleExists;

  }

  /**
   * @param noRuleExists no CDRRule exists
   */
  public void setRuleInCompleteFlag(final boolean noRuleExists) {
    this.ruleInComplete = noRuleExists;

  }

  /**
   * Filter For the Undefined - Review Type Icdm-654
   *
   * @param rvwUndefined rvwUndefined
   */
  public void setRvwUnDefinedFlag(final boolean rvwUndefined) {
    this.rvwUndefined = rvwUndefined;

  }


  /**
   * @param paramWithDepn the paramWithDepn to set
   */
  public void setParamWithDepn(final boolean paramWithDepn) {
    this.paramWithDepn = paramWithDepn;
  }


  /**
   * @param paramWithOutDepn the paramWithOutDepn to set
   */
  public void setParamWithOutDepn(final boolean paramWithOutDepn) {
    this.paramWithOutDepn = paramWithOutDepn;
  }


  /**
   * @param noRuleExistsSearch the noRuleExistsSearch to set
   */
  public void setNoRuleExistsSearch(final boolean noRuleExistsSearch) {
    this.noRuleExistsSearch = noRuleExistsSearch;
  }

  // ICDM-2439
  /**
   * @param complianceFlag complianceFlag
   */
  public void setComplianceFlag(final boolean complianceFlag) {
    this.complianceFlag = complianceFlag;

  }

  /**
   * @param nonComplianceFlag the nonComplianceFlag to set
   */
  public void setNonComplianceFlag(final boolean nonComplianceFlag) {
    this.nonComplianceFlag = nonComplianceFlag;
  }


  /**
   * @param blackListFlag the blackListFlag to set
   */
  public void setBlackListFlag(final boolean blackListFlag) {
    this.blackListFlag = blackListFlag;
  }


  /**
   * @param nonBlackListFlag the nonBlackListFlag to set
   */
  public void setNonBlackListFlag(final boolean nonBlackListFlag) {
    this.nonBlackListFlag = nonBlackListFlag;
  }

  /**
   * @return Matcher
   */
  @SuppressWarnings("rawtypes")
  public Matcher getToolBarMatcher() {
    return new ParamMatcher<IParameter>();
  }

  private class ParamMatcher<E> implements Matcher<E> {

    /** {@inheritDoc} */
    @Override
    public boolean matches(final E element) {
      boolean returnValue = selectElement(element);
      if (element instanceof IParameter) {
        ReviewParamToolBarFilters.this.paramVisiblilityMap.put(((IParameter) element).getName(), returnValue);
      }
      return returnValue;
    }
  }

}

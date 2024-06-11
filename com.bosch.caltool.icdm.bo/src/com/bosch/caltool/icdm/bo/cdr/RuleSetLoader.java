/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSet;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;
import com.bosch.caltool.icdm.model.user.ApicAccessRight;

/**
 * @author dja7cob Fetch list of functions
 */
public class RuleSetLoader extends AbstractBusinessObject<RuleSet, TRuleSet> {

  /**
   * @param serviceData serviceData
   */
  public RuleSetLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RULE_SET, TRuleSet.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RuleSet createDataObject(final TRuleSet dbRuleSet) throws DataException {
    RuleSet ruleSet = new RuleSet();
    ruleSet.setId(dbRuleSet.getRsetId());
    ruleSet.setAttrValId(dbRuleSet.getTabvAttrValue().getValueId());

    ruleSet.setDeleted(yOrNToBoolean(dbRuleSet.getDeletedFlag()));
    ruleSet.setDescEng(dbRuleSet.getDescEng());
    ruleSet.setDescGer(dbRuleSet.getDescGer());
    ruleSet.setName(dbRuleSet.getRsetName());
    String desc = getLangSpecTxt(dbRuleSet.getDescEng(), dbRuleSet.getDescGer());
    ruleSet.setDescription(desc);
    ruleSet.setVersion(dbRuleSet.getVersion());
    ruleSet.setSsdNodeId(dbRuleSet.getSsdNodeId());
    ruleSet.setReadAccessFlag(yOrNToBoolean(dbRuleSet.getReadAccessFlag()));
    setCommonFields(ruleSet, dbRuleSet);
    return ruleSet;
  }


  /**
   * @return set of FunctionDetails
   * @throws DataException DataException
   */
  public Set<ParamCollection> getAllRuleSets() throws DataException {
    Set<ParamCollection> retSet = new HashSet<>();

    TypedQuery<TRuleSet> tQuery = getEntMgr().createNamedQuery(TRuleSet.FIND_ALL, TRuleSet.class);

    List<TRuleSet> dbRuleSets = tQuery.getResultList();

    for (TRuleSet dbRuleSet : dbRuleSets) {
      retSet.add(createDataObject(dbRuleSet));
    }
    return retSet;
  }

  /**
   * Checks if the Rule Set has minimum READ access
   *
   * @param coll Parameter Collection
   * @return true if ruleset is deleted or does not have read/write access for the user
   * @throws DataException error while retrieving access rights
   */
  public boolean isRestricted(final ParamCollection coll) throws DataException {
    ApicAccessRight curUserRight = new ApicAccessRightLoader(getServiceData()).getAccessRightsCurrentUser();
    return !(curUserRight.isApicRead() || curUserRight.isApicWrite()) || isDeleted(coll);
  }

  /**
   * @param paramCol whether rule set is deleted
   * @return true if it is deleted
   */
  public boolean isDeleted(final ParamCollection paramCol) {
    return (paramCol instanceof RuleSet) && ((RuleSet) paramCol).isDeleted();
  }

  /**
   * @param ruleSetId rule Set Id
   * @param additionalDetails as Param and Rule Ids
   * @return name
   * @throws IcdmException
   */
  public String getExtendedName(final Long ruleSetId, final Map<String, String> additionalDetails)
      throws IcdmException {
    RuleSet ruleSet = getDataObjectByID(ruleSetId);
    if (!additionalDetails.isEmpty()) {
      String ruleSetParamId = additionalDetails.get(ApicConstants.RULESET_PARAM_ID);
      String ruleId = additionalDetails.get(ApicConstants.RULE_ID);
      RuleSetParameter ruleSetParameter =
          new RuleSetParameterLoader(getServiceData()).getDataObjectByID(Long.valueOf(ruleSetParamId));
      RuleSetRulesResponse rulesForSingleParam =
          new ParameterRuleFetcher(getServiceData()).getRulesForSingleParam(ruleSetId, ruleSetParameter);
      List<ReviewRule> reviewRules = rulesForSingleParam.getReviewRuleMap().get(ruleSetParameter.getName());
      ReviewRule matchingReviewRule = null;
      for (ReviewRule reviewRule : reviewRules) {
        if (CommonUtils.isEqual(reviewRule.getRuleId().longValue(), Long.valueOf(ruleId))) {
          matchingReviewRule = reviewRule;
        }
      }
      return EXTERNAL_LINK_TYPE.RULE_SET.getTypeDisplayText() + ": " + ruleSet.getName() + "-> " +
          ruleSetParameter.getName() + "-> " +
          getRuleDependencyDesc(matchingReviewRule, ruleSetParameter.getName(), rulesForSingleParam);
    }
    return EXTERNAL_LINK_TYPE.RULE_SET.getTypeDisplayText() + ": " + ruleSet.getName();
  }

  /**
   * @param parameter parameter
   * @param rule rule
   * @return true if it is default rule
   */
  private boolean isDefaultRule(final String paramName, final ReviewRule rule,
      final RuleSetRulesResponse ruleSetRulesResponse) {
    List<RuleSetParameterAttr> depList = ruleSetRulesResponse.getAttrMap().get(paramName);
    return rule.getDependencyList().isEmpty() && (depList != null) && (!depList.isEmpty());

  }


  /**
   * @param cdrRule CDRRule
   * @param parameter parameter collection
   * @return String representation of the rule dependency. Adds a new line in the beginning, if the text is not empty
   */
  private String getRuleDependencyDesc(final ReviewRule cdrRule, final String paramName,
      final RuleSetRulesResponse ruleSetRulesResponse) {
    StringBuilder desc = new StringBuilder();
    if (null == cdrRule) {
      return desc.toString();
    }
    // For default rule, add text default rule
    if (isDefaultRule(paramName, cdrRule, ruleSetRulesResponse)) {
      desc.append("Default Rule");

    }
    // For dependency rule, add the dependency rule, then the dependency details
    else if (!cdrRule.getDependencyList().isEmpty()) {
      desc.append("Rule dependency : ").append(getAttrValString(cdrRule));
    }

    return desc.toString();
  }

  /**
   * @param result
   * @param attrValSet
   * @param depen
   * @return
   */
  private String getAttrValString(final ReviewRule rule) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    if ((rule != null) && (rule.getDependencyList() != null)) {
      for (AttributeValueModel attrVal : rule.getDependencyList()) {
        // iCDM-1317
        depen.append(attrVal.getAttr().getName()).append("  --> ").append(attrVal.getValue().getName()).append("  ;  ");
      }
      if (!CommonUtils.isEmptyString(depen.toString())) {
        result = depen.substring(0, depen.length() - 4).trim();
      }
    }
    return result;
  }

}

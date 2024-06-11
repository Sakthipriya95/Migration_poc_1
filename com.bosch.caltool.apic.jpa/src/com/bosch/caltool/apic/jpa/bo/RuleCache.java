/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Rules Cache
 *
 * @author bne4cob
 */
public final class RuleCache {

  /**
   * Map of all rules, cached so far
   * <p>
   * Key - SSD Node ID <br>
   * Value - Rule Map for all icdm nodes <br>
   * <p>
   * Rule Map for all icdm nodes details:<br>
   * Key - ICDM Node ID (e.g. RuleSet ID, Component Package ID etc.)<br>
   * Value - Map of the rules of the node, represented by key
   * <p>
   * Rule Map of single icdm node details:<br>
   * Key - Parameter name<br>
   * Value - List of rules of the parameter
   */
  private final ConcurrentMap<Long, ConcurrentMap<Long, ConcurrentMap<String, List<CDRRule>>>> allRulesMap =
      new ConcurrentHashMap<>();


  /**
   * Add/remove the list of rules to the iCDM rule cache. If adding, if the rule(s) already exist, then they are
   * replaced.
   *
   * @param ssdNodeID SSD Node ID
   * @param icdmNodeID iCDM Node ID
   * @param ruleList List of rules
   * @param addRules if <code>true</code> rules are added, else removed.
   */
  public void updateRules(final Long ssdNodeID, final Long icdmNodeID, final List<CDRRule> ruleList,
      final boolean addRules) {

    ConcurrentMap<String, List<CDRRule>> icdmNodeRulesMap = getNodeRulesMap(ssdNodeID, icdmNodeID);

    for (CDRRule rule : ruleList) {
      List<CDRRule> cacheRuleList = getParamRuleList(rule.getParameterName(), icdmNodeRulesMap);

      Iterator<CDRRule> cacheRuleItr = cacheRuleList.iterator();
      while (cacheRuleItr.hasNext()) {
        CDRRule cacheRule = cacheRuleItr.next();
        if (CommonUtils.isEqual(cacheRule.getRuleId(), rule.getRuleId())) {
          cacheRuleList.remove(cacheRule);
          break;
        }
      }
      if (addRules) {
        cacheRuleList.add(rule);
      }
    }
  }

  /**
   * @param ssdNodeID
   * @param icdmNodeID
   * @return
   */
  private ConcurrentMap<String, List<CDRRule>> getNodeRulesMap(final Long ssdNodeID, final Long icdmNodeID) {
    ConcurrentMap<Long, ConcurrentMap<String, List<CDRRule>>> ssdNodeRulesMap = this.allRulesMap.get(ssdNodeID);
    if (ssdNodeRulesMap == null) {
      ssdNodeRulesMap = new ConcurrentHashMap<>();
      this.allRulesMap.put(ssdNodeID, ssdNodeRulesMap);
    }
    ConcurrentMap<String, List<CDRRule>> icdmNodeRulesMap = ssdNodeRulesMap.get(icdmNodeID);
    if (icdmNodeRulesMap == null) {
      icdmNodeRulesMap = new ConcurrentHashMap<>();
      ssdNodeRulesMap.put(icdmNodeID, icdmNodeRulesMap);
    }
    return icdmNodeRulesMap;
  }

  /**
   * Adds/removes the rule to the iCDM rule cache. If adding, if the rule already exists, then it is replaced.
   *
   * @param ssdNodeID SSD Node ID
   * @param icdmNodeID iCDM Node ID
   * @param rule rule
   * @param addRule if <code>true</code> rule is added, else removed.
   */
  public void updateRule(final Long ssdNodeID, final Long icdmNodeID, final CDRRule rule, final boolean addRule) {
    List<CDRRule> ruleList = new ArrayList<>();
    ruleList.add(rule);
    updateRules(ssdNodeID, icdmNodeID, ruleList, addRule);
  }


  /**
   * Get the list of CDR Rules for the parameter, identified by name and type
   *
   * @param ssdNodeID SSD Node ID
   * @param icdmNodeID iCDM Node ID
   * @param paramName parameter name
   * @return list of rules of this parameter
   */
  public List<CDRRule> getRulesForParam(final Long ssdNodeID, final long icdmNodeID, final String paramName) {

    ConcurrentMap<String, List<CDRRule>> icdmNodeRulesMap = getNodeRulesMap(ssdNodeID, icdmNodeID);
    return getParamRuleList(paramName, icdmNodeRulesMap);
  }

  /**
   * @param paramName
   * @param icdmNodeRulesMap
   * @return
   */
  private List<CDRRule> getParamRuleList(final String paramName,
      final ConcurrentMap<String, List<CDRRule>> icdmNodeRulesMap) {

    String paramNameKey = ApicUtil.getParamKey(paramName);

    List<CDRRule> cacheRuleList = icdmNodeRulesMap.get(paramNameKey);

    if (cacheRuleList == null) {
      cacheRuleList = new ArrayList<>();
      icdmNodeRulesMap.put(paramNameKey, cacheRuleList);
    }
    return cacheRuleList;
  }

  /**
   * Get the parameters, name as available in the rule object, for the given iCDM node
   *
   * @param ssdNodeID SSD Node
   * @param icdmNodeID iCDM node
   * @return set of parameter names, as set in rule object
   */
  public Set<String> getParamNamesFromRules(final Long ssdNodeID, final Long icdmNodeID) {
    Set<String> retSet = new HashSet<>();

    for (List<CDRRule> paramRuleList : getNodeRulesMap(ssdNodeID, icdmNodeID).values()) {
      if (paramRuleList.isEmpty()) {
        continue;
      }
      retSet.add(paramRuleList.get(0).getParameterName());
    }
    return retSet;
  }

  /**
   * Reset all rules that are stored against the given iCDM node
   *
   * @param ssdNodeID SSD Node
   * @param icdmNodeID iCDM Node
   */
  public void resetRules(final Long ssdNodeID, final Long icdmNodeID) {
    ConcurrentMap<String, List<CDRRule>> icdmNodeRulesMap = getNodeRulesMap(ssdNodeID, icdmNodeID);
    icdmNodeRulesMap.clear();
  }

}

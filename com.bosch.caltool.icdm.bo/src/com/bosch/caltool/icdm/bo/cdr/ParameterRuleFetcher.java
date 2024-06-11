/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterAttributeLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.a2l.ParameterRulesResponse;
import com.bosch.caltool.icdm.model.a2l.RuleSetRulesResponse;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;


/**
 * @author rgo7cob
 */
public class ParameterRuleFetcher extends AbstractSimpleBusinessObject {


  /**
   * Class Instance
   *
   * @param serviceData initialise the service transaction
   */
  public ParameterRuleFetcher(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param funcName funcName
   * @param version version
   * @param byVariant byVariant
   * @param paramNameSet set of parameters from imported file
   * @return the Param Rules ouput
   * @throws IcdmException errros during data loading
   */
  public ParameterRulesResponse createParamRulesOutput(final String funcName, final String version,
      final String byVariant, final Set<String> paramNameSet)
      throws IcdmException {
    Boolean varaintNeeded = null;

    ParameterLoader loader = new ParameterLoader(getServiceData());
    if (ApicConstants.BOOLEAN_TRUE_STRING.equalsIgnoreCase(byVariant)) {
      varaintNeeded = true;
    }
    else if (ApicConstants.BOOLEAN_FALSE_STRING.equalsIgnoreCase(byVariant)) {
      varaintNeeded = false;
    }

    Map<String, Parameter> paramMap = loader.getParamsMap(funcName, version, varaintNeeded, paramNameSet);
    VariantCodedParamHandler<Parameter> handler = new VariantCodedParamHandler<>();
    Map<String, Parameter> filteredParams = handler.getFilteredParams(paramMap);

    ParameterRulesResponse output = getParamRuleOutput(funcName, version, paramMap);
    output.setParamMap(filteredParams);

    // set dont care feature value map
    output.setDontCareAttrValueMap(new FeatureValueLoader(getServiceData()).getDontCareAttrValues());

    return output;
  }


  /**
   * @param funcName
   * @param version
   * @param paramMap input parameter map
   * @return ParameterRulesResponse model
   * @throws IcdmException errros during data loading
   */
  public ParameterRulesResponse getParamRuleOutput(final String funcName, final String version,
      final Map<String, Parameter> paramMap)
      throws IcdmException {
    ParameterRulesResponse output = new ParameterRulesResponse();
    Map<String, List<ReviewRule>> rulesMap = getRules(new ArrayList<>(paramMap.keySet()));

    ParameterAttributeLoader paramAttrLoader = new ParameterAttributeLoader(getServiceData());

    Map<String, List<ParameterAttribute>> paramAttrMap =
        paramAttrLoader.fetchParameterAttrDepn(new HashSet<>(paramMap.values()));

    output.setAttrObjMap(getAttrObjMap(paramAttrMap));

    output.setAttrMap(paramAttrMap);
    output.setRulesMap(rulesMap);
    return output;
  }

  /**
   * Get dependent param attrs
   *
   * @param paramSet input parameters
   * @return the map of all the attributes of all tha prameteres
   * @throws DataException error during data loading
   */
  public Map<String, List<Attribute>> getAttributes(final Set<IParameter> paramSet) throws DataException {
    boolean useRuleSetDep = false;
    // if the param is rule set prameter then fetch the Rule set param
    for (IParameter iParameter : paramSet) {
      if (iParameter instanceof RuleSetParameter) {
        useRuleSetDep = true;
        break;
      }
    }

    if (useRuleSetDep) {
      RuleSetParameterAttrLoader ruleSetParamAttrLoader = new RuleSetParameterAttrLoader(getServiceData());
      Map<String, List<RuleSetParameterAttr>> paramAttrMap = ruleSetParamAttrLoader.fetchParameterAttrDepn(paramSet);
      return getRuleSetParamAttrObjMap(paramAttrMap);
    }
    ParameterAttributeLoader paramAttrLoader = new ParameterAttributeLoader(getServiceData());
    Map<String, List<ParameterAttribute>> paramAttrMap = paramAttrLoader.fetchParameterAttrDepn(paramSet);
    return getParamAttrObjMap(paramAttrMap);

  }


  /**
   * @param paramAttrMap
   * @return
   * @throws DataException
   */
  private Map<String, List<Attribute>> getRuleSetParamAttrObjMap(
      final Map<String, List<RuleSetParameterAttr>> paramAttrMap)
      throws DataException {
    Map<String, List<Attribute>> attrObjMap = new HashMap<>();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());

    for (Entry<String, List<RuleSetParameterAttr>> paramEntry : paramAttrMap.entrySet()) {
      List<Attribute> attrList = new ArrayList<>();
      List<RuleSetParameterAttr> paramAttr = paramEntry.getValue();
      for (RuleSetParameterAttr parameterAttribute : paramAttr) {
        attrList.add(attrLoader.getDataObjectByID(parameterAttribute.getAttrId()));
      }
      attrObjMap.put(paramEntry.getKey(), attrList);
    }
    return attrObjMap;
  }


  /**
   * @param paramAttrMap
   * @return
   * @throws DataException
   */
  private Map<String, List<Attribute>> getParamAttrObjMap(final Map<String, List<ParameterAttribute>> paramAttrMap)
      throws DataException {
    Map<String, List<Attribute>> attrObjMap = new HashMap<>();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());

    for (Entry<String, List<ParameterAttribute>> paramEntry : paramAttrMap.entrySet()) {
      List<Attribute> attrList = new ArrayList<>();
      List<ParameterAttribute> paramAttr = paramEntry.getValue();
      for (ParameterAttribute parameterAttribute : paramAttr) {
        attrList.add(attrLoader.getDataObjectByID(parameterAttribute.getAttrId()));

      }
      attrObjMap.put(paramEntry.getKey(), attrList);
    }
    return attrObjMap;
  }

  /**
   * @param paramAttrMap
   * @return
   */
  private Map<Long, Attribute> getAttrObjMap(final Map<String, List<ParameterAttribute>> paramAttrMap)
      throws DataException {
    Map<Long, Attribute> attrObjMap = new HashMap<>();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (List<ParameterAttribute> paramAttr : paramAttrMap.values()) {

      for (ParameterAttribute parameterAttribute : paramAttr) {
        if (attrObjMap.get(parameterAttribute.getAttrId()) == null) {
          attrObjMap.put(parameterAttribute.getAttrId(), attrLoader.getDataObjectByID(parameterAttribute.getAttrId()));
        }

      }
    }
    return attrObjMap;
  }


  private Map<String, List<ReviewRule>> getRules(final List<String> paramList) throws IcdmException {
    getLogger().info("Fetching Rules for parameters -" + paramList);

    return new ReviewRuleAdapter(getServiceData())
        .fetchReviewRuleForCdr(new SSDServiceHandler(getServiceData()).readReviewRule(paramList));
  }

  /**
   * @param ruleSetId ruleSet Id
   * @return the RuleSetRulesResponse model
   * @throws IcdmException error during data loading
   */
  public RuleSetRulesResponse createRuleSetRulesOutput(final long ruleSetId) throws IcdmException {
    RuleSetRulesResponse output = new RuleSetRulesResponse();

    // Fetch rule-set parameters
    RuleSetParameterLoader loader = new RuleSetParameterLoader(getServiceData());
    Map<String, RuleSetParameter> paramMap = loader.getAllRuleSetParams(ruleSetId);

    // Remove variant coded parameters
    Map<String, RuleSetParameter> filteredParams =
        new VariantCodedParamHandler<RuleSetParameter>().getFilteredParams(paramMap);
    getLogger().debug("Parameter count after removing variant coded items = {}", filteredParams.size());

    output.setParamMap(filteredParams);

    // Fetch rule-set parameter attributes
    RuleSetParameterAttrLoader paramAttrLoader = new RuleSetParameterAttrLoader(getServiceData());
    Map<String, List<RuleSetParameterAttr>> paramAttrMap = paramAttrLoader.getParamAttrMap(filteredParams);
    output.setAttrMap(paramAttrMap);

    Long ssdNodeId = loader.getRuleSetEntity(ruleSetId).getSsdNodeId();
    getLogger().debug("Rule-set {} : SSD Node ID = {}", ruleSetId, ssdNodeId);

    // Fetch rule-set rules
    List<String> paramNames = new ArrayList<>(paramMap.keySet());

    Map<String, List<ReviewRule>> rulesMap = new HashMap<>();
    if (!paramNames.isEmpty()) {
      rulesMap = getRulesForRuleSet(paramNames, ssdNodeId);
    }
    output.setRulesMap(rulesMap);

    // Fetch attributes relavant for the rule set
    output.setAttrValModelMap(getRuleSetAttrObjMap(paramAttrMap));

    // set dont care feature value map
    output.setDontCareAttrValueMap(new FeatureValueLoader(getServiceData()).getDontCareAttrValues());

    return output;
  }

  /**
   * Get rules of a single parameter in the given ruleset
   *
   * @param ruleSetId Rule Set ID
   * @param ruleSetParameter Rule Set Parameter
   * @return RuleSetRulesResponse
   * @throws IcdmException error during data loading
   */
  public RuleSetRulesResponse getRulesForSingleParam(final Long ruleSetId, final RuleSetParameter ruleSetParameter)
      throws IcdmException {

    RuleSetRulesResponse output = new RuleSetRulesResponse();
    RuleSetParameterLoader loader = new RuleSetParameterLoader(getServiceData());
    Map<String, RuleSetParameter> paramMap = new HashMap<>();
    paramMap.put(ruleSetParameter.getName(), ruleSetParameter);
    VariantCodedParamHandler<RuleSetParameter> handler = new VariantCodedParamHandler<>();
    Map<String, RuleSetParameter> filteredParams = handler.getFilteredParams(paramMap);
    output.setParamMap(filteredParams);

    RuleSetParameterAttrLoader paramAttrLoader = new RuleSetParameterAttrLoader(getServiceData());
    Map<String, List<RuleSetParameterAttr>> paramAttrMap = paramAttrLoader.getParamAttrMap(filteredParams);
    output.setAttrMap(paramAttrMap);

    Long ssdNodeId = loader.getRuleSetEntity(ruleSetId).getSsdNodeId();

    List<String> paramNames = new ArrayList<>(paramMap.keySet());

    Map<String, List<ReviewRule>> rulesMap = getRulesForRuleSet(paramNames, ssdNodeId);

    output.setRulesMap(rulesMap);

    output.setAttrValModelMap(getRuleSetAttrObjMap(paramAttrMap));
    return output;
  }


  /**
   * @param paramAttrMap
   * @return
   */
  private Map<Long, Attribute> getRuleSetAttrObjMap(final Map<String, List<RuleSetParameterAttr>> paramAttrMap)
      throws DataException {

    getLogger().debug("Fetching Rule-Set attribute object map...");

    Map<Long, Attribute> attrObjMap = new HashMap<>();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (List<RuleSetParameterAttr> paramAttr : paramAttrMap.values()) {

      for (RuleSetParameterAttr parameterAttribute : paramAttr) {
        if (attrObjMap.get(parameterAttribute.getAttrId()) == null) {
          attrObjMap.put(parameterAttribute.getAttrId(), attrLoader.getDataObjectByID(parameterAttribute.getAttrId()));
        }

      }
    }

    getLogger().debug("Fetching Rule-Set attribute object map finished");

    return attrObjMap;
  }


  /**
   * Fetch rule set rules
   *
   * @param paramNames list of params
   * @param ssdNodeId ssdNodeId
   * @return Map of review rules. Key - parameter name, value - list of ReviewRules
   * @throws IcdmException error during data loading
   */
  public Map<String, List<ReviewRule>> getRulesForRuleSet(final List<String> paramNames, final Long ssdNodeId)
      throws IcdmException {

    return new ReviewRuleAdapter(getServiceData())
        .fetchReviewRuleForCdr(new SSDServiceHandler(getServiceData()).readReviewRule(paramNames, ssdNodeId));
  }
}


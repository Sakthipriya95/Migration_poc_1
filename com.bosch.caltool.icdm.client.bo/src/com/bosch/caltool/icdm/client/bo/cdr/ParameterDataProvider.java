/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.IParamRuleResponse;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.IParameterAttribute;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.ssdfeature.Feature;
import com.bosch.caltool.icdm.model.ssdfeature.FeatureValue;
import com.bosch.caltool.icdm.ws.rest.client.a2l.IParamAttrServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParamAttrServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.FeatureServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.FeatureValueServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RuleSetParamAttrServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author rgo7cob
 */
public class ParameterDataProvider<D extends IParameterAttribute, P extends IParameter> {


  // new enumeration for compliance
  public enum SSD_CLASS {

                         /**
                          * compliance
                          */
                         COMPLIANCE("COMPLIANCE"),
                         /**
                          * monitoring
                          */
                         MONITORING("MONITORING"),
                         /**
                          * component
                          */
                         COMPONENT("COMPONENT"),
                         /**
                          * cust spec
                          */
                         CUSTSPEC("CUSTSPEC"),
                         /**
                          * not in ssd
                          */
                         NOT_IN_SSD("NOT_IN_SSD");

    private final String dbType;


    /**
     * @param dbType
     * @param compliant
     */
    private SSD_CLASS(final String dbType) {
      this.dbType = dbType;
    }

    /**
     * default not in ssd
     *
     * @param dbType dbType
     * @return the ssd class
     */
    public static SSD_CLASS getSsdClass(final String dbType) {
      for (SSD_CLASS ssdclass : SSD_CLASS.values()) {
        if ((dbType != null) && ssdclass.dbType.equals(dbType)) {
          return ssdclass;
        }
      }
      return NOT_IN_SSD;
    }

    /**
     * @return true if it is compliant
     */
    public boolean isCompliant() {
      return this == SSD_CLASS.COMPLIANCE;
    }

  }


  private final IParamRuleResponse<D, P> paramRulesOutput;


  /**
   * Text constant for VALUE
   */
  public static final String VALUE_TEXT = "VALUE";


  /**
   * To remove semicolon as the end of attr-val string
   */
  private static final int SEMICOLON_SIZE = 4;


  /**
   * @param paramRulesOutput paramRulesOutput
   */
  public ParameterDataProvider(final IParamRuleResponse<D, P> paramRulesOutput) {
    this.paramRulesOutput = paramRulesOutput;

  }

  public IParamAttrProvider<D> getParamAttrProvider(final IParameter parameter) {
    if (parameter instanceof Parameter) {
      return (IParamAttrProvider<D>) new ParamAttrProvider();
    }
    return (IParamAttrProvider<D>) new RuleSetParamAttrProvider();
  }


  public IParamAttrServiceClient<D> getParamAttrServiceClient(final D parameter) {
    if (parameter instanceof ParameterAttribute) {
      return (IParamAttrServiceClient<D>) new ParamAttrServiceClient();
    }
    return (IParamAttrServiceClient<D>) new RuleSetParamAttrServiceClient();
  }

  /**
   * @param parameter parameter
   * @return the review rule list
   */
  public List<ReviewRule> getRuleList(final IParameter parameter) {
    return this.paramRulesOutput.getReviewRuleMap().get(parameter.getName());
  }

  /**
   * @param paramName
   * @return
   */
  public List<ReviewRule> getRuleList(final String paramName) {
    return this.paramRulesOutput.getReviewRuleMap().get(paramName);
  }


  /**
   * @param parameter parameter
   * @return the review rule.
   */
  public ReviewRule getReviewRule(final IParameter parameter) {

    if (hasDependency(parameter)) {
      return null;
    }
    List<ReviewRule> ruleList = this.paramRulesOutput.getReviewRuleMap().get(parameter.getName());
    if ((ruleList != null) && (!ruleList.isEmpty())) {
      return ruleList.get(0);
    }
    return null;
  }

  /**
   * @param parameter parameter
   * @return true if it has dependency
   */
  public boolean hasDependency(final IParameter parameter) {
    return hasDependencyCheckByName(parameter.getName());
  }
  
  /**
   * @param paramName - parameter name
   * @return true if it has dependency
   * Method has been added for  data review flow, to check if there is any dependency by name
   */
  public boolean hasDependencyCheckByName(final String paramName) {
    List<D> depList = this.paramRulesOutput.getAttrMap().get(paramName);
    return CommonUtils.isNotEmpty(depList);
  }


  /**
   * @param parameter parameter
   * @param rule rule
   * @return true if it is default rule
   */
  public boolean isDefaultRule(final IParameter parameter, final ReviewRule rule) {
    List<D> depList = this.paramRulesOutput.getAttrMap().get(parameter.getName());
    return rule.getDependencyList().isEmpty() && (depList != null) && (!depList.isEmpty());

  }


  /**
   * @param cdrRule CDRRule
   * @param parameter parameter collection
   * @return String representation of the rule dependency. Adds a new line in the beginning, if the text is not empty
   */
  public String getDependencyDesc(final ReviewRule cdrRule, final IParameter parameter) {
    StringBuilder desc = new StringBuilder();
    if (null == cdrRule) {
      return desc.toString();
    }
    // For default rule, add text default rule
    if (isDefaultRule(parameter, cdrRule)) {
      desc.append("\nDefault Rule");

    }
    // For dependency rule, add the dependency rule, then the dependency details
    else if (!cdrRule.getDependencyList().isEmpty()) {
      desc.append("\nRule dependency : ").append(getAttrValString(cdrRule));
    }

    return desc.toString();
  }


  /**
   * @param parameter parameter
   * @return true or false for the code word
   */
  public boolean isCodeWord(final IParameter parameter) {
    return ApicConstants.CODE_YES.equals(parameter.getCodeWord());
  }

  /**
   * @param parameter parameter
   * @return true if the parameter is Compliant
   */
  public boolean isComplianceParam(final IParameter parameter) {
    return SSD_CLASS.valueOf(parameter.getSsdClass()).isCompliant();
  }

  /**
   * @param parameter
   * @return
   */
  public String getBitWiseRule(final IParameter parameter) {
    if (ApicConstants.CODE_YES.equalsIgnoreCase(parameter.getIsBitWise())) {
      return ApicConstants.CODE_WORD_YES;
    }
    return ApicConstants.CODE_WORD_NO;
  }


  /**
   * @param parameter parameter
   * @return the isBitiwise
   */
  public boolean isBitWise(final IParameter parameter) {
    return ApicConstants.CODE_YES.equalsIgnoreCase(parameter.getIsBitWise());
  }

  /**
   * @param rule rule
   * @return the ref value Display String
   */
  public String getRefValueDispString(final ReviewRule rule) {
    String refValString = "";
    // VALUE type label
    if ((CommonUtils.isEqual(rule.getValueType(), VALUE_TEXT)) && (rule.getRefValueCalData() == null)) {
      refValString = rule.getRefValue() == null ? "" : rule.getRefValue().toString();
    }
    // Complex type labels
    else if (rule.getRefValueCalData() != null) {
      refValString = rule.getRefValueCalData() == null ? "" : rule.getRefValueDispString();
    }
    return refValString;
  }


  /**
   * @param param param
   * @return the sorted set of param rules
   */
  public SortedSet<ReviewRule> getSortedRuleSet(final IParameter param) {
    SortedSet<ReviewRule> cdrRulesSet = new TreeSet<>(new CDRRuleAttrDependencyComparator(this));
    List<ReviewRule> ruleList = getRuleList(param);

    if (CommonUtils.isNotNull(ruleList)) {
      cdrRulesSet.addAll(ruleList);
    }
    return cdrRulesSet;
  }

  /**
   * Icdm-500
   *
   * @param rule Cdr Rule
   * @return the Rule Ready for series Ui Value
   */
  public String getReadyForSeriesUIVal(final ReviewRule rule) {
    if (rule != null) {
      if (ApicConstants.READY_FOR_SERIES.YES.dbType.equals(rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.YES.uiType;
      }
      else if (ApicConstants.READY_FOR_SERIES.NO.dbType.equals(rule.getReviewMethod())) {
        return ApicConstants.READY_FOR_SERIES.NO.uiType;
      }
    }
    return null;
  }


  /**
   * @return the paramRulesOutput
   */
  public IParamRuleResponse<D, P> getParamRulesOutput() {
    return this.paramRulesOutput;
  }


  /**
   * @param result
   * @param attrValSet
   * @param depen
   * @return
   */
  public String getAttrValString(final ReviewRule rule) {
    String result = "";
    StringBuilder depen = new StringBuilder();
    if ((rule != null) && (rule.getDependencyList() != null)) {
      for (AttributeValueModel attrVal : rule.getDependencyList()) {
        // iCDM-1317
        depen.append(attrVal.getAttr().getName()).append("  --> ").append(attrVal.getValue().getName()).append("  ;  ");
      }
      if (!CommonUtils.isEmptyString(depen.toString())) {
        result = depen.substring(0, depen.length() - SEMICOLON_SIZE).trim();
      }
    }
    return result;
  }


  /**
   * @param param param
   * @return the list of param attrs
   */
  public List<D> getParamAttrs(final IParameter param) {
    return this.paramRulesOutput.getAttrMap().get(param.getName());
  }


  /**
   * @param param param
   * @return the pclass
   */
  public ParameterClass getPclass(final IParameter param) {
    return ParameterClass.getParamClassT(param.getpClassText());
  }


  /**
   * @param rule
   * @return
   * @throws ClassNotFoundException
   * @throws IOException
   */
  public CalData getRefValForRule(final ReviewRule rule) {
    CalData caldataObj = null;
    try {
      caldataObj = CalDataUtil.getCalDataObj(rule.getRefValueCalData());
    }
    catch (ClassNotFoundException | IOException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);

    }

    return caldataObj;
  }

  public String getCodeWordText(final IParameter param) {
    if (param.getCodeWord().equalsIgnoreCase(ApicConstants.CODE_NO)) {
      return ApicConstants.USED_NO_DISPLAY;
    }
    return ApicConstants.USED_YES_DISPLAY;

  }


  /**
   * @param rule
   * @return
   */
  public boolean isRuleComplete(final ReviewRule rule) {
    if ((null != rule.getLowerLimit()) || (null != rule.getUpperLimit()) ||
        ((null != rule.getBitWiseRule()) && !rule.getBitWiseRule().isEmpty())) {
      return true;
    }
    if (rule.isDcm2ssd()) {
      if (null != getRefValForRule(rule)) {
        return true;
      }
      if ((rule.getRefValue() != null) ||
          ((getRefValForRule(rule) != null) && (null != getRefValForRule(rule).getCalDataPhy()))) {
        return true;
      }

    }

    return false;
  }

  /**
   * @param paramAttr
   */
  public SortedSet<AttributeValue> getMappedAttrVal(final D paramAttr) throws ApicWebServiceException {

    AttributeValueServiceClient client = new AttributeValueServiceClient();
    return client.getFeatureMappedAttrVal(paramAttr.getAttrId());


  }

  /**
   * @param attrId
   * @param val
   * @throws ApicWebServiceException
   */
  public AttributeValueModel createAttrValModel(final Long attrId, final AttributeValue val)
      throws ApicWebServiceException {

    AttributeServiceClient client = new AttributeServiceClient();
    Attribute attr = client.get(attrId);
    AttributeValueModel attrValModel = createAttrValModel(val, attr);

    return attrValModel;

  }

  /**
   * @param attrId
   * @param val
   * @param attr
   * @return
   */
  public AttributeValueModel createAttrValModel(AttributeValue val, final Attribute attr) {
    AttributeValueModel attrValModel;
    if (val.getName().equals(ApicConstants.USED)) {
      val = createAttrValUsedObject(attr);
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(val);
    }
    else if (val.getName().equals(ApicConstants.NOT_USED)) {
      val = createAttrValNotUsedObject(attr);
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(val);
    }
    else {
      attrValModel = new AttributeValueModel();
      attrValModel.setAttr(attr);
      attrValModel.setValue(val);
    }
    return attrValModel;
  }


  /**
   * @param attrId attribute id
   * @return Attribute values - used
   */
  public AttributeValue createAttrValUsedObject(final Attribute attr) {
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setAttributeId(attr.getId());
    attributeValue.setName(ApicConstants.USED);
    attributeValue.setId(ApicConstants.ATTR_VAL_USED_VALUE_ID);
    attributeValue.setDescription(ApicConstants.USED);
    return attributeValue;
  }

  /**
   * @param attrId attribute id
   * @return Attribute values - Not used
   */
  public AttributeValue createAttrValNotUsedObject(final Attribute attr) {
    AttributeValue attributeValue = new AttributeValue();
    attributeValue.setAttributeId(attr.getId());
    attributeValue.setName(ApicConstants.NOT_USED);
    attributeValue.setId(ApicConstants.ATTR_VAL_NOT_USED_VALUE_ID);
    attributeValue.setDescription(ApicConstants.NOT_USED);
    return attributeValue;
  }


  /**
   * @param param
   * @return the default rule of the param
   */
  public ReviewRule getDefaultRule(final IParameter param) {

    if (hasDependency(param) && (null != getRuleList(param))) {
      for (ReviewRule cdrRule : getRuleList(param)) {
        if ((cdrRule.getDependencyList() == null) || cdrRule.getDependencyList().isEmpty()) {
          return cdrRule;
        }
      }
    }
    // Return null if there is no dep
    return null;
  }

  /**
   * @param selectedParam
   * @return
   */
  public boolean canModifyAttributeMapping(final IParameter selectedParam) {
    // Attribute mapping can be modified, if there are no rules present with dependency attributes
    List<ReviewRule> rulesList = getRuleList(selectedParam);
    if ((null != rulesList) && (!rulesList.isEmpty())) {
      for (ReviewRule cdrRule : rulesList) {
        if ((null != cdrRule.getDependencyList()) && !cdrRule.getDependencyList().isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }


  public Parameter getParamByName(final String paramName, final String paramType) throws ApicWebServiceException {

    return new ParameterServiceClient().getParameter(paramName, paramType);

  }

  /**
   * @param paramName
   * @return
   * @throws ApicWebServiceException
   */
  public Parameter getParamOnlyByName(final String paramName) throws ApicWebServiceException {
    return new ParameterServiceClient().getParameterOnlyByName(paramName);
  }


  /**
   * @param funcParam funcParam
   * @return true if Param is Compliant
   */
  public boolean isComplianceParameter(final IParameter funcParam) {
    return SSD_CLASS.COMPLIANCE == SSD_CLASS.getSsdClass(funcParam.getSsdClass());
  }

  /**
   * @param paramAttr
   */
  public Attribute getAttribute(final IParameterAttribute paramAttr) {
    return this.paramRulesOutput.getAttrObjMap().get(paramAttr.getAttrId());

  }

  /**
   * @param paramList paramList
   * @return the list of all the attributes of all tha prameteres
   */
  public List<Attribute> getAttributes(final List<P> paramList) {
    List<Attribute> attrList = new ArrayList<>();
    for (P param : paramList) {
      List<D> paramAttrs = getParamAttrs(param);
      for (D paramAttr : paramAttrs) {
        getAttribute(paramAttr);

      }

    }
    return attrList;
  }

  /**
   *
   */
  public SortedSet<Attribute> getMappedAttributes() throws ApicWebServiceException {
    AttributeServiceClient client = new AttributeServiceClient();
    return client.getMappedAttrs();

  }


  /**
   * @param paramAttr List
   * @return the list of param Attrs
   * @throws ApicWebServiceException
   */


  public List<Object> createParamAttrs(final List<D> iParamAttrs) throws ApicWebServiceException {
    List<Object> paramAttrList = new ArrayList<>();
    for (D iparamAttr : iParamAttrs) {
      IParamAttrServiceClient<D> iparamClient = getParamAttrServiceClient(iparamAttr);
      D paramAttr = iparamClient.create(iparamAttr);
      paramAttrList.add(paramAttr);
    }
    return paramAttrList;

  }

  public void deleteParamAttrs(final List<D> iParamAttrs) throws ApicWebServiceException {
    for (D iParamAttr : iParamAttrs) {

      IParamAttrServiceClient<D> iparamClient = getParamAttrServiceClient(iParamAttr);
      iparamClient.delete(iParamAttr);

      boolean repeats = false;
      Set<Attribute> dupAttrCheck = new HashSet<>();
      for (List<D> existingDepList : this.paramRulesOutput.getAttrMap().values()) {

        for (D existingdep : existingDepList) {
          Attribute attribute = this.paramRulesOutput.getAttrObjMap().get(existingdep.getAttrId());
          if (!dupAttrCheck.add(attribute) && existingdep.getAttrId().equals(iParamAttr.getAttrId())) {
            repeats = true;
          }
        }
      }
      if (!repeats) {
        // remove the attrs from the data model.
        this.paramRulesOutput.getAttrObjMap().remove(iParamAttr.getAttrId());
      }
    }

  }

  /**
   * @return
   */
  public Map<Long, Feature> getAllFeatures() throws ApicWebServiceException {

    FeatureServiceClient client = new FeatureServiceClient();
    return client.getAllFeatures();
  }

  /**
   * @param id
   * @return
   * @throws ApicWebServiceException
   */
  public List<FeatureValue> getFeatureValues(final Long featureId) throws ApicWebServiceException {
    FeatureValueServiceClient client = new FeatureValueServiceClient();
    return client.getFeatureValues(featureId);
  }


  public IParameter getParameter(final String paramName) {
    IParameter param = getParamRulesOutput().getParamMap().get(paramName);
    if (param == null) {
      param = getParamRulesOutput().getParamMap().get(paramName.toUpperCase(Locale.getDefault()));
    }
    return param;
  }

  /**
   * @param parameter param
   * @return boolean - is qssd param
   */
  public boolean isQssdParam(final IParameter parameter) {
    return parameter.isQssdFlag();
  }

}


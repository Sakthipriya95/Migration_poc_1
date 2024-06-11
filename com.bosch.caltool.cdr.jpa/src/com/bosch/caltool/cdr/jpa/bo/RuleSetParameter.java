/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.ParameterClass;
import com.bosch.caltool.apic.jpa.rules.bo.ParameterSorter;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.cdr.TFunction;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParamAttr;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Rule Set Paramater
 *
 * @author adn1cob
 */
public class RuleSetParameter extends AbstractParameter<RuleSetParameterAttribute>
    implements Comparable<RuleSetParameter> {


  /**
   * @param cdrDataProvider cdrDataprovider
   * @param objID primarykey
   */
  protected RuleSetParameter(final CDRDataProvider cdrDataProvider, final Long objID) {
    super(cdrDataProvider, objID);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // get the tparameter name
    return getFuncParameter().getName();
  }

  /**
   * @return RuleSet
   */
  public RuleSet getRuleSet() {
    // get the ruleset
    return getDataCache().getRuleSet(getEntityProvider().getDbRuleSetParam(getID()).getTRuleSet().getRsetId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RULE_SET_PARAM;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // get the parameter description
    return getFuncParameter().getDescription();
  }


  /**
   * @return the created user
   */
  @Override
  public final String getCreatedUser() {
    // user who assigned param to ruleset
    return getDataProvider().getEntityProvider().getDbRuleSetParam(getID()).getCreatedUser();
  }

  /**
   * @return the created date
   */
  @Override
  public final Calendar getCreatedDate() {
    return ApicUtil
        .timestamp2calendar(getDataProvider().getEntityProvider().getDbRuleSetParam(getID()).getCreatedDate());
  }

  /**
   * @return the modified date
   */
  @Override
  public final Calendar getModifiedDate() {
    return ApicUtil
        .timestamp2calendar(getDataProvider().getEntityProvider().getDbRuleSetParam(getID()).getModifiedDate());
  }

  /**
   * @return the modified user
   */
  @Override
  public final String getModifiedUser() {
    return getDataProvider().getEntityProvider().getDbRuleSetParam(getID()).getModifiedUser();
  }


  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public Set<RuleSetParameterAttribute> getParamAttrs() throws SsdInterfaceException {
    Set<RuleSetParameterAttribute> paramAttrSet = getDataCache().getRuleSetParamDependencyMap().get(getID());
    // fetch param attrs only if not loaded
    if (paramAttrSet == null) {
      fetchParamAttrs();
    }
    return getDataCache().getRuleSetParamDependencyMap().get(getID());
  }

  /**
   * Fetch param attrs
   *
   * @throws SsdInterfaceException
   */
  private void fetchParamAttrs() throws SsdInterfaceException {
    // Read the attributes added to this ruleSet Parameter
    List<TRuleSetParamAttr> dbParamAttr =
        getDataProvider().getEntityProvider().getDbRuleSetParam(getID()).getTRuleSetParamAttrs();
    if (dbParamAttr != null) {
      RuleSetParameterAttribute rsParamAttr;
      for (TRuleSetParamAttr dbRuleSetParamAttr : dbParamAttr) {
        rsParamAttr = getDataCache().getRuleSetParamAttrMap().get(dbRuleSetParamAttr.getRsetParAttrId());
        if (rsParamAttr == null) {
          rsParamAttr = new RuleSetParameterAttribute(getDataProvider(), dbRuleSetParamAttr.getRsetParAttrId());
        }
        getDataCache().addRuleSetParamAttr(rsParamAttr);
      }
    }
  }

  /**
   * @return the ssd class String
   */
  @Override
  public String getSsdClass() {
    return getFuncParameter().getSsdClass();
  }

  /**
   * @return the ssd class enum
   */
  @Override
  public SSD_CLASS getSsdClassEnum() {
    CDRFuncParameter funcParameter = getFuncParameter();
    return funcParameter.getSsdClassEnum();
  }

  /**
   * @return the ssd class String
   */
  @Override
  public boolean isComplianceParameter() {
    SSD_CLASS ssdClass = getSsdClassEnum();
    return ssdClass.isCompliant();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RuleSetParameter other) {
    return ParameterSorter.compare(this, other);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLongNameGer() {
    return getFuncParameter().getLongNameGer();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLongNameEng() {
    return getFuncParameter().getLongNameEng();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ParameterClass getpClass() {
    return getFuncParameter().getpClass();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCodeWord() {
    return getFuncParameter().isCodeWord();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isBitWise() {
    return getFuncParameter().isBitWise();
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public List<CDRRule> getReviewRuleList() throws SsdInterfaceException {
    // fetch rules if not already loaded
    if (!getRuleSet().hasRulesLoaded()) {
      getRuleSet().fetchRules();
    }
    return getApicDataProvider().getCDRRulesForRuleSetParam(getRuleSet().getID(), getName());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getType() {
    // get the param type
    return getFuncParameter().getType();
  }

  /**
   * Get the paramater object
   *
   * @return CDR function parameter for this rule set parameter
   */
  private CDRFuncParameter getFuncParameter() {
    TParameter dbParam = getEntityProvider().getDbRuleSetParam(getID()).getTParameter();
    CDRFuncParameter funcParam = getDataCache().getCDRFuncParameter(dbParam.getName(), dbParam.getPtype());
    if (funcParam == null) {
      funcParam = new CDRFuncParameter(getDataProvider(), dbParam.getId());
      getDataCache().addCDRFuncParameter(funcParam);
    }
    return funcParam;
  }

  /**
   * Get cdr function object
   *
   * @return CDRFunction
   */
  public CDRFunction getFunction() {
    TFunction dbFunc = getDataProvider().getEntityProvider().getDbRuleSetParam(getID()).getTFunction();
    if (dbFunc != null) {
      CDRFunction cdrFunc = getDataCache().getCDRFunction(dbFunc.getId());
      if (cdrFunc == null) {
        return getDataProvider().fetchFunctions(getName()).get(0);
      }
      return cdrFunc;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getParamHint() {
    return getFuncParameter().getParamHint();
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public Set<Attribute> getAttributes() throws SsdInterfaceException {
    Set<Attribute> mappedAttributes = new TreeSet<>();
    if (null != getParamAttrs()) {
      for (RuleSetParameterAttribute ruleParamAttrs : getParamAttrs()) {
        mappedAttributes.add(ruleParamAttrs.getAttribute());
      }
    }
    return mappedAttributes;
  }

}

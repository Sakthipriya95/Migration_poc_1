/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.IAttributeMappedObject;
import com.bosch.caltool.apic.jpa.bo.ParameterClass;
import com.bosch.caltool.apic.jpa.rules.bo.IParameter;
import com.bosch.caltool.apic.jpa.rules.bo.ParamRulesModel;
import com.bosch.caltool.apic.jpa.rules.bo.ParameterSorter;
import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Abstract class for parameters
 *
 * @author adn1cob
 * @param <P> implementation of AbstractParameterAttribute
 */
@Deprecated
public abstract class AbstractParameter<P extends AbstractParameterAttribute> extends AbstractCdrObject
    implements IAttributeMappedObject, IParameter<P> {

  /**
   * Constructor
   *
   * @param dataProvider dataprovider
   * @param objID primary Key
   */
  protected AbstractParameter(final AbstractDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public Set<Attribute> getAttributes() throws SsdInterfaceException {
    // Get the attributes from ParamAttr object
    Set<Attribute> attrSet = new HashSet<>();
    if (getParamAttrs() != null) {
      for (P prmAttr : getParamAttrs()) {
        attrSet.add(prmAttr.getAttribute());
      }
    }
    return attrSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCodeWord() {
    if (isCodeWord()) {
      return ApicConstants.CODE_WORD_YES;
    }
    return ApicConstants.CODE_WORD_NO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getBitWiseRule() {
    if (isBitWise()) {
      return ApicConstants.CODE_WORD_YES;
    }
    return ApicConstants.CODE_WORD_NO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Description is same as long name
    return getLongName();
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public CDRRule getReviewRule() throws SsdInterfaceException {
    // Icdm-500
    // Icdm-1191 - If the Param has dependency return null
    if (hasDependencies()) {
      return null;
    }
    // Else get the Rule from the Cache- Since no dep it has only one rule
    final List<CDRRule> rulesList = getReviewRuleList();
    if ((rulesList != null) && (!rulesList.isEmpty())) {
      return rulesList.get(0);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public CDRRule getDefaultRule() throws SsdInterfaceException {
    // Return the default rule if there is dep
    if (hasDependencies() && (null != getReviewRuleList())) {
      for (CDRRule cdrRule : getReviewRuleList()) {
        if ((cdrRule.getDependencyList() == null) || cdrRule.getDependencyList().isEmpty()) {
          return cdrRule;
        }
      }
    }
    // Return null if there is no dep
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public boolean canModifyAttributeMapping() throws SsdInterfaceException {
    // Attribute mapping can be modified, if there are no rules present with dependency attributes
    List<CDRRule> rulesList = getReviewRuleList();
    if ((null != rulesList) && (!rulesList.isEmpty())) {
      for (CDRRule cdrRule : rulesList) {
        if ((null != cdrRule.getDependencyList()) && !cdrRule.getDependencyList().isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws SsdInterfaceException
   */
  @Override
  public boolean hasDependencies() throws SsdInterfaceException {
    // If parameter attributes are present, then parameter have dependencies
    return (getParamAttrs() != null) && !getParamAttrs().isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLongName() {

    // If the description is not available in the client language set during the data model initialization,
    // the description in the other language is returned

    return ApicUtil.getLangSpecTxt(getDataCache().getLanguage(), getLongNameEng(), getLongNameGer(),
        ApicConstants.EMPTY_STRING);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getpClassText() {
    // Get the string representation of the parameter class
    final ParameterClass pClass = getpClass();
    if (pClass == null) {
      return "";
    }
    return pClass.getText();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SortedSet<CDRRule> getRulesSet() {
    // return CDRRuleUtil.sortRulesWithAttrDependency(getReviewRuleList(), getApicDataProvider());
    return null;
  }

  /**
   * {@inheritDoc}
   *
   * @throws SsdInterfaceException
   */
  @Override
  public int compareTo(final IParameter<P> param2, final SortColumns sortColumn) throws SsdInterfaceException {
    return ParameterSorter.compare(this, param2, sortColumn);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ParamRulesModel getParamRulesModel() {
    // A new instance of the rule model
    return new ParamRulesModel(getApicDataProvider(), this);
  }


}

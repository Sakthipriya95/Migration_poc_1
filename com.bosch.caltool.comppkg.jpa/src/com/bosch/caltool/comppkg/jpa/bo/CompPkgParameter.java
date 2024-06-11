/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comppkg.jpa.bo;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.ParameterClass;
import com.bosch.caltool.apic.jpa.rules.bo.IParameter;
import com.bosch.caltool.apic.jpa.rules.bo.ParamRulesModel;
import com.bosch.caltool.apic.jpa.rules.bo.ParameterSorter;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRFuncParameter;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * Parameter of a component package
 *
 * @author bne4cob
 */
@Deprecated
public class CompPkgParameter extends AbstractCPObject
    implements IParameter<CompPkgRuleAttr>, Comparable<CompPkgParameter> {

  /**
   * Component package
   */
  private final CompPkg compPkg;


  /**
   * @param dataProvider CPDataProvider
   * @param compPkg CompPkg
   * @param objID parameter ID
   */
  protected CompPkgParameter(final CPDataProvider dataProvider, final CompPkg compPkg, final Long objID) {
    super(dataProvider, objID);
    this.compPkg = compPkg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<CompPkgRuleAttr> getParamAttrs() {
    return new HashSet<>(this.compPkg.getCompPkgRuleAttrMap().values());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<Attribute> getAttributes() {
    return this.compPkg.getAttributes();
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
   */
  @Override
  public List<CDRRule> getReviewRuleList() {
    return getApicDataProvider().getRulesForParam(getCompPkg().getCaldataImporterObject().getSsdNodeID(),
        getCompPkg().getID(), getName(), getType());
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
   * {@inheritDoc}
   */
  @Override
  public String getParamHint() {
    return getFuncParameter().getParamHint();
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
  public String getDescription() {
    // get the parameter description
    return getFuncParameter().getDescription();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRule getReviewRule() {
    // If the Param has dependency return null
    if (hasDependencies()) {
      return null;
    }
    // Else get the Rule from the Cache- Since no dep it has, only one rule
    final List<CDRRule> rulesList = getReviewRuleList();
    if ((rulesList != null) && (!rulesList.isEmpty())) {
      return rulesList.get(0);
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRule getDefaultRule() {
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
   */
  @Override
  public boolean canModifyAttributeMapping() {
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
   */
  @Override
  public boolean hasDependencies() {
    return (getParamAttrs() != null) && !getParamAttrs().isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLongName() {
    return getFuncParameter().getLongName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getpClassText() {
    final ParameterClass pClass = getpClass();
    return pClass == null ? "" : pClass.getText();
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
  public int compareTo(final IParameter<CompPkgRuleAttr> param2, final SortColumns sortColumn)
      throws SsdInterfaceException {
    return ParameterSorter.compare(this, param2, sortColumn);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getFuncParameter().getName();
  }

  /**
   * Get the CDRFuncParameter object
   *
   * @return CDR function parameter for this rule set parameter
   */
  private CDRFuncParameter getFuncParameter() {
    // Since component package does not have direct parameter mapping, Tparameter.id is used as id for comppkgparametr
    return getCdrDataProvider().getCDRFuncParameter(getID());
  }

  /**
   * @return CompPkg
   */
  private CompPkg getCompPkg() {
    return this.compPkg;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    // The underlying entity is TParameter, which is already mapped to CdrFuncParam BO
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getFuncParameter().getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getFuncParameter().getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return getFuncParameter().getCreatedDate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return getFuncParameter().getModifiedDate();
  }

  /**
   * Returns APIC data provider
   *
   * @return ApicDataProvider
   */
  private CDRDataProvider getCdrDataProvider() {
    return getDataProvider().getCdrDataProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ParamRulesModel getParamRulesModel() {
    return new ParamRulesModel(getApicDataProvider(), this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkgParameter compPkgParameter) {
    return ParameterSorter.compare(this, compPkgParameter);
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
  public String getSsdClass() {
    return getFuncParameter().getSsdClass();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SSD_CLASS getSsdClassEnum() {

    return getFuncParameter().getSsdClassEnum();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isComplianceParameter() {
    return getFuncParameter().isComplianceParameter();
  }
}

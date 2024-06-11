/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.bosch.caltool.apic.jpa.bo.ParameterClass;
import com.bosch.caltool.apic.jpa.rules.bo.ParameterSorter;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.ssd.icdm.model.CDRRule;


/**
 * CDRFuncParameter.java, This class is the business object of the Calibration data review PARAMETER
 *
 * @author adn1cob
 */
// iCDM-471
@Deprecated
public class CDRFuncParameter extends AbstractParameter<ParameterAttribute> implements Comparable<CDRFuncParameter> {

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * This Constructor is protected and triggered from DataCache/DataLoader only
   *
   * @param dataProvider data provider
   * @param paramId ID
   */
  public CDRFuncParameter(final CDRDataProvider dataProvider, final long paramId) {
    super(dataProvider, paramId);
  }

  /**
   * Get the parameter name
   *
   * @return the name
   */
  @Override
  public String getName() {
    return getEntityProvider().getDbFunctionParameter(getID()).getName();
  }

  /**
   * get the parameter type
   *
   * @return the type
   */
  @Override
  public String getType() {
    return getEntityProvider().getDbFunctionParameter(getID()).getPtype();
  }

  /**
   * ICDM-1113
   *
   * @return the longName
   */
  @Override
  public String getLongNameEng() {
    return getEntityProvider().getDbFunctionParameter(getID()).getLongname();
  }

  /**
   * ICDM-1113
   *
   * @return the longName
   */
  @Override
  public String getLongNameGer() {
    return getEntityProvider().getDbFunctionParameter(getID()).getLongnameGer();
  }

  /**
   * @return String the hint Value
   */
  @Override
  public String getParamHint() {
    return getEntityProvider().getDbFunctionParameter(getID()).getHint();
  }

  /**
   * @return the pClass
   */
  @Override
  public ParameterClass getpClass() {
    return ParameterClass.getParamClass(getEntityProvider().getDbFunctionParameter(getID()).getPclass());
  }


  /**
   * @return the isCodeWord
   */
  @Override
  public boolean isCodeWord() {
    return ApicConstants.YES.equals(getEntityProvider().getDbFunctionParameter(getID()).getIscodeword()) ? true : false;
  }


  /**
   * @return the isCustPrm
   */
  public boolean isCustPrm() {
    return getEntityProvider().getDbFunctionParameter(getID()).getIscustprm().equalsIgnoreCase(ApicConstants.YES) ? true
        : false;

  }

  /**
   * @return the ssd class String
   */
  @Override
  public String getSsdClass() {
    return getEntityProvider().getDbFunctionParameter(getID()).getSsdClass();
  }

  /**
   * @return the ssd class enum
   */
  @Override
  public SSD_CLASS getSsdClassEnum() {
    String ssdClassDb = getEntityProvider().getDbFunctionParameter(getID()).getSsdClass();
    return SSD_CLASS.getSsdClass(ssdClassDb);
  }

  /**
   * @return the ssd class String
   */
  @Override
  public boolean isComplianceParameter() {
    return getSsdClassEnum().isCompliant();
  }

  /**
   * @return Created Date
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFunctionParameter(getID()).getCreatedDate());
  }

  /**
   * @return Modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbFunctionParameter(getID()).getModifiedDate());
  }

  /**
   * @return the createdUser
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbFunctionParameter(getID()).getCreatedUser();

  }

  /**
   * @return the modifiedUser
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbFunctionParameter(getID()).getModifiedUser();

  }


  // ICDM-1070
  /**
   * @return CDRRule object
   */
  @Override
  public List<CDRRule> getReviewRuleList() {
    final List<CDRRule> rulesList = getApicDataProvider().getParamCDRRules(getName());
    if ((rulesList != null) && (!rulesList.isEmpty())) {
      return rulesList;
    }

    return rulesList;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRFuncParameter other) {
    return ParameterSorter.compare(this, other);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    CDRFuncParameter other = (CDRFuncParameter) obj;
    return CommonUtils.isEqual(getID(), other.getID());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getID() == null) ? 0 : getID().hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_FUNC_PARAM;
  }

  /**
   * @return the param-attrs for a Cdr paramter
   */
  @Override
  public Set<ParameterAttribute> getParamAttrs() {
    return getDataCache().getParamDependencyMap().get(getID());
  }

  /**
   * @return
   */
  public boolean isBitWise() {
    return ApicConstants.YES.equals(getEntityProvider().getDbFunctionParameter(getID()).getIsbitwise()) ? true : false;
  }

}

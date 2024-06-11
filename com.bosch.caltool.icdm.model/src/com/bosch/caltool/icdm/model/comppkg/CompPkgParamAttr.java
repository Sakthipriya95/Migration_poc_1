/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;

import com.bosch.caltool.icdm.model.cdr.AbstractParameterAttribute;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author dmr1cob
 */
public class CompPkgParamAttr extends AbstractParameterAttribute implements Comparable<CompPkgParamAttr> {

  /**
   *
   */
  private static final long serialVersionUID = 4684859104097825888L;

  private Long cpRuleAttrId;

  private Long compPkgId;

  private Long attrId;


  /**
   * @return the cpRuleAttrId
   */
  public Long getCpRuleAttrId() {
    return this.cpRuleAttrId;
  }


  /**
   * @param cpRuleAttrId the cpRuleAttrId to set
   */
  public void setCpRuleAttrId(final Long cpRuleAttrId) {
    this.cpRuleAttrId = cpRuleAttrId;
  }


  /**
   * @return the compPkgId
   */
  public Long getCompPkgId() {
    return this.compPkgId;
  }


  /**
   * @param compPkgId the compPkgId to set
   */
  public void setCompPkgId(final Long compPkgId) {
    this.compPkgId = compPkgId;
  }


  /**
   * @return the attrId
   */
  @Override
  public Long getAttrId() {
    return this.attrId;
  }


  /**
   * @param attrId the attrId to set
   */
  @Override
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getParamId() {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setParamId(final Long paramId) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CompPkgParamAttr other) {
    return ModelUtil.compare(getAttrId(), other.getAttrId());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    // If the object is not saved in the database then adding to set has problems
    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((CompPkgParamAttr) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((CompPkgParamAttr) obj).getName());
    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author rgo7cob
 */
public class RuleSetParameterAttr extends AbstractParameterAttribute implements Comparable<RuleSetParameterAttr> {

  /**
   *
   */
  private static final long serialVersionUID = 2629926832697304423L;

  private Long id;

  private Long version;

  private Long paramId;

  private Long attrId;
  private Long ruleSetId;

  private Long ruleSetParamId;

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getAttrId() {
    return this.attrId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getParamId() {
    return this.paramId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long id) {
    this.id = id;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;

  }


  /**
   * @return the ruleSetId
   */
  public Long getRuleSetId() {
    return this.ruleSetId;
  }


  /**
   * @param ruleSetId the ruleSetId to set
   */
  public void setRuleSetId(final Long ruleSetId) {
    this.ruleSetId = ruleSetId;
  }


  /**
   * @return the ruleSetParamId
   */
  public Long getRuleSetParamId() {
    return this.ruleSetParamId;
  }


  /**
   * @param ruleSetParamId the ruleSetParamId to set
   */
  public void setRuleSetParamId(final Long ruleSetParamId) {
    this.ruleSetParamId = ruleSetParamId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RuleSetParameterAttr paramAttr) {
    return ModelUtil.compare(getName(), paramAttr.getName());
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
      return ModelUtil.isEqual(getId(), ((RuleSetParameterAttr) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((RuleSetParameterAttr) obj).getName());
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

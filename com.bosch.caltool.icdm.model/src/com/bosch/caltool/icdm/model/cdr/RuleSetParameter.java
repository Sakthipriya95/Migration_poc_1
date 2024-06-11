/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author rgo7cob
 */
public class RuleSetParameter extends AbstractParameter implements Comparable<RuleSetParameter> {

  /**
   *
   */
  private static final long serialVersionUID = 5299047217041720989L;
  private Long id;
  private String name;
  private String description;

  private Long version;


  private String longName;
  private String createdDate;
  private String createdUser;
  private String modifiedUser;
  private String modifiedDate;


  private Long ruleSetId;

  private Long funcId;

  private Long paramId;


  private String pClassText;

  private String custPrm;

  private String paramType;

  private String paramResp;
  
  private String sysElement;
  
  private String hwComponent;


  /**
   * {@inheritDoc}
   */
  @Override
  public String getpClassText() {
    return this.pClassText;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedDate() {
    return this.createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCreatedDate(final String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setModifiedDate(final String modifiedDate) {
    this.modifiedDate = modifiedDate;
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
  public void setId(final Long objId) {
    this.id = objId;
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
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(final String name) {
    this.name = name;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {

    return this.description;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the longName
   */
  @Override
  public String getLongName() {
    return this.longName;
  }


  /**
   * @param longName the longName to set
   */
  @Override
  public void setLongName(final String longName) {
    this.longName = longName;
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
   * @return the funcId
   */
  public Long getFuncId() {
    return this.funcId;
  }


  /**
   * @param funcId the funcId to set
   */
  public void setFuncId(final Long funcId) {
    this.funcId = funcId;
  }


  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }


  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }


  /**
   * @return the custPrm
   */
  public String getCustPrm() {
    return this.custPrm;
  }


  /**
   * @param custPrm the custPrm to set
   */
  public void setCustPrm(final String custPrm) {
    this.custPrm = custPrm;
  }


  /**
   * @param pClassText the pClassText to set
   */
  public void setpClassText(final String pClassText) {
    this.pClassText = pClassText;
  }


  /**
   * @return the paramType
   */
  public String getParamType() {
    return this.paramType;
  }


  /**
   * @param paramType the paramType to set
   */
  public void setParamType(final String paramType) {
    this.paramType = paramType;
  }


  /**
   * @return the paramResp
   */
  public String getParamResp() {
    return this.paramResp;
  }


  /**
   * @param paramResp the paramResp to set
   */
  public void setParamResp(final String paramResp) {
    this.paramResp = paramResp;
  }


  /**
   * @return the sysElement
   */
  public String getSysElement() {
    return this.sysElement;
  }


  /**
   * @param sysElement the sysElement to set
   */
  public void setSysElement(final String sysElement) {
    this.sysElement = sysElement;
  }


  /**
   * @return the hwComponent
   */
  public String getHwComponent() {
    return this.hwComponent;
  }


  /**
   * @param hwComponent the hwComponent to set
   */
  public void setHwComponent(final String hwComponent) {
    this.hwComponent = hwComponent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RuleSetParameter object) {
    int compareResult = ModelUtil.compare(getName(), object.getName());
    if (compareResult == 0) {
      compareResult = ModelUtil.compare(getId(), object.getId());
    }
    return compareResult;
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
      return ModelUtil.isEqual(getId(), ((RuleSetParameter) obj).getId()) &&
          ModelUtil.isEqual(getName(), ((RuleSetParameter) obj).getName());
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

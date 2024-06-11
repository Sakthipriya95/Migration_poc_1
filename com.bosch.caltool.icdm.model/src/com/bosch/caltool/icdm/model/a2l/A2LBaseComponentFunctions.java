/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.math.BigDecimal;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * @author dmo5cob
 */
public class A2LBaseComponentFunctions implements Comparable<A2LBaseComponentFunctions>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 5074871069131320061L;

  private BigDecimal functionId;

  private String functionversion;

  private String longidentifier;

  private BigDecimal moduleId;

  private String name;

  private BigDecimal sdomBcId;


  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;


  /**
   * @return the functionId
   */
  public BigDecimal getFunctionId() {
    return this.functionId;
  }


  /**
   * @return the functionversion
   */
  public String getFunctionversion() {
    return this.functionversion;
  }


  /**
   * @return the longidentifier
   */
  public String getLongidentifier() {
    return this.longidentifier;
  }


  /**
   * @return the moduleId
   */
  public BigDecimal getModuleId() {
    return this.moduleId;
  }


  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }


  /**
   * @return the sdomBcId
   */
  public BigDecimal getSdomBcId() {
    return this.sdomBcId;
  }


  /**
   * @param functionId the functionId to set
   */
  public void setFunctionId(final BigDecimal functionId) {
    this.functionId = functionId;
  }


  /**
   * @param functionversion the functionversion to set
   */
  public void setFunctionversion(final String functionversion) {
    this.functionversion = functionversion;
  }


  /**
   * @param longidentifier the longidentifier to set
   */
  public void setLongidentifier(final String longidentifier) {
    this.longidentifier = longidentifier;
  }


  /**
   * @param moduleId the moduleId to set
   */
  public void setModuleId(final BigDecimal moduleId) {
    this.moduleId = moduleId;
  }


  /**
   * @param name the name to set
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * @param sdomBcId the sdomBcId to set
   */
  public void setSdomBcId(final BigDecimal sdomBcId) {
    this.sdomBcId = sdomBcId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LBaseComponentFunctions arg0) {

    return ModelUtil.compare(getName(), arg0.getName());
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
    A2LBaseComponentFunctions a2lBaseCompFunc = (A2LBaseComponentFunctions) obj;
    if (getName() == null) {
      return null == a2lBaseCompFunc.getName();
    }
    return getName().equals(a2lBaseCompFunc.getName());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getName() == null) ? 0 : getName().hashCode());
    return result;
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

}

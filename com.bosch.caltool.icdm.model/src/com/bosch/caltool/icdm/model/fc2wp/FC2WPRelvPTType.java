/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.fc2wp;

import java.util.Objects;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author gge6cob
 */
public class FC2WPRelvPTType implements IModel, Comparable<FC2WPRelvPTType> {

  /**
   *
   */
  private static final long serialVersionUID = -5508164746302425374L;
  private Long id;
  private Long fcwpDefId;
  private Long ptTypeId;
  private String ptTypeName;
  private String ptTypeDesc;

  private Long version;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;

  /**
   * @return the fcwpPtTypeRelvId
   */
  @Override
  public Long getId() {
    return this.id;
  }

  /**
   * @param fcwpPtTypeRelvId the fcwpPtTypeRelvId to set
   */
  @Override
  public void setId(final Long fcwpPtTypeRelvId) {
    this.id = fcwpPtTypeRelvId;
  }

  /**
   * @return the fcwpDefId
   */
  public Long getFcwpDefId() {
    return this.fcwpDefId;
  }

  /**
   * @param fcwpDefId the fcwpDefId to set
   */
  public void setFcwpDefId(final Long fcwpDefId) {
    this.fcwpDefId = fcwpDefId;
  }

  /**
   * @return the ptTypeId
   */
  public Long getPtTypeId() {
    return this.ptTypeId;
  }

  /**
   * @param ptTypeId the ptTypeId to set
   */
  public void setPtTypeId(final Long ptTypeId) {
    this.ptTypeId = ptTypeId;
  }

  /**
   * @return the ptTypeName
   */
  public String getPtTypeName() {
    return this.ptTypeName;
  }

  /**
   * @param ptTypeName the ptTypeName to set
   */
  public void setPtTypeName(final String ptTypeName) {
    this.ptTypeName = ptTypeName;
  }

  /**
   * @return the ptTypeDesc
   */
  public String getPtTypeDesc() {
    return this.ptTypeDesc;
  }

  /**
   * @param ptTypeDesc the ptTypeDesc to set
   */
  public void setPtTypeDesc(final String ptTypeDesc) {
    this.ptTypeDesc = ptTypeDesc;
  }


  /**
   * @return the version
   */
  @Override
  public Long getVersion() {
    return this.version;
  }


  /**
   * @param version the version to set
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final FC2WPRelvPTType obj) {
    // compare using name
    return ModelUtil.compare(getPtTypeName(), obj.getPtTypeName());
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
    FC2WPRelvPTType other = (FC2WPRelvPTType) obj;
    // compare using name
    return Objects.equals(getPtTypeName(), other.getPtTypeName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getPtTypeName() == null) ? 0 : getPtTypeName().hashCode());
    return result;
  }
}

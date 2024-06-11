/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;

import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.util.CloneNotSupportedRuntimeException;

/**
 * @author say8cob
 */

public class CompPackage extends ParamCollection implements Cloneable {

  /**
   *
   */
  private static final long serialVersionUID = 6286179894718136940L;

  private String compPkgType;

  private String descEng;

  private String descGer;

  private Long ssdNodeId;

  private String ssdParamClass;

  private String ssdUsecase;

  private Long ssdVersNodeId;

  private boolean deleted;


  /**
   * @return the compPkgType
   */
  public String getCompPkgType() {
    return this.compPkgType;
  }


  /**
   * @param compPkgType the compPkgType to set
   */
  public void setCompPkgType(final String compPkgType) {
    this.compPkgType = compPkgType;
  }


  /**
   * @return the descEng
   */
  public String getDescEng() {
    return this.descEng;
  }


  /**
   * @param descEng the descEng to set
   */
  public void setDescEng(final String descEng) {
    this.descEng = descEng;
  }


  /**
   * @return the descGer
   */
  public String getDescGer() {
    return this.descGer;
  }


  /**
   * @param descGer the descGer to set
   */
  public void setDescGer(final String descGer) {
    this.descGer = descGer;
  }


  /**
   * @return the ssdNodeId
   */
  public Long getSsdNodeId() {
    return this.ssdNodeId;
  }


  /**
   * @param ssdNodeId the ssdNodeId to set
   */
  public void setSsdNodeId(final Long ssdNodeId) {
    this.ssdNodeId = ssdNodeId;
  }


  /**
   * @return the ssdParamClass
   */
  public String getSsdParamClass() {
    return this.ssdParamClass;
  }


  /**
   * @param ssdParamClass the ssdParamClass to set
   */
  public void setSsdParamClass(final String ssdParamClass) {
    this.ssdParamClass = ssdParamClass;
  }

  /**
   * @return the ssdUsecase
   */
  public String getSsdUsecase() {
    return this.ssdUsecase;
  }


  /**
   * @param ssdUsecase the ssdUsecase to set
   */
  public void setSsdUsecase(final String ssdUsecase) {
    this.ssdUsecase = ssdUsecase;
  }


  /**
   * @return the ssdVersNodeId
   */
  public Long getSsdVersNodeId() {
    return this.ssdVersNodeId;
  }


  /**
   * @param ssdVersNodeId the ssdVersNodeId to set
   */
  public void setSsdVersNodeId(final Long ssdVersNodeId) {
    this.ssdVersNodeId = ssdVersNodeId;
  }


  /**
   * @return the deletedFlag
   */
  public boolean isDeleted() {
    return this.deleted;
  }

  /**
   * @param deleted the deletedFlag to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getDescEng();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    // No addditional implementation required
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    // No addditional implementation required
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompPackage clone() {
    try {
      return (CompPackage) super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new CloneNotSupportedRuntimeException(e);
    }
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.fc2wp;

import com.bosch.caltool.datamodel.core.IModel;

/**
 * @author bne4cob
 */
public class FC2WPMapPtType implements IModel {

  /**
   *
   */
  private static final long serialVersionUID = 3514225508199561436L;


  private Long id;


  private Long fcwpMapId;
  private Long ptTypeId;
  private String ptTypeName;
  private String ptTypeDesc;

  private Long version;


  /**
   * @return the fcwpMapPtTypeId
   */
  @Override
  public Long getId() {
    return this.id;
  }


  /**
   * @param fcwpMapPtTypeId the fcwpMapPtTypeId to set
   */
  @Override
  public void setId(final Long fcwpMapPtTypeId) {
    this.id = fcwpMapPtTypeId;
  }


  /**
   * @return the fcwpMapId
   */
  public Long getFcwpMapId() {
    return this.fcwpMapId;
  }


  /**
   * @param fcwpMapId the fcwpMapId to set
   */
  public void setFcwpMapId(final Long fcwpMapId) {
    this.fcwpMapId = fcwpMapId;
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

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

/**
 * @author gge6cob
 */
public class ImportA2lWpRespInput {

  /**
   *
   */
  private static final long serialVersionUID = -6109027418121737715L;

  /** The wp def vers id. */
  private Long wpDefVersId;

  /** The variant grp id. */
  private Long variantGrpId;

  /** The a 2 l file id. */
  private Long a2lFileId;

  /** The fc 2 wp vers id. */
  private Long fc2wpVersId;

  /** The pidc version id. */
  private Long pidcVersionId;

  /** The can create param mapping. */
  private boolean createParamMapping;


  /**
   * @return the wpDefVersId
   */
  public Long getWpDefVersId() {
    return this.wpDefVersId;
  }


  /**
   * @return the variantGrpId
   */
  public Long getVariantGrpId() {
    return this.variantGrpId;
  }


  /**
   * @return the a2lFileId
   */
  public Long getA2lFileId() {
    return this.a2lFileId;
  }


  /**
   * @return the fc2wpVersId
   */
  public Long getFc2wpVersId() {
    return this.fc2wpVersId;
  }


  /**
   * @return the createParamMapping
   */
  public boolean isCreateParamMapping() {
    return this.createParamMapping;
  }


  /**
   * @param wpDefVersId the wpDefVersId to set
   */
  public void setWpDefVersId(final Long wpDefVersId) {
    this.wpDefVersId = wpDefVersId;
  }


  /**
   * @param variantGrpId the variantGrpId to set
   */
  public void setVariantGrpId(final Long variantGrpId) {
    this.variantGrpId = variantGrpId;
  }


  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final Long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }


  /**
   * @param fc2wpVersId the fc2wpVersId to set
   */
  public void setFc2wpVersId(final Long fc2wpVersId) {
    this.fc2wpVersId = fc2wpVersId;
  }


  /**
   * @param createParamMapping the createParamMapping to set
   */
  public void setCreateParamMapping(final boolean createParamMapping) {
    this.createParamMapping = createParamMapping;
  }


  /**
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


}

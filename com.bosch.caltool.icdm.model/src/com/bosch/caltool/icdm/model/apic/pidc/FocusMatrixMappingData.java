/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author mkl2cob
 */
public class FocusMatrixMappingData {

  private Long useCaseId;

  private Long useCaseSectionId;

  private Long attrId;

  private Long pidcVrsnId;

  /**
   * @return the useCaseId
   */
  public Long getUseCaseId() {
    return this.useCaseId;
  }


  /**
   * @param useCaseId the useCaseId to set
   */
  public void setUseCaseId(final Long useCaseId) {
    this.useCaseId = useCaseId;
  }


  /**
   * @return the useCaseSectionId
   */
  public Long getUseCaseSectionId() {
    return this.useCaseSectionId;
  }


  /**
   * @param useCaseSectionId the useCaseSectionId to set
   */
  public void setUseCaseSectionId(final Long useCaseSectionId) {
    this.useCaseSectionId = useCaseSectionId;
  }


  /**
   * @return the attrId
   */
  public Long getAttrId() {
    return this.attrId;
  }


  /**
   * @param attrId the attrId to set
   */
  public void setAttrId(final Long attrId) {
    this.attrId = attrId;
  }


  /**
   * @return the pidcVrsnId
   */
  public Long getPidcVrsnId() {
    return this.pidcVrsnId;
  }


  /**
   * @param pidcVrsnId the pidcVrsnId to set
   */
  public void setPidcVrsnId(final Long pidcVrsnId) {
    this.pidcVrsnId = pidcVrsnId;
  }


}

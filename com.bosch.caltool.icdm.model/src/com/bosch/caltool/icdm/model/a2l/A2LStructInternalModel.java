/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * @author SAY8COB
 *
 */
public class A2LStructInternalModel {

  private Long pidcVarId;
  
  private Long pidcVersId;
  
  private A2lVariantGroup a2lVarGrp;
  
  private A2lWpDefnVersion activeVersion;

  
  /**
   * @return the pidcVarId
   */
  public Long getPidcVarId() {
    return pidcVarId;
  }

  
  /**
   * @param pidcVarId the pidcVarId to set
   */
  public void setPidcVarId(Long pidcVarId) {
    this.pidcVarId = pidcVarId;
  }

  
  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return pidcVersId;
  }

  
  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }

  
  /**
   * @return the a2lVarGrp
   */
  public A2lVariantGroup getA2lVarGrp() {
    return a2lVarGrp;
  }

  
  /**
   * @param a2lVarGrp the a2lVarGrp to set
   */
  public void setA2lVarGrp(A2lVariantGroup a2lVarGrp) {
    this.a2lVarGrp = a2lVarGrp;
  }

  
  /**
   * @return the activeVersion
   */
  public A2lWpDefnVersion getActiveVersion() {
    return activeVersion;
  }

  
  /**
   * @param activeVersion the activeVersion to set
   */
  public void setActiveVersion(A2lWpDefnVersion activeVersion) {
    this.activeVersion = activeVersion;
  }
  
  
}

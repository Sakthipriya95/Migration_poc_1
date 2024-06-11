/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.math.BigDecimal;


/**
 * model class for the swversion
 * @author mrf5cob
 *
 */
public class SoftwareVersion {
 
  //VILLA swVersionNodeID
  private BigDecimal swVersNodeId;
  //VILLA swProjectNodeId
  private BigDecimal swProjNodeId;
  
  private String swVersionNumber;
  /*Sw VersionName associated with VILLA*/
  private String swVersionName;
  
  /*Sw Version Description associated with SSD*/
  private String swVersionDesc;

  //VILLA swVersionID
  private BigDecimal swVersId;
 

  
  /**
   * @return the swVersionName
   */
  public String getSwVersionName() {
    return swVersionName;
  }

  
  /**
   * @param swVersionName the swVersionName to set
   */
  public void setSwVersionName(String swVersionName) {
    this.swVersionName = swVersionName;
  }

  
  /**
   * @return the swVersionDesc
   */
  public String getSwVersionDesc() {
    return swVersionDesc;
  }

  
  /**
   * @param swVersionDesc the swVersionDesc to set
   */
  public void setSwVersionDesc(String swVersionDesc) {
    this.swVersionDesc = swVersionDesc;
  }


  /**
   * @return the swVersionNumber
   */
  public String getSwVersionNumber() {
    return swVersionNumber;
  }


  /**
   * @param swVersionNumber the swVersionNumber to set
   */
  public void setSwVersionNumber(String swVersionNumber) {
    this.swVersionNumber = swVersionNumber;
  }


  
  /**
   * @return the swVersNodeId
   */
  public BigDecimal getSwVersNodeId() {
    return swVersNodeId;
  }


  
  /**
   * @param swVersNodeId the swVersNodeId to set
   */
  public void setSwVersNodeId(BigDecimal swVersNodeId) {
    this.swVersNodeId = swVersNodeId;
  }


  
  /**
   * @return the swProjNodeId
   */
  public BigDecimal getSwProjNodeId() {
    return swProjNodeId;
  }


  
  /**
   * @param swProjNodeId the swProjNodeId to set
   */
  public void setSwProjNodeId(BigDecimal swProjNodeId) {
    this.swProjNodeId = swProjNodeId;
  }


  /**
   * @return the swVersId
   */
  public BigDecimal getSwVersId() {
    return swVersId;
  }


  /**
   * @param swVersId the swVersId to set
   */
  public void setSwVersId(BigDecimal swVersId) {
    this.swVersId = swVersId;
  }

}

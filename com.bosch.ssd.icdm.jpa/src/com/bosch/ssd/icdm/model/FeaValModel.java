/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * @author apl1cob
 */
public class FeaValModel {

  private BigDecimal featureId;

  private List<BigDecimal> valueIdList;
  
  private BigDecimal operatorId;

  private BigDecimal selValueIdFromIcdm;


  /**
   * @return the featureId
   */
  public BigDecimal getFeatureId() {
    return this.featureId;
  }


  /**
   * @param featureId the featureId to set
   */
  public void setFeatureId(final BigDecimal featureId) {
    this.featureId = featureId;
  }


  /**
   * @return the valueIdList
   */
  public List<BigDecimal> getValueIdList() {
    return new ArrayList<>(this.valueIdList);
  }


  /**
   * @param valueIdList the valueIdList to set
   */
  public void setValueIdList(final List<BigDecimal> valueIdList) {
    this.valueIdList = new ArrayList<>(valueIdList);
  }

  /**
   * @param additionalId extra
   */
  public void addValueIdList(final BigDecimal additionalId) {
    this.valueIdList.add(additionalId);
  }

  /**
   * @return the selValueIdFromIcdm
   */
  public BigDecimal getSelValueIdFromIcdm() {
    return this.selValueIdFromIcdm;
  }


  /**
   * @param selValueIdFromIcdm the selValueIdFromIcdm to set
   * @return - release should not be continued with false
   */
  public boolean setSelValueIdFromIcdm(final BigDecimal selValueIdFromIcdm) {
    if (this.valueIdList.contains(selValueIdFromIcdm)) {
      this.selValueIdFromIcdm = selValueIdFromIcdm;
      return true;
    }
    return false;
  }
  

  /**
   * @return the operatorId
   */
  public BigDecimal getOperatorId() {
    return operatorId;
  }
  
  /**
   * @param operatorId the operatorId to set
   */
  public void setOperatorId(BigDecimal operatorId) {
       this.operatorId = operatorId;
  }

}

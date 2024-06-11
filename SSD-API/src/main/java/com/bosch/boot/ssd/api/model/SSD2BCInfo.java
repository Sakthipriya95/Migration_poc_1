/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author vau3cob
 */
public class SSD2BCInfo {

  private String bcName;

  private BigDecimal bcNumber;

  private Set<String> assignedNodes;

  private BigDecimal bcRevision;

  private String bcStatus;

  private String bcVariant;

  private String varSsdStatus;

  /**
   * @return the assignedNodes
   */
  public Set<String> getAssignedNodes() {
    if(assignedNodes==null) {
      assignedNodes = new HashSet<>();
    }
    return assignedNodes;
  }

  /**
   * @param assignedNodes the assignedNodes to set
   */
  public void setAssignedNodes(Set<String> assignedNodes) {
    this.assignedNodes = assignedNodes;
  }

  public String getBcName() {
    return this.bcName;
  }

  public void setBcName(String bcName) {
    this.bcName = bcName;
  }

  public BigDecimal getBcNumber() {
    return this.bcNumber;
  }

  public void setBcNumber(BigDecimal bcNumber) {
    this.bcNumber = bcNumber;
  }

  public BigDecimal getBcRevision() {
    return this.bcRevision;
  }

  public void setBcRevision(BigDecimal bcRevision) {
    this.bcRevision = bcRevision;
  }

  public String getBcStatus() {
    return this.bcStatus;
  }

  public void setBcStatus(final String ssdStatus) {
    this.bcStatus = ssdStatus;
  }

  public String getBcVariant() {
    return this.bcVariant;
  }

  public void setBcVariant(String bcVariant) {
    this.bcVariant = bcVariant;
  }

  /**
   * @return the varSsdStatus
   */
  public String getVarSsdStatus() {
    return this.varSsdStatus;
  }

  /**
   * @param varSsdStatus the varSsdStatus to set
   */
  public void setVarSsdStatus(final String varSsdStatus) {
    this.varSsdStatus = varSsdStatus;
  }

}

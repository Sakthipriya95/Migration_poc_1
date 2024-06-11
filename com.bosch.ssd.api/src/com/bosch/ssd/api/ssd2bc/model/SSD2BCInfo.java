/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.api.ssd2bc.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author vau3cob
 *
 */
public class SSD2BCInfo {

  private static final long serialVersionUID = 1L;

  private String bcName;

  private BigDecimal bcNumber;

  private Set<String> assignedNodes;

  private BigDecimal bcRevision;

  private String ssdStatus;

  private String bcVariant;

  /**
   * @return the assignedNodes
   */
  public Set<String> getAssignedNodes() {
    if(assignedNodes==null) {
      assignedNodes = new HashSet<String>();
    }
    return assignedNodes;
  }
  
  
  /**
   * @param assignedNodes the assignedNodes to set
   */
  @XmlElement
  public void setAssignedNodes(Set<String> assignedNodes) {
    this.assignedNodes = assignedNodes;
  }

  public String getBcName() {
    return this.bcName;
  }

  @XmlElement
  public void setBcName(String bcName) {
    this.bcName = bcName;
  }

  public BigDecimal getBcNumber() {
    return this.bcNumber;
  }

  @XmlElement
  public void setBcNumber(BigDecimal bcNumber) {
    this.bcNumber = bcNumber;
  }

  

  public BigDecimal getBcRevision() {
    return this.bcRevision;
  }

  @XmlElement
  public void setBcRevision(BigDecimal bcRevision) {
    this.bcRevision = bcRevision;
  }

  public String getSsdStatus() {
    return this.ssdStatus;
  }

  @XmlElement
  public void setSsdStatus(String ssdStatus) {
    this.ssdStatus = ssdStatus;
  }

  public String getBcVariant() {
    return this.bcVariant;
  }

  @XmlElement
  public void setBcVariant(String bcVariant) {
    this.bcVariant = bcVariant;
  }


}

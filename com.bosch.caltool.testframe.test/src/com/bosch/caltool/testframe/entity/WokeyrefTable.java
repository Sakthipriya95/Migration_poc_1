/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.testframe.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;


/**
 * The persistent class for the TEST_WOKEYREF_TABLE database table.
 */
@Entity
@Table(name = "TEST_WOKEYREF_TABLE")
public class WokeyrefTable implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TEMP_GEN")
  @Column(name = "WKRT_ID", unique = true, nullable = false)
  private long wkrtId;

  @Column(name = "NODE_ID", nullable = false)
  private BigDecimal nodeId;

  @Column(name = "REC_TYPE", nullable = false, length = 200)
  private String recType;

  @Column(name = "REF_NAME", length = 50)
  private String refName;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;

  public WokeyrefTable() {}

  public long getWkrtId() {
    return this.wkrtId;
  }

  public void setWkrtId(final long wkrtId) {
    this.wkrtId = wkrtId;
  }

  public BigDecimal getNodeId() {
    return this.nodeId;
  }

  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }

  public String getRecType() {
    return this.recType;
  }

  public void setRecType(final String recType) {
    this.recType = recType;
  }

  public String getRefName() {
    return this.refName;
  }

  public void setRefName(final String refName) {
    this.refName = refName;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

}
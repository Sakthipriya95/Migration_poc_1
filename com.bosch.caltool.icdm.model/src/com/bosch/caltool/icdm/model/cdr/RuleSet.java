/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

/**
 * @author rgo7cob
 */
public class RuleSet extends ParamCollection {


  /**
   *
   */
  private static final long serialVersionUID = -6102832901951767279L;

  private String descEng;

  private String descGer;


  private Long ssdNodeId;

  private boolean deleted;

  private boolean readAccessFlag;

  private Long attrValId;

  private Long version;


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.version = version;
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
   * @return the deleted
   */
  public boolean isDeleted() {
    return this.deleted;
  }


  /**
   * @param deleted the deleted to set
   */
  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * @return the attrValId
   */
  public Long getAttrValId() {
    return this.attrValId;
  }


  /**
   * @param attrValId the attrValId to set
   */
  public void setAttrValId(final Long attrValId) {
    this.attrValId = attrValId;
  }


  /**
   * @return the readAccessFlag
   */
  public boolean isReadAccessFlag() {
    return this.readAccessFlag;
  }


  /**
   * @param readAccessFlag the readAccessFlag to set
   */
  public void setReadAccessFlag(final boolean readAccessFlag) {
    this.readAccessFlag = readAccessFlag;
  }

}

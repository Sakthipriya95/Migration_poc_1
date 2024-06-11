/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.util.Date;

@Deprecated
/**
 * A ICDM specific Class representing a DST Revision in VCDM
 *
 * @author jvi6cob
 */
public class VCDMDSTRevision {

  private Long dstID;

  private BigDecimal revisionNo;

  private Date createdDate;

  private String versionName;

  /**
   * @return the dstID
   */
  public Long getDstID() {
    return this.dstID;
  }


  /**
   * @param dstID the dstID to set
   */
  public void setDstID(final Long dstID) {
    this.dstID = dstID;
  }


  /**
   * @return the revisionNo
   */
  public BigDecimal getRevisionNo() {
    return this.revisionNo;
  }


  /**
   * @param revisionNo the revisionNo to set
   */
  public void setRevisionNo(final BigDecimal revisionNo) {
    this.revisionNo = revisionNo;
  }


  /**
   * @return the createdDate
   */
  public Date getCreatedDate() {
    return this.createdDate;
  }


  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(final Date createdDate) {
    this.createdDate = createdDate;
  }


  /**
   * @return the versionName
   */
  public String getVersionName() {
    return this.versionName;
  }


  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(final String versionName) {
    this.versionName = versionName;
  }


}

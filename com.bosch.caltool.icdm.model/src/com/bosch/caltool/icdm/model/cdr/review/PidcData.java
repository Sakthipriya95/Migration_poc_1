/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr.review;


/**
 * @author bru2cob
 */
public class PidcData {

  /**
   * Pidc a2l id
   */
  private Long pidcA2lId;

  private long a2lFileId;

  /**
   * Selected pidc variant id
   */
  private Long selPIDCVariantId;
  /**
   * selected source pidc vers id
   */
  private Long sourcePidcVerId;
  /**
   * selected source pidc variant id
   */
  private Long sourcePIDCVariantId;


  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @return the selPIDCVariantId
   */
  public Long getSelPIDCVariantId() {
    return this.selPIDCVariantId;
  }

  /**
   * @param selPIDCVariantId the selPIDCVariantId to set
   */
  public void setSelPIDCVariantId(final Long selPIDCVariantId) {
    this.selPIDCVariantId = selPIDCVariantId;
  }

  /**
   * @return the sourcePidcVerId
   */
  public Long getSourcePidcVerId() {
    return this.sourcePidcVerId;
  }

  /**
   * @param sourcePidcVerId the sourcePidcVerId to set
   */
  public void setSourcePidcVerId(final Long sourcePidcVerId) {
    this.sourcePidcVerId = sourcePidcVerId;
  }

  /**
   * @return the sourcePIDCVariantId
   */
  public Long getSourcePIDCVariantId() {
    return this.sourcePIDCVariantId;
  }

  /**
   * @param sourcePIDCVariantId the sourcePIDCVariantId to set
   */
  public void setSourcePIDCVariantId(final Long sourcePIDCVariantId) {
    this.sourcePIDCVariantId = sourcePIDCVariantId;
  }


  /**
   * @return the a2lFileId
   */
  public long getA2lFileId() {
    return this.a2lFileId;
  }


  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }


}

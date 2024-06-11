/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic;

/**
 * @author and4cob
 */
public class PidcA2lDetails {

  private String pidcVersionName;
  private Long pidcVersionId;
  private String pverName;
  private String pverVariant;
  private Long pverRevision;
  private String pidcVariantName;
  private Long pidcVariantId;

  private String pidcA2lName;
  private Long pidcA2lId;


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
   * @return pidc version name
   */
  public String getPidcVersionName() {
    return this.pidcVersionName;
  }

  /**
   * @param pidcVersionName the pidc version name to set
   */
  public void setPidcVersionName(final String pidcVersionName) {
    this.pidcVersionName = pidcVersionName;
  }

  /**
   * @return pidc version id
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }

  /**
   * @param pidcVersionId the pidc version id to set
   */
  public void setPidcVersionId(final long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }

  /**
   * @return the pverRevision
   */
  public Long getPverRevision() {
    return this.pverRevision;
  }


  /**
   * @param pverRevision the pverRevision to set
   */
  public void setPverRevision(final Long pverRevision) {
    this.pverRevision = pverRevision;
  }

  /**
   * @return pvername
   */
  public String getPverName() {
    return this.pverName;
  }

  /**
   * @param pverName the pver name to set
   */
  public void setPverName(final String pverName) {
    this.pverName = pverName;
  }

  /**
   * @return pidc variant name
   */
  public String getPidcVariantName() {
    return this.pidcVariantName;
  }

  /**
   * @param pidcVariantName the pidc variant name to set
   */
  public void setPidcVariantName(final String pidcVariantName) {
    this.pidcVariantName = pidcVariantName;
  }

  /**
   * @return pidc variant id
   */
  public Long getPidcVariantId() {
    return this.pidcVariantId;
  }

  /**
   * @param pidcVariantId the pidc variant id to set
   */
  public void setPidcVariantId(final Long pidcVariantId) {
    this.pidcVariantId = pidcVariantId;
  }

  /**
   * @return pver variant name
   */
  public String getPverVariant() {
    return this.pverVariant;
  }

  /**
   * @param pverVariant the pver variant to set
   */
  public void setPverVariant(final String pverVariant) {
    this.pverVariant = pverVariant;
  }


  /**
   * @return the pidcA2lName
   */
  public String getPidcA2lName() {
    return this.pidcA2lName;
  }


  /**
   * @param pidcA2lName the pidcA2lName to set
   */
  public void setPidcA2lName(final String pidcA2lName) {
    this.pidcA2lName = pidcA2lName;
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class PidcVersionType {

  private long pidcVersionId;

  private long proRevId;

  private String longName;

  private String description;

  private String descriptionE;

  private String descriptionG;

  private String versionStatus;

  private boolean isActive;

  private long changeNumber;


  /**
   * @return the pidcVersionId
   */
  public long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


  /**
   * @return the proRevId
   */
  public long getProRevId() {
    return this.proRevId;
  }


  /**
   * @param proRevId the proRevId to set
   */
  public void setProRevId(final long proRevId) {
    this.proRevId = proRevId;
  }


  /**
   * @return the longName
   */
  public String getLongName() {
    return this.longName;
  }


  /**
   * @param longName the longName to set
   */
  public void setLongName(final String longName) {
    this.longName = longName;
  }


  /**
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   * @return the descriptionE
   */
  public String getDescriptionE() {
    return this.descriptionE;
  }


  /**
   * @param descriptionE the descriptionE to set
   */
  public void setDescriptionE(final String descriptionE) {
    this.descriptionE = descriptionE;
  }


  /**
   * @return the descriptionG
   */
  public String getDescriptionG() {
    return this.descriptionG;
  }


  /**
   * @param descriptionG the descriptionG to set
   */
  public void setDescriptionG(final String descriptionG) {
    this.descriptionG = descriptionG;
  }


  /**
   * @return the versionStatus
   */
  public String getVersionStatus() {
    return this.versionStatus;
  }


  /**
   * @param versionStatus the versionStatus to set
   */
  public void setVersionStatus(final String versionStatus) {
    this.versionStatus = versionStatus;
  }


  /**
   * @return the isActive
   */
  public boolean isActive() {
    return this.isActive;
  }


  /**
   * @param isActive the isActive to set
   */
  public void setActive(final boolean isActive) {
    this.isActive = isActive;
  }


  /**
   * @return the changeNumber
   */
  public long getChangeNumber() {
    return this.changeNumber;
  }


  /**
   * @param changeNumber the changeNumber to set
   */
  public void setChangeNumber(final long changeNumber) {
    this.changeNumber = changeNumber;
  }

}

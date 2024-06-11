/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author mkl2cob
 */
public class PidcVersionWithDetailsInput {

  /**
   * pidc version id
   */
  private Long pidcVersionId;

  /**
   * whether pidc version info needed
   */
  private boolean pidcVersionInfoNeeded = true;
  /**
   * whether alias definition model needed
   */
  private boolean aliasDefModelNeeded = true;

  /**
   * whether characteristics model needed, attr value links are also loaded based on this condition
   */
  private boolean characteristicsModelNeeded = true;

  /**
   * whether attribute group model needed
   */
  private boolean attrGroupModelNeeded = true;

  /**
   * whether pidc details structure needed
   */
  private boolean pidcDetStructNeeded = true;

  /**
   * whether attribute links needed
   */
  private boolean attrLinksNeeded = true;

  /**
   * whether pre defined attr model needed
   */
  private boolean predefinedAttrModelNeeded = true;

  /**
   * whether attribute dependency model is needed
   */
  private boolean attrDepnModelNeeded = true;

  /**
   * whether mandatory attribute information is needed
   */
  private boolean mandatoryAttrNeeded = true;

  /**
   * whether loading of use case details are needed
   */
  private boolean usecaseModelNeeded = true;


  /**
   * default constructor
   */
  public PidcVersionWithDetailsInput() {
    // DO NOT DELETE this constructor as it will be used for JSON conversion
  }

  /**
   * @param pidcVersionId pidc version id
   * @param loadOnlyPidcModel if true, only pidc model will be loaded
   */
  public PidcVersionWithDetailsInput(final Long pidcVersionId, final boolean loadOnlyPidcModel) {
    this.pidcVersionId = pidcVersionId;

    if (loadOnlyPidcModel) {
      this.pidcVersionInfoNeeded = false;
      this.aliasDefModelNeeded = false;
      this.attrDepnModelNeeded = false;
      this.attrGroupModelNeeded = false;
      this.attrLinksNeeded = false;
      this.characteristicsModelNeeded = false;
      this.mandatoryAttrNeeded = false;
      this.pidcDetStructNeeded = false;
      this.predefinedAttrModelNeeded = false;
      this.usecaseModelNeeded = false;
    }
  }

  /**
   * @return the aliasDefModelNeeded
   */
  public boolean isAliasDefModelNeeded() {
    return this.aliasDefModelNeeded;
  }


  /**
   * @param aliasDefModelNeeded the aliasDefModelNeeded to set
   */
  public void setAliasDefModelNeeded(final boolean aliasDefModelNeeded) {
    this.aliasDefModelNeeded = aliasDefModelNeeded;
  }


  /**
   * @return the characteristicsModelNeeded
   */
  public boolean isCharacteristicsModelNeeded() {
    return this.characteristicsModelNeeded;
  }


  /**
   * @param characteristicsModelNeeded the characteristicsModelNeeded to set
   */
  public void setCharacteristicsModelNeeded(final boolean characteristicsModelNeeded) {
    this.characteristicsModelNeeded = characteristicsModelNeeded;
  }


  /**
   * @return the attrGroupModelNeeded
   */
  public boolean isAttrGroupModelNeeded() {
    return this.attrGroupModelNeeded;
  }


  /**
   * @param attrGroupModelNeeded the attrGroupModelNeeded to set
   */
  public void setAttrGroupModelNeeded(final boolean attrGroupModelNeeded) {
    this.attrGroupModelNeeded = attrGroupModelNeeded;
  }


  /**
   * @return the pidcDetStructNeeded
   */
  public boolean isPidcDetStructNeeded() {
    return this.pidcDetStructNeeded;
  }


  /**
   * @param pidcDetStructNeeded the pidcDetStructNeeded to set
   */
  public void setPidcDetStructNeeded(final boolean pidcDetStructNeeded) {
    this.pidcDetStructNeeded = pidcDetStructNeeded;
  }


  /**
   * @return the attrLinksNeeded
   */
  public boolean isAttrLinksNeeded() {
    return this.attrLinksNeeded;
  }


  /**
   * @param attrLinksNeeded the attrLinksNeeded to set
   */
  public void setAttrLinksNeeded(final boolean attrLinksNeeded) {
    this.attrLinksNeeded = attrLinksNeeded;
  }


  /**
   * @return the predefinedAttrModelNeeded
   */
  public boolean isPredefinedAttrModelNeeded() {
    return this.predefinedAttrModelNeeded;
  }


  /**
   * @param predefinedAttrModelNeeded the predefinedAttrModelNeeded to set
   */
  public void setPredefinedAttrModelNeeded(final boolean predefinedAttrModelNeeded) {
    this.predefinedAttrModelNeeded = predefinedAttrModelNeeded;
  }


  /**
   * @return the attrDepnModelNeeded
   */
  public boolean isAttrDepnModelNeeded() {
    return this.attrDepnModelNeeded;
  }


  /**
   * @param attrDepnModelNeeded the attrDepnModelNeeded to set
   */
  public void setAttrDepnModelNeeded(final boolean attrDepnModelNeeded) {
    this.attrDepnModelNeeded = attrDepnModelNeeded;
  }


  /**
   * @return the mandatoryAttrNeeded
   */
  public boolean isMandatoryAttrNeeded() {
    return this.mandatoryAttrNeeded;
  }


  /**
   * @param mandatoryAttrNeeded the mandatoryAttrNeeded to set
   */
  public void setMandatoryAttrNeeded(final boolean mandatoryAttrNeeded) {
    this.mandatoryAttrNeeded = mandatoryAttrNeeded;
  }


  /**
   * @return the usecaseModelNeeded
   */
  public boolean isUsecaseModelNeeded() {
    return this.usecaseModelNeeded;
  }


  /**
   * @param usecaseModelNeeded the usecaseModelNeeded to set
   */
  public void setUsecaseModelNeeded(final boolean usecaseModelNeeded) {
    this.usecaseModelNeeded = usecaseModelNeeded;
  }


  /**
   * @return the pidcVersionId
   */
  public Long getPidcVersionId() {
    return this.pidcVersionId;
  }


  /**
   * @param pidcVersionId the pidcVersionId to set
   */
  public void setPidcVersionId(final Long pidcVersionId) {
    this.pidcVersionId = pidcVersionId;
  }


  /**
   * @return the pidcVersionInfoNeeded
   */
  public boolean isPidcVersionInfoNeeded() {
    return this.pidcVersionInfoNeeded;
  }

  /**
   * @param pidcVersionInfoNeeded the pidcVersionInfoNeeded to set
   */
  public void setPidcVersionInfoNeeded(final boolean pidcVersionInfoNeeded) {
    this.pidcVersionInfoNeeded = pidcVersionInfoNeeded;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return "PidcVersionWithDetailsInput [pidcVersionId=" + this.pidcVersionId + ", aliasDefModelNeeded=" +
        this.aliasDefModelNeeded + ", characteristicsModelNeeded=" + this.characteristicsModelNeeded +
        ", attrGroupModelNeeded=" + this.attrGroupModelNeeded + ", pidcDetStructNeeded=" + this.pidcDetStructNeeded +
        ", attrLinksNeeded=" + this.attrLinksNeeded + ", predefinedAttrModelNeeded=" + this.predefinedAttrModelNeeded +
        ", attrDepnModelNeeded=" + this.attrDepnModelNeeded + ", mandatoryAttrNeeded=" + this.mandatoryAttrNeeded +
        ", usecaseModelNeeded=" + this.usecaseModelNeeded + "]";
  }


}

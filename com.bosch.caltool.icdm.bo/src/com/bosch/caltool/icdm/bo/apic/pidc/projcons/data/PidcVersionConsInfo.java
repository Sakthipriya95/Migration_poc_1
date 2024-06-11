/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons.data;


/**
 * @author bne4cob
 */
public class PidcVersionConsInfo extends PidcConsInfo {

  /**
   * Version ID
   */
  private Long pidcVersID;

  /**
   * Version Name
   */
  private String pidcVersName;

  /**
   * Attribute ID
   */
  private Long attrID;

  /**
   * Attribute Name
   */
  private String attrName;

  /**
   * @return the pidcVersID
   */
  public final Long getPidcVersID() {
    return this.pidcVersID;
  }

  /**
   * @param pidcVersID the pidcVersID to set
   */
  public final void setPidcVersID(final Long pidcVersID) {
    this.pidcVersID = pidcVersID;
  }

  /**
   * @return the pidcVersName
   */
  public final String getPidcVersName() {
    return this.pidcVersName;
  }

  /**
   * @param pidcVersName the pidcVersName to set
   */
  public final void setPidcVersName(final String pidcVersName) {
    this.pidcVersName = pidcVersName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSummary() {
    return getProjectName() + '\t' + getProjectID() + '\t' + getPidcVersName() + '\t' + getPidcVersID() + '\t' +
        getAttrName() + '\t' + getAttrID() + '\t' + getErrorType();
  }

  /**
   * @return the attrID
   */
  public final Long getAttrID() {
    return this.attrID;
  }

  /**
   * @param attrID the attrID to set
   */
  public final void setAttrID(final Long attrID) {
    this.attrID = attrID;
  }

  /**
   * @return the attrName
   */
  public final String getAttrName() {
    return this.attrName;
  }

  /**
   * @param attrName the attrName to set
   */
  public final void setAttrName(final String attrName) {
    this.attrName = attrName;
  }


}

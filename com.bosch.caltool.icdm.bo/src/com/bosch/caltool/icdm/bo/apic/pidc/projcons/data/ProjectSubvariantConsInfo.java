/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons.data;


/**
 * @author bne4cob
 */
public class ProjectSubvariantConsInfo extends ProjectVariantConsInfo {

  /**
   * Sub Variant ID
   */
  private Long subvariantID;

  /**
   * Sub Variant Name
   */
  private String subvariantName;

  /**
   * @return the subvariantID
   */
  public final Long getSubvariantID() {
    return this.subvariantID;
  }

  /**
   * @param subvariantID the subvariantID to set
   */
  public final void setSubvariantID(final Long subvariantID) {
    this.subvariantID = subvariantID;
  }

  /**
   * @return the subvariantName
   */
  public final String getSubvariantName() {
    return this.subvariantName;
  }

  /**
   * @param subvariantName the subvariantName to set
   */
  public final void setSubvariantName(final String subvariantName) {
    this.subvariantName = subvariantName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSummary() {
    return getProjectName() + '\t' + getProjectID() + '\t' + getPidcVersName() + '\t' + getPidcVersID() + '\t' +
        getVariantName() + '\t' + getVariantID() + '\t' + getSubvariantName() + '\t' + getSubvariantID() + '\t' +
        getAttrName() + '\t' + getAttrID() + '\t' + getErrorType();
  }

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc.projcons.data;


/**
 * @author bne4cob
 */
public class ProjectVariantConsInfo extends PidcVersionConsInfo {


  /**
   * Variant ID
   */
  private Long variantID;

  /**
   * Variant Name
   */
  private String variantName;

  /**
   * @return the variantID
   */
  public final Long getVariantID() {
    return this.variantID;
  }

  /**
   * @param variantID the variantID to set
   */
  public final void setVariantID(final Long variantID) {
    this.variantID = variantID;
  }

  /**
   * @return the variantName
   */
  public final String getVariantName() {
    return this.variantName;
  }

  /**
   * @param variantName the variantName to set
   */
  public final void setVariantName(final String variantName) {
    this.variantName = variantName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSummary() {
    return getProjectName() + '\t' + getProjectID() + '\t' + getPidcVersName() + '\t' + getPidcVersID() + '\t' +
        getVariantName() + '\t' + getVariantID() + '\t' + getAttrName() + '\t' + getAttrID() + '\t' + getErrorType();
  }

}

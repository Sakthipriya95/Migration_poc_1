/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author dmr1cob
 */
public class ReviewInfoWpDefDetails {

  private String wpDefVersName;

  private String variantGrpname;


  /**
   * @return the wpDefVersName
   */
  public String getWpDefVersName() {
    return this.wpDefVersName;
  }


  /**
   * @param wpDefVersName the wpDefVersName to set
   */
  public void setWpDefVersName(final String wpDefVersName) {
    this.wpDefVersName = wpDefVersName;
  }


  /**
   * @return the variantGrpname
   */
  public String getVariantGrpname() {
    return this.variantGrpname;
  }


  /**
   * @param variantGrpname the variantGrpname to set
   */
  public void setVariantGrpname(final String variantGrpname) {
    this.variantGrpname = variantGrpname;
  }


}

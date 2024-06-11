/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;


/**
 * Model to be used for copy, paste in wp defn page nattable
 *
 * @author pdh2cob
 */
public class A2lWpResponsibilityCopyModel {


  private A2lWpResponsibility selectedA2lWpResp;

  private int[] selectedColumns;


  /**
   * @return the a2lWpResp
   */
  public A2lWpResponsibility getSelectedA2lWpResp() {
    return this.selectedA2lWpResp;
  }


  /**
   * @param a2lWpResp the a2lWpResponsibility to set
   */
  public void setSelectedA2lWpResp(final A2lWpResponsibility a2lWpResp) {
    this.selectedA2lWpResp = a2lWpResp;
  }


  /**
   * @return the selectedColumns
   */
  public int[] getSelectedColumns() {
    return this.selectedColumns;
  }


  /**
   * @param selectedColumns the selectedColumns to set
   */
  public void setSelectedColumns(final int[] selectedColumns) {
    this.selectedColumns = selectedColumns;
  }


}

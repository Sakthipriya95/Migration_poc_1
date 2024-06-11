/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;


/**
 * @author pdh2cob
 */
public class ParameterFilterLabel {

  private String label;

  private boolean mustExist;


  /**
   * @return the label
   */
  public String getLabel() {
    return this.label;
  }


  /**
   * @param label the label to set
   */
  public void setLabel(final String label) {
    this.label = label;
  }


  /**
   * @return the mustExist
   */
  public boolean isMustExist() {
    return this.mustExist;
  }


  /**
   * @param mustExist the mustExist to set
   */
  public void setMustExist(final boolean mustExist) {
    this.mustExist = mustExist;
  }


  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() == this.getClass()) {
      return ((ParameterFilterLabel) obj).getLabel().equalsIgnoreCase(this.label);
    }
    return false;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }


}

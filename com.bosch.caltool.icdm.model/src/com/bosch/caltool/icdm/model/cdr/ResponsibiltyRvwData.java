/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

/**
 * @author rgo7cob
 */
public class ResponsibiltyRvwData implements Comparable<ResponsibiltyRvwData> {

  /**
   * Hash code prime number
   */
  private static final int HASHCODE_PRIME = 31;

  private String responsibility;

  private int numOfParams;

  private int numOfRvwdParams;


  /**
   * @return the responsibility
   */
  public String getResponsibility() {
    return this.responsibility;
  }


  /**
   * @param responsibility the responsibility to set
   */
  public void setResponsibility(final String responsibility) {
    this.responsibility = responsibility;
  }

  /**
   * @return the numOfParams
   */
  public int getNumOfParams() {
    return this.numOfParams;
  }


  /**
   * @param numOfParams the numOfParams to set
   */
  public void setNumOfParams(final int numOfParams) {
    this.numOfParams = numOfParams;
  }


  /**
   * @return the numOfRvwdParams
   */
  public int getNumOfRvwdParams() {
    return this.numOfRvwdParams;
  }


  /**
   * @param numOfRvwdParams the numOfRvwdParams to set
   */
  public void setNumOfRvwdParams(final int numOfRvwdParams) {
    this.numOfRvwdParams = numOfRvwdParams;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ResponsibiltyRvwData obj) {
    return getResponsibility().compareTo(obj.getResponsibility());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ResponsibiltyRvwData respRvwData = (ResponsibiltyRvwData) obj;
    if (getResponsibility() == null) {
      return null == respRvwData.getResponsibility();
    }
    return getResponsibility().equals(respRvwData.getResponsibility());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getResponsibility() == null) ? 0 : getResponsibility().hashCode());
    return result;
  }
}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr.chkssdxlrptparser;


/**
 * @author APJ4COB
 */
public class CheckSSDXlRprtRowData {

  String usecase;
  String status;
  String ssdLabel;

  /**
   * @return the usecase column from excel file
   */
  public String getUsecase() {
    return this.usecase;
  }

  /**
   * @param usecase the usecase to set
   */
  public void setUsecase(final String usecase) {
    this.usecase = usecase;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return this.status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(final String status) {
    this.status = status;
  }

  /**
   * @return the ssdLabel
   */
  public String getSsdLabel() {
    return this.ssdLabel;
  }

  /**
   * @param ssdLabel the ssdLabel to set
   */
  public void setSsdLabel(final String ssdLabel) {
    this.ssdLabel = ssdLabel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.ssdLabel == null) ? 0 : this.ssdLabel.hashCode());
    result = (prime * result) + ((this.status == null) ? 0 : this.status.hashCode());
    result = (prime * result) + ((this.usecase == null) ? 0 : this.usecase.hashCode());
    return result;
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
    CheckSSDXlRprtRowData other = (CheckSSDXlRprtRowData) obj;
    if (this.ssdLabel == null) {
      if (other.ssdLabel != null) {
        return false;
      }
    }
    else if (!this.ssdLabel.equals(other.ssdLabel)) {
      return false;
    }
    if (this.status == null) {
      if (other.status != null) {
        return false;
      }
    }
    else if (!this.status.equals(other.status)) {
      return false;
    }
    if (this.usecase == null) {
      if (other.usecase != null) {
        return false;
      }
    }
    else if (!this.usecase.equals(other.usecase)) {
      return false;
    }
    return true;
  }

}

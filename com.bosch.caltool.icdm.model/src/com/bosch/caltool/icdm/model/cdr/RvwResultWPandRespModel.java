/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author say8cob
 */
public class RvwResultWPandRespModel implements Comparable<RvwResultWPandRespModel> {

  private A2lResponsibility a2lResponsibility;
  
  private A2lWorkPackage a2lWorkPackage;

  /**
   * @return the a2lWorkPackage
   */
  public A2lWorkPackage getA2lWorkPackage() {
    return this.a2lWorkPackage;
  }


  /**
   * @param a2lWorkPackage the a2lWorkPackage to set
   */
  public void setA2lWorkPackage(final A2lWorkPackage a2lWorkPackage) {
    this.a2lWorkPackage = a2lWorkPackage;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final RvwResultWPandRespModel object) {
    return ModelUtil.compare(this.a2lWorkPackage.getName(), object.getA2lWorkPackage().getName());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.a2lWorkPackage == null) ? 0 : this.a2lWorkPackage.hashCode());
    result = (prime * result) + ((this.a2lResponsibility == null) ? 0 : this.a2lResponsibility.hashCode());
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
    RvwResultWPandRespModel other = (RvwResultWPandRespModel) obj;
    if (this.a2lWorkPackage == null) {
      if (other.a2lWorkPackage != null) {
        return false;
      }
    }
    else if (!this.a2lWorkPackage.equals(other.a2lWorkPackage)) {
      return false;
    }
    if (this.a2lResponsibility == null) {
      if (other.a2lResponsibility != null) {
        return false;
      }
    }
    else if (!this.a2lResponsibility.equals(other.a2lResponsibility)) {
      return false;
    }
    return true;
  }


  
  /**
   * @return the a2lResponsibility
   */
  public A2lResponsibility getA2lResponsibility() {
    return a2lResponsibility;
  }


  
  /**
   * @param a2lResponsibility the a2lResponsibility to set
   */
  public void setA2lResponsibility(A2lResponsibility a2lResponsibility) {
    this.a2lResponsibility = a2lResponsibility;
  }


}

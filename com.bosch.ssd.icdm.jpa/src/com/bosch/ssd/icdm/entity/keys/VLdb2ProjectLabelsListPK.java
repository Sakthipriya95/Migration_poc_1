package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigInteger;


/**
 * @author SSN9COB
 */
public class VLdb2ProjectLabelsListPK implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * Pro Rev id
   */
  private BigInteger proRevId;
  /**
   * Label
   */
  private String label;

  /**
   *
   */
  public VLdb2ProjectLabelsListPK() {
    // empty constructor
  }

  /**
   * @param proRevId id
   * @param label name
   */
  public VLdb2ProjectLabelsListPK(final BigInteger proRevId, final String label) {
    this.proRevId = proRevId;
    this.label = label;
  }

  /**
   * @return id
   */
  public BigInteger getProRevId() {
    return this.proRevId;
  }

  /**
   * @param proRevId id
   */
  public void setProRevId(final BigInteger proRevId) {
    this.proRevId = proRevId;
  }

  /**
   * @return label
   */
  public String getLabel() {
    return this.label;
  }

  /**
   * @param label label
   */
  public void setLabel(final String label) {
    this.label = label;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.label == null) ? 0 : this.label.hashCode());
    result = (prime * result) + ((this.proRevId == null) ? 0 : this.proRevId.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    // If equal, true
    if (this == obj) {
      return true;
    }
    // if null, false
    if (obj == null) {
      return false;
    }
    // if diff, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    VLdb2ProjectLabelsListPK other = (VLdb2ProjectLabelsListPK) obj;
    // if null, false
    if (this.label == null) {
      if (other.label != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.label.equals(other.label)) {
      return false;
    }
    // if null, false
    if (this.proRevId == null) {
      if (other.proRevId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.proRevId.equals(other.proRevId)) {
      return false;
    }
    return true;
  }


}

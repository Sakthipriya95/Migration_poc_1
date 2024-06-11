package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author SSN9COB
 */
public class VLdb2ProjectLabelsPK implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   * Pro Revid
   */
  private BigDecimal proRevId;
  /**
   * Label
   */
  private String label;

  /**
   *
   */
  public VLdb2ProjectLabelsPK() {
    // empty Constructor
  }

  /**
   * @param proRevId id
   * @param label label
   */
  public VLdb2ProjectLabelsPK(final BigDecimal proRevId, final String label) {
    this.proRevId = proRevId;
    this.label = label;
  }

  /**
   * @return id
   */
  public BigDecimal getProRevId() {
    return this.proRevId;
  }

  /**
   * @param proRevId id
   */
  public void setProRevId(final BigDecimal proRevId) {
    this.proRevId = proRevId;
  }

  /**
   * @return label
   */
  public String getLabel() {
    return this.label;
  }

  /**
   * @param label name
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
    // if equal, true
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
    VLdb2ProjectLabelsPK other = (VLdb2ProjectLabelsPK) obj;
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

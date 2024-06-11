/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.entity.keys;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;


/**
 * @author apl1cob
 */
public class VLdb2ObjectTreePK implements Serializable {

  /**
   * default serial version id
   */
  private static final long serialVersionUID = 1L;

  /**
   * Node ID
   */
  @Column(name = "NODE_ID", nullable = false, precision = 15)
  private BigDecimal nodeId;

  /**
  *
  */
  public VLdb2ObjectTreePK() {
    super();
  }

  /**
   * @return the nodeId
   */
  public BigDecimal getNodeId() {
    return this.nodeId;
  }


  /**
   * @param nodeId the revId to set
   */
  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.nodeId == null) ? 0 : this.nodeId.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
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
    // if different, false
    if (getClass() != obj.getClass()) {
      return false;
    }
    VLdb2ObjectTreePK other = (VLdb2ObjectTreePK) obj;
    // if null, false
    if (this.nodeId == null) {
      if (other.nodeId != null) {
        return false;
      }
    }
    // if unequal, false
    else if (!this.nodeId.equals(other.nodeId)) {
      return false;
    }
    return true;
  }


}

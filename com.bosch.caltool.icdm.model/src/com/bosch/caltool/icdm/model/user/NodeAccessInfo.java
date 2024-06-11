/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

import java.util.Objects;

/**
 * @author rgo7cob
 */
public class NodeAccessInfo {


  private NodeAccess access;

  private String nodeName;

  private String nodeDesc;

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;


  /**
   * @return the access
   */
  public NodeAccess getAccess() {
    return this.access;
  }


  /**
   * @param access the access to set
   */
  public void setAccess(final NodeAccess access) {
    this.access = access;
  }


  /**
   * @return the nodeName
   */
  public String getNodeName() {
    return this.nodeName;
  }


  /**
   * @param nodeName the nodeName to set
   */
  public void setNodeName(final String nodeName) {
    this.nodeName = nodeName;
  }


  /**
   * @return the nodeDesc
   */
  public String getNodeDesc() {
    return this.nodeDesc;
  }


  /**
   * @param nodeDesc the nodeDesc to set
   */
  public void setNodeDesc(final String nodeDesc) {
    this.nodeDesc = nodeDesc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASH_CODE_PRIME_31 * result) + ((getNodeName() == null) ? 0 : getNodeName().hashCode());
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
    return Objects.equals(getNodeName(), ((NodeAccessInfo) obj).getNodeName());
  }

}

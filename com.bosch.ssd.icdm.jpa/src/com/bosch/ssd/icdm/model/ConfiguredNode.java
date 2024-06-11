/**
 *
 */
package com.bosch.ssd.icdm.model;

import java.math.BigDecimal;

/**
 * @author gue1cob Model for configured Nodes.
 */
public class ConfiguredNode {


  private String nodeScope;
  /**
   * node id of the node
   */
  private BigDecimal nodeId;
  /**
   * id for the labellist valid
   */
  private BigDecimal proRevId;

  /**
   * Constructor for {@link ConfiguredNode}
   */
  public ConfiguredNode() {

  }

  /**
   * @param nodeId - id of the node
   */
  public ConfiguredNode(final BigDecimal nodeId) {
    // entire scope name is set as node name in configNode
    setNodeId(nodeId);
  }

  /**
   * @return the nodeId
   */
  public BigDecimal getNodeId() {
    return this.nodeId;
  }

  /**
   * @param nodeId the nodeId to set
   */
  public void setNodeId(final BigDecimal nodeId) {
    this.nodeId = nodeId;
  }

  /**
   * @return the proRevId
   */
  public BigDecimal getProRevId() {
    return this.proRevId;
  }

  /**
   * @param proRevId the proRevId to set
   */
  public void setProRevId(final BigDecimal proRevId) {
    this.proRevId = proRevId;
  }

  @Override
  public boolean equals(final Object obj) {
    // check equals
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConfiguredNode other = (ConfiguredNode) obj;
    if (this.nodeId == null) {
      if (other.nodeId != null) {
        return false;
      }
    }
    else if (!this.nodeId.equals(other.nodeId)) {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * @return the nodeScope
   */
  public String getNodeScope() {
    return this.nodeScope;
  }

  /**
   * @param nodeScope the nodeScope to set
   */
  public void setNodeScope(final String nodeScope) {
    this.nodeScope = nodeScope;
  }


}

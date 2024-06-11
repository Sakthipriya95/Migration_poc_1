/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.user;

/**
 * @author rgo7cob
 */
public class NodeAccessWithUserInput {


  private NodeAccess nodeAccess;

  private boolean delete;


  /**
   * @return the nodeAccess
   */
  public NodeAccess getNodeAccess() {
    return this.nodeAccess;
  }


  /**
   * @param nodeAccess the nodeAccess to set
   */
  public void setNodeAccess(final NodeAccess nodeAccess) {
    this.nodeAccess = nodeAccess;
  }


  /**
   * @return the delete
   */
  public boolean isDelete() {
    return this.delete;
  }


  /**
   * @param delete the delete to set
   */
  public void setDelete(final boolean delete) {
    this.delete = delete;
  }


}

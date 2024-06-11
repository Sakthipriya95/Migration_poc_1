/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.comppkg;

import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author say8cob
 */
public class CompPkgResponse {

  private CompPackage compPackage;

  private NodeAccess nodeAccess;


  /**
   * @return the compPackage
   */
  public CompPackage getCompPackage() {
    return this.compPackage;
  }


  /**
   * @param compPackage the compPackage to set
   */
  public void setCompPackage(final CompPackage compPackage) {
    this.compPackage = compPackage;
  }


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


}

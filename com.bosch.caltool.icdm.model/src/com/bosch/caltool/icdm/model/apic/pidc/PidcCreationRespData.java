/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * @author dja7cob
 */
public class PidcCreationRespData {

  private Pidc pidc;

  private NodeAccess nodeAccess;


  /**
   * @return the pidc
   */
  public Pidc getPidc() {
    return this.pidc;
  }


  /**
   * @param pidc the pidc to set
   */
  public void setPidc(final Pidc pidc) {
    this.pidc = pidc;
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

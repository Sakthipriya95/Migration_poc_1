/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmr1cob
 */
public class MappedBcsData extends UnMappedBcsData {

  private List<Long> ssdNodes = new ArrayList<>();


  /**
   * @return the ssdNodes
   */
  public List<Long> getSsdNodes() {
    return this.ssdNodes;
  }


  /**
   * @param ssdNodes the ssdNodes to set
   */
  public void setSsdNodes(final List<Long> ssdNodes) {
    this.ssdNodes = ssdNodes;
  }
}

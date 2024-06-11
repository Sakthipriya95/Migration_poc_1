/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.Map;
import java.util.Set;

/**
 * @deprecated
 */
/**
 * ICDM-2603
 *
 * @author mkl2cob
 */
@Deprecated
public class A2LWPRespCopyInput {

  /**
   * key- wp resp id , value - set of a2l wp resp id
   */
  Map<Long, Set<Long>> respToBeUpdated;


  /**
   * @return the respToBeUpdated
   */
  public Map<Long, Set<Long>> getRespToBeUpdated() {
    return this.respToBeUpdated;
  }


  /**
   * @param respToBeUpdated the respToBeUpdated to set
   */
  public void setRespToBeUpdated(final Map<Long, Set<Long>> respToBeUpdated) {
    this.respToBeUpdated = respToBeUpdated;
  }
}

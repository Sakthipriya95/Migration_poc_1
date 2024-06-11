/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NDV4KOR
 */
public class A2lWpRespStatusUpdateModel {

  /**
   * Map of A2lWpResponsibility before Wp status updation. Key - A2lWpResponsibility id, value - A2lWpResponsibility
   */
  private Map<Long, A2lWpResponsibility> beforeWpRespMap = new HashMap<>();

  /**
   * Map of A2lWpResponsibility after Wp status updation. Key - A2lWpResponsibility id, value - A2lWpResponsibility
   */
  private Map<Long, A2lWpResponsibility> afterWpRespMap = new HashMap<>();


  /**
   * @return the afterWpRespMap
   */
  public Map<Long, A2lWpResponsibility> getAfterWpRespMap() {
    return this.afterWpRespMap;
  }


  /**
   * @param afterWpRespMap the afterWpRespMap to set
   */
  public void setAfterWpRespMap(final Map<Long, A2lWpResponsibility> afterWpRespMap) {
    this.afterWpRespMap = afterWpRespMap;
  }


  /**
   * @return the beforeWpRespMap
   */
  public Map<Long, A2lWpResponsibility> getBeforeWpRespMap() {
    return this.beforeWpRespMap;
  }


  /**
   * @param beforeWpRespMap the beforeWpRespMap to set
   */
  public void setBeforeWpRespMap(final Map<Long, A2lWpResponsibility> beforeWpRespMap) {
    this.beforeWpRespMap = beforeWpRespMap;
  }

}

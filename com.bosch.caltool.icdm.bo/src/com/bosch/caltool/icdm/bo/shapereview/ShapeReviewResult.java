/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.shapereview;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bne4cob
 */
public class ShapeReviewResult {

  private SHAPE_STATUS shapeStatus = SHAPE_STATUS.NOT_STARTED;

  private final Map<String, ShapeReviewParamResult> paramResultMap = new ConcurrentHashMap<>();

  /**
   * @param paramResultMap the paramResultMap to set
   */
  public void setParamResultMap(final Map<String, ShapeReviewParamResult> paramResultMap) {
    this.paramResultMap.clear();
    this.paramResultMap.putAll(paramResultMap);
  }

  /**
   * @return the paramResultMap
   */
  public Map<String, ShapeReviewParamResult> getParamResultMap() {
    return new ConcurrentHashMap<>(this.paramResultMap);
  }

  /**
   * @return the shapeStatus
   */
  public SHAPE_STATUS getStatus() {
    return this.shapeStatus;
  }

  /**
   * @param shapeStatus the shapeStatus to set
   */
  public void setStatus(final SHAPE_STATUS shapeStatus) {
    this.shapeStatus = shapeStatus;
  }

}

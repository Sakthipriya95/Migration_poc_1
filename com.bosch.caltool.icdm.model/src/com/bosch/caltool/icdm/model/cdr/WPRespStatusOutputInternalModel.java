/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.HashMap;
import java.util.Map;

import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;

/**
 * @author say8cob
 *
 */
public class WPRespStatusOutputInternalModel {

  /**
   * holds the status of all the Wp Resp Combination in a review result key - WPRESPID and Value - WP Resp Status
   * (Finished / Not Finished)
   */
  Map<A2lWPRespModel, String> wpRespStatus = new HashMap<>();

  /**
   * holds un completed WP RESP for a review results key - WPRESPID and Value - WPRespStatusMsgWrapper
   */
  Map<A2lWPRespModel, WPRespStatusMsgWrapper> inCompleteWPRespMap = new HashMap<>();

  /**
   * holds completed WP RESP for a review results key - WPRESPID and Value - WPRespStatusMsgWrapper
   */
  Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap = new HashMap<>();


  /**
   * @return the wpRespStatus
   */
  public Map<A2lWPRespModel, String> getWpRespStatus() {
    return this.wpRespStatus;
  }


  /**
   * @param wpRespStatus the wpRespStatus to set
   */
  public void setWpRespStatus(final Map<A2lWPRespModel, String> wpRespStatus) {
    this.wpRespStatus = wpRespStatus;
  }


  /**
   * @return the unCompletedWPRespMap
   */
  public Map<A2lWPRespModel, WPRespStatusMsgWrapper> getInCompleteWPRespMap() {
    return this.inCompleteWPRespMap;
  }


  /**
   * @param inCompleteWPRespMap the unCompletedWPRespMap to set
   */
  public void setInCompleteWPRespMap(final Map<A2lWPRespModel, WPRespStatusMsgWrapper> inCompleteWPRespMap) {
    this.inCompleteWPRespMap = inCompleteWPRespMap;
  }


  /**
   * @return the completedWPRespMap
   */
  public Map<A2lWPRespModel, WPRespStatusMsgWrapper> getCompletedWPRespMap() {
    return this.completedWPRespMap;
  }


  /**
   * @param completedWPRespMap the completedWPRespMap to set
   */
  public void setCompletedWPRespMap(final Map<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap) {
    this.completedWPRespMap = completedWPRespMap;
  }

}

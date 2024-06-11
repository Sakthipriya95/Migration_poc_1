/*
 * Copyright (c) ETAS GmbH 2024. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class WpArchivalCommonModel {

  /**
   * holds completed Archival WP RESP for a review results key - RESPID and (Key - WPID and Value -
   * WPRespStatusMsgWrapper)
   */
  private Map<Long, Map<Long, WPRespStatusMsgWrapper>> wpArchivalCompletedWPRespMap = new HashMap<>();

  /**
   * holds failed Archival WP RESP for a review results key - RESPID and (Key - WPID and Value - WPRespStatusMsgWrapper)
   */
  private Map<Long, Map<Long, WPRespStatusMsgWrapper>> wpArchivalFailedWPRespMap = new HashMap<>();

  private List<WpArchival> wpArchival = new ArrayList<>();


  /**
   * @return the wpArchivalCompletedWPRespMap
   */
  public Map<Long, Map<Long, WPRespStatusMsgWrapper>> getWpArchivalCompletedWPRespMap() {
    return this.wpArchivalCompletedWPRespMap;
  }


  /**
   * @param wpArchivalCompletedWPRespMap the wpArchivalCompletedWPRespMap to set
   */
  public void setWpArchivalCompletedWPRespMap(
      final Map<Long, Map<Long, WPRespStatusMsgWrapper>> wpArchivalCompletedWPRespMap) {
    this.wpArchivalCompletedWPRespMap = wpArchivalCompletedWPRespMap;
  }


  /**
   * @return the wpArchivalFailedWPRespMap
   */
  public Map<Long, Map<Long, WPRespStatusMsgWrapper>> getWpArchivalFailedWPRespMap() {
    return this.wpArchivalFailedWPRespMap;
  }


  /**
   * @param wpArchivalFailedWPRespMap the wpArchivalFailedWPRespMap to set
   */
  public void setWpArchivalFailedWPRespMap(
      final Map<Long, Map<Long, WPRespStatusMsgWrapper>> wpArchivalFailedWPRespMap) {
    this.wpArchivalFailedWPRespMap = wpArchivalFailedWPRespMap;
  }


  /**
   * @return the wpArchival
   */
  public List<WpArchival> getWpArchival() {
    return this.wpArchival;
  }


  /**
   * @param wpArchival the wpArchival to set
   */
  public void setWpArchival(final List<WpArchival> wpArchival) {
    if (wpArchival != null) {
      this.wpArchival = wpArchival;
    }
  }

}

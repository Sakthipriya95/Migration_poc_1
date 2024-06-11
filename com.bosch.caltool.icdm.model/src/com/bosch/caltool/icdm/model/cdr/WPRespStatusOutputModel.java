/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;

/**
 * @author say8cob
 */
/**
 * @author say8cob
 */
public class WPRespStatusOutputModel {

  /**
   * List of newly created A2lWpResponsibilityStatus entries in T_A2L_WP_RESPONSIBILITY_STATUS table
   */
  private List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus = new ArrayList<>();

  /**
   * Map to Store Before Update A2lWpResponsibilityStatus for CNS Refresh Key - A2lWpResponsibilityStatus Id, Value -
   * A2lWpResponsibilityStatus
   */
  Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpdMap = new HashMap<>();

  /**
   * Map to Store Updated A2lWpResponsibilityStatus for CNS Refresh Key - A2lWpResponsibilityStatus Id, Value -
   * A2lWpResponsibilityStatus
   */
  Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpdMap = new HashMap<>();

  /**
   * holds un completed WP RESP for a review results key - RESPID and (Key - WPID and Value - WPRespStatusMsgWrapper)
   */
  Map<Long, Map<Long, WPRespStatusMsgWrapper>> inCompleteWPRespMap = new HashMap<>();

  /**
   * holds completed WP RESP for a review results key - RESPID and (Key - WPID and Value - WPRespStatusMsgWrapper)
   */
  Map<Long, Map<Long, WPRespStatusMsgWrapper>> completedWPRespMap = new HashMap<>();

  /**
   * holds un completed WP RESP 
   */
  Set<A2lWPRespModel> inCompleteWPRespModelSet = new HashSet<>();

  /**
   * holds completed WP RESP 
   */
  Set<A2lWPRespModel> completedWPRespModelSet = new HashSet<>();


  /**
   * @return the listOfNewlyCreatedA2lWpRespStatus
   */
  public List<A2lWpResponsibilityStatus> getListOfNewlyCreatedA2lWpRespStatus() {
    return this.listOfNewlyCreatedA2lWpRespStatus;
  }


  /**
   * @param listOfNewlyCreatedA2lWpRespStatus the listOfNewlyCreatedA2lWpRespStatus to set
   */
  public void setListOfNewlyCreatedA2lWpRespStatus(
      final List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus) {
    this.listOfNewlyCreatedA2lWpRespStatus = listOfNewlyCreatedA2lWpRespStatus;
  }


  /**
   * @return the unCompletedWPRespMap
   */
  public Map<Long, Map<Long, WPRespStatusMsgWrapper>> getInCompleteWPRespMap() {
    return this.inCompleteWPRespMap;
  }


  /**
   * @param inCompleteWPRespMap the unCompletedWPRespMap to set
   */
  public void setInCompleteWPRespMap(final Map<Long, Map<Long, WPRespStatusMsgWrapper>> inCompleteWPRespMap) {
    this.inCompleteWPRespMap = inCompleteWPRespMap;
  }


  /**
   * @return the completedWPRespMap
   */
  public Map<Long, Map<Long, WPRespStatusMsgWrapper>> getCompletedWPRespMap() {
    return this.completedWPRespMap;
  }


  /**
   * @param completedWPRespMap the completedWPRespMap to set
   */
  public void setCompletedWPRespMap(final Map<Long, Map<Long, WPRespStatusMsgWrapper>> completedWPRespMap) {
    this.completedWPRespMap = completedWPRespMap;
  }


  /**
   * @return the a2lWpRespStatusBeforeUpdMap
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusBeforeUpdMap() {
    return this.a2lWpRespStatusBeforeUpdMap;
  }


  /**
   * @param a2lWpRespStatusBeforeUpdMap the a2lWpRespStatusBeforeUpdMap to set
   */
  public void setA2lWpRespStatusBeforeUpdMap(final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpdMap) {
    this.a2lWpRespStatusBeforeUpdMap = a2lWpRespStatusBeforeUpdMap;
  }


  /**
   * @return the a2lWpRespStatusAfterUpdMap
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusAfterUpdMap() {
    return this.a2lWpRespStatusAfterUpdMap;
  }


  /**
   * @param a2lWpRespStatusAfterUpdMap the a2lWpRespStatusAfterUpdMap to set
   */
  public void setA2lWpRespStatusAfterUpdMap(final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusAfterUpdMap) {
    this.a2lWpRespStatusAfterUpdMap = a2lWpRespStatusAfterUpdMap;
  }


  /**
   * @return the inCompleteWPRespModelSet
   */
  public Set<A2lWPRespModel> getInCompleteWPRespModelSet() {
    return this.inCompleteWPRespModelSet;
  }


  /**
   * @param inCompleteWPRespModelSet the inCompleteWPRespModelSet to set
   */
  public void setInCompleteWPRespModelSet(final Set<A2lWPRespModel> inCompleteWPRespModelSet) {
    this.inCompleteWPRespModelSet = inCompleteWPRespModelSet;
  }


  /**
   * @return the completedWPRespModelSet
   */
  public Set<A2lWPRespModel> getCompletedWPRespModelSet() {
    return this.completedWPRespModelSet;
  }


  /**
   * @param completedWPRespModelSet the completedWPRespModelSet to set
   */
  public void setCompletedWPRespModelSet(final Set<A2lWPRespModel> completedWPRespModelSet) {
    this.completedWPRespModelSet = completedWPRespModelSet;
  }

}

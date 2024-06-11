/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author UKT1COB
 */
public class A2lWpRespStatusUpdationModel {

  /**
   * List of new A2lWpResponsibilityStatus entries to be created in T_A2L_WP_RESPONSIBILITY_STATUS table
   */
  private List<A2lWpResponsibilityStatus> a2lWpRespStatusListToBeCreated = new ArrayList<>();
  /**
   * Map of A2lWpResponsibilityStatus before Updating Wp finished status. Key - A2lWpResponsibilityStatus id, value -
   * A2lWpResponsibilityStatus
   */
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusToBeUpdatedMap = new HashMap<>();

  /**
   * Map of A2lWpResponsibilityStatus after Updating Wp finished status. Key - A2lWpResponsibilityStatus id, value -
   * A2lWpResponsibilityStatus
   */
  private Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusMapAfterUpdate = new HashMap<>();
  /**
   * List of newly created A2lWpResponsibilityStatus entries in T_A2L_WP_RESPONSIBILITY_STATUS table
   */
  private List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWpRespStatus = new ArrayList<>();

  /**
   * @return the a2lWpRespStatusListToBeCreated
   */
  public List<A2lWpResponsibilityStatus> getA2lWpRespStatusListToBeCreated() {
    return this.a2lWpRespStatusListToBeCreated;
  }


  /**
   * @param a2lWpRespStatusListToBeCreated the a2lWpRespStatusListToBeCreated to set
   */
  public void setA2lWpRespStatusListToBeCreated(final List<A2lWpResponsibilityStatus> a2lWpRespStatusListToBeCreated) {
    this.a2lWpRespStatusListToBeCreated = a2lWpRespStatusListToBeCreated;
  }


  /**
   * @return the a2lWpRespStatusToBeUpdatedMap
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusToBeUpdatedMap() {
    return this.a2lWpRespStatusToBeUpdatedMap;
  }


  /**
   * @param a2lWpRespStatusBeforeUpdateMap the a2lWpRespStatusToBeUpdatedMap to set
   */
  public void setA2lWpRespStatusToBeUpdatedMap(
      final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusBeforeUpdateMap) {
    this.a2lWpRespStatusToBeUpdatedMap = a2lWpRespStatusBeforeUpdateMap;
  }


  /**
   * @return the a2lWpRespStatusMapAfterUpdate
   */
  public Map<Long, A2lWpResponsibilityStatus> getA2lWpRespStatusMapAfterUpdate() {
    return this.a2lWpRespStatusMapAfterUpdate;
  }


  /**
   * @param a2lWpRespStatusMapAfterUpdate the a2lWpRespStatusMapAfterUpdate to set
   */
  public void setA2lWpRespStatusMapAfterUpdate(
      final Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusMapAfterUpdate) {
    this.a2lWpRespStatusMapAfterUpdate = a2lWpRespStatusMapAfterUpdate;
  }


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


}

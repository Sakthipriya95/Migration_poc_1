/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import java.util.Map;

import com.bosch.caltool.datamodel.core.IModelType;

/**
 * @author bne4cob
 */
public interface IClientDataHandler {

  /**
   * Provides the checkers for verifying the applicability of CNS change in the handler
   *
   * @return Map : Key - model type, value - CNS applicability checker
   */
  Map<IModelType, ICnsApplicabilityCheckerChangeData> getCnsApplicabilityCheckersChangeData();

  /**
   * refresh data based on DCE changes
   *
   * @param dce Display Change Event
   * @return refresher
   */
  ICnsRefresherDce getCnsRefresherDce();

}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import com.bosch.caltool.datamodel.core.cns.ChangeData;

/**
 * Refresh action for CNS event from the same source (Local changes)
 * 
 * @author bne4cob
 */
public interface ILocalCnsRefreshAction {

  /**
   * Refresh based on the change data
   *
   * @param changeData Change Data
   */
  void execute(ChangeData<?> changeData);
}

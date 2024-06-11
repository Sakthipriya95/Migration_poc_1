/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import java.util.Map;

/**
 * Refresh action for CNS changes. Supports both global and local changes. The implimentations should invoke appropriate
 * web services to update the data.
 *
 * @author bne4cob
 */
@FunctionalInterface
public interface ICnsRefreshAction {

  /**
   * Global refresh action. Basic information on the change data would be available.
   *
   * @param chDataInfoMap information on the change data. Key - Object ID; value - change data info
   */
  void execute(Map<Long, ChangeDataInfo> chDataInfoMap);
}

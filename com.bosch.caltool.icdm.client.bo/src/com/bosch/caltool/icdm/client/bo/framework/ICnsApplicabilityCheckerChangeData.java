/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import com.bosch.caltool.datamodel.core.cns.ChangeData;

/**
 * @author bne4cob
 */
@FunctionalInterface
public interface ICnsApplicabilityCheckerChangeData {

  /**
   * Check whether refresh is applicable for the type
   *
   * @param chData change data
   * @return true, if refresh is applicable
   */
  boolean isRefreshApplicable(ChangeData<?> chData);

}

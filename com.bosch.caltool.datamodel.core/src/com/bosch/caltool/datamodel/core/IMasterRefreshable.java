/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core;


/**
 * @author bru2cob
 */
public interface IMasterRefreshable {

  /**
   * @return true is master refresh is applicable
   */
  boolean isMasterRefreshApplicable();

  /**
   * @param isRefreshApplicable is refresh applicable
   */
  void setIsMasterRefreshApplicable(boolean isRefreshApplicable);
}

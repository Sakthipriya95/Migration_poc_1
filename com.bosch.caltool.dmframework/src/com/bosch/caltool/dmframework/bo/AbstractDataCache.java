/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;

import com.bosch.caltool.dmframework.common.ObjectStore;


/**
 * Data cache stores the reusable data in the data model.
 *
 * @author bne4cob
 */
@Deprecated
public abstract class AbstractDataCache { // NOPMD by BNE4COB on 12/12/13 5:16 PM

  /**
   * @return the user name
   */
  public String getAppUsername() {
    return ObjectStore.getInstance().getAppUserName();
  }

}

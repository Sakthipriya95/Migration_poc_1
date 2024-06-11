/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import com.bosch.caltool.datamodel.core.IDataObject;


/**
 * @author bne4cob
 */
public interface IUseCaseItem extends IDataObject {

  /**
   * @return deleted flag
   */
  boolean isDeleted();

  /**
   * @param deletedFlag deleted flag string
   */
  void setDeleted(final boolean deletedFlag);

}

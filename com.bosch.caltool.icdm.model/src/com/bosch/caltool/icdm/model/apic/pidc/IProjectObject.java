/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IDataObject;


/**
 * @author bne4cob
 */
public interface IProjectObject extends IDataObject {

  /**
   * @return name
   */
  @Override
  String getName();

  /**
   * @param name
   */
  @Override
  void setName(String name);

  /**
   * @return deleted
   */
  boolean isDeleted();

  /**
   * @param deleted
   */
  void setDeleted(boolean deleted);
}

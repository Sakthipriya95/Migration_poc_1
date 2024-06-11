/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core;

import java.io.Serializable;

/**
 * @author bne4cob
 */
public interface IModelType extends Serializable {

  /**
   * @return type as string
   */
  String getTypeCode();

  /**
   * @return the display name of the type
   */
  String getTypeName();

  /**
   * @return class of this type
   */
  Class<?> getTypeClass();

  /**
   * @return the order number with which the model types are to be sorted
   */
  int getOrder();


}

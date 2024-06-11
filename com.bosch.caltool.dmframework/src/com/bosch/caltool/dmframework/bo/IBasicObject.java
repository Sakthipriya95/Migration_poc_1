/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.bo;


/**
 * Interface of a basic object in the application
 *
 * @author bne4cob
 */
// ICDM-1830
@Deprecated
public interface IBasicObject {

  /**
   * This method returns the name of this object to be displayed in the UI. The name may refer to<br>
   * a) the name of the specific entity which this object uses OR <br>
   * b) the parent object in case of mapping entities.
   * <p>
   * NOTE : The name is NOT guarenteed to be unique.
   *
   * @return Name of the data object
   */
  String getName();

  /**
   * @return Brief description of this object or <code>null</code> if not applicable
   */
  // ICDM-1314
  String getDescription();

  /**
   * Returns tooltip, to be displayed, of the object
   *
   * @return tooltip
   */
  // ICDM-1314
  String getToolTip();

}

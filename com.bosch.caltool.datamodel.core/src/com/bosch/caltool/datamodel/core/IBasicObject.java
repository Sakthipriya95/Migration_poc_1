/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core;


/**
 * @author rgo7cob
 */
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
   * @param name name
   */
  void setName(String name);


  /**
   * @return Brief description of this object or <code>null</code> if not applicable
   */
  // ICDM-1314
  String getDescription();

  /**
   * @param description description
   */
  void setDescription(String description);
}

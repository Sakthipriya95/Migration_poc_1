/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core;

/**
 * @author rgo7cob
 */
public interface IDataObject extends IModel, IBasicObject {


  /**
   * @return Created User of this object or <code>null</code> if not applicable
   */
  String getCreatedUser();


  /**
   * @param user user
   */
  void setCreatedUser(String user);


  /**
   * @return Last Modified user of this object, or <code>null</code> if not applicable
   */
  String getModifiedUser();


  /**
   * @param modifiedUser modifiedUser
   */
  void setModifiedUser(String modifiedUser);


  /**
   * Get Created date of this object
   *
   * @return Created date of this object or <code>null</code> if not applicable
   */

  String getCreatedDate();


  /**
   * set created Date
   * 
   * @param date created date to set
   */
  void setCreatedDate(String date);


  /**
   * Get Last modified date of this object
   *
   * @return Last modified date of this object or <code>null</code> if not applicable or not available
   */

  String getModifiedDate();


  /**
   * set modified Date
   * 
   * @param date modified date to set
   */
  void setModifiedDate(String date);


}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.cocwp;

import com.bosch.caltool.datamodel.core.IDataObject;


/**
 * @author ukt1cob
 */
public interface IProjectCoCWP extends IDataObject, Comparable<IProjectCoCWP> {

  /**
   * @return flag
   */
  String getUsedFlag();

  /**
   * @param usedFlag
   */
  void setUsedFlag(String usedFlag);

  /**
   * @return wpDivId
   */
  Long getWPDivId();

  /**
   * @param wpDivId
   */
  void setWPDivId(Long wpDivId);

  /**
   * @return true if WP Division is deleted
   */
  boolean isDeleted();

  /**
   * @param deleted deleted flag - true/false
   */
  void setDeleted(final boolean deleted);

  /**
   * @return the isAtChildLevel
   */
  boolean isAtChildLevel();

  /**
   * @param atChildLevel the atChildLevel to set
   */
  void setAtChildLevel(final boolean atChildLevel);

  /**
   * @param projectCoCWP
   * @param sortColumn
   * @return comparison result
   */
  int compareTo(IProjectCoCWP projectCoCWP, int sortColumn);


}

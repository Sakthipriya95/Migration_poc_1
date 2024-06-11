/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.wp;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author say8cob
 */
public class WorkPkgInput {

  private Long divValId;

  private SortedSet<WorkPkg> workPkgSet;


  /**
   * @return the divValId
   */
  public Long getDivValId() {
    return this.divValId;
  }


  /**
   * @param divValId the divValId to set
   */
  public void setDivValId(final Long divValId) {
    this.divValId = divValId;
  }


  /**
   * @return the workPkgSet
   */
  public SortedSet<WorkPkg> getWorkPkgSet() {
    return this.workPkgSet;
  }


  /**
   * @param workPkgSet the workPkgSet to set
   */
  public void setWorkPkgSet(final SortedSet<WorkPkg> workPkgSet) {
    if (workPkgSet != null) {
      this.workPkgSet = new TreeSet<>(workPkgSet);
    }
  }


}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bosch.caltool.icdm.common.util.ApicUtil;


/**
 * @author rgo7cob
 */
@Deprecated
public class WorkPackageGroup implements Comparable<WorkPackageGroup> {

  private final String groupID;

  private Map<String, WorkPackage> workPackageMap = new TreeMap<String, WorkPackage>();


  /**
   * Constructor
   *
   * @param groupID Group's ID
   */
  public WorkPackageGroup(final String groupID) {
    super();
    this.groupID = groupID;

  }

  /**
   * @return the groupID
   */
  public String getGroupID() {
    return this.groupID;
  }


  /**
   * @return the workPackageMap
   */
  public Map<String, WorkPackage> getWorkPackageMap() {
    return this.workPackageMap;
  }

  /**
   * @return
   */
  public SortedSet<WorkPackage> getWorkPackage() {
    return new TreeSet<WorkPackage>(this.workPackageMap.values());
  }

  /**
   * @return Group's name
   */
  public String getGroupName() {
    return this.groupID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final WorkPackageGroup arg0) {
    return ApicUtil.compare(getGroupName(), arg0.getGroupName());
  }


  /**
   * @param hashMap
   */
  public void setWorkPackageMap(final Map<String, WorkPackage> workPackageMap) {

    this.workPackageMap = workPackageMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object paramObject) {

    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    if (this.groupID.equals(((WorkPackageGroup) paramObject).getGroupID())) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return super.hashCode();
  }


}

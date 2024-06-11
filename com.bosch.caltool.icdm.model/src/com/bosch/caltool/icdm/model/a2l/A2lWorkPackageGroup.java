/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * The Class A2lWorkPackageGroup.
 *
 * @author rgo7cob
 */
public class A2lWorkPackageGroup implements Comparable<A2lWorkPackageGroup>, IModel {

  /**
   *
   */
  private static final long serialVersionUID = 6232438511772039876L;

  /** The group id. */
  private String groupName;

  /** The work package map. */
  private Map<String, Long> workPackageMap = new TreeMap<>();

  /**
   * Gets the work package map.
   *
   * @return the workPackageMap
   */
  public Map<String, Long> getWorkPackageMap() {
    return this.workPackageMap;
  }

  /**
   * Gets the work package.
   *
   * @return the work package
   */
  public SortedSet<Long> getWorkPackage() {
    return new TreeSet<>(this.workPackageMap.values());
  }

  /**
   * Gets the group name.
   *
   * @return Group's name
   */
  public String getGroupName() {
    return this.groupName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2lWorkPackageGroup arg0) {
    return ModelUtil.compare(getGroupName(), arg0.getGroupName());
  }


  /**
   * Sets the work package map.
   *
   * @param workPackageMap the work package map
   */
  public void setWorkPackageMap(final Map<String, Long> workPackageMap) {
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
    return this.groupName.equals(((A2lWorkPackageGroup) paramObject).getGroupName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // TODO Auto-generated method stub

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // TODO Auto-generated method stub

  }

  /**
   * @param trim
   */
  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }


}

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.a2l;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.bosch.caltool.icdm.model.util.ModelUtil;


/**
 * The Class A2LGroup.
 *
 * @author gge6cob
 */
public class A2LGroup implements Comparable<A2LGroup> {

  /** Hash code prime number. */
  private static final int HASHCODE_PRIME = 31;

  /** The group name. */
  private String groupName;

  /** The group long name. */
  private String groupLongName;

  /** The label map. */
  private Map<String, String> labelMap = new TreeMap<>();

  /** The sub grp map. */
  private Map<String, List<String>> subGrpMap = new HashMap<>();

  /** The root name. */
  private String rootName;

  private boolean isRootGroup;

  /**
   * enum to declare the sort columns.
   */
  public enum SortColumns {

                           /** Group Name column. */
                           SORT_GROUP_NAME,

                           /** Work Package Name. */
                           SORT_GROUP_LONG,

                           /** Work Package Number. */
                           SORT_NUM_REF

  }

  /**
   * Gets the group name.
   *
   * @return the groupName
   */
  public String getGroupName() {
    return this.groupName;
  }


  /**
   * Sets the group name.
   *
   * @param groupName the groupName to set
   */
  public void setGroupName(final String groupName) {
    this.groupName = groupName;
  }


  /**
   * Gets the group long name.
   *
   * @return the groupLongName
   */
  public String getGroupLongName() {
    return this.groupLongName;
  }


  /**
   * Sets the group long name.
   *
   * @param groupLongName the groupLongName to set
   */
  public void setGroupLongName(final String groupLongName) {
    this.groupLongName = groupLongName;
  }


  /**
   * Gets the label map.
   *
   * @return the labelMap
   */
  public Map<String, String> getLabelMap() {
    return this.labelMap;
  }


  /**
   * Gets the root name.
   *
   * @return the rootName
   */
  public String getRootName() {
    return this.rootName;
  }


  /**
   * Sets the root name.
   *
   * @param rootName the rootName to set
   */
  public void setRootName(final String rootName) {
    this.rootName = rootName;
  }


  /**
   * Gets the sub grp map.
   *
   * @return the subGrpMap
   */
  public Map<String, List<String>> getSubGrpMap() {
    return this.subGrpMap;
  }


  /**
   * Sets the label map.
   *
   * @param labelMap the labelMap to set
   */
  public void setLabelMap(final Map<String, String> labelMap) {
    this.labelMap = labelMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final A2LGroup arg0) {
    return ModelUtil.compare(getGroupName(), arg0.getGroupName());
  }

  /**
   * Compare to.
   *
   * @param a2lgroup the a 2 lgroup
   * @param sortColumn the sort column
   * @return the int
   */
  public int compareTo(final A2LGroup a2lgroup, final SortColumns sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case SORT_GROUP_NAME:
        compareResult = ModelUtil.compare(getGroupName(), a2lgroup.getGroupName());
        break;
      case SORT_GROUP_LONG:
        compareResult = ModelUtil.compare(getGroupLongName(), a2lgroup.getGroupLongName());
        break;

      case SORT_NUM_REF:

        compareResult = ModelUtil.compare(getLabelMap().size(), a2lgroup.getLabelMap().size());

        break;
      default:
        compareResult = 0;
        break;
    }
    // additional compare column is the name of the system constant
    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = ModelUtil.compare(getGroupName(), a2lgroup.getGroupName());
    }

    return compareResult;

  }


  /**
   * Sets the sub grp map.
   *
   * @param subGrpMap subGrpMap
   */
  public void setSubGrpMap(final Map<String, List<String>> subGrpMap) {
    this.subGrpMap = subGrpMap;
  }


  /**
   * @return the isRootGroup
   */
  public boolean isRootGroup() {
    return this.isRootGroup;
  }


  /**
   * @param isRootGroup the isRootGroup to set
   */
  public void setRootGroup(final boolean isRootGroup) {
    this.isRootGroup = isRootGroup;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    A2LGroup a2lGroup = (A2LGroup) obj;
    if (getGroupName() == null) {
      return null == a2lGroup.getGroupName();
    }
    return getGroupName().equals(a2lGroup.getGroupName());
  }

  /**
   * {@inheritDoc} return the hash code
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getGroupName() == null) ? 0 : getGroupName().hashCode());
    return result;
  }

}
